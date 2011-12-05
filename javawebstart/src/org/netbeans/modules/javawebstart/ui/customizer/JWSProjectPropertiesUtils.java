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
package org.netbeans.modules.javawebstart.ui.customizer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ant.AntBuildExtender;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.modules.java.j2seproject.api.J2SEProjectConfigurations;
import org.netbeans.modules.java.j2seproject.api.J2SEPropertyEvaluator;
import org.netbeans.spi.project.ProjectConfiguration;
import org.netbeans.spi.project.ProjectConfigurationProvider;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.GeneratedFilesHelper;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.openide.cookies.CloseCookie;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.Mutex;
import org.openide.util.MutexException;
import org.openide.util.NbBundle;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Tomas Zezula
 * @author Petr Somol
 */
public final class JWSProjectPropertiesUtils {

    private static final String MASTER_NAME_APPLICATION = "master-application.jnlp"; // NOI18N
    private static final String MASTER_NAME_APPLET = "master-applet.jnlp"; // NOI18N
    private static final String MASTER_NAME_COMPONENT = "master-component.jnlp"; // NOI18N

    private static final String PREVIEW_NAME_APPLICATION = "preview-application.html"; // NOI18N
    private static final String PREVIEW_NAME_APPLET = "preview-applet.html"; // NOI18N

    private static final String JWS_ANT_TASKS_LIB_NAME = "JWSAntTasks"; // NOI18N
    private static final String BUILD_TEMPLATE = "Templates/JWS/jnlp-impl.xml"; //NOI18N

    private static final Logger LOG = Logger.getLogger(JWSProjectPropertiesUtils.class.getName());

    private static volatile String currentJnlpImplCRCCache;

    private JWSProjectPropertiesUtils() {
    }

    public static void updateMasterFiles(JWSProjectProperties props, Project proj) {
        try {
            if (props.isJWSEnabled()) {
                // test if the file already exists, if so do not generate, just set as active
                J2SEProjectConfigurations.createConfigurationFiles(proj, "JWS_generated", prepareSharedProps(), null /*or new Properties()*/); // NOI18N
                // create master file according to properties
                FileObject projDirFO = proj.getProjectDirectory();
                JWSProjectProperties.DescType descType = props.getDescTypeProp();
                if (JWSProjectProperties.DescType.application.equals(descType)) {
                    FileObject masterFO = projDirFO.getFileObject(MASTER_NAME_APPLICATION);
                    if (masterFO == null || !masterFO.isValid()) {
                        createMasterFile(projDirFO, MASTER_NAME_APPLICATION, descType);
                    }
                    FileObject previewFO = projDirFO.getFileObject(PREVIEW_NAME_APPLICATION);
                    if (previewFO == null || !previewFO.isValid()) {
                        createPreviewFile(projDirFO, PREVIEW_NAME_APPLICATION, descType);
                    }
                } else if (JWSProjectProperties.DescType.applet.equals(descType)) {
                    FileObject masterFO = projDirFO.getFileObject(MASTER_NAME_APPLET);
                    if (masterFO == null || !masterFO.isValid()) {
                        createMasterFile(projDirFO, MASTER_NAME_APPLET, descType);
                    }
                    FileObject previewFO = projDirFO.getFileObject(PREVIEW_NAME_APPLET);
                    if (previewFO == null || !previewFO.isValid()) {
                        createPreviewFile(projDirFO, PREVIEW_NAME_APPLET, descType);
                    }
                } else if (JWSProjectProperties.DescType.component.equals(descType)) {
                    FileObject masterFO = projDirFO.getFileObject(MASTER_NAME_COMPONENT);
                    if (masterFO == null || !masterFO.isValid()) {
                        createMasterFile(projDirFO, MASTER_NAME_COMPONENT, descType);
                    }
                }
            }
        } catch (IOException ioe) {
            Exceptions.printStackTrace(ioe);
        }
    }

