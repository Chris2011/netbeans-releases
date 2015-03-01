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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.git.remote.cli.jgit.commands;

import org.netbeans.modules.git.remote.cli.jgit.commands.LogCommand;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import org.netbeans.modules.git.remote.cli.ApiUtils;
import org.netbeans.modules.git.remote.cli.GitClient;
import org.netbeans.modules.git.remote.cli.GitConstants;
import org.netbeans.modules.git.remote.cli.GitException;
import org.netbeans.modules.git.remote.cli.GitRevisionInfo;
import org.netbeans.modules.git.remote.cli.GitRevisionInfo.GitFileInfo;
import org.netbeans.modules.git.remote.cli.GitUser;
import org.netbeans.modules.git.remote.cli.SearchCriteria;
import org.netbeans.modules.git.remote.cli.jgit.AbstractGitTestCase;
import org.netbeans.modules.git.remote.cli.jgit.JGitRepository;
import org.netbeans.modules.versioning.core.api.VCSFileProxy;

/**
 *
 * @author ondra
 */
public class LogTest extends AbstractGitTestCase {
    private static final boolean KIT = LogCommand.KIT;
    private JGitRepository repository;
    private VCSFileProxy workDir;

    public LogTest (String testName) throws IOException {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        workDir = getWorkingDirectory();
        repository = getLocalGitRepository();
    }

    public void testLogRevision () throws Exception {
        VCSFileProxy f = VCSFileProxy.createFileProxy(workDir, "testcat1");
        write(f, "initial content");
        VCSFileProxy[] files = new VCSFileProxy[] { f };
        add(files);

        GitClient client = getClient(workDir);
        GitRevisionInfo revision1 = client.commit(files, "commit1", null, null, NULL_PROGRESS_MONITOR);
        GitRevisionInfo revision = client.log(revision1.getRevision(), NULL_PROGRESS_MONITOR);
        assertRevisions(revision1, revision);

        write(f, "modification");
        add(files);
        GitRevisionInfo revision2 = client.commit(files, "commit2", null, null, NULL_PROGRESS_MONITOR);
        revision = client.log(revision1.getRevision(), NULL_PROGRESS_MONITOR);
        assertRevisions(revision1, revision);

        revision = client.log(revision2.getRevision(), NULL_PROGRESS_MONITOR);
        assertRevisions(revision2, revision);
    }

    public void testLogRevisionTo () throws Exception {
        VCSFileProxy f = VCSFileProxy.createFileProxy(workDir, "testcat1");
        write(f, "initial content");
        VCSFileProxy[] files = new VCSFileProxy[] { f };
        add(files);
        GitClient client = getClient(workDir);
        GitRevisionInfo revision0 = client.commit(files, "initial commit", null, null, NULL_PROGRESS_MONITOR);

        write(f, "modification1");
        add(files);

        GitRevisionInfo revision1 = client.commit(files, "modification1", null, null, NULL_PROGRESS_MONITOR);
        
        write(f, "modification2");
        add(files);
        GitRevisionInfo revision2 = client.commit(files, "modification2", null, null, NULL_PROGRESS_MONITOR);
        
        write(f, "modification3");
        add(files);
        GitRevisionInfo revision3 = client.commit(files, "modification3", null, null, NULL_PROGRESS_MONITOR);

        write(f, "modification4");
        add(files);
        GitRevisionInfo revision4 = client.commit(files, "modification4", null, null, NULL_PROGRESS_MONITOR);
        SearchCriteria crit = new SearchCriteria();
        crit.setRevisionTo(revision4.getRevision());
        GitRevisionInfo[] revisions = client.log(crit, true, NULL_PROGRESS_MONITOR);
        assertEquals(5, revisions.length);
        assertRevisions(revision4, revisions[0]);
        assertRevisions(revision3, revisions[1]);
        assertRevisions(revision2, revisions[2]);
        assertRevisions(revision1, revisions[3]);
        assertRevisions(revision0, revisions[4]);
    }
    
