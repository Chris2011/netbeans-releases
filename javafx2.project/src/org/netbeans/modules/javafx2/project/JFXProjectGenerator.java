/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2011 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2010 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */
package org.netbeans.modules.javafx2.project;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.ant.AntBuildExtender;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.api.queries.FileEncodingQuery;
import org.netbeans.modules.java.api.common.project.ProjectProperties;
import org.netbeans.modules.javafx2.platform.api.JavaFXPlatformUtils;
import org.netbeans.spi.project.libraries.support.LibrariesSupport;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.ProjectGenerator;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.netbeans.spi.project.support.ant.ReferenceHelper;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.modules.SpecificationVersion;
import org.openide.util.Mutex;
import org.openide.util.MutexException;
import org.openide.ErrorManager;
import org.openide.filesystems.URLMapper;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.Parameters;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Creates a J2SEProject from scratch according to some initial configuration.
 * 
 * TODO use J2SEProjectBuider instead
 */
public class JFXProjectGenerator {

    private JFXProjectGenerator() {
    }

    /**
     * Create a new empty J2SE project.
     * @param dir the top-level directory (need not yet exist but if it does it must be empty)
     * @param name the name for the project
     * @param librariesDefinition project relative or absolute OS path to libraries definition; can be null
     * @return the helper object permitting it to be further customized
     * @throws IOException in case something went wrong
     */
    public static AntProjectHelper createProject(final File dir, final String name, final String mainClass,
            final String manifestFile, final String librariesDefinition, final String platformName) throws IOException {
        Parameters.notNull("dir", dir); //NOI18N
        Parameters.notNull("name", name);   //NOI18N
        final FileObject dirFO = FileUtil.createFolder(dir);
        // if manifestFile is null => it's TYPE_LIB
        final AntProjectHelper[] h = new AntProjectHelper[1];
        dirFO.getFileSystem().runAtomicAction(new FileSystem.AtomicAction() {
            @Override
            public void run() throws IOException {
                h[0] = createProject(dirFO, name, "src", "test", mainClass, manifestFile, manifestFile == null, librariesDefinition, platformName); //NOI18N
                final Project p = ProjectManager.getDefault().findProject(dirFO);
                createJfxExtension(p, dirFO);
                ProjectManager.getDefault().saveProject(p);
                final ReferenceHelper refHelper = getReferenceHelper(p);
                try {
                    ProjectManager.mutex().writeAccess(new Mutex.ExceptionAction<Void>() {
                        @Override
                        public Void run() throws Exception {
                            copyRequiredLibraries(h[0], refHelper);
                            return null;
                        }
                    });
                } catch (MutexException ex) {
                    Exceptions.printStackTrace(ex.getException());
                }
                FileObject srcFolder = dirFO.createFolder("src"); // NOI18N
                dirFO.createFolder("test"); // NOI18N
                if (mainClass != null) {
                    createMainClass(mainClass, srcFolder);
                }
            }
        });

        return h[0];
    }

    private static ReferenceHelper getReferenceHelper(Project p) {
        try {
            return (ReferenceHelper) p.getClass().getMethod("getReferenceHelper").invoke(p); // NOI18N
        } catch (Exception e) {
            return null;
        }
    }

