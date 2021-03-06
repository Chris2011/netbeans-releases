/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2015 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2015 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.editor.verification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.netbeans.modules.csl.spi.support.CancelSupport;
import org.netbeans.modules.php.api.PhpVersion;
import org.netbeans.modules.php.editor.CodeUtils;
import org.netbeans.modules.php.editor.model.impl.Type;
import org.netbeans.modules.php.editor.parser.PHPParseResult;
import org.netbeans.modules.php.editor.parser.astnodes.ASTNode;
import org.netbeans.modules.php.editor.parser.astnodes.AnonymousObjectVariable;
import org.netbeans.modules.php.editor.parser.astnodes.ArrayCreation;
import org.netbeans.modules.php.editor.parser.astnodes.ArrayDimension;
import org.netbeans.modules.php.editor.parser.astnodes.Assignment;
import org.netbeans.modules.php.editor.parser.astnodes.ClassInstanceCreation;
import org.netbeans.modules.php.editor.parser.astnodes.CloneExpression;
import org.netbeans.modules.php.editor.parser.astnodes.ConditionalExpression;
import org.netbeans.modules.php.editor.parser.astnodes.ConstantDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.DereferencableVariable;
import org.netbeans.modules.php.editor.parser.astnodes.DereferencedArrayAccess;
import org.netbeans.modules.php.editor.parser.astnodes.Dispatch;
import org.netbeans.modules.php.editor.parser.astnodes.Expression;
import org.netbeans.modules.php.editor.parser.astnodes.ExpressionArrayAccess;
import org.netbeans.modules.php.editor.parser.astnodes.FormalParameter;
import org.netbeans.modules.php.editor.parser.astnodes.FunctionDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.FunctionInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.FunctionName;
import org.netbeans.modules.php.editor.parser.astnodes.GroupUseStatementPart;
import org.netbeans.modules.php.editor.parser.astnodes.Identifier;
import org.netbeans.modules.php.editor.parser.astnodes.InfixExpression;
import org.netbeans.modules.php.editor.parser.astnodes.LambdaFunctionDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.MethodDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.MethodInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.NamespaceName;
import org.netbeans.modules.php.editor.parser.astnodes.Scalar;
import org.netbeans.modules.php.editor.parser.astnodes.StaticConstantAccess;
import org.netbeans.modules.php.editor.parser.astnodes.StaticDispatch;
import org.netbeans.modules.php.editor.parser.astnodes.StaticFieldAccess;
import org.netbeans.modules.php.editor.parser.astnodes.StaticMethodInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.Variable;
import org.netbeans.modules.php.editor.parser.astnodes.VariableBase;
import org.netbeans.modules.php.editor.parser.astnodes.YieldExpression;
import org.netbeans.modules.php.editor.parser.astnodes.YieldFromExpression;
import org.netbeans.modules.php.editor.parser.astnodes.visitors.DefaultVisitor;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

public class PHP70UnhandledError extends UnhandledErrorRule {

    @NbBundle.Messages("PHP70UnhandledError.displayName=Language feature not compatible with PHP version indicated in project settings")
    @Override
    public String getDisplayName() {
        return Bundle.PHP70UnhandledError_displayName();
    }

    @Override
    public void invoke(PHPRuleContext context, List<org.netbeans.modules.csl.api.Error> errors) {
        PHPParseResult phpParseResult = (PHPParseResult) context.parserResult;
        if (phpParseResult.getProgram() == null) {
            return;
        }
        FileObject fileObject = phpParseResult.getSnapshot().getSource().getFileObject();
        if (fileObject != null
                && appliesTo(fileObject)) {
            if (CancelSupport.getDefault().isCancelled()) {
                return;
            }
            CheckVisitor checkVisitor = new CheckVisitor(fileObject);
            phpParseResult.getProgram().accept(checkVisitor);
            if (CancelSupport.getDefault().isCancelled()) {
                return;
            }
            errors.addAll(checkVisitor.getErrors());
        }
    }

    private static boolean appliesTo(FileObject fileObject) {
        return CodeUtils.isPhpVersionLessThan(fileObject, PhpVersion.PHP_70);
    }

    //~ Inner classes

    private static final class CheckVisitor extends DefaultVisitor {

        private static final Set<String> TYPES_FOR_SOURCES;

        private final List<VerificationError> errors = new ArrayList<>();
        private final FileObject fileObject;


