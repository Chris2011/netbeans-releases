/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.core;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.Iterator;
import java.text.MessageFormat;
import javax.swing.*;
import javax.swing.text.Keymap;
import javax.swing.border.*;

import org.openide.util.datatransfer.ExClipboard;
import org.openide.*;
import org.openide.awt.HtmlBrowser;
import org.openide.loaders.*;
import org.openide.actions.*;
import org.openide.cookies.SaveCookie;
import org.openide.cookies.ProjectCookie;
import org.openide.debugger.Debugger;
import org.openide.debugger.DebuggerNotFoundException;
import org.openide.filesystems.*;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.JarFileSystem;
import org.openide.modules.Dependency;
import org.openide.modules.SpecificationVersion;
import org.openide.options.ControlPanel;
import org.openide.windows.WindowManager;
import org.openide.windows.OutputWriter;
import org.openide.windows.InputOutput;
import org.openide.windows.TopComponent;
import org.openide.explorer.*;
import org.openide.explorer.view.BeanTreeView;
import org.openide.util.*;
import org.openide.util.io.*;
import org.openide.nodes.*;
import org.openide.util.lookup.*;

import org.netbeans.core.actions.*;
import org.netbeans.core.output.OutputTabTerm;
import org.netbeans.core.windows.WindowManagerImpl;
import org.netbeans.core.execution.TopSecurityManager;
import org.netbeans.core.perftool.StartLog;
import org.netbeans.core.modules.ModuleManager;
import org.netbeans.core.modules.ModuleSystem;
import org.netbeans.core.windows.util.WindowUtils;

/** This class is a TopManager for Corona environment.
*
* @author Ales Novak, Jaroslav Tulach, Ian Formanek, Petr Hamernik, Jan Jancura, Jesse Glick
*/
public abstract class NbTopManager extends TopManager {
    /* masks to define the interactivity level */

    /** initialize the main window?
    * if not set the main window is not create nor shown.
    */
    public static final int IL_MAIN_WINDOW = 0x0001;
    /** initialize window system?
    * if not set the selected node is taken from the top manager.
    */
    public static final int IL_WINDOWS = 0x0002;
    /** initialize workspaces when not created?
    */
    public static final int IL_WORKSPACES = 0x0004;


    /** Initialize everything.
    */
    public static final int IL_ALL = 0xffff;


    /** property for status text */
    public static final String PROP_STATUS_TEXT = "statusText"; // NOI18N
    
    /** property for system class loader */
    public static final String PROP_SYSTEM_CLASS_LOADER = "systemClassLoader"; // NOI18N
    /** property for current class loader */
    public static final String PROP_CURRENT_CLASS_LOADER = "currentClassLoader"; // NOI18N

    /** stores main shortcut context*/
    private Keymap shortcutContext;

    /** inner access to dynamic lookup service for this top mangager */
    private InstanceContent instanceContent;
    /** dynamic lookup service for this top mangager */
    private Lookup instanceLookup;

    /** default repository */
    private Repository repository;

    /** error manager */
    private static ErrorManager defaultErrorManager;

    /** WWW browser window. */
    private HtmlBrowser.BrowserComponent htmlViewer;

    /** ProjectOperation main variable */
    static NbProjectOperation projectOperation;

    /** support for listeners */
    private PropertyChangeSupport change = new PropertyChangeSupport (this);

    /** repository */
    private Repository defaultRepository;

    /** loader pool */
    private DataLoaderPool loaderPool;

    /** status text */
    private String statusText = " "; // NOI18N

    /** the debugger listener listening on adding/removing debugger*/
    private static LookupListener debuggerLsnr = null;
    /** the lookup query finding all registered debuggers */
    private static Lookup.Result debuggerLkpRes = null;
    
