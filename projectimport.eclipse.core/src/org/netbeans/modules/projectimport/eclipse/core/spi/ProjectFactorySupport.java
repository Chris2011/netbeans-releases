/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.projectimport.eclipse.core.spi;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.project.classpath.ProjectClassPathModifier;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ant.AntArtifact;
import org.netbeans.api.project.ant.AntArtifactQuery;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.api.queries.CollocationQuery;
import org.netbeans.modules.java.api.common.SourceRoots;
import org.netbeans.modules.java.api.common.util.CommonProjectUtils;
import org.netbeans.modules.projectimport.eclipse.core.ClassPathContainerResolver;
import org.netbeans.modules.projectimport.eclipse.core.EclipseProject;
import org.netbeans.modules.projectimport.eclipse.core.EclipseProjectReference;
import org.netbeans.modules.projectimport.eclipse.core.EclipseUtils;
import org.netbeans.spi.project.SubprojectProvider;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.netbeans.spi.project.support.ant.ReferenceHelper;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

/**
 * Misc helper methods for implementors of ProjectTypeFactory.
 * 
 */
public class ProjectFactorySupport {

    /** Logger for this class. */
    private static final Logger LOG =
            Logger.getLogger(ProjectFactorySupport.class.getName());

    /**
     * Default translation of eclipse classpath to netbeans classpath. Should
     * be useful for most of the project types.
     */
    public static void updateProjectClassPath(AntProjectHelper helper, ReferenceHelper refHelper, ProjectImportModel model, 
            List<String> importProblems) throws IOException {
        if (model.getEclipseSourceRoots().size() == 0) {
            importProblems.add(org.openide.util.NbBundle.getMessage(EclipseProject.class, "MSG_NoSourceRootsFound"));
            return;
        }
        FileObject sourceRoot = FileUtil.toFileObject(model.getEclipseSourceRootsAsFileArray()[0]);
        for (DotClassPathEntry entry : model.getEclipseClassPathEntries()) {
            addItemToClassPath(helper, refHelper, entry, model, importProblems, sourceRoot);
        }
    }

    /**
     * Convenience method for synchronization of projects metadata. Must be used together with {@link #calculateKey}.
     * @return newKey updated according to progress of update, that is if removal of an item failed then it will be kept in key
     *  and similarly for adding an item
     */
    public static String synchronizeProjectClassPath(Project project, AntProjectHelper helper, ReferenceHelper refHelper, ProjectImportModel model, 
            String oldKey, String newKey, List<String> importProblems) throws IOException {
        if (model.getEclipseSourceRoots().size() == 0) {
            importProblems.add(org.openide.util.NbBundle.getMessage(EclipseProject.class, "MSG_NoSourceRootsFound"));
            return oldKey;
        }
        // compare old and new key and add and remove items from classpath;
        FileObject sourceRoot = FileUtil.toFileObject(model.getEclipseSourceRootsAsFileArray()[0]);
        
        String resultingKey = newKey;
        
        // add new CP items:
        StringTokenizer st = new StringTokenizer(newKey, ";"); //NOI18N
        while (st.hasMoreTokens()) {
            String t = st.nextToken();
            if (t.startsWith("src") || t.startsWith("output") || t.startsWith("jre")) { //NOI18N
                continue;
            }
            if (!oldKey.contains(t)) {
                DotClassPathEntry entry = findEntryByEncodedValue(model.getEclipseClassPathEntries(), t);
                // TODO: items appended to the end of classpath; there is no API to control this apart from editting javac.classpath directly
                addItemToClassPath(helper, refHelper, entry, model, importProblems, sourceRoot);
                if (Boolean.FALSE.equals(entry.getImportSuccessful())) {
                    // adding of item failed: remove it from key so that it can be retried
                    resultingKey = resultingKey.replace(t+";", ""); //NOI18N
                }
            }
        }
        
        // remove removed CP items:
        st = new StringTokenizer(oldKey, ";"); //NOI18N
        while (st.hasMoreTokens()) {
            String t = st.nextToken();
            if (t.startsWith("src") || t.startsWith("output") || t.startsWith("jre")) { //NOI18N
                continue;
            }
            if (!newKey.contains(t)) {
                if (!removeOldItemFromClassPath(project, helper, t.substring(0, t.indexOf("=")), t.substring(t.indexOf("=")+1), importProblems, sourceRoot, model.getEclipseProjectFolder())) { //NOI18N
                    // removing of item failed: keep it in new key so that it can be retried
                    resultingKey += t+";"; //NOI18N
                }
            }
        }
        return resultingKey;
    }
    
