/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 *
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.test.subversion.main.delete;

import java.io.File;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Test;
import org.netbeans.jellytools.FilesTabOperator;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jellytools.NewProjectWizardOperator;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jemmy.EventTool;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;
import org.netbeans.jemmy.operators.Operator;
import org.netbeans.jemmy.operators.Operator.DefaultStringComparator;
import org.netbeans.junit.NbModuleSuite;
import org.netbeans.test.subversion.operators.SourcePackagesNode;
import org.netbeans.test.subversion.operators.CheckoutWizardOperator;
import org.netbeans.test.subversion.operators.CommitOperator;
import org.netbeans.test.subversion.operators.RepositoryStepOperator;
import org.netbeans.test.subversion.operators.VersioningOperator;
import org.netbeans.test.subversion.operators.WorkDirStepOperator;
import org.netbeans.test.subversion.utils.MessageHandler;
import org.netbeans.test.subversion.utils.RepositoryMaintenance;
import org.netbeans.test.subversion.utils.TestKit;

/**
 *
 * @author novakm
 */
public class FilesViewDoubleRefTest extends JellyTestCase {

    public static final String TMP_PATH = "/tmp";
    public static final String REPO_PATH = "repo";
    public static final String WORK_PATH = "work";
    public static final String PROJECT_NAME = "JavaApp";
    public File projectPath;
    public PrintStream stream;
    Operator.DefaultStringComparator comOperator;
    Operator.DefaultStringComparator oldOperator;
    static Logger log;

