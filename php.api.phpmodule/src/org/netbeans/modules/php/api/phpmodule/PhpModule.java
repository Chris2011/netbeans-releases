/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.php.api.phpmodule;

import java.beans.PropertyChangeEvent;
import java.util.prefs.Preferences;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.Parameters;
import org.openide.util.Utilities;
import org.openide.windows.WindowManager;

/**
 * This class could be useful for extending a PHP project.
 * <p>
 * Note: For public API, this should likely be final class using accessor pattern.
 * @author Tomas Mysik
 */
public abstract class PhpModule implements Lookup.Provider {

    /**
     * Property for frameworks.
     * @see #propertyChanged(PropertyChangeEvent)
     * @since 2.4
     */
    public static final String PROPERTY_FRAMEWORKS = "PROPERTY_FRAMEWORKS"; // NOI18N

    /**
     * See {@link org.netbeans.api.project.ProjectInformation#getName}.
     */
    @NonNull
    public abstract String getName();

    /**
     * See {@link org.netbeans.api.project.ProjectInformation#getDisplayName}.
     */
    @NonNull
    public abstract String getDisplayName();

    /**
     * CHeck whether the PHP module is broken (e.g. missing Source Files).
     * @return {@code true} if the PHP module is broken, {@code false} otherwise
     */
    public abstract boolean isBroken();

    /**
     * Get the project directory for this PHP module.
     * @return the project directory, never <code>null</code>
     */
    @NonNull
    public abstract FileObject getProjectDirectory();

    /**
     * Get the source directory for this PHP module.
     * @return the source directory, <b>can be <code>null</code> or {@link org.openide.filesystems.FileObject#isValid() invalid} if the project is {@link #isBroken() broken}.</b>
     */
    @CheckForNull
    public abstract FileObject getSourceDirectory();

    /**
     * Get the test directory for this PHP module.
     * @return the test directory, can be <code>null</code> if not set yet
     */
    @CheckForNull
    public abstract FileObject getTestDirectory();

    /**
     * Get any optional abilities of this PHP module.
     * @return a set of abilities
     * @since 2.28
     */
    @Override
    public abstract Lookup getLookup();

    /**
     * Get {@link Preferences} of this PHP module.
     * This method is suitable for storing (and reading) PHP module specific properties.
     * For more information, see {@link org.netbeans.api.project.ProjectUtils#getPreferences(org.netbeans.api.project.Project, Class, boolean)}.
     * @param clazz a class which defines the namespace of preferences
     * @param shared whether the returned settings should be shared
     * @return {@link Preferences} for this PHP module and the given class
     * @see org.netbeans.api.project.ProjectUtils#getPreferences(org.netbeans.api.project.Project, Class, boolean)
     */
    @NonNull
    public abstract Preferences getPreferences(Class<?> clazz, boolean shared);

    /**
     * A way for informing PHP module that something has changed.
     * @param propertyChangeEvent property change event
     * @since 2.18
     * @see #PROPERTY_FRAMEWORKS
     */
    public abstract void notifyPropertyChanged(@NonNull PropertyChangeEvent propertyChangeEvent);

    /**
     * <b>Deprecated, {@link #getLookup() lookup} {@link PhpModuleProperties} class.
     * This method will be removed after NB 8.0.</b>
     * <p>
     * Get the current {@link PhpModuleProperties properties} of this PHP module.
     * Please note that caller should not hold this properties because they can
     * change very often (if user changes Run Configuration).
     * @return the current {@link PhpModuleProperties properties}
     */
    @Deprecated
    @NonNull
    public abstract PhpModuleProperties getProperties();

    /**
     * <b>Deprecated, {@link #getLookup() lookup} {@link org.netbeans.spi.project.ui.CustomizerProvider2} class
     * and use its methods. This method will be removed after NB 8.0.</b>
     * <p>
     * Open Project Properties dialog for this PHP module with the given category.
     * @param category category to be preselected
     * @since 2.12
     */
    @Deprecated
    public abstract void openCustomizer(String category);

    //~ Factories

    /**
     * Gets PHP module for the given {@link FileObject}.
     * @param fo {@link FileObject} to get PHP module for
     * @return PHP module or <code>null</code> if not found
     */
    @CheckForNull
    public static PhpModule forFileObject(FileObject fo) {
        Parameters.notNull("fo", fo); // NOI18N
        Project project = FileOwnerQuery.getOwner(fo);
        if (project == null) {
            return null;
        }
        return lookupPhpModule(project);
    }

    /**
     * Infers PHP module - from the currently selected top component, open projects etc.
     * @return PHP module or <code>null</code> if not found.
     */
    @CheckForNull
    public static PhpModule inferPhpModule() {
        // try current context firstly
        Node[] activatedNodes = WindowManager.getDefault().getRegistry().getActivatedNodes();
        if (activatedNodes != null) {
            for (Node n : activatedNodes) {
                PhpModule result = lookupPhpModule(n.getLookup());
                if (result != null) {
                    return result;
                }
            }
        }

        Lookup globalContext = Utilities.actionsGlobalContext();
        PhpModule result = lookupPhpModule(globalContext);
        if (result != null) {
            return result;
        }
        FileObject fo = globalContext.lookup(FileObject.class);
        if (fo != null) {
            result = forFileObject(fo);
            if (result != null) {
                return result;
            }
        }

        // next try main project
        OpenProjects projects = OpenProjects.getDefault();
        Project mainProject = projects.getMainProject();
        if (mainProject != null) {
            result = lookupPhpModule(mainProject);
            if (result != null) {
                return result;
            }
        }

        // next try other opened projects
        for (Project project : projects.getOpenProjects()) {
            result = lookupPhpModule(project);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    /**
     * Get {@link PhpModule PHP module} from the given project.
     * @param project a PHP project where to look for a PHP module for
     * @return PHP module or {@code null} if not found
     * @see 1.38
     */
    @CheckForNull
    public static PhpModule lookupPhpModule(Project project) {
        Parameters.notNull("project", project);

        return project.getLookup().lookup(PhpModule.class);
    }

    /**
     * Get {@link PhpModule PHP module} from the given lookup.
     * @param lookup a lookup where to look for a PHP module for
     * @return PHP module or {@code null} if not found
     * @see 1.38
     */
    @CheckForNull
    public static PhpModule lookupPhpModule(Lookup lookup) {
        Parameters.notNull("lookup", lookup);

        // try directly
        PhpModule result = lookup.lookup(PhpModule.class);
        if (result != null) {
            return result;
        }
        // try through Project instance
        Project project = lookup.lookup(Project.class);
        if (project == null) {
            return null;
        }
        return lookupPhpModule(project);
    }

}
