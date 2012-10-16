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
package org.netbeans.modules.css.editor.csl;

import org.netbeans.modules.css.lib.api.properties.GrammarElement;
import org.netbeans.modules.css.lib.api.properties.Properties;
import org.netbeans.modules.css.lib.api.properties.PropertyDefinition;
import java.awt.Color;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import javax.swing.ImageIcon;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.csl.api.ElementHandle.UrlHandle;
import org.netbeans.modules.csl.api.*;
import org.netbeans.modules.csl.spi.DefaultCompletionResult;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.css.editor.CssProjectSupport;
import org.netbeans.modules.css.editor.HtmlTags;
import org.netbeans.modules.css.editor.URLRetriever;
import org.netbeans.modules.css.editor.api.CssCslParserResult;
import org.netbeans.modules.css.editor.module.CssModuleSupport;
import org.netbeans.modules.css.editor.module.spi.*;
import org.netbeans.modules.css.lib.api.properties.PropertyModel;
import org.netbeans.modules.css.lib.api.properties.ResolvedProperty;
import org.netbeans.modules.css.indexing.api.CssIndex;
import org.netbeans.modules.css.lib.api.*;
import org.netbeans.modules.css.lib.api.properties.UnitGrammarElement;
import org.netbeans.modules.css.lib.api.properties.ValueGrammarElement;
import org.netbeans.modules.css.refactoring.api.RefactoringElementType;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.web.common.api.DependenciesGraph;
import org.netbeans.modules.web.common.api.FileReferenceCompletion;
import org.netbeans.modules.web.common.api.LexerUtils;
import org.openide.filesystems.FileObject;

/**
 *
 * @author mfukala@netbeans.org
 */
public class CssCompletion implements CodeCompletionHandler {

    private static final Collection<String> AT_RULES = Arrays.asList(new String[]{"@media", "@page", "@import", "@charset", "@font-face"}); //NOI18N
    private static char firstPrefixChar; //read getPrefix() comment!
    private static final String EMPTY_STRING = ""; //NOI18N
    private static final String UNIVERSAL_SELECTOR = "*"; //NOI18N
    //unit testing support
    static String[] TEST_USED_COLORS;

    @Override
    public CodeCompletionResult complete(CodeCompletionContext context) {

        final List<CompletionProposal> completionProposals = new ArrayList<CompletionProposal>();

        CssCslParserResult info = (CssCslParserResult) context.getParserResult();
        Snapshot snapshot = info.getSnapshot();
        FileObject file = snapshot.getSource().getFileObject();

        int caretOffset = context.getCaretOffset();
        String prefix = context.getPrefix() != null ? context.getPrefix() : "";

        //read getPrefix() comment!
        if (firstPrefixChar != 0) {
            prefix = firstPrefixChar + prefix;
        }

        TokenHierarchy<?> th = snapshot.getTokenHierarchy();
        TokenSequence<CssTokenId> ts = th.tokenSequence(CssTokenId.language());

        assert ts != null;

        //handle lexical completion only
        CodeCompletionResult lexicalCompletionResult = handleLexicalBasedCompletion(file, ts, snapshot, caretOffset);
        if (lexicalCompletionResult != null) {
            return lexicalCompletionResult;
        }

        //continue with AST completion
        int offset = caretOffset - prefix.length();
        int astOffset = snapshot.getEmbeddedOffset(offset);
        int astCaretOffset = snapshot.getEmbeddedOffset(caretOffset);

        boolean unmappableClassOrId = false;
        if (astOffset == -1) {
            if ((prefix.length() == 1 && prefix.charAt(0) == '.') || (prefix.length() > 0 && prefix.charAt(0) == '#')) {
                //this happens if completion is invoked in empty css embedding,
                //for example in <div class="|"/>. The virtual source contains doesn't
                //map the position do the document, se we need to hack it
                unmappableClassOrId = true;
            } else {
                //cannot map the offset
                return null;
            }
        }

        int diff = ts.move(astCaretOffset);
        boolean tokenFound = diff == 0 ? ts.movePrevious() : ts.moveNext();

        Node root = info.getParseTree();
        if (root == null) {
            //broken source
            return CodeCompletionResult.NONE;
        }

        char charAfterCaret = snapshot.getText().length() > (astCaretOffset + 1)
                ? snapshot.getText().subSequence(astCaretOffset, astCaretOffset + 1).charAt(0)
                : ' '; //NOI18N

        //if the caret points to a token node then determine its type
        Node tokenNode = NodeUtil.findNodeAtOffset(root, astCaretOffset);
        CssTokenId tokenNodeTokenId = tokenNode.type() == NodeType.token ? NodeUtil.getTokenNodeTokenId(tokenNode) : null;

        Node node = NodeUtil.findNonTokenNodeAtOffset(root, astCaretOffset);
        if(node.type() == NodeType.ws) {
            node = node.parent();
        }
//        if (node.type() == NodeType.error) {
//            node = node.parent();
//            if (node == null) {
//                return CodeCompletionResult.NONE;
//            }
//        }

        //xxx: handleLexicalBasedCompletion breaks the contract - in the case it is used the css modules are
        //     not asked for the completion results. The main reason is that the CompletionProposal doesn't 
        //     allow to move the caret somewhere when the completion item is completed. This functionality
        //     is achievable onto the CompletionResult. However this means it is not completion item specific
        //     and as such cannot vary from different items from various css modules.

        //css modules
        CompletionContext completionContext =
                new CompletionContext(node,
                tokenNode,
                info,
                ts,
                diff,
                context.getQueryType(),
                caretOffset,
                offset,
                astCaretOffset,
                astOffset,
                prefix);

        List<CompletionProposal> cssModulesCompletionProposals = CssModuleSupport.getCompletionProposals(completionContext);
        completionProposals.addAll(cssModulesCompletionProposals);

        //non-modules based (legacy) completion - to be refactored to modules later
        completeClassSelectors(completionContext, completionProposals, unmappableClassOrId);
        completeIdSelectors(completionContext, completionProposals, unmappableClassOrId);
        completeAtRulesAndHtmlSelectors(completionContext, completionProposals);
        completeHtmlSelectors(completionContext, completionProposals, tokenNodeTokenId);
        completeKeywords(completionContext, completionProposals, tokenFound);
        completePropertyName(completionContext, completionProposals);
        completePropertyValue(completionContext, completionProposals, charAfterCaret);

        return new DefaultCompletionResult(completionProposals, false);
    }

