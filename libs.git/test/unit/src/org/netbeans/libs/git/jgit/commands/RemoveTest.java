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

package org.netbeans.libs.git.jgit.commands;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import org.eclipse.jgit.lib.Repository;
import org.netbeans.libs.git.GitClient;
import org.netbeans.libs.git.GitException;
import org.netbeans.libs.git.GitStatus;
import org.netbeans.libs.git.jgit.AbstractGitTestCase;
import org.netbeans.libs.git.progress.ProgressMonitor;

/**
 *
 * @author ondra
 */
public class RemoveTest extends AbstractGitTestCase {

    private Repository repository;
    private File workDir;
    
    public RemoveTest (String name) throws IOException {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        workDir = getWorkingDirectory();
        repository = getRepository(getLocalGitRepository());
    }

    public void testRemoveNoRoots () throws Exception {
        File file = new File(workDir, "toRemove");
        file.createNewFile();
        File file2 = new File(workDir, "unversioned");
        file2.createNewFile();

        GitClient client = getClient(workDir);
        add(file);
        commit(file);
        Map<File, GitStatus> statuses = client.getStatus(new File[] { file }, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(1, statuses.size());
        assertStatus(statuses, workDir, file, true, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_NORMAL, false);

        Monitor m = new Monitor();
        client.addNotificationListener(m);
        client.remove(new File[0], false, m);
        assertEquals(1, m.notifiedWarnings.size());
        assertTrue(file.exists());
        assertTrue(file2.exists());
    }

    public void testRemoveFileHard () throws Exception {
        File file = new File(workDir, "toRemove");
        file.createNewFile();

        GitClient client = getClient(workDir);
        add(file);
        commit(file);
        Map<File, GitStatus> statuses = client.getStatus(new File[] { file }, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(1, statuses.size());
        assertStatus(statuses, workDir, file, true, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_NORMAL, false);

        Monitor m = new Monitor();
        client.addNotificationListener(m);
        client.remove(new File[] { file }, false, m);
        assertEquals(Collections.singleton(file), m.notifiedFiles);
        assertFalse(file.exists());
        statuses = client.getStatus(new File[] { file }, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(1, statuses.size());
        assertStatus(statuses, workDir, file, true, GitStatus.Status.STATUS_REMOVED, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_REMOVED, false);

        commit(file);
        statuses = client.getStatus(new File[] { file }, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(0, statuses.size());
    }

    // test for: removing a file only from git control, but leave it on disk
    public void testRemoveFileCached () throws Exception {
        File file = new File(workDir, "toRemove");
        file.createNewFile();
        add(file);
        commit(file);

        GitClient client = getClient(workDir);
        Map<File, GitStatus> statuses = client.getStatus(new File[] { file }, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(1, statuses.size());
        assertStatus(statuses, workDir, file, true, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_NORMAL, false);

        Monitor m = new Monitor();
        client.addNotificationListener(m);
        client.remove(new File[] { file }, true, m);
        assertTrue(file.exists());
        assertEquals(Collections.singleton(file), m.notifiedFiles);
        statuses = client.getStatus(new File[] { file }, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(1, statuses.size());
        assertStatus(statuses, workDir, file, true, GitStatus.Status.STATUS_REMOVED, GitStatus.Status.STATUS_ADDED, GitStatus.Status.STATUS_NORMAL, false);

        commit(file);

        statuses = client.getStatus(new File[] { file }, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(1, statuses.size());
        assertStatus(statuses, workDir, file, false, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_ADDED, GitStatus.Status.STATUS_ADDED, false);
    }

    public void testRemoveTreeHard () throws Exception {
        File folder = new File(workDir, "folder");
        File file = new File(folder, "file");
        File folder1 = new File(folder, "folder1");
        File folder2 = new File(folder1, "folder2");
        folder2.mkdirs();
        file.createNewFile();
        File file1 = new File(folder1, "file1");
        file1.createNewFile();
        File file2 = new File(folder1, "file2");
        file2.createNewFile();
        File file3 = new File(folder2, "file3");
        file3.createNewFile();

        File[] folders = new File[] { folder1, folder2 };
        add(folders);
        add(file);
        commit(workDir);
        GitClient client = getClient(workDir);
        Map<File, GitStatus> statuses = client.getStatus(folders, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(3, statuses.size());
        assertStatus(statuses, workDir, file1, true, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_NORMAL, false);
        assertStatus(statuses, workDir, file2, true, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_NORMAL, false);
        assertStatus(statuses, workDir, file3, true, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_NORMAL, false);

        Monitor m = new Monitor();
        client.addNotificationListener(m);
        client.remove(folders, false, m);
        assertFalse(file1.exists());
        assertFalse(file2.exists());
        assertFalse(file3.exists());
        assertFalse(folder1.exists());
        assertFalse(folder2.exists());
        assertTrue(file.exists());
        assertTrue(folder.exists());
        assertEquals(new HashSet<File>(Arrays.asList(file1, file2, file3, folder1, folder2)), m.notifiedFiles);
        statuses = client.getStatus(folders, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(3, statuses.size());
        assertStatus(statuses, workDir, file1, true, GitStatus.Status.STATUS_REMOVED, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_REMOVED, false);
        assertStatus(statuses, workDir, file2, true, GitStatus.Status.STATUS_REMOVED, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_REMOVED, false);
        assertStatus(statuses, workDir, file3, true, GitStatus.Status.STATUS_REMOVED, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_REMOVED, false);
        statuses = client.getStatus(new File[] { file }, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(1, statuses.size());
        assertStatus(statuses, workDir, file, true, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_NORMAL, false);

        commit(workDir);
        statuses = client.getStatus(folders, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(0, statuses.size());
    }

    // test for: removing a file only from git control, but leave it on disk
    public void testRemoveTreeCached () throws Exception {
        File folder1 = new File(workDir, "folder1");
        File folder2 = new File(folder1, "folder2");
        folder2.mkdirs();
        File file1 = new File(folder1, "file1");
        file1.createNewFile();
        File file2 = new File(folder1, "file2");
        file2.createNewFile();
        File file3 = new File(folder2, "file3");
        file3.createNewFile();
        File file = new File(workDir, "file");
        file.createNewFile();


        File[] folders = new File[] { folder1, folder2 };
        add(folders);
        add(file);
        commit(workDir);
        GitClient client = getClient(workDir);
        Map<File, GitStatus> statuses = client.getStatus(folders, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(3, statuses.size());
        assertStatus(statuses, workDir, file1, true, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_NORMAL, false);
        assertStatus(statuses, workDir, file2, true, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_NORMAL, false);
        assertStatus(statuses, workDir, file3, true, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_NORMAL, false);

        Monitor m = new Monitor();
        client.addNotificationListener(m);
        client.remove(folders, true, m);
        assertTrue(file1.exists());
        assertTrue(file2.exists());
        assertTrue(file3.exists());
        assertEquals(new HashSet<File>(Arrays.asList(file1, file2, file3)), m.notifiedFiles);
        statuses = client.getStatus(folders, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(3, statuses.size());
        assertStatus(statuses, workDir, file1, true, GitStatus.Status.STATUS_REMOVED, GitStatus.Status.STATUS_ADDED, GitStatus.Status.STATUS_NORMAL, false);
        assertStatus(statuses, workDir, file2, true, GitStatus.Status.STATUS_REMOVED, GitStatus.Status.STATUS_ADDED, GitStatus.Status.STATUS_NORMAL, false);
        assertStatus(statuses, workDir, file3, true, GitStatus.Status.STATUS_REMOVED, GitStatus.Status.STATUS_ADDED, GitStatus.Status.STATUS_NORMAL, false);
        statuses = client.getStatus(new File[] { file }, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(1, statuses.size());
        assertStatus(statuses, workDir, file, true, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_NORMAL, false);

        commit(workDir);
        statuses = client.getStatus(folders,ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(3, statuses.size());
        assertStatus(statuses, workDir, file1, false, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_ADDED, GitStatus.Status.STATUS_ADDED, false);
        assertStatus(statuses, workDir, file2, false, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_ADDED, GitStatus.Status.STATUS_ADDED, false);
        assertStatus(statuses, workDir, file3, false, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_ADDED, GitStatus.Status.STATUS_ADDED, false);
    }

    public void testRemoveUntrackedFile () throws Exception {
        File file = new File(workDir, "toRemove");
        file.createNewFile();
        assertTrue(file.exists());
        GitClient client = getClient(workDir);
        Map<File, GitStatus> statuses = client.getStatus(new File[] { file }, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(1, statuses.size());
        assertStatus(statuses, workDir, file, false, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_ADDED, GitStatus.Status.STATUS_ADDED, false);

        Monitor m = new Monitor();
        client.addNotificationListener(m);
        client.remove(new File[] { file }, false, m);
        assertFalse(file.exists());
        assertEquals(Collections.singleton(file), m.notifiedFiles);
        statuses = client.getStatus(new File[] { file }, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(0, statuses.size());
    }

    public void testRemoveUntrackedTree () throws Exception {
        File folder = new File(workDir, "folder");
        File folder1 = new File(folder, "folder1");
        File folder2 = new File(folder1, "folder2");
        folder2.mkdirs();
        File file1 = new File(folder1, "file1");
        file1.createNewFile();
        File file2 = new File(folder1, "file2");
        file2.createNewFile();
        File file3 = new File(folder2, "file3");
        file3.createNewFile();

        File[] folders = new File[] { folder1, folder2 };
        GitClient client = getClient(workDir);
        Map<File, GitStatus> statuses = client.getStatus(folders, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(3, statuses.size());
        assertStatus(statuses, workDir, file1, false, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_ADDED, GitStatus.Status.STATUS_ADDED, false);
        assertStatus(statuses, workDir, file2, false, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_ADDED, GitStatus.Status.STATUS_ADDED, false);
        assertStatus(statuses, workDir, file3, false, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_ADDED, GitStatus.Status.STATUS_ADDED, false);

        Monitor m = new Monitor();
        client.addNotificationListener(m);
        client.remove(folders, false, m);
        assertFalse(file1.exists());
        assertFalse(file2.exists());
        assertFalse(file3.exists());
        assertFalse(folder1.exists());
        assertFalse(folder2.exists());
        assertTrue(folder.exists());
        assertEquals(new HashSet<File>(Arrays.asList(file1, file2, file3, folder1, folder2)), m.notifiedFiles);
        statuses = client.getStatus(folders, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(0, statuses.size());
    }

    public void testCancel () throws Exception {
        final File file = new File(workDir, "file");
        file.createNewFile();
        final File file2 = new File(workDir, "file2");
        file2.createNewFile();

        final Monitor m = new Monitor();
        final GitClient client = getClient(workDir);
        final Exception[] exs = new Exception[1];
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.addNotificationListener(m);
                    client.remove(new File[] { file, file2 }, false, m);
                } catch (GitException ex) {
                    exs[0] = ex;
                }
            }
        });
        m.cont = false;
        t1.start();
        m.waitAtBarrier();
        m.cancel();
        m.cont = true;
        t1.join();
        assertTrue(m.isCanceled());
        assertEquals(1, m.count);
        assertEquals(null, exs[0]);
    }
}