    /** initializes properties about builds etc. */
    static {
        // Set up module-versioning properties, which logger prints.
        Package p = Package.getPackage ("org.openide"); // NOI18N
        
        putSystemProperty ("org.openide.specification.version", p.getSpecificationVersion (), "3.12"); // NOI18N
        putSystemProperty ("org.openide.version", p.getImplementationVersion (), "OwnBuild"); // NOI18N
        putSystemProperty ("org.openide.major.version", p.getSpecificationTitle (), "IDE/1"); // NOI18N
        putSystemProperty ("netbeans.buildnumber", p.getImplementationVersion (), "OwnBuild"); // NOI18N
        
        if (System.getProperties ().get ("org.openide.util.Lookup") == null) { // NOI18N
          // update the top manager to our main if it has not been provided yet
          System.getProperties().put (
            "org.openide.util.Lookup", // NOI18N
            "org.netbeans.core.NbTopManager$Lkp" // NOI18N
          );
        }
        
        // Enforce JDK 1.3+ since we would not work without it.
        if (Dependency.JAVA_SPEC.compareTo(new SpecificationVersion("1.3")) < 0) { // NOI18N
            System.err.println("The IDE requires JDK 1.3 or higher to run."); // XXX I18N?
            TopSecurityManager.exit(1);
        }

        // In the past we derived ${jdk.home} from ${java.home} by appending
        // "/.." to the end of ${java.home} assuming that JRE is under JDK
        // directory.  It does not always work.  On MacOS X JDK and JRE files
        // are mixed together, thus ${jdk.home} == ${java.home}.  In several
        // Linux distros JRE and JDK are installed at the same directory level
        // with ${jdk.home}/jre a symlink to ${java.home}, which means
        // ${java.home}/.. != ${jdk.home}.
        //
        // Now the launcher can set ${jdk.home} explicitly because it knows
        // best where the JDK is.
        
        String jdkHome = System.getProperty("jdk.home");  // NOI18N
        
        if (jdkHome == null) {
            jdkHome = System.getProperty("java.home");  // NOI18N
            
            if (Utilities.getOperatingSystem() != Utilities.OS_MAC) {
                jdkHome += File.separator + "..";  // NOI18N
            }
                
            System.setProperty("jdk.home", jdkHome);  // NOI18N
        }

        // read environment properties from external file, if any
        readEnvMap ();

        // initialize the URL factory
        // XXX(-ttran) why this?
        Object o = org.openide.execution.NbClassLoader.class;
    }
    
    /** Puts a property into the system ones, but only if the value is not null.
     * @param propName name of property
     * @param value value to assign or null
     * @param failbackValue value to assign if the previous value is null
     */
    private static void putSystemProperty (
        String propName, String value, String failbackValue
    ) {
        if (System.getProperty (propName) == null) {
            // only set it if not null
            if (value != null) {
                System.setProperty (propName, value);
            } else {
                if (!Boolean.getBoolean("netbeans.suppress.sysprop.warning")) {
                    System.err.println(
                        "Warning: Versioning property \"" + propName + // NOI18N
                        "\" is not set. Defaulting to \"" + failbackValue + '"' // NOI18N
                    ); 
                    System.err.println("(to suppress this message run with -Dnetbeans.suppress.sysprop.warning=true)"); // NOI18N
                }
                System.setProperty (propName, failbackValue);
            }
        }
    }

    /** Constructs a new manager.
    */
    public NbTopManager() {
        instanceContent = new InstanceContent ();
        instanceLookup = new AbstractLookup (instanceContent);
        register (ClassLoaderConvertor.CLASSLOADER, ClassLoaderConvertor.CLASSLOADER);
    }

    /** Getter for instance of this manager.
    */
    public static NbTopManager get () {
        return (NbTopManager)TopManager.getDefault ();
    }

    /** Test method to check whether some level of interactivity is enabled.
    * @param il mask composed of the constants of IL_XXXX
    * @return true if such level is enabled
    */
    public abstract boolean isInteractive (int il);
    
    /** Creates error logger.
     */
    protected abstract PrintWriter createErrorLogger (int minLogSeverity);

