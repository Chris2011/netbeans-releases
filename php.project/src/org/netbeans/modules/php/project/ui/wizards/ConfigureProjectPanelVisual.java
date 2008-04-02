/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.php.project.ui.wizards;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.openide.util.NbBundle;

class ConfigureProjectPanelVisual extends JPanel {

    private static final long serialVersionUID = 5104232236340989L;

    private LocationPanelVisual locationPanelVisual;
    private OptionsPanelVisual optionsPanelVisual;


    ConfigureProjectPanelVisual(ConfigureProjectPanel wizardPanel) {
        // Provide a name in the title bar.
        setName(NbBundle.getMessage(ConfigureProjectPanelVisual.class, "LBL_ProjectTitleName"));
        putClientProperty("WizardPanel_contentSelectedIndex", 0); // NOI18N
        // Step name (actually the whole list for reference).
        putClientProperty("WizardPanel_contentData", wizardPanel.getSteps()); // NOI18N

        initComponents();

        // location
        locationPanelVisual = new LocationPanelVisual();
        locationContainer.add(BorderLayout.NORTH, locationPanelVisual);

        // options
        optionsPanelVisual = new OptionsPanelVisual(null);
        optionsContainer.add(BorderLayout.NORTH, optionsPanelVisual);
    }

    LocationPanelVisual getLocationPanelVisual() {
        return locationPanelVisual;
    }

    OptionsPanelVisual getOptionsPanelVisual() {
        return optionsPanelVisual;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        locationContainer = new javax.swing.JPanel();
        separator1 = new javax.swing.JSeparator();
        optionsContainer = new javax.swing.JPanel();

        setPreferredSize(new java.awt.Dimension(0, 52));
        setRequestFocusEnabled(false);
        setLayout(new java.awt.GridBagLayout());

        locationContainer.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(locationContainer, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 7, 0);
        add(separator1, gridBagConstraints);

        optionsContainer.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(optionsContainer, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel locationContainer;
    private javax.swing.JPanel optionsContainer;
    private javax.swing.JSeparator separator1;
    // End of variables declaration//GEN-END:variables

}
