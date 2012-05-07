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
import java.util.List;
import java.util.Set;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.xml.XMLUtil;

/**
 *
 * @author Martin Entlicher
 */
class ElementCompletionItem implements CompletionItem {
    
    private static final String ICON_PACKAGE = "org/netbeans/modules/debugger/jpda/resources/completion_package.gif"; //NOI18N
    private static final String ICON_CLASS = "org/netbeans/modules/editor/resources/completion/class_16.png"; //NOI18N
    private static final String ICON_INTERFACE = "org/netbeans/modules/editor/resources/completion/interface.png"; // NOI18N
    private static final String ICON_ENUM = "org/netbeans/modules/editor/resources/completion/enum.png"; // NOI18N
    private static final String ICON_ANNOTATION = "org/netbeans/modules/editor/resources/completion/annotation_type.png"; // NOI18N
    private static final String ICON_FIELD_PUBLIC = "org/netbeans/modules/editor/resources/completion/field_16.png"; //NOI18N
    private static final String ICON_FIELD_PROTECTED = "org/netbeans/modules/editor/resources/completion/field_protected_16.png"; //NOI18N
    private static final String ICON_FIELD_PACKAGE = "org/netbeans/modules/editor/resources/completion/field_package_private_16.png"; //NOI18N
    private static final String ICON_FIELD_PRIVATE = "org/netbeans/modules/editor/resources/completion/field_private_16.png"; //NOI18N        
    private static final String ICON_FIELD_ST_PUBLIC = "org/netbeans/modules/editor/resources/completion/field_static_16.png"; //NOI18N
    private static final String ICON_FIELD_ST_PROTECTED = "org/netbeans/modules/editor/resources/completion/field_static_protected_16.png"; //NOI18N
    private static final String ICON_FIELD_ST_PACKAGE = "org/netbeans/modules/editor/resources/completion/field_static_package_private_16.png"; //NOI18N
    private static final String ICON_FIELD_ST_PRIVATE = "org/netbeans/modules/editor/resources/completion/field_static_private_16.png"; //NOI18N
    private static final String ICON_CONSTRUCTOR_PUBLIC = "org/netbeans/modules/editor/resources/completion/constructor_16.png"; //NOI18N
    private static final String ICON_CONSTRUCTOR_PROTECTED = "org/netbeans/modules/editor/resources/completion/constructor_protected_16.png"; //NOI18N
    private static final String ICON_CONSTRUCTOR_PACKAGE = "org/netbeans/modules/editor/resources/completion/constructor_package_private_16.png"; //NOI18N
    private static final String ICON_CONSTRUCTOR_PRIVATE = "org/netbeans/modules/editor/resources/completion/constructor_private_16.png"; //NOI18N
    private static final String ICON_METHOD_PUBLIC = "org/netbeans/modules/editor/resources/completion/method_16.png"; //NOI18N
    private static final String ICON_METHOD_PROTECTED = "org/netbeans/modules/editor/resources/completion/method_protected_16.png"; //NOI18N
    private static final String ICON_METHOD_PACKAGE = "org/netbeans/modules/editor/resources/completion/method_package_private_16.png"; //NOI18N
    private static final String ICON_METHOD_PRIVATE = "org/netbeans/modules/editor/resources/completion/method_private_16.png"; //NOI18N        
    private static final String ICON_METHOD_ST_PUBLIC = "org/netbeans/modules/editor/resources/completion/method_static_16.png"; //NOI18N
    private static final String ICON_METHOD_ST_PROTECTED = "org/netbeans/modules/editor/resources/completion/method_static_protected_16.png"; //NOI18N
    private static final String ICON_METHOD_ST_PRIVATE = "org/netbeans/modules/editor/resources/completion/method_static_private_16.png"; //NOI18N
    private static final String ICON_METHOD_ST_PACKAGE = "org/netbeans/modules/editor/resources/completion/method_static_package_private_16.png"; //NOI18N
    private static final String PARAMETER_NAME_COLOR = "<font color=#a06001>"; //NOI18N
    private static final String BOLD = "<b>"; //NOI18N
    private static final String BOLD_END = "</b>"; //NOI18N

