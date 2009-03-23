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

package org.netbeans.modules.cnd.remote.compilers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.StringTokenizer;
import org.netbeans.modules.cnd.remote.support.RemoteConnectionSupport;
import org.netbeans.modules.cnd.remote.support.ShellUtils;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;
import org.netbeans.modules.nativeexecution.api.NativeProcessBuilder;

/**
 * Manage the getCompilerSets script.
 * 
 * @author gordonp
 */
/*package-local*/ class CompilerSetScriptManager extends RemoteConnectionSupport {
        
    private BufferedReader in;
    private StringWriter out;
    private StringTokenizer st;
    private String platform;

    public CompilerSetScriptManager(ExecutionEnvironment env) {
        super(env);
    }

//    private String substituteCommand(String script) {
//        // The PATH stuff makes in much less likely to get a non-standard chmod...
//        String cmd = ShellUtils.prepareExportString(new String[] {"PATH=/bin:/usr/bin:$PATH"})+ "(chmod 755 " + script + ") && " + script; // NOI18N
//        log.finest("RemoteScriptSupport runs: " + cmd);
//        return ShellUtils.wrapCommand(executionEnvironment, cmd);
//    }


    private static int emulateFailure = Integer.getInteger("cnd.remote.failure", 0); // NOI18N

    public void runScript() {
        if (!isFailedOrCancelled()) {
            try {
                //String cmd = "(chmod 755 " + SCRIPT + ") && " + SCRIPT;
                String cmd = SCRIPT;
                NativeProcessBuilder pb = new NativeProcessBuilder(executionEnvironment, cmd);
                Process process = pb.call();
                InputStream is = process.getInputStream();
                in = new BufferedReader(new InputStreamReader(is));
                out = new StringWriter();

                if (emulateFailure>0) {
                    log.warning("CSSM.runScript: failure emulation [" + emulateFailure + "]"); // NOI18N
                    setFailed("failure emulation in CompilerSetScriptManager"); // NOI18N
                    emulateFailure--;
                    return;
                }


                String line;
                platform = in.readLine();
                log.fine("CSSM.runScript: Reading input from getCompilerSets.bash");
                log.fine("    platform [" + platform + "]");
                while ((line = in.readLine()) != null) {
                    log.fine("    line [" + line + "]");
                    out.write(line + '\n');
                    out.flush();
                }
                in.close();
                is.close();
                st = new StringTokenizer(out.toString());
            } catch (IOException ex) {
                log.warning("CSSM.runScript: IOException [" + ex.getMessage() + "]") ; // NOI18N
                setFailed(ex.getMessage());
//            } finally {
//                support.disconnect();
            }
        }
    }

    public static final String SCRIPT = ".netbeans/6.5/cnd2/scripts/getCompilerSets.bash"; // NOI18N

    public String getPlatform() {
        return platform;
    }

    public boolean hasMoreCompilerSets() {
        return st != null && st.hasMoreTokens();
    }

    public String getNextCompilerSetData() {
        String compilerSetInfo = st.nextToken();
        return compilerSetInfo;
    }
    
    @Override
    public String toString() {
        if (out != null) {
            return out.toString();
        } else {
            return "";
        }
    }
}
