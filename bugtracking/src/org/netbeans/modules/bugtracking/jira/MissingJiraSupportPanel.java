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

/*
 * MissingClientPanel.java
 *
 * Created on Jul 9, 2008, 4:55:42 PM
 */

package org.netbeans.modules.bugtracking.jira;

import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.GroupLayout.SequentialGroup;

/**
 *
 * @author Tomas Stupka
 */
public class MissingJiraSupportPanel extends javax.swing.JPanel{

    final javax.swing.JButton downloadButton = new javax.swing.JButton();
    final javax.swing.JCheckBox forceGlobalCheckBox = new javax.swing.JCheckBox();
    private javax.swing.JLabel jLabel1;
    
    /** Creates new form MissingClientPanel */
    public MissingJiraSupportPanel(boolean containerGaps) {
        initComponents(containerGaps);
    }

    void setMessage(String msg) {
        jLabel1.setText(msg);
        forceGlobalCheckBox.setVisible(false);
    }

    private void initComponents(boolean containerGaps) {

        jLabel1 = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(MissingJiraSupportPanel.class, "MissingJiraSupportPanel.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(downloadButton, org.openide.util.NbBundle.getMessage(MissingJiraSupportPanel.class, "MissingJiraSupportPanel.downloadButton.text")); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(forceGlobalCheckBox, org.openide.util.NbBundle.getMessage(MissingJiraSupportPanel.class, "MissingJiraSupportPanel.forceGlobalCheckBox.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(createSequentialGroup(layout, containerGaps)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(forceGlobalCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 142, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(downloadButton))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, createSequentialGroup(layout, containerGaps)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(forceGlobalCheckBox)
                    .add(downloadButton))
                .addContainerGap(30, Short.MAX_VALUE)
                    )
        );
    }

    private SequentialGroup createSequentialGroup(GroupLayout layout, boolean containerGaps) {
        SequentialGroup sg = layout.createSequentialGroup();
        if (containerGaps) {
            sg.addContainerGap();
        }
        return sg;
    }
    
}
