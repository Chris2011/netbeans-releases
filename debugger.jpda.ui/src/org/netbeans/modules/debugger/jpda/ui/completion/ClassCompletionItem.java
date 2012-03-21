/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.debugger.jpda.ui.completion;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;
import org.openide.util.Exceptions;

/**
 *
 * @author Martin Entlicher
 */
class ClassCompletionItem implements CompletionItem {

    private String clazz;
    private boolean isPackage;
    private int caretOffset;

    public ClassCompletionItem(String clazz, int caretOffset, boolean isPackage) {
        this.clazz = clazz;
        this.isPackage = isPackage;
        this.caretOffset = caretOffset;
    }

    @Override
    public void defaultAction(JTextComponent component) {
        //StyledDocument doc = (StyledDocument) component.getDocument();
        Document doc = component.getDocument();
        try {
            String text = doc.getText(0, caretOffset);
            int dot = text.lastIndexOf('.');
            if (dot < 0) dot = 0;
            else dot++;
            doc.remove(dot, caretOffset - dot);
            caretOffset = dot;
            doc.insertString(caretOffset, clazz, null);
            if (isPackage) {
                doc.insertString(caretOffset + clazz.length(), ".", null);
            }
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
        if (!isPackage) {
            //This statement will close the code completion box:
            Completion.get().hideAll();
        }
    }

    @Override
    public void processKeyEvent(KeyEvent evt) {

    }

    @Override
    public int getPreferredWidth(Graphics g, Font font) {
        return CompletionUtilities.getPreferredWidth(clazz, null, g, font);
    }

    @Override
    public void render(Graphics g, Font defaultFont, Color defaultColor, Color backgroundColor, int width, int height, boolean selected) {
        ImageIcon fieldIcon = null;
        CompletionUtilities.renderHtml(fieldIcon, clazz, null, g, defaultFont,
        Color.black/*(selected ? Color.white : fieldColor)*/, width, height, selected);
    }

    @Override
    public CompletionTask createDocumentationTask() {
        return null;
    }

    @Override
    public CompletionTask createToolTipTask() {
        return null;
    }

    @Override
    public boolean instantSubstitution(JTextComponent component) {
        return false;
    }

    @Override
    public int getSortPriority() {
        return 0;
    }

    @Override
    public CharSequence getSortText() {
        return clazz;
    }

    @Override
    public CharSequence getInsertPrefix() {
        return clazz;
    }

}