    /**
     * TBD if this is really neede or not.
     */
    public static void updateSourceRootLabels(List<DotClassPathEntry> sources, SourceRoots roots) {
        URL[] rootURLs = roots.getRootURLs();
        String[] labels = new String[rootURLs.length];
        for (int i = 0; i < rootURLs.length; i++) {
            for (DotClassPathEntry e : sources) {
                String path;
                try {
                    path = new File(rootURLs[i].toURI()).getPath();
                } catch (URISyntaxException ex) {
                    LOG.info("cannot convert '"+rootURLs[i].toExternalForm()+"' to file: "+ex.toString()); //NOI18N
                    continue;
                }
                if (path.endsWith("/") || path.endsWith("\\")) { //NOI18N
                    path = path.substring(0, path.length()-1);
                }
                if (path.equals(e.getAbsolutePath())) {
                    labels[i] = e.getRawPath();
                    break;
                }
            }
        }
        roots.putRoots(rootURLs, labels);
    }
    
    /**
     * Convenience impl of key creation.
     */
    public static String calculateKey(ProjectImportModel model) {
        StringBuffer sb = new StringBuffer();
        List<DotClassPathEntry> all = new ArrayList<DotClassPathEntry>();
        all.addAll(model.getEclipseSourceRoots());
        all.addAll(model.getEclipseTestSourceRoots());
        all.addAll(model.getEclipseClassPathEntries());
        for (DotClassPathEntry entry : all) {
            if (entry.getImportSuccessful() != null && !entry.getImportSuccessful().booleanValue()) {
                continue;
            }
            String oneItem = encodeDotClassPathEntryToKey(entry);
            if (oneItem != null) {
                sb.append(oneItem);
                sb.append(";"); //NOI18N
            }
        }
        if (model.getJavaPlatform() != null) {
            sb.append("jre="+model.getJavaPlatform().getDisplayName()+";"); //NOI18N
        }
        if (model.getOutput() != null) {
            sb.append("output="+model.getOutput().getRawPath()+";"); //NOI18N
        }
        return sb.toString().replace("con=;", ""); // remove empty container entries //NOI18N
    }
    
    private static String encodeDotClassPathEntryToKey(DotClassPathEntry entry) {
        if (ClassPathContainerResolver.isJUnit(entry)) {
            // always ignore junit and never add it to key
            return null;
        }
        String value = getValueTag(entry);
        if (value == null || value.length() == 0) {
            return null;
        }
        return getKindTag(entry.getKind()) + "=" + value; //NOI18N
    }

    private static String getKindTag(DotClassPathEntry.Kind kind) {
        switch (kind) {
            case PROJECT:
                return "prj"; //NOI18N
            case LIBRARY:
                return "file"; //NOI18N
            case VARIABLE:
                return "var"; //NOI18N
            case CONTAINER:
                return "ant"; //NOI18N
            case OUTPUT:
                return "out"; //NOI18N
            case SOURCE:
            default:
                return "src"; //NOI18N
        }
    }

    private static String getValueTag(DotClassPathEntry entry) {
        switch (entry.getKind()) {
            case PROJECT:
                return entry.getRawPath().substring(1); // project name
            case VARIABLE:
                String v[] = EclipseUtils.splitVariable(entry.getRawPath());
                return PropertyUtils.getUsablePropertyName(v[0]) + v[1]; // variable name
            case CONTAINER:
                return entry.getContainerMapping(); // mapping as produced by container resolver
            case LIBRARY:
            case OUTPUT:
            case SOURCE:
            default:
                return entry.getRawPath(); // file path
        }
    }
    
