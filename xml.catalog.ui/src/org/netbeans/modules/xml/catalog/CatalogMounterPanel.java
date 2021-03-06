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
package org.netbeans.modules.xml.catalog;

import java.awt.*;
import java.beans.*;

import javax.swing.*;
import javax.swing.event.*;
import org.openide.util.NbBundle;

/**
 * Panel for selecting catalog providers showing customizer for selected one.
 *
 * @author  Petr Kuzel
 */
final class CatalogMounterPanel extends javax.swing.JPanel implements ChangeListener {

    /** Serial Version UID */
    private static final long serialVersionUID =-1208422697106159058L;

    private CatalogMounterModel model;

    /** Creates new form CatalogMounterPanel */
    public CatalogMounterPanel(CatalogMounterModel model) {
        this.model = model;
        initComponents();
        initAccessibility();
        this.catalogLabel.setDisplayedMnemonic(NbBundle.getMessage(
                CatalogMounterPanel.class,"CatalogMounterPanel.catalogLabel.mne").charAt(0)); // NOI18N                
        catalogComboBox.setModel(model.getCatalogComboBoxModel());
        updateCatalogPanel();
        
        model.addChangeListener(this);
    }

    /**
     * Compute preffered dimension for combo with
     * particulal number of columns
     */
    private Dimension comboSize(int columns) {
        JTextField template = new JTextField();
        template.setColumns(columns);
        return template.getPreferredSize();
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        catalogLabel = new javax.swing.JLabel();
        catalogComboBox = new javax.swing.JComboBox();
        parentPanel = new javax.swing.JPanel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        setMinimumSize(new java.awt.Dimension(380, 100));
        setLayout(new java.awt.GridBagLayout());

        catalogLabel.setLabelFor(catalogComboBox);
        catalogLabel.setText(NbBundle.getMessage(CatalogMounterPanel.class, "CatalogMounterPanel.catalogLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        add(catalogLabel, gridBagConstraints);

        catalogComboBox.setPreferredSize(comboSize(40));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 11);
        add(catalogComboBox, gridBagConstraints);

        parentPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 11);
        add(parentPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void updateCatalogPanel() {
        Customizer cust = model.getCatalogCustomizer();
        cust.setObject(model.getCatalog());
        invalidate();
        parentPanel.removeAll();
        Component catalogPanel = (Component) cust;
        parentPanel.add(catalogPanel, BorderLayout.CENTER);
        validate();
    }
    
    public void stateChanged(ChangeEvent e) {
        updateCatalogPanel();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox catalogComboBox;
    private javax.swing.JLabel catalogLabel;
    private javax.swing.JPanel parentPanel;
    // End of variables declaration//GEN-END:variables

    private void initAccessibility(){
        catalogComboBox.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CatalogMounterPanel.class, "ACSD_catalogComboBox"));
        this.getAccessibleContext().setAccessibleName(NbBundle.getMessage(CatalogMounterPanel.class, "ACSN_CatalogMounterPanel"));
        this.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CatalogMounterPanel.class, "ACSD_CatalogMounterPanel"));
        
    }
    
}
