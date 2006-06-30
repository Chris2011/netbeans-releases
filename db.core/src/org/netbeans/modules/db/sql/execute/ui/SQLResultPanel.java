/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.db.sql.execute.ui;

import java.awt.CardLayout;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.DateFormat;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.netbeans.modules.db.sql.execute.NullValue;
import org.openide.util.Lookup;
import org.netbeans.modules.db.sql.execute.SQLExecutionResults;
import org.openide.util.NbBundle;
import org.openide.util.datatransfer.ExClipboard;

/**
 *
 * @author  Andrei Badea
 */
public class SQLResultPanel extends javax.swing.JPanel {
    
    private static final String CARD_RESULT_SET = "resultSet"; // NOI18N
    private static final String CARD_ROW_COUNT = "rowCount"; // NOI18N
    
    private SQLExecutionResults executionResults;
    private String currentCardName;
    
    public SQLResultPanel() {
        initComponents();
    }
    
    public void setModel(SQLResultPanelModel model) {
        ResultSetTableModel resultSetModel = null;
        String cardName = null;
        
        if (model != null) {
            if (model.getResultSetModel() != null) {
                resultSetModel = model.getResultSetModel();
                cardName = CARD_RESULT_SET; // NOI18N
            } else if (model.getAffectedRows() != null) {
                resultSetModel = new ResultSetTableModel.Empty();
                rowCountLabel.setText(NbBundle.getMessage(SQLResultPanel.class, "LBL_AffectedRows", model.getAffectedRows()));
                cardName = CARD_ROW_COUNT; // NOI18N
            } else {
                resultSetModel = new ResultSetTableModel.Empty();
                cardName = CARD_RESULT_SET; // NOI18N
            }
        } else {
            resultSetModel = new ResultSetTableModel.Empty();
            cardName = CARD_RESULT_SET; // NOI18N
        }
        
        assert resultSetModel != null;
        assert cardName != null;
        
        resultTable.setModel(resultSetModel);
        showCard(cardName);
    }
    
    private void showCard(String cardName) {
        if (!cardName.equals(currentCardName)) {
            ((CardLayout)getLayout()).show(this, cardName); // NOI18N
        }
        currentCardName = cardName;
    }
    
