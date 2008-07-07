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
package org.netbeans.modules.subversion.options;

import org.openide.util.Utilities;

/**
 *
 * @author Tomas Stupka
 */
public class SvnOptionsPanel extends javax.swing.JPanel {

    /** Creates new form SvnOptionsPanel */
    public SvnOptionsPanel() {
        initComponents();
        if(Utilities.isWindows()) {
            jLabel5.setText(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.jLabel5.windows.text"));
        } else {
            jLabel5.setText(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.jLabel5.unix.text"));
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();

        jLabel1.setLabelFor(executablePathTextField);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.jLabel1.text")); // NOI18N

        jLabel5.setText(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.jLabel5.windows.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(browseButton, org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.browseButton.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(manageConnSettingsButton, org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.manageConnSettingsButton.text")); // NOI18N

        jLabel3.setText(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.jLabel3.text")); // NOI18N

        jLabel6.setText(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.jLabel6.text")); // NOI18N

        jLabel4.setText(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.jLabel4.text")); // NOI18N

        jLabel2.setLabelFor(annotationTextField);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.jLabel2.text")); // NOI18N

        annotationTextField.setText(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.annotationTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(addButton, org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.addButton.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(manageLabelsButton, org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.manageLabelsButton.text")); // NOI18N

        jLabel7.setText(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.jLabel7.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(0, 0, 0)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(12, 12, 12)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(jLabel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                            .add(layout.createSequentialGroup()
                                .add(jLabel2)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(annotationTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
                                .add(7, 7, 7)))
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, addButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, manageLabelsButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .add(layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel5)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(executablePathTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(browseButton))))
                    .add(layout.createSequentialGroup()
                        .add(jLabel3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(12, 12, 12)
                        .add(jLabel6)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 59, Short.MAX_VALUE)
                        .add(manageConnSettingsButton))
                    .add(layout.createSequentialGroup()
                        .add(jLabel4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jSeparator2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(browseButton)
                    .add(executablePathTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel5)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel3)
                    .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(manageConnSettingsButton))
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(18, 18, 18)
                        .add(jLabel4))
                    .add(layout.createSequentialGroup()
                        .add(24, 24, 24)
                        .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(annotationTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(addButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7)
                    .add(manageLabelsButton))
                .addContainerGap())
        );

        executablePathTextField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "ACSN_SvnOptionsPanel.executablePathTextField.text")); // NOI18N
        executablePathTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "ACSD_SvnOptionsPanel.executablePathTextField.text")); // NOI18N
        browseButton.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "ACSN_SvnOptionsPanel.browseButton.text")); // NOI18N
        browseButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "ACSD_SvnOptionsPanel.browseButton.text")); // NOI18N
        manageConnSettingsButton.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "ACSN_SvnOptionsPanel.manageConnSettingsButton.text")); // NOI18N
        manageConnSettingsButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "ACSD_SvnOptionsPanel.manageConnSettingsButton.text")); // NOI18N
        annotationTextField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "ACSN_SvnOptionsPanel.annotationTextField.text")); // NOI18N
        annotationTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "ACSD_SvnOptionsPanel.annotationTextField.text")); // NOI18N
        addButton.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "ACSN_SvnOptionsPanel.addButton.text")); // NOI18N
        addButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "ACSD_SvnOptionsPanel.addButton.text")); // NOI18N
        manageLabelsButton.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "ACSN_SvnOptionsPanel.manageLabelsButton.text")); // NOI18N
        manageLabelsButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "ACSD_SvnOptionsPanel.manageLabelsButton.text")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    final javax.swing.JButton addButton = new javax.swing.JButton();
    final javax.swing.JTextField annotationTextField = new javax.swing.JTextField();
    final javax.swing.JButton browseButton = new javax.swing.JButton();
    final javax.swing.JTextField executablePathTextField = new javax.swing.JTextField();
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    final javax.swing.JButton manageConnSettingsButton = new javax.swing.JButton();
    final javax.swing.JButton manageLabelsButton = new javax.swing.JButton();
    // End of variables declaration//GEN-END:variables

}
