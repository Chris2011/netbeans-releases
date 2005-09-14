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

package org.netbeans.modules.apisupport.project.ui;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.modules.apisupport.project.TestBase;
import org.netbeans.modules.apisupport.project.suite.SuiteProject;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.nodes.Node;
import org.openide.nodes.NodeAdapter;

/**
 * Test functionality of {@link SuiteLogicalView}.
 *
 * @author Martin Krauskopf
 */
public class SuiteLogicalViewTest extends TestBase {
    
    public SuiteLogicalViewTest(String name) {
        super(name);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        clearWorkDir();
        TestBase.initializeBuildProperties(getWorkDir());
    }
    
    public void testModulesNode() throws Exception {
        SuiteProject suite1 = TestBase.generateSuite(getWorkDir(), "suite1");
        TestBase.generateSuiteComponent(suite1, "module1a");
        SuiteLogicalView.ModulesNode modulesNode = new SuiteLogicalView.ModulesNode(suite1);
        assertEquals("one children", 1, modulesNode.getChildren().getNodes().length);
        
        TestBase.generateSuiteComponent(suite1, "module1b");
        Thread.sleep(SuiteLogicalView.MODULES_NODE_SCHEDULE * 2);
        assertEquals("two children", 2, modulesNode.getChildren().getNodes().length);
    }
    
    public void testNameAndDisplayName() throws Exception {
        SuiteProject p = TestBase.generateSuite(getWorkDir(), "Sweet Stuff");
        Node n = ((LogicalViewProvider) p.getLookup().lookup(LogicalViewProvider.class)).createLogicalView();
        assertEquals("Sweet_Stuff", n.getName());
        assertEquals("Sweet Stuff", n.getDisplayName());
        NL nl = new NL();
        n.addNodeListener(nl);
        EditableProperties ep = p.getHelper().getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
        ep.setProperty("app.name", "sweetness");
        ep.setProperty("app.title", "Sweetness is Now!");
        p.getHelper().putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, ep);
        assertEquals(new HashSet(Arrays.asList(new String[] {Node.PROP_NAME, Node.PROP_DISPLAY_NAME})), nl.changed);
        assertEquals("Sweet_Stuff", n.getName());
        assertEquals("Sweetness is Now!", n.getDisplayName());
    }
    
    private static final class NL extends NodeAdapter {
        public final Set/*<String>*/ changed = new HashSet();
        public void propertyChange(PropertyChangeEvent evt) {
            changed.add(evt.getPropertyName());
        }
    }
    
}
