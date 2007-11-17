/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
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

package org.netbeans.lib.editor.view;

import javax.swing.text.AbstractDocument;
import javax.swing.text.View;
import org.netbeans.editor.view.spi.FlyView;
import org.netbeans.editor.view.spi.LockView;
import org.netbeans.editor.view.spi.ViewLayoutState;
import org.netbeans.editor.view.spi.ViewUtilities;

/**
 * Implementation of {@link org.netbeans.editor.view.spi.ViewLayoutState}
 * for non-flyweight views that only caches preferred spans
 * along both axes.
 * <br>
 * Preferred span is returned for maximum and minimum spans
 * along both axes.
 *
 * @author Miloslav Metelka
 * @version 1.00
 */

public class SimpleViewLayoutState implements ViewLayoutState {
    
    /**
     * Bit value in <code>statusBits</code> determining
     * whether the x is the major axis of this layout state.
     */
    private static final int X_MAJOR_AXIS_BIT = 1;
    
    /**
     * Bit value in <code>statusBits</code> determining
     * that preference along the major axis of this layout state has changed.
     */
    private static final int MAJOR_AXIS_PREFERENCE_CHANGED_BIT = 2;

    /**
     * Bit value in <code>statusBits</code> determining
     * that preference along the minor axis of this layout state has changed.
     */
    private static final int MINOR_AXIS_PREFERENCE_CHANGED_BIT = 4;
    
    /**
     * Bit value in <code>statusBits</code> determining
     * that size of the wrapped view needs to be set again
     * by <code>View.setSize()</code>
     */
    private static final int VIEW_SIZE_INVALID_BIT = 8;
    
    protected static final int LAST_USED_BIT = VIEW_SIZE_INVALID_BIT;

    /**
     * Bit composition being used to test whether 
     * the layout is up-to-date or not.
     */
    private static final int ANY_INVALID
        = MAJOR_AXIS_PREFERENCE_CHANGED_BIT
        | MINOR_AXIS_PREFERENCE_CHANGED_BIT
        | VIEW_SIZE_INVALID_BIT;

    
    private int statusBits; // 4

    private View view; // 8
    
    private double layoutMajorAxisRawOffset; // double => 16

    private int viewRawIndex; // 20

    // major axis
    private float layoutMajorAxisPreferredSpan; // 24
    
    // minor axis
    private float layoutMinorAxisPreferredSpan; // 28
    
    private float layoutMinorAxisAlignment; // 32

    
    public SimpleViewLayoutState(View v) {
//        assert !(v instanceof FlyView); // ensure not flyweight view

        view = v;
    }

    public final View getView() {
        return view;
    }
    
    public boolean isFlyweight() {
        return false;
    }
    
    public ViewLayoutState selectLayoutMajorAxis(int axis) {
//        assert ViewUtilities.isAxisValid(axis);

        if (axis == View.X_AXIS) {
            setStatusBits(X_MAJOR_AXIS_BIT);
        } else { // y axis
            clearStatusBits(X_MAJOR_AXIS_BIT);
        }
        
        return this;
    }
    
    protected final boolean isXMajorAxis() {
        return isStatusBitsNonZero(X_MAJOR_AXIS_BIT);
    }
    
    protected final int getMajorAxis() {
        return isXMajorAxis() ? View.X_AXIS : View.Y_AXIS;
    }
    
    protected final int getMinorAxis() {
        return isXMajorAxis() ? View.Y_AXIS : View.X_AXIS;
    }
    
    protected final ViewLayoutState.Parent getLayoutStateParent() {
        View parent = getView().getParent();
        return (parent instanceof ViewLayoutState.Parent)
            ? ((ViewLayoutState.Parent)parent)
            : null;
    }

    public void updateLayout() {
        View parent = view.getParent();
        if (parent == null) { // disconnected from hierarchy => no layout update
            return;
        }

        while (!isLayoutValid()) { // Nothing to do
            doUpdateLayout(parent);
        }
    }
        
