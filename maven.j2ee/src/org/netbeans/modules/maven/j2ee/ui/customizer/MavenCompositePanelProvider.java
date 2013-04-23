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
package org.netbeans.modules.maven.j2ee.ui.customizer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import javax.swing.JComponent;
import org.netbeans.api.project.Project;
import org.netbeans.modules.maven.api.NbMavenProject;
import org.netbeans.modules.maven.api.customizer.ModelHandle2;
import org.netbeans.modules.maven.j2ee.ui.customizer.impl.CustomizerFrameworks;
import org.netbeans.modules.maven.j2ee.ui.customizer.impl.CustomizerRunEar;
import org.netbeans.modules.maven.j2ee.ui.customizer.impl.CustomizerRunEjb;
import org.netbeans.modules.maven.j2ee.ui.customizer.impl.CustomizerRunWeb;
import org.netbeans.modules.maven.j2ee.web.WebProjectUtils;
import org.netbeans.modules.web.clientproject.api.jslibs.JavaScriptLibraryCustomizerPanel;
import org.netbeans.modules.web.clientproject.api.jslibs.JavaScriptLibrarySelectionPanel;
import org.netbeans.modules.web.common.api.CssPreprocessors;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.netbeans.spi.project.ui.support.ProjectCustomizer.Category;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Martin Janicek
 */
public final class MavenCompositePanelProvider implements ProjectCustomizer.CompositeCategoryProvider {

    private static final String JS_LIBRARIES = "JavaScript-Libraries"; // NOI18N
    private static final String FRAMEWORKS = "Frameworks"; // NOI18N
    private static final String RUN = "Run"; // NOI18N

    private CustomizerFrameworks frameworkCustomizer;
    private BaseRunCustomizer runCustomizer;


    private String type;

    private MavenCompositePanelProvider(String type) {
        this.type = type;
    }

    @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "org-netbeans-modules-maven", position = 257)
    public static MavenCompositePanelProvider createFrameworks() {
        return new MavenCompositePanelProvider(FRAMEWORKS);
    }

    @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "org-netbeans-modules-maven", position = 301)
    public static MavenCompositePanelProvider createRun() {
        return new MavenCompositePanelProvider(RUN);
    }
    
    @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "org-netbeans-modules-maven", position = 351)
    public static MavenCompositePanelProvider createJavaScriptLibraries() {
        return new MavenCompositePanelProvider(JS_LIBRARIES);
    }
    
    @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "org-netbeans-modules-maven", position = 375)
    public static ProjectCustomizer.CompositeCategoryProvider createCssPreprocessors() {
        return CssPreprocessors.getDefault().createCustomizer();
    }


    @Override
    public Category createCategory(Lookup context) {
        Project project = context.lookup(Project.class);
        String projectType = project.getLookup().lookup(NbMavenProject.class).getPackagingType();
        
        if (JS_LIBRARIES.equals(type)) {
            if (NbMavenProject.TYPE_WAR.equalsIgnoreCase(projectType) == false) {
                return null; // We want to create JavaScript libraries customizer only for Maven Web Project
            }
        }
        if (FRAMEWORKS.equals(type)) {
            if (NbMavenProject.TYPE_WAR.equalsIgnoreCase(projectType) == false) {
                return null; // We want to create Framework customizer only for Maven Web Project
            }
        }
        if (RUN.equals(type)) {
            if ((NbMavenProject.TYPE_WAR.equalsIgnoreCase(projectType) == false) &&
                (NbMavenProject.TYPE_EJB.equalsIgnoreCase(projectType) == false) &&
                (NbMavenProject.TYPE_EAR.equalsIgnoreCase(projectType) == false)) {
                return null; // We want to create Run customizer only for Web/Ejb/Ear projects
            }
        }

        return ProjectCustomizer.Category.create(type, NbBundle.getMessage(MavenCompositePanelProvider.class, "PNL_" + type), null); // NOI18N
    }

    @Override
    public JComponent createComponent(Category category, Lookup context) {
        String name = category.getName();
        ModelHandle2 handle = context.lookup(ModelHandle2.class);
        final Project project = context.lookup(Project.class);

        category.setOkButtonListener(listenerAWT);
        category.setStoreListener(listenerNonAWT);

        if (JS_LIBRARIES.equals(name)) {
            return new JavaScriptLibraryCustomizerPanel(category, new JavaScriptLibraryCustomizerPanel.CustomizerSupport() {
    
                @Override
                public File getWebRoot() {
                    FileObject docBase = WebProjectUtils.getDocumentBase(project);
                    if (docBase != null) {
                        return FileUtil.toFile(docBase);
                    }
                    return null;
                }

                @Override
                public void setLibrariesFolder(String librariesFolder) {
                }

                @Override
                public void setSelectedLibraries(List<JavaScriptLibrarySelectionPanel.SelectedLibrary> selectedLibraries) {
                }
            });
        }
        if (FRAMEWORKS.equals(name)) {
            frameworkCustomizer = new CustomizerFrameworks(category, project);
            return frameworkCustomizer;
        }
        if (RUN.equals(name)) {
            String projectType = project.getLookup().lookup(NbMavenProject.class).getPackagingType();

            if (NbMavenProject.TYPE_WAR.equalsIgnoreCase(projectType)) {
                runCustomizer = new CustomizerRunWeb(handle, project);
            }
            if (NbMavenProject.TYPE_EJB.equalsIgnoreCase(projectType)) {
                runCustomizer = new CustomizerRunEjb(handle, project);
            }
            if (NbMavenProject.TYPE_EAR.equalsIgnoreCase(projectType)) {
                runCustomizer = new CustomizerRunEar(handle, project);
            }
            return runCustomizer;
        }

        return null;
    }

    private ActionListener listenerAWT = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            if (runCustomizer != null) {
                runCustomizer.applyChangesInAWT();
            }
            if (frameworkCustomizer != null) {
                frameworkCustomizer.applyChangesInAWT();
            }
        }
    };

    private ActionListener listenerNonAWT = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            if (runCustomizer != null) {
                runCustomizer.applyChanges();
            }
            if (frameworkCustomizer != null) {
                frameworkCustomizer.applyChanges();
            }
        }
    };
}
