/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2008-2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.java.hints.jackpot.spi;

import com.sun.javadoc.Doc;
import com.sun.javadoc.Tag;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.CatchTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.TryTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.VariableTree;
import java.util.Collection;
import java.util.regex.Matcher;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Scope;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.UnionTypeTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.SourceUtils;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.TypeMirrorHandle;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.java.hints.jackpot.impl.JavaFixImpl;
import org.netbeans.modules.java.hints.jackpot.impl.Utilities;
import org.netbeans.modules.java.hints.jackpot.impl.pm.Pattern;
import org.netbeans.spi.editor.hints.ChangeInfo;
import org.netbeans.spi.editor.hints.Fix;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.filesystems.FileObject;
import org.openide.modules.SpecificationVersion;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Jan Lahoda
 */
public abstract class JavaFix {

    public static final String ENSURE_DEPENDENCY = "ensure-dependency";

    private final TreePathHandle handle;
    private final Map<String, String> options;

    protected JavaFix(CompilationInfo info, TreePath tp) {
        this(info, tp, Collections.<String, String>emptyMap());
    }

    protected JavaFix(CompilationInfo info, TreePath tp, Map<String, String> options) {
        this.handle = TreePathHandle.create(tp, info);
        this.options = Collections.unmodifiableMap(new HashMap<String, String>(options));
    }

    protected JavaFix(TreePathHandle handle) {
        this(handle, Collections.<String, String>emptyMap());
    }

    protected JavaFix(TreePathHandle handle, Map<String, String> options) {
        this.handle = handle;
        this.options = Collections.unmodifiableMap(new HashMap<String, String>(options));
    }

    protected abstract String getText();

    protected abstract void performRewrite(WorkingCopy wc, TreePath tp, boolean canShowUI);

    final ChangeInfo process(WorkingCopy wc, boolean canShowUI) throws Exception {
        TreePath tp = handle.resolve(wc);

        if (tp == null) {
            Logger.getLogger(JavaFix.class.getName()).log(Level.SEVERE, "Cannot resolve handle={0}", handle);
            return null;
        }

        performRewrite(wc, tp, canShowUI);

        return null;
    }

    final FileObject getFile() {
        return handle.getFileObject();
    }

    public static Fix rewriteFix(HintContext ctx, String displayName, TreePath what, final String to, String... imports) {
        return rewriteFix(ctx, displayName, what, to, Collections.<String, TypeMirror>emptyMap(), imports);
    }
    
    public static Fix rewriteFix(HintContext ctx, String displayName, TreePath what, final String to, Map<String, TypeMirror> constraints, String... imports) {
        return rewriteFix(ctx.getInfo(), displayName, what, to, ctx.getVariables(), ctx.getMultiVariables(), ctx.getVariableNames(), constraints, imports);
    }

    public static Fix rewriteFix(CompilationInfo info, String displayName, TreePath what, final String to, Map<String, TreePath> parameters, Map<String, Collection<? extends TreePath>> parametersMulti, final Map<String, String> parameterNames, Map<String, TypeMirror> constraints, String... imports) {
        return rewriteFix(info, displayName, what, to, parameters, parametersMulti, parameterNames, constraints, Collections.<String, String>emptyMap(), imports);
    }

    public static Fix rewriteFix(CompilationInfo info, String displayName, TreePath what, final String to, Map<String, TreePath> parameters, Map<String, Collection<? extends TreePath>> parametersMulti, final Map<String, String> parameterNames, Map<String, TypeMirror> constraints, Map<String, String> options, String... imports) {
        final Map<String, TreePathHandle> params = new HashMap<String, TreePathHandle>();

        for (Entry<String, TreePath> e : parameters.entrySet()) {
            params.put(e.getKey(), TreePathHandle.create(e.getValue(), info));
        }

        final Map<String, Collection<TreePathHandle>> paramsMulti = new HashMap<String, Collection<TreePathHandle>>();

        for (Entry<String, Collection<? extends TreePath>> e : parametersMulti.entrySet()) {
            Collection<TreePathHandle> tph = new LinkedList<TreePathHandle>();

            for (TreePath tp : e.getValue()) {
                tph.add(TreePathHandle.create(tp, info));
            }

            paramsMulti.put(e.getKey(), tph);
        }

        final Map<String, TypeMirrorHandle<?>> constraintsHandles = new HashMap<String, TypeMirrorHandle<?>>();

        for (Entry<String, TypeMirror> c : constraints.entrySet()) {
            constraintsHandles.put(c.getKey(), TypeMirrorHandle.create(c.getValue()));
        }

        if (displayName == null) {
            displayName = defaultFixDisplayName(info, parameters, to);
        }

        return toEditorFix(new JavaFixRealImpl(info, what, options, displayName, to, params, paramsMulti, parameterNames, constraintsHandles, Arrays.asList(imports)));
    }

    private static String defaultFixDisplayName(CompilationInfo info, Map<String, TreePath> variables, String replaceTarget) {
        Map<String, String> stringsForVariables = new HashMap<String, String>();

        for (Entry<String, TreePath> e : variables.entrySet()) {
            Tree t = e.getValue().getLeaf();
            SourcePositions sp = info.getTrees().getSourcePositions();
            int startPos = (int) sp.getStartPosition(info.getCompilationUnit(), t);
            int endPos = (int) sp.getEndPosition(info.getCompilationUnit(), t);

            if (startPos >= 0 && endPos >= 0) {
                stringsForVariables.put(e.getKey(), info.getText().substring(startPos, endPos));
            } else {
                stringsForVariables.put(e.getKey(), "");
            }
        }

        if (!stringsForVariables.containsKey("$this")) {
            //XXX: is this correct?
            stringsForVariables.put("$this", "this");
        }

        for (Entry<String, String> e : stringsForVariables.entrySet()) {
            String quotedVariable = java.util.regex.Pattern.quote(e.getKey());
            String quotedTarget = Matcher.quoteReplacement(e.getValue());
            replaceTarget = replaceTarget.replaceAll(quotedVariable, quotedTarget);
        }

        return "Rewrite to " + replaceTarget;
    }

