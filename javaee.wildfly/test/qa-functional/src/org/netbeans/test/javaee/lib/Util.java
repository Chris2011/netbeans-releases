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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.test.javaee.lib;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.java.source.SourceUtils;
import org.netbeans.api.j2ee.core.Profile;
import org.netbeans.modules.j2ee.earproject.EarProjectGenerator;
import org.netbeans.modules.java.j2seproject.J2SEProjectGenerator;
import org.netbeans.modules.project.ui.OpenProjectList;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Mutex;
import org.netbeans.modules.web.project.api.WebProjectUtilities;
import org.netbeans.modules.j2ee.ejbjarproject.api.EjbJarProjectGenerator;


/**
 *
 * @author jungi
 */
public class Util {
    
    public static final int J2SE_PROJECT = 0;
    public static final int WEB_PROJECT = 1;
    public static final int EJB_PROJECT = 2;
    public static final int J2EE_PROJECT = 3;
    
    public static final String DEFAULT_J2EE_LEVEL
            = WebModule.J2EE_14_LEVEL;
    
    public static final String DEFAULT_APPSRV_ID
            = "deployer:Sun:AppServer::localhost:4848";
    
//    public static final String DEFAULT_SRC_STRUCTURE
//            = WebProjectGenerator.SRC_STRUCT_BLUEPRINTS;
    
    public static final String DEFAULT_SRC_STRUCTURE
            = WebProjectUtilities.SRC_STRUCT_BLUEPRINTS;
    
    /** Creates a new instance of J2eeProjectSupport */
    private Util() {
        throw new UnsupportedOperationException("It is just a helper class.");
    }
    
    /** Opens project in specified directory.
     * @param projectDir a directory with project to open
     * @return Project instance of opened project
     */
    public static Object openProject(File projectDir) {
        final ProjectOpenListener listener = new ProjectOpenListener();
        try {
            // open project
            final Project project = OpenProjectList.fileToProject(projectDir);
            // posting the to AWT event thread
            Mutex.EVENT.writeAccess(new Runnable() {
                public void run() {
                    OpenProjectList.getDefault().addPropertyChangeListener(listener);
                    OpenProjectList.getDefault().open(project);
                }
            });
            // WAIT PROJECT OPEN - start
            // We need to wait until project is open and then we can start to
            // wait when scanning finishes. If we don't wait, scanning is started
            // too early and finishes immediatelly.
            Thread waitThread = new Thread(new Runnable() {
                public void run() {
                    while (!listener.projectOpened) {
                        try {
                            Thread.sleep(50);
                        } catch (Exception e) {
                            Logger.getLogger("global").log(Level.WARNING, null, e);
                        }
                    }
                }
            });
            waitThread.start();
            try {
                waitThread.join(60000L);  // wait 1 minute at the most
            } catch (InterruptedException iex) {
                Logger.getLogger("global").log(Level.WARNING, null, iex);
            }
            if (waitThread.isAlive()) {
                // time-out expired, project not opened -> interrupt the wait thread
                Logger.getLogger("global").log(Level.WARNING, "Project not opened in 60 second.");
                waitThread.interrupt();
            }
            // WAIT PROJECT OPEN - end
            // wait until metadata scanning is finished
            waitScanFinished();
            return project;
        } catch (Exception ex) {
            Logger.getLogger("global").log(Level.WARNING, null, ex);
            return null;
        } finally {
            OpenProjectList.getDefault().removePropertyChangeListener(listener);
        }
    }
    
    /** Opens project on specified path.
     * @param projectPath path to a directory with project to open
     * @return Project instance of opened project
     */
    public static Object openProject(String projectPath) {
        return openProject(new File(projectPath));
    }
    
    /** Creates an empty Java project in specified directory and opens it.
     * Its name is defined by name parameter.
     * @param projectParentPath path to directory where to create name subdirectory and
     * new project structure in that subdirectory.
     * @param name name of the project
     * @return Project instance of created project
     */
    public static Object createProject(String projectParentPath, String name) {
        return createProject(new File(projectParentPath), name, J2SE_PROJECT, null);
    }
    
