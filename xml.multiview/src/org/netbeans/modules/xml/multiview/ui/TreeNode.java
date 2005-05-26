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

/*
 * TreeNode.java
 *
 * Created on May 26, 2005, 11:49 AM
 *
 */

package org.netbeans.modules.xml.multiview.ui;

/**
 *
 * @author mkuchtiak
 */
public interface TreeNode {
    public TreePanel getPanel();
    public String getPanelId();
}
