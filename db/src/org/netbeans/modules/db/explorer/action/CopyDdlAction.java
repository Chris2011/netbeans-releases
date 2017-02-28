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

import javax.swing.ImageIcon;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.lib.ddl.impl.Specification;
import org.netbeans.modules.db.explorer.DatabaseConnection;
import org.netbeans.modules.db.explorer.DatabaseConnector;
import org.netbeans.modules.db.explorer.node.TableNode;
import org.netbeans.modules.db.metadata.model.api.MetadataModel;
import org.netbeans.modules.db.util.DdlUtil;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.awt.NotificationDisplayer;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Christian Lenz
 */
@ActionID(id = "org.netbeans.modules.db.explorer.action.CopyDdlAction", category = "Database")
@ActionRegistration(displayName = "#ViewDDL", lazy = false)
@ActionReferences(value = {
    @ActionReference(path = "Databases/Explorer/Table/Actions", position = 460)
    ,
    @ActionReference(path = "Databases/Explorer/TableList/Actions", position = 460)
})
public class CopyDdlAction extends BaseAction {
    @Override
    public String getName() {
        return NbBundle.getMessage(CopyDdlAction.class, "CopyDDL"); // NOI18N
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx("org.netbeans.modules.db.explorer.action.CopyDdlAction");
    }

    @Override
    public void performAction(final Node[] activatedNodes) {
        final ProgressHandle ph = ProgressHandle.createHandle(this.getName());
        
        ph.setInitialDelay(0);
        ph.start();
        ph.progress("Collecting table structure...");
        
        final DdlUtil ddlUtil = new DdlUtil();
        final DatabaseConnection dbConn = activatedNodes[0].getLookup().lookup(DatabaseConnection.class);

        if (dbConn != null) {
            final DatabaseConnector connector = dbConn.getConnector();
            final MetadataModel model = dbConn.getMetadataModel();
            final Specification spec = connector.getDatabaseSpecification();

            RequestProcessor.getDefault().post(new Runnable() {
                @Override
                public void run() {
                    for (Node activatedNode : activatedNodes) {
                        final TableNode node = activatedNode.getLookup().lookup(TableNode.class);

                        ddlUtil.getTableStructure(node, connector, model, spec);
                    }

                    ph.finish();
                    ddlUtil.copySqlToClipboard();
                    NotificationDisplayer.getDefault().notify("Finished Copy DDL/SQL", new ImageIcon(""), "Finished copying table structure. It is now in your clipboard.", null);
                }
            });
        }
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        return true;
    }
}
