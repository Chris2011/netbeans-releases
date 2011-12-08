/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javascript2.editor;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.javascript2.editor.lexer.JsTokenId;
import org.netbeans.modules.javascript2.editor.lexer.LexUtilities;
import org.netbeans.spi.editor.bracesmatching.BracesMatcher;
import org.netbeans.spi.editor.bracesmatching.MatcherContext;

/**
 *
 * @author Petr Hejl
 */
public class JsBracesMatcher implements BracesMatcher {

    private final MatcherContext context;

    public JsBracesMatcher (MatcherContext context) {
        this.context = context;
    }

    @Override
    public int [] findOrigin() throws InterruptedException, BadLocationException {
        ((AbstractDocument) context.getDocument()).readLock();
        try {
            BaseDocument doc = (BaseDocument) context.getDocument();
            int offset = context.getSearchOffset();

            TokenSequence<?extends JsTokenId> ts = LexUtilities.getJsTokenSequence(doc, offset);

            if (ts != null) {
                ts.move(offset);

                if (!ts.moveNext()) {
                    return null;
                }

                Token<?extends JsTokenId> token = ts.token();

                if (token == null) {
                    return null;
                }

                TokenId id = token.id();

                if (id == JsTokenId.STRING_BEGIN) {
                    return new int [] { ts.offset(), ts.offset() + token.length() };
                } else if (id == JsTokenId.STRING_END) {
                    return new int [] { ts.offset(), ts.offset() + token.length() };
                } else if (id == JsTokenId.REGEXP_BEGIN) {
                    return new int [] { ts.offset(), ts.offset() + token.length() };
                } else if (id == JsTokenId.REGEXP_END) {
                    return new int [] { ts.offset(), ts.offset() + token.length() };
                } else if (id == JsTokenId.BRACKET_LEFT_PAREN) {
                    return new int [] { ts.offset(), ts.offset() + token.length() };
                } else if (id == JsTokenId.BRACKET_RIGHT_PAREN) {
                    return new int [] { ts.offset(), ts.offset() + token.length() };
                } else if (id == JsTokenId.BRACKET_LEFT_BRACKET) {
                    return new int [] { ts.offset(), ts.offset() + token.length() };
                } else if (id == JsTokenId.BRACKET_RIGHT_BRACKET) {
                    return new int [] { ts.offset(), ts.offset() + token.length() };
                } else if (id == JsTokenId.BRACKET_LEFT_CURLY) {
                    return new int [] { ts.offset(), ts.offset() + token.length() };
                } else if (id == JsTokenId.BRACKET_RIGHT_CURLY) {
                    return new int [] { ts.offset(), ts.offset() + token.length() };
                }
            }
            return null;
        } finally {
            ((AbstractDocument) context.getDocument()).readUnlock();
        }
    }

    @Override
    public int [] findMatches() throws InterruptedException, BadLocationException {
        ((AbstractDocument) context.getDocument()).readLock();
        try {
            BaseDocument doc = (BaseDocument) context.getDocument();
            int offset = context.getSearchOffset();

            TokenSequence<?extends JsTokenId> ts = LexUtilities.getJsTokenSequence(doc, offset);

            if (ts != null) {
                ts.move(offset);

                if (!ts.moveNext()) {
                    return null;
                }

                Token<?extends JsTokenId> token = ts.token();

                if (token == null) {
                    return null;
                }

                TokenId id = token.id();

                OffsetRange r;
                if (id == JsTokenId.STRING_BEGIN) {
                    r = LexUtilities.findFwd(doc, ts, JsTokenId.STRING_BEGIN, JsTokenId.STRING_END);
                    return new int [] {r.getStart(), r.getEnd() };
                } else if (id == JsTokenId.STRING_END) {
                    r = LexUtilities.findBwd(doc, ts, JsTokenId.STRING_BEGIN, JsTokenId.STRING_END);
                    return new int [] {r.getStart(), r.getEnd() };
                } else if (id == JsTokenId.REGEXP_BEGIN) {
                    r = LexUtilities.findFwd(doc, ts, JsTokenId.REGEXP_BEGIN, JsTokenId.REGEXP_END);
                    return new int [] {r.getStart(), r.getEnd() };
                } else if (id == JsTokenId.REGEXP_END) {
                    r = LexUtilities.findBwd(doc, ts, JsTokenId.REGEXP_BEGIN, JsTokenId.REGEXP_END);
                    return new int [] {r.getStart(), r.getEnd() };
                } else if (id == JsTokenId.BRACKET_LEFT_PAREN) {
                    r = LexUtilities.findFwd(doc, ts, JsTokenId.BRACKET_LEFT_PAREN, JsTokenId.BRACKET_RIGHT_PAREN);
                    return new int [] {r.getStart(), r.getEnd() };
                } else if (id == JsTokenId.BRACKET_RIGHT_PAREN) {
                    r = LexUtilities.findBwd(doc, ts, JsTokenId.BRACKET_LEFT_PAREN, JsTokenId.BRACKET_RIGHT_PAREN);
                    return new int [] {r.getStart(), r.getEnd() };
                } else if (id == JsTokenId.BRACKET_LEFT_BRACKET) {
                    r = LexUtilities.findFwd(doc, ts, JsTokenId.BRACKET_LEFT_BRACKET, JsTokenId.BRACKET_RIGHT_BRACKET);
                    return new int [] {r.getStart(), r.getEnd() };
                } else if (id == JsTokenId.BRACKET_RIGHT_BRACKET) {
                    r = LexUtilities.findBwd(doc, ts, JsTokenId.BRACKET_LEFT_BRACKET, JsTokenId.BRACKET_RIGHT_BRACKET);
                    return new int [] {r.getStart(), r.getEnd() };
                } else if (id == JsTokenId.BRACKET_LEFT_CURLY) {
                    r = LexUtilities.findFwd(doc, ts, JsTokenId.BRACKET_LEFT_CURLY, JsTokenId.BRACKET_RIGHT_CURLY);
                    return new int [] {r.getStart(), r.getEnd() };
                } else if (id == JsTokenId.BRACKET_RIGHT_CURLY) {
                    r = LexUtilities.findBwd(doc, ts, JsTokenId.BRACKET_LEFT_CURLY, JsTokenId.BRACKET_RIGHT_CURLY);
                    return new int [] {r.getStart(), r.getEnd() };
                }
            }
            return null;
        } finally {
            ((AbstractDocument) context.getDocument()).readUnlock();
        }
    }
}
