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

/** Used to call "Window|Favorites" main menu item or CTRL+3 shortcut.
 * @see Action
 * @author Jiri.Skrivanek@sun.com
 */
public class FavoritesAction extends Action {
    private static final String allFilesMenu = Bundle.getStringTrimmed("org.netbeans.core.Bundle", "Menu/Window")
                                           + "|"
                                           + Bundle.getStringTrimmed("org.netbeans.modules.favorites.Bundle", "ACT_View");
    private static final Shortcut shortcut = new Shortcut(KeyEvent.VK_3, KeyEvent.CTRL_MASK);

    /** creates new FavoritesAction instance */    
    public FavoritesAction() {
        super(allFilesMenu, null, shortcut);
    }
}