    public void testLogRevisionRange () throws Exception {
        VCSFileProxy f = VCSFileProxy.createFileProxy(workDir, "testcat1");
        write(f, "initial content");
        VCSFileProxy[] files = new VCSFileProxy[] { f };
        add(files);
        commit(files);

        write(f, "modification1");
        add(files);

        GitClient client = getClient(workDir);
        GitRevisionInfo revision1 = client.commit(files, "modification1", null, null, NULL_PROGRESS_MONITOR);

        
        write(f, "modification2");
        add(files);
        GitRevisionInfo revision2 = client.commit(files, "modification2", null, null, NULL_PROGRESS_MONITOR);
        
        write(f, "modification3");
        add(files);
        GitRevisionInfo revision3 = client.commit(files, "modification3", null, null, NULL_PROGRESS_MONITOR);
        SearchCriteria crit = new SearchCriteria();
        crit.setRevisionFrom(revision2.getRevision());
        crit.setRevisionTo(revision3.getRevision());
        GitRevisionInfo[] revisions = client.log(crit, true, NULL_PROGRESS_MONITOR);
        assertEquals(2, revisions.length);
        assertRevisions(revision3, revisions[0]);
        assertRevisions(revision2, revisions[1]);

        write(f, "modification4");
        add(files);
        GitRevisionInfo revision4 = client.commit(files, "modification4", null, null, NULL_PROGRESS_MONITOR);
        crit = new SearchCriteria();
        crit.setRevisionFrom(revision2.getRevision());
        crit.setRevisionTo(revision4.getRevision());
        revisions = client.log(crit, true, NULL_PROGRESS_MONITOR);
        assertEquals(3, revisions.length);
        assertRevisions(revision4, revisions[0]);
        assertRevisions(revision3, revisions[1]);
        assertRevisions(revision2, revisions[2]);
    }
    
    public void testLogSingleBranch () throws Exception {
        VCSFileProxy f = VCSFileProxy.createFileProxy(workDir, "file");
        write(f, "initial content");
        VCSFileProxy[] files = new VCSFileProxy[] { f };
        add(files);
        GitClient client = getClient(workDir);
        GitRevisionInfo revision0 = client.commit(files, "initial commit", null, null, NULL_PROGRESS_MONITOR);

        write(f, "modification1");
        add(files);

        GitRevisionInfo revision1 = client.commit(files, "modification1", null, null, NULL_PROGRESS_MONITOR);
        
        write(VCSFileProxy.createFileProxy(workDir, ".git/refs/heads/A"), revision1.getRevision());
        write(VCSFileProxy.createFileProxy(workDir, ".git/refs/heads/B"), revision1.getRevision());
        write(VCSFileProxy.createFileProxy(workDir, ".git/HEAD"), "ref: refs/heads/A");
        Thread.sleep(1000);
        write(f, "modificationOnA-1");
        add(files);
        GitRevisionInfo revisionA1 = client.commit(files, "modificationOnA-1", null, null, NULL_PROGRESS_MONITOR);
        // to B
        write(VCSFileProxy.createFileProxy(workDir, ".git/HEAD"), "ref: refs/heads/B");
        client.reset(revision1.getRevision(), GitClient.ResetType.SOFT, NULL_PROGRESS_MONITOR);
        Thread.sleep(1000);
        write(f, "modificationOnB-1");
        add(files);
        GitRevisionInfo revisionB1 = client.commit(files, "modificationOnB-1", null, null, NULL_PROGRESS_MONITOR);
        
        // to A
        write(VCSFileProxy.createFileProxy(workDir, ".git/HEAD"), "ref: refs/heads/A");
        client.reset(revisionA1.getRevision(), GitClient.ResetType.SOFT, NULL_PROGRESS_MONITOR);
        Thread.sleep(1000);
        write(f, "modificationOnA-2");
        add(files);
        GitRevisionInfo revisionA2 = client.commit(files, "modificationOnA-2", null, null, NULL_PROGRESS_MONITOR);

        // to B
        write(VCSFileProxy.createFileProxy(workDir, ".git/HEAD"), "ref: refs/heads/B");
        client.reset(revisionB1.getRevision(), GitClient.ResetType.SOFT, NULL_PROGRESS_MONITOR);
        Thread.sleep(1000);
        write(f, "modificationOnB-2");
        add(files);
        GitRevisionInfo revisionB2 = client.commit(files, "modificationOnB-2", null, null, NULL_PROGRESS_MONITOR);
        SearchCriteria crit = new SearchCriteria();
        crit.setRevisionTo("A");
        GitRevisionInfo[] revisions = client.log(crit, true, NULL_PROGRESS_MONITOR);
        assertEquals(4, revisions.length);
        assertRevisions(revisionA2, revisions[0]);
        assertRevisions(revisionA1, revisions[1]);
        assertRevisions(revision1, revisions[2]);
        assertRevisions(revision0, revisions[3]);
        
        crit = new SearchCriteria();
        crit.setRevisionTo("B");
        revisions = client.log(crit, true, NULL_PROGRESS_MONITOR);
        assertEquals(4, revisions.length);
        assertRevisions(revisionB2, revisions[0]);
        assertRevisions(revisionB1, revisions[1]);
        assertRevisions(revision1, revisions[2]);
        assertRevisions(revision0, revisions[3]);
        
        // try both branches, how are the revisions sorted?
        revisions = client.log(new SearchCriteria(), true, NULL_PROGRESS_MONITOR);
        assertEquals(6, revisions.length);
        assertRevisions(revisionB2, revisions[0]);
        assertRevisions(revisionA2, revisions[1]);
        assertRevisions(revisionB1, revisions[2]);
        assertRevisions(revisionA1, revisions[3]);
        assertRevisions(revision1, revisions[4]);
        assertRevisions(revision0, revisions[5]);
    }
    