    private static void checkDependency(WorkingCopy copy, Element e, boolean canShowUI) {
        SpecificationVersion sv = computeSpecVersion(copy, e);

        while (sv == null && e.getKind() != ElementKind.PACKAGE) {
            e = e.getEnclosingElement();
            sv = computeSpecVersion(copy, e);
        }
        
        if (sv == null) {
            return ;
        }

        Project currentProject = FileOwnerQuery.getOwner(copy.getFileObject());

        if (currentProject == null) {
            return ;
        }

        FileObject file = getFile(copy, e);

        if (file == null) {
            return ;
        }

        FileObject root = findRootForFile(file, copy.getClasspathInfo());

        if (root == null) {
            return ;
        }

        Project referedProject = FileOwnerQuery.getOwner(file);

        if (referedProject != null && currentProject.getProjectDirectory().equals(referedProject.getProjectDirectory())) {
            return ;
        }

        for (ProjectDependencyUpgrader pdu : Lookup.getDefault().lookupAll(ProjectDependencyUpgrader.class)) {
            if (pdu.ensureDependency(currentProject, root, sv, canShowUI)) {
                return ;
            }
        }
    }

    private static java.util.regex.Pattern SPEC_VERSION = java.util.regex.Pattern.compile("[0-9]+(\\.[0-9]+)+");
    
    static SpecificationVersion computeSpecVersion(CompilationInfo info, Element el) {
        if (!Utilities.isJavadocSupported(info)) return null;
        
        Doc javaDoc = info.getElementUtilities().javaDocFor(el);

        if (javaDoc == null) return null;

        for (Tag since : javaDoc.tags("@since")) {
            String text = since.text();

            Matcher m = SPEC_VERSION.matcher(text);

            if (!m.find()) {
                continue;
            }

            return new SpecificationVersion(m.group()/*ver.toString()*/);
        }

        return null;
    }
    
    public static Fix toEditorFix(final JavaFix jf) {
        return new JavaFixImpl(jf);
    }

    @SuppressWarnings("deprecation")
    private static FileObject getFile(WorkingCopy copy, Element e) {
        return SourceUtils.getFile(e, copy.getClasspathInfo());
    }

    private static FileObject findRootForFile(final FileObject file, final ClasspathInfo cpInfo) {
        ClassPath cp = ClassPathSupport.createProxyClassPath(
            new ClassPath[] {
                cpInfo.getClassPath(ClasspathInfo.PathKind.SOURCE),
                cpInfo.getClassPath(ClasspathInfo.PathKind.BOOT),
                cpInfo.getClassPath(ClasspathInfo.PathKind.COMPILE),
            });

        FileObject root = cp.findOwnerRoot(file);

        if (root != null) {
            return root;
        }

        for (ClassPath.Entry e : cp.entries()) {
            FileObject[] sourceRoots = SourceForBinaryQuery.findSourceRoots(e.getURL()).getRoots();

            if (sourceRoots.length == 0) continue;

            ClassPath sourcePath = ClassPathSupport.createClassPath(sourceRoots);

            root = sourcePath.findOwnerRoot(file);

            if (root != null) {
                return root;
            }
        }
        return null;
    }

    private static boolean isStaticElement(Element el) {
        if (el == null) return false;

        if (el.asType() == null || el.asType().getKind() == TypeKind.ERROR) {
            return false;
        }
        
        if (el.getModifiers().contains(Modifier.STATIC)) {
            //XXX:
            if (!el.getKind().isClass() && !el.getKind().isInterface()) {
                return false;
            }
            
            return true;
        }

        if (el.getKind().isClass() || el.getKind().isInterface()) {
            return el.getEnclosingElement().getKind() == ElementKind.PACKAGE;
        }

        return false;
    }

    private static class JavaFixRealImpl extends JavaFix {
        private final String displayName;
        private final Map<String, TreePathHandle> params;
        private final Map<String, Collection<TreePathHandle>> paramsMulti;
        private final Map<String, String> parameterNames;
        private final Map<String, TypeMirrorHandle<?>> constraintsHandles;
        private final Iterable<? extends String> imports;
        private final String to;

        public JavaFixRealImpl(CompilationInfo info, TreePath what, Map<String, String> options, String displayName, String to, Map<String, TreePathHandle> params, Map<String, Collection<TreePathHandle>> paramsMulti, final Map<String, String> parameterNames, Map<String, TypeMirrorHandle<?>> constraintsHandles, Iterable<? extends String> imports) {
            super(info, what, options);
            
            this.displayName = displayName;
            this.to = to;
            this.params = params;
            this.paramsMulti = paramsMulti;
            this.parameterNames = parameterNames;
            this.constraintsHandles = constraintsHandles;
            this.imports = imports;
        }

        @Override
        protected String getText() {
            return displayName;
        }

