/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011-2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.db.dataview.table.celleditor;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.charset.Charset;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.TableCellEditor;
import org.netbeans.api.progress.ProgressUtils;
import org.netbeans.modules.db.dataview.util.FileBackedClob;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

public class ClobFieldTableCellEditor extends AbstractCellEditor
        implements TableCellEditor,
        ActionListener, AlwaysEnable {
    
    private class CharsetSelector extends JPanel {
        private JComboBox charsetSelect;
        
        CharsetSelector() {
            List<Charset> charset = new ArrayList<Charset>(Charset.availableCharsets().values());
            Collections.sort(charset, new Comparator<Charset>() {
                @Override
                public int compare(Charset o1, Charset o2) {
                    return o1.displayName().compareTo(o2.displayName());
                }
            });
            charsetSelect = new JComboBox();
            charsetSelect.setModel(new DefaultComboBoxModel(
                    charset.toArray(new Charset[charset.size()])));
            charsetSelect.setSelectedItem(Charset.defaultCharset());
            this.add(charsetSelect);
        }
        
        public Charset getSelectedCharset() {
            return (Charset) charsetSelect.getSelectedItem();
        }
        
        public void setSelectedCharset(Charset selectedCharset) {
            charsetSelect.setSelectedItem(selectedCharset);
        }
    }
    private static final Logger LOG = Logger.getLogger(
            ClobFieldTableCellEditor.class.getName());
    private static final String EDIT = "edit";
    
    private static File lastFile;
    
    private Clob currentValue;
    private JButton button;
    private JPopupMenu popup;
    private JTable table;
    private int currentRow;
    private int currentColumn;
    private int currentModelRow;
    private int currentModelColumn;
    private JMenuItem saveContentMenuItem;
    private JMenuItem editContentMenuItem;
    private JMenuItem loadContentMenuItem;
    private JMenuItem nullContentMenuItem;
    
    @SuppressWarnings("LeakingThisInConstructor")
    public ClobFieldTableCellEditor() {
        button = new JButton();
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setRolloverEnabled(false);
        button.setAlignmentX(0);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font(button.getFont().getFamily(), Font.ITALIC, 9));
        
        popup = new JPopupMenu();
        saveContentMenuItem = new JMenuItem(NbBundle.getMessage(ClobFieldTableCellEditor.class, "saveLob.title"));
        saveContentMenuItem.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                saveLobToFile(currentValue);
                fireEditingCanceled();
            }
        });
        popup.add(saveContentMenuItem);
        editContentMenuItem = new JMenuItem(NbBundle.getMessage(ClobFieldTableCellEditor.class, "editClob.title"));
        editContentMenuItem.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
                editCell();
            }
        });
        popup.add(editContentMenuItem);
        loadContentMenuItem = new JMenuItem(NbBundle.getMessage(ClobFieldTableCellEditor.class, "loadLob.title"));
        loadContentMenuItem.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                Object newValue = loadLobFromFile();
                if (newValue != null) {
                    currentValue = (Clob) newValue;
                }
                fireEditingStopped();
            }
        });
        popup.add(loadContentMenuItem);
        nullContentMenuItem = new JMenuItem(NbBundle.getMessage(ClobFieldTableCellEditor.class, "nullLob.title"));
        nullContentMenuItem.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                currentValue = null;
                fireEditingStopped();
            }
        });
        popup.add(nullContentMenuItem);
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (EDIT.equals(e.getActionCommand())) {
            popup.show(button, 0, button.getHeight());
        }
    }
    
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        currentValue = (java.sql.Clob) value;
        this.currentColumn = column;
        this.currentRow = row;
        this.table = table;
        this.currentModelColumn = table.convertColumnIndexToModel(column);
        this.currentModelRow = table.convertRowIndexToModel(row);
        boolean editable = table.getModel().isCellEditable(currentModelRow, currentModelColumn);
        if (currentValue != null) {
            saveContentMenuItem.setEnabled(true);
            try {
                long size = currentValue.length();
                StringBuilder stringValue = new StringBuilder();
                stringValue.append("<CLOB ");
                if (size < 1000) {
                    stringValue.append(String.format("%1$d Chars", size));
                } else if (size < 1000000) {
                    stringValue.append(String.format("%1$d kChars", size / 1000));
                } else {
                    stringValue.append(String.format("%1$d MChars", size / 1000000));
                }
                stringValue.append(">");
                button.setText(stringValue.toString());
            } catch (SQLException ex) {
                button.setText("<CLOB of unknown size>");
            }
        } else {
            saveContentMenuItem.setEnabled(false);
            button.setText("<NULL>");
        }
        loadContentMenuItem.setEnabled(editable);
        nullContentMenuItem.setEnabled(editable);
        if (editable) {
            editContentMenuItem.setEnabled(true);
            editContentMenuItem.setText(NbBundle.getMessage(ClobFieldTableCellEditor.class, "editClob.title"));
        } else {
            editContentMenuItem.setEnabled(currentValue != null);
            editContentMenuItem.setText(NbBundle.getMessage(ClobFieldTableCellEditor.class, "editClobReadOnly.title"));
        }
        return button;
    }
    
    @Override
    public Object getCellEditorValue() {
        return currentValue;
    }
    
    @Override
    public boolean isCellEditable(EventObject anEvent) {
        if (anEvent instanceof MouseEvent) {
            return ((MouseEvent) anEvent).getClickCount() >= 2;
        }
        return super.isCellEditable(anEvent);
    }
    
    private void saveLobToFile(Clob b) {
        if (b == null) {
            return;
        }
        CharsetSelector charset = new CharsetSelector();
        JFileChooser c = new JFileChooser();
        c.setCurrentDirectory(lastFile);
        c.setAccessory(charset);
        int fileDialogState = c.showSaveDialog(table);
        if (fileDialogState == JFileChooser.APPROVE_OPTION) {
            File f = c.getSelectedFile();
            lastFile = f;
            Reader r;
            Writer w;
            try {
                r = b.getCharacterStream();
                w = new OutputStreamWriter(new FileOutputStream(f), charset.getSelectedCharset());
                if(! doTransfer(r, w, (int) b.length(), "Save to file: " + f.toString(), false)) {
                    f.delete();
                }
            } catch (IOException ex) {
                LOG.log(Level.INFO, "IOException while saving CLOB to file", ex);
                displayError(f, ex, false);
            } catch (SQLException ex) {
                LOG.log(Level.INFO, "SQLException while saving CLOB to file", ex);
                displayError(f, ex, false);
            }
        }
    }
    
    private Clob loadLobFromFile() {
        CharsetSelector charset = new CharsetSelector();
        JFileChooser c = new JFileChooser();
        c.setCurrentDirectory(lastFile);
        c.setAccessory(charset);
        Clob result = null;
        int fileDialogState = c.showOpenDialog(table);
        if (fileDialogState == JFileChooser.APPROVE_OPTION) {
            File f = c.getSelectedFile();
            lastFile = f;
            Reader r;
            try {
                result = new FileBackedClob();
                r = new InputStreamReader(new FileInputStream(f), charset.getSelectedCharset());
                if(! doTransfer(r, result.setCharacterStream(1), (int) f.length() / 2, "Load from file: " + f.toString(), true)) {
                    result = null;
                }
            } catch (IOException ex) {
                LOG.log(Level.INFO, "IOException while loading CLOB from file", ex);
                displayError(f, ex, true);
                result = null;
            } catch (SQLException ex) {
                LOG.log(Level.INFO, "SQLException while loading CLOB from file", ex);
                displayError(f, ex, true);
                result = null;
            }
        }
        return result;
    }

    /**
     * Note: The character streams will be closed after this method was invoked
     * 
     * @return true if transfer is complete and not interrupted 
     */
    private boolean doTransfer(Reader in, Writer out, Integer size, String title, boolean sizeEstimated) throws IOException {
        // Only pass size if it is _not_ estimated
        MonitorableCharacterStreamTransfer ft = new MonitorableCharacterStreamTransfer(in, out, sizeEstimated ? null : size);
        Throwable t;
        // Only show dialog, if the filesize is large enougth and has a use for the user
        if (size == null || size > (1024 * 1024)) {
            t = ProgressUtils.showProgressDialogAndRun(ft, title, false);
        } else {
            t = ft.run(null);
        }
        in.close();
        out.close();
        if (t != null && t instanceof RuntimeException) {
            throw (RuntimeException) t;
        } else if (t != null && t instanceof IOException) {
            throw (IOException) t;
        } else if (t != null) {
            throw new RuntimeException(t);
        }
        return !ft.isCancel();
    }
    
    private void displayError(File f, Exception ex, boolean read) {
        DialogDisplayer dd = DialogDisplayer.getDefault();

        String errorObjectMsg;
        String messageMsg;
        String titleMsg;

        if (ex instanceof SQLException) {
            errorObjectMsg = NbBundle.getMessage(ClobFieldTableCellEditor.class,
                    "lobErrorObject.database");
        } else {
            errorObjectMsg = NbBundle.getMessage(ClobFieldTableCellEditor.class,
                    "lobErrorObject.file");
        }

        if (!read) {
            titleMsg = NbBundle.getMessage(ClobFieldTableCellEditor.class,
                    "clobSaveToFileError.title");
            messageMsg = NbBundle.getMessage(ClobFieldTableCellEditor.class,
                    "clobSaveToFileError.message",
                    errorObjectMsg,
                    f.getAbsolutePath(),
                    ex.getLocalizedMessage());
        } else {
            titleMsg = NbBundle.getMessage(ClobFieldTableCellEditor.class,
                    "clobReadFromFileError.title");
            messageMsg = NbBundle.getMessage(ClobFieldTableCellEditor.class,
                    "clobReadFromFileError.message",
                    errorObjectMsg,
                    f.getAbsolutePath(),
                    ex.getLocalizedMessage());
        }

        NotifyDescriptor nd = new NotifyDescriptor(
                messageMsg,
                titleMsg,
                NotifyDescriptor.OK_CANCEL_OPTION,
                NotifyDescriptor.WARNING_MESSAGE,
                new Object[]{NotifyDescriptor.CANCEL_OPTION},
                NotifyDescriptor.CANCEL_OPTION);

        dd.notifyLater(nd);
    }

    protected void editCell() {
        String stringVal = "";
        if (currentValue != null) {
            try {
                stringVal = currentValue.getSubString(1, (int) currentValue.length());
            } catch (SQLException ex) {
            }
            
        }
        
        JTextArea textArea = new JTextArea(20, 80);
        // Work around: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=7100524
        textArea.setDropTarget(null);
        textArea.setText(stringVal);
        textArea.setCaretPosition(0);
        textArea.setEditable(table.getModel().isCellEditable(currentModelRow, currentModelColumn));
        
        JScrollPane pane = new JScrollPane(textArea);
        pane.addHierarchyListener(
                new StringTableCellEditor.MakeResizableListener(pane));
        Component parent = WindowManager.getDefault().getMainWindow();
        
        if (table.isCellEditable(currentRow, currentColumn)) {
            int result = JOptionPane.showOptionDialog(parent, pane, table.getColumnName(currentColumn), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    table.setValueAt(new FileBackedClob(textArea.getText()), currentRow, currentColumn);
                } catch (SQLException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        } else {
            JOptionPane.showMessageDialog(parent, pane, table.getColumnName(currentColumn), JOptionPane.PLAIN_MESSAGE, null);
        }
    }
}
