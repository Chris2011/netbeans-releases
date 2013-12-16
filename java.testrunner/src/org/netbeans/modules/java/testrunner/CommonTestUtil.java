/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
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

package org.netbeans.modules.java.testrunner;

import java.net.URL;
import java.util.*;
import org.netbeans.api.java.queries.UnitTestForSourceQuery;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.modules.gsf.testrunner.plugin.CommonPlugin.CreateTestParam;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;

/**
 *
 * @author  rmatous
 * @author  Marian Petras
 * @version 1.1
 */
public class CommonTestUtil {
    static private final String JAVA_SOURCES_SUFFIX = "java"; //NOI18N

    /**
     */
    public static boolean isJavaFile(FileObject fileObj) {
        return "java".equals(fileObj.getExt())                          //NOI18N
               || "text/x-java".equals(FileUtil.getMIMEType(fileObj));  //NOI18N
    }
    
    static public FileObject getFileObjectFromNode(Node node) {
        DataObject dO;
        DataFolder df;

        dO = node.getLookup().lookup(DataObject.class);
        if (null != dO) {
            return dO.getPrimaryFile();
        }

        df = node.getLookup().lookup(DataFolder.class);
        if (null != df) {
            return df.getPrimaryFile();
        }
        return null;
    }
    
    /**
     * Finds all <code>SourceGroup</code>s of the given project
     * containing a class of the given name.
     *
     * @param  project  project to be searched for matching classes
     * @param  className  class name pattern
     * @return  unmodifiable collection of <code>SourceGroup</code>s
     *          which contain files corresponding to the given name
     *          (may be empty but not <code>null</code>)
     * @author  Marian Petras
     */
    public static Collection<SourceGroup> findSourceGroupOwners(
            final Project project,
            final String className) {
        final SourceGroup[] sourceGroups
                = new Utils(project).getJavaSourceGroups();
        if (sourceGroups.length == 0) {
            return Collections.<SourceGroup>emptyList();
        }
        
        final String relativePath = className.replace('.', '/')
                                    + ".java";                          //NOI18N
        
        ArrayList<SourceGroup> result = new ArrayList<SourceGroup>(4);
        for (int i = 0; i < sourceGroups.length; i++) {
            SourceGroup srcGroup = sourceGroups[i];
            FileObject root = srcGroup.getRootFolder();
            FileObject file = root.getFileObject(relativePath);
            if (file != null && FileUtil.isParentOf(root, file)
                             && srcGroup.contains(file)) {
                result.add(srcGroup);
            }
        }
        if (result.isEmpty()) {
            return Collections.<SourceGroup>emptyList();
        }
        result.trimToSize();
        return Collections.unmodifiableList(result);
    }

    /**
     * Finds <code>SourceGroup</code>s where a test for the given class
     * can be created (so that it can be found by the projects infrastructure
     * when a test for the class is to be opened or run).
     *
     * @param  fileObject  <code>FileObject</code> to find target
     *                     <code>SourceGroup</code>(s) for
     * @return  an array of objects - each of them can be either
     *          a <code>SourceGroup</code> for a possible target folder
     *          or simply a <code>FileObject</code> representing a possible
     *          target folder (if <code>SourceGroup</code>) for the folder
     *          was not found);
     *          the returned array may be empty but not <code>null</code>
     * @author  Marian Petras
     */
    public static Object[] getTestTargets(FileObject fileObject) {
        
        /* .) get project owning the given FileObject: */
        final Project project = FileOwnerQuery.getOwner(fileObject);
        if (project == null) {
            return new Object[0];
        }
        
        SourceGroup sourceGroupOwner = findSourceGroupOwner(fileObject);
        if (sourceGroupOwner == null) {
            return new Object[0];
        }
        
        /* .) get URLs of target SourceGroup's roots: */
        final URL[] rootURLs = UnitTestForSourceQuery.findUnitTests(sourceGroupOwner.getRootFolder());
        if (rootURLs.length == 0) {
            return new Object[0];
        }
        
        /* .) convert the URLs to FileObjects: */
        boolean someSkipped = false;
        FileObject[] sourceRoots = new FileObject[rootURLs.length];
        for (int i = 0; i < rootURLs.length; i++) {
            if ((sourceRoots[i] = URLMapper.findFileObject(rootURLs[i]))
                    == null) {
                ErrorManager.getDefault().notify(
                        ErrorManager.INFORMATIONAL,
                        new IllegalStateException(
                           "No FileObject found for the following URL: "//NOI18N
                           + rootURLs[i]));
                someSkipped = true;
                continue;
            }
            if (FileOwnerQuery.getOwner(sourceRoots[i]) != project) {
                ErrorManager.getDefault().notify(
                        ErrorManager.INFORMATIONAL,
                        new IllegalStateException(
                    "Source root found by FileOwnerQuery points "       //NOI18N
                    + "to a different project for the following URL: "  //NOI18N
                    + rootURLs[i]));
                sourceRoots[i] = null;
                someSkipped = true;
                continue;
            }
        }
        
        if (someSkipped) {
            FileObject roots[] = skipNulls(sourceRoots, new FileObject[0]);
            if (roots.length == 0) {
                return new Object[0];
            }
            sourceRoots = roots;
        }
        
        /* .) find SourceGroups corresponding to the FileObjects: */
        final Object[] targets = new Object[sourceRoots.length];
        Map<FileObject,SourceGroup> map = getFileObject2SourceGroupMap(project);
        for (int i = 0; i < sourceRoots.length; i++) {
            SourceGroup srcGroup = map.get(sourceRoots[i]);
            targets[i] = srcGroup != null ? srcGroup : sourceRoots[i];
        }
        return targets;
    }
    