    /** Allows subclasses to override this method and return different default set of nodes
    * the should be "selected". If no top component is active then this method is called to
    * allow the top manager to decide which nodes should be pointed as selected.
    *
    * @param activated true if the result cannot be null
    * @return the array of nodes to return from TopComponent.getRegistry ().getSelectedNodes or
    *    getActivatedNodes ()
    */
    public Node[] getDefaultNodes (boolean activated) {
        return activated ? new Node[0] : null;
    }
    
    //
    // The main method allows access to registration service
    //
    
    
    /** Register new instance.
     */
    public final void register (Object obj) {
        instanceContent.add (obj);
    }
    
    /** Register new instance.
     * @param obj source
     * @param conv convertor which postponing an instantiation
     */
    public final void register(Object obj, InstanceContent.Convertor conv) {
        instanceContent.add(obj, conv);
    }
    
    /** Unregisters the service.
     */
    public final void unregister (Object obj) {
        instanceContent.remove (obj);
    }
    /** Unregisters the service registered with a convertor.
     */
    public final void unregister (Object obj, InstanceContent.Convertor conv) {
        instanceContent.remove (obj, conv);
    }
    
    /** Private get instance lookup.
     */
    private final Lookup getInstanceLookup () {
        return instanceLookup;
    }
    
    
    //
    // Implementation of methods from TopManager
    //

    /** Shows a specified HelpCtx in IDE's help window.
    * @param helpCtx thehelp to be shown
    */
    public void showHelp(HelpCtx helpCtx) {
        // Awkward but should work.
        try {
            Class c = systemClassLoader().loadClass("org.netbeans.api.javahelp.Help"); // NOI18N
            Object o = Lookup.getDefault().lookup(c);
            if (o != null) {
                Method m = c.getMethod("showHelp", new Class[] {HelpCtx.class}); // NOI18N
                m.invoke(o, new Object[] {helpCtx});
                return;
            }
        } catch (ClassNotFoundException cnfe) {
            // ignore - maybe javahelp module is not installed, not so strange
        } catch (Exception e) {
            // potentially more serious
            getErrorManager().notify(ErrorManager.INFORMATIONAL, e);
        }
        // Did not work.
        Toolkit.getDefaultToolkit().beep();
    }

    /** Provides support for www documents.
    * @param url Url of WWW document to be showen.
    */
    public void showUrl (URL url) {
        if (htmlViewer == null) htmlViewer = new NbBrowser ();

	((NbBrowser)htmlViewer).showUrl (url);
    }

    private static WindowManager wmgr = null;
    /** @return a window manager impl or null */
    private static synchronized WindowManager getDefaultWindowManager() {
        if (wmgr == null) {
            wmgr = (WindowManager)Lookup.getDefault().lookup(WindowManager.class);
        }
        return wmgr;
    }
    /** @return the main window from the window manager impl or null */
    static Frame getMainWindow() {
        WindowManager m = getDefaultWindowManager();
        if (m != null) {
            return m.getMainWindow();
        } else {
            return null;
        }
    }


    /** Adds new explorer manager that will rule the selection of current
    * nodes until the runnable is running.
    *
    * @param run runnable to execute (till it is running the explorer manager is in progress)
    * @param em explorer manager 
    */
    public void attachExplorer (Runnable run, ExplorerManager em) {
        WindowManager m = getDefaultWindowManager();
        if (m instanceof WindowManagerImpl) {
            ((WindowManagerImpl)m).attachExplorer(run, em);
        }
    }

    /** Creates new dialog.
    */
    public Dialog createDialog (final DialogDescriptor d) {
        return (Dialog)Mutex.EVENT.readAccess (new Mutex.Action () {
            public Object run () {
                // if a modal dialog active use it as parent
                // otherwise use the main window
                if (NbPresenter.currentModalDialog != null) {
                    return new NbDialog(d, NbPresenter.currentModalDialog);
                }
                else {
                    return new NbDialog(d, getMainWindow());
                }
            }
        });
    }

