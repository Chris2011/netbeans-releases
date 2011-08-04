/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009-2010 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009-2010 Sun Microsystems, Inc.
 */
package org.netbeans.modules.refactoring.java.test;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.TreePath;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.modules.refactoring.api.Problem;
import org.netbeans.modules.refactoring.api.RefactoringSession;
import org.netbeans.modules.refactoring.java.api.InnerToOuterRefactoring;

public class InnerToOutterTest extends RefactoringTestBase {

    public InnerToOutterTest(String name) {
        super(name);
    }
    
    public void test196955() throws Exception {
        writeFilesAndWaitForScan(src,
                                 new File("t/A.java", "package t; public class A { class B { } class F { B b; } }"));
        performInnerToOuterTest(true);
        verifyContent(src,
                      new File("t/F.java", "package t; class F { A.B b; private final A outer; F(final A outer) { this.outer = outer; } } "),
                      new File("t/A.java", "package t; public class A { class B { } }"));
    }

    public void test178451() throws Exception {
        writeFilesAndWaitForScan(src,
                                 new File("t/A.java", "@A(foo=A.FOO) package t; public @interface A { public String foo(); public static final String FOO = \"foo\"; public static class F { } }"));
        performInnerToOuterTest(false);
        verifyContent(src,
                      new File("t/F.java", "package t;\n\npublic class F { }\n"),//TODO: why outer reference?
                      new File("t/A.java", "@A(foo=A.FOO) package t; public @interface A { public String foo(); public static final String FOO = \"foo\"; }"));
    }

    public void test138204a() throws Exception {
        writeFilesAndWaitForScan(src,
                                 new File("t/A.java", "package t; public class A { static class S { private static void f() {} } private class F { private void t() {S.f();} } }"));
        performInnerToOuterTest(true);
        verifyContent(src,
                      new File("t/F.java", "package t; class F { private final A outer; F(final A outer) { this.outer = outer; }\n private void t() { A.S.f(); } }\n"),//TODO: why outer reference?
                      new File("t/A.java", "package t; public class A { static class S { private static void f() {} } }"));
    }

    public void test195947() throws Exception {
        writeFilesAndWaitForScan(src,
                                 new File("t/A.java", "package t; public class A { private final int foo; public A() { this.foo = 0; } static class F { } }")); 
        performInnerToOuterTest(false);
        verifyContent(src,
                      new File("t/F.java", "package t; class F { }\n"),
                      new File("t/A.java", "package t; public class A { private final int foo; public A() { this.foo = 0; } }"));

}
    
    public void test138204b() throws Exception {
        writeFilesAndWaitForScan(src,
                                 new File("t/A.java", "package t; public class A { static class S { private static void f() {} } private class F { private void t() { A.S.f(); t();} } }"));
        performInnerToOuterTest(true);
        verifyContent(src,
                      new File("t/F.java", "package t; class F { private final A outer; F(final A outer) { this.outer = outer; }\n private void t() { A.S.f();  t(); } }\n"),//TODO: why outer reference?
                      new File("t/A.java", "package t; public class A { static class S { private static void f() {} } }"));
    }

    public void test138204c() throws Exception {
        writeFilesAndWaitForScan(src,
                                 new File("t/A.java", "package t; public class A { private static class S { private static void f() {} } private class F { private void t() {S.f();} } }"));
        performInnerToOuterTest(true, new Problem(false, "WRN_InnerToOuterRefToPrivate/t.A.S"));
        verifyContent(src,
                      new File("t/F.java", "package t; class F { private final A outer; F(final A outer) { this.outer = outer; }\n private void t() { A.S.f(); } }\n"),//TODO: why outer reference?
                      new File("t/A.java", "package t; public class A { private static class S { private static void f() {} } }"));
    }

