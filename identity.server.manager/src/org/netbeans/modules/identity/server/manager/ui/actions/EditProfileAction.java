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

package org.netbeans.modules.identity.server.manager.ui.actions;

import java.awt.Dialog;
import org.netbeans.modules.identity.profile.api.configurator.SecurityMechanism;
import org.netbeans.modules.identity.server.manager.api.ServerInstance;
import org.netbeans.modules.identity.server.manager.ui.EditDialogDescriptor;
import org.netbeans.modules.identity.server.manager.ui.ProfileEditorPanel;
import org.netbeans.modules.identity.server.manager.ui.ProfileNode;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 * Action for bring up the editor panel for editing the security mechanism
 * profiles.
 *
 * Created on July 12, 2006, 8:55 PM
 *
 * @author ptliu
 */
public class EditProfileAction extends NodeAction {
    
    private static final String HELP_ID = "idmtools_am_config_am_sec_mech"; //NOI18N
    
    /** Creates a new instance of EditProfileAction */
    public EditProfileAction() {
    }
    
    
    protected void performAction(Node[] activatedNodes) {
        ProfileNode node = (ProfileNode) activatedNodes[0].getLookup().lookup(ProfileNode.class);
        SecurityMechanism secMech = node.getSecurityMechanism();
        ServerInstance instance = node.getServerInstance();
        final ProfileEditorPanel panel = new ProfileEditorPanel(secMech, instance);
        
        EditDialogDescriptor descriptor = new EditDialogDescriptor(
                panel,
                NbBundle.getMessage(EditProfileAction.class, "TTL_Profile",
                secMech.getName()),
                false,
                panel.getEditableComponents(),
                getHelpCtx()) {
            public String validate() {
                return panel.checkValues();
            }
        };
        
        panel.addChangeListener(descriptor);
  
        Dialog dlg = null;
        try {
            dlg = DialogDisplayer.getDefault().createDialog(descriptor);
            dlg.setVisible(true);
            
            if (descriptor.getValue().equals(DialogDescriptor.OK_OPTION)) {
                panel.save();
            }
        } finally {
            if (dlg != null) {
                dlg.dispose();
            }
        }
    }
    
    protected boolean enable(Node[] activatedNodes) {
        return true;
    }
    
    public String getName() {
        return NbBundle.getMessage(EditProfileAction.class,
                "LBL_Edit");
    }
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx(HELP_ID);
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
}
