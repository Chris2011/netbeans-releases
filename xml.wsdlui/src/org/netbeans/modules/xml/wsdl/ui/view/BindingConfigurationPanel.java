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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

/*
 * BindingAndServiceConfigurationPanel.java
 *
 * Created on August 25, 2006, 2:51 PM
 */

package org.netbeans.modules.xml.wsdl.ui.view;


import javax.swing.JTextField;



/**
 *
 * @author  radval
 */
public class BindingConfigurationPanel extends javax.swing.JPanel {
    
    /** Creates new form BindingAndServiceConfigurationPanel */
    public BindingConfigurationPanel() {
        initComponents();
        initGUI();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bindingNameLabel = new javax.swing.JLabel();
        bindingNameTextField = new javax.swing.JTextField();
        serviceNameLabel = new javax.swing.JLabel();
        serviceNameTextField = new javax.swing.JTextField();
        portNameLabel = new javax.swing.JLabel();
        servicePortTextField = new javax.swing.JTextField();
        bindingConfigurationLabel = new javax.swing.JLabel();
        bindingConfigurationPanel = new javax.swing.JPanel();

        setName("Form"); // NOI18N

        bindingNameLabel.setLabelFor(bindingNameTextField);
        org.openide.awt.Mnemonics.setLocalizedText(bindingNameLabel, org.openide.util.NbBundle.getMessage(BindingConfigurationPanel.class, "BindingConfigurationPanel.bindingNameLabel.text")); // NOI18N
        bindingNameLabel.setToolTipText(org.openide.util.NbBundle.getMessage(BindingConfigurationPanel.class, "BindingConfigurationPanel.bindingNameLabel.toolTipText")); // NOI18N
        bindingNameLabel.setName("bindingNameLabel"); // NOI18N

        bindingNameTextField.setName("bindingNameTextField"); // NOI18N

        serviceNameLabel.setLabelFor(serviceNameTextField);
        org.openide.awt.Mnemonics.setLocalizedText(serviceNameLabel, org.openide.util.NbBundle.getMessage(BindingConfigurationPanel.class, "BindingConfigurationPanel.serviceNameLabel.text")); // NOI18N
        serviceNameLabel.setToolTipText(org.openide.util.NbBundle.getMessage(BindingConfigurationPanel.class, "BindingConfigurationPanel.serviceNameLabel.toolTipText")); // NOI18N
        serviceNameLabel.setName("serviceNameLabel"); // NOI18N

        serviceNameTextField.setName("serviceNameTextField"); // NOI18N

        portNameLabel.setLabelFor(servicePortTextField);
        org.openide.awt.Mnemonics.setLocalizedText(portNameLabel, org.openide.util.NbBundle.getMessage(BindingConfigurationPanel.class, "BindingConfigurationPanel.portNameLabel.text")); // NOI18N
        portNameLabel.setToolTipText(org.openide.util.NbBundle.getMessage(BindingConfigurationPanel.class, "BindingConfigurationPanel.portNameLabel.toolTipText")); // NOI18N
        portNameLabel.setName("portNameLabel"); // NOI18N

        servicePortTextField.setName("servicePortTextField"); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(bindingConfigurationLabel, org.openide.util.NbBundle.getMessage(BindingConfigurationPanel.class, "BindingConfigurationPanel.bindingConfigurationLabel.text")); // NOI18N
        bindingConfigurationLabel.setName("bindingConfigurationLabel"); // NOI18N

        bindingConfigurationPanel.setName("bindingConfigurationPanel"); // NOI18N
        bindingConfigurationPanel.setLayout(new java.awt.BorderLayout());

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, bindingConfigurationPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(bindingNameLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(bindingNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(serviceNameLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(portNameLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(serviceNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
                            .add(servicePortTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, bindingConfigurationLabel))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(bindingNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(bindingNameLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(serviceNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(serviceNameLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(servicePortTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(portNameLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(bindingConfigurationLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(bindingConfigurationPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
        
    public String getBindingName() {
        return this.bindingNameTextField.getText();
    }
    
    public void setBindingName(String bindingName) {
        this.bindingNameTextField.setText(bindingName);
    }
    
    public String getServiceName() {
        return serviceNameTextField.getText();
    }
    
    public void setServiceName(String serviceName) {
        this.serviceNameTextField.setText(serviceName);
    }
    
    public String getServicePortName() {
        return servicePortTextField.getText();
    }
    
    public void setServicePortName(String servicePortName) {
        this.servicePortTextField.setText(servicePortName);
    }
    
    public JTextField getBindingNameTextField() {
        return this.bindingNameTextField;
    }
    
    public JTextField getServiceNameTextField() {
        return this.serviceNameTextField;
    }
    
    public JTextField getServicePortTextField() {
        return this.servicePortTextField;
    }
    
    
    private void initGUI() {
        bindingConfigurationLabel.setVisible(false);
        bindingConfigurationPanel.setVisible(false);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel bindingConfigurationLabel;
    private javax.swing.JPanel bindingConfigurationPanel;
    private javax.swing.JLabel bindingNameLabel;
    private javax.swing.JTextField bindingNameTextField;
    private javax.swing.JLabel portNameLabel;
    private javax.swing.JLabel serviceNameLabel;
    private javax.swing.JTextField serviceNameTextField;
    private javax.swing.JTextField servicePortTextField;
    // End of variables declaration//GEN-END:variables
    
}