    /**
     * Adds single DotClassPathEntry to NB project classpath.
     */
    private static boolean addItemToClassPath(AntProjectHelper helper, ReferenceHelper refHelper, DotClassPathEntry entry, 
            ProjectImportModel model, List<String> importProblems, FileObject sourceRoot) throws IOException {
        if (ClassPathContainerResolver.isJUnit(entry)) {
            if (model.getEclipseSourceRoots().size() == 1 && model.getEclipseTestSourceRoots().size() == 0) {
                // if project has one source root with mixed sources and tests it is mess but keep junit on CP
            } else {
                // ignore junit and do not add it to NB project compilation classpath
                return true;
            }
        }
        if (entry.getKind() == DotClassPathEntry.Kind.PROJECT) {
            Project requiredProject = null;
            // first try to find required project in list of already created projects.
            // if this is workspace import than required project must be there:
            for (Project p : model.getAlreadyImportedProjects()) {
                // XXX use of ProjectInformation.displayName here is very suspicious:
                if (entry.getRawPath().substring(1).equals(p.getLookup().lookup(ProjectInformation.class).getDisplayName())) {
                    requiredProject = p;
                    break;
                }
            }
            if (requiredProject == null) {
                // try to find project by its name in list of opened projects.
                // if this is single project import than project might have already been imported:
                for (Project p : OpenProjects.getDefault().getOpenProjects()) {
                    // XXX this will not work well in the case of synchronizing an existing import:
                    if (entry.getRawPath().substring(1).equals(p.getLookup().lookup(ProjectInformation.class).getDisplayName())) {
                        requiredProject = p;
                        break;
                    }
                }
            }
            if (requiredProject == null) {
                importProblems.add(org.openide.util.NbBundle.getMessage(EclipseProject.class, "MSG_RequiredProjectNotFound", entry.getRawPath().substring(1)));
                entry.setImportSuccessful(Boolean.FALSE);
                return false;
            }
            AntArtifact[] artifact = AntArtifactQuery.findArtifactsByType(requiredProject, JavaProjectConstants.ARTIFACT_TYPE_JAR);
            List<URI> elements = new ArrayList<URI>();
            for (AntArtifact art : artifact) {
                elements.addAll(Arrays.asList(art.getArtifactLocations()));
            }
            if (artifact.length == 0) {
                importProblems.add(org.openide.util.NbBundle.getMessage(EclipseProject.class, "MSG_RequiredProjectHasNoArtifacts", requiredProject.getProjectDirectory()));
                entry.setImportSuccessful(Boolean.FALSE);
                return false;
            } else {
                ProjectClassPathModifier.addAntArtifacts(artifact, elements.toArray(new URI[elements.size()]), sourceRoot, ClassPath.COMPILE);
            }
            EclipseProjectReference ref = EclipseProjectReference.read(requiredProject);
            if (ref != null) {
                EclipseProject ecprj = ref.getEclipseProject(false);
                if (ecprj != null) {
                    for (DotClassPathEntry transentry : ecprj.getClassPathEntries()) {
                        if (transentry.isExported()) {
                            boolean result = addItemToClassPath(helper, refHelper, transentry, model, importProblems, sourceRoot);
                            if (!result) {
                                importProblems.add(org.openide.util.NbBundle.getMessage(EclipseProject.class, "MSG_TransitiveExportCannotBeResolved", transentry.getRawPath()));
                                entry.setImportSuccessful(Boolean.FALSE);
                                return false;
                            }
                        }
                    }
                }
            }
            entry.setImportSuccessful(Boolean.TRUE);
        } else if (entry.getKind() == DotClassPathEntry.Kind.LIBRARY) {
            File f = new File(entry.getAbsolutePath());
            if (!f.exists()) {
                importProblems.add(org.openide.util.NbBundle.getMessage(EclipseProject.class, "MSG_MissingClasspathEntry", f.getPath()));
            }
            try {
                ProjectClassPathModifier.addRoots(new URL[]{FileUtil.urlForArchiveOrDir(f)}, sourceRoot, ClassPath.COMPILE);
                entry.setImportSuccessful(Boolean.TRUE);
            } catch (UnsupportedOperationException x) {
                // java.lang.UnsupportedOperationException: Project in .../JSPWiki of class org.netbeans.modules.autoproject.core.AutomaticProject has neither a ProjectClassPathModifierImplementation nor a ProjectClassPathExtender in its lookup
                //         at org.netbeans.api.java.project.classpath.ProjectClassPathModifier.findExtensible(ProjectClassPathModifier.java:360)
                //         at org.netbeans.api.java.project.classpath.ProjectClassPathModifier.addRoots(ProjectClassPathModifier.java:138)
                //         at org.netbeans.modules.projectimport.eclipse.core.spi.ProjectFactorySupport.addItemToClassPath(ProjectFactorySupport.java:323)
                //         at org.netbeans.modules.projectimport.eclipse.core.spi.ProjectFactorySupport.updateProjectClassPath(ProjectFactorySupport.java:98)
                //         at org.netbeans.modules.projectimport.eclipse.j2se.J2SEProjectFactory.createProject(J2SEProjectFactory.java:105)
                //         at org.netbeans.modules.projectimport.eclipse.core.Importer.importProject(Importer.java:197)
                importProblems.add(x.getMessage()); // XXX could do better
                entry.setImportSuccessful(false);
            }
            updateSourceAndJavadoc(helper, f, null, entry, false);
        } else if (entry.getKind() == DotClassPathEntry.Kind.VARIABLE) {
            // add property directly to Ant property
            String antProp = createFileReference(helper, refHelper, entry);
            addToBuildProperties(helper, "javac.classpath", antProp); //NOI18N
            testProperty(antProp, helper, importProblems);
            updateSourceAndJavadoc(helper, null, antProp, entry, false);
            entry.setImportSuccessful(Boolean.TRUE);
        } else if (entry.getKind() == DotClassPathEntry.Kind.CONTAINER) {
            String antProperty = entry.getContainerMapping();
            if (antProperty != null && antProperty.length() > 0) {
                // add property directly to Ant property
                String antProp = "${"+antProperty+"}"; //NOI18N
                addToBuildProperties(helper, "javac.classpath", antProp); //NOI18N
                testProperty(antProp, helper, importProblems);
                entry.setImportSuccessful(Boolean.TRUE);
            }
        }
        return true;
    }

