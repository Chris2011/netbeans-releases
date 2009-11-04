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

/*
 * ContactListCellRenderer.java
 *
 * Created on Aug 3, 2009, 11:23:06 AM
 */
package org.netbeans.modules.kenai.collab.chat;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import org.openide.awt.HtmlRenderer;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Jan Becicka
 */
public class ContactListCellRenderer extends javax.swing.JPanel implements ListCellRenderer {

    /** Creates new form ContactListCellRenderer */
    public ContactListCellRenderer() {
        initComponents();
        setOpaque(true);
        buddyLabel.setOpaque(true);
        messageLabel.setOpaque(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buddyLabel = HtmlRenderer.createLabel();
        messageLabel = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 5));
        setLayout(new java.awt.BorderLayout());
        add(buddyLabel, java.awt.BorderLayout.CENTER);

        messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        add(messageLabel, java.awt.BorderLayout.EAST);
    }// </editor-fold>//GEN-END:initComponents

    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        buddyLabel.setText(value.toString());
        ContactListItem item = (ContactListItem) value;
        buddyLabel.setBorder(new EmptyBorder(0,item.getIcon()==null?19:0,0,0));
        buddyLabel.setIcon(item.getIcon());
        if (item.hasMessages()) {
            messageLabel.setIcon(ImageUtilities.loadImageIcon("org/netbeans/modules/kenai/collab/resources/newmessage.png", true));
            messageLabel.setBorder(new EmptyBorder(0,3,0,0));
        } else {
            messageLabel.setIcon(null);
        }
        if (isSelected) {
            this.setBackground(list.getSelectionBackground());
            this.setForeground(list.getSelectionForeground());
            buddyLabel.setBackground(list.getSelectionBackground());
            buddyLabel.setForeground(list.getSelectionForeground());
            messageLabel.setBackground(list.getSelectionBackground());
            messageLabel.setForeground(list.getSelectionForeground());
        } else {
            this.setBackground(list.getBackground());
            this.setForeground(list.getForeground());
            buddyLabel.setBackground(list.getBackground());
            buddyLabel.setForeground(list.getForeground());
            messageLabel.setBackground(list.getBackground());
            messageLabel.setForeground(list.getForeground());
        }
        this.setPreferredSize(new Dimension(10, getPreferredSize().height));
        return this;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel buddyLabel;
    private javax.swing.JLabel messageLabel;
    // End of variables declaration//GEN-END:variables
}
