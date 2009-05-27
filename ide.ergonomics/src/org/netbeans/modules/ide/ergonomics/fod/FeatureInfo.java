/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.ide.ergonomics.fod;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathExpression;
import org.openide.filesystems.FileObject;
import org.w3c.dom.Document;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.XMLFileSystem;
import org.openide.modules.ModuleInfo;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.xml.sax.SAXException;

/** Description of <em>Feature On Demand</em> capabilities and a 
 * factory to create new instances.
 *
 * @author Jaroslav Tulach <jtulach@netbeans.org>, Jirka Rechtacek <jrechtacek@netbeans.org>
 */
public final class FeatureInfo {
    private final URL delegateLayer;
    private FileSystem fs;
    private final Set<String> cnbs;
    private final Map<String,String> nbproject = new HashMap<String,String>();
    private final Map<Object[],String> files = new HashMap<Object[],String>();
    private Properties properties;
    final String clusterName;
    private Boolean cacheEnabled;
    private Boolean cachePresent;

    private FeatureInfo(String clusterName, Set<String> cnbs, URL delegateLayer, Properties p) {
        this.cnbs = cnbs;
        this.delegateLayer = delegateLayer;
        this.properties = p;
        this.clusterName = clusterName;
    }
    

    public static FeatureInfo create(String clusterName, URL delegateLayer, URL bundle) throws IOException {
        Properties p = new Properties();
        p.load(bundle.openStream());
        String cnbs = p.getProperty("cnbs");
        assert cnbs != null : "Error loading from " + bundle; // NOI18N
        TreeSet<String> s = new TreeSet<String>();
        s.addAll(Arrays.asList(cnbs.split(",")));

        FeatureInfo info = new FeatureInfo(clusterName, s, delegateLayer, p);
        final String prefix = "nbproject.";
        final String prefFile = "project.file.";
        final String prefXPath = "project.xpath.";
        for (Object k : p.keySet()) {
            String key = (String) k;
            if (key.startsWith(prefix)) {
                info.nbproject(
                    key.substring(prefix.length()),
                    p.getProperty(key)
                );
            }
            if (key.startsWith(prefFile)) {
                try {
                    info.projectFile(key.substring(prefFile.length()), null, p.getProperty(key));
                } catch (XPathExpressionException ex) {
                    IOException e = new IOException(ex.getMessage());
                    e.initCause(ex);
                    throw e;
                }
            }
            if (key.startsWith(prefXPath)) {
                try {
                    String xpaths = p.getProperty(key);
                    for (String xp : xpaths.split(",")) {
                        info.projectFile(key.substring(prefXPath.length()), xp, "");
                    }
                } catch (XPathExpressionException ex) {
                    IOException e = new IOException(ex.getMessage());
                    e.initCause(ex);
                    throw e;
                }
            }
        }
        FileObject fo = info.getXMLFileSystem().findResource("Ergonomics/AntBasedProjectTypes"); // NOI18N
        if (fo != null) {
            for (FileObject o : fo.getChildren()) {
                String type = (String)o.getAttribute("type"); // NOI18N
                info.nbproject(type, "org.netbeans.modules.project.ant.AntBasedGenericType"); // NOI18N
            }
        }
        return info;
    }

    public Object getProjectImporter() {
        return properties.getProperty("projectImporter");
    }

    String getPreferredCodeNameBase() {
        return properties.getProperty("mainModule");
    }
    String getFeatureCodeNameBase() {
        String f = properties.getProperty("featureModule");
        if (f != null) {
            return f.length() == 0 ? null : f;
        }
        return getPreferredCodeNameBase();
    }

    public final boolean isEnabled() {
        Boolean e = cacheEnabled;
        if (e != null) {
            return e;
        }

        for (ModuleInfo mi : Lookup.getDefault().lookupAll(ModuleInfo.class)) {
            if (cnbs.contains(mi.getCodeNameBase())) {
                if (!FeatureManager.showInAU(mi)) {
                    continue;
                }
                return cacheEnabled = mi.isEnabled();
            }
        }
        return cacheEnabled = false;
    }

    public final URL getLayerURL() {
        return delegateLayer;
    }

