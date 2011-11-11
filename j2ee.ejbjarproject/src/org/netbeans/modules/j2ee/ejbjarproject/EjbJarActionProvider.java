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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.j2ee.ejbjarproject;

import java.util.*;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.Session;
import org.netbeans.api.debugger.jpda.AttachingDICookie;
import org.netbeans.api.j2ee.core.Profile;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.project.runner.JavaRunner;
import org.netbeans.modules.j2ee.api.ejbjar.EjbProjectConstants;
import org.netbeans.modules.j2ee.common.project.ui.J2EEProjectProperties;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.modules.j2ee.deployment.plugins.api.ServerDebugInfo;
import org.netbeans.modules.j2ee.ejbjarproject.ui.customizer.EjbJarProjectProperties;
import org.netbeans.modules.java.api.common.ant.UpdateHelper;
import org.netbeans.modules.java.api.common.project.BaseActionProvider;
import org.netbeans.modules.java.api.common.project.ProjectProperties;
import org.netbeans.spi.java.classpath.ClassPathFactory;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.netbeans.spi.java.project.classpath.support.ProjectClassPathSupport;
import org.netbeans.spi.project.SingleMethod;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;


/** Action provider of the Web project. This is the place where to do
 * strange things to Web actions. E.g. compile-single.
 */
class EjbJarActionProvider extends BaseActionProvider {

    private static final String DIRECTORY_DEPLOYMENT_SUPPORTED = "directory.deployment.supported"; // NOI18N

    // Definition of commands
    
    private static final String COMMAND_VERIFY = "verify"; //NOI18N
    
    // Commands available from Web project
    private static final String[] supportedActions = {
        COMMAND_BUILD,
        COMMAND_CLEAN,
        COMMAND_REBUILD,
        COMMAND_COMPILE_SINGLE,
        COMMAND_RUN,
        COMMAND_RUN_SINGLE,
        COMMAND_DEBUG,
        COMMAND_DEBUG_SINGLE,
        EjbProjectConstants.COMMAND_REDEPLOY,
        JavaProjectConstants.COMMAND_JAVADOC,
        COMMAND_TEST,
        COMMAND_TEST_SINGLE,
        COMMAND_DEBUG_TEST_SINGLE,
        SingleMethod.COMMAND_RUN_SINGLE_METHOD,
        SingleMethod.COMMAND_DEBUG_SINGLE_METHOD,
        JavaProjectConstants.COMMAND_DEBUG_FIX,
        COMMAND_VERIFY,
        COMMAND_DELETE,
        COMMAND_COPY,
        COMMAND_MOVE,
        COMMAND_RENAME,
    };
    
    private static final String[] platformSensitiveActions = {
        COMMAND_BUILD,
        COMMAND_REBUILD,
        COMMAND_COMPILE_SINGLE,
        COMMAND_RUN_SINGLE,
        COMMAND_DEBUG_SINGLE,
        JavaProjectConstants.COMMAND_JAVADOC,
        COMMAND_TEST,
        COMMAND_TEST_SINGLE,
        COMMAND_DEBUG_TEST_SINGLE,
        SingleMethod.COMMAND_RUN_SINGLE_METHOD,
        SingleMethod.COMMAND_DEBUG_SINGLE_METHOD,
    };

    private final EjbJarProject project;

    /**Set of commands which are affected by background scanning*/
    private Set<String> bkgScanSensitiveActions;

    /**Set of commands which need java model up to date*/
    private Set<String> needJavaModelActions;

    private static final String[] actionsDisabledForQuickRun = {
        COMMAND_COMPILE_SINGLE,
        JavaProjectConstants.COMMAND_DEBUG_FIX,
    };
    
    /** Map from commands to ant targets */
    private Map<String,String[]> commands;
    
    public EjbJarActionProvider(EjbJarProject project, UpdateHelper updateHelper) {
        super(project, updateHelper, project.evaluator(), project.getSourceRoots(), project.getTestSourceRoots(), 
                project.getAntProjectHelper(), new CallbackImpl(new BaseActionProvider.CallbackImpl(project.getClassPathProvider()), project.getEjbModule()));
        this.project = project;
        commands = new HashMap<String,String[]>();
        commands.put(COMMAND_BUILD, new String[] {"dist"}); // NOI18N
        commands.put(COMMAND_CLEAN, new String[] {"clean"}); // NOI18N
        commands.put(COMMAND_REBUILD, new String[] {"clean", "dist"}); // NOI18N
        commands.put(COMMAND_COMPILE_SINGLE, new String[] {"compile-single"}); // NOI18N
        commands.put(COMMAND_RUN, new String[] {"run"}); // NOI18N
        commands.put(COMMAND_RUN_SINGLE, new String[] {"run-main"}); // NOI18N
        commands.put(EjbProjectConstants.COMMAND_REDEPLOY, new String[] {"run"}); // NOI18N
        commands.put(COMMAND_DEBUG, new String[] {"debug"}); // NOI18N
        commands.put(JavaProjectConstants.COMMAND_JAVADOC, new String[] {"javadoc"}); // NOI18N
        commands.put(COMMAND_TEST, new String[] {"test"}); // NOI18N
        commands.put(COMMAND_TEST_SINGLE, new String[] {"test-single"}); // NOI18N
        commands.put(COMMAND_DEBUG_TEST_SINGLE, new String[] {"debug-test"}); // NOI18N
        commands.put(JavaProjectConstants.COMMAND_DEBUG_FIX, new String[] {"debug-fix"}); // NOI18N
        commands.put(COMMAND_VERIFY, new String[] {"verify"}); // NOI18N
        commands.put(COMMAND_DEBUG_SINGLE, new String[] {"debug-single"}); // NOI18N
        this.bkgScanSensitiveActions = new HashSet<String>(Arrays.asList(
            COMMAND_RUN_SINGLE
        ));

        this.needJavaModelActions = new HashSet<String>(Arrays.asList(
            JavaProjectConstants.COMMAND_DEBUG_FIX
        ));
        setServerExecution(true);
    }

