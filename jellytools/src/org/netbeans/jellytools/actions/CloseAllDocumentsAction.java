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
package org.netbeans.jellytools.actions;

import java.awt.event.KeyEvent;
import org.netbeans.jellytools.Bundle;
import org.netbeans.jellytools.TopComponentOperator;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jemmy.operators.ComponentOperator;

/** Used to call "Close All Documents" popup menu item, "Window|Close All Documents"
 * main menu, "org.netbeans.core.windows.actions.CloseAllDocumentsAction"
 * or Ctrl+Shift+F4 shortcut.
 * @see Action
 * @author Jiri.Skrivanek@sun.com
 */
public class CloseAllDocumentsAction extends Action {
    
    /** Window main menu item. */
    private static final String windowItem = Bundle.getStringTrimmed("org.netbeans.core.Bundle", 
                                                                    "Menu/Window");
    /** "Close All Documents" popup menu item. */
    private static final String popupPath = Bundle.getStringTrimmed("org.netbeans.core.windows.actions.Bundle",
                                                                    "LBL_CloseAllDocumentsAction");
    /** "Windows|Close All Documents" main menu item */
    private static final String menuPath = windowItem+"|"+
                            Bundle.getStringTrimmed("org.netbeans.core.windows.actions.Bundle",
                                                    "CTL_CloseAllDocumentsAction");
    private static final Shortcut shortcut = new Shortcut(KeyEvent.VK_F4, KeyEvent.CTRL_MASK|KeyEvent.SHIFT_MASK);
    
    /** Create new CloseAllDocumentsAction instance. */
    public CloseAllDocumentsAction() {
        super(menuPath, popupPath, "org.netbeans.core.windows.actions.CloseAllDocumentsAction", shortcut);
    }

    /** Performs popup action Close All Documents on given component operator 
     * which is activated before the action. It only accepts TopComponentOperator
     * as parameter.
     * @param compOperator operator which should be activated
     */
    public void performPopup(ComponentOperator compOperator) {
        if(compOperator instanceof TopComponentOperator) {
            performPopup((TopComponentOperator)compOperator);
        } else {
            throw new UnsupportedOperationException(
                    "CloseAllDocumentsAction can only be called on TopComponentOperator.");
        }
    }

    /** Performs popup action Close All Documents on given top component operator 
     * which is activated before the action.
     * @param tco operator which should be activated
     */
    public void performPopup(TopComponentOperator tco) {
        tco.pushMenuOnTab(popupPath);
    }
    
    /** Throws UnsupportedOperationException because CloseAllDocumentsAction doesn't have
     * popup representation on nodes.
     * @param nodes array of nodes
     */
    public void performPopup(Node[] nodes) {
        throw new UnsupportedOperationException(
                    "CloseAllDocumentsAction doesn't have popup representation on nodes.");
    }

    /** Throws UnsupportedOperationException because CloseAllDocumentsAction doesn't have
     * representation on nodes.
     * @param nodes array of nodes
     */
    public void performAPI(Node[] nodes) {
        throw new UnsupportedOperationException(
                    "CloseAllDocumentsAction doesn't have popup representation on nodes.");
    }
    
    /** Throws UnsupportedOperationException because CloseAllDocumentsAction doesn't have
     * representation on nodes.
     * @param nodes array of nodes
     */
    public void performMenu(Node[] nodes) {
        throw new UnsupportedOperationException(
                    "CloseAllDocumentsAction doesn't have popup representation on nodes.");
    }
    
    /** Throws UnsupportedOperationException because CloseAllDocumentsAction doesn't have
     * representation on nodes.
     * @param nodes array of nodes
     */
    public void performShortcut(Node[] nodes) {
        throw new UnsupportedOperationException(
                    "CloseAllDocumentsAction doesn't have popup representation on nodes.");
    }
    
}