    public synchronized FileSystem getXMLFileSystem() {
        if (fs == null) {
            if (doParseXML()) {
                URL url = delegateLayer;
                if (url != null) {
                    try {
                        fs = new XMLFileSystem(url);
                        return fs;
                    } catch (SAXException ex) {
                        FoDFileSystem.LOG.log(Level.SEVERE, "Cannot parse: " + url, ex);
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
            fs = FileUtil.createMemoryFileSystem();
        }
        return fs;
    }

    /** @return 0 = no
     *          1 = yes
     *          2 = I am interested to be turned on when this project is opened
     */
    int isProject(FeatureProjectFactory.Data data) {
        FeatureProjectFactory.LOG.log(Level.FINE, "Checking project {0}", data);
        int toRet;
        if (isNbProject(data)) {
            toRet = 1;
        } else {
            if (files.isEmpty()) {
                toRet = 0;
            } else {
                toRet = 0;
                for (Object[] required : files.keySet()) {
                    String s = (String)required[0];
                    FeatureProjectFactory.LOG.log(Level.FINER, "    checking file {0}", s);
                    if (data.hasFile(s)) {
                        FeatureProjectFactory.LOG.log(Level.FINER, "    found", s);
                        if (data.isDeepCheck() && required[1] != null) {
                            XPathExpression e = (XPathExpression)required[1];
                            Document content = data.dom(s);
                            try {
                                String res = e.evaluate(content);
                                FeatureProjectFactory.LOG.log(
                                    Level.FINER,
                                    "Parsed result {0} of type {1}",
                                    new Object[] {
                                        res, res == null ? null : res.getClass()
                                    }
                                );
                                if (res != null && res.length() > 0) {
                                    toRet = 2;
                                }
                            } catch (XPathExpressionException ex) {
                                FeatureProjectFactory.LOG.log(Level.INFO, "Cannot parse " + data, ex);
                            }
                        } else {
                            toRet = 1;
                        }
                        break;
                    }
                }
            }
        }
        FeatureProjectFactory.LOG.log(Level.FINE, "  isProject: {0}", toRet);
        return toRet;
    }

    public final Set<String> getCodeNames() {
        return Collections.unmodifiableSet(cnbs);
    }

    public boolean isPresent() {
        Boolean p = cachePresent;
        if (p != null) {
            return p;
        }

        Set<String> codeNames = new HashSet<String>(getCodeNames());
        for (ModuleInfo moduleInfo : Lookup.getDefault().lookupAll(ModuleInfo.class)) {
            codeNames.remove(moduleInfo.getCodeNameBase());
        }
        return cachePresent = codeNames.isEmpty();
    }

    void clearCache() {
        cachePresent = null;
        cacheEnabled = null;
    }

    @Override
    public String toString() {
        return "FeatureInfo[" + clusterName + "]";
    }
    
    private boolean isNbProject(FeatureProjectFactory.Data data) {
        if (nbproject.isEmpty()) {
            return false;
        } else {
            if (!data.hasFile("nbproject/project.xml")) { // NOI18N
                FeatureProjectFactory.LOG.log(Level.FINEST, "    nbproject/project.xml not found"); // NOI18N
                return false;
            }
            if (!data.isDeepCheck()) {
                FeatureProjectFactory.LOG.log(Level.FINEST, "    no deep check, OK"); // NOI18N
                return true;
            }
            String text = data.is("nbproject/project.xml"); // NOI18N
            if (text == null) {
                return false;
            }
            for (String t : nbproject.keySet()) {
                final String pattern = "<type>" + t + "</type>"; // NOI18N
                if (text.indexOf(pattern) >= 0) { // NOI18N
                    FeatureProjectFactory.LOG.log(Level.FINEST, "    '" + pattern + "' found, OK"); // NOI18N
                    return true;
                } else {
                    FeatureProjectFactory.LOG.log(Level.FINEST, "    '" + pattern + "' not found"); // NOI18N
                }
            }
            FeatureProjectFactory.LOG.log(Level.FINEST, "    not accepting"); // NOI18N
            return false;
        }
    }

    final void nbproject(String prjType, String clazz) {
        nbproject.put(prjType, clazz);
    }
    final void projectFile(String file, String xpath, String clazz) throws XPathExpressionException {
        XPathExpression e = null;
        if (xpath != null) {
            e = XPathFactory.newInstance().newXPath().compile(xpath);
        }

        files.put(new Object[] { file, e }, clazz);
    }
    static Map<String,String> nbprojectTypes() {
        Map<String,String> map = new HashMap<String, String>();

        for (FeatureInfo info : FeatureManager.features()) {
            map.putAll(info.nbproject);
        }
        return map;
    }

    static Map<String,String> projectFiles() {
        Map<String,String> map = new HashMap<String, String>();

        for (FeatureInfo info : FeatureManager.features()) {
            for (Map.Entry<Object[], String> e : info.files.entrySet()) {
                if (e.getValue().length() > 0) {
                    map.put((String)(e.getKey()[0]), e.getValue());
                }
            }
        }
        return map;
    }

    static boolean doParseXML() {
        return !Boolean.getBoolean("org.netbeans.modules.ide.ergonomics.noparse"); // NOI18N
    }
}