    private static EditableProperties prepareSharedProps() {
        EditableProperties props = new EditableProperties(true);
        props.setProperty(JWSProjectProperties.CONFIG_LABEL_PROPNAME, NbBundle.getMessage(JWSCompositeCategoryProvider.class, "LBL_Category_WebStart")); //NOI18N
        props.setProperty(JWSProjectProperties.CONFIG_TARGET_RUN_PROPNAME, JWSProjectProperties.CONFIG_TARGET_RUN);
        props.setProperty(JWSProjectProperties.CONFIG_TARGET_DEBUG_PROPNAME, JWSProjectProperties.CONFIG_TARGET_DEBUG);
        props.setProperty(JWSProjectProperties.COS_UNSUPPORTED_PROPNAME, "true"); // NOI18N
        return props;
    }

    private static void createMasterFile(FileObject prjDir, String flName, JWSProjectProperties.DescType desc) throws IOException {
        FileObject masterFile = prjDir.createData(flName);
        FileLock lock = masterFile.lock();
        try {
            OutputStream os = masterFile.getOutputStream(lock);
            PrintWriter writer = new PrintWriter(os);
            writer.println("<jnlp spec=\"1.0+\" codebase=\"${jnlp.codebase}\" href=\"launch.jnlp\">"); // NOI18N
            writer.println("    <information>"); // NOI18N
            writer.println("        <title>${APPLICATION.TITLE}</title>"); // NOI18N
            writer.println("        <vendor>${APPLICATION.VENDOR}</vendor>"); // NOI18N
            writer.println("        <homepage href=\"${APPLICATION.HOMEPAGE}\"/>"); // NOI18N
            writer.println("        <description>${APPLICATION.DESC}</description>"); // NOI18N
            writer.println("        <description kind=\"short\">${APPLICATION.DESC.SHORT}</description>"); // NOI18N
            writer.println("        <!--${JNLP.ICONS}-->");  // NOI18N
            writer.println("        <!--${JNLP.OFFLINE.ALLOWED}-->"); // NOI18N
            writer.println("    </information>"); // NOI18N
            writer.println("    <!--${JNLP.UPDATE}-->"); // NOI18N
            writer.println("    <!--${JNLP.SECURITY}-->"); // NOI18N
            writer.println("    <resources>"); // NOI18N
            writer.println("        <!--${JNLP.RESOURCES.RUNTIME}-->"); // NOI18N
            writer.println("        <!--${JNLP.RESOURCES.MAIN.JAR}-->"); // NOI18N
            writer.println("        <!--${JNLP.RESOURCES.JARS}-->"); // NOI18N
            writer.println("        <!--${JNLP.RESOURCES.EXTENSIONS}-->"); // NOI18N
            writer.println("    </resources>"); // NOI18N
            // type of descriptor
            if (desc.equals(JWSProjectProperties.DescType.application)) {
                writer.println("    <application-desc main-class=\"${jnlp.main.class}\">"); // NOI18N
                writer.println("        <!--${JNLP.APPLICATION.ARGS}-->"); // NOI18N
                writer.println("    </application-desc>"); // NOI18N
            } else if (desc.equals(JWSProjectProperties.DescType.applet)) {
                writer.println("    <applet-desc main-class=\"${jnlp.main.class}\" name=\"${APPLICATION.TITLE}\"\n" +  // NOI18N
                    "        width=\"${jnlp.applet.width}\" height=\"${jnlp.applet.height}\">"); // NOI18N
                writer.println("        <!--${JNLP.APPLET.PARAMS}-->"); // NOI18N
                writer.println("    </applet-desc>"); // NOI18N
            } else if (desc.equals(JWSProjectProperties.DescType.component)) {
                writer.println("    <component-desc/>"); // NOI18N
            }
            writer.println("</jnlp>"); // NOI18N
            writer.flush();
            writer.close();
            os.close();
        } finally {
            lock.releaseLock();
        }
    }