    public static AntProjectHelper createProject(final File dir, final String name,
            final File[] sourceFolders, final File[] testFolders,
            final String manifestFile, final String librariesDefinition,
            final String buildXmlName, final String platformName) throws IOException {
        Parameters.notNull("dir", dir); //NOI18N
        Parameters.notNull("name", name);   //NOI8N
        Parameters.notNull("sourceFolders", sourceFolders); //NOI18N
        Parameters.notNull("testFolders", testFolders); //NOI18N
        final FileObject dirFO = FileUtil.createFolder(dir);
        final AntProjectHelper[] h = new AntProjectHelper[1];
        // this constructor creates only java application type
        dirFO.getFileSystem().runAtomicAction(new FileSystem.AtomicAction() {
            @Override
            public void run() throws IOException {
                h[0] = createProject(dirFO, name, null, null, null, manifestFile, false, librariesDefinition, platformName);
                final Project p = ProjectManager.getDefault().findProject(dirFO);
                final ReferenceHelper refHelper = getReferenceHelper(p);
                try {
                    ProjectManager.mutex().writeAccess(new Mutex.ExceptionAction<Void>() {
                        @Override
                        public Void run() throws Exception {
                            Element data = h[0].getPrimaryConfigurationData(true);
                            Document doc = data.getOwnerDocument();
                            NodeList nl = data.getElementsByTagNameNS(J2SEProjectType.PROJECT_CONFIGURATION_NAMESPACE, "source-roots"); // NOI18N
                            assert nl.getLength() == 1;
                            Element sourceRoots = (Element) nl.item(0);
                            nl = data.getElementsByTagNameNS(J2SEProjectType.PROJECT_CONFIGURATION_NAMESPACE, "test-roots");  //NOI18N
                            assert nl.getLength() == 1;
                            Element testRoots = (Element) nl.item(0);
                            for (int i = 0; i < sourceFolders.length; i++) {
                                String propName;
                                if (i == 0) {
                                    //Name the first src root src.dir to be compatible with NB 4.0
                                    propName = "src.dir";       //NOI18N
                                } else {
                                    String name = sourceFolders[i].getName();
                                    propName = name + ".dir";    //NOI18N
                                }

                                int rootIndex = 1;
                                EditableProperties props = h[0].getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
                                while (props.containsKey(propName)) {
                                    rootIndex++;
                                    propName = name + rootIndex + ".dir";   //NOI18N
                                }
                                String srcReference = refHelper.createForeignFileReference(sourceFolders[i], JavaProjectConstants.SOURCES_TYPE_JAVA);
                                Element root = doc.createElementNS(J2SEProjectType.PROJECT_CONFIGURATION_NAMESPACE, "root");   //NOI18N
                                root.setAttribute("id", propName);   //NOI18N
                                sourceRoots.appendChild(root);
                                props = h[0].getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
                                props.put(propName, srcReference);
                                h[0].putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, props); // #47609
                            }
                            for (int i = 0; i < testFolders.length; i++) {
                                if (!testFolders[i].exists()) {
                                    testFolders[i].mkdirs();
                                }
                                String propName;
                                if (i == 0) {
                                    //Name the first test root test.src.dir to be compatible with NB 4.0
                                    propName = "test.src.dir";  //NOI18N
                                } else {
                                    String name = testFolders[i].getName();
                                    propName = "test." + name + ".dir"; // NOI18N
                                }
                                int rootIndex = 1;
                                EditableProperties props = h[0].getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
                                while (props.containsKey(propName)) {
                                    rootIndex++;
                                    propName = "test." + name + rootIndex + ".dir"; // NOI18N
                                }
                                String testReference = refHelper.createForeignFileReference(testFolders[i], JavaProjectConstants.SOURCES_TYPE_JAVA);
                                Element root = doc.createElementNS(J2SEProjectType.PROJECT_CONFIGURATION_NAMESPACE, "root"); // NOI18N
                                root.setAttribute("id", propName); // NOI18N
                                testRoots.appendChild(root);
                                props = h[0].getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH); // #47609
                                props.put(propName, testReference);
                                h[0].putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, props);
                            }
                            h[0].putPrimaryConfigurationData(data, true);
                            if (buildXmlName != null) {
                                final EditableProperties props = h[0].getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
                                props.put(JFXProjectProperties.BUILD_SCRIPT, buildXmlName);
                                h[0].putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, props);
                            }
                            createJfxExtension(p, dirFO);
                            ProjectManager.getDefault().saveProject(p);
                            copyRequiredLibraries(h[0], refHelper);
                            ProjectUtils.getSources(p).getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
                            return null;
                        }
                    });
                } catch (MutexException me) {
                    ErrorManager.getDefault().notify(me);
                }
            }
        });
        return h[0];
    }

    private static void createJfxExtension(Project p, FileObject dirFO) throws IOException {
        //adding JavaFX buildscript extension
        FileObject templateFO = FileUtil.getConfigFile("Templates/JFX/jfx-impl.xml"); //NOI18N
        if (templateFO != null) {
            FileObject jfxBuildFile = FileUtil.copyFile(templateFO, dirFO.getFileObject("nbproject"), "jfx-impl"); // NOI18N
            AntBuildExtender extender = p.getLookup().lookup(AntBuildExtender.class);
            if (extender != null) {
                assert jfxBuildFile != null;
                if (extender.getExtension("jfx") == null) { // NOI18N
                    AntBuildExtender.Extension ext = extender.addExtension("jfx", jfxBuildFile); // NOI18N
                    ext.addDependency("-post-jar", "jfx-deployment"); //NOI18N 
                }
            }
        }
    }

    private static AntProjectHelper createProject(FileObject dirFO, String name,
            String srcRoot, String testRoot, String mainClass, String manifestFile,
            boolean isLibrary, String librariesDefinition, String platformName) throws IOException {
        AntProjectHelper h = ProjectGenerator.createProject(dirFO, J2SEProjectType.TYPE, librariesDefinition);
        Element data = h.getPrimaryConfigurationData(true);
        Document doc = data.getOwnerDocument();
        Element nameEl = doc.createElementNS(J2SEProjectType.PROJECT_CONFIGURATION_NAMESPACE, "name"); // NOI18N
        nameEl.appendChild(doc.createTextNode(name));
        data.appendChild(nameEl);
        EditableProperties ep = h.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
        Element sourceRoots = doc.createElementNS(J2SEProjectType.PROJECT_CONFIGURATION_NAMESPACE, "source-roots");  //NOI18N
        if (srcRoot != null) {
            Element root = doc.createElementNS(J2SEProjectType.PROJECT_CONFIGURATION_NAMESPACE, "root");   //NOI18N
            root.setAttribute("id", "src.dir");   //NOI18N
            sourceRoots.appendChild(root);
            ep.setProperty("src.dir", srcRoot); // NOI18N
        }
        data.appendChild(sourceRoots);
        Element testRoots = doc.createElementNS(J2SEProjectType.PROJECT_CONFIGURATION_NAMESPACE, "test-roots");  //NOI18N
        if (testRoot != null) {
            Element root = doc.createElementNS(J2SEProjectType.PROJECT_CONFIGURATION_NAMESPACE, "root");   //NOI18N
            root.setAttribute("id", "test.src.dir");   //NOI18N
            testRoots.appendChild(root);
            ep.setProperty("test.src.dir", testRoot); // NOI18N
        }
        data.appendChild(testRoots);
        h.putPrimaryConfigurationData(data, true);
        ep.setProperty(ProjectProperties.ANNOTATION_PROCESSING_ENABLED, "true"); // NOI18N
        ep.setProperty(ProjectProperties.ANNOTATION_PROCESSING_ENABLED_IN_EDITOR, "false"); // NOI18N
        ep.setProperty(ProjectProperties.ANNOTATION_PROCESSING_RUN_ALL_PROCESSORS, "true"); // NOI18N
        ep.setProperty(ProjectProperties.ANNOTATION_PROCESSING_PROCESSORS_LIST, ""); // NOI18N
        ep.setProperty(ProjectProperties.ANNOTATION_PROCESSING_SOURCE_OUTPUT, "${build.generated.sources.dir}/ap-source-output"); // NOI18N
        ep.setProperty(ProjectProperties.ANNOTATION_PROCESSING_PROCESSOR_OPTIONS, ""); // NOI18N
        
        ep.setProperty("dist.dir", "dist"); // NOI18N
        ep.setComment("dist.dir", new String[]{"# " + NbBundle.getMessage(JFXProjectGenerator.class, "COMMENT_dist.dir")}, false); // NOI18N
        ep.setProperty("dist.jar", "${dist.dir}/" + validatePropertyValue(name) + ".jar"); // NOI18N
        ep.setProperty(ProjectProperties.JAVAC_CLASSPATH, ""); // NOI18N
        ep.setProperty("application.vendor", System.getProperty("user.name", "User Name")); //NOI18N
        ep.setProperty("application.title", name); // NOI18N
        
        // FX-specific CLASSPATH stuff
//        ep.setProperty("endorsed.classpath", new String[]{ // NOI18N
//                    "${libs.JavaFX2Runtime.classpath}", // NOI18N
//                });
        ep.setProperty(JavaFXPlatformUtils.PROPERTY_JAVAFX_SDK, JavaFXPlatformUtils.getJavaFXSDKPath(platformName));
        ep.setProperty(JavaFXPlatformUtils.PROPERTY_JAVAFX_RUNTIME, JavaFXPlatformUtils.getJavaFXRuntimePath(platformName));
        ep.setProperty(ProjectProperties.ENDORSED_CLASSPATH, JavaFXPlatformUtils.getJavaFXClassPath()); // NOI18N

        ep.setProperty(JFXProjectProperties.RUN_APP_WIDTH, "800"); // NOI18N
        ep.setProperty(JFXProjectProperties.RUN_APP_HEIGHT, "600"); // NOI18N
        ep.setProperty(ProjectProperties.MAIN_CLASS, "com.javafx.main.Main"); // NOI18N
        ep.setComment(ProjectProperties.MAIN_CLASS, new String[]{"# " + NbBundle.getMessage(JFXProjectGenerator.class, "COMMENT_main.class")}, false); // NOI18N
                
        ep.setProperty(ProjectProperties.JAVAC_PROCESSORPATH, new String[]{"${javac.classpath}"}); // NOI18N
        ep.setProperty("javac.test.processorpath", new String[]{"${javac.test.classpath}"}); // NOI18N
        ep.setProperty("build.sysclasspath", "ignore"); // NOI18N
        ep.setComment("build.sysclasspath", new String[]{"# " + NbBundle.getMessage(JFXProjectGenerator.class, "COMMENT_build.sysclasspath")}, false); // NOI18N
        ep.setProperty(ProjectProperties.RUN_CLASSPATH, new String[]{ // NOI18N
                    "${javac.classpath}:", // NOI18N
                    "${build.classes.dir}", // NOI18N
                });
        ep.setProperty("debug.classpath", new String[]{ // NOI18N
                    "${run.classpath}", // NOI18N
                });
        ep.setComment("debug.classpath", new String[]{ // NOI18N
                    "# " + NbBundle.getMessage(JFXProjectGenerator.class, "COMMENT_debug.transport"), // NOI18N
                    "#debug.transport=dt_socket" // NOI18N
                }, false);
        ep.setProperty("jar.compress", "false"); // NOI18N
        if (!isLibrary) {
            ep.setProperty(JFXProjectProperties.MAIN_CLASS, mainClass == null ? "" : mainClass); // NOI18N
            ep.setComment(JFXProjectProperties.MAIN_CLASS, new String[]{"# " + NbBundle.getMessage(JFXProjectGenerator.class, "COMMENT_main.fxclass")}, false); // NOI18N
        }

        ep.setProperty("javac.compilerargs", ""); // NOI18N
        ep.setComment("javac.compilerargs", new String[]{ // NOI18N
                    "# " + NbBundle.getMessage(JFXProjectGenerator.class, "COMMENT_javac.compilerargs"), // NOI18N
                }, false);
        SpecificationVersion sourceLevel = getDefaultSourceLevel();
        ep.setProperty("javac.source", sourceLevel.toString()); // NOI18N
        ep.setProperty("javac.target", sourceLevel.toString()); // NOI18N
        ep.setProperty("javac.deprecation", "false"); // NOI18N
        ep.setProperty(ProjectProperties.JAVAC_TEST_CLASSPATH, new String[]{ // NOI18N
                    "${javac.classpath}:", // NOI18N
                    "${build.classes.dir}", // NOI18N
                });
        ep.setProperty(ProjectProperties.RUN_TEST_CLASSPATH, new String[]{ // NOI18N
                    "${javac.test.classpath}:", // NOI18N
                    "${build.test.classes.dir}", // NOI18N
                });
        ep.setProperty("debug.test.classpath", new String[]{ // NOI18N
                    "${run.test.classpath}", // NOI18N
                });

        ep.setProperty("build.generated.dir", "${build.dir}/generated"); // NOI18N
        ep.setProperty("meta.inf.dir", "${src.dir}/META-INF"); // NOI18N

        ep.setProperty(ProjectProperties.BUILD_DIR, "build"); // NOI18N
        ep.setComment(ProjectProperties.BUILD_DIR, new String[]{"# " + NbBundle.getMessage(JFXProjectGenerator.class, "COMMENT_build.dir")}, false); // NOI18N
        ep.setProperty(ProjectProperties.BUILD_CLASSES_DIR, "${build.dir}/classes"); // NOI18N
        ep.setProperty("build.generated.sources.dir", "${build.dir}/generated-sources"); // NOI18N
        ep.setProperty("build.test.classes.dir", "${build.dir}/test/classes"); // NOI18N
        ep.setProperty("build.test.results.dir", "${build.dir}/test/results"); // NOI18N
        ep.setProperty("build.classes.excludes", "**/*.java,**/*.form"); // NOI18N
        ep.setProperty("dist.javadoc.dir", "${dist.dir}/javadoc"); // NOI18N
        ep.setProperty("platform.active", platformName); // NOI18N

//        ep.setProperty(ProjectProperties.RUN_JVM_ARGS, "-Xbootclasspath/p:\"${libs.JavaFX2Runtime.classpath}\""); // NOI18N
//        ep.setComment(ProjectProperties.RUN_JVM_ARGS, new String[] {
//            "# " + NbBundle.getMessage(J2SEProjectGenerator.class, "COMMENT_run.jvmargs"), // NOI18N
//            "# " + NbBundle.getMessage(J2SEProjectGenerator.class, "COMMENT_run.jvmargs_2"), // NOI18N
//            "# " + NbBundle.getMessage(J2SEProjectGenerator.class, "COMMENT_run.jvmargs_3"), // NOI18N
//        }, false);

        ep.setProperty(JFXProjectProperties.JAVAFX_ENABLED, "true"); // NOI18N
        ep.setProperty("jnlp.enabled", "false"); // NOI18N
        ep.setProperty(ProjectProperties.COMPILE_ON_SAVE, "true"); // NOI18N
        ep.setProperty(ProjectProperties.COMPILE_ON_SAVE_UNSUPPORTED_PREFIX + ".javafx", "true"); // NOI18N

        ep.setProperty(JFXProjectProperties.JAVADOC_PRIVATE, "false"); // NOI18N
        ep.setProperty(JFXProjectProperties.JAVADOC_NO_TREE, "false"); // NOI18N
        ep.setProperty(JFXProjectProperties.JAVADOC_USE, "true"); // NOI18N
        ep.setProperty(JFXProjectProperties.JAVADOC_NO_NAVBAR, "false"); // NOI18N
        ep.setProperty(JFXProjectProperties.JAVADOC_NO_INDEX, "false"); // NOI18N
        ep.setProperty(JFXProjectProperties.JAVADOC_SPLIT_INDEX, "true"); // NOI18N
        ep.setProperty(JFXProjectProperties.JAVADOC_AUTHOR, "false"); // NOI18N
        ep.setProperty(JFXProjectProperties.JAVADOC_VERSION, "false"); // NOI18N
        ep.setProperty(JFXProjectProperties.JAVADOC_WINDOW_TITLE, ""); // NOI18N
        ep.setProperty(JFXProjectProperties.JAVADOC_ENCODING, "${" + JFXProjectProperties.SOURCE_ENCODING + "}"); // NOI18N
        ep.setProperty(JFXProjectProperties.JAVADOC_ADDITIONALPARAM, ""); // NOI18N
        Charset enc = FileEncodingQuery.getDefaultEncoding();
        ep.setProperty(JFXProjectProperties.SOURCE_ENCODING, enc.name());
        if (manifestFile != null) {
            ep.setProperty("manifest.file", manifestFile); // NOI18N
        }
        h.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, ep);
