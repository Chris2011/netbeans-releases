/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010-2015 Oracle and/or its affiliates. All rights reserved.
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

package org.netbeans.modules.glassfish.eecommon.api.config;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.j2ee.dd.api.common.RootInterface;
import org.netbeans.modules.j2ee.dd.api.webservices.Webservices;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.sun.dd.api.ASDDVersion;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * Java EE module helper.
 * <p/>
 * @author Peter Williams, Tomas Kraus
 */
public abstract class J2eeModuleHelper {

    /** Web application meta data directory. */
    public static final String WEB_INF = JavaEEModule.WEB_INF;

    /** GlassFish specific meta data file for version 1 and 2. */
    public static final String GF_WEB_XML_V1
            = WEB_INF + File.separatorChar + "sun-web.xml";

    /** GlassFish specific meta data file for version 3 and 4. */
    public static final String GF_WEB_XML_V2
            = WEB_INF + File.separatorChar + "glassfish-web.xml";

    private static final Map<Object, J2eeModuleHelper> helperMap;
    private static final Map<Object, J2eeModuleHelper> gfhelperMap;

    static {
        Map<Object, J2eeModuleHelper> map = new HashMap<Object, J2eeModuleHelper>();
        map.put(J2eeModule.Type.WAR, new WebDDHelper());
        map.put(J2eeModule.Type.EJB, new EjbDDHelper());
        map.put(J2eeModule.Type.EAR, new EarDDHelper());
        map.put(J2eeModule.Type.CAR, new ClientDDHelper());
        helperMap = Collections.unmodifiableMap(map);
        map = new HashMap<Object, J2eeModuleHelper>();
        map.put(J2eeModule.Type.WAR, new WebDDHelper(GF_WEB_XML_V2, null));
        map.put(J2eeModule.Type.EJB, new EjbDDHelper("META-INF/glassfish-ejb-jar.xml", "META-INF/glassfish-cmp-mappings.xml"));
        map.put(J2eeModule.Type.EAR, new EarDDHelper("META-INF/glassfish-application.xml", null));
        map.put(J2eeModule.Type.CAR, new ClientDDHelper("META-INF/glassfish-application-client.xml",null));
        gfhelperMap = Collections.unmodifiableMap(map);
    }

    /**
     * Check for <code>WEB-INF/glassfish-web.xml</code> in Java EE module.
     * <p/>
     * @return Value of <code>true</code> when
     * <code>WEB-INF/glassfish-web.xml</code> exists and is readable
     * or <code>false</code> otherwise.
     */
    public static boolean isGlassFishWeb(final J2eeModule module) {
         File webXml = module.getDeploymentConfigurationFile(GF_WEB_XML_V2);
         return webXml.canRead();
    }

    public static final J2eeModuleHelper getSunDDModuleHelper(Object type) {
        return helperMap.get(type);
    }

    public static final J2eeModuleHelper getGlassfishDDModuleHelper(Object type) {
        return gfhelperMap.get(type);
    }

    public static final J2eeModuleHelper getWsModuleHelper(String primarySunDDName) {
        return new WebServerDDHelper(primarySunDDName);
    }

    private final Object moduleType;
    private final String standardDDName;
    private final String webserviceDDName;
    private final String primarySunDDName;
    private final String secondarySunDDName;

    private J2eeModuleHelper(Object type, String stdDD, String wsDD, String sunDD, String cmpDD) {
        moduleType = type;
        standardDDName = stdDD;
        webserviceDDName = wsDD;
        primarySunDDName = sunDD;
        secondarySunDDName = cmpDD;
    }

    public Object getJ2eeModule() {
        return moduleType;
    }

    public String getStandardDDName() {
        return standardDDName;
    }

    public String getWebserviceDDName() {
        return webserviceDDName;
    }

    public String getPrimarySunDDName() {
        return primarySunDDName;
    }

    public String getSecondarySunDDName() {
        return secondarySunDDName;
    }

    public File getPrimarySunDDFile(J2eeModule module) {
        return primarySunDDName != null ? 
            module.getDeploymentConfigurationFile(primarySunDDName) : null;
    }

    public File getSecondarySunDDFile(J2eeModule module) {
        return secondarySunDDName != null ?
            module.getDeploymentConfigurationFile(secondarySunDDName) : null;
    }

