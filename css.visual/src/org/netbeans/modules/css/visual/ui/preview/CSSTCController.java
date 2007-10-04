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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.css.visual.ui.preview;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import org.netbeans.modules.css.editor.CssCloneableEditor;
import org.netbeans.modules.css.visual.ui.StyleBuilderTopComponent;
import org.openide.util.WeakListeners;
import org.openide.windows.TopComponent;
import org.openide.windows.TopComponent.Registry;
import org.openide.windows.WindowManager;

/**
 * Listens on the WindowManager.Registry for TCs and updates registered TCs accordingly.
 *
 * @author Marek Fukala
 */
public class CSSTCController implements PropertyChangeListener {
    
    //allow GCize the shared class instance if noone needs it anymore
    public static WeakReference<CSSTCController> instance;
    
    private boolean windowsOpened = false;
    
    /** Clients (CSSPreviewable TopComponent-s) should hold a strong reference to the
     * instance obtained by this method call during its livecycle.
     */
    public static synchronized CSSTCController getDefault() {
        if(instance == null) {
            instance = new WeakReference<CSSTCController>(new CSSTCController());
        }
        CSSTCController controllerInstance = instance.get();
        if(controllerInstance == null) {
            controllerInstance = new CSSTCController();
            instance = new WeakReference<CSSTCController>(controllerInstance);
            return controllerInstance;
        }
        return instance.get();
    }
    
    public CSSTCController() {
        //register a weak property change listener to the window manager registry
        Registry reg =  WindowManager.getDefault().getRegistry();
        reg.addPropertyChangeListener(
                WeakListeners.propertyChange(this, reg));
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        if (TopComponent.Registry.PROP_ACTIVATED.equals(evt.getPropertyName())) {
            //a TC activated -
            //check if the TC is editor TC and if so close the CSS preview and style builder
            TopComponent activated = (TopComponent)evt.getNewValue();
            if(isCSSTC(activated)) {
                previewableActivated();
                windowsOpened = true;
            } else {
                //issue 104603 workaround
                if(activated instanceof CssPreviewTopComponent
                        || activated instanceof StyleBuilderTopComponent) {
                    return ; //do not close the windows if user click on them
                }
                
                //A non - CSS previewable activated in editor - close the CSS windows
                if(WindowManager.getDefault().isOpenedEditorTopComponent(activated)) {
                    notPreviewableActivated();
                }
            }
        } else if(windowsOpened && TopComponent.Registry.PROP_TC_CLOSED.equals(evt.getPropertyName())) {
            //a TC closed - check if the TC is CSSpreviewable
            //check if the activated nodes
            TopComponent closedTC = (TopComponent)evt.getNewValue();
            if(isCSSTC(closedTC)) {
                //close the CSS windows
                //FIXME side effect is that the windows are close
                //and reopened again if another css previewable gets active
                notPreviewableActivated();
                windowsOpened = false;
            }
        }
    }
    
    private boolean isCSSTC(TopComponent tc) {
        return tc instanceof CssCloneableEditor;
//        if(tc.getActivatedNodes() != null) {
//            for(Node n : tc.getActivatedNodes()) {
//                CssPreviewable previewable = n.getCookie(CssPreviewable.class);
//                if(previewable != null) {
//                    return true;
//                }
//            }
//        }
//        return false;
    }
    
    private void previewableActivated() {
        WindowManager.getDefault().findTopComponentGroup("Csswsgrp").open();
    }
    
    private void notPreviewableActivated() {
        WindowManager.getDefault().findTopComponentGroup("Csswsgrp").close();
    }
    
}