    public void test180364() throws Exception {
        writeFilesAndWaitForScan(src,
                                 new File("t/A.java", "package t; public class A { int i; static class F extends A { private void t() { i = 0; } } }"));
        performInnerToOuterTest(false);
        verifyContent(src,
                      new File("t/F.java", "package t; class F extends A {  private void t() { i = 0; } }\n"),//TODO: why outer reference?
                      new File("t/A.java", "package t; public class A { int i; }"));
    }
    
    public void test144209() throws Exception {
        writeFilesAndWaitForScan(src,
                                 new File("t/A.java",
                                          "package t;\n" +
                                          "\n" +
                                          "import java.awt.event.ActionEvent;\n" +
                                          "import java.awt.event.MouseEvent;\n" +
                                          "import javax.swing.AbstractAction;\n" +
                                          "\n" +
                                          "public class Outer {\n" +
                                          "\n" +
                                          "    static void refresh() {\n" +
                                          "    }\n" +
                                          "\n" +
                                          "    /**\n" +
                                          "     * javadoc comment for F.\n" +
                                          "     */\n" +
                                          "    static class F {\n" +
                                          "        void refresh() {\n" +
                                          "            //Outer.refresh();\n" +
                                          "        }\n" +
                                          "\n" +
                                          "        /**\n" +
                                          "         * javadoc for F.handler\n" +
                                          "         * @param e\n" +
                                          "         */\n" +
                                          "        void handler(MouseEvent e) {\n" +
                                          "            new InnerInner();\n" +
                                          "        }\n" +
                                          "\n" +
                                          "        private void someInnerMethod() {\n" +
                                          "            // test comment\n" +
                                          "            System.err.println(\"in inner method\");\n" +
                                          "        }\n" +
                                          "\n" +
                                          "        /**\n" +
                                          "         * javadoc comment for InnerInner\n" +
                                          "         */\n" +
                                          "        private class InnerInner extends AbstractAction {\n" +
                                          "\n" +
                                          "            /* coment with '*' */\n" +
                                          "\n" +
                                          "            @Override\n" +
                                          "            public void actionPerformed(ActionEvent e) {\n" +
                                          "                someInnerMethod();\n" +
                                          "            }\n" +
                                          "\n" +
                                          "        }\n" +
                                          "    }\n" +
                                          "\n" +
                                          "}\n"));
        performInnerToOuterTest(false);
        verifyContent(src,
                                 new File("t/A.java",
                                          "package t;\n" +
                                          "\n" +
                                          "import java.awt.event.ActionEvent;\n" +
                                          "import java.awt.event.MouseEvent;\n" +
                                          "import javax.swing.AbstractAction;\n" +
                                          "\n" +
                                          "public class Outer {\n" +
                                          "\n" +
                                          "    static void refresh() {\n" +
                                          "    }\n" +
                                          "\n" +
                                          "}\n"),
                                 new File("t/F.java",
                                          "package t;\n" +
                                          "\n" +
                                          "import java.awt.event.ActionEvent;\n" +
                                          "import java.awt.event.MouseEvent;\n" +
                                          "import javax.swing.AbstractAction;\n" +
                                          "\n" +
                                          "/**\n" +
                                          " * javadoc comment for F.\n" +
                                          " */\n" +
                                          "class F {\n" +
                                          "    void refresh() {\n" +
                                          "        //Outer.refresh();\n" +
                                          "    }\n" +
                                          "\n" +
                                          "    /**\n" +
                                          "     * javadoc for F.handler\n" +
                                          "     * @param e\n" +
                                          "     */\n" +
                                          "    void handler(MouseEvent e) {\n" +
                                          "        new InnerInner();\n" +
                                          "    }\n" +
                                          "\n" +
                                          "    private void someInnerMethod() {\n" +
                                          "        // test comment\n" +
                                          "        System.err.println(\"in inner method\");\n" +
                                          "    }\n" +
                                          "\n" +
                                          "    /**\n" +
                                          "     * javadoc comment for InnerInner\n" +
                                          "     */\n" +
                                          "    private class InnerInner extends AbstractAction {\n" +
                                          "\n" +
                                          "        /* coment with '*' */\n" +
                                          "\n" +
                                          "        @Override\n" +
                                          "        public void actionPerformed(ActionEvent e) {\n" +
                                          "            someInnerMethod();\n" +
                                          "        }\n" +
                                          "\n" +
                                          "    }\n" +
                                          "}\n"));
    }

