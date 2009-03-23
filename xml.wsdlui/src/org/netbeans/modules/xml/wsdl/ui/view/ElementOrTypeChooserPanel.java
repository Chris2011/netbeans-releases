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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

/*
 * ElementOrTypeChooserPanel.java
 *
 * Created on September 1, 2006, 2:37 PM
 */

package org.netbeans.modules.xml.wsdl.ui.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.netbeans.api.project.Project;
import org.netbeans.modules.xml.schema.model.GlobalElement;
import org.netbeans.modules.xml.schema.model.GlobalType;
import org.netbeans.modules.xml.schema.model.SchemaComponent;
import org.netbeans.modules.xml.schema.model.SchemaComponentReference;
import org.netbeans.modules.xml.wsdl.model.WSDLModel;
import org.netbeans.modules.xml.wsdl.ui.api.property.ElementOrTypeChooserHelper;
import org.netbeans.modules.xml.wsdl.ui.netbeans.module.Utility;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author  skini
 */
public class ElementOrTypeChooserPanel extends javax.swing.JPanel implements ExplorerManager.Provider {
    
    private Map<String, String> namespaceToPrefixMap;
    private Project mProject;
    private WSDLModel mModel;
    private SchemaComponent mPreviousSelectedComponent;
    private Node mRootNode = null;
    
    /** Creates new form ElementOrTypeChooserPanel */
    public ElementOrTypeChooserPanel(Project project, Map<String, String> namespaceToPrefixMap, WSDLModel model) {
        this.namespaceToPrefixMap = namespaceToPrefixMap;
        this.mProject = project;
        this.mModel = model;
        initComponents();
        initGUI();
    }
    
    public ElementOrTypeChooserPanel(Project project, Map<String, String> namespaceToPrefixMap, WSDLModel model, SchemaComponent previousSelectedComponent) {
        this.namespaceToPrefixMap = namespaceToPrefixMap;
        this.mProject = project;
        this.mModel = model;
        this.mPreviousSelectedComponent = previousSelectedComponent;
        initComponents();
        initGUI();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        beanTreeView1 = new org.openide.explorer.view.BeanTreeView();

        setName("Form"); // NOI18N

        beanTreeView1.setAutoscrolls(true);
        beanTreeView1.setDefaultActionAllowed(false);
        beanTreeView1.setDragSource(false);
        beanTreeView1.setDropTarget(false);
        beanTreeView1.setFocusable(false);
        beanTreeView1.setName("beanTreeView1"); // NOI18N
        beanTreeView1.setPopupAllowed(false);
        beanTreeView1.setRootVisible(false);
        beanTreeView1.setSelectionMode(1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(beanTreeView1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(beanTreeView1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    
    private void initGUI() {
        manager = new ExplorerManager();
        manager.addPropertyChangeListener(new ExplorerPropertyChangeListener());
        Node rootNode = new AbstractNode(new Children.Array());
        manager.setRootContext(rootNode);
        populateRootNode(rootNode);
        Utility.expandNodes(beanTreeView1, 3, rootNode);
    }
    
    private void populateRootNode(Node rootNode) {
        ElementOrTypeChooserHelper schemaHelper = new ElementOrTypeChooserHelper(mProject, mModel);
        schemaHelper.populateNodes(rootNode);
        
        if (mPreviousSelectedComponent != null) {
            Node node = schemaHelper.selectNode(mPreviousSelectedComponent);
            if (node != null) {
                selectNode(node);
            }
        } else {
            selectNode(rootNode);
        }
        
    }
    
    private void selectNode(Node node) {
        final Node finalNode = node;
        Runnable run = new Runnable() {
            public void run() {
                if(manager != null) {
                    try {
                        manager.setExploredContextAndSelection(finalNode, new Node[] {finalNode});
                        beanTreeView1.expandNode(finalNode);
                    } catch(PropertyVetoException ex) {
                        //ignore this
                    }
                    // somehow expanding of nodes causes the scroll to go to the 
                    // last expanded node.  if the previous selection is null, 
                    // then reexpand the 1st node to force the scroll to be on top
                    if ((mPreviousSelectedComponent == null) && 
                            (manager.getRootContext() != null) && 
                            (manager.getRootContext().getChildren().getNodesCount() > 0)) {
                        beanTreeView1.collapseNode(manager.getRootContext().getChildren().getNodes()[0]);
                        beanTreeView1.expandNode(manager.getRootContext().getChildren().getNodes()[0]);                        
                    }
                }
            }
        };
        SwingUtilities.invokeLater(run);
    }
    
    public ExplorerManager getExplorerManager() {
        return manager;
    }
    
    
    
    public static final void main(String[] args) {
/*        JFrame frame = new JFrame();
        frame.add(new ElementOrTypeChooserEditorPanel());
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);*/
    }

    public void apply() {
        if (selectedComponent != null) {
            if (selectedComponent instanceof GlobalType) {
                selectedElementOrType = new ElementOrType((GlobalType)selectedComponent, namespaceToPrefixMap);
            } else if (selectedComponent instanceof GlobalElement) {
                selectedElementOrType = new ElementOrType((GlobalElement)selectedComponent, namespaceToPrefixMap);
            }
        }
    }
    
    private ExplorerManager manager;
    public static String PROP_ACTION_APPLY = "APPLY";
    private ElementOrType selectedElementOrType;
    private SchemaComponent selectedComponent;
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.openide.explorer.view.BeanTreeView beanTreeView1;
    // End of variables declaration//GEN-END:variables
    class ExplorerPropertyChangeListener implements PropertyChangeListener {
        
        public void propertyChange(PropertyChangeEvent evt) {
            if(evt.getPropertyName().equals(ExplorerManager.PROP_SELECTED_NODES)) {
                Node[] nodes = (Node[]) evt.getNewValue();
                if(nodes.length > 0) {
                    Node node = nodes[0];
                    //set the selected node to null and state as invalid by default
                    firePropertyChange(PROP_ACTION_APPLY, true, false);
                    SchemaComponent sc = null;
                    SchemaComponentReference reference = node.getLookup().lookup(SchemaComponentReference.class);
                    if (reference != null) {
                        sc = reference.get();
                    }
                    if (sc == null) {
                        sc = node.getLookup().lookup(SchemaComponent.class);
                    }

                    if (sc != null && (sc instanceof GlobalType || sc instanceof GlobalElement)) {
                        selectedComponent = sc;
                        firePropertyChange(PROP_ACTION_APPLY, false, true);
                    }
                }
            }
        }
    }

    public ElementOrType getSelectedComponent() {
        return selectedElementOrType;
    }
    
    public SchemaComponent getSelectedSchemaComponent() {
        return selectedComponent;
    }

    public void setSelectedComponent(ElementOrType selectedComponent) {
        this.selectedElementOrType = selectedComponent;
    }
    
}
