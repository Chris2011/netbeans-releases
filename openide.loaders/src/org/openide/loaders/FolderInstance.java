/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.openide.loaders;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openide.*;
import org.openide.filesystems.*;
import org.openide.cookies.InstanceCookie;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.util.RequestProcessor;

/** Support class for creation of an object from the content
* of a {@link DataObject.Container}. It implements
* {@link InstanceCookie}, so it
* can be used as a cookie for a node or data object.
* <P>
* When created on a container and started by invoking run method,
* it scans its content (in a separate
* thread) and creates a list of instances from which the new
* instance of this object should be composed. The object
* automatically listens to changes of components
* in the container, and if some change occurs, it allows the subclass to create
* a new object.
* </p>
*
* <p>Subclasses shall override the following methods:</p>
*
* <ol>
*  <li>{@link #createInstance(InstanceCookie[])} (required): this method is
*      called whenever the content has been changed. Its implementation
*      shall build up the data structures and perform the actions required
*      by this implementation.</li>
*  <li>The filter methods {@link #acceptDataObject(DataObject)}, {@link
*       #acceptCookie(InstanceCookie)},
*       {@link #acceptFolder(DataFolder)} and
*       {@link #acceptContainer(DataObject.Container)} (optional): the standard
*       way is to override one or several of the latter methods. Overriding
*       {@link #acceptDataObject(DataObject)} more deeply
*       modifies the default behavior, because the default implementation of
*       {@link #acceptDataObject(DataObject)} calls the
*       other 3 filter methods. See the method documentation for details.</li>
*  <li>The {@link InstanceCookie} methods 
*          {@link #instanceClass()} (optional but recommended)
*           to inform about the class implemented by the return value of
*          {@link #instanceCreate()}.</li>
*  <li>Advanced subclasses may need to override {@link #postCreationTask}
*      and/or {@link #instanceForCookie}, but it is not common to need these.</li>
* </ol>
*
* @author Jaroslav Tulach
*/
public abstract class FolderInstance extends Task implements InstanceCookie {
    
    /* -------------------------------------------------------------------- */
    /* -- Constants ------------------------------------------------------- */
    /* -------------------------------------------------------------------- */
    
    /** a queue to run requests in */
    private static final RequestProcessor PROCESSOR = new RequestProcessor (
      "Folder Instance Processor" // NOI18N
    );
    
    /** static variable to hold current value for callbacks to tasks 
     * Also used to synchronize access to map field on.
     */
    private static final ThreadLocal CURRENT = new ThreadLocal ();

    /** The last finished folder instance in this thread. Works together
     * with CURRENT, because sometimes more than one FolderInstance.instanceCreate
     * can be called on the same thread.
     */
    private static final ThreadLocal LAST_CURRENT = new ThreadLocal ();

    /* -------------------------------------------------------------------- */
    /* -- Instance attributes --------------------------------------------- */
    /* -------------------------------------------------------------------- */

    /** Folder to work with. Non null only if a constructor with DataFolder
     * is used to construct this object.
     */
    protected DataFolder folder;
    
    /** container to work with */
    private DataObject.Container container;

    /** map of primary file to their cookies (FileObject, HoldInstance) */
    private HashMap map = new HashMap (17);

    /** Array of tasks that we have to check before we are ok. These are the tasks
     *  associated with children of the current folder.
     */
    private Task[] waitFor;

    /** object for this cookie. Either the right instance of object or
    * an instance of IOException or ClassNotFoundException. By default 
    * it is assigned to some private object in this class to signal that
    * it is uninitialized.
    */
    private Object object = CURRENT;

    /** Listener and runner  for this object */
    private Listener listener;
    
    /** error manager for this instance */
    private Logger err;

    /** Task that computes the children list of the folder */
    private Task recognizingTask;
    
    /** A task that gets objects from InstanceCookie's and calls createInstance.
     *  Started immediately after the <code>recognizingTask</code> is finished.
     */
    private Task creationTask;
    
