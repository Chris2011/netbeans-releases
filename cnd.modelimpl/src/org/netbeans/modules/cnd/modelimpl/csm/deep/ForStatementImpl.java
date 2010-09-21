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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.cnd.modelimpl.csm.deep;

import java.util.*;

import org.netbeans.modules.cnd.api.model.*;
import org.netbeans.modules.cnd.api.model.deep.*;

import org.netbeans.modules.cnd.modelimpl.csm.core.*;

import org.netbeans.modules.cnd.antlr.collections.AST;
import org.netbeans.modules.cnd.modelimpl.parser.generated.CPPTokenTypes;

/**
 * Implements CsmForStatement statements
 * @author Vladimir Kvashin
 */
public final class ForStatementImpl extends StatementBase implements CsmForStatement {
    
    private StatementBase init;
    private CsmCondition condition;
    private ExpressionBase iteration;
    private StatementBase body;
    private boolean rendered = false;
    
    private ForStatementImpl(AST ast, CsmFile file, CsmScope scope) {
        super(ast, file, scope);
    }

    public static ForStatementImpl create(AST ast, CsmFile file, CsmScope scope) {
        return new ForStatementImpl(ast, file, scope);
    }
    
    @Override
    public CsmStatement.Kind getKind() {
        return CsmStatement.Kind.FOR;
    }

    @Override
    public boolean isPostCheck() {
        return true;
    }

    @Override
    public CsmExpression getIterationExpression() {
        renderIfNeed();
        return iteration;
    }

    @Override
    public CsmStatement getInitStatement() {
        renderIfNeed();
        return init;
    }

    @Override
    public CsmCondition getCondition() {
        renderIfNeed();
        return condition;
    }

    @Override
    public CsmStatement getBody() {
        renderIfNeed();
        return body;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (condition instanceof Disposable) {
            ((Disposable) condition).dispose();
        }
        if (init instanceof Disposable) {
            ((Disposable) init).dispose();
        }
        if (iteration instanceof Disposable) {
            ((Disposable) iteration).dispose();
        }
        if (body instanceof Disposable) {
            ((Disposable) body).dispose();
        }        
    }

    private void renderIfNeed() {
        if( ! rendered ) {
            rendered = true;
            render();
        }
    }
    
    private void render() {
        
        AstRenderer renderer = new AstRenderer((FileImpl) getContainingFile());
        
        for( AST token = getAst().getFirstChild(); token != null; token = token.getNextSibling() ) {
            switch( token.getType() ) {
            case CPPTokenTypes.CSM_FOR_INIT_STATEMENT:
                AST child = AstRenderer.getFirstChildSkipQualifiers(token);
                if( child != null ) {
                    switch( child.getType() ) {
                        case CPPTokenTypes.SEMICOLON:
                            init = null;
                            break;
                        case CPPTokenTypes.CSM_TYPE_BUILTIN:
                        case CPPTokenTypes.CSM_TYPE_COMPOUND:
                        case CPPTokenTypes.LITERAL_struct:
                        case CPPTokenTypes.LITERAL_class:
                        case CPPTokenTypes.LITERAL_union:
                            //renderer.renderVariable(token, null, null);
                            init = DeclarationStatementImpl.create(token, getContainingFile(), ForStatementImpl.this);
                            break;
                        default:
                            if( AstRenderer.isExpression(child) ) {
                                init = ExpressionStatementImpl.create(token, getContainingFile(), ForStatementImpl.this);
                            }
                            break;
                    }
                }
                break;
            case CPPTokenTypes.CSM_CONDITION:
                condition = renderer.renderCondition(token, this);
                break;
            default:
                if( AstRenderer.isStatement(token) ) {
                    body = AstRenderer.renderStatement(token, getContainingFile(), this);
                }
                else if( AstRenderer.isExpression(token) ) {
                    iteration = renderer.renderExpression(token, ForStatementImpl.this);
                }
            }
        }
    }

    @Override
    public Collection<CsmScopeElement> getScopeElements() {
        //return DeepUtil.merge(getInitStatement(), getCondition(), getBody());
        List<CsmScopeElement> l = new ArrayList<CsmScopeElement>();
        CsmStatement stmt = getInitStatement();
        if( stmt != null ) {
            l.add(stmt);
        }
        CsmCondition cond = getCondition();
        if( cond != null ) {
            CsmVariable decl = cond.getDeclaration();
            if( decl != null ) {
                l.add(decl);
            }
        }
        stmt = getBody();
        if( stmt != null ) {
            l.add(stmt);
        }
        return l;
    }
    
    
}
