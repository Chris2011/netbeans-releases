/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.maven.codegen;

import javax.swing.DefaultListModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.modules.maven.model.pom.POMModel;
import org.openide.DialogDescriptor;
import org.openide.NotificationLineSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;

/**
 *
 * @author mkleint
 */
public class NewLicensePanel extends javax.swing.JPanel {
    private NotificationLineSupport nls;
    private final POMModel model;
    private DialogDescriptor dd;



    NewLicensePanel(POMModel model) {
        initComponents();
        this.model = model;
       
        txtName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkFields();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                checkFields();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                checkFields();
            }
        });
        lstKnown.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (lstKnown.getSelectedValue() != null) {
                    Tuple tup = (Tuple) lstKnown.getSelectedValue();
                    txtName.setText(tup.dn);
                    txtUrl.setText(tup.url);
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblUrl = new javax.swing.JLabel();
        txtUrl = new javax.swing.JTextField();
        lblKnown = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstKnown = new javax.swing.JList();

        lblName.setLabelFor(txtName);
        org.openide.awt.Mnemonics.setLocalizedText(lblName, org.openide.util.NbBundle.getMessage(NewLicensePanel.class, "NewLicensePanel.lblName.text")); // NOI18N

        lblUrl.setLabelFor(txtUrl);
        org.openide.awt.Mnemonics.setLocalizedText(lblUrl, org.openide.util.NbBundle.getMessage(NewLicensePanel.class, "NewLicensePanel.lblUrl.text")); // NOI18N

        lblKnown.setLabelFor(lstKnown);
        org.openide.awt.Mnemonics.setLocalizedText(lblKnown, org.openide.util.NbBundle.getMessage(NewLicensePanel.class, "NewLicensePanel.lblKnown.text")); // NOI18N

        jScrollPane1.setViewportView(lstKnown);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblName)
                            .addComponent(lblUrl))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtUrl)
                            .addComponent(txtName)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblKnown)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblName)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUrl)
                    .addComponent(txtUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblKnown)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(82, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblKnown;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblUrl;
    private javax.swing.JList lstKnown;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtUrl;
    // End of variables declaration//GEN-END:variables

    void attachDialogDisplayer(DialogDescriptor dd) {
       this.dd = dd;
       dd.setValid(false);
       nls = dd.getNotificationLineSupport();
        if (nls == null) {
            nls = dd.createNotificationLineSupport();
        }
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        
        assert nls != null : " The notificationLineSupport was not attached to the panel."; //NOI18N
        DefaultListModel dlm = new DefaultListModel();
        FileObject root = FileUtil.getConfigFile("Templates/Licenses");
        if (root != null) {
            for (FileObject lic : root.getChildren()) {
                String url = (String) lic.getAttribute("mavenLicenseURL");
                if (url != null) {
                    String dn = (String) lic.getAttribute("displayName");
                    if (dn == null) {
                        dn = lic.getName();
                        if (dn.startsWith("license-")) {
                            dn = dn.substring("license-".length());
                        }
                    }
                    Tuple tup = new Tuple(url.replaceFirst(" .+", ""), dn);
                    dlm.addElement(tup);
                }
            }
        }
        lstKnown.setModel(dlm);
    }    

    String getLicenseName() {
        return txtName.getText().trim();
    }

    String getLicenseUrl() {
        return txtUrl.getText().trim();
    }
    
    private void checkFields() {
        if (getLicenseUrl().isEmpty() && getLicenseName().isEmpty()) {
            nls.setWarningMessage("Both license name and url cannot be empty.");
            dd.setValid(false);
        } else {
            nls.clearMessages();
            dd.setValid(true);
        }
    }    

    private static class Tuple {
        final String url;
        final String dn;

        private Tuple(String url, String dn) {
            this.url = url;
            this.dn = dn;
        }
        
        @Override
        public String toString() {
            return dn;
        }
    }
}
