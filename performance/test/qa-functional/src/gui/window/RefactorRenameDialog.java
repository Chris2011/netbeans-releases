/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package gui.window;

import gui.Utilities;
import org.netbeans.jellytools.Bundle;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.ProjectsTabOperator;
import org.netbeans.jellytools.NbDialogOperator;

import org.netbeans.jemmy.operators.ComponentOperator;

/**
 * Test of Refactor | Rename Dialog
 *
 * @author  mmirilovic@netbeans.org
 */
public class RefactorRenameDialog extends org.netbeans.performance.test.utilities.PerformanceTestCase {
    
    private static Node testNode;
    private String TITLE, ACTION;
    
    /** Creates a new instance of RefactorRenameDialog */
    public RefactorRenameDialog(String testName) {
        super(testName);
        expectedTime = WINDOW_OPEN;
    }
    
    /** Creates a new instance of RefactorRenameDialog */
    public RefactorRenameDialog(String testName, String performanceDataName) {
        super(testName,performanceDataName);
        expectedTime = WINDOW_OPEN;
    }
    
    public void initialize() {
        String BUNDLE = "org.netbeans.modules.refactoring.ui.Bundle";
        TITLE = Bundle.getStringTrimmed(BUNDLE,"LBL_Rename");  // "Rename"
        ACTION = Bundle.getStringTrimmed(BUNDLE,"LBL_Action") + "|" + Bundle.getStringTrimmed(BUNDLE,"LBL_RenameAction"); // "Refactor|Rename..."
        testNode = new Node(new ProjectsTabOperator().getProjectRootNode("jEdit"),Utilities.SOURCE_PACKAGES + "|org.gjt.sp.jedit|jEdit.java");
    }
    
    public void prepare() {
        // do nothing
    }
    
    public ComponentOperator open() {
        // invoke Refactor | Rename from the popup menu
        testNode.performPopupAction(ACTION);
        return new NbDialogOperator(TITLE);
    }
    
}
