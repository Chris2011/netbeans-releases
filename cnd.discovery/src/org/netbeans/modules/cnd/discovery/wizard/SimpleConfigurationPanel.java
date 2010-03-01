/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.cnd.discovery.wizard;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.prefs.Preferences;
import javax.swing.ComboBoxEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.cnd.discovery.wizard.api.DiscoveryDescriptor;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;

/**
 *
 * @author Alexander Simon
 */
public class SimpleConfigurationPanel extends javax.swing.JPanel {
    private SimpleConfigurationWizard wizard;
    
    
    /** Creates new form SimpleConfigurationPanel */
    public SimpleConfigurationPanel(SimpleConfigurationWizard wizard) {
        this.wizard = wizard;
        initComponents();
        configurationComboBox.addItem(new ConfigutationItem("project",getString("CONFIGURATION_LEVEL_project"))); // NOI18N
        configurationComboBox.addItem(new ConfigutationItem("folder",getString("CONFIGURATION_LEVEL_folder"))); // NOI18N
        configurationComboBox.addItem(new ConfigutationItem("file",getString("CONFIGURATION_LEVEL_file"))); // NOI18N
        configurationComboBox.setSelectedIndex(2);
        addListeners();
    }
    
    private void addListeners(){
        librariesTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                update();
            }
        });
        ComboBoxEditor editor = librariesTextField.getEditor();
        Component component = editor.getEditorComponent();
        if (component instanceof JTextField) {
            ((JTextField)component).getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) {
                    update();
                }
                public void removeUpdate(DocumentEvent e) {
                    update();
                }
                public void changedUpdate(DocumentEvent e) {
                    update();
                }
            });
        }
    }
    
    private void update() {
        wizard.stateChanged(null);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        instructionPanel = new javax.swing.JPanel();
        instructionsTextArea = new javax.swing.JTextArea();
        discoveryPanel = new javax.swing.JPanel();
        configurationComboBox = new javax.swing.JComboBox();
        configurationLabel = new javax.swing.JLabel();
        librariesLabel = new javax.swing.JLabel();
        additionalLibrariesButton = new javax.swing.JButton();
        librariesTextField = new javax.swing.JComboBox();

        setLayout(new java.awt.GridBagLayout());

        instructionPanel.setLayout(new java.awt.GridBagLayout());

        instructionsTextArea.setBackground(instructionPanel.getBackground());
        instructionsTextArea.setEditable(false);
        instructionsTextArea.setLineWrap(true);
        instructionsTextArea.setWrapStyleWord(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        instructionPanel.add(instructionsTextArea, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        add(instructionPanel, gridBagConstraints);

        discoveryPanel.setLayout(new java.awt.GridBagLayout());

        configurationComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                configurationComboBoxItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        discoveryPanel.add(configurationComboBox, gridBagConstraints);

        configurationLabel.setLabelFor(configurationComboBox);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/cnd/discovery/wizard/Bundle"); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(configurationLabel, bundle.getString("ConfigurationLevelLabel")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        discoveryPanel.add(configurationLabel, gridBagConstraints);

        librariesLabel.setLabelFor(librariesTextField);
        org.openide.awt.Mnemonics.setLocalizedText(librariesLabel, bundle.getString("AdditionalLibrariesLabel")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        discoveryPanel.add(librariesLabel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(additionalLibrariesButton, bundle.getString("LIBRARY_BROWSE_BUTTON_TXT")); // NOI18N
        additionalLibrariesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                additionalLibrariesButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        discoveryPanel.add(additionalLibrariesButton, gridBagConstraints);

        librariesTextField.setEditable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        discoveryPanel.add(librariesTextField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(discoveryPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    
    private void configurationComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_configurationComboBoxItemStateChanged
        Object item = evt.getItem();
        if (item instanceof ConfigutationItem) {
            ConfigutationItem conf = (ConfigutationItem)item;
            instructionsTextArea.setText(getString("SimpleInstructionText_"+conf.getID())); // NOI18N
        }
    }//GEN-LAST:event_configurationComboBoxItemStateChanged
    
    private void additionalLibrariesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_additionalLibrariesButtonActionPerformed
        StringTokenizer tokenizer = new StringTokenizer(getLibraryText(), ";"); // NOI18N
        List<String> list = new ArrayList<String>();
        while (tokenizer.hasMoreTokens()) {
            list.add(tokenizer.nextToken());
        }
        AdditionalLibrariesListPanel panel = new AdditionalLibrariesListPanel(list);
        DialogDescriptor dialogDescriptor = new DialogDescriptor(AdditionalLibrariesListPanel.wrapPanel(panel),
                getString("ADDITIONAL_LIBRARIES_TXT")); // NOI18N
        DialogDisplayer.getDefault().notify(dialogDescriptor);
        if (dialogDescriptor.getValue() == DialogDescriptor.OK_OPTION) {
            List<String> newList = panel.getListData();
            StringBuilder includes = new StringBuilder();
            for (int i = 0; i < newList.size(); i++) {
                if (i > 0) {
                    includes.append(';'); // NOI18N
                }
                includes.append(newList.get(i));
            }
            librariesTextField.setSelectedItem(includes.toString());
        }
    }//GEN-LAST:event_additionalLibrariesButtonActionPerformed
    
    private String getString(String key) {
        return NbBundle.getBundle(SimpleConfigurationPanel.class).getString(key);
    }
    
    void read(final DiscoveryDescriptor wizardDescriptor) {
        String providerID = wizardDescriptor.getProviderID();
        if ("dwarf-executable".equals(providerID)){ // NOI18N
            additionalLibrariesButton.setVisible(true);
            librariesLabel.setVisible(true);
            librariesTextField.setVisible(true);
        } else if ("make-log".equals(providerID)){ // NOI18N
            additionalLibrariesButton.setVisible(false);
            librariesLabel.setVisible(false);
            librariesTextField.setVisible(false);
        } else if ("dwarf-folder".equals(providerID)){ // NOI18N
            additionalLibrariesButton.setVisible(false);
            librariesLabel.setVisible(false);
            librariesTextField.setVisible(false);
        }
        if (librariesTextField.isVisible()) {
            List<String> vector = new ArrayList<String>();
            vector.add(""); // NOI18N
            Preferences prefs = NbPreferences.forModule(SimpleConfigurationPanel.class);
            String old = prefs.get("libraries", ""); // NOI18N
            StringTokenizer st = new StringTokenizer(old, "\u0000"); // NOI18N
            int history = 5;
            while(st.hasMoreTokens()) {
                String s = st.nextToken();
                if (!vector.contains(s)) {
                    vector.add(s);
                    history--;
                    if (history == 0) {
                        break;
                    }
                }
            }
            DefaultComboBoxModel rootModel = new DefaultComboBoxModel(vector.toArray());
            librariesTextField.setModel(rootModel);
            StringBuilder buf = new StringBuilder();
            for(int i = 0; i < 35; i++) {
                buf.append("w"); // NOI18N
            }
            librariesTextField.setPrototypeDisplayValue(buf.toString());
        }
    }

    private String getLibraryText() {
        ComboBoxEditor editor = librariesTextField.getEditor();
        if (editor != null) {
            Component component = editor.getEditorComponent();
            if (component instanceof JTextField) {
                return ((JTextField)component).getText();
            }
        }
        if (librariesTextField.getSelectedItem() != null) {
            return librariesTextField.getSelectedItem().toString();
        }
        return "";
    }

    void store(DiscoveryDescriptor wizardDescriptor) {
        ConfigutationItem level = (ConfigutationItem)configurationComboBox.getSelectedItem();
        wizardDescriptor.setLevel(level.getID());
        wizardDescriptor.setAditionalLibraries(getLibraryText());
        {
            List<String> vector = new ArrayList<String>();
            vector.add(getLibraryText());
            for(int i = 0; i < librariesTextField.getModel().getSize(); i++){
                String s = librariesTextField.getModel().getElementAt(i).toString();
                if (!vector.contains(s)) {
                    vector.add(s);
                }
            }
            StringBuilder buf = new StringBuilder();
            for(String s : vector) {
                if (buf.length()>0) {
                    buf.append((char)0);
                }
                buf.append(s);
            }
            Preferences prefs = NbPreferences.forModule(SimpleConfigurationPanel.class);
            prefs.put("libraries", buf.toString()); // NOI18N
        }
    }
    
    boolean valid() {
        StringTokenizer st = new StringTokenizer(getLibraryText(), ";"); // NOI18N
        while(st.hasMoreTokens()){
            String path = st.nextToken();
            File file = new File(path);
            if (!(file.exists() && file.isFile())){
                return false;
            }
        }
        return true;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton additionalLibrariesButton;
    private javax.swing.JComboBox configurationComboBox;
    private javax.swing.JLabel configurationLabel;
    private javax.swing.JPanel discoveryPanel;
    private javax.swing.JPanel instructionPanel;
    private javax.swing.JTextArea instructionsTextArea;
    private javax.swing.JLabel librariesLabel;
    private javax.swing.JComboBox librariesTextField;
    // End of variables declaration//GEN-END:variables
    
    private static class ConfigutationItem {
        private String ID;
        private String name;
        private ConfigutationItem(String ID, String name){
            this.ID = ID;
            this.name = name;
        }
        @Override
        public String toString(){
            return name;
        }
        public String getID(){
            return ID;
        }
    }
    
}
