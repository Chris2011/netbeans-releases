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
import java.net.URL;
import java.util.Locale;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import org.netbeans.modules.apisupport.project.Util;
import org.netbeans.modules.apisupport.project.universe.NbPlatform;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

/**
 * Represents <em>Javadoc</em> tab in the NetBeans platforms customizer.
 *
 * @author Martin Krauskopf
 */
final class NbPlatformCustomizerJavadoc extends JPanel {
    
    private NbPlatform plaf;
    private ComponentFactory.NbPlatformJavadocRootsModel model;
    
    /** Creates new form NbPlatformCustomizerModules */
    NbPlatformCustomizerJavadoc() {
        initComponents();
        initAccessibility();
        javadocList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    updateEnabled();
                }
            }
        });
        updateEnabled();
    }
    
    private void updateEnabled() {
        // update buttons enability appropriately
        removeButton.setEnabled(javadocList.getSelectedIndex() != -1);
        moveUpButton.setEnabled(javadocList.getSelectionModel().getMinSelectionIndex() > 0);
        moveDownButton.setEnabled(plaf != null &&
                javadocList.getSelectionModel().getMaxSelectionIndex() < plaf.getJavadocRoots().length - 1);
    }
    
    void setPlatform(NbPlatform plaf) {
        this.plaf = plaf;
        this.model = new ComponentFactory.NbPlatformJavadocRootsModel(plaf);
        javadocList.setModel(model);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        javadocLabel = new javax.swing.JLabel();
        javadocSP = new javax.swing.JScrollPane();
        javadocList = new javax.swing.JList();
        buttonPanel = new javax.swing.JPanel();
        addFolderButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        moveUpButton = new javax.swing.JButton();
        moveDownButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(12, 12, 12, 12)));
        javadocLabel.setLabelFor(javadocList);
        org.openide.awt.Mnemonics.setLocalizedText(javadocLabel, org.openide.util.NbBundle.getMessage(NbPlatformCustomizerJavadoc.class, "LBL_PlatformJavadoc"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(javadocLabel, gridBagConstraints);

        javadocList.setCellRenderer(ComponentFactory.URL_RENDERER);
        javadocSP.setViewportView(javadocList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(javadocSP, gridBagConstraints);

        buttonPanel.setLayout(new java.awt.GridBagLayout());

        buttonPanel.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(0, 12, 0, 0)));
        org.openide.awt.Mnemonics.setLocalizedText(addFolderButton, org.openide.util.NbBundle.getMessage(NbPlatformCustomizerJavadoc.class, "CTL_AddZipOrFolder"));
        addFolderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addZipFolder(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        buttonPanel.add(addFolderButton, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(removeButton, org.openide.util.NbBundle.getMessage(NbPlatformCustomizerJavadoc.class, "CTL_Remove"));
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeFolder(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 12, 0);
        buttonPanel.add(removeButton, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(moveUpButton, org.openide.util.NbBundle.getMessage(NbPlatformCustomizerJavadoc.class, "CTL_MoveUp"));
        moveUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveUp(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        buttonPanel.add(moveUpButton, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(moveDownButton, org.openide.util.NbBundle.getMessage(NbPlatformCustomizerJavadoc.class, "CTL_MoveDown"));
        moveDownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveDown(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        buttonPanel.add(moveDownButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        add(buttonPanel, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents
    
    private void moveDown(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveDown
        int[] selIndices = javadocList.getSelectedIndices();
        model.moveJavadocRootsDown(selIndices);
        for (int i = 0; i < selIndices.length; i++) {
            selIndices[i] = ++selIndices[i];
        }
        javadocList.setSelectedIndices(selIndices);
    }//GEN-LAST:event_moveDown
    
    private void moveUp(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveUp
        int[] selIndices = javadocList.getSelectedIndices();
        model.moveJavadocRootsUp(selIndices);
        for (int i = 0; i < selIndices.length; i++) {
            selIndices[i] = --selIndices[i];
        }
        javadocList.setSelectedIndices(selIndices);
    }//GEN-LAST:event_moveUp
    
    private void removeFolder(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeFolder
        Object[] selVals = javadocList.getSelectedValues();
        URL[] selURLs = new URL[selVals.length];
        System.arraycopy(selVals, 0, selURLs, 0, selVals.length);
        model.removeJavadocRoots(selURLs);
    }//GEN-LAST:event_removeFolder
    
    private void addZipFolder(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addZipFolder
        JFileChooser chooser = new JFileChooser();
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setFileFilter(new FileFilter() {
            public boolean accept(File f)  {
                return f.isDirectory() ||
                        f.getName().toLowerCase(Locale.US).endsWith(".jar") || // NOI18N
                        f.getName().toLowerCase(Locale.US).endsWith(".zip"); // NOI18N
            }
            public String getDescription() {
                return NbBundle.getMessage(NbPlatformCustomizerJavadoc.class, "CTL_JavadocTab"); // NOI18N
            }
        });
        int ret = chooser.showOpenDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            URL newUrl = Util.urlForDirOrJar(FileUtil.normalizeFile(chooser.getSelectedFile()));
            model.addJavadocRoot(newUrl);
            javadocList.setSelectedValue(newUrl, true);
        }
    }//GEN-LAST:event_addZipFolder
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addFolderButton;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JLabel javadocLabel;
    private javax.swing.JList javadocList;
    private javax.swing.JScrollPane javadocSP;
    private javax.swing.JButton moveDownButton;
    private javax.swing.JButton moveUpButton;
    private javax.swing.JButton removeButton;
    // End of variables declaration//GEN-END:variables
    
    private void initAccessibility() {
        addFolderButton.getAccessibleContext().setAccessibleDescription(getMessage("ACS_CTL_addFolderButton"));
        javadocList.getAccessibleContext().setAccessibleDescription(getMessage("ACS_CTL_javadocList"));
        moveDownButton.getAccessibleContext().setAccessibleDescription(getMessage("ACS_CTL_moveDownButton"));
        moveUpButton.getAccessibleContext().setAccessibleDescription(getMessage("ACS_CTL_moveUpButton"));
        removeButton.getAccessibleContext().setAccessibleDescription(getMessage("ACS_CTL_removeButton"));
    }
    
    private String getMessage(String key) {
        return NbBundle.getMessage(NbPlatformCustomizerJavadoc.class, key);
    }
}
