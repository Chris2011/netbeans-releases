/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.java.j2seproject;

import java.awt.Dialog;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.api.fileinfo.NonRecursiveFolder;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.java.j2seproject.applet.AppletSupport;
import org.netbeans.modules.java.j2seproject.ui.customizer.J2SEProjectProperties;
import org.netbeans.modules.java.j2seproject.ui.customizer.MainClassChooser;
import org.netbeans.modules.java.j2seproject.ui.customizer.MainClassWarning;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.GeneratedFilesHelper;
import org.netbeans.spi.project.ui.support.DefaultProjectOperations;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.awt.MouseUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/** Action provider of the J2SE project. This is the place where to do
 * strange things to J2SE actions. E.g. compile-single.
 */
class J2SEActionProvider implements ActionProvider {
    
    // Commands available from J2SE project
    private static final String[] supportedActions = {
        COMMAND_BUILD, 
        COMMAND_CLEAN, 
        COMMAND_REBUILD, 
        COMMAND_COMPILE_SINGLE, 
        COMMAND_RUN, 
        COMMAND_RUN_SINGLE, 
        COMMAND_DEBUG, 
        COMMAND_DEBUG_SINGLE,
        JavaProjectConstants.COMMAND_JAVADOC,         
        COMMAND_TEST, 
        COMMAND_TEST_SINGLE, 
        COMMAND_DEBUG_TEST_SINGLE, 
        JavaProjectConstants.COMMAND_DEBUG_FIX,
        COMMAND_DEBUG_STEP_INTO,
        COMMAND_DELETE,
        COMMAND_COPY,
        COMMAND_MOVE,
        COMMAND_RENAME,
    };
    
    
    private static final String[] platformSensitiveActions = {
        COMMAND_BUILD, 
        COMMAND_REBUILD, 
        COMMAND_COMPILE_SINGLE, 
        COMMAND_RUN, 
        COMMAND_RUN_SINGLE, 
        COMMAND_DEBUG, 
        COMMAND_DEBUG_SINGLE,
        JavaProjectConstants.COMMAND_JAVADOC,         
        COMMAND_TEST, 
        COMMAND_TEST_SINGLE, 
        COMMAND_DEBUG_TEST_SINGLE, 
        JavaProjectConstants.COMMAND_DEBUG_FIX,
        COMMAND_DEBUG_STEP_INTO,
    };
    
    // Project
    J2SEProject project;
    
    // Ant project helper of the project
    private UpdateHelper updateHelper;
    
        
    /** Map from commands to ant targets */
    Map<String,String[]> commands;
    
    /**Set of commands which are affected by background scanning*/
    final Set<String> bkgScanSensitiveActions;
    
    public J2SEActionProvider( J2SEProject project, UpdateHelper updateHelper ) {
        
        commands = new HashMap<String,String[]>();
        commands.put(COMMAND_BUILD, new String[] {"jar"}); // NOI18N
        commands.put(COMMAND_CLEAN, new String[] {"clean"}); // NOI18N
        commands.put(COMMAND_REBUILD, new String[] {"clean", "jar"}); // NOI18N
        commands.put(COMMAND_COMPILE_SINGLE, new String[] {"compile-single"}); // NOI18N
        // commands.put(COMMAND_COMPILE_TEST_SINGLE, new String[] {"compile-test-single"}); // NOI18N
        commands.put(COMMAND_RUN, new String[] {"run"}); // NOI18N
        commands.put(COMMAND_RUN_SINGLE, new String[] {"run-single"}); // NOI18N
        commands.put(COMMAND_DEBUG, new String[] {"debug"}); // NOI18N
        commands.put(COMMAND_DEBUG_SINGLE, new String[] {"debug-single"}); // NOI18N
        commands.put(JavaProjectConstants.COMMAND_JAVADOC, new String[] {"javadoc"}); // NOI18N
        commands.put(COMMAND_TEST, new String[] {"test"}); // NOI18N
        commands.put(COMMAND_TEST_SINGLE, new String[] {"test-single"}); // NOI18N
        commands.put(COMMAND_DEBUG_TEST_SINGLE, new String[] {"debug-test"}); // NOI18N
        commands.put(JavaProjectConstants.COMMAND_DEBUG_FIX, new String[] {"debug-fix"}); // NOI18N
        commands.put(COMMAND_DEBUG_STEP_INTO, new String[] {"debug-stepinto"}); // NOI18N

        this.bkgScanSensitiveActions = new HashSet<String>(Arrays.asList(new String[] {
            COMMAND_RUN, 
            COMMAND_RUN_SINGLE, 
            COMMAND_DEBUG, 
            COMMAND_DEBUG_SINGLE,
            COMMAND_DEBUG_STEP_INTO
        }));
            
        this.updateHelper = updateHelper;
        this.project = project;
    }
    