        @Override
        protected void performRewrite(final WorkingCopy wc, TreePath tp, final boolean canShowUI) {
            final Map<String, TreePath> parameters = new HashMap<String, TreePath>();

            for (Entry<String, TreePathHandle> e : params.entrySet()) {
                TreePath p = e.getValue().resolve(wc);

                if (p == null) {
                    Logger.getLogger(JavaFix.class.getName()).log(Level.SEVERE, "Cannot resolve handle={0}", e.getValue());
                }

                parameters.put(e.getKey(), p);
            }

            final Map<String, Collection<TreePath>> parametersMulti = new HashMap<String, Collection<TreePath>>();

            for (Entry<String, Collection<TreePathHandle>> e : paramsMulti.entrySet()) {
                Collection<TreePath> tps = new LinkedList<TreePath>();

                for (TreePathHandle tph : e.getValue()) {
                    TreePath p = tph.resolve(wc);

                    if (p == null) {
                        Logger.getLogger(JavaFix.class.getName()).log(Level.SEVERE, "Cannot resolve handle={0}", e.getValue());
                    }

                    tps.add(p);
                }

                parametersMulti.put(e.getKey(), tps);
            }

            Map<String, TypeMirror> constraints = new HashMap<String, TypeMirror>();

            for (Entry<String, TypeMirrorHandle<?>> c : constraintsHandles.entrySet()) {
                constraints.put(c.getKey(), c.getValue().resolve(wc));
            }

            Tree parsed = Pattern.parseAndAttribute(wc, to, constraints, new Scope[1], imports);

            if (Utilities.isFakeBlock(parsed)) {
                TreePath parent = tp.getParentPath();
                List<? extends StatementTree> statements = ((BlockTree) parsed).getStatements();

                statements = statements.subList(1, statements.size() - 1);

                if (parent.getLeaf().getKind() == Kind.BLOCK) {
                    List<StatementTree> newStatements = new LinkedList<StatementTree>();

                    for (StatementTree st : ((BlockTree) parent.getLeaf()).getStatements()) {
                        if (st == tp.getLeaf()) {
                            newStatements.addAll(statements);
                        } else {
                            newStatements.add(st);
                        }
                    }

                    wc.rewrite(parent.getLeaf(), wc.getTreeMaker().Block(newStatements, ((BlockTree) parent.getLeaf()).isStatic()));
                } else {
                    wc.rewrite(tp.getLeaf(), wc.getTreeMaker().Block(statements, false));
                }
            } else if (Utilities.isFakeClass(parsed)) {
                TreePath parent = tp.getParentPath();
                List<? extends Tree> members = ((ClassTree) parsed).getMembers();

                members = members.subList(1, members.size());

                assert parent.getLeaf().getKind() == Kind.CLASS;

                List<Tree> newMembers = new LinkedList<Tree>();

                ClassTree ct = (ClassTree) parent.getLeaf();

                for (Tree t : ct.getMembers()) {
                    if (t == tp.getLeaf()) {
                        newMembers.addAll(members);
                    } else {
                        newMembers.add(t);
                    }
                }

                wc.rewrite(parent.getLeaf(), wc.getTreeMaker().Class(ct.getModifiers(), ct.getSimpleName(), ct.getTypeParameters(), ct.getExtendsClause(), ct.getImplementsClause(), newMembers));
            } else if (tp.getLeaf().getKind() == Kind.BLOCK && parametersMulti.containsKey("$$1$") && parsed.getKind() != Kind.BLOCK && StatementTree.class.isAssignableFrom(parsed.getKind().asInterface())) {
                List<StatementTree> newStatements = new LinkedList<StatementTree>();

                newStatements.add(wc.getTreeMaker().ExpressionStatement(wc.getTreeMaker().Identifier("$$1$")));
                newStatements.add((StatementTree) parsed);
                newStatements.add(wc.getTreeMaker().ExpressionStatement(wc.getTreeMaker().Identifier("$$2$")));

                parsed = wc.getTreeMaker().Block(newStatements, ((BlockTree) tp.getLeaf()).isStatic());

                wc.rewrite(tp.getLeaf(), parsed);
            } else {
                while (   tp.getParentPath().getLeaf().getKind() == Kind.PARENTHESIZED
                       && tp.getLeaf().getKind() != parsed.getKind()
                       && tp.getParentPath() != null
                       && tp.getParentPath().getParentPath() != null
                       && !requiresParenthesis(parsed, tp.getParentPath().getLeaf(), tp.getParentPath().getParentPath().getLeaf())
                       && requiresParenthesis(tp.getLeaf(), tp.getParentPath().getLeaf(), tp.getParentPath().getParentPath().getLeaf()))
                    tp = tp.getParentPath();
                wc.rewrite(tp.getLeaf(), parsed);
            }

            //prevent generating QualIdents inside import clauses - might be better to solve that inside ImportAnalysis2,
            //but that seems not to be straightforward:
            boolean inImport = parsed.getKind() == Kind.IMPORT;
            TreePath w = tp.getParentPath();

            while (!inImport && w != null) {
                inImport |= w.getLeaf().getKind() == Kind.IMPORT;
                w = w.getParentPath();
            }

            new ReplaceParameters(wc, canShowUI, inImport, parameters, parametersMulti, parameterNames).scan(new TreePath(tp.getParentPath(), parsed), null);
        }
    }

    private static final Set<Kind> NUMBER_LITERAL_KINDS = EnumSet.of(Kind.FLOAT_LITERAL, Kind.DOUBLE_LITERAL, Kind.INT_LITERAL, Kind.LONG_LITERAL);
    
    private static class ReplaceParameters extends TreePathScanner<Number, Void> {

        private final WorkingCopy wc;
        private final boolean canShowUI;
        private final boolean inImport;
        private final Map<String, TreePath> parameters;
        private final Map<String, Collection<TreePath>> parametersMulti;
        private final Map<String, String> parameterNames;

        public ReplaceParameters(WorkingCopy wc, boolean canShowUI, boolean inImport, Map<String, TreePath> parameters, Map<String, Collection<TreePath>> parametersMulti, Map<String, String> parameterNames) {
            this.parameters = parameters;
            this.wc = wc;
            this.canShowUI = canShowUI;
            this.inImport = inImport;
            this.parametersMulti = parametersMulti;
            this.parameterNames = parameterNames;
        }

