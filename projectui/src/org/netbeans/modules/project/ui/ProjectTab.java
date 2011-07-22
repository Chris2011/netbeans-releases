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

package org.netbeans.modules.project.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.DefaultEditorKit;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.modules.project.ui.groups.Group;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.explorer.view.Visualizer;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.nodes.NodeNotFoundException;
import org.openide.nodes.NodeOp;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Mutex;
import org.openide.util.NbBundle;
import org.openide.util.NbCollections;
import org.openide.util.NbPreferences;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/** TopComponment for viewing open projects. 
 * <P>
 * PENEDING : Fix persistence when new Winsys allows 
 *
 * @author Petr Hrebejk
 */
public class ProjectTab extends TopComponent 
                        implements ExplorerManager.Provider, PropertyChangeListener {
                
    public static final String ID_LOGICAL = "projectTabLogical_tc"; // NOI18N                            
    public static final String ID_PHYSICAL = "projectTab_tc"; // NOI18N                        
    
    private static final Image ICON_LOGICAL = ImageUtilities.loadImage( "org/netbeans/modules/project/ui/resources/projectTab.png" );
    private static final Image ICON_PHYSICAL = ImageUtilities.loadImage( "org/netbeans/modules/project/ui/resources/filesTab.png" );

    private static final Logger LOG = Logger.getLogger(ProjectTab.class.getName());

    private static Map<String, ProjectTab> tabs = new HashMap<String, ProjectTab>();                            
                            
    private transient final ExplorerManager manager;
    private transient Node rootNode;
    
    private String id;
    private transient final ProjectTreeView btv;

    private final JLabel noProjectsLabel = new JLabel(NbBundle.getMessage(ProjectTab.class, "NO_PROJECT_OPEN"));

    private boolean synchronizeViews = false;

    private FileObject objectToSelect;
    private boolean prompt;
    private Task selectionTask;

    private static final int NODE_SELECTION_DELAY = 200;

    public ProjectTab( String id ) {
        this();
        this.id = id;
        initValues();
    }
    
    public ProjectTab() {
        
        // See #36315        
        manager = new ExplorerManager();
        
        ActionMap map = getActionMap();
        map.put(DefaultEditorKit.copyAction, ExplorerUtils.actionCopy(manager));
        map.put(DefaultEditorKit.cutAction, ExplorerUtils.actionCut(manager));
        map.put(DefaultEditorKit.pasteAction, ExplorerUtils.actionPaste(manager));
        map.put("delete", ExplorerUtils.actionDelete(manager, true));
        
        initComponents();

        btv = new ProjectTreeView();    // Add the BeanTreeView
        
        btv.setDragSource (true);
        btv.setUseSubstringInQuickSearch(true);
        btv.setRootVisible(false);
        
        add( btv, BorderLayout.CENTER ); 

        OpenProjects.getDefault().addPropertyChangeListener(this);

        noProjectsLabel.addMouseListener(new LabelPopupDisplayer(noProjectsLabel));
        noProjectsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        noProjectsLabel.setEnabled(false);
        Color usualWindowBkg = UIManager.getColor("window"); // NOI18N
        noProjectsLabel.setBackground(usualWindowBkg != null ? usualWindowBkg : Color.white);
        noProjectsLabel.setOpaque(true);

        associateLookup( ExplorerUtils.createLookup(manager, map) );

        selectionTask = createSelectionTask();

        Preferences nbPrefs = NbPreferences.forModule(SyncEditorWithViewsAction.class);
        synchronizeViews = nbPrefs.getBoolean(SyncEditorWithViewsAction.SYNC_ENABLED_PROP_NAME, false);
        nbPrefs.addPreferenceChangeListener(new NbPrefsListener());
        
    }

    /**
     * Update display to reflect {@link Group#getActiveGroup}.
     * @param group current group, or null
     */
    public void setGroup(Group g) {
        if (id.equals(ID_LOGICAL)) {
            if (g != null) {
                setName(NbBundle.getMessage(ProjectTab.class, "LBL_projectTabLogical_tc_with_group", g.getName()));
            } else {
                setName(NbBundle.getMessage(ProjectTab.class, "LBL_projectTabLogical_tc"));
            }
        } else {
            setName(NbBundle.getMessage(ProjectTab.class, "LBL_projectTab_tc"));
        }
        // Seems to be useless: setToolTipText(getName());
    }

    private void initValues() {
        setGroup(Group.getActiveGroup());
        
        if (id.equals(ID_LOGICAL)) {
            setIcon( ICON_LOGICAL ); 
        }
        else {
            setIcon( ICON_PHYSICAL );
        }
            
        if ( rootNode == null ) {
            // Create the node which lists open projects      
            rootNode = new ProjectsRootNode(id.equals(ID_LOGICAL) ? ProjectsRootNode.LOGICAL_VIEW : ProjectsRootNode.PHYSICAL_VIEW);
        }
        manager.setRootContext( rootNode );
    }
            
    /** Explorer manager implementation 
     */
    public ExplorerManager getExplorerManager() {
        return manager;
    }
    
    /* Singleton accessor. As ProjectTab is persistent singleton this
     * accessor makes sure that ProjectTab is deserialized by window system.
     * Uses known unique TopComponent ID TC_ID = "projectTab_tc" to get ProjectTab instance
     * from window system. "projectTab_tc" is name of settings file defined in module layer.
     * For example ProjectTabAction uses this method to create instance if necessary.
     */
    public static synchronized ProjectTab findDefault( String tcID ) {

        ProjectTab tab = tabs.get(tcID);
        
        if ( tab == null ) {
            //If settings file is correctly defined call of WindowManager.findTopComponent() will
            //call TestComponent00.getDefault() and it will set static field component.
            
            TopComponent tc = WindowManager.getDefault().findTopComponent( tcID ); 
            if (tc != null) {
                if (!(tc instanceof ProjectTab)) {
                    //This should not happen. Possible only if some other module
                    //defines different settings file with the same name but different class.
                    //Incorrect settings file?
                    IllegalStateException exc = new IllegalStateException
                    ("Incorrect settings file. Unexpected class returned." // NOI18N
                    + " Expected:" + ProjectTab.class.getName() // NOI18N
                    + " Returned:" + tc.getClass().getName()); // NOI18N
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, exc);
                    //Fallback to accessor reserved for window system.
                    tab = ProjectTab.getDefault( tcID );
                }
                else {
                    tab = (ProjectTab)tc;
                }
            } 
            else {
                //This should not happen when settings file is correctly defined in module layer.
                //TestComponent00 cannot be deserialized
                //Fallback to accessor reserved for window system.
                tab = ProjectTab.getDefault( tcID );
            }
        }
        return tab;
    }
    
    /* Singleton accessor reserved for window system ONLY. Used by window system to create
     * ProjectTab instance from settings file when method is given. Use <code>findDefault</code>
     * to get correctly deserialized instance of ProjectTab */
    public static synchronized ProjectTab getDefault( String tcID ) {
        
        ProjectTab tab = tabs.get(tcID);
        
        if ( tab == null ) {
            tab = new ProjectTab( tcID );            
            tabs.put( tcID, tab );
        }
        
        return tab;        
    }
    
    public static TopComponent getLogical() {
        return getDefault( ID_LOGICAL );
    }
    
    public static TopComponent getPhysical() {
        return getDefault( ID_PHYSICAL );
    }
    
    @Override
    protected String preferredID () {
        return id;
    }
    
    @Override
    public HelpCtx getHelpCtx() {
        return ExplorerUtils.getHelpCtx( 
            manager.getSelectedNodes(),
            ID_LOGICAL.equals( id ) ? new HelpCtx( "ProjectTab_Projects" ) : new HelpCtx( "ProjectTab_Files" ) );
    }

     
    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }
    
    // APPEARANCE
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents

        setLayout(new java.awt.BorderLayout());

    }//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
        
    @Override
    public boolean requestFocusInWindow() {
        super.requestFocusInWindow();
        return btv.requestFocusInWindow();
    }

    //#41258: In the SDI, requestFocus is called rather than requestFocusInWindow:
    @Override
    public void requestFocus() {
        super.requestFocus();
        btv.requestFocus();
    }
    
    // PERSISTENCE
    
    private static final long serialVersionUID = 9374872358L;
    
    @Override
    public void writeExternal (ObjectOutput out) throws IOException {
        super.writeExternal( out );
        
        out.writeObject( id );
        out.writeObject( rootNode.getHandle() );                
        out.writeObject( btv.getExpandedPaths() );
        out.writeObject( getSelectedPaths() );
    }

    public @Override void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal( in );
        id = (String)in.readObject();
        rootNode = ((Node.Handle)in.readObject()).getNode();
        final List<String[]> exPaths = NbCollections.checkedListByCopy((List<?>) in.readObject(), String[].class, true);
        final List<String[]> selPaths = new ArrayList<String[]>();
        try {
            selPaths.addAll(NbCollections.checkedListByCopy((List<?>) in.readObject(), String[].class, true));
        }
        catch ( java.io.OptionalDataException e ) {
            // Sel paths missing
        }
        initValues();
        if (!"false".equals(System.getProperty("netbeans.keep.expansion"))) { // #55701
            KeepExpansion ke = new KeepExpansion(exPaths, selPaths);
            ke.task.schedule(0);
        }
    }

    private class KeepExpansion implements Runnable {
        final RequestProcessor.Task task;
        final List<String[]> exPaths;
        final List<String[]> selPaths;

        KeepExpansion(List<String[]> exPaths, List<String[]> selPaths) {
            this.exPaths = exPaths;
            this.selPaths = selPaths;
            this.task = RP.create(this);
        }

        @Override
        public void run() {
            try {
                LOG.log(Level.FINE, "{0}: waiting for projects being open", id);
                OpenProjects.getDefault().openProjects().get(10, TimeUnit.SECONDS);
            } catch (TimeoutException ex) {
                LOG.log(Level.FINE, "{0}: Timeout. Will retry in a second", id);
                task.schedule(1000);
                return;
            } catch (ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            }
            LOG.log(Level.FINE, "{0}: Checking node state", id);
            for (Node n : rootNode.getChildren().getNodes()) {
                if (btv.isExpanded(n)) {
                    LOG.log(Level.FINE, "{0}: Node {1} has been expanded. Giving up.", new Object[] {id, n});
                    return;
                }
            }
            LOG.log(Level.FINE, "{0}: expanding paths", id);
            btv.expandNodes(exPaths);
            LOG.log(Level.FINE, "{0}: selecting paths", id);
            final List<Node> selectedNodes = new ArrayList<Node>();
            Node root = manager.getRootContext();
            for (String[] sp : selPaths) {
                LOG.log(Level.FINE, "{0}: selecting {1}", new Object[] {id, Arrays.asList(sp)});
                try {
                    Node n = NodeOp.findPath(root, sp);
                    if (n != null) {
                        selectedNodes.add(n);
                    }
                } catch (NodeNotFoundException x) {
                    LOG.log(Level.FINE, null, x);
                }
            }
            if (!selectedNodes.isEmpty()) {
                LOG.log(Level.FINE, "{0}: Switching to AWT", id);
                EventQueue.invokeLater(new Runnable() {
                    @Override public void run() {
                        try {
                            manager.setSelectedNodes(selectedNodes.toArray(new Node[selectedNodes.size()]));
                        } catch (PropertyVetoException x) {
                            LOG.log(Level.FINE, null, x);
                        }
                        LOG.log(Level.FINE, "{0}: done.", id);
                    }
                });
            }
        }

    }

    
    // MANAGING ACTIONS
    
    @Override
    protected void componentActivated() {
        ExplorerUtils.activateActions(manager, true);
    }
    
    @Override
    protected void componentDeactivated() {
        ExplorerUtils.activateActions(manager, false);
    }

    // SEARCHING NODES

    private static final Lookup context = Utilities.actionsGlobalContext();

    private static final Lookup.Result<FileObject> foSelection = context.lookup(new Lookup.Template<FileObject>(FileObject.class));

    private static final Lookup.Result<DataObject> doSelection = context.lookup(new Lookup.Template<DataObject>(DataObject.class));

    private final LookupListener baseListener = new LookupListener() {
        public void resultChanged(LookupEvent ev) {
            if (TopComponent.getRegistry().getActivated() == ProjectTab.this) {
                // Do not want to go into a loop.
                return;
            }
            if (synchronizeViews) {
                Collection<? extends FileObject> fos = foSelection.allInstances();
                if (fos.size() == 1) {
                    selectNodeAsyncNoSelect(fos.iterator().next(), false);
                } else {
                    Collection<? extends DataObject> dos = doSelection.allInstances();
                    if (dos.size() == 1) {
                        selectNodeAsyncNoSelect((dos.iterator().next()).getPrimaryFile(), false);
                    }
                }
            }
        }
    };

    private final LookupListener weakListener = WeakListeners.create(LookupListener.class, baseListener, null);

    private void startListening() {
        foSelection.addLookupListener(weakListener);
        doSelection.addLookupListener(weakListener);
        baseListener.resultChanged(null);
    }

    private void stopListening() {
        foSelection.removeLookupListener(weakListener);
        doSelection.removeLookupListener(weakListener);
    }

    @Override
    protected void componentShowing() {
        super.componentShowing();
        startListening();
    }

    @Override
    protected void componentHidden() {
        super.componentHidden();
        stopListening();
    }

    // Called from the SelectNodeAction
    
    static final RequestProcessor RP = new RequestProcessor(ProjectTab.class);
    
    public void selectNodeAsync(FileObject object) {
        setCursor( Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) );
        open();
        requestActive();
        selectNodeAsyncNoSelect(object, true);
    }

    private Task createSelectionTask() {
        Task task = RP.create(new Runnable() {
            public void run() {
                if (objectToSelect == null) {
                    return;
                }
                ProjectsRootNode root = (ProjectsRootNode) manager.getRootContext();
                 Node tempNode = root.findNode(objectToSelect);
                 if (tempNode == null) {
                     Project project = FileOwnerQuery.getOwner(objectToSelect);
                     Project found = null;
                     for (;;) {
                         if (project != null) {
                             for (Project p : OpenProjectList.getDefault().getOpenProjects()) {
                                 if (p.getProjectDirectory().equals(project.getProjectDirectory())) {
                                     found = p;
                                     break;
                                 }
                             }
                         }
                         if (found instanceof LazyProject) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                         } else {
                             tempNode = root.findNode(objectToSelect);
                             break;
                         }
                     }
                     if (prompt && project != null && found == null) {
                         String message = NbBundle.getMessage(ProjectTab.class, "MSG_openProject_confirm", //NOI18N
                                 ProjectUtils.getInformation(project).getDisplayName());
                         String title = NbBundle.getMessage(ProjectTab.class, "MSG_openProject_confirm_title");//NOI18N
                         NotifyDescriptor.Confirmation confirm =
                                 new NotifyDescriptor.Confirmation(message, title, NotifyDescriptor.OK_CANCEL_OPTION);
                         DialogDisplayer.getDefault().notify(confirm);
                         if (confirm.getValue() == NotifyDescriptor.OK_OPTION) {
                             if (!OpenProjectList.getDefault().isOpen(project)) {
                                 OpenProjects.getDefault().open(new Project[] { project }, false);
                                 ProjectsRootNode.ProjectChildren.RP.post(new Runnable() {@Override public void run() {}}).waitFinished(); // #199669
                             }
                             tempNode = root.findNode(objectToSelect);
                         }
                     }
                 }
                 final Node selectedNode = tempNode;
                // Back to AWT             // Back to AWT
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        if ( selectedNode != null ) {
                            try {
                                manager.setSelectedNodes( new Node[] { selectedNode } );
                                btv.scrollToNode(selectedNode);
                                StatusDisplayer.getDefault().setStatusText( "" ); // NOI18N
                            }
                            catch ( PropertyVetoException e ) {
                                // Bad day node found but can't be selected
                            }
                        } else if (prompt) {
                            StatusDisplayer.getDefault().setStatusText(
                                NbBundle.getMessage( ProjectTab.class,
                                                     ID_LOGICAL.equals( id ) ? "MSG_NodeNotFound_ProjectsTab" : "MSG_NodeNotFound_FilesTab" ) ); // NOI18N
                        }
                        setCursor( null );
                    }
                } );
            }
        });
        return task;
    }

    private void selectNodeAsyncNoSelect(FileObject object, boolean prompt) {
        objectToSelect = object;
        this.prompt = prompt;
        selectionTask.schedule(NODE_SELECTION_DELAY);
    }

    Node findNode(FileObject object) {
        return ((ProjectsRootNode) manager.getRootContext()).findNode(object);
    }
    
    void selectNode(final Node node) {
        Mutex.EVENT.writeAccess(new Runnable() {
            public @Override void run() {
                try {
                    manager.setSelectedNodes(new Node[] {node});
                    btv.scrollToNode(node);
                } catch (PropertyVetoException e) {
                    // Bad day node found but can't be selected
                }
            }
        });
    }
    
    void expandNode(Node node) {
        btv.expandNode( node );
    }
    
    private List<String[]> getSelectedPaths() {
        List<String[]> result = new ArrayList<String[]>();
        Node root = manager.getRootContext();
        for (Node n : manager.getSelectedNodes()) {
            String[] path = NodeOp.createPath(n, root);
            LOG.log(Level.FINE, "path from {0} to {1}: {2}", new Object[] {root, n, Arrays.asList(path)});
            if (path != null) {
                result.add(path);
            }
        }
        return result;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (OpenProjects.PROPERTY_OPEN_PROJECTS.equals(evt.getPropertyName())) {
            final boolean someProjectsOpen = OpenProjects.getDefault().getOpenProjects().length > 0;
            Mutex.EVENT.readAccess(new Runnable() {
                public @Override void run() {
                    if (someProjectsOpen) {
                        restoreTreeView();
                    } else {
                        showNoProjectsLabel();
                    }
                }
            });
        }
    }

    private void showNoProjectsLabel() {
        if (noProjectsLabel.isShowing()) {
            return;
        }
        remove(btv);
        add(noProjectsLabel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void restoreTreeView() {
        if (btv.isShowing()) {
            return;
        }
        remove(noProjectsLabel);
        add(btv, BorderLayout.CENTER );
        revalidate();
        repaint();
    }

    // Private innerclasses ----------------------------------------------------
    
    /** Extending bean treeview. To be able to persist the selected paths
     */
    private class ProjectTreeView extends BeanTreeView {
        public void scrollToNode(final Node n) {
            // has to be delayed to be sure that events for Visualizers
            // were processed and TreeNodes are already in hierarchy
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    TreeNode tn = Visualizer.findVisualizer(n);
                    if (tn == null) {
                        return;
                    }
                    TreeModel model = tree.getModel();
                    if (!(model instanceof DefaultTreeModel)) {
                        return;
                    }
                    TreePath path = new TreePath(((DefaultTreeModel) model).getPathToRoot(tn));
                    Rectangle r = tree.getPathBounds(path);
                    if (r != null) {
                        tree.scrollRectToVisible(r);
                    }
                }
            });
    }
                        
        public List<String[]> getExpandedPaths() { 

            List<String[]> result = new ArrayList<String[]>();
            
            TreeNode rtn = Visualizer.findVisualizer( rootNode );
            TreePath tp = new TreePath( rtn ); // Get the root
            
            for( Enumeration exPaths = tree.getExpandedDescendants( tp ); exPaths != null && exPaths.hasMoreElements(); ) {
                TreePath ep = (TreePath)exPaths.nextElement();
                Node en = Visualizer.findNode( ep.getLastPathComponent() );                
                String[] path = NodeOp.createPath( en, rootNode );
                
                // System.out.print("EXP "); ProjectTab.print( path );
                
                result.add( path );
            }
            
            return result;
            
        }
        
        /** Expands all the paths, when exists
         */
        public void expandNodes(List<String[]> exPaths) {
            for (final String[] sp : exPaths) {
                LOG.log(Level.FINE, "{0}: expanding {1}", new Object[] {id, Arrays.asList(sp)});
                Node n;
                try {
                    n = NodeOp.findPath(rootNode, sp);
                } catch (NodeNotFoundException e) {
                    LOG.log(Level.FINE, "got {0}", e.toString());
                    n = e.getClosestNode();
                }
                if (n == null) { // #54832: it seems that sometimes we get unparented node
                    LOG.log(Level.FINE, "nothing from {0} via {1}", new Object[] {rootNode, Arrays.toString(sp)});
                    continue;
                }
                final Node leafNode = n;
                EventQueue.invokeLater(new Runnable() {
                    public @Override void run() {
                        TreeNode tns[] = new TreeNode[sp.length + 1];
                        Node n = leafNode;
                        for (int i = sp.length; i >= 0; i--) {
                            if (n == null) {
                                LOG.log(Level.FINE, "lost parent node at #{0} from {1}", new Object[] {i, leafNode});
                                return;
                            }
                            tns[i] = Visualizer.findVisualizer(n);
                            n = n.getParentNode();
                        }
                        showPath(new TreePath(tns));
                    }
                });
            }
        }
    }
    

    // showing popup on right click in projects tab when label <No Project Open> is shown
    private class LabelPopupDisplayer extends MouseAdapter {

        private Component component;

        public LabelPopupDisplayer(Component comp) {
            component = comp;
        }

        private void showPopup(int x, int y) {
            Action actions[] = rootNode.getActions(false);
            JPopupMenu popup = Utilities.actionsToPopup(actions, component);
            popup.show(component, x, y);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger() && id.equals(ID_LOGICAL)) {
                showPopup(e.getX(), e.getY());
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger() && id.equals(ID_LOGICAL)) {
                showPopup(e.getX(), e.getY());
            }
        }

    }

    private class NbPrefsListener implements PreferenceChangeListener {

        public void preferenceChange(PreferenceChangeEvent evt) {
            if (SyncEditorWithViewsAction.SYNC_ENABLED_PROP_NAME.equals(evt.getKey())) {
                synchronizeViews = Boolean.parseBoolean(evt.getNewValue());
            }
        }

    }

}
