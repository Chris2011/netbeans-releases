/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.html.knockout.cc;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import junit.framework.Test;
import org.netbeans.jellytools.EditorOperator;
import org.netbeans.jellytools.modules.editor.CompletionJListOperator;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.junit.NbModuleSuite;
import org.netbeans.modules.html.knockout.GeneralKnockout;

/**
 *
 * @author vriha
 */
public class TemplatesTest extends GeneralKnockout {

    public TemplatesTest(String args) {
        super(args);
    }

    public static Test suite() {
        return NbModuleSuite.create(
                NbModuleSuite.createConfiguration(TemplatesTest.class).addTest(
                        "openProject",
                        "testBindedObject",
                        "testBindedForeach",
                        "testBindedNested",
                        "testBindedInner",
                        "testBindedInner2",
                        "testBindedNested2"
                ).enableModules(".*").clusters(".*").honorAutoloadEager(true));
    }

    public void openProject() throws Exception {
        startTest();
        JemmyProperties.setCurrentTimeout("ActionProducer.MaxActionTime", 180000);
        openDataProjects("sample");
        openFile("template.html", "sample");
        openFile("template.js", "sample");
        TemplatesTest.originalContent = new EditorOperator("template.html").getText();
        waitScanFinished();
        endTest();
    }

    public void testBindedObject() {
        startTest();
        doTest(17, 34, new String[]{"name", "credits", "date", "test", "$context", "Date", "Number", "Math"});
        endTest();
    }

    public void testBindedForeach() {
        startTest();
        doTest(24, 34, new String[]{"name1", "credits1", "date1", "test1", "$context", "Date", "Number", "Math"});
        endTest();
    }

    public void testBindedNested() {
        startTest();
        doTest(40, 36, new String[]{"season", "month", "$context", "Date", "Number", "Math"});
        endTest();
    }

    public void testBindedInner() {
        startTest();
        doTest(49, 35, new String[]{"getDay", "UTC"});
        endTest();
    }

    public void testBindedInner2() {
        startTest();
        doTest(55, 35, new String[]{"a", "create"});
        endTest();
    }

    public void testBindedNested2() {
        startTest();
        doTest(65, 34, new String[]{"name", "lastName"});
        endTest();
    }

    public void doTest(int lineNumber, int columnNumber, String[] result) {
        EditorOperator eo = new EditorOperator("template.html");
        eo.setCaretPosition(lineNumber, columnNumber);
        eo.typeKey(' ', InputEvent.CTRL_MASK);
        evt.waitNoEvent(500);
        CompletionInfo completion = getCompletion();
        CompletionJListOperator cjo = completion.listItself;
        checkCompletionItems(cjo, result);
    }

    @Override
    public void tearDown() throws Exception {
        EditorOperator eo = new EditorOperator("template.html");
        eo.typeKey('a', InputEvent.CTRL_MASK);
        eo.pressKey(KeyEvent.VK_DELETE);
        eo.insert(TemplatesTest.originalContent);
        eo.save();
        eo.pressKey(KeyEvent.VK_ESCAPE);
        eo.clickMouse();
        evt.waitNoEvent(2500);
    }

}
