/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tomcat5.util;

import java.io.File;
import junit.textui.TestRunner;
import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.modules.tomcat5.TomcatFactory55;
import org.netbeans.modules.tomcat5.TomcatFactory55Test;
import org.netbeans.modules.tomcat5.TomcatManager;
import org.openide.util.Utilities;
import org.netbeans.modules.tomcat5.util.LogSupport.LineInfo;
import org.netbeans.modules.tomcat5.util.LogViewer.ContextLogSupport;

/**
 *
 * @author sherold
 */
public class LogViewerTest extends NbTestCase {
    
    private File datadir;
    
    public LogViewerTest(String testName) {
        super(testName);
    }
    
    public static NbTestSuite suite() {
        NbTestSuite suite = new NbTestSuite();
        suite.addTest(new LogViewerTest("testAnalyzeLine"));
        return suite;
    }
    
    protected void setUp() throws Exception {
        super.setUp ();
        datadir = getDataDir();
    }
    
    public void testAnalyzeLine() {
        
        String log[] = new String[] {
            "Jan 5, 2006 6:46:45 PM org.apache.catalina.core.StandardWrapperValve invoke",
            "SEVERE: Servlet.service() for servlet HyperlinkTest threw exception",
            "java.lang.IllegalStateException",
            "       at t.HyperlinkTest$1.run(HyperlinkTest.java:24)",
            "       at t.HyperlinkTest.processRequest(HyperlinkTest.java:27)",
            "       at foo.bar",
        };
        
        String files[] = new String[] {
            null,
            null,
            null,
            "t/HyperlinkTest.java",
            "t/HyperlinkTest.java",
            null,
        };
        
        int lines[] = new int[] {
            -1,
            -1,
            -1,
            24,
            27,
            -1,
        };
        
        String message[] = new String[] {
            null,
            null,
            null,
            "java.lang.IllegalStateException",
            "java.lang.IllegalStateException",
            null,
        };
        
        ContextLogSupport sup = new ContextLogSupport("foo", "bar");
        for (int i = 0; i < log.length; i++) {
            LineInfo nfo = sup.analyzeLine(log[i]);
            assertEquals("Path \"" + nfo.path() + "\" incorrectly recognized from: " + log[i],
                         files[i], nfo.path());
            assertEquals("Line \"" + nfo.line() + "\" incorrectly recognized from: " + log[i],
                         lines[i], nfo.line());
            assertEquals("Message \"" + nfo.message() + "\" incorrectly recognized from: " + log[i],
                         message[i], nfo.message());
        }
    }
    
    public static void main(java.lang.String[] args) {
        TestRunner.run(suite());
    }
}
