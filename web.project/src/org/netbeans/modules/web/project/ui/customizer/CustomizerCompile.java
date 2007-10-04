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

package org.netbeans.modules.web.project.ui.customizer;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

public class CustomizerCompile extends javax.swing.JPanel implements HelpCtx.Provider {

    /** Creates new form CustomizerCompile */
    public CustomizerCompile(WebProjectProperties uiProperties) {
        initComponents();

        uiProperties.JAVAC_DEPRECATION_MODEL.setMnemonic( jCheckBoxDeprecation.getMnemonic() );
        jCheckBoxDeprecation.setModel( uiProperties.JAVAC_DEPRECATION_MODEL );
        uiProperties.JAVAC_DEBUG_MODEL.setMnemonic( jCheckBoxDebugInfo.getMnemonic() );
        jCheckBoxDebugInfo.setModel( uiProperties.JAVAC_DEBUG_MODEL );
        uiProperties.COMPILE_JSP_MODEL.setMnemonic( jCheckBoxCompileJSP.getMnemonic() );
        jCheckBoxCompileJSP.setModel( uiProperties.COMPILE_JSP_MODEL );
        additionalJavacParamsJTextField.setDocument( uiProperties.JAVAC_COMPILER_ARG_MODEL );                 

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabelOptions = new javax.swing.JLabel();
        jCheckBoxDebugInfo = new javax.swing.JCheckBox();
        jCheckBoxDeprecation = new javax.swing.JCheckBox();
        additionalJavacParamsJLabel = new javax.swing.JLabel();
        additionalJavacParamsJTextField = new javax.swing.JTextField();
        additionalJavacParamsExampleJLabel = new javax.swing.JLabel();
        jCheckBoxCompileJSP = new javax.swing.JCheckBox();

        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabelOptions, org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Compiler_Options_JLabel"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(jLabelOptions, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxDebugInfo, org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Compiler_DebugInfo_JCheckBox"));
        jCheckBoxDebugInfo.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 5, 0);
        add(jCheckBoxDebugInfo, gridBagConstraints);
        jCheckBoxDebugInfo.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("ACS_CustomizeCompile_Debugging__A11YDesc"));

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxDeprecation, org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Compiler_Deprecation_JCheckBox"));
        jCheckBoxDeprecation.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 11, 0);
        add(jCheckBoxDeprecation, gridBagConstraints);
        jCheckBoxDeprecation.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("ACS_CustomizeCompile_Deprecated_A11YDesc"));

        additionalJavacParamsJLabel.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage (CustomizerCompile.class,"MNE_AdditionalCompilerOptions").charAt(0));
        additionalJavacParamsJLabel.setLabelFor(additionalJavacParamsJTextField);
        org.openide.awt.Mnemonics.setLocalizedText(additionalJavacParamsJLabel, org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_AdditionalCompilerOptions"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 12);
        add(additionalJavacParamsJLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(additionalJavacParamsJTextField, gridBagConstraints);
        additionalJavacParamsJTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage (CustomizerCompile.class,"AD_AdditionalCompilerOptions"));

        org.openide.awt.Mnemonics.setLocalizedText(additionalJavacParamsExampleJLabel, org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_AdditionalCompilerOptionsExample"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        add(additionalJavacParamsExampleJLabel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxCompileJSP, NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Compiler_CompileJSP_JCheckBox"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        add(jCheckBoxCompileJSP, gridBagConstraints);
        jCheckBoxCompileJSP.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("ACS_CustomizeCompile_TestCompile_A11YDesc"));

    }
    // </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel additionalJavacParamsExampleJLabel;
    private javax.swing.JLabel additionalJavacParamsJLabel;
    private javax.swing.JTextField additionalJavacParamsJTextField;
    private javax.swing.JCheckBox jCheckBoxCompileJSP;
    private javax.swing.JCheckBox jCheckBoxDebugInfo;
    private javax.swing.JCheckBox jCheckBoxDeprecation;
    private javax.swing.JLabel jLabelOptions;
    // End of variables declaration//GEN-END:variables

    /** Help context where to find more about the paste type action.
     * @return the help context for this action
     */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(CustomizerCompile.class);
    }

}
