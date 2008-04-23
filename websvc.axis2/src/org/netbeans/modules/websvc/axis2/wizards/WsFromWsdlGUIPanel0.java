/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.websvc.axis2.wizards;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;


/**
 *
 * @author  mkuchtiak
 */
public class WsFromWsdlGUIPanel0 extends javax.swing.JPanel {
    WsFromWsdlPanel0 wizardPanel;
    private File lastWsdl;
    
    /** Creates new form WsFromJavaGUIPanel1 */
    public WsFromWsdlGUIPanel0(final WsFromWsdlPanel0 wizardPanel) {
        this.wizardPanel = wizardPanel;
        initComponents();
        setName("WSDL Selection"); //NOI18N
        tfWsdlUrl.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                wizardPanel.fireChange();
            }

            public void removeUpdate(DocumentEvent e) {
                wizardPanel.fireChange();
            }

            public void changedUpdate(DocumentEvent e) {
                wizardPanel.fireChange();
            }
        });
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        tfWsdlUrl = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();

        jLabel1.setLabelFor(tfWsdlUrl);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(WsFromWsdlGUIPanel0.class, "WsFromWsdlGUIPanel0.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(browseButton, org.openide.util.NbBundle.getMessage(WsFromWsdlGUIPanel0.class, "WsFromJavaGUIPanel0.jButton1.text")); // NOI18N
        browseButton.setActionCommand(org.openide.util.NbBundle.getMessage(WsFromWsdlGUIPanel0.class, "WsFromWsdlGUIPanel0.browseButton.actionCommand")); // NOI18N
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(tfWsdlUrl, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(browseButton))
                    .add(jLabel1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(browseButton)
                    .add(tfWsdlUrl, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tfWsdlUrl.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(WsFromWsdlGUIPanel0.class, "WsFromWsdlGUIPanel0.tfWsdlUrl.AccessibleContext.accessibleDescription")); // NOI18N
        browseButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(WsFromWsdlGUIPanel0.class, "WsFromWsdlGUIPanel0.browseButton.AccessibleContext.accessibleDescription")); // NOI18N

        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(WsFromWsdlGUIPanel0.class, "WsFromWsdlGUIPanel0.AccessibleContext.accessibleDescription")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
// TODO add your handling code here:
        JFileChooser chooser = new JFileChooser(lastWsdl);
        chooser.setMultiSelectionEnabled(false);
        //chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        FileFilter fileFilter = new FileFilter() {

            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".wsdl") || f.getName().toLowerCase().endsWith(".xsd") ; //NOI18N
            }

            @Override
            public String getDescription() {
                return org.openide.util.NbBundle.getMessage(WsFromWsdlGUIPanel0.class,"DESC_FileFilter");
            }
            
        };
        chooser.setAcceptAllFileFilterUsed(true);
        chooser.addChoosableFileFilter(fileFilter);
        chooser.setFileFilter(fileFilter);
        if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File wsdlFile = chooser.getSelectedFile();
            tfWsdlUrl.setText(wsdlFile.getAbsolutePath());
        } 
}//GEN-LAST:event_browseButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField tfWsdlUrl;
    // End of variables declaration//GEN-END:variables
    
    
    boolean dataIsValid() {
        String wsdlUrl = tfWsdlUrl.getText().trim();
        if (wsdlUrl.length() == 0) return false;
        File wsdlFile = new File(wsdlUrl);
        return (wsdlFile != null && wsdlFile.exists());
    }
    
    File getWsdlFile() {
        String wsdlUrl = tfWsdlUrl.getText().trim();
        return new File(wsdlUrl);
    }

}
