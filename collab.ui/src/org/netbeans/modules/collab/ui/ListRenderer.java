/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package org.netbeans.modules.collab.ui;

import java.awt.*;
import javax.swing.*;

import com.sun.collablet.CollabPrincipal;
import com.sun.collablet.Conversation;

public final class ListRenderer extends DefaultListCellRenderer {
    private ListModel _model = null;
    private Color _fontColor = null;
    private int _fontSize = 0;

    public ListRenderer(ListModel m) {
        super();
        this._model = m;
        setOpaque(false);
    }

    public void setFontColor(Color color) {
        _fontColor = color;
    }

    public void setFontSize(int size) {
        _fontSize = size;
    }

    public void setFontSize(String size) {
        setFontSize(Integer.parseInt(size));
    }

    final public Component getListCellRendererComponent(
        JList list, Object value, int index, boolean isSelected, boolean cellHasFocus
    ) {
        if (_model != null) {
            JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            lbl.setText(_model.getName(value));
            lbl.setOpaque(isSelected);

            // set the icon for login or never login people..
            lbl.setIcon(_model.getIcon(value));
            ToolTipManager.sharedInstance().registerComponent(list);

            if (value instanceof CollabPrincipal) {
                CollabPrincipal nu = (CollabPrincipal) value;

                list.setToolTipText(nu.getDisplayName());
            } else if (value instanceof Conversation) {
                Conversation c = (Conversation) value;
                list.setToolTipText(c.getDisplayName());
            } else if (value instanceof String) {
                list.setToolTipText((String) value);
            }

            if ((_fontColor != null) && (!isSelected)) {
                lbl.setForeground(_fontColor);
            }

            if (_fontSize > 0) {
                lbl.setFont(lbl.getFont().deriveFont((float) _fontSize));
            }

            return this;
        } else {
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }
}
