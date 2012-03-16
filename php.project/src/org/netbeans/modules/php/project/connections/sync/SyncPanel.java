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
package org.netbeans.modules.php.project.connections.sync;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.modules.php.project.PhpProject;
import org.netbeans.modules.php.project.ProjectPropertiesSupport;
import org.netbeans.modules.php.project.connections.RemoteClient;
import org.netbeans.modules.php.project.connections.sync.diff.DiffPanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotificationLineSupport;
import org.openide.NotifyDescriptor;
import org.openide.awt.Mnemonics;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 * Panel for remote synchronization.
 */
public final class SyncPanel extends JPanel {

    private static final long serialVersionUID = 1674646546545121L;

    static final Logger LOGGER = Logger.getLogger(SyncPanel.class.getName());

    @StaticResource
    private static final String INFO_ICON_PATH = "org/netbeans/modules/php/project/ui/resources/info_icon.png"; // NOI18N
    @StaticResource
    private static final String DIFF_ICON_PATH = "org/netbeans/modules/php/project/ui/resources/diff.png"; // NOI18N
    @StaticResource
    private static final String RESET_ICON_PATH = "org/netbeans/modules/php/project/ui/resources/reset.png"; // NOI18N
    @StaticResource
    private static final String ERROR_ICON_PATH = "org/netbeans/modules/php/project/ui/resources/error.png"; // NOI18N
    @StaticResource
    private static final String WARNING_ICON_PATH = "org/netbeans/modules/php/project/ui/resources/warning.gif"; // NOI18N

    static final TableCellRenderer DEFAULT_TABLE_CELL_RENDERER = new DefaultTableCellRenderer();
    static final TableCellRenderer ERROR_TABLE_CELL_RENDERER = new DefaultTableCellRenderer();

    final RemoteClient remoteClient;
    // @GuardedBy(AWT)
    final List<SyncItem> allItems;
    // @GuardedBy(AWT)
    final List<SyncItem> displayedItems;
    // @GuardedBy(AWT)
    final FileTableModel tableModel;

    private final PhpProject project;
    private final String remoteConfigurationName;
    // @GuardedBy(AWT)
    private final List<ViewButton> viewButtons;
    // @GuardedBy(AWT)
    private final ItemListener viewButtonListener = new ViewButtonListener();

    // @GuardedBy(AWT)
    private DialogDescriptor descriptor = null;
    // @GuardedBy(AWT)
    private NotificationLineSupport notificationLineSupport = null;


    SyncPanel(PhpProject project, String remoteConfigurationName, List<SyncItem> items, RemoteClient remoteClient) {
        assert SwingUtilities.isEventDispatchThread();
        assert items != null;

        this.project = project;
        this.remoteConfigurationName = remoteConfigurationName;
        this.allItems = items;
        displayedItems = new ArrayList<SyncItem>(items);
        this.remoteClient = remoteClient;
        tableModel = new FileTableModel(displayedItems);

        initComponents();
        viewButtons = getViewButtons();
        initViewButtons();
        initTable();
        initOperationButtons();
        initDiffButton();
        initInfos();
    }

    private JToggleButton createViewButton() {
        ViewButton viewButton = new ViewButton();
        viewButton.addItemListener(viewButtonListener);
        return viewButton;
    }

    @NbBundle.Messages({
        "# {0} - project name",
        "# {1} - remote configuration name",
        "SyncPanel.title=Remote Synchronization for {0}: {1}",
    })
    public boolean open(boolean firstRun) {
        assert SwingUtilities.isEventDispatchThread();
        descriptor = new DialogDescriptor(
                this,
                Bundle.SyncPanel_title(project.getName(), remoteConfigurationName),
                true,
                NotifyDescriptor.OK_CANCEL_OPTION,
                NotifyDescriptor.OK_OPTION,
                null);
        notificationLineSupport = descriptor.createNotificationLineSupport();
        final Dialog dialog = DialogDisplayer.getDefault().createDialog(descriptor);
        descriptor.setButtonListener(new OkActionListener(dialog));
        descriptor.setClosingOptions(new Object[] {NotifyDescriptor.CANCEL_OPTION});
        validateItems();
        updateSyncInfo();
        firstRunInfoLabel.setVisible(firstRun);
        boolean okPressed;
        try {
            dialog.setVisible(true);
            okPressed = descriptor.getValue() == NotifyDescriptor.OK_OPTION;
        } finally {
            dialog.dispose();
        }
        return okPressed;
    }

