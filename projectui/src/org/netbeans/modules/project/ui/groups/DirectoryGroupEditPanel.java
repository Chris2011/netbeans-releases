/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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

package org.netbeans.modules.project.ui.groups;

import java.io.File;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import static org.netbeans.modules.project.ui.groups.Group.KEY_PATH;
import static org.netbeans.modules.project.ui.groups.Group.NODE;
import static org.netbeans.modules.project.ui.groups.GroupEditPanel.PROP_READY;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.RequestProcessor;

/**
 * Panel to configure state of an existing directory-based group.
 * @author Jesse Glick
 */
public class DirectoryGroupEditPanel extends GroupEditPanel {

    private final DirectoryGroup g;

    public DirectoryGroupEditPanel(DirectoryGroup g) {
        this.g = g;
        initComponents();
        DocumentListener l = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) {
                firePropertyChange(PROP_READY, null, null);
            }
            @Override public void removeUpdate(DocumentEvent e) {
                firePropertyChange(PROP_READY, null, null);
            }
            @Override public void changedUpdate(DocumentEvent e) {}
        };
        nameField.setText(g.getName());
        nameField.getDocument().addDocumentListener(l);
        FileObject dir = g.getDirectory();
        if (dir != null) {
            File d = FileUtil.toFile(dir);
            if (d != null) {
                folderField.setText(d.getAbsolutePath());
            }
        }
        folderField.getDocument().addDocumentListener(l);
    }

    @Override
    public void applyChanges() {
        g.setName(nameField.getText().trim());
        updateDirectory();
    }

    private void updateDirectory() {
        String s = folderField.getText();
        if (s != null && s.length() > 0) {
            FileObject dir = FileUtil.toFileObject(FileUtil.normalizeFile(new File(s)));
            String path = dir.toURL().toExternalForm();
            Preferences pref = NODE.node(g.id);
            pref.put(KEY_PATH, path);
            if(Group.getActiveGroup().equals(g)) {
                RequestProcessor.getDefault().post(new Runnable() {
                    @Override
                    public void run() {
                        Group.open(g, null, false, null);
                    }
                });
            }
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nameLabel = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        folderLabel = new javax.swing.JLabel();
        folderField = new javax.swing.JTextField();
        directoryButton = new javax.swing.JButton();

        nameLabel.setLabelFor(nameField);
        org.openide.awt.Mnemonics.setLocalizedText(nameLabel, org.openide.util.NbBundle.getMessage(DirectoryGroupEditPanel.class, "DirectoryGroupEditPanel.nameLabel.text")); // NOI18N

        folderLabel.setLabelFor(folderField);
        org.openide.awt.Mnemonics.setLocalizedText(folderLabel, org.openide.util.NbBundle.getMessage(DirectoryGroupEditPanel.class, "DirectoryGroupEditPanel.folderLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(directoryButton, org.openide.util.NbBundle.getMessage(DirectoryGroupEditPanel.class, "DirectoryGroupEditPanel.directoryButton.text_2")); // NOI18N
        directoryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                directoryButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nameLabel)
                    .addComponent(folderLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nameField, javax.swing.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(folderField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(directoryButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(folderLabel)
                    .addComponent(folderField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(directoryButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        nameLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(DirectoryGroupEditPanel.class, "DirectoryGroupEditPanel.nameLabel.AccessibleContext.accessibleDescription")); // NOI18N
        nameField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(DirectoryGroupEditPanel.class, "DirectoryGroupEditPanel.nameField.AccessibleContext.accessibleName")); // NOI18N
        nameField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(DirectoryGroupEditPanel.class, "DirectoryGroupEditPanel.nameField.AccessibleContext.accessibleDescription")); // NOI18N
        folderLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(DirectoryGroupEditPanel.class, "DirectoryGroupEditPanel.folderLabel.AccessibleContext.accessibleDescription")); // NOI18N
        folderField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(DirectoryGroupEditPanel.class, "DirectoryGroupEditPanel.folderField.AccessibleContext.accessibleName")); // NOI18N
        folderField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(DirectoryGroupEditPanel.class, "DirectoryGroupEditPanel.folderField.AccessibleContext.accessibleDescription")); // NOI18N

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(DirectoryGroupEditPanel.class, "DirectoryGroupEditPanel.AccessibleContext.accessibleName")); // NOI18N
        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(DirectoryGroupEditPanel.class, "DirectoryGroupEditPanel.AccessibleContext.accessibleDescription")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void directoryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_directoryButtonActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        File start = ProjectChooser.getProjectsFolder();
        if (folderField.getText() != null && folderField.getText().trim().length() > 0) {
            start = new File(folderField.getText().trim());
        }
        chooser.setCurrentDirectory(start);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            if (f != null) {
                folderField.setText(f.getAbsolutePath());
            }
        }
    }//GEN-LAST:event_directoryButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton directoryButton;
    private javax.swing.JTextField folderField;
    private javax.swing.JLabel folderLabel;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameLabel;
    // End of variables declaration//GEN-END:variables

    @Override
    public boolean isReady() {
        if(!doCheckExistingGroups(nameField, g)) {
            return false;
        }
        String s = folderField.getText();
        if (s != null) {
            return new File(s.trim()).isDirectory();
        } else {
            return false;
        }
    }

}
