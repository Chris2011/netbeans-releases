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
package org.netbeans.modules.cnd.actions;

import java.io.File;
import java.io.IOException;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 * @author Alexey Vladykin
 */
/*package*/ class OpenWithQtDesignerAction extends NodeAction {

    private static final String QTUI_MIME_TYPE = "text/xml+qtui"; // NOI18N
    private static final String DESIGNER_EXECUTABLE = "designer"; // NOI18N

    private final String name;

    public OpenWithQtDesignerAction() {
        name = NbBundle.getMessage(OpenWithQtDesignerAction.class, "LBL_OpenWithQtDesigner"); // NOI18N
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public String getName() {
        return name;
    }

    protected boolean enable(Node[] activatedNodes) {
        return getUiFile(activatedNodes) != null;
    }

    protected void performAction(Node[] activatedNodes) {
        File file = getUiFile(activatedNodes);
        if (file != null) {
            ProcessBuilder pb = new ProcessBuilder(DESIGNER_EXECUTABLE, file.getAbsolutePath());
            try {
                pb.start();
            } catch (IOException ex) {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                        NbBundle.getMessage(OpenWithQtDesignerAction.class, "MSG_OpenWithQtDesignerFailed", DESIGNER_EXECUTABLE))); // NOI18N
            }
        }
    }

    private static File getUiFile(Node[] activatedNodes) {
        if (activatedNodes != null && activatedNodes.length == 1) {
            DataObject dataObject = activatedNodes[0].getCookie(DataObject.class);
            if (dataObject != null) {
                FileObject fileObject = dataObject.getPrimaryFile();
                if (fileObject != null && QTUI_MIME_TYPE.equals(fileObject.getMIMEType())) {
                    File file = FileUtil.toFile(fileObject);
                    if (file != null) {
                        return file;
                    }
                }
            }
        }
        return null;
    }

}
