/*
 * NewPanel.java
 *
 * Created on January 24, 2007, 5:25 PM
 */

package org.netbeans.modules.cnd.discovery.wizard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import org.netbeans.modules.cnd.discovery.wizard.api.DiscoveryDescriptor;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author  as204739
 */
public class ConsolidationStrategyPanel extends JPanel {
    private ConsolidationStrategyWizard wizard;
    public static final String PROJECT_LEVEL = "project"; // NOI18N
    public static final String FOLDER_LEVEL = "folder"; // NOI18N
    public static final String FILE_LEVEL = "file"; // NOI18N
    private String level = FILE_LEVEL;
    
    public ConsolidationStrategyPanel(ConsolidationStrategyWizard wizard) {
        this.wizard = wizard;
        initComponents();
        addListeners();
        fileConsolidation.setSelected(true);
        update(FILE_LEVEL);
    }

    private void addListeners(){
        fileConsolidation.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                update(FILE_LEVEL);
            }
        });
        folderConsolidation.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                update(FOLDER_LEVEL);
            }
        });
        projectConsolidation.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                update(PROJECT_LEVEL);
            }
        });
    }
    
    void read(DiscoveryDescriptor wizardDescriptor) {
    }
    
    void store(DiscoveryDescriptor wizardDescriptor) {
        wizardDescriptor.setLevel(level);
    }
    
    boolean valid(DiscoveryDescriptor settings) {
        // TOD: remove when folder can be configured
        return true;
    }

    private void update(String level) {
        this.level = level;
        wizard.stateChanged(null);
      	String description = NbBundle.getMessage(ConsolidationStrategyPanel.class,
                "ConsolidationDescription_"+level); // NOI18N
        instructionsTextArea.setText(description);
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        consolidationGroup = new javax.swing.ButtonGroup();
        instructionPanel = new javax.swing.JPanel();
        instructionsTextArea = new javax.swing.JTextArea();
        projectConsolidation = new javax.swing.JRadioButton();
        folderConsolidation = new javax.swing.JRadioButton();
        fileConsolidation = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        instructionPanel.setLayout(new java.awt.GridBagLayout());

        instructionPanel.setVerifyInputWhenFocusTarget(false);
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

        consolidationGroup.add(projectConsolidation);
        org.openide.awt.Mnemonics.setLocalizedText(projectConsolidation, java.util.ResourceBundle.getBundle("org/netbeans/modules/cnd/discovery/wizard/Bundle").getString("ConsolidateToProjectLabel"));
        projectConsolidation.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        projectConsolidation.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        add(projectConsolidation, gridBagConstraints);

        consolidationGroup.add(folderConsolidation);
        org.openide.awt.Mnemonics.setLocalizedText(folderConsolidation, java.util.ResourceBundle.getBundle("org/netbeans/modules/cnd/discovery/wizard/Bundle").getString("ConsolidateToFolderLabel"));
        folderConsolidation.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        folderConsolidation.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(folderConsolidation, gridBagConstraints);

        consolidationGroup.add(fileConsolidation);
        org.openide.awt.Mnemonics.setLocalizedText(fileConsolidation, java.util.ResourceBundle.getBundle("org/netbeans/modules/cnd/discovery/wizard/Bundle").getString("FileConsolidateLabel"));
        fileConsolidation.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        fileConsolidation.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(fileConsolidation, gridBagConstraints);

        jLabel1.setLabelFor(projectConsolidation);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, java.util.ResourceBundle.getBundle("org/netbeans/modules/cnd/discovery/wizard/Bundle").getString("ConsolidationLevelText"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabel1, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup consolidationGroup;
    private javax.swing.JRadioButton fileConsolidation;
    private javax.swing.JRadioButton folderConsolidation;
    private javax.swing.JPanel instructionPanel;
    private javax.swing.JTextArea instructionsTextArea;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JRadioButton projectConsolidation;
    // End of variables declaration//GEN-END:variables
    
}
