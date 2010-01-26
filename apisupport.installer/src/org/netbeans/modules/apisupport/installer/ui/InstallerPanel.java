/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */


package org.netbeans.modules.apisupport.installer.ui;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.openide.util.NbBundle;

/**
 *
 * @author Dmitry Lipin
 * @author Alexei Mokeev
 */
public class InstallerPanel extends javax.swing.JPanel {

    private SuiteInstallerProjectProperties installerProps;

    /** Creates new form InstallerPanel */
    public InstallerPanel(SuiteInstallerProjectProperties props) {
        this.installerProps = props;
        initComponents();

        jCheckBox1.setModel(installerProps.windowsModel);
        jCheckBox2.setModel(installerProps.linuxModel);
        jCheckBox3.setModel(installerProps.macModel);
        jCheckBox4.setModel(installerProps.solarisModel);
        jCheckBox5.setModel(installerProps.pack200Model);
        
        licenseComboBox.setModel(installerProps.licenseModel);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jCheckBox4 = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        licenseLabel = new javax.swing.JLabel();
        licenseComboBox = new javax.swing.JComboBox();
        jCheckBox5 = new javax.swing.JCheckBox();
        jSeparator2 = new javax.swing.JSeparator();
        pack200Info = new javax.swing.JLabel();

        jLabel2.setText(org.openide.util.NbBundle.getMessage(InstallerPanel.class, "InstallerPanel.Platforms.Label")); // NOI18N

        jCheckBox1.setText(org.openide.util.NbBundle.getMessage(InstallerPanel.class, "InstallerPanel.OSLabelWindows")); // NOI18N

        jCheckBox2.setText(org.openide.util.NbBundle.getMessage(InstallerPanel.class, "InstallerPanel.OSLabelLinux")); // NOI18N

        jCheckBox3.setText(org.openide.util.NbBundle.getMessage(InstallerPanel.class, "InstallerPanel.OSLabelMacOS")); // NOI18N

        jCheckBox4.setText(org.openide.util.NbBundle.getMessage(InstallerPanel.class, "InstallerPanel.OSLabelSolaris")); // NOI18N

        licenseLabel.setText(org.openide.util.NbBundle.getMessage(InstallerPanel.class, "InstallerPanel.licenseLabel.text")); // NOI18N

        jCheckBox5.setText(org.openide.util.NbBundle.getMessage(InstallerPanel.class, "InstallerPanel.pack200checkBox.text")); // NOI18N

        pack200Info.setText(org.openide.util.NbBundle.getMessage(InstallerPanel.class, "InstallerPanel.Pack200.Description.Text")); // NOI18N
        pack200Info.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        pack200Info.setFocusable(false);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1122, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(jLabel2)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jCheckBox1)
                                    .add(jCheckBox2)
                                    .add(jCheckBox3)
                                    .add(jCheckBox4)))
                            .add(layout.createSequentialGroup()
                                .add(licenseLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(licenseComboBox, 0, 941, Short.MAX_VALUE)))
                        .add(0, 0, 0))))
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jCheckBox5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1116, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(jSeparator2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1122, Short.MAX_VALUE)))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(31, 31, 31)
                .add(pack200Info, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1091, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jCheckBox1)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jCheckBox2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jCheckBox3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jCheckBox4)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(licenseLabel)
                    .add(licenseComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(7, 7, 7)
                .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jCheckBox5)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pack200Info, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                .add(99, 99, 99))
        );

        jLabel2.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(InstallerPanel.class, "InstallerPanel.Platforms.Label")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JComboBox licenseComboBox;
    private javax.swing.JLabel licenseLabel;
    private javax.swing.JLabel pack200Info;
    // End of variables declaration//GEN-END:variables

    
}
