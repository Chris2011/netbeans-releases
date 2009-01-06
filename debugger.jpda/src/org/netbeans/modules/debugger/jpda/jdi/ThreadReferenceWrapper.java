/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.debugger.jpda.jdi;

// DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
// Generated by org.netbeans.modules.debugger.jpda.jdi.Generate class.

/**
 * Wrapper for ThreadReference JDI class.
 * Use methods of this class instead of direct calls on JDI objects.
 * These methods assure that exceptions thrown from JDI calls are handled appropriately.
 *
 * @author Martin Entlicher
 */
public final class ThreadReferenceWrapper {

    private ThreadReferenceWrapper() {}

    public static com.sun.jdi.ObjectReference currentContendedMonitor(com.sun.jdi.ThreadReference a) throws com.sun.jdi.IncompatibleThreadStateException, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper {
        try {
            return a.currentContendedMonitor();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper(ex);
        } catch (java.lang.IllegalThreadStateException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper(ex);
        }
    }

    /** Wrapper for method forceEarlyReturn from JDK 1.6. */
    public static void forceEarlyReturn(com.sun.jdi.ThreadReference a, com.sun.jdi.Value b) throws com.sun.jdi.InvalidTypeException, com.sun.jdi.ClassNotLoadedException, com.sun.jdi.IncompatibleThreadStateException, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.NativeMethodExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InvalidStackFrameExceptionWrapper {
        try {
            com.sun.jdi.ThreadReference.class.getMethod("forceEarlyReturn", com.sun.jdi.Value.class).invoke(a, b);
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException(ex);
        } catch (SecurityException ex) {
            throw new IllegalStateException(ex);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(ex);
        } catch (java.lang.reflect.InvocationTargetException ex) {
            Throwable t = ex.getTargetException();
            if (t instanceof com.sun.jdi.InvalidTypeException) {
                throw (com.sun.jdi.InvalidTypeException) t;
            }
            if (t instanceof com.sun.jdi.ClassNotLoadedException) {
                throw (com.sun.jdi.ClassNotLoadedException) t;
            }
            if (t instanceof com.sun.jdi.IncompatibleThreadStateException) {
                throw (com.sun.jdi.IncompatibleThreadStateException) t;
            }
            if (t instanceof com.sun.jdi.InternalException) {
                org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report((com.sun.jdi.InternalException) t);
                throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper((com.sun.jdi.InternalException) t);
            }
            if (t instanceof com.sun.jdi.VMDisconnectedException) {
                throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper((com.sun.jdi.VMDisconnectedException) t);
            }
            if (t instanceof java.lang.IllegalThreadStateException) {
                throw new org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper((java.lang.IllegalThreadStateException) t);
            }
            if (t instanceof com.sun.jdi.NativeMethodException) {
                throw new org.netbeans.modules.debugger.jpda.jdi.NativeMethodExceptionWrapper((com.sun.jdi.NativeMethodException) t);
            }
            if (t instanceof com.sun.jdi.InvalidStackFrameException) {
                throw new org.netbeans.modules.debugger.jpda.jdi.InvalidStackFrameExceptionWrapper((com.sun.jdi.InvalidStackFrameException) t);
            }
            throw new IllegalStateException(t);
        }
    }

    public static com.sun.jdi.StackFrame frame(com.sun.jdi.ThreadReference a, int b) throws com.sun.jdi.IncompatibleThreadStateException, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper {
        try {
            return a.frame(b);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper(ex);
        } catch (java.lang.IllegalThreadStateException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper(ex);
        }
    }

    public static int frameCount0(com.sun.jdi.ThreadReference a) throws com.sun.jdi.IncompatibleThreadStateException, org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper {
        try {
            return a.frameCount();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return 0;
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return 0;
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            return 0;
        } catch (java.lang.IllegalThreadStateException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper(ex);
        }
    }

    public static int frameCount(com.sun.jdi.ThreadReference a) throws com.sun.jdi.IncompatibleThreadStateException, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper {
        try {
            return a.frameCount();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper(ex);
        } catch (java.lang.IllegalThreadStateException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper(ex);
        }
    }

    public static java.util.List<com.sun.jdi.StackFrame> frames0(com.sun.jdi.ThreadReference a) throws com.sun.jdi.IncompatibleThreadStateException, org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper {
        try {
            return a.frames();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return java.util.Collections.emptyList();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return java.util.Collections.emptyList();
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            return java.util.Collections.emptyList();
        } catch (java.lang.IllegalThreadStateException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper(ex);
        }
    }