    private List<? extends CompletionProposal> completeImport(FileObject base, int offset, String prefix, boolean addQuotes, boolean addSemicolon) {
        FileReferenceCompletion<CssCompletionItem> fileCompletion = new CssLinkCompletion(base, addQuotes, addSemicolon);
        return fileCompletion.getItems(base, offset - prefix.length(), prefix);
    }

    private List<CompletionProposal> completeHtmlSelectors(CompletionContext context, String prefix, int offset) {
        List<CompletionProposal> proposals = new ArrayList<CompletionProposal>(20);
        Collection<String> items = new ArrayList(Arrays.asList(HtmlTags.getTags()));
        items.add(UNIVERSAL_SELECTOR);
        for (String tagName : items) {
            if (tagName.startsWith(prefix.toLowerCase(Locale.ENGLISH))) {
                proposals.add(CssCompletionItem.createSelectorCompletionItem(new CssElement(context.getSource().getFileObject(), tagName),
                        tagName,
                        offset,
                        true));
            }
        }
        return proposals;
    }

    private Collection<CompletionProposal> wrapPropertyValues(CompletionContext context,
            String prefix,
            PropertyDefinition propertyDescriptor,
            Collection<ValueGrammarElement> props,
            int anchor,
            boolean addSemicolon,
            boolean addSpaceBeforeItem,
            boolean extendedItemsOnly) {

        //there might be more grammar elements from multiple branches with the same name
        Map<String, Collection<ValueGrammarElement>> value2GrammarElement =
                new HashMap<String, Collection<ValueGrammarElement>>();
        for (ValueGrammarElement element : props) {
            String elementValue = element.getValue().toString();
            Collection<ValueGrammarElement> col = value2GrammarElement.get(elementValue);
            if (col == null) {
                col = new LinkedList<ValueGrammarElement>();
                value2GrammarElement.put(elementValue, col);
            }
            col.add(element);
        }


        List<CompletionProposal> proposals = new ArrayList<CompletionProposal>(props.size());
        boolean colorChooserAdded = false;

        for (Map.Entry<String, Collection<ValueGrammarElement>> entry : value2GrammarElement.entrySet()) {

            String elementValue = entry.getKey();
            Collection<ValueGrammarElement> elements = entry.getValue();
            ValueGrammarElement element = elements.iterator().next();

            if(element instanceof UnitGrammarElement) {
                proposals.add(CssCompletionItem.createUnitCompletionItem((UnitGrammarElement)element));
                continue;
            }

            CssValueElement handle = new CssValueElement(propertyDescriptor, element);

            String origin = element.origin();
            String visibleOrigin = element.getVisibleOrigin();
            
            //some hardcoded items filtering
            
            //1. do not show NAMED operators in the code completion
            if(origin.endsWith("_operator")) { //NOI18N
                continue;
            }
            if ("@colors-list".equals(origin)) { //NOI18N
                if (!colorChooserAdded) {
                    //add color chooser item
                    proposals.add(CssCompletionItem.createColorChooserCompletionItem(anchor, visibleOrigin, addSemicolon));
                    //add used colors items
                    proposals.addAll(getUsedColorsItems(context, prefix, handle, visibleOrigin, anchor, addSemicolon, addSpaceBeforeItem));
                    colorChooserAdded = true;
                }
                if (!extendedItemsOnly) {
                    proposals.add(CssCompletionItem.createColorValueCompletionItem(handle, element, anchor, addSemicolon, addSpaceBeforeItem));
                }
            } else {
                if (!extendedItemsOnly) {
                    //for elements which are alternatives from multiple grammar braches do not
                    //show the origin

                    //check if the visible origin off all the elements is the same, 
                    //if so use it if not, do not show any origin
                    String vo = null;
                    boolean same = true;
                    for (ValueGrammarElement e : elements) {
                        if (vo == null) {
                            vo = e.getVisibleOrigin();
                        } else {
                            if (!vo.equals(e.getVisibleOrigin())) {
                                same = false;
                                break;
                            }
                        }
                    }

                    proposals.add(
                            CssCompletionItem.createValueCompletionItem(
                            handle,
                            element,
                            same ? visibleOrigin : "...",
                            anchor,
                            addSemicolon,
                            addSpaceBeforeItem));
                }
            }
        }
        return proposals;
    }

