/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.jira.query;

import com.atlassian.connector.eclipse.internal.jira.core.model.JiraFilter;
import com.atlassian.connector.eclipse.internal.jira.core.model.NamedFilter;
import com.atlassian.connector.eclipse.internal.jira.core.model.filter.FilterDefinition;
import com.atlassian.connector.eclipse.internal.jira.core.model.filter.ProjectFilter;
import com.atlassian.connector.eclipse.internal.jira.core.service.JiraException;
import java.io.File;
import org.netbeans.modules.jira.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import junit.framework.Test;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.netbeans.junit.NbModuleSuite;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.jira.issue.NbJiraIssue;

/**
 *
 * @author tomas
 */
public class JiraQueryTest extends NbTestCase {

    public JiraQueryTest(String arg0) {
        super(arg0);
    }

    @Override
    protected void setUp() throws Exception {    
        super.setUp();
        clearWorkDir();
        System.setProperty("netbeans.user", new File(getWorkDir(), "userdir").getAbsolutePath());
        JiraTestUtil.initClient(getWorkDir());
        // need this to initialize cache -> server defined status values & co
//        getClient().getCache().refreshDetails(JiraTestUtil.nullProgressMonitor);
        JiraTestUtil.cleanProject(JiraTestUtil.getRepositoryConnector(), JiraTestUtil.getTaskRepository(), JiraTestUtil.getClient(), JiraTestUtil.getProject(JiraTestUtil.getClient()));
    }

    public static Test suite () {
        return NbModuleSuite.createConfiguration(JiraQueryTest.class).gui(false).suite();
    }

    public void testQuery() throws Throwable {
        try {
            List<String> ids = new ArrayList<String>();
            ids.add(createIssue("1"));
            ids.add(createIssue("2"));

            FilterDefinition fd = new FilterDefinition();
            fd.setProjectFilter(new ProjectFilter(JiraTestUtil.getProject(JiraTestUtil.getClient())));
            executeFilter(fd, 2);

        } catch (Exception exception) {
            JiraTestUtil.handleException(exception);
        }
    }

    public void testFilters() throws JiraException, CoreException {
        NamedFilter[] filters = JiraTestUtil.getClient().getNamedFilters(JiraTestUtil.nullProgressMonitor);
        assertTrue(filters.length > 0);

        NamedFilter filter = null;
        for (NamedFilter f : filters) {
            if(f.getName().equals("TestFilter")) {
                filter = f;
                break;
            }
        }
        assertNotNull(filter);

        createIssue("blabla");
        executeFilter(filter, 1);
    }

    private String createIssue(String id) throws CoreException, JiraException {
        RepositoryResponse rr = JiraTestUtil.createIssue(JiraTestUtil.getRepositoryConnector(), JiraTestUtil.getTaskRepository(), JiraTestUtil.getClient(), JiraTestUtil.getProject(JiraTestUtil.getClient()), "Kaputt " + id, "Alles Kaputt! " + id, "Bug");
        assertEquals(rr.getReposonseKind(), RepositoryResponse.ResponseKind.TASK_CREATED);
        assertNotNull(JiraTestUtil.getTaskData(JiraTestUtil.getRepositoryConnector(), JiraTestUtil.getTaskRepository(), rr.getTaskId()));
        return rr.getTaskId();
    }

    private void executeFilter(JiraFilter fd, int issuesCount) {
        JiraQuery q = new JiraQuery("testfilter", JiraTestUtil.getRepository(), fd);
        q.refresh();        
        Collection<NbJiraIssue> issues = q.getIssues();
        assertNotNull(issues);
        assertEquals(issuesCount, issues.size());
    }

}
