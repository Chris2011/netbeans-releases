/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2016 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2016 Sun Microsystems, Inc.
 */
package org.netbeans.lib.jshell.agent;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import jdk.jshell.execution.LoaderDelegate;
import jdk.jshell.execution.RemoteExecutionControl;
import static jdk.jshell.execution.Util.forwardExecutionControlAndIO;
import static org.netbeans.lib.jshell.agent.RemoteCodes.RESULT_KILLED;
import org.netbeans.lib.jshell.agent.NbJShellAgent;

/**
 *
 * @author sdedic
 */
public class AgentWorker extends RemoteExecutionControl implements Executor, Runnable {
    private static final Logger LOG = Logger.getLogger("org.netbeans.lib.jshell.agent.AgentWorker"); // NOI18N
    
    public static final String PROPERTY_EXECUTOR = "org.netbeans.lib.jshell.agent.AgentWorker.executor"; // NOI18N
    
    /**
     * Reference set by instrumented classes
     */
    public static volatile ClassLoader referenceClassLoader;
            
    /**
     * The JShell agent main with options read from commandline
     */
    private final NbJShellAgent   agent;
    
    /**
     * The control socket
     */
    private final Socket socket;
    private final int   socketPort;

    private RemoteClassLoader loader;
    /**
     * The Classloader last obtained from a field or method
     */
    private ClassLoader lastClassLoader;
    
    /**
     * Provider for the classloader. Contacted before every user invocation
     * and before every class load.
     */
    private Callable<ClassLoader>   loaderProvider;
    

    /**
     * Executor for user's code. Can be provided from the environment using System.properties,
     * see {@link #PROPERTY_EXECUTOR}.
     */
    private Executor userExecutor;
    
    /**
     * Threads which execute user code, keyed by agent's socket local port.
     */
    // @GuardedBy(self)
    private final static Map<Integer, Thread>     userCodeExecutingThreads = new HashMap<Integer, Thread>();
        
    private AgentWorker() {
        agent = null;
        socket = null;
        socketPort = -1;
        this.userExecutor = findExecutor();
        loaderProvider = new Callable<ClassLoader>() {
            public ClassLoader call() {
                return loader;
            }
        };
        setup();
    }
    
    public AgentWorker(NbJShellAgent agent, Socket controlSocket) {
        this.agent = agent;
        this.socket = controlSocket;
        this.socketPort = controlSocket.getLocalPort();
        setup();
    }
    
    private void setup() {
        this.loader = new NbRemoteLoader(ClassLoader.getSystemClassLoader(), null, new URL[0]);
        if (agent != null) {
            if (agent.getField() != null || agent.getMethod() != null) {
                loaderProvider = new LoaderEvaluator();
            } else if (agent.getClassName() != null) {
                loaderProvider = new LoaderAccessor(loader);
            }
        } 
        if (loaderProvider == null) {
            loaderProvider = new Callable<ClassLoader>() {
                public ClassLoader call() {
                    return loader;
                }
            };
        }
        this.userExecutor = findExecutor();
    }
    