    private FileObject findBuildXml() {
        return project.getProjectDirectory().getFileObject(GeneratedFilesHelper.BUILD_XML_PATH);
    }
    
    public String[] getSupportedActions() {
        return supportedActions;
    }
    
    public void invokeAction( final String command, final Lookup context ) throws IllegalArgumentException {
        if (COMMAND_DELETE.equals(command)) {
            DefaultProjectOperations.performDefaultDeleteOperation(project);
            return ;
        }
        
        if (COMMAND_COPY.equals(command)) {
            DefaultProjectOperations.performDefaultCopyOperation(project);
            return ;
        }
        
        if (COMMAND_MOVE.equals(command)) {
            DefaultProjectOperations.performDefaultMoveOperation(project);
            return ;
        }
        
        if (COMMAND_RENAME.equals(command)) {
            DefaultProjectOperations.performDefaultRenameOperation(project, null);
            return ;
        }
        
        Runnable action = new Runnable () {
            public void run () {
                Properties p = new Properties();
                String[] targetNames;
        
                targetNames = getTargetNames(command, context, p);
                if (targetNames == null) {
                    return;
                }
                if (targetNames.length == 0) {
                    targetNames = null;
                }
                if (p.keySet().size() == 0) {
                    p = null;
                }
                try {
                    FileObject buildFo = findBuildXml();
                    if (buildFo == null || !buildFo.isValid()) {
                        //The build.xml was deleted after the isActionEnabled was called
                        NotifyDescriptor nd = new NotifyDescriptor.Message(NbBundle.getMessage(J2SEActionProvider.class,
                                "LBL_No_Build_XML_Found"), NotifyDescriptor.WARNING_MESSAGE);
                        DialogDisplayer.getDefault().notify(nd);
                    }
                    else {
                        ActionUtils.runTarget(buildFo, targetNames, p);
                    }
                } 
                catch (IOException e) {
                    ErrorManager.getDefault().notify(e);
                }
            }            
        };
        
//        if (this.bkgScanSensitiveActions.contains(command)) {        
//            JMManager.getManager().invokeAfterScanFinished(action, NbBundle.getMessage (J2SEActionProvider.class,"ACTION_"+command)); //NOI18N
//        }
//        else {
            action.run();
//        }
    }