        @Override
        public Number visitIdentifier(IdentifierTree node, Void p) {
            String name = node.getName().toString();
            TreePath tp = parameters.get(name);

            if (tp != null) {
                if (tp.getLeaf() instanceof Hacks.RenameTree) {
                    Hacks.RenameTree rt = (Hacks.RenameTree) tp.getLeaf();
                    Tree nue = wc.getTreeMaker().setLabel(rt.originalTree, rt.newName);

                    wc.rewrite(node, nue);

                    return null;
                }
                if (!parameterNames.containsKey(name)) {
                    wc.rewrite(node, tp.getLeaf());
                    if (NUMBER_LITERAL_KINDS.contains(tp.getLeaf().getKind())) {
                        return (Number) ((LiteralTree) tp.getLeaf()).getValue();
                    }
                    Tree target = tp.getLeaf();
                    //TODO: might also remove parenthesis, but needs to ensure the diff will still be minimal
//                    while (target.getKind() == Kind.PARENTHESIZED
//                           && !requiresParenthesis(((ParenthesizedTree) target).getExpression(), getCurrentPath().getParentPath().getLeaf())) {
//                        target = ((ParenthesizedTree) target).getExpression();
//                    }
                    if (requiresParenthesis(target, node, getCurrentPath().getParentPath().getLeaf())) {
                        target = wc.getTreeMaker().Parenthesized((ExpressionTree) target);
                    }
                    wc.rewrite(node, target);
                    return null;
                }
            }

            String variableName = parameterNames.get(name);

            if (variableName != null) {
                wc.rewrite(node, wc.getTreeMaker().Identifier(variableName));
                return null;
            }

            Element e = wc.getTrees().getElement(getCurrentPath());

            if (e != null && isStaticElement(e) && !inImport) {
                wc.rewrite(node, wc.getTreeMaker().QualIdent(e));
            }

            return super.visitIdentifier(node, p);
        }

        @Override
        public Number visitMemberSelect(MemberSelectTree node, Void p) {
            Element e = wc.getTrees().getElement(getCurrentPath());

            if (e == null || (e.getKind() == ElementKind.CLASS && ((TypeElement) e).asType().getKind() == TypeKind.ERROR)) {
                MemberSelectTree nue = node;
                String selectedName = node.getIdentifier().toString();

                if (selectedName.startsWith("$") && parameterNames.get(selectedName) != null) {
                    nue = wc.getTreeMaker().MemberSelect(node.getExpression(), parameterNames.get(selectedName));
                }

                if (nue.getExpression().getKind() == Kind.IDENTIFIER) {
                    String name = ((IdentifierTree) nue.getExpression()).getName().toString();

                    if (name.startsWith("$") && parameters.get(name) == null) {
                        //XXX: unbound variable, use identifier instead of member select - may cause problems?
                        wc.rewrite(node, wc.getTreeMaker().Identifier(nue.getIdentifier()));
                        return null;
                    }
                }

                wc.rewrite(node, nue);
                
                return super.visitMemberSelect(node, p);
            }

            //check correct dependency:
            checkDependency(wc, e, canShowUI);

            if (isStaticElement(e) && !inImport) {
                wc.rewrite(node, wc.getTreeMaker().QualIdent(e));

                return null;
            } else {
                return super.visitMemberSelect(node, p);
            }
        }

        @Override
        public Number visitVariable(VariableTree node, Void p) {
            String name = node.getName().toString();

            if (name.startsWith("$")) {
                String nueName = parameterNames.get(name);

                if (nueName != null) {
                    VariableTree nue = wc.getTreeMaker().Variable(node.getModifiers(), nueName, node.getType(), node.getInitializer());

                    wc.rewrite(node, nue);

                    return super.visitVariable(nue, p);
                }
            }

            return super.visitVariable(node, p);
        }

        @Override
        public Number visitExpressionStatement(ExpressionStatementTree node, Void p) {
            CharSequence name = Utilities.getWildcardTreeName(node);

            if (name != null) {
                TreePath tp = parameters.get(name.toString());

                if (tp != null) {
                    wc.rewrite(node, tp.getLeaf());
                    return null;
                }
            }

            return super.visitExpressionStatement(node, p);
        }

        @Override
        public Number visitLiteral(LiteralTree node, Void p) {
            if (node.getValue() instanceof Number) {
                return (Number) node.getValue();
            }
            
            return super.visitLiteral(node, p);
        }

