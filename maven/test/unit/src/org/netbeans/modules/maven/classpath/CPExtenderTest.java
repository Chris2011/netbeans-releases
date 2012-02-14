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

package org.netbeans.modules.maven.classpath;

import java.beans.Customizer;
import java.net.URL;
import java.util.Collections;
import java.util.logging.Level;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.classpath.ProjectClassPathModifier;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.RandomlyFails;
import org.netbeans.modules.maven.api.NbMavenProject;
import org.netbeans.modules.project.libraries.DefaultLibraryImplementation;
import org.netbeans.spi.project.libraries.LibraryImplementation;
import org.netbeans.spi.project.libraries.LibraryTypeProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.test.TestFileUtils;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

public class CPExtenderTest extends NbTestCase {

    public CPExtenderTest(String name) {
        super(name);
    }

    protected @Override void setUp() throws Exception {
        clearWorkDir();
    }

    @Override protected Level logLevel() {
        return Level.FINE;
    }

    @Override protected String logRoot() {
        return "org.netbeans.modules.maven";
    }

    @RandomlyFails // frequently fails in NB-Core-Build; [CPExtender] checkLibraryForPoms on Library[Stuff] -> true and [Utilities] WORKDIR/o.n.m.m.c.C/testAddRemovePomLib/pom.xml@1:2: CHILD_REMOVED:org.netbeans.modules.maven.model.pom.impl.ProjectImpl$PList@4 yet [Utilities] no changes in org.openide.loaders.XMLDataObject@c[WORKDIR/o.n.m.m.c.C/testAddRemovePomLib/pom.xml@1:2] where modified=true
    public void testAddRemovePomLib() throws Exception {
        Library lib = LibraryManager.getDefault().createLibrary("j2se", "Stuff", Collections.singletonMap("maven-pom", Collections.singletonList(new URL("http://repo1.maven.org/maven2/grp/stuff/1.0/stuff-1.0.pom"))));
        Library lib2 = LibraryManager.getDefault().createLibrary("j2se", "Stuff2", Collections.singletonMap("maven-pom", Collections.singletonList(new URL("http://repo1.maven.org/maven2/grp/stuff/2.0/stuff-2.0.pom"))));
        FileObject d = FileUtil.toFileObject(getWorkDir());
        TestFileUtils.writeFile(d, "pom.xml", "<project><modelVersion>4.0.0</modelVersion>"
                + "<groupId>test</groupId><artifactId>prj</artifactId>"
                + "<packaging>jar</packaging><version>1.0</version></project>");
        FileObject java = TestFileUtils.writeFile(d, "src/main/java/p/C.java", "package p; class C {}");
        Project p = ProjectManager.getDefault().findProject(d);
        NbMavenProject mp = p.getLookup().lookup(NbMavenProject.class);
        assertEquals("[]", mp.getMavenProject().getDependencies().toString());
        assertTrue(ProjectClassPathModifier.addLibraries(new Library[] {lib}, java, ClassPath.COMPILE));
        assertFalse(ProjectClassPathModifier.addLibraries(new Library[] {lib}, java, ClassPath.COMPILE));
        NbMavenProject.fireMavenProjectReload(p); // XXX why is this necessary?
        assertEquals("[Dependency {groupId=grp, artifactId=stuff, version=1.0, type=jar}]", mp.getMavenProject().getDependencies().toString());
        assertFalse(ProjectClassPathModifier.removeLibraries(new Library[] {lib2}, java, ClassPath.COMPILE));
        assertTrue(ProjectClassPathModifier.removeLibraries(new Library[] {lib}, java, ClassPath.COMPILE));
        assertFalse(ProjectClassPathModifier.removeLibraries(new Library[] {lib}, java, ClassPath.COMPILE));
        NbMavenProject.fireMavenProjectReload(p);
        assertEquals("[]", mp.getMavenProject().getDependencies().toString());
    }

    // XXX test adding & removing POM lib when <dependencyManagement> is in parent
    // XXX test adding & removing JARs (incl. lib w/o POM)
    // XXX test adding subprojects

    @ServiceProvider(service=LibraryTypeProvider.class, path=/*LibraryTypeRegistry.REGISTRY*/"org-netbeans-api-project-libraries/LibraryTypeProviders")
    public static class MockLibraryProvider implements LibraryTypeProvider {
        public @Override String getDisplayName() {
            return "test";
        }
        public @Override String getLibraryType() {
            return "j2se";
        }
        public @Override String[] getSupportedVolumeTypes() {
            return new String[] {"classpath", "maven-pom"};
        }
        public @Override LibraryImplementation createLibrary() {
            return new DefaultLibraryImplementation(getLibraryType(), getSupportedVolumeTypes());
        }
        public @Override void libraryDeleted(LibraryImplementation libraryImpl) {}
        public @Override void libraryCreated(LibraryImplementation libraryImpl) {}
        public @Override Customizer getCustomizer(String volumeType) {
            return null;
        }
        public @Override Lookup getLookup() {
            return Lookup.EMPTY;
        }
    }

}
