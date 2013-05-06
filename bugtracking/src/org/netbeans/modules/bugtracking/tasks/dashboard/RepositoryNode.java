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
package org.netbeans.modules.bugtracking.tasks.dashboard;

import org.netbeans.modules.bugtracking.tasks.actions.Actions;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.netbeans.modules.team.ui.util.treelist.LinkButton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import org.netbeans.modules.bugtracking.QueryImpl;
import org.netbeans.modules.bugtracking.RepositoryImpl;
import org.netbeans.modules.bugtracking.tasks.actions.Actions.CloseRepositoryNodeAction;
import org.netbeans.modules.bugtracking.tasks.actions.Actions.CreateTaskAction;
import org.netbeans.modules.bugtracking.tasks.actions.Actions.OpenRepositoryNodeAction;
import org.netbeans.modules.bugtracking.tasks.actions.Actions.CreateQueryAction;
import org.netbeans.modules.bugtracking.tasks.actions.Actions.QuickSearchAction;
import org.netbeans.modules.bugtracking.tasks.DashboardUtils;
import org.netbeans.modules.team.ui.util.treelist.AsynchronousNode;
import org.netbeans.modules.team.ui.util.treelist.TreeLabel;
import org.netbeans.modules.team.ui.util.treelist.TreeListNode;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author jpeska
 */
public class RepositoryNode extends AsynchronousNode<Collection<QueryImpl>> implements Comparable<RepositoryNode>, Refreshable {

    private final RepositoryImpl repository;
    private List<QueryNode> queryNodes;
    private List<QueryNode> filteredQueryNodes;
    private boolean refresh;
    private JPanel panel;
    private TreeLabel lblName;
    private final Object LOCK = new Object();
    private LinkButton btnRefresh;
    private LinkButton btnSearch;
    private LinkButton btnCreateTask;
    private LinkButton btnAddQuery;
    private CloseRepositoryNodeAction closeRepositoryAction;
    private OpenRepositoryNodeAction openRepositoryAction;
    private Map<String, QueryNode> queryNodesMap;
    private RepositoryListener repositoryListener;

    public RepositoryNode(RepositoryImpl repository) {
        this(repository, true);
    }

    public RepositoryNode(RepositoryImpl repository, boolean opened) {
        super(opened, null, repository.getDisplayName());
        this.repository = repository;
        this.refresh = false;
        queryNodesMap = new HashMap<String, QueryNode>();
        repositoryListener = new RepositoryListener();
    }

    @Override
    protected Collection<QueryImpl> load() {
        if (refresh && queryNodes != null) {
            for (QueryNode queryNode : queryNodes) {
                queryNode.refreshContent();
            }
            refresh = false;
        }
        return getQueries();
    }

    @Override
    protected void configure(JComponent component, Color foreground, Color background, boolean isSelected, boolean hasFocus) {
        lblName.setText(DashboardUtils.getRepositoryDisplayText(this));
        lblName.setForeground(foreground);
    }