    public static java.util.List<com.sun.jdi.StackFrame> frames(com.sun.jdi.ThreadReference a) throws com.sun.jdi.IncompatibleThreadStateException, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper {
        try {
            return a.frames();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper(ex);
        } catch (java.lang.IllegalThreadStateException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper(ex);
        }
    }

    public static java.util.List<com.sun.jdi.StackFrame> frames0(com.sun.jdi.ThreadReference a, int b, int c) throws com.sun.jdi.IncompatibleThreadStateException, org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper {
        try {
            return a.frames(b, c);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return java.util.Collections.emptyList();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return java.util.Collections.emptyList();
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            return java.util.Collections.emptyList();
        } catch (java.lang.IllegalThreadStateException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper(ex);
        }
    }

    public static java.util.List<com.sun.jdi.StackFrame> frames(com.sun.jdi.ThreadReference a, int b, int c) throws com.sun.jdi.IncompatibleThreadStateException, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper {
        try {
            return a.frames(b, c);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper(ex);
        } catch (java.lang.IllegalThreadStateException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper(ex);
        }
    }

    public static void interrupt(com.sun.jdi.ThreadReference a) throws org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper {
        try {
            a.interrupt();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper(ex);
        } catch (java.lang.IllegalThreadStateException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper(ex);
        }
    }

    public static boolean isAtBreakpoint0(com.sun.jdi.ThreadReference a) throws org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper {
        try {
            return a.isAtBreakpoint();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return false;
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return false;
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            return false;
        } catch (java.lang.IllegalThreadStateException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper(ex);
        }
    }

    public static boolean isAtBreakpoint(com.sun.jdi.ThreadReference a) throws org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper {
        try {
            return a.isAtBreakpoint();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper(ex);
        } catch (java.lang.IllegalThreadStateException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper(ex);
        }
    }

    public static boolean isSuspended0(com.sun.jdi.ThreadReference a) throws org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper {
        try {
            return a.isSuspended();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return false;
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return false;
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            return false;
        } catch (java.lang.IllegalThreadStateException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper(ex);
        }
    }

    public static boolean isSuspended(com.sun.jdi.ThreadReference a) throws org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper {
        try {
            return a.isSuspended();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper(ex);
        } catch (java.lang.IllegalThreadStateException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper(ex);
        }
    }

    public static java.lang.String name(com.sun.jdi.ThreadReference a) throws org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper {
        try {
            return a.name();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper(ex);
        } catch (java.lang.IllegalThreadStateException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper(ex);
        }
    }

    public static java.util.List<com.sun.jdi.ObjectReference> ownedMonitors0(com.sun.jdi.ThreadReference a) throws com.sun.jdi.IncompatibleThreadStateException, org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper {
        try {
            return a.ownedMonitors();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return java.util.Collections.emptyList();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return java.util.Collections.emptyList();
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            return java.util.Collections.emptyList();
        } catch (java.lang.IllegalThreadStateException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper(ex);
        }
    }

    public static java.util.List<com.sun.jdi.ObjectReference> ownedMonitors(com.sun.jdi.ThreadReference a) throws com.sun.jdi.IncompatibleThreadStateException, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper {
        try {
            return a.ownedMonitors();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper(ex);
        } catch (java.lang.IllegalThreadStateException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper(ex);
        }
    }

    /** Wrapper for method ownedMonitorsAndFrames from JDK 1.6. */
    public static java.util.List<Object/*com.sun.jdi.MonitorInfo*/> ownedMonitorsAndFrames(com.sun.jdi.ThreadReference a) throws com.sun.jdi.IncompatibleThreadStateException, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper {
        try {
            return (java.util.List<Object/*com.sun.jdi.MonitorInfo*/>) com.sun.jdi.ThreadReference.class.getMethod("ownedMonitorsAndFrames").invoke(a);
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException(ex);
        } catch (SecurityException ex) {
            throw new IllegalStateException(ex);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(ex);
        } catch (java.lang.reflect.InvocationTargetException ex) {
            Throwable t = ex.getTargetException();
            if (t instanceof com.sun.jdi.IncompatibleThreadStateException) {
                throw (com.sun.jdi.IncompatibleThreadStateException) t;
            }
            if (t instanceof com.sun.jdi.InternalException) {
                org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report((com.sun.jdi.InternalException) t);
                throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper((com.sun.jdi.InternalException) t);
            }
            if (t instanceof com.sun.jdi.VMDisconnectedException) {
                throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper((com.sun.jdi.VMDisconnectedException) t);
            }
            if (t instanceof java.lang.IllegalThreadStateException) {
                throw new org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper((java.lang.IllegalThreadStateException) t);
            }
            throw new IllegalStateException(t);
        }
    }

