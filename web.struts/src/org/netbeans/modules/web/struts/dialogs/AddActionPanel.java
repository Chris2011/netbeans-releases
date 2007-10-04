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

package org.netbeans.modules.web.struts.dialogs;

import java.util.Iterator;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingUtilities;
//import org.netbeans.modules.j2ee.common.FQNSearch;
import org.netbeans.modules.web.struts.StrutsConfigDataObject;
import org.netbeans.modules.web.struts.StrutsConfigUtilities;
import org.netbeans.modules.web.struts.config.model.Action;
import org.netbeans.modules.web.struts.config.model.FormBean;
import org.openide.util.NbBundle;

/**
 *
 * @author  Milan Kuchtiak
 */
public class AddActionPanel extends javax.swing.JPanel implements ValidatingPanel {
    /** Creates new form AddFIActionPanel */
    StrutsConfigDataObject config;
    public AddActionPanel(StrutsConfigDataObject dObject) {
        config=dObject;
        initComponents();
        List actions = StrutsConfigUtilities.getAllActionsInModule(dObject);
        DefaultComboBoxModel model = (DefaultComboBoxModel)CBInputAction.getModel();
        Iterator iter = actions.iterator();
        while (iter.hasNext())
            model.addElement(((Action)iter.next()).getAttributeValue("path")); //NOI18N
        
        List formBeans = StrutsConfigUtilities.getAllFormBeansInModule(dObject);
        model = (DefaultComboBoxModel)CBFormName.getModel();
        iter = formBeans.iterator();
        while (iter.hasNext())
            model.addElement(((FormBean)iter.next()).getAttributeValue("name")); //NOI18N
    }

    public String validatePanel() {
        if (TFActionClass.getText().trim().length()==0)
            return NbBundle.getMessage(AddActionPanel.class,"MSG_EmptyActionClass");
        String actionPath = TFActionPath.getText().trim();
        if (actionPath.length()==0 || actionPath.equals("/")) //NOI18N
            return NbBundle.getMessage(AddActionPanel.class,"MSG_EmptyActionPath");
        if (!actionPath.startsWith("/") ) //NOI18N
            return NbBundle.getMessage(AddActionPanel.class,"MSG_IncorrectActionPath", actionPath);
        if (containsActionPath(actionPath)) //NOI18N
            return NbBundle.getMessage(AddActionPanel.class,"MSG_DupliciteActionPath",actionPath);
        if (CHBUseFormBean.isSelected()) {
            if (CBFormName.getSelectedItem()==null)
                NbBundle.getMessage(AddActionPanel.class,"MSG_EmptyFormName");
            if (RBInputResource.isSelected()) {
                String inputResource = TFInputResource.getText().trim();
                if (inputResource.length()==0 || inputResource.equals("/")) //NOI18N
                    return NbBundle.getMessage(AddActionPanel.class,"MSG_EmptyInputResource");
            } else if (CBInputAction.getSelectedItem()==null) {
                return NbBundle.getMessage(AddActionPanel.class,"MSG_EmptyAction");
            }
        }
        return null;
    }

    public javax.swing.AbstractButton[] getStateChangeComponents() {
        return new javax.swing.AbstractButton[]{ CHBUseFormBean, RBInputResource, RBInputAction };
    }

