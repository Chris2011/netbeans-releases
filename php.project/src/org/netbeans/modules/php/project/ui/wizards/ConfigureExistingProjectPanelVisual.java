/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.php.project.ui.wizards;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.charset.Charset;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.MutableComboBoxModel;
import javax.swing.SwingConstants;
import org.netbeans.modules.php.project.api.PhpLanguageProperties.PhpVersion;
import org.netbeans.modules.php.project.ui.LastUsedFolders;
import org.netbeans.modules.php.project.ui.LocalServer;
import org.netbeans.modules.php.project.ui.Utils;
import org.netbeans.modules.php.project.ui.Utils.PhpVersionComboBoxModel;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

class ConfigureExistingProjectPanelVisual extends ConfigurableProjectPanel {

    private static final long serialVersionUID = 97658795442153213L;

    ConfigureExistingProjectPanelVisual(ConfigureProjectPanel wizardPanel) {
        super(wizardPanel);

        initComponents();
        projectFolderPanel.add(BorderLayout.CENTER, projectFolderComponent);
        init();
    }

    // XXX remove
    @SuppressWarnings("unchecked")
    private void init() {
        sourcesTextField.getDocument().addDocumentListener(this);
        projectNameTextField.getDocument().addDocumentListener(this);

        phpVersionComboBox.setModel(new PhpVersionComboBoxModel(PhpVersion.PHP_53));

        encodingComboBox.setModel(ProjectCustomizer.encodingModel(Charset.defaultCharset().name()));
        encodingComboBox.setRenderer(ProjectCustomizer.encodingRenderer());
    }

