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

import org.netbeans.api.visual.action.MoveControlPointProvider;
import org.netbeans.api.visual.widget.ConnectionWidget;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author David Kaspar
 */
public final class OrthogonalMoveControlPointProvider implements MoveControlPointProvider {

    public java.util.List<Point> locationSuggested (ConnectionWidget connectionWidget, int index, Point suggestedLocation) {
        java.util.List<Point> controlPoints = connectionWidget.getControlPoints ();
        int last = controlPoints.size () - 1;
        if (index <= 0 || index >= last)
            return null;

        Point pointPre = controlPoints.get (index - 1);
        Point pointIndex = controlPoints.get (index);
        Point pointPost = controlPoints.get (index + 1);

        boolean changeX = true;
        boolean changeY = true;
        if (index <= 1) {
            Point pointFirst = controlPoints.get (0);
            if (pointFirst.x == pointIndex.x)
                changeX = false;
            if (pointFirst.y == pointIndex.y)
                changeY = false;
        }
        if (index >= last - 1) {
            Point pointLast = controlPoints.get (last);
            if (pointLast.x == pointIndex.x)
                changeX = false;
            if (pointLast.y == pointIndex.y)
                changeY = false;
        }

        Point newPointPre = new Point (pointPre);
        Point newPointIndex = new Point (pointIndex);
        Point newPointPost = new Point (pointPost);

        if (changeX) {
            final int x = suggestedLocation.x;
            if (pointPre.x == pointIndex.x)
                newPointPre.x = x;
            newPointIndex.x = x;
            if (pointPost.x == pointIndex.x)
                newPointPost.x = x;
        }
        if (changeY) {
            final int y = suggestedLocation.y;
            if (pointPre.y == pointIndex.y)
                newPointPre.y = y;
            newPointIndex.y = y;
            if (pointPost.y == pointIndex.y)
                newPointPost.y = y;
        }

        ArrayList<Point> list = new ArrayList<Point> (controlPoints);
        list.set (index - 1, newPointPre);
        list.set (index, newPointIndex);
        list.set (index + 1, newPointPost);
        return list;
    }

}