        static {
            TYPES_FOR_SOURCES = new HashSet<>(Type.getTypesForEditor());
            TYPES_FOR_SOURCES.remove(Type.ARRAY);
            TYPES_FOR_SOURCES.remove(Type.CALLABLE);
            TYPES_FOR_SOURCES.remove(Type.ITERABLE);
        }


        public CheckVisitor(FileObject fileObject) {
            this.fileObject = fileObject;
        }

        public Collection<VerificationError> getErrors() {
            return Collections.unmodifiableCollection(errors);
        }

        @Override
        public void visit(InfixExpression node) {
            if (CancelSupport.getDefault().isCancelled()) {
                return;
            }
            if (InfixExpression.OperatorType.SPACESHIP.equals(node.getOperator())) {
                createError(node);
            }
            super.visit(node);
        }

        @Override
        public void visit(ConditionalExpression node) {
            if (CancelSupport.getDefault().isCancelled()) {
                return;
            }
            if (ConditionalExpression.OperatorType.COALESCE.equals(node.getOperator())) {
                createError(node);
            }
            super.visit(node);
        }

        @Override
        public void visit(FunctionDeclaration node) {
            if (CancelSupport.getDefault().isCancelled()) {
                return;
            }
            checkScalarTypes(node.getFormalParameters());
            checkReturnType(node.getReturnType());
            super.visit(node);
        }

        @Override
        public void visit(MethodDeclaration node) {
            if (CancelSupport.getDefault().isCancelled()) {
                return;
            }
            checkScalarTypes(node.getFunction().getFormalParameters());
            checkReturnType(node.getFunction().getReturnType());
            checkMethodName(node.getFunction().getFunctionName());
            super.visit(node);
        }

        @Override
        public void visit(LambdaFunctionDeclaration node) {
            if (CancelSupport.getDefault().isCancelled()) {
                return;
            }
            checkScalarTypes(node.getFormalParameters());
            checkReturnType(node.getReturnType());
            super.visit(node);
        }

        @Override
        public void visit(GroupUseStatementPart node) {
            if (CancelSupport.getDefault().isCancelled()) {
                return;
            }
            createError(node);
            super.visit(node);
        }

        @Override
        public void visit(YieldFromExpression node) {
            if (CancelSupport.getDefault().isCancelled()) {
                return;
            }
            createError(node);
            super.visit(node);
        }

        @Override
        public void visit(Assignment node) {
            if (CancelSupport.getDefault().isCancelled()) {
                return;
            }
            checkYieldInAssignment(node);
            super.visit(node);
        }

        @Override
        public void visit(ClassInstanceCreation node) {
            if (CancelSupport.getDefault().isCancelled()) {
                return;
            }
            if (node.isAnonymous()) {
                createError(node);
            }
            super.visit(node);
        }

        @Override
        public void visit(StaticConstantAccess node) {
            if (CancelSupport.getDefault().isCancelled()) {
                return;
            }
            checkDispatcher(node);
            super.visit(node);
        }

        @Override
        public void visit(StaticFieldAccess node) {
            if (CancelSupport.getDefault().isCancelled()) {
                return;
            }
            checkDispatcher(node);
            super.visit(node);
        }

        @Override
        public void visit(MethodInvocation node) {
            if (CancelSupport.getDefault().isCancelled()) {
                return;
            }
            checkDispatcher(node);
            super.visit(node);
        }

        @Override
        public void visit(StaticMethodInvocation node) {
            if (CancelSupport.getDefault().isCancelled()) {
                return;
            }
            checkDispatcher(node);
            super.visit(node);
        }

        @Override
        public void visit(FunctionInvocation node) {
            if (CancelSupport.getDefault().isCancelled()) {
                return;
            }
            checkIssetFunction(node);
            checkFunctionName(node);
            super.visit(node);
        }

        @Override
        public void visit(ConstantDeclaration node) {
            if (CancelSupport.getDefault().isCancelled()) {
                return;
            }
            checkConstName(node);
            super.visit(node);
        }

        @Override
        public void visit(AnonymousObjectVariable node) {
            if (CancelSupport.getDefault().isCancelled()) {
                return;
            }
            // (clone $this->getSomething())
            checkCloneExpression(node.getName());
            super.visit(node);
        }

        @Override
        public void visit(ExpressionArrayAccess node) {
            if (CancelSupport.getDefault().isCancelled()) {
                return;
            }
            // e.g. [1, 2, 3]{0}
            checkArrayDimension(node.getDimension());
            super.visit(node);
        }

