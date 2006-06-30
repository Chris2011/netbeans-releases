/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.apache.tools.ant.module.nodes;

import java.util.Arrays;
import java.util.List;
import org.apache.tools.ant.module.nodes.AntProjectChildren;
import org.apache.tools.ant.module.xml.AntProjectSupport;
import org.netbeans.junit.NbTestCase;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 * Test children of an Ant project.
 * @author Jesse Glick
 */
public class AntProjectChildrenTest extends NbTestCase {

    public AntProjectChildrenTest(String name) {
        super(name);
    }
    
    private FileObject testdir;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        testdir = FileUtil.toFileObject(this.getDataDir());
        assertNotNull("testdir unit/data exists", testdir);
    }
    
    public void testBasicChildren() throws Exception {
        FileObject simple = testdir.getFileObject("targetlister/simple.xml");
        assertNotNull("simple.xml found", simple);
        assertEquals("correct children of simple.xml",
            Arrays.asList(new String[] {"described", "-internal", "-internal-described", "main", "undescribed"}),
            displayNamesForChildrenOf(simple));
    }
    
    public void testImportedChildren() throws Exception {
        // #44491 caused this to fail.
        FileObject importing = testdir.getFileObject("targetlister/importing.xml");
        assertNotNull("importing.xml found", importing);
        assertEquals("correct children of importing.xml",
            Arrays.asList(new String[] {"main", "subtarget1", "subtarget2", "subtarget3", "whatever"}),
            displayNamesForChildrenOf(importing));
    }
    
    private static List<String> displayNamesForChildrenOf(FileObject fo) {
        Children ch = new AntProjectChildren(new AntProjectSupport(fo));
        Node[] nodes = ch.getNodes(true);
        return displayNamesFor(nodes);
    }
    
    private static List<String> displayNamesFor(Node[] nodes) {
        String[] names = new String[nodes.length];
        for (int i = 0; i < nodes.length; i++) {
            names[i] = nodes[i].getDisplayName();
        }
        return Arrays.asList(names);
    }
    
}