    private Collection<CompletionProposal> getUsedColorsItems(CompletionContext context, String prefix,
            CssElement element, String origin, int anchor, boolean addSemicolon,
            boolean addSpaceBeforeItem) {
        Collection<CompletionProposal> proposals = new HashSet<CompletionProposal>();
        //unit testing - inject some testing used colors to the completion
        if (TEST_USED_COLORS != null) {
            for (String color : TEST_USED_COLORS) {
                proposals.add(CssCompletionItem.createHashColorCompletionItem(element, color, origin,
                        anchor, addSemicolon, addSpaceBeforeItem, true));
            }
        }
        //

        FileObject current = context.getParserResult().getSnapshot().getSource().getFileObject();
        if (current == null) {
            return proposals;
        }
        CssProjectSupport support = CssProjectSupport.findFor(current);
        if (support == null) {
            //we are outside of a project
            return proposals;
        }
        CssIndex index = support.getIndex();
        Map<FileObject, Collection<String>> result = index.findAll(RefactoringElementType.COLOR);

        //resort the files collection so the current file it first
        //we need that to ensure the color from current file has precedence
        //over the others
        List<FileObject> resortedKeys = new ArrayList<FileObject>(result.keySet());
        if (resortedKeys.remove(current)) {
            resortedKeys.add(0, current);
        }
        for (FileObject file : resortedKeys) {
            Collection<String> colors = result.get(file);
            boolean usedInCurrentFile = file.equals(current);
            for (String color : colors) {
                if (color.startsWith(prefix)) {
                    proposals.add(CssCompletionItem.createHashColorCompletionItem(element, color, origin,
                            anchor, addSemicolon, addSpaceBeforeItem, usedInCurrentFile));
                }
            }
        }


        return proposals;
    }

    private Collection<String> filterStrings(Collection<String> values, String propertyNamePrefix) {
        propertyNamePrefix = propertyNamePrefix.toLowerCase();
        List<String> filtered = new ArrayList<String>();
        for (String value : values) {
            if (value.toLowerCase().startsWith(propertyNamePrefix)) {
                filtered.add(value);
            }
        }
        return filtered;
    }

    private Collection<ValueGrammarElement> filterElements(Collection<ValueGrammarElement> values, String propertyNamePrefix) {
        propertyNamePrefix = propertyNamePrefix.toLowerCase();
        List<ValueGrammarElement> filtered = new ArrayList<ValueGrammarElement>();
        for (ValueGrammarElement value : values) {
            if (value.toString().toLowerCase().startsWith(propertyNamePrefix)) {
                filtered.add(value);
            }
        }
        return filtered;
    }

    private Collection<PropertyDefinition> filterProperties(Collection<PropertyDefinition> props, String propertyNamePrefix) {
        propertyNamePrefix = propertyNamePrefix.toLowerCase();
        List<PropertyDefinition> filtered = new ArrayList<PropertyDefinition>();
        for (PropertyDefinition p : props) {
            if (p.getName().toLowerCase().startsWith(propertyNamePrefix)) {
                filtered.add(p);
            }
        }
        return filtered;
    }

    @Override
    public String document(ParserResult info, ElementHandle element) {
        HelpResolver resolver = CssModuleSupport.getHelpResolver();
        if (element instanceof CssPropertyElement) {
            CssPropertyElement e = (CssPropertyElement) element;
            PropertyDefinition property = e.getPropertyDescriptor();
            return resolver.getHelp(property);
        } else if (element instanceof ElementHandle.UrlHandle) {
            try {
                return URLRetriever.getURLContentAndCache(new URL(element.getName()));
            } catch (MalformedURLException e) {
                assert false;
            }
        }
        return null;
    }

    @Override
    public ElementHandle resolveLink(String link, ElementHandle elementHandle) {
        if (elementHandle instanceof CssPropertyElement) {
            CssPropertyElement e = (CssPropertyElement) elementHandle;
            PropertyDefinition property = e.getPropertyDescriptor();
            URL url = CssModuleSupport.getHelpResolver().resolveLink(property, link);
            if (url != null) {
                return new UrlHandle(url.toExternalForm());
            }
        }
        return null;
    }

