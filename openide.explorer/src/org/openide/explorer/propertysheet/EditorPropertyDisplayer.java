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
/*
 * EditorPropertyDisplayer_java
 *
 * Created on 18 October 2003, 16:36
 */
package org.openide.explorer.propertysheet;

import org.openide.nodes.Node.Property;
import org.openide.util.ImageUtilities;
import org.openide.util.Utilities;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;

import java.beans.FeatureDescriptor;
import java.beans.PropertyEditor;

import java.lang.ref.WeakReference;

import javax.swing.*;


/** An implementation of PropertyDisplayer_Inline which replicates the inline
 * editor mode of PropertyPanel.  This class is a base class which is simply
 * responsible for finding and embedding the correct inline editor - it contains
 * no code for actually updating the value or anything such.  That code is in
 * EditablePropertyDisplayer, to improve readability and maintainability.
 *
 * @author  Tim Boudreau
 */
class EditorPropertyDisplayer extends JComponent implements PropertyDisplayer_Inline {
    private Property prop = null;
    private InplaceEditor inplace = null;
    private JComponent inner = null;
    private int radioButtonMax = 0;
    private boolean showCustomEditorButton = true;
    private boolean tableUI = false;
    private boolean useLabels = true;
    private PropertyEnv env = null;
    private boolean radioBoolean = false;
    protected WeakReference<PropertyModel> modelRef = null;
    protected boolean inReplaceInner = false;
    private InplaceEditorFactory factory1 = null;
    private InplaceEditorFactory factory2 = null;
    private ReusablePropertyEnv reusableEnv = new ReusablePropertyEnv(); //XXX supply from PP?

    /** Creates a new instance of EditorPropertyDisplayer */
    public EditorPropertyDisplayer(Property p) {
        this(p, null);
    }

    EditorPropertyDisplayer(Property p, PropertyModel mdl) {
        if (p == null) {
            throw new NullPointerException("Property may not be null"); //NOI18N
        }

        this.prop = p;

        if (mdl != null) {
            modelRef = new WeakReference<PropertyModel>(mdl);
        }
    }

    public void addNotify() {
        try {
            if (inner == null) {
                replaceInner();
            }
        } finally {
            super.addNotify();
        }
    }

    protected void processFocusEvent(FocusEvent fe) {
        super.processFocusEvent(fe);

        if ((fe.getID() == FocusEvent.FOCUS_GAINED) && (inner != null)) {
            inner.requestFocus();
        }
    }

    public void removeNotify() {
        super.removeNotify();
        setInplaceEditor(null);
        reusableEnv.clear();
    }

    public final Component getComponent() {
        return this;
    }

    public final Property getProperty() {
        return prop;
    }

    public final int getRadioButtonMax() {
        return radioButtonMax;
    }

    public final boolean isShowCustomEditorButton() {
        boolean result = showCustomEditorButton;

        if (getProperty() != null) {
            Boolean explicit = (Boolean) getProperty().getValue("suppressCustomEditor"); //NOI18N

            if (explicit != null) {
                result = !explicit.booleanValue();
            }
        }

        return result;
    }

    public final boolean isTableUI() {
        return tableUI;
    }

    public final void refresh() {
        if (isDisplayable()) {
            replaceInner();
        }
    }

    public final boolean isUseLabels() {
        return useLabels;
    }

    public final void setRadioButtonMax(int max) {
        if (max != radioButtonMax) {
            int old = radioButtonMax;
            boolean needChange = false;

            if (inplace != null) {
                InplaceEditor innermost = PropUtils.findInnermostInplaceEditor(inplace);

                if (innermost instanceof JComboBox || innermost instanceof RadioInplaceEditor) {
                    PropertyEditor ped = innermost.getPropertyEditor();
                    int tagCount = (ped.getTags() == null) ? (-1) : ped.getTags().length;
                    needChange = (old <= tagCount) != (max <= tagCount);
                }
            }

            radioButtonMax = max;

            if (needChange && (inner != null)) {
                replaceInner();
                firePropertyChange("preferredSize", null, null); //NOI18N
            }
        }
    }

