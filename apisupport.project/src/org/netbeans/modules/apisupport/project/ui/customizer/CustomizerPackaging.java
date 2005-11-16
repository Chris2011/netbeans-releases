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

package org.netbeans.modules.apisupport.project.ui.customizer;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.event.DocumentEvent;
import org.netbeans.modules.apisupport.project.ui.UIUtil;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.openide.util.NbBundle;

/**
 * Represents <em>Packaging</em> panel in Netbeans Module customizer.
 *
 * @author Martin Krauskopf
 */
final class CustomizerPackaging extends NbPropertyPanel.Single {
    
    /** Creates new form CustomizerPackaging */
    CustomizerPackaging(final SingleModuleProperties props) {
        super(props, CustomizerPackaging.class);
        initComponents();
        initAccesibility();
        refresh();
        if (!getProperties().isNetBeansOrg()) {
            licenseValue.getDocument().addDocumentListener(new UIUtil.DocumentAdapter() {
                public void insertUpdate(DocumentEvent e) {
                    checkForm();
                }
            });
        } else {
            license.setEnabled(false);
            licenseValue.setEnabled(false);
            browseLicense.setEnabled(false);
        }
    }
    
    
    protected void checkForm() {
        File currentLicenceF = getCurrentLicenceFile();
        if (currentLicenceF != null && !currentLicenceF.isFile()) {
            setErrorMessage(NbBundle.getMessage(CustomizerPackaging.class, "MSG_LicenceFileDoesNotExist"));
        } else {
            setErrorMessage(null);
        }
    }
    
    void refresh() {
        UIUtil.setText(jarFileValue, getProperties().getJarFile());
        needsRestart.setSelected(getBooleanProperty(SingleModuleProperties.NBM_NEEDS_RESTART));
        isGlobal.setSelected(getBooleanProperty(SingleModuleProperties.NBM_IS_GLOBAL));
        UIUtil.setText(homePageValue, getProperty(SingleModuleProperties.NBM_HOMEPAGE));
        UIUtil.setText(authorValue, getProperty(SingleModuleProperties.NBM_MODULE_AUTHOR));
        if (!getProperties().isNetBeansOrg()) {
            UIUtil.setText(licenseValue, getProperty(SingleModuleProperties.LICENSE_FILE));
        }
    }
    
    public void store() {
        setBooleanProperty(SingleModuleProperties.NBM_NEEDS_RESTART, needsRestart.isSelected());
        setBooleanProperty(SingleModuleProperties.NBM_IS_GLOBAL, isGlobal.isSelected());
        setProperty(SingleModuleProperties.NBM_HOMEPAGE, homePageValue.getText());
        setProperty(SingleModuleProperties.NBM_MODULE_AUTHOR, authorValue.getText());
        if (!getProperties().isNetBeansOrg()) {
            setProperty(SingleModuleProperties.LICENSE_FILE, licenseValue.getText());
        }
    }
    
    private String getCurrentLicence() {
        return licenseValue.getText().trim();
    }
    
