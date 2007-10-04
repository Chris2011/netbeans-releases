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

package org.netbeans.modules.web.jsf.dialogs;

import java.util.Collection;
import java.util.Iterator;
import javax.swing.DefaultComboBoxModel;
import javax.swing.text.JTextComponent;
import org.netbeans.modules.web.jsf.JSFConfigDataObject;
import org.netbeans.modules.web.jsf.JSFConfigUtilities;
import org.netbeans.modules.web.jsf.api.ConfigurationUtils;
import org.netbeans.modules.web.jsf.api.facesmodel.FacesConfig;
import org.netbeans.modules.web.jsf.api.facesmodel.NavigationCase;
import org.netbeans.modules.web.jsf.api.facesmodel.NavigationRule;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author  radko
 */
public class AddNavigationCaseDialog extends javax.swing.JPanel implements ValidatingPanel{
    private JSFConfigDataObject config;
    /** Creates new form AddNavigationCaseDialog */
    public AddNavigationCaseDialog(JSFConfigDataObject config, String rule) {
        initComponents();
        this.config = config;
        FacesConfig facesConfig = ConfigurationUtils.getConfigModel(config.getPrimaryFile(), true).getRootComponent();
        
        DefaultComboBoxModel modelF = (DefaultComboBoxModel)jComboBoxFromView.getModel();
        DefaultComboBoxModel modelT = (DefaultComboBoxModel)jComboBoxToView.getModel();
        modelF.addElement("");
        modelT.addElement("");
        Iterator iter = facesConfig.getNavigationRules().iterator();
        while (iter.hasNext()) {
            String fromViewID=((NavigationRule)iter.next()).getFromViewId();
            if (fromViewID != null && fromViewID.trim().length() > 0){
                modelF.addElement(fromViewID);
                modelT.addElement(fromViewID);
            }
        }
        if (rule != null)
            jComboBoxFromView.setSelectedItem(rule);
    }
    
    public javax.swing.text.JTextComponent[] getDocumentChangeComponents() {
        return new javax.swing.text.JTextComponent[]{(JTextComponent)jComboBoxFromView.getEditor().getEditorComponent(),
        (JTextComponent) jComboBoxToView.getEditor().getEditorComponent(), jTextFieldFromAction, jTextFieldFromOutcome};
    }
    
    public javax.swing.AbstractButton[] getStateChangeComponents() {
        return new javax.swing.AbstractButton[]{  };
    }
    
    public String validatePanel() {
        if (getToView().length()==0)
            return NbBundle.getMessage(AddManagedBeanDialog.class,"MSG_AddNavigationCase_EmptyToView");
        if (getFromAction().length() == 0 && getFromOutcome().length() == 0)
            return NbBundle.getMessage(AddManagedBeanDialog.class,"MSG_AddNavigationCase_EmptyFromActionOutcome");
        NavigationRule rule = JSFConfigUtilities.findNavigationRule(config, getRule());
        //if the rule exist, check whether doesn't already indlclude the case
        if (rule != null){
            
            Collection<NavigationCase> cases = rule.getNavigationCases();
            String from;
            for (Iterator<NavigationCase> it = cases.iterator(); it.hasNext();) {
                NavigationCase navigationCase = it.next();
                from = navigationCase.getFromAction();
                if (from != null && from.equals(getFromAction()))
                    return NbBundle.getMessage(AddManagedBeanDialog.class,"MSG_AddNavigationCase_FromActionExist");
                from = navigationCase.getFromOutcome();
                if (from != null && from.equals(getFromOutcome()))
                    return NbBundle.getMessage(AddManagedBeanDialog.class,"MSG_AddNavigationCase_FromOutcomeExist");
            }
        }
        return null;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabelFromView = new javax.swing.JLabel();
        jComboBoxFromView = new javax.swing.JComboBox();
        jButtonFromView = new javax.swing.JButton();
        jLabelFromAction = new javax.swing.JLabel();
        jTextFieldFromAction = new javax.swing.JTextField();
        jLabelFromOutcome = new javax.swing.JLabel();
        jTextFieldFromOutcome = new javax.swing.JTextField();
        jLabelToView = new javax.swing.JLabel();
        jButtonToView = new javax.swing.JButton();
        jCheckBoxRedirect = new javax.swing.JCheckBox();
        jLabelDesc = new javax.swing.JLabel();
        jScrollPaneDesc = new javax.swing.JScrollPane();
        jTextAreaDesc = new javax.swing.JTextArea();
        jSeparator1 = new javax.swing.JSeparator();
        jComboBoxToView = new javax.swing.JComboBox();

        setLayout(new java.awt.GridBagLayout());

        getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/jsf/dialogs/Bundle").getString("ACSD_AddNavigationCaseDialog"));
        jLabelFromView.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(AddNavigationCaseDialog.class, "MNE_FromView").charAt(0));
        jLabelFromView.setLabelFor(jComboBoxFromView);
        jLabelFromView.setText(org.openide.util.NbBundle.getMessage(AddNavigationCaseDialog.class, "LBL_CaseFromView"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 5, 12);
        add(jLabelFromView, gridBagConstraints);

        jComboBoxFromView.setEditable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 5, 0);
        add(jComboBoxFromView, gridBagConstraints);
        jComboBoxFromView.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/jsf/dialogs/Bundle").getString("ACSD_FromView"));

