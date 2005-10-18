/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.apisupport.project.ui.platform;

import java.awt.EventQueue;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.ListModel;
import org.netbeans.modules.apisupport.project.universe.ModuleEntry;
import org.netbeans.modules.apisupport.project.universe.NbPlatform;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 * Represents <em>Modules</em> tab in the NetBeans platforms customizer.
 *
 * @author Martin Krauskopf
 */
final class NbPlatformCustomizerModules extends JPanel {
    
    private static final ListModel EMPTY_MODEL = new DefaultListModel();
    private static final DefaultListModel WAIT_MODEL = new DefaultListModel();
    
    static {
        WAIT_MODEL.addElement(NbBundle.getMessage(
                NbPlatformCustomizerModules.class, "LBL_PleaseWait")); // NOI18N
    }
    
    /** Creates new form NbPlatformCustomizerModules */
    NbPlatformCustomizerModules() {
        initComponents();
        initAccessibility();
    }
    
    void setPlatform(final NbPlatform plaf) {
        moduleList.setModel(WAIT_MODEL);
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                final ModuleEntry[] modules = plaf.getModules();
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        moduleList.setModel(new ComponentFactory.ModuleEntryListModel(modules));
                    }
                });
            }
        });
    }
    
    void reset() {
        moduleList.setModel(EMPTY_MODEL);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        moduleLabel = new javax.swing.JLabel();
        moduleSP = new javax.swing.JScrollPane();
        moduleList = new javax.swing.JList();

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(12, 12, 12, 12)));
        moduleLabel.setLabelFor(moduleList);
        org.openide.awt.Mnemonics.setLocalizedText(moduleLabel, org.openide.util.NbBundle.getMessage(NbPlatformCustomizerModules.class, "LBL_PlatformModules"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(moduleLabel, gridBagConstraints);

        moduleList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        moduleSP.setViewportView(moduleList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(moduleSP, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel moduleLabel;
    private javax.swing.JList moduleList;
    private javax.swing.JScrollPane moduleSP;
    // End of variables declaration//GEN-END:variables
    
    private void initAccessibility() {
        moduleList.getAccessibleContext().setAccessibleDescription(getMessage("ACS_CTL_moduleList"));
    }
    
    private String getMessage(String key) {
        return NbBundle.getMessage(NbPlatformCustomizerJavadoc.class, key);
    }
    
}
