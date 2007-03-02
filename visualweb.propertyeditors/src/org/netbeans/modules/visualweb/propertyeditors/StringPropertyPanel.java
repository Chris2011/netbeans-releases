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
package org.netbeans.modules.visualweb.propertyeditors;

import java.util.ResourceBundle;

/**
 * A custom property editor panel for string properties. Empty strings are
 * treated the same as in <code>StringPropertyEditor.setAsText()</code>.
 *
 * @author gjmurphy
 */
public class StringPropertyPanel extends PropertyPanelBase {

    static ResourceBundle bundle =
            ResourceBundle.getBundle(StringPropertyPanel.class.getPackage().getName() + ".Bundle"); //NOI18N

    public StringPropertyPanel(StringPropertyEditor editor) {
        super(editor);
        initComponents();
        this.textArea.setText((String) editor.getValue());
    }

    public Object getPropertyValue() throws IllegalArgumentException {
        String value = this.textArea.getText();
        if (value.trim().length() == 0)
            return null;
        return value;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();

        setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setPreferredSize(new java.awt.Dimension(80, 40));
        textArea.setRows(1);
        textArea.setTabSize(4);
        jScrollPane1.setViewportView(textArea);
        textArea.getAccessibleContext().setAccessibleName(bundle.getString("StringPropertyPanel.panel.AccessibleName"));
        textArea.getAccessibleContext().setAccessibleDescription(bundle.getString("StringPropertyPanel.panel.AccessibleDescription"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(6, 10, 10, 10);
        add(jScrollPane1, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea textArea;
    // End of variables declaration//GEN-END:variables

}