    /**
     * @return array of targets or null to stop execution; can return empty array
     */
    /*private*/ String[] getTargetNames(String command, Lookup context, Properties p) throws IllegalArgumentException {
        if (Arrays.asList(platformSensitiveActions).contains(command)) {
            final String activePlatformId = this.project.evaluator().getProperty("platform.active");  //NOI18N
            if (J2SEProjectUtil.getActivePlatform (activePlatformId) == null) {
                showPlatformWarning ();
                return null;
            }
        }
        String[] targetNames = new String[0];
        Map<String,String[]> targetsFromConfig = loadTargetsFromConfig();
        if ( command.equals( COMMAND_COMPILE_SINGLE ) ) {
            FileObject[] sourceRoots = project.getSourceRoots().getRoots();
            FileObject[] files = findSourcesAndPackages( context, sourceRoots);
            boolean recursive = (context.lookup(NonRecursiveFolder.class) == null);
            if (files != null) {
                p.setProperty("javac.includes", ActionUtils.antIncludesList(files, getRoot(sourceRoots,files[0]), recursive)); // NOI18N
                String[] targets = targetsFromConfig.get(command);
                targetNames = (targets != null) ? targets : commands.get(command);
            } 
            else {
                FileObject[] testRoots = project.getTestSourceRoots().getRoots();
                files = findSourcesAndPackages(context, testRoots);
                p.setProperty("javac.includes", ActionUtils.antIncludesList(files, getRoot(testRoots,files[0]), recursive)); // NOI18N
                targetNames = new String[] {"compile-test-single"}; // NOI18N
            }
        } 
        else if ( command.equals( COMMAND_TEST_SINGLE ) ) {
            FileObject[] files = findTestSourcesForSources(context);
            targetNames = setupTestSingle(p, files);
        } 
        else if ( command.equals( COMMAND_DEBUG_TEST_SINGLE ) ) {
            FileObject[] files = findTestSourcesForSources(context);
            targetNames = setupDebugTestSingle(p, files);
        } 
        else if ( command.equals( JavaProjectConstants.COMMAND_DEBUG_FIX ) ) {
            FileObject[] files = findSources( context );
            String path = null;
            if (files != null) {
                path = FileUtil.getRelativePath(getRoot(project.getSourceRoots().getRoots(),files[0]), files[0]);
                targetNames = new String[] {"debug-fix"}; // NOI18N
            } else {
                files = findTestSources(context, false);
                path = FileUtil.getRelativePath(getRoot(project.getTestSourceRoots().getRoots(),files[0]), files[0]);
                targetNames = new String[] {"debug-fix-test"}; // NOI18N
            }
            // Convert foo/FooTest.java -> foo/FooTest
            if (path.endsWith(".java")) { // NOI18N
                path = path.substring(0, path.length() - 5);
            }
            p.setProperty("fix.includes", path); // NOI18N
        }
        else if (command.equals (COMMAND_RUN) || command.equals(COMMAND_DEBUG) || command.equals(COMMAND_DEBUG_STEP_INTO)) {
            String config = project.evaluator().getProperty(J2SEConfigurationProvider.PROP_CONFIG);
            String path;
            if (config == null || config.length() == 0) {
                path = AntProjectHelper.PROJECT_PROPERTIES_PATH;
            } else {
                // Set main class for a particular config only.
                path = "nbproject/configs/" + config + ".properties"; // NOI18N
            }
            EditableProperties ep = updateHelper.getProperties(path);

            // check project's main class
            // Check whether main class is defined in this config. Note that we use the evaluator,
            // not ep.getProperty(MAIN_CLASS), since it is permissible for the default pseudoconfig
            // to define a main class - in this case an active config need not override it.
            String mainClass = project.evaluator().getProperty(J2SEProjectProperties.MAIN_CLASS);
            MainClassStatus result = isSetMainClass (project.getSourceRoots().getRoots(), mainClass);
            if (context.lookup(J2SEConfigurationProvider.Config.class) != null) {
                // If a specific config was selected, just skip this check for now.
                // XXX would ideally check that that config in fact had a main class.
                // But then evaluator.getProperty(MAIN_CLASS) would be inaccurate.
                // Solvable but punt on it for now.
                result = MainClassStatus.SET_AND_VALID;
            }
            if (result != MainClassStatus.SET_AND_VALID) {
                do {
                    // show warning, if cancel then return
                    if (showMainClassWarning (mainClass, ProjectUtils.getInformation(project).getDisplayName(), ep,result)) {
                        return null;
                    }
                    // No longer use the evaluator: have not called putProperties yet so it would not work.
                    mainClass = ep.get(J2SEProjectProperties.MAIN_CLASS);
                    result=isSetMainClass (project.getSourceRoots().getRoots(), mainClass);
                } while (result != MainClassStatus.SET_AND_VALID);
                try {
                    if (updateHelper.requestSave()) {
                        updateHelper.putProperties(path, ep);
                        ProjectManager.getDefault().saveProject(project);
                    }
                    else {
                        return null;
                    }
                } catch (IOException ioe) {           
                    ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, "Error while saving project: " + ioe);
                }
            }
            if (!command.equals(COMMAND_RUN) && /* XXX should ideally look up proper mainClass in evaluator x config */ mainClass != null) {
                p.setProperty("debug.class", mainClass); // NOI18N
            }
            String[] targets = targetsFromConfig.get(command);
            targetNames = (targets != null) ? targets : commands.get(command);
            if (targetNames == null) {
                throw new IllegalArgumentException(command);
            }
        } else if (command.equals (COMMAND_RUN_SINGLE) || command.equals (COMMAND_DEBUG_SINGLE)) {
            FileObject[] files = findTestSources(context, false);
            if (files != null) {
                if (command.equals(COMMAND_RUN_SINGLE)) {
                    targetNames = setupTestSingle(p, files);
                } else {
                    targetNames = setupDebugTestSingle(p, files);
                }
            } else {
                FileObject file = findSources(context)[0];
                String clazz = FileUtil.getRelativePath(getRoot(project.getSourceRoots().getRoots(),file), file);
                p.setProperty("javac.includes", clazz); // NOI18N
                // Convert foo/FooTest.java -> foo.FooTest
                if (clazz.endsWith(".java")) { // NOI18N
                    clazz = clazz.substring(0, clazz.length() - 5);
                }
                clazz = clazz.replace('/','.');

                if (!J2SEProjectUtil.hasMainMethod (file)) {
                    if (AppletSupport.isApplet(file)) {
                        
                        EditableProperties ep = updateHelper.getProperties (AntProjectHelper.PROJECT_PROPERTIES_PATH);
                        String jvmargs = ep.getProperty("run.jvmargs");
                        
                        URL url = null;

                        // do this only when security policy is not set manually
                        if ((jvmargs == null) || !(jvmargs.indexOf("java.security.policy") > 0)) {  //NOI18N
                            AppletSupport.generateSecurityPolicy(project.getProjectDirectory());
                            if ((jvmargs == null) || (jvmargs.length() == 0)) {
                                ep.setProperty("run.jvmargs", "-Djava.security.policy=applet.policy"); //NOI18N
                            } else {
                                ep.setProperty("run.jvmargs", jvmargs + " -Djava.security.policy=applet.policy"); //NOI18N
                            }
                            updateHelper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, ep);
                            try {
                                ProjectManager.getDefault().saveProject(project);
                            } catch (Exception e) {
                                ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, "Error while saving project: " + e);
                            }
                        }
                        
                        if (file.existsExt("html") || file.existsExt("HTML")) { //NOI18N
                            url = copyAppletHTML(file, "html"); //NOI18N
                        } else {                    
                            url = generateAppletHTML(file);
                        }
                        if (url == null) {
                            return null;
                        }
                        p.setProperty("applet.url", url.toString()); // NOI18N
                        if (command.equals (COMMAND_RUN_SINGLE)) {
                            targetNames = new String[] {"run-applet"}; // NOI18N
                        } else {
                            p.setProperty("debug.class", clazz); // NOI18N
                            targetNames = new String[] {"debug-applet"}; // NOI18N
                        }
                    } else {
                        NotifyDescriptor nd = new NotifyDescriptor.Message(NbBundle.getMessage(J2SEActionProvider.class, "LBL_No_Main_Classs_Found", clazz), NotifyDescriptor.INFORMATION_MESSAGE);
                        DialogDisplayer.getDefault().notify(nd);
                        return null;
                    }
                } else {
                    if (command.equals (COMMAND_RUN_SINGLE)) {
                        p.setProperty("run.class", clazz); // NOI18N
                        String[] targets = targetsFromConfig.get(command);
                        targetNames = (targets != null) ? targets : commands.get(COMMAND_RUN_SINGLE);
                    } else {
                        p.setProperty("debug.class", clazz); // NOI18N
                        String[] targets = targetsFromConfig.get(command);
                        targetNames = (targets != null) ? targets : commands.get(COMMAND_DEBUG_SINGLE);
                    }
                }
            }
        } else {
            String[] targets = targetsFromConfig.get(command);
            targetNames = (targets != null) ? targets : commands.get(command);
            if (targetNames == null) {
                throw new IllegalArgumentException(command);
            }
        }
        J2SEConfigurationProvider.Config c = context.lookup(J2SEConfigurationProvider.Config.class);
        if (c != null) {
            String config;
            if (c.name != null) {
                config = c.name;
            } else {
                // Invalid but overrides any valid setting in config.properties.
                config = "";
            }
            p.setProperty(J2SEConfigurationProvider.PROP_CONFIG, config);
        }
        return targetNames;
    }
    
    // loads targets for specific commands from shared config property file
    // returns map; key=command name; value=array of targets for given command
    private HashMap<String,String[]> loadTargetsFromConfig() {
        HashMap<String,String[]> targets = new HashMap<String,String[]>(6);
        String config = project.evaluator().getProperty(J2SEConfigurationProvider.PROP_CONFIG);
        // load targets from shared config
        FileObject propFO = project.getProjectDirectory().getFileObject("nbproject/configs/" + config + ".properties");
        if (propFO == null) {
            return targets;
        }
        Properties props = new Properties();
        try {
            InputStream is = propFO.getInputStream();
            try {
                props.load(is);
            } finally {
                is.close();
            }
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
            return targets;
        }
        Enumeration propNames = props.propertyNames();
        while (propNames.hasMoreElements()) {
            String propName = (String) propNames.nextElement();
            if (propName.startsWith("$target.")) {
                String tNameVal = props.getProperty(propName);
                String cmdNameKey = null;
                if (tNameVal != null && !tNameVal.equals("")) {
                    cmdNameKey = propName.substring("$target.".length());
                    StringTokenizer stok = new StringTokenizer(tNameVal.trim(), " ");
                    List<String> targetNames = new ArrayList<String>(3);
                    while (stok.hasMoreTokens()) {
                        targetNames.add(stok.nextToken());
                    }
                    targets.put(cmdNameKey, targetNames.toArray(new String[targetNames.size()]));
                }
            }
        }
        return targets;
    }
    
    private String[] setupTestSingle(Properties p, FileObject[] files) {
        FileObject[] testSrcPath = project.getTestSourceRoots().getRoots();
        FileObject root = getRoot(testSrcPath, files[0]);
        p.setProperty("test.includes", ActionUtils.antIncludesList(files, root)); // NOI18N
        p.setProperty("javac.includes", ActionUtils.antIncludesList(files, root)); // NOI18N
        return new String[] {"test-single"}; // NOI18N
    }

    private String[] setupDebugTestSingle(Properties p, FileObject[] files) {
        FileObject[] testSrcPath = project.getTestSourceRoots().getRoots();
        FileObject root = getRoot(testSrcPath, files[0]);
        String path = FileUtil.getRelativePath(root, files[0]);
        // Convert foo/FooTest.java -> foo.FooTest
        p.setProperty("test.class", path.substring(0, path.length() - 5).replace('/', '.')); // NOI18N
        return new String[] {"debug-test"}; // NOI18N
    }

    public boolean isActionEnabled( String command, Lookup context ) {
        FileObject buildXml = findBuildXml();
        if (  buildXml == null || !buildXml.isValid()) {
            return false;
        }
        if ( command.equals( COMMAND_COMPILE_SINGLE ) ) {
            return findSourcesAndPackages( context, project.getSourceRoots().getRoots()) != null
                    || findSourcesAndPackages( context, project.getTestSourceRoots().getRoots()) != null;
        }
        else if ( command.equals( COMMAND_TEST_SINGLE ) ) {
            return findTestSourcesForSources(context) != null;
        }
        else if ( command.equals( COMMAND_DEBUG_TEST_SINGLE ) ) {
            FileObject[] files = findTestSourcesForSources(context);
            return files != null && files.length == 1;
        } else if (command.equals(COMMAND_RUN_SINGLE) || 
                        command.equals(COMMAND_DEBUG_SINGLE) ||
                        command.equals(JavaProjectConstants.COMMAND_DEBUG_FIX)) {
            FileObject fos[] = findSources(context);
            if (fos != null && fos.length == 1) {
                return true;
            }
            fos = findTestSources(context, false);
            return fos != null && fos.length == 1;
        } else {
            // other actions are global
            return true;
        }
    }
    
    
   
    // Private methods -----------------------------------------------------
    
    
    private static final Pattern SRCDIRJAVA = Pattern.compile("\\.java$"); // NOI18N
    private static final String SUBST = "Test.java"; // NOI18N
    
    /** Find selected sources, the sources has to be under single source root,
     *  @param context the lookup in which files should be found
     */
    private FileObject[] findSources(Lookup context) {
        FileObject[] srcPath = project.getSourceRoots().getRoots();
        for (int i=0; i< srcPath.length; i++) {
            FileObject[] files = ActionUtils.findSelectedFiles(context, srcPath[i], ".java", true); // NOI18N
            if (files != null) {
                return files;
            }
        }
        return null;
    }

    private FileObject[] findSourcesAndPackages (Lookup context, FileObject srcDir) {
        if (srcDir != null) {
            FileObject[] files = ActionUtils.findSelectedFiles(context, srcDir, null, true); // NOI18N
            //Check if files are either packages of java files
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    if (!files[i].isFolder() && !"java".equals(files[i].getExt())) {
                        return null;
                    }
                }
            }
            return files;
        } else {
            return null;
        }
    }
    
    private FileObject[] findSourcesAndPackages (Lookup context, FileObject[] srcRoots) {
        for (int i=0; i<srcRoots.length; i++) {
            FileObject[] result = findSourcesAndPackages(context, srcRoots[i]);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
    
    /** Find either selected tests or tests which belong to selected source files
     */
    private FileObject[] findTestSources(Lookup context, boolean checkInSrcDir) {
        //XXX: Ugly, should be rewritten
        FileObject[] testSrcPath = project.getTestSourceRoots().getRoots();
        for (int i=0; i< testSrcPath.length; i++) {
            FileObject[] files = ActionUtils.findSelectedFiles(context, testSrcPath[i], ".java", true); // NOI18N
            if (files != null) {
                return files;
            }
        }
        if (checkInSrcDir && testSrcPath.length>0) {
            FileObject[] files = findSources (context);
            if (files != null) {
                //Try to find the test under the test roots
                FileObject srcRoot = getRoot(project.getSourceRoots().getRoots(),files[0]);
                for (int i=0; i<testSrcPath.length; i++) {
                    FileObject[] files2 = ActionUtils.regexpMapFiles(files,srcRoot, SRCDIRJAVA, testSrcPath[i], SUBST, true);
                    if (files2 != null) {
                        return files2;
                    }
                }
            }
        }
        return null;
    }
   

    /** Find tests corresponding to selected sources.
     */
    private FileObject[] findTestSourcesForSources(Lookup context) {
        FileObject[] sourceFiles = findSources(context);
        if (sourceFiles == null) {
            return null;
        }
        FileObject[] testSrcPath = project.getTestSourceRoots().getRoots();
        if (testSrcPath.length == 0) {
            return null;
        }
        FileObject[] srcPath = project.getSourceRoots().getRoots();
        FileObject srcDir = getRoot(srcPath, sourceFiles[0]);
        for (int i=0; i<testSrcPath.length; i++) {
            FileObject[] files2 = ActionUtils.regexpMapFiles(sourceFiles, srcDir, SRCDIRJAVA, testSrcPath[i], SUBST, true);
            if (files2 != null) {
                return files2;
            }
        }
        return null;
    }      
    
    private FileObject getRoot (FileObject[] roots, FileObject file) {
        assert file != null : "File can't be null";   //NOI18N
        FileObject srcDir = null;
        for (int i=0; i< roots.length; i++) {
            assert roots[i] != null : "Source Path Root can't be null"; //NOI18N
            if (FileUtil.isParentOf(roots[i],file) || roots[i].equals(file)) {
                srcDir = roots[i];
                break;
            }
        }
        return srcDir;
    }
    
    private static enum MainClassStatus {
        SET_AND_VALID,
        SET_BUT_INVALID,
        UNSET
    }

    /**
     * Tests if the main class is set
     * @param sourcesRoots source roots
     * @param mainClass main class name
     * @return status code
     */
    private MainClassStatus isSetMainClass(FileObject[] sourcesRoots, String mainClass) {

        // support for unit testing
        if (MainClassChooser.unitTestingSupport_hasMainMethodResult != null) {
            return MainClassChooser.unitTestingSupport_hasMainMethodResult ? MainClassStatus.SET_AND_VALID : MainClassStatus.SET_BUT_INVALID;
        }

        if (mainClass == null || mainClass.length () == 0) {
            return MainClassStatus.UNSET;
        }
        
        ClassPath bootPath = ClassPath.getClassPath (sourcesRoots[0], ClassPath.BOOT);        //Single compilation unit
        ClassPath compilePath = ClassPath.getClassPath (sourcesRoots[0], ClassPath.COMPILE);
        ClassPath sourcePath = ClassPath.getClassPath(sourcesRoots[0], ClassPath.SOURCE);
        if (J2SEProjectUtil.isMainClass (mainClass, bootPath, compilePath, sourcePath)) {
            return MainClassStatus.SET_AND_VALID;
        }
        return MainClassStatus.SET_BUT_INVALID;
    }
    
    /**
     * Asks user for name of main class
     * @param mainClass current main class
     * @param projectName the name of project
     * @param ep project.properties to possibly edit
     * @param messgeType type of dialog
     * @return true if user selected main class
     */
    private boolean showMainClassWarning(String mainClass, String projectName, EditableProperties ep, MainClassStatus messageType) {
        boolean canceled;
        final JButton okButton = new JButton (NbBundle.getMessage (MainClassWarning.class, "LBL_MainClassWarning_ChooseMainClass_OK")); // NOI18N
        okButton.getAccessibleContext().setAccessibleDescription (NbBundle.getMessage (MainClassWarning.class, "AD_MainClassWarning_ChooseMainClass_OK"));
        
        // main class goes wrong => warning
        String message;
        switch (messageType) {
            case UNSET:
                message = MessageFormat.format (NbBundle.getMessage(MainClassWarning.class,"LBL_MainClassNotFound"), new Object[] {
                    projectName
                });
                break;
            case SET_BUT_INVALID:
                message = MessageFormat.format (NbBundle.getMessage(MainClassWarning.class,"LBL_MainClassWrong"), new Object[] {
                    mainClass,
                    projectName
                });
                break;
            default:
                throw new IllegalArgumentException ();
        }
        final MainClassWarning panel = new MainClassWarning (message,project.getSourceRoots().getRoots());
        Object[] options = new Object[] {
            okButton,
            DialogDescriptor.CANCEL_OPTION
        };
        
        panel.addChangeListener (new ChangeListener () {
           public void stateChanged (ChangeEvent e) {
               if (e.getSource () instanceof MouseEvent && MouseUtils.isDoubleClick (((MouseEvent)e.getSource ()))) {
                   // click button and the finish dialog with selected class
                   okButton.doClick ();
               } else {
                   okButton.setEnabled (panel.getSelectedMainClass () != null);
               }
           }
        });
        
        okButton.setEnabled (false);
        DialogDescriptor desc = new DialogDescriptor (panel,
            NbBundle.getMessage (MainClassWarning.class, "CTL_MainClassWarning_Title", ProjectUtils.getInformation(project).getDisplayName()), // NOI18N
            true, options, options[0], DialogDescriptor.BOTTOM_ALIGN, null, null);
        desc.setMessageType (DialogDescriptor.INFORMATION_MESSAGE);
        Dialog dlg = DialogDisplayer.getDefault ().createDialog (desc);
        dlg.setVisible (true);
        if (desc.getValue() != options[0]) {
            canceled = true;
        } else {
            mainClass = panel.getSelectedMainClass ();
            canceled = false;
            ep.put(J2SEProjectProperties.MAIN_CLASS, mainClass == null ? "" : mainClass);
        }
        dlg.dispose();            

        return canceled;
    }
    
    private void showPlatformWarning () {
        final JButton closeOption = new JButton (NbBundle.getMessage(J2SEActionProvider.class, "CTL_BrokenPlatform_Close"));
        closeOption.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(J2SEActionProvider.class, "AD_BrokenPlatform_Close"));
        final ProjectInformation pi = (ProjectInformation) this.project.getLookup().lookup (ProjectInformation.class);
        final String projectDisplayName = pi == null ? 
            NbBundle.getMessage (J2SEActionProvider.class,"TEXT_BrokenPlatform_UnknownProjectName")
            : pi.getDisplayName();
        final DialogDescriptor dd = new DialogDescriptor(
            NbBundle.getMessage(J2SEActionProvider.class, "TEXT_BrokenPlatform", projectDisplayName),
            NbBundle.getMessage(J2SEActionProvider.class, "MSG_BrokenPlatform_Title"),
            true,
            new Object[] {closeOption},
            closeOption,
            DialogDescriptor.DEFAULT_ALIGN,
            null,
            null);
        dd.setMessageType(DialogDescriptor.WARNING_MESSAGE);
        final Dialog dlg = DialogDisplayer.getDefault().createDialog(dd);
        dlg.setVisible(true);
    }

    private URL generateAppletHTML(FileObject file) {
        URL url = null;
        try {
            String buildDirProp = project.evaluator().getProperty("build.dir"); //NOI18N
            String classesDirProp = project.evaluator().getProperty("build.classes.dir"); //NOI18N
            FileObject buildDir = this.updateHelper.getAntProjectHelper().resolveFileObject(buildDirProp);
            FileObject classesDir = this.updateHelper.getAntProjectHelper().resolveFileObject(classesDirProp);

            if (buildDir == null) {
                buildDir = FileUtil.createFolder(project.getProjectDirectory(), buildDirProp);
            }

            if (classesDir == null) {
                classesDir = FileUtil.createFolder(project.getProjectDirectory(), classesDirProp);
            }
            String activePlatformName = project.evaluator().getProperty("platform.active"); //NOI18N
            url = AppletSupport.generateHtmlFileURL(file, buildDir, classesDir, activePlatformName);
        } catch (FileStateInvalidException fe) {
            //ingore
        } catch (IOException ioe) {
            ErrorManager.getDefault().notify(ioe);
            return null;
        }
        return url;
    }

    private URL copyAppletHTML(FileObject file, String ext) {
        URL url = null;
        try {
            String buildDirProp = project.evaluator().getProperty("build.dir"); //NOI18N
            FileObject buildDir = updateHelper.getAntProjectHelper().resolveFileObject(buildDirProp);

            if (buildDir == null) {
                buildDir = FileUtil.createFolder(project.getProjectDirectory(), buildDirProp);
            }
            
            FileObject htmlFile = null;
            htmlFile = file.getParent().getFileObject(file.getName(), "html"); //NOI18N
            if (htmlFile == null) {
                htmlFile = file.getParent().getFileObject(file.getName(), "HTML"); //NOI18N
            }
            if (htmlFile == null) {
                return null;
            }
            
            FileObject existingFile = buildDir.getFileObject(htmlFile.getName(), htmlFile.getExt());
            if (existingFile != null) {
                existingFile.delete();
            }
            
            FileObject targetHtml = htmlFile.copy(buildDir, file.getName(), ext);
            
            if (targetHtml != null) {
                String activePlatformName = project.evaluator().getProperty("platform.active"); //NOI18N
                url = AppletSupport.getHTMLPageURL(targetHtml, activePlatformName);
            }
        } catch (FileStateInvalidException fe) {
            //ingore
        } catch (IOException ioe) {
            ErrorManager.getDefault().notify(ioe);
            return null;
        }
        return url;
    }
    
}
