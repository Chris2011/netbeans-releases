/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.openide.util;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

/** Read-many/write-one lock.
* Allows control over resources that
* can be read by several readers at once but only written by one writer.
* <P>
* It is guaranteed that if you are a writer you can also enter the
* mutex as a reader. Conversely, if you are the <em>only</em> reader you
* can enter the mutex as a writer, but you'll be warned because it is very
* deadlock prone (two readers trying to get write access concurently).
* <P>
* If the mutex is used only by one thread, the thread can repeatedly
* enter it as a writer or reader. So one thread can never deadlock itself,
* whichever order operations are performed in.
* <P>
* There is no strategy to prevent starvation.
* Even if there is a writer waiting to enter, another reader might enter
* the section instead.
* <P>
* Examples of use:
*
* <pre>
* Mutex m = new Mutex();
*
* // Grant write access, compute an integer and return it:
* return m.writeAccess(new Mutex.Action&lt;Integer>(){
*     public Integer run() {
*         return 1;
*     }
* });
*
* // Obtain read access, do some computation,
* // possibly throw an IOException:
* try {
*     m.readAccess(new Mutex.ExceptionAction&lt;Void>() {
*         public Void run() throws IOException {
*             if (...) throw new IOException();
*             return null;
*         }
*     });
* } catch (MutexException ex) {
*     throw (IOException) ex.getException();
* }
*
* // check whether you are already in read access
* if (m.isReadAccess()) {
*     // do your work
* }
* </pre>
*
* @author Ales Novak
*/
public final class Mutex {
    /**
     * The actual delegate, which performs the work
     */
    private final ReadWriteAccess delegate;
    
    /** logger for things that happen in mutex */
    private static final Logger LOG = Logger.getLogger(Mutex.class.getName());

    /** Mutex that allows code to be synchronized with the AWT event dispatch thread.
     * <P>
     * When the Mutex methods are invoked on this mutex, the methods' semantics 
     * change as follows:
     * <UL>
     * <LI>The {@link #isReadAccess} and {@link #isWriteAccess} methods
     *  return <code>true</code> if the current thread is the event dispatch thread
     *  and false otherwise.
     * <LI>The {@link #postReadRequest} and {@link #postWriteRequest} methods
     *  asynchronously execute the {@link java.lang.Runnable} passed in their 
     *  <code>run</code> parameter on the event dispatch thead.
     * <LI>The {@link #readAccess(java.lang.Runnable)} and 
     *  {@link #writeAccess(java.lang.Runnable)} methods asynchronously execute the 
     *  {@link java.lang.Runnable} passed in their <code>run</code> parameter 
     *  on the event dispatch thread, unless the current thread is 
     *  the event dispatch thread, in which case 
     *  <code>run.run()</code> is immediately executed.
     * <LI>The {@link #readAccess(Mutex.Action)},
     *  {@link #readAccess(Mutex.ExceptionAction action)},
     *  {@link #writeAccess(Mutex.Action action)} and
     *  {@link #writeAccess(Mutex.ExceptionAction action)} 
     *  methods synchronously execute the {@link Mutex.ExceptionAction}
     *  passed in their <code>action</code> parameter on the event dispatch thread,
     *  unless the current thread is the event dispatch thread, in which case
     *  <code>action.run()</code> is immediately executed.
     * </UL>
     */
    public static final Mutex EVENT = new Mutex();

    // lock mode constants

    public Mutex(Object lock) {
        this.delegate = ReadWriteAccess.usingLock(lock);
    }

    /** Default constructor.
    */
    public Mutex() {
        this.delegate = ReadWriteAccess.create();
    }

    /** @param privileged can enter privileged states of this Mutex
     * This helps avoid creating of custom Runnables.
     */
    public Mutex(Privileged privileged) {
        this.delegate = ReadWriteAccess.controlledBy(privileged.delegate);
    }

    /** Constructor for those who wish to do some custom additional tasks
     * whenever an action or runnable is executed in the {@link Mutex}. This
     * may be useful for wrapping all the actions with custom {@link ThreadLocal}
     * value, etc. Just implement the {@link Executor}'s <code>execute(Runnable)</code>
     * method and do pre and post initialization tasks before running the runnable.
     * <p>
     * The {@link Executor#execute} method shall return only when the passed in
     * {@link Runnable} is finished, otherwise methods like {@link Mutex#readAccess(Action)} and co.
     * might not return proper result.
     * 
     * @param privileged can enter privileged states of this Mutex
     *  @param executor allows to wrap the work of the mutex with a custom code
     * @since 7.12
     * @see SimpleMutex#SimpleMutex(org.openide.util.SimpleMutex.Privileged, java.util.concurrent.Executor)
     */
    public Mutex(Privileged privileged, Executor executor) {
        this.delegate = ReadWriteAccess.controlledBy(privileged.delegate, executor);
    }