    /**
     * Finds a <code>SourceGroup</code> the given file belongs to.
     * Only Java <code>SourceGroup</code>s are taken into account.
     *
     * @param  file  <code>FileObject</code> whose owning
     *               <code>SourceGroup</code> to be found
     * @return  Java <code>SourceGroup</code> containing the given
     *          file; or <code>null</code> if no such
     *          <code>SourceGroup</code> was found
     * @author  Marian Petras
     */
    public static SourceGroup findSourceGroupOwner(FileObject file) {
        final Project project = FileOwnerQuery.getOwner(file);
        return findSourceGroupOwner(project, file);
    }
    
    /**
     * Finds a <code>SourceGroup</code> the given file belongs to.
     * Only Java <code>SourceGroup</code>s are taken into account. 
     *
     * @param project the <code>Project</code> the file belongs to
     * @param  file  <code>FileObject</code> whose owning
     *               <code>SourceGroup</code> to be found
     * @return  Java <code>SourceGroup</code> containing the given
     *          file; or <code>null</code> if no such
     *          <code>SourceGroup</code> was found
     */

    public static SourceGroup findSourceGroupOwner(Project project, FileObject file) {        
        final SourceGroup[] sourceGroups
                = new Utils(project).getJavaSourceGroups();
        for (int i = 0; i < sourceGroups.length; i++) {
            SourceGroup srcGroup = sourceGroups[i];
            FileObject root = srcGroup.getRootFolder();
            if (((file==root)||(FileUtil.isParentOf(root,file))) && 
                 srcGroup.contains(file)) {
                return srcGroup;
            }
        }
        return null;
    }
    
    /**
     * Creates a copy of the given array, except that <code>null</code> objects
     * are omitted.
     * The length of the returned array is (<var>l</var> - <var>n</var>), where
     * <var>l</var> is length of the passed array and <var>n</var> is number
     * of <code>null</code> elements of the array. Order of
     * non-<code>null</code> elements is kept in the returned array.
     * The returned array is always a new array, even if the passed
     * array does not contain any <code>null</code> elements.
     *
     * @param  objs  array to copy
     * @param  type  an empty array of the correct type to be returned
     * @return  array containing the same objects as the passed array, in the
     *          same order, just with <code>null</code> elements missing
     * @author  Marian Petras
     */
    public static <T> T[] skipNulls(final T[] objs, final T[] type) {
        List<T> resultList = new ArrayList<T>(objs.length);
        
        for (int i = 0; i < objs.length; i++) {
            if (objs[i] != null) {
                resultList.add(objs[i]);
            }
        }
        
        return resultList.toArray(type);
    }
    
