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
package org.netbeans.performance.j2ee.actions;

import junit.framework.Test;
import org.netbeans.jellytools.Bundle;
import org.netbeans.modules.performance.utilities.PerformanceTestCase;
import org.netbeans.modules.performance.utilities.CommonUtilities;
import org.netbeans.performance.j2ee.setup.J2EESetup;
import org.netbeans.jellytools.EditorOperator;
import org.netbeans.jellytools.NewFileWizardOperator;
import org.netbeans.jellytools.actions.NewFileAction;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JComboBoxOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;
import org.netbeans.modules.performance.guitracker.LoggingRepaintManager;

/**
 * Test of Open File Dialog
 *
 * @author lmartinek@netbeans.org
 */
public class CreateNewFileTest extends PerformanceTestCase {

    private NewFileWizardOperator wizard;

    private String project;
    private String category;
    private String fileType;
    private String fileName;
    private String packageName;
    private final boolean isEntity = false;

    /**
     * Creates a new instance of CreateNewFileTest
     *
     * @param testName
     */
    public CreateNewFileTest(String testName) {
        super(testName);
        expectedTime = 5000;
    }

    /**
     * Creates a new instance of CreateNewFileTest
     *
     * @param testName
     * @param performanceDataName
     */
    public CreateNewFileTest(String testName, String performanceDataName) {
        super(testName, performanceDataName);
        expectedTime = 5000;
    }

    public static Test suite() {
        return emptyConfiguration().addTest(J2EESetup.class).addTest(CreateNewFileTest.class).suite();
    }

    public void testCreateNewSessionBean() {
        WAIT_AFTER_OPEN = 10000;
        project = "TestApplication-ejb";
        category = Bundle.getStringTrimmed("org.netbeans.modules.j2ee.ejbcore.resources.Bundle", "Templates/J2EE");
        fileType = "Session Bean";
        fileName = "NewTestSession";
        packageName = "test.newfiles";
        doMeasurement();
    }

    /*    public void testCreateNewEntityBean() {
     WAIT_AFTER_OPEN = 10000;
     project = "TestApplication-ejb";
     category = "Enterprise";
     fileType = "Entity Bean";
     fileName = "NewTestEntity";
     packageName = "test.newfiles";
     isEntity = true;
     doMeasurement();
     }
     */
    @Override
    public void initialize() {
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void prepare() {
        new NewFileAction().performMenu();
        wizard = new NewFileWizardOperator();
        wizard.selectProject(project);
        wizard.selectCategory(category);
        wizard.selectFileType(fileType);
        wizard.next();
        JTextFieldOperator eBname;
        if (isEntity == true) {
            eBname = new JTextFieldOperator(wizard, 1);
        } else {
            eBname = new JTextFieldOperator(wizard);
        }
        eBname.setText(fileName + CommonUtilities.getTimeIndex());
        new JComboBoxOperator(wizard, 1).getTextField().setText(packageName);

    }

    @Override
    public ComponentOperator open() {
        repaintManager().addRegionFilter(LoggingRepaintManager.EDITOR_FILTER);
        repaintManager().addRegionFilter(LoggingRepaintManager.IGNORE_STATUS_LINE_FILTER);
        wizard.finish();
        return null;
    }

    @Override
    public void close() {
        repaintManager().resetRegionFilters();
        EditorOperator.closeDiscardAll();
    }
}
