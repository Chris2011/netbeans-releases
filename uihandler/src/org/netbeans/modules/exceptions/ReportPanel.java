/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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

package org.netbeans.modules.exceptions;

import java.awt.Cursor;
import java.net.MalformedURLException;
import org.netbeans.lib.uihandler.PasswdEncryption;
import org.openide.awt.HtmlBrowser;

/**
 *
 * @author  Jindrich Sedek
 */


public class ReportPanel extends javax.swing.JPanel {
    private final ExceptionsSettings exSettings;
    
    public ReportPanel(boolean isOOM, ExceptionsSettings exSettings) {
        this.exSettings = exSettings;
        initComponents();
        if (isOOM){
            oomInfo.setVisible(true);
        }else{
            oomInfo.setVisible(false);
        }
        jLabel10.setVisible(false);
        asAGuestCheckBoxActionPerformed(null);
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel6.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jLabel1 = new javax.swing.JLabel();
        loginField = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel9 = new javax.swing.JLabel();
        summaryField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        commentArea = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPasswordField1 = new javax.swing.JPasswordField();
        rememberCheckBox = new javax.swing.JCheckBox();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        asAGuestCheckBox = new javax.swing.JCheckBox();
        oomInfo = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(630, 430));

        jLabel3.setText(org.openide.util.NbBundle.getMessage(ReportPanel.class, "ReportPanel.jLabel3.text")); // NOI18N