    public void testLogLimit () throws Exception {
        VCSFileProxy f = VCSFileProxy.createFileProxy(workDir, "testcat1");
        write(f, "initial content");
        VCSFileProxy[] files = new VCSFileProxy[] { f };
        add(files);
        commit(files);

        write(f, "modification1");
        add(files);

        GitClient client = getClient(workDir);
        GitRevisionInfo revision1 = client.commit(files, "modification1", null, null, NULL_PROGRESS_MONITOR);
        
        write(f, "modification2");
        add(files);
        GitRevisionInfo revision2 = client.commit(files, "modification2", null, null, NULL_PROGRESS_MONITOR);
        
        write(f, "modification3");
        add(files);
        GitRevisionInfo revision3 = client.commit(files, "modification3", null, null, NULL_PROGRESS_MONITOR);

        write(f, "modification4");
        add(files);
        GitRevisionInfo revision4 = client.commit(files, "modification4", null, null, NULL_PROGRESS_MONITOR);
        SearchCriteria crit = new SearchCriteria();
        crit.setRevisionFrom(revision2.getRevision());
        crit.setRevisionTo(revision4.getRevision());
        GitRevisionInfo[] revisions = client.log(crit, true, NULL_PROGRESS_MONITOR);
        assertEquals(3, revisions.length);
        assertRevisions(revision4, revisions[0]);
        assertRevisions(revision3, revisions[1]);
        assertRevisions(revision2, revisions[2]);
        
        crit = new SearchCriteria();
        crit.setRevisionFrom(revision2.getRevision());
        crit.setRevisionTo(revision4.getRevision());
        crit.setLimit(2);
        revisions = client.log(crit, true, NULL_PROGRESS_MONITOR);
        assertEquals(2, revisions.length);
        assertRevisions(revision4, revisions[0]);
        assertRevisions(revision3, revisions[1]);
    }
    
    public void testLogFiles () throws Exception {
        VCSFileProxy f1 = VCSFileProxy.createFileProxy(workDir, "file1");
        write(f1, "initial content");
        VCSFileProxy f2 = VCSFileProxy.createFileProxy(workDir, "file2");
        write(f2, "initial content");
        VCSFileProxy f3 = VCSFileProxy.createFileProxy(workDir, "file3");
        VCSFileProxy[] files = new VCSFileProxy[] { f1, f2 };
        add(files);
        GitClient client = getClient(workDir);
        commit(files);

        write(f1, "modification1");
        add(files);

        GitRevisionInfo revision1 = client.commit(files, "modification1", null, null, NULL_PROGRESS_MONITOR);
        
        write(f2, "modification2");
        add(files);
        GitRevisionInfo revision2 = client.commit(files, "modification2", null, null, NULL_PROGRESS_MONITOR);
        
        write(f1, "modification3");
        write(f2, "modification3");
        add(files);
        GitRevisionInfo revision3 = client.commit(files, "modification3", null, null, NULL_PROGRESS_MONITOR);

        write(f3, "modification4");
        add(new VCSFileProxy[] { f3 });
        GitRevisionInfo revision4 = client.commit(new VCSFileProxy[] { f3 }, "modification4", null, null, NULL_PROGRESS_MONITOR);
        
        SearchCriteria crit = new SearchCriteria();
        crit.setFiles(new VCSFileProxy[] { f1 });
        crit.setRevisionFrom(revision1.getRevision());
        GitRevisionInfo[] revisions = client.log(crit, true, NULL_PROGRESS_MONITOR);
        assertEquals(2, revisions.length);
        assertRevisions(revision3, revisions[0]);
        assertRevisions(revision1, revisions[1]);
        
        crit = new SearchCriteria();
        crit.setFiles(new VCSFileProxy[] { f2 });
        crit.setRevisionFrom(revision1.getRevision());
        revisions = client.log(crit, true, NULL_PROGRESS_MONITOR);
        assertEquals(2, revisions.length);
        assertRevisions(revision3, revisions[0]);
        assertRevisions(revision2, revisions[1]);
        
        crit = new SearchCriteria();
        crit.setFiles(files);
        crit.setRevisionFrom(revision1.getRevision());
        revisions = client.log(crit, true, NULL_PROGRESS_MONITOR);
        assertEquals(3, revisions.length);
        assertRevisions(revision3, revisions[0]);
        assertRevisions(revision2, revisions[1]);
        assertRevisions(revision1, revisions[2]);
        
        crit = new SearchCriteria();
        crit.setFiles(new VCSFileProxy[] { f1, f2, f3 });
        crit.setRevisionFrom(revision1.getRevision());
        revisions = client.log(crit, true, NULL_PROGRESS_MONITOR);
        assertEquals(4, revisions.length);
        assertRevisions(revision4, revisions[0]);
        assertRevisions(revision3, revisions[1]);
        assertRevisions(revision2, revisions[2]);
        assertRevisions(revision1, revisions[3]);
        
        crit = new SearchCriteria();
        crit.setFiles(new VCSFileProxy[] { workDir });
        crit.setRevisionFrom(revision1.getRevision());
        revisions = client.log(crit, true, NULL_PROGRESS_MONITOR);
        assertEquals(4, revisions.length);
        assertRevisions(revision4, revisions[0]);
        assertRevisions(revision3, revisions[1]);
        assertRevisions(revision2, revisions[2]);
        assertRevisions(revision1, revisions[3]);
    }
    
