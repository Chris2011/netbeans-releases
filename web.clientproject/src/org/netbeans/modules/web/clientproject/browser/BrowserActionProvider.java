/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.web.clientproject.browser;

import java.net.URL;
import org.netbeans.modules.web.browser.api.BrowserSupport;
import org.netbeans.modules.web.clientproject.ClientSideProject;
import org.netbeans.modules.web.clientproject.ClientSideProjectConstants;
import org.netbeans.modules.web.clientproject.CustomizerProviderImpl;
import org.netbeans.modules.web.clientproject.api.ServerURLMapping;
import org.netbeans.modules.web.clientproject.spi.webserver.WebServer;
import org.netbeans.spi.project.ActionProvider;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

public class BrowserActionProvider implements ActionProvider {

    final private ClientSideProject project;
    private final BrowserSupport support;
    private ClientProjectConfigurationImpl cfg;

    public BrowserActionProvider(ClientSideProject project, BrowserSupport support, ClientProjectConfigurationImpl cfg) {
        this.project = project;
        this.support = support;
        this.cfg = cfg;
    }
    
    @Override
    public String[] getSupportedActions() {
        return new String[] {COMMAND_RUN};
    }

    @Override
    public void invokeAction(String command, Lookup context) throws IllegalArgumentException {
        if (project.isUsingEmbeddedServer()) {
            WebServer.getWebserver().start(project, project.getSiteRootFolder(), project.getWebContextRoot());
        } else {
            WebServer.getWebserver().stop(project);
        }
        FileObject fo = null;
        String startFile = project.getEvaluator().getProperty(ClientSideProjectConstants.PROJECT_START_FILE);
        if (startFile == null) {
            startFile = "index.html";
        }
        if (COMMAND_RUN.equals(command)) {
            fo = project.getSiteRootFolder().getFileObject(startFile);
            if (fo == null) {
                DialogDisplayer.getDefault().notify(
                    new DialogDescriptor.Message("Main file "+startFile+" cannot be found and opened."));
                CustomizerProviderImpl cust = project.getLookup().lookup(CustomizerProviderImpl.class);
                cust.showCustomizer("buildConfig");
                // try again:
                fo = project.getSiteRootFolder().getFileObject(startFile);
                if (fo == null) {
                    return;
                }
            }
        } else if (COMMAND_RUN_SINGLE.equals(command)) {
            fo = getFile(context);
        }
        if (fo != null) {
            browseFile(support, fo);
        }
    }

    
    @Override
    public boolean isActionEnabled(String command, Lookup context) throws IllegalArgumentException {
//        Project prj = context.lookup(Project.class);
//        ClientSideConfigurationProvider provider = prj.getLookup().lookup(ClientSideConfigurationProvider.class);
//        if (provider.getActiveConfiguration().getBrowser() != null) {
//            return true;
//        }
//        return false;
            return true;
        }
    
    private FileObject getFile(Lookup context) {
        return context.lookup(FileObject.class);
    }

    private boolean isHTMLFile(FileObject fo) {
        return (fo != null && "html".equals(fo.getExt()));
    }
    
    private void browseFile(BrowserSupport bs, FileObject fo) {
        URL url = ServerURLMapping.toServer(project, fo);
        bs.load(url, fo);
    }
}
