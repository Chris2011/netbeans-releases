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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.dbschema.jdbcimpl.wizard;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JDialog;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.NbBundle;

/**
 *
 * @author Martin Adamek, Andrei Badea
 */
public class ProgressPanel extends javax.swing.JPanel {

    private Dialog dialog;

    /** Creates new form ProgressPanel */
    public ProgressPanel() {
        initComponents();
    }

    public void open(JComponent progressComponent) {
        holder.add(progressComponent, BorderLayout.CENTER);
        DialogDescriptor dd = new DialogDescriptor(
                this,
                NbBundle.getMessage(ProgressPanel.class, "MSG_PleaseWait"),
                true,
                new Object[0],
                DialogDescriptor.NO_OPTION,
                DialogDescriptor.DEFAULT_ALIGN,
                null,
                null
                );
        dialog = DialogDisplayer.getDefault().createDialog(dd);
        if (dialog instanceof JDialog) {
            ((JDialog)dialog).setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        }
        dialog.setResizable(false);
        dialog.setVisible(true);
    }
    
    public void close() {
	if (dialog != null) {
	    dialog.setVisible(false);
	    dialog.dispose();
	}
    }
    
    public void setText(String text) {
        info.setText(text);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        info = new javax.swing.JLabel();
        holder = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        info.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 11, 11);
        add(info, gridBagConstraints);

        holder.setLayout(new java.awt.BorderLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 11, 11);
        add(holder, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel holder;
    private javax.swing.JLabel info;
    // End of variables declaration//GEN-END:variables
    
    public Dimension getPreferredSize() {
        Dimension orig = super.getPreferredSize();
        return new Dimension(500, orig.height);
    }
    
}