    /** Run an action only with read access.
    * See class description re. entering for write access within the dynamic scope.
    * @param action the action to perform
    * @return the object returned from {@link Mutex.Action#run}
    */
    public <T> T readAccess(final Action<T> action) {
        if (this == EVENT) {
            try {
                return doEventAccess(action);
            } catch (MutexException e) {
                throw (InternalError) new InternalError("Exception from non-Exception Action").initCause(e.getException()); // NOI18N
            }
        }
        return delegate.readAccess(action);
    }
    

    /** Run an action with read access and possibly throw a checked exception.
    * The exception if thrown is then encapsulated
    * in a <code>MutexException</code> and thrown from this method. One is encouraged
    * to catch <code>MutexException</code>, obtain the inner exception, and rethrow it.
    * Here is an example:
    * <p><code><PRE>
    * try {
    *   mutex.readAccess (new ExceptionAction () {
    *     public void run () throws IOException {
    *       throw new IOException ();
    *     }
    *   });
    *  } catch (MutexException ex) {
    *    throw (IOException) ex.getException ();
    *  }
    * </PRE></code>
    * Note that <em>runtime exceptions</em> are always passed through, and neither
    * require this invocation style, nor are encapsulated.
    * @param action the action to execute
    * @return the object returned from {@link Mutex.ExceptionAction#run}
    * @exception MutexException encapsulates a user exception
    * @exception RuntimeException if any runtime exception is thrown from the run method
    * @see #readAccess(Mutex.Action)
    */
    public <T> T readAccess(final ExceptionAction<T> action) throws MutexException {
        if (this == EVENT) {
            return doEventAccess(action);
        }
        return delegate.readAccess(action);
    }

    /** Run an action with read access, returning no result.
    * It may be run asynchronously.
    *
    * @param action the action to perform
    * @see #readAccess(Mutex.Action)
    */
    public void readAccess(final Runnable action) {
        if (this == EVENT) {
            doEvent(action);

            return;
        }
        delegate.readAccess(action);
    }

    /** Run an action with write access.
    * The same thread may meanwhile reenter the mutex; see the class description for details.
    *
    * @param action the action to perform
    * @return the result of {@link Mutex.Action#run}
    */
    public <T> T writeAccess(Action<T> action) {
        if (this == EVENT) {
            try {
                return doEventAccess(action);
            } catch (MutexException e) {
                throw (InternalError) new InternalError("Exception from non-Exception Action").initCause(e.getException()); // NOI18N
            }
        }
        return delegate.writeAccess(action);
    }

    /** Run an action with write access and possibly throw an exception.
    * Here is an example:
    * <p><code><PRE>
    * try {
    *   mutex.writeAccess (new ExceptionAction () {
    *     public void run () throws IOException {
    *       throw new IOException ();
    *     }
    *   });
    *  } catch (MutexException ex) {
    *    throw (IOException) ex.getException ();
    *  }
    * </PRE></code>
    *
    * @param action the action to execute
    * @return the result of {@link Mutex.ExceptionAction#run}
    * @exception MutexException an encapsulated checked exception, if any
    * @exception RuntimeException if a runtime exception is thrown in the action
    * @see #writeAccess(Mutex.Action)
    * @see #readAccess(Mutex.ExceptionAction)
    */
    public <T> T writeAccess(ExceptionAction<T> action) throws MutexException {
        return delegate.writeAccess(action);
    }

    /** Run an action with write access and return no result.
    * It may be run asynchronously.
    *
    * @param action the action to perform
    * @see #writeAccess(Mutex.Action)
    * @see #readAccess(Runnable)
    */
    public void writeAccess(final Runnable action) {
        if (this == EVENT) {
            doEvent(action);

            return;
        }
        delegate.writeAccess(action);
    }

    /** Tests whether this thread has already entered the mutex in read access.
     * If it returns true, calling <code>readAccess</code>
     * will be executed immediatelly
     * without any blocking.
     * Calling <code>postWriteAccess</code> will delay the execution
     * of its <code>Runnable</code> until a readAccess section is over
     * and calling <code>writeAccess</code> is strongly prohibited and will
     * result in a warning as a deadlock prone behaviour.
     * <p><strong>Warning:</strong> since a thread with write access automatically
     * has effective read access as well (whether or not explicitly requested), if
     * you want to check whether a thread can read some data, you should check for
     * either kind of access, e.g.:
     * <pre>assert myMutex.isReadAccess() || myMutex.isWriteAccess();</pre>
     *
     * @return true if the thread is in read access section
     * @since 4.48
     */
    public boolean isReadAccess() {
        if (this == EVENT) {
            return javax.swing.SwingUtilities.isEventDispatchThread();
        }
        return delegate.isReadAccess();
    }

