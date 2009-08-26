/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.php.project;

import java.util.LinkedList;
import java.util.List;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.editor.PhpElement;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.editor.EditorExtender;
import org.netbeans.modules.php.spi.phpmodule.PhpFrameworkProvider;
import org.openide.filesystems.FileObject;

/**
 * @author Tomas Mysik
 */
public class PhpEditorExtender extends EditorExtender {

    private final PhpProject project;

    public PhpEditorExtender(PhpProject project) {
        assert project != null;
        this.project = project;
    }

    @Override
    public List<PhpElement> getElementsForCodeCompletion(FileObject fo) {
        List<PhpElement> elements = new LinkedList<PhpElement>();
        PhpModule module = project.getPhpModule();
        for (PhpFrameworkProvider frameworkProvider : ProjectPropertiesSupport.getFrameworks(project)) {
            EditorExtender editorExtender = frameworkProvider.getEditorExtender(module);
            if (editorExtender != null) {
                elements.addAll(editorExtender.getElementsForCodeCompletion(fo));
            }
        }
        return elements;
    }

    @Override
    public PhpClass getClass(FileObject fo, String variableName) {
        PhpModule module = project.getPhpModule();
        for (PhpFrameworkProvider frameworkProvider : ProjectPropertiesSupport.getFrameworks(project)) {
            EditorExtender editorExtender = frameworkProvider.getEditorExtender(module);
            if (editorExtender != null) {
                PhpClass phpClass = editorExtender.getClass(fo, variableName);
                if (phpClass != null) {
                    return phpClass;
                }
            }
        }
        return null;
    }
}
