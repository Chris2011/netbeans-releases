/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javascript2.requirejs.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.javascript2.editor.api.lexer.JsTokenId;
import org.netbeans.modules.javascript2.editor.api.lexer.LexUtilities;
import org.netbeans.modules.parsing.api.Snapshot;

/**
 *
 * @author Petr Pisl
 */
public class EditorUtils {

    public static final String DEFINE = "define";    //NOI18N
    public static final String REQUIRE = "require";    //NOI18N
    public static final String REQUIREJS = "requirejs"; // NOI18N
    public static final String PATHS = "paths";        //NOI18N
    public static final String BASE_URL = "baseUrl";    //NOI8N
    public static final String CONFIG_METHOD_NAME = "config"; //NOI18N

    /**
     * Returns true if at the offset there is a string and the string is in a
     * call of define or require method.
     *
     * @param snapshot
     * @param offset
     * @return
     */
    public static boolean isFileReference(final Snapshot snapshot, int offset) {
        CodeCompletionContext context = findContext(snapshot, offset);
        return context == CodeCompletionContext.CONFIG_BASE_URL_VALUE 
                || context == CodeCompletionContext.CONFIG_PATHS_VALUE
                || context == CodeCompletionContext.REQUIRE_MODULE;
    }

    public static enum CodeCompletionContext {

        /**
         * in configuration object after baseUrl: ''
         */
        CONFIG_BASE_URL_VALUE,
        /**
         * Define file path in paths object in the configuration object
         */
        CONFIG_PATHS_VALUE,
        /**
         * name of properties in the configuration object
         */
        CONFIG_PROPERTY_NAME,
        /**
         * names and paths of modules in require, requirejs, define etc.
         */
        REQUIRE_MODULE,
        /**
         * other
         */
        UNKNOWN
    };

    public static CodeCompletionContext findContext(final Snapshot snapshot, final int offset) {
        TokenSequence<? extends JsTokenId> ts = LexUtilities.getJsTokenSequence(snapshot, offset);
        if (ts == null) {
            return CodeCompletionContext.UNKNOWN;
        }

        ts.move(offset);
        if (ts.moveNext()) {
            Token<? extends JsTokenId> token = ts.token();
            if (token.id() == JsTokenId.STRING || token.id() == JsTokenId.STRING_END) {
                token = LexUtilities.findPrevious(ts, Arrays.asList(JsTokenId.WHITESPACE, JsTokenId.EOL, JsTokenId.BLOCK_COMMENT, JsTokenId.LINE_COMMENT,
                        JsTokenId.STRING_BEGIN, JsTokenId.STRING, JsTokenId.STRING_END, JsTokenId.OPERATOR_COMMA));
                if (token.id() == JsTokenId.BRACKET_LEFT_BRACKET || token.id() == JsTokenId.OPERATOR_COLON || token.id() == JsTokenId.BRACKET_LEFT_PAREN) {
                    token = LexUtilities.findPreviousToken(ts, Arrays.asList(JsTokenId.IDENTIFIER));
                    if (token.id() == JsTokenId.IDENTIFIER) {
                        if (DEFINE.equals(token.text().toString()) || REQUIRE.equals(token.text().toString()) || REQUIREJS.equals(token.text().toString())) {
                            return CodeCompletionContext.REQUIRE_MODULE;
                        } else if (BASE_URL.equals(token.text().toString())) {
                            return CodeCompletionContext.CONFIG_BASE_URL_VALUE;
                        } else if (PATHS.equals(token.text().toString())) {
                            // in the case, when the property of path are written as string
                            return CodeCompletionContext.CONFIG_PATHS_VALUE;
                        }
                        token = LexUtilities.findPreviousToken(ts, Arrays.asList(JsTokenId.BRACKET_LEFT_CURLY));
                        if (token.id() == JsTokenId.BRACKET_LEFT_CURLY) {
                            token = LexUtilities.findPreviousToken(ts, Arrays.asList(JsTokenId.IDENTIFIER));
                            if (token.id() == JsTokenId.IDENTIFIER && PATHS.equals(token.text().toString())) {
                                return CodeCompletionContext.CONFIG_PATHS_VALUE;
                            }
                        }
                    }
                }
            } else {
                // can be property name?
                List<JsTokenId> listIds = Arrays.asList(JsTokenId.OPERATOR_COMMA, JsTokenId.OPERATOR_COLON, JsTokenId.BRACKET_LEFT_CURLY, JsTokenId.OPERATOR_SEMICOLON, JsTokenId.BRACKET_RIGHT_CURLY);
                // find previous , or : or { or ;
                token = LexUtilities.findPreviousToken(ts, listIds);

                boolean commaFirst = false;
                boolean isPropertyName = false;
                int balance = 1;
                while (token.id() == JsTokenId.OPERATOR_COMMA && ts.movePrevious()) {
                    token = LexUtilities.findPreviousToken(ts, listIds);
                    commaFirst = true;
                    if (token.id() == JsTokenId.BRACKET_RIGHT_CURLY) {
                        while (ts.movePrevious() && balance > 0) {
                            token = ts.token();
                            if (token.id() == JsTokenId.BRACKET_RIGHT_CURLY) {
                                balance++;
                            } else if (token.id() == JsTokenId.BRACKET_LEFT_CURLY) {
                                balance--;
                            }
                        }
                        token = LexUtilities.findPreviousToken(ts, listIds);
                    }
                    if (token.id() == JsTokenId.OPERATOR_COLON) {
                        // we are in the previous property definition
                        isPropertyName = true;
                        break;
                    }
                }
                List<JsTokenId> emptyIds = Arrays.asList(JsTokenId.WHITESPACE, JsTokenId.EOL, JsTokenId.BLOCK_COMMENT);
                if (token.id() == JsTokenId.BRACKET_LEFT_CURLY && ts.movePrevious()) {
                    
                    // check whether it's the first property in the object literal definion
                    token = LexUtilities.findPrevious(ts, emptyIds);
                    if (token.id() == JsTokenId.BRACKET_LEFT_PAREN || token.id() == JsTokenId.OPERATOR_COMMA || token.id() == JsTokenId.OPERATOR_EQUALS) {
                        isPropertyName = true;
                    } else if (token.id() == JsTokenId.BRACKET_RIGHT_PAREN) {
                        // it can be a method definition
                        balance = 1;
                        while (ts.movePrevious() && balance > 0) {
                            token = ts.token();
                            if (token.id() == JsTokenId.BRACKET_RIGHT_PAREN) {
                                balance++;
                            } else if (token.id() == JsTokenId.BRACKET_LEFT_PAREN) {
                                balance--;
                            }
                        }
                        if (balance == 0) {
                            token = LexUtilities.findPrevious(ts, emptyIds);
                            if (token.id() == JsTokenId.KEYWORD_FUNCTION && ts.movePrevious()) {
                                // we found a method definition, now we need to check, whether its in an object literal
                                token = LexUtilities.findPrevious(ts, emptyIds);
                                if (token.id() == JsTokenId.OPERATOR_COLON) {
                                    isPropertyName = commaFirst;
                                }
                            }
                        }
                    }
                }
                if (isPropertyName) {
                    if (token.id() == JsTokenId.OPERATOR_COLON) {
                        balance = 1;
                        while (ts.movePrevious() && balance > 0) {
                            token = ts.token();
                            if (token.id() == JsTokenId.BRACKET_RIGHT_CURLY) {
                                balance++;
                            } else if (token.id() == JsTokenId.BRACKET_LEFT_CURLY) {
                                balance--;
                            }
                        }
                        token = LexUtilities.findPrevious(ts, emptyIds);
                    }
                    if (token.id() == JsTokenId.BRACKET_LEFT_PAREN && ts.movePrevious()) {
                        token = LexUtilities.findPrevious(ts, emptyIds);
                        if (token.id() == JsTokenId.IDENTIFIER && CONFIG_METHOD_NAME.equals(token.text().toString())) {
                            return CodeCompletionContext.CONFIG_PROPERTY_NAME;
                        }
                    }
                }
            }
        }
        return CodeCompletionContext.UNKNOWN;
    }