    public void testLogUsername () throws Exception {
        VCSFileProxy f = VCSFileProxy.createFileProxy(workDir, "f");
        write(f, "initial content");
        VCSFileProxy[] files = new VCSFileProxy[] { f };
        add(files);
        commit(files);

        write(f, "modification1");
        add(files);

        GitClient client = getClient(workDir);
        GitUser user1 = ApiUtils.getClassFactory().createUser("git-test-user", "git-test-user@domain.com");
        GitRevisionInfo revision1 = client.commit(files, "modification1", user1, null, NULL_PROGRESS_MONITOR);

        write(f, "modification2");
        add(files);
        GitRevisionInfo revision2 = client.commit(files, "modification2", null, user1, NULL_PROGRESS_MONITOR);
        
        SearchCriteria crit = new SearchCriteria();
        GitRevisionInfo[] revisions = client.log(crit, true, NULL_PROGRESS_MONITOR);
        assertEquals(3, revisions.length);
        
        crit = new SearchCriteria();
        crit.setUsername("git-test-user");
        revisions = client.log(crit, true, NULL_PROGRESS_MONITOR);
        assertEquals(2, revisions.length);
        assertRevisions(revision2, revisions[0]);
        assertRevisions(revision1, revisions[1]);
        
        crit = new SearchCriteria();
        crit.setUsername("git-test-user@domain.com");
        revisions = client.log(crit, true, NULL_PROGRESS_MONITOR);
        assertEquals(2, revisions.length);
        assertRevisions(revision2, revisions[0]);
        assertRevisions(revision1, revisions[1]);
        
        crit = new SearchCriteria();
        crit.setUsername("test-user");
        revisions = client.log(crit, true, NULL_PROGRESS_MONITOR);
        assertEquals(2, revisions.length);
        assertRevisions(revision2, revisions[0]);
        assertRevisions(revision1, revisions[1]);
        
        crit = new SearchCriteria();
        crit.setUsername("git-test-user222@domain.com");
        revisions = client.log(crit, true, NULL_PROGRESS_MONITOR);
        assertEquals(0, revisions.length);
        
        crit = new SearchCriteria();
        crit.setUsername("git-test-user <git-test-user@domain.com>");
        revisions = client.log(crit, true, NULL_PROGRESS_MONITOR);
        assertEquals(2, revisions.length);
        assertRevisions(revision2, revisions[0]);
        assertRevisions(revision1, revisions[1]);
    }
    
    public void testLogMessage () throws Exception {
        VCSFileProxy f = VCSFileProxy.createFileProxy(workDir, "f");
        write(f, "initial content");
        VCSFileProxy[] files = new VCSFileProxy[] { f };
        add(files);
        commit(files);

        write(f, "modification1");
        add(files);

        GitClient client = getClient(workDir);
        GitRevisionInfo revision1 = client.commit(files, "modification1\non master", null, null, NULL_PROGRESS_MONITOR);
        
        SearchCriteria crit = new SearchCriteria();
        GitRevisionInfo[] revisions = client.log(crit, true, NULL_PROGRESS_MONITOR);
        assertEquals(2, revisions.length);
        
        crit = new SearchCriteria();
        crit.setMessage("blablabla");
        revisions = client.log(crit, true, NULL_PROGRESS_MONITOR);
        assertEquals(0, revisions.length);
        
        crit = new SearchCriteria();
        crit.setMessage("modification");
        revisions = client.log(crit, true, NULL_PROGRESS_MONITOR);
        assertEquals(1, revisions.length);
        assertRevisions(revision1, revisions[0]);
        
        crit = new SearchCriteria();
        crit.setMessage("modification1\non master");
        revisions = client.log(crit, true, NULL_PROGRESS_MONITOR);
        assertEquals(1, revisions.length);
        assertRevisions(revision1, revisions[0]);
        
        // see bug #228905
        crit = new SearchCriteria();
        crit.setMessage("on master");
        revisions = client.log(crit, true, NULL_PROGRESS_MONITOR);
        assertEquals(1, revisions.length);
        assertRevisions(revision1, revisions[0]);
    }
    