    public final void setShowCustomEditorButton(boolean val) {
        //If the property descriptor explicitly says it does not
        //want a custom editor button, this overrides anything set on
        //the PropertyPanel.
        if (getProperty() != null) {
            Property p = getProperty();
            Boolean explicit = (Boolean) p.getValue("suppressCustomEditor"); //NOI18N

            if (explicit != null) {
                val = explicit.booleanValue();
                System.err.println("Found explicit value: " + val);
            }
        }

        if (showCustomEditorButton != val) {
            showCustomEditorButton = val;
            replaceInner();
        }
    }

    public final void setTableUI(boolean val) {
        if (val != tableUI) {
            tableUI = val;
            replaceInner();
        }
    }

    public final void setUseLabels(boolean useLabels) {
        if (useLabels != this.useLabels) {
            boolean needChange = false;

            if (isShowing()) {
                InplaceEditor innermost = PropUtils.findInnermostInplaceEditor(inplace);

                needChange = (innermost instanceof RadioInplaceEditor || innermost instanceof JCheckBox);
            }

            this.useLabels = useLabels;

            if (needChange && (inner != null)) {
                replaceInner();
            }
        }
    }

    public final void requestFocus() {
        if (inner != null) {
            inner.requestFocus();
        } else {
            super.requestFocus();
        }
    }

    public final Dimension getPreferredSize() {
        Dimension result;

        if (inner == null) {
            //Use the renderer infrastructure to do it if we're not initialized
            result = new RendererPropertyDisplayer(getProperty()).getRenderer(this).getPreferredSize();
        } else {
            result = inner.getPreferredSize();
        }

        return result;
    }

    public final boolean requestFocusInWindow() {
        boolean result;

        if (inner != null) {
            result = inner.requestFocusInWindow();
        } else {
            result = super.requestFocusInWindow();
        }

        return result;
    }

    private final void installInner(JComponent c) {
        synchronized (getTreeLock()) {
            if (inner != null) {
                remove(inner);
            }

            inner = c;

            if (inner != null) {
                c.setBounds(0, 0, getWidth(), getHeight());
                add(c);
            }
        }
    }

    protected final void replaceInner() {
        inReplaceInner = true;

        try {
            Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            boolean hadFocus = isEnabled() &&
                ((focusOwner == this) || isAncestorOf(focusOwner) ||
                ((getInplaceEditor() != null) && getInplaceEditor().isKnownComponent(focusOwner)));

            //Figure out if a combo popup was open, so we can re-open it.
            //If we're processing a mouse event, close it because that's
            //the normal behavior of a popup.  We want arrow keyboard events
            //to not trigger closing of the popup if they caused a change
            //to a value that is marked as invalid
            boolean wasComboPopup = hadFocus && focusOwner instanceof JComboBox &&
                ((JComboBox) focusOwner).isPopupVisible() &&
                (EventQueue.getCurrentEvent() instanceof KeyEvent &&
                ((((KeyEvent) EventQueue.getCurrentEvent()).getKeyCode() == KeyEvent.VK_UP) ||
                (((KeyEvent) EventQueue.getCurrentEvent()).getKeyCode() == KeyEvent.VK_DOWN)));

            //            System.err.println("REPLACE INNER - " + prop.getDisplayName() + " focus:" + hadFocus);
            if (hadFocus) {
                //We don't want focus to jump to another component and back
                KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
            }

            setInplaceEditor(createInplaceEditor());

            if (hadFocus) {
                requestFocus();

                //At this point the focus owner is still null, the event is queued - 
                if (wasComboPopup) {
                    //We have to let the request focus on the event queue get
                    //processed before this can be done, or the component will
                    //not yet be laid out and the popup will be 1 pixel wide
                    SwingUtilities.invokeLater(
                        new Runnable() {
                            public void run() {
                                InplaceEditor ied = getInplaceEditor();
                                ied = PropUtils.findInnermostInplaceEditor(ied);

                                JComponent c = ied.getComponent();

                                if (c instanceof JComboBox && c.isShowing()) {
                                    ((JComboBox) c).showPopup();
                                }
                            }
                        }
                    );
                }
            }

            revalidate();
            repaint();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            inReplaceInner = false;
        }
    }

    protected final JComponent getInner() {
        return inner;
    }

