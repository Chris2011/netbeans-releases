/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.dlight.visualizers;

import java.awt.Component;
import java.awt.FlowLayout;
import java.io.Serializable;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.netbeans.modules.dlight.management.api.DLightManager;
import org.netbeans.modules.dlight.management.api.DLightSession;
import org.netbeans.modules.dlight.management.api.DLightSessionListener;
import org.netbeans.modules.dlight.spi.visualizer.Visualizer;
import org.netbeans.modules.dlight.spi.visualizer.VisualizerContainer;
import org.netbeans.modules.dlight.util.DLightLogger;
import org.netbeans.modules.dlight.visualizers.api.VisualizerToolbarComponentsProvider;
import org.netbeans.modules.dlight.visualizers.util.TimeIntervalPanel;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
public final class VisualizerTopComponentTopComponent extends TopComponent implements VisualizerContainer, DLightSessionListener {

    private static VisualizerTopComponentTopComponent instance;
    private static final String PREFERRED_ID = "VisualizerTopComponentTopComponent"; // NOI18N
    private JComponent visualizerContent;
    private final TimeIntervalPanel timeFilterPanel = new TimeIntervalPanel(null);
    private final JPanel extraToolbarContent = new JPanel(new FlowLayout(FlowLayout.LEADING));

    private VisualizerTopComponentTopComponent() {
        initComponents();
        toolbarPanel.add(timeFilterPanel);
        toolbarPanel.add(extraToolbarContent);

        updateToolbar(null);

        setName(NbBundle.getMessage(VisualizerTopComponentTopComponent.class, "CTL_VisualizerTopComponentTopComponent")); // NOI18N
        setToolTipText(NbBundle.getMessage(VisualizerTopComponentTopComponent.class, "HINT_VisualizerTopComponentTopComponent")); // NOI18N
//        setIcon(Utilities.loadImage(ICON_PATH, true));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toolbarPanel = new javax.swing.JPanel();
        visualizerPanel = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        toolbarPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 3, 3));
        add(toolbarPanel, java.awt.BorderLayout.PAGE_START);

        visualizerPanel.setLayout(new java.awt.BorderLayout());
        add(visualizerPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel toolbarPanel;
    private javax.swing.JPanel visualizerPanel;
    // End of variables declaration//GEN-END:variables

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link #findInstance}.
     */
    public static synchronized VisualizerTopComponentTopComponent getDefault() {
        if (instance == null) {
            instance = new VisualizerTopComponentTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the VisualizerTopComponentTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized VisualizerTopComponentTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(VisualizerTopComponentTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");//NOI18N
            return getDefault();
        }
        if (win instanceof VisualizerTopComponentTopComponent) {
            return (VisualizerTopComponentTopComponent) win;
        }
        Logger.getLogger(VisualizerTopComponentTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID +//NOI18N
                "' ID. That is a potential source of errors and unexpected behavior.");//NOI18N
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override
    public void componentOpened() {
    }

    @Override
    public void componentClosed() {
        DLightManager.getDefault().removeDLightSessionListener(this);
    }

    /** replaces this in object stream */
    @Override
    public Object writeReplace() {
        return new ResolvableHelper();
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    @Override
    public void setContent(String toolName, JComponent viewComponent) {
        DLightLogger.assertTrue(SwingUtilities.isEventDispatchThread());

        if (this.visualizerContent == viewComponent) {
            return;
        }
        visualizerContent = viewComponent;
        visualizerPanel.removeAll();
        visualizerPanel.add(viewComponent);

        extraToolbarContent.removeAll();

        if (viewComponent instanceof VisualizerToolbarComponentsProvider) {
            VisualizerToolbarComponentsProvider p = (VisualizerToolbarComponentsProvider) viewComponent;
            for (Component c : p.getToolbarComponents()) {
                extraToolbarContent.add(c);
            }
        }

        updateToolbar(DLightManager.getDefault().getActiveSession());

        setName(toolName);
        setToolTipText(toolName);

        validate();
        repaint();
    }

    /**
     * This implementation doesn't support multiple views. No tabs.
     * Adding a component when another one is already set will
     * substitute the component.
     * Adding the same component has no effect.
     *
     * @param toolID - not used
     * @param toolName - string that will be displayed in the TopComponent caption.
     * @param view - visualizer that ownes a component to be added
     */
    @Override
    public void addVisualizer(String toolID, String toolName, Visualizer<?> view) {
        setContent(toolName, view.getComponent());
        view.refresh();
    }

    @Override
    public void showup() {
        DLightLogger.assertTrue(SwingUtilities.isEventDispatchThread());

        open();
        requestActive();
    }

    @Override
    public void removeVisualizer(final Visualizer<?> v) {
        closePerformanceMonitor(v);
    }

    /**
     * This implementation doesn't support multiple views. No tabs.
     * Adding a component when another one is already set will
     * substitute the component.
     * Adding the same component has no effect.
     *
     * @param toolName - string that will be displayed in the TopComponent caption.
     * @param viewComponent - visualizer's component to add
     */
    @Override
    public void addContent(String toolName, JComponent viewComponent) {
        setContent(toolName, viewComponent);
    }

    @Override
    public void activeSessionChanged(DLightSession oldSession, DLightSession newSession) {
        updateToolbar(newSession);
    }

    @Override
    public void sessionAdded(DLightSession newSession) {
    }

    @Override
    public void sessionRemoved(DLightSession removedSession) {
    }

    private void updateToolbar(DLightSession session) {
        timeFilterPanel.setVisible(session != null);
        timeFilterPanel.update(session);
        extraToolbarContent.setVisible(extraToolbarContent.getComponentCount() > 0);
    }

    private static final class ResolvableHelper implements Serializable {

        private static final long serialVersionUID = 1L;

        public Object readResolve() {
            return VisualizerTopComponentTopComponent.getDefault();
        }
    }

    /**
     * This implementation doesn't support multiple views.
     * Method has no effect if current content differs from what is provided by
     * the visualizer.
     *
     * @param visualizer - visualizer whos component should be removed
     */
    public void closePerformanceMonitor(Visualizer<?> visualizer) {
        DLightLogger.assertTrue(SwingUtilities.isEventDispatchThread());

        if (visualizerContent != visualizer.getComponent()) { // nothing to do
            return;
        }

        visualizerPanel.removeAll();

        updateToolbar(null);

        setName(NbBundle.getMessage(VisualizerTopComponentTopComponent.class, "RunMonitorDetailes"));
        repaint();
    }
}