    /* -------------------------------------------------------------------- */
    /* -- Constructor(s) -------------------------------------------------- */
    /* -------------------------------------------------------------------- */

    /** Create new folder instance.
     * @param df data folder to create instances from
    */
    public FolderInstance (DataFolder df) {
        this ((DataObject.Container)df);
    }
    
    /** A new object that listens on changes in a container.
     * @param container the object to associate with
     * @since 1.11
     */
    public FolderInstance (DataObject.Container container) {
        this (container, null);
    }
    
    /** Constructs everything.
     * @param container container
     * @param logName the name to use for logging purposes
     */
    private FolderInstance (DataObject.Container container, String logName) {
        if (container instanceof DataFolder) {
            folder = (DataFolder)container;
            if (logName == null) {
                logName = folder.getPrimaryFile().getPath().replace('/','.');
            }
            container = FolderList.find (folder.getPrimaryFile (), true);
        }
        
        listener = new Listener ();
        
        if (logName == null) {
            logName = "org.openide.loaders.FolderInstance"; // NOI18N
        } else {
            logName = "org.openide.loaders.FolderInstance" + '.' + logName; // NOI18N
        }

        err = Logger.getLogger(logName);

        this.container = container;
        container.addPropertyChangeListener (
            org.openide.util.WeakListeners.propertyChange (listener, container)
        );
        
        if (err.isLoggable(Level.FINE)) {
            err.fine("new " + this); // NOI18N
        }
    }
    
    /* -------------------------------------------------------------------- */
    /* -- Implementation of org.openide.Cookies.InstanceCookie ------------ */
    /* -------------------------------------------------------------------- */

    /** The name of the class that we create.
    * @return the name
    */
    public String instanceName () {
        try {
            return instanceClass ().getName ();
        } catch (java.io.IOException ex) {
            return "java.lang.Object"; // NOI18N
        } catch (ClassNotFoundException ex) {
            return "java.lang.Object"; // NOI18N
        }
    }

    /** Returns the root class of all objects.
    * Supposed to be overriden in subclasses.
    *
    * @return Object.class
    * @exception IOException an I/O error occured
    * @exception ClassNotFoundException the class has not been found
    */
    public Class instanceClass ()
    throws java.io.IOException, ClassNotFoundException {
        Object object = this.object;
        if (object != null) {
            if (object instanceof java.io.IOException) {
                throw (java.io.IOException)object;
            }
            if (object instanceof ClassNotFoundException) {
                throw (ClassNotFoundException)object;
            }
            return object.getClass ();
        }

        return Object.class;
    }

    /** Creates instance.
    * @return an object to work with
    * @exception IOException an I/O error occured
    * @exception ClassNotFoundException the class has not been found
    */
    public Object instanceCreate ()
    throws java.io.IOException, ClassNotFoundException {
        Object object = CURRENT.get ();
        
        if (object == null || LAST_CURRENT.get () != this) {
            err.fine("do into waitFinished"); // NOI18N
            waitFinished ();

            object = FolderInstance.this.object;
        }

        if (err.isLoggable(Level.FINE)) {
            err.fine("instanceCreate: " + object); // NOI18N
        }

        if (object instanceof java.io.IOException) {
            throw (java.io.IOException)object;
        }
        if (object instanceof ClassNotFoundException) {
            throw (ClassNotFoundException)object;
        }
        
        if (object == CURRENT) {
            // uninitialized
            throw new IOException ("Cyclic reference. Somebody is trying to get value from FolderInstance (" + getClass ().getName () + ") from the same thread that is processing the instance"); // NOI18N
        }
        
        return object;
    }
    
    /* -------------------------------------------------------------------- */
    /* -- Wait ------------------------------------------------------------ */
    /* -------------------------------------------------------------------- */

    /** Wait for instance initialization to finish.
    */
    public final void instanceFinished () {
        waitFinished ();
    }
    
    /* -------------------------------------------------------------------- */
    /* -- Extends org.openide.util.Task ----------------------------------- */
    /* -------------------------------------------------------------------- */
    