    public List<SyncItem> getItems() {
        assert SwingUtilities.isEventDispatchThread();
        return allItems;
    }

    private List<ViewButton> getViewButtons() {
        return Arrays.asList(
                (ViewButton) noopToggleButton,
                (ViewButton) downloadToggleButton,
                (ViewButton) uploadToggleButton,
                (ViewButton) deleteToggleButton,
                (ViewButton) symlinkToggleButton,
                (ViewButton) fileDirCollisionToggleButton,
                (ViewButton) fileConflictToggleButton,
                (ViewButton) warningToggleButton,
                (ViewButton) errorToggleButton);
    }

    @NbBundle.Messages({
        "SyncPanel.view.warning=Warning",
        "SyncPanel.view.error=Error"
    })
    private void initViewButtons() {
        // operations
        initViewButton(noopToggleButton, SyncItem.Operation.NOOP);
        initViewButton(downloadToggleButton, EnumSet.of(SyncItem.Operation.DOWNLOAD, SyncItem.Operation.DOWNLOAD_REVIEW));
        initViewButton(uploadToggleButton, EnumSet.of(SyncItem.Operation.UPLOAD, SyncItem.Operation.UPLOAD_REVIEW));
        initViewButton(deleteToggleButton, SyncItem.Operation.DELETE);
        initViewButton(symlinkToggleButton, SyncItem.Operation.SYMLINK);
        initViewButton(fileDirCollisionToggleButton, SyncItem.Operation.FILE_DIR_COLLISION);
        initViewButton(fileConflictToggleButton, SyncItem.Operation.FILE_CONFLICT);
        // warnings & errors
        initViewButton(warningToggleButton, ImageUtilities.loadImageIcon(WARNING_ICON_PATH, false), Bundle.SyncPanel_view_warning());
        initViewButton(errorToggleButton, ImageUtilities.loadImageIcon(ERROR_ICON_PATH, false), Bundle.SyncPanel_view_error());
        ((ViewButton) warningToggleButton).setFilter(new SyncItemFilter() {
            @Override
            public boolean accept(SyncItem syncItem) {
                return syncItem.hasWarning();
            }
        });
        ((ViewButton) errorToggleButton).setFilter(new SyncItemFilter() {
            @Override
            public boolean accept(SyncItem syncItem) {
                return syncItem.hasError();
            }
        });
    }

    private void initViewButton(JToggleButton button, SyncItem.Operation operation) {
        initViewButton(button, EnumSet.of(operation));
    }

    private void initViewButton(JToggleButton button, final EnumSet<SyncItem.Operation> operations) {
        SyncItem.Operation operation = operations.iterator().next();
        initViewButton(button, operation.getIcon(), operation.getTitle());
        ((ViewButton) button).setFilter(new SyncItemFilter() {
            @Override
            public boolean accept(SyncItem syncItem) {
                return operations.contains(syncItem.getOperation());
            }
        });
    }

    private void initViewButton(JToggleButton button, Icon icon, String toolTip) {
        button.setText(null);
        button.setIcon(icon);
        button.setToolTipText(toolTip);
    }