    public void testSubstringFilter () throws Exception {
//        Git git = new Git(repository.getRepository());
//        RevCommit c = git.commit().setMessage("abcd").call();
//
//        RevWalk walk = new RevWalk(repository.getRepository());
//        {
//            walk.setRevFilter(AndRevFilter.create(RevFilter.ALL, MessageRevFilter.create("ab")));
//            walk.markStart(c);
//            RevCommit next = walk.next();
//            assertNotNull(next);
//            walk.reset();
//            SearchCriteria crit = new SearchCriteria();
//            crit.setMessage("ab");
//            GitRevisionInfo[] log = getClient(workDir).log(crit, NULL_PROGRESS_MONITOR);
//            assertEquals(1, log.length);
//        }
//
//        {
//            walk.setRevFilter(AndRevFilter.create(RevFilter.ALL, MessageRevFilter.create("bc")));
//            walk.markStart(c);
//            RevCommit next = walk.next();
//            assertNotNull(next);
//            walk.reset();
//            SearchCriteria crit = new SearchCriteria();
//            crit.setMessage("bc");
//            GitRevisionInfo[] log = getClient(workDir).log(crit, NULL_PROGRESS_MONITOR);
//            assertEquals(1, log.length);
//        }
//
//        {
//            walk.setRevFilter(AndRevFilter.create(RevFilter.ALL, MessageRevFilter.create("cd")));
//            walk.markStart(c);
//            RevCommit next = walk.next();
//            // JGit bug is fixed: https://bugs.eclipse.org/bugs/show_bug.cgi?id=409144
//            assertNotNull(next);
//            walk.reset();
//            SearchCriteria crit = new SearchCriteria();
//            crit.setMessage("cd");
//            GitRevisionInfo[] log = getClient(workDir).log(crit, NULL_PROGRESS_MONITOR);
//            assertEquals(1, log.length);
//        }
//
//        {
//            walk.setRevFilter(AndRevFilter.create(RevFilter.ALL, MessageRevFilter.create("abcd")));
//            walk.markStart(c);
//            RevCommit next = walk.next();
//            // JGit bug is fixed: https://bugs.eclipse.org/bugs/show_bug.cgi?id=409144
//            assertNotNull(next);
//            walk.reset();
//            SearchCriteria crit = new SearchCriteria();
//            crit.setMessage("abcd");
//            GitRevisionInfo[] log = getClient(workDir).log(crit, NULL_PROGRESS_MONITOR);
//            assertEquals(1, log.length);
//        }
    }
    
    public void testLogShowMerges () throws Exception {
        VCSFileProxy f = VCSFileProxy.createFileProxy(workDir, "f");
        write(f, "a\nb\nc");
        VCSFileProxy[] files = new VCSFileProxy[] { f };
        add(files);
        commit(files);

        GitClient client = getClient(workDir);
        client.createBranch("b", GitConstants.MASTER, NULL_PROGRESS_MONITOR);
        client.checkoutRevision("b", true, NULL_PROGRESS_MONITOR);
        
        write(f, "modification on branch\nb\nc");
        add(files);
        GitRevisionInfo revisionBranch = client.commit(files, "modification on branch", null, null, NULL_PROGRESS_MONITOR);
        
        Thread.sleep(1100);
        
        client.checkoutRevision(GitConstants.MASTER, true, NULL_PROGRESS_MONITOR);
        write(f, "a\nb\nmodification on master");
        add(files);
        GitRevisionInfo revisionMaster = client.commit(files, "modification on master", null, null, NULL_PROGRESS_MONITOR);
        
        GitRevisionInfo revisionMerge = client.log(client.merge("b", NULL_PROGRESS_MONITOR).getNewHead(), NULL_PROGRESS_MONITOR);
        
        SearchCriteria crit = new SearchCriteria();
        crit.setRevisionTo(GitConstants.MASTER);
        GitRevisionInfo[] revisions = client.log(crit, true, NULL_PROGRESS_MONITOR);
        assertEquals(4, revisions.length);
        assertRevisions(revisionMerge, revisions[0]);
        
        crit = new SearchCriteria();
        crit.setIncludeMerges(true);
        revisions = client.log(crit, true, NULL_PROGRESS_MONITOR);
        assertEquals(4, revisions.length);
        assertRevisions(revisionMerge, revisions[0]);
        
        crit = new SearchCriteria();
        crit.setIncludeMerges(false);
        revisions = client.log(crit, true, NULL_PROGRESS_MONITOR);
        assertEquals(3, revisions.length);
        assertRevisions(revisionMaster, revisions[0]);
        assertRevisions(revisionBranch, revisions[1]);
    }
    
