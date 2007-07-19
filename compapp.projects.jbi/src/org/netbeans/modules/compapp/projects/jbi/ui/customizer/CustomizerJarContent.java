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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.compapp.projects.jbi.ui.customizer;

import java.awt.Dialog;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/** 
 * Customizer for WAR packaging.
 */
public class CustomizerJarContent extends JPanel 
        implements JbiJarCustomizer.Panel, HelpCtx.Provider {

    private Dialog dialog;
    JbiProjectProperties jbiProperties;
    private VisualPropertySupport vps;
    private VisualArchiveIncludesSupport vas;
    private ActionListener actionListener;
    
    /** Creates new form CustomizerCompile */
    public CustomizerJarContent(JbiProjectProperties jbiProperties) {
        initComponents();
        this.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(CustomizerJarContent.class, "ACS_CustomizeWAR_A11YDesc")); // NOI18N

        this.jbiProperties = jbiProperties;
        vps = new VisualPropertySupport(jbiProperties);
        vas = new VisualArchiveIncludesSupport(jbiProperties,
                                            jTableComp,
                                            jTableAddContent,
                                            jButtonUpdateComponents,
                                            jButtonAddProject,
                                            jButtonRemoveProject);
    }

    public void initValues() {
        //vps.register(jTextFieldFileName, JbiProjectProperties.DIST_JAR);
        jTextFieldFileName.setDocument(jbiProperties.DIST_JAR_MODEL);
        vas.initTableValues();
        vps.register(vas, JbiProjectProperties.JBI_CONTENT_ADDITIONAL);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelFileName = new javax.swing.JLabel();
        jTextFieldFileName = new javax.swing.JTextField();
        jLabelExContent = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableComp = new javax.swing.JTable();
        jButtonUpdateComponents = new javax.swing.JButton();
        jLabelAddContent = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableAddContent = new javax.swing.JTable();
        jButtonAddProject = new javax.swing.JButton();
        jButtonRemoveProject = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabelFileName.setLabelFor(jTextFieldFileName);
        org.openide.awt.Mnemonics.setLocalizedText(jLabelFileName, org.openide.util.NbBundle.getMessage(CustomizerJarContent.class, "LBL_CustomizeWAR_FileName_JLabel")); // NOI18N

        jTextFieldFileName.setEditable(false);

        jLabelExContent.setLabelFor(jTableComp);
        org.openide.awt.Mnemonics.setLocalizedText(jLabelExContent, org.openide.util.NbBundle.getMessage(CustomizerJarContent.class, "LBL_CustomizeWAR_Content_JLabel")); // NOI18N

        jTableComp.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, "SE", "BPEL Service Engine"},
                {null, "BC", "HTTP SOAP Binding Component"}
            },
            new String [] {
                " ", "Type", "Title"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTableComp);

        org.openide.awt.Mnemonics.setLocalizedText(jButtonUpdateComponents, org.openide.util.NbBundle.getMessage(CustomizerJarContent.class, "LBL_CustomizeWAR_AddFilter_JButton")); // NOI18N

        jLabelAddContent.setLabelFor(jTableAddContent);
        org.openide.awt.Mnemonics.setLocalizedText(jLabelAddContent, org.openide.util.NbBundle.getMessage(CustomizerJarContent.class, "LBL_CustomizeWAR_AddContent_JLabel")); // NOI18N

        jTableAddContent.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(jTableAddContent);
        jTableAddContent.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CustomizerJarContent.class, "LBL_AACH_ProjectJarFiles_JLabel")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButtonAddProject, org.openide.util.NbBundle.getMessage(CustomizerJarContent.class, "LBL_CustomizeWAR_AddProject_JButton")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButtonRemoveProject, org.openide.util.NbBundle.getMessage(CustomizerJarContent.class, "LBL_CustomizeWAR_Remove_JButton")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                            .add(jLabelExContent)
                            .add(jLabelAddContent))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                .add(jButtonAddProject)
                                .add(jButtonRemoveProject))
                            .add(jButtonUpdateComponents)))
                    .add(layout.createSequentialGroup()
                        .add(jLabelFileName)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jTextFieldFileName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {jButtonAddProject, jButtonRemoveProject, jButtonUpdateComponents}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelFileName)
                    .add(jTextFieldFileName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(11, 11, 11)
                .add(jLabelExContent)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jButtonUpdateComponents)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE))
                .add(11, 11, 11)
                .add(jLabelAddContent)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(jButtonAddProject)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButtonRemoveProject)))
                .addContainerGap())
        );

        jTextFieldFileName.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(CustomizerJarContent.class).getString("ACS_CustomizeWAR_FileName_A11YDesc")); // NOI18N
        jButtonUpdateComponents.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CustomizerJarContent.class, "ACS_CustomizeWAR_AddFilter_A11YDesc")); // NOI18N
        jButtonAddProject.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CustomizerJarContent.class, "ACS_CustomizeWAR_AddProject_A11YDesc")); // NOI18N
        jButtonRemoveProject.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CustomizerJarContent.class, "ACS_CustomizeWAR_AdditionalRemove_A11YDesc")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAddProject;
    private javax.swing.JButton jButtonRemoveProject;
    private javax.swing.JButton jButtonUpdateComponents;
    private javax.swing.JLabel jLabelAddContent;
    private javax.swing.JLabel jLabelExContent;
    private javax.swing.JLabel jLabelFileName;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableAddContent;
    private javax.swing.JTable jTableComp;
    private javax.swing.JTextField jTextFieldFileName;
    // End of variables declaration//GEN-END:variables

    /** Help context where to find more about the paste type action.
     * @return the help context for this action
     */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(CustomizerJarContent.class);
    }    
}
