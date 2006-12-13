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

package org.netbeans.modules.websvc.jaxrpc.client.ui;

import org.openide.DialogDescriptor;
import org.openide.util.NbBundle;
import org.netbeans.modules.websvc.core.WsdlRetriever;

/**
 *
 * @author Peter Williams
 */
public class DownloadWsdlPanel extends javax.swing.JPanel implements WsdlRetriever.MessageReceiver {

    private DialogDescriptor descriptor;
    private WsdlRetriever retriever;
    private String newWsdlUrl;
    private String downloadMsg;
    private boolean downloadOk;

    public DownloadWsdlPanel(String newWsdlUrl) {
        this.newWsdlUrl = newWsdlUrl;
        this.retriever = null;
        this.downloadMsg = " "; // NOI18N
        this.downloadOk = false;
        
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jLblStatusLabel = new javax.swing.JLabel();
        jTxtStatus = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        jLblStatusLabel.setText(NbBundle.getMessage(DownloadWsdlPanel.class, "LBL_Status"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(11, 11, 11, 4);
        add(jLblStatusLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 11, 11);
        add(jTxtStatus, gridBagConstraints);

    }//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLblStatusLabel;
    private javax.swing.JLabel jTxtStatus;
    // End of variables declaration//GEN-END:variables
 
    public void addNotify() {
        super.addNotify();
        
        retriever = new WsdlRetriever(this, newWsdlUrl);
        new Thread(retriever).start();
    }
    
    public void setDescriptor(DialogDescriptor descriptor) {
		this.descriptor = descriptor;
        descriptor.setValid(downloadOk);
	}

    public byte [] getWsdl() {
        byte [] result = null;
        
        if(retriever.getState() == WsdlRetriever.STATUS_COMPLETE) {
            result = retriever.getWsdl();
        }
        
        return result;
    }
    
    public void setWsdlDownloadMessage(String m) {
        downloadMsg = m;
        jTxtStatus.setText(downloadMsg);
        
        if(retriever.getState() == WsdlRetriever.STATUS_COMPLETE) {
            downloadOk = true;

            // !PW FIXME Find a way to press <OK> button from here.
        }
        
        descriptor.setValid(downloadOk);
    }

    public java.awt.Dimension getPreferredSize() {
        java.awt.Dimension result = super.getPreferredSize();
        if(result.width < 240) {
            result.width = 240;
        }
        return result;
    }
}
