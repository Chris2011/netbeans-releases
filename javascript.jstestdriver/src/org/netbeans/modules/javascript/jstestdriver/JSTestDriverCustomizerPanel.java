/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javascript.jstestdriver;

import java.awt.Component;
import java.awt.Dialog;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import org.netbeans.modules.web.browser.api.BrowserFamilyId;
import org.netbeans.modules.web.browser.api.WebBrowser;
import org.netbeans.modules.web.browser.api.WebBrowsers;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.filesystems.FileUtil;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;

/**
 *
 */
public class JSTestDriverCustomizerPanel extends javax.swing.JPanel implements DocumentListener, HelpCtx.Provider  {

    private static final String LOCATION = "location"; //NOI18N
    private static final String USE_BROWSER = "use.browser."; //NOI18N
    private static final String SERVER_URL = "server.url"; //NOI18N
    private static final String STRICT_MODE = "strict.mode"; //NOI18N

    private DialogDescriptor descriptor;
    
    /**
     * Creates new form JSTestDriverCustomizerPanel
     */
    public JSTestDriverCustomizerPanel() {
        initComponents();
        String l = getPersistedLocation();
        jLocationTextField.setText(l != null ? l : ""); //NOI18N
        jLocationTextField.getDocument().addDocumentListener(this);
        jStrictCheckBox.setSelected(isStricModel());
        jServerURLTextField.setText(getServerURL());
        jServerURLTextField.getDocument().addDocumentListener(this);
        jBrowsersTable.setModel(new BrowsersTableModel());
        jBrowsersTable.setDefaultRenderer(TableRow.class, new TableRowCellRenderer());
        initTableVisualProperties(jBrowsersTable);
        jRestartNeededLabel.setVisible(JSTestDriverSupport.getDefault().isRunning() && 
                !JSTestDriverSupport.getDefault().wasStartedExternally());
    }
    
    private void initTableVisualProperties(JTable table) {
        table.setRowSelectionAllowed(true);
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setTableHeader(null);
        table.setRowHeight(jBrowsersTable.getRowHeight() + 4);        
        table.setIntercellSpacing(new java.awt.Dimension(0, 0));        
        // set the color of the table's JViewport
        table.getParent().setBackground(table.getBackground());
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.getColumnModel().getColumn(0).setMaxWidth(30);
    }
    
    
    private static String getPersistedLocation() {
        return NbPreferences.forModule(JSTestDriverCustomizerPanel.class).get(LOCATION, null);
    }

    private void setDescriptor(DialogDescriptor descriptor) {
        this.descriptor = descriptor;
        updateValidity();
    }
    
    private void updateValidity() {
        descriptor.setValid(isValidJSTestDriverJar(jLocationTextField.getText()));
        boolean externalServer = (getPort(jServerURLTextField.getText()) == -1);
        jBrowsersTable.setEnabled(!externalServer);
        jStrictCheckBox.setEnabled(!externalServer);
        jRestartNeededLabel.setVisible(JSTestDriverSupport.getDefault().isRunning() && 
                (!externalServer && !JSTestDriverSupport.getDefault().wasStartedExternally()));
        jRemoteServerLabel.setVisible(externalServer);
    }

    private static boolean isValidJSTestDriverJar(String s) {
        if (s == null) {
            return false;
        }
        File f = new File(s);
        return (f.exists() && isValidFileName(f));
    }
    
    private static boolean isValidFileName(File f) {
        return (f.getName().toLowerCase().startsWith("jstestdriver") && //NOI18N
                f.getName().toLowerCase().endsWith(".jar")); //NOI18N
    }