    /** Wrapper for method ownedMonitorsAndFrames from JDK 1.6. */
    public static java.util.List<Object/*com.sun.jdi.MonitorInfo*/> ownedMonitorsAndFrames0(com.sun.jdi.ThreadReference a) throws com.sun.jdi.IncompatibleThreadStateException, org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper {
        try {
            return (java.util.List<Object/*com.sun.jdi.MonitorInfo*/>) com.sun.jdi.ThreadReference.class.getMethod("ownedMonitorsAndFrames").invoke(a);
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException(ex);
        } catch (SecurityException ex) {
            throw new IllegalStateException(ex);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(ex);
        } catch (java.lang.reflect.InvocationTargetException ex) {
            Throwable t = ex.getTargetException();
            if (t instanceof com.sun.jdi.IncompatibleThreadStateException) {
                throw (com.sun.jdi.IncompatibleThreadStateException) t;
            }
            if (t instanceof com.sun.jdi.InternalException) {
                org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report((com.sun.jdi.InternalException) t);
                return java.util.Collections.emptyList();
            }
            if (t instanceof com.sun.jdi.VMDisconnectedException) {
                return java.util.Collections.emptyList();
            }
            if (t instanceof java.lang.IllegalThreadStateException) {
                throw new org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper((java.lang.IllegalThreadStateException) t);
            }
            throw new IllegalStateException(t);
        }
    }

    public static void popFrames(com.sun.jdi.ThreadReference a, com.sun.jdi.StackFrame b) throws com.sun.jdi.IncompatibleThreadStateException, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.NativeMethodExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InvalidStackFrameExceptionWrapper {
        try {
            a.popFrames(b);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper(ex);
        } catch (java.lang.IllegalThreadStateException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper(ex);
        } catch (com.sun.jdi.NativeMethodException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.NativeMethodExceptionWrapper(ex);
        } catch (com.sun.jdi.InvalidStackFrameException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.InvalidStackFrameExceptionWrapper(ex);
        }
    }

    public static void resume(com.sun.jdi.ThreadReference a) throws org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper {
        try {
            a.resume();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper(ex);
        } catch (java.lang.IllegalThreadStateException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper(ex);
        }
    }

    public static int status0(com.sun.jdi.ThreadReference a) throws org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper {
        try {
            return a.status();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return 0;
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return 0;
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            return 0;
        } catch (java.lang.IllegalThreadStateException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper(ex);
        }
    }

    public static int status(com.sun.jdi.ThreadReference a) throws org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper {
        try {
            return a.status();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper(ex);
        } catch (java.lang.IllegalThreadStateException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper(ex);
        }
    }

    public static void stop(com.sun.jdi.ThreadReference a, com.sun.jdi.ObjectReference b) throws com.sun.jdi.InvalidTypeException, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper {
        try {
            a.stop(b);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper(ex);
        } catch (java.lang.IllegalThreadStateException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper(ex);
        }
    }

    public static void suspend(com.sun.jdi.ThreadReference a) throws org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper {
        try {
            a.suspend();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper(ex);
        } catch (java.lang.IllegalThreadStateException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper(ex);
        }
    }

    public static int suspendCount0(com.sun.jdi.ThreadReference a) throws org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper {
        try {
            return a.suspendCount();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return 0;
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return 0;
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            return 0;
        } catch (java.lang.IllegalThreadStateException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper(ex);
        }
    }

    public static int suspendCount(com.sun.jdi.ThreadReference a) throws org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper {
        try {
            return a.suspendCount();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper(ex);
        } catch (java.lang.IllegalThreadStateException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper(ex);
        }
    }

    public static com.sun.jdi.ThreadGroupReference threadGroup(com.sun.jdi.ThreadReference a) throws org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper {
        try {
            return a.threadGroup();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper(ex);
        } catch (java.lang.IllegalThreadStateException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.IllegalThreadStateExceptionWrapper(ex);
        }
    }

}