    public void testLogDateCriteria () throws Exception {
        VCSFileProxy f = VCSFileProxy.createFileProxy(workDir, "f");
        write(f, "initial content");
        VCSFileProxy[] files = new VCSFileProxy[] { f };
        add(files);
        commit(files);

        write(f, "modification1");
        add(files);

        GitClient client = getClient(workDir);
        GitRevisionInfo revision1 = client.commit(files, "modification1", null, null, NULL_PROGRESS_MONITOR);

        Thread.sleep(1100);
        
        write(f, "modification2");
        add(files);
        GitRevisionInfo revision2 = client.commit(files, "modification2", null, null, NULL_PROGRESS_MONITOR);

        Thread.sleep(1100);

        write(f, "modification3");
        add(files);
        GitRevisionInfo revision3 = client.commit(files, "modification3", null, null, NULL_PROGRESS_MONITOR);
        SearchCriteria crit = new SearchCriteria();
        crit.setFrom(new Date(revision2.getCommitTime()));
        crit.setTo(new Date(revision3.getCommitTime()));
        GitRevisionInfo[] revisions = client.log(crit, true, NULL_PROGRESS_MONITOR);
        assertEquals(2, revisions.length);
        assertRevisions(revision3, revisions[0]);
        assertRevisions(revision2, revisions[1]);

        Thread.sleep(1100);

        write(f, "modification4");
        add(files);
        GitRevisionInfo revision4 = client.commit(files, "modification4", null, null, NULL_PROGRESS_MONITOR);
        crit = new SearchCriteria();
        crit.setFrom(new Date(revision2.getCommitTime()));
        crit.setTo(new Date(revision3.getCommitTime()));
        revisions = client.log(crit, true, NULL_PROGRESS_MONITOR);
        assertEquals(2, revisions.length);
        assertRevisions(revision3, revisions[0]);
        assertRevisions(revision2, revisions[1]);
        
        crit = new SearchCriteria();
        crit.setFrom(new Date(revision2.getCommitTime()));
        crit.setTo(new Date(revision4.getCommitTime()));
        revisions = client.log(crit, true, NULL_PROGRESS_MONITOR);
        assertEquals(3, revisions.length);
        assertRevisions(revision4, revisions[0]);
        assertRevisions(revision3, revisions[1]);
        assertRevisions(revision2, revisions[2]);
        
        crit = new SearchCriteria();
        crit.setFrom(new Date(revision2.getCommitTime()));
        revisions = client.log(crit, true, NULL_PROGRESS_MONITOR);
        assertEquals(3, revisions.length);
        assertRevisions(revision4, revisions[0]);
        assertRevisions(revision3, revisions[1]);
        assertRevisions(revision2, revisions[2]);
    }
    
    public void testLogFollowRename () throws Exception {
        VCSFileProxy f = VCSFileProxy.createFileProxy(workDir, "f");
        VCSFileProxy to = VCSFileProxy.createFileProxy(workDir, "renamed");
        write(f, "initial content");
        VCSFileProxy[] files = new VCSFileProxy[] { f, to };
        add(files);
        commit(files);

        write(f, "modification1");
        add(files);

        GitClient client = getClient(workDir);
        GitRevisionInfo revision1 = client.commit(files, "modification1", null, null, NULL_PROGRESS_MONITOR);
        
        client.rename(f, to, false, NULL_PROGRESS_MONITOR);
        GitRevisionInfo revision2 = client.commit(files, "rename", null, null, NULL_PROGRESS_MONITOR);

        write(to, "modification2");
        add(files);
        GitRevisionInfo revision3 = client.commit(files, "modification2", null, null, NULL_PROGRESS_MONITOR);
        
        SearchCriteria crit = new SearchCriteria();
        crit.setFiles(new VCSFileProxy[] { to });
        GitRevisionInfo[] revisions = client.log(crit, true, NULL_PROGRESS_MONITOR);
        assertEquals(2, revisions.length);
        assertRevisions(revision3, revisions[0]);
        assertRevisions(revision2, revisions[1]);
        Map<VCSFileProxy, GitFileInfo> modifiedFiles = revision2.getModifiedFiles();
        assertEquals(2, modifiedFiles.size());
        assertEquals(GitFileInfo.Status.RENAMED, modifiedFiles.get(to).getStatus());
        assertEquals(GitFileInfo.Status.REMOVED, modifiedFiles.get(f).getStatus());
        
        crit = new SearchCriteria();
        crit.setFiles(new VCSFileProxy[] { to });
        crit.setFollowRenames(true);
        revisions = client.log(crit, true, NULL_PROGRESS_MONITOR);
        assertEquals(4, revisions.length);
        assertRevisions(revision3, revisions[0]);
        assertRevisions(revision2, revisions[1]);
        assertRevisions(revision1, revisions[2]);
    }

