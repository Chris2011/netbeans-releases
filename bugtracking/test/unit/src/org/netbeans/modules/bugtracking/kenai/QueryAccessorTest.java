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

package org.netbeans.modules.bugtracking.kenai;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.bugtracking.BugtrackingManager;
import org.netbeans.modules.bugtracking.spi.BugtrackingConnector;
import org.netbeans.modules.bugtracking.spi.KenaiSupport;
import org.netbeans.modules.kenai.api.Kenai;
import org.netbeans.modules.kenai.api.KenaiProject;
import org.netbeans.modules.kenai.ui.spi.QueryAccessor;
import org.openide.util.Lookup;

/**
 *
 * @author tomas
 */
public class QueryAccessorTest extends NbTestCase {

    private Kenai instance;
    private TaskRepositoryManager trm;
    private BugzillaRepositoryConnector brc;

    public QueryAccessorTest(String arg0) {
        super(arg0);
    }

    @Override
    protected Level logLevel() {
        return Level.ALL;
    }

    @Override
    protected void setUp() throws Exception {
        try {
            System.setProperty("kenai.com.url","http://testkenai.com");
            instance = Kenai.getDefault();
            BufferedReader br = new BufferedReader(new FileReader(new File(System.getProperty("user.home"), ".test-kenai")));
            String username = br.readLine();
            String password = br.readLine();
            br.close();
            instance.login(username, password.toCharArray());

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        BugzillaCorePlugin bcp = new BugzillaCorePlugin();
        try {
            bcp.start(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        trm = new TaskRepositoryManager();
        brc = new BugzillaRepositoryConnector();

        trm.addRepositoryConnector(brc);

        WebUtil.init();
    }
    
    public void testGetRepository () throws Throwable {
        KenaiProject prj = instance.getProject("koliba");
        assertNotNull(prj);

        Collection<? extends QueryAccessor> c = Lookup.getDefault().lookupAll(QueryAccessor.class);
        assertNotNull(c);
        assertTrue(c.size() > 0);
        
        QueryAccessorImpl qa = null;
        for (QueryAccessor a : c) {
            if(a instanceof QueryAccessorImpl) {
                qa = (QueryAccessorImpl) a;
                break;
            }
        }
        assertNotNull(qa);
        KenaiDashboardDummy kdd = new KenaiDashboardDummy(qa, prj);
        kdd.setVisible(true);
        while(kdd.isVisible()) {
            Thread.sleep(1000);
        }
    }

}
