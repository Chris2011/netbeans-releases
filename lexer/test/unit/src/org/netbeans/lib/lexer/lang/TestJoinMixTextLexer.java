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

package org.netbeans.lib.lexer.lang;

import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;

/**
 *
 * @author mmetelka
 */
final class TestJoinMixTextLexer implements Lexer<TestJoinMixTextTokenId> {

    // Copy of LexerInput.EOF
    private static final int EOF = LexerInput.EOF;

    private final LexerInput input;
    
    private final TokenFactory<TestJoinMixTextTokenId> tokenFactory;
    
    TestJoinMixTextLexer(LexerRestartInfo<TestJoinMixTextTokenId> info) {
        this.input = info.input();
        this.tokenFactory = info.tokenFactory();
    }

    public Object state() {
        return null;
    }

    public Token<TestJoinMixTextTokenId> nextToken() {
        boolean inWS = false;
        while (true) {
            int c = input.read();
            switch (c) {
                case ' ':
                case '\t':
                case '\n':
                    if (!inWS) {
                        inWS = true;
                        if (input.readLength() > 1) {
                            input.backup(1);
                            return token(TestJoinMixTextTokenId.WORD);
                        }
                    }
                    break;

                case EOF: // no more chars on the input
                    if (input.readLength() > 0) {
                        return inWS ?
                                token(TestJoinMixTextTokenId.WHITESPACE) :
                                token(TestJoinMixTextTokenId.WORD);
                    }
                    return null; // the only legal situation when null can be returned

                default: // Non-ws
                    if (inWS) {
                        inWS = false;
                        if (input.readLength() > 1) {
                            input.backup(1);
                            return token(TestJoinMixTextTokenId.WHITESPACE);
                        }
                    }
                    break;
            }
        }
    }
    
    private Token<TestJoinMixTextTokenId> token(TestJoinMixTextTokenId id) {
        return tokenFactory.createToken(id);
    }
    
    public void release() {
    }

}