    private void setClipboard(String contents) {
        ExClipboard clipboard = (ExClipboard) Lookup.getDefault().lookup (ExClipboard.class);
        StringSelection strSel = new StringSelection(contents);
        clipboard.setContents(strSel, strSel);        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        tablePopupMenu = new javax.swing.JPopupMenu();
        copyCellValueMenuItem = new javax.swing.JMenuItem();
        copyRowValuesMenuItem = new javax.swing.JMenuItem();
        resultSetPanel = new javax.swing.JPanel();
        resultScrollPane = new javax.swing.JScrollPane();
        resultTable = new SQLResultTable();
        rowCountPanel = new javax.swing.JPanel();
        rowCountLabel = new javax.swing.JLabel();

        copyCellValueMenuItem.setText(org.openide.util.NbBundle.getMessage(SQLResultPanel.class, "LBL_CopyCellValue"));
        copyCellValueMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyCellValueMenuItemActionPerformed(evt);
            }
        });

        tablePopupMenu.add(copyCellValueMenuItem);
        copyCellValueMenuItem.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SQLResultPanel.class, "ACSD_CopyCellValue"));

        copyRowValuesMenuItem.setText(org.openide.util.NbBundle.getMessage(SQLResultPanel.class, "LBL_CopyRowValues"));
        copyRowValuesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyRowValuesMenuItemActionPerformed(evt);
            }
        });

        tablePopupMenu.add(copyRowValuesMenuItem);
        copyRowValuesMenuItem.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SQLResultPanel.class, "ACSD_CopyRowValues"));

        setLayout(new java.awt.CardLayout());

        resultSetPanel.setLayout(new java.awt.BorderLayout());

        resultScrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        resultScrollPane.getViewport().setBackground(UIManager.getDefaults().getColor("Table.background"));
        resultTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        resultTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        resultTable.setOpaque(false);
        resultTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                resultTableMouseReleased(evt);
            }
        });

        resultScrollPane.setViewportView(resultTable);

        resultSetPanel.add(resultScrollPane, java.awt.BorderLayout.CENTER);

        add(resultSetPanel, "resultSet");

        rowCountPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 2));

        rowCountPanel.setBackground(javax.swing.UIManager.getDefaults().getColor("Table.background"));
        rowCountLabel.setText("jLabel1");
        rowCountPanel.add(rowCountLabel);

        add(rowCountPanel, "rowCount");

    }
    // </editor-fold>//GEN-END:initComponents

    private void copyRowValuesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyRowValuesMenuItemActionPerformed
        int[] rows = resultTable.getSelectedRows();
        StringBuffer output = new StringBuffer();
        for (int i = 0; i < rows.length; i++) {
            for (int col = 0; col < resultTable.getColumnCount(); col++) {
                if (col > 0) {
                    output.append('\t');
                }
                Object o = resultTable.getValueAt(rows[i], col);
                output.append(o.toString());
            }
            output.append('\n');
        }
        setClipboard(output.toString());
    }//GEN-LAST:event_copyRowValuesMenuItemActionPerformed

    private void copyCellValueMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyCellValueMenuItemActionPerformed
        Object o = resultTable.getValueAt(resultTable.getSelectedRow(), resultTable.getSelectedColumn());
        setClipboard(o.toString());
    }//GEN-LAST:event_copyCellValueMenuItemActionPerformed

    private void resultTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resultTableMouseReleased
        if (evt.getButton() != MouseEvent.BUTTON3) {
            return;
        }
        int row = resultTable.rowAtPoint(evt.getPoint());
        int column = resultTable.columnAtPoint(evt.getPoint());
        boolean inSelection = false;
        int[] rows = resultTable.getSelectedRows();
        for (int i = 0; i < rows.length; i++) {
            if (rows[i] == row) {
                inSelection = true;
                break;
            }
        }
        if (!inSelection) {
            resultTable.changeSelection (row, column, false, false);
        }
        tablePopupMenu.show(resultTable, evt.getX(), evt.getY());
    }//GEN-LAST:event_resultTableMouseReleased
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem copyCellValueMenuItem;
    private javax.swing.JMenuItem copyRowValuesMenuItem;
    private javax.swing.JScrollPane resultScrollPane;
    private javax.swing.JPanel resultSetPanel;
    private javax.swing.JTable resultTable;
    private javax.swing.JLabel rowCountLabel;
    private javax.swing.JPanel rowCountPanel;
    private javax.swing.JPopupMenu tablePopupMenu;
    // End of variables declaration//GEN-END:variables

    private static final class SQLResultTable extends JTable {
        
        public SQLResultTable() {
            // issue 70521: rendering java.sql.Timestamp as date and time
            setDefaultRenderer(Timestamp.class, new DateTimeRenderer());
        }
        
        /**
         * Overrinding in order to provide a valid renderer for NullValue.
         * NullValue can appear in any column and causes formatting exceptions.
         * See issue 62622.
         */
        public TableCellRenderer getCellRenderer(int row, int column) {
            Object value = getValueAt(row, column);
            if (value instanceof NullValue) {
                return getDefaultRenderer(Object.class);
            } else {
                return super.getCellRenderer(row, column);
            }
        }
        
        /**
         * Renderer which renders both the date and time part of a Date.
         */
        private static final class DateTimeRenderer extends DefaultTableCellRenderer.UIResource {
            
            DateFormat formatter;
            
            public DateTimeRenderer() { 
                super(); 
            }

            public void setValue(Object value) {
                if (formatter == null) {
                    formatter = DateFormat.getDateTimeInstance();
                }
                setText((value == null) ? "" : formatter.format(value)); // NOI18N
            }
        }
    }
}