    /** Overrides the instance finished to deal with
     * internal state correctly.
     */
    public void waitFinished () {
        boolean isLog = err.isLoggable(Level.FINE);
        for (;;) {
            err.fine("waitProcessingFinished on container"); // NOI18N
            waitProcessingFinished (container);

            Task originalRecognizing = checkRecognizingStarted ();
            if (isLog) {
                err.fine("checkRecognizingStarted: " + originalRecognizing); // NOI18N
            }
            originalRecognizing.waitFinished ();

            Task t = creationTask;
            if (isLog) {
                err.fine("creationTask: " + creationTask); // NOI18N
            }
            if (t != null) {
                t.waitFinished ();
            }


            Task[] toWait = waitFor;
            if (isLog) {
                err.fine("toWait: " + toWait); // NOI18N
            }
            if (toWait != null) {
                for (int i = 0; i < toWait.length; i++) {
                    if (isLog) {
                        err.fine("  wait[" + i + "]: " + toWait[i]); // NOI18N
                    }
                    toWait[i].waitFinished ();
                }
            }

            // loop if there was yet another task started to compute the
            // children list
            if (originalRecognizing == checkRecognizingStarted ()) {
                err.fine("breaking the wait loop"); // NOI18N
                break;
            }
            
            //
            // otherwise go on an try it once more
            //
        }
    }

    /** Synchronously starts the creation of the instance. */
    public void run () {
        recreate ();
        instanceFinished ();
    }
    
    /* -------------------------------------------------------------------- */
    /* -- Filter methods (protected, may be overridden by sub-classes) ---- */
    /* -------------------------------------------------------------------- */

    /** Allows subclasses to decide whether they want to work with the specified
    * <code>DataObject</code> or not.
    *
    * <p>The default implementation roughly performs the following steps:</p>
    *
    * <ol>
    *  <li>if <code>dob</code> has an <code>InstanceCookie</code>
    *    {@link #acceptCookie(InstanceCookie)} is called on that cookie</li>
    *  <li>if <code>dob</code> has a <code>DataFolder</code> cookie,
    *    {@link #acceptFolder(DataFolder)} is called on that folder</li>
    *  <li>if <code>dob</code> has a <code>DataObject.Container</code> cookie,
    *    {@link #acceptContainer(DataObject.Container)} is called on that
    *    container</li>
    * </ol>
    *
    * <p>The first of the aforementioned steps which returns a non-<code>null</code>
    *   cookie and does not throw an exception determines the return value. If
    *   none of the steps succeeds, <code>null</code> is returned.</p>
    *
    * @param dob a <code>DataObject</code> to test
    * @return the cookie for the <code>DataObject</code> or <code>null</code>
    * if it should not be used
    */
    protected InstanceCookie acceptDataObject(DataObject dob) {
        int acceptType = -1;
        
        InstanceCookie cookie;
        //Order of checking reversed first check cookie and then folder
        // test if we accept the instance
        cookie = (InstanceCookie)dob.getCookie (InstanceCookie.class);
        try {
            cookie = cookie == null ? null : acceptCookie (cookie);
            acceptType = 1;
        } catch (IOException ex) {
            // an error during a call to acceptCookie
            err.log(Level.WARNING, null, ex);
            cookie = null;
        } catch (ClassNotFoundException ex) {
            // an error during a call to acceptCookie
            err.log(Level.WARNING, null, ex);
            cookie = null;
        }
        
        if (cookie == null) {
            DataFolder folder = (DataFolder) dob.getCookie (DataFolder.class);
            if (folder != null) {
                HoldInstance previous = (HoldInstance)map.get (folder.getPrimaryFile ());
                if (previous != null && previous.cookie != null) {
                    // the old cookie will be returned if the folder is already registered
                    cookie = previous;
                    acceptType = 2;
                } else {
                    cookie = acceptFolder (folder);
                    acceptType = 3;
                }
            }
        }
        
        if (cookie == null) {
            // try also the container
            DataObject.Container c = (DataObject.Container)dob.getCookie (DataObject.Container.class);
            if (c != null) {
                cookie = acceptContainer (c);
                acceptType = 4;
            }
        }

        if (err.isLoggable(Level.FINE)) {
            err.fine("acceptDataObject: " + dob + " cookie: " + cookie + " acceptType: " + acceptType); // NOI18N
        }

        return cookie;
    }
    
