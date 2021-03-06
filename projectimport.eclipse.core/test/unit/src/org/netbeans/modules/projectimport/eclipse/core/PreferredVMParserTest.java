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

package org.netbeans.modules.projectimport.eclipse.core;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Martin Krauskopf
 */
public class PreferredVMParserTest extends ProjectImporterTestCase {

    public PreferredVMParserTest(String testName) {
        super(testName);
    }

    /** Also test 57661. */
    public void testParse() throws ProjectImporterException {
        String org_eclipse_jdt_launching_PREF_VM_XML =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<vmSettings defaultVM=\"57,org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType13,1135246830946\" defaultVMConnector=\"\">\n" +
                "<vmType id=\"org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType\">\n" +
                "<vm id=\"0\" name=\"jdk-6-beta-bin-b59c\" path=\"/space/java/jdk-6-beta-bin-b59c\"/>\n" +
                "<vm id=\"1135246830946\" name=\"jdk-6-rc-bin-b64\" path=\"/space/java/jdk-6-rc-bin-b64\">\n" +
                "<libraryLocations>\n" +
                "<libraryLocation jreJar=\"/space/java/0_lib/commons-collections-2.1.jar\" jreSrc=\"\" pkgRoot=\"\"/>\n" +
                "<libraryLocation jreJar=\"/space/java/jdk-6-rc-bin-b64/jre/lib/resources.jar\" jreSrc=\"/space/java/jdk-6-rc-bin-b64/src.zip\" pkgRoot=\"\"/>\n" +
                "<libraryLocation jreJar=\"/space/java/jdk-6-rc-bin-b64/jre/lib/rt.jar\" jreSrc=\"/space/java/jdk-6-rc-bin-b64/src.zip\" pkgRoot=\"\"/>\n" +
                "<libraryLocation jreJar=\"/space/java/jdk-6-rc-bin-b64/jre/lib/jsse.jar\" jreSrc=\"/space/java/jdk-6-rc-bin-b64/src.zip\" pkgRoot=\"\"/>\n" +
                "<libraryLocation jreJar=\"/space/java/jdk-6-rc-bin-b64/jre/lib/jce.jar\" jreSrc=\"/space/java/jdk-6-rc-bin-b64/src.zip\" pkgRoot=\"\"/>\n" +
                "<libraryLocation jreJar=\"/space/java/jdk-6-rc-bin-b64/jre/lib/charsets.jar\" jreSrc=\"/space/java/jdk-6-rc-bin-b64/src.zip\" pkgRoot=\"\"/>\n" +
                "<libraryLocation jreJar=\"/space/java/jdk-6-rc-bin-b64/jre/lib/ext/sunjce_provider.jar\" jreSrc=\"\" pkgRoot=\"\"/>\n" +
                "<libraryLocation jreJar=\"/space/java/jdk-6-rc-bin-b64/jre/lib/ext/sunpkcs11.jar\" jreSrc=\"\" pkgRoot=\"\"/>\n" +
                "<libraryLocation jreJar=\"/space/java/jdk-6-rc-bin-b64/jre/lib/ext/dnsns.jar\" jreSrc=\"\" pkgRoot=\"\"/>\n" +
                "<libraryLocation jreJar=\"/space/java/jdk-6-rc-bin-b64/jre/lib/ext/localedata.jar\" jreSrc=\"\" pkgRoot=\"\"/>\n" +
                "</libraryLocations>\n" +
                "</vm>\n" +
                "</vmType>\n" +
                "</vmSettings>\n";
        
        Map<String,String> jdks = PreferredVMParser.parse(org_eclipse_jdt_launching_PREF_VM_XML);
        
        Map<String,String> expectedJDKs = new HashMap<String,String>();
        expectedJDKs.put("jdk-6-rc-bin-b64", "/space/java/jdk-6-rc-bin-b64");
        expectedJDKs.put("org.eclipse.jdt.launching.JRE_CONTAINER", "/space/java/jdk-6-rc-bin-b64");
        expectedJDKs.put("jdk-6-beta-bin-b59c", "/space/java/jdk-6-beta-bin-b59c");
        
        assertEquals("JDKs were successfully parsed", expectedJDKs, jdks);
    }
    
}