    public javax.swing.text.JTextComponent[] getDocumentChangeComponents() {
        return new javax.swing.text.JTextComponent[]{TFActionClass, TFActionPath, TFInputResource};
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jLabelFormName = new javax.swing.JLabel();
        CBFormName = new javax.swing.JComboBox();
        CBInputAction = new javax.swing.JComboBox();
        TFInputResource = new javax.swing.JTextField();
        CHBUseFormBean = new javax.swing.JCheckBox();
        jButtonBrowse = new javax.swing.JButton();
        jLabelScope = new javax.swing.JLabel();
        jLabelAttribute = new javax.swing.JLabel();
        TFAttribute = new javax.swing.JTextField();
        CHBValidate = new javax.swing.JCheckBox();
        jLabelParameter = new javax.swing.JLabel();
        TFParameter = new javax.swing.JTextField();
        RBInputResource = new javax.swing.JRadioButton();
        RBInputAction = new javax.swing.JRadioButton();
        jPanel1 = new javax.swing.JPanel();
        RBSession = new javax.swing.JRadioButton();
        RBRequest = new javax.swing.JRadioButton();
        jLabelActionClass = new javax.swing.JLabel();
        TFActionClass = new javax.swing.JTextField();
        jButtonBrowseClass = new javax.swing.JButton();
        jLabelActionPath = new javax.swing.JLabel();
        TFActionPath = new javax.swing.JTextField();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 11, 11));
        setLayout(new java.awt.GridBagLayout());

        jLabelFormName.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(AddActionPanel.class, "LBL_FormName_mnem").charAt(0));
        jLabelFormName.setLabelFor(CBFormName);
        jLabelFormName.setText(org.openide.util.NbBundle.getMessage(AddActionPanel.class, "LBL_FormName")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 0, 0);
        add(jLabelFormName, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(CBFormName, gridBagConstraints);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/web/struts/dialogs/Bundle"); // NOI18N
        CBFormName.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_CBFormName")); // NOI18N

        CBInputAction.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(CBInputAction, gridBagConstraints);
        CBInputAction.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(AddActionPanel.class, "ACSN_TFInputAction")); // NOI18N
        CBInputAction.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_CBInputAction")); // NOI18N

        TFInputResource.setText("/");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(TFInputResource, gridBagConstraints);
        TFInputResource.getAccessibleContext().setAccessibleName(bundle.getString("ACSN_TFInputResource")); // NOI18N
        TFInputResource.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_TFinputResource")); // NOI18N

        CHBUseFormBean.setMnemonic(org.openide.util.NbBundle.getMessage(AddActionPanel.class, "LBL_UseFormBean_mnem").charAt(0));
        CHBUseFormBean.setSelected(true);
        CHBUseFormBean.setText(org.openide.util.NbBundle.getMessage(AddActionPanel.class, "CB_UseFormBean")); // NOI18N
        CHBUseFormBean.setMargin(new java.awt.Insets(0, 0, 0, 0));
        CHBUseFormBean.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                CHBUseFormBeanItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        add(CHBUseFormBean, gridBagConstraints);
        CHBUseFormBean.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_CHBUseFormBean")); // NOI18N

        jButtonBrowse.setMnemonic(org.openide.util.NbBundle.getMessage(AddActionPanel.class, "LBL_BrowseButton_mnem").charAt(0));
        jButtonBrowse.setText(org.openide.util.NbBundle.getMessage(AddActionPanel.class, "LBL_BrowseButton")); // NOI18N
        jButtonBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(jButtonBrowse, gridBagConstraints);
        jButtonBrowse.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_jButtonBrowse")); // NOI18N

        jLabelScope.setText(org.openide.util.NbBundle.getMessage(AddActionPanel.class, "LBL_Scope")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 0, 0);
        add(jLabelScope, gridBagConstraints);

        jLabelAttribute.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(AddActionPanel.class, "LBL_Attribute_mnem").charAt(0));
        jLabelAttribute.setLabelFor(TFAttribute);
        jLabelAttribute.setText(org.openide.util.NbBundle.getMessage(AddActionPanel.class, "LBL_Attribute")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 0, 0);
        add(jLabelAttribute, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(TFAttribute, gridBagConstraints);
        TFAttribute.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_TFAttribute")); // NOI18N

        CHBValidate.setMnemonic(org.openide.util.NbBundle.getMessage(AddActionPanel.class, "CB_Validate_mnem").charAt(0));
        CHBValidate.setSelected(true);
        CHBValidate.setText(org.openide.util.NbBundle.getMessage(AddActionPanel.class, "CB_Validate")); // NOI18N
        CHBValidate.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 0, 0);
        add(CHBValidate, gridBagConstraints);
        CHBValidate.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_CHBValidate")); // NOI18N

        jLabelParameter.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(AddActionPanel.class, "LBL_Parameter_mnem").charAt(0));
        jLabelParameter.setLabelFor(TFParameter);
        jLabelParameter.setText(org.openide.util.NbBundle.getMessage(AddActionPanel.class, "LBL_Parameter")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        add(jLabelParameter, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 12, 0, 0);
        add(TFParameter, gridBagConstraints);
        TFParameter.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_TFParameter")); // NOI18N

        buttonGroup1.add(RBInputResource);
        RBInputResource.setMnemonic(org.openide.util.NbBundle.getMessage(AddActionPanel.class, "RB_InputResource_mnem").charAt(0));
        RBInputResource.setSelected(true);
        RBInputResource.setText(org.openide.util.NbBundle.getMessage(AddActionPanel.class, "RB_InputResource")); // NOI18N
        RBInputResource.setMargin(new java.awt.Insets(0, 0, 0, 0));
        RBInputResource.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                RBInputResourceItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 0, 0);
        add(RBInputResource, gridBagConstraints);
        RBInputResource.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_RBInputResources")); // NOI18N

        buttonGroup1.add(RBInputAction);
        RBInputAction.setMnemonic(org.openide.util.NbBundle.getMessage(AddActionPanel.class, "RB_InputAction_mnem").charAt(0));
        RBInputAction.setText(org.openide.util.NbBundle.getMessage(AddActionPanel.class, "RB_InputAction")); // NOI18N
        RBInputAction.setMargin(new java.awt.Insets(0, 0, 0, 0));
        RBInputAction.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                RBInputActionItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 0, 0);
        add(RBInputAction, gridBagConstraints);
        RBInputAction.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_RBInputAction")); // NOI18N

        buttonGroup2.add(RBSession);
        RBSession.setMnemonic(org.openide.util.NbBundle.getMessage(AddActionPanel.class, "RB_Session_mnem").charAt(0));
        RBSession.setSelected(true);
        RBSession.setText(org.openide.util.NbBundle.getMessage(AddActionPanel.class, "RB_Sesson")); // NOI18N
        RBSession.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jPanel1.add(RBSession);
        RBSession.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_RBSession")); // NOI18N

        buttonGroup2.add(RBRequest);
        RBRequest.setMnemonic(org.openide.util.NbBundle.getMessage(AddActionPanel.class, "RB_Request_mnem").charAt(0));
        RBRequest.setText(org.openide.util.NbBundle.getMessage(AddActionPanel.class, "RB_Request")); // NOI18N
        RBRequest.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jPanel1.add(RBRequest);
        RBRequest.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_RBRequest")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(jPanel1, gridBagConstraints);
        jPanel1.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_Scope")); // NOI18N

        jLabelActionClass.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(AddActionPanel.class, "LBL_ActionClass_mnem").charAt(0));
        jLabelActionClass.setLabelFor(TFActionClass);
        jLabelActionClass.setText(org.openide.util.NbBundle.getMessage(AddActionPanel.class, "LBL_ActionClass")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabelActionClass, gridBagConstraints);

        TFActionClass.setColumns(30);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        add(TFActionClass, gridBagConstraints);
        TFActionClass.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_TFActionClass")); // NOI18N

        jButtonBrowseClass.setMnemonic(org.openide.util.NbBundle.getMessage(AddActionPanel.class, "LBL_Browse_mnem").charAt(0));
        jButtonBrowseClass.setText(org.openide.util.NbBundle.getMessage(AddActionPanel.class, "LBL_BrowseButton")); // NOI18N
        jButtonBrowseClass.setEnabled(false);
        jButtonBrowseClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseClassActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(jButtonBrowseClass, gridBagConstraints);
        jButtonBrowseClass.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_jButtonBrowseClass")); // NOI18N

        jLabelActionPath.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(AddActionPanel.class, "LBL_ActionPath_mnem").charAt(0));
        jLabelActionPath.setLabelFor(TFActionPath);
        jLabelActionPath.setText(org.openide.util.NbBundle.getMessage(AddActionPanel.class, "LBL_ActionPath")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(jLabelActionPath, gridBagConstraints);

        TFActionPath.setText("/");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(TFActionPath, gridBagConstraints);
        TFActionPath.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_TFActionPath")); // NOI18N

        getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_AddActionDialog")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonBrowseClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseClassActionPerformed
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            //TODO: RETOUCHE  
//            FQNSearch.showFastOpen(TFActionClass);
            }
        }); 
                
    }//GEN-LAST:event_jButtonBrowseClassActionPerformed

    private void jButtonBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseActionPerformed
// TODO add your handling code here:
        try{
        org.netbeans.api.project.SourceGroup[] groups = StrutsConfigUtilities.getDocBaseGroups(config.getPrimaryFile());
            org.openide.filesystems.FileObject fo = BrowseFolders.showDialog(groups);
            if (fo!=null) {
                String res = "/"+StrutsConfigUtilities.getResourcePath(groups,fo,'/',true);
                TFInputResource.setText(res);
            }
        } catch (java.io.IOException ex) {}
    }//GEN-LAST:event_jButtonBrowseActionPerformed

    private void RBInputActionItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_RBInputActionItemStateChanged
// TODO add your handling code here:
        boolean selected = RBInputAction.isSelected();
        TFInputResource.setEditable(!selected);
        jButtonBrowse.setEnabled(!selected);
        CBInputAction.setEnabled(selected);
    }//GEN-LAST:event_RBInputActionItemStateChanged

    private void RBInputResourceItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_RBInputResourceItemStateChanged
// TODO add your handling code here:
        boolean selected = RBInputResource.isSelected();
        TFInputResource.setEditable(selected);
        jButtonBrowse.setEnabled(selected);
        CBInputAction.setEnabled(!selected);
    }//GEN-LAST:event_RBInputResourceItemStateChanged

    private void CHBUseFormBeanItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_CHBUseFormBeanItemStateChanged
