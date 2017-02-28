/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
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
 */
package org.netbeans.modules.db.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.Collection;
import org.netbeans.api.db.explorer.DatabaseException;
import org.netbeans.lib.ddl.CommandNotSupportedException;
import org.netbeans.lib.ddl.DDLException;
import org.netbeans.lib.ddl.impl.CreateTable;
import org.netbeans.lib.ddl.impl.Specification;
import org.netbeans.lib.ddl.impl.TableColumn;
import org.netbeans.modules.db.explorer.DatabaseConnector;
import org.netbeans.modules.db.explorer.node.TableNode;
import org.netbeans.modules.db.metadata.model.api.Action;
import org.netbeans.modules.db.metadata.model.api.Column;
import org.netbeans.modules.db.metadata.model.api.Metadata;
import org.netbeans.modules.db.metadata.model.api.MetadataModel;
import org.netbeans.modules.db.metadata.model.api.MetadataModelException;
import org.netbeans.modules.db.metadata.model.api.Table;
import org.openide.util.Exceptions;
import org.openide.windows.WindowManager;

/**
 *
 * @author Chrl
 */
public class DdlUtil implements ClipboardOwner {
    private final StringBuilder createCommand;
    private ShowSQLDialog sqlDialog;

    public DdlUtil() {
        createCommand = new StringBuilder();
    }

    public void initSqlDialog() {
        sqlDialog = new ShowSQLDialog();
        sqlDialog.setLocationRelativeTo(WindowManager.getDefault().getMainWindow());
    }
    
    public void copySqlToClipboard() {
        StringSelection stringSelection = new StringSelection(createCommand.toString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, this);
    }

    public void showSqlDialog() {
        sqlDialog.setText(createCommand.toString());
        sqlDialog.setCursorToTop();
        sqlDialog.setVisible(true);
    }

    public void getTableStructure(final TableNode node, final DatabaseConnector connector, MetadataModel model, final Specification spec) {
        try {
            model.runReadAction(new Action<Metadata>() {
                @Override
                public void run(final Metadata metaData) {
                    String tablename = node.getName();
                    Table table = node.getTableHandle().resolve(metaData);

                    try {
                        CreateTable createCommandCreateTable = spec.createCommandCreateTable(tablename);
                        Collection<Column> columns = table.getColumns();

                        for (Column column : columns) {
                            TableColumn col = connector.getColumnSpecification(table, column);
                            createCommandCreateTable.getColumns().add(col);
                        }

                        createCommand.append("# Table structure of ").append(tablename).append("\n");
                        createCommand.append(createCommandCreateTable.getCommand()).append("\n\n");
                    } catch (DatabaseException | CommandNotSupportedException | DDLException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (MetadataModelException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {}
}