    protected void doUpdateLayout(View parent) {
        ViewLayoutState.Parent lsParent = (parent instanceof ViewLayoutState.Parent)
            ? (ViewLayoutState.Parent)parent
            : null;

        // Check whether minor axis has changed
        if (isStatusBitsNonZero(MINOR_AXIS_PREFERENCE_CHANGED_BIT)) { // minor not valid
            clearStatusBits(MINOR_AXIS_PREFERENCE_CHANGED_BIT);

            int minorAxis = getMinorAxis();
            if (minorAxisUpdateLayout(minorAxis)) {
                if (lsParent != null) {
                    lsParent.minorAxisPreferenceChanged(this);
                }
            }
        }

        // Check whether major axis has changed
        if (isStatusBitsNonZero(MAJOR_AXIS_PREFERENCE_CHANGED_BIT)) { // major not valid
            clearStatusBits(MAJOR_AXIS_PREFERENCE_CHANGED_BIT);

            float oldSpan = getLayoutMajorAxisPreferredSpanFloat();
            float newSpan = view.getPreferredSpan(getMajorAxis());
            setLayoutMajorAxisPreferredSpan(newSpan);
            double majorAxisSpanDelta = newSpan - oldSpan;
            if (majorAxisSpanDelta != 0 && lsParent != null) {
                lsParent.majorAxisPreferenceChanged(this, majorAxisSpanDelta);
            }
        }

        // Check whether size must be set on the view
        if (isStatusBitsNonZero(VIEW_SIZE_INVALID_BIT)) {
            clearStatusBits(VIEW_SIZE_INVALID_BIT);

            if (lsParent != null) {
                float width;
                float height;
                float majorAxisSpan = (float)getLayoutMajorAxisPreferredSpan();
                float minorAxisSpan = lsParent.getMinorAxisSpan(this);
                if (isXMajorAxis()) { // x is major axis
                    width = majorAxisSpan;
                    height = minorAxisSpan;
                } else {
                    width = minorAxisSpan;
                    height = majorAxisSpan;
                }

                view.setSize(width, height);
            }
        }
        
        // Call recursively to make sure that there is no more work.
        updateLayout();
    }
    
    protected boolean minorAxisUpdateLayout(int minorAxis) {
        boolean minorAxisPreferenceChanged = false;
        float val;
        
        val = view.getPreferredSpan(minorAxis);
        if (val != getLayoutMinorAxisPreferredSpan()) {
            setLayoutMinorAxisPreferredSpan(val);
            minorAxisPreferenceChanged = true;
        }
        
        val = view.getAlignment(getMinorAxis());
        if (val != getLayoutMinorAxisAlignment()) {
            setLayoutMinorAxisAlignment(val);
            minorAxisPreferenceChanged = true;
        }

        return minorAxisPreferenceChanged;
    }

    public int getViewRawIndex() {
        return viewRawIndex;
    }
    
    public void setViewRawIndex(int viewRawIndex) {
        this.viewRawIndex = viewRawIndex;
    }

    public double getLayoutMajorAxisRawOffset() {
        return layoutMajorAxisRawOffset;
    }
    
    public void setLayoutMajorAxisRawOffset(double layoutMajorAxisRawOffset) {
        this.layoutMajorAxisRawOffset = layoutMajorAxisRawOffset;
    }
    
    public double getLayoutMajorAxisPreferredSpan() {
        return layoutMajorAxisPreferredSpan;
    }
    
    public float getLayoutMajorAxisPreferredSpanFloat() {
        return layoutMajorAxisPreferredSpan;
    }

    protected void setLayoutMajorAxisPreferredSpan(float layoutMajorAxisPreferredSpan) {
        this.layoutMajorAxisPreferredSpan = layoutMajorAxisPreferredSpan;
    }
    
    public float getLayoutMinorAxisPreferredSpan() {
        return layoutMinorAxisPreferredSpan;
    }
    
    protected void setLayoutMinorAxisPreferredSpan(float layoutMinorAxisPreferredSpan) {
        this.layoutMinorAxisPreferredSpan = layoutMinorAxisPreferredSpan;
    }

    public float getLayoutMinorAxisMinimumSpan() {
        return getLayoutMinorAxisPreferredSpan();
    }

    public float getLayoutMinorAxisMaximumSpan() {
        return getLayoutMinorAxisPreferredSpan();
    }
    
    public float getLayoutMinorAxisAlignment() {
        return layoutMinorAxisAlignment;
    }

    public void setLayoutMinorAxisAlignment(float layoutMinorAxisAlignment) {
        this.layoutMinorAxisAlignment = layoutMinorAxisAlignment;
    }
    
    public void viewPreferenceChanged(boolean width, boolean height) {
        if (isXMajorAxis()) { // x is major axis
            if (width) {
                setStatusBits(MAJOR_AXIS_PREFERENCE_CHANGED_BIT); // major no longer valid
            }
            if (height) {
                setStatusBits(MINOR_AXIS_PREFERENCE_CHANGED_BIT); // minor no longer valid
            }
        } else {
            if (width) {
                setStatusBits(MINOR_AXIS_PREFERENCE_CHANGED_BIT); // minor no longer valid
            }
            if (height) {
                setStatusBits(MAJOR_AXIS_PREFERENCE_CHANGED_BIT); // major no longer valid
            }
        }
        setStatusBits(VIEW_SIZE_INVALID_BIT); // child size no longer valid
    }
    
    public void markViewSizeInvalid() {
        setStatusBits(VIEW_SIZE_INVALID_BIT);
    }

    public boolean isLayoutValid() {
        return !isStatusBitsNonZero(ANY_INVALID);
    }

    protected final int getStatusBits(int bits) {
        return (statusBits & bits);
    }
    
    protected final boolean isStatusBitsNonZero(int bits) {
        return (getStatusBits(bits) != 0);
    }
    
    protected final void setStatusBits(int bits) {
        statusBits |= bits;
    }
    
    protected final void clearStatusBits(int bits) {
        statusBits &= ~bits;
    }

}
