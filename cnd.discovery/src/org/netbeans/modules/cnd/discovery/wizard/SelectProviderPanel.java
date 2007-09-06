/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.cnd.discovery.wizard;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.api.project.Project;
import org.netbeans.modules.cnd.api.model.CsmFile;
import org.netbeans.modules.cnd.api.model.CsmModelAccessor;
import org.netbeans.modules.cnd.api.model.CsmProgressListener;
import org.netbeans.modules.cnd.api.model.CsmProject;
import org.netbeans.modules.cnd.discovery.api.DiscoveryProvider;
import org.netbeans.modules.cnd.discovery.api.ProjectProxy;
import org.netbeans.modules.cnd.discovery.wizard.api.DiscoveryDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author Alexander Simon
 */
public final class SelectProviderPanel extends JPanel implements CsmProgressListener {
    private static boolean SHOW_RESTRICT = Boolean.getBoolean("cnd.discovery.wizard.restrictSources"); // NOI18N
    private SelectProviderWizard wizard;
    /** Creates new form SelectProviderVisualPanel1 */
    public SelectProviderPanel(SelectProviderWizard wizard) {
        this.wizard = wizard;
        initComponents();
        if (!SHOW_RESTRICT){
            restrictSources.setVisible(false);
        }
        addListeners();
    }
    
    private void addListeners(){
        DocumentListener documentListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                update(e);
            }
            
            public void removeUpdate(DocumentEvent e) {
                update(e);
            }
            