    /** Allows subclasses to decide whether they want to work with
    * the specified <code>InstanceCookie</code> or not.
    * <p>The default implementation simply
    * returns the same cookie, but subclasses may
    * decide to return <code>null</code> or a different cookie.
    * </p>
    * <p>Compare {@link #acceptDataObject(DataObject)} to learn when this method
    * is called.</p>
    *
    * @param cookie the instance cookie to test
    * @return the cookie to use or <code>null</code> if this cookie should not
    *    be used
    * @exception IOException if an I/O error occurred calling a cookie method
    * @exception ClassNotFoundException if a class is not found in a call to a cookie method
    */
    protected InstanceCookie acceptCookie (InstanceCookie cookie)
    throws java.io.IOException, ClassNotFoundException {
        return cookie;
    }

    /** Allows subclasses to decide how they want to work with a
    * provided folder.
    *
    * <p>The default implementation simply calls {@link #acceptContainer(DataObject.Container)}.</p>
    *
     * <p>A common override of this method is to return a new
     * <code>FolderInstance</code> based on the subfolder, permitting
     * recursion.</p>
     *
    * <p>Compare {@link #acceptDataObject(DataObject)} to learn when this method
    * is called.</p>
    *
    * @param df data folder to create cookie for
    * @return the cookie for this folder or <code>null</code> if this folder should not
    *    be used
    */
    protected InstanceCookie acceptFolder (DataFolder df) {
        return acceptContainer (df);
    }
    
    /** Allows subclasses to decide how they want to work with an object
     * that implements a DataObject.Container.
     * 
     * <p>By default this returns <code>null</code> to indicated that subfolders
     * (as well as {@link DataShadow}s, etc.) should be ignored.</p>
     *
     * <p>A common override of this method is to return a new
     * <code>FolderInstance</code> based on the subfolder, permitting
     * recursion.</p>
     *
     * <p>Compare {@link #acceptDataObject(DataObject)} to learn when this method
     * is called.</p>
     *
     * @param container the container to accept or not
     * @return cookie for this container or <code>null</code> if this object should
     * be ignored
     *
     * @since 1.11
     */
    protected InstanceCookie acceptContainer (DataObject.Container container) {
        return null;
    }
    
    /* ----------------------------------------------------------------------------- */
    /* -- Instances creation method (protected, must be overridden by sub-classes) - */
    /* ----------------------------------------------------------------------------- */

    /** Notifies subclasses that the set of cookies for this folder
    * has changed.
    * A new object representing the folder should
    * be created (or the old one updated).
    * Called both upon initialization of the class, and change of its cookies.
    *
    * <p>It may be poor style for this method to have side-effects. A
    * common way to use <code>FolderInstance</code> is to have this
    * method set some global state which is then used as the resulting
    * instance. Better is to treat the <code>FolderInstance</code> as
    * pure SPI and assign it to a variable of type
    * <code>InstanceCookie</code>. Then use the {@link
    * #instanceCreate} method to get the final result. However in some
    * cases there is a singleton live object which must be updated
    * in-place, and it only makes sense to do so here (in which case
    * the <code>InstanceCookie</code> methods are unused).</p>
    *
    * @param cookies updated array of instance cookies for the folder
    * @return object to represent these cookies
    *
    * @exception IOException an I/O error occured
    * @exception ClassNotFoundException a class has not been found
    */
    protected abstract Object createInstance (InstanceCookie[] cookies)
    throws java.io.IOException, ClassNotFoundException;
    
    /* ----------------------------------------------------------------------------- */
    /* -- Instances creation method (protected, may be overridden by sub-classes) - */
    /* ----------------------------------------------------------------------------- */

