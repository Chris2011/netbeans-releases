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
package org.netbeans.modules.visual.action;

import org.netbeans.api.visual.action.RectangularSelectDecorator;
import org.netbeans.api.visual.model.ObjectState;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

/**
 * @author David Kaspar
 */
public final class DefaultRectangularSelectDecorator implements RectangularSelectDecorator {

    private Scene scene;

    public DefaultRectangularSelectDecorator (Scene scene) {
        this.scene = scene;
    }

    public Widget createSelectionWidget () {
        Widget widget = new Widget (scene);
        widget.setBorder (scene.getLookFeel ().getMiniBorder (ObjectState.NORMAL.deriveSelected (true)));
        return widget;
    }

}