    public void testLogMergeFilesFromAllParents () throws Exception {
        VCSFileProxy f = VCSFileProxy.createFileProxy(workDir, "f");
        VCSFileProxy f2 = VCSFileProxy.createFileProxy(workDir, "f2");
        write(f, "init");
        write(f2, "init");
        VCSFileProxy[] files = new VCSFileProxy[] { f, f2 };
        add(files);
        commit(files);

        GitClient client = getClient(workDir);
        client.createBranch("b", GitConstants.MASTER, NULL_PROGRESS_MONITOR);
        client.checkoutRevision("b", true, NULL_PROGRESS_MONITOR);
        
        write(f, "modification on branch");
        add(files);
        client.commit(files, "modification on branch", null, null, NULL_PROGRESS_MONITOR);
        
        client.checkoutRevision(GitConstants.MASTER, true, NULL_PROGRESS_MONITOR);
        write(f2, "modification");
        add(files);
        client.commit(files, "modification on master", null, null, NULL_PROGRESS_MONITOR);
        String newHead = client.merge("b", NULL_PROGRESS_MONITOR).getNewHead();
        GitRevisionInfo revisionMerge = client.log(newHead, NULL_PROGRESS_MONITOR);
        Map<VCSFileProxy, GitFileInfo> modifiedFiles = revisionMerge.getModifiedFiles();
        assertEquals(2, modifiedFiles.size());
        assertEquals(GitFileInfo.Status.MODIFIED, modifiedFiles.get(f).getStatus());
        assertEquals(GitFileInfo.Status.MODIFIED, modifiedFiles.get(f2).getStatus());
    }
    
    public void testLogWithBranchInfo () throws Exception {
        VCSFileProxy f = VCSFileProxy.createFileProxy(workDir, "f");
        VCSFileProxy f2 = VCSFileProxy.createFileProxy(workDir, "f2");
        write(f, "init");
        write(f2, "init");
        VCSFileProxy[] files = new VCSFileProxy[] { f, f2 };
        add(files);
        commit(files);
        
        write(f, "modification");
        add(files);
        commit(files);
        
        GitClient client = getClient(workDir);
        client.createBranch("BRANCH", GitConstants.MASTER, NULL_PROGRESS_MONITOR);
        SearchCriteria crit = new SearchCriteria();
        crit.setRevisionTo(GitConstants.MASTER);
        GitRevisionInfo[] log = client.log(crit, NULL_PROGRESS_MONITOR);
        for (GitRevisionInfo info : log) {
            // no branch info fetched by this version of the command
            assertEquals(0, info.getBranches().size());
        }
        
        log = client.log(crit, true, NULL_PROGRESS_MONITOR);
        for (GitRevisionInfo info : log) {
            // all commits are from master
            assertEquals(2, info.getBranches().size());
            assertNotNull(info.getBranches().get(GitConstants.MASTER));
            assertNotNull(info.getBranches().get("BRANCH"));
        }
    }
    
    // commit in the middle of a named branch
    public void testLogWithBranchInfoMiddleCommit () throws Exception {
        VCSFileProxy f = VCSFileProxy.createFileProxy(workDir, "f");
        VCSFileProxy f2 = VCSFileProxy.createFileProxy(workDir, "f2");
        write(f, "init");
        write(f2, "init");
        VCSFileProxy[] files = new VCSFileProxy[] { f, f2 };
        add(files);
        GitClient client = getClient(workDir);
        GitRevisionInfo firstCommit = client.commit(files, "initial commit", null, null, NULL_PROGRESS_MONITOR);
        
        client.createBranch("newbranch", GitConstants.MASTER, NULL_PROGRESS_MONITOR);
        
        write(f, "modification");
        add(files);
        commit(files);
        
        write(f2, "modification");
        add(files);
        commit(files);
        
        SearchCriteria crit = new SearchCriteria();
        crit.setRevisionFrom(firstCommit.getRevision());
        crit.setRevisionTo(firstCommit.getRevision());
        crit.setFiles(new VCSFileProxy[] { f });
        GitRevisionInfo info = client.log(crit, true, NULL_PROGRESS_MONITOR)[0];
        // the initial commit is from master and head of newbranch
        assertNotNull(info.getBranches().get("newbranch"));
        assertEquals(2, info.getBranches().size());
    }
    
