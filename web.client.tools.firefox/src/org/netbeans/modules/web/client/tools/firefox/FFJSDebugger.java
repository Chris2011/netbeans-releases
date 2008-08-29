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
package org.netbeans.modules.web.client.tools.firefox;

import java.io.IOException;
import java.net.URI;

import org.netbeans.modules.web.client.tools.api.JSLocation;
import org.netbeans.modules.web.client.tools.common.dbgp.Breakpoint;
import org.netbeans.modules.web.client.tools.common.dbgp.DbgpUtils;
import org.netbeans.modules.web.client.tools.common.launcher.Launcher;
import org.netbeans.modules.web.client.tools.common.launcher.Launcher.LaunchDescriptor;
import org.netbeans.modules.web.client.tools.common.launcher.Utils;
import org.netbeans.modules.web.client.tools.javascript.debugger.api.JSBreakpoint;
import org.netbeans.modules.web.client.tools.javascript.debugger.spi.JSAbstractExternalDebugger;
import org.openide.awt.HtmlBrowser;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

/**
 *
 * @author Sandip V. Chitale <sandipchitale@netbeans.org>, jdeva
 */
public class FFJSDebugger extends JSAbstractExternalDebugger {

    public FFJSDebugger(URI uri, HtmlBrowser.Factory browser) {
        super(uri, browser);
    }

    @Override
    protected void launchImpl(int port) {
        LaunchDescriptor launchDescriptor = new LaunchDescriptor(getBrowserExecutable());
        launchDescriptor.setURI(Utils.getDebuggerLauncherURI(port, getID()));
        if (!Utilities.isMac()) {
            launchDescriptor.setArguments(getBrowserArguments());
        }
        try {
            Launcher.launch(launchDescriptor);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }        
    }
    
    @Override
    protected boolean startDebuggingImpl() {
        boolean result = super.startDebuggingImpl();
        startHttpMonitorThread();
        return result;
    }    

    public String getID() {
        if (ID == null) {
            ID = FFJSDebuggerConstants.NETBEANS_FIREFOX_DEBUGGER + "-" + getSequenceId(); // NOI18N
        }
        return ID;
    }
    
    /*
     * Translates file URI
     */
    private String translateURI(String uri) {
        return uri.replaceFirst("\\Afile:/+", "file:///"); // NOI18N
    }
    
    @Override
    public String setBreakpoint(JSBreakpoint breakpoint) {
        Breakpoint.BreakpointSetCommand setCommand = DbgpUtils.getDbgpBreakpointCommand(proxy, breakpoint);
        JSLocation location = breakpoint.getLocation();
        String uri = location.getURI().toASCIIString();
        setCommand.setFileURI(translateURI(uri));
        return proxy.setBreakpoint(setCommand);
    }
}
