/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.ruby.rubyproject.ui.customizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import org.netbeans.api.ruby.platform.RubyPlatform;
import org.netbeans.modules.parsing.spi.indexing.support.IndexResult;
import org.netbeans.modules.parsing.spi.indexing.support.QuerySupport.Kind;
import org.netbeans.modules.ruby.RubyIndex;
import org.netbeans.modules.ruby.RubyIndexer;
import org.netbeans.modules.ruby.platform.gems.Gem;
import org.netbeans.modules.ruby.platform.gems.GemManager;
import org.netbeans.modules.ruby.rubyproject.RubyBaseProject;
import org.netbeans.modules.ruby.rubyproject.SharedRubyProjectProperties;

/**
 *
 * @author Erno Mononen
 */
public class GemsPanel extends javax.swing.JPanel {

    private final SharedRubyProjectProperties properties;
    private final RubyBaseProject project;

    /** Creates new form GemsPanel */
    public GemsPanel(SharedRubyProjectProperties properties, RubyBaseProject project) {
        this.properties = properties;
        this.project = project;
        initComponents();
    }

    private DefaultTableModel createTableModel() {

        RubyPlatform platform = properties.getPlatform();
        if (platform == null) {
            return new DefaultTableModel();
        }
        GemManager gemManager = platform.getGemManager();
        if (gemManager == null) {
            return null;
        }
        List<Gem> gems = gemManager.getInstalledGems(new ArrayList<String>());

        Object[][] data = new Object[gems.size()][2];
        for (int i = 0; i < gems.size(); i++) {
            Gem gem = gems.get(i);
            data[i][0] = gem.getName();
            data[i][1] = gem.getInstalledVersionsAsString();
        }

        RubyIndex index = RubyIndex.get(project.getProjectDirectory());
        if (index != null) {
            Collection<String> requires = index.getRequires();
            for (String e : requires) {
                System.out.println(e);
            }
        }

        return new DefaultTableModel(data, new Object[]{"name", "version"});
//        gemsTable.getModel().

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        gemsTable = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        gemsTable.setModel(createTableModel());
        gemsTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(gemsTable);
        gemsTable.getColumnModel().getColumn(0).setHeaderValue(org.openide.util.NbBundle.getMessage(GemsPanel.class, "GemsPanel.gemsTable.columnModel.title0")); // NOI18N
        gemsTable.getColumnModel().getColumn(1).setHeaderValue(org.openide.util.NbBundle.getMessage(GemsPanel.class, "GemsPanel.gemsTable.columnModel.title1")); // NOI18N

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(GemsPanel.class, "GemsPanel.jScrollPane1.TabConstraints.tabTitle"), jScrollPane1); // NOI18N

        jTable1.setModel(createTableModel());
        jScrollPane2.setViewportView(jTable1);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(GemsPanel.class, "GemsPanel.jScrollPane2.TabConstraints.tabTitle"), jScrollPane2); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(addButton, org.openide.util.NbBundle.getMessage(GemsPanel.class, "GemsPanel.addButton.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(removeButton, org.openide.util.NbBundle.getMessage(GemsPanel.class, "GemsPanel.removeButton.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(GemsPanel.class, "GemsPanel.jButton1.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 465, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(removeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
                    .addComponent(addButton, javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addComponent(addButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeButton)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JTable gemsTable;
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton removeButton;
    // End of variables declaration//GEN-END:variables

    private static class GemTableModel extends DefaultTableModel {


    }
}