    private File getCurrentLicenceFile() {
        File file = null;
        String currentLicence = getCurrentLicence();
        if (!currentLicence.equals("")) {
            file = getProperties().evaluateFile(currentLicence);
        }
        return file;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        license = new javax.swing.JLabel();
        licenseValue = new javax.swing.JTextField();
        homePage = new javax.swing.JLabel();
        homePageValue = new javax.swing.JTextField();
        nbmPkgMetadata = new javax.swing.JLabel();
        author = new javax.swing.JLabel();
        authorValue = new javax.swing.JTextField();
        jarFile = new javax.swing.JLabel();
        jarFileValue = new javax.swing.JTextField();
        sep1 = new javax.swing.JSeparator();
        needsRestart = new javax.swing.JCheckBox();
        isGlobal = new javax.swing.JCheckBox();
        filler = new javax.swing.JLabel();
        browseLicense = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        license.setLabelFor(licenseValue);
        org.openide.awt.Mnemonics.setLocalizedText(license, org.openide.util.NbBundle.getMessage(CustomizerPackaging.class, "LBL_License"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 6, 6);
        add(license, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 6, 0);
        add(licenseValue, gridBagConstraints);

        homePage.setLabelFor(homePageValue);
        org.openide.awt.Mnemonics.setLocalizedText(homePage, org.openide.util.NbBundle.getMessage(CustomizerPackaging.class, "LBL_HomePage"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 6);
        add(homePage, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        add(homePageValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(nbmPkgMetadata, org.openide.util.NbBundle.getMessage(CustomizerPackaging.class, "LBL_NBMPackageMetadata"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 0, 0, 0);
        add(nbmPkgMetadata, gridBagConstraints);

        author.setLabelFor(authorValue);
        org.openide.awt.Mnemonics.setLocalizedText(author, org.openide.util.NbBundle.getMessage(CustomizerPackaging.class, "LBL_Author"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 6);
        add(author, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        add(authorValue, gridBagConstraints);

        jarFile.setLabelFor(jarFileValue);
        org.openide.awt.Mnemonics.setLocalizedText(jarFile, org.openide.util.NbBundle.getMessage(CustomizerPackaging.class, "LBL_JarFile"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        add(jarFile, gridBagConstraints);

        jarFileValue.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jarFileValue, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 6, 0);
        add(sep1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(needsRestart, org.openide.util.NbBundle.getMessage(CustomizerPackaging.class, "CTL_NeedsRestartOnInstall"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(needsRestart, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(isGlobal, org.openide.util.NbBundle.getMessage(CustomizerPackaging.class, "CTL_MustBeInstalledGlobally"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        add(isGlobal, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.weighty = 1.0;
        add(filler, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(browseLicense, org.openide.util.NbBundle.getMessage(CustomizerPackaging.class, "CTL_BrowseButton"));
        browseLicense.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseLicense(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 6, 0);
        add(browseLicense, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents
    
    private void browseLicense(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseLicense
        String startDir;
        File currentLicenceF = getCurrentLicenceFile();
        if (currentLicenceF != null && currentLicenceF.exists() && currentLicenceF.getParent() != null) {
            startDir = currentLicenceF.getParent();
        } else {
            startDir = getProperties().getProjectDirectory();
        }
        JFileChooser chooser = new JFileChooser(startDir);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int ret = chooser.showOpenDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            String relPath = PropertyUtils.relativizeFile(
                    getProperties().getProjectDirectoryFile(), chooser.getSelectedFile());
            licenseValue.setText(relPath);
        }
    }//GEN-LAST:event_browseLicense
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel author;
    private javax.swing.JTextField authorValue;
    private javax.swing.JButton browseLicense;
    private javax.swing.JLabel filler;
    private javax.swing.JLabel homePage;
    private javax.swing.JTextField homePageValue;
    private javax.swing.JCheckBox isGlobal;
    private javax.swing.JLabel jarFile;
    private javax.swing.JTextField jarFileValue;
    private javax.swing.JLabel license;
    private javax.swing.JTextField licenseValue;
    private javax.swing.JLabel nbmPkgMetadata;
    private javax.swing.JCheckBox needsRestart;
    private javax.swing.JSeparator sep1;
    // End of variables declaration//GEN-END:variables
    
    private static String getMessage(String key) {
        return NbBundle.getMessage(CustomizerPackaging.class, key);
    }
    
    private void initAccesibility() {
        browseLicense.getAccessibleContext().setAccessibleDescription(getMessage("ACSD_BrowseLicense"));
        isGlobal.getAccessibleContext().setAccessibleDescription(getMessage("ACSD_IsGlobal"));
        needsRestart.getAccessibleContext().setAccessibleDescription(getMessage("ACSD_NeedsRestart"));
        authorValue.getAccessibleContext().setAccessibleDescription(getMessage("ACSD_AuthorValue"));
        homePageValue.getAccessibleContext().setAccessibleDescription(getMessage("ACSD_HomePageValue"));
        jarFileValue.getAccessibleContext().setAccessibleDescription(getMessage("ACSD_JarFileValue"));
        licenseValue.getAccessibleContext().setAccessibleDescription(getMessage("ACSD_LicenseValue"));
    }
    
}
