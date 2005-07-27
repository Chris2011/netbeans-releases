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

import java.io.File;
import java.io.IOException;
import javax.swing.event.DocumentEvent;
import org.netbeans.modules.apisupport.project.ui.UIUtil;
import org.netbeans.modules.apisupport.project.ui.wizard.BasicVisualPanel;
import org.netbeans.modules.apisupport.project.universe.NbPlatform;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

/**
 * Second panel from <em>Adding New Platform</em> wizard panels. Allows user to
 * add additional info about a selected platform.
 *
 * @author Martin Krauskopf
 */
public class PlatformInfoVisualPanel extends BasicVisualPanel {
    
    private boolean attached;
    
    /** Creates new form BasicInfoVisualPanel */
    public PlatformInfoVisualPanel(WizardDescriptor setting) {
        super(setting);
        initComponents();
        setName(NbPlatformCustomizer.INFO_STEP);
    }
    
    void refreshData() {
        String destDir = (String) getSettings().getProperty(NbPlatformCustomizer.PLAF_DIR_PROPERTY);
        try {
            plafNameValue.setText(NbPlatform.computeDisplayName(new File(destDir)));
        } catch (IOException e) {
            plafNameValue.setText(destDir);
        }
        checkForm();
    }
    
    private void checkForm() {
        String plafName = plafNameValue.getText().trim();
        if (plafName.equals("")) {
            setErrorMessage(NbBundle.getMessage(PlatformInfoVisualPanel.class,
                    "MSG_BlankPlatformName")); // NOI18N
        } else if (!NbPlatform.isLabelValid(plafName)) {
            setErrorMessage(NbBundle.getMessage(PlatformInfoVisualPanel.class,
                    "MSG_NameIsAlreadyUsed")); // NOI18N
        } else {
            setErrorMessage(null);
        }
    }
    
    void storeData() {
        getSettings().putProperty(NbPlatformCustomizer.PLAF_LABEL_PROPERTY,
                plafNameValue.getText().trim());
    }
    
    public void addNotify() {
        super.addNotify();
        if (!attached) {
            plafNameValue.getDocument().addDocumentListener(new UIUtil.DocumentAdapter() {
                public void insertUpdate(DocumentEvent e) {
                    checkForm();
                }
            });
            attached = true;
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        plafName = new javax.swing.JLabel();
        plafNameValue = new javax.swing.JTextField();
        filler = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(plafName, org.openide.util.NbBundle.getMessage(PlatformInfoVisualPanel.class, "LBL_PlatformName_P"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        add(plafName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(plafNameValue, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weighty = 1.0;
        add(filler, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel filler;
    private javax.swing.JLabel plafName;
    private javax.swing.JTextField plafNameValue;
    // End of variables declaration//GEN-END:variables
}