    /** Creates a new instance of FilesViewRefTest */
    public FilesViewDoubleRefTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        System.out.println("### " + getName() + " ###");
        if (log == null) {
            log = Logger.getLogger(TestKit.LOGGER_NAME);
            log.setLevel(Level.ALL);
            TestKit.removeHandlers(log);
        } else {
            TestKit.removeHandlers(log);
        }
    }
    
    public static Test suite() {
         return NbModuleSuite.create(
                 NbModuleSuite.createConfiguration(FilesViewDoubleRefTest.class).addTest(
                    "testFilesViewDoubleRefactoring"
                 )
                 .enableModules(".*")
                 .clusters(".*")
        );
     }

    public void testFilesViewDoubleRefactoring() throws Exception {
        
            MessageHandler mh = new MessageHandler("Checking out");
            log.addHandler(mh);

            TestKit.closeProject(PROJECT_NAME);
            if (TestKit.getOsName().indexOf("Mac") > -1)
                new NewProjectWizardOperator().invoke().close();

            stream = new PrintStream(new File(getWorkDir(), getName() + ".log"));
            VersioningOperator vo = VersioningOperator.invoke();
            comOperator = new Operator.DefaultStringComparator(true, true);
            oldOperator = (DefaultStringComparator) Operator.getDefaultStringComparator();
            Operator.setDefaultStringComparator(comOperator);
            CheckoutWizardOperator.invoke();
            Operator.setDefaultStringComparator(oldOperator);
            RepositoryStepOperator rso = new RepositoryStepOperator();

            //create repository...
            File work = new File(TMP_PATH + File.separator + WORK_PATH + File.separator + "w" + System.currentTimeMillis());
            new File(TMP_PATH).mkdirs();
            work.mkdirs();
            RepositoryMaintenance.deleteFolder(new File(TMP_PATH + File.separator + REPO_PATH));
            RepositoryMaintenance.createRepository(TMP_PATH + File.separator + REPO_PATH);
            RepositoryMaintenance.loadRepositoryFromFile(TMP_PATH + File.separator + REPO_PATH, getDataDir().getCanonicalPath() + File.separator + "repo_dump");
            rso.setRepositoryURL(RepositoryStepOperator.ITEM_FILE + RepositoryMaintenance.changeFileSeparator(TMP_PATH + File.separator + REPO_PATH, false));

            rso.next();
            WorkDirStepOperator wdso = new WorkDirStepOperator();
            wdso.setRepositoryFolder("trunk/" + PROJECT_NAME);
            wdso.setLocalFolder(work.getCanonicalPath());
            wdso.checkCheckoutContentOnly(false);
            wdso.finish();
            //open project

            TestKit.waitText(mh);

            NbDialogOperator nbdialog = new NbDialogOperator("Checkout Completed");
            JButtonOperator open = new JButtonOperator(nbdialog, "Open Project");
            open.push();
            TestKit.waitForScanFinishedSimple();

            TestKit.createNewPackage(PROJECT_NAME, "a.b.c");
            new EventTool().waitEvent(2000);
            TestKit.createNewElement(PROJECT_NAME, "a", "AClass");
            new EventTool().waitEvent(2000);
            TestKit.createNewElement(PROJECT_NAME, "a.b", "BClass");
            new EventTool().waitEvent(2000);
            TestKit.createNewElement(PROJECT_NAME, "a.b.c", "CClass");
            new EventTool().waitEvent(2000);

            mh = new MessageHandler("Refreshing");
            TestKit.removeHandlers(log);
            log.addHandler(mh);

            Node node = new Node(new SourcePackagesNode(PROJECT_NAME), "");
            node = new Node(new FilesTabOperator().tree(), PROJECT_NAME);
            node.performPopupActionNoBlock("Subversion|Show Changes");

            TestKit.waitText(mh);

            mh = new MessageHandler("Committing");
            TestKit.removeHandlers(log);
            log.addHandler(mh);

            CommitOperator cmo = CommitOperator.invoke(node);
            cmo.commit();

            TestKit.waitText(mh);

            node = new Node(new FilesTabOperator().tree(), PROJECT_NAME + "|src|a|AClass.java");
            node.performPopupActionNoBlock("Refactor|Rename...");
            nbdialog = new NbDialogOperator("Rename");
            JTextFieldOperator txt = new JTextFieldOperator(nbdialog);
            txt.setText("A_AClass");
            JButtonOperator refBut = new JButtonOperator(nbdialog, "Refactor");
            refBut.push();
            nbdialog.waitClosed();
            Thread.sleep(1000);
            node = new Node(new FilesTabOperator().tree(), PROJECT_NAME);
            node.performPopupActionNoBlock("Subversion|Show Changes");
            vo = VersioningOperator.invoke();
            String[] expected = new String[]{"AClass.java", "A_AClass.java"};
            String[] actual = new String[vo.tabFiles().getRowCount()];
            for (int i = 0; i < vo.tabFiles().getRowCount(); i++) {
                actual[i] = vo.tabFiles().getValueAt(i, 0).toString().trim();
            }
            int result = TestKit.compareThem(expected, actual, false);
            assertEquals("Wrong files in Versioning View", expected.length, result);
            expected = new String[]{"Locally Added", "Locally Deleted"};
            actual = new String[vo.tabFiles().getRowCount()];
            for (int i = 0; i < vo.tabFiles().getRowCount(); i++) {
                actual[i] = vo.tabFiles().getValueAt(i, 1).toString().trim();
            }

            result = TestKit.compareThem(expected, actual, false);
            assertEquals("Wrong status in Versioning View", expected.length, result);
//            commit
            node = new Node(new FilesTabOperator().tree(), PROJECT_NAME);

            mh = new MessageHandler("Committing");
            TestKit.removeHandlers(log);
            log.addHandler(mh);

            node.performPopupActionNoBlock("Subversion|Show Changes");
            cmo = CommitOperator.invoke(node);
            cmo.commit();

            TestKit.waitText(mh);
//            refactor back
            node = new Node(new FilesTabOperator().tree(), PROJECT_NAME + "|src|a|A_AClass.java");
            node.performPopupActionNoBlock("Refactor|Rename...");
            nbdialog = new NbDialogOperator("Rename");
            txt = new JTextFieldOperator(nbdialog);
            txt.setText("AClass");
            refBut = new JButtonOperator(nbdialog, "Refactor");
            refBut.push();
        
            TestKit.closeProject(PROJECT_NAME);
        
    }
}