    public void test187766() throws Exception {
        writeFilesAndWaitForScan(src,
                                 new File("t/A.java", "package t; public class A { int i; public enum F { A, B, C; } }"));
        performInnerToOuterTest(false);
        verifyContent(src,
                      new File("t/F.java", "package t; public enum F {  A, B, C }\n"),
                      new File("t/A.java", "package t; public class A { int i; }"));
    }
    
    public void test198186() throws Exception {
        writeFilesAndWaitForScan(src,
                new File("t/A.java", "/*\n"
                + " * To change this template, choose Tools | Templates\n"
                + " * and open the template in the editor.\n"
                + " */\n"
                + "package t;\n"
                + "\n"
                + "/**\n"
                + " *\n"
                + " * @author Ernie Rael <err at raelity.com>\n"
                + " */\n"
                + "public class A {\n"
                + "    ChangeNotify changeNotify;\n"
                + "\n"
                + "    public A(ChangeNotify changeNotify)\n"
                + "    {\n"
                + "        this.changeNotify = changeNotify;\n"
                + "    }\n"
                + "\n"
                + "    void foo() {\n"
                + "        StartAsNested n = new StartAsNested();\n"
                + "    }\n"
                + "\n"
                + "    public static interface ChangeNotify {\n"
                + "        public void change();\n"
                + "    }\n"
                + "    public static interface ForDebug {\n"
                + "        public void iFunc();\n"
                + "    }\n"
                + "\n"
                + "    class StartAsNested {\n"
                + "\n"
                + "        public StartAsNested()\n"
                + "        {\n"
                + "            ForDebug pcl;\n"
                + "            pcl = new ForDebug() {\n"
                + "                @Override\n"
                + "                public void iFunc() {\n"
                + "                    changeNotify.change();\n"
                + "                }\n"
                + "            };\n"
                + "        }\n"
                + "\n"
                + "        void func1()\n"
                + "        {\n"
                + "            C1 c = new MyC1(1);\n"
                + "        }\n"
                + "\n"
                + "        class MyC1 extends C1\n"
                + "        {\n"
                + "            public MyC1(int i)\n"
                + "            {\n"
                + "                super(i);\n"
                + "            }\n"
                + "        }\n"
                + "\n"
                + "        class C1\n"
                + "        {\n"
                + "            int i;\n"
                + "\n"
                + "            public C1()\n"
                + "            {\n"
                + "            }\n"
                + "\n"
                + "            public C1(int i)\n"
                + "            {\n"
                + "                this();\n"
                + "                this.i = i;\n"
                + "            }\n"
                + "\n"
                + "            @Override\n"
                + "            protected Object clone() throws CloneNotSupportedException\n"
                + "            {\n"
                + "                C1 c1 = new C1(i);\n"
                + "                return c1;\n"
                + "            }\n"
                + "        }\n"
                + "\n"
                + "    }\n"
                + "}\n"));
        performInnerToOuterTest(true);
        verifyContent(src,
                new File("t/StartAsNested.java", "package t;\n"
                + "\n"
                + "class StartAsNested {\n"
                + "\n"
                + "    private final A outer;\n"
                + "\n"
                + "    public StartAsNested(final A outer) {\n"
                + "        this.outer = outer;\n"
                + "        A.ForDebug pcl;\n"
                + "        pcl = new A.ForDebug() {\n"
                + "\n"
                + "            @Override\n"
                + "            public void iFunc() {\n"
                + "                outer.changeNotify.change();\n"
                + "            }\n"
                + "        };\n"
                + "    }\n"
                + "\n"
                + "    void func1() {\n"
                + "        C1 c = new MyC1(1);\n"
                + "    }\n"
                + "\n"
                + "    class MyC1 extends C1 {\n"
                + "\n"
                + "        public MyC1(int i) {\n"
//                + "            super(i);\n" // Should be fixed in #197097
                + "        }\n"
                + "    }\n"
                + "\n"
                + "    class C1 {\n"
                + "\n"
                + "        int i;\n"
                + "\n"
                + "        public C1() {\n"
                + "        }\n"
                + "\n"
                + "        public C1(int i) {\n"
                + "            this();\n"
                + "            this.i = i;\n"
                + "        }\n"
                + "\n"
                + "        @Override\n"
                + "        protected Object clone() throws CloneNotSupportedException {\n"
                + "            C1 c1 = new C1(i);\n"
                + "            return c1;\n"
                + "        }\n"
                + "    }\n"
                + "}\n"
                + "\n"),
                new File("t/A.java", "/*\n"
                + " * To change this template, choose Tools | Templates\n"
                + " * and open the template in the editor.\n"
                + " */\n"
                + "package t;\n"
                + "\n"
                + "/**\n"
                + " *\n"
                + " * @author Ernie Rael <err at raelity.com>\n"
                + " */\n"
                + "public class A {\n"
                + "    ChangeNotify changeNotify;\n"
                + "\n"
                + "    public A(ChangeNotify changeNotify)\n"
                + "    {\n"
                + "        this.changeNotify = changeNotify;\n"
                + "    }\n"
                + "\n"
                + "    void foo() {\n"
                + "        StartAsNested n = new StartAsNested(this);\n"
                + "    }\n"
                + "\n"
                + "    public static interface ChangeNotify {\n"
                + "        public void change();\n"
                + "    }\n"
                + "    public static interface ForDebug {\n"
                + "        public void iFunc();\n"
                + "    }\n"
                + "}\n"));
    }

