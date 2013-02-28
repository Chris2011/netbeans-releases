/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.css.prep;

import org.netbeans.modules.css.prep.model.CPModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.csl.api.ColoringAttributes;
import org.netbeans.modules.csl.api.CompletionProposal;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.css.editor.module.spi.CompletionContext;
import org.netbeans.modules.css.editor.module.spi.CssEditorModule;
import org.netbeans.modules.css.editor.module.spi.EditorFeatureContext;
import org.netbeans.modules.css.editor.module.spi.FeatureContext;
import org.netbeans.modules.css.editor.module.spi.SemanticAnalyzer;
import org.netbeans.modules.css.editor.module.spi.Utilities;
import org.netbeans.modules.css.lib.api.CssTokenId;
import org.netbeans.modules.css.lib.api.Node;
import org.netbeans.modules.css.lib.api.NodeType;
import org.netbeans.modules.css.lib.api.NodeUtil;
import org.netbeans.modules.css.lib.api.NodeVisitor;
import org.netbeans.modules.css.prep.model.Variable;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.web.common.api.LexerUtils;
import org.openide.util.lookup.ServiceProvider;

/**
 * Less Css Editor Module implementation.
 *
 * TODO fix the instant rename and the mark occurrences - they are pretty naive
 * - not scoped at all :-)
 *
 * @author marekfukala
 */
@ServiceProvider(service = CssEditorModule.class)
public class CPCssEditorModule extends CssEditorModule {

    private final SemanticAnalyzer semanticAnalyzer = new CPSemanticAnalyzer();
    private static Map<NodeType, ColoringAttributes> COLORINGS;

    @Override
    public SemanticAnalyzer getSemanticAnalyzer() {
        return semanticAnalyzer;
    }

    @Override
    public List<CompletionProposal> getCompletionProposals(final CompletionContext context) {
        final List<CompletionProposal> proposals = new ArrayList<CompletionProposal>();
        
        CPModel model = CPModel.getModel(context.getParserResult());
        List<CompletionProposal> allVars = getVariableCompletionProposals(context, model);
        
        //errorneous source
        Node tokenNode = context.getActiveTokenNode();
        if(NodeUtil.getTokenNodeTokenId(tokenNode) == CssTokenId.ERROR) {
            if(LexerUtils.equals(tokenNode.image(), "$", false, true)) {
                //"$" as a prefix - user likely wants to type variable
                //check context
                if(NodeUtil.getAncestorByType(tokenNode, NodeType.rule) != null) {
                    //in declarations node -> offer all vars
                    proposals.addAll(allVars);
                    return Utilities.filterCompletionProposals(proposals, context.getPrefix(), true);
                }
            }
        }
        
        
        Node activeNode = context.getActiveNode();
        boolean isError = activeNode.type() == NodeType.error;
        if (isError) {
            activeNode = activeNode.parent();
        }

//        NodeUtil.dumpTree(context.getParseTreeRoot());
        
        
        
        switch (activeNode.type()) {
            case cp_variable:
                //already in the prefix
                proposals.addAll(allVars);
                break;
            case propertyValue:
                //just $ or @ prefix
                if(context.getPrefix().length() == 1 && context.getPrefix().charAt(0) == model.getPreprocessorType().getVarPrefix()) {
                    proposals.addAll(allVars);
                }

        }
        return Utilities.filterCompletionProposals(proposals, context.getPrefix(), true);
    }

    private static List<CompletionProposal> getVariableCompletionProposals(final CompletionContext context, CPModel model) {
        //filter the variable at the current location (being typed)
        Collection<String> filtered = new HashSet<String>();
        for(Variable var : model.getVariables()) {
            if(!var.getRange().containsInclusive(context.getCaretOffset())) {
                filtered.add(var.getName());
            }
        }
        return Utilities.createRAWCompletionProposals(filtered, ElementKind.VARIABLE, context.getAnchorOffset());
    }

    @Override
    public <T extends Map<OffsetRange, Set<ColoringAttributes>>> NodeVisitor<T> getSemanticHighlightingNodeVisitor(FeatureContext context, T result) {
        final Snapshot snapshot = context.getSnapshot();
        return new NodeVisitor<T>(result) {
            @Override
            public boolean visit(Node node) {
                ColoringAttributes coloring = getColorings().get(node.type());
                if (coloring != null) {
                    int dso = snapshot.getOriginalOffset(node.from());
                    int deo = snapshot.getOriginalOffset(node.to());
                    if (dso >= 0 && deo >= 0) { //filter virtual nodes
                        //check vendor speficic property
                        OffsetRange range = new OffsetRange(dso, deo);
                        getResult().put(range, Collections.singleton(coloring));
                    }
                }
                return false;
            }
        };
    }

    private static Map<NodeType, ColoringAttributes> getColorings() {
        if (COLORINGS == null) {
            COLORINGS = new EnumMap<NodeType, ColoringAttributes>(NodeType.class);
            COLORINGS.put(NodeType.cp_variable, ColoringAttributes.LOCAL_VARIABLE);
            COLORINGS.put(NodeType.cp_mixin_name, ColoringAttributes.PRIVATE);
        }
        return COLORINGS;
    }

    @Override
    public <T extends Set<OffsetRange>> NodeVisitor<T> getMarkOccurrencesNodeVisitor(EditorFeatureContext context, T result) {
        return Utilities.createMarkOccurrencesNodeVisitor(context, result, NodeType.cp_variable, NodeType.cp_mixin_name);
    }

    @Override
    public boolean isInstantRenameAllowed(EditorFeatureContext context) {
        TokenSequence<CssTokenId> tokenSequence = context.getTokenSequence();
        int diff = tokenSequence.move(context.getCaretOffset());
        if (diff > 0 && tokenSequence.moveNext() || diff == 0 && tokenSequence.movePrevious()) {
            Token<CssTokenId> token = tokenSequence.token();
            return token.id() == CssTokenId.AT_IDENT //less 
                    || token.id() == CssTokenId.SASS_VAR //sass
                    || token.id() == CssTokenId.IDENT; //sass/less mixin name

        }
        return false;
    }

    @Override
    public <T extends Set<OffsetRange>> NodeVisitor<T> getInstantRenamerVisitor(EditorFeatureContext context, T result) {
        TokenSequence<CssTokenId> tokenSequence = context.getTokenSequence();
        int diff = tokenSequence.move(context.getCaretOffset());
        if (diff > 0 && tokenSequence.moveNext() || diff == 0 && tokenSequence.movePrevious()) {
            Token<CssTokenId> token = tokenSequence.token();
            final CharSequence elementName = token.text();
            return new NodeVisitor<T>(result) {
                @Override
                public boolean visit(Node node) {
                    switch (node.type()) {
                        case cp_mixin_name:
                        case cp_variable:
                            if (LexerUtils.equals(elementName, node.image(), false, false)) {
                                OffsetRange range = new OffsetRange(node.from(), node.to());
                                getResult().add(range);
                                break;
                            }
                    }
                    return false;
                }
            };

        }
        return null;
    }

   
}