    /** Tests whether this thread has already entered the mutex in write access.
     * If it returns true, calling <code>writeAccess</code> will be executed
     * immediatelly without any other blocking. <code>postReadAccess</code>
     * will be delayed until a write access runnable is over.
     *
     * @return true if the thread is in write access section
     * @since 4.48
     */
    public boolean isWriteAccess() {
        if (this == EVENT) {
            return javax.swing.SwingUtilities.isEventDispatchThread();
        }
        return delegate.isWriteAccess();
    }

    /** toString */
    @Override
    public String toString() {
        if (this == EVENT) {
            return "Mutex.EVENT"; // NOI18N
        }
        return delegate.toString();
    }

    // priv methods  -----------------------------------------

    /** Posts a read request. This request runs immediately iff
     * this SimpleMutex is in the shared mode or this SimpleMutex is not contended
     * at all.
     *
     * This request is delayed if this SimpleMutex is in the exclusive
     * mode and is held by this thread, until the exclusive is left.
     *
     * Finally, this request blocks, if this SimpleMutex is in the exclusive
     * mode and is held by another thread.
     *
     * <p><strong>Warning:</strong> this method blocks.</p>
     *
     * @param run runnable to run
     */
    public void postReadRequest(final Runnable run) {
        if (this == EVENT) {
            doEventRequest(run);

            return;
        }
        delegate.postReadRequest(run);
    }

    /** Posts a write request. This request runs immediately iff
     * this SimpleMutex is in the "pure" exclusive mode, i.e. this SimpleMutex
     * is not reentered in shared mode after the exclusive mode
     * was acquired. Otherwise it is delayed until all read requests
     * are executed.
     *
     * This request runs immediately if this SimpleMutex is not contended at all.
     *
     * This request blocks if this SimpleMutex is in the shared mode.
     *
     * <p><strong>Warning:</strong> this method blocks.</p>
     * @param run runnable to run
     */
    public void postWriteRequest(Runnable run) {
        if (this == EVENT) {
            doEventRequest(run);

            return;
        }
        delegate.postWriteRequest(run);
    }
    
    // ------------------------------- EVENT METHODS ----------------------------

    /** Runs the runnable in event queue, either immediatelly,
    * or it posts it into the queue.
    */
    private static void doEvent(Runnable run) {
        if (EventQueue.isDispatchThread()) {
            run.run();
        } else {
            EventQueue.invokeLater(run);
        }
    }

    /** Methods for access to event queue.
    * @param run runabble to post later
    */
    private static void doEventRequest(Runnable run) {
        EventQueue.invokeLater(run);
    }