    public RootInterface getStandardRootDD(J2eeModule module) {
        RootInterface stdRootDD = null;
        if (standardDDName != null) {
            File ddFile = module.getDeploymentConfigurationFile(standardDDName);
            if (ddFile.exists()) {
                FileUtil.normalizeFile(ddFile);
                FileObject ddFO = FileUtil.toFileObject(ddFile);
                try {
                    stdRootDD = getStandardRootDD(ddFO);
                } catch (IOException ex) {
                    Logger.getLogger("glassfish-eecommon").log(Level.INFO, ex.getLocalizedMessage(), ex);
                }
            }
        }
        return stdRootDD;    
    }

    public Webservices getWebServicesRootDD(J2eeModule module) {
        Webservices wsRootDD = null;
        if (webserviceDDName != null) {
            File ddFile = module.getDeploymentConfigurationFile(webserviceDDName);
            if (ddFile.exists()) {
                FileObject ddFO = FileUtil.toFileObject(ddFile);
                try {
                    wsRootDD = org.netbeans.modules.j2ee.dd.api.webservices.DDProvider.getDefault().getDDRoot(ddFO);
                } catch (IOException ex) {
                    Logger.getLogger("glassfish-eecommon").log(Level.INFO, ex.getLocalizedMessage(), ex);
                }
            }
        }
        return wsRootDD;
    }

    protected abstract RootInterface getStandardRootDD(FileObject ddFO) throws IOException;

    protected abstract ASDDVersion getMinASVersion(String j2eeModuleVersion, ASDDVersion defaultVersion);

    public static class WebDDHelper extends J2eeModuleHelper {

        private WebDDHelper() {
            this(GF_WEB_XML_V1, null);
        }

        private WebDDHelper(String dd1, String dd2) {
            super(J2eeModule.WAR, J2eeModule.WEB_XML, J2eeModule.WEBSERVICES_XML,
                    dd1, dd2);
        }

        @Override
        protected RootInterface getStandardRootDD(final FileObject ddFO) throws IOException {
            return org.netbeans.modules.j2ee.dd.api.web.DDProvider.getDefault().getDDRoot(ddFO);
        }

        @Override
        protected ASDDVersion getMinASVersion(String j2eeModuleVersion, ASDDVersion defaultVersion) {
            ASDDVersion result = defaultVersion;
            ServletVersion servletVersion = ServletVersion.getServletVersion(j2eeModuleVersion);
            if (ServletVersion.SERVLET_2_4.equals(servletVersion)) {
                result = ASDDVersion.SUN_APPSERVER_8_1;
            } else if (ServletVersion.SERVLET_2_5.equals(servletVersion)) {
                result = ASDDVersion.SUN_APPSERVER_9_0;
            } else if (ServletVersion.SERVLET_3_0.equals(servletVersion)) {
                result = ASDDVersion.SUN_APPSERVER_10_0;
            }
            return result;
        }

    }

    public static class WebServerDDHelper extends J2eeModuleHelper {

        private WebServerDDHelper(String primarySunDDName) {
            super(J2eeModule.WAR, J2eeModule.WEB_XML, J2eeModule.WEBSERVICES_XML,
                    primarySunDDName, null);
        }

        @Override
        protected RootInterface getStandardRootDD(final FileObject ddFO) throws IOException {
            return org.netbeans.modules.j2ee.dd.api.web.DDProvider.getDefault().getDDRoot(ddFO);
        }

        @Override
        protected ASDDVersion getMinASVersion(String j2eeModuleVersion, ASDDVersion defaultVersion) {
            ASDDVersion result = defaultVersion;
            ServletVersion servletVersion = ServletVersion.getServletVersion(j2eeModuleVersion);
            if (ServletVersion.SERVLET_2_4.equals(servletVersion)) {
                result = ASDDVersion.SUN_APPSERVER_8_1;
            } else if (ServletVersion.SERVLET_2_5.equals(servletVersion)) {
                result = ASDDVersion.SUN_APPSERVER_9_0;
            } else if (ServletVersion.SERVLET_3_0.equals(servletVersion)) {
                result = ASDDVersion.SUN_APPSERVER_10_0;
            }
            return result;
        }

    }


