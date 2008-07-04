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

package org.netbeans.modules.cnd.remote.server;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.netbeans.modules.cnd.remote.support.RemoteCommandSupport;
import org.netbeans.modules.cnd.remote.support.RemoteCopySupport;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.NbBundle;

/**
 *
 * @author gordonp
 */
public class RemoteServerSetup {
    
    private static Logger log = Logger.getLogger("cnd.remote.logger");
    private static final String REMOTE_SCRIPT_DIR = ".netbeans/6.5/cnd2/scripts/"; // NOI18N
    private static final String LOCAL_SCRIPT_DIR = "src/scripts/"; // NOI18N
    private static final String GET_SCRIPT_INFO = "PATH=/bin:/usr/bin:$PATH  grep VERSION= " + REMOTE_SCRIPT_DIR + "* /dev/null"; // NOI18N
    
    private static Map<String, Double> setupMap;
    private static Map<String, List<String>> updateMap;
    
    static {
        setupMap = new HashMap<String, Double>();
        setupMap.put("getCompilerSets.bash", Double.valueOf(1.0));
        updateMap = new HashMap<String, List<String>>();
    }
    
    protected static void setup(String name) {
        List<String> list = updateMap.remove(name);
        boolean ok = true;
        String err = null;
        
        for (String script : list) {
            if (script.equals(REMOTE_SCRIPT_DIR)) {
                log.fine("RemoteServerSetup: Creating ~/" + REMOTE_SCRIPT_DIR);
                int exit_status = RemoteCommandSupport.run(name,
                        "PATH=/bin:/usr/bin:$PATH mkdir -p " + REMOTE_SCRIPT_DIR); // NOI18N
                if (exit_status == 0) {
                    for (String key : setupMap.keySet()) {
                        File file = InstalledFileLocator.getDefault().locate(LOCAL_SCRIPT_DIR + key, null, false);
                        ok |= RemoteCopySupport.copyTo(name, file.getAbsolutePath(), REMOTE_SCRIPT_DIR);
                        log.fine("RemoteServerSetup: Updating " + script);
                    }
                } else {
                    err = NbBundle.getMessage(RemoteServerSetup.class, "ERR_DirectorySetupFailure", name, exit_status);
                    ok = false;
                }
            } else {
                File file = InstalledFileLocator.getDefault().locate(LOCAL_SCRIPT_DIR + script, null, false);
                ok |= RemoteCopySupport.copyTo(name, file.getAbsolutePath(), REMOTE_SCRIPT_DIR);
                err = NbBundle.getMessage(RemoteServerSetup.class, "ERR_UpdateSetupFailure", name, script);
            }
        }
        if (!ok && err != null) {
            throw new IllegalStateException(err);
        }
    }

    static boolean needsSetupOrUpdate(String name) {
        List<String> updateList = new ArrayList<String>();
        
        updateMap.clear(); // remote entries if run for other remote systems
        RemoteCommandSupport support = new RemoteCommandSupport(name, GET_SCRIPT_INFO);
        if (support.getExitStatus() == 0) {
            String val = support.toString();
            for (String line : val.split("\n")) { // NOI18N
                try {
                    int pos = line.indexOf(':');
                    if (pos > 0 && line.length() > 0) {
                        String script = line.substring(REMOTE_SCRIPT_DIR.length(), pos);
                        Double installedVersion = Double.valueOf(line.substring(pos + 9));
                        Double expectedVersion = setupMap.get(script);
                        if (expectedVersion > installedVersion) {
                            log.fine("RemoteServerSetup: Need to update " + script);
                            updateList.add(script);
                        }
                    } else {
                        log.warning("RemoteServerSetup: Grep returned [" + line + "]");
                    }
                } catch (NumberFormatException nfe) {
                    log.warning("RemoteServerSetup: Bad response from remote grep comand (NFE parsing version)");
                } catch (Exception ex) {
                    log.warning("RemoteServerSetup: Bad response from remote grep comand: " + ex.getClass().getName());
                }
            }
        } else {
            log.fine("RemoteServerSetup: Need to create ~/" + REMOTE_SCRIPT_DIR);
            updateList.add(REMOTE_SCRIPT_DIR);
        }
        if (!updateList.isEmpty()) {
            updateMap.put(name, updateList);
            return true;
        } else {
            return false;
        }
    }
}
