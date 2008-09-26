/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.openide.filesystems.test;

import java.io.File;
import java.io.FileDescriptor;
import java.security.Permission;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import junit.framework.Assert;

/**
 *
 * @author rmatous, Jiri Skrivanek
 */
public class StatFiles extends SecurityManager {

    public static final int ALL = 0;
    public static final int READ = 1;
    public static final int WRITE = 2;
    public static final int DELETE = 3;
    private Results results;
    private Monitor monitor;
    private SecurityManager defaultSecurityManager;

    public StatFiles() {
        reset();
    }

    public void register() {
        if (defaultSecurityManager == null) {
            defaultSecurityManager = System.getSecurityManager();
        }
        System.setSecurityManager(this);
    }
    
    public void unregister() {
        if (defaultSecurityManager == null) {
            System.setSecurityManager(defaultSecurityManager);
        }
    }
    
    public void reset() {
        results = new Results();
    }
    
    public Results getResults() {
        return results;
    }

    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void checkPermission(Permission perm) {
    }

    @Override
    public void checkRead(FileDescriptor fd) {
        super.checkRead(fd);
    }

    @Override
    public void checkRead(String file) {
        File f = new File(file);
        if (!canBeSkipped()) {
            if (monitor != null) {
                monitor.checkRead(f);
                monitor.checkAll(f);
            }
            results.forRead.put(f, results.statResult(f, READ) + 1);
            putStackTrace(f, results.forReadStack);
        }
    }

    @Override
    public void checkRead(String file, Object context) {
        super.checkRead(file, context);
    }

    @Override
    public void checkWrite(FileDescriptor fd) {
        super.checkWrite(fd);
    }

    @Override
    public void checkWrite(String file) {
        File f = new File(file);
        if (!canBeSkipped()) {
            if (monitor != null) {
                monitor.checkAll(f);
            }
            results.forWrite.put(f, results.statResult(f, WRITE) + 1);
            putStackTrace(f, results.forWriteStack);
        }

    }

    @Override
    public void checkDelete(String file) {
        File f = new File(file);
        if (!canBeSkipped()) {
            if (monitor != null) {
                monitor.checkAll(f);
            }
            results.forDelete.put(f, results.statResult(f, DELETE) + 1);
            putStackTrace(f, results.forDeleteStack);
        }
    }

    private boolean canBeSkipped() {
        boolean result = false;
        Throwable th = new Throwable();
        StackTraceElement[] elems = th.getStackTrace();
        for (StackTraceElement stackTraceElement : elems) {
            if (stackTraceElement.getClassName().endsWith("ClassLoader") &&
                    (stackTraceElement.getMethodName().endsWith("loadClass") ||
                    stackTraceElement.getMethodName().endsWith("getResource"))) {
                result = true;
                break;
            }
        }
        return result;
    }

    /** Add current stack trace to given map or increase count if the stack trace
     * already added. */
    private static void putStackTrace(File file, Map<File, Map<String, Integer>> fileStackMap) {
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        for (int i = 2; i < ste.length; i++) {
            sb.append(ste[i].toString()).append('\n');
        }
        String stackTrace = sb.toString();
        Map<String, Integer> stackMap = fileStackMap.get(file);
        if (stackMap == null) {
            stackMap = new HashMap<String, Integer>();
            fileStackMap.put(file, stackMap);
        }
        if (stackMap.get(stackTrace) == null) {
            stackMap.put(stackTrace, 1);
        } else {
            stackMap.put(stackTrace, stackMap.get(stackTrace) + 1);
        }
    }

    /** Get all stack traces for given file. */
    private static String getStackTraces(File file, Map<File, Map<String, Integer>> fileStackMap) {
        Map<String, Integer> stackMap = fileStackMap.get(file);
        if (stackMap == null) {
            return "";
        } else {
            String allStackTraces = "";
            for (String stackTrace : stackMap.keySet()) {
                allStackTraces += "  Count: " + stackMap.get(stackTrace) + "\n";
                allStackTraces += stackTrace + "\n";
            }
            return allStackTraces;
        }
    }

    public static interface Monitor {

        void checkRead(File file);

        void checkAll(File file);
    }

    public static class Results {