        @Override
        public void visit(DereferencedArrayAccess node) {
            if (CancelSupport.getDefault().isCancelled()) {
                return;
            }
            // e.g. test(){0};
            checkArrayDimension(node.getDimension());
            super.visit(node);
        }

        private void checkScalarTypes(List<FormalParameter> formalParameters) {
            for (FormalParameter formalParameter : formalParameters) {
                // nullable types are checked in PHP71UnhandledError, so just ignore "?"
                String typeName = CodeUtils.extractUnqualifiedTypeName(formalParameter);
                if (typeName != null
                        && TYPES_FOR_SOURCES.contains(typeName)) {
                    createError(formalParameter);
                }
            }
        }

        private void checkReturnType(Expression returnType) {
            if (returnType != null) {
                createError(returnType);
            }
        }

        private void checkDispatcher(Dispatch node) {
            // check dereferencable variable
            // e.g. ($foo->test())->test2();
            // [$object1, $object2][0]->test();
            VariableBase dispatcher = node.getDispatcher();
            if (dispatcher instanceof DereferencableVariable
                    || dispatcher instanceof ExpressionArrayAccess) {
                createError(dispatcher);
            }
        }

        private void checkDispatcher(StaticDispatch node) {
            Expression dispatcher = node.getDispatcher();
            if (dispatcher instanceof NamespaceName
                    || dispatcher instanceof Identifier
                    || dispatcher instanceof Variable) {
                // pre php7 access => ok
                return;
            }
            createError(dispatcher);
        }

        private void checkIssetFunction(FunctionInvocation node) {
            String functionName = CodeUtils.extractFunctionName(node);
            if ("isset".equals(functionName)) { // NOI18N
                List<Expression> parameters = node.getParameters();
                for (Expression parameter : parameters) {
                    Expression expression = parameter;
                    if (expression instanceof StaticConstantAccess) {
                        StaticConstantAccess sca = (StaticConstantAccess) expression;
                        expression = sca.getConstant();
                    }
                    if (expression instanceof ExpressionArrayAccess) {
                        createError(parameter);
                    }
                }
            }
        }

        private void checkFunctionName(FunctionInvocation node) {
            FunctionName functionName = node.getFunctionName();
            Expression name = functionName.getName();
            if (name instanceof Scalar // "strlen"("something");
                    || name instanceof ArrayCreation // ["Foo", "bar"]();
                    || name instanceof DereferencableVariable // ($foo)();
                    || name instanceof AnonymousObjectVariable) { // (new Object)();
                createError(name);
            }
        }

        private void checkYieldInAssignment(Assignment node) {
            Assignment.Type operator = node.getOperator();
            if (operator == Assignment.Type.EQUAL) {
                Expression expression = node.getRightHandSide();
                if (expression instanceof YieldExpression) {
                    createError(expression);
                }
            }
        }

        private void checkConstName(ConstantDeclaration node) {
            List<Identifier> names = node.getNames();
            names.stream().filter(name -> name.isKeyword()).forEach(name -> {
                createError(name);
            });
        }

        private void checkMethodName(Identifier node) {
            if (node != null && node.isKeyword()) {
                createError(node);
            }
        }

        private void checkCloneExpression(Expression node) {
            if (node instanceof CloneExpression) {
                createError(node);
            }
        }

        private void checkArrayDimension(ArrayDimension node) {
            if (node.getType() == ArrayDimension.Type.VARIABLE_HASHTABLE) {
                createError(node);
            }
        }

        private void createError(int startOffset, int endOffset) {
            errors.add(new PHP70VersionError(fileObject, startOffset, endOffset));
        }

        private void createError(ASTNode node) {
            createError(node.getStartOffset(), node.getEndOffset());
        }

    }

    private static final class PHP70VersionError extends VerificationError {

        private static final String KEY = "Php.Version.70"; // NOI18N


        private PHP70VersionError(FileObject fileObject, int startOffset, int endOffset) {
            super(fileObject, startOffset, endOffset);
        }

        @NbBundle.Messages("PHP70VersionError.displayName=Language feature not compatible with PHP version indicated in project settings")
        @Override
        public String getDisplayName() {
            return Bundle.PHP70VersionError_displayName();
        }

        @NbBundle.Messages("PHP70VersionError.description=Detected language features not compatible with PHP version indicated in project settings")
        @Override
        public String getDescription() {
            return Bundle.PHP70VersionError_description();
        }

        @Override
        public String getKey() {
            return KEY;
        }

    }

}
