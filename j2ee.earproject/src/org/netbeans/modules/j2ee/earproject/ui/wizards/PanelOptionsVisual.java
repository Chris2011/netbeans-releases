/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.earproject.ui.wizards;

import java.util.ArrayList;
import java.util.List;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;
import java.util.ResourceBundle;
import java.util.Set;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eePlatform;
import javax.swing.text.Document;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class PanelOptionsVisual extends javax.swing.JPanel implements DocumentListener {
    
    private PanelConfigureProject panel;
    private boolean contextModified = false;
    private List serverInstanceIDs;
    private String projName = "";
    private J2eeVersionWarningPanel warningPanel;
    
    private static final String J2EE_SPEC_13_LABEL = NbBundle.getMessage(PanelOptionsVisual.class, "J2EESpecLevel_13"); //NOI18N
    private static final String J2EE_SPEC_14_LABEL = NbBundle.getMessage(PanelOptionsVisual.class, "J2EESpecLevel_14"); //NOI18N
    
    private static final String WEB_MOD_SUFFIX =
            NbBundle.getMessage(PanelOptionsVisual.class, "WEB_MOD_SUFFIX");
    private static final String EJB_MOD_SUFFIX =
            NbBundle.getMessage(PanelOptionsVisual.class, "EJB_MOD_SUFFIX");
    
    /** Creates new form PanelOptionsVisual */
    public PanelOptionsVisual(PanelConfigureProject panel, boolean importStyle) {
        panel.getProjectTypeFlag();
        initComponents();
        this.panel = panel;
        setJ2eeVersionWarningPanel();
        initServerInstances();
        jTextFieldEjbModuleName.getDocument().addDocumentListener(this);
        jTextFieldWebAppName.getDocument().addDocumentListener(this);
        
        // if this panel is used during import there are lots of things we don't
        // need to ask about -- hide them from the user.
        createEjbCheckBox.setSelected(!importStyle);
        createWARCheckBox.setSelected(!importStyle);
        createEjbCheckBox.setVisible(!importStyle);
        createWARCheckBox.setVisible(!importStyle);
        jTextFieldEjbModuleName.setVisible(!importStyle);
        jTextFieldWebAppName.setVisible(!importStyle);
        //j2eeSpecComboBox.setVisible(!importStyle);
        //j2eeSpecLabel.setVisible(!importStyle);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        setAsMainCheckBox = new javax.swing.JCheckBox();
        j2eeSpecLabel = new javax.swing.JLabel();
        j2eeSpecComboBox = new javax.swing.JComboBox();
        serverInstanceLabel = new javax.swing.JLabel();
        serverInstanceComboBox = new javax.swing.JComboBox();
        createEjbCheckBox = new javax.swing.JCheckBox();
        jTextFieldEjbModuleName = new javax.swing.JTextField();
        createWARCheckBox = new javax.swing.JCheckBox();
        jTextFieldWebAppName = new javax.swing.JTextField();
        warningPlaceHolderPanel = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        setAsMainCheckBox.setMnemonic(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "LBL_NWP1_SetAsMain_CheckBoxMnemonic").charAt(0));
        setAsMainCheckBox.setSelected(true);
        setAsMainCheckBox.setText(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "LBL_NWP1_SetAsMain_CheckBox"));
        setAsMainCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 5, 11, 0);
        add(setAsMainCheckBox, gridBagConstraints);
        setAsMainCheckBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "ACS_LBL_NWP1_SetAsMain_A11YDesc"));

        j2eeSpecLabel.setLabelFor(j2eeSpecComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(j2eeSpecLabel, org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "LBL_NWP1_J2EESpecLevel_Label"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 11, 11);
        add(j2eeSpecLabel, gridBagConstraints);

        j2eeSpecComboBox.setMinimumSize(new java.awt.Dimension(100, 24));
        j2eeSpecComboBox.setPreferredSize(new java.awt.Dimension(100, 24));
        j2eeSpecComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                j2eeSpecComboBoxActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 5);
        add(j2eeSpecComboBox, gridBagConstraints);
        j2eeSpecComboBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "ACS_LBL_NPW1_J2EESpecLevel_A11YDesc"));

        serverInstanceLabel.setLabelFor(serverInstanceComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(serverInstanceLabel, org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "LBL_NWP1_Server_Label"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 11);
        add(serverInstanceLabel, gridBagConstraints);

        serverInstanceComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                serverInstanceComboBoxActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        add(serverInstanceComboBox, gridBagConstraints);
        serverInstanceComboBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "ACS_NWP1_Server_ComboBox_A11YDesc"));

        createEjbCheckBox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(createEjbCheckBox, org.openide.util.NbBundle.getBundle(PanelOptionsVisual.class).getString("LBL_NEAP_CreateEjbModule"));
        createEjbCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        createEjbCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createEjbCheckBox_action(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 11);
        add(createEjbCheckBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        add(jTextFieldEjbModuleName, gridBagConstraints);
        jTextFieldEjbModuleName.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(PanelOptionsVisual.class).getString("ACSN_EjbModuleName"));
        jTextFieldEjbModuleName.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "ACSD_EjbModuleName"));

        createWARCheckBox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(createWARCheckBox, org.openide.util.NbBundle.getBundle(PanelOptionsVisual.class).getString("LBL_NEAP_CreatWebAppModule"));
        createWARCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        createWARCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createWebAppCheckBox_action(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 11, 11);
        add(createWARCheckBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 5);
        add(jTextFieldWebAppName, gridBagConstraints);
        jTextFieldWebAppName.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "ACSN_WebAppName"));
        jTextFieldWebAppName.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "ACSD_WebAppName"));

        warningPlaceHolderPanel.setLayout(new java.awt.BorderLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(warningPlaceHolderPanel, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents
    
    private void createWebAppCheckBox_action(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createWebAppCheckBox_action
        jTextFieldWebAppName.setEnabled(createWARCheckBox.isSelected());
        updateTexts(jTextFieldWebAppName.getDocument());
    }//GEN-LAST:event_createWebAppCheckBox_action
    
    private void createEjbCheckBox_action(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createEjbCheckBox_action
        jTextFieldEjbModuleName.setEnabled(createEjbCheckBox.isSelected());
        updateTexts(jTextFieldEjbModuleName.getDocument());
    }//GEN-LAST:event_createEjbCheckBox_action
    
    private void serverInstanceComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_serverInstanceComboBoxActionPerformed
        String prevSelectedItem = (String)j2eeSpecComboBox.getSelectedItem();
        int dex = serverInstanceComboBox.getSelectedIndex();
        if (dex > -1) {
            String servInsID = (String)serverInstanceIDs.get(serverInstanceComboBox.getSelectedIndex());
            J2eePlatform j2eePlatform = Deployment.getDefault().getJ2eePlatform(servInsID);
            Set supportedVersions = j2eePlatform.getSupportedSpecVersions();
            j2eeSpecComboBox.removeAllItems();
            if (supportedVersions.contains(J2eeModule.J2EE_14)) j2eeSpecComboBox.addItem(J2EE_SPEC_14_LABEL);
            //if (supportedVersions.contains(J2eeModule.J2EE_13)) j2eeSpecComboBox.addItem(J2EE_SPEC_13_LABEL);
            if (prevSelectedItem != null) {
                j2eeSpecComboBox.setSelectedItem(prevSelectedItem);
            }
        }
    }//GEN-LAST:event_serverInstanceComboBoxActionPerformed
    
    private void j2eeSpecComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_j2eeSpecComboBoxActionPerformed
        // trigger validation...
        updateTexts(jTextFieldEjbModuleName.getDocument());
    }//GEN-LAST:event_j2eeSpecComboBoxActionPerformed
    
    boolean valid(WizardDescriptor wizardDescriptor) {
        if (getSelectedServer() == null) {
            String errMsg = NbBundle.getMessage(PanelOptionsVisual.class, "MSG_NoServer"); // NOI18N
            wizardDescriptor.putProperty( "WizardPanel_errorMessage", errMsg); // NOI18N
            return false;
        }
        
        if (createWARCheckBox.isSelected()) {
            String warName = jTextFieldWebAppName.getText();
            if (warName.length() < 1) {
                String errMsg = NbBundle.getMessage(PanelOptionsVisual.class, "MSG_NoWARName"); // NOI18N
                wizardDescriptor.putProperty( "WizardPanel_errorMessage", errMsg); // NOI18N
                return false;
            }
            if (!warName.equals(projName + "-war")) { // NOI18N
                // this is really just a warning
                String errMsg = NbBundle.getMessage(PanelOptionsVisual.class, "MSG_WARNameNotBlueprints"); //NOI18N
                wizardDescriptor.putProperty( "WizardPanel_errorMessage", errMsg); // NOI18N
            }
        }
        
        if (createEjbCheckBox.isSelected()) {
            String jarName = jTextFieldEjbModuleName.getText();
            if (jarName.length() < 1) {
                String errMsg = NbBundle.getMessage(PanelOptionsVisual.class, "MSG_NoJARName"); // NOI18N
                wizardDescriptor.putProperty( "WizardPanel_errorMessage", errMsg); // NOI18N
                return false;
            }
            if (!jarName.equals(projName + "-ejb")) { //NOI18N
                // this is really just a warning
                String errMsg = NbBundle.getMessage(PanelOptionsVisual.class, "MSG_JARNameNotBlueprints"); // NOI18N
                wizardDescriptor.putProperty( "WizardPanel_errorMessage", errMsg); // NOI18N
            }
        }
        
        String specVer = getSelectedJ2eeSpec();
        if (null == specVer || specVer.equals(J2eeModule.J2EE_13)) {
            String errMsg = NbBundle.getMessage(PanelOptionsVisual.class, "MSG_UnsupportedSpec");  // NOI18N
            wizardDescriptor.putProperty( "WizardPanel_errorMessage", errMsg); // NOI18N
            return false;
            
        }
        wizardDescriptor.putProperty( "WizardPanel_errorMessage", ""); // NOI18N
        return true;
    }
    
    void store(WizardDescriptor d) {
        d.putProperty(WizardProperties.SET_AS_MAIN, setAsMainCheckBox.isSelected() ? Boolean.TRUE : Boolean.FALSE );
        d.putProperty(WizardProperties.SERVER_INSTANCE_ID, getSelectedServer());
        d.putProperty(WizardProperties.J2EE_LEVEL, getSelectedJ2eeSpec());
        //        d.putProperty(WizardProperties.CONTEXT_PATH, jTextFieldContextPath.getText().trim());
        d.putProperty(WizardProperties.CREATE_WAR, createWARCheckBox.isSelected() ? Boolean.TRUE: Boolean.FALSE);
        d.putProperty(WizardProperties.CREATE_JAR, createEjbCheckBox.isSelected() ? Boolean.TRUE: Boolean.FALSE);
        d.putProperty(WizardProperties.WAR_NAME,  jTextFieldWebAppName.getText());
        d.putProperty(WizardProperties.JAR_NAME, jTextFieldEjbModuleName.getText());
        if (warningPanel != null && warningPanel.getDowngradeAllowed()) {
            d.putProperty(WizardProperties.JAVA_PLATFORM, warningPanel.getJava14PlatformName());
            d.putProperty(WizardProperties.SOURCE_LEVEL, "1.4"); // NOI18N
        }
    }
    
    void read(WizardDescriptor d) {
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox createEjbCheckBox;
    private javax.swing.JCheckBox createWARCheckBox;
    private javax.swing.JComboBox j2eeSpecComboBox;
    private javax.swing.JLabel j2eeSpecLabel;
    private javax.swing.JTextField jTextFieldEjbModuleName;
    private javax.swing.JTextField jTextFieldWebAppName;
    private javax.swing.JComboBox serverInstanceComboBox;
    private javax.swing.JLabel serverInstanceLabel;
    private javax.swing.JCheckBox setAsMainCheckBox;
    private javax.swing.JPanel warningPlaceHolderPanel;
    // End of variables declaration//GEN-END:variables
    
    private void initServerInstances() {
        String[] servInstIDs = Deployment.getDefault().getServerInstanceIDs();
        serverInstanceIDs = new ArrayList();
        for (int i = 0; i < servInstIDs.length; i++) {
            J2eePlatform j2eePlat = Deployment.getDefault().getJ2eePlatform(servInstIDs[i]);
            String servInstDisplayName = Deployment.getDefault().getServerInstanceDisplayName(servInstIDs[i]);
            if (servInstDisplayName != null
                    && j2eePlat != null && j2eePlat.getSupportedModuleTypes().contains(J2eeModule.EAR)) {
                serverInstanceIDs.add(servInstIDs[i]);
                serverInstanceComboBox.addItem(servInstDisplayName);
            }
        }
        if (serverInstanceIDs.size() > 0) {
            serverInstanceComboBox.setSelectedIndex(0);
        } else {
            serverInstanceComboBox.setEnabled(false);
            j2eeSpecComboBox.setEnabled(false);
        }
    }
    
    private String getSelectedJ2eeSpec() {
        Object item = j2eeSpecComboBox.getSelectedItem();
        return item == null ? null
                : item.equals(J2EE_SPEC_14_LABEL) ? J2eeModule.J2EE_14 : J2eeModule.J2EE_13;
    }
    
    private String getSelectedServer() {
        int idx = serverInstanceComboBox.getSelectedIndex();
        return idx == -1 ? null
                : (String)serverInstanceIDs.get(idx);
    }
    
    protected boolean isContextModified() {
        return contextModified;
    }
    
    // Implementation of DocumentListener --------------------------------------
    public void changedUpdate(DocumentEvent e) {
        updateTexts(e.getDocument());
    }
    
    public void insertUpdate(DocumentEvent e) {
        updateTexts(e.getDocument());
    }
    
    public void removeUpdate(DocumentEvent e) {
        updateTexts(e.getDocument());
    }
    // End if implementation of DocumentListener -------------------------------
    
    
    /** Handles changes in the project name and sub-module names
     */
    private void updateTexts(Document d) {
        boolean updated = updateWARName(d);
        updated |= updateJARName(d);
        updateProjName(d);
        if (updated)
            panel.fireChangeEvent(); // Notify that the panel changed
    }
    
    private void updateProjName(Document d) {
        if (d.equals(jTextFieldWebAppName.getDocument())) {
            return;
        }
        if (d.equals(jTextFieldEjbModuleName.getDocument())) {
            return;
        }
        try {
            projName = d.getText(0,d.getLength());
        } catch (javax.swing.text.BadLocationException ble) {
            // this had better not happen here
        }
        
    }
    
    private boolean updateWARName(Document d) {
        if (d.equals(jTextFieldWebAppName.getDocument())) {
            return true;
        }
        if (d.equals(jTextFieldEjbModuleName.getDocument())) {
            return false;
        } else {
            // check to see if we need to update the field
            int len = d.getLength();
            try {
                if (len > 0) {
                    jTextFieldWebAppName.setText(d.getText(0,len)+
                            WEB_MOD_SUFFIX);
                } else {
                    jTextFieldWebAppName.setText("");
                }
            } catch (javax.swing.text.BadLocationException ble) {
                // this should not be possible
            }
            return true;
        }
    }
   
    private boolean updateJARName(Document d) {
        if (d.equals(jTextFieldWebAppName.getDocument())) {
            return false;
        }
        if (d.equals(jTextFieldEjbModuleName.getDocument())) {
            return true;
        } else {
            // check to see if we need to update the field
            int len = d.getLength();
            try {
                if (len > 0) {
                    jTextFieldEjbModuleName.setText(d.getText(0,d.getLength())+
                            EJB_MOD_SUFFIX);
                } else {
                    jTextFieldEjbModuleName.setText("");
                }
            } catch (javax.swing.text.BadLocationException ble) {
                // this should not be possible
            }
            return false;
        }
    }
    
    private void setJ2eeVersionWarningPanel() {
        String warningType = J2eeVersionWarningPanel.findWarningType();
        if (warningType == null)
            return;
        
        warningPanel = new J2eeVersionWarningPanel(warningType);
        warningPlaceHolderPanel.add(warningPanel, java.awt.BorderLayout.CENTER);
    }
}