    public static class EjbDDHelper extends J2eeModuleHelper {

        private EjbDDHelper() {
            this("META-INF/sun-ejb-jar.xml", "META-INF/sun-cmp-mappings.xml");
        }

        private EjbDDHelper(String dd1, String dd2) {
            super(J2eeModule.EJB, "ejb-jar.xml", "webservices.xml",
                dd1,dd2);
        }

        @Override
        protected RootInterface getStandardRootDD(final FileObject ddFO) throws IOException {
            return org.netbeans.modules.j2ee.dd.api.ejb.DDProvider.getDefault().getDDRoot(ddFO);
        }

        @Override
        protected ASDDVersion getMinASVersion(String j2eeModuleVersion, ASDDVersion defaultVersion) {
            ASDDVersion result = defaultVersion;
            EjbJarVersion ejbJarVersion = EjbJarVersion.getEjbJarVersion(j2eeModuleVersion);
            if (EjbJarVersion.EJBJAR_2_1.equals(ejbJarVersion)) {
                result = ASDDVersion.SUN_APPSERVER_8_1;
            } else if (EjbJarVersion.EJBJAR_3_0.equals(ejbJarVersion)) {
                result = ASDDVersion.SUN_APPSERVER_9_0;
            } else if (EjbJarVersion.EJBJAR_3_1.equals(ejbJarVersion)) {
                result = ASDDVersion.SUN_APPSERVER_10_0;
            }
            return result;
        }

    }

    public static class EarDDHelper extends J2eeModuleHelper {

        private EarDDHelper() {
            this("META-INF/sun-application.xml", null);
        }

        private EarDDHelper(String dd1, String dd2) {
            super(J2eeModule.EAR, "application.xml", null, dd1, dd2);
        }

        @Override
        protected RootInterface getStandardRootDD(final FileObject ddFO) throws IOException {
            return org.netbeans.modules.j2ee.dd.api.application.DDProvider.getDefault().getDDRoot(ddFO);
        }

        @Override
        protected ASDDVersion getMinASVersion(String j2eeModuleVersion, ASDDVersion defaultVersion) {
            ASDDVersion result = defaultVersion;
            ApplicationVersion applicationVersion = ApplicationVersion.getApplicationVersion(j2eeModuleVersion);
            if (ApplicationVersion.APPLICATION_1_4.equals(applicationVersion)) {
                result = ASDDVersion.SUN_APPSERVER_8_1;
            } else if (ApplicationVersion.APPLICATION_5_0.equals(applicationVersion)) {
                result = ASDDVersion.SUN_APPSERVER_9_0;
            } else if (ApplicationVersion.APPLICATION_6_0.equals(applicationVersion)) {
                result = ASDDVersion.SUN_APPSERVER_10_0;
            }
            return result;
        }

    }

    public static class ClientDDHelper extends J2eeModuleHelper {

        private ClientDDHelper() {
            this("META-INF/sun-application-client.xml", null);
        }

        private ClientDDHelper(String dd1, String dd2) {
            super(J2eeModule.CLIENT, "application-client.xml", null, dd1, dd2);
        }

        @Override
        protected RootInterface getStandardRootDD(final FileObject ddFO) throws IOException {
            return org.netbeans.modules.j2ee.dd.api.client.DDProvider.getDefault().getDDRoot(ddFO);
        }

        @Override
        protected ASDDVersion getMinASVersion(String j2eeModuleVersion, ASDDVersion defaultVersion) {
            ASDDVersion result = defaultVersion;
            AppClientVersion appClientVersion = AppClientVersion.getAppClientVersion(j2eeModuleVersion);
            if (AppClientVersion.APP_CLIENT_1_4.equals(appClientVersion)) {
                result = ASDDVersion.SUN_APPSERVER_8_1;
            } else if (AppClientVersion.APP_CLIENT_5_0.equals(appClientVersion)) {
                result = ASDDVersion.SUN_APPSERVER_9_0;
            } else if (AppClientVersion.APP_CLIENT_6_0.equals(appClientVersion)) {
                result = ASDDVersion.SUN_APPSERVER_10_0;
            }
            return result;
        }

    }



}
