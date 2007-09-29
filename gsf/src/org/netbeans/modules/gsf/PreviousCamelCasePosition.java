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
package org.netbeans.modules.gsf;

import javax.swing.Action;
import javax.swing.text.JTextComponent;

/** @author Sandip V. Chitale (Sandip.Chitale@Sun.Com) */
public class PreviousCamelCasePosition extends AbstractCamelCasePosition {
    public static final String previousCamelCasePosition = "previous-camel-case-position"; //NOI18N

    public PreviousCamelCasePosition(Action originalAction, Language language) {
        this(previousCamelCasePosition, originalAction, language);
    }

    protected PreviousCamelCasePosition(String name, Action originalAction, Language language) {
        super(name, originalAction, language);
    }

    @Override
    protected int newOffset(JTextComponent textComponent) {
        return CamelCaseOperations.previousCamelCasePosition(textComponent, language);
    }

    @Override
    protected void moveToNewOffset(JTextComponent textComponent, int offset) {
        textComponent.setCaretPosition(offset);
    }
}
    