    @Override
    public String getPrefix(ParserResult info, final int caretOffset, boolean upToOffset) {
        Snapshot snapshot = info.getSnapshot();
        TokenHierarchy hi = snapshot.getTokenHierarchy();
        String prefix = getPrefix(hi.tokenSequence(), snapshot.getEmbeddedOffset(caretOffset));

        //really ugly handling of class or id selector prefix:
        //Since the getPrefix() method is parser result based it is supposed
        //to work on top of the snapshot, while GsfCompletionProvider$Task.canFilter()
        //should be fast and hence operates on document, there arises a big contradiction -
        //For the virtually generated class and id selectors, the dot or hash chars
        //are part of the virtual source and hence becomes a part of the prefix in
        //this method call, while in the real html document they are invisible and an
        //attribute quote resides on their place.
        //So if a GsfCompletionProvider$CompletionEnvironment is created, an anchor
        //is computed from the caret offset and prefix lenght (prefix returned from
        //this method). After subsequent user's keystrokes the canFilter() method
        //gets text from this anchor to the caret from the edited document! So the
        //prefix contains the attribute quotation and any css items cannot be filtered.

        //this is a poor and hacky solution to this issue, some bug may appear for
        //non class or id elements starting with dot or hash?!?!?

        if (prefix.length() > 0 && (prefix.charAt(0) == '.' || prefix.charAt(0) == '#')) {
            firstPrefixChar = prefix.charAt(0);
            return prefix.substring(1);
        } else {
            firstPrefixChar = 0;
            return prefix;
        }

    }

//    private String normalizeLink(ElementHandle handle , String link){
//        if ( link.startsWith("." )|| link.startsWith("/" )){ // NOI18N
//            return normalizeLink(handle, link.substring( 1 ));
//        }
//        int index = link.lastIndexOf('#');
//        if ( index !=-1 ){
//            if ( index ==0 || link.charAt(index-1) =='/'){
//                String helpZipUrl = CssHelpResolver.getHelpZIPURL().getPath();
//                if ( handle instanceof CssPropertyElement ){
//                    String name = ((CssPropertyElement)handle).getPropertyDescriptor().getName();
//                    URL propertyHelpURL = CssHelpResolver.instance().
//                        getPropertyHelpURL(name);
//                    String path = propertyHelpURL.getPath();
//                    if ( path.startsWith( helpZipUrl )){
//                        path = path.substring(helpZipUrl.length());
//                    }
//                    return path+link.substring( index );
//                }
//                else if (handle instanceof UrlHandle){
//                    String url = handle.getName();
//                    int anchorIndex = url.lastIndexOf('#');
//                    if ( anchorIndex!= -1 ){
//                        url = url.substring( 0, anchorIndex);
//                    }
//                    //"normalize" the URL - use just the "path" part
//                    try {
//                        URL _url = new URL(url);
//                        url = _url.getPath();
//                    } catch(MalformedURLException mue) {
//                        Logger.getLogger("global").log(Level.INFO, null, mue);
//                    }
//
//                    if ( url.startsWith( helpZipUrl)){
//                        url = url.substring(helpZipUrl.length());
//                    }
//                    return url+link.substring( index );
//                }
//            }
//        }
//        return link;
//    }
    private String getPrefix(TokenSequence<CssTokenId> ts, int caretOffset) {
        //we are out of any css
        if (ts == null) {
            return null;
        }

        int diff = ts.move(caretOffset);
        if (diff == 0) {
            if (!ts.movePrevious()) {
                //beginning of the token sequence, cannot get any prefix
                return ""; //NOI18N
            }
        } else {
            if (!ts.moveNext()) {
                return null;
            }
        }
        Token<CssTokenId> t = ts.token();

        if(t.id() != CssTokenId.GENERIC_AT_RULE) {
            switch (t.id().getTokenCategory()) {
                case KEYWORDS:
                case OPERATORS:
                case BRACES:
                    return EMPTY_STRING;
            }
        }


        int skipPrefixChars = 0;
        switch (t.id()) {
            case COLON:
            case DCOLON:
            case COMMA:
            case LBRACKET:
                return EMPTY_STRING;

            case STRING:
                skipPrefixChars = 1; //skip the leading quotation char
                break;
        }

        return t.text().subSequence(skipPrefixChars, diff == 0 ? t.text().length() : diff).toString().trim();

    }

    @Override
    public QueryType getAutoQuery(JTextComponent component, String typedText) {
        int offset = component.getCaretPosition();
        if (typedText == null || typedText.length() == 0) {
            return QueryType.NONE;
        }
        char c = typedText.charAt(typedText.length() - 1);

        TokenSequence<CssTokenId> ts = LexerUtils.getJoinedTokenSequence(component.getDocument(), offset, CssTokenId.language());
        if (ts != null) {
            int diff = ts.move(offset);
            TokenId currentTokenId = null;
            if (ts != null) {
                if (diff == 0 && ts.movePrevious() || ts.moveNext()) {
                    currentTokenId = ts.token().id();
                }
            }

            if (currentTokenId == CssTokenId.IDENT) {
                return QueryType.COMPLETION;
            }

            //#177306  Eager CSS CC
            //
            //open completion when a space is pressed, but only
            //if typed by user by pressing the spacebar.
            //
            //1) filters out tabs which are converted to spaces
            //   before being put into the document
            //2) filters out newline keystrokes which causes the indentation
            //   to put some spaces on the newline
            //3) filters out typing spaces in comments
            //
            if (typedText.length() == 1 && c == ' ' && currentTokenId != CssTokenId.COMMENT) {
                return QueryType.COMPLETION;
            }
        }

        switch (c) {
            case '\n':
            case '}':
            case ';': {
                return QueryType.STOP;
            }
            case '.':
            case '#':
            case ':':
            case ',': {
                return QueryType.COMPLETION;
            }
        }
        return QueryType.NONE;
    }

    @Override
    public String resolveTemplateVariable(String variable, ParserResult info, int caretOffset, String name, Map parameters) {
        return null;
    }

    @Override
    public Set<String> getApplicableTemplates(Document doc, int selectionBegin, int selectionEnd) {
        return Collections.emptySet();
    }

    @Override
    public ParameterInfo parameters(ParserResult info, int caretOffset, CompletionProposal proposal) {
        return ParameterInfo.NONE;
    }

    private CodeCompletionResult handleLexicalBasedCompletion(FileObject file, TokenSequence<CssTokenId> ts, Snapshot snapshot, int caretOffset) {
        //position the token sequence on the caret position, not the recomputed offset with substracted prefix length
        int tokenDiff = ts.move(snapshot.getEmbeddedOffset(caretOffset));
        if (ts.moveNext() || ts.movePrevious()) {
            boolean addSemicolon = true;
            switch (ts.token().id()) {
                case SEMI: //@import |;
                    addSemicolon = false;
                case WS: //@import |
                    if (addSemicolon) {
                        Token semicolon = LexerUtils.followsToken(ts, CssTokenId.SEMI, false, true, CssTokenId.S);
                        addSemicolon = (semicolon == null);
                    }
                    if (null != LexerUtils.followsToken(ts, CssTokenId.IMPORT_SYM, true, false, CssTokenId.S)) {
                        List<CompletionProposal> imports = (List<CompletionProposal>) completeImport(file, caretOffset, "", true, addSemicolon);
                        int moveBack = (addSemicolon ? 1 : 0) + 1; //+1 means the added quotation mark length
                        return new CssFileCompletionResult(imports, moveBack);
                    }
                    break;
                case STRING: //@import "|"; or @import "fil|";
                    Token<CssTokenId> originalToken = ts.token();
                    addSemicolon = false;
                    if (null != LexerUtils.followsToken(ts, CssTokenId.IMPORT_SYM, true, false, CssTokenId.S)) {
                        //strip off the leading quote and the rest of token after caret
                        String valuePrefix = originalToken.text().toString().substring(1, tokenDiff);
                        List<CompletionProposal> imports = (List<CompletionProposal>) completeImport(file,
                                caretOffset, valuePrefix, false, addSemicolon);
                        int moveBack = addSemicolon ? 1 : 0;
                        return new CssFileCompletionResult(imports, moveBack);

                    }
                    break;

            }
        }

        return null;
    }

