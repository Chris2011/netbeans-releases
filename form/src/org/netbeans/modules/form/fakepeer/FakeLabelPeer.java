/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/* $Id$ */

package org.netbeans.modules.form.fakepeer;

import java.awt.*;
import java.awt.peer.LabelPeer;
import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 *
 * @author Tran Duc Trung
 */

class FakeLabelPeer extends FakeComponentPeer implements LabelPeer
{
    FakeLabelPeer(Label target) {
        super(target);
    }

    Component createDelegate() {
        return new Delegate();
    }

    public void setText(String label) {
    }

    public void setAlignment(int alignment) {
    }

    //
    //
    //

    private class Delegate extends Component 
    {
        public void paint(Graphics g) {
            Label target =(Label) _target;

            String label = target.getText();
            if (label != null) {
                Dimension sz = target.getSize();

                Color c = getBackground();
                if (c == null)
                    c = SystemColor.control;
                g.setColor(c);
                g.fillRect(0, 0, sz.width, sz.height);
                g.setFont(target.getFont());

                FontMetrics fm = g.getFontMetrics();
                int w = fm.stringWidth(label),
                    h = fm.getHeight() - fm.getDescent(),
                    x = 0,
                    y = (sz.height - h) / 2 + h - 2,
                    alignment = target.getAlignment();

                if (alignment == Label.RIGHT)
                    x = sz.width - w;
                else if (alignment == Label.CENTER)
                    x =(sz.width - w) / 2;

                if (target.isEnabled()) {
                    c = getForeground();
                    if (c == null)
                        c = SystemColor.controlText;
                    g.setColor(c);
                } else {
                    g.setColor(SystemColor.controlLtHighlight);
                    g.drawString(label, x+1, y+1);
                    g.setColor(SystemColor.controlShadow);
                }

                g.drawString(label, x, y);
            }
        }

        public Dimension getMinimumSize() {
            String label =((Label)_target).getText();

            FontMetrics fm = this.getFontMetrics(_target.getFont());
            int w = fm.stringWidth(label);
            int h = fm.getHeight();

            return new Dimension(w + 4, h + 4);
        }
    }
}
