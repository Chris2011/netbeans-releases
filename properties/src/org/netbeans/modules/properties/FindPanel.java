/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.modules.properties;


import java.util.ResourceBundle;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.openide.util.NbBundle;


/**
 * Find panel for Resource Bundles table view component. GUI represenation only.
 *
 * @author  Peter Zavadsky
 */
public class FindPanel extends javax.swing.JPanel {
    
    /** Resource bundle variable used for i18n os string in this source. */
    private static ResourceBundle bundle;
    
    
    /** Creates new form FindPanel. */
    public FindPanel() {
        initComponents ();
    }
 

    // Accessor methods.
    
    /** Accessor to buttons. */
    public JButton[] getButtons() {
        return new JButton[] { findButton, cancelButton};
    }
    
    /** Accessor to combo box. */
    public JComboBox getComboBox() {
        return findCombo;
    }

    /** Accessor to highlight check box. */
    public JCheckBox getHighlightCheck() {
        return highlightCheck;
    }
    
    /** Accessor to match case check box. */
    public JCheckBox getMatchCaseCheck() {
        return matchCaseCheck;
    }
    
    /** Accessor to backward check box. */
    public JCheckBox getBackwardCheck() {
        return backwardCheck;
    }
    
    /** Accessor to wrap check box. */
    public JCheckBox getWrapCheck() {
        return wrapCheck;
    }
    
    /** Accessor to row check box. */
    public JCheckBox getRowCheck() {
        return rowCheck;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        findLabel = new javax.swing.JLabel();
        findCombo = new javax.swing.JComboBox();
        findButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        matchCaseCheck = new javax.swing.JCheckBox();
        backwardCheck = new javax.swing.JCheckBox();
        wrapCheck = new javax.swing.JCheckBox();
        rowCheck = new javax.swing.JCheckBox();
        highlightCheck = new javax.swing.JCheckBox();
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        
        findLabel.setText(FindPanel.getBundle().getString("LBL_Find"));
        findLabel.setLabelFor(findCombo);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.insets = new java.awt.Insets(12, 12, 0, 0);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(findLabel, gridBagConstraints1);
        
        
        findCombo.setEditable(true);
        findCombo.setNextFocusableComponent(highlightCheck);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.gridwidth = 2;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(12, 11, 0, 0);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.weightx = 1.0;
        add(findCombo, gridBagConstraints1);
        
        
        findButton.setText(FindPanel.getBundle().getString("CTL_Find"));
        findButton.setNextFocusableComponent(cancelButton);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 3;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(12, 11, 0, 11);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.EAST;
        add(findButton, gridBagConstraints1);
        
        
        cancelButton.setText(FindPanel.getBundle().getString("CTL_Cancel"));
        cancelButton.setNextFocusableComponent(findCombo);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 3;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(5, 11, 0, 11);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHEAST;
        add(cancelButton, gridBagConstraints1);
        
        
        matchCaseCheck.setText(FindPanel.getBundle().getString("CTL_MatchCaseCheck"));
        matchCaseCheck.setNextFocusableComponent(backwardCheck);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.gridwidth = 2;
        gridBagConstraints1.insets = new java.awt.Insets(0, 12, 0, 0);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        add(matchCaseCheck, gridBagConstraints1);
        
        
        backwardCheck.setText(FindPanel.getBundle().getString("CTL_BackwardCheck"));
        backwardCheck.setNextFocusableComponent(wrapCheck);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 3;
        gridBagConstraints1.gridwidth = 2;
        gridBagConstraints1.insets = new java.awt.Insets(0, 12, 11, 0);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(backwardCheck, gridBagConstraints1);
        
        
        wrapCheck.setText(FindPanel.getBundle().getString("CTL_WrapSearch"));
        wrapCheck.setNextFocusableComponent(rowCheck);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 2;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.insets = new java.awt.Insets(5, 12, 0, 0);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        add(wrapCheck, gridBagConstraints1);
        
        
        rowCheck.setText(FindPanel.getBundle().getString("CTL_SearchByRows"));
        rowCheck.setNextFocusableComponent(findButton);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 2;
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.insets = new java.awt.Insets(0, 11, 0, 0);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(rowCheck, gridBagConstraints1);
        
        
        highlightCheck.setText(FindPanel.getBundle().getString("CTL_HighlightCheck"));
        highlightCheck.setNextFocusableComponent(matchCaseCheck);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.gridwidth = 2;
        gridBagConstraints1.insets = new java.awt.Insets(5, 12, 0, 0);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        add(highlightCheck, gridBagConstraints1);
        
    }//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel findLabel;
    private javax.swing.JComboBox findCombo;
    private javax.swing.JButton findButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JCheckBox matchCaseCheck;
    private javax.swing.JCheckBox backwardCheck;
    private javax.swing.JCheckBox wrapCheck;
    private javax.swing.JCheckBox rowCheck;
    private javax.swing.JCheckBox highlightCheck;
    // End of variables declaration//GEN-END:variables

    /** Helper variable for lazy <code>bundle</code> initialization. */
    private static ResourceBundle getBundle() {
        if(bundle == null)
            bundle = NbBundle.getBundle(PropertiesModule.class);
        
        return bundle;            
    }
}
