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

package org.netbeans.modules.git.ui.status;

import java.awt.EventQueue;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.swing.JTable;
import org.netbeans.junit.MockServices;
import org.netbeans.modules.git.AbstractGitTestCase;
import org.netbeans.modules.git.FileInformation;
import org.netbeans.modules.git.FileStatusCache;
import org.netbeans.modules.git.FileStatusCache.ChangedEvent;
import org.netbeans.modules.git.Git;
import org.netbeans.modules.git.GitVCS;
import org.netbeans.modules.git.ui.repository.RepositoryInfo;
import org.netbeans.modules.versioning.VersioningAnnotationProvider;
import org.netbeans.modules.versioning.spi.VCSContext;
import org.netbeans.modules.versioning.util.Utils;
import org.netbeans.modules.versioning.util.status.VCSStatusTable;
import org.netbeans.modules.versioning.util.status.VCSStatusTableModel;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author ondra
 */
public class StatusTest extends AbstractGitTestCase {

    public StatusTest (String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MockServices.setServices(new Class[] {
            VersioningAnnotationProvider.class,
            GitVCS.class});
        Git.STATUS_LOG.setLevel(Level.ALL);
    }

    public void testVersioningPanel () throws Exception {
        final JTable tables[] = new JTable[1];
        EventQueue.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                GitVersioningTopComponent tc = GitVersioningTopComponent.findInstance();
                VCSContext ctx = VCSContext.forNodes(new Node[] {
                    new AbstractNode(Children.LEAF, Lookups.singleton(repositoryLocation))
                });
                tc.setContentTitle(Utils.getContextDisplayName(ctx));
                tc.setContext(ctx);
		Field f;
                try {
                    f = GitVersioningTopComponent.class.getDeclaredField("controller");
                    f.setAccessible(true);
                    VersioningPanelController controller = (VersioningPanelController) f.get(tc);
                    f = VersioningPanelController.class.getDeclaredField("syncTable");
                    f.setAccessible(true);
                    GitStatusTable table = (GitStatusTable) f.get(controller);
                    f = VCSStatusTable.class.getDeclaredField("table");
                    f.setAccessible(true);
                    tables[0] = (JTable) f.get(table);
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });
        JTable table = tables[0];
        assertNotNull(table);
        assertTable(table, Collections.<File>emptySet());
        File file = new File(repositoryLocation, "file");

        file.createNewFile();
        File[] files = new File[] { repositoryLocation };
        getCache().refreshAllRoots(files);
        assertTable(table, Collections.singleton(file));

        add();
        commit();
        getCache().refreshAllRoots(files);
        assertTable(table, Collections.<File>emptySet());

        write(file, "blabla");
        add(file);
        getCache().refreshAllRoots(files);
        assertTable(table, Collections.singleton(file));

        commit();
        getCache().refreshAllRoots(files);
        assertTable(table, Collections.<File>emptySet());

        delete(false, file);
        getCache().refreshAllRoots(files);
        assertTable(table, Collections.singleton(file));
    }

    public void testBranchName () throws Exception {
        final GitVersioningTopComponent tcs[] = new GitVersioningTopComponent[1];
        EventQueue.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                GitVersioningTopComponent tc = GitVersioningTopComponent.findInstance();
                tcs[0] = tc;
                VCSContext ctx = VCSContext.forNodes(new Node[] {
                    new AbstractNode(Children.LEAF, Lookups.singleton(FileUtil.toFileObject(FileUtil.normalizeFile(repositoryLocation))))
                });
                tc.setContentTitle(Utils.getContextDisplayName(ctx));
                tc.setContext(ctx);
            }
        });
        GitVersioningTopComponent tc = tcs[0];
        assertNotNull(tc);
        assertName(tc, "Git - work - (no branch)");
        File f = new File(repositoryLocation, "f");
        f.createNewFile();
        add();
        commit();
        RepositoryInfo.getInstance(repositoryLocation).refresh();
        assertName(tc, "Git - work - master");
    }
    
    public void test193781 () throws Exception {
        File file = new File(repositoryLocation, "f");
        file.createNewFile();
        add();
        commit();
        
        RequestProcessor.Task refreshNodesTask;
        RequestProcessor.Task changeTask;
        final VersioningPanelController[] controllers = new VersioningPanelController[1];
        EventQueue.invokeAndWait(new Runnable() {
            @Override
            public void run () {
                controllers[0] = new VersioningPanelController();
            }
        });
        VersioningPanelController controller = controllers[0];
        Field f = VersioningPanelController.class.getDeclaredField("refreshNodesTask");
        f.setAccessible(true);
        refreshNodesTask = (Task) f.get(controller);
        f = VersioningPanelController.class.getDeclaredField("changeTask");
        f.setAccessible(true);
        changeTask = (Task) f.get(controller);
        f = VersioningPanelController.class.getDeclaredField("syncTable");
        f.setAccessible(true);
        GitStatusTable statusTable = (GitStatusTable) f.get(controller);
        f = VCSStatusTable.class.getDeclaredField("tableModel");
        f.setAccessible(true);
        VCSStatusTableModel model = (VCSStatusTableModel) f.get(statusTable);
        f = VersioningPanelController.class.getDeclaredField("changes");
        f.setAccessible(true);
        Map<File, FileStatusCache.ChangedEvent> changes = (Map<File, ChangedEvent>) f.get(controller);
        final boolean barrier[] = new boolean[1];
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run () {
                while (!barrier[0]) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        });
        
        // do a modification
        write(file, "modification");
        getCache().refreshAllRoots(new File[] { file });
        // and simultaneously refresh all nodes ...
        controller.setContext(VCSContext.forNodes(new Node[] { new AbstractNode(Children.LEAF, Lookups.fixed(file)) }));
        // ... and simulate parallel change event from cache
        refreshNodesTask.waitFinished();
        synchronized (changes) {
            FileInformation fi = getCache().getStatus(file);
            assertTrue(fi.containsStatus(FileInformation.Status.MODIFIED_HEAD_WORKING_TREE));
            changes.put(file, new ChangedEvent(file, null, fi));
        }
        changeTask.schedule(0);
        for (;;) {
            synchronized (changes) {
                if (changes.isEmpty()) {
                    break;
                }
            }
            Thread.sleep(100);
        }
        assertEquals(0, model.getNodes().length);
        barrier[0] = true;
        for (int i = 0; i < 100 && model.getNodes().length == 0; ++i) {
            Thread.sleep(100);
        }
        changeTask.waitFinished();
        EventQueue.invokeAndWait(new Runnable() {
            @Override
            public void run() {
            }
        });
        assertEquals(1, model.getNodes().length);
    }

    private void assertTable (final JTable table, Set<File> files) throws Exception {
        Thread.sleep(5000);
        final Set<File> displayedFiles = new HashSet<File>();
        EventQueue.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < table.getRowCount(); ++i) {
                    String path = table.getValueAt(i, 2).toString();
                    displayedFiles.add(new File(repositoryLocation, path));
                }
            }
        });
        assertEquals(files, displayedFiles);
    }

    private void assertName (GitVersioningTopComponent tc, String expected) throws InterruptedException {
        for (int i = 0; i < 100; ++i) {
            if (expected.equals(tc.getName())) {
                break;
            }
            Thread.sleep(100);
        }
        assertEquals(expected, tc.getName());
    }
}