    private void initTable() {
        assert SwingUtilities.isEventDispatchThread();
        // model
        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                validateItems();
                updateSyncInfo();
            }
        });
        itemTable.setModel(tableModel);
        // renderer
        itemTable.setDefaultRenderer(Icon.class, new IconRenderer());
        itemTable.setDefaultRenderer(String.class, new StringRenderer());
        itemTable.setDefaultRenderer(SyncItem.Operation.class, new OperationRenderer());
        // rows
        itemTable.setRowHeight(20);
        // columns
        itemTable.getTableHeader().setReorderingAllowed(false);
        TableColumnModel columnModel = itemTable.getColumnModel();
        columnModel.getColumn(0).setMinWidth(20);
        columnModel.getColumn(0).setMaxWidth(20);
        columnModel.getColumn(0).setResizable(false);
        columnModel.getColumn(1).setPreferredWidth(1000);
        columnModel.getColumn(2).setMinWidth(40);
        columnModel.getColumn(2).setPreferredWidth(40);
        columnModel.getColumn(3).setPreferredWidth(1000);
        // selections
        itemTable.setColumnSelectionAllowed(false);
        itemTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting()) {
                    return;
                }
                setEnabledOperationButtons(itemTable.getSelectedRows());
                setEnabledDiffButton(itemTable.getSelectedRowCount());
            }
        });
        // actions
        itemTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2
                        && diffButton.isEnabled()) {
                    openDiffPanel();
                }
            }
        });
    }

    private void initOperationButtons() {
        // operations
        initOperationButton(noopButton, SyncItem.Operation.NOOP);
        initOperationButton(downloadButton, SyncItem.Operation.DOWNLOAD);
        initOperationButton(uploadButton, SyncItem.Operation.UPLOAD);
        initOperationButton(deleteButton, SyncItem.Operation.DELETE);
        // reset
        initResetButton();
    }

    private void initOperationButton(JButton button, SyncItem.Operation operation) {
        button.setText(null);
        button.setIcon(operation.getIcon());
        button.setToolTipText(operation.getTitle());
        button.addActionListener(new OperationButtonListener(operation));
    }

    @NbBundle.Messages("SyncPanel.resetButton.toolTip=Reset the file to the original state (any changes will be discarded!)")
    private void initResetButton() {
        resetButton.setText(null);
        resetButton.setIcon(ImageUtilities.loadImageIcon(RESET_ICON_PATH, false));
        resetButton.setToolTipText(Bundle.SyncPanel_resetButton_toolTip());
        resetButton.addActionListener(new OperationButtonListener(null));
    }

    @NbBundle.Messages("SyncPanel.diffButton.toolTip=View differences between remote and local file")
    private void initDiffButton() {
        diffButton.setText(null);
        diffButton.setIcon(ImageUtilities.loadImageIcon(DIFF_ICON_PATH, false));
        diffButton.setToolTipText(Bundle.SyncPanel_diffButton_toolTip());
        diffButton.addActionListener(new DiffActionListener());
    }

    private void initInfos() {
        firstRunInfoLabel.setIcon(ImageUtilities.loadImageIcon(INFO_ICON_PATH, false));
        warningLabel.setIcon(ImageUtilities.loadImageIcon(WARNING_ICON_PATH, false));
        syncInfoLabel.setIcon(ImageUtilities.loadImageIcon(INFO_ICON_PATH, false));
    }

    void setEnabledOperationButtons(int[] selectedRows) {
        boolean enabled = areOperationButtonsEnabled(selectedRows);
        noopButton.setEnabled(enabled);
        downloadButton.setEnabled(enabled);
        uploadButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
        resetButton.setEnabled(enabled);
    }

    private boolean areOperationButtonsEnabled(int[] selectedRows) {
        assert SwingUtilities.isEventDispatchThread();
        if (selectedRows.length == 0) {
            return false;
        }
        for (int i : selectedRows) {
            if (!displayedItems.get(i).isOperationChangePossible()) {
                return false;
            }
        }
        return true;
    }

    void setEnabledDiffButton(int selectedRowCount) {
        if (selectedRowCount != 1) {
            diffButton.setEnabled(false);
            return;
        }
        diffButton.setEnabled(getSelectedItem().isDiffPossible());
    }

    SyncItem getSelectedItem() {
        assert SwingUtilities.isEventDispatchThread();
        return displayedItems.get(itemTable.getSelectedRow());
    }

    @NbBundle.Messages({
        "SyncPanel.error.operations=Synchronization not possible. Resolve conflicts first.",
        "SyncPanel.warn.operations=Synchronization possible but warnings should be reviewed first."
    })
    void validateItems() {
        assert SwingUtilities.isEventDispatchThread();
        boolean warn = false;
        for (SyncItem syncItem : allItems) {
            if (syncItem.hasError()) {
                setError(Bundle.SyncPanel_error_operations());
                return;
            }
            if (syncItem.hasWarning()) {
                warn = true;
            }
        }
        if (warn) {
            setWarning(Bundle.SyncPanel_warn_operations());
        } else {
            clearError();
        }
    }

    void setError(String error) {
        notificationLineSupport.setErrorMessage(error);
        descriptor.setValid(false);
    }

    void setWarning(String warning) {
        notificationLineSupport.setWarningMessage(warning);
        descriptor.setValid(true);
    }

    void clearError() {
        notificationLineSupport.clearMessages();
        descriptor.setValid(true);
    }

    @NbBundle.Messages({
        "# {0} - number of files to be downloaded",
        "# {1} - number of files to be uploaded",
        "# {2} - number of files to be deleted",
        "# {3} - number of files without any operation",
        "# {4} - number of files with errors",
        "SyncPanel.info.status=Download: {0} files, upload: {1} files, delete: {2} files, "
            + "no operation: {3} files, errors: {4} files."
    })
    void updateSyncInfo() {
        SyncInfo syncInfo = getSyncInfo();
        syncInfoLabel.setText(Bundle.SyncPanel_info_status(syncInfo.download, syncInfo.upload, syncInfo.delete, syncInfo.noop, syncInfo.errors));
    }

    public SyncInfo getSyncInfo() {
        assert SwingUtilities.isEventDispatchThread();
        SyncInfo syncInfo = new SyncInfo();
        for (SyncItem syncItem : allItems) {
            if (syncItem.hasError()) {
                syncInfo.errors++;
            }
            switch (syncItem.getOperation()) {
                case SYMLINK:
                    // noop
                    break;
                case NOOP:
                    syncInfo.noop++;
                    break;
                case DOWNLOAD:
                case DOWNLOAD_REVIEW:
                    syncInfo.download++;
                    break;
                case UPLOAD:
                case UPLOAD_REVIEW:
                    syncInfo.upload++;
                    break;
                case DELETE:
                    syncInfo.delete++;
                    break;
                case FILE_CONFLICT:
                case FILE_DIR_COLLISION:
                    // noop, already counted
                    break;
                default:
                    assert false : "Unknown operation: " + syncItem.getOperation();
            }
        }
        return syncInfo;
    }

    void openDiffPanel() {
        assert SwingUtilities.isEventDispatchThread();
        assert diffButton.isEnabled() : "Diff button has to be enabled";

        SyncItem syncItem = getSelectedItem();
        DiffPanel diffPanel = new DiffPanel(remoteClient, syncItem, ProjectPropertiesSupport.getEncoding(project));
        try {
            if (diffPanel.open()) {
                assert syncItem.getTmpLocalFile() != null : "TMP local file should be found for " + syncItem;
                syncItem.setOperation(SyncItem.Operation.UPLOAD);
                // need to redraw table
                updateDisplayedItems();
                // reselect the row?
                int index = displayedItems.indexOf(syncItem); // XXX performance?
                if (index != -1) {
                    itemTable.getSelectionModel().setSelectionInterval(index, index);
                }
            }
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Error while saving document", ex);
            setError(Bundle.SyncPanel_error_documentSave());
        }
    }

    /**
     * To preserve correct order and to show items that belong to more
     * view groups (e.g. symlink - warning & symlink).
     */
    void updateDisplayedItems() {
        assert SwingUtilities.isEventDispatchThread();
        displayedItems.clear();
        boolean anyButtonSelected = false;
        for (SyncItem syncItem : allItems) {
            for (ViewButton button : viewButtons) {
                if (button.isSelected()) {
                    anyButtonSelected = true;
                    if (button.getFilter().accept(syncItem)) {
                        displayedItems.add(syncItem);
                        break;
                    }
                }
            }
        }
        if (!anyButtonSelected) {
            displayedItems.addAll(allItems);
        }
        tableModel.fireSyncItemsChange();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form
     * Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        firstRunInfoLabel = new JLabel();
        warningLabel = new JLabel();
        viewLabel = new JLabel();
        noopToggleButton = createViewButton();
        downloadToggleButton = createViewButton();
        uploadToggleButton = createViewButton();
        deleteToggleButton = createViewButton();
        symlinkToggleButton = createViewButton();
        fileDirCollisionToggleButton = createViewButton();
        fileConflictToggleButton = createViewButton();
        warningToggleButton = createViewButton();
        errorToggleButton = createViewButton();
        itemScrollPane = new JScrollPane();
        itemTable = new JTable();
        syncInfoLabel = new JLabel();
        operationLabel = new JLabel();
        diffButton = new JButton();
        noopButton = new JButton();
        downloadButton = new JButton();
        uploadButton = new JButton();
        deleteButton = new JButton();
        resetButton = new JButton();

        Mnemonics.setLocalizedText(firstRunInfoLabel, NbBundle.getMessage(SyncPanel.class, "SyncPanel.firstRunInfoLabel.text")); // NOI18N
        itemTable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        Mnemonics.setLocalizedText(warningLabel, NbBundle.getMessage(SyncPanel.class, "SyncPanel.warningLabel.text")); // NOI18N
        Mnemonics.setLocalizedText(viewLabel, NbBundle.getMessage(SyncPanel.class, "SyncPanel.viewLabel.text")); // NOI18N

        noopToggleButton.setIcon(new ImageIcon(getClass().getResource("/org/netbeans/modules/php/project/ui/resources/diff.png"))); Mnemonics.setLocalizedText(downloadToggleButton, " "); // NOI18N
        Mnemonics.setLocalizedText(uploadToggleButton, " "); // NOI18N
        Mnemonics.setLocalizedText(deleteToggleButton, " "); // NOI18N
        Mnemonics.setLocalizedText(symlinkToggleButton, " "); // NOI18N
        Mnemonics.setLocalizedText(fileDirCollisionToggleButton, " "); // NOI18N
        Mnemonics.setLocalizedText(fileConflictToggleButton, " "); // NOI18N
        Mnemonics.setLocalizedText(warningToggleButton, " "); // NOI18N
        Mnemonics.setLocalizedText(errorToggleButton, " "); // NOI18N

        itemTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        itemScrollPane.setViewportView(itemTable);

        Mnemonics.setLocalizedText(syncInfoLabel, "SYNC INFO LABEL"); // NOI18N
        Mnemonics.setLocalizedText(operationLabel, NbBundle.getMessage(SyncPanel.class, "SyncPanel.operationLabel.text")); // NOI18N

        diffButton.setIcon(new ImageIcon(getClass().getResource("/org/netbeans/modules/php/project/ui/resources/diff.png")));         diffButton.setEnabled(false);

        Mnemonics.setLocalizedText(noopButton, " "); // NOI18N
        noopButton.setEnabled(false);

        Mnemonics.setLocalizedText(downloadButton, " "); // NOI18N
        downloadButton.setEnabled(false);

        Mnemonics.setLocalizedText(uploadButton, " "); // NOI18N
        uploadButton.setEnabled(false);

        Mnemonics.setLocalizedText(deleteButton, " "); // NOI18N
        deleteButton.setEnabled(false);

        Mnemonics.setLocalizedText(resetButton, " "); // NOI18N
        resetButton.setEnabled(false);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup()
                .addContainerGap()

                .addGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(itemScrollPane).addGroup(layout.createSequentialGroup()

                        .addGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup()
                                .addComponent(operationLabel)

                                .addPreferredGap(ComponentPlacement.RELATED).addComponent(diffButton).addGap(18, 18, 18).addComponent(noopButton).addPreferredGap(ComponentPlacement.RELATED).addComponent(downloadButton).addPreferredGap(ComponentPlacement.RELATED).addComponent(uploadButton).addPreferredGap(ComponentPlacement.RELATED).addComponent(deleteButton).addPreferredGap(ComponentPlacement.RELATED).addComponent(resetButton)).addComponent(firstRunInfoLabel).addComponent(syncInfoLabel).addComponent(warningLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addGroup(layout.createSequentialGroup()
                                .addComponent(viewLabel)

                                .addPreferredGap(ComponentPlacement.RELATED).addComponent(noopToggleButton).addPreferredGap(ComponentPlacement.RELATED).addComponent(downloadToggleButton).addPreferredGap(ComponentPlacement.RELATED).addComponent(uploadToggleButton).addPreferredGap(ComponentPlacement.RELATED).addComponent(deleteToggleButton).addPreferredGap(ComponentPlacement.RELATED).addComponent(symlinkToggleButton).addPreferredGap(ComponentPlacement.RELATED).addComponent(fileDirCollisionToggleButton).addPreferredGap(ComponentPlacement.RELATED).addComponent(fileConflictToggleButton).addPreferredGap(ComponentPlacement.RELATED).addComponent(warningToggleButton).addPreferredGap(ComponentPlacement.RELATED).addComponent(errorToggleButton))).addGap(0, 0, Short.MAX_VALUE))).addContainerGap())
        );

        layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {deleteButton, downloadButton, noopButton, resetButton, uploadButton});

        layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {deleteToggleButton, downloadToggleButton, errorToggleButton, fileConflictToggleButton, fileDirCollisionToggleButton, noopToggleButton, symlinkToggleButton, uploadToggleButton, warningToggleButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(firstRunInfoLabel)

                .addPreferredGap(ComponentPlacement.UNRELATED).addComponent(warningLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.UNRELATED).addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(viewLabel).addComponent(noopToggleButton).addComponent(downloadToggleButton).addComponent(uploadToggleButton).addComponent(deleteToggleButton).addComponent(symlinkToggleButton).addComponent(fileDirCollisionToggleButton).addComponent(fileConflictToggleButton).addComponent(warningToggleButton).addComponent(errorToggleButton)).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(itemScrollPane).addPreferredGap(ComponentPlacement.RELATED).addComponent(syncInfoLabel).addPreferredGap(ComponentPlacement.UNRELATED).addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(diffButton).addComponent(noopButton).addComponent(downloadButton).addComponent(uploadButton).addComponent(deleteButton).addComponent(resetButton).addComponent(operationLabel)))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton deleteButton;
    private JToggleButton deleteToggleButton;
    private JButton diffButton;
    private JButton downloadButton;
    private JToggleButton downloadToggleButton;
    private JToggleButton errorToggleButton;
    private JToggleButton fileConflictToggleButton;
    private JToggleButton fileDirCollisionToggleButton;
    private JLabel firstRunInfoLabel;
    private JScrollPane itemScrollPane;
    private JTable itemTable;
    private JButton noopButton;
    private JToggleButton noopToggleButton;
    private JLabel operationLabel;
    private JButton resetButton;
    private JToggleButton symlinkToggleButton;
    private JLabel syncInfoLabel;
    private JButton uploadButton;
    private JToggleButton uploadToggleButton;
    private JLabel viewLabel;
    private JLabel warningLabel;
    private JToggleButton warningToggleButton;
    // End of variables declaration//GEN-END:variables

    //~ Inner classes

    private static final class FileTableModel extends AbstractTableModel {

        private static final long serialVersionUID = 16478634354314324L;

        @NbBundle.Messages({
            "SyncPanel.table.column.remote.title=Remote Path",
            "SyncPanel.table.column.local.title=Local Path"
        })
        private static final String[] COLUMNS = {
            "", // NOI18N
            Bundle.SyncPanel_table_column_remote_title(),
            "", // NOI18N
            Bundle.SyncPanel_table_column_local_title(),
        };

        private final List<SyncItem> items;


        public FileTableModel(List<SyncItem> items) {
            assert SwingUtilities.isEventDispatchThread();
            this.items = items;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            assert SwingUtilities.isEventDispatchThread();
            return false;
        }

        @Override
        public int getRowCount() {
            assert SwingUtilities.isEventDispatchThread();
            return items.size();
        }

        @Override
        public int getColumnCount() {
            assert SwingUtilities.isEventDispatchThread();
            return COLUMNS.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            assert SwingUtilities.isEventDispatchThread();
            SyncItem syncItem = items.get(rowIndex);
            if (columnIndex == 0) {
                if (syncItem.hasError()) {
                    return ImageUtilities.loadImageIcon(ERROR_ICON_PATH, false);
                }
                if (syncItem.hasWarning()) {
                    return ImageUtilities.loadImageIcon(WARNING_ICON_PATH, false);
                }
                return null;
            } else if (columnIndex == 1) {
                return syncItem.getRemotePath();
            } else if (columnIndex == 2) {
                return syncItem.getOperation();
            } else if (columnIndex == 3) {
                return syncItem.getLocalPath();
            }
            throw new IllegalStateException("Unknown column index: " + columnIndex);
        }

        @Override
        public String getColumnName(int column) {
            assert SwingUtilities.isEventDispatchThread();
            return COLUMNS[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            assert SwingUtilities.isEventDispatchThread();
            if (columnIndex == 0) {
                return Icon.class;
            } else if (columnIndex == 1
                    || columnIndex == 3) {
                return String.class;
            } else if (columnIndex == 2) {
                return SyncItem.Operation.class;
            }
            throw new IllegalStateException("Unknown column index: " + columnIndex);
        }

        public void fireSyncItemsChange() {
            assert SwingUtilities.isEventDispatchThread();
            fireTableDataChanged();
        }

    }

    private final class IconRenderer implements TableCellRenderer {

        private static final long serialVersionUID = -46865321321L;


        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            assert SwingUtilities.isEventDispatchThread();
            Icon icon = (Icon) value;
            JLabel rendererComponent = (JLabel) DEFAULT_TABLE_CELL_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            rendererComponent.setHorizontalAlignment(SwingConstants.CENTER);
            rendererComponent.setToolTipText(displayedItems.get(row).getMessage());
            rendererComponent.setText(null);
            rendererComponent.setIcon(icon);
            return rendererComponent;
        }

    }

    private final class StringRenderer implements TableCellRenderer {

        private static final long serialVersionUID = 567654543546954L;


        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String text = (String) value;
            JLabel rendererComponent = (JLabel) DEFAULT_TABLE_CELL_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            rendererComponent.setHorizontalAlignment(SwingConstants.LEFT);
            rendererComponent.setToolTipText(text);
            rendererComponent.setText(text);
            rendererComponent.setIcon(null);
            return rendererComponent;
        }

    }

    private final class OperationRenderer implements TableCellRenderer {

        private static final long serialVersionUID = -6786654671313465458L;


        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel rendererComponent = (JLabel) DEFAULT_TABLE_CELL_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            SyncItem.Operation operation = (SyncItem.Operation) value;
            rendererComponent.setIcon(operation.getIcon());
            rendererComponent.setToolTipText(operation.getTitle());
            rendererComponent.setText(null);
            rendererComponent.setHorizontalAlignment(SwingConstants.CENTER);
            return rendererComponent;
        }

    }

    private final class OperationButtonListener implements ActionListener {

        private final SyncItem.Operation operation;


        public OperationButtonListener(SyncItem.Operation operation) {
            this.operation = operation;
        }

        // can be done in background thread if needed
        @Override
        public void actionPerformed(ActionEvent e) {
            assert SwingUtilities.isEventDispatchThread();
            int[] selectedRows = itemTable.getSelectedRows();
            assert selectedRows.length > 0;
            List<SyncItem> selectedItems = new ArrayList<SyncItem>(selectedRows.length);
            for (int index : selectedRows) {
                SyncItem syncItem = displayedItems.get(index);
                selectedItems.add(syncItem);
                if (operation == null) {
                    syncItem.resetOperation();
                } else {
                    syncItem.setOperation(operation);
                }
            }
            // need to redraw table
            updateDisplayedItems();
            // reselect the rows?
            for (SyncItem item : selectedItems) {
                int index = displayedItems.indexOf(item); // XXX performance?
                if (index != -1) {
                    itemTable.getSelectionModel().addSelectionInterval(index, index);
                }
            }
        }

    }

    private final class DiffActionListener implements ActionListener {

        @NbBundle.Messages("SyncPanel.error.documentSave=Cannot save file content.")
        @Override
        public void actionPerformed(ActionEvent e) {
            openDiffPanel();
        }

    }

    private final class OkActionListener implements ActionListener {

        private final Dialog dialog;


        public OkActionListener(Dialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == NotifyDescriptor.OK_OPTION) {
                SyncInfo syncInfo = getSyncInfo();
                SummaryPanel panel = new SummaryPanel(
                        syncInfo.upload,
                        syncInfo.download,
                        syncInfo.delete,
                        syncInfo.noop);
                if (panel.open()) {
                    dialog.setVisible(false);
                }
            }
        }

    }

    private static final class ViewButton extends JToggleButton {

        private static final long serialVersionUID = 874534687646546546L;

        private SyncItemFilter filter;


        public SyncItemFilter getFilter() {
            return filter;
        }

        public void setFilter(SyncItemFilter filter) {
            this.filter = filter;
        }

    }

    private interface SyncItemFilter {
        boolean accept(SyncItem syncItem);
    }

    private class ViewButtonListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            updateDisplayedItems();
        }

    }

    public static final class SyncInfo {

        public int download = 0;
        public int upload = 0;
        public int delete = 0;
        public int noop = 0;
        public int errors = 0;

    }

}
