/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2010 Sun
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

package org.netbeans.core.netigso;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.logging.Level;
import junit.framework.Test;
import org.netbeans.Module;
import org.netbeans.ModuleManager;
import org.netbeans.NetigsoFramework;
import org.netbeans.SetupHid;
import org.netbeans.core.startup.Main;
import org.netbeans.junit.NbModuleSuite;
import org.netbeans.junit.NbTestCase;
import org.openide.util.Lookup;
import org.osgi.framework.launch.Framework;

/**
 *
 * @author Jaroslav Tulach <jtulach@netbeans.org>
 */
public class IntegrationTest extends NbTestCase {
    private File j1;

    public IntegrationTest(String name) {
        super(name);
    }

    public static Test suite() {
        return NbModuleSuite.create(
            NbModuleSuite.emptyConfiguration().addTest(
                IntegrationTest.class
            ).honorAutoloadEager(true).clusters(
                ".*"
            ).failOnException(Level.WARNING)/*.failOnMessage(Level.WARNING)*/
            .gui(false)
        );
    }

    @Override
    protected void setUp() throws Exception {
        File jars = new File(getWorkDir(), "jars");
        jars.mkdirs();

        j1 = SetupHid.createTestJAR(getDataDir(), jars, "simple-module.jar", null);
    }


    public void testCheckWhichContainerIsRunning() throws Exception {
        ModuleManager mgr = Main.getModuleSystem().getManager();
        mgr.mutexPrivileged().enterWriteAccess();
        Module m1;
        String mf = "Bundle-SymbolicName: org.foo\n" +
            "Bundle-Version: 1.1.0\n" +
            "Bundle-ManifestVersion: 2\n" +
            "Export-Package: org.foo";

        File jj1 = NetigsoHid.changeManifest(getWorkDir(), j1, mf);
        m1 = mgr.create(jj1, null, false, false, false);
        mgr.enable(m1);

        assertTrue("OSGi module is now enabled", m1.isEnabled());
        mgr.mutexPrivileged().exitWriteAccess();

        Object obj = Lookup.getDefault().lookup(NetigsoFramework.class);
        final Method m = obj.getClass().getDeclaredMethod("getFramework");
        m.setAccessible(true);
        Framework w = (Framework) m.invoke(obj);
        assertNotNull("Framework found", w);
        if (!w.getClass().getName().contains("felix")) {
            fail("By default the OSGi framework is felix: " + w.getClass());
        }


        ClassLoader fwloader = w.getClass().getClassLoader();
        Method addURLMethod = howEclipseFindsMethodToSupportFrameworks(fwloader.getClass());

        assertNotNull("addURL method found", addURLMethod);
    }

    private static Method howEclipseFindsMethodToSupportFrameworks(Class<?> clazz) {

        if (clazz == null) {
            return null;
        }
        try {
            Method result = clazz.getDeclaredMethod("addURL", URL.class);
            result.setAccessible(true);
            return result;
        } catch (NoSuchMethodException ex) {
        } catch (SecurityException ex) {
        }
        return howEclipseFindsMethodToSupportFrameworks(clazz.getSuperclass());
    }
}