    /**
     * Creates a map from folders to <code>SourceGroup</code>s of a given
     * project.
     * The map allows to ascertian for a given folder
     * which <code>SourceGroup</code> it is a root folder of.
     *
     * @param  project  project whose <code>SourceGroup</code>s should be in the
     *                  returned map
     * @return  map from containing all <code>SourceGroup</code>s of a given
     *          project, having their root folders as keys
     * @author  Marian Petras
     */
    public static Map<FileObject,SourceGroup> getFileObject2SourceGroupMap(
                                                              Project project) {
        final SourceGroup[] sourceGroups
                = new Utils(project).getJavaSourceGroups();
        
        if (sourceGroups.length == 0) {
            return Collections.<FileObject,SourceGroup>emptyMap();
        } else if (sourceGroups.length == 1) {
            return Collections.singletonMap(sourceGroups[0].getRootFolder(),
                                            sourceGroups[0]);
        } else {
            Map<FileObject,SourceGroup> map;
            map = new HashMap<FileObject,SourceGroup>(
                    Math.round(sourceGroups.length * 1.4f + .5f),
                               .75f);
            for (int i = 0; i < sourceGroups.length; i++) {
                map.put(sourceGroups[i].getRootFolder(),
                        sourceGroups[i]);
            }
            return map;
        }
    }

    /**
     * Creates a map of parameters according to the current JUnit module
     * settings.<br />
     * Note: The map may not contain all the necessary settings,
     *       i.g. name of a test class is missing.
     *
     * @param  multipleFiles  if {@literal true}, the map should contain
     *                        also settings need for creation of multiple
     *                        tests
     * @return  map of settings to be used by a
     *          {@link org.netbeans.modules.junit.plugin JUnitPlugin}
     * @see  org.netbeans.modules.junit.plugin.JUnitPlugin
     */
    public static Map<CreateTestParam, Object> getSettingsMap(
            boolean multipleFiles) {
        final CommonSettings settings = CommonSettings.getDefault();
        final Map<CreateTestParam, Object> params
                    = new HashMap<CreateTestParam, Object>(17);
        
        params.put(CreateTestParam.INC_PUBLIC,
                   Boolean.valueOf(settings.isMembersPublic()));
        params.put(CreateTestParam.INC_PROTECTED,
                   Boolean.valueOf(settings.isMembersProtected()));
        params.put(CreateTestParam.INC_PKG_PRIVATE,
                   Boolean.valueOf(settings.isMembersPackage()));
        params.put(CreateTestParam.INC_CODE_HINT,
                   Boolean.valueOf(settings.isBodyComments()));
        params.put(CreateTestParam.INC_METHOD_BODIES,
                   Boolean.valueOf(settings.isBodyContent()));
        params.put(CreateTestParam.INC_JAVADOC,
                   Boolean.valueOf(settings.isJavaDoc()));
        
        if (multipleFiles) {
            params.put(CreateTestParam.INC_GENERATE_SUITE,
                       Boolean.valueOf(settings.isGenerateSuiteClasses()));
            params.put(CreateTestParam.INC_PKG_PRIVATE_CLASS,
                    Boolean.valueOf(settings.isIncludePackagePrivateClasses()));
            params.put(CreateTestParam.INC_ABSTRACT_CLASS,
                       Boolean.valueOf(settings.isGenerateAbstractImpl()));
            params.put(CreateTestParam.INC_EXCEPTION_CLASS,
                       Boolean.valueOf(settings.isGenerateExceptionClasses()));
        }
        else {
            // If a class is explicitly selected then corresponding test class
            // should be generated in any cases.
            params.put(CreateTestParam.INC_PKG_PRIVATE_CLASS,
                       Boolean.valueOf(true));
            params.put(CreateTestParam.INC_ABSTRACT_CLASS,
                       Boolean.valueOf(true));
            params.put(CreateTestParam.INC_EXCEPTION_CLASS,
                       Boolean.valueOf(true));
        }
        
        params.put(CreateTestParam.INC_SETUP,
                   Boolean.valueOf(settings.isGenerateSetUp()));
        params.put(CreateTestParam.INC_TEAR_DOWN,
                   Boolean.valueOf(settings.isGenerateTearDown()));
        params.put(CreateTestParam.INC_CLASS_SETUP,
                   Boolean.valueOf(settings.isGenerateClassSetUp()));
        params.put(CreateTestParam.INC_CLASS_TEAR_DOWN,
                   Boolean.valueOf(settings.isGenerateClassTearDown()));
        
        return params;
    }
    
}