    /** Opens specified project. Asks to save the previously opened project.
    * @exception IOException if error occurs accessing the project
    * @exception UserCancelException if the selection is interrupted by the user
    */
    public void openProject (ProjectCookie project) throws IOException, UserCancelException {
        if (ExitDialog.showDialog (null, true)) {
            NbProjectOperation.setOpeningProject (project);
        }
        else {
            throw new UserCancelException ();
        }
    }

    /** Notifies user by a dialog.
    * @param descriptor description that contains needed informations
    * @return the option that has been choosen in the notification
    */
    public Object notify (final NotifyDescriptor descriptor) {
        return Mutex.EVENT.readAccess (new Mutex.Action () {
                public Object run () {
                    Component focusOwner = null;
                    Component comp = org.openide.windows.TopComponent.getRegistry ().getActivated ();
                    Component win = comp;
                    while ((win != null) && (!(win instanceof Window))) win = win.getParent ();
                    if (win != null) focusOwner = ((Window)win).getFocusOwner ();

                    // if a modal dialog is active use it as parent
                    // otherwise use the main window
                    
                    NbPresenter presenter = null;
                    if (descriptor instanceof DialogDescriptor) {
                        if (NbPresenter.currentModalDialog != null) {
                            presenter = new NbDialog((DialogDescriptor) descriptor, NbPresenter.currentModalDialog);
                        } else {
                            presenter = new NbDialog((DialogDescriptor) descriptor, getMainWindow());
                        }
                    } else {
                        if (NbPresenter.currentModalDialog != null) {
                            presenter = new NbPresenter(descriptor, NbPresenter.currentModalDialog, true);
                        } else {
                            presenter = new NbPresenter(descriptor, getMainWindow(), true);
                        }
                    }

                    //Bugfix #8551
                    presenter.getRootPane().requestDefaultFocus();
                    presenter.setVisible(true);

                    // dialog is gone, restore the focus
                    
                    if (focusOwner != null) {
                        win.requestFocus ();
                        comp.requestFocus ();
                        focusOwner.requestFocus ();
                    }
                    return descriptor.getValue();
                }
            });
    }

    /** Shows specified text in MainWindow's status line.
    * @param text the text to be shown
    */
    public final void setStatusText(String text) {
        if (text == null || text.length () == 0) {
            text = " "; // NOI18N
        }
        if (text.equals(statusText)) return;
        String old = statusText;
        statusText = text;
        setStatusTextImpl(text);
        firePropertyChange (PROP_STATUS_TEXT, old, text);
    }
    protected abstract void setStatusTextImpl(String text);

    /** Getter for status text.
    */
    public String getStatusText () {
        return statusText;
    }

    /** Returns currently installed debugger or throws
    *  DebuggerException (when no debugger is installed)
    * @return currently installed  debugger.
    */
    public Debugger getDebugger () throws DebuggerNotFoundException {
        Iterator it = getDebuggerResult().allInstances().iterator();
        if (it.hasNext()) return (Debugger) it.next();
        throw new DebuggerNotFoundException();
    }
    
    /** get the lookup query finding all registered debuggers */
    private synchronized Lookup.Result getDebuggerResult() {
        if (debuggerLkpRes == null) {
            debuggerLkpRes = Lookup.getDefault().lookup(new Lookup.Template(Debugger.class));
        }
        return debuggerLkpRes;
    }
    
    /** fire property change PROP_DEBUGGER */
    private void fireDebuggerChange() {
        firePropertyChange (PROP_DEBUGGER, null, null);
    }
    
    /** initialize listening on adding/removing debugger. */
    private void initDebuggerListener() {
        Lookup.Result res;
        synchronized (this) {
            if (debuggerLsnr != null) return;
            res = getDebuggerResult();
            debuggerLsnr = new LookupListener() {
                public void resultChanged(LookupEvent ev) {
                    fireDebuggerChange();
                }
            };
            res.addLookupListener(debuggerLsnr);
        }
        res.allClasses();
    }

    /** Print output writer.
    * @return default system output printer
    */
    public OutputWriter getStdOut () {
        return OutputTabTerm.getStdOut ();
    }

