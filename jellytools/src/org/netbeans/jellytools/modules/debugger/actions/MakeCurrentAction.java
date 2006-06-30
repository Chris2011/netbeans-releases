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
package org.netbeans.jellytools.modules.debugger.actions;

import org.netbeans.jellytools.Bundle;
import org.netbeans.jellytools.actions.Action;

/** Used to call "Make Current" popup menu item in Sessions view.
 * @see Action
 * @author Jiri.Skrivanek@sun.com
 * @see org.netbeans.jellytools.modules.debugger.SessionsOperator
 */
public class MakeCurrentAction extends Action {

    // "Make Current"
    private static final String popupPath = Bundle.getStringTrimmed(
                                "org.netbeans.modules.debugger.ui.actions.Bundle",
                                "CTL_SwitchOn");
    
    /** Creates new MakeCurrentAction instance. */
    public MakeCurrentAction() {
        super(null, popupPath);
    }
}
