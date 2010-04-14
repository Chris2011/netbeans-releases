/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.html.editor.refactoring.api;

import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.refactoring.api.AbstractRefactoring;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 *
 * @author marekfukala
 */
public class ExtractInlinedStyleRefactoring extends AbstractRefactoring {

    public enum Mode {
        refactorToExistingEmbeddedSection,
        refactorToNewEmbeddedSection,
        refactorToReferedExternalSheet,
        refactorToExistingExternalSheet,
        refactorToNewExternalSheet,
    }

    private Mode mode;
    private SelectorType selectorType;
    private OffsetRange existingEmbeddedCssSection;
    private FileObject externalSheet;

    public ExtractInlinedStyleRefactoring(Lookup refactoringSource) {
        super(refactoringSource);
    }

    public OffsetRange getExistingEmbeddedCssSection() {
        return existingEmbeddedCssSection;
    }

    public void setExistingEmbeddedCssSection(OffsetRange existingEmbeddedCssSection) {
        this.existingEmbeddedCssSection = existingEmbeddedCssSection;
    }

    public FileObject getExternalSheet() {
        return externalSheet;
    }

    public void setExternalSheet(FileObject externalSheet) {
        this.externalSheet = externalSheet;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public SelectorType getSelectorType() {
        return selectorType;
    }

    public void setSelectorType(SelectorType selectorType) {
        this.selectorType = selectorType;
    }

}