    /** Method that is called when a the folder instance really wants to
    * create an object from provided cookie.
    * It allows subclasses to overwrite the default behaviour (which is
    * to call {@link InstanceCookie#instanceCreate}).
    *
    * @param obj the data object that is the source of the cookie
    * @param cookie the instance cookie to read the instance from
    * @exception IOException when there I/O error
    * @exception ClassNotFoundException if the class cannot be found
    */
    protected Object instanceForCookie (DataObject obj, InstanceCookie cookie)
    throws IOException, ClassNotFoundException {
        return cookie.instanceCreate ();
    }
    
    /* ----------------------------------------------------------------------------- */
    /* -- Recreation --------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------- */

    /** Starts recreation of the instance in special thread.
    */
    public synchronized void recreate () {
        // this method should be synchronized so the recognizingTask is created
        // together with notification that we are running.
        // Fix of #16136 => sometimes it happened that the thread started in 
        // the recognizer task was finished sooner then notifyRunning called.
        // In such case notifyFinished could be called before notifyRunning 
        // and everything was completely broken
        err.fine("recreate");
        recognizingTask = computeChildrenList (container, listener);
        if (err.isLoggable(Level.FINE)) {
            err.fine("  recognizing task is now " + recognizingTask);
        }
        notifyRunning ();
    }

    /** Checks whether recreation of this instance is running already 
     * and in that case does nothing, otherwise calls 
     * {@link #recreate() recreate} method.
     * This prevents from redundant recreation tasks of this instance caused by 
     * first creation of underlying items which are also of {@link org.openide.util.Task Task}
     * type (e.g. sub-FolderInstances, sub-FolderLookups etc.). */
    final void checkRecreate() {
        if(isFinished()) {
            recreate();
        }
    }

    /** Checks whether recreation has already started and starts it if if was
     *  was not yet started during the live of this <code>FolderInstance</code>.
     * @return the latest started task for children computation */
    private final synchronized Task checkRecognizingStarted () {
        if(recognizingTask == null) {
            recreate();
        }
        
        return recognizingTask;
    }

    /* ----------------------------------------------------------------------------- */
    /* -- Static helper methods (abstract away the differences between different --- */
    /* ------------------------- DataObject.Container types) ----------------------- */
    /* ----------------------------------------------------------------------------- */
    
    /** Waits until the task to compute the children of the currents folder is
     * finished. This methods provides a unified interface which allows to
     * treat <code>FolderList</code>s and general <code>DataObject.Container</code>s
     * is a uniform way.
     */
    private static void waitProcessingFinished (
        DataObject.Container c
    ) {
        if (c instanceof FolderList) {
            ((FolderList)c).waitProcessingFinished ();
        }
    }
    
    /** Starts and returns the task to compute the children of the current
     * folder. This methods provides a unified interface which allows to
     * treat <code>FolderList</code>s and general <code>DataObject.Container</code>s
     * is a uniform way.
     *
     * <p>The task returned uses the {@link #listener} to process the children.</p>
     */
    private static Task computeChildrenList (
        final DataObject.Container container, final FolderListListener listener
    ) {
        if (container instanceof FolderList) {
            FolderList list = (FolderList)container;
            return list.computeChildrenList (listener);
        }
        
        // otherwise we have to simulate the listener by container methods
        // itself
        return PROCESSOR.post (new Runnable () {
            public void run () {
                DataObject[] arr = container.getChildren ();
                ArrayList list = new ArrayList (arr.length);
                for (int i = 0; i < arr.length; i++) {
                    listener.process (arr[i], list);
                }
                listener.finished (list);
            }
        });
    }
    
    /* ----------------------------------------------------------------------------- */
    /* -- Processing --------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------- */

    /** A method that starts <code>creationTask</code>, the task which really
     *  creates the instances from given objects. The task is started by a
     * call to {@link #postCreationTask(Runnable)}.
    * 
    * @param arr collection of DataObjects
    */
    final void processObjects (final Collection arr) {
        creationTask = postCreationTask (new Runnable () {
            public void run () {
                defaultProcessObjects (arr);
            }
        });
    }

