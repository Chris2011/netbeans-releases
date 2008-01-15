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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.bpel.project.anttasks;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.netbeans.modules.bpel.project.CommandlineBpelProjectXmlCatalogProvider;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Reference;

/**
 * Generates JBI Descriptor
 * @author Sreenivasan Genipudi
 */
public class IDEGenerateJBIDescriptor extends Task {

    public void execute() throws BuildException {
        if(this.mSourceDirectory == null) {
            throw new BuildException("No directory is set for source files.");
        }
        File sourceDirectory = new File(this.mSourceDirectory);
        // read project classpath
        ArrayList projectDirs = new ArrayList();

        if(this.mProjectClassPath != null
                && !this.mProjectClassPath.trim().equals("")
                && !this.mProjectClassPath.trim().equals("${javac.classpath}")) {
            StringTokenizer st = new StringTokenizer(this.mProjectClassPath, ";");

            while (st.hasMoreTokens()) {
                String spath = st.nextToken();
                try {
                    File sFile =  new File(sourceDirectory.getParentFile().getCanonicalPath() + File.separator + spath);
                    File srcFolder = new File(sFile.getParentFile().getParentFile().getCanonicalFile(), "src");
                    projectDirs.add(srcFolder);
                } catch(Exception ex) {
                    throw new BuildException("Failed to create File object for dependent project path "+ spath);
                }
            }
        }
        // find the owner project
        if(sourceDirectory != null) {
            ArrayList srcList = new ArrayList();
            srcList.add(sourceDirectory);
            CommandlineBpelProjectXmlCatalogProvider.getInstance().setSourceDirectory(this.mSourceDirectory);
            IDEJBIGenerator generator = new IDEJBIGenerator(projectDirs, srcList);
            generator.generate(new File(mBuildDirectory));
        }
    }

    public IDEGenerateJBIDescriptor() {}

    public void setBuildDirectory(String buildDir) {
        mBuildDirectory = buildDir;
    }

    public void setSourceDirectory(String srcDir) {
        this.mSourceDirectory = srcDir;
    }
    
    public void setClasspathRef(Reference ref) {
    }
    
    public String getSourceDirectory() {
        return this.mSourceDirectory;
    }
    
    public void setProjectClassPath(String projectClassPath) {
        this.mProjectClassPath = projectClassPath;
    }

    private String mSourceDirectory = null;
    private String mBuildDirectory = null;
    private String mProjectClassPath = null;
}
