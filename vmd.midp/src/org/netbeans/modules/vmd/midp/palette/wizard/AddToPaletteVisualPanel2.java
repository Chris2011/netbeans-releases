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
 */package org.netbeans.modules.vmd.midp.palette.wizard;

import org.openide.util.NbBundle;

import javax.swing.*;
import java.util.*;
import java.util.List;
import java.awt.*;

/**
 * @author David Kaspar
 */
public final class AddToPaletteVisualPanel2 extends JPanel {

    private Collection<ComponentInstaller.Item> items;

    public AddToPaletteVisualPanel2() {
        initComponents();
        list.setCellRenderer (new ItemRenderer ());
    }

    public String getName() {
        return NbBundle.getMessage(AddToPaletteVisualPanel2.class, "TITLE_SelectClasses"); // NOI18N
    }

    public void setItems (Collection<ComponentInstaller.Item> items) {
        this.items = items;
        reload ();
    }

    public void reload () {
        final ArrayList<ComponentInstaller.Item> lst;
        if (cLibraries.isSelected ())
            lst = new ArrayList<ComponentInstaller.Item> (items);
        else {
            lst = new ArrayList<ComponentInstaller.Item> ();
            for (ComponentInstaller.Item item : items)
                if (item.isInSource ())
                    lst.add (item);
        }
        Collections.sort (lst, new Comparator<ComponentInstaller.Item>() {
            public int compare (ComponentInstaller.Item o1, ComponentInstaller.Item o2) {
                return o1.getFQN ().compareTo (o2.getFQN ());
            }
        });
        list.setModel (new AbstractListModel() {
            public int getSize () { return lst.size (); }
            public Object getElementAt (int i) { return lst.get (i); }
        });
        if (lst.size () > 0)
            list.setSelectionInterval (0, lst.size () - 1);
    }

    public List<ComponentInstaller.Item> getSelectedItems () {
        ArrayList<ComponentInstaller.Item> lst = new ArrayList<ComponentInstaller.Item> ();
        for (Object o : list.getSelectedValues ())
            lst.add ((ComponentInstaller.Item) o);
        return lst;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        list = new javax.swing.JList();
        bSelectAll = new javax.swing.JButton();
        bDeselectAll = new javax.swing.JButton();
        cLibraries = new javax.swing.JCheckBox();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, NbBundle.getMessage(AddToPaletteVisualPanel2.class, "LBL_FoundClasses")); // NOI18N

        jScrollPane1.setViewportView(list);

        org.openide.awt.Mnemonics.setLocalizedText(bSelectAll, NbBundle.getMessage(AddToPaletteVisualPanel2.class, "LBL_SelectAll")); // NOI18N
        bSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSelectAllActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(bDeselectAll, NbBundle.getMessage(AddToPaletteVisualPanel2.class, "LBL_DeselectAll")); // NOI18N
        bDeselectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bDeselectAllActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(cLibraries, NbBundle.getMessage(AddToPaletteVisualPanel2.class, "LBL_ShowAll")); // NOI18N
        cLibraries.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cLibraries.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cLibrariesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addContainerGap(29, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(bSelectAll)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bDeselectAll)
                        .addGap(10, 10, 10))
                    .addComponent(cLibraries)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cLibraries)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bDeselectAll)
                    .addComponent(bSelectAll))
                .addContainerGap())
        );

        jLabel1.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(AddToPaletteVisualPanel2.class, "ACCESSIBLE_NAME_jLabel1")); // NOI18N
        jLabel1.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(AddToPaletteVisualPanel2.class, "ACCESSIBLE_DESCRIPTION_jLabel1")); // NOI18N
        bSelectAll.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(AddToPaletteVisualPanel2.class, "ACCESSIBLE_NAME_bSelectAll")); // NOI18N
        bSelectAll.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(AddToPaletteVisualPanel2.class, "ACCESSIBLE_DESCRIPTION_bSelectAll")); // NOI18N
        bDeselectAll.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(AddToPaletteVisualPanel2.class, "ACCESSIBLE_NAME_bDesceletAll")); // NOI18N
        bDeselectAll.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(AddToPaletteVisualPanel2.class, "ACCESSIBLE_DESCRIPTION_bDesceletAll")); // NOI18N
        cLibraries.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(AddToPaletteVisualPanel2.class, "ACCESSIBLE_NAME_jCheckBox")); // NOI18N
        cLibraries.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(AddToPaletteVisualPanel2.class, "ACCESSIBLE_DESCRIPTION_jCheckBox")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

private void cLibrariesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cLibrariesActionPerformed
    reload ();
}//GEN-LAST:event_cLibrariesActionPerformed

private void bDeselectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bDeselectAllActionPerformed
    list.setSelectedIndices(new int[0]);
}//GEN-LAST:event_bDeselectAllActionPerformed

private void bSelectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSelectAllActionPerformed
    int size = list.getModel().getSize();
    if (size > 0)
        list.setSelectionInterval(0, size - 1);
}//GEN-LAST:event_bSelectAllActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bDeselectAll;
    private javax.swing.JButton bSelectAll;
    private javax.swing.JCheckBox cLibraries;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList list;
    // End of variables declaration//GEN-END:variables

    private static class ItemRenderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent (JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            return super.getListCellRendererComponent (list, ((ComponentInstaller.Item) value).getFQN (), index, isSelected, cellHasFocus);
        }

    }

}