    public static Collection<String> getUsedFileInDefine(final Snapshot shanpshot, final int offset) {
        TokenSequence<? extends JsTokenId> ts = LexUtilities.getJsTokenSequence(shanpshot, offset);
        if (ts == null) {
            return Collections.emptyList();
        }
        ts.move(0);
        if (!ts.moveNext()) {
            return Collections.emptyList();
        }
        Token<? extends JsTokenId> token = LexUtilities.findNextToken(ts, Arrays.asList(JsTokenId.IDENTIFIER));
        while (token.id() == JsTokenId.IDENTIFIER && !DEFINE.equals(token.text().toString()) && ts.moveNext()) {
            token = LexUtilities.findNextToken(ts, Arrays.asList(JsTokenId.IDENTIFIER));
        }
        if (token.id() == JsTokenId.IDENTIFIER && DEFINE.equals(token.text().toString())) {
            // we are probably found the define method
            List<String> paths = new ArrayList<String>();
            token = LexUtilities.findNextToken(ts, Arrays.asList(JsTokenId.BRACKET_LEFT_BRACKET, JsTokenId.KEYWORD_FUNCTION, JsTokenId.BRACKET_LEFT_CURLY, JsTokenId.BRACKET_RIGHT_PAREN));
            if (token.id() == JsTokenId.BRACKET_LEFT_BRACKET) {
                do {
                    token = LexUtilities.findNextToken(ts, Arrays.asList(JsTokenId.STRING, JsTokenId.OPERATOR_COMMA, JsTokenId.BRACKET_RIGHT_PAREN));
                    if (token.id() == JsTokenId.STRING) {
                        paths.add(token.text().toString());
                    }
                } while ((token.id() != JsTokenId.BRACKET_RIGHT_PAREN && token.id() != JsTokenId.OPERATOR_SEMICOLON && !token.id().isKeyword()) && ts.moveNext());
                return paths;
            }
        }
        return Collections.emptyList();
    }
}
