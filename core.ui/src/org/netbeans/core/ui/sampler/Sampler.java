/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.core.ui.sampler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/**
 *
 * @author Jaroslav Bachorik, Tomas Hurka, Jaroslav Tulach
 */
abstract class Sampler implements Runnable, ActionListener {
    private static final int SAMPLER_RATE = 10;
    private static final double MAX_AVERAGE = SAMPLER_RATE * 3;
    private static final double MAX_STDDEVIATION = SAMPLER_RATE * 4;
    private static final int MAX_SAMPLING_TIME = 5*60;  // 5 minutes
    private static final int MAX_SAMPLES = MAX_SAMPLING_TIME * (1000/SAMPLER_RATE);
    
    private final String name;
    private Timer timer;
    private ByteArrayOutputStream out;
    private SamplesOutputStream samplesStream;
    private long startTime;
    private long nanoTimeCorrection;
    private long samples;
    private long laststamp;
    private double max;
    private double min = Long.MAX_VALUE;
    private double sum;
    private double devSquaresSum;
    private volatile boolean stopped;
    private volatile boolean running;

    Sampler(String n) {
        name = n;
    }
    
    /** Returns the bean to use for sampling.
     * @return instance of the bean to take thread dumps from
     */
    protected abstract ThreadMXBean getThreadMXBean();

    /** Allows subclasses to handle created snapshot
     * @param arr the content of the snapshot
     * @throws IOException thrown in case of I/O error
     */
    protected abstract void saveSnapshot(byte[] arr) throws IOException;
    
    /** How to report an exception.
     * 
     * @param ex exception
     */
    protected abstract void printStackTrace(Throwable ex);
    
    /** Methods for displaying progress.
     */
    protected abstract void openProgress(int steps);
    protected abstract void closeProgress();
    protected abstract void progress(int i);
    
    private void updateStats(long timestamp) {
        if (laststamp != 0) {
            double diff = (timestamp - laststamp) / 1000000.0;
            samples++;
            sum += diff;
            devSquaresSum += (diff - SAMPLER_RATE) * (diff - SAMPLER_RATE);
            if (diff > max) {
                max = diff;
            } else if (diff < min) {
                min = diff;
            }
        }
        laststamp = timestamp;
    }

    @Override
    public synchronized void run() {
        assert !running;
        running = true;
        final ThreadMXBean threadBean = getThreadMXBean();
        out = new ByteArrayOutputStream(64 * 1024);
        try {
            samplesStream = new SamplesOutputStream(out, this, MAX_SAMPLES);
        } catch (IOException ex) {
            printStackTrace(ex);
            return;
        }
        startTime = System.currentTimeMillis();
        nanoTimeCorrection = startTime * 1000000 - System.nanoTime();
        timer = new Timer(name);
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                synchronized (Sampler.this) {
                    if (stopped) {
                        return;
                    }
                    try {
                        ThreadInfo[] infos = threadBean.dumpAllThreads(false, false);
                        long timestamp = System.nanoTime() + nanoTimeCorrection;
                        samplesStream.writeSample(infos, timestamp, Thread.currentThread().getId());
                        updateStats(timestamp);
                    } catch (Throwable ex) {
                        printStackTrace(ex);
                    }
                }
            }
        }, SAMPLER_RATE, SAMPLER_RATE);
    }

    @Override
    public synchronized void actionPerformed(ActionEvent e) {
        try {
            assert running;
            assert !stopped;
            stopped = true;
            timer.cancel();
            if ("cancel".equals(e.getActionCommand()) || samples < 1) {     // NOi18N
                return;
            }
            double average = sum / samples;
            double std_deviation = Math.sqrt(devSquaresSum / samples);
            boolean writeCommand = "write".equals(e.getActionCommand()); // NOI18N
            if (writeCommand) {
                Object[] params = new Object[]{startTime, "Samples", samples, "Average", average, "Minimum", min, "Maximum", max, "Std. deviation", std_deviation};
                Logger.getLogger("org.netbeans.ui.performance").log(Level.CONFIG, "Snapshot statistics", params); // NOI18N
                if (average > MAX_AVERAGE || std_deviation > MAX_STDDEVIATION) {
                    // do not take snapshot if the sampling was not regular enough
                    return;
                }
            }
            samplesStream.close();
            samplesStream = null;
            if (writeCommand) {
                DataOutputStream dos = (DataOutputStream) e.getSource();
                dos.write(out.toByteArray());
                dos.close();
                return;
            }
            saveSnapshot(out.toByteArray());
        } catch (Exception ex) {
            printStackTrace(ex);
        } finally {
            // just to be sure
            out = null;
            samplesStream = null;
        }
    }
    
    //
    // Support for sampling from command line
    //
    
    public static void main(String... args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: <port> <snapshot.npss>");
            System.out.println();
            System.out.println("First of all start your application with following parameters:");
            System.out.println("  -Dcom.sun.management.jmxremote.authenticate=false");
            System.out.println("  -Dcom.sun.management.jmxremote.ssl=false");
            System.out.println("  -Dcom.sun.management.jmxremote.port=<port>");
            System.out.println("Then you can start this sampler with correct port and file to write snapshot to.");
            System.exit(1);
        }
        
        String u = args[0];
        try {
            u = "service:jmx:rmi:///jndi/rmi://localhost:" + Integer.parseInt(args[0]) + "/jmxrmi";
        } catch (NumberFormatException ex) {
            // OK, use args[0]
        }
        
        System.err.println("Connecting to " + u);
        JMXServiceURL url = new JMXServiceURL(u);
        JMXConnector jmxc = null;
        Exception ex = null;
        for (int i = 0; i < 100; i++) {
            try {
                jmxc = JMXConnectorFactory.connect(url, null);
                break;
            } catch (IOException e) {
                ex = e;
                System.err.println("Connection failed. Will retry in 300ms.");
                Thread.sleep(300);
            }
        }
        if (jmxc == null) {
            ex.printStackTrace();
            System.err.println("Cannot connect to " + u);
            System.exit(3);
        }
        MBeanServerConnection server = jmxc.getMBeanServerConnection();
        
        final ThreadMXBean threadMXBean = ManagementFactory.newPlatformMXBeanProxy(
            server,ManagementFactory.THREAD_MXBEAN_NAME,ThreadMXBean.class
        );
        final File output = new File(args[1]);
        class CLISampler extends Sampler {
            CLISampler() {
                super("");
            }
            
            @Override
            protected ThreadMXBean getThreadMXBean() {
                return threadMXBean;
            }

            @Override
            protected void saveSnapshot(byte[] arr) throws IOException {
                FileOutputStream os = new FileOutputStream(output);
                os.write(arr);
                os.close();
            }

            @Override
            protected void printStackTrace(Throwable ex) {
                ex.printStackTrace();
                System.exit(2);
            }

            @Override
            protected void openProgress(int steps) {
            }

            @Override
            protected void closeProgress() {
            }

            @Override
            protected void progress(int i) {
                System.out.print("#");
                System.out.flush();
            }
        }
        
        CLISampler s = new CLISampler();
        s.run();
        System.out.println("Press enter to generate sample into " + output);
        System.in.read();
        s.actionPerformed(new ActionEvent(s, 0, ""));
        System.out.println();
        System.out.println("Sample written to " + output);
        System.exit(0);
    }
}
