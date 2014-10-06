/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javascript2.nodejs.navigate;

import junit.framework.Test;
import org.netbeans.jellytools.EditorOperator;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.junit.NbModuleSuite;
import org.netbeans.modules.javascript2.nodejs.GeneralNodeJs;

/**
 *
 * @author vriha
 */
public class FuncNavTest extends GeneralNodeJs {

    public FuncNavTest(String args) {
        super(args);
    }

    public static Test suite() {
        return NbModuleSuite.create(
                NbModuleSuite.createConfiguration(FuncNavTest.class).addTest(
                        "openProject",
                        "testDirectProperty",
                        "testDirectProperty2",
                        "testDirectProperty3",
                        "testDirectProperty4",
                        "testDirectProperty5",
                        "testInstanceProperty",
                        "testInstanceProperty2",
                        "testAnonymProperty",
                        "testModule",
                        "testPrototypeProperty",
                        "testPrototypeProperty2"
                ).enableModules(".*").clusters(".*").honorAutoloadEager(true));
    }

    public void openProject() throws Exception {
        startTest();
        JemmyProperties.setCurrentTimeout("ActionProducer.MaxActionTime", 180000);
        openDataProjects("SimpleNode");
        evt.waitNoEvent(2000);
        openFile("app|maingt.js", "SimpleNode");
        endTest();
    }

    public void testDirectProperty() throws Exception {
        startTest();
        testGoToDeclaration(new EditorOperator("maingt.js"), 16);
        endTest();
    }

    public void testDirectProperty2() throws Exception {
        startTest();
        testGoToDeclaration(new EditorOperator("maingt.js"), 17);
        endTest();
    }

    public void testDirectProperty3() throws Exception {
        startTest();
        testGoToDeclaration(new EditorOperator("maingt.js"), 18);
        endTest();
    }

    public void testDirectProperty4() throws Exception {
        startTest();
        testGoToDeclaration(new EditorOperator("maingt.js"), 19);
        endTest();
    }

    public void testDirectProperty5() throws Exception {
        startTest();
        testGoToDeclaration(new EditorOperator("maingt.js"), 20);
        endTest();
    }

    public void testInstanceProperty() throws Exception {
        startTest();
        testGoToDeclaration(new EditorOperator("maingt.js"), 23);
        endTest();
    }

    public void testInstanceProperty2() throws Exception {
        startTest();
        testGoToDeclaration(new EditorOperator("maingt.js"), 24);
        endTest();
    }

    public void testAnonymProperty() throws Exception {
        startTest();
        testGoToDeclaration(new EditorOperator("maingt.js"), 25);
        endTest();
    }

    public void testPrototypeProperty() throws Exception {
        startTest();
        testGoToDeclaration(new EditorOperator("maingt.js"), 26);
        endTest();
    }

    public void testPrototypeProperty2() throws Exception {
        startTest();
        testGoToDeclaration(new EditorOperator("maingt.js"), 27);
        endTest();
    }

    public void testModule() throws Exception {
        startTest();
        testGoToDeclaration(new EditorOperator("maingt.js"), 14);
        endTest();
    }

}
