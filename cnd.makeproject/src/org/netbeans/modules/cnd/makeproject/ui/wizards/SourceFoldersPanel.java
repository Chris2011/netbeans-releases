/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.

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

package org.netbeans.modules.cnd.makeproject.ui.wizards;

import java.awt.Dimension;
import java.io.File;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import org.netbeans.modules.cnd.api.utils.FileChooser;
import org.netbeans.modules.cnd.makeproject.api.remote.FilePathAdaptor;
import org.netbeans.modules.cnd.makeproject.ui.utils.ListEditorPanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

public class SourceFoldersPanel extends javax.swing.JPanel implements HelpCtx.Provider{
    private SourceFoldersDescriptorPanel sourceFoldersDescriptorPanel;
    private SourceFilesPanel sourceFilesPanel;
    private boolean firstTime = true;

    public SourceFoldersPanel(SourceFoldersDescriptorPanel sourceFoldersDescriptorPanel) {
        initComponents();
	this.sourceFoldersDescriptorPanel = sourceFoldersDescriptorPanel;
	sourceFilesPanel = new SourceFilesPanel();
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        headerFoldersOuterPanel.add(sourceFilesPanel, gridBagConstraints);
        instructionsTextArea.setBackground(instructionPanel.getBackground());
    }

    public HelpCtx getHelpCtx() {
        return new HelpCtx(SourceFoldersPanel.class);
    }

    void read(WizardDescriptor settings) {
        if (firstTime) {
            String workingdir = (String)settings.getProperty("buildCommandWorkingDirTextField"); // NOI18N
            //sourceFilesPanel.setSeed(workingdir, workingdir);
            File wd = new File(workingdir);
            sourceFilesPanel.getListData().add(new FolderEntry(wd, wd.getPath()));
            firstTime = false;
    }
    }

    void store(WizardDescriptor wizardDescriptor) {
	wizardDescriptor.putProperty("sourceFolders", sourceFilesPanel.getListData().iterator()); // NOI18N
    }
    
    boolean valid(WizardDescriptor settings) {
	return true;
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        headerFoldersOuterPanel = new javax.swing.JPanel();
        instructionPanel = new javax.swing.JPanel();
        instructionsTextArea = new javax.swing.JTextArea();

        setLayout(new java.awt.GridBagLayout());

        setPreferredSize(new java.awt.Dimension(323, 223));
        headerFoldersOuterPanel.setLayout(new java.awt.GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(headerFoldersOuterPanel, gridBagConstraints);

        instructionPanel.setLayout(new java.awt.GridBagLayout());

        instructionsTextArea.setEditable(false);
        instructionsTextArea.setLineWrap(true);
        instructionsTextArea.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/cnd/makeproject/ui/wizards/Bundle").getString("SourceFilesInstructions"));
        instructionsTextArea.setWrapStyleWord(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        instructionPanel.add(instructionsTextArea, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(24, 0, 0, 0);
        add(instructionPanel, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents
            
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel headerFoldersOuterPanel;
    private javax.swing.JPanel instructionPanel;
    private javax.swing.JTextArea instructionsTextArea;
    // End of variables declaration//GEN-END:variables
    
    private static String getString(String s) {
        return NbBundle.getBundle(PanelProjectLocationVisual.class).getString(s);
    }
}
