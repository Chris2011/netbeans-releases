/*
 * VcsAdvancedOptionsPanel.java
 *
 * Created on June 13, 2007, 3:21 PM
 */

package org.netbeans.modules.versioning.util;

import java.awt.CardLayout;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;

/**
 *
 * @author  pbuzek
 */
public class VcsAdvancedOptionsPanel extends javax.swing.JPanel {
    
    /** Creates new form VcsAdvancedOptionsPanel */
    public VcsAdvancedOptionsPanel() {
        initComponents();
    }
    
    public void addPanel(String name, JComponent component) {
        ((DefaultListModel)versioningSystemsList.getModel()).addElement(name);
        containerPanel.add(name, component);
        if (versioningSystemsList.getModel().getSize() == 1) {
            versioningSystemsList.setSelectedIndex(0);
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

        containerPanel.setLayout(new java.awt.CardLayout());

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
    ((CardLayout) containerPanel.getLayout()).show(
            containerPanel, (String) versioningSystemsList.getSelectedValue());
}//GEN-LAST:event_versioningSystemsListValueChanged
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel containerPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList versioningSystemsList;
    // End of variables declaration//GEN-END:variables
    
}