    @Override
    protected String[] getPlatformSensitiveActions() {
        return platformSensitiveActions;
    }

    @Override
    protected String[] getActionsDisabledForQuickRun() {
        return actionsDisabledForQuickRun;
    }

    @Override
    public Map<String, String[]> getCommands() {
        return commands;
    }

    @Override
    protected Set<String> getScanSensitiveActions() {
        return bkgScanSensitiveActions;
    }

    @Override
    protected Set<String> getJavaModelActions() {
        return needJavaModelActions;
    }

    @Override
    protected boolean isCompileOnSaveEnabled() {
        return Boolean.parseBoolean(getEvaluator().getProperty(EjbJarProjectProperties.J2EE_COMPILE_ON_SAVE));
    }

    @Override
    public String[] getSupportedActions() {
        return supportedActions;
    }

    @Override
    protected void updateJavaRunnerClasspath(String command, Map<String, Object> execProperties) {
        if (COMMAND_TEST_SINGLE.equals(command) || COMMAND_DEBUG_TEST_SINGLE.equals(command) ||
            SingleMethod.COMMAND_DEBUG_SINGLE_METHOD.equals(command) || SingleMethod.COMMAND_RUN_SINGLE_METHOD.equals(command) ||
            COMMAND_RUN_SINGLE.equals(command) || COMMAND_DEBUG_SINGLE.equals(command)) {
            FileObject fo = (FileObject)execProperties.get(JavaRunner.PROP_EXECUTE_FILE);
            ClassPath cp = getCallback().findClassPath(fo, ClassPath.EXECUTE);
            ClassPath cp2 = ClassPathFactory.createClassPath(
                    ProjectClassPathSupport.createPropertyBasedClassPathImplementation(
                    FileUtil.toFile(getProject().getProjectDirectory()), getEvaluator(), 
                    new String[]{"j2ee.platform.classpath", "j2ee.platform.embeddableejb.classpath"}));
            cp = ClassPathSupport.createProxyClassPath(cp, cp2);
            execProperties.put(JavaRunner.PROP_EXECUTE_CLASSPATH, cp);
            Collection<String> coll = (Collection<String>)execProperties.get(JavaRunner.PROP_RUN_JVMARGS);
            if (coll == null) {
                coll = new LinkedList<String>();
                execProperties.put(JavaRunner.PROP_RUN_JVMARGS, coll);
            }
            String s = getEvaluator().getProperty(EjbJarProjectProperties.RUNMAIN_JVM_ARGS);
            if (s != null && s.trim().length() > 0) {
                coll.add(s);
            }
            s = getEvaluator().getProperty(ProjectProperties.ENDORSED_CLASSPATH);
            if (s != null && s.trim().length() > 0) {
                ClassPath ecp = ClassPathFactory.createClassPath(
                        ProjectClassPathSupport.createPropertyBasedClassPathImplementation(
                        FileUtil.toFile(getProject().getProjectDirectory()), getEvaluator(),
                        new String[]{ProjectProperties.ENDORSED_CLASSPATH}));
                coll.add("-Xbootclasspath/p:\""+ecp.toString(ClassPath.PathConversionMode.WARN) +"\"");
            }
        }
    }

    @Override
    public String[] getTargetNames(String command, Lookup context, Properties p, boolean doJavaChecks) throws IllegalArgumentException {
        if (command.equals(COMMAND_RUN_SINGLE) ||command.equals(COMMAND_RUN) ||
            command.equals(EjbProjectConstants.COMMAND_REDEPLOY) ||command.equals(COMMAND_DEBUG) ||
            command.equals(COMMAND_DEBUG_SINGLE) || command.equals(JavaProjectConstants.COMMAND_DEBUG_FIX) ||
            command.equals( COMMAND_TEST_SINGLE) || command.equals(COMMAND_DEBUG_TEST_SINGLE)) {
            setDirectoryDeploymentProperty(p);
        }
        if (command.equals(COMMAND_RUN) || command.equals(EjbProjectConstants.COMMAND_REDEPLOY)) {
            if (!isSelectedServer()) {
                return null;
            }
            if (isDebugged()) {
                p.setProperty("is.debugged", "true");
            }
            if (command.equals(EjbProjectConstants.COMMAND_REDEPLOY)) {
                p.setProperty("forceRedeploy", "true"); //NOI18N
            } else {
                p.setProperty("forceRedeploy", "false"); //NOI18N
            }
            return commands.get(command);
        } else {
            return super.getTargetNames(command, context, p, doJavaChecks);
        }
    }

