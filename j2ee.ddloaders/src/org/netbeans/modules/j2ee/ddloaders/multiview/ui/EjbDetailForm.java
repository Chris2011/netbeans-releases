/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.ddloaders.multiview.ui;

import org.netbeans.modules.xml.multiview.ui.SectionInnerPanel;
import org.netbeans.modules.xml.multiview.ui.SectionNodeView;

import javax.swing.*;

/**
 * @author pfiala
 */
public class EjbDetailForm extends SectionInnerPanel {

    /**
     * Creates new form EjbDetailForm
     */
    public EjbDetailForm(SectionNodeView sectionNodeView) {
        super(sectionNodeView);
        initComponents();
        descriptionTextArea.setBorder(displayNameTextField.getBorder());
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        displayNameLabel = new javax.swing.JLabel();
        descriptionLabel = new javax.swing.JLabel();
        smallIconlabel = new javax.swing.JLabel();
        largeIconLabel = new javax.swing.JLabel();
        browseSmallIconButton = new javax.swing.JButton();
        browseLargeIconButton = new javax.swing.JButton();
        displayNameTextField = new javax.swing.JTextField();
        smallIconTextField = new javax.swing.JTextField();
        descriptionTextArea = new javax.swing.JTextArea();
        largeIconTextField = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        displayNameLabel.setText("Display Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(displayNameLabel, gridBagConstraints);

        descriptionLabel.setText("Description:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(descriptionLabel, gridBagConstraints);

        smallIconlabel.setText("Small Icon:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(smallIconlabel, gridBagConstraints);

        largeIconLabel.setText("Large Icon:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(largeIconLabel, gridBagConstraints);

        browseSmallIconButton.setText("Browse ...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(browseSmallIconButton, gridBagConstraints);

        browseLargeIconButton.setText("Browse ...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(browseLargeIconButton, gridBagConstraints);

        displayNameTextField.setColumns(25);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(displayNameTextField, gridBagConstraints);

        smallIconTextField.setColumns(25);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(smallIconTextField, gridBagConstraints);

        descriptionTextArea.setRows(3);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(descriptionTextArea, gridBagConstraints);

        largeIconTextField.setColumns(25);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(largeIconTextField, gridBagConstraints);

    }//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseLargeIconButton;
    private javax.swing.JButton browseSmallIconButton;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JTextArea descriptionTextArea;
    private javax.swing.JLabel displayNameLabel;
    private javax.swing.JTextField displayNameTextField;
    private javax.swing.JLabel largeIconLabel;
    private javax.swing.JTextField largeIconTextField;
    private javax.swing.JTextField smallIconTextField;
    private javax.swing.JLabel smallIconlabel;
    // End of variables declaration//GEN-END:variables

    public JTextArea getDescriptionTextArea() {
        return descriptionTextArea;
    }

    public JTextField getDisplayNameTextField() {
        return displayNameTextField;
    }

    public JTextField getLargeIconTextField() {
        return largeIconTextField;
    }

    public JTextField getSmallIconTextField() {
        return smallIconTextField;
    }

    public JButton getBrowseLargeIconButton() {
        return browseLargeIconButton;
    }

    public JButton getBrowseSmallIconButton() {
        return browseSmallIconButton;
    }

    public JComponent getErrorComponent(String errorId) {
        return null;
    }

    public void setValue(JComponent source, Object value) {

    }

    public void linkButtonPressed(Object ddBean, String ddProperty) {

    }

}