// TODO add your handling code here:
        boolean selected = CHBUseFormBean.isSelected();
        CBFormName.setEnabled(selected);
        RBInputResource.setEnabled(selected);
        RBInputAction.setEnabled(selected);
        if (selected) {
            if (RBInputResource.isSelected()) {
                TFInputResource.setEditable(true);
                jButtonBrowse.setEnabled(true);
            } else {
                CBInputAction.setEnabled(true);
            }
        } else {
            TFInputResource.setEditable(false);
            jButtonBrowse.setEnabled(false);
            CBInputAction.setEnabled(false);
        }
        
        RBSession.setEnabled(selected);
        RBRequest.setEnabled(selected);
        TFAttribute.setEditable(selected);
        CHBValidate.setEnabled(selected);
    }//GEN-LAST:event_CHBUseFormBeanItemStateChanged
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox CBFormName;
    private javax.swing.JComboBox CBInputAction;
    private javax.swing.JCheckBox CHBUseFormBean;
    private javax.swing.JCheckBox CHBValidate;
    private javax.swing.JRadioButton RBInputAction;
    private javax.swing.JRadioButton RBInputResource;
    private javax.swing.JRadioButton RBRequest;
    private javax.swing.JRadioButton RBSession;
    private javax.swing.JTextField TFActionClass;
    private javax.swing.JTextField TFActionPath;
    private javax.swing.JTextField TFAttribute;
    private javax.swing.JTextField TFInputResource;
    private javax.swing.JTextField TFParameter;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton jButtonBrowse;
    private javax.swing.JButton jButtonBrowseClass;
    private javax.swing.JLabel jLabelActionClass;
    private javax.swing.JLabel jLabelActionPath;
    private javax.swing.JLabel jLabelAttribute;
    private javax.swing.JLabel jLabelFormName;
    private javax.swing.JLabel jLabelParameter;
    private javax.swing.JLabel jLabelScope;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
    
    public String getActionClass() {
        return TFActionClass.getText().trim();
    }
    
    public String getActionPath() {
        return TFActionPath.getText().trim();
    }
    
    public String getFormName() {
        return (String)CBFormName.getSelectedItem();
    }
    
    public String getInput() {
        if (!CHBUseFormBean.isSelected()) return null;
        if (RBInputResource.isSelected()) {
            String input=TFInputResource.getText().trim();
            return input.length()==0?null:input;
        } else {
            return (String)CBInputAction.getSelectedItem();
        }
    }
    
    public String getScope() {
        if (!CHBUseFormBean.isSelected()) return null;
        if (RBSession.isSelected()) {
            return "session"; //NOI18N
        } else {
            return "request"; //NOI18N
        }
    }
    
    public String getValidate() {
        if (!CHBUseFormBean.isSelected()) return null;
        if (CHBValidate.isSelected()) return null;
        return "false"; //NOI18N
    }
    
    public String getAttribute() {
        if (!CHBUseFormBean.isSelected()) return null;
        String attr=TFAttribute.getText().trim();
        return attr.length()==0?null:attr;
    }
    
    public String getParameter() {
        String param=TFParameter.getText().trim();
        return param.length()==0?null:param;
    }
    
    public boolean isActionFormUsed(){
        return CHBUseFormBean.isSelected();
    }
    
    private boolean containsActionPath(String path) {
        DefaultComboBoxModel model = (DefaultComboBoxModel)CBInputAction.getModel();
        for (int i=0; i<model.getSize(); i++) {
            if (path.equals(model.getElementAt(i))) return true;
        }
        return false;
    }
}
