/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.beans;

import java.awt.Dialog;
import java.util.ResourceBundle;
import java.text.MessageFormat;
import javax.swing.border.TitledBorder;
import org.openide.DialogDisplayer;

import org.openide.util.Utilities;
import org.openide.util.NbBundle;
import org.openide.NotifyDescriptor;
import org.openide.src.*;
import org.openide.util.HelpCtx;
/** Customizer for new Multicast Event Set
 *
 * @author Petr Hrebejk
 */
public class EventSetPatternPanel extends javax.swing.JPanel
    implements java.awt.event.ActionListener {

    /** Dialog for displaiyng this panel */
    private Dialog dialog;
    /** Group node under which the new pattern will below */
    private PatternGroupNode groupNode;
    /** Geneartion for interface/class */
    private boolean forInterface;
    
    private transient PatternAnalyser patternAnalyser;

    static final long serialVersionUID =-6439362166672698327L;

    /** Initializes the Form */
    public EventSetPatternPanel( PatternAnalyser patternAnalyser) {
        this.patternAnalyser = patternAnalyser;
        
        initComponents ();
        initAccessibility();

        for( int i = 0; i < EventSetPattern.WELL_KNOWN_LISTENERS.length; i++ ) {
            typeComboBox.addItem( EventSetPattern.WELL_KNOWN_LISTENERS[i] );
        }
        typeComboBox.setSelectedItem( "" ); // NOI18N

        javax.swing.ButtonGroup bg = new javax.swing.ButtonGroup();
        bg.add( emptyRadioButton );
        bg.add( alRadioButton );
        bg.add( ellRadioButton );

        // i18n

        ((TitledBorder)eventSetPanel.getBorder()).setTitle(
            PatternNode.getString( "CTL_EventSetPanel_eventSetPanel" ));
        ((TitledBorder)optionsPanel.getBorder()).setTitle(
            PatternNode.getString( "CTL_EventSetPanel_optionsPanel" ) );
        typeLabel.setText( PatternNode.getString( "CTL_EventSetPanel_typeLabel" ) );
        typeLabel.setDisplayedMnemonic(PatternNode.getString("CTL_EventSetPanel_typeLabel_Mnemonic").charAt(0));
        typeLabel.setLabelFor(typeComboBox);
        typeComboBox.setToolTipText(PatternNode.getString("ACS_EventSetPanel_typeComboBoxA11yDesc"));
        textLabel.setText( PatternNode.getString( "CTL_EventSetPanel_textLabel" ) );
        emptyRadioButton.setText( PatternNode.getString( "CTL_EventSetPanel_emptyRadioButton" ) );
        emptyRadioButton.setMnemonic(PatternNode.getString("CTL_EventSetPanel_emptyRadioButton_Mnemonic").charAt(0));
        emptyRadioButton.setToolTipText(PatternNode.getString("ACS_EventSetPanel_emptyRadioButtonA11yDesc"));
        alRadioButton.setText( PatternNode.getString( "CTL_EventSetPanel_alRadioButton" ) );
        alRadioButton.setMnemonic(PatternNode.getString("CTL_EventSetPanel_alRadioButton_Mnemonic").charAt(0));
        alRadioButton.setToolTipText(PatternNode.getString("ACS_EventSetPanel_alRadioButtonA11yDesc"));
        ellRadioButton.setText( PatternNode.getString( "CTL_EventSetPanel_ellRadioButton" ) );
        ellRadioButton.setMnemonic(PatternNode.getString("CTL_EventSetPanel_ellRadioButton_Mnemonic").charAt(0));
        ellRadioButton.setToolTipText(PatternNode.getString("ACS_EventSetPanel_ellRadioButtonA11yDesc"));
        fireCheckBox.setText( PatternNode.getString( "CTL_EventSetPanel_fireCheckBox" ) );
        fireCheckBox.setMnemonic(PatternNode.getString("CTL_EventSetPanel_fireCheckBox_Mnemonic").charAt(0));
        fireCheckBox.setToolTipText(PatternNode.getString("ACS_EventSetPanel_fireCheckBoxA11yDesc"));
        passEventCheckBox.setText( PatternNode.getString( "CTL_EventSetPanel_passEventCheckBox" ) );
        passEventCheckBox.setMnemonic(PatternNode.getString("CTL_EventSetPanel_passEventCheckBox_Mnemonic").charAt(0));
        passEventCheckBox.setToolTipText(PatternNode.getString("ACS_EventSetPanel_passEventCheckBoxA11yDesc"));
        
        HelpCtx.setHelpIDString(this, HelpCtxKeys.BEAN_EVENTSETS_HELP); //NO I18N
    }
    
    private void initAccessibility()
    {
        this.getAccessibleContext().setAccessibleDescription(PatternNode.getString("ACSD_EventSetPanelDialog"));
        typeLabel.getAccessibleContext().setAccessibleDescription(PatternNode.getString("ACS_EventSetPanel_typeLabelA11yDesc"));
        typeComboBox.getAccessibleContext().setAccessibleName(PatternNode.getString("ACS_EventSetPanel_typeComboBoxA11yName"));
        textLabel.getAccessibleContext().setAccessibleDescription(PatternNode.getString("ACS_EventSetPanel_textLabelA11yDesc"));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        mainPanel = new javax.swing.JPanel();
        eventSetPanel = new javax.swing.JPanel();
        typeLabel = new javax.swing.JLabel();
        typeComboBox = new javax.swing.JComboBox();
        textLabel = new javax.swing.JLabel();
        optionsPanel = new javax.swing.JPanel();
        emptyRadioButton = new javax.swing.JRadioButton();
        alRadioButton = new javax.swing.JRadioButton();
        ellRadioButton = new javax.swing.JRadioButton();
        fireCheckBox = new javax.swing.JCheckBox();
        passEventCheckBox = new javax.swing.JCheckBox();

        setLayout(new java.awt.BorderLayout());

        mainPanel.setLayout(new java.awt.GridBagLayout());

        mainPanel.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(5, 5, 5, 5)));
        eventSetPanel.setLayout(new java.awt.GridBagLayout());

        eventSetPanel.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.EtchedBorder(null, new java.awt.Color(149, 142, 130)), "eventSetPanel"));
        typeLabel.setText("typeLabel");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);
        eventSetPanel.add(typeLabel, gridBagConstraints);

        typeComboBox.setEditable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        eventSetPanel.add(typeComboBox, gridBagConstraints);

        textLabel.setText("textLabel");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 2, 2);
        eventSetPanel.add(textLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        mainPanel.add(eventSetPanel, gridBagConstraints);

        optionsPanel.setLayout(new java.awt.GridBagLayout());

        optionsPanel.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.EtchedBorder(null, new java.awt.Color(149, 142, 130)), "optionsPanel"));
        emptyRadioButton.setSelected(true);
        emptyRadioButton.setLabel("emptyRadioButton");
        emptyRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emptyRadioButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 2, 4);
        optionsPanel.add(emptyRadioButton, gridBagConstraints);

        alRadioButton.setLabel("alRadioButton");
        alRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alRadioButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 2, 4);
        optionsPanel.add(alRadioButton, gridBagConstraints);

        ellRadioButton.setText("ellradioButton");
        ellRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ellRadioButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 2, 4);
        optionsPanel.add(ellRadioButton, gridBagConstraints);

        fireCheckBox.setText("fireCheckBox");
        fireCheckBox.setEnabled(false);
        fireCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireCheckBoxActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 4);
        optionsPanel.add(fireCheckBox, gridBagConstraints);

        passEventCheckBox.setText("passEventCheckBox");
        passEventCheckBox.setEnabled(false);
        passEventCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passEventCheckBoxActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 2, 4);
        optionsPanel.add(passEventCheckBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        mainPanel.add(optionsPanel, gridBagConstraints);

        add(mainPanel, java.awt.BorderLayout.CENTER);

    }//GEN-END:initComponents

    private void emptyRadioButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emptyRadioButtonActionPerformed
        protectControls();
    }//GEN-LAST:event_emptyRadioButtonActionPerformed

    private void fireCheckBoxActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fireCheckBoxActionPerformed
        protectControls();

    }//GEN-LAST:event_fireCheckBoxActionPerformed

    private void ellRadioButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ellRadioButtonActionPerformed
        protectControls();

    }//GEN-LAST:event_ellRadioButtonActionPerformed

    private void alRadioButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alRadioButtonActionPerformed
        protectControls();
    }//GEN-LAST:event_alRadioButtonActionPerformed


    private void passEventCheckBoxActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passEventCheckBoxActionPerformed
        protectControls();
    }//GEN-LAST:event_passEventCheckBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox fireCheckBox;
    private javax.swing.JRadioButton emptyRadioButton;
    private javax.swing.JPanel eventSetPanel;
    private javax.swing.JRadioButton ellRadioButton;
    private javax.swing.JLabel textLabel;
    private javax.swing.JRadioButton alRadioButton;
    private javax.swing.JPanel optionsPanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JCheckBox passEventCheckBox;
    private javax.swing.JComboBox typeComboBox;
    private javax.swing.JLabel typeLabel;
    // End of variables declaration//GEN-END:variables


    class Result {

        String type;
        int    implementation = 0;

        boolean firing = false;
        boolean passEvent = false;
    }

    EventSetPatternPanel.Result getResult( ) {
        Result result = new Result();


        result.type = typeComboBox.getEditor().getItem().toString();

        if ( alRadioButton.isSelected() )
            result.implementation = 1;

        else if ( ellRadioButton.isSelected() )
            result.implementation = 2;

        if ( fireCheckBox.isSelected() )
            result.firing = true;

        if ( passEventCheckBox.isSelected() )
            result.passEvent = true;

        return result;
    }

    private void protectControls() {
        alRadioButton.setEnabled( !forInterface );
        ellRadioButton.setEnabled( !forInterface );

        fireCheckBox.setEnabled( !emptyRadioButton.isSelected() );
        passEventCheckBox.setEnabled( !emptyRadioButton.isSelected() && fireCheckBox.isSelected() );
    }

    void setDialog( Dialog dialog ) {
        this.dialog = dialog;
    }

    void setForInterface( boolean forInterface ) {
        this.forInterface = forInterface;
        protectControls();
    }

    void setGroupNode( PatternGroupNode groupNode ) {
        this.groupNode = groupNode;
    }

    public void actionPerformed( java.awt.event.ActionEvent e ) {
        if ( dialog != null ) {

            if ( e.getSource() == org.openide.DialogDescriptor.OK_OPTION ) {
                //Test wether the string is empty
                if ( typeComboBox.getEditor().getItem().toString().trim().length() <= 0) {
                    DialogDisplayer.getDefault().notify(
                        new NotifyDescriptor.Message(
                            PatternNode.getString("MSG_Not_Valid_Type"),
                            NotifyDescriptor.ERROR_MESSAGE) );
                    typeComboBox.requestFocus();
                    return;
                }

                Type type;
                
                try {
                    type = Type.parse( typeComboBox.getEditor().getItem().toString() );
                    // Test wheter property with this name already exists
                    if ( groupNode.eventSetExists( type ) ) {
                        String msg = MessageFormat.format( PatternNode.getString("MSG_EventSet_Exists"),
                                                           new Object[] { type.toString() } );
                        DialogDisplayer.getDefault().notify(
                            new NotifyDescriptor.Message( msg, NotifyDescriptor.ERROR_MESSAGE) );

                        typeComboBox.requestFocus();
                        return;
                    }
                }
                catch ( IllegalArgumentException ex ) {
                    DialogDisplayer.getDefault().notify(
                        new NotifyDescriptor.Message(
                            PatternNode.getString("MSG_Not_Valid_Type"),
                            NotifyDescriptor.ERROR_MESSAGE) );
                    typeComboBox.requestFocus();
                    return;
                }
                
                // Check whether the property points to a valid listener
                if ( !PatternAnalyser.isSubclass(
                    patternAnalyser.findClassElement( type.getClassName().getFullName() ),
                    patternAnalyser.findClassElement( "java.util.EventListener" ) ) ) { // NOI18N

                    DialogDisplayer.getDefault().notify(
                        new NotifyDescriptor.Message(PatternNode.getString("MSG_InvalidListenerInterface"),
                                                     NotifyDescriptor.ERROR_MESSAGE) );
                    return;
                }
            }


            dialog.setVisible( false );
            dialog.dispose();
        }
    }

}
