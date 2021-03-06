/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */
package org.netbeans.modules.web.inspect.webkit.knockout;

import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.netbeans.modules.web.browser.api.Page;
import org.netbeans.modules.web.browser.api.PageInspector;
import org.netbeans.modules.web.inspect.webkit.WebKitPageModel;
import org.netbeans.modules.web.webkit.debugging.api.debugger.RemoteObject;
import org.openide.modules.OnStop;
import org.openide.util.RequestProcessor;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.TopComponentGroup;
import org.openide.windows.WindowManager;
import org.openide.windows.WindowSystemEvent;
import org.openide.windows.WindowSystemListener;

/**
 * Class responsible for opening and closing of Knockout view.
 *
 * @author Jan Stola
 */
public class KnockoutTCController implements PropertyChangeListener {
    /** Default instance of this class. */
    private static final KnockoutTCController DEFAULT = new KnockoutTCController();
    /** Request processor used by this class. */
    private static final RequestProcessor RP = new RequestProcessor(KnockoutTCController.class.getName(), 5);
    /** Current {@code KnockoutChecker}. */
    KnockoutChecker currentChecker;

    /**
     * Creates a new {@code KnockoutTCController}.
     */
    @SuppressWarnings("LeakingThisInConstructor") // NOI18N
    private KnockoutTCController() {
        PageInspector inspector = PageInspector.getDefault();
        inspector.addPropertyChangeListener(this);
    }

    /**
     * Returns the default instance of this class.
     * 
     * @return default instance of this class.
     */
    public static KnockoutTCController getDefault() {
        return DEFAULT;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propName = evt.getPropertyName();
        if (PageInspector.PROP_MODEL.equals(propName)) {
            updateTC();
        }
    }

    /**
     * Updates the state of Knockout view. This method can be called from
     * any thread.
     */
    private void updateTC() {
        if (EventQueue.isDispatchThread()) {
            updateTCInAWT();
        } else {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    updateTCInAWT();
                }
            });
        }
    }

    /**
     * Updates the state of Knockout view. This method can be called
     * from event-dispatch thread only.
     */
    private void updateTCInAWT() {
        Page inspectedPage = PageInspector.getDefault().getPage();
        if (inspectedPage == null) {
            synchronized (this) {
                currentChecker = null;
            }
            getKnockoutTCGroup().close();
        } else {
            synchronized (this) {
                currentChecker = new KnockoutChecker((WebKitPageModel)inspectedPage);
                currentChecker.startCheck();
            }
        }
    }

    /**
     * Returns the Knockout window group.
     * 
     * @return Knockout {@code TopComponentGroup}.
     */
    static TopComponentGroup getKnockoutTCGroup() {
        return WindowManager.getDefault().findTopComponentGroup("Knockout"); // NOI18N
    }

    /**
     * Returns the Knockout top component.
     * 
     * @return Knockout {@code TopComponent}.
     */
    static KnockoutTC getKnockoutTC() {
        return (KnockoutTC)WindowManager.getDefault().findTopComponent(KnockoutTC.ID);
    }

    /**
     * Determines whether the inspected page uses Knockout.
     * 
     * @return {@code true} when the inspected page uses knockout,
     * returns {@code false} otherwise.
     */
    public static boolean isKnockoutUsed() {
        return getKnockoutTC().isKnockoutUsed();
    }

    /**
     * Ensures that Knockout context is shown in Knockout TC.
     */
    public static void showKnockoutContext() {
        getKnockoutTCGroup().open();
        KnockoutTC tc = getKnockoutTC();
        tc.open();
        tc.requestActive();
        tc.showKnockoutContext();
    }

    /**
     * Class that checks whether the inspected page uses Knockout. It opens
     * Knockout window group when Knockout is found in the inspected page.
     */
    final class KnockoutChecker implements Runnable, PropertyChangeListener {
        /** Initial delay before the check. */
        private static final int initialDelay = 1;
        /** Current delay of the (repeated) check. */
        private int currentDelay = initialDelay;
        /** Page model associated with this checker. */
        private final WebKitPageModel pageModel;
        /** Task scheduled for the next check. */
        private ScheduledFuture currentTask;

        /**
         * Creates a new {@code KnockoutChecker} for the specified page model.
         * 
         * @param pageModel page model associated with the checker.
         */
        KnockoutChecker(WebKitPageModel pageModel) {
            this.pageModel = pageModel;
        }

        /**
         * Starts the check.
         */
        void startCheck() {
            scheduleKnockoutCheck(true);
            pageModel.addPropertyChangeListener(this);            
        }

        @Override
        public void run() {
            synchronized (KnockoutTCController.this) {
                if (currentChecker != this) {
                    return; // this checker is obsolete
                }
            }
            String expression = "window.NetBeans ? NetBeans.getKnockoutVersion() : null"; // NOI18N
            RemoteObject object = pageModel.getWebKit().getRuntime().evaluate(expression);
            boolean koFound = (object != null && object.getType() == RemoteObject.Type.STRING);
            if (koFound) {
                synchronized (this) {
                    currentTask = null;
                }
                openKnockoutTCGroup(object.getValueAsString());
            } else {
                // try it later
                scheduleKnockoutCheck(false);
            }
        }

        /**
         * Schedules a Knockout check.
         * 
         * @param initial {@code true} for the initial check, {@code false}
         * for a repeated one.
         */
        private synchronized void scheduleKnockoutCheck(boolean initial) {
            if (initial) {
                currentDelay = initialDelay;
            } else {
                currentDelay *= 2;
            }
            currentTask = RP.schedule(this, currentDelay, TimeUnit.SECONDS);
        }

        /**
         * Opens the Knockout top component group.
         * 
         * @param koVersion version of Knockout used by the inspected page.
         */
        private void openKnockoutTCGroup(final String koVersion) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TopComponentGroup group = getKnockoutTCGroup();
                    KnockoutTC knockoutTC = (KnockoutTC)getKnockoutTC();
                    knockoutTC.knockoutUsed(pageModel, koVersion);
                    Mode mode = WindowManager.getDefault().findMode(knockoutTC);
                    TopComponent selectedTC = mode.getSelectedTopComponent();
                    group.open();
                    if (selectedTC != null) {
                        // When the group is opened then Knockout view jumps
                        // to front by default. We don't want this.
                        // CSS Styles view is more important probably.
                        // This call moves the original TC from the same
                        // mode to front.
                        selectedTC.requestVisible();
                    }
                }
            });
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (Page.PROP_DOCUMENT.equals(evt.getPropertyName())) {
                synchronized (this) {
                    if (currentTask != null) {
                        currentTask.cancel(false);
                    }
                    scheduleKnockoutCheck(true);
                }
            }
        }
        
    }

    /**
     * Ensures that Knockout window group is closed when the IDE shuts down.
     */
    @OnStop
    public static class ShutdownHook implements Callable<Boolean>, WindowSystemListener {
        /** Determines whether the window system listener has been installed already. */
        private boolean listenerInstalled;

        @Override
        public Boolean call() throws Exception {
            if (!listenerInstalled) {
                listenerInstalled = true;
                WindowManager.getDefault().addWindowSystemListener(this);
            }
            return Boolean.TRUE;
        }

        @Override
        public void beforeLoad(WindowSystemEvent event) {
        }

        @Override
        public void afterLoad(WindowSystemEvent event) {
        }

        @Override
        public void beforeSave(WindowSystemEvent event) {
            // Close the group before window system saves its state (during IDE shutdown)
            getKnockoutTCGroup().close();
        }

        @Override
        public void afterSave(WindowSystemEvent event) {
        }

    }

}
