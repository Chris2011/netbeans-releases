/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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

package org.netbeans.modules.apisupport.project.spi;

import java.io.File;
import java.io.IOException;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.modules.apisupport.project.api.LayerHandle;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.modules.SpecificationVersion;

/**
 * Interface to be implemented by NetBeans module projects. 
 *
 * @see org.netbeans.api.project.Project#getLookup
 * @author Martin Krauskopf, Milos Kleint
 * @since org.netbeans.modules.apisupport.project 1.15
 */
public interface NbModuleProvider {

    /**
     * Returns the specification version of the module
     * @return specification version of the module
     */ 
    @CheckForNull String getSpecVersion();
    
    /**
     * Returns the codenamebase of the module
     * @return module's codenamebase 
     */ 
    String getCodeNameBase();
    
    
    /** Returns a relative path to a project's source directory. 
     * @return relative path to sources..
     */
    String getSourceDirectoryPath();
    /**
     * relative path to the directory which contains/is to contain resources, META-INF/services folder or layer file for example
     * @param inTests 
     * @return relative path from project root to resource root.
     */
    String getResourceDirectoryPath(boolean inTests);

    /**
     * Gets a directory where you may place files to be copied unmodified to the cluster/NBM in an analogous tree structure.
     * You must call {@link #getReleaseDirectory} to actually make it.
     * @return relative path from project root to release dir
     */
    String getReleaseDirectoryPath();

    /**
     *
     * @return an actual directory corresponding to {@link #getReleaseDirectoryPath}
     * @throws IOException in case of problem
     */
    FileObject getReleaseDirectory() throws IOException;

    /**
     *  returns the relative path to the main project file (eg. nbproject/project.xml)
     * @return relative path from project root to the main project file.
     */
    String getProjectFilePath();
    
    /**
     * returns root directory with sources.
     * @return sources root FileObject
     */
    FileObject getSourceDirectory();
    
    /**
     * returns the location of the module's manifest
     * @return manifest FileObject.
     */ 
    @CheckForNull FileObject getManifestFile();
    
    /**
     * add/updates the given dependency to the project
     * @param codeNameBase 
     * @param releaseVersion 
     * @param version 
     * @param useInCompiler 
     * @return true if a dependency was really added
     * @throws IOException
     */
    boolean addDependency(
            final String codeNameBase, final String releaseVersion,
            final SpecificationVersion version, final boolean useInCompiler) throws IOException;
    
    /**
     * checks the declared version of the given dependency
     * @param codenamebase 
     * @return a known version, or null if unknown
     * @throws IOException
     */ 
    @CheckForNull SpecificationVersion getDependencyVersion(String codenamebase) throws IOException;

    /**
     * Checks whether the project currently has a (direct) dependency on the given module.
     * @since 1.37
     */
    boolean hasDependency(String codeNameBase) throws IOException;

    /**
     * Returns location of built module JAR (file need not to exist).
     *
     * Currently (6.7) used only for suite-chaining. May return <tt>null</tt>,
     * module project cannot be chained into another suite in such case.
     * @return location of built module JAR
     */
    @CheckForNull File getModuleJarLocation();

    /** Location of class output directory. */
    @CheckForNull File getClassesDirectory();

    /**
     * Creates a view of the system file system as it would be seen from this module and perhaps some siblings.
     * The project's own layer (if any) should be writable if possible, whereas platform layers may be read-only.
     * @see LayerUtil#mergeFilesystems
     * @see LayerUtil#layersOf
     * @see LayerHandle#forProject
     * @see PlatformJarProvider
     */
    @NonNull FileSystem getEffectiveSystemFilesystem() throws IOException;

}