    private static void createPreviewFile(FileObject prjDir, String flName, JWSProjectProperties.DescType descType) throws IOException {
        FileObject previewFile = prjDir.createData(flName);
        FileLock lock = previewFile.lock();
        try {
            OutputStream os = previewFile.getOutputStream(lock);
            PrintWriter writer = new PrintWriter(os);
            writer.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">"); // NOI18N
            if (JWSProjectProperties.DescType.applet.equals(descType)) {
                writer.println("<!-- ########################## IMPORTANT NOTE ############################ -->"); // NOI18N
                writer.println("<!-- This preview HTML page will work only with JDK 6 update 10 and higher! -->"); // NOI18N
                writer.println("<!-- ###################################################################### -->"); // NOI18N
            }
            writer.println("<html>"); // NOI18N
            writer.println("    <head>"); // NOI18N
            if (JWSProjectProperties.DescType.applet.equals(descType)) {
                writer.println("        <title>Test page for launching the applet via JNLP</title>"); // NOI18N
            } else if (JWSProjectProperties.DescType.application.equals(descType)) {
                writer.println("        <title>Test page for launching the application via JNLP</title>"); // NOI18N
            }
            writer.println("    </head>"); // NOI18N
            writer.println("    <body>"); // NOI18N
            if (JWSProjectProperties.DescType.applet.equals(descType)) {
                writer.println("        <h3>Test page for launching the applet via JNLP</h3>"); // NOI18N
                writer.println("        <script src=\"http://java.com/js/deployJava.js\"></script>"); // NOI18N
                writer.println("        <script>"); // NOI18N
                writer.println("            var attributes = {"); // NOI18N
                writer.println("                code:       \"${JNLP.APPLET.CLASS}\","); // NOI18N
                writer.println("                archive:    \"${JNLP.RESOURCES.MAIN.JAR}\","); // NOI18N
                writer.println("                width:      ${JNLP.APPLET.WIDTH},"); // NOI18N
                writer.println("                height:     ${JNLP.APPLET.HEIGHT}"); // NOI18N
                writer.println("            };"); // NOI18N
                writer.println("            var parameters = {${JNLP.APPLET.PARAMS}}; <!-- Applet Parameters -->"); // NOI18N
                writer.println("            var version = \"${JNLP_VM_VERSION}\"; <!-- Required Java Version -->"); // NOI18N
                writer.println("            deployJava.runApplet(attributes, parameters, version);"); // NOI18N
                writer.println("        </script>"); // NOI18N
                writer.println("        <!-- Or use the following applet element to launch the applet using jnlp_href -->"); // NOI18N
                writer.println("        <!--"); // NOI18N
                writer.println("        <applet width=\"${JNLP.APPLET.WIDTH}\" height=\"${JNLP.APPLET.HEIGHT}\">"); // NOI18N
                writer.println("            <param name=\"jnlp_href\" value=\"${JNLP.FILE}\"/>"); // NOI18N
                writer.println("        </applet>"); // NOI18N
                writer.println("        -->"); // NOI18N

            } else if (JWSProjectProperties.DescType.application.equals(descType)) {
                writer.println("        <h3>Test page for launching the application via JNLP</h3>"); // NOI18N
                writer.println("        <script src=\"http://java.com/js/deployJava.js\"></script>"); // NOI18N
                writer.println("        <script>"); // NOI18N
                writer.println("            deployJava.createWebStartLaunchButton(\"${JNLP.FILE}\")"); // NOI18N
                writer.println("        </script>"); // NOI18N
                writer.println("        <!-- Or use the following link element to launch with the application -->"); // NOI18N
                writer.println("        <!--"); // NOI18N
                writer.println("        <a href=\"${JNLP.FILE}\">Launch the application</a>"); // NOI18N
                writer.println("        -->"); // NOI18N
            }
            writer.println("    </body>"); // NOI18N
            writer.println("</html>"); // NOI18N
            writer.flush();
            writer.close();
            os.close();
        } finally {
            lock.releaseLock();
        }
    }

    public static void savePropsAndUpdateMetaFiles(JWSProjectProperties props, Project proj) {
        try {
            props.store();
        } catch (IOException ioe) {
            Exceptions.printStackTrace(ioe);
        }
        final ProjectConfigurationProvider<?> configProvider =
                proj.getLookup().lookup(ProjectConfigurationProvider.class);
        try {
            if (props.isJWSEnabled()) {
                setActiveConfig(configProvider, NbBundle.getMessage(JWSCompositeCategoryProvider.class, "LBL_Category_WebStart")); // NOI18N
                copyTemplate(proj);
                modifyBuildXml(proj);
                copyJWSAntTasksLibrary(proj);
            } else {
                setActiveConfig(configProvider, NbBundle.getMessage(JWSCompositeCategoryProvider.class, "LBL_Category_Default")); // NOI18N
            }
        } catch (IOException ioe) {
            Exceptions.printStackTrace(ioe);
        }
    }

    private static <C extends ProjectConfiguration> void setActiveConfig(final ProjectConfigurationProvider<C> provider, String displayName) throws IOException {
        Collection<C> configs = provider.getConfigurations();
        for (final C c : configs) {
            if (displayName.equals(c.getDisplayName())) {
                try {
                    ProjectManager.mutex().writeAccess(new Mutex.ExceptionAction<Void>() {
                        @Override
                        public Void run() throws Exception {
                            provider.setActiveConfiguration(c);
                            return null;
                        }
                    });
                } catch (MutexException mex) {
                    throw (IOException) mex.getException();
                }
            }
        }
    }

    private static void copyTemplate(Project proj) throws IOException {
        FileObject projDir = proj.getProjectDirectory();
        FileObject jnlpBuildFile = projDir.getFileObject("nbproject/jnlp-impl.xml"); // NOI18N

        if (jnlpBuildFile != null && !isJnlpImplCurrentVer(computeCrc32(jnlpBuildFile.getInputStream()))) {
            // try to close the file just in case the file is already opened in editor
            DataObject dobj = DataObject.find(jnlpBuildFile);
            CloseCookie closeCookie = dobj.getLookup().lookup(CloseCookie.class);
            if (closeCookie != null) {
                closeCookie.close();
            }
            final FileObject nbproject = projDir.getFileObject("nbproject");                    //NOI18N
            final FileObject backupFile = nbproject.getFileObject("jnlp-impl_backup", "xml");   //NOI18N
            if (backupFile != null) {
                backupFile.delete();
            }
            FileUtil.moveFile(jnlpBuildFile, nbproject, "jnlp-impl_backup");                    //NOI18N
            jnlpBuildFile = null;
        }

        if (jnlpBuildFile == null) {
            FileObject templateFO = FileUtil.getConfigFile(BUILD_TEMPLATE);
            if (templateFO != null) {
                FileUtil.copyFile(templateFO, projDir.getFileObject("nbproject"), "jnlp-impl"); // NOI18N
            }
        }
    }

    private static void modifyBuildXml(Project proj) throws IOException {
        FileObject projDir = proj.getProjectDirectory();
        final FileObject buildXmlFO = getBuildXml(proj);
        if (buildXmlFO == null) {
            LOG.warning("The project build script does not exist, the project cannot be extended by JWS.");     //NOI18N
            return;
        }
        Document xmlDoc = null;
        try {
            xmlDoc = XMLUtil.parse(new InputSource(buildXmlFO.getURL().toExternalForm()), false, true, null, null);
        } catch (SAXException ex) {
            Exceptions.printStackTrace(ex);
        }
        FileObject jnlpBuildFile = projDir.getFileObject("nbproject/jnlp-impl.xml"); // NOI18N
        AntBuildExtender extender = proj.getLookup().lookup(AntBuildExtender.class);
        if (extender != null) {
            assert jnlpBuildFile != null;
            if (extender.getExtension("jws") == null) { // NOI18N
                AntBuildExtender.Extension ext = extender.addExtension("jws", jnlpBuildFile); // NOI18N
                ext.addDependency("jar", "jnlp"); // NOI18N
                ext.addDependency("-post-jar", "jnlp"); //NOI18N
            }
            ProjectManager.getDefault().saveProject(proj);
        } else {
            LOG.log(Level.INFO,
                    "Trying to include JWS build snippet in project type that doesn't support AntBuildExtender API contract."); // NOI18N
        }

        //TODO this piece shall not proceed when the upgrade to j2se-project/4 was cancelled.
        //how to figure..
        Element docElem = xmlDoc.getDocumentElement();
        NodeList nl = docElem.getElementsByTagName("target"); // NOI18N
        Element target = null;
        for (int i = 0; i < nl.getLength(); i++) {
            Element e = (Element) nl.item(i);
            if (e.getAttribute("name") != null && "-post-jar".equals(e.getAttribute("name"))) { // NOI18N
                target = e;
                break;
            }
        }
        boolean changed = false;
        if (target != null) {
            if ((target.getAttribute("depends") != null && target.getAttribute("depends").contains("jnlp"))) { // NOI18N
                String old = target.getAttribute("depends"); // NOI18N
                old = old.replaceAll("jnlp", ""); // NOI18N
                old = old.replaceAll(",[\\s]*$", ""); // NOI18N
                old = old.replaceAll("^[\\s]*,", ""); // NOI18N
                old = old.replaceAll(",[\\s]*,", ","); // NOI18N
                old = old.trim();
                if (old.length() == 0) {
                    target.removeAttribute("depends"); // NOI18N
                } else {
                    target.setAttribute("depends", old); // NOI18N
                }
                changed = true;
            }
        }
        nl = docElem.getElementsByTagName("import"); // NOI18N
        for (int i = 0; i < nl.getLength(); i++) {
            Element e = (Element) nl.item(i);
            if (e.getAttribute("file") != null && "nbproject/jnlp-impl.xml".equals(e.getAttribute("file"))) { // NOI18N
                e.getParentNode().removeChild(e);
                changed = true;
                break;
            }
        }

        if (changed) {
            final Document fdoc = xmlDoc;
            try {
                ProjectManager.mutex().writeAccess(new Mutex.ExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        FileLock lock = buildXmlFO.lock();
                        try {
                            OutputStream os = buildXmlFO.getOutputStream(lock);
                            try {
                                XMLUtil.write(fdoc, os, "UTF-8"); // NOI18N
                            } finally {
                                os.close();
                            }
                        } finally {
                            lock.releaseLock();
                        }
                        return null;
                    }
                });
            } catch (MutexException mex) {
                throw (IOException) mex.getException();
            }
        }
    }

    private static void copyJWSAntTasksLibrary(Project proj) throws IOException {
        AntBuildExtender extender = proj.getLookup().lookup(AntBuildExtender.class);
        if (extender != null) {
            LibraryManager.getDefault();
            extender.addLibrary(LibraryManager.getDefault().getLibrary(JWS_ANT_TASKS_LIB_NAME));
            ProjectManager.getDefault().saveProject(proj);
        }
    }

    private static FileObject getBuildXml(final Project prj) {
        final J2SEPropertyEvaluator j2sepe = prj.getLookup().lookup(J2SEPropertyEvaluator.class);
        assert j2sepe != null;
        final PropertyEvaluator eval = j2sepe.evaluator();
        String buildScriptPath = eval.getProperty(JWSProjectProperties.BUILD_SCRIPT);
        if (buildScriptPath == null) {
            buildScriptPath = GeneratedFilesHelper.BUILD_XML_PATH;
        }
        return prj.getProjectDirectory().getFileObject (buildScriptPath);
    }

    static String computeCrc32(InputStream is) throws IOException {
        Checksum crc = new CRC32();
        int last = -1;
        int curr;
        while ((curr = is.read()) != -1) {
            if (curr != '\n' && last == '\r') {
                crc.update('\n');
            }
            if (curr != '\r') {
                crc.update(curr);
            }
            last = curr;
        }
        if (last == '\r') {
            crc.update('\n');
        }
        int val = (int)crc.getValue();
        String hex = Integer.toHexString(val);
        while (hex.length() < 8) {
            hex = "0" + hex; // NOI18N
        }
        return hex;
    }

    static boolean isJnlpImplCurrentVer(String crc) throws IOException {
        String _currentJnlpImplCRC = currentJnlpImplCRCCache;
        if (_currentJnlpImplCRC == null) {
            final FileObject template = FileUtil.getConfigFile(BUILD_TEMPLATE);
            final InputStream in = template.getInputStream();
            try {
                currentJnlpImplCRCCache = _currentJnlpImplCRC = computeCrc32(in);
            } finally {
                in.close();
            }
        }
        return _currentJnlpImplCRC.equals(crc);
    }

}
