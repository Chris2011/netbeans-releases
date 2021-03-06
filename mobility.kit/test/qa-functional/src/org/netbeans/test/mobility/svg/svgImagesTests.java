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

package org.netbeans.test.mobility.svg;

//<editor-fold desc="imports">
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jellytools.FilesTabOperator;
import org.netbeans.jellytools.actions.ActionNoBlock;
//</editor-fold>

/**
 *
 * @author joshis
 */
public class svgImagesTests extends JellyTestCase {
    public static final String DIALOG_EXPORT = "Exporting as image";
    public static final String DIALOG_EXPORT_ANIMATION = "Exporting animation";
    public static final String JPG_1_FILE = "MobileApplication|src|svg|Halloween.jpg";
    public static final String JPG_2_FILE = "MobileApplication|src|svg|thumbsUp.jpg";
    public static final String MENU_EPORT_ANIMATION = "Export animation as image";
    public static final String MENU_EXPORT = "Export as image";
    public static final String SVG_1_FILE = "MobileApplication|src|svg|Halloween.svg";
    public static final String SVG_2_FILE = "MobileApplication|src|svg|thumbsUp.svg";
    
    /** Constructor required by JUnit */
    public svgImagesTests(String tname) {
        super(tname);
    }
    
    /** Creates suite from particular test cases. */
    public static NbTestSuite suite() {
        NbTestSuite suite = new NbTestSuite();
        suite.addTest(new svgImagesTests("PrepareProjectWithSVG"));
        suite.addTest(new svgImagesTests("exportAnimationAsImage"));
        suite.addTest(new svgImagesTests("exportAsImage"));
        return suite;
    }
    
    public void exportAnimationAsImage() {
        Node SVGFile = new Node(FilesTabOperator.invoke().tree(),SVG_1_FILE);
        SVGFile.select();
        ActionNoBlock exportAnimAsImg = new ActionNoBlock(null,MENU_EPORT_ANIMATION);
        exportAnimAsImg.perform(SVGFile);
        sleep(1000);
        NbDialogOperator exportAnimAsImgDialog = new NbDialogOperator(DIALOG_EXPORT_ANIMATION);
        sleep(1000);
        exportAnimAsImgDialog.btOK().push();
        sleep(1000);
        new Node(FilesTabOperator.invoke().tree(),JPG_1_FILE).select();
    }
    
    public void exportAsImage() {
        Node SVGFile = new Node(FilesTabOperator.invoke().tree(),SVG_2_FILE);
        SVGFile.select();
        ActionNoBlock exportAnimAsImg = new ActionNoBlock(null,MENU_EXPORT);
        exportAnimAsImg.perform(SVGFile);
        sleep(1000);
        NbDialogOperator exportAnimAsImgDialog = new NbDialogOperator(DIALOG_EXPORT);
        sleep(1000);
        exportAnimAsImgDialog.btOK().push();
        sleep(1000);
        new Node(FilesTabOperator.invoke().tree(),JPG_2_FILE).select();
    }
    
    /*public void PrepareProjectWithSVG() {
        NewProjectWizardOperator npwop = NewProjectWizardOperator.invoke();
        npwop.selectCategory("Samples|Java ME (MIDP)"); // XXX use Bundle.getString instead
        npwop.selectProject("SVG Demo"); 
        npwop.next();
        NewProjectNameLocationStepOperator step = new NewProjectNameLocationStepOperator();
        step.txtProjectLocation().setText(getWorkDirPath());
        step.txtProjectName().setText("mySVGDemo"); //NOI18N
        sleep(20);
        step.finish();
        ProjectSupport.waitScanFinished();
    }*/
    
    public void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

}




