    /** Methods for access to event queue and waiting for result.
    * @param run runnable to post later
    */
    private static <T> T doEventAccess(final ReadWriteAccess.ExceptionAction<T> run)
    throws MutexException {
        if (isDispatchThread()) {
            try {
                return run.run();
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new MutexException(e);
            }
        }

        final AtomicReference<Union2<T,Throwable>> res = new AtomicReference<Union2<T,Throwable>>();
        final AtomicBoolean started = new AtomicBoolean(); // #210991
        final AtomicBoolean finished = new AtomicBoolean();
        final AtomicBoolean invoked = new AtomicBoolean();
        try {
            class AWTWorker implements Runnable {
                @Override
                public void run() {
                    started.set(true);
                    try {
                        res.set(Union2.<T,Throwable>createFirst(run.run()));
                    } catch (Exception e) {
                        res.set(Union2.<T,Throwable>createSecond(e));
                    } catch (LinkageError e) {
                        // #20467
                        res.set(Union2.<T,Throwable>createSecond(e));
                    } catch (StackOverflowError e) {
                        // #20467
                        res.set(Union2.<T,Throwable>createSecond(e));
                    }
                    finished.set(true);
                }
            }

            AWTWorker w = new AWTWorker();
            EventQueue.invokeAndWait(w);
            invoked.set(true);
        } catch (InterruptedException e) {
            res.set(Union2.<T,Throwable>createSecond(e));
        } catch (InvocationTargetException e) {
            res.set(Union2.<T,Throwable>createSecond(e));
        }

        Union2<T,Throwable> _res = res.get();
        if (_res == null) {
            throw new IllegalStateException("#210991: got neither a result nor an exception; started=" + started + " finished=" + finished + " invoked=" + invoked);
        } else if (_res.hasFirst()) {
            return _res.first();
        } else {
            Throwable e = _res.second();
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw notifyException(e);
            }
        }
    }

    /** @return true iff current thread is EventDispatchThread */
    static boolean isDispatchThread() {
        boolean dispatch = EventQueue.isDispatchThread();

        if (!dispatch && (Utilities.getOperatingSystem() == Utilities.OS_SOLARIS)) {
            // on solaris the event queue is not always recognized correctly
            // => try to guess by name
            dispatch = (Thread.currentThread().getClass().getName().indexOf("EventDispatchThread") >= 0); // NOI18N
        }

        return dispatch;
    }

    /** Notify exception and returns new MutexException */
    private static MutexException notifyException(Throwable t) {
        if (t instanceof InvocationTargetException) {
            t = unfoldInvocationTargetException((InvocationTargetException) t);
        }

        if (t instanceof Error) {
            annotateEventStack(t);
            throw (Error) t;
        }

        if (t instanceof RuntimeException) {
            annotateEventStack(t);
            throw (RuntimeException) t;
        }

        MutexException exc = new MutexException((Exception) t);
        exc.initCause(t);

        return exc;
    }

    private static void annotateEventStack(Throwable t) {
        //ErrorManager.getDefault().annotate(t, new Exception("Caught here in mutex")); // NOI18N
    }

    private static Throwable unfoldInvocationTargetException(InvocationTargetException e) {
        Throwable ret;

        do {
            ret = e.getTargetException();

            if (ret instanceof InvocationTargetException) {
                e = (InvocationTargetException) ret;
            } else {
                e = null;
            }
        } while (e != null);

        return ret;
    }

    // --------------------------------------------- END OF EVENT METHODS ------------------------------

    /** Action to be executed in a mutex without throwing any checked exceptions.
    * Unchecked exceptions will be propagated to calling code.
     * @param T the type of object to return
    */
    @SuppressWarnings("PublicInnerClass")
    public interface Action<T> extends ReadWriteAccess.Action<T>, ExceptionAction<T> {
        /** Execute the action.
        * @return any object, then returned from {@link Mutex#readAccess(Mutex.Action)} or {@link Mutex#writeAccess(Mutex.Action)}
        */
        @Override
        T run();
    }

    /** Action to be executed in a mutex, possibly throwing checked exceptions.
    * May throw a checked exception, in which case calling
    * code should catch the encapsulating exception and rethrow the
    * real one.
    * Unchecked exceptions will be propagated to calling code without encapsulation.
     * @param T the type of object to return
    */
    @SuppressWarnings("PublicInnerClass")
    public interface ExceptionAction<T> extends ReadWriteAccess.ExceptionAction<T> {
        /** Execute the action.
        * Can throw an exception.
        * @return any object, then returned from {@link Mutex#readAccess(Mutex.ExceptionAction)} or {@link Mutex#writeAccess(Mutex.ExceptionAction)}
        * @exception Exception any exception the body needs to throw
        */
        T run() throws Exception;
    }

    /** Provides access to Mutex's internal methods.
     *
     * This class can be used when one wants to avoid creating a
     * bunch of Runnables. Instead,
     * <pre>
     * try {
     *     enterXAccess ();
     *     yourCustomMethod ();
     * } finally {
     *     exitXAccess ();
     * }
     * </pre>
     * can be used.
     *
     * You must, however, control the related Mutex, i.e. you must be creator of
     * the Mutex.
     *
     * @since 1.17
     */
    public static final class Privileged {
        private final ReadWriteAccess.Privileged delegate;
        
        public Privileged() {
            this.delegate = new ReadWriteAccess.Privileged();
        }

        public void enterReadAccess() {
            delegate.enterReadAccess();
        }
        
        /** Tries to obtain read access. If the access cannot by
         * gained by given milliseconds, the method returns without gaining
         * it.
         * 
         * @param timeout amount of milliseconds to wait before giving up.
         *   <code>0</code> means to wait indefinitely.
         *   <code>-1</code> means to not wait at all and immediately exit
         * @return <code>true</code> if the access has been granted, 
         *   <code>false</code> otherwise
         * @since 8.37
         */
        public boolean tryReadAccess(long timeout) {
            return delegate.tryReadAccess(timeout);
        }

        public void enterWriteAccess() {
            delegate.enterWriteAccess();
        }
        
        /**
         * Tries to obtain write access. If the access cannot by gained by given
         * milliseconds, the method returns without gaining it.
         *
         * @param timeout amount of milliseconds to wait before giving up.
         *   <code>0</code> means to wait indefinitely.
         *   <code>-1</code> means to not wait at all and immediately exit
         * @return <code>true</code> if the access has been granted,
         * <code>false</code> otherwise
         * @since 8.37
         */
        public boolean tryWriteAccess(long timeout) {
            return delegate.tryWriteAccess(timeout);
        }

        public void exitReadAccess() {
            delegate.exitReadAccess();
        }

        public void exitWriteAccess() {
            delegate.exitWriteAccess();
        }
    }
}
