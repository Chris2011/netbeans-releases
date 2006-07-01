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
package org.openide.awt;

/**
 * An implementation of a toggle toolbar button.
 * @deprecated This class was a workaround for JDK 1.2 era Windows Look and
 * feel issues.  All implementation code has been removed.  It is here only
 * for backward compatibility.
 */
public class ToolbarToggleButton extends javax.swing.JToggleButton {
    /** generated Serialized Version UID */
    static final long serialVersionUID = -4783163952526348942L;

    public ToolbarToggleButton() {
    }

    public ToolbarToggleButton(javax.swing.Icon icon) {
        super(icon);
    }

    public ToolbarToggleButton(javax.swing.Icon icon, boolean selected) {
        super(icon, selected);
    }
}