        @Override
        public Number visitBinary(BinaryTree node, Void p) {
            Number left  = scan(node.getLeftOperand(), p);
            Number right = scan(node.getRightOperand(), p);

            if (left != null && right != null) {
                Number result = null;
                switch (node.getKind()) {
                    case MULTIPLY:
                            if (left instanceof Double || right instanceof Double) {
                                result = left.doubleValue() * right.doubleValue();
                            } else if (left instanceof Float || right instanceof Float) {
                                result = left.floatValue() * right.floatValue();
                            } else if (left instanceof Long || right instanceof Long) {
                                result = left.longValue() * right.longValue();
                            } else if (left instanceof Integer || right instanceof Integer) {
                                result = left.intValue() * right.intValue();
                            } else {
                                throw new IllegalStateException("left=" + left.getClass() + ", right=" + right.getClass());
                            }
                            break;

                    case DIVIDE:
                            if (left instanceof Double || right instanceof Double) {
                                result = left.doubleValue() / right.doubleValue();
                            } else if (left instanceof Float || right instanceof Float) {
                                result = left.floatValue() / right.floatValue();
                            } else if (left instanceof Long || right instanceof Long) {
                                result = left.longValue() / right.longValue();
                            } else if (left instanceof Integer || right instanceof Integer) {
                                result = left.intValue() / right.intValue();
                            } else {
                                throw new IllegalStateException("left=" + left.getClass() + ", right=" + right.getClass());
                            }
                            break;

                    case REMAINDER:
                            if (left instanceof Double || right instanceof Double) {
                                result = left.doubleValue() % right.doubleValue();
                            } else if (left instanceof Float || right instanceof Float) {
                                result = left.floatValue() % right.floatValue();
                            } else if (left instanceof Long || right instanceof Long) {
                                result = left.longValue() % right.longValue();
                            } else if (left instanceof Integer || right instanceof Integer) {
                                result = left.intValue() % right.intValue();
                            } else {
                                throw new IllegalStateException("left=" + left.getClass() + ", right=" + right.getClass());
                            }
                            break;

                    case PLUS:
                            if (left instanceof Double || right instanceof Double) {
                                result = left.doubleValue() + right.doubleValue();
                            } else if (left instanceof Float || right instanceof Float) {
                                result = left.floatValue() + right.floatValue();
                            } else if (left instanceof Long || right instanceof Long) {
                                result = left.longValue() + right.longValue();
                            } else if (left instanceof Integer || right instanceof Integer) {
                                result = left.intValue() + right.intValue();
                            } else {
                                throw new IllegalStateException("left=" + left.getClass() + ", right=" + right.getClass());
                            }
                            break;
                            
                    case MINUS:
                            if (left instanceof Double || right instanceof Double) {
                                result = left.doubleValue() - right.doubleValue();
                            } else if (left instanceof Float || right instanceof Float) {
                                result = left.floatValue() - right.floatValue();
                            } else if (left instanceof Long || right instanceof Long) {
                                result = left.longValue() - right.longValue();
                            } else if (left instanceof Integer || right instanceof Integer) {
                                result = left.intValue() - right.intValue();
                            } else {
                                throw new IllegalStateException("left=" + left.getClass() + ", right=" + right.getClass());
                            }
                            break;

                    case LEFT_SHIFT:
                            if (left instanceof Long || right instanceof Long) {
                                result = left.longValue() << right.longValue();
                            } else if (left instanceof Integer || right instanceof Integer) {
                                result = left.intValue() << right.intValue();
                            } else {
                                throw new IllegalStateException("left=" + left.getClass() + ", right=" + right.getClass());
                            }
                            break;

                    case RIGHT_SHIFT:
                            if (left instanceof Long || right instanceof Long) {
                                result = left.longValue() >> right.longValue();
                            } else if (left instanceof Integer || right instanceof Integer) {
                                result = left.intValue() >> right.intValue();
                            } else {
                                throw new IllegalStateException("left=" + left.getClass() + ", right=" + right.getClass());
                            }
                            break;

                    case UNSIGNED_RIGHT_SHIFT:
                            if (left instanceof Long || right instanceof Long) {
                                result = left.longValue() >>> right.longValue();
                            } else if (left instanceof Integer || right instanceof Integer) {
                                result = left.intValue() >>> right.intValue();
                            } else {
                                throw new IllegalStateException("left=" + left.getClass() + ", right=" + right.getClass());
                            }
                            break;

                    case AND:
                            if (left instanceof Long || right instanceof Long) {
                                result = left.longValue() & right.longValue();
                            } else if (left instanceof Integer || right instanceof Integer) {
                                result = left.intValue() & right.intValue();
                            } else {
                                throw new IllegalStateException("left=" + left.getClass() + ", right=" + right.getClass());
                            }
                            break;

                    case XOR:
                            if (left instanceof Long || right instanceof Long) {
                                result = left.longValue() ^ right.longValue();
                            } else if (left instanceof Integer || right instanceof Integer) {
                                result = left.intValue() ^ right.intValue();
                            } else {
                                throw new IllegalStateException("left=" + left.getClass() + ", right=" + right.getClass());
                            }
                            break;

                    case OR:
                            if (left instanceof Long || right instanceof Long) {
                                result = left.longValue() | right.longValue();
                            } else if (left instanceof Integer || right instanceof Integer) {
                                result = left.intValue() | right.intValue();
                            } else {
                                throw new IllegalStateException("left=" + left.getClass() + ", right=" + right.getClass());
                            }
                            break;
                }

                if (result != null) {
                    wc.rewrite(node, wc.getTreeMaker().Literal(result));

                    return result;
                }
            }

            return null;
        }

        @Override
        public Number visitUnary(UnaryTree node, Void p) {
            Number op  = scan(node.getExpression(), p);

            if (op != null) {
                Number result = null;
                switch (node.getKind()) {
                    case UNARY_MINUS:
                            if (op instanceof Double) {
                                result = -op.doubleValue();
                            } else if (op instanceof Float) {
                                result = -op.floatValue();
                            } else if (op instanceof Long) {
                                result = -op.longValue();
                            } else if (op instanceof Integer) {
                                result = -op.intValue();
                            } else {
                                throw new IllegalStateException("op=" + op.getClass());
                            }
                            break;
                    case UNARY_PLUS:
                        result = op;
                        break;
                }
                
                if (result != null) {
                    wc.rewrite(node, wc.getTreeMaker().Literal(result));

                    return result;
                }
            }

            return super.visitUnary(node, p);
        }

        @Override
        public Number visitBlock(BlockTree node, Void p) {
            List<? extends StatementTree> nueStatement = resolveMultiParameters(node.getStatements());
            BlockTree nue = wc.getTreeMaker().Block(nueStatement, node.isStatic());

            wc.rewrite(node, nue);

            return super.visitBlock(nue, p);
        }

        @Override
        public Number visitCase(CaseTree node, Void p) {
            List<? extends StatementTree> statements = (List<? extends StatementTree>) resolveMultiParameters(node.getStatements());
            CaseTree nue = wc.getTreeMaker().Case(node.getExpression(), statements);

            wc.rewrite(node, nue);
            return super.visitCase(node, p);
        }

