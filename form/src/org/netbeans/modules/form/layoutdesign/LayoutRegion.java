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

package org.netbeans.modules.form.layoutdesign;

import java.awt.Rectangle;

/**
 * Defines a rectangular area in the layout. For each dimension it holds the
 * starting, ending, and center positions. For vertical dimension also the
 * "baseline" position.
 */

class LayoutRegion implements LayoutConstants {

    // number of tracked points for each dimension
    static final int[] POINT_COUNT = new int[] { 3, 4 };

    // all points - used as param where no particular but all points should be used
    static final int ALL_POINTS = Integer.MAX_VALUE;

    // no point - used as param where no point should be processed
    static final int NO_POINT = Integer.MIN_VALUE;

    // unknown point position value
    static final int UNKNOWN = Integer.MIN_VALUE;

    // array of tracked positions - for each tracked point within each dimension
    // - for HORIZONTAL dimension there are LEADING, TRAILING, CENTER points
    // - for VERTICAL dimension there are LEADING, TRAILING, CENTER, BASELINE points
    // (the constants can be used as indexes to the array)
    int positions[][];

    LayoutRegion() {
        positions = new int[DIM_COUNT][];
        positions[HORIZONTAL] = new int[] { UNKNOWN, UNKNOWN, UNKNOWN };
        positions[VERTICAL] = new int[] { UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN }; // including BASELINE
    }

    LayoutRegion(LayoutRegion reg) {
        this();
        set(reg);
    }

    LayoutRegion(Rectangle bounds, int baselinePos) {
        this();
        set(bounds, baselinePos);
    }

    boolean isSet() {
        return isSet(HORIZONTAL) && isSet(VERTICAL);
    }

    boolean isSet(int dimension) {
        return positions[dimension][LEADING] != UNKNOWN
               && positions[dimension][TRAILING] != UNKNOWN;
    }

    int size(int dimension) {
        int trail = positions[dimension][TRAILING];
        int lead = positions[dimension][LEADING];
        return trail != UNKNOWN && lead != UNKNOWN ? trail - lead : UNKNOWN;
    }

    /**
     * Sets up the region based on given bounds rectangle and baseline position.
     */
    void set(Rectangle bounds, int baselinePos) {
        int[] horiz = positions[HORIZONTAL];
        horiz[LEADING] = bounds.x;
        horiz[TRAILING] = bounds.x + bounds.width;
        horiz[CENTER] = bounds.x + bounds.width / 2;

        int[] vert = positions[VERTICAL];
        vert[LEADING] = bounds.y;
        vert[TRAILING] = bounds.y + bounds.height;
        vert[CENTER] = bounds.y + bounds.height / 2;
        vert[BASELINE] = baselinePos;
    }

    /**
     * Converts the region to a rectangle.
     * @param bounds rectangle to be set (output)
     * @return Rectangle updated with actual position values (same instance as
     *         passed in as parameter)
     */
    Rectangle toRectangle(Rectangle bounds) {
        int[] horiz = positions[HORIZONTAL];
        bounds.x = horiz[LEADING];
        bounds.width = horiz[TRAILING] - bounds.x;
        int[] vert = positions[VERTICAL];
        bounds.y = vert[LEADING];
        bounds.height = vert[TRAILING] - bounds.y;
        return bounds;
    }

    /**
     * Copies all position values from another region.
     */
    void set(LayoutRegion reg) {
        for (int i=0; i < DIM_COUNT; i++) {
            set(i, reg);
        }
    }

    /**
     * Copies position values of given dimension from another region.
     */
    void set(int dimension, LayoutRegion reg) {
        int[] pos = positions[dimension];
        int[] setPos = reg.positions[dimension];
        for (int j=0; j < pos.length; j++) {
            pos[j] = setPos[j];
        }
    }

    void set(int dimension, int leading, int trailing) {
        int[] pos = positions[dimension];
        if (pos[LEADING] != leading || pos[TRAILING] != trailing) {
            pos[LEADING] = leading;
            pos[TRAILING] = trailing;
            pos[CENTER] = leading != UNKNOWN && trailing != UNKNOWN ?
                          (leading + trailing) / 2 : UNKNOWN;
            if (dimension == VERTICAL) {
                pos[BASELINE] = UNKNOWN; // undefined after change
            }
        }
    }