    /** creates new OutputWriter
    * @param name is a name of the writer
    * @return new OutputWriter with given name
    */
    public InputOutput getIO(String name, boolean newIO) {
        return OutputTabTerm.getIO (name, newIO);
    }

    /** saves all opened objects */
    public void saveAll () {
        DataObject dobj = null;
        ArrayList bad = new ArrayList ();
        DataObject[] modifs = DataObject.getRegistry ().getModified ();
        for (int i = 0; i < modifs.length; i++) {
            try {
                dobj = modifs[i];
                SaveCookie sc = (SaveCookie)dobj.getCookie(SaveCookie.class);
                if (sc != null) {
                    TopManager.getDefault().setStatusText (
                        java.text.MessageFormat.format (
                            NbBundle.getBundle (NbTopManager.class).getString ("CTL_FMT_SavingMessage"),
                            new Object[] { dobj.getName () }
                        )
                    );
                    sc.save();
                }
            } catch (IOException ex) {
                bad.add (dobj);
            }
        }
        NotifyDescriptor descriptor;
        //recode this part to show only one dialog?
        Iterator ee = bad.iterator ();
        while (ee.hasNext ()) {
            descriptor = new NotifyDescriptor.Message(
                        MessageFormat.format (
                            NbBundle.getBundle (NbTopManager.class).getString("CTL_Cannot_save"),
                            new Object[] { ((DataObject)ee.next()).getPrimaryFile().getName() }
                        )
                    );
            TopManager.getDefault ().notify (descriptor);
        }
        // notify user that everything is done
        TopManager.getDefault().setStatusText(
            NbBundle.getBundle (NbTopManager.class).getString ("MSG_AllSaved"));
    }

    private boolean doingExit=false;
    public void exit ( ) {
        synchronized (this) {
            if (doingExit) {
                return ;
            }
            doingExit = true;
        }
        // save all open files
        try {
            if ( System.getProperty ("netbeans.close") != null || ExitDialog.showDialog(null, false) ) {
                if (getModuleSystem().shutDown()) {
                    // hide windows explicitly, they are of no use during exit process
                    WindowUtils.hideAllFrames();
                    try {
                        // save project
                        NbProjectOperation.storeLastProject ();
                        org.netbeans.core.projects.SessionManager.getDefault().close();
                    } catch (ThreadDeath td) {
                        throw td;
                    } catch (Throwable t) {
                        // Do not let problems here prevent system shutdown. The module
                        // system is down; the IDE cannot be used further.
                        ErrorManager.getDefault().notify(t);
                    }
                    TopSecurityManager.exit(0);
                }
            }
        } finally {
            synchronized (this) {
                doingExit = false; 
            }
        }
    }

    /** Shows exit dialog for activated File system nodes
    * after unmounting filesystem(s)
    * @return result of dialog (mount or unmount)
    */    
    public static boolean showExitDialog (Node[] activatedNodes) {
        return ExitDialog.showDialog(activatedNodes);
    }
    
    /** Get the module subsystem. */
    public abstract ModuleSystem getModuleSystem();

    /** Obtains current up-to system classloader
    */
    public ClassLoader systemClassLoader () {
        ModuleSystem ms = getModuleSystem();
        if (ms != null) {
            // #16265: do not go straight to ModuleManager
            return ms.getSystemClassLoader();
        } else {
            // This can be called very early: if lookup asks for ClassLoader.
            // For now, just give the startup classloader.
            //System.err.println("Warning: giving out bogus systemClassLoader for now");
            //Thread.dumpStack();
            return NbTopManager.class.getClassLoader();
        }
    }
    // Access from ModuleSystem and from subclasses when moduleSystem is created:
    public final void fireSystemClassLoaderChange() {
        //System.err.println("change: systemClassLoader -> " + systemClassLoader());
        firePropertyChange(PROP_SYSTEM_CLASS_LOADER, null, null);
        Lkp.systemClassLoaderChanged(); // #26245
    }