        @Override
        public Number visitMethodInvocation(MethodInvocationTree node, Void p) {
            List<? extends ExpressionTree> typeArgs = (List<? extends ExpressionTree>) resolveMultiParameters(node.getTypeArguments());
            List<? extends ExpressionTree> args = resolveMultiParameters(node.getArguments());
            MethodInvocationTree nue = wc.getTreeMaker().MethodInvocation(typeArgs, node.getMethodSelect(), args);

            wc.rewrite(node, nue);

            return super.visitMethodInvocation(nue, p);
        }

        @Override
        public Number visitNewClass(NewClassTree node, Void p) {
            List<? extends ExpressionTree> typeArgs = (List<? extends ExpressionTree>) resolveMultiParameters(node.getTypeArguments());
            List<? extends ExpressionTree> args = resolveMultiParameters(node.getArguments());
            NewClassTree nue = wc.getTreeMaker().NewClass(node.getEnclosingExpression(), typeArgs, node.getIdentifier(), args, node.getClassBody());

            wc.rewrite(node, nue);
            return super.visitNewClass(nue, p);
        }

        @Override
        public Number visitParameterizedType(ParameterizedTypeTree node, Void p) {
            List<? extends ExpressionTree> typeArgs = (List<? extends ExpressionTree>) resolveMultiParameters(node.getTypeArguments());
            ParameterizedTypeTree nue = wc.getTreeMaker().ParameterizedType(node.getType(), typeArgs);

            wc.rewrite(node, nue);
            return super.visitParameterizedType(node, p);
        }

        @Override
        public Number visitSwitch(SwitchTree node, Void p) {
            List<? extends CaseTree> cases = (List<? extends CaseTree>) resolveMultiParameters(node.getCases());
            SwitchTree nue = wc.getTreeMaker().Switch(node.getExpression(), cases);

            wc.rewrite(node, nue);
            return super.visitSwitch(node, p);
        }

        @Override
        public Number visitTry(TryTree node, Void p) {
            List<? extends Tree> resources = (List<? extends Tree>) resolveMultiParameters(node.getResources());
            List<? extends CatchTree> catches = (List<? extends CatchTree>) resolveMultiParameters(node.getCatches());
            TryTree nue = wc.getTreeMaker().Try(resources, node.getBlock(), catches, node.getFinallyBlock());

            wc.rewrite(node, nue);
            return super.visitTry(node, p);
        }
        
        private <T extends Tree> List<T> resolveMultiParameters(List<T> list) {
            if (!Utilities.containsMultistatementTrees(list)) return list;

            List<T> result = new LinkedList<T>();

            for (T t : list) {
                if (Utilities.isMultistatementWildcardTree(t)) {
                    Collection<TreePath> embedded = parametersMulti.get(Utilities.getWildcardTreeName(t).toString());

                    if (embedded != null) {
                        for (TreePath tp : embedded) {
                            result.add((T) tp.getLeaf());
                        }
                    }
                } else {
                    result.add(t);
                }
            }

            return result;
        }
    }

    private static final Map<Kind, Integer> OPERATOR_PRIORITIES;

    static {
        OPERATOR_PRIORITIES = new EnumMap<Kind, Integer>(Kind.class);

        OPERATOR_PRIORITIES.put(Kind.IDENTIFIER, 0);

        for (Kind k : Kind.values()) {
            if (k.asInterface() == LiteralTree.class) {
                OPERATOR_PRIORITIES.put(k, 0);
            }
        }

        OPERATOR_PRIORITIES.put(Kind.ARRAY_ACCESS, 1);
        OPERATOR_PRIORITIES.put(Kind.METHOD_INVOCATION, 1);
        OPERATOR_PRIORITIES.put(Kind.MEMBER_SELECT, 1);
        OPERATOR_PRIORITIES.put(Kind.POSTFIX_DECREMENT, 1);
        OPERATOR_PRIORITIES.put(Kind.POSTFIX_INCREMENT, 1);
        OPERATOR_PRIORITIES.put(Kind.NEW_ARRAY, 1);
        OPERATOR_PRIORITIES.put(Kind.NEW_CLASS, 1);

        OPERATOR_PRIORITIES.put(Kind.BITWISE_COMPLEMENT, 2);
        OPERATOR_PRIORITIES.put(Kind.LOGICAL_COMPLEMENT, 2);
        OPERATOR_PRIORITIES.put(Kind.PREFIX_DECREMENT, 2);
        OPERATOR_PRIORITIES.put(Kind.PREFIX_INCREMENT, 2);
        OPERATOR_PRIORITIES.put(Kind.UNARY_MINUS, 2);
        OPERATOR_PRIORITIES.put(Kind.UNARY_PLUS, 2);

        OPERATOR_PRIORITIES.put(Kind.TYPE_CAST, 3);

        OPERATOR_PRIORITIES.put(Kind.DIVIDE, 4);
        OPERATOR_PRIORITIES.put(Kind.MULTIPLY, 4);
        OPERATOR_PRIORITIES.put(Kind.REMAINDER, 4);

        OPERATOR_PRIORITIES.put(Kind.MINUS, 5);
        OPERATOR_PRIORITIES.put(Kind.PLUS, 5);

        OPERATOR_PRIORITIES.put(Kind.LEFT_SHIFT, 6);
        OPERATOR_PRIORITIES.put(Kind.RIGHT_SHIFT, 6);
        OPERATOR_PRIORITIES.put(Kind.UNSIGNED_RIGHT_SHIFT, 6);

        OPERATOR_PRIORITIES.put(Kind.INSTANCE_OF, 7);
        OPERATOR_PRIORITIES.put(Kind.GREATER_THAN, 7);
        OPERATOR_PRIORITIES.put(Kind.GREATER_THAN_EQUAL, 7);
        OPERATOR_PRIORITIES.put(Kind.LESS_THAN, 7);
        OPERATOR_PRIORITIES.put(Kind.LESS_THAN_EQUAL, 7);

        OPERATOR_PRIORITIES.put(Kind.EQUAL_TO, 8);
        OPERATOR_PRIORITIES.put(Kind.NOT_EQUAL_TO, 8);

        OPERATOR_PRIORITIES.put(Kind.AND, 9);
        OPERATOR_PRIORITIES.put(Kind.OR, 11);
        OPERATOR_PRIORITIES.put(Kind.XOR, 10);

        OPERATOR_PRIORITIES.put(Kind.CONDITIONAL_AND, 12);
        OPERATOR_PRIORITIES.put(Kind.CONDITIONAL_OR, 13);

        OPERATOR_PRIORITIES.put(Kind.CONDITIONAL_EXPRESSION, 14);

        OPERATOR_PRIORITIES.put(Kind.AND_ASSIGNMENT, 15);
        OPERATOR_PRIORITIES.put(Kind.ASSIGNMENT, 15);
        OPERATOR_PRIORITIES.put(Kind.DIVIDE_ASSIGNMENT, 15);
        OPERATOR_PRIORITIES.put(Kind.LEFT_SHIFT_ASSIGNMENT, 15);
        OPERATOR_PRIORITIES.put(Kind.MINUS_ASSIGNMENT, 15);
        OPERATOR_PRIORITIES.put(Kind.MULTIPLY_ASSIGNMENT, 15);
        OPERATOR_PRIORITIES.put(Kind.OR_ASSIGNMENT, 15);
        OPERATOR_PRIORITIES.put(Kind.PLUS_ASSIGNMENT, 15);
        OPERATOR_PRIORITIES.put(Kind.REMAINDER_ASSIGNMENT, 15);
        OPERATOR_PRIORITIES.put(Kind.RIGHT_SHIFT_ASSIGNMENT, 15);
        OPERATOR_PRIORITIES.put(Kind.UNSIGNED_RIGHT_SHIFT_ASSIGNMENT, 15);
        OPERATOR_PRIORITIES.put(Kind.XOR_ASSIGNMENT, 15);
    }

