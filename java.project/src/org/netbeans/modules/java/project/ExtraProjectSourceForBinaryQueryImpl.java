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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.java.project;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.java.queries.SourceForBinaryQueryImplementation2;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.Mutex;
import org.openide.util.BaseUtilities;

/**
 *
 * @author mkleint
 */
public final class ExtraProjectSourceForBinaryQueryImpl extends ProjectOpenedHook implements SourceForBinaryQueryImplementation2 {

    private static final String REF_START = "file.reference."; //NOI18N
    private static final String SOURCE_START = "source.reference."; //NOI18N
    
    private final AntProjectHelper helper;
    private final PropertyEvaluator evaluator;
    private final Map<URL,ExtraResult>  cache = new HashMap<URL,ExtraResult>();
    private PropertyChangeListener listener;
    private Map<URL, URL> mappings = new HashMap<URL, URL>();
    private final Object MAPPINGS_LOCK = new Object();
    private Project project;

    public ExtraProjectSourceForBinaryQueryImpl(Project prj, AntProjectHelper helper, PropertyEvaluator evaluator) {
        this.helper = helper;
        this.evaluator = evaluator;
        project = prj;
        listener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName() == null || evt.getPropertyName().startsWith(SOURCE_START)) {
                    checkAndRegisterExtraSources(getExtraSources());
                    Collection<ExtraResult> results = null;
                    synchronized (cache) {
                        results = new ArrayList<ExtraResult>(cache.values());
                    }
                    for (ExtraResult res : results) {
                        res.fire();
                    }
                }
            }
            
        };
    }

    /**
     * 
     * returns a result even if only the binary root is found in the project.
     * only returns null when the binary root is missing from project altogether.
     * @param binaryRoot
     * @return
     */
    public SourceForBinaryQueryImplementation2.Result findSourceRoots2 (URL binaryRoot) {
        synchronized (cache) {
            ExtraResult res = cache.get(binaryRoot);
            if (res != null) {
                return res;
            }
            if (mappings.containsKey(binaryRoot)) {
                res = new ExtraResult(binaryRoot);
                cache.put (binaryRoot, res);
                return res;
            }
        }
        return null;
    }
    
    public SourceForBinaryQuery.Result findSourceRoots(URL binaryRoot) {
        return this.findSourceRoots2(binaryRoot);
    }
    
    @Override
    protected void projectOpened() {
        checkAndRegisterExtraSources(getExtraSources());
        evaluator.addPropertyChangeListener(listener);
    }

    @Override
    protected void projectClosed()   {
        checkAndRegisterExtraSources(new HashMap<URL, URL>());
        evaluator.removePropertyChangeListener(listener);
    }
    

    private Map<URL, URL> getExtraSources() {
        return ProjectManager.mutex().readAccess(new Mutex.Action<Map<URL,URL>>() {
            @Override
            public Map<URL, URL> run() {
                Map<URL, URL> result = new HashMap<URL, URL>();
                Map<String, String> props = evaluator.getProperties();
                if (props != null) {
                    for (Map.Entry<String, String> entry : props.entrySet()) {
                        if (entry.getKey().startsWith(REF_START)) {
                            String val = entry.getKey().substring(REF_START.length());
                            String sourceKey = SOURCE_START + val;
                            String source[] = ExtraProjectJavadocForBinaryQueryImpl.stripJARPath(props.get(sourceKey));
                            File bin = PropertyUtils.resolveFile(FileUtil.toFile(helper.getProjectDirectory()), entry.getValue());
                            URL binURL = FileUtil.urlForArchiveOrDir(bin);
                            if (source[0] != null && binURL != null) {
                                File src = PropertyUtils.resolveFile(FileUtil.toFile(helper.getProjectDirectory()), source[0]);
                                // #138349 - ignore non existing paths or entries with undefined IDE variables
                                if (src.exists()) {
                                    try {
                                        URL url = BaseUtilities.toURI(src).toURL();
                                        if (FileUtil.isArchiveFile(url)) {
                                            url = FileUtil.getArchiveRoot(url);
                                        }
                                        if (source[1] != null) {
                                            assert url.toExternalForm().endsWith("!/") : url.toExternalForm();
                                            url = new URL(url.toExternalForm()+source[1]);
                                        }
                                        result.put(binURL, url);
                                    } catch (MalformedURLException ex) {
                                        Exceptions.printStackTrace(ex);
                                    }
                                }
                            }
                        }
                    }
                }
                return result;
            }
        });
    }
    
    private void checkAndRegisterExtraSources(Map<URL, URL> newvalues) {
        Set<URL> removed;
        Set<URL> added;
        synchronized (MAPPINGS_LOCK) {
            removed = new HashSet<URL>(mappings.keySet());
            removed.removeAll(newvalues.keySet());
            added = new HashSet<URL>(newvalues.keySet());
            added.removeAll(mappings.keySet());
            mappings = newvalues;
        }

                //TODO removing/adding the mapping can cause lost javadoc/source for other open projects..
                //the mappings should be probably static, or there should be a way to trigger recalculations 
                //in other ant projects from here
        
        for (URL rem : removed) {
            synchronized (cache) {
                ExtraResult res = cache.remove(rem);
                if (res != null) {
                    res.fire();
                }
            }
            try {
                URL jaradd = FileUtil.getArchiveFile(rem);
                if (jaradd != null) {
                    rem = jaradd;
                }
                FileOwnerQuery.markExternalOwner(rem.toURI(), null, FileOwnerQuery.EXTERNAL_ALGORITHM_TRANSIENT);
            } catch (URISyntaxException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        for (URL add : added) {
            try {
                URL jaradd = FileUtil.getArchiveFile(add);
                if (jaradd != null) {
                    add = jaradd;
                }
                FileOwnerQuery.markExternalOwner(add.toURI(), project, FileOwnerQuery.EXTERNAL_ALGORITHM_TRANSIENT);
            } catch (URISyntaxException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        
    }
    
    private class ExtraResult implements SourceForBinaryQueryImplementation2.Result {
        private URL binaryroot;
        private ChangeSupport chs = new ChangeSupport(this);
        
        public ExtraResult(URL binary) {
            binaryroot = binary;
        }

        public FileObject[] getRoots() {
            URL source = mappings.get(binaryroot);
            if (source != null) {
                FileObject fo = URLMapper.findFileObject(source);
                if ( fo != null ) {
                    return new FileObject[]{fo};
                }
            }
            return new FileObject[0];
        }
        
        public void fire() {
            chs.fireChange();
        }

        public void addChangeListener(ChangeListener l) {
            chs.addChangeListener(l);
        }

        public void removeChangeListener(ChangeListener l) {
            chs.removeChangeListener(l);
        }

        public boolean preferSources() {
            return false;
        }        
    }



}