    /** Creates an empty project in specified directory and opens it.
     * Its name is defined by name parameter.
     * @param projectParentDir directory where to create name subdirectory and
     * new project structure in that subdirectory.
     * @param name name of the project
     * @param type type of project
     * @param params parameters passed to created project
     */
    public static Object createProject(File projectParentDir, String name,
            int type, String[] params) {
        String mainClass = null;
        try {
            File projectDir = new File(projectParentDir, name);
            switch (type) {
                case J2SE_PROJECT:
                    J2SEProjectGenerator.createProject(projectDir, name, mainClass, null, null, false);
                    break;
                case WEB_PROJECT:
                    //params[0] = serverInstanceID
                    //params[1] = sourceStructure
                    //params[2] = j2eeLevel
                    if (params == null){
                        params = new String[] {DEFAULT_APPSRV_ID, DEFAULT_SRC_STRUCTURE, DEFAULT_J2EE_LEVEL};
                    }
                    WebProjectUtilities.createProject(projectDir, name, params[0], params[1], params[2], name);
                    break;
                case EJB_PROJECT:
                    //params[0] = j2eeLevel
                    //params[1] = serverInstanceID
                    if (params == null){
                        params = new String[] {DEFAULT_J2EE_LEVEL, DEFAULT_APPSRV_ID};
                    }
                    EjbJarProjectGenerator.createProject(projectDir, name, params[0], params[1]);
                    break;
                case J2EE_PROJECT:
                    //params[0] = j2eeLevel
                    //params[1] = serverInstanceID
                    //params[2] = sourceLevel
                    if (params == null){
                        params = new String[] {DEFAULT_J2EE_LEVEL, DEFAULT_APPSRV_ID, null};
                    }
                    EarProjectGenerator.createProject(projectDir, name,
                            Profile.fromPropertiesString(params[0]), params[1], params[2],null);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid project type.");
            }
            return openProject(projectDir);
        } catch (IOException e) {
            Logger.getLogger("global").log(Level.WARNING, null, e);
            return null;
        }
    }
    
    /** Closes project with system or display name equals to given name.
     * @param name system or display name of project to be closed.
     * @return true if project is closed, false otherwise (i.e. project was
     * not found).
     */
    public static boolean closeProject(String name) {
        Project[] projects = OpenProjectList.getDefault().getOpenProjects();
        for(int i=0;i<projects.length;i++) {
            final Project project = projects[i];
            if(ProjectUtils.getInformation(project).getDisplayName().equals(name) ||
                    ProjectUtils.getInformation(project).getName().equals(name)) {
                // posting the to AWT event thread
                Mutex.EVENT.writeAccess(new Runnable() {
                    public void run() {
                        OpenProjectList.getDefault().close(new Project[] { project }, true);
                    }
                });
                return true;
            }
        }
        // project not found
        return false;
    }
    
    /** Waits until metadata scanning is finished. */
    public static void waitScanFinished() {
        try {
            SourceUtils.waitScanFinished();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     *
     * @return set of file names under the project root
     */
    public static Set getFileSet(Project p) {
        File f = FileUtil.toFile(p.getProjectDirectory());
        Set dummy = new HashSet();
        visitAllDirsAndFiles(f, dummy);
        Set retVal = new HashSet(dummy.size());
        Iterator i = dummy.iterator();
        while (i.hasNext()) {
            String s = ((String) i.next()).substring(f.getAbsolutePath().length() + 1);
            if (s.length() > 2) {
                retVal.add(s);
            }
        }
        return retVal;
    }
    
    public static Set getFileSet(String projectRoot) {
        File f = new File(projectRoot);
        Set dummy = new HashSet();
        visitAllDirsAndFiles(f, dummy);
        Set retVal = new HashSet(dummy.size());
        Iterator i = dummy.iterator();
        while (i.hasNext()) {
            String s = ((String) i.next()).substring(f.getAbsolutePath().length() + 1);
            if (s.length() > 2) {
                retVal.add(s);
            }
        }
        return retVal;
    }
    
    public static Project getProject(File wd, String relativePath) throws Exception {
        File f = new File(wd, relativePath);
        f = f.getCanonicalFile();
        FileObject fo = FileUtil.toFileObject(f);
        return ProjectManager.getDefault().findProject(fo);
    }
    
    // Process all files and directories under dir and put their names to given set
    private static void visitAllDirsAndFiles(File dir, Set s) {
        s.add(dir.isDirectory() ? dir.getPath() + File.separatorChar : dir.getPath());
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                visitAllDirsAndFiles(new File(dir, children[i]), s);
            }
        }
    }
    
    /** Listener for project open. */
    static class ProjectOpenListener implements PropertyChangeListener {
        public boolean projectOpened = false;
        
        /** Listen for property which changes when project is hopefully opened. */
        public void propertyChange(PropertyChangeEvent evt) {
            if(OpenProjectList.PROPERTY_OPEN_PROJECTS.equals(evt.getPropertyName())) {
                projectOpened = true;
            }
        }
    }
}