        jButtonFromView.setMnemonic(org.openide.util.NbBundle.getMessage(AddNavigationCaseDialog.class, "MNE_Browse").charAt(0));
        jButtonFromView.setText(org.openide.util.NbBundle.getMessage(AddNavigationCaseDialog.class, "LBL_Browse"));
        jButtonFromView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFromViewActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 5, 11);
        add(jButtonFromView, gridBagConstraints);
        jButtonFromView.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/jsf/dialogs/Bundle").getString("ACSD_BrowseFromView"));

        jLabelFromAction.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(AddNavigationCaseDialog.class, "MNE_FromAction").charAt(0));
        jLabelFromAction.setLabelFor(jTextFieldFromAction);
        jLabelFromAction.setText(org.openide.util.NbBundle.getMessage(AddNavigationCaseDialog.class, "LBL_FromAction"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 5, 12);
        add(jLabelFromAction, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(jTextFieldFromAction, gridBagConstraints);
        jTextFieldFromAction.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/jsf/dialogs/Bundle").getString("ACSD_FromAction"));

        jLabelFromOutcome.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(AddNavigationCaseDialog.class, "MNE_FromOutcome").charAt(0));
        jLabelFromOutcome.setLabelFor(jTextFieldFromOutcome);
        jLabelFromOutcome.setText(org.openide.util.NbBundle.getMessage(AddNavigationCaseDialog.class, "LBL_FromOutcome"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 5, 12);
        add(jLabelFromOutcome, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(jTextFieldFromOutcome, gridBagConstraints);
        jTextFieldFromOutcome.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/jsf/dialogs/Bundle").getString("ACSD_FromOutcome"));

        jLabelToView.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(AddNavigationCaseDialog.class, "MNE_ToView").charAt(0));
        jLabelToView.setLabelFor(jComboBoxToView);
        jLabelToView.setText(org.openide.util.NbBundle.getMessage(AddNavigationCaseDialog.class, "LBL_ToView"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 5, 12);
        add(jLabelToView, gridBagConstraints);

        jButtonToView.setMnemonic(org.openide.util.NbBundle.getMessage(AddNavigationCaseDialog.class, "MNE_BrowseToView").charAt(0));
        jButtonToView.setText(org.openide.util.NbBundle.getMessage(AddNavigationCaseDialog.class, "LBL_Browse"));
        jButtonToView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonToViewActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 5, 11);
        add(jButtonToView, gridBagConstraints);
        jButtonToView.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/jsf/dialogs/Bundle").getString("ACSD_BrowseToView"));

        jCheckBoxRedirect.setMnemonic(org.openide.util.NbBundle.getMessage(AddNavigationCaseDialog.class, "MNE_Redirect").charAt(0));
        jCheckBoxRedirect.setText(org.openide.util.NbBundle.getMessage(AddNavigationCaseDialog.class, "LBL_Redirect"));
        jCheckBoxRedirect.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxRedirect.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 5, 0);
        add(jCheckBoxRedirect, gridBagConstraints);
        jCheckBoxRedirect.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/jsf/dialogs/Bundle").getString("ACSD_Redirect"));

        jLabelDesc.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(AddNavigationCaseDialog.class, "MNE_BeanDescription").charAt(0));
        jLabelDesc.setLabelFor(jTextAreaDesc);
        jLabelDesc.setText(org.openide.util.NbBundle.getMessage(AddNavigationCaseDialog.class, "LBL_RuleDescription"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 11, 12);
        add(jLabelDesc, gridBagConstraints);

        jTextAreaDesc.setColumns(20);
        jTextAreaDesc.setRows(5);
        jScrollPaneDesc.setViewportView(jTextAreaDesc);
        jTextAreaDesc.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/jsf/dialogs/Bundle").getString("ACSD_CaseDescription"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 0);
        add(jScrollPaneDesc, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 6, 11);
        add(jSeparator1, gridBagConstraints);

        jComboBoxToView.setEditable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jComboBoxToView, gridBagConstraints);
        jComboBoxToView.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/jsf/dialogs/Bundle").getString("ACSD_ToViewSelect"));

    }
    // </editor-fold>//GEN-END:initComponents
    
    private void jButtonToViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonToViewActionPerformed
        try{
            org.netbeans.api.project.SourceGroup[] groups = JSFConfigUtilities.getDocBaseGroups(config.getPrimaryFile());
            org.openide.filesystems.FileObject fo = BrowseFolders.showDialog(groups);
            if (fo!=null) {
                String res = "/"+JSFConfigUtilities.getResourcePath(groups,fo,'/',true);
                jComboBoxToView.setSelectedItem(res);
            }
        } catch (java.io.IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_jButtonToViewActionPerformed
    
    private void jButtonFromViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFromViewActionPerformed
        try{
            org.netbeans.api.project.SourceGroup[] groups = JSFConfigUtilities.getDocBaseGroups(config.getPrimaryFile());
            org.openide.filesystems.FileObject fo = BrowseFolders.showDialog(groups);
            if (fo!=null) {
                String res = "/"+JSFConfigUtilities.getResourcePath(groups,fo,'/',true);
                jComboBoxFromView.setSelectedItem(res);
            }
        } catch (java.io.IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_jButtonFromViewActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonFromView;
    private javax.swing.JButton jButtonToView;
    private javax.swing.JCheckBox jCheckBoxRedirect;
    private javax.swing.JComboBox jComboBoxFromView;
    private javax.swing.JComboBox jComboBoxToView;
    private javax.swing.JLabel jLabelDesc;
    private javax.swing.JLabel jLabelFromAction;
    private javax.swing.JLabel jLabelFromOutcome;
    private javax.swing.JLabel jLabelFromView;
    private javax.swing.JLabel jLabelToView;
    private javax.swing.JScrollPane jScrollPaneDesc;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextArea jTextAreaDesc;
    private javax.swing.JTextField jTextFieldFromAction;
    private javax.swing.JTextField jTextFieldFromOutcome;
    // End of variables declaration//GEN-END:variables
    
    public String getRule(){
        return (String)jComboBoxFromView.getSelectedItem();
    }
    
    public String getFromAction(){
        return jTextFieldFromAction.getText();
    }
    
    public String getFromOutcome(){
        return jTextFieldFromOutcome.getText();
    }
    
    public String getToView(){
        return (String)jComboBoxToView.getEditor().getItem();
    }
    
    public boolean isRedirect(){
        return jCheckBoxRedirect.isSelected();
    }
    
    public String getDescription(){
        return jTextAreaDesc.getText();
    }
}