    /** Default processing of objects.
    * @param arr array of objects to process
    */
    private final void defaultProcessObjects (Collection arr) {
        err.fine("defaultProcessObjects");
        HashSet toRemove;
        ArrayList cookies = new ArrayList ();

        // synchronized for safe access to map field
        synchronized (CURRENT) {
            toRemove = new HashSet (map.keySet ());
        }
            
        Iterator dataObjects = arr.iterator ();
        while (dataObjects.hasNext ()) {
            DataObject obj = (DataObject)dataObjects.next ();
            if (! obj.isValid()) {
                // #12960: skip over it, probably invalidated while we were
                // waiting for this task to be run...
                continue;
            }

            // testing
            InstanceCookie cookie = acceptDataObject(obj);
            if (cookie != null) {
                // cookie accepted
                FileObject fo = obj.getPrimaryFile ();
                
                boolean attachListener = true;
                HoldInstance prevCookie = null;
                if (toRemove.remove (fo)) {
                    // if the fo is in the map than try to find its cookie
                    prevCookie = (HoldInstance)map.get (fo);
                    if (prevCookie != null && (prevCookie.cookie == null || !prevCookie.cookie.equals (cookie))) {
                        prevCookie = null;
                        // #49199 - do not add second listener
                        attachListener = false;
                    }
                }
                
                if (prevCookie == null) {
                    // such cookie is not there yet
                    HoldInstance hold;
                    
                    if (cookie instanceof HoldInstance) {
                        hold = (HoldInstance)cookie;
                    } else {
                        hold = new HoldInstance(obj, cookie);
                    }
                    
                    // synchronized for safe access to map field
                    synchronized (CURRENT) {
                        map.put (fo, hold);
                    }
                
                    // register for changes of PROP_COOKIE property
                    if (attachListener) {
                        obj.addPropertyChangeListener (
                            org.openide.util.WeakListeners.propertyChange (listener, obj)
                        );
                    }
                    
                    cookies.add (hold);
                } else {
                    // old cookie, already there => only add it to the list of cookies
                    cookies.add (prevCookie);
                }
            } else {
                // empty instance placeholder
                synchronized (CURRENT) {
                    FileObject fo = obj.getPrimaryFile ();
                    toRemove.remove (fo);
                    
                    HoldInstance hold = (HoldInstance)map.get (fo);
                    if (hold != null && hold.cookie == null) {
                        // already registered do not do any changes
                        continue;
                    }
                    
                    // not yet registered, add new
                    
                    hold = new HoldInstance (obj, null);
                    
                    map.put (fo, hold);
                }
                
                // register for changes of PROP_COOKIE property
                obj.addPropertyChangeListener (
                    org.openide.util.WeakListeners.propertyChange (listener, obj)
                );
                
            }
            
        }

        // synchronized for safe access to map field
        synchronized (CURRENT) {
            // now remove the cookies that are no longer in the folder
            map.keySet ().removeAll (toRemove);
        }

        // create the list of cookies
        HoldInstance[] all = new HoldInstance[cookies.size ()];
        cookies.toArray (all);
        
        updateWaitFor (all);

        Object result = null;
        try {
            result = createInstance (all);
        } catch (IOException ex) {
            result = ex;
        } catch (ClassNotFoundException ex) {
            result = ex;
        } finally {
            if (err.isLoggable(Level.FINE)) {
                err.fine("notifying finished"); // NOI18N
                for (int log = 0; log < all.length; log++) {
                    err.fine("  #" + log + ": " + all[log]); // NOI18N
                }
            }
            object = result;
            
            Object prevResult = CURRENT.get ();
            CURRENT.set (result);
            Object prevLast = LAST_CURRENT.get ();
            LAST_CURRENT.set (this);
            
            try {
                notifyFinished ();
            } finally {
                CURRENT.set (prevResult);
                LAST_CURRENT.set (prevLast);
            }
        }
    }
    