    private Executor findExecutor() {
        Object o = System.getProperties().get(PROPERTY_EXECUTOR);;
        if (o instanceof Executor) {
            return this.userExecutor = (Executor)o;
        } else if (o instanceof String) {
            try {
                Class executorClazz = Class.forName((String)o);
                return (Executor)executorClazz.newInstance();
            } catch (ClassNotFoundException ex) {
                LOG.log(Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                LOG.log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        return this;
    }
    
    private static class LoaderAccessor implements Callable<ClassLoader> {
        private final ClassLoader defaultLoader;
        
        public LoaderAccessor(ClassLoader defaultLoader) {
            this.defaultLoader = defaultLoader;
        }

        @Override
        public ClassLoader call() throws Exception {
            if (referenceClassLoader != null) {
                return referenceClassLoader;
            } else {
                return defaultLoader;
            }
        }
    }
    
    private class LoaderEvaluator implements Callable<ClassLoader> {
        private Class   clazz;
        private Method  method;
        private Field   field;
        
        public ClassLoader call() throws Exception {
            if (clazz == null) {
                try {
                    clazz = Class.forName(agent.getClassName(), false, loader);
                } catch (ClassNotFoundException ex) {
                    // the class may not be loaded yet, use the default loader now
                    return loader;
                }
                String m = agent.getMethod();
                String f = agent.getField();
                if (m != null) {
                    method = clazz.getDeclaredMethod(m);
                    if (!method.getReturnType().isAssignableFrom(ClassLoader.class) ||
                         (method.getModifiers() & Modifier.STATIC) == 0) {
                        throw new IllegalStateException("Loader access method must be static and return ClassLoader");
                    }
                    method.setAccessible(true);
                } else if (f != null) {
                    field = clazz.getDeclaredField(f);
                    field.setAccessible(true);
                    if (!field.getType().isAssignableFrom(ClassLoader.class) ||
                         (field.getModifiers() & Modifier.STATIC) == 0) {
                        throw new IllegalStateException("Loader access field must be static and assignable to ClassLoader");
                    }
                }
            }
            
            if (method != null) {
                return (ClassLoader)method.invoke(null);
            } else if (field != null) {
                return (ClassLoader)field.get(null);
            } else {
                return loader;
            }
        }
    }
    
    private void installNewClassLoader(ClassLoader delegate) {
        lastClassLoader = delegate;
        loader = new NbRemoteLoader(delegate, loader, 
                additionalClasspath.toArray(new URL[additionalClasspath.size()]));
    }

    @Override
    public void execute(Runnable command) {
        command.run();
    }
    
    protected NbRemoteLoader prepareClassLoader() {
        try {
            ClassLoader current = loaderProvider.call();
            if (current != lastClassLoader && current != loader) {
                installNewClassLoader(current);
            }
        } catch (Exception ex) {
            // don't touch
        }
        return (NbRemoteLoader)loader;
    }
    
    public static void main(String[] args) throws Exception {
        String loopBack = null;
        LOG.log(Level.INFO, "Running main, port = ", args[0]);
        Socket socket = new Socket(loopBack, Integer.parseInt(args[0]));
        try {
            AgentWorker worker = new AgentWorker();
            LOG.log(Level.INFO, "Worker created", args[0]);
            InputStream inStream = socket.getInputStream();
            OutputStream outStream = socket.getOutputStream();
            Map<String, Consumer<OutputStream>> chans = new HashMap<>();
            chans.put("out", st -> System.setOut(new PrintStream(st, true)));
            chans.put("err", st -> System.setErr(new PrintStream(st, true)));
            forwardExecutionControlAndIO(worker, inStream, outStream, chans);
        } catch (EOFException ex) {
            // ignore, forcible close by the tool
        }
    }

    @Override
    public Object extensionCommand(String command, Object arg) throws RunException, EngineTerminationException, InternalException {
        try {
            switch (command) {
                case "nb_vmInfo":
                    return returnVMInfo(null);
                case "nb_stop":
                    if (!(arg instanceof Integer)) {
                        throw new InternalError("Unexpected agent ID: " + arg);
                    }
                    performStop((Integer)arg);
                    return null;
                default:
                    throw new NotImplementedException("Command " + command + " not implemented");
            }
        } catch (Exception ex) {
            throw new InternalException("Internal error: " + ex.getClass().getName());
        }
    }
    
    
    
    @Override
    public void run() {
        // reset the classloader
        OutputStream osm = null;
        InputStream ism = null;
        try {
            LOG.fine("Opening output stream to master");
            // will block, but this is necessary so the IDE eventually sets the debuggerKey
            osm = socket.getOutputStream();
            ism = socket.getInputStream();
            // will read immediately
            LOG.fine("Opening input stream from master");

            Map<String, Consumer<OutputStream>> chans = new HashMap<>();
            forwardExecutionControlAndIO(this, ism, osm, chans);
        } catch (EOFException ex) {
            // expected.
        } catch (IOException ex) {
            
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (osm != null) {
                try {
                    osm.close();
                } catch (IOException ex) {
                    // ignore
                }
            }
            if (ism != null) {
                try {
                    ism.close();
                } catch (IOException ex) {
                    // ignore
                }
            }
        }
    }
    
    /**
     * Collect and send information about the executing VM.
     */
    public static final int CMD_VM_INFO   = 100;
    
    public static final int CMD_REDEFINE   = 101;
    
    public static final int CMD_STOP        = 102;
    
    public static final int CMD_CLASSID     = 103;
    
    /**
     * Find out reference identity of a class.
     */
    public static final int CMD_TYPE_ID   = 101;
    
    private Pattern EXCLUDE_CLASSPATH_ITEMS = Pattern.compile(
              "lib/tools.jar$|"
            + "modules/ext/nb-custom-jshell-probe.jar$"
    );
        

    private Object returnVMInfo(ObjectOutput o) throws IOException {
        Map<String, String>  result = new HashMap<String, String>();
        Properties props = System.getProperties();
        for (String s : props.stringPropertyNames()) {
            if (!s.startsWith("java")) { // NOI18N
                continue;
            }
            result.put(s, props.getProperty(s));
        }
        LOG.log(Level.FINE, "Sending properties: " + props);
        
        StringBuilder cp = new StringBuilder();
        for (URL u: loader.getURLs()) {
            try {
                File f = new File(u.toURI());
                String s = f.getPath();
                if (EXCLUDE_CLASSPATH_ITEMS.matcher(s).find()) {
                    continue;
                }
                if (cp.length() > 0) {
                    cp.append(":"); // NOI18N
                }
                cp.append(f.getPath());
            } catch (URISyntaxException ex) {
                cp.append(u.toExternalForm());
            }
        }
        for (String s : props.getProperty("java.class.path").split(File.pathSeparator)) {  // NOI18N
            if (s.isEmpty()) {
                continue;
            }
            if (EXCLUDE_CLASSPATH_ITEMS.matcher(s).find()) {
                continue;
            }
            if (cp.length() > 0) {
                cp.append(":"); // NOI18N
            }
            cp.append(s);
        }
        LOG.log(Level.FINE, "Classloader path: " + cp);
        result.put("nb.class.path", cp.toString()); // NOI18N
        return result;
    }
    
    private ClassLoader contextLoader;
    
    protected void clientCodeEnter() {
        LOG.log(Level.FINER, "Entering client code");
        this.contextLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(loader);
        super.clientCodeEnter();
    }
    
    private AtomicBoolean killed = new AtomicBoolean();
//    private boolean killed;
    
    /**
     * Removes our thread from the user-executing map.
     * It's important that clientCodeLeave is called BEFORE the agent starts to send
     * response code: either the thread is removed here, and the response code is sent by
     * the original Agent's code, or the entry is removed by the killer agent before/while
     * executing agent is waiting to lock the map - and in that case the `killed' will be set 
     * to true and response code is produced in performExecute. Since ThreadDeath is thrown, 
     * the executing agent will not complete the synchronized block normally
     */
    protected void clientCodeLeave() throws InternalException {
        super.clientCodeLeave();
        LOG.log(Level.FINER, "Exiting client code");
        Thread.currentThread().setContextClassLoader(contextLoader);
        synchronized (userCodeExecutingThreads) {
            Thread t = userCodeExecutingThreads.get(socketPort);
            if (t == Thread.currentThread()) {
                killed.set(userCodeExecutingThreads.remove(socketPort) != null);
            }
            LOG.log(Level.FINER, "User code killed: {0}", killed);
        }
    }

    @Override
    public String invoke(String className, String methodName) throws RunException, InternalException, EngineTerminationException {
        final Exception [] err = new IOException[1];
        final CountDownLatch execLatch = new CountDownLatch(1);
        final String[] result = new String[1];
        // for Graalists :)
        
        userExecutor.execute(new Runnable() {
            public void run() {
                try {
                    result[0] = performExecute(className, methodName, execLatch);
                } catch (Exception ex) {
                    err[0] = ex;
                }
            }
        });
        try {
            execLatch.await();
        } catch (InterruptedException ex) {
            throw new StoppedException();
        }
        if (err[0] != null) {
            try {
                throw err[0];
            } catch (RunException | InternalException | EngineTerminationException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new InternalException("InternalException: " + ex.getClass().getName());
            }
        }
        return result[0];
    }
    
    
    
    private String performExecute(String className, String methodName, CountDownLatch latch) 
            throws ExecutionControlException {
        killed.set(false);
        try {
            synchronized (userCodeExecutingThreads) {
                userCodeExecutingThreads.put(socketPort, Thread.currentThread());
            }
            return super.invoke(className, methodName);
        } catch (ThreadDeath td) {
            LOG.log(Level.FINE, "Received ThreadDeath, killed: {0}", killed);
            if (!killed.get()) {
                throw td;
            }
        } catch (Throwable t) {
            killed.set(true);
        } finally {
            latch.countDown();
        }
        throw new StoppedException();
    }
    
    private void performStop(int agentId) throws ExecutionControlException {
        Thread targetThread;
        if (agentId == -1) {
            agentId = this.socketPort;
        }
        synchronized (userCodeExecutingThreads) {
            targetThread = userCodeExecutingThreads.remove(agentId);
            if (targetThread != null) {
                // throw ThreadDeath in the target thread
                targetThread.stop();
            }
        }
        if (targetThread == null) {
            throw new InternalException("Invalid agent ID or not executing user code");
        }
    }
    
    private List<URL> additionalClasspath = new ArrayList<>();

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return prepareClassLoader().findClass(name);
    }

    @Override
    public void setClasspath(String path) throws EngineTerminationException, InternalException {
        List<URL> savePath = additionalClasspath;
        additionalClasspath = new ArrayList<>();
        boolean success = false;
        try {
            addToClasspath(path);
            success = true;
        } finally {
            if (!success) {
                additionalClasspath = savePath;
            }
        }
    }

    @Override
    public void addToClasspath(String cp) throws EngineTerminationException, InternalException {
        for (String cpItem : cp.split(";")) { // NOI18N
            File f = new File(cpItem);
            if (!f.isAbsolute()) {
                throw new InternalException("Relative paths unuspported yet"); // NOI18N
            }
            try {
                additionalClasspath.add(f.toURI().toURL());
            } catch (MalformedURLException ex) {
                throw new InternalException("Invalid file url: " + cpItem);
            }
        }
        // reset the classloader
        lastClassLoader = null;
    }

    @Override
    public void load(ClassBytecodes[] cbcs) throws ClassInstallException, NotImplementedException, EngineTerminationException {
        int count = cbcs.length;
        boolean[] status = new boolean[cbcs.length];
        int success = 0;
        NbRemoteLoader ldr = prepareClassLoader();
        for (int i = 0; i < count; i++) {
            String name = cbcs[i].name();
            byte[] byteCode = cbcs[i].bytecodes();
            ldr.delare(name, byteCode);
            try {
                Class.forName(name, false, ldr);
                success++;
                status[i] = true;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(AgentWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (success < count) {
            throw new ClassInstallException("Could not define class", status);
        }
    }

    @Override
    public void redefine(ClassBytecodes[] cbcs) throws ClassInstallException, NotImplementedException, EngineTerminationException {
        int count = cbcs.length;
        boolean[] status = new boolean[cbcs.length];
        int success = 0;
        NbRemoteLoader ldr = prepareClassLoader();
        for (int i = 0; i < count; i++) {
            String name = cbcs[i].name();
            byte[] replaceBytecode = cbcs[i].bytecodes();
            Long id = ldr.getClassId(name);
            if (id != null) {
                Class defined = ldr.getClassOfId(id);
                try {
                    agent.getInstrumentation().redefineClasses(
                            new ClassDefinition(defined, replaceBytecode)
                    );
                    status[i] = true;
                    success++;
                } catch (ClassNotFoundException | UnmodifiableClassException ex) {
                    Logger.getLogger(AgentWorker.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (success < count) {
                throw new ClassInstallException("Could not redefine classes", status);
            }
        }
    }
    
    private void performClassId(ObjectInputStream in, ObjectOutputStream out) throws IOException {
        String className = in.readUTF();
        Long id = ((NbRemoteLoader)loader).getClassId(className);
        out.writeInt(RemoteCodes.RESULT_SUCCESS);
        if (id == null) {
            out.writeLong(-1);
        } else {
            out.writeLong(id);
        }
        out.flush();
    }
 }
