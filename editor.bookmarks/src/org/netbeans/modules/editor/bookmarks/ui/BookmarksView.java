/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.editor.bookmarks.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.editor.EditorUI;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.editor.bookmarks.BookmarkChange;
import org.netbeans.modules.editor.bookmarks.BookmarkInfo;
import org.netbeans.modules.editor.bookmarks.BookmarkManager;
import org.netbeans.modules.editor.bookmarks.BookmarkManagerEvent;
import org.netbeans.modules.editor.bookmarks.BookmarkManagerListener;
import org.netbeans.modules.editor.bookmarks.BookmarkUtils;
import org.netbeans.modules.editor.bookmarks.BookmarksPersistence;
import org.netbeans.modules.editor.bookmarks.ProjectBookmarks;
import org.netbeans.modules.editor.bookmarks.FileBookmarks;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.swing.etable.ETable;
import org.openide.cookies.EditorCookie;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.WeakListeners;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * View of all currently known bookmarks (from all opened projects).
 *
 * @author Miloslav Metelka
 */
public final class BookmarksView extends TopComponent
implements BookmarkManagerListener, PropertyChangeListener, ExplorerManager.Provider
{
    
    private static final int PREVIEW_PANE_REFRESH_DELAY = 300;
    
    /**
     * Invoked from layer.
     * @return bookmarks view instance.
     */
    public static TopComponent create() {
        return new BookmarksView();
    }
    
    public static BookmarksView openView() {
        BookmarksView bookmarksView = (BookmarksView) WindowManager.getDefault().findTopComponent("bookmarks"); // NOI18N
        if (bookmarksView == null) {
            bookmarksView = (BookmarksView) create();
        }
        bookmarksView.open();
        bookmarksView.requestActive();
        return bookmarksView;
    }
    
    public static ActionListener openAction() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openView();
            }
        };
    }

    private transient final ExplorerManager explorerManager;
    private transient boolean treeViewShowing; // Whether viewed as tree or as a table
    private transient JSplitPane splitPane;
    private transient BookmarksTableView tableView;
    private transient BeanTreeView treeView;
    private transient JPanel previewPanel;

    private transient boolean dividerLocationSet;
    
    private transient JToggleButton bookmarksTreeButton;
    private transient JToggleButton bookmarksTableButton;
    
    private transient Timer previewRefreshTimer;
    private transient BookmarkInfo displayedBookmarkInfo;
    
    BookmarksView() {
//        getActionMap().put("rename", SystemAction.get(RenameAction.class));
        explorerManager = new ExplorerManager();
        ActionMap actionMap = getActionMap();
        actionMap.put("delete", ExplorerUtils.actionDelete(explorerManager, false));
        associateLookup(ExplorerUtils.createLookup(explorerManager, actionMap));
        explorerManager.addPropertyChangeListener(this);

        // Ctrl+T will toggle the tree/table view
        InputMap inputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_DOWN_MASK), "toggle-view");
        actionMap.put("toggle-view", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setTreeViewVisible(!treeViewShowing);
            }
        });
    }
    
    @Override
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    @Override
    public String getName () {
        return NbBundle.getMessage (BookmarksView.class, "LBL_BookmarksView");
    }

    @Override
    protected String preferredID() {
        return "bookmarks"; // NOI18N
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_ALWAYS;
    }
    
    @Override
    public String getToolTipText () {
        return NbBundle.getMessage (BookmarksView.class, "LBL_BookmarksViewToolTip");// NOI18N
    }
    
    private void initLayoutAndComponents() {
        if (previewPanel == null) { // Not inited yet
            setLayout(new GridBagLayout());
            GridBagConstraints gridBagConstraints;
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.gridheight = 1;
            gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
            gridBagConstraints.fill = GridBagConstraints.NONE;
            gridBagConstraints.weightx = 0.0;
            gridBagConstraints.weighty = 0.0;
            add(createLeftToolBar(), gridBagConstraints);
            
            splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            splitPane.setContinuousLayout(true);

            previewPanel = new JPanel();
            previewPanel.setLayout(new GridLayout(1, 1));
            fixScrollPaneinSplitPaneJDKIssue(previewPanel);
            splitPane.setRightComponent(previewPanel);
            
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.gridheight = 1;
            gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.weighty = 1.0;
            add(splitPane, gridBagConstraints);

            // Make treeView visible
            // treeViewVisible = false;
            setTreeViewVisible(true);
            BookmarkManager lockedBookmarkManager = BookmarkManager.getLocked();
            try {
                lockedBookmarkManager.addBookmarkManagerListener(
                    WeakListeners.create(BookmarkManagerListener.class, this, lockedBookmarkManager));
            } finally {
                lockedBookmarkManager.unlock();
            }
        }
    }

    @Override
    public void bookmarksChanged(final BookmarkManagerEvent evt) {
        SwingUtilities.invokeLater(new Runnable() { // Can come within project's mutex lock
            @Override
            public void run() {
                updateTreeRootContext(evt.getBookmarkChanges());
            }
        });
    }
    
    private void setTreeViewVisible(boolean treeViewVisible) {
        if (treeViewVisible != this.treeViewShowing) {
            this.treeViewShowing = treeViewVisible;
            TreeOrTableContainer container;
            boolean create;
            if (treeViewVisible) {
                create = (treeView == null);
                if (create) {
                    container = new TreeOrTableContainer();
                    updateTreeRootContext(null);
                    treeView = new BeanTreeView();
                    container.add(treeView);
                    fixScrollPaneinSplitPaneJDKIssue(treeView);
                    treeView.setRootVisible(false);
                    treeView.setDragSource(false);
                    treeView.setDropTarget(false);
                } else {
                    container = (TreeOrTableContainer) treeView.getParent();
                }

            } else { // Table view visible
                create = (tableView == null);
                if (create) {
                    tableView = new BookmarksTableView();
                    container = new TreeOrTableContainer();
                    container.add(tableView);
                    updateTableEntries();
                    initTableView();
                } else {
                    container = (TreeOrTableContainer) tableView.getParent();
                }
            }
            int dividerLocation = splitPane.getDividerLocation();
            splitPane.setLeftComponent(container);
            splitPane.setDividerLocation(dividerLocation);
            if (!treeViewVisible && create) {
                splitPane.validate(); // Have to validate to properly update column sizes
                updateTableColumnSizes();
            }
            bookmarksTreeButton.setSelected(treeViewVisible);
            bookmarksTableButton.setSelected(!treeViewVisible);
            requestFocusTreeOrTable();
        }
    }
    
    private void updateTreeRootContext(List<BookmarkChange> changes) {
        Node[] projectNodes;
        BookmarkManager lockedBookmarkManager = BookmarkManager.getLocked();
        try {
            Node selNode = getTreeSelectedNode();
            BookmarkInfo selectedBookmark = null;
            ProjectBookmarks selectedProjectBookmarks = null;
            Node selectedProjectNode = null;
            FileBookmarks selectedFileBookmarks = null;
            if (selNode instanceof BookmarkNode) {
                selectedBookmark = ((BookmarkNode)selNode).getBookmarkInfo();
                selectedFileBookmarks = selectedBookmark.getFileBookmarks();
                selectedProjectBookmarks = selectedFileBookmarks.getProjectBookmarks();
            }
            
            List<ProjectBookmarks> loadedProjectBookmarks = lockedBookmarkManager.allLoadedProjectBookmarks();
            List<Node> projectNodeList = new ArrayList<Node>(loadedProjectBookmarks.size());
            for (ProjectBookmarks projectBookmarks : loadedProjectBookmarks) {
                if (projectBookmarks.containsAnyBookmarks()) {
                    FileObject[] sortedFileObjects = lockedBookmarkManager.getSortedFileObjects(projectBookmarks);
                    ProjectBookmarksChildren children = new ProjectBookmarksChildren(projectBookmarks, sortedFileObjects);
                    LogicalViewProvider lvp = projectBookmarks.getProject().getLookup().lookup(LogicalViewProvider.class);
                    Node prjNode = (lvp != null) ? lvp.createLogicalView() : null;
                    if (prjNode == null) {
                        prjNode = new AbstractNode(Children.LEAF);
                        prjNode.setDisplayName(children.getProjectDisplayName());
                    }
                    Node n = new FilterNode(prjNode, children) {
                        @Override
                        public boolean canCopy() {
                            return false;
                        }
                        @Override
                        public boolean canCut() {
                            return false;
                        }
                        @Override
                        public boolean canDestroy() {
                            return false;
                        }
                        @Override
                        public boolean canRename() {
                            return false;
                        }
                    };
                    projectNodeList.add(n);
                    if (projectBookmarks == selectedProjectBookmarks) {
                        selectedProjectNode = n;
                    }
                }
            }
            projectNodes = new Node[projectNodeList.size()];
            projectNodeList.toArray(projectNodes);

            // Sort by project's display name
            Arrays.sort(projectNodes, new Comparator<Node>() {
                @Override
                public int compare(Node n1, Node n2) {
                    return ((ProjectBookmarksChildren) n1.getChildren()).getProjectDisplayName().compareTo(
                            ((ProjectBookmarksChildren) n2.getChildren()).getProjectDisplayName());
                }
            });
            Children rootChildren = new Children.Array();
            rootChildren.add(projectNodes);
            Node rootNode = new AbstractNode(rootChildren);
            explorerManager.setRootContext(rootNode);
            
            if (selectedProjectNode != null) {
                for (Node fileNodes : selectedProjectNode.getChildren().snapshot()) {
                    FileBookmarksChildren ch = (FileBookmarksChildren) fileNodes.getChildren();
                    if (ch.getFileBookmarks() == selectedFileBookmarks) {
                        for (Node bookmarkNode : ch.snapshot()) {
                            if (((BookmarkNode)bookmarkNode).getBookmarkInfo() == selectedBookmark) {
                                try {
                                    selNode = bookmarkNode;
                                    explorerManager.setSelectedNodes(new Node[] { bookmarkNode });
                                } catch (PropertyVetoException ex) {
                                    Exceptions.printStackTrace(ex);
                                }
                            }
                        }
                    }
                }
            }

            // Update table nodes as well if they exist
            updateTableEntries();

            if (selNode == null && projectNodes.length > 0) { // Select first node
                try {
                    explorerManager.setSelectedNodes(new Node[] { projectNodes[0] });
                } catch (PropertyVetoException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        } finally {
            lockedBookmarkManager.unlock();
        }
    }
    
    private void updateTableEntries() {
        if (tableView != null) {
            BookmarksTable table = tableView.getTable();
            int selectedIndex = Math.max(table.getSelectedRow(), 0); // If no selection request first row selection
            List<BookmarkInfo> bookmarks = new ArrayList<BookmarkInfo>();
            collectBookmarksFromNodes(bookmarks, explorerManager.getRootContext());
            ((BookmarksTableModel)table.getModel()).setEntries(bookmarks);
            selectedIndex = Math.min(selectedIndex, table.getRowCount() - 1);
            if (selectedIndex >= 0) {
                table.getSelectionModel().setSelectionInterval(selectedIndex, selectedIndex);
            }
        }
    }
    
    private void collectBookmarksFromNodes(List<BookmarkInfo> bookmarks, Node n) {
        if (n instanceof BookmarkNode) {
            bookmarks.add(((BookmarkNode)n).getBookmarkInfo());
        } else {
            for (Node cn : n.getChildren().snapshot()) {
                collectBookmarksFromNodes(bookmarks, cn);
            }
        }
    }
    
    private void initTableView() {
        fixScrollPaneinSplitPaneJDKIssue(tableView);
        // ETable defines "enter" action => change its meaning
        tableView.getTable().getActionMap().put("enter", new AbstractAction() { // NOI18N
            @Override
            public void actionPerformed(ActionEvent e) {
                BookmarkInfo selectedBookmark = getTableSelectedBookmark();
                if (selectedBookmark != null) {
                    BookmarkUtils.postOpenEditor(selectedBookmark);
                }
            }
        });
        tableView.getTable().getActionMap().put("delete", new AbstractAction() { // NOI18N
            @Override
            public void actionPerformed(ActionEvent e) {
                BookmarkInfo selectedBookmark = getTableSelectedBookmark();
                if (selectedBookmark != null) {
                    BookmarkUtils.removeBookmarkUnderLock(selectedBookmark);
                }
            }
        });
        tableView.getTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                schedulePaneRefresh();
            }
        });
    }
    
    private void updateTableColumnSizes() {
        ETable table = tableView.getTable();
        Font font = tableView.getFont();
        FontMetrics fm = tableView.getFontMetrics(font);
        int maxCharWidth = fm.getMaxAdvance();
        int editingBorder = 4;
        TableColumnModel columnModel = table.getColumnModel();

        TableColumn nameColumn = columnModel.getColumn(0);
        nameColumn.setPreferredWidth(8 * maxCharWidth + editingBorder); // 8 chars for name

        TableColumn keyColumn = columnModel.getColumn(1);
        keyColumn.setPreferredWidth(1 * maxCharWidth + editingBorder); // single char for key
        keyColumn.setMinWidth(keyColumn.getPreferredWidth());

        TableColumn locationColumn = columnModel.getColumn(2);
        Insets insets = tableView.getBorder().getBorderInsets(tableView);
        int remainingWidth = tableView.getParent().getWidth() - insets.left - insets.right;
        remainingWidth -= 2 * columnModel.getColumnMargin();
        remainingWidth -= nameColumn.getPreferredWidth();
        remainingWidth -= keyColumn.getPreferredWidth();
        locationColumn.setPreferredWidth(remainingWidth); // remaining space for location
    }

    void requestFocusTreeOrTable() {
        if (treeViewShowing) {
            treeView.requestFocusInWindow();
        } else {
            tableView.getTable().requestFocusInWindow();
        }
        Node selectedNode = getTreeSelectedNode();
        if (selectedNode == null) {
            Children rootChildren = explorerManager.getRootContext().getChildren();
            if (rootChildren.getNodesCount() > 0) {
                try {
                    explorerManager.setSelectedNodes(new Node[] { rootChildren.getNodeAt(0) });
                } catch (PropertyVetoException ex) {
                    // Ignored
                }
            }
        }
    }

    @Override
    public boolean requestFocusInWindow() {
        super.requestFocusInWindow();
        requestFocusTreeOrTable();
        return isFocusable();
    }
    
    void refreshView() {
        updateTreeRootContext(null);
        requestFocusTreeOrTable();
    }

    @Override
    protected void componentActivated() {
        super.componentActivated();
        ExplorerUtils.activateActions(explorerManager, true);
        requestFocusTreeOrTable();
    }

    @Override
    protected void componentDeactivated() {
        ExplorerUtils.activateActions(explorerManager, false);
        super.componentDeactivated();
    }
    
    @Override
    protected void componentShowing() {
        // Ensure all bookmarks from all projects loaded
        BookmarksPersistence.get().ensureAllOpenedProjectsBookmarksLoaded();
        initLayoutAndComponents();
        super.componentShowing();
    }

    @Override
    protected void componentHidden() {
        super.componentHidden();
    }
    
    private void schedulePaneRefresh() {
        if (previewRefreshTimer == null) {
            previewRefreshTimer = new Timer(PREVIEW_PANE_REFRESH_DELAY, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    checkShowPreview();
                }
            });
            previewRefreshTimer.setRepeats(false);
        }
        previewRefreshTimer.restart();
    }
    
    void checkShowPreview() {
        BookmarkInfo selectedBookmark = null;
        if (treeViewShowing) {
            Node selectedNode = getTreeSelectedNode();
            if (selectedNode instanceof BookmarkNode) {
                BookmarkNode bmNode = (BookmarkNode) selectedNode;
                selectedBookmark = bmNode.getBookmarkInfo();
            }
        } else {
            selectedBookmark = getTableSelectedBookmark();
        }
        
        if (selectedBookmark != null) {
            final BookmarkInfo bookmark = selectedBookmark;
            if (bookmark != displayedBookmarkInfo) {
                final FileObject fo = bookmark.getFileBookmarks().getFileObject();
                try {
                    DataObject dob = DataObject.find(fo);
                    final EditorCookie ec = dob.getCookie(EditorCookie.class);
                    if (ec != null) {
                        Document doc = ec.getDocument();
                        if (doc == null) {
                            // Open document on background
                            RequestProcessor.getDefault().post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        final Document d = ec.openDocument();
                                        SwingUtilities.invokeLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                showPreview(fo, d, bookmark);
                                            }
                                        });
                                    } catch (IOException ex) {
                                        Exceptions.printStackTrace(ex);
                                    }
                                }
                            });
                        } else { // doc != null
                            showPreview(fo, doc, bookmark);
                        }
                    }
                } catch (DataObjectNotFoundException ex) {
                    // Ignore preview
                }
            }
        }
    }
    
    void showPreview(FileObject fo, Document doc, BookmarkInfo bookmarkInfo) {
        if (bookmarkInfo != displayedBookmarkInfo) {
            int lineIndex = bookmarkInfo.getCurrentLineIndex();
            String mimeType = (String) doc.getProperty("mimeType");
            if (mimeType != null) {
                JEditorPane pane = new JEditorPane();
                EditorKit editorKit = MimeLookup.getLookup(mimeType).lookup(EditorKit.class);
                pane.setEditorKit(editorKit);
                pane.setDocument(doc);
                pane.setEditable(false);
                Component editorComponent;
                EditorUI editorUI = Utilities.getEditorUI(pane);
                if (editorUI != null) {
                    editorComponent = editorUI.getExtComponent();
                } else {
                    editorComponent = new JScrollPane(pane);
                }
                previewPanel.removeAll();
                previewPanel.add(editorComponent);

                int offset = BookmarkUtils.lineIndex2Offset(doc, lineIndex);
                pane.setCaretPosition(offset);
                displayedBookmarkInfo = bookmarkInfo;
                
                previewPanel.revalidate();
            }
        }
    }
    
    Node getTreeSelectedNode() {
        Node selectedNode = null;
        if (treeViewShowing) {
            Node[] selectedNodes = explorerManager.getSelectedNodes();
            if (selectedNodes.length > 0) {
                selectedNode = selectedNodes[0];
            }
        }
        return selectedNode;
    }

    BookmarkInfo getTableSelectedBookmark() {
        BookmarksTable table = tableView.getTable();
        int selectedRowIndex = table.getSelectedRow();
        if (selectedRowIndex != -1 && selectedRowIndex < table.getRowCount()) {
            return ((BookmarksTableModel)table.getModel()).getEntry(selectedRowIndex);
        }
        return null;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (!dividerLocationSet && splitPane != null && treeView != null) {
            dividerLocationSet = true;
            // setDividerLocation() only works when layout is finished
            splitPane.setDividerLocation(0.5d);
            splitPane.setResizeWeight(0.5d); // Resize in the same proportions
        }
    }
    
    private JToolBar createLeftToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setOrientation(SwingConstants.VERTICAL);
        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        toolBar.setBorderPainted(true);
        if( "Aqua".equals(UIManager.getLookAndFeel().getID()) ) { //NOI18N
            toolBar.setBackground(UIManager.getColor("NbExplorerView.background")); //NOI18N
        }
        
        JButton refreshButton = new JButton(
                ImageUtilities.loadImageIcon("org/netbeans/modules/editor/bookmarks/resources/refresh.png", false));
        refreshButton.setToolTipText(NbBundle.getMessage(BookmarksView.class, "LBL_toolBarRefreshButtonToolTip"));
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshView();
            }
        });
        toolBar.add(refreshButton);

        toolBar.addSeparator();
        bookmarksTreeButton = new JToggleButton(
                ImageUtilities.loadImageIcon("org/netbeans/modules/editor/bookmarks/resources/bookmarksTree.png", false));
        bookmarksTreeButton.setToolTipText(NbBundle.getMessage(BookmarksView.class, "LBL_toolBarTreeViewButtonToolTip"));
        bookmarksTreeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setTreeViewVisible(true);
            }
        });
        toolBar.add(bookmarksTreeButton);

        bookmarksTableButton = new JToggleButton(
                ImageUtilities.loadImageIcon("org/netbeans/modules/editor/bookmarks/resources/bookmarksTable.png", false));
        bookmarksTableButton.setToolTipText(NbBundle.getMessage(BookmarksView.class, "LBL_toolBarTableViewButtonToolTip"));
        bookmarksTableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setTreeViewVisible(false);
            }
        });
        toolBar.add(bookmarksTableButton);
        toolBar.addSeparator();
        
        return toolBar;
    }
    
    private static void fixScrollPaneinSplitPaneJDKIssue(Component c) {
        c.setMinimumSize(new Dimension(10, 10)); // Workaround for JSplitPane-containing-JScrollPane JDK bug
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("selectedNodes".equals(evt.getPropertyName())) {
            schedulePaneRefresh();
        }
    }
    
    private final class TreeOrTableContainer extends JPanel {
        
        TreeOrTableContainer() {
            // Use GridLayout since BorderLayout does not behave well inside JSplitPane's left component
            // - it centers the contained component
            setLayout(new GridLayout(1, 1));
            fixScrollPaneinSplitPaneJDKIssue(this);
        }

    }

    private static final class ProjectBookmarksChildren extends Children.Keys<FileObject> {
        
        String projectDisplayName;
        
        ProjectBookmarks projectBookmarks;
        
        ProjectBookmarksChildren(ProjectBookmarks projectBookmarks, FileObject[] sortedFileObjects) {
            this.projectBookmarks = projectBookmarks;
            projectDisplayName = ProjectUtils.getInformation(projectBookmarks.getProject()).getDisplayName();
            setKeys(sortedFileObjects);
        }
        
        String getProjectDisplayName() {
            return projectDisplayName;
        }

        @Override
        protected Node[] createNodes(FileObject fo) {
            try {
                DataObject dob = DataObject.find(fo);
                Node node = dob.getNodeDelegate().cloneNode();
                URL url = fo.toURL();
                FileBookmarks urlBookmarks = projectBookmarks.get(url);
                return new Node[] { new FilterNode(node, new FileBookmarksChildren(urlBookmarks, fo)) };
            } catch (DataObjectNotFoundException ex) {
                throw new IllegalStateException(); // TODO generic node for FO
            }
        }

    }
    
    private static final class FileBookmarksChildren extends Children.Array {
        
        private final FileBookmarks fileBookmarks;
        
        FileBookmarksChildren(FileBookmarks fileBookmarks, FileObject fo) {
            super(toNodes(fileBookmarks));
            this.fileBookmarks = fileBookmarks;
        }

        public FileBookmarks getFileBookmarks() {
            return fileBookmarks;
        }
        
        @Override
        public boolean remove(Node[] arr) {
            boolean ret = super.remove(arr);
            if (ret) {
                BookmarkManager lockedBookmarkManager = BookmarkManager.getLocked();
                try {
                    List<BookmarkInfo> removedBookmarks = new ArrayList<BookmarkInfo>(arr.length);
                    for (Node n : arr) {
                        removedBookmarks.add(((BookmarkNode)n).getBookmarkInfo());
                    }
                    lockedBookmarkManager.removeBookmarks(removedBookmarks);
                } finally {
                    lockedBookmarkManager.unlock();
                }
            }
            return ret;
        }
        
        private static List<Node> toNodes(FileBookmarks fb) {
            BookmarkManager lockedBookmarkManager = BookmarkManager.getLocked();
            try {
                List<BookmarkInfo> bookmarks = fb.getBookmarks();
                List<Node> nodes = new ArrayList<Node>(bookmarks.size());
                for (int i = 0; i < bookmarks.size(); i++) {
                    BookmarkInfo bookmark = bookmarks.get(i);
                    BookmarkNode bookmarkNode = new BookmarkNode(bookmark);
                    nodes.add(bookmarkNode);
                }
                return nodes;
            } finally {
                lockedBookmarkManager.unlock();
            }
        }
        
    }

    private static final class BookmarksTableView extends JScrollPane { // Similar construct to explorer's TableView

        BookmarksTableView() {
            setViewportView(new BookmarksTable());
        }
        
        BookmarksTable getTable() {
            return (BookmarksTable) getViewport().getView();
        }

    }

}