    /** Recomputes the list of tasks we should wait for (i.e. the tasks associated
     *  with the children of the folder).
     */
    private void updateWaitFor (HoldInstance[] arr) {
        ArrayList out = new ArrayList (arr.length);
        for (int i = 0; i < arr.length; i++) {
            Task t = arr[i].getTask ();
            if (t != null) {
                out.add (t);
            }
        }
        waitFor = (Task[])out.toArray (new Task[out.size ()]);
    }
    
    /* ----------------------------------------------------------------------------- */
    /* -- Processing: Start the creation task (protected, may be overridden) ------- */
    /* ----------------------------------------------------------------------------- */
    
    /** Invokes the creation of objects in a "safe" thread. This method is
    * for expert subclasses that want to control the thread that the 
    * instance is created in.
    *
    * <p>The default implementation invokes the creation logic in the
    * request processor in non-blocking mode (no other tasks will
    * block on this).</p>
    *
    * @param run runnable to run
    * @return task to control the execution of the runnable or null if 
    *    the runnable is run immediatelly
    * @since 1.5
    */
    protected Task postCreationTask (Runnable run) {
        return PROCESSOR.post (run);
    }
    
    /* ----------------------------------------------------------------------------- */
    /* -- Getters ------------------------------------------------------------------ */
    /* ----------------------------------------------------------------------------- */
    
    /** Access to error manager for FolderLookup.
     */
    final Logger err () {
        return err;
    }
    
    public String toString () {
        return getClass ().getName () + "@" + Integer.toHexString (System.identityHashCode (this)) + "(" + this.container + ")"; // NOI18N
    }
    
    /* -------------------------------------------------------------------- */
    /* -- Inner class Listener -------------------------------------------- */
    /* -------------------------------------------------------------------- */

    /** Listener on change of folder's children and a starter for the task.
     *
     * <p>Each instance of {@link FolderInstance} has one instance of this class
     *  associated with it. The latter serves for three purposes:</p>
     *
     * <ol>
     *   <li>to listen for property changes of the {@link DataObject.Container}
     *        this {@link FolderInstance} was created for (by implementing
     *        {@link java.beans.PropertyChangeListener})</li>
     *    <li>to listen for changes of the cookies of the children of this folder</li>
     *   <li>to process the results of the computation of a child list
     *      (by implementing {@link FolderListListener#finished(java.util.List)})
     *   </li>
     * </ol>
    */
    private class Listener implements PropertyChangeListener, FolderListListener {
        
        Listener() {}

        /** Recreates the {@link FolderInstance} if the children list of
         *  its container was changed.
         *
         * <p>Additionally ...</p>
         */
        public void propertyChange (PropertyChangeEvent ev) {
            Object s = ev.getSource ();
            if (s == container) {
                if (DataObject.Container.PROP_CHILDREN.equals(ev.getPropertyName ())) {
                    err.fine("PROP_CHILDREN");

                    recreate();
                }
                return;
            }

            if (DataObject.PROP_NAME.equals(ev.getPropertyName())) {
                if (s instanceof DataObject) {
                    err.fine("PROP_NAME");
                    recreate();
                }
            }
            
            // change of cookie in one of children of the container
            
            if (DataObject.PROP_COOKIE.equals(ev.getPropertyName ())) {
                if (s instanceof DataObject) {
                    DataObject source = (DataObject)s;
                    err.fine("PROP_COOKIE: " + source); // NOI18N

                    InstanceCookie ic = acceptDataObject (source);
                    
                    HoldInstance hi;
                    FileObject fo = source.getPrimaryFile();
                    synchronized (CURRENT) {
                        hi = (HoldInstance)map.get(fo);
                    }
                
                    if (hi != null) {
                        err.fine("previous instance: " + hi + " new instance " + ic); // NOI18N
                        /* Recreate if the new instance cookie is null or differs
                         * from the previous one.
                         * When the default implementation of acceptDataObject is
                         * used ic == hi is the case if source is a folder.
                         * [XXX] Why not:
                         * if ((ic == null && hi.cookie != null) || (ic != hi && !ic.equals (hi.cookie))) { 
                         */
                        if (ic == null || (ic != hi && !ic.equals (hi.cookie))) { 
                            hi = new HoldInstance(source, ic);

                            // synchronized for safe access to map field
                            synchronized (CURRENT) {
                                map.put (fo, hi);
                            }
                            recreate ();
                        }
                    }
                }
            }
        }