    private static void testProperty(String property, AntProjectHelper helper, List<String> importProblems) {
        String value = helper.getStandardPropertyEvaluator().evaluate(property);
        if (value.contains("${")) { //NOI18N
            importProblems.add(org.openide.util.NbBundle.getMessage(EclipseProject.class, "MSG_CannotResolveClasspathEntry", property));
            return;
        }
        String paths[] = PropertyUtils.tokenizePath(value);
        for (String path : paths) {
            File f = new File(path);
            if (!f.exists()) {
                importProblems.add(org.openide.util.NbBundle.getMessage(EclipseProject.class, "MSG_ClasspathEntryDoesNotExist", path));
            }
        }
    }
    
    /**
     * Remove single classpath item (in encoded key form) from NB project classpath.
     */
    private static boolean removeOldItemFromClassPath(Project project, AntProjectHelper helper, 
            String encodedKind, String encodedValue, List<String> importProblems, FileObject sourceRoot, File eclipseProject) throws IOException {
        if ("prj".equals(encodedKind)) { // NOI18N
            SubprojectProvider subProjs = project.getLookup().lookup(SubprojectProvider.class);
            if (subProjs != null) {
                boolean found = false;
                for (Project p : subProjs.getSubprojects()) {
                    ProjectInformation info = p.getLookup().lookup(ProjectInformation.class);
                    if (info.getDisplayName().equals(encodedValue)) {
                        AntArtifact[] artifact = AntArtifactQuery.findArtifactsByType(p, JavaProjectConstants.ARTIFACT_TYPE_JAR);
                        List<URI> elements = new ArrayList<URI>();
                        for (AntArtifact art : artifact) {
                            elements.addAll(Arrays.asList(art.getArtifactLocations()));
                        }
                        boolean b = ProjectClassPathModifier.removeAntArtifacts(artifact, elements.toArray(new URI[elements.size()]), sourceRoot, ClassPath.COMPILE);
                        if (!b) {
                            importProblems.add(org.openide.util.NbBundle.getMessage(EclipseProject.class, "MSG_CannotRemoveReference", encodedValue));
                            return false;
                        }
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    importProblems.add(org.openide.util.NbBundle.getMessage(EclipseProject.class, "MSG_ReferenceToProjectNotFound", encodedValue));
                    return false;
                }
            } else {
                throw new IllegalStateException("project "+project.getProjectDirectory()+" does not have SubprojectProvider in its lookup"); // NOI18N
            }
        } else if ("file".equals(encodedKind)) { // NOI18N
            File f = new File(encodedValue);
            if (!f.isAbsolute()) {
                f = new File(eclipseProject, encodedValue);
            }
            updateSourceAndJavadoc(helper, f, null, null, true);
            boolean b = ProjectClassPathModifier.removeRoots(new URL[]{FileUtil.urlForArchiveOrDir(f)}, sourceRoot, ClassPath.COMPILE);
            if (!b) {
                importProblems.add(org.openide.util.NbBundle.getMessage(EclipseProject.class, "MSG_ReferenceNotRemoved", encodedValue));
                return false;
            }
        } else if ("var".equals(encodedKind)) { // NOI18N
            String v[] = EclipseUtils.splitVariable(encodedValue);
            String antProp = findFileReference(helper, "${var."+v[0]+"}"+v[1]);// NOI18N
            updateSourceAndJavadoc(helper, null, antProp, null, true);
            boolean b = removeFromBuildProperties(helper, "javac.classpath", antProp); // NOI18N
            if (!b) {
                importProblems.add(org.openide.util.NbBundle.getMessage(EclipseProject.class, "MSG_VariableReferenceNotRemoved", encodedValue));
                return false;
            }
        } else if ("ant".equals(encodedKind)) { // NOI18N
            boolean b = removeFromBuildProperties(helper, "javac.classpath", "${"+encodedValue+"}"); // NOI18N
            if (!b) {
                importProblems.add(org.openide.util.NbBundle.getMessage(EclipseProject.class, "MSG_ContainerReferenceNotRemoved", encodedValue));
                return false;
            }
        }
        return true;
    }

    private static String createFileReference(AntProjectHelper helper, ReferenceHelper refHelper, DotClassPathEntry entry) {
        String filePath = entry.getAbsolutePath();
        if (filePath == null) {
            filePath = entry.getRawPath();
        }
        // setup a file.reference.xxx property
        String ref = refHelper.createForeignFileReferenceAsIs(filePath, null);
        EditableProperties ep = helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
        // update value of file.reference.xxx to "var.xxx":
        ep.setProperty(CommonProjectUtils.getAntPropertyName(ref),
                ProjectFactorySupport.asAntVariable(entry));
        helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, ep);
        return ref;
    }
    
