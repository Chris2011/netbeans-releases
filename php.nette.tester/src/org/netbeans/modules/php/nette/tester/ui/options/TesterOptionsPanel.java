/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.nette.tester.ui.options;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.api.util.UiUtils;
import org.netbeans.modules.php.nette.tester.commands.Tester;
import org.netbeans.modules.php.nette.tester.util.TesterUtils;
import org.openide.awt.HtmlBrowser;
import org.openide.awt.Mnemonics;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

public class TesterOptionsPanel extends JPanel {

    private static final long serialVersionUID = -4168765465465778L;

    private final ChangeSupport changeSupport = new ChangeSupport(this);


    public TesterOptionsPanel() {
        initComponents();
        init();
    }

    @NbBundle.Messages({
        "# {0} - nette tester file name",
        "TesterOptionsPanel.tester.hint=Full path of Nette Tester file (typically {0}).",
    })
    private void init() {
        for (String binaryExecutable : TesterUtils.BINARY_EXECUTABLES) {
            binaryExecutableComboBox.addItem(binaryExecutable);
        }
        errorLabel.setText(" "); // NOI18N
        testerPathHintLabel.setText(Bundle.TesterOptionsPanel_tester_hint(Tester.TESTER_FILE_NAME));

        DocumentListener defaultDocumentListener = new DefaultDocumentListener();
        ActionListener defaultActionListener = new DefaultActionListener();
        testerPathTextField.getDocument().addDocumentListener(defaultDocumentListener);
        phpIniTextField.getDocument().addDocumentListener(defaultDocumentListener);
        binaryExecutableComboBox.addActionListener(defaultActionListener);
    }

    public String getTesterPath() {
        return testerPathTextField.getText();
    }

    public void setTesterPath(String path) {
        testerPathTextField.setText(path);
    }

    public String getPhpIniPath() {
        return phpIniTextField.getText();
    }

    public void setPhpIniPath(String path) {
        phpIniTextField.setText(path);
    }

    @CheckForNull
    public String getBinaryExecutable() {
        return (String) binaryExecutableComboBox.getSelectedItem();
    }

    public void setBinaryExecutable(String binaryExecutable) {
        binaryExecutableComboBox.setSelectedItem(binaryExecutable);
    }

    public void setError(String message) {
        errorLabel.setText(" "); // NOI18N
        errorLabel.setForeground(UIManager.getColor("nb.errorForeground")); // NOI18N
        errorLabel.setText(message);
    }

