/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU General
 * Public License Version 2 only ("GPL") or the Common Development and
 * Distribution License("CDDL") (collectively, the "License"). You may not use
 * this file except in compliance with the License. You can obtain a copy of the
 * License at http://www.netbeans.org/cddl-gplv2.html or
 * nbbuild/licenses/CDDL-GPL-2-CP. See the License for the specific language
 * governing permissions and limitations under the License. When distributing
 * the software, include this License Header Notice in each file and include the
 * License file at nbbuild/licenses/CDDL-GPL-2-CP. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided by Oracle
 * in the GPL Version 2 section of the License file that accompanied this code.
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL or only
 * the GPL Version 2, indicate your decision by adding "[Contributor] elects to
 * include this software in this distribution under the [CDDL or GPL Version 2]
 * license." If you do not indicate a single choice of license, a recipient has
 * the option to distribute your version of this file under either the CDDL, the
 * GPL Version 2 or to extend the choice of license to its licensees as provided
 * above. However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is made
 * subject to such option by the copyright holder.
 */
package org.netbeans.modules.form.layoutdesign;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.*;
import org.openide.filesystems.FileUtil;

public class ALT_Baseline04Test extends LayoutTestCase {

    public ALT_Baseline04Test(String name) {
        super(name);
        try {
            className = this.getClass().getName();
            className = className.substring(className.lastIndexOf('.') + 1, className.length());
            startingFormFile = FileUtil.toFileObject(new File(url.getFile() + goldenFilesPath + className + "-StartingForm.form").getCanonicalFile());
        } catch (IOException ioe) {
            fail(ioe.toString());
        }
    }