    @Override
    public void addNotify() {
        super.addNotify();
        // same problem as in 31086, initial focus on Cancel button
        projectNameTextField.requestFocus();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sourcesLabel = new JLabel();
        sourcesTextField = new JTextField();
        sourcesBrowseButton = new JButton();
        sourcesInfoLabel = new JLabel();
        projectNameLabel = new JLabel();
        projectNameTextField = new JTextField();
        phpVersionLabel = new JLabel();
        phpVersionComboBox = new JComboBox<PhpVersion>();
        phpVersionInfoLabel = new JLabel();
        encodingLabel = new JLabel();
        encodingComboBox = new JComboBox<Charset>();
        separator = new JSeparator();
        projectFolderPanel = new JPanel();

        sourcesLabel.setLabelFor(sourcesTextField);
        Mnemonics.setLocalizedText(sourcesLabel, NbBundle.getMessage(ConfigureExistingProjectPanelVisual.class, "LBL_Sources")); // NOI18N
        sourcesLabel.setVerticalAlignment(SwingConstants.TOP);

        sourcesTextField.setColumns(20);

        Mnemonics.setLocalizedText(sourcesBrowseButton, NbBundle.getMessage(ConfigureExistingProjectPanelVisual.class, "LBL_LocalServerBrowse")); // NOI18N
        sourcesBrowseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                sourcesBrowseButtonActionPerformed(evt);
            }
        });

        Mnemonics.setLocalizedText(sourcesInfoLabel, NbBundle.getMessage(ConfigureExistingProjectPanelVisual.class, "TXT_ExistingSourcesHint")); // NOI18N

        projectNameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        projectNameLabel.setLabelFor(projectNameTextField);
        Mnemonics.setLocalizedText(projectNameLabel, NbBundle.getMessage(ConfigureExistingProjectPanelVisual.class, "LBL_ProjectName")); // NOI18N
        projectNameLabel.setVerticalAlignment(SwingConstants.TOP);

        projectNameTextField.setColumns(20);

        phpVersionLabel.setLabelFor(phpVersionComboBox);
        Mnemonics.setLocalizedText(phpVersionLabel, NbBundle.getMessage(ConfigureExistingProjectPanelVisual.class, "ConfigureExistingProjectPanelVisual.phpVersionLabel.text")); // NOI18N

        Mnemonics.setLocalizedText(phpVersionInfoLabel, NbBundle.getMessage(ConfigureExistingProjectPanelVisual.class, "ConfigureExistingProjectPanelVisual.phpVersionInfoLabel.text")); // NOI18N

        encodingLabel.setLabelFor(encodingComboBox);
        Mnemonics.setLocalizedText(encodingLabel, NbBundle.getMessage(ConfigureExistingProjectPanelVisual.class, "LBL_Encoding")); // NOI18N

        projectFolderPanel.setLayout(new BorderLayout());

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addComponent(separator)
            .addComponent(projectFolderPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(encodingLabel)
                    .addComponent(projectNameLabel)
                    .addComponent(sourcesLabel)
                    .addComponent(phpVersionLabel))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(phpVersionInfoLabel)
                        .addContainerGap())
                    .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(phpVersionComboBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(sourcesInfoLabel)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(sourcesTextField, GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(sourcesBrowseButton))
                        .addComponent(projectNameTextField, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addComponent(encodingComboBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(sourcesLabel)
                    .addComponent(sourcesBrowseButton)
                    .addComponent(sourcesTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(sourcesInfoLabel)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(projectNameLabel)
                    .addComponent(projectNameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(phpVersionComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(phpVersionLabel))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(phpVersionInfoLabel)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(encodingLabel)
                    .addComponent(encodingComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(separator, GroupLayout.PREFERRED_SIZE, 2, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(projectFolderPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        sourcesLabel.getAccessibleContext().setAccessibleName(NbBundle.getMessage(ConfigureExistingProjectPanelVisual.class, "ConfigureExistingProjectPanelVisual.sourcesLabel.AccessibleContext.accessibleName")); // NOI18N
        sourcesLabel.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(ConfigureExistingProjectPanelVisual.class, "ConfigureExistingProjectPanelVisual.sourcesLabel.AccessibleContext.accessibleDescription")); // NOI18N
        sourcesTextField.getAccessibleContext().setAccessibleName(NbBundle.getMessage(ConfigureExistingProjectPanelVisual.class, "ConfigureExistingProjectPanelVisual.sourcesTextField.AccessibleContext.accessibleName_1")); // NOI18N
        sourcesTextField.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(ConfigureExistingProjectPanelVisual.class, "ConfigureExistingProjectPanelVisual.sourcesTextField.AccessibleContext.accessibleDescription")); // NOI18N
        sourcesBrowseButton.getAccessibleContext().setAccessibleName(NbBundle.getMessage(ConfigureExistingProjectPanelVisual.class, "ConfigureExistingProjectPanelVisual.sourcesBrowseButton.AccessibleContext.accessibleName")); // NOI18N
        sourcesBrowseButton.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(ConfigureExistingProjectPanelVisual.class, "ConfigureExistingProjectPanelVisual.sourcesBrowseButton.AccessibleContext.accessibleDescription")); // NOI18N
        sourcesInfoLabel.getAccessibleContext().setAccessibleName(NbBundle.getMessage(ConfigureExistingProjectPanelVisual.class, "ConfigureExistingProjectPanelVisual.sourcesInfoLabel.AccessibleContext.accessibleName")); // NOI18N
        sourcesInfoLabel.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(ConfigureExistingProjectPanelVisual.class, "ConfigureExistingProjectPanelVisual.sourcesInfoLabel.AccessibleContext.accessibleDescription")); // NOI18N
        projectNameLabel.getAccessibleContext().setAccessibleName(NbBundle.getMessage(ConfigureExistingProjectPanelVisual.class, "ConfigureExistingProjectPanelVisual.projectNameLabel.AccessibleContext.accessibleName")); // NOI18N
        projectNameLabel.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(ConfigureExistingProjectPanelVisual.class, "ConfigureExistingProjectPanelVisual.projectNameLabel.AccessibleContext.accessibleDescription")); // NOI18N
        projectNameTextField.getAccessibleContext().setAccessibleName(NbBundle.getMessage(ConfigureExistingProjectPanelVisual.class, "ConfigureExistingProjectPanelVisual.projectNameTextField.AccessibleContext.accessibleName")); // NOI18N
        projectNameTextField.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(ConfigureExistingProjectPanelVisual.class, "ConfigureExistingProjectPanelVisual.projectNameTextField.AccessibleContext.accessibleDescription")); // NOI18N
        phpVersionLabel.getAccessibleContext().setAccessibleName(NbBundle.getMessage(ConfigureExistingProjectPanelVisual.class, "ConfigureExistingProjectPanelVisual.phpVersionLabel.AccessibleContext.accessibleName_1")); // NOI18N
        phpVersionLabel.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(ConfigureExistingProjectPanelVisual.class, "ConfigureExistingProjectPanelVisual.phpVersionLabel.AccessibleContext.accessibleDescription_1")); // NOI18N
        phpVersionComboBox.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(ConfigureExistingProjectPanelVisual.class, "ConfigureExistingProjectPanelVisual.phpVersionComboBox.AccessibleContext.accessibleDescription_1")); // NOI18N
        phpVersionInfoLabel.getAccessibleContext().setAccessibleName(NbBundle.getMessage(ConfigureExistingProjectPanelVisual.class, "ConfigureExistingProjectPanelVisual.phpVersionInfoLabel.AccessibleContext.accessibleName_1")); // NOI18N
        phpVersionInfoLabel.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(ConfigureExistingProjectPanelVisual.class, "ConfigureExistingProjectPanelVisual.phpVersionInfoLabel.AccessibleContext.accessibleDescription_1")); // NOI18N
        encodingLabel.getAccessibleContext().setAccessibleName(NbBundle.getMessage(ConfigureExistingProjectPanelVisual.class, "ConfigureExistingProjectPanelVisual.encodingLabel.AccessibleContext.accessibleName")); // NOI18N
        encodingLabel.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(ConfigureExistingProjectPanelVisual.class, "ConfigureExistingProjectPanelVisual.encodingLabel.AccessibleContext.accessibleDescription")); // NOI18N
        encodingComboBox.getAccessibleContext().setAccessibleName(NbBundle.getMessage(ConfigureExistingProjectPanelVisual.class, "ConfigureExistingProjectPanelVisual.encodingComboBox.AccessibleContext.accessibleName")); // NOI18N
        encodingComboBox.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(ConfigureExistingProjectPanelVisual.class, "ConfigureExistingProjectPanelVisual.encodingComboBox.AccessibleContext.accessibleDescription")); // NOI18N
        separator.getAccessibleContext().setAccessibleName(NbBundle.getMessage(ConfigureExistingProjectPanelVisual.class, "ConfigureExistingProjectPanelVisual.separator.AccessibleContext.accessibleName")); // NOI18N
        separator.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(ConfigureExistingProjectPanelVisual.class, "ConfigureExistingProjectPanelVisual.separator.AccessibleContext.accessibleDescription")); // NOI18N
        projectFolderPanel.getAccessibleContext().setAccessibleName(NbBundle.getMessage(ConfigureExistingProjectPanelVisual.class, "ConfigureExistingProjectPanelVisual.projectFolderPanel.AccessibleContext.accessibleName")); // NOI18N
        projectFolderPanel.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(ConfigureExistingProjectPanelVisual.class, "ConfigureExistingProjectPanelVisual.projectFolderPanel.AccessibleContext.accessibleDescription")); // NOI18N

        getAccessibleContext().setAccessibleName(NbBundle.getMessage(ConfigureExistingProjectPanelVisual.class, "ConfigureExistingProjectPanelVisual.AccessibleContext.accessibleName")); // NOI18N
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(ConfigureExistingProjectPanelVisual.class, "ConfigureExistingProjectPanelVisual.AccessibleContext.accessibleDescription")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void sourcesBrowseButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_sourcesBrowseButtonActionPerformed
        File newLocation = Utils.browseLocationAction(LastUsedFolders.EXISTING_SOURCES,
                NbBundle.getMessage(ProjectFolder.class, "LBL_SelectProjectFolder"));
        if (newLocation != null) {
            sourcesTextField.setText(newLocation.getAbsolutePath());
        }
    }//GEN-LAST:event_sourcesBrowseButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JComboBox<Charset> encodingComboBox;
    private JLabel encodingLabel;
    private JComboBox<PhpVersion> phpVersionComboBox;
    private JLabel phpVersionInfoLabel;
    private JLabel phpVersionLabel;
    private JPanel projectFolderPanel;
    private JLabel projectNameLabel;
    protected JTextField projectNameTextField;
    private JSeparator separator;
    private JButton sourcesBrowseButton;
    private JLabel sourcesInfoLabel;
    private JLabel sourcesLabel;
    private JTextField sourcesTextField;
    // End of variables declaration//GEN-END:variables

    @Override
    public String getProjectName() {
        return projectNameTextField.getText().trim();
    }

    @Override
    public void setProjectName(String projectName) {
        projectNameTextField.setText(projectName);
        projectNameTextField.selectAll();
    }

    @Override
    public String getSourcesFolder() {
        return sourcesTextField.getText();
    }

    // because of compatibility with ConfigureNewProjectPanelVisual
    @Override
    public LocalServer getSourcesLocation() {
        return new LocalServer(sourcesTextField.getText());
    }

    @Override
    public MutableComboBoxModel<LocalServer> getLocalServerModel() {
        return null;
    }

    @Override
    public void setLocalServerModel(MutableComboBoxModel<LocalServer> localServers) {
    }

    @Override
    public void selectSourcesLocation(LocalServer localServer) {
    }

    @Override
    public PhpVersion getPhpVersion() {
        return (PhpVersion) phpVersionComboBox.getSelectedItem();
    }

    @Override
    public void setPhpVersion(PhpVersion phpVersion) {
        phpVersionComboBox.setSelectedItem(phpVersion);
    }

    @Override
    public Charset getEncoding() {
        return (Charset) encodingComboBox.getSelectedItem();
    }

    @Override
    public void setEncoding(Charset encoding) {
        encodingComboBox.setSelectedItem(encoding);
    }

    @Override
    public void setState(boolean enabled) {
        throw new IllegalStateException("Should not be called for existing sources");
    }

    @Override
    public boolean getState() {
        // always enabled
        return true;
    }
}