    public void setWarning(String message) {
        errorLabel.setText(" "); // NOI18N
        errorLabel.setForeground(UIManager.getColor("nb.warningForeground")); // NOI18N
        errorLabel.setText(message);
    }

    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    void fireChange() {
        changeSupport.fireChange();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form
     * Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        testerPathLabel = new javax.swing.JLabel();
        testerPathTextField = new javax.swing.JTextField();
        browseTesterButton = new javax.swing.JButton();
        searchTesterButton = new javax.swing.JButton();
        testerPathHintLabel = new javax.swing.JLabel();
        phpIniLabel = new javax.swing.JLabel();
        phpIniTextField = new javax.swing.JTextField();
        phpIniBrowseButton = new javax.swing.JButton();
        binaryExecutableLabel = new javax.swing.JLabel();
        binaryExecutableComboBox = new javax.swing.JComboBox<String>();
        noteLabel = new javax.swing.JLabel();
        minVersionLabel = new javax.swing.JLabel();
        installLabel = new javax.swing.JLabel();
        learnMoreLabel = new javax.swing.JLabel();
        errorLabel = new javax.swing.JLabel();

        testerPathLabel.setLabelFor(testerPathTextField);
        org.openide.awt.Mnemonics.setLocalizedText(testerPathLabel, org.openide.util.NbBundle.getMessage(TesterOptionsPanel.class, "TesterOptionsPanel.testerPathLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(browseTesterButton, org.openide.util.NbBundle.getMessage(TesterOptionsPanel.class, "TesterOptionsPanel.browseTesterButton.text")); // NOI18N
        browseTesterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseTesterButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(searchTesterButton, org.openide.util.NbBundle.getMessage(TesterOptionsPanel.class, "TesterOptionsPanel.searchTesterButton.text")); // NOI18N
        searchTesterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchTesterButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(testerPathHintLabel, "HINT"); // NOI18N

        phpIniLabel.setLabelFor(phpIniTextField);
        org.openide.awt.Mnemonics.setLocalizedText(phpIniLabel, org.openide.util.NbBundle.getMessage(TesterOptionsPanel.class, "TesterOptionsPanel.phpIniLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(phpIniBrowseButton, org.openide.util.NbBundle.getMessage(TesterOptionsPanel.class, "TesterOptionsPanel.phpIniBrowseButton.text")); // NOI18N
        phpIniBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                phpIniBrowseButtonActionPerformed(evt);
            }
        });

        binaryExecutableLabel.setLabelFor(binaryExecutableComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(binaryExecutableLabel, org.openide.util.NbBundle.getMessage(TesterOptionsPanel.class, "TesterOptionsPanel.binaryExecutableLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(noteLabel, org.openide.util.NbBundle.getMessage(TesterOptionsPanel.class, "TesterOptionsPanel.noteLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(minVersionLabel, org.openide.util.NbBundle.getMessage(TesterOptionsPanel.class, "TesterOptionsPanel.minVersionLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(installLabel, org.openide.util.NbBundle.getMessage(TesterOptionsPanel.class, "TesterOptionsPanel.installLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(learnMoreLabel, org.openide.util.NbBundle.getMessage(TesterOptionsPanel.class, "TesterOptionsPanel.learnMoreLabel.text")); // NOI18N
        learnMoreLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                learnMoreLabelMouseEntered(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                learnMoreLabelMousePressed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(errorLabel, "ERROR"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(testerPathLabel)
                    .addComponent(phpIniLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(testerPathTextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(browseTesterButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchTesterButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(testerPathHintLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(phpIniTextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(phpIniBrowseButton))))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(minVersionLabel)
                    .addComponent(installLabel)
                    .addComponent(learnMoreLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(binaryExecutableLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(binaryExecutableComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(noteLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(errorLabel))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {browseTesterButton, phpIniBrowseButton, searchTesterButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchTesterButton)
                    .addComponent(browseTesterButton)
                    .addComponent(testerPathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(testerPathLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(testerPathHintLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(phpIniLabel)
                    .addComponent(phpIniTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(phpIniBrowseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(binaryExecutableLabel)
                    .addComponent(binaryExecutableComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(noteLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(minVersionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(installLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(learnMoreLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(errorLabel))
        );
    }// </editor-fold>//GEN-END:initComponents

    @NbBundle.Messages("TesterOptionsPanel.tester.browse.title=Select Nette Tester")
    private void browseTesterButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_browseTesterButtonActionPerformed
        File file = new FileChooserBuilder(TesterOptionsPanel.class)
                .setFilesOnly(true)
                .setTitle(Bundle.TesterOptionsPanel_tester_browse_title())
                .showOpenDialog();
        if (file != null) {
            testerPathTextField.setText(file.getAbsolutePath());
        }
    }//GEN-LAST:event_browseTesterButtonActionPerformed

    @NbBundle.Messages({
        "TesterOptionsPanel.tester.search.title=Nette Tester files",
        "TesterOptionsPanel.tester.search.files=&Nette Tester files:",
        "TesterOptionsPanel.tester.search.pleaseWaitPart=Nette Tester files",
        "TesterOptionsPanel.tester.search.notFound=No Nette Tester files found."
    })
    private void searchTesterButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_searchTesterButtonActionPerformed
        String tester = UiUtils.SearchWindow.search(new UiUtils.SearchWindow.SearchWindowSupport() {
            @Override
            public List<String> detect() {
                return FileUtils.findFileOnUsersPath(Tester.TESTER_FILE_NAME);
            }
            @Override
            public String getWindowTitle() {
                return Bundle.TesterOptionsPanel_tester_search_title();
            }
            @Override
            public String getListTitle() {
                return Bundle.TesterOptionsPanel_tester_search_files();
            }
            @Override
            public String getPleaseWaitPart() {
                return Bundle.TesterOptionsPanel_tester_search_pleaseWaitPart();
            }
            @Override
            public String getNoItemsFound() {
                return Bundle.TesterOptionsPanel_tester_search_notFound();
            }
        });
        if (tester != null) {
            testerPathTextField.setText(tester);
        }
    }//GEN-LAST:event_searchTesterButtonActionPerformed

    private void learnMoreLabelMouseEntered(MouseEvent evt) {//GEN-FIRST:event_learnMoreLabelMouseEntered
        evt.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_learnMoreLabelMouseEntered

    private void learnMoreLabelMousePressed(MouseEvent evt) {//GEN-FIRST:event_learnMoreLabelMousePressed
        try {
            URL url = new URL("https://github.com/nette/tester"); // NOI18N
            HtmlBrowser.URLDisplayer.getDefault().showURL(url);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_learnMoreLabelMousePressed

    @NbBundle.Messages({
        "TesterOptionsPanel.php.ini.browse.title=Select file or folder for php.ini",
        "TesterOptionsPanel.php.ini.browse.ok=Select",
    })
    private void phpIniBrowseButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_phpIniBrowseButtonActionPerformed
        File file = new FileChooserBuilder(TesterOptionsPanel.class)
                .setTitle(Bundle.TesterOptionsPanel_php_ini_browse_title())
                .setApproveText(Bundle.TesterOptionsPanel_php_ini_browse_ok())
                .showOpenDialog();
        if (file != null) {
            phpIniTextField.setText(file.getAbsolutePath());
        }
    }//GEN-LAST:event_phpIniBrowseButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> binaryExecutableComboBox;
    private javax.swing.JLabel binaryExecutableLabel;
    private javax.swing.JButton browseTesterButton;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JLabel installLabel;
    private javax.swing.JLabel learnMoreLabel;
    private javax.swing.JLabel minVersionLabel;
    private javax.swing.JLabel noteLabel;
    private javax.swing.JButton phpIniBrowseButton;
    private javax.swing.JLabel phpIniLabel;
    private javax.swing.JTextField phpIniTextField;
    private javax.swing.JButton searchTesterButton;
    private javax.swing.JLabel testerPathHintLabel;
    private javax.swing.JLabel testerPathLabel;
    private javax.swing.JTextField testerPathTextField;
    // End of variables declaration//GEN-END:variables

    //~ Inner classes

    private final class DefaultDocumentListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            processUpdate();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            processUpdate();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            processUpdate();
        }

        private void processUpdate() {
            fireChange();
        }

    }

    private final class DefaultActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            fireChange();
        }

    }

}
