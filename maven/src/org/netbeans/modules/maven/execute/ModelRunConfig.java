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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.maven.execute;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.netbeans.modules.maven.NbMavenProjectImpl;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.maven.api.PluginPropertyUtils;
import org.netbeans.modules.maven.execute.model.NetbeansActionMapping;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 * run configuration backed up by model
 * @author mkleint
 */
public final class ModelRunConfig extends BeanRunConfig {
    
    private final static Logger LOG = Logger.getLogger(ModelRunConfig.class.getName());
    
    private final NetbeansActionMapping model;
    private final boolean fallback;
    
    public ModelRunConfig(Project proj, NetbeansActionMapping mod, String actionName, FileObject selectedFile, Lookup lookup, boolean fallback) {
        model = mod;
        this.fallback = fallback;
        NbMavenProjectImpl nbprj = proj.getLookup().lookup(NbMavenProjectImpl.class);
        setProject(nbprj);
        String label = ProjectUtils.getInformation(proj).getDisplayName();
        setExecutionName(label);        
        setTaskDisplayName(label);
        for (Map.Entry<String,String> entry : model.getProperties().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if(EXEC_ARGS.equals(key)) {                                
                if(value != null && value.contains(DEFAULT_EXEC_ARGS_CLASSPATH)) {
                    String execArgsByPom = getExecArgsByPom(model, proj);
                    if(execArgsByPom != null) {
                        value = execArgsByPom + " " + value;
                    }
                }        
            }
            setProperty(key, value);
        }
        setGoals(model.getGoals());
        setExecutionDirectory(ActionToGoalUtils.resolveProjectExecutionBasedir(mod, proj));
        setRecursive(mod.isRecursive());
        setActivatedProfiles(mod.getActivatedProfiles());
        setActionName(actionName);
        setFileObject(selectedFile);
        if (mod.getPreAction() != null) {
            setPreExecution(ActionToGoalUtils.createRunConfig(mod.getPreAction(), nbprj, lookup));
        }
        String react = mod.getReactor();
        if (react != null) {
            if ("am".equals(react) || "also-make".equals(react)) {
                setReactorStyle(ReactorStyle.ALSO_MAKE);
            } else if ("amd".equals(react) || "also-make-dependents".equals(react)) {
                setReactorStyle(ReactorStyle.ALSO_MAKE_DEPENDENTS);
            }
        }
    }

    public boolean isFallback() {
        return fallback;
    }        

    private static final String EXEC_ARGS = "exec.args"; // NOI18N
    private static final String DEFAULT_EXEC_ARGS_CLASSPATH = "-classpath %classpath"; // NOI18N
    
    static String getExecArgsByPom(NetbeansActionMapping model, Project proj) {
        if(Boolean.getBoolean("maven.doNotMergePomExecArgs")) { // NOI18N
            return null;
        }
        List<String> goals = model.getGoals();
        for (String goal : goals) {
            if ( goal.matches("org\\.codehaus\\.mojo\\:exec-maven-plugin\\:(.)+\\:exec")) // NOI18N
            {
                NbMavenProjectImpl projectImpl = proj instanceof NbMavenProjectImpl ? (NbMavenProjectImpl)proj : proj.getLookup().lookup(NbMavenProjectImpl.class);
                assert projectImpl != null : "Requires a maven project instance"; // NOI18N
                return PluginPropertyUtils.getPluginPropertyBuildable(projectImpl, "org.codehaus.mojo", "exec-maven-plugin", "exec", new ExecPluginConfigBuilder()); // NOI18N
            }
        }
        return null;
    }
    
    private static class ExecPluginConfigBuilder implements PluginPropertyUtils.ConfigurationBuilder<String> {
        @Override
        public String build(Xpp3Dom configRoot, ExpressionEvaluator eval) {
            if (configRoot != null) {
                Xpp3Dom domArgs = configRoot.getChild("arguments"); // NOI18N
                if (domArgs != null) {
                    Xpp3Dom[] children = domArgs.getChildren();
                    if(children == null || children.length == 0) {
                        return null;
                    }
                    Iterator<Xpp3Dom> it = Arrays.asList(children).iterator();
                    StringBuilder sb = new StringBuilder();
                    try {
                        while(it.hasNext()) {
                            Xpp3Dom xpp3Dom = it.next();                            
                            String val = null;
                            if ("argument".equals(xpp3Dom.getName())) { // NOI18N
                                val = xpp3Dom.getValue();
                                if (val == null || val.trim().isEmpty()) {
                                    continue;
                                }
                                val = val.trim();
                                if(val.contains("${")) {                                    
                                    // not evaluated prop? 
                                    LOG.log(Level.FINE, "skipping not evaluated property: {0}", val); // NOI18N
                                    val = null;
                                }
                                if ("-cp".equals(val) || "-classpath".equals(val)) { // NOI18N
                                    val = null;
                                    // the -cp/-classpath parameter is already in exec.args
                                    // lets assume that the following accords to
                                    // -cp/-classpath %classpath mainClass 
                                    // 1.) the classpath tag
                                    Xpp3Dom dom = it.next();                                    
                                    if (dom != null && "classpath".equals(dom.getName())) { // NOI18N
                                        Xpp3Dom[] deps = dom.getChildren("dependency"); // NOI18N
                                        if (deps == null || deps.length == 0) {
                                            // the classpath argument results to '-classpath %classpath'
                                            // and that is already part of exec.args -> do nothing
                                        } else {
                                            for (Xpp3Dom dep : deps) {
                                                if(dep != null) {
                                                    String d = dep.getValue();
                                                    if(d != null && !d.trim().isEmpty()) {
                                                        // explicitely declared deps - skip the whole thing.
                                                        // would need to be resolved and we do not want 
                                                        // to reimplement the whole exec plugin 
                                                        LOG.log(Level.FINE, "skipping whole args evaluation due to explicitely declared deps"); // NOI18N
                                                        return null;                                                                                                    
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    // 2.) the main class
                                    // doesn't necessaryli have to be after "-cp %classpath", so do not skip.
                                    // it.next(); 
                                }
                            }
                            if (val != null && !val.isEmpty()) {
                                if (sb.length() > 0) {
                                    sb.append(" "); // NOI18N
                                }
                                sb.append(val);
                            }                            
                        }  
                    } catch (NoSuchElementException e) {
                        // ignore and return what you got
                    }
                    return sb.length() > 0 ? sb.toString() : null;
                }
            }
            return null;
        }
    }    
    
}
