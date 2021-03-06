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

public class ALT_Bug203480Test extends LayoutTestCase {

    public ALT_Bug203480Test(String name) {
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
     * Resize jPanel1 vertically by its top edge to make it a bit smaller,
     * snapped at the longest default distance (separate).
     * This is exactly the size where the resizing gap in the panel's vertical
     * layout has zero size (should be set to default, not tried to be removed).
     */
    public void doChanges0() {
        ld.externalSizeChangeHappened();
// > UPDATE CURRENT STATE
        compBounds.put("Form", new Rectangle(0, 0, 597, 536));
        contInterior.put("Form", new Rectangle(0, 0, 597, 536));
        compBounds.put("jScrollPane2", new Rectangle(12, 13, 118, 510));
        baselinePosition.put("jScrollPane2-118-510", new Integer(0));
        compBounds.put("jScrollPane1", new Rectangle(142, 13, 443, 309));
        baselinePosition.put("jScrollPane1-443-309", new Integer(0));
        compBounds.put("jPanel1", new Rectangle(142, 329, 443, 194));
        baselinePosition.put("jPanel1-443-194", new Integer(0));
        contInterior.put("jPanel1", new Rectangle(142, 329, 443, 194));
        compBounds.put("jButton1", new Rectangle(154, 449, 79, 25));
        baselinePosition.put("jButton1-79-25", new Integer(17));
        compBounds.put("jButton2", new Rectangle(154, 476, 79, 25));
        baselinePosition.put("jButton2-79-25", new Integer(17));
        compBounds.put("jTextField2", new Rectangle(240, 477, 122, 22));
        baselinePosition.put("jTextField2-122-22", new Integer(16));
        compBounds.put("jTextField1", new Rectangle(240, 450, 122, 22));
        baselinePosition.put("jTextField1-122-22", new Integer(16));
        compBounds.put("jCheckBox4", new Rectangle(370, 449, 93, 25));
        baselinePosition.put("jCheckBox4-93-25", new Integer(17));
        compBounds.put("jCheckBox3", new Rectangle(370, 476, 93, 25));
        baselinePosition.put("jCheckBox3-93-25", new Integer(17));
        compBounds.put("jRadioButton3", new Rectangle(142, 401, 107, 25));
        baselinePosition.put("jRadioButton3-107-25", new Integer(17));
        compBounds.put("jSlider2", new Rectangle(267, 403, 200, 23));
        baselinePosition.put("jSlider2-200-23", new Integer(0));
        compBounds.put("jRadioButton2", new Rectangle(142, 365, 107, 25));
        baselinePosition.put("jRadioButton2-107-25", new Integer(17));
        compBounds.put("jSlider1", new Rectangle(267, 365, 200, 23));
        baselinePosition.put("jSlider1-200-23", new Integer(0));
        compBounds.put("jRadioButton1", new Rectangle(142, 329, 107, 25));
        baselinePosition.put("jRadioButton1-107-25", new Integer(17));
        compBounds.put("jCheckBox1", new Rectangle(253, 329, 93, 25));
        baselinePosition.put("jCheckBox1-93-25", new Integer(17));
        compBounds.put("jCheckBox2", new Rectangle(350, 329, 93, 25));
        baselinePosition.put("jCheckBox2-93-25", new Integer(17));
        compMinSize.put("jPanel1", new Dimension(329, 186));
        compBounds.put("jPanel1", new Rectangle(142, 329, 443, 194));
        compPrefSize.put("jPanel1", new Dimension(329, 194));
        compPrefSize.put("jTextField2", new Dimension(69, 22));
        prefPaddingInParent.put("jPanel1-jCheckBox4-0-1", new Integer(8)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("jPanel1-jCheckBox3-0-1", new Integer(8)); // parentId-compId-dimension-compAlignment
        hasExplicitPrefSize.put("jPanel1", new Boolean(false));
        prefPadding.put("jRadioButton1-jRadioButton2-1-0-0", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jRadioButton2-1-0-1", new Integer(5)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jRadioButton2-1-0-2", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jRadioButton2-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jSlider1-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jSlider1-1-0-1", new Integer(9)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jSlider1-1-0-2", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jSlider1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jRadioButton2-1-0-0", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jRadioButton2-1-0-1", new Integer(5)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jRadioButton2-1-0-2", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jRadioButton2-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jSlider1-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jSlider1-1-0-1", new Integer(9)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jSlider1-1-0-2", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jSlider1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jRadioButton2-1-0-0", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jRadioButton2-1-0-1", new Integer(5)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jRadioButton2-1-0-2", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jRadioButton2-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jSlider1-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jSlider1-1-0-1", new Integer(9)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jSlider1-1-0-2", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jSlider1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton2-jRadioButton3-1-0-0", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton2-jRadioButton3-1-0-1", new Integer(5)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton2-jRadioButton3-1-0-2", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton2-jRadioButton3-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        hasExplicitPrefSize.put("jPanel1", new Boolean(false));
        compMinSize.put("Form", new Dimension(483, 536));
        compBounds.put("Form", new Rectangle(0, 0, 597, 536));
        compPrefSize.put("jScrollPane1", new Dimension(443, 24));
        compPrefSize.put("jPanel1", new Dimension(329, 194));
        prefPadding.put("jScrollPane1-jPanel1-1-0-0", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jScrollPane1-jPanel1-1-0-1", new Integer(13)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jScrollPane1-jPanel1-1-0-2", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jScrollPane1-jPanel1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        compPrefSize.put("jScrollPane2", new Dimension(79, 322));
        ld.updateCurrentState();
// < UPDATE CURRENT STATE
// > START RESIZING
        baselinePosition.put("jPanel1-443-194", new Integer(0));
        compMinSize.put("jPanel1", new Dimension(329, 186));
        prefPadding.put("jRadioButton1-jRadioButton2-1-0-0", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jRadioButton2-1-0-1", new Integer(5)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jRadioButton2-1-0-2", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jRadioButton2-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jSlider1-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jSlider1-1-0-1", new Integer(9)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jSlider1-1-0-2", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jSlider1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jRadioButton2-1-0-0", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jRadioButton2-1-0-1", new Integer(5)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jRadioButton2-1-0-2", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jRadioButton2-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jSlider1-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jSlider1-1-0-1", new Integer(9)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jSlider1-1-0-2", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jSlider1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jRadioButton2-1-0-0", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jRadioButton2-1-0-1", new Integer(5)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jRadioButton2-1-0-2", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jRadioButton2-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jSlider1-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jSlider1-1-0-1", new Integer(9)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jSlider1-1-0-2", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jSlider1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        {
            String[] compIds = new String[]{
                "jPanel1"
            };
            Rectangle[] bounds = new Rectangle[]{
                new Rectangle(142, 329, 443, 194)
            };
            Point hotspot = new Point(364, 330);
            int[] resizeEdges = new int[]{
                -1,
                0
            };
            boolean inLayout = true;
            ld.startResizing(compIds, bounds, hotspot, resizeEdges, inLayout);
        }
// < START RESIZING
        prefPadding.put("jScrollPane1-jPanel1-1-0-0", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jScrollPane1-jPanel1-1-0-1", new Integer(13)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jScrollPane1-jPanel1-1-0-2", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jScrollPane1-jPanel1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
// > MOVE
        {
            Point p = new Point(363, 340);
            String containerId = "Form";
            boolean autoPositioning = true;
            boolean lockDimension = false;
            Rectangle[] bounds = new Rectangle[]{
                new Rectangle(142, 340, 443, 183)
            };
            ld.move(p, containerId, autoPositioning, lockDimension, bounds);
        }
// < MOVE
        prefPadding.put("jScrollPane1-jPanel1-1-0-0", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jScrollPane1-jPanel1-1-0-1", new Integer(13)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jScrollPane1-jPanel1-1-0-2", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jScrollPane1-jPanel1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
// > MOVE
        {
            Point p = new Point(363, 341);
            String containerId = "Form";
            boolean autoPositioning = true;
            boolean lockDimension = false;
            Rectangle[] bounds = new Rectangle[]{
                new Rectangle(142, 340, 443, 183)
            };
            ld.move(p, containerId, autoPositioning, lockDimension, bounds);
        }
// < MOVE
// > END MOVING
        compPrefSize.put("jScrollPane1", new Dimension(443, 24));
        compPrefSize.put("jScrollPane2", new Dimension(79, 322));
        contInterior.put("jPanel1", new Rectangle(-32626, -32428, 443, 186));
        compBounds.put("jButton1", new Rectangle(-32614, -32316, 79, 25));
        baselinePosition.put("jButton1-79-25", new Integer(17));
        compBounds.put("jButton2", new Rectangle(-32614, -32289, 79, 25));
        baselinePosition.put("jButton2-79-25", new Integer(17));
        compBounds.put("jTextField2", new Rectangle(-32528, -32288, 122, 22));
        baselinePosition.put("jTextField2-122-22", new Integer(16));
        compBounds.put("jTextField1", new Rectangle(-32528, -32315, 122, 22));
        baselinePosition.put("jTextField1-122-22", new Integer(16));
        compBounds.put("jCheckBox4", new Rectangle(-32398, -32316, 93, 25));
        baselinePosition.put("jCheckBox4-93-25", new Integer(17));
        compBounds.put("jCheckBox3", new Rectangle(-32398, -32289, 93, 25));
        baselinePosition.put("jCheckBox3-93-25", new Integer(17));
        compBounds.put("jRadioButton3", new Rectangle(-32626, -32364, 107, 25));
        baselinePosition.put("jRadioButton3-107-25", new Integer(17));
        compBounds.put("jSlider2", new Rectangle(-32501, -32362, 200, 23));
        baselinePosition.put("jSlider2-200-23", new Integer(0));
        compBounds.put("jRadioButton2", new Rectangle(-32626, -32400, 107, 25));
        baselinePosition.put("jRadioButton2-107-25", new Integer(17));
        compBounds.put("jSlider1", new Rectangle(-32501, -32400, 200, 23));
        baselinePosition.put("jSlider1-200-23", new Integer(0));
        compBounds.put("jRadioButton1", new Rectangle(-32626, -32428, 107, 25));
        baselinePosition.put("jRadioButton1-107-25", new Integer(17));
        compBounds.put("jCheckBox1", new Rectangle(-32515, -32428, 93, 25));
        baselinePosition.put("jCheckBox1-93-25", new Integer(17));
        compBounds.put("jCheckBox2", new Rectangle(-32418, -32428, 93, 25));
        baselinePosition.put("jCheckBox2-93-25", new Integer(17));
        ld.endMoving(true);
// < END MOVING
        ld.externalSizeChangeHappened();
// > UPDATE CURRENT STATE
        compBounds.put("Form", new Rectangle(0, 0, 597, 536));
        contInterior.put("Form", new Rectangle(0, 0, 597, 536));
        compBounds.put("jScrollPane2", new Rectangle(12, 13, 118, 510));
        baselinePosition.put("jScrollPane2-118-510", new Integer(0));
        compBounds.put("jScrollPane1", new Rectangle(142, 13, 443, 309));
        baselinePosition.put("jScrollPane1-443-309", new Integer(0));
        compBounds.put("jPanel1", new Rectangle(142, 340, 443, 183));
        baselinePosition.put("jPanel1-443-183", new Integer(0));
        contInterior.put("jPanel1", new Rectangle(142, 340, 443, 186));
        compBounds.put("jButton1", new Rectangle(154, 452, 79, 25));
        baselinePosition.put("jButton1-79-25", new Integer(17));
        compBounds.put("jButton2", new Rectangle(154, 479, 79, 25));
        baselinePosition.put("jButton2-79-25", new Integer(17));
        compBounds.put("jTextField2", new Rectangle(240, 480, 122, 22));
        baselinePosition.put("jTextField2-122-22", new Integer(16));
        compBounds.put("jTextField1", new Rectangle(240, 453, 122, 22));
        baselinePosition.put("jTextField1-122-22", new Integer(16));
        compBounds.put("jCheckBox4", new Rectangle(370, 452, 93, 25));
        baselinePosition.put("jCheckBox4-93-25", new Integer(17));
        compBounds.put("jCheckBox3", new Rectangle(370, 479, 93, 25));
        baselinePosition.put("jCheckBox3-93-25", new Integer(17));
        compBounds.put("jRadioButton3", new Rectangle(142, 404, 107, 25));
        baselinePosition.put("jRadioButton3-107-25", new Integer(17));
        compBounds.put("jSlider2", new Rectangle(267, 406, 200, 23));
        baselinePosition.put("jSlider2-200-23", new Integer(0));
        compBounds.put("jRadioButton2", new Rectangle(142, 368, 107, 25));
        baselinePosition.put("jRadioButton2-107-25", new Integer(17));
        compBounds.put("jSlider1", new Rectangle(267, 368, 200, 23));
        baselinePosition.put("jSlider1-200-23", new Integer(0));
        compBounds.put("jRadioButton1", new Rectangle(142, 340, 107, 25));
        baselinePosition.put("jRadioButton1-107-25", new Integer(17));
        compBounds.put("jCheckBox1", new Rectangle(253, 340, 93, 25));
        baselinePosition.put("jCheckBox1-93-25", new Integer(17));
        compBounds.put("jCheckBox2", new Rectangle(350, 340, 93, 25));
        baselinePosition.put("jCheckBox2-93-25", new Integer(17));
        compMinSize.put("jPanel1", new Dimension(329, 186));
        compBounds.put("jPanel1", new Rectangle(142, 340, 443, 183));
        compPrefSize.put("jPanel1", new Dimension(329, 186));
        compPrefSize.put("jTextField2", new Dimension(69, 22));
        prefPaddingInParent.put("jPanel1-jCheckBox4-0-1", new Integer(8)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("jPanel1-jCheckBox3-0-1", new Integer(8)); // parentId-compId-dimension-compAlignment
        hasExplicitPrefSize.put("jPanel1", new Boolean(false));
        prefPadding.put("jRadioButton1-jRadioButton2-1-0-0", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jRadioButton2-1-0-1", new Integer(5)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jRadioButton2-1-0-2", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jRadioButton2-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jSlider1-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jSlider1-1-0-1", new Integer(9)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jSlider1-1-0-2", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jSlider1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jRadioButton2-1-0-0", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jRadioButton2-1-0-1", new Integer(5)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jRadioButton2-1-0-2", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jRadioButton2-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jSlider1-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jSlider1-1-0-1", new Integer(9)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jSlider1-1-0-2", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jSlider1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jRadioButton2-1-0-0", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jRadioButton2-1-0-1", new Integer(5)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jRadioButton2-1-0-2", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jRadioButton2-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jSlider1-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jSlider1-1-0-1", new Integer(9)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jSlider1-1-0-2", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jSlider1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton2-jRadioButton3-1-0-0", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton2-jRadioButton3-1-0-1", new Integer(5)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton2-jRadioButton3-1-0-2", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton2-jRadioButton3-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        hasExplicitPrefSize.put("jPanel1", new Boolean(false));
        compMinSize.put("Form", new Dimension(483, 536));
        compBounds.put("Form", new Rectangle(0, 0, 597, 536));
        compPrefSize.put("jScrollPane1", new Dimension(443, 24));
        compPrefSize.put("jPanel1", new Dimension(329, 186));
        compPrefSize.put("jPanel1", new Dimension(329, 186));
        compPrefSize.put("jScrollPane2", new Dimension(79, 322));
        compBounds.put("Form", new Rectangle(0, 0, 597, 536));
        contInterior.put("Form", new Rectangle(0, 0, 597, 536));
        compBounds.put("jScrollPane2", new Rectangle(12, 13, 118, 510));
        baselinePosition.put("jScrollPane2-118-510", new Integer(0));
        compBounds.put("jScrollPane1", new Rectangle(142, 13, 443, 309));
        baselinePosition.put("jScrollPane1-443-309", new Integer(0));
        compBounds.put("jPanel1", new Rectangle(142, 340, 443, 183));
        baselinePosition.put("jPanel1-443-183", new Integer(0));
        contInterior.put("jPanel1", new Rectangle(142, 340, 443, 186));
        compBounds.put("jButton1", new Rectangle(154, 452, 79, 25));
        baselinePosition.put("jButton1-79-25", new Integer(17));
        compBounds.put("jButton2", new Rectangle(154, 479, 79, 25));
        baselinePosition.put("jButton2-79-25", new Integer(17));
        compBounds.put("jTextField2", new Rectangle(240, 480, 122, 22));
        baselinePosition.put("jTextField2-122-22", new Integer(16));
        compBounds.put("jTextField1", new Rectangle(240, 453, 122, 22));
        baselinePosition.put("jTextField1-122-22", new Integer(16));
        compBounds.put("jCheckBox4", new Rectangle(370, 452, 93, 25));
        baselinePosition.put("jCheckBox4-93-25", new Integer(17));
        compBounds.put("jCheckBox3", new Rectangle(370, 479, 93, 25));
        baselinePosition.put("jCheckBox3-93-25", new Integer(17));
        compBounds.put("jRadioButton3", new Rectangle(142, 404, 107, 25));
        baselinePosition.put("jRadioButton3-107-25", new Integer(17));
        compBounds.put("jSlider2", new Rectangle(267, 406, 200, 23));
        baselinePosition.put("jSlider2-200-23", new Integer(0));
        compBounds.put("jRadioButton2", new Rectangle(142, 368, 107, 25));
        baselinePosition.put("jRadioButton2-107-25", new Integer(17));
        compBounds.put("jSlider1", new Rectangle(267, 368, 200, 23));
        baselinePosition.put("jSlider1-200-23", new Integer(0));
        compBounds.put("jRadioButton1", new Rectangle(142, 340, 107, 25));
        baselinePosition.put("jRadioButton1-107-25", new Integer(17));
        compBounds.put("jCheckBox1", new Rectangle(253, 340, 93, 25));
        baselinePosition.put("jCheckBox1-93-25", new Integer(17));
        compBounds.put("jCheckBox2", new Rectangle(350, 340, 93, 25));
        baselinePosition.put("jCheckBox2-93-25", new Integer(17));
        compMinSize.put("jPanel1", new Dimension(329, 186));
        compBounds.put("jPanel1", new Rectangle(142, 340, 443, 183));
        compPrefSize.put("jTextField2", new Dimension(69, 22));
        prefPaddingInParent.put("jPanel1-jCheckBox4-0-1", new Integer(8)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("jPanel1-jCheckBox3-0-1", new Integer(8)); // parentId-compId-dimension-compAlignment
        prefPadding.put("jRadioButton1-jRadioButton2-1-0-0", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jRadioButton2-1-0-1", new Integer(5)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jRadioButton2-1-0-2", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jRadioButton2-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jSlider1-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jSlider1-1-0-1", new Integer(9)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jSlider1-1-0-2", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jSlider1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jRadioButton2-1-0-0", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jRadioButton2-1-0-1", new Integer(5)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jRadioButton2-1-0-2", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jRadioButton2-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jSlider1-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jSlider1-1-0-1", new Integer(9)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jSlider1-1-0-2", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jSlider1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jRadioButton2-1-0-0", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jRadioButton2-1-0-1", new Integer(5)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jRadioButton2-1-0-2", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jRadioButton2-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jSlider1-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jSlider1-1-0-1", new Integer(9)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jSlider1-1-0-2", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jSlider1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton2-jRadioButton3-1-0-0", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton2-jRadioButton3-1-0-1", new Integer(5)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton2-jRadioButton3-1-0-2", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton2-jRadioButton3-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        compMinSize.put("Form", new Dimension(483, 353));
        compBounds.put("Form", new Rectangle(0, 0, 597, 536));
        compPrefSize.put("jScrollPane1", new Dimension(443, 24));
        compPrefSize.put("jPanel1", new Dimension(329, 186));
        compPrefSize.put("jPanel1", new Dimension(329, 186));
        compPrefSize.put("jScrollPane2", new Dimension(79, 322));
        ld.updateCurrentState();
// < UPDATE CURRENT STATE
    }
}
