/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.web.clientproject.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import org.netbeans.modules.web.clientproject.ClientSideConfigurationProvider;
import org.netbeans.modules.web.clientproject.ClientSideProject;
import org.netbeans.modules.web.clientproject.spi.platform.ClientProjectConfigurationImplementation;
import org.netbeans.modules.web.clientproject.spi.platform.ProjectConfigurationCustomizer;
import org.netbeans.spi.project.ProjectConfiguration;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author Jan Becicka
 */
public class ClientSideProjectPanel extends javax.swing.JPanel {
    
    private ClientSideProject p;

    private void updateCustomizerPanel(final ClientSideConfigurationProvider configProvider) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                customizerArea.removeAll();
                final ClientProjectConfigurationImplementation activeConfiguration = configProvider.getActiveConfiguration();
                if (activeConfiguration != null) {
                    ProjectConfigurationCustomizer customizerPanel = activeConfiguration.getProjectConfigurationCustomizer();
                    if (customizerPanel != null) {
                        customizerArea.add(customizerPanel.createPanel(), BorderLayout.CENTER);
                    }
                }
                customizerArea.validate();
                customizerArea.repaint();
            }
        });
    }

    private void updateDeleteButton() {
        ClientProjectConfigurationImplementation cfg = ((ClientProjectConfigurationImplementation) configCombo.getSelectedItem());
        deleteButton.setEnabled(cfg.canBeDeleted());
    }

    private static class ConfigRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof ProjectConfiguration) {
                setText(((ProjectConfiguration) value).getDisplayName());
            }
            return this;
        }
        
    }

    /**
     * Creates new form ClientSideProjectPanel
     */
    public ClientSideProjectPanel(ClientSideProject p) {
        this.p = p;
        initComponents();
        final ClientSideConfigurationProvider configProvider = p.getProjectConfigurations();
        configCombo.setModel((ComboBoxModel) configProvider);
        configCombo.setRenderer(new ConfigRenderer());
        configProvider.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                updateCustomizerPanel(configProvider);
            }
        });
        updateCustomizerPanel(configProvider);
        newButton.setEnabled(p.getProjectConfigurations().getNewConfigurationTypes().length > 0);
        updateDeleteButton();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        configLabel = new javax.swing.JLabel();
        configCombo = new javax.swing.JComboBox();
        customizerArea = new javax.swing.JPanel();
        newButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        seperator = new javax.swing.JSeparator();

        configLabel.setLabelFor(configCombo);
        org.openide.awt.Mnemonics.setLocalizedText(configLabel, org.openide.util.NbBundle.getMessage(ClientSideProjectPanel.class, "ClientSideProjectPanel.configLabel.text")); // NOI18N

        configCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                configComboItemStateChanged(evt);
            }
        });

        customizerArea.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(newButton, org.openide.util.NbBundle.getMessage(ClientSideProjectPanel.class, "ClientSideProjectPanel.newButton.text")); // NOI18N
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(deleteButton, org.openide.util.NbBundle.getMessage(ClientSideProjectPanel.class, "ClientSideProjectPanel.deleteButton.text")); // NOI18N
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(customizerArea, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(configLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(configCombo, 0, 176, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(newButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deleteButton))
            .addComponent(seperator)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(configLabel)
                    .addComponent(configCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(newButton)
                    .addComponent(deleteButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(seperator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(customizerArea, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
        NewConfigurationPanel newPanel = new NewConfigurationPanel("Configuration", p.getProjectConfigurations().getNewConfigurationTypes());
        DialogDescriptor dd = new DialogDescriptor(newPanel, "New Configuration");
        Object result = DialogDisplayer.getDefault().notify(dd);
        if (DialogDescriptor.OK_OPTION == result) {
            String configId = p.getProjectConfigurations().createNewConfiguration(newPanel.getType(), newPanel.getNewName());
            for (ClientProjectConfigurationImplementation config : p.getProjectConfigurations().getConfigurations()) {
                if (configId.equals(config.getId())) {
                        configCombo.setSelectedItem(config);
                        break;
                    }
                }
        }
    }//GEN-LAST:event_newButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        final String dname = ((ClientProjectConfigurationImplementation) configCombo.getSelectedItem()).getDisplayName();
            NotifyDescriptor yesNo = new NotifyDescriptor("Are You Sure You Want to Delete " + dname,
                    "Confirm Object Deletion",
                    NotifyDescriptor.YES_NO_OPTION,
                    NotifyDescriptor.QUESTION_MESSAGE,
                    null,
                    null);

            if (DialogDisplayer.getDefault().notify(yesNo) == NotifyDescriptor.YES_OPTION) {
                int i = configCombo.getSelectedIndex();
            ClientProjectConfigurationImplementation cfg = ((ClientProjectConfigurationImplementation) configCombo.getSelectedItem());
            cfg.delete();
        }
    
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void configComboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_configComboItemStateChanged
        updateDeleteButton();
    }//GEN-LAST:event_configComboItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox configCombo;
    private javax.swing.JLabel configLabel;
    private javax.swing.JPanel customizerArea;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton newButton;
    private javax.swing.JSeparator seperator;
    // End of variables declaration//GEN-END:variables
}
