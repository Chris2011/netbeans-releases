/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.php.editor.indent;

import javax.swing.text.BadLocationException;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.editor.indent.spi.Context;
import org.netbeans.modules.editor.indent.spi.ExtraLock;
import org.netbeans.modules.editor.indent.spi.IndentTask;
import org.netbeans.modules.php.editor.lexer.PHPTokenId;

/**
 * @author tomslot
 */
public class PHPIndentTask implements IndentTask {
    
    private Context context;

    PHPIndentTask(Context context) {
        this.context = context;
    }

    public void reindent() throws BadLocationException {
        
        BaseDocument doc = (BaseDocument)context.document();
        doc.atomicLock();
        // a workaround for issue #131929
        doc.putProperty("HTML_FORMATTER_ACTS_ON_TOP_LEVEL", Boolean.TRUE); //NOI18N
        
        try {
            if (context.isIndent()) {
                
                // check if the cursor is within PHP language block
                TokenHierarchy th = TokenHierarchy.get(doc);
                TokenSequence ts = th.tokenSequence();
                ts.move(context.caretOffset());
                
                if (!ts.moveNext()){
                    return;
                }
                
                while (ts.embedded() != null) {
                    ts = ts.embedded();
                    ts.move(context.caretOffset());
                    ts.moveNext();
                }
                
                if (ts.language() != PHPTokenId.language()) {
                    return;
                }

                // trivial implementation, copy the indent from the previous line
                int currentLine = Utilities.getLineOffset(doc, context.startOffset());
                int previousLineIndent = 0;
                
                if (currentLine > 0) {
                    int previousLineStartOffset = Utilities.getRowStartFromLineOffset(doc, currentLine - 1);
                    previousLineIndent = context.lineIndent(previousLineStartOffset);
                }
                
                context.modifyIndent(Utilities.getRowStartFromLineOffset(doc, currentLine),
                        previousLineIndent);
            }

        } finally {
            doc.atomicUnlock();
        }

    }

    public ExtraLock indentLock() {
        return null;
    }

}
