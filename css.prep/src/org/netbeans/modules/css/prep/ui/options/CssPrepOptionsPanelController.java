/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.css.prep.ui.options;

import java.awt.EventQueue;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.css.prep.options.CssPrepOptions;
import org.netbeans.modules.css.prep.options.CssPrepOptionsValidator;
import org.netbeans.modules.css.prep.process.LessProcessor;
import org.netbeans.modules.css.prep.process.SassProcessor;
import org.netbeans.modules.css.prep.util.UiUtils;
import org.netbeans.modules.css.prep.util.ValidationResult;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

@OptionsPanelController.SubRegistration(
    location=UiUtils.OPTIONS_CATEGORY,
    id=UiUtils.OPTIONS_SUBCATEGORY,
    displayName="#CssPrepOptionsPanel.name" // NOI18N
)
public final class CssPrepOptionsPanelController extends OptionsPanelController implements ChangeListener {

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    // @GuardedBy("EDT")
    private CssPrepOptionsPanel cssPrepOptionsPanel = null;
    private volatile boolean changed = false;


    @Override
    public void update() {
        assert EventQueue.isDispatchThread();
        getCssPrepOptionsPanel().setSassPath(getCssPrepOptions().getSassPath());
        getCssPrepOptionsPanel().setLessPath(getCssPrepOptions().getLessPath());

        changed = false;
    }

    @Override
    public void applyChanges() {
        getCssPrepOptions().setSassPath(getCssPrepOptionsPanel().getSassPath());
        getCssPrepOptions().setLessPath(getCssPrepOptionsPanel().getLessPath());

        LessProcessor.warningShown = false;
        SassProcessor.warningShown = false;

        changed = false;
    }

    @Override
    public void cancel() {
    }

    @Override
    public boolean isValid() {
        assert EventQueue.isDispatchThread();
        CssPrepOptionsPanel panel = getCssPrepOptionsPanel();
        ValidationResult result = new CssPrepOptionsValidator()
                .validateSassPath(panel.getSassPath())
                .validateLessPath(panel.getLessPath())
                .getResult();
        // errors
        if (result.hasErrors()) {
            panel.setError(result.getErrors().get(0).getMessage());
            return false;
        }
        // warnings
        if (result.hasWarnings()) {
            panel.setWarning(result.getWarnings().get(0).getMessage());
            return true;
        }
        // everything ok
        panel.setError(" "); // NOI18N
        return true;
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    @Override
    public JComponent getComponent(Lookup masterLookup) {
        assert EventQueue.isDispatchThread();
        return getCssPrepOptionsPanel();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx("org.netbeans.modules.css.prep.ui.options.CssPrepOptionsPanelController"); // NOI18N
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener(l);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (!changed) {
            changed = true;
            propertyChangeSupport.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        propertyChangeSupport.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }

    private CssPrepOptionsPanel getCssPrepOptionsPanel() {
        assert EventQueue.isDispatchThread();
        if (cssPrepOptionsPanel == null) {
            cssPrepOptionsPanel = new CssPrepOptionsPanel();
            cssPrepOptionsPanel.addChangeListener(this);
        }
        return cssPrepOptionsPanel;
    }

    private CssPrepOptions getCssPrepOptions() {
        return CssPrepOptions.getInstance();
    }

}