        private Map<File, Integer> forRead = new HashMap<File, Integer>();
        private Map<File, Map<String, Integer>> forReadStack = new HashMap<File, Map<String, Integer>>();
        private Map<File, Integer> forWrite = new HashMap<File, Integer>();
        private Map<File, Map<String, Integer>> forWriteStack = new HashMap<File, Map<String, Integer>>();
        private Map<File, Integer> forDelete = new HashMap<File, Integer>();
        private Map<File, Map<String, Integer>> forDeleteStack = new HashMap<File, Map<String, Integer>>();
        
        Results addResult(Results results) {
            if (results == this) {
                throw new IllegalArgumentException();
            }
            forRead.putAll(results.forRead);
            forWrite.putAll(results.forWrite);
            forDelete.putAll(results.forDelete);
            return this;
        }

        /** If real number of accesses is bigger than expected, it fails with
         * message containing real numbers for all access types and list
         * of stack traces.
         * @param cnt expected count of accesses
         * @param type type of access
         */
        public void assertResult(int cnt, int type) {
            int real = statResult(type);
            if (cnt < real) {
                Assert.fail("Expected " + cnt + " but was " + real + 
                        "\n  Read: " + forRead + "\n  Write: " + forWrite +
                        "\n  Delete: " + forDelete + "\n" +
                        statResultStack(type));
            }
        }

        public Set<File> getFiles() {
            Set<File> result = new HashSet<File>();
            result.addAll(forRead.keySet());
            result.addAll(forWrite.keySet());
            result.addAll(forDelete.keySet());
            return result;
        }

        public int statResult(int type) {
            Set<File> files = getFiles();
            int result = 0;
            for (File file : files) {
                result += statResult(file, type);
            }
            return result;
        }

        public int statResult(File file, int type) {
            switch (type) {
                case READ:
                    Integer read = forRead.get(file);
                    return (read != null) ? read : 0;
                case WRITE:
                    Integer write = forWrite.get(file);
                    return (write != null) ? write : 0;
                case DELETE:
                    Integer delete = forDelete.get(file);
                    return (delete != null) ? delete : 0;
                case ALL:
                    int all = statResult(file, READ);
                    all += statResult(file, WRITE);
                    all += statResult(file, DELETE);
                    return all;
            }
            return -1;
        }

        public String statResultStack(int type) {
            String result = "";
            for (File file : getFiles()) {
                result += "FILE=" + file;
                result += statResultStack(file, type);
            }
            return result;
        }

        public String statResultStack(File file, int type) {
            switch (type) {
                case READ:
                    return "--------------- READ STACKS -----------------\n" +
                            getStackTraces(file, forReadStack);
                case WRITE:
                    return "--------------- WRITE STACKS ----------------\n" +
                            getStackTraces(file, forWriteStack);
                case DELETE:
                    return "--------------- DELETE STACKS ---------------\n" +
                            getStackTraces(file, forDeleteStack);
                case ALL:
                    String all = statResultStack(file, READ);
                    all += statResultStack(file, WRITE);
                    all += statResultStack(file, DELETE);
                    all += "---------------------------------------------\n";
                    return all;
            }
            return null;
        }
        
        /** Dump all files sorted by name with number of accesses. */
        public void dump() {
            File[] files = getFiles().toArray(new File[0]);
            Arrays.sort(files);
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                System.out.print(file + "   READ=" + statResult(file, StatFiles.READ));
                System.out.print(" WRITE=" + statResult(file, StatFiles.WRITE));
                System.out.print(" DELETE=" + statResult(file, StatFiles.DELETE));
                System.out.println(" ALL=" + statResult(file, StatFiles.ALL));
            }
            System.out.print("READ=" + statResult(StatFiles.READ));
            System.out.print(" WRITE=" + statResult(StatFiles.WRITE));
            System.out.print(" DELETE=" + statResult(StatFiles.DELETE));
            System.out.println(" ALL=" + statResult(StatFiles.ALL));
        }

        /** Dump all files sorted by name with stack traces. */
        public void dumpStacks() {
            File[] files = getFiles().toArray(new File[0]);
            Arrays.sort(files);
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                System.out.println("FILE="+file);
                System.out.println(statResultStack(file, StatFiles.ALL));
            }
        }
    }
}
