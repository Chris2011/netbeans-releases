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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.java.hints.errors;

import java.util.prefs.Preferences;

/**
 *
 * @author Jan Lahoda
 */
public class SurroundWithTryCatchLog extends javax.swing.JPanel {

    private Preferences p;

    /** Creates new form SurroundWithTryCatchLog */
    public SurroundWithTryCatchLog(Preferences p) {
        initComponents();
        this.p = p;
        exceptions.setSelected(ErrorFixesFakeHint.isUseExceptions(p));
        logger.setSelected(ErrorFixesFakeHint.isUseLogger(p));
        rethrowRuntime.setSelected(ErrorFixesFakeHint.isRethrowAsRuntimeException(p));
        rethrow.setSelected(ErrorFixesFakeHint.isRethrow(p));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        exceptions = new javax.swing.JCheckBox();
        logger = new javax.swing.JCheckBox();
        printStackTrace = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        rethrowRuntime = new javax.swing.JCheckBox();
        rethrow = new javax.swing.JCheckBox();

        exceptions.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(exceptions, org.openide.util.NbBundle.getBundle(SurroundWithTryCatchLog.class).getString("SurroundWithTryCatchLog.exceptions.text")); // NOI18N
        exceptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exceptionsActionPerformed(evt);
            }
        });

        logger.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(logger, org.openide.util.NbBundle.getBundle(SurroundWithTryCatchLog.class).getString("SurroundWithTryCatchLog.logger.text")); // NOI18N
        logger.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loggerActionPerformed(evt);
            }
        });

        printStackTrace.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(printStackTrace, org.openide.util.NbBundle.getBundle(SurroundWithTryCatchLog.class).getString("SurroundWithTryCatchLog.printStackTrace.text")); // NOI18N
        printStackTrace.setEnabled(false);

        jLabel1.setText(org.openide.util.NbBundle.getBundle(SurroundWithTryCatchLog.class).getString("SurroundWithTryCatchLog.jLabel1.text")); // NOI18N

        rethrowRuntime.setText(org.openide.util.NbBundle.getMessage(SurroundWithTryCatchLog.class, "SurroundWithTryCatchLog.rethrowRuntime.text")); // NOI18N
        rethrowRuntime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rethrowRuntimeActionPerformed(evt);
            }
        });

        rethrow.setText(org.openide.util.NbBundle.getMessage(SurroundWithTryCatchLog.class, "SurroundWithTryCatchLog.rethrow.text")); // NOI18N
        rethrow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rethrowActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(logger)
                            .addComponent(exceptions)
                            .addComponent(rethrowRuntime)
                            .addComponent(rethrow)
                            .addComponent(printStackTrace))))
                .addContainerGap(57, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(exceptions)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(logger)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rethrowRuntime)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rethrow)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(printStackTrace)
                .addContainerGap(134, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void loggerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loggerActionPerformed
    ErrorFixesFakeHint.setUseLogger(p, logger.isSelected());
}//GEN-LAST:event_loggerActionPerformed

private void exceptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exceptionsActionPerformed
    ErrorFixesFakeHint.setUseExceptions(p, exceptions.isSelected());
}//GEN-LAST:event_exceptionsActionPerformed

private void rethrowRuntimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rethrowRuntimeActionPerformed
    ErrorFixesFakeHint.setRethrowAsRuntimeException(p, rethrowRuntime.isSelected());
}//GEN-LAST:event_rethrowRuntimeActionPerformed

private void rethrowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rethrowActionPerformed
    ErrorFixesFakeHint.setRethrow(p, rethrow.isSelected());
}//GEN-LAST:event_rethrowActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox exceptions;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JCheckBox logger;
    private javax.swing.JCheckBox printStackTrace;
    private javax.swing.JCheckBox rethrow;
    private javax.swing.JCheckBox rethrowRuntime;
    // End of variables declaration//GEN-END:variables

}
