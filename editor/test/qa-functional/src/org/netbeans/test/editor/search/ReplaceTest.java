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

package org.netbeans.test.editor.search;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.KeyEvent;
import junit.framework.Test;
import junit.textui.TestRunner;
import org.netbeans.test.editor.lib.EditorTestCase;
import org.netbeans.jellytools.EditorOperator;
import org.netbeans.jellytools.MainWindowOperator;
import org.netbeans.jellytools.modules.editor.Replace;
import org.netbeans.jellytools.modules.editor.ReplaceBarOperator;
import org.netbeans.jellytools.modules.editor.SearchBarOperator;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.EventTool;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.operators.WindowOperator;
import org.netbeans.junit.NbModuleSuite;

/**
 *
 * @author Roman Strobl
 */
public class ReplaceTest extends EditorTestCase {
    
    private static int REPLACE_TIMEOUT = 100;
    
    /** Creates a new instance of ReplaceTest */
    public ReplaceTest(String testMethodName) {
        super(testMethodName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();        
        System.out.println("#############");
        System.out.println("# Starting "+this.getName());
        System.out.println("#############");
    }

    @Override
    protected void tearDown() throws Exception {
        System.out.println("#############");
        System.out.println("# Finished "+this.getName());
        System.out.println("#############");
        super.tearDown();
    }
    
    
                
    /**
     * TC1 - open and close replace dialog
     */
    public void testReplaceDialogOpenClose() {
        openDefaultProject();
        openDefaultSampleFile();
        try {
            EditorOperator editor = getDefaultSampleEditorOperator();
            ReplaceBarOperator bar = ReplaceBarOperator.invoke(editor);            
            new EventTool().waitNoEvent(100);
            assertTrue(bar.isVisible());            
            bar.getContainerOperator().pushKey(KeyEvent.VK_ESCAPE);
            new EventTool().waitNoEvent(200);
            assertFalse(bar.isVisible());                                    
        } finally {            
            closeFileWithDiscard();
        }
    }
    
    /**
     * TC2 - Replace Dialog Open - Selection
     */
    public void testReplaceSelectionRepeated() {
        openDefaultProject();
        openDefaultSampleFile();
        try {
            EditorOperator editor = getDefaultSampleEditorOperator();           
            // choose the "testReplaceSelectionRepeated" word            
            editor.setCaretPosition(1,1);            
            new EventTool().waitNoEvent(REPLACE_TIMEOUT);
            ReplaceBarOperator bar = ReplaceBarOperator.invoke(editor);            
            // check only selected checkboxes
            bar.uncheckAll();            
            new EventTool().waitNoEvent(REPLACE_TIMEOUT);
            bar.getSearchBar().findCombo().typeText("testReplaceSelectionRepeated");
            bar.replaceCombo().clearText();
            bar.replaceCombo().typeText("testReplaceSelectionRepeated2");            
            new EventTool().waitNoEvent(REPLACE_TIMEOUT);            
            bar.replaceButton().doClick();            
            new EventTool().waitNoEvent(REPLACE_TIMEOUT);            
            editor.setCaretPosition(12,1);                        
            bar.getSearchBar().findCombo().clearText();
            bar.getSearchBar().findCombo().typeText("testReplaceSelectionRepeated");            
            bar.replaceCombo().clearText();
            bar.replaceCombo().typeText("testReplaceSelectionRepeated2");               
            new EventTool().waitNoEvent(REPLACE_TIMEOUT);
            bar.replaceButton().doClick();            
            bar.replaceButton().doClick();
            new EventTool().waitNoEvent(REPLACE_TIMEOUT);                       
            bar.closeButton().doClick();
            ref(editor.getText());
            compareReferenceFiles();            
        } finally {            
            closeFileWithDiscard();
        }
    }
    
    /**
     * TC3 - Replace Dialog Combo Box
     */
//    public void testReplaceDialogComboBox() {
//        openDefaultProject();
//        openDefaultSampleFile();
//        try {
//            EditorOperator editor = getDefaultSampleEditorOperator();
//            editor.setCaretPosition(1,1);
//            ReplaceBarOperator bar = ReplaceBarOperator.invoke(editor);
//            bar.uncheckAll();
//            SearchBarOperator search = bar.getSearchBar();            
//            //search.wrapAroundCheckBox().setSelected(true);            
//            search.findCombo().clearText();
//            search.findCombo().typeText("package");
//            bar.replaceCombo().clearText();
//            bar.replaceCombo().typeText("pakaz");
//            bar.replaceButton().doClick();
//            new EventTool().waitNoEvent(REPLACE_TIMEOUT);           
//            bar.replaceButton().doClick();
//            // check status bar
////            waitForLabel("'package' not found");
//		
//            editor.setCaretPosition(1,1);
//            new EventTool().waitNoEvent(REPLACE_TIMEOUT);           
//            search.findCombo().clearText();
//            search.findCombo().typeText("class");
//            bar.replaceCombo().clearText();
//            bar.replaceCombo().typeText("klasa");
//            bar.replaceButton().doClick();
//            new EventTool().waitNoEvent(REPLACE_TIMEOUT);           
//            bar.replaceButton().doClick();
//            // check status bar
////            waitForLabel("'class' not found");
//            
//            editor.setCaretPosition(1,1);
//            new EventTool().waitNoEvent(REPLACE_TIMEOUT);           
//            search.findCombo().clearText();
//            search.findCombo().typeText("testReplaceDialogComboBox");
//            bar.replaceCombo().clearText();
//            bar.replaceCombo().typeText("testReplaceDialogComboBox2");
//            bar.replaceButton().doClick();
//            // check status bar
////            waitForLabel("'testReplaceDialogComboBox' found at 13:35");
//            
//		new EventTool().waitNoEvent(REPLACE_TIMEOUT);           
//            boolean[] found = new boolean[3];
//            String[] etalon = {"testReplaceDialogComboBox","class","package"};
//            for (int i = 0; i<search.findCombo().getItemCount(); i++) {
//		    System.out.println(search.findCombo().getItemAt(i));
//		    if(i<found.length) 
//			  found[i] = etalon[i].equals((String)search.findCombo().getItemAt(i));
//            }
//            for (boolean b : found) {
//                assertTrue(b);
//            }                        
//            
//            String[] etalonReplace = {"testReplaceDialogComboBox2","klasa","pakaz"};
//            
//            for (int i = 0; i<bar.replaceCombo().getItemCount(); i++) {
//		    System.out.println(bar.replaceCombo().getItemAt(i));
//		    if(i<found.length)
//			  found[i] = etalonReplace[i].equals((String)bar.replaceCombo().getItemAt(i));                
//            }
//            for (boolean b : found) {
//                assertTrue(b);
//            }                                                
//            new EventTool().waitNoEvent(REPLACE_TIMEOUT);
//            bar.closeButton().doClick();            
//            ref(editor.getText());
//            compareReferenceFiles();            
//        } finally {
//            closeFileWithDiscard();
//        }
//    }
    
    /**
     * TC4 - Replace Match Case
     */
    public void testReplaceMatchCase() {
        openDefaultProject();
        openDefaultSampleFile();
        try {
            EditorOperator editor = getDefaultSampleEditorOperator();
            ReplaceBarOperator bar = ReplaceBarOperator.invoke(editor);
            editor.setCaretPosition(1,1);
            bar.uncheckAll();
            final SearchBarOperator searchBar = bar.getSearchBar();
            searchBar.matchCaseCheckBox().setSelected(true);
            searchBar.findCombo().clearText();
            searchBar.findCombo().typeText("testCase");            
            bar.replaceCombo().clearText();
            bar.replaceCombo().typeText("xxxxXxxx");
            bar.replaceButton().doClick();                                    
            ref(editor.getText());
            compareReferenceFiles();
        } finally {
            closeFileWithDiscard();
        }
    }
    
    /**
     * TC5 - Replace All
     */
    public void testReplaceAll() {
        openDefaultProject();
        openDefaultSampleFile();
        try {
            EditorOperator editor = getDefaultSampleEditorOperator();           
            editor.setCaretPosition(1,1);
            ReplaceBarOperator bar = ReplaceBarOperator.invoke(editor);
            bar.uncheckAll();
            final SearchBarOperator searchBar = bar.getSearchBar();
            searchBar.matchCaseCheckBox().setSelected(true);
            searchBar.findCombo().clearText();
            searchBar.findCombo().typeText("testWord");            
            bar.replaceCombo().clearText();
            bar.replaceCombo().typeText("xxxxXxxx");
            
            bar.replaceAll().doClick();
//            waitForLabel("14 of 14 items replaced");
            ref(editor.getText());
            compareReferenceFiles();
        } finally {
            closeFileWithDiscard();
        }
    }
    
    /**
     * TC6 - Replace in Selection Only
     */
    public void testReplaceInSelectionOnly() {
        openDefaultProject();
        openDefaultSampleFile();
        try {
            EditorOperator editor = getDefaultSampleEditorOperator();
            editor.select(20, 24);
            ReplaceBarOperator bar = ReplaceBarOperator.invoke(editor);
            bar.uncheckAll();
            final SearchBarOperator searchBar = bar.getSearchBar();
            searchBar.findCombo().clearText();
            searchBar.findCombo().typeText("testWord");
            bar.replaceCombo().clearText();
            bar.replaceCombo().typeText("xxxxXxxx");            
            new EventTool().waitNoEvent(REPLACE_TIMEOUT);           
            bar.replaceAll().doClick();
            // check status bar
//            waitForLabel("5 of 5 items replaced");                                    
            ref(editor.getText());
            compareReferenceFiles();
            
        } finally {
            closeFileWithDiscard();
        }
    }
           
    /**
     * Waits for label to appear on Status Bar, checks it 10 times before
     * failing.
     * @param label label which should be displayed on status bar
     */
    public void waitForLabel(final String label) {   
        String statusText = null;
        for (int i = 0; i<30; i++) {
            statusText = MainWindowOperator.getDefault().getStatusText();
            if (label.equals(statusText)) {
                break;
            }
            new EventTool().waitNoEvent(100);
        }        
        System.out.println(statusText);        
        assertEquals(label, MainWindowOperator.getDefault().getStatusText());
    }
            
    public static void main(String[] args) {
        TestRunner.run(ReplaceTest.class);                
    }
}