            public void changedUpdate(DocumentEvent e) {
                update(e);
            }
        };
        rootFolder.getDocument().addDocumentListener(documentListener);
        CsmModelAccessor.getModel().addProgressListener(this);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        rootFolder = new javax.swing.JTextField();
        rootFolderButton = new javax.swing.JButton();
        instructionPanel = new javax.swing.JPanel();
        instructionsTextArea = new javax.swing.JTextArea();
        labelForRoot = new javax.swing.JLabel();
        prividersComboBox = new javax.swing.JComboBox();
        labelForProviders = new javax.swing.JLabel();
        restrictSources = new javax.swing.JCheckBox();

        setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        add(rootFolder, gridBagConstraints);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/cnd/discovery/wizard/Bundle"); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(rootFolderButton, bundle.getString("ROOT_DIR_BROWSE_BUTTON_TXT")); // NOI18N
        rootFolderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rootFolderButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        add(rootFolderButton, gridBagConstraints);

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
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        add(instructionPanel, gridBagConstraints);

        labelForRoot.setLabelFor(rootFolder);
        org.openide.awt.Mnemonics.setLocalizedText(labelForRoot, bundle.getString("ProjectRootFolder")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(labelForRoot, gridBagConstraints);

        prividersComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                prividersComboBoxItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        add(prividersComboBox, gridBagConstraints);

        labelForProviders.setLabelFor(prividersComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(labelForProviders, bundle.getString("SelectDiscoveryProviderText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        add(labelForProviders, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(restrictSources, org.openide.util.NbBundle.getMessage(SelectProviderPanel.class, "RestrictSourcesText")); // NOI18N
        restrictSources.setBorder(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        add(restrictSources, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    
    private void prividersComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_prividersComboBoxItemStateChanged
        Object item = evt.getItem();
        if (item instanceof ProviderItem) {
            ProviderItem provider = (ProviderItem)item;
            instructionsTextArea.setText(provider.getDescription());
            wizard.stateChanged(null);
        }
    }//GEN-LAST:event_prividersComboBoxItemStateChanged
    
    private void update(DocumentEvent e) {
        wizard.stateChanged(null);
    }
    
    private void rootFolderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rootFolderButtonActionPerformed
        String seed = null;
        if (rootFolder.getText().length() > 0) {
            seed = rootFolder.getText();
        } else if (FileChooser.getCurrectChooserFile() != null) {
            seed = FileChooser.getCurrectChooserFile().getPath();
        } else {
            seed = System.getProperty("user.home"); // NOI18N
        }
        
        JFileChooser fileChooser = new FileChooser(
                getString("ROOT_DIR_CHOOSER_TITLE_TXT"), // NOI18N
                getString("ROOT_DIR_BUTTON_TXT"), // NOI18N
                JFileChooser.DIRECTORIES_ONLY, false,
                null,
                seed,
                false
                );
        int ret = fileChooser.showOpenDialog(this);
        if (ret == JFileChooser.CANCEL_OPTION)
            return;
        String path = fileChooser.getSelectedFile().getPath();
        //path = FilePathAdaptor.normalize(path);
        rootFolder.setText(path);
    }//GEN-LAST:event_rootFolderButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel instructionPanel;
    private javax.swing.JTextArea instructionsTextArea;
    private javax.swing.JLabel labelForProviders;
    private javax.swing.JLabel labelForRoot;
    private javax.swing.JComboBox prividersComboBox;
    private javax.swing.JCheckBox restrictSources;
    private javax.swing.JTextField rootFolder;
    private javax.swing.JButton rootFolderButton;
    // End of variables declaration//GEN-END:variables
    
    void read(final DiscoveryDescriptor wizardDescriptor) {
        Lookup.Result<DiscoveryProvider> providers = Lookup.getDefault().lookup(new Lookup.Template<DiscoveryProvider>(DiscoveryProvider.class));
        DefaultComboBoxModel model = (DefaultComboBoxModel)prividersComboBox.getModel();
        model.removeAllElements();
        ProjectProxy proxy = new ProjectProxy() {
            public boolean createSubProjects() {
                return false;
            }
            public Object getProject() {
                return wizardDescriptor.getProject();
            }
        };
        List<ProviderItem> list = new ArrayList<ProviderItem>();
        for(DiscoveryProvider provider : providers.allInstances()){
            provider.clean();
            if (provider.isApplicable(proxy)) {
                list.add(new ProviderItem(provider));
            }
        }
        Collections.<ProviderItem>sort(list);
        for(ProviderItem item:list){
            model.addElement(item);
        }
        ProviderItem def = getDefaultProvider(list,proxy,wizardDescriptor);
        if (def != null){
            prividersComboBox.setSelectedItem(def);
        }
        String path = wizardDescriptor.getRootFolder();
        if (Utilities.isWindows()) {
            path = path.replace('/', File.separatorChar);
        }
        rootFolder.setText(path);
        restrictSources.setSelected(wizardDescriptor.isCutResult());
    }
    
    private ProviderItem getDefaultProvider(List<ProviderItem> list, ProjectProxy proxy, DiscoveryDescriptor wizardDescriptor){
        ProviderItem def = null;
        for(ProviderItem item:list){
            if ("model-folder".equals(item.getID())){ // NOI18N
                // select model if no other variants
                def = item;
            } else if ("dwarf-executable".equals(item.getID())){ // NOI18N
                // select executable if make project has output
                // and output has debug information.
                item.getProvider().getProperty("executable").setValue(wizardDescriptor.getBuildResult()); // NOI18N
                if (item.getProvider().canAnalyze(proxy)) {
                    return item;
                }
            } else if ("dwarf-folder".equals(item.getID())){ // NOI18N
                // select executable if make project has output
                // and output has debug information.
                item.getProvider().getProperty("folder").setValue(wizardDescriptor.getRootFolder()); // NOI18N
                if (item.getProvider().canAnalyze(proxy)) {
                    return item;
                }
            }
        }
        return def;
    }
    
    void store(DiscoveryDescriptor wizardDescriptor) {
        ProviderItem provider = (ProviderItem)prividersComboBox.getSelectedItem();
        wizardDescriptor.setProvider(provider.getProvider());
        wizardDescriptor.setRootFolder(rootFolder.getText());
        wizardDescriptor.setCutResult(restrictSources.isSelected());
    }
    
    boolean valid(DiscoveryDescriptor wizardDescriptor) {
        String path = rootFolder.getText();
        File file = new File(path);
        if (!(file.exists() && file.isDirectory())) {
            return false;
        }
        ProviderItem provider = (ProviderItem)prividersComboBox.getSelectedItem();
        if ("model-folder".equals(provider.getID())){ // NOI18N
            Project project = wizardDescriptor.getProject();
            if (project != null){
                CsmProject langProject = CsmModelAccessor.getModel().getProject(project);
                if (langProject != null && langProject.isStable(null)){
                    return true;
                }
            }
      	    wizardDescriptor.setMessage(getString("ModelNotFinishParsing")); // NOI18N
            return false;
        }
        return true;
    }
    
    private String getString(String key) {
        return NbBundle.getBundle(SelectProviderPanel.class).getString(key);
    }

    public void projectParsingStarted(CsmProject project) {
    }

    public void projectFilesCounted(CsmProject project, int filesCount) {
    }

    public void projectParsingFinished(CsmProject project) {
        wizard.stateChanged(null);
    }
    
    public void projectLoaded(CsmProject project) {
        wizard.stateChanged(null);
    }
    

    public void projectParsingCancelled(CsmProject project) {
    }

    public void fileInvalidated(CsmFile file) {
    }

    public void fileParsingStarted(CsmFile file) {
    }

    public void fileParsingFinished(CsmFile file) {
    }

    public void parserIdle() {
    }
    
    private static class ProviderItem implements Comparable<ProviderItem> {
        private DiscoveryProvider provider;
        private ProviderItem(DiscoveryProvider provider){
            this.provider = provider;
        }
        @Override
        public String toString(){
            return provider.getName();
        }
        public String getID(){
            return provider.getID();
        }
        public String getDescription(){
            return provider.getDescription();
        }
        public DiscoveryProvider getProvider(){
            return provider;
        }
        
        public int compareTo(ProviderItem o) {
            return toString().compareTo( o.toString() );
        }
    }
}