    protected static boolean requiresParenthesis(Tree inner, Tree original, Tree outter) {
        if (!ExpressionTree.class.isAssignableFrom(inner.getKind().asInterface())) return false;
        if (!ExpressionTree.class.isAssignableFrom(outter.getKind().asInterface())) return false;

        if (outter.getKind() == Kind.PARENTHESIZED || inner.getKind() == Kind.PARENTHESIZED) return false;

        if (outter.getKind() == Kind.METHOD_INVOCATION) {
            if (((MethodInvocationTree) outter).getArguments().contains(original)) return false;
        }

        if (outter.getKind() == Kind.NEW_CLASS) {
            if (((NewClassTree) outter).getArguments().contains(original)) return false;
        }
        
        Integer innerPriority = OPERATOR_PRIORITIES.get(inner.getKind());
        Integer outterPriority = OPERATOR_PRIORITIES.get(outter.getKind());

        if (innerPriority == null || outterPriority == null) {
            Logger.getLogger(JavaFix.class.getName()).log(Level.WARNING, "Unknown tree kind(s): {0}/{1}", new Object[] {inner.getKind(), outter.getKind()});
            return true;
        }

        if (innerPriority > outterPriority) {
            return true;
        }

        if (innerPriority < outterPriority) {
            return false;
        }

        //associativity
        if (BinaryTree.class.isAssignableFrom(outter.getKind().asInterface())) {
            BinaryTree ot = (BinaryTree) outter;

            //TODO: for + it might be possible to skip the parenthesis:
            return ot.getRightOperand() == original;
        }

        if (CompoundAssignmentTree.class.isAssignableFrom(outter.getKind().asInterface())) {
            CompoundAssignmentTree ot = (CompoundAssignmentTree) outter;

            return ot.getVariable() == original;
        }

        if (AssignmentTree.class.isAssignableFrom(outter.getKind().asInterface())) {
            AssignmentTree ot = (AssignmentTree) outter;

            return ot.getVariable() == original;
        }

        return false;
    }

    static {
        JavaFixImpl.Accessor.INSTANCE = new JavaFixImpl.Accessor() {
            @Override
            public String getText(JavaFix jf) {
                return jf.getText();
            }
            @Override
            public ChangeInfo process(JavaFix jf, WorkingCopy wc, boolean canShowUI) throws Exception {
                return jf.process(wc, canShowUI);
            }
            @Override
            public FileObject getFile(JavaFix jf) {
                return jf.getFile();
            }
            @Override
            public Map<String, String> getOptions(JavaFix jf) {
                return jf.options;
            }
        };
    }

    @Messages("FIX_RemoveFromParent=Remove {0} from parent")
    public static Fix removeFromParent(HintContext ctx, TreePath what) {
        return removeFromParent(ctx, Bundle.FIX_RemoveFromParent(/*TODO: better short name:*/what.getLeaf().toString()), what);
    }

    public static Fix removeFromParent(HintContext ctx, String displayName, TreePath what) {
        return toEditorFix(new RemoveFromParent(displayName, ctx.getInfo(), what));
    }

    private static final class RemoveFromParent extends JavaFix {

        private final String displayName;

        public RemoveFromParent(String displayName, CompilationInfo info, TreePath toRemove) {
            super(info, toRemove);
            this.displayName = displayName;
        }

        @Override
        protected String getText() {
            return displayName;
        }