    //private String clazz;
    private String htmlName;
    private String rightHtmlName;
    private String elementName;
    private ElementKind elementKind;
    private Set<Modifier> modifiers;
    private ExecutableElement executableElement;
    private String prefix;
    //private boolean isPackage;
    private int caretOffset;
    private ImageIcon icon;

    public ElementCompletionItem(String elementName, ElementKind elementKind, int caretOffset) {
        this(elementName, elementKind, null, caretOffset);
    }
    
    public ElementCompletionItem(String elementName, ElementKind elementKind, Set<Modifier> modifiers, int caretOffset) {
        //this.clazz = clazz;
        this.elementName = elementName;
        this.elementKind = elementKind;
        this.modifiers = modifiers;
        //this.isPackage = isPackage;
        this.caretOffset = caretOffset;
        String color;
        switch (elementKind) {
            case PACKAGE:   color = "#005600"; // NOI18N
                            break;
            case ANNOTATION_TYPE:
            case ENUM:
            case CLASS:     color = "#560000"; // NOI18N
                            break;
            case INTERFACE: color = "#404040"; // NOI18N
                            break;
            case FIELD:     color = "#008618"; // NOI18N
                            break;
            case CONSTRUCTOR:color = "#b28b00";// NOI18N
                            break;
            case METHOD:    color = "#000000"; // NOI18N
                            break;
            default:        color = null;
        }
        if (color != null) {
            htmlName = "<font color="+color+">" + escape(elementName) + "</font>"; // NOI18N
        } else {
            htmlName = elementName;
        }
    }
    
    void setInsertPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    void setExecutableElement(ExecutableElement executableElement) {
        this.executableElement = executableElement;
        StringBuilder nameBuilder = new StringBuilder();
        nameBuilder.append(BOLD);
        nameBuilder.append(htmlName);
        nameBuilder.append(BOLD_END);
        nameBuilder.append('(');
        List<? extends VariableElement> parameters = executableElement.getParameters();
        //List<? extends TypeParameterElement> typeParameters = executableElement.getTypeParameters();
        int n = parameters.size();
        for (int i = 0; i < n; i++) {
            if (i > 0) {
                nameBuilder.append(", ");
            }
            //nameBuilder.append(escape(typeParameters.get(i).getSimpleName().toString()));
            nameBuilder.append(escape(getTypeSimpleName(parameters.get(i).asType())));
            nameBuilder.append(' ');
            nameBuilder.append(PARAMETER_NAME_COLOR);
            nameBuilder.append(parameters.get(i).getSimpleName());
            nameBuilder.append("</font>");
        }
        nameBuilder.append(')');
        htmlName = nameBuilder.toString();
        TypeMirror returnType = executableElement.getReturnType();
        rightHtmlName = getTypeSimpleName(returnType);
    }
    
    void setElementType(TypeMirror tm) {
        rightHtmlName = getTypeSimpleName(tm);
    }
    
    private static String getTypeSimpleName(TypeMirror tm) {
        String name = tm.toString();
        int i = name.lastIndexOf('.');
        if (i > 0) {
            name = name.substring(i+1);
        }
        return name;
    }