    public void test177996() throws Exception {
        writeFilesAndWaitForScan(src,
                                 new File("t/A.java", "package t; public class A { public void t() { A t = new A(); Inner inner = t.new Inner(); } class Inner { }}"));
        performInnerToOuterTest(true);
        verifyContent(src,
                      new File("t/Inner.java", "package t; class Inner { private final A outer; Inner(final A outer) { this.outer = outer; } } "),
                      new File("t/A.java", "package t; public class A { public void t() { A t = new A(); Inner inner = new Inner(t); }}"));
    }
    
    private void performInnerToOuterTest(boolean generateOuter, Problem... expectedProblems) throws Exception {
        final InnerToOuterRefactoring[] r = new InnerToOuterRefactoring[1];
        
        JavaSource.forFileObject(src.getFileObject("t/A.java")).runUserActionTask(new Task<CompilationController>() {

            public void run(CompilationController parameter) throws Exception {
                parameter.toPhase(JavaSource.Phase.RESOLVED);
                CompilationUnitTree cut = parameter.getCompilationUnit();
                
                ClassTree outter = (ClassTree) cut.getTypeDecls().get(0);
                ClassTree inner = (ClassTree) outter.getMembers().get(outter.getMembers().size() - 1);

                TreePath tp = TreePath.getPath(cut, inner);
                r[0] = new InnerToOuterRefactoring(TreePathHandle.create(tp, parameter));
            }
        }, true);

        r[0].setClassName("F");
        r[0].setReferenceName(null);

        RefactoringSession rs = RefactoringSession.create("Session");
        List<Problem> problems = new LinkedList<Problem>();

        addAllProblems(problems, r[0].preCheck());
        if (!generateOuter) r[0].setReferenceName(null);
        addAllProblems(problems, r[0].prepare(rs));
        addAllProblems(problems, rs.doRefactoring(true));

        assertProblems(Arrays.asList(expectedProblems), problems);
    }

}