    protected void setInplaceEditor(InplaceEditor ed) {
        if (inplace != ed) {
            if (inplace != null) {
                inplace.clear();
            }

            inplace = ed;

            if (ed == null) {
                installInner(null);
            } else {
                JComponent comp = inplace.getComponent();
                prepareComponent(inplace);
                installInner(comp);
            }
        }
    }

    public void setEnabled(boolean b) {
        if (inner != null) {
            inner.setEnabled(b);
        }

        super.setEnabled(b);
    }

    public void setBackground(Color c) {
        super.setBackground(c);

        if (inner != null) {
            if ((inplace != null) && inplace.supportsTextEntry()) {
                inner.setBackground(PropUtils.getTextFieldBackground());
            } else {
                inner.setBackground(c);
            }
        }
    }

    public void setForeground(Color c) {
        super.setForeground(c);

        if (inner != null) {
            if ((inplace != null) && inplace.supportsTextEntry()) {
                inner.setForeground(PropUtils.getTextFieldForeground());
            } else {
                inner.setForeground(c);
            }
        }
    }

    protected void prepareComponent(InplaceEditor inplace) {
        InplaceEditor innermost = PropUtils.findInnermostInplaceEditor(inplace);
        JComponent comp = innermost.getComponent();

        if (!isTableUI() && inplace.supportsTextEntry()) {
            comp.setBackground(PropUtils.getTextFieldBackground());
            comp.setForeground(PropUtils.getTextFieldForeground());
        } else {
            comp.setBackground(getBackground());

            if (!isEnabled() || !prop.canWrite()) {
                comp.setForeground(UIManager.getColor("textInactiveText"));
            } else {
                comp.setForeground(getForeground());
            }
        }

        if( comp instanceof ComboInplaceEditor )
            comp.setEnabled( isEnabled() && getPropertyEnv().isEditable() );
        else
            comp.setEnabled(isEnabled() && PropUtils.checkEnabled(this, inplace.getPropertyEditor(), getPropertyEnv()));
    }

    @SuppressWarnings("deprecation")
    public void reshape(int x, int y, int w, int h) {
        if (inner != null) {
            inner.setBounds(0, 0, w, h);
        }

        super.reshape(x, y, w, h);
    }

    protected void setPropertyEnv(PropertyEnv env) {
        this.env = env;
    }

    public final PropertyEnv getPropertyEnv() {
        return env;
    }

    protected final InplaceEditor getInplaceEditor() {
        return inplace;
    }

    protected void configureButtonPanel(ButtonPanel bp) {
    }

    /** Basically some hacks to acquire the underlying property descriptor in
     * the case of a wrapper.  This is here because some property editors will
     * cast the result of PropertyEnv.getFeatureDescriptor() as a specific
     * implementation type, so even if we're wrapping a property model, we
     * still need to make sure we're returning the class they expect.  */
    static final FeatureDescriptor findFeatureDescriptor(PropertyDisplayer pd) {
        if (pd instanceof EditorPropertyDisplayer) {
            //Issue 38004, more gunk to ensure we get the right feature
            //descriptor
            EditorPropertyDisplayer epd = (EditorPropertyDisplayer) pd;

            if (epd.modelRef != null) {
                PropertyModel pm = epd.modelRef.get();

                if (pm instanceof ExPropertyModel) {
                    FeatureDescriptor fd = ((ExPropertyModel) pm).getFeatureDescriptor();

                    if (fd != null) {
                        return fd;
                    }
                }
            }
        }

        Property p = pd.getProperty();

        if (p instanceof ModelProperty) {
            return ((ModelProperty) p).getFeatureDescriptor();
        } else if (p instanceof ModelProperty.DPMWrapper) {
            return ((ModelProperty.DPMWrapper) p).getFeatureDescriptor();
        } else {
            return p;
        }
    }