    private ImageIcon getIcon() {
        if (icon == null) {
            String iconPath;
            switch (elementKind) {
                case PACKAGE:   iconPath = ICON_PACKAGE;
                                break;
                case CLASS:     iconPath = ICON_CLASS;
                                break;
                case INTERFACE: iconPath = ICON_INTERFACE;
                                break;
                case ENUM:      iconPath = ICON_ENUM;
                                break;
                case ANNOTATION_TYPE:iconPath = ICON_ANNOTATION;
                                break;
                case FIELD:
                    if (modifiers != null) {
                        if (modifiers.contains(Modifier.STATIC)) {
                            if (modifiers.contains(Modifier.PRIVATE)) {
                                iconPath = ICON_FIELD_ST_PRIVATE;
                            } else if (modifiers.contains(Modifier.PROTECTED)) {
                                iconPath = ICON_FIELD_ST_PROTECTED;
                            } else if (modifiers.contains(Modifier.PUBLIC)) {
                                iconPath = ICON_FIELD_ST_PUBLIC;
                            } else {
                                iconPath = ICON_FIELD_ST_PACKAGE;
                            }
                        } else {
                            if (modifiers.contains(Modifier.PRIVATE)) {
                                iconPath = ICON_FIELD_PRIVATE;
                            } else if (modifiers.contains(Modifier.PROTECTED)) {
                                iconPath = ICON_FIELD_PROTECTED;
                            } else if (modifiers.contains(Modifier.PUBLIC)) {
                                iconPath = ICON_FIELD_PUBLIC;
                            } else {
                                iconPath = ICON_FIELD_PACKAGE;
                            }
                        }
                    } else {
                        iconPath = ICON_FIELD_PACKAGE;
                    }
                                break;
                case CONSTRUCTOR:
                    if (modifiers.contains(Modifier.PRIVATE)) {
                        iconPath = ICON_CONSTRUCTOR_PRIVATE;
                    } else if (modifiers.contains(Modifier.PROTECTED)) {
                        iconPath = ICON_CONSTRUCTOR_PROTECTED;
                    } else if (modifiers.contains(Modifier.PUBLIC)) {
                        iconPath = ICON_CONSTRUCTOR_PUBLIC;
                    } else {
                        iconPath = ICON_CONSTRUCTOR_PACKAGE;
                    }
                case METHOD:
                    if (modifiers != null) {
                        if (modifiers.contains(Modifier.STATIC)) {
                            if (modifiers.contains(Modifier.PRIVATE)) {
                                iconPath = ICON_METHOD_ST_PRIVATE;
                            } else if (modifiers.contains(Modifier.PROTECTED)) {
                                iconPath = ICON_METHOD_ST_PROTECTED;
                            } else if (modifiers.contains(Modifier.PUBLIC)) {
                                iconPath = ICON_METHOD_ST_PUBLIC;
                            } else {
                                iconPath = ICON_METHOD_ST_PACKAGE;
                            }
                        } else {
                            if (modifiers.contains(Modifier.PRIVATE)) {
                                iconPath = ICON_METHOD_PRIVATE;
                            } else if (modifiers.contains(Modifier.PROTECTED)) {
                                iconPath = ICON_METHOD_PROTECTED;
                            } else if (modifiers.contains(Modifier.PUBLIC)) {
                                iconPath = ICON_METHOD_PUBLIC;
                            } else {
                                iconPath = ICON_METHOD_PACKAGE;
                            }
                        }
                    } else {
                        iconPath = ICON_METHOD_PACKAGE;
                    }
                                break;
                default:        iconPath = null;
            }
            if (iconPath != null) {
                icon = ImageUtilities.loadImageIcon(iconPath, false);
            }
        }
        return icon;
    }
    
    private static String escape(String s) {
        if (s != null) {
            try {
                return XMLUtil.toAttributeValue(s);
            } catch (Exception ex) {}
        }
        return s;
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
            String insertStr = ((prefix != null) ? prefix : "") + elementName;
            doc.insertString(caretOffset, insertStr, null);
            if (elementKind == ElementKind.PACKAGE) {
                doc.insertString(caretOffset + insertStr.length(), ".", null);
            }
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
        if (elementKind != ElementKind.PACKAGE) {
            //This statement will close the code completion box:
            Completion.get().hideAll();
        }
    }

    @Override
    public void processKeyEvent(KeyEvent evt) {

    }

    @Override
    public int getPreferredWidth(Graphics g, Font font) {
        return CompletionUtilities.getPreferredWidth(htmlName, rightHtmlName, g, font);
    }

    @Override
    public void render(Graphics g, Font defaultFont, Color defaultColor, Color backgroundColor, int width, int height, boolean selected) {
        CompletionUtilities.renderHtml(getIcon(), htmlName, rightHtmlName, g, defaultFont,
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
        return elementName;
    }

    @Override
    public CharSequence getInsertPrefix() {
        return elementName;
    }

}
