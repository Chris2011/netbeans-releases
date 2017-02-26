/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010-2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.db.explorer.action;

import java.util.Collection;
import org.netbeans.api.db.explorer.DatabaseException;
import org.netbeans.lib.ddl.CommandNotSupportedException;
import org.netbeans.lib.ddl.DDLException;
import org.netbeans.lib.ddl.impl.CreateTable;
import org.netbeans.lib.ddl.impl.Specification;
import org.netbeans.lib.ddl.impl.TableColumn;
import org.netbeans.modules.db.explorer.DatabaseConnection;
import org.netbeans.modules.db.explorer.DatabaseConnector;
import org.netbeans.modules.db.explorer.node.TableNode;
import org.netbeans.modules.db.metadata.model.api.Action;
import org.netbeans.modules.db.metadata.model.api.Column;
import org.netbeans.modules.db.metadata.model.api.Metadata;
import org.netbeans.modules.db.metadata.model.api.MetadataModel;
import org.netbeans.modules.db.metadata.model.api.MetadataModelException;
import org.netbeans.modules.db.metadata.model.api.Table;
import org.netbeans.modules.db.util.ShowSQLDialog;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.windows.WindowManager;

/**
 *
 * @author Christian Lenz
 */
@ActionID(id = "org.netbeans.modules.db.explorer.action.ViewDdlAction", category = "Database")
@ActionRegistration(displayName = "#ViewDDL", lazy = false)
@ActionReferences(value = {
    @ActionReference(path = "Databases/Explorer/Table/Actions", position = 450),
    @ActionReference(path = "Databases/Explorer/TableList/Actions", position = 450)
})
public class ViewDdlAction extends BaseAction {

    private final StringBuilder createCommands = new StringBuilder();
    private final ShowSQLDialog dialog = new ShowSQLDialog();

    @Override
    public String getName() {
        return NbBundle.getMessage(ViewDdlAction.class, "ViewDDL"); // NOI18N
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx("org.netbeans.modules.db.explorer.action.ViewDdlAction");
    }

    @Override
    public void performAction(final Node[] activatedNodes) {
        dialog.setLocationRelativeTo(WindowManager.getDefault().getMainWindow());

        final DatabaseConnection dbConn = activatedNodes[0].getLookup().lookup(DatabaseConnection.class);

        if (dbConn != null) {
            final DatabaseConnector connector = dbConn.getConnector();
            final MetadataModel model = dbConn.getMetadataModel();
            final Specification spec = connector.getDatabaseSpecification();

            RequestProcessor.getDefault().post(new Runnable() {
                @Override
                public void run() {
                    dialog.setText("");

                    for (Node activatedNode : activatedNodes) {
                        final TableNode node = activatedNode.getLookup().lookup(TableNode.class);

                        try {
                            getTableStructure(node, connector, model, spec);
                        } catch (MetadataModelException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
            });
            
            dialog.setVisible(true);
        }
    }

    private void getTableStructure(final TableNode node, final DatabaseConnector connector, MetadataModel model, final Specification spec) throws MetadataModelException {
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

                    createCommands.append("# Table structure of ").append(tablename).append("\n");
                    createCommands.append(createCommandCreateTable.getCommand()).append("\n\n");
                    dialog.setText(createCommands.toString()); // NOI18N
                } catch (DatabaseException | CommandNotSupportedException | DDLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        return true;
    }
}