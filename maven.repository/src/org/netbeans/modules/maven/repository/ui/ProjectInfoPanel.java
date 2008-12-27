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

package org.netbeans.modules.maven.repository.ui;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;
import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Result;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.windows.TopComponent;

/**
 *
 * @author mkleint
 */
public class ProjectInfoPanel extends TopComponent implements MultiViewElement, LookupListener {
    private MultiViewElementCallback callback;
    private Result<MavenProject> result;

    /** Creates new form ProjectInfoPanel */
    public ProjectInfoPanel(Lookup lookup) {
        super(lookup);
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblProjectName = new javax.swing.JLabel();
        txtProjectName = new javax.swing.JTextField();
        lblDescription = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        taDescription = new javax.swing.JTextArea();
        lblProjectHome = new javax.swing.JLabel();
        btnProjectHome = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        lblSystem = new javax.swing.JLabel();
        txtSystem = new javax.swing.JTextField();
        lblIssues = new javax.swing.JLabel();
        btnIssues = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        lblScmUrl = new javax.swing.JLabel();
        btnScmUrl = new javax.swing.JButton();
        lblConnection = new javax.swing.JLabel();
        txtConnection = new javax.swing.JTextField();
        lblDevConnection = new javax.swing.JLabel();
        txtDevConnection = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();

        lblProjectName.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.lblProjectName.text")); // NOI18N

        txtProjectName.setEditable(false);

        lblDescription.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.lblDescription.text")); // NOI18N

        taDescription.setColumns(20);
        taDescription.setEditable(false);
        taDescription.setRows(5);
        jScrollPane1.setViewportView(taDescription);

        lblProjectHome.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.lblProjectHome.text")); // NOI18N

        btnProjectHome.setText("prj url"); // NOI18N
        btnProjectHome.setBorder(null);
        btnProjectHome.setBorderPainted(false);
        btnProjectHome.setContentAreaFilled(false);
        btnProjectHome.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "TIT_Issues"))); // NOI18N

        lblSystem.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.lblSystem.text")); // NOI18N

        txtSystem.setEditable(false);

        lblIssues.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.lblIssues.text")); // NOI18N

        btnIssues.setText("isssue tracking url"); // NOI18N
        btnIssues.setBorder(null);
        btnIssues.setBorderPainted(false);
        btnIssues.setContentAreaFilled(false);
        btnIssues.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblSystem)
                    .add(lblIssues))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(btnIssues, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 396, Short.MAX_VALUE)
                    .add(txtSystem, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 396, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblSystem)
                    .add(txtSystem, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnIssues)
                    .add(lblIssues))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "TIT_SCM"))); // NOI18N

        lblScmUrl.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.lblScmUrl.text")); // NOI18N

        btnScmUrl.setText("scm url"); // NOI18N
        btnScmUrl.setBorder(null);
        btnScmUrl.setBorderPainted(false);
        btnScmUrl.setContentAreaFilled(false);
        btnScmUrl.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);

        lblConnection.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.lblConnection.text")); // NOI18N

        txtConnection.setEditable(false);

        lblDevConnection.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.lblDevConnection.text")); // NOI18N

        txtDevConnection.setEditable(false);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblScmUrl)
                    .add(lblConnection)
                    .add(lblDevConnection))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(txtConnection, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE)
                    .add(btnScmUrl, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE)
                    .add(txtDevConnection, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblScmUrl)
                    .add(btnScmUrl))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblConnection)
                    .add(txtConnection, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblDevConnection)
                    .add(txtDevConnection, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "TIT_CIManagement"))); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 484, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 94, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblProjectName)
                            .add(lblDescription)
                            .add(lblProjectHome))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(btnProjectHome, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
                            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
                            .add(txtProjectName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblProjectName)
                    .add(txtProjectName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblDescription)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblProjectHome)
                    .add(btnProjectHome, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(50, 50, 50))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnIssues;
    private javax.swing.JButton btnProjectHome;
    private javax.swing.JButton btnScmUrl;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblConnection;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblDevConnection;
    private javax.swing.JLabel lblIssues;
    private javax.swing.JLabel lblProjectHome;
    private javax.swing.JLabel lblProjectName;
    private javax.swing.JLabel lblScmUrl;
    private javax.swing.JLabel lblSystem;
    private javax.swing.JTextArea taDescription;
    private javax.swing.JTextField txtConnection;
    private javax.swing.JTextField txtDevConnection;
    private javax.swing.JTextField txtProjectName;
    private javax.swing.JTextField txtSystem;
    // End of variables declaration//GEN-END:variables

    public JComponent getVisualRepresentation() {
        return this;
    }

    public JComponent getToolbarRepresentation() {
        return new JPanel();
    }


    @Override
    public void componentOpened() {
        super.componentOpened();
        result = getLookup().lookup(new Lookup.Template<MavenProject>(MavenProject.class));
        populateFields();
        result.addLookupListener(this);
    }

    @Override
    public void componentClosed() {
        super.componentClosed();
        result.removeLookupListener(this);
    }

    @Override
    public void componentShowing() {
        super.componentShowing();
    }

    @Override
    public void componentHidden() {
        super.componentHidden();
    }

    @Override
    public void componentActivated() {
        super.componentActivated();
    }

    @Override
    public void componentDeactivated() {
        super.componentDeactivated();
    }


    public void setMultiViewCallback(MultiViewElementCallback callback) {
        this.callback = callback;
    }

    public CloseOperationState canCloseElement() {
        return CloseOperationState.STATE_OK;
    }

    private void populateFields() {
        boolean loading = true;
        Iterator<? extends MavenProject> iter = result.allInstances().iterator();
        String name = null, desc = null, homeUrl = null;
        String imUrl = null, imSystem = null;
        String scmUrl = null, scmConn = null, scmDevConn = null;
        if (iter.hasNext()) {
            loading = false;
            MavenProject prj = iter.next();
            name = prj.getName();
            desc = prj.getDescription();
            homeUrl = prj.getUrl();
            IssueManagement im = prj.getIssueManagement();
            if (im != null) {
                imUrl = im.getUrl();
                imSystem = im.getSystem();
            }
            Scm scm = prj.getScm();
            if (scm != null) {
                scmUrl = scm.getUrl();
                scmConn = scm.getConnection();
                scmDevConn = scm.getDeveloperConnection();
            }
        }
        setPlainText(txtProjectName, name, loading); 
        setPlainText(taDescription, desc, loading); 
        setLinkedText(btnProjectHome, homeUrl, loading);
        
        setLinkedText(btnIssues, imUrl, loading);
        setPlainText(txtSystem, imSystem, loading); 

        setLinkedText(btnScmUrl, scmUrl, loading);
        setPlainText(txtConnection, scmConn, loading);
        setPlainText(txtDevConnection, scmDevConn, loading);
    }

    public void resultChanged(LookupEvent ev) {
        populateFields();
    }

    private void setLinkedText(JButton btn, String url, boolean loading) {
        if (url == null) {
            btn.setAction(null);
            if (loading) {
                btn.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "LBL_Loading"));
            } else {
                btn.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "LBL_Undefined"));
            }
            btn.setCursor(null);
        } else {
            btn.setAction(new LinkAction(url));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setText("<html><a href=\"\">" + url + "</a></html>");
        }
    }

    private void setPlainText(JTextComponent field, String value, boolean loading) {
        if (value == null) {
            if (loading) {
                field.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "LBL_Loading"));
            } else {
                field.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "LBL_Undefined"));
            }
        } else {
            field.setText(value);
        }
    }

    private class LinkAction extends AbstractAction {
        private String url;

        public LinkAction(String url) {
            this.url = url;
        }

        public void actionPerformed(ActionEvent e) {
            System.out.println("linked.." + url);
        }

    }
}
