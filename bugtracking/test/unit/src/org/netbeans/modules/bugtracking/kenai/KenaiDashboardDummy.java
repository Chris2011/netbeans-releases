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
 * KenaiDashboardDummy.java
 *
 * Created on Feb 24, 2009, 4:47:06 PM
 */

package org.netbeans.modules.bugtracking.kenai;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import org.netbeans.modules.kenai.api.KenaiProject;
import org.netbeans.modules.kenai.ui.spi.ProjectHandle;
import org.netbeans.modules.kenai.ui.spi.QueryHandle;
import org.netbeans.modules.kenai.ui.spi.QueryResultHandle;

/**
 *
 * @author tomas
 */
public class KenaiDashboardDummy extends javax.swing.JFrame implements PropertyChangeListener {
    private QueryAccessorImpl qa;
    private KenaiProject p;

    /** Creates new form KenaiDashboardDummy */
    public KenaiDashboardDummy(final QueryAccessorImpl qa, KenaiProject p) {
        initComponents();
        this.qa = qa;
        this.p = p;
        DummyProjectHandle ph = new DummyProjectHandle(p.getName());
        ph.addPropertyChangeListener(this);
        stripListernes(findIssuesButton);
        findIssuesButton.addActionListener(qa.getFindIssueAction(ph));

        List<QueryHandle> qhs = qa.getQueries(ph);
        populateCombo(qhs);
        queryCombo.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if(value != null) {
                    QueryHandle h = (QueryHandle) value;
                    value = h.getDisplayName();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        queryCombo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                QueryHandle h = (QueryHandle) e.getItem();
                setHandle(h);
            }
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        findIssuesButton = new javax.swing.JButton();
        defaultButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        totalButton.setText("jButton1");

        unseenButton.setText("jButton2");

        findIssuesButton.setText("Find Issues...");

        defaultButton.setText("default");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(queryCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(totalButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(unseenButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(defaultButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 98, Short.MAX_VALUE)
                .add(findIssuesButton)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(queryCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(totalButton)
                    .add(unseenButton)
                    .add(findIssuesButton)
                    .add(defaultButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton defaultButton;
    private javax.swing.JButton findIssuesButton;
    final javax.swing.JComboBox queryCombo = new javax.swing.JComboBox();
    final javax.swing.JButton totalButton = new javax.swing.JButton();
    final javax.swing.JButton unseenButton = new javax.swing.JButton();
    // End of variables declaration//GEN-END:variables

    public void propertyChange(final PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals(ProjectHandle.PROP_QUERY_LIST)) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    List<QueryHandle> l = (List<QueryHandle>) evt.getNewValue();
                    populateCombo(l);
                }
            });
        } else if (evt.getPropertyName().equals(QueryHandle.PROP_QUERY_RESULT)) {
            List<QueryResultHandle> l = (List<QueryResultHandle>) evt.getNewValue();
            populateButtons(l);
        }
    }

    private void populateButtons(List<QueryResultHandle> qrs) {
        if(qrs != null) {
            int i = 0;
            stripListernes(totalButton);
            stripListernes(unseenButton);
            unseenButton.setText("");
            totalButton.setText("");
            for (QueryResultHandle qrh : qrs) {
                if(i == 0) {
                    totalButton.setText(qrh.getText());
                    totalButton.addActionListener(qa.getOpenQueryResultAction(qrh));
                } else if(i == 1) {
                    unseenButton.setText(qrh.getText());
                    unseenButton.addActionListener(qa.getOpenQueryResultAction(qrh));
                }
                i++;
            }
        }
    }
    
    private void populateCombo(List<QueryHandle> l) {
        if(l == null) {
            queryCombo.setModel(new DefaultComboBoxModel());
        } else {
            queryCombo.setModel(new DefaultComboBoxModel(l.toArray(new QueryHandle[l.size()])));
            for (QueryHandle qh : l) {
                qh.addPropertyChangeListener(this);
            }
            if(l.size() > 0) {
                queryCombo.setSelectedItem(l.get(0));
            }
        }
    }

    private void setHandle(QueryHandle h) {
        List<QueryResultHandle> qrs = qa.getQueryResults(h);
        stripListernes(defaultButton);
        defaultButton.addActionListener(qa.getDefaultAction(h));
        populateButtons(qrs);
    }

    private void stripListernes(JButton bt) {
        ActionListener[] l = bt.getActionListeners();
        for (ActionListener al : l) {
            bt.removeActionListener(al);
        }
    }

    private class DummyProjectHandle extends ProjectHandle {

        public DummyProjectHandle(String id) {
            super(id);
        }

        @Override
        public String getDisplayName() {
            return p.getDisplayName();
        }

        @Override
        public boolean isPrivate() {
            return false;
        }
        
    }

}