    /** Obtains current up-to data te classloader
    */
    public ClassLoader currentClassLoader () {
        ClassLoader l = ClassLoaderSupport.currentClassLoader ();
        if (l == null) {
            System.err.println("SHOULD NEVER HAPPEN: currentClassLoader==null"); // NOI18N
            l = systemClassLoader ();
        }
        return l;
    }
    // Access from ClassLoaderSupport:
    final void fireCurrentClassLoaderChange() {
        //System.err.println("change: currentClassLoader");
        firePropertyChange(PROP_CURRENT_CLASS_LOADER, null, null);
    }



    /** Add listener */
    public void addPropertyChangeListener (PropertyChangeListener l) {
        initDebuggerListener();
        change.addPropertyChangeListener (l);
    }

    /** Removes the listener */
    public void removePropertyChangeListener (PropertyChangeListener l) {
        change.removePropertyChangeListener (l);
    }

    /** Fires property change
    */
    public void firePropertyChange (String p, Object o, Object n) {
        change.firePropertyChange (p, o, n);
    }

    /** Someone running NonGuiMain might want to set this to true.
     * This variable is read from CompilationEngineImpl to determine
     * whether to do synchronous compile.
     */
    public static boolean compileSync = false;
    
    /** Sets the compileSync static variable.
     */
    public static void setCompileSync(boolean sync) {
        compileSync = sync;
    }

