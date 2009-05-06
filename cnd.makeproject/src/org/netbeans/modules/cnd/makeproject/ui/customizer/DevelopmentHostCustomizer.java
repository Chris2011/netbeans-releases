/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.cnd.makeproject.ui.customizer;

import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import javax.swing.JOptionPane;
import org.netbeans.modules.cnd.api.compilers.CompilerSetManager;
import org.netbeans.modules.cnd.api.remote.ServerList;
import org.netbeans.modules.cnd.api.remote.ServerRecord;
import org.netbeans.modules.cnd.makeproject.api.configurations.DevelopmentHostConfiguration;
import org.netbeans.modules.cnd.utils.ui.ModalMessageDlg;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

/**
 * Popup a dialog which lets the user reconnect to an offline remote host.
 *
 * @author gordonp
 */
public class DevelopmentHostCustomizer extends JOptionPane implements VetoableChangeListener {

    private DevelopmentHostConfiguration dhconf;
    private PropertyEnv propertyEnv;

    /**
     * Show the customizer dialog. If we're already online, show a meaningless message (I don't think
     * we can disable the property editor just because we're online). If we're offline, let the user
     * decide if they want to try and reconnect. If they do, do the same reconnect done via a build or
     * run action.
     *
     * @param dhconf The remote host configuration
     * @param propertyEnv A PropertyEnv where we can control the custom property editor
     */
    public DevelopmentHostCustomizer(DevelopmentHostConfiguration dhconf, PropertyEnv propertyEnv) {
        super(NbBundle.getMessage(DevelopmentHostCustomizer.class, 
                dhconf.isConfigured() ? "ERR_NothingToDo" : "ERR_NeedToInitializeRemoteHost", dhconf.getDisplayName(false)), // NOI18N
                dhconf.isConfigured() ? INFORMATION_MESSAGE : QUESTION_MESSAGE,
                DEFAULT_OPTION, null, new Object[] { });
        this.dhconf = dhconf;
        this.propertyEnv = propertyEnv;
        if (!dhconf.isConfigured()) {
            propertyEnv.setState(PropertyEnv.STATE_NEEDS_VALIDATION);
            propertyEnv.addVetoableChangeListener(this);
        }
    }

    /**
     * Once the user presses OK, we attempt to validate the remote host. We never veto the action
     * because a failure should still close the property editor, but with the host still offline.
     * Set the PropertyEnv state to valid so the dialog is removed.
     *
     * @param evt A PropertyEnv where we can control the custom property editor
     * @throws java.beans.PropertyVetoException
     */
    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
        if (!dhconf.isConfigured()) {
            ExecutionEnvironment execEnv = dhconf.getExecutionEnvironment();
            final ServerRecord record = ServerList.get(execEnv);
            assert record != null;

            // start validation phase
            final Frame mainWindow = WindowManager.getDefault().getMainWindow();
            Runnable csmWorker = new Runnable() {
                public void run() {
                    try {
                        record.validate(true);
                        // initialize compiler sets for remote host if needed
                        CompilerSetManager csm = CompilerSetManager.getDefault(record.getExecutionEnvironment());
                        csm.initialize(true, true);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            // Note: Messages come from different class bundle...
            String msg = NbBundle.getMessage(getClass(), "MSG_Configure_Host_Progress", record.getDisplayName());
            final String title = NbBundle.getMessage(getClass(), "DLG_TITLE_Configure_Host", record.getExecutionEnvironment().getHost());
            ModalMessageDlg.runLongTask(mainWindow, csmWorker, null, null, title, msg);
            propertyEnv.removeVetoableChangeListener(this);
            propertyEnv.setState(PropertyEnv.STATE_VALID);
            if (!record.isOnline()) {
                System.err.println("");
            }
        }
    }
}