    /**
     * Reverts the region to unset state - like it was just after the creation.
     */
    void reset() {
        for (int i=0; i < DIM_COUNT; i++) {
            int[] pos = positions[i];
            for (int j=0; j < pos.length; j++)
                pos[j] = UNKNOWN;
        }
    }

    /**
     * @param points array of alignment constants (LEADING or TRAILING) defining
     *        for each dimension which point should be moved (can be null to
     *        move everything)
     */
    void reshape(int[] points, int[] moves) {
        for (int i=0; i < DIM_COUNT; i++) {
            reshape(i, (points != null ? points[i] : ALL_POINTS), moves[i]);
        }
    }

    void reshape(int dimension, int align, int move) {
        int[] pos = positions[dimension];
        if (align == ALL_POINTS) { // move everything
            for (int j=0; j < pos.length; j++) {
                if (pos[j] != UNKNOWN)
                    pos[j] += move;
            }
        }
        else if (align != NO_POINT) { // move only the desired point
            assert align == LEADING || align == TRAILING;
            if (pos[align] != UNKNOWN) {
                pos[align] += move;
                if (pos[LEADING] != UNKNOWN && pos[TRAILING] != UNKNOWN) {
                    if (pos[LEADING] > pos[TRAILING]) { // don't allow negative size
                        pos[align] = pos[align^1];
                    }
                    pos[CENTER] = (pos[LEADING] + pos[TRAILING]) / 2;
                }
                if (dimension == VERTICAL && move != 0) {
                    pos[BASELINE] = UNKNOWN; // undefined after resizing
                }
            }
        }
    }

    /**
     * Grows to bounds of given region.
     */
    void expand(LayoutRegion reg) {
        for (int i=0; i < DIM_COUNT; i++) {
            expand(reg, i);
        }
    }

    void expand(LayoutRegion reg, int dimension) {
        int[] pos = positions[dimension];
        int[] exPos = reg.positions[dimension];
        if (exPos[LEADING] != UNKNOWN
            && (pos[LEADING] == UNKNOWN || exPos[LEADING] < pos[LEADING]))
        {
            pos[LEADING] = exPos[LEADING];
        }
        if (exPos[TRAILING] != UNKNOWN
            && (pos[TRAILING] == UNKNOWN || exPos[TRAILING] > pos[TRAILING]))
        {
            pos[TRAILING] = exPos[TRAILING];
        }
        if (pos[LEADING] != UNKNOWN && pos[TRAILING] != UNKNOWN) {
            pos[CENTER] = (pos[LEADING] + pos[TRAILING]) / 2;
        }
    }

    /**
     * @param sp1 base LayoutSpace
     * @param sp2 compared LayoutSpace
     * @param points array of alignment constants defining what points should
     *        be compared in each dimension (can be null if it does not matter)
     * @param diffs output array with the position difference for each dimension
     */
/*    static void positionDiff(LayoutSpace sp1, LayoutSpace sp2,
                             int[] points, int[] diffs)
    {
        for (int i=0; i < DIM_COUNT; i++) {
            int[] pos1 = sp1.positions[i];
            int[] pos2 = sp2.positions[i];
            int align = points != null ? points[i] : LEADING;
            if (align != NO_POINT) {
                if (align == ALL_POINTS) {
                    align = LEADING;
                }
                diffs[i] = pos1[align] != UNKNOWN && pos2[align] != UNKNOWN ?
                           pos2[align] - pos1[align] : UNKNOWN;
            }
        }
    } */

    static boolean isValidCoordinate(int pos) {
        return pos > Short.MIN_VALUE && pos < Short.MAX_VALUE;
    }