    private InplaceEditor createInplaceEditor() {
        PropertyEnv env = new PropertyEnv();
        env.setFeatureDescriptor(findFeatureDescriptor(this));

        InplaceEditor result;

        //Get the real inplace editor
        InplaceEditor innermost = result = factory(this).getInplaceEditor(getProperty(), env, true);

        //        System.err.println("  CREATE INPLACE EDITOR - INNERMOST IS " + innermost);
        //See if it should be embedded in an instance of ButtonPanel to show
        //the custom editor button
        if (isShowCustomEditorButton() && innermost.getPropertyEditor().supportsCustomEditor()) {
            ButtonPanel bp = new ButtonPanel();
            bp.setInplaceEditor(innermost);

            //            System.err.println("  wrapping in a buttonpanel");
            configureButtonPanel(bp);
            result = bp;
        }

        Icon ic = null;

        //See if there's an icon to display, either invalid state or
        //a property-specified icon
        if (env.getState() == env.STATE_INVALID) {
            ic = new ImageIcon(ImageUtilities.loadImage("org/openide/resources/propertysheet/invalid.gif")); //NOI18N
        } else if (getProperty().getValue("valueIcon") != null) { //NOI18N

            Object o = getProperty().getValue("valueIcon"); //NOI18N

            if (o instanceof Image) {
                ic = new ImageIcon((Image) o);
            } else {
                ic = (Icon) o;
            }
        }

        //If we have an icon, use an IconPanel to display it
        if (ic != null) {
            IconPanel iconPanel = new IconPanel();
            iconPanel.setIcon(ic);
            iconPanel.setInplaceEditor(result);

            //            System.err.println("  wrapping in an IconPanel");
            result = iconPanel;
        }

        setPropertyEnv(env);

        return result;
    }

    private InplaceEditorFactory factory(PropertyDisplayer_Inline inline) {
        InplaceEditorFactory result;

        if (inline.isTableUI()) {
            if (factory1 == null) {
                factory1 = new InplaceEditorFactory(inline.isTableUI(), inline.getReusablePropertyEnv());
            }

            result = factory1;
        } else {
            if (factory2 == null) {
                factory2 = new InplaceEditorFactory(inline.isTableUI(), inline.getReusablePropertyEnv());
            }

            result = factory2;
        }

        result.setUseRadioBoolean(inline.isRadioBoolean());
        result.setRadioButtonMax(inline.getRadioButtonMax());
        result.setUseLabels(inline.isUseLabels());

        return result;
    }

    public boolean isTitleDisplayed() {
        if (isUseLabels()) {
            InplaceEditor inp = null;

            if (inplace != null) {
                inp = inplace;
            } else {
                inp = createInplaceEditor();
            }

            InplaceEditor most = PropUtils.findInnermostInplaceEditor(inp);

            return (most instanceof RadioInplaceEditor || most instanceof CheckboxInplaceEditor);
        }

        return false;
    }

    public boolean isRadioBoolean() {
        return radioBoolean;
    }

    public void setRadioBoolean(boolean b) {
        radioBoolean = b;
    }

    public ReusablePropertyEnv getReusablePropertyEnv() {
        return reusableEnv;
    }

    static final Object[] findBeans(PropertyDisplayer pd) {
        Object[] result = null;

        if (pd instanceof EditorPropertyDisplayer) {
            //Issue 38132, fiendish evil to support PropertyEnv.getBeans()
            EditorPropertyDisplayer epd = (EditorPropertyDisplayer) pd;

            if (epd.modelRef != null) {
                PropertyModel pm = epd.modelRef.get();

                if (pm instanceof ExPropertyModel) {
                    result = ((ExPropertyModel) pm).getBeans();
                }
            }
        }

        if (result == null) {
            Property p = pd.getProperty();

            if (p instanceof ModelProperty) {
                result = ((ModelProperty) p).getBeans();
            } else if (p instanceof ModelProperty.DPMWrapper) {
                result = ((ModelProperty.DPMWrapper) p).getBeans();
            } else {
                if (
                    pd instanceof EditorPropertyDisplayer &&
                        ((EditorPropertyDisplayer) pd).getParent() instanceof PropertyPanel
                ) {
                    result = ((PropertyPanel) ((EditorPropertyDisplayer) pd).getParent()).getBeans();
                } else if (
                    pd instanceof RendererPropertyDisplayer &&
                        ((RendererPropertyDisplayer) pd).getParent() instanceof PropertyPanel
                ) {
                    result = ((PropertyPanel) ((RendererPropertyDisplayer) pd).getParent()).getBeans();
                }
            }
        }

        return result;
    }
}