    private void setDirectoryDeploymentProperty(Properties p) {
        String instance = getAntProjectHelper().getStandardPropertyEvaluator().getProperty(EjbJarProjectProperties.J2EE_SERVER_INSTANCE);
        if (instance != null) {
            J2eeModuleProvider jmp = getProject().getLookup().lookup(J2eeModuleProvider.class);
            String sdi = jmp.getServerInstanceID();
            J2eeModule mod = jmp.getJ2eeModule();
            if (sdi != null && mod != null) {
                boolean cFD = Deployment.getDefault().canFileDeploy(instance, mod);
                p.setProperty(DIRECTORY_DEPLOYMENT_SUPPORTED, "" + cFD); // NOI18N
            }
        }
    }

    @Override
    public boolean isActionEnabled( String command, Lookup context ) {
        boolean res = super.isActionEnabled(command, context);
        if (res && command.equals(COMMAND_VERIFY)) {
            return ((EjbJarProject)getProject()).getEjbModule().hasVerifierSupport();
        }
        return res;
    }
    
    private boolean isDebugged() {
        J2eeModuleProvider jmp = getProject().getLookup().lookup(J2eeModuleProvider.class);
        ServerDebugInfo sdi = jmp.getServerDebugInfo();
        if (sdi == null) {
            return false;
        }
        //        server.getServerInstance().getStartServer().getDebugInfo(null);
        Session[] sessions = DebuggerManager.getDebuggerManager().getSessions();
        
        for (int i=0; i < sessions.length; i++) {
            Session s = sessions[i];
            if (s != null) {
                Object o = s.lookupFirst(null, AttachingDICookie.class);
                if (o != null) {
                    AttachingDICookie attCookie = (AttachingDICookie)o;
                    if (ServerDebugInfo.TRANSPORT_SHMEM.equals(sdi.getTransport())) {
                        if (attCookie.getSharedMemoryName().equalsIgnoreCase(sdi.getShmemName())) {
                            return true;
                        }
                    } else {
                        if (sdi.getHost() != null && sdi.getHost().equalsIgnoreCase(attCookie.getHostName())) {
                            if (attCookie.getPortNumber() == sdi.getPort()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    private boolean isSelectedServer() {
        String instance = getAntProjectHelper().getStandardPropertyEvaluator().getProperty(EjbJarProjectProperties.J2EE_SERVER_INSTANCE);
        if (instance != null) {
            String id = Deployment.getDefault().getServerID(instance);
            if (id != null) {
                return true;
            }
        }
        
        // if there is some server instance of the type which was used
        // previously do not ask and use it
        String serverType = getAntProjectHelper().getStandardPropertyEvaluator().getProperty(EjbJarProjectProperties.J2EE_SERVER_TYPE);
        if (serverType != null) {
            String instanceID = J2EEProjectProperties.getMatchingInstance(
                    serverType, J2eeModule.Type.EJB, project.getAPIEjbJar().getJ2eeProfile());
            if (instanceID != null) {
                setServerInstance(instanceID);
                return true;
            }
        }
        
        // no selected server => warning
        String msg = NbBundle.getMessage(EjbJarActionProvider.class, "MSG_No_Server_Selected"); //  NOI18N
        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(msg, NotifyDescriptor.WARNING_MESSAGE));
        return false;
    }
    
    private void setServerInstance(final String serverInstanceId) {
        EjbJarProjectProperties.setServerInstance((EjbJarProject)getProject(), getAntProjectHelper(), serverInstanceId);
    }
    
    private static class CallbackImpl implements Callback2 {

        private final BaseActionProvider.CallbackImpl impl;

        private final J2eeModuleProvider provider;

        public CallbackImpl(BaseActionProvider.CallbackImpl impl, J2eeModuleProvider provider) {
            this.impl = impl;
            this.provider = provider;
        }

        @Override
        public ClassPath getProjectSourcesClassPath(String type) {
            return impl.getProjectSourcesClassPath(type);
        }

        @Override
        public ClassPath findClassPath(FileObject file, String type) {
            return impl.findClassPath(file, type);
        }

        @Override
        public void antTargetInvocationFailed(String command, Lookup context) {
            Deployment.getDefault().resumeDeployOnSave(provider);
        }

        @Override
        public void antTargetInvocationFinished(String command, Lookup context, int result) {
            Deployment.getDefault().resumeDeployOnSave(provider);
        }

        @Override
        public void antTargetInvocationStarted(String command, Lookup context) {
            Deployment.getDefault().suspendDeployOnSave(provider);
        }
    }

}