    /**
     * @param r1 base LayoutRegion
     * @param r2 compared LayoutRegion
     * @param dimension HORIZONTAL or VERTICAL (dimension index)
     * @param align1 alignment constant of a point in base LayoutRegion
     * @param align2 alignment constant of a point in compared LayoutRegion
     * @return distance sp2 - sp1 in given dimension between given points
     */
    static int distance(LayoutRegion r1, LayoutRegion r2,
                        int dimension,
                        int align1, int align2)
    {
        int pos1 = r1.positions[dimension][align1];
        int pos2 = r2.positions[dimension][align2];
        return pos1 != UNKNOWN && pos2 != UNKNOWN ? pos2 - pos1 : UNKNOWN;
    }

    /**
     * Goes through all points of given two regions in given dimension and finds
     * the smallest distance (abs) between the regions. Positive value is
     * returned if r2 has higher position.
     * @return the smallest distance between the corresponding points of given
     *         regions in given dimension
     */
    static int minDistance(LayoutRegion r1, LayoutRegion r2, int dimension) {
        int[] pos1 = r1.positions[dimension];
        int[] pos2 = r2.positions[dimension];
        int min = UNKNOWN;
        int sign = 1;
        for (int i=0; i < pos1.length; i++) {
            if (pos1[i] != UNKNOWN && pos2[i] != UNKNOWN) {
                int dst = pos2[i] - pos1[i];
                int s;
                if (dst < 0) {
                    dst = -dst;
                    s = -1;
                }
                else s = 1;
                if (min == UNKNOWN || dst < min) {
                    min = dst;
                    sign = s;
                }
            }
        }
        return min * sign;
    }

    /**
     * Computes distance between two regions supposing they do not overlap.
     * The distance is between LEADING point of one region and TRAILING of the
     * other (or vice versa, depending on their relative position). Positive
     * value is returned if r2 has higher position.
     * @return the distance between two regions if they don't overlap, or 0 if
     *         they overlap
     */
    static int nonOverlapDistance(LayoutRegion r1, LayoutRegion r2, int dimension) {
        int[] pos1 = r1.positions[dimension];
        int[] pos2 = r2.positions[dimension];
        int dst = pos2[LEADING] - pos1[TRAILING];
        if (dst >= 0) {
            return dst;
        }
        dst = pos2[TRAILING] - pos1[LEADING];
        if (dst <= 0) {
            return dst;
        }
        return 0;
    }

    /**
     * Checks whether a point of 'contained' region (described by 'alignment')
     * is inside the 'container' region's area in given 'dimension'.
     * @return whether a point of a region is inside the other region
     */
    static boolean pointInside(LayoutRegion contained, int alignment, LayoutRegion container, int dimension) {
        int[] pos = container.positions[dimension];
        int point = contained.positions[dimension][alignment];
        assert point != UNKNOWN && pos[LEADING] != UNKNOWN && pos[TRAILING] != UNKNOWN;
        if (alignment == LEADING) {
            return point >= pos[LEADING] && point < pos[TRAILING];
        }
//        if (alignment == TRAILING) {
            return point > pos[LEADING] && point <= pos[TRAILING];
//        }
    }

    /**
     * @return whether the given regions overlap in given dimension
     */
    static boolean overlap(LayoutRegion r1, LayoutRegion r2, int dimension,
                           int margin)
    {
        int[] pos1 = r1.positions[dimension];
        int[] pos2 = r2.positions[dimension];
        assert pos1[LEADING] != UNKNOWN && pos1[TRAILING] != UNKNOWN
               && pos2[LEADING] != UNKNOWN && pos2[TRAILING] != UNKNOWN;
        return pos1[TRAILING] + margin > pos2[LEADING]
               && pos1[LEADING] - margin < pos2[TRAILING];
    }

    /**
     * @return whether given regions occupy the same space
     */
    static boolean sameSpace(LayoutRegion r1, LayoutRegion r2) {
        return sameSpace(r1, r2, HORIZONTAL) && sameSpace(r1, r2, VERTICAL);
    }

    /**
     * @return whether given regions occupy the same space in given dimension
     */
    static boolean sameSpace(LayoutRegion r1, LayoutRegion r2, int dimension) {
        return r1.positions[dimension][LEADING] == r2.positions[dimension][LEADING]
            && r1.positions[dimension][TRAILING] == r2.positions[dimension][TRAILING];
    }
}