    /**
     * Resize first button to snap to container bottom (without gap).
     */
    public void doChanges0() {
        ld.externalSizeChangeHappened();
// > UPDATE CURRENT STATE
        compBounds.put("Form", new Rectangle(0, 0, 400, 300));
        contInterior.put("Form", new Rectangle(0, 0, 400, 300));
        compBounds.put("jButton1", new Rectangle(10, 11, 73, 23));
        baselinePosition.put("jButton1-73-23", new Integer(15));
        compBounds.put("jButton2", new Rectangle(89, 11, 73, 23));
        baselinePosition.put("jButton2-73-23", new Integer(15));
        compBounds.put("jButton3", new Rectangle(168, 11, 73, 23));
        baselinePosition.put("jButton3-73-23", new Integer(15));
        compBounds.put("jButton4", new Rectangle(247, 11, 73, 23));
        baselinePosition.put("jButton4-73-23", new Integer(15));
        compMinSize.put("Form", new Dimension(330, 45));
        compBounds.put("Form", new Rectangle(0, 0, 400, 300));
        prefPaddingInParent.put("Form-jButton4-0-1", new Integer(10)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jButton1-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jButton2-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jButton3-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jButton4-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        ld.updateCurrentState();
// < UPDATE CURRENT STATE
// > START RESIZING
        baselinePosition.put("jButton1-73-23", new Integer(15));
        compPrefSize.put("jButton1", new Dimension(73, 23));
        {
            String[] compIds = new String[]{
                "jButton1"
            };
            Rectangle[] bounds = new Rectangle[]{
                new Rectangle(10, 11, 73, 23)
            };
            Point hotspot = new Point(49, 33);
            int[] resizeEdges = new int[]{
                -1,
                1
            };
            boolean inLayout = true;
            ld.startResizing(compIds, bounds, hotspot, resizeEdges, inLayout);
        }
// < START RESIZING
        prefPaddingInParent.put("Form-jButton1-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
// > MOVE
        {
            Point p = new Point(33, 306);
            String containerId = "Form";
            boolean autoPositioning = true;
            boolean lockDimension = false;
            Rectangle[] bounds = new Rectangle[]{
                new Rectangle(10, 11, 73, 289)
            };
            ld.move(p, containerId, autoPositioning, lockDimension, bounds);
        }
// < MOVE
        prefPaddingInParent.put("Form-jButton1-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
// > MOVE
        {
            Point p = new Point(33, 305);
            String containerId = "Form";
            boolean autoPositioning = true;
            boolean lockDimension = false;
            Rectangle[] bounds = new Rectangle[]{
                new Rectangle(10, 11, 73, 289)
            };
            ld.move(p, containerId, autoPositioning, lockDimension, bounds);
        }
// < MOVE
// > END MOVING
        prefPaddingInParent.put("Form-jButton4-0-1", new Integer(10)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jButton2-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jButton3-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jButton4-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        ld.endMoving(true);
// < END MOVING
        ld.externalSizeChangeHappened();
// > UPDATE CURRENT STATE
        compBounds.put("Form", new Rectangle(0, 0, 400, 300));
        contInterior.put("Form", new Rectangle(0, 0, 400, 300));
        compBounds.put("jButton1", new Rectangle(10, 11, 73, 289));
        baselinePosition.put("jButton1-73-289", new Integer(148));
        compBounds.put("jButton2", new Rectangle(89, 11, 73, 23));
        baselinePosition.put("jButton2-73-23", new Integer(15));
        compBounds.put("jButton3", new Rectangle(168, 11, 73, 23));
        baselinePosition.put("jButton3-73-23", new Integer(15));
        compBounds.put("jButton4", new Rectangle(247, 11, 73, 23));
        baselinePosition.put("jButton4-73-23", new Integer(15));
        compMinSize.put("Form", new Dimension(330, 45));
        compBounds.put("Form", new Rectangle(0, 0, 400, 300));
        prefPaddingInParent.put("Form-jButton4-0-1", new Integer(10)); // parentId-compId-dimension-compAlignment
        compPrefSize.put("jButton1", new Dimension(73, 23));
        prefPaddingInParent.put("Form-jButton2-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jButton3-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jButton4-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        compBounds.put("Form", new Rectangle(0, 0, 400, 300));
        contInterior.put("Form", new Rectangle(0, 0, 400, 300));
        compBounds.put("jButton1", new Rectangle(10, 11, 73, 289));
        baselinePosition.put("jButton1-73-289", new Integer(148));
        compBounds.put("jButton2", new Rectangle(89, 11, 73, 23));
        baselinePosition.put("jButton2-73-23", new Integer(15));
        compBounds.put("jButton3", new Rectangle(168, 11, 73, 23));
        baselinePosition.put("jButton3-73-23", new Integer(15));
        compBounds.put("jButton4", new Rectangle(247, 11, 73, 23));
        baselinePosition.put("jButton4-73-23", new Integer(15));
        compMinSize.put("Form", new Dimension(330, 45));
        compBounds.put("Form", new Rectangle(0, 0, 400, 300));
        prefPaddingInParent.put("Form-jButton4-0-1", new Integer(10)); // parentId-compId-dimension-compAlignment
        compPrefSize.put("jButton1", new Dimension(73, 23));
        prefPaddingInParent.put("Form-jButton2-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jButton3-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jButton4-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        ld.updateCurrentState();
// < UPDATE CURRENT STATE
    }

    /**
     * Resize second button to snap to container bottom at preferred distance.
     */
    public void doChanges1() {
// > START RESIZING
        baselinePosition.put("jButton2-73-23", new Integer(15));
        compPrefSize.put("jButton2", new Dimension(73, 23));
        {
            String[] compIds = new String[]{
                "jButton2"
            };
            Rectangle[] bounds = new Rectangle[]{
                new Rectangle(89, 11, 73, 23)
            };
            Point hotspot = new Point(129, 33);
            int[] resizeEdges = new int[]{
                -1,
                1
            };
            boolean inLayout = true;
            ld.startResizing(compIds, bounds, hotspot, resizeEdges, inLayout);
        }
// < START RESIZING
        prefPaddingInParent.put("Form-jButton2-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
// > MOVE
        {
            Point p = new Point(132, 281);
            String containerId = "Form";
            boolean autoPositioning = true;
            boolean lockDimension = false;
            Rectangle[] bounds = new Rectangle[]{
                new Rectangle(89, 11, 73, 278)
            };
            ld.move(p, containerId, autoPositioning, lockDimension, bounds);
        }
// < MOVE
        prefPaddingInParent.put("Form-jButton2-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
// > MOVE
        {
            Point p = new Point(132, 282);
            String containerId = "Form";
            boolean autoPositioning = true;
            boolean lockDimension = false;
            Rectangle[] bounds = new Rectangle[]{
                new Rectangle(89, 11, 73, 278)
            };
            ld.move(p, containerId, autoPositioning, lockDimension, bounds);
        }
// < MOVE
// > END MOVING
        prefPaddingInParent.put("Form-jButton4-0-1", new Integer(10)); // parentId-compId-dimension-compAlignment
        compPrefSize.put("jButton1", new Dimension(73, 23));
        prefPaddingInParent.put("Form-jButton3-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jButton4-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        ld.endMoving(true);
// < END MOVING
        ld.externalSizeChangeHappened();
// > UPDATE CURRENT STATE
        compBounds.put("Form", new Rectangle(0, 0, 400, 300));
        contInterior.put("Form", new Rectangle(0, 0, 400, 300));
        compBounds.put("jButton1", new Rectangle(10, 11, 73, 289));
        baselinePosition.put("jButton1-73-289", new Integer(148));
        compBounds.put("jButton2", new Rectangle(89, 11, 73, 278));
        baselinePosition.put("jButton2-73-278", new Integer(143));
        compBounds.put("jButton3", new Rectangle(168, 11, 73, 23));
        baselinePosition.put("jButton3-73-23", new Integer(15));
        compBounds.put("jButton4", new Rectangle(247, 11, 73, 23));
        baselinePosition.put("jButton4-73-23", new Integer(15));
        compMinSize.put("Form", new Dimension(330, 45));
        compBounds.put("Form", new Rectangle(0, 0, 400, 300));
        prefPaddingInParent.put("Form-jButton4-0-1", new Integer(10)); // parentId-compId-dimension-compAlignment
        compPrefSize.put("jButton1", new Dimension(73, 23));
        compPrefSize.put("jButton2", new Dimension(73, 23));
        compBounds.put("Form", new Rectangle(0, 0, 400, 300));
        contInterior.put("Form", new Rectangle(0, 0, 400, 300));
        compBounds.put("jButton1", new Rectangle(10, 11, 73, 289));
        baselinePosition.put("jButton1-73-289", new Integer(148));
        compBounds.put("jButton2", new Rectangle(89, 11, 73, 278));
        baselinePosition.put("jButton2-73-278", new Integer(143));
        compBounds.put("jButton3", new Rectangle(168, 11, 73, 23));
        baselinePosition.put("jButton3-73-23", new Integer(15));
        compBounds.put("jButton4", new Rectangle(247, 11, 73, 23));
        baselinePosition.put("jButton4-73-23", new Integer(15));
        compMinSize.put("Form", new Dimension(330, 45));
        compBounds.put("Form", new Rectangle(0, 0, 400, 300));
        prefPaddingInParent.put("Form-jButton4-0-1", new Integer(10)); // parentId-compId-dimension-compAlignment
        compPrefSize.put("jButton1", new Dimension(73, 23));
        compPrefSize.put("jButton2", new Dimension(73, 23));
        ld.updateCurrentState();
// < UPDATE CURRENT STATE
    }

    /**
     * Resize third button vertically, e.g. to about half of the form height.
     */
    public void doChanges2() {
// > START RESIZING
        baselinePosition.put("jButton3-73-23", new Integer(15));
        compPrefSize.put("jButton3", new Dimension(73, 23));
        {
            String[] compIds = new String[]{
                "jButton3"
            };
            Rectangle[] bounds = new Rectangle[]{
                new Rectangle(168, 11, 73, 23)
            };
            Point hotspot = new Point(206, 32);
            int[] resizeEdges = new int[]{
                -1,
                1
            };
            boolean inLayout = true;
            ld.startResizing(compIds, bounds, hotspot, resizeEdges, inLayout);
        }
// < START RESIZING
        prefPaddingInParent.put("Form-jButton3-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
// > MOVE
        {
            Point p = new Point(209, 157);
            String containerId = "Form";
            boolean autoPositioning = true;
            boolean lockDimension = false;
            Rectangle[] bounds = new Rectangle[]{
                new Rectangle(168, 11, 73, 148)
            };
            ld.move(p, containerId, autoPositioning, lockDimension, bounds);
        }
// < MOVE
        prefPaddingInParent.put("Form-jButton3-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
// > MOVE
        {
            Point p = new Point(209, 158);
            String containerId = "Form";
            boolean autoPositioning = true;
            boolean lockDimension = false;
            Rectangle[] bounds = new Rectangle[]{
                new Rectangle(168, 11, 73, 149)
            };
            ld.move(p, containerId, autoPositioning, lockDimension, bounds);
        }
// < MOVE
// > END MOVING
        prefPaddingInParent.put("Form-jButton4-0-1", new Integer(10)); // parentId-compId-dimension-compAlignment
        compPrefSize.put("jButton1", new Dimension(73, 23));
        compPrefSize.put("jButton2", new Dimension(73, 23));
        compPrefSize.put("jButton3", new Dimension(73, 23));
        ld.endMoving(true);
// < END MOVING
        ld.externalSizeChangeHappened();
// > UPDATE CURRENT STATE
        compBounds.put("Form", new Rectangle(0, 0, 400, 300));
        contInterior.put("Form", new Rectangle(0, 0, 400, 300));
        compBounds.put("jButton1", new Rectangle(10, 11, 73, 289));
        baselinePosition.put("jButton1-73-289", new Integer(148));
        compBounds.put("jButton2", new Rectangle(89, 11, 73, 278));
        baselinePosition.put("jButton2-73-278", new Integer(143));
        compBounds.put("jButton3", new Rectangle(168, 11, 73, 149));
        baselinePosition.put("jButton3-73-149", new Integer(78));
        compBounds.put("jButton4", new Rectangle(247, 74, 73, 23));
        baselinePosition.put("jButton4-73-23", new Integer(15));
        compMinSize.put("Form", new Dimension(330, 171));
        compBounds.put("Form", new Rectangle(0, 0, 400, 300));
        prefPaddingInParent.put("Form-jButton4-0-1", new Integer(10)); // parentId-compId-dimension-compAlignment
        compPrefSize.put("jButton1", new Dimension(73, 23));
        compPrefSize.put("jButton2", new Dimension(73, 23));
        compBounds.put("Form", new Rectangle(0, 0, 400, 300));
        contInterior.put("Form", new Rectangle(0, 0, 400, 300));
        compBounds.put("jButton1", new Rectangle(10, 11, 73, 289));
        baselinePosition.put("jButton1-73-289", new Integer(148));
        compBounds.put("jButton2", new Rectangle(89, 11, 73, 278));
        baselinePosition.put("jButton2-73-278", new Integer(143));
        compBounds.put("jButton3", new Rectangle(168, 11, 73, 149));
        baselinePosition.put("jButton3-73-149", new Integer(78));
        compBounds.put("jButton4", new Rectangle(247, 74, 73, 23));
        baselinePosition.put("jButton4-73-23", new Integer(15));
        compMinSize.put("Form", new Dimension(330, 171));
        compBounds.put("Form", new Rectangle(0, 0, 400, 300));
        prefPaddingInParent.put("Form-jButton4-0-1", new Integer(10)); // parentId-compId-dimension-compAlignment
        compPrefSize.put("jButton1", new Dimension(73, 23));
        compPrefSize.put("jButton2", new Dimension(73, 23));
        ld.updateCurrentState();
// < UPDATE CURRENT STATE
    }

    /**
     * Resize last button downwards to snap to third's button. It should end up
     * with the same size.
     */
    public void doChanges3() {
// > START RESIZING
        baselinePosition.put("jButton4-73-23", new Integer(15));
        compPrefSize.put("jButton4", new Dimension(73, 23));
        {
            String[] compIds = new String[]{
                "jButton4"
            };
            Rectangle[] bounds = new Rectangle[]{
                new Rectangle(247, 74, 73, 23)
            };
            Point hotspot = new Point(282, 95);
            int[] resizeEdges = new int[]{
                -1,
                1
            };
            boolean inLayout = true;
            ld.startResizing(compIds, bounds, hotspot, resizeEdges, inLayout);
        }
// < START RESIZING
        prefPaddingInParent.put("Form-jButton4-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
// > MOVE
        {
            Point p = new Point(286, 152);
            String containerId = "Form";
            boolean autoPositioning = true;
            boolean lockDimension = false;
            Rectangle[] bounds = new Rectangle[]{
                new Rectangle(247, 74, 73, 86)
            };
            ld.move(p, containerId, autoPositioning, lockDimension, bounds);
        }
// < MOVE
        prefPaddingInParent.put("Form-jButton4-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
// > MOVE
        {
            Point p = new Point(286, 153);
            String containerId = "Form";
            boolean autoPositioning = true;
            boolean lockDimension = false;
            Rectangle[] bounds = new Rectangle[]{
                new Rectangle(247, 74, 73, 86)
            };
            ld.move(p, containerId, autoPositioning, lockDimension, bounds);
        }
// < MOVE
// > END MOVING
        prefPaddingInParent.put("Form-jButton3-0-1", new Integer(10)); // parentId-compId-dimension-compAlignment
        compPrefSize.put("jButton1", new Dimension(73, 23));
        compPrefSize.put("jButton2", new Dimension(73, 23));
        compPrefSize.put("jButton4", new Dimension(73, 23));
        ld.endMoving(true);
// < END MOVING
        ld.externalSizeChangeHappened();
// > UPDATE CURRENT STATE
        compBounds.put("Form", new Rectangle(0, 0, 400, 300));
        contInterior.put("Form", new Rectangle(0, 0, 400, 300));
        compBounds.put("jButton1", new Rectangle(10, 11, 73, 289));
        baselinePosition.put("jButton1-73-289", new Integer(148));
        compBounds.put("jButton2", new Rectangle(89, 11, 73, 278));
        baselinePosition.put("jButton2-73-278", new Integer(143));
        compBounds.put("jButton3", new Rectangle(168, 11, 73, 149));
        baselinePosition.put("jButton3-73-149", new Integer(78));
        compBounds.put("jButton4", new Rectangle(247, 11, 73, 149));
        baselinePosition.put("jButton4-73-149", new Integer(78));
        compMinSize.put("Form", new Dimension(330, 171));
        compBounds.put("Form", new Rectangle(0, 0, 400, 300));
        prefPaddingInParent.put("Form-jButton4-0-1", new Integer(10)); // parentId-compId-dimension-compAlignment
        compPrefSize.put("jButton1", new Dimension(73, 23));
        compPrefSize.put("jButton2", new Dimension(73, 23));
        ld.updateCurrentState();
// < UPDATE CURRENT STATE
    }
}