    private void completeClassSelectors(CompletionContext context, List<CompletionProposal> completionProposals, boolean unmappableClassOrId) {
        //Why we need the (prefix.length() > 0 || astCaretOffset == node.from())???
        //
        //We need to filter out situation when the node contains some whitespaces
        //at the end. For example:
        //    h1 { color     : red;}
        // the color property node contains the whole text to the colon
        //
        //In such case the prefix is empty and the cc would offer all 
        //possible values there

        Node node = context.getActiveNode();
        FileObject file = context.getSnapshot().getSource().getFileObject();
        String prefix = context.getPrefix();
        int offset = context.getAnchorOffset();
        NodeType nodeType = node.type();

        if (nodeType == NodeType.cssClass
                || (unmappableClassOrId || nodeType == NodeType.error) && prefix.length() == 1 && prefix.charAt(0) == '.') {
            //complete class selectors
            if (file != null) {
                CssProjectSupport sup = CssProjectSupport.findFor(file);
                if (sup != null) {
                    CssIndex index = sup.getIndex();
                    DependenciesGraph deps = index.getDependencies(file);
                    Collection<FileObject> refered = deps.getAllReferedFiles();

                    //adjust prefix - if there's just . before the caret, it is returned
                    //as a prefix. If there are another characters, the dot is ommited
                    if (prefix.length() == 1 && prefix.charAt(0) == '.') {
                        prefix = "";
                        offset++; //offset point to the dot position, we need to skip it
                    }
                    //get map of all fileobject declaring classes with the prefix
                    Map<FileObject, Collection<String>> search = index.findClassesByPrefix(prefix);
                    Collection<String> refclasses = new HashSet<String>();
                    Collection<String> allclasses = new HashSet<String>();
                    for (FileObject fo : search.keySet()) {
                        allclasses.addAll(search.get(fo));
                        //is the file refered by the current file?
                        if (refered.contains(fo)) {
                            //yes - add its classes
                            refclasses.addAll(search.get(fo));
                        }
                    }

                    //lets create the completion items
                    List<CompletionProposal> proposals = new ArrayList<CompletionProposal>(refclasses.size());
                    for (String clazz : allclasses) {
                        proposals.add(CssCompletionItem.createSelectorCompletionItem(new CssElement(context.getFileObject(), clazz),
                                clazz,
                                offset,
                                refclasses.contains(clazz)));
                    }
                    completionProposals.addAll(proposals);

                }
            }
        }
    }

    private void completeIdSelectors(CompletionContext context, List<CompletionProposal> completionProposals, boolean unmappableClassOrId) {
        Node node = context.getActiveNode();
        FileObject file = context.getSnapshot().getSource().getFileObject();
        String prefix = context.getPrefix();
        int offset = context.getAnchorOffset();
        NodeType nodeType = node.type();

        if (prefix.length() > 0 && (node.type() == NodeType.cssId
                || (unmappableClassOrId || nodeType == NodeType.error /*
                 * || nodeType == NodeType.JJTERROR_SKIPBLOCK
                 */) && prefix.charAt(0) == '#')) {
            //complete class selectors
            if (file != null) {
                CssProjectSupport sup = CssProjectSupport.findFor(file);
                if (sup != null) {
                    CssIndex index = sup.getIndex();
                    DependenciesGraph deps = index.getDependencies(file);
                    Collection<FileObject> refered = deps.getAllReferedFiles();

                    //adjust prefix - if there's just # before the caret, it is returned as a prefix
                    //if there is some text behind the prefix the hash is part of the prefix
                    if (prefix.length() == 1 && prefix.charAt(0) == '#') {
                        prefix = "";
                    } else {
                        prefix = prefix.substring(1); //cut off the #
                    }
                    offset++; //offset point to the hash position, we need to skip it

                    //get map of all fileobject declaring classes with the prefix
                    Map<FileObject, Collection<String>> search = index.findIdsByPrefix(prefix); //cut off the dot (.)
                    Collection<String> allids = new HashSet<String>();
                    Collection<String> refids = new HashSet<String>();
                    for (FileObject fo : search.keySet()) {
                        allids.addAll(search.get(fo));
                        //is the file refered by the current file?
                        if (refered.contains(fo)) {
                            //yes - add its classes
                            refids.addAll(search.get(fo));
                        }
                    }

                    //lets create the completion items
                    List<CompletionProposal> proposals = new ArrayList<CompletionProposal>(allids.size());
                    for (String id : allids) {
                        proposals.add(CssCompletionItem.createSelectorCompletionItem(new CssElement(context.getFileObject(), id),
                                id,
                                offset,
                                refids.contains(id)));
                    }
                    completionProposals.addAll(proposals);
                }
            }
        }
    }

