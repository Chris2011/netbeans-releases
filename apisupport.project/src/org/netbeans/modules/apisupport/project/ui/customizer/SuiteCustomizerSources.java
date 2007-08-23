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

package org.netbeans.modules.apisupport.project.ui.customizer;

import java.util.Arrays;
import javax.swing.event.ListSelectionListener;
import org.netbeans.modules.apisupport.project.NbModuleProject;
import org.netbeans.modules.apisupport.project.ui.UIUtil;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.util.NbBundle;

/**
 * Represents <em>Sources</em> panel in Suite customizer.
 *
 * @author Martin Krauskopf
 */
final class SuiteCustomizerSources extends NbPropertyPanel.Suite {

    /**
     * Creates new form SuiteCustomizerSources
     */
    SuiteCustomizerSources(final SuiteProperties suiteProps, ProjectCustomizer.Category cat) {
        super(suiteProps, SuiteCustomizerSources.class, cat);
        initComponents();
        initAccesibility();
        prjFolderValue.setText(suiteProps.getProjectDirectory());
        refresh();
        moduleList.setCellRenderer(CustomizerComponentFactory.getModuleCellRenderer());
        moduleList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    updateEnabled();
                }
            }
        });
    }
    
    void refresh() {
        moduleList.setModel(getProperties().getModulesListModel());
    }
    
    private void updateEnabled() {
        boolean enabled = moduleList.getSelectedIndex() != -1;
        removeModuleButton.setEnabled(enabled);
    }
    
    private CustomizerComponentFactory.SuiteSubModulesListModel getModuleListModel() {
        return (CustomizerComponentFactory.SuiteSubModulesListModel) moduleList.getModel();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        prjFolderPanel = new javax.swing.JPanel();
        prjFolder = new javax.swing.JLabel();
        prjFolderValue = new javax.swing.JTextField();
        moduleLabel = new javax.swing.JLabel();
        modulesSP = new javax.swing.JScrollPane();
        moduleList = new javax.swing.JList();
        buttonPanel = new javax.swing.JPanel();
        addModuleButton = new javax.swing.JButton();
        removeModuleButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        prjFolderPanel.setLayout(new java.awt.GridBagLayout());

        prjFolder.setLabelFor(prjFolderValue);
        org.openide.awt.Mnemonics.setLocalizedText(prjFolder, org.openide.util.NbBundle.getMessage(SuiteCustomizerSources.class, "LBL_ProjectFolder"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        prjFolderPanel.add(prjFolder, gridBagConstraints);

        prjFolderValue.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        prjFolderPanel.add(prjFolderValue, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(prjFolderPanel, gridBagConstraints);

        moduleLabel.setLabelFor(moduleList);
        org.openide.awt.Mnemonics.setLocalizedText(moduleLabel, org.openide.util.NbBundle.getMessage(SuiteCustomizerSources.class, "LBL_SuiteModules"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 0, 2, 0);
        add(moduleLabel, gridBagConstraints);

        modulesSP.setViewportView(moduleList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(modulesSP, gridBagConstraints);

        buttonPanel.setLayout(new java.awt.GridLayout(2, 1, 0, 6));

        org.openide.awt.Mnemonics.setLocalizedText(addModuleButton, org.openide.util.NbBundle.getMessage(SuiteCustomizerSources.class, "CTL_AddButton"));
        addModuleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addModule(evt);
            }
        });

        buttonPanel.add(addModuleButton);

        org.openide.awt.Mnemonics.setLocalizedText(removeModuleButton, org.openide.util.NbBundle.getMessage(SuiteCustomizerSources.class, "CTL_RemoveButton"));
        removeModuleButton.setEnabled(false);
        removeModuleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeModule(evt);
            }
        });

        buttonPanel.add(removeModuleButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        add(buttonPanel, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents
    
    private void removeModule(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeModule
        getModuleListModel().removeModules(Arrays.asList(moduleList.getSelectedValues()));
        if (moduleList.getModel().getSize() > 0) {
            moduleList.setSelectedIndex(0);
        }
        moduleList.requestFocus();
    }//GEN-LAST:event_removeModule
    
    private void addModule(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addModule
        NbModuleProject project = UIUtil.chooseSuiteComponent(this, getProperties().getProject());
        if (project != null) {
            if (getModuleListModel().contains(project)) {
                moduleList.setSelectedValue(project, true);
            } else {
                getModuleListModel().addModule(project);
            }
        }
    }//GEN-LAST:event_addModule
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addModuleButton;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JLabel moduleLabel;
    private javax.swing.JList moduleList;
    private javax.swing.JScrollPane modulesSP;
    private javax.swing.JLabel prjFolder;
    private javax.swing.JPanel prjFolderPanel;
    private javax.swing.JTextField prjFolderValue;
    private javax.swing.JButton removeModuleButton;
    // End of variables declaration//GEN-END:variables
    
    private static String getMessage(String key) {
        return NbBundle.getMessage(CustomizerDisplay.class, key);
    }
    
    private void initAccesibility() {
        addModuleButton.getAccessibleContext().setAccessibleDescription(getMessage("ACSD_AddModuleButton"));
        moduleList.getAccessibleContext().setAccessibleDescription(getMessage("ACSD_ModuleList"));
        prjFolderValue.getAccessibleContext().setAccessibleDescription(getMessage("ACSD_PrjFolderValue"));
        removeModuleButton.getAccessibleContext().setAccessibleDescription(getMessage("ACSD_RemoveModuleButton"));
    }
    
}