    public void testLogWithBranchInfoMoreBranches () throws Exception {
        VCSFileProxy f = VCSFileProxy.createFileProxy(workDir, "f");
        VCSFileProxy f2 = VCSFileProxy.createFileProxy(workDir, "f2");
        write(f, "init");
        write(f2, "init");
        VCSFileProxy[] files = new VCSFileProxy[] { f, f2 };
        add(files);
        commit(files);
        
        write(f, "modification");
        add(files);
        commit(files);
                
        GitClient client = getClient(workDir);
        client.createBranch("newbranch", GitConstants.MASTER, NULL_PROGRESS_MONITOR);
        write(f, "modification on trunk");
        add(files);
        commit(files);
        
        client.checkoutRevision("newbranch", true, NULL_PROGRESS_MONITOR);
        write(f, "modification on branch");
        add(files);
        commit(files);
        
        SearchCriteria crit = new SearchCriteria();
        // log across all branches
        GitRevisionInfo[] log = client.log(crit, true, NULL_PROGRESS_MONITOR);
        // branch commit
        assertEquals(1, log[0].getBranches().size());
if(KIT) assertNotNull(log[0].getBranches().get("newbranch"));
else    assertNotNull(log[0].getBranches().get(GitConstants.MASTER));
        // master commit
        assertEquals(1, log[1].getBranches().size());
if(KIT) assertNotNull(log[1].getBranches().get(GitConstants.MASTER));
else    assertNotNull(log[1].getBranches().get("newbranch"));
        // common commit
        assertEquals(2, log[2].getBranches().size());
        assertNotNull(log[2].getBranches().get(GitConstants.MASTER));
        assertNotNull(log[2].getBranches().get("newbranch"));
        // initial commit
        assertEquals(2, log[3].getBranches().size());
        assertNotNull(log[3].getBranches().get(GitConstants.MASTER));
        assertNotNull(log[3].getBranches().get("newbranch"));
    }
    
    public void testShortMessages () throws Exception {
        VCSFileProxy f = VCSFileProxy.createFileProxy(workDir, "f");
        write(f, "init");
        VCSFileProxy[] files = new VCSFileProxy[] { f };
        add(files);
        GitClient client = getClient(workDir);
        client.commit(files, "short message", null, null, NULL_PROGRESS_MONITOR);
        assertEquals("short message", client.log(GitConstants.HEAD, NULL_PROGRESS_MONITOR).getShortMessage());
        
        write(f, "m1");
        add(f);
        client.commit(files, "short message\n\n\n", null, null, NULL_PROGRESS_MONITOR);
        assertEquals("short message", client.log(GitConstants.HEAD, NULL_PROGRESS_MONITOR).getShortMessage());
        
        write(f, "m2");
        add(f);
        client.commit(files, "short message\nbla\nbla\nbla", null, null, NULL_PROGRESS_MONITOR);
        assertEquals("short message", client.log(GitConstants.HEAD, NULL_PROGRESS_MONITOR).getShortMessage());
    }
    
    public void testLimit () throws Exception {
        VCSFileProxy f1 = VCSFileProxy.createFileProxy(workDir, "f1");
        write(f1, "init");
        VCSFileProxy f2 = VCSFileProxy.createFileProxy(workDir, "f2");
        write(f2, "init");
        
        VCSFileProxy[] files = new VCSFileProxy[] { f1, f2 };
        add(files);
        commit(files);
        
        write(f1, "modif1");
        add(f1);
        
        GitClient client = getClient(workDir);
        GitRevisionInfo c1 = client.commit(files, "m1", new GitUser("another", "netbeans.org"), new GitUser("another", "netbeans.org"), NULL_PROGRESS_MONITOR);
        
        write(f2, "modif2");
        add(f2);
        GitRevisionInfo c2 = client.commit(files, "m2", new GitUser("user", "netbeans.org"), new GitUser("user", "netbeans.org"), NULL_PROGRESS_MONITOR);
        
        SearchCriteria crit = new SearchCriteria();
        crit.setLimit(1);
        crit.setUsername("another");
        GitRevisionInfo[] log = client.log(crit, NULL_PROGRESS_MONITOR);
        assertEquals(c1.getRevision(), log[0].getRevision());        
    }

    private void assertRevisions (GitRevisionInfo expected, GitRevisionInfo info) throws GitException {
        assertEquals(expected.getRevision(), info.getRevision());
        assertEquals(expected.getAuthor().toString(), info.getAuthor().toString());
        assertEquals(expected.getCommitTime(), info.getCommitTime());
        assertEquals(expected.getCommitter().toString(), info.getCommitter().toString());
        assertEquals(expected.getFullMessage(), info.getFullMessage());
        assertEquals(expected.getModifiedFiles().size(), info.getModifiedFiles().size());
        assertEquals(expected.getShortMessage(), info.getShortMessage());
    }

}