    private void completeAtRulesAndHtmlSelectors(CompletionContext context, List<CompletionProposal> completionProposals) {
        Node node = context.getActiveNode();
        String prefix = context.getPrefix();
        int offset = context.getAnchorOffset();
        NodeType nodeType = node.type();

        if (NodeUtil.isOfType(node, NodeType.root, NodeType.styleSheet, NodeType.body, NodeType.moz_document)
                || nodeType == NodeType.error && NodeUtil.isOfType(node.parent(), NodeType.root, NodeType.styleSheet, NodeType.body, NodeType.moz_document)) {
            /*
             * somewhere between rules, in an empty or very broken file, between
             * rules
             */
            List<CompletionProposal> all = new ArrayList<CompletionProposal>();
            //complete at keywords without prefix
            all.addAll(Utilities.createRAWCompletionProposals(AT_RULES, ElementKind.FIELD, offset));
            //complete html selector names
            all.addAll(completeHtmlSelectors(context, prefix, offset));
            completionProposals.addAll(all);

        }
    }

    private void completeHtmlSelectors(CompletionContext completionContext, List<CompletionProposal> completionProposals, TokenId tokenNodeTokenId) {
        NodeType nodeType = completionContext.getActiveNode().type();
        String prefix = completionContext.getPrefix();
        int caretOffset = completionContext.getCaretOffset();

        switch (nodeType) {
            case media:
                completionProposals.addAll(completeHtmlSelectors(completionContext, completionContext.getPrefix(), completionContext.getCaretOffset()));
                break;
            case elementName:
                //complete selector's element name - with a prefix
                completionProposals.addAll(completeHtmlSelectors(completionContext, prefix, completionContext.getSnapshot().getOriginalOffset(completionContext.getActiveNode().from())));
                break;

            case elementSubsequent:
            case typeSelector:
                //complete element name - without a or with a prefix
                completionProposals.addAll(completeHtmlSelectors(completionContext, prefix, caretOffset));
                break;
            case selectorsGroup:
            case combinator:
            case selector:
            case body:
                //complete selector list without prefix in selector list e.g. BODY, | { ... }
                completionProposals.addAll(completeHtmlSelectors(completionContext, prefix, caretOffset));
                break;

            case error:
                Node parentNode = completionContext.getActiveNode().parent();
                if (parentNode == null) {
                    break;
                }
                switch (completionContext.getTokenSequence().token().id()) {
                    //completion of selectors after universal selector * | { ... }
                    case WS:
                        switch (parentNode.type()) {
                            case typeSelector:
                            case simpleSelectorSequence:
                                //complete selector list in selector list with an error
                                completionProposals.addAll(completeHtmlSelectors(completionContext, prefix, caretOffset));
                                break;
                        }
                        break;

                }
                break;
        }

    }

    private void completeKeywords(CompletionContext completionContext, List<CompletionProposal> completionProposals, boolean tokenFound) {
        NodeType nodeType = completionContext.getActiveNode().type();
        if (nodeType == NodeType.imports 
                || nodeType == NodeType.media 
                || nodeType == NodeType.page 
                || nodeType == NodeType.charSet
                || nodeType == NodeType.generic_at_rule/*
                 * || nodeType == NodeType.JJTFONTFACERULE
                 */) {
            //complete at keywords with prefix - parse tree OK
            if (tokenFound) {
                TokenId id = completionContext.getTokenSequence().token().id();
                if (id == CssTokenId.IMPORT_SYM 
                        || id == CssTokenId.MEDIA_SYM 
                        || id == CssTokenId.PAGE_SYM 
                        || id == CssTokenId.CHARSET_SYM
                        || id == CssTokenId.GENERIC_AT_RULE/*
                         * || id == CssTokenId.FONT_FACE_SYM
                         */
                        || id == CssTokenId.ERROR) {
                    //we are on the right place in the node

                    Collection<String> possibleValues = filterStrings(AT_RULES, completionContext.getPrefix());
                    completionProposals.addAll(Utilities.createRAWCompletionProposals(possibleValues, ElementKind.FIELD, completionContext.getSnapshot().getOriginalOffset(completionContext.getActiveNode().from())));
                }
            }

        }
    }

    private void completePropertyName(CompletionContext cc, List<CompletionProposal> completionProposals) {
        NodeType nodeType = cc.getActiveNode().type();

        String prefix = cc.getPrefix();
        
        //1. css property name completion with prefix
        if (nodeType == NodeType.property && (prefix.length() > 0 || cc.getEmbeddedCaretOffset() == cc.getActiveNode().from())) {
            Collection<PropertyDefinition> possibleProps = filterProperties(Properties.getProperties(), prefix);
            completionProposals.addAll(Utilities.wrapProperties(possibleProps, cc.getSnapshot().getOriginalOffset(cc.getActiveNode().from())));
        }

        //2. in a garbage (may be for example a dash prefix in a ruleset
        if (nodeType == NodeType.recovery || nodeType == NodeType.error) {
            Node parent = cc.getActiveNode().parent();
            if (parent != null && (parent.type() == NodeType.rule || parent.type() == NodeType.moz_document)) {
                
                //>>> Bug 204821 - Incorrect completion for vendor specific properties
                boolean bug204821 = false;
                if (prefix.length() == 0) {
                    //
                    //If the vendor specific property name is completed with the - (MINUS)
                    //prefix the cc.getPrefix() is empty since minus is operator
                    //But particulary in this case the prefix must count
                    if (cc.getActiveTokenNode().type() == NodeType.token) {
                        CssTokenId tokenId = NodeUtil.getTokenNodeTokenId(cc.getActiveTokenNode());
                        if (tokenId == CssTokenId.MINUS) {
                            bug204821 = true;
                        }
                    }
                }
                if(bug204821) {
                    //get all "-" prefixed props
                    Collection<PropertyDefinition> possibleProps = 
                            filterProperties(Properties.getProperties(), "-");
                    //and add them to the result with the "-" prefix stripped
                    completionProposals.addAll(Utilities.wrapProperties(possibleProps, cc.getCaretOffset(), 1));
                } else {
                    Collection<PropertyDefinition> possibleProps = 
                            filterProperties(Properties.getProperties(), prefix);
                    completionProposals.addAll(Utilities.wrapProperties(possibleProps, cc.getCaretOffset()));
                }
            }
        }
        
        //3. in empty rule (NodeType.ruleSet)
        //h1 { | }
        //
        //4. between declaration-s (NodeType.declarations)
        //h1 { color:red; | font: bold }
        //
        //should be no prefix 
        if (nodeType == NodeType.rule
                || nodeType == NodeType.moz_document
                || nodeType == NodeType.declarations) {
            completionProposals.addAll(Utilities.wrapProperties(Properties.getProperties(), cc.getCaretOffset()));
        }

    }