    /** Provides support for www documents.
    *
    static HtmlBrowser.BrowserComponent getWWWBrowser () {
      return htmlViewer;
}

    /** Reads system properties from a file on a disk and stores them 
     * in System.getPropeties ().
     */
    private static void readEnvMap () {
        java.util.Properties env = System.getProperties ();
        String envfile = System.getProperty("netbeans.osenv"); // NOI18N
        if (envfile != null) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                    new FileInputStream(envfile)));
                
                while (true) {
                    String line = in.readLine();
                    if (line == null)
                        break;
                    
                    int i = line.indexOf("="); // NOI18N
                    if (i == -1) {
                        continue;
                    }

                    String key = line.substring(0, i);
                    String value = line.substring(i + 1);
                    if (i >= 0) {
                        env.put("Env-" + key, value); // NOI18N
                        env.put("env-" + key.toLowerCase (), value); // NOI18N
                    }
                }
            }
            catch (IOException ignore) {
                ErrorManager.getDefault ().notify (
                    ErrorManager.INFORMATIONAL, ignore
                );
            }
        }
    }


    /**
    * For externalization of HTMLBrowser.
    */
    public static class NbBrowser extends HtmlBrowser.BrowserComponent {

        static final long serialVersionUID =5000673049583700380L;

        private transient PropertyChangeListener idePCL = null;
        /**
        * For externalization.
        */
        public NbBrowser () {
            super (((IDESettings)IDESettings.findObject (IDESettings.class, true)).getWWWBrowser (), true, true);
            
            putClientProperty(WindowManagerImpl.TopComponentManager.TAB_POLICY, 
                WindowManagerImpl.TopComponentManager.HIDE_WHEN_ALONE);
            
            setListener ();
        }
        
        /** 
         * Release resources and also allow to create new browser later using another implementation
         * @return result from ancestor is returned 
         */
        protected boolean closeLast () {
            if (idePCL != null) {
                ((IDESettings)IDESettings.findObject (IDESettings.class, true)).removePropertyChangeListener (idePCL);
                idePCL = null;
            }
            NbTopManager.get ().htmlViewer = null;
            return super.closeLast ();
        }

	/** Show URL in browser
	 * @param url URL to be shown 
	 */
	private void showUrl (URL url) {
	    if (Boolean.TRUE.equals (getClientProperty ("InternalBrowser"))) { // NOI18N
		NbPresenter d = NbPresenter.currentModalDialog;
		if (d != null) {
                    HtmlBrowser htmlViewer = new HtmlBrowser ();
                    htmlViewer.setURL (url);
                    JDialog d1 = new JDialog (d);
                    d1.getContentPane ().add ("Center", htmlViewer); // NOI18N
                    // [PENDING] if nonmodal, better for the dialog to be reused...
                    // (but better nonmodal than modal here)
                    d1.setModal (false);
                    d1.setTitle (Main.getString ("CTL_Help"));
                    d1.pack ();
                    d1.show ();
                    return;
		}
	    }
            open ();
            requestFocus ();
            setURL (url);
	}
        /* Deserialize this top component.
        * @param in the stream to deserialize from
        */
        public void readExternal (ObjectInput in) throws IOException, ClassNotFoundException {
            super.readExternal (in);
            setListener ();
            NbTopManager.get ().htmlViewer = this;
        }

        /**
         *  Sets listener that invalidates this as main IDE's browser if user changes the settings
         */
        private void setListener () {
            if (idePCL != null)
                return;
            try {
                // listen on preffered browser change
                idePCL = new PropertyChangeListener () {
                    public void propertyChange (PropertyChangeEvent evt) {
                        String name = evt.getPropertyName ();
                        if (name == null) return;
                        if (name.equals (IDESettings.PROP_WWWBROWSER)) {
                            NbTopManager.get ().htmlViewer = null;
                            if (idePCL != null) {
                                ((IDESettings)IDESettings.findObject (IDESettings.class, true))
                                .removePropertyChangeListener (idePCL);
                                idePCL = null;
                            }
                        }
                    }
                };
                ((IDESettings)IDESettings.findObject (IDESettings.class, true)).addPropertyChangeListener (idePCL);
            }
            catch (Exception ex) {
                NbTopManager.get ().notifyException (ex);
            }
        }
    }
    
    /** The default lookup for the system.
     */
    public static final class Lkp extends ProxyLookup {
        /** task that initializes the lookup */
        private Task initTask;
        /** thread that initialized the task and has to recieve good results */
        private Thread initThread;
        
        /** Initialize the lookup to delegate to NbTopManager.
        */
        public Lkp () {
            super (new Lookup[] {
                       // #14722: pay attention also to META-INF/services/class.Name resources:
                       createMetaInfServicesLookup(false),
                   });
        }
        
        /** @param modules if true, use module classloader, else not */
        private static Lookup createMetaInfServicesLookup(boolean modules) {
            //System.err.println("cMISL: modules=" + modules);
            try {
                // XXX consider just making this a public class!
                Class clazz = Class.forName("org.openide.util.MetaInfServicesLookup"); // NOI18N
                Constructor c = clazz.getDeclaredConstructor(new Class[] {ClassLoader.class});
                c.setAccessible(true);
                ClassLoader loader;
                if (modules) {
                    loader = get().getModuleSystem().getManager().getClassLoader();
                } else {
                    loader = Lkp.class.getClassLoader();
                }
                return (Lookup)c.newInstance(new Object[] {loader});
            } catch (Exception e) {
                e.printStackTrace();
                return Lookup.EMPTY;
            }
        }
        
        private static final class ConvertorListener
                implements PropertyChangeListener {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt == null || ModuleManager.PROP_ENABLED_MODULES.equals(evt.getPropertyName())) {
                    //System.err.println("modules changed; changing metaInfServicesLookup");
                    // Time to refresh META-INF/services/ lookup; modules turned on or off.
                    Lookup lookup = Lookup.getDefault();
                    if (lookup instanceof Lkp) {
                        Lkp lkp = (Lkp)lookup;
                        Lookup[] old = lkp.getLookups();
                        Lookup[] nue = (Lookup[])old.clone();
                        nue[0] = createMetaInfServicesLookup(true);
                        lkp.setLookups(nue);
                        //System.err.println("lookups: " + java.util.Arrays.asList(nue));
                    }
                    /* just testing:
                    {
                        try {
                            Class c = get().systemClassLoader().loadClass("org.foo.Interface");
                            System.err.println("org.foo.Interface: " + Lookup.getDefault().lookup(new Lookup.Template(c)).allInstances());
                        } catch (Exception e) {
                            System.err.println(e.toString());
                        }
                    }
                    */
                }
            }
        }
        
        /** Called when a system classloader changes.
         */
        public static final void systemClassLoaderChanged () {
            NbTopManager.get ().unregister (ClassLoaderConvertor.CLASSLOADER, ClassLoaderConvertor.CLASSLOADER);
            NbTopManager.get ().register (ClassLoaderConvertor.CLASSLOADER, ClassLoaderConvertor.CLASSLOADER);
        }

        /** When all module classes are accessible thru systemClassLoader, this
         * method is called to initialize the FolderLookup.
         */
	    
        public static final void modulesClassPathInitialized () {
            //System.err.println("mCPI");
	    //StartLog.logStart ("NbTopManager$Lkp: initialization of FolderLookup"); // NOI18N

            // replace the lookup by new one
            Lookup lookup = Lookup.getDefault ();
	    StartLog.logProgress ("Got Lookup"); // NOI18N

            if (lookup instanceof Lkp) {
                Lkp lkp = (Lkp)lookup;
                lkp.initializeLookup ();
                // Wait for the lookup initialization to better measure module startups
                Task t;
                synchronized (lkp) {
                    t = lkp.initTask;
                }
                if (t != null) {
                    t.waitFinished();
                }
            }
        }
        
        private synchronized void initializeLookup () {
            //System.err.println("initializeLookup");
            initTask = RequestProcessor.getDefault().post (new Runnable () {
                public void run () {
                    doInitializeLookup ();
                }
            }, 0, Thread.MAX_PRIORITY);
            initThread = Thread.currentThread();
        }
            
        final void doInitializeLookup () {
            //System.err.println("doInitializeLookup");
            FileObject services = TopManager.getDefault().getRepository().getDefaultFileSystem().findResource("Services");
            Lookup nue;
            if (services != null) {
                StartLog.logProgress("Got Services folder"); // NOI18N
                FolderLookup f = new FolderLookup(DataFolder.findFolder(services), "SL["); // NOI18N
                StartLog.logProgress("created FolderLookup"); // NOI18N
                nue = f.getLookup();
            } else {
                nue = Lookup.EMPTY;
            }

            // extend the lookup
            Lookup[] arr = new Lookup[] {
                getLookups()[0], // metaInfServicesLookup; still keep classpath one till later...
                NbTopManager.get ().getInstanceLookup (),
                nue,
                NbTopManager.get().getModuleSystem().getManager().getModuleLookup(),
            };
            StartLog.logProgress ("prepared other Lookups"); // NOI18N

            setLookups (arr);
            StartLog.logProgress ("Lookups set"); // NOI18N

            // Also listen for changes in modules, as META-INF/services/ would change:
            ModuleManager mgr = get().getModuleSystem().getManager();
            ConvertorListener l = new ConvertorListener();
            mgr.addPropertyChangeListener(l);
            if (!mgr.getEnabledModules().isEmpty()) {
                // Ready now.
                l.propertyChange(null);
            }
            
	    //StartLog.logEnd ("NbTopManager$Lkp: initialization of FolderLookup"); // NOI18N
            
            synchronized (this) {
                // clear the task again
                initTask = null;
                initThread = null;
            }
        }
        
        protected void beforeLookup(Lookup.Template templ) {
            Task t;
            Thread h;
            synchronized (this) {
                t = initTask;
                h = initThread;
            }
            
            if (t != null && h == Thread.currentThread ()) {
                t.waitFinished();
            }
            
            super.beforeLookup(templ);
        }
    }
    
    
    /** Special item for system class loader (which is dynamic).
     */
    private final static class ClassLoaderConvertor
    implements InstanceContent.Convertor {
        public static final ClassLoaderConvertor CLASSLOADER = new ClassLoaderConvertor ();
        
        public Object convert(Object obj) {
            return NbTopManager.get ().systemClassLoader();
        }
        
        public String displayName(Object obj) {
            return id (obj);
        }
        
        public String id(Object obj) {
            return "TM[systemClassLoader"; // NOI18N
        }
        
        public Class type(Object obj) {
            return ClassLoader.class;
        }
        
    } // end of ClassLoaderConvertor
}
