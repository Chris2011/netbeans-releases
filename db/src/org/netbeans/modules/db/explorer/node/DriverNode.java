/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.db.explorer.node;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.db.explorer.DatabaseException;
import org.netbeans.api.db.explorer.JDBCDriver;
import org.netbeans.api.db.explorer.JDBCDriverManager;
import org.netbeans.api.db.explorer.node.BaseNode;
import org.netbeans.api.db.explorer.node.NodeProvider;
import org.netbeans.modules.db.explorer.DatabaseDriver;
import org.netbeans.modules.db.explorer.infos.DriverNodeInfo;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;

/**
 *
 * @author Rob Englander
 */
public class DriverNode extends BaseNode {
    private static final String PREFERREDICONBASE = "org/netbeans/modules/db/resources/driverPrefered.gif";
    private static final String FOLDER = "Driver"; //NOI18N
    
    private DatabaseDriver databaseDriver;
    
    /** 
     * Create an instance of DriverNode.
     * 
     * @param dataLookup the lookup to use when creating node providers
     * @return the DriverNode instance
     */
    public static DriverNode create(NodeDataLookup dataLookup, NodeProvider provider) {
        DriverNode node = new DriverNode(dataLookup, provider);
        node.setup();
        return node;
    }

    private DriverNode(NodeDataLookup lookup, NodeProvider provider) {
        super(lookup, FOLDER, provider);
    }

    @Override
    protected void initialize() {
        StringBuffer sb = new StringBuffer();
        
        JDBCDriver driver = getLookup().lookup(JDBCDriver.class);
        for (int j = 0; j < driver.getURLs().length; j++) {
            if (j != 0)
                sb.append(", "); //NOI18N
            String file = driver.getURLs()[j].getFile();
            if (Utilities.isWindows())
                file = file.substring(1);
            sb.append(file);
        }
        
        databaseDriver = new DatabaseDriver(driver.getDisplayName(), driver.getClassName(), sb.toString(), driver);
    }

    public DatabaseDriver getDatabaseDriver() {
        return databaseDriver;
    }
    
    @Override
    public boolean canDestroy() {
        return true;
    }
    
    @Override
    public void destroy() {
        RequestProcessor.getDefault().post(
            new Runnable() {
                public void run() {
                    try {
                        JDBCDriver driver = databaseDriver.getJDBCDriver();
                        if (driver != null) {
                            JDBCDriverManager.getDefault().removeDriver(driver);
                        }
                    } catch (DatabaseException e) {
                        Logger.getLogger(DriverNodeInfo.class.getName()).log(Level.INFO, null, e);
                    }
                }
            }
        );
    }
    
    public String getName() {
        return databaseDriver.getName();
    }

    @Override
    public String getDisplayName() {
        return databaseDriver.getName();
    }
 
    public String getIconBase() {
        return PREFERREDICONBASE;
    }
}
