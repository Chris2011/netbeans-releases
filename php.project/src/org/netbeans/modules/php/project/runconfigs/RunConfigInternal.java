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
package org.netbeans.modules.php.project.runconfigs;

import java.io.File;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.php.project.PhpProject;
import org.netbeans.modules.php.project.ProjectPropertiesSupport;
import org.netbeans.modules.php.project.ui.customizer.PhpProjectProperties;
import org.netbeans.modules.php.project.util.PhpProjectUtils;
import org.openide.filesystems.FileUtil;
import org.openide.util.Mutex;

/**
 * Run configuration for INTERNAL SERVER (PHP 5.4+).
 */
public final class RunConfigInternal {

    public static final int DEFAULT_PORT = 8888;

    private String hostname;
    private String port;
    protected File routerParentDir;
    protected String routerRelativePath;


    private RunConfigInternal() {
    }

    public static PhpProjectProperties.RunAsType getRunAsType() {
        return PhpProjectProperties.RunAsType.INTERNAL;
    }

    public static String getDisplayName() {
        return getRunAsType().getLabel();
    }

    //~ Factories

    public static RunConfigInternal create() {
        return new RunConfigInternal();
    }

    public static RunConfigInternal forProject(final PhpProject project) {
        return ProjectManager.mutex().readAccess(new Mutex.Action<RunConfigInternal>() {
            @Override
            public RunConfigInternal run() {
                return new RunConfigInternal()
                        .setHostname(ProjectPropertiesSupport.getPhpInterpreter(project))
                        .setPort(ProjectPropertiesSupport.getPort(project))
                        .setRouterParentDir(FileUtil.toFile(ProjectPropertiesSupport.getWebRootDirectory(project)))
                        .setRouterRelativePath(ProjectPropertiesSupport.getInternalRouter(project));
            }
        });
    }

    //~ Methods

    public File getRouterFile() {
        return PhpProjectUtils.resolveFile(routerParentDir, routerRelativePath);
    }

    //~ Getters & Setters

    public String getHostname() {
        return hostname;
    }

    public RunConfigInternal setHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    public String getPort() {
        return port;
    }

    public RunConfigInternal setPort(String port) {
        this.port = port;
        return this;
    }

    public File getRouterParentDir() {
        return routerParentDir;
    }

    public RunConfigInternal setRouterParentDir(File routerParentDir) {
        this.routerParentDir = routerParentDir;
        return this;
    }

    public String getRouterRelativePath() {
        return routerRelativePath;
    }

    public RunConfigInternal setRouterRelativePath(String routerRelativePath) {
        this.routerRelativePath = routerRelativePath;
        return this;
    }

}