        /** Callback for object processing after all children are computed.
         *  This implementation starts a new task for the creation of the
         *  child objects.
         * @param arr list of DataObjects
         */
        public void finished(java.util.List arr) {
            processObjects (arr);
        }

        /** Default implementation without filtering.
         * @param obj the object recognized
         * @param arr array where the implementation should add the 
         *   object
         */
        public void process(DataObject obj, java.util.List arr) {
            arr.add (obj);
        }

    }
    
    /* -------------------------------------------------------------------- */
    /* -- Inner class HoldInstance ---------------------------------------- */
    /* -------------------------------------------------------------------- */

    /** A instance cookie that holds the result of first
    * invocation of the provided cookie.
    *
    */
    private class HoldInstance extends Object 
    implements InstanceCookie.Of, TaskListener {
        /** the data object -> source of this instance */
        private final DataObject source;
        /** the cookie to delegate to */
        protected final InstanceCookie cookie;

        public HoldInstance (DataObject source, InstanceCookie cookie) {
            this.cookie = cookie;
            this.source = source;

            if (cookie instanceof Task) {
                // for example FolderInstance ;-) attach itself for changes
                // in the cookie
                Task t = (Task)cookie;
                t.addTaskListener (
                    (TaskListener)org.openide.util.WeakListeners.create (TaskListener.class, this, t)
                );
            }
        }

        /** Full name of the data folder's primary file separated by dots.
        * @return the name
        */
        public String instanceName () {
            return cookie.instanceName ();
        }

        /** Query to find out if the object created by this cookie is 
         * instance of given type. The same code as:
         * <pre>
         *   Class actualClass = instanceClass ();
         *   result = type.isAsignableFrom (actualClass);
         * </pre>
         * But this can prevent the class <code>actualClass</code> to be
         * loaded into the <em>JavaVM</em>.
         *
         * @param type the class type we want to check
         * @return true if this cookie can produce object of given type
         */
        public boolean instanceOf(Class type) {
            if (cookie instanceof InstanceCookie.Of) {
                InstanceCookie.Of of = (InstanceCookie.Of)cookie;
                return of.instanceOf (type);
            }
            // delegate
            try {
                Class clazz = cookie.instanceClass ();
                return type.isAssignableFrom (clazz);
            } catch (IOException ex) {
                return false;
            } catch (ClassNotFoundException ex) {
                return false;
            }
        }

        /** Returns the root class of all objects.
        * Supposed to be overriden in subclasses.
        *
        * @return Object.class
        * @exception IOException an I/O error occured
        * @exception ClassNotFoundException the class has not been found
        */
        public Class instanceClass ()
        throws java.io.IOException, ClassNotFoundException {
            return cookie.instanceClass ();
        }

        /**
        * @return an object to work with
        * @exception IOException an I/O error occured
        * @exception ClassNotFoundException the class has not been found
        */
        public Object instanceCreate ()
        throws java.io.IOException, ClassNotFoundException {
            return instanceForCookie (source, cookie);
        }

        /** Called when a task finishes running.
         * @param task the finished task
         */
        public void taskFinished(Task task) {
            checkRecreate();
        }
        
        /** Waits till the instance is ready.
         */
        public Task getTask () {
            if (cookie instanceof Task) {
                // for example FolderInstance ;-) attach itself for changes
                // in the cookie
                return (Task)cookie;
            } else {
                return null;
            }
        }
    } // end of HoldInstance
    
}