    private void completePropertyValue(
            CompletionContext context,
            List<CompletionProposal> completionProposals,
            char charAfterCaret) {

        Node node = context.getActiveNode();
        String prefix = context.getPrefix();
        NodeType nodeType = node.type();

        switch (nodeType) {

            case declaration: {
                //value cc without prefix
                //find property node

                final Node[] result = new Node[2];
                NodeVisitor propertySearch = new NodeVisitor() {

                    @Override
                    public boolean visit(Node node) {
                        if (node.type() == NodeType.property) {
                            result[0] = node;
                        } else if (node.type() == NodeType.error) {
                            result[1] = node;
                        }
                        return false;
                    }
                };
                propertySearch.visitChildren(node);

                Node property = result[0];
                if (property == null) {
                    return;
                }

                String expressionText = ""; //NOI18N
                if (result[1] != null) {
                    //error in the property value
                    //we need to extract the value from the property node image

                    String propertyImage = node.image().toString().trim();
                    //if the property is the last one in the rule then the error
                    //contains the closing rule bracket
                    if (propertyImage.endsWith("}")) { //NOI18N
                        propertyImage = propertyImage.substring(0, propertyImage.length() - 1);
                    }

                    int colonIndex = propertyImage.indexOf(':'); //NOI18N
                    if (colonIndex >= 0) {
                        expressionText = propertyImage.substring(colonIndex + 1);
                    }

                    //remove semicolon if it happens to appear in the image
                    //completion in place like: background: | ;
                    //or in html code <div style="backgroung:|"/> (virtual source generator adds the semi)
                    int semiIndex = expressionText.lastIndexOf(';');
                    if(semiIndex > 0) {
                        expressionText = expressionText.substring(0, semiIndex);
                    }
                    
                    //use just the current line, if the expression spans to multiple
                    //lines it is likely because of parsing error
                    int eolIndex = expressionText.indexOf('\n');
                    if (eolIndex > 0) {
                        expressionText = expressionText.substring(0, eolIndex);
                    }

                }

                PropertyModel prop = Properties.getPropertyModel(property.image().toString().trim());
                if (prop != null) {

                    ResolvedProperty propVal = new ResolvedProperty(prop, expressionText);

                    Collection<ValueGrammarElement> alts = propVal.getAlternatives();

                    Collection<ValueGrammarElement> filteredByPrefix = filterElements(alts, prefix);

                    int completionItemInsertPosition = prefix.trim().length() == 0
                            ? context.getCaretOffset()
                            : context.getSnapshot().getOriginalOffset(node.from());

                    //test the situation when completion is invoked just after a valid token
                    //like color: rgb|
                    //in such case the parser offers ( alternative which is valid
                    //so we must not use the prefix for filtering the results out.
                    //do that only if the completion is not called in the middle of a text,
                    //there must be a whitespace after the caret
                    boolean addSpaceBeforeItem = false;
                    if (alts.size() > 0 && filteredByPrefix.isEmpty() && Character.isWhitespace(charAfterCaret)) {
                        completionItemInsertPosition = context.getCaretOffset(); //complete on the position of caret
                        filteredByPrefix = alts; //prefix is empty, do not filter at all
                        addSpaceBeforeItem = true;
                    }

                    completionProposals.addAll(wrapPropertyValues(context,
                            prefix,
                            prop.getProperty(),
                            filteredByPrefix,
                            completionItemInsertPosition,
                            false,
                            addSpaceBeforeItem,
                            false));


                }
            }
            break;

            case recovery:
                Node parent = node.parent();
                if (parent.type() != NodeType.error) {
                    break;
                }
                node = parent;
            //fall through

            case error:
                NodeType parentType = node.parent().type();
                if (!(parentType == NodeType.term || parentType == NodeType.expression || parentType == NodeType.operator)) {
                    break;
                }
            //fall through

            case function:
            case functionName:
            case term:
            case expression:
            case operator: {
                //value cc with prefix
                //a. for term nodes
                //b. for error skip declaration nodes with declaration parent,
                //for example if user types color: # and invokes the completion
                //find property node
                //1.find declaration node first

                final Node[] result = new Node[1];
                NodeVisitor declarationSearch = new NodeVisitor() {

                    @Override
                    public boolean visit(Node node) {
                        if (node.type() == NodeType.declaration) {
                            result[0] = node;
                        }
                        return false;
                    }
                };
                declarationSearch.visitAncestors(node);
                Node declaratioNode = result[0];
                
                if(declaratioNode == null) {
                    //not in property declaration, give up
                    break;
                }

                //2.find the property node
                result[0] = null;
                NodeVisitor propertySearch = new NodeVisitor() {

                    @Override
                    public boolean visit(Node node) {
                        if (node.type() == NodeType.property) {
                            result[0] = node;
                        }
                        return false;
                    }
                };
                propertySearch.visitChildren(declaratioNode);

                Node property = result[0];

                String propertyName = property.image().toString();
                PropertyModel propertyModel = Properties.getPropertyModel(propertyName);
                if (propertyModel == null) {
                    return;
                }

                //text from the node start to the embedded anchor offset (=embedded caret offset - prefix length)

                Node expressionNode = NodeUtil.query(declaratioNode, "propertyValue/expression"); //NOI18N

                String expressionText = context.getSnapshot().getText().subSequence(
                        expressionNode.from(),
                        context.getEmbeddedAnchorOffset()).toString();

                //use just the current line, if the expression spans to multiple
                //lines it is likely because of parsing error
                int eolIndex = expressionText.indexOf('\n');
                if (eolIndex > 0) {
                    expressionText = expressionText.substring(0, eolIndex);
                }

                ResolvedProperty propVal = new ResolvedProperty(propertyModel, expressionText);
                Collection<ValueGrammarElement> alts = propVal.getAlternatives();
                Collection<ValueGrammarElement> filteredByPrefix = filterElements(alts, prefix);

                int completionItemInsertPosition = context.getAnchorOffset();
                boolean addSpaceBeforeItem = false;

                boolean includePrefixInTheExpression = false;
                boolean extendedItemsOnly = false;

                
                block: if (Character.isWhitespace(charAfterCaret)) {
                    //do the following heuristics only if the completion is invoked after, not inside a token

                    //case #1
                    //========
                    //color: #| completion
                    if (prefix.equals("#")) {
                        completionItemInsertPosition = context.getCaretOffset() - 1;
                        filteredByPrefix = alts; //prefix is empty, do not filter at all
                        extendedItemsOnly = true; //do not add any default alternatives items
                        break block;
                    }

                    //case #2
                    //========
                    //in the situation that there's a prefix, but no alternatives matches
                    //we may also try alternatives after the prefix (at the end of the expression)
                    //
                    //animation: cubic-bezier(20|  => evaluated expression is "cubic-bezier("
                    //                                so !number is the alternative, but filtered out since
                    //                                the completion prefix is "20"
                    //
                    //so lets use "cubic-bezier(20" as the expression and empty prefix
                    //this usually happens for unit acceptors (they typically do not put all possible
                    //values to the completion list so the filtering fails).
                    if (!prefix.isEmpty() && filteredByPrefix.isEmpty()) {
                        includePrefixInTheExpression = true;
                        break block;
                    }

                    //case #3
                    //test the situation when completion is invoked just after a valid token
                    //like color: rgb| or font-family: cursive|
                    for (ValueGrammarElement vge : filteredByPrefix) {
                        if (vge.getValue().toString().equals(prefix)) {
                            includePrefixInTheExpression = true;
                            break;
                        }
                    }
                }

                if (includePrefixInTheExpression) {
                    //re-run the property value evaluation with expression including the prefix token
                    expressionText = context.getSnapshot().getText().subSequence(
                            expressionNode.from(),
                            context.getEmbeddedCaretOffset()).toString();

                    //use just the current line, if the expression spans to multiple
                    //lines it is likely because of parsing error
                    eolIndex = expressionText.indexOf('\n');
                    if (eolIndex > 0) {
                        expressionText = expressionText.substring(0, eolIndex);
                    }

                    propVal = new ResolvedProperty(propertyModel, expressionText);
                    alts = propVal.getAlternatives();
                    filteredByPrefix = alts; //no prefix
                    completionItemInsertPosition = context.getCaretOffset(); //no prefix
                    addSpaceBeforeItem = true;
                }

                completionProposals.addAll(wrapPropertyValues(context,
                        prefix,
                        propertyModel.getProperty(),
                        filteredByPrefix,
                        completionItemInsertPosition,
                        false,
                        addSpaceBeforeItem,
                        extendedItemsOnly));


            } //case
        } //switch
    }

