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

package org.netbeans.modules.db.sql.history;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author John Baker
 */
public class SQLHistoryModelImpl implements SQLHistoryModel {    
    public static final String SQL_HISTORY_FOLDER = "Databases/SQLHISTORY"; // NOI18N
    public static final String SQL_HISTORY_FILE_NAME = "sql_history";  // NOI18N
    public static final Logger LOGGER = Logger.getLogger(SQLHistoryModelImpl.class.getName());

    List<SQLHistory> sqlHistoryList = new ArrayList<SQLHistory>();
    
    public void initialize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setFilter(String filter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getFilter() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<SQLHistory> getSQLHistoryList() {
        List<SQLHistory> retrievedSQL = new ArrayList<SQLHistory>();
        try {
            FileObject root = Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject(SQL_HISTORY_FOLDER);
            String historyFilePath = FileUtil.getFileDisplayName(root) + File.separator + SQL_HISTORY_FILE_NAME + ".xml"; // NOI18N
            retrievedSQL = SQLHistoryPersistenceManager.getInstance().retrieve(historyFilePath, FileUtil.toFileObject(new File(historyFilePath)));
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        if (null == retrievedSQL) {
            LOGGER.log(Level.WARNING, NbBundle.getMessage(SQLHistoryModelImpl.class, "MSG_SQLHistoryFileError"));
            return new ArrayList<SQLHistory>();
        } else {
            return retrievedSQL;
        }
    }

    public void setSQLHistoryList(List<SQLHistory> sqlHistoryList) {
        this.sqlHistoryList = sqlHistoryList;
    }
}
