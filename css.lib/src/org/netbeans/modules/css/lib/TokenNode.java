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
package org.netbeans.modules.css.lib;

import org.antlr.runtime.CommonToken;
import org.netbeans.modules.css.lib.api.CssTokenId;
import org.netbeans.modules.css.lib.api.NodeType;

/**
 *
 * @author marekfukala
 */
public class TokenNode extends AbstractParseTreeNode {

    private final int from, to;
    private CssTokenId tokenId;

    public TokenNode(CharSequence source, CommonToken token) {
        super(source); 
        int[] range = CommonTokenUtil.getCommonTokenOffsetRange(token);
        from = range[0];
        to = range[1];
        tokenId = CssTokenId.forTokenTypeCode(token.getType());
    }

    @Override
    public String name() {
        return image().toString();
    }
    
    @Override
    public NodeType type() {
        return NodeType.token;
    }

    /**
     * Gets the kind of the token encapsulated by this TokenNode. Please notice
     * that it's not possible to get the token itself since the parser source
     * is not a stream of the netbeans tokens, but tokens generated by the antlr lexer.
     */
    public CssTokenId getTokenId() {
        return tokenId;
    }

    @Override
    public int from() {
        return from;
    }

    @Override
    public int to() {
        return to;
    }

    @Override
    public String toString() {
        return new StringBuilder(super.toString())
                .append(' ')
                .append('\'')
                .append(image())
                .append('\'')
                .append(' ')
                .append('[')
                .append(getTokenId())
                .append(']')
                .toString();
    }
}
