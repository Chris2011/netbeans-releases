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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-200? Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.web.jsf.wizards;

import java.io.InputStream;
import org.openide.util.HelpCtx;

/**
 *
 * @author  Petr Pisl
 */
public class TemplatePanelVisual extends javax.swing.JPanel implements HelpCtx.Provider{

    private static final String RESOURCES_FOLDER="org/netbeans/modules/web/jsf/facelets/resources/templates/";  //NOI18N
    /** Creates new form TemplatePanelVisual */
    public TemplatePanelVisual() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgTemplates = new javax.swing.ButtonGroup();
        bgLayout = new javax.swing.ButtonGroup();
        jpTemplateChooser = new javax.swing.JPanel();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jRadioButton5 = new javax.swing.JRadioButton();
        jRadioButton6 = new javax.swing.JRadioButton();
        jRadioButton7 = new javax.swing.JRadioButton();
        jRadioButton8 = new javax.swing.JRadioButton();
        jRadioButton9 = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton10 = new javax.swing.JRadioButton();

        jpTemplateChooser.setLayout(new java.awt.GridLayout(2, 0, 10, 10));

        bgTemplates.add(jRadioButton2);
        jRadioButton2.setSelected(true);
        jRadioButton2.setActionCommand("1");
        jRadioButton2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButton2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jRadioButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/web/jsf/facelets/resources/template1-unselected.png"))); // NOI18N
        jRadioButton2.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/web/jsf/facelets/resources/template1-selected.png"))); // NOI18N
        jpTemplateChooser.add(jRadioButton2);

        bgTemplates.add(jRadioButton3);
        jRadioButton3.setActionCommand("2");
        jRadioButton3.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButton3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jRadioButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/web/jsf/facelets/resources/template2-unselected.png"))); // NOI18N
        jRadioButton3.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/web/jsf/facelets/resources/template2-selected.png"))); // NOI18N
        jpTemplateChooser.add(jRadioButton3);

        bgTemplates.add(jRadioButton4);
        jRadioButton4.setActionCommand("3");
        jRadioButton4.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButton4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jRadioButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/web/jsf/facelets/resources/template8-unselected.png"))); // NOI18N
        jRadioButton4.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/web/jsf/facelets/resources/template8-selected.png"))); // NOI18N
        jpTemplateChooser.add(jRadioButton4);

        bgTemplates.add(jRadioButton5);
        jRadioButton5.setActionCommand("4");
        jRadioButton5.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButton5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jRadioButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/web/jsf/facelets/resources/template7-unselected.png"))); // NOI18N
        jRadioButton5.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/web/jsf/facelets/resources/template7-selected.png"))); // NOI18N
        jpTemplateChooser.add(jRadioButton5);

        bgTemplates.add(jRadioButton6);
        jRadioButton6.setActionCommand("5");
        jRadioButton6.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButton6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jRadioButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/web/jsf/facelets/resources/template3-unselected.png"))); // NOI18N
        jRadioButton6.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/web/jsf/facelets/resources/template3-selected.png"))); // NOI18N
        jpTemplateChooser.add(jRadioButton6);

        bgTemplates.add(jRadioButton7);
        jRadioButton7.setActionCommand("6");
        jRadioButton7.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButton7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jRadioButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/web/jsf/facelets/resources/template6-unselected.png"))); // NOI18N
        jRadioButton7.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/web/jsf/facelets/resources/template6-selected.png"))); // NOI18N
        jpTemplateChooser.add(jRadioButton7);

        bgTemplates.add(jRadioButton8);
        jRadioButton8.setActionCommand("7");
        jRadioButton8.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButton8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jRadioButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/web/jsf/facelets/resources/template4-unselected.png"))); // NOI18N
        jRadioButton8.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/web/jsf/facelets/resources/template4-selected.png"))); // NOI18N
        jpTemplateChooser.add(jRadioButton8);

        bgTemplates.add(jRadioButton9);
        jRadioButton9.setActionCommand("8");
        jRadioButton9.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButton9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jRadioButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/web/jsf/facelets/resources/template5-unselected.png"))); // NOI18N
        jRadioButton9.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/web/jsf/facelets/resources/template5-selected.png"))); // NOI18N
        jpTemplateChooser.add(jRadioButton9);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/web/jsf/wizards/Bundle"); // NOI18N
        jLabel1.setText(bundle.getString("LBL_Layout")); // NOI18N

        bgLayout.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        jRadioButton1.setText(bundle.getString("LBL_CSS_Layout")); // NOI18N
        jRadioButton1.setActionCommand("css");
        jRadioButton1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        bgLayout.add(jRadioButton10);
        jRadioButton10.setText(bundle.getString("LBL_Table_Layout")); // NOI18N
        jRadioButton10.setActionCommand("table");
        jRadioButton10.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton10)
                .addGap(201, 201, 201))
            .addComponent(jpTemplateChooser, javax.swing.GroupLayout.DEFAULT_SIZE, 623, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jRadioButton1)
                    .addComponent(jRadioButton10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpTemplateChooser, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgLayout;
    private javax.swing.ButtonGroup bgTemplates;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton10;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JRadioButton jRadioButton6;
    private javax.swing.JRadioButton jRadioButton7;
    private javax.swing.JRadioButton jRadioButton8;
    private javax.swing.JRadioButton jRadioButton9;
    private javax.swing.JPanel jpTemplateChooser;
    // End of variables declaration//GEN-END:variables

    public HelpCtx getHelpCtx() {
        return new HelpCtx(TemplatePanelVisual.class);
    }
    
    InputStream getTemplate(){
        String path = RESOURCES_FOLDER+"template-";  //NOI18N
        path = path + bgLayout.getSelection().getActionCommand() + "-"; //NOI18N
        path = path + bgTemplates.getSelection().getActionCommand() + ".xhtml";          //NOI18N
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(path);
        return is;
    }
    
    InputStream getDefaultCSS(){
        String path = RESOURCES_FOLDER+ "default.css";  //NOI18N
        return this.getClass().getClassLoader().getResourceAsStream(path);
    }
    
    InputStream getLayoutCSS(){
        String path = RESOURCES_FOLDER;
        path = path + bgLayout.getSelection().getActionCommand() + "Layout.css";    //NOI18N
        return this.getClass().getClassLoader().getResourceAsStream(path);
    }
    
    String getLayoutFileName(){
        String name = bgLayout.getSelection().getActionCommand() + "Layout";    //NOI18N
        return name;
    }
}