    private static String findFileReference(AntProjectHelper helper, String value) {
        for (Map.Entry<String, String> e : helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH).entrySet()) {
            if (value.equals(e.getValue())) {
                return "${"+e.getKey()+"}"; // NOI18N
            }
        }
        /* XXX can get here if user just deleted an unimported lib, e.g. ${var.APPLICATION_HOME_DIR}/lib/javaee.jar from JSPWiki:
        assert false : value + " " +helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
         */
        return value;
    }
    
    /**
     * Add given value to given classpath-like Ant property.
     */
    private static void addToBuildProperties(AntProjectHelper helper, String property, String valueToAppend) {
        EditableProperties ep = helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
        String cp = ep.getProperty(property);
        if (cp == null) {
            cp = ""; //NOI18N
        } else {
            cp += ":"; //NOI18N
        }
        cp += valueToAppend;
        String[] arr = PropertyUtils.tokenizePath(cp);
        for (int i = 0; i < arr.length - 1; i++) {
            arr[i] += ":"; //NOI18N
        }
        ep.setProperty(property, arr);
        helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, ep);
    }

    /**
     * Remove given value to given classpath-like Ant property.
     */
    private static boolean removeFromBuildProperties(AntProjectHelper helper, String property, String referenceToRemove) {
        boolean result = true;
        EditableProperties ep = helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
        String cp = ep.getProperty(property);
        String oldCp = cp;
        if (cp != null && referenceToRemove != null) {
            cp = cp.replace(referenceToRemove, ""); //NOI18N
        }
        if (cp.equals(oldCp)) {
            result = false;
        }
        String[] arr = PropertyUtils.tokenizePath(cp);
        for (int i = 0; i < arr.length - 1; i++) {
            arr[i] += ":"; // NOI18N
        }
        ep.setProperty(property, arr);
        if (referenceToRemove.startsWith("${file.reference.") && isLastReference(ep, CommonProjectUtils.getAntPropertyName(referenceToRemove))) { //NOI18N
            ep.remove(CommonProjectUtils.getAntPropertyName(referenceToRemove));
        }
        helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, ep);
        return result;
    }

    private static boolean isLastReference(EditableProperties ep, String referenceToRemove) {
        for (String value : ep.values()) {
            if (referenceToRemove.equals(value)) {
                return false;
            }
        }
        return true;
    }
    
    private static DotClassPathEntry findEntryByEncodedValue(List<DotClassPathEntry> eclipseClassPathEntries, String t) {
        for (DotClassPathEntry e : eclipseClassPathEntries) {
            if (t.equals(encodeDotClassPathEntryToKey(e))) {
                return e;
            }
        }
        throw new IllegalStateException("cannot find entry '"+t+"' in "+eclipseClassPathEntries); // NOI18N
    }

    /**
     * Converts VARIABLE classpath entry to Ant property, eg.
     * SOME_ROOT/lib/a.jar -> ${var.SOME_ROOT}/lib/a.jar
     */
    private static String asAntVariable(DotClassPathEntry entry) {
        if (entry.getKind() != DotClassPathEntry.Kind.VARIABLE) {
            throw new IllegalStateException("not a VARIABLE entry "+entry); // NOI18N
        }
        String s[] = EclipseUtils.splitVariable(entry.getRawPath());
        String varName = PropertyUtils.getUsablePropertyName(s[0]);
        return "${var."+varName+"}"+s[1]; // NOI18N
    }

    private static void updateSourceAndJavadoc(AntProjectHelper helper, File file, String antProp, DotClassPathEntry entry, boolean removal) {
        if (!removal &&
            entry.getProperty(DotClassPathEntry.ATTRIBUTE_JAVADOC) == null  && 
            entry.getProperty(DotClassPathEntry.ATTRIBUTE_SOURCEPATH) == null) {
            return;
        }
        EditableProperties ep = helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
        if (antProp == null) {
            antProp = findReferenceName(helper, ep, file);
            if (antProp == null) {
                return;
            }
        }
        antProp = CommonProjectUtils.getAntPropertyName(antProp);
        if (!antProp.startsWith("file.reference")) { // NOI18N
            return;
        }
        String refName = antProp.substring(15);
        if (removal) {
            ep.remove("javadoc.reference."+refName); // NOI18N
            ep.remove("source.reference."+refName); // NOI18N
        } else {
            if (entry.getProperty(DotClassPathEntry.ATTRIBUTE_JAVADOC) != null) {
                ep.put("javadoc.reference."+refName, makeRelative(helper, entry.getProperty(DotClassPathEntry.ATTRIBUTE_JAVADOC))); // NOI18N
            }
            if (entry.getProperty(DotClassPathEntry.ATTRIBUTE_SOURCEPATH) != null) {
                ep.put("source.reference."+refName, makeRelative(helper, entry.getProperty(DotClassPathEntry.ATTRIBUTE_SOURCEPATH))); // NOI18N
            }
        }
        helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, ep);
    }

    private static String findReferenceName(AntProjectHelper helper, EditableProperties ep, File file) {
        for (Map.Entry<String, String> entry : ep.entrySet()) {
            if (!entry.getKey().startsWith("file.reference.")) { // NOI18N
                continue;
            }
            String value = entry.getValue();
            if (value == null) {
                continue;
            }
            File f = helper.resolveFile(value);
            if (file.equals(f)) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    private static String makeRelative(AntProjectHelper helper, String path) {
        File f = new File(path);
        if (!f.isAbsolute()) {
            return path;
        }
        File proj = FileUtil.toFile(helper.getProjectDirectory());
        if (CollocationQuery.areCollocated(f, proj)) {
            String relativePath = PropertyUtils.relativizeFile(proj, f);
            if (relativePath != null) {
                return relativePath;
            }
        }
        return path;
    }

    public static void setupSourceExcludes(AntProjectHelper helper, ProjectImportModel model, List<String> importProblems) {
        StringBuffer excludes = new StringBuffer();
        StringBuffer includes = new StringBuffer();
        int numberOfSourceRootsWithExcludes = 0;
        int numberOfSourceRootsWithIncludes = 0;
        for (DotClassPathEntry entry : model.getEclipseSourceRoots()) {
            String s = entry.getProperty(DotClassPathEntry.ATTRIBUTE_SOURCE_EXCLUDES);
            if (s != null) {
                if (s.contains("**/*.java")) { // NOI18N
                    // ignore this exclude: merging it would result in hiding all sources
                    // which is not desirable; this exclude seems to be frequently used 
                    // in .classpath files generated from Maven pom.xml - it is used on folders
                    // keeping resources (e.g. src/main/resources)
                    importProblems.add(org.openide.util.NbBundle.getMessage(EclipseProject.class, "MSG_ExcludesWarning1"));
                    continue;
                }
                if (excludes.length() > 0) {
                    excludes.append(","); // NOI18N
                }
                excludes.append(s.replace('|', ',')); // NOI18N
                numberOfSourceRootsWithExcludes++;
            }
            s = entry.getProperty(DotClassPathEntry.ATTRIBUTE_SOURCE_INCLUDES);
            if (s != null) {
                if (includes.length() > 0) {
                    includes.append(","); // NOI18N
                }
                includes.append(s.replace('|', ',')); // NOI18N
                numberOfSourceRootsWithIncludes++;
            }
        }
        if (numberOfSourceRootsWithExcludes > 1 || numberOfSourceRootsWithIncludes > 1) {
            importProblems.add(org.openide.util.NbBundle.getMessage(EclipseProject.class, "MSG_ExcludesWarning2"));
        }
        EditableProperties ep = helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
        boolean changed = false;
        if (excludes.length() > 0) {
            changed = true;
            ep.setProperty("excludes", excludes.toString()); // NOI18N
        }
        if (includes.length() > 0) {
            changed = true;
            ep.setProperty("includes", includes.toString()); // NOI18N
        }
        if (changed) {
            helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, ep);
        }
    }

    /**
     * Checks whether (potentially) external source roots are already owned by another project.
     * If they are, the import cannot proceed - the imported project would not be treated as
     * the new owner of the source roots, and so could not work with them (e.g. to set their CP).
     * Internal source roots beneath the project directory do not pose this problem, as
     * {@link FileOwnerQuery} should consider the new project to be the default owner of anything
     * underneath it.
     * @param model a model of the project being considered for import
     * @param nbProjectDir the proposed directory to use as the NetBeans {@linkplain Project#getProjectDirectory project directory}
     * @param importProblems problems to append to in case this returns true
     * @return true if the import would be blocked by ownership issues; false normally
     */
    public static boolean areSourceRootsOwned(ProjectImportModel model, File nbProjectDir, List<String> importProblems) {
        for (File sourceRootFile : model.getEclipseSourceRootsAsFileArray()) {
            if (sourceRootFile.getAbsolutePath().startsWith(nbProjectDir.getAbsolutePath())) {
                continue;
            }
            FileObject fo = FileUtil.toFileObject(sourceRootFile);
            Project p = FileOwnerQuery.getOwner(fo);
            if (p != null) {
                importProblems.add(NbBundle.getMessage(EclipseProject.class, "MSG_SourceRootOwned", // NOI18N
                        model.getProjectName(), sourceRootFile.getPath(),
                        FileUtil.getFileDisplayName(p.getProjectDirectory())));
                return true;
            }
        }
        return false;
    }
    
}