    public static boolean showCustomizer() {
        JSTestDriverCustomizerPanel panel = new JSTestDriverCustomizerPanel();
        DialogDescriptor descriptor = new DialogDescriptor(panel, 
                org.openide.util.NbBundle.getMessage(JSTestDriverCustomizerPanel.class, "MSG_CONFIGURE"));
        panel.setDescriptor(descriptor);
        Dialog dialog = DialogDisplayer.getDefault().createDialog(descriptor);
        dialog.setModal(true);
        dialog.setVisible(true);
        dialog.dispose();
        if (descriptor.getValue() == DialogDescriptor.OK_OPTION) {
            if (JSTestDriverSupport.getDefault().isRunning() && 
                JSTestDriverSupport.getDefault().wasStartedExternally()) {
                // forget current server:
                JSTestDriverSupport.getDefault().forgetCurrentServer();
            }
            Preferences prefs = NbPreferences.forModule(JSTestDriverCustomizerPanel.class);
            prefs.put(LOCATION, panel.jLocationTextField.getText());
            prefs.put(SERVER_URL, panel.jServerURLTextField.getText());
            prefs.putBoolean(STRICT_MODE, panel.jStrictCheckBox.isSelected());
            boolean usesIntegratedBrowser = false;
            for (TableRow row : ((BrowsersTableModel)panel.jBrowsersTable.getModel()).model) {
                prefs.putBoolean(getBrowserPropertyName(row.getBrowser(), row.hasNbIntegration()), row.isSelected());
                if (row.isSelected() && row.hasNbIntegration()) {
                    usesIntegratedBrowser = true;
                }
            }
            boolean externalServer = (getPort(panel.jServerURLTextField.getText()) == -1);
            JSTestDriverSupport.logUsage(JSTestDriverCustomizerPanel.class, "USG_JSTESTDRIVER_CONFIGURED", // NOI18N
                    new Object[]{externalServer ? "YES" : "NO", usesIntegratedBrowser ? "YES" : "NO"}); // NOI18N
            return true;
        } else {
            return false;
        }
    }
    
    private static String getBrowserPropertyName(WebBrowser browser, boolean nbIntegration) {
        return PropertyUtils.getUsablePropertyName(USE_BROWSER+browser.getId()+(nbIntegration ? ".nbint" : "")); //NOI18N
    }
    
    public static boolean isConfiguredProperly() {
        String l = getPersistedLocation();
        return isValidJSTestDriverJar(l);
    }
    
    public static String getJSTestDriverJar() {
        return getPersistedLocation();
    }

    public static String getServerURL() {
        return NbPreferences.forModule(JSTestDriverCustomizerPanel.class).get(SERVER_URL, "http://localhost:42442"); //NOI18N
    }

    public static boolean isStricModel() {
        return NbPreferences.forModule(JSTestDriverCustomizerPanel.class).getBoolean(STRICT_MODE, false);
    }

    static int getPort(String s) {
        if (s.endsWith("/")) { //NOI18N
            s = s.substring(0, s.length()-1);
        }
        if (s.startsWith("http://localhost:")) { //NOI18N
            try {
                return Integer.parseInt(s.substring(17));
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    public static int getPort() {
        return getPort(getServerURL());
    }
    
    private static List<TableRow> createModel() {
        List<TableRow> model = new ArrayList<TableRow>();
        for (WebBrowser browser : WebBrowsers.getInstance().getAll(false, false, false, true)) {
            if (browser.isEmbedded()) {
                continue;
            }
            model.add(new TableRow(browser, 
                NbPreferences.forModule(JSTestDriverCustomizerPanel.class).getBoolean(getBrowserPropertyName(browser, false), false), browser.hasNetBeansIntegration()));
        }
        return model;
    }
    
    public static List<WebBrowser> getBrowsers() {
        List<TableRow> model = createModel();
        List<WebBrowser> res = new ArrayList<WebBrowser>();
        for (TableRow row : model) {
            if (row.isSelected()) {
                res.add(row.getBrowser());
            }
        }
        return res;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLocationTextField = new javax.swing.JTextField();
        jBrowseButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jBrowsersTable = new javax.swing.JTable();
        jStrictCheckBox = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        jServerURLTextField = new javax.swing.JTextField();
        jRestartNeededLabel = new javax.swing.JLabel();
        jRemoteServerLabel = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(JSTestDriverCustomizerPanel.class, "JSTestDriverCustomizerPanel.jLabel1.text")); // NOI18N

        jLocationTextField.setText(org.openide.util.NbBundle.getMessage(JSTestDriverCustomizerPanel.class, "JSTestDriverCustomizerPanel.jLocationTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jBrowseButton, org.openide.util.NbBundle.getMessage(JSTestDriverCustomizerPanel.class, "JSTestDriverCustomizerPanel.jBrowseButton.text")); // NOI18N
        jBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBrowseButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(JSTestDriverCustomizerPanel.class, "JSTestDriverCustomizerPanel.jLabel2.text")); // NOI18N

        jScrollPane1.setViewportView(jBrowsersTable);

        org.openide.awt.Mnemonics.setLocalizedText(jStrictCheckBox, org.openide.util.NbBundle.getMessage(JSTestDriverCustomizerPanel.class, "JSTestDriverCustomizerPanel.jStrictCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(JSTestDriverCustomizerPanel.class, "JSTestDriverCustomizerPanel.jLabel3.text")); // NOI18N

        jServerURLTextField.setText("http://localhost:42442");

        org.openide.awt.Mnemonics.setLocalizedText(jRestartNeededLabel, org.openide.util.NbBundle.getMessage(JSTestDriverCustomizerPanel.class, "JSTestDriverCustomizerPanel.jRestartNeededLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jRemoteServerLabel, org.openide.util.NbBundle.getMessage(JSTestDriverCustomizerPanel.class, "JSTestDriverCustomizerPanel.jRemoteServerLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jLabel2)
                    .addComponent(jStrictCheckBox)
                    .addComponent(jRestartNeededLabel)
                    .addComponent(jRemoteServerLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLocationTextField)
                            .addComponent(jServerURLTextField))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBrowseButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLocationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBrowseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jServerURLTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jStrictCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRestartNeededLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRemoteServerLabel)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBrowseButtonActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return !f.isFile() || isValidFileName(f);
            }
            @Override
            public String getDescription() {
                return "JsTestDriver*.jar"; //NOI18N
            }
        });
        File file = new File(jLocationTextField.getText());
        if (jLocationTextField.getText().length() > 0 && file.exists()) {
            chooser.setSelectedFile(file);
        }
        if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
            File selected = FileUtil.normalizeFile(chooser.getSelectedFile());
            jLocationTextField.setText(selected.getAbsolutePath());
        }
    }//GEN-LAST:event_jBrowseButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBrowseButton;
    private javax.swing.JTable jBrowsersTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField jLocationTextField;
    private javax.swing.JLabel jRemoteServerLabel;
    private javax.swing.JLabel jRestartNeededLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jServerURLTextField;
    private javax.swing.JCheckBox jStrictCheckBox;
    // End of variables declaration//GEN-END:variables