    private static class CssFileCompletionResult extends DefaultCompletionResult {

        private int moveCaretBack;

        public CssFileCompletionResult(List<CompletionProposal> list, int moveCaretBack) {
            super(list, false);
            this.moveCaretBack = moveCaretBack;
        }

        @Override
        public void afterInsert(CompletionProposal item) {
            Caret c = EditorRegistry.lastFocusedComponent().getCaret();
            c.setDot(c.getDot() - moveCaretBack);
        }
    }

    private static class CssLinkCompletion extends FileReferenceCompletion<CssCompletionItem> {

        private static final String GO_UP_TEXT = "../"; //NOI18N
        private boolean addQuotes;
        private boolean addSemicolon;
        private FileObject file;

        public CssLinkCompletion(FileObject file, boolean addQuotes, boolean addSemicolon) {
            this.file = file;
            this.addQuotes = addQuotes;
            this.addSemicolon = addSemicolon;
        }

        @Override
        public CssCompletionItem createFileItem(int anchor, String name, Color color, ImageIcon icon) {
            return CssCompletionItem.createFileCompletionItem(new CssElement(file, name), name, anchor, color, icon, addQuotes, addSemicolon);
        }

        @Override
        public CssCompletionItem createGoUpItem(int anchor, Color color, ImageIcon icon) {
            return CssCompletionItem.createFileCompletionItem(new CssElement(file, GO_UP_TEXT), GO_UP_TEXT, anchor, color, icon, addQuotes, addSemicolon);
        }
    }
}