    @Override
    protected JComponent createComponent(Collection<QueryImpl> data) {
        if (isOpened()) {
            updateNodes(data);
            setExpanded(true);
        }
        panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        final JLabel iconLabel = new JLabel(getIcon()); //NOI18N
        if (!isOpened()) {
            iconLabel.setEnabled(false);
        }
        panel.add(iconLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 3), 0, 0));

        lblName = new TreeLabel(getRepository().getDisplayName());
        panel.add(lblName, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 3), 0, 0));
        panel.add(new JLabel(), new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        if (isOpened()) {
            btnRefresh = new LinkButton(ImageUtilities.loadImageIcon("org/netbeans/modules/bugtracking/tasks/resources/refresh.png", true), Actions.RefreshAction.createAction(this)); //NOI18N
            btnRefresh.setToolTipText(NbBundle.getMessage(CategoryNode.class, "LBL_Refresh")); //NOI18N
            panel.add(btnRefresh, new GridBagConstraints(9, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 3, 0, 0), 0, 0));

            btnSearch = new LinkButton(ImageUtilities.loadImageIcon("org/netbeans/modules/bugtracking/tasks/resources/search_repo.png", true), new QuickSearchAction(this)); //NOI18N
            btnSearch.setToolTipText(NbBundle.getMessage(CategoryNode.class, "LBL_SearchInRepo")); //NOI18N
            panel.add(btnSearch, new GridBagConstraints(8, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 3, 0, 0), 0, 0));

            btnAddQuery = new LinkButton(ImageUtilities.loadImageIcon("org/netbeans/modules/bugtracking/tasks/resources/add_query.png", true), new CreateQueryAction(this)); //NOI18N
            btnAddQuery.setToolTipText(NbBundle.getMessage(CategoryNode.class, "LBL_CreateQuery")); //NOI18N
            panel.add(btnAddQuery, new GridBagConstraints(7, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 3, 0, 0), 0, 0));

            btnCreateTask = new LinkButton(ImageUtilities.loadImageIcon("org/netbeans/modules/bugtracking/tasks/resources/add_task.png", true), new CreateTaskAction(this)); //NOI18N
            btnCreateTask.setToolTipText(NbBundle.getMessage(CategoryNode.class, "LBL_CreateTask")); //NOI18N
            panel.add(btnCreateTask, new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 3, 0, 0), 0, 0));
        }
        return panel;
    }

    @Override
    protected void attach() {
        super.attach();
        repository.addPropertyChangeListener(repositoryListener);
    }

    @Override
    protected void dispose() {
        super.dispose();
        getRepository().removePropertyChangeListener(repositoryListener);
    }

    @Override
    protected List<TreeListNode> createChildren() {
        synchronized (LOCK) {
            if (filteredQueryNodes == null) {
                return new ArrayList<TreeListNode>(0);
            }
            DashboardViewer dashboard = DashboardViewer.getInstance();
            if (!filteredQueryNodes.isEmpty()) {
                List<QueryNode> children = filteredQueryNodes;
                boolean expand = dashboard.expandNodes();
                for (QueryNode queryNode : children) {
                    queryNode.setExpanded(expand);
                }
                Collections.sort(children);
                return new ArrayList<TreeListNode>(children);
            } else {
                List<TreeListNode> children = new ArrayList<TreeListNode>();
                children.add(new EmptyContentNode(this, NbBundle.getMessage(RepositoryNode.class, "LBL_NoQuery")));
                return children;
            }
        }
    }

    private void updateNodes() {
        updateNodes(getQueries());
    }

    private void updateNodes(Collection<QueryImpl> queries) {
        synchronized (LOCK) {
            queryNodes = new ArrayList<QueryNode>();
            filteredQueryNodes = new ArrayList<QueryNode>();
            for (QueryImpl query : queries) {
                QueryNode queryNode = queryNodesMap.get(query.getDisplayName());
                if (queryNode == null) {
                    queryNode = new QueryNode(query, this, true);
                    queryNodesMap.put(query.getDisplayName(), queryNode);
                }
                queryNode.updateContent();
                queryNodes.add(queryNode);
                if (queryNode.getFilteredTaskCount() > 0 || !DashboardViewer.getInstance().expandNodes()) {
                    filteredQueryNodes.add(queryNode);
                }
            }
        }
    }

    public final RepositoryImpl getRepository() {
        return repository;
    }

    public boolean isOpened() {
        return true;
    }

    @Override
    public final Action[] getPopupActions() {
        List<TreeListNode> selectedNodes = DashboardViewer.getInstance().getSelectedNodes();
        RepositoryNode[] repositoryNodes = new RepositoryNode[selectedNodes.size()];
        boolean justRepositories = true;
        for (int i = 0; i < selectedNodes.size(); i++) {
            TreeListNode treeListNode = selectedNodes.get(i);
            if (treeListNode instanceof RepositoryNode) {
                repositoryNodes[i] = (RepositoryNode) treeListNode;
            } else {
                justRepositories = false;
                break;
            }
        }
        List<Action> actions = new ArrayList<Action>();
        if (justRepositories) {
            Action repositoryAction = getRepositoryAction(repositoryNodes);
            if (repositoryAction != null) {
                actions.add(repositoryAction);
            }
            actions.addAll(Actions.getRepositoryPopupActions(repositoryNodes));
        }

        actions.addAll(Actions.getDefaultActions(selectedNodes.toArray(new TreeListNode[selectedNodes.size()])));
        return actions.toArray(new Action[actions.size()]);
    }

    private Action getRepositoryAction(RepositoryNode... repositoryNodes) {
        boolean allOpened = true;
        boolean allClosed = true;
        for (RepositoryNode repositoryNode : repositoryNodes) {
            if (repositoryNode.isOpened()) {
                allClosed = false;
            } else {
                allOpened = false;
            }
        }
        if (allOpened) {
            if (closeRepositoryAction == null) {
                closeRepositoryAction = new CloseRepositoryNodeAction(this);
            }
            return closeRepositoryAction;
        } else if (allClosed) {
            if (openRepositoryAction == null) {
                openRepositoryAction = new OpenRepositoryNodeAction(this);
            }
            return openRepositoryAction;
        }
        return null;
    }

    public List<QueryNode> getQueryNodes() {
        return queryNodes;
    }

    public final int getFilteredQueryCount() {
        synchronized (LOCK) {
            return filteredQueryNodes != null ? filteredQueryNodes.size() : 0;
        }
    }

    public void setFilteredQueryNodes(List<QueryNode> filteredQueryNodes) {
        this.filteredQueryNodes = filteredQueryNodes;
    }

    public int getFilterHits() {
        if (filteredQueryNodes == null) {
            return 0;
        }
        int hits = 0;
        for (QueryNode queryNode : filteredQueryNodes) {
            hits += queryNode.getFilteredTaskCount();
        }
        return hits;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RepositoryNode other = (RepositoryNode) obj;
        return repository.getDisplayName().equalsIgnoreCase(other.repository.getDisplayName());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (this.repository != null ? this.repository.hashCode() : 0);
        return hash;
    }

    @Override
    public int compareTo(RepositoryNode toCompare) {
        if (this.isOpened() != toCompare.isOpened()) {
            return this.isOpened() ? -1 : 1;
        } else {
            return repository.getDisplayName().compareToIgnoreCase(toCompare.repository.getDisplayName());
        }
    }

    @Override
    public String toString() {
        return repository.getDisplayName();
    }

    void updateContent() {
        updateNodes();
        refreshChildren();
    }

    Collection<QueryImpl> getQueries() {
        return repository.getQueries();
    }

    ImageIcon getIcon() {
        return ImageUtilities.loadImageIcon("org/netbeans/modules/bugtracking/tasks/resources/remote_repo.png", true);
    }

    @Override
    public void refreshContent() {
        refresh = true;
        refresh();
    }

    private class RepositoryListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(RepositoryImpl.EVENT_QUERY_LIST_CHANGED)) {
                updateContent();
            } else if (evt.getPropertyName().equals(RepositoryImpl.EVENT_ATTRIBUTES_CHANGED)) {
                if (evt.getNewValue() instanceof Map) {
                    Map<String, String> attributes = (Map<String, String>) evt.getNewValue();
                    String displayName = attributes.get(RepositoryImpl.ATTRIBUTE_DISPLAY_NAME);
                    if (displayName != null && !displayName.isEmpty()) {
                        if (lblName != null) {
                            lblName.setText(displayName);
                            fireContentChanged();
                        }
                    }
                }
            }
        }
    }
}