//        ep = h.getProperties(AntProjectHelper.PRIVATE_PROPERTIES_PATH);
//        h.putProperties(AntProjectHelper.PRIVATE_PROPERTIES_PATH, ep);
        logUsage();
        return h;
    }
    private static final String loggerName = "org.netbeans.ui.metrics.j2se"; // NOI18N
    private static final String loggerKey = "USG_PROJECT_CREATE_J2SE"; // NOI18N

    // http://wiki.netbeans.org/UsageLoggingSpecification
    private static void logUsage() {
        LogRecord logRecord = new LogRecord(Level.INFO, loggerKey);
        logRecord.setLoggerName(loggerName);
        //logRecord.setParameters(new Object[] {""}); // NOI18N
        Logger.getLogger(loggerName).log(logRecord);
    }

    private static void copyRequiredLibraries(AntProjectHelper h, ReferenceHelper rh) throws IOException {
        if (!h.isSharableProject()) {
            return;
        }
        if (rh.getProjectLibraryManager().getLibrary("junit") == null // NOI18N
                && LibraryManager.getDefault().getLibrary("junit") != null) { // NOI18N
            rh.copyLibrary(LibraryManager.getDefault().getLibrary("junit")); // NOI18N
        }
        if (rh.getProjectLibraryManager().getLibrary("junit_4") == null // NOI18N
                && LibraryManager.getDefault().getLibrary("junit_4") != null) { // NOI18N
            rh.copyLibrary(LibraryManager.getDefault().getLibrary("junit_4")); // NOI18N
        }
        if (rh.getProjectLibraryManager().getLibrary("CopyLibs") == null // NOI18N
                && LibraryManager.getDefault().getLibrary("CopyLibs") != null) { // NOI18N
            rh.copyLibrary(LibraryManager.getDefault().getLibrary("CopyLibs")); // NOI18N
        }
        if (rh.getProjectLibraryManager().getLibrary("JavaFX2Runtime") == null // NOI18N
                && LibraryManager.getDefault().getLibrary("JavaFX2Runtime") != null) { // NOI18N
            File mainPropertiesFile = h.resolveFile(h.getLibrariesLocation());
            referenceLibrary(LibraryManager.getDefault().getLibrary("JavaFX2Runtime"), mainPropertiesFile.toURI().toURL(), true); //NOI18N

        }
    }

    /** for jar uri this method returns path wihtin jar or null*/
    private static String getJarFolder(URI uri) {
        String u = uri.toString();
        int index = u.indexOf("!/"); //NOI18N
        if (index != -1 && index + 2 < u.length()) {
            return u.substring(index + 2);
        }
        return null;
    }

    /** append path to given jar root uri */
    private static URI appendJarFolder(URI u, String jarFolder) {
        try {
            if (u.isAbsolute()) {
                return new URI("jar:" + u.toString() + "!/" + (jarFolder == null ? "" : jarFolder.replace('\\', '/'))); // NOI18N
            } else {
                return new URI(u.toString() + "!/" + (jarFolder == null ? "" : jarFolder.replace('\\', '/'))); // NOI18N
            }
        } catch (URISyntaxException e) {
            throw new AssertionError(e);
        }
    }

    private static Library referenceLibrary(final Library lib, final URL location,
            final boolean generateLibraryUniqueName) throws IOException {
        final File libBaseFolder = new File(URI.create(location.toExternalForm())).getParentFile();
        final Map<String, List<URI>> content = new HashMap<String, List<URI>>();
        String[] volumes = LibrariesSupport.getLibraryTypeProvider(lib.getType()).getSupportedVolumeTypes();
        for (String volume : volumes) {
            List<URI> volumeContent = new ArrayList<URI>();
            for (URL origlibEntry : lib.getContent(volume)) {
                URL libEntry = origlibEntry;
                String jarFolder = null;
                if ("jar".equals(libEntry.getProtocol())) { // NOI18N
                    jarFolder = getJarFolder(URI.create(libEntry.toExternalForm()));
                    libEntry = FileUtil.getArchiveFile(libEntry);
                }
                FileObject libEntryFO = URLMapper.findFileObject(libEntry);
                if (libEntryFO == null) {
                    if (!"file".equals(libEntry.getProtocol()) && // NOI18N
                            !"nbinst".equals(libEntry.getProtocol())) { // NOI18N
                        Logger.getLogger(JFXProjectGenerator.class.getName()).info("referenceLibrary is ignoring entry " + libEntry); // NOI18N
                        //this is probably exclusively urls to maven poms.
                        continue;
                    } else {
                        Logger.getLogger(JFXProjectGenerator.class.getName()).warning("Library '" + lib.getDisplayName() + // NOI18N
                                "' contains entry (" + libEntry + ") which does not exist. This entry is ignored and will not be refernced from sharable libraries."); // NOI18N
                        continue;
                    }
                }
                URI u;
                String name = PropertyUtils.relativizeFile(libBaseFolder, FileUtil.toFile(libEntryFO));
                if (name == null) { // #198955
                    Logger.getLogger(JFXProjectGenerator.class.getName()).warning("Can not relativize file: " + libEntryFO.getPath()); // NOI18N
                    continue;
                }
                u = LibrariesSupport.convertFilePathToURI(name);
                if (FileUtil.isArchiveFile(libEntryFO)) {
                    u = appendJarFolder(u, jarFolder);
                }
                volumeContent.add(u);
            }
            content.put(volume, volumeContent);
        }
        final LibraryManager man = LibraryManager.forLocation(location);
        try {
            return ProjectManager.mutex().writeAccess(new Mutex.ExceptionAction<Library>() {
                @Override
                public Library run() throws IOException {
                    String name = lib.getName();
                    if (generateLibraryUniqueName) {
                        int index = 2;
                        while (man.getLibrary(name) != null) {
                            name = lib.getName() + "-" + index;
                            index++;
                        }
                    }
                    return man.createURILibrary(lib.getType(), name, content);
                }
            });
        } catch (MutexException ex) {
            throw (IOException) ex.getException();
        }
    }

    private static void createMainClass(String mainClassName, FileObject srcFolder) throws IOException {

        int lastDotIdx = mainClassName.lastIndexOf('.');
        String mName, pName;
        if (lastDotIdx == -1) {
            mName = mainClassName.trim();
            pName = null;
        } else {
            mName = mainClassName.substring(lastDotIdx + 1).trim();
            pName = mainClassName.substring(0, lastDotIdx).trim();
        }

        if (mName.length() == 0) {
            return;
        }

        FileObject mainTemplate = FileUtil.getConfigFile("Templates/Classes/FXMain.java"); // NOI18N

        if (mainTemplate == null) {
            return; // Don't know the template
        }

        DataObject mt = DataObject.find(mainTemplate);

        FileObject pkgFolder = srcFolder;
        if (pName != null) {
            String fName = pName.replace('.', '/'); // NOI18N
            pkgFolder = FileUtil.createFolder(srcFolder, fName);
        }
        DataFolder pDf = DataFolder.findFolder(pkgFolder);
        mt.createFromTemplate(pDf, mName);

    }
    //------------ Used by unit tests -------------------
    private static SpecificationVersion defaultSourceLevel;

    private static SpecificationVersion getDefaultSourceLevel() {
        if (defaultSourceLevel != null) {
            return defaultSourceLevel;
        } else {
            JavaPlatform defaultPlatform = JavaPlatformManager.getDefault().getDefaultPlatform();
            SpecificationVersion v = defaultPlatform.getSpecification().getVersion();
            if (v.equals(new SpecificationVersion("1.6")) || v.equals(new SpecificationVersion("1.7"))) { // NOI18N
                // #89131: these levels are not actually distinct from 1.5. - xxx not true, but may be acceptable to have 1.5 as default
                return new SpecificationVersion("1.5"); // NOI18N
            } else {
                return v;
            }
        }
    }
    private static final Pattern INVALID_NAME = Pattern.compile("[$/\\\\\\p{Cntrl}]");  //NOI18N

    private static String validatePropertyValue(String value) {
        final Matcher m = INVALID_NAME.matcher(value);
        if (m.find()) {
            value = m.replaceAll("_");  //NOI18N
        }
        return value;
    }

    /**
     * Unit test only method. Sets the default source level for tests
     * where the default platform is not available.
     * @param version the default source level set to project when it is created
     *
     */
    public static void setDefaultSourceLevel(SpecificationVersion version) {
        defaultSourceLevel = version;
    }
}
