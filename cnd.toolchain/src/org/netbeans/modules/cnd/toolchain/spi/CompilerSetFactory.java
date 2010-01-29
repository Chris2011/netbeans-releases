/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.cnd.toolchain.spi;

import org.netbeans.modules.cnd.toolchain.compilers.impl.CompilerSetImpl;
import org.netbeans.modules.cnd.toolchain.compilers.impl.CompilerFlavorImpl;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.cnd.toolchain.api.CompilerFlavor;
import org.netbeans.modules.cnd.toolchain.api.CompilerSet;
import org.netbeans.modules.cnd.toolchain.api.CompilerSetManager;
import org.netbeans.modules.cnd.toolchain.api.ToolchainManager.ToolchainDescriptor;
import org.netbeans.modules.cnd.toolchain.compilers.impl.ToolchainManagerImpl;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;

/**
 *
 * @author Alexander Simon
 */
public class CompilerSetFactory {

    private CompilerSetFactory() {
    }

    /**
     * Get an existing compiler set. If it doesn't exist, get an empty one based on the requested name.
     *
     * @param name The name of the compiler set we want
     * @returns The best fitting compiler set (may be an empty CompilerSet)
     */
    public static CompilerSet getCompilerSet(ExecutionEnvironment env, String name, int platform) {
        CompilerSet cs = CompilerSetManager.get(env).getCompilerSet(CompilerFlavorImpl.toFlavor(name, platform));
        if (cs == null) {
            CompilerFlavor flavor = CompilerFlavorImpl.toFlavor(name, platform);
            flavor = flavor == null ? CompilerFlavorImpl.getUnknown(platform) : flavor;
            cs = new CompilerSetImpl(flavor, "", null); // NOI18N
        }
        return cs;
    }

    public static List<CompilerFlavor> getCompilerSetFlavor(String directory, int platform) {
        List<CompilerFlavor> list = new ArrayList<CompilerFlavor>();
        for(ToolchainDescriptor d : ToolchainManagerImpl.getImpl().getToolchains(platform)) {
            if (d.isAbstract()) {
                continue;
            }
            if (ToolchainManagerImpl.getImpl().isMyFolder(directory, d, platform, false)){
                CompilerFlavor f = CompilerFlavorImpl.toFlavor(d.getName(), platform);
                if (f != null) {
                    list.add(f);
                }
            }
        }
        return list;
    }

    public static CompilerSet getCustomCompilerSet(String directory, CompilerFlavor flavor, String name) {
        CompilerSetImpl cs = new CompilerSetImpl(flavor, directory, name);
        cs.setAutoGenerated(false);
        return cs;
    }

}