        jLabel6.setForeground(new java.awt.Color(0, 0, 255));
        jLabel6.setText(org.openide.util.NbBundle.getMessage(ReportPanel.class, "ReportPanel.jLabel6.text")); // NOI18N
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                registerClicked(evt);
            }
        });

        jLabel1.setLabelFor(loginField);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(ReportPanel.class, "ReportPanel.jLabel1.text")); // NOI18N

        loginField.setText(exSettings.getUserName());

        jLabel9.setLabelFor(summaryField);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel9, org.openide.util.NbBundle.getMessage(ReportPanel.class, "ReportPanel.jLabel9.text")); // NOI18N

        summaryField.setText(org.openide.util.NbBundle.getMessage(ReportPanel.class, "ReportPanel.summaryField.text")); // NOI18N

        jLabel4.setFont(jLabel4.getFont().deriveFont(jLabel4.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel4.setForeground(new java.awt.Color(166, 113, 6));
        jLabel4.setLabelFor(commentArea);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(ReportPanel.class, "ReportPanel.jLabel4.text")); // NOI18N

        commentArea.setColumns(20);
        commentArea.setLineWrap(true);
        commentArea.setRows(5);
        commentArea.setWrapStyleWord(true);
        jScrollPane1.setViewportView(commentArea);
        commentArea.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ReportPanel.class, "ReportPanel.commentArea.AccessibleContext.accessibleName")); // NOI18N
        commentArea.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ReportPanel.class, "ReportPanel.commentArea.AccessibleContext.accessibleDescription")); // NOI18N

        jLabel2.setText(org.openide.util.NbBundle.getMessage(ReportPanel.class, "ReportPanel.jLabel2.text")); // NOI18N

        jLabel8.setText(org.openide.util.NbBundle.getMessage(ReportPanel.class, "ReportPanel.jLabel8.text_1")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(ReportPanel.class, "ReportPanel.jLabel5.text_1")); // NOI18N

        jPasswordField1.setText(new String(exSettings.getPasswd()));

        rememberCheckBox.setSelected(exSettings.rememberPasswd());
        org.openide.awt.Mnemonics.setLocalizedText(rememberCheckBox, org.openide.util.NbBundle.getMessage(ReportPanel.class, "jCheckBox1.text")); // NOI18N
        rememberCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        jLabel10.setForeground(new java.awt.Color(255, 51, 51));
        jLabel10.setText(org.openide.util.NbBundle.getMessage(ReportPanel.class, "ReportPanel.jLabel10.text")); // NOI18N

        jLabel11.setText(org.openide.util.NbBundle.getMessage(ReportPanel.class, "ReportPanel.jLabel11.text")); // NOI18N

        asAGuestCheckBox.setSelected(exSettings.isGuest());
        org.openide.awt.Mnemonics.setLocalizedText(asAGuestCheckBox, org.openide.util.NbBundle.getMessage(ReportPanel.class, "ReportPanel.asAGuestCheckBox.text")); // NOI18N
        asAGuestCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                asAGuestCheckBoxActionPerformed(evt);
            }
        });

        oomInfo.setText(org.openide.util.NbBundle.getMessage(ReportPanel.class, "ReportPanel.oomInfo.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 623, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPasswordField1, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                            .addComponent(loginField, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                            .addComponent(rememberCheckBox)
                            .addComponent(asAGuestCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(jLabel11))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 624, Short.MAX_VALUE)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 624, Short.MAX_VALUE))
                        .addGap(11, 11, 11))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(oomInfo))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel6)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(summaryField)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(summaryField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel11)
                    .addComponent(loginField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel10)
                    .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addComponent(rememberCheckBox)
                .addGap(3, 3, 3)
                .addComponent(asAGuestCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(oomInfo))
        );

        loginField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ReportPanel.class, "ReportPanel.loginField.AccessibleContext.accessibleName")); // NOI18N
        loginField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ReportPanel.class, "ReportPanel.loginField.AccessibleContext.accessibleDescription")); // NOI18N
        summaryField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ReportPanel.class, "ReportPanel.summaryField.AccessibleContext.accessibleName")); // NOI18N
        summaryField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ReportPanel.class, "ReportPanel.summaryField.AccessibleContext.accessibleDescription")); // NOI18N
        jPasswordField1.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ReportPanel.class, "ReportPanel.jPasswordField1.AccessibleContext.accessibleName")); // NOI18N
        jPasswordField1.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ReportPanel.class, "ReportPanel.jPasswordField1.AccessibleContext.accessibleDescription")); // NOI18N
        rememberCheckBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ReportPanel.class, "ReportPanel.rememberCheckBox.AccessibleContext.accessibleDescription")); // NOI18N

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ReportPanel.class, "ReportPanel.AccessibleContext.accessibleName")); // NOI18N
        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ReportPanel.class, "ReportPanel.AccessibleContext.accessibleDescription")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents
        private void registerClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_registerClicked
            try {
                HtmlBrowser.URLDisplayer.getDefault().showURL(new java.net.URL(org.openide.util.NbBundle.getMessage(ReportPanel.class, "REGISTRATION_URL")));
            } catch (MalformedURLException ex) {
                java.util.logging.Logger.getLogger(ReportPanel.class.getName()).log(java.util.logging.Level.INFO, ex.getMessage(), ex);
            }
    }//GEN-LAST:event_registerClicked

        private void asAGuestCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_asAGuestCheckBoxActionPerformed
            if (asAGuestCheckBox.isSelected()){
                loginField.setEnabled(false);
                jPasswordField1.setEnabled(false);
                rememberCheckBox.setEnabled(false);
                jLabel10.setVisible(false);
            }else{
                loginField.setEnabled(true);
                jPasswordField1.setEnabled(true);
                rememberCheckBox.setEnabled(true);
            }
        }//GEN-LAST:event_asAGuestCheckBoxActionPerformed
        
       public  void saveUserData() {
            if (asAGuestCheckBox.isSelected()){
                exSettings.setGuest(true);
                return;
            }
            String login = loginField.getText();
            exSettings.setUserName(login);
            exSettings.setGuest(false);
            boolean rememberPasswd = rememberCheckBox.isSelected();
            exSettings.setRememberPasswd(rememberPasswd);
            if (rememberPasswd){
                exSettings.setPasswd(getPasswdChars());
            }else{
                exSettings.setPasswd(new char[0]);   //NOI18N
            }
        }
        
        public boolean asAGuest(){
            return asAGuestCheckBox.isSelected();
        }

        public String getUserName(){
            return loginField.getText().trim();
        }

        public char[] getPasswdChars(){
            char[] localPasswd = jPasswordField1.getPassword();
            if (localPasswd.length > PasswdEncryption.MAX_ENCRYPTION_LENGHT){
                localPasswd = copyOf(localPasswd, PasswdEncryption.MAX_ENCRYPTION_LENGHT);
            }
            return localPasswd;
        }

        public static char[] copyOf(char[] original, int newLength) {
            char[] copy = new char[newLength];
            System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
            return copy;
        }

        public String getSummary() {
            return summaryField.getText();
        }
        
        public String getComment() {
            return commentArea.getText();
        }
        
        public void setSummary(String str){
            summaryField.setText(str);
        }
        
        public void showWrongPassword(){
            jLabel10.setForeground(new java.awt.Color(255, 51, 51));
            jLabel10.setText(org.openide.util.NbBundle.getMessage(ReportPanel.class, "ReportPanel.jLabel10.text")); // NOI18N
            jLabel10.setVisible(true);
        }

        public void showCheckingPassword() {
            jLabel10.setForeground(new java.awt.Color(51, 51, 255));
            jLabel10.setText(org.openide.util.NbBundle.getMessage(ReportPanel.class, "ReportPanel.checking_password")); // NOI18N
            jLabel10.setVisible(true);
        }

        public void setInitialFocus(){
            commentArea.requestFocusInWindow();
        }
        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox asAGuestCheckBox;
    private javax.swing.JTextArea commentArea;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField loginField;
    private javax.swing.JLabel oomInfo;
    private javax.swing.JCheckBox rememberCheckBox;
    private javax.swing.JTextField summaryField;
    // End of variables declaration//GEN-END:variables
}
