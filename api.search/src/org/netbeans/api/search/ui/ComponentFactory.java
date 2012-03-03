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
package org.netbeans.api.search.ui;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.modules.search.FindDialogMemory;

/**
 * Factory class containing methods for creating controller objects for GUI
 * components and adjusting their properties so that they can be used in search
 * forms of search dialog.
 *
 * @author jhavlin
 */
public class ComponentFactory {

    private ComponentFactory() {
        // hiding default constructor
    }

    /**
     * Adjust a jComboBox to act as component for selecting file name pattern,
     * and return a controller object for interacting with it.
     *
     * @param jComboBox Freshly created component that will be modified.
     * @return Controller for modified jComboBox.
     */
    public static @NonNull FileNameComboBox createFileNameComboBox(
            @NonNull JComboBox jComboBox) {
        return new FileNameComboBox(jComboBox);
    }

    /**
     * Adjust a jComboBox to act as component for selecting search scope, and
     * return a controller object for interacting with it.
     *
     * @param jComboBox Freshly created component that will be modified.
     * @return Controller for modified jComboBox.
     */
    public static @NonNull
    ScopeComboBox createScopeComboBox(
            @NonNull JComboBox jComboBox,
            @NullAllowed String preferredScopeId) {
        return new ScopeComboBox(jComboBox,
                preferredScopeId == null
                ? FindDialogMemory.getDefault().getScopeTypeId()
                : preferredScopeId);
    }

    /**
     * Adjust a panel for specifying search scope options.
     *
     * @param jPanel Empty (with no child components) panel to adjust.
     * @param searchAndReplace True if options for search-and-replace mode
     * should be shown.
     * @param fileNameComboBox File-name combo box that will be bound to this
     * settings panel.
     * @return Panel with controls for setting search options (search in
     * archives, search in generated sources, use ignore list, treat file name
     * pattern as regular expression matching file path)
     */
    public static @NonNull ScopeSettingsPanel createScopeSettingsPanel(
            @NonNull JPanel jPanel, boolean searchAndReplace,
            @NonNull FileNameComboBox fileNameComboBox) {
         return new ScopeSettingsPanel(jPanel, fileNameComboBox,
                searchAndReplace);
    }
}