        @Override
        protected void performRewrite(WorkingCopy wc, TreePath tp, boolean canShowUI) {
            TreeMaker make = wc.getTreeMaker();
            Tree leaf = tp.getLeaf();
            Tree parentLeaf = tp.getParentPath().getLeaf();

            switch (parentLeaf.getKind()) {
                case ANNOTATION:
                    AnnotationTree at = (AnnotationTree) parentLeaf;
                    AnnotationTree newAnnot;

                    newAnnot = make.removeAnnotationAttrValue(at, (ExpressionTree) leaf);

                    wc.rewrite(at, newAnnot);
                    break;
                case BLOCK:
                    BlockTree bt = (BlockTree) parentLeaf;

                    wc.rewrite(bt, make.removeBlockStatement(bt, (StatementTree) leaf));
                    break;
                case CASE:
                    CaseTree caseTree = (CaseTree) parentLeaf;
                    
                    wc.rewrite(caseTree, make.removeCaseStatement(caseTree, (StatementTree) leaf));
                    break;
                case CLASS:
                    ClassTree classTree = (ClassTree) parentLeaf;
                    ClassTree nueClassTree;

                    if (classTree.getTypeParameters().contains(leaf)) {
                        nueClassTree = make.removeClassTypeParameter(classTree, (TypeParameterTree) leaf);
                    } else if (classTree.getExtendsClause() == leaf) {
                        nueClassTree = make.Class(classTree.getModifiers(), classTree.getSimpleName(), classTree.getTypeParameters(), null, classTree.getImplementsClause(), classTree.getMembers());
                    } else if (classTree.getImplementsClause().contains(leaf)) {
                        nueClassTree = make.removeClassImplementsClause(classTree, leaf);
                    } else if (classTree.getMembers().contains(leaf)) {
                        nueClassTree = make.removeClassMember(classTree, leaf);
                    } else {
                        throw new UnsupportedOperationException();
                    }

                    wc.rewrite(classTree, nueClassTree);
                    break;
                case UNION_TYPE:
                    UnionTypeTree disjunct = (UnionTypeTree) parentLeaf;
                    List<? extends Tree> alternatives = new LinkedList<Tree>(disjunct.getTypeAlternatives());

                    alternatives.remove(leaf);

                    wc.rewrite(disjunct, make.UnionType(alternatives));
                    break;
                case METHOD:
                    MethodTree mTree = (MethodTree) parentLeaf;
                    MethodTree newMethod;

                    if (mTree.getTypeParameters().contains(leaf)) {
                        newMethod = make.removeMethodTypeParameter(mTree, (TypeParameterTree) leaf);
                    } else if (mTree.getParameters().contains(leaf)) {
                        newMethod = make.removeMethodParameter(mTree, (VariableTree) leaf);
                    } else if (mTree.getThrows().contains(leaf)) {
                        newMethod = make.removeMethodThrows(mTree, (ExpressionTree) leaf);
                    } else {
                        throw new UnsupportedOperationException();
                    }

                    wc.rewrite(mTree, newMethod);
                    break;
                case METHOD_INVOCATION:
                    MethodInvocationTree iTree = (MethodInvocationTree) parentLeaf;
                    MethodInvocationTree newInvocation;

                    if (iTree.getTypeArguments().contains(leaf)) {
                        newInvocation = make.removeMethodInvocationTypeArgument(iTree, (ExpressionTree) leaf);
                    } else if (iTree.getArguments().contains(leaf)) {
                        newInvocation = make.removeMethodInvocationArgument(iTree, (ExpressionTree) leaf);
                    } else {
                        throw new UnsupportedOperationException();
                    }

                    wc.rewrite(iTree, newInvocation);
                    break;
                case MODIFIERS:
                    ModifiersTree modsTree = (ModifiersTree) parentLeaf;

                    wc.rewrite(modsTree, make.removeModifiersAnnotation(modsTree, (AnnotationTree) leaf));
                    break;
                case NEW_CLASS:
                    NewClassTree newCTree = (NewClassTree) parentLeaf;
                    NewClassTree newNCT;

                    if (newCTree.getTypeArguments().contains(leaf)) {
                        newNCT = make.removeNewClassTypeArgument(newCTree, (ExpressionTree) leaf);
                    } else if (newCTree.getArguments().contains(leaf)) {
                        newNCT = make.removeNewClassArgument(newCTree, (ExpressionTree) leaf);
                    } else {
                        throw new UnsupportedOperationException();
                    }

                    wc.rewrite(newCTree, newNCT);
                    break;
                case PARAMETERIZED_TYPE:
                    ParameterizedTypeTree parTree = (ParameterizedTypeTree) parentLeaf;

                    wc.rewrite(parTree, make.removeParameterizedTypeTypeArgument(parTree, (ExpressionTree) leaf));
                    break;
                case SWITCH:
                    SwitchTree switchTree = (SwitchTree) parentLeaf;
                    SwitchTree newSwitch;

                    if (switchTree.getCases().contains(leaf)) {
                        newSwitch = make.removeSwitchCase(switchTree, (CaseTree) leaf);
                    } else {
                        throw new UnsupportedOperationException();
                    }

                    wc.rewrite(switchTree, newSwitch);
                    break;
                case TRY:
                    TryTree tryTree = (TryTree) parentLeaf;
                    TryTree newTry;

                    if (tryTree.getResources().contains(leaf)) {
                        LinkedList<Tree> resources = new LinkedList<Tree>(tryTree.getResources());

                        resources.remove(leaf);

                        newTry = make.Try(resources, tryTree.getBlock(), tryTree.getCatches(), tryTree.getFinallyBlock());
                    } else if (tryTree.getCatches().contains(leaf)) {
                        newTry = make.removeTryCatch(tryTree, (CatchTree) leaf);
                    } else {
                        throw new UnsupportedOperationException();
                    }

                    wc.rewrite(tryTree, newTry);
                    break;
            }
        }

    }
}
