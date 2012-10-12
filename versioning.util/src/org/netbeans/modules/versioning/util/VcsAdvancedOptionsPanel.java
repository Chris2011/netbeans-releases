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

package org.netbeans.modules.versioning.util;

import java.awt.BorderLayout;
import java.util.HashMap;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import org.netbeans.api.options.OptionsDisplayer;
import org.netbeans.spi.options.OptionsPanelController;

/**
 *
 * @author  pbuzek
 */
@OptionsPanelController.Keywords(keywords={"systems", "versioning", "#KW_VersioningOptions"}, location=OptionsDisplayer.ADVANCED, tabTitle="Versioning")
public class VcsAdvancedOptionsPanel extends javax.swing.JPanel {

    HashMap<String, JComponent> versioningPanels = new HashMap<String, JComponent>();
    
    /** Creates new form VcsAdvancedOptionsPanel */
    public VcsAdvancedOptionsPanel() {
        initComponents();
    }
    
    public void addPanel(String name, JComponent component) {
        if (versioningPanels.containsKey(name)) {
            versioningSystemsList.setSelectedValue(name, true);
        } else {
            ((DefaultListModel) versioningSystemsList.getModel()).addElement(name);
            versioningPanels.put(name, component);
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        versioningSystemsList = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        containerPanel = new javax.swing.JPanel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 0, 0, 0));

        versioningSystemsList.setModel(new DefaultListModel());
        versioningSystemsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        versioningSystemsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                versioningSystemsListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(versioningSystemsList);
        versioningSystemsList.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(VcsAdvancedOptionsPanel.class, "VcsAdvancedOptionsPanel.versioningSystemsList.AccessibleContext.accessibleDescription")); // NOI18N

        jLabel1.setLabelFor(versioningSystemsList);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getBundle(VcsAdvancedOptionsPanel.class).getString("LBL_VersioningSystems")); // NOI18N

        containerPanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(containerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(containerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

private void versioningSystemsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_versioningSystemsListValueChanged
    containerPanel.setVisible(false);
    containerPanel.removeAll();
    containerPanel.add(versioningPanels.get((String) versioningSystemsList.getSelectedValue()), BorderLayout.CENTER);
    containerPanel.setVisible(true);
}//GEN-LAST:event_versioningSystemsListValueChanged
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel containerPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList versioningSystemsList;
    // End of variables declaration//GEN-END:variables
    
}