    @Override
    public void insertUpdate(DocumentEvent e) {
        updateValidity();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        updateValidity();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        updateValidity();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx("org.netbeans.modules.javascript.jstestdriver.JSTestDriverCustomizerPanel"); //NOI18N
    }

    private static class TableRowCellRenderer extends DefaultTableCellRenderer {
        
        @NbBundle.Messages({"# {0} - browser name", "IntegratedBrowserName={0} with NetBeans JS Debugger"})
        public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column ) {
            if (value instanceof TableRow) {
                TableRow item = (TableRow) value;
                String s = item.getBrowser().getName();
                if (item.hasNbIntegration()) {
                    s = Bundle.IntegratedBrowserName(s);
                }
                return super.getTableCellRendererComponent(table, s, isSelected, false, row, column);
            }
            return super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
        }
        
    }

    /** 
     * Implements a TableModel.
     */
    private static final class BrowsersTableModel extends AbstractTableModel {

        private List<TableRow> model;
        
        public BrowsersTableModel() {
            model = createModel();
        }
        
        @Override
        public int getColumnCount() {
            return 2;
        }
        
        @Override
        public int getRowCount() {
            return model.size();
        }
        
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return Boolean.class;
            }
            else {
                return TableRow.class;
            }
        }
        
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return (columnIndex == 0);
        }
        
        @Override
        public Object getValueAt(int row, int column) {
            TableRow item = model.get(row);
            switch (column) {
                case 0: return item.isSelected();
                case 1: return item;
            }
            return ""; //NOI18N
        }
        
        @Override
        public void setValueAt(Object value, int row, int column) {
            TableRow item = model.get(row);
            switch (column) {
                case 0: item.setSelected((Boolean) value);break;
            }
            fireTableCellUpdated(row, column);
        }
        
    }

    private static final class TableRow {
        private WebBrowser browser;
        private boolean selected;
        private boolean nbIntegration;

        public TableRow(WebBrowser browser, boolean selected, boolean nbIntegration) {
            this.browser = browser;
            this.selected = selected;
            this.nbIntegration = nbIntegration;
        }

        public boolean isSelected() {
            return selected;
        }

        public boolean hasNbIntegration() {
            return nbIntegration;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public WebBrowser getBrowser() {
            return browser;
        }
        
    }
    
}
