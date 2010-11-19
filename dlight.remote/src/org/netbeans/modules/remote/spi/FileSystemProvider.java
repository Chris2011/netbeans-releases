/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.remote.spi;

import java.io.File;
import java.util.Collection;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;

/**
 * A temporary solution until we have an official file system provider in thus module
 * @author Andrew Krasny
 * @author Vladimir Kvashin
 */
public abstract class FileSystemProvider {

    private static final FileSystemProvider DEFAULT = new FileSystemProviderImpl();

    protected FileSystemProvider() {
    }

    protected abstract FileSystem getFileSystemImpl(ExecutionEnvironment env, String root);
    protected abstract String normalizeAbsolutePathImpl(String absPath, ExecutionEnvironment env);
    protected abstract FileObject normalizeFileObjectImpl(FileObject fileObject);
    protected abstract FileObject getFileObjectImpl(FileObject baseFileObject, String relativeOrAbsolutePath);

    public static FileSystem getFileSystem(ExecutionEnvironment env) {
        return DEFAULT.getFileSystemImpl(env, "/"); //NOI18N
    }

    public static FileSystem getFileSystem(ExecutionEnvironment env, String root) {
        return DEFAULT.getFileSystemImpl(env, root);
    }

    public static String normalizeAbsolutePath(String absPath, ExecutionEnvironment env) {
        return DEFAULT.normalizeAbsolutePathImpl(absPath, env);
    }

    public static FileObject normalizeFileObject(FileObject fileObject) {
        return DEFAULT.normalizeFileObjectImpl(fileObject);
    }

    /**
     * In many places, standard sequence is as follows:
     *  - convert path to absolute if need
     *  - normalize it
     *  - find file object
     * In the case of non-local file systems we should delegate it to correspondent file systems.
     */
    public static FileObject getFileObject(FileObject baseFileObject, String relativeOrAbsolutePath) {
        return DEFAULT.getFileObjectImpl(baseFileObject, relativeOrAbsolutePath);
    }

    private static class FileSystemProviderImpl extends FileSystemProvider {

        @Override
        protected FileSystem getFileSystemImpl(ExecutionEnvironment env, String root) {
            Collection<? extends FileSystemProvider> allProviders = Lookup.getDefault().lookupAll(FileSystemProvider.class);
            FileSystem result = null;

            for (FileSystemProvider provider : allProviders) {
                if ((result = provider.getFileSystemImpl(env, root)) != null) {
                    break;
                }
            }

            return result;
        }

        @Override
        protected String normalizeAbsolutePathImpl(String absPath, ExecutionEnvironment env) {
            Collection<? extends FileSystemProvider> allProviders = Lookup.getDefault().lookupAll(FileSystemProvider.class);
            for (FileSystemProvider provider : allProviders) {
                String result = provider.normalizeAbsolutePathImpl(absPath, env);
                if (result != null) {
                    return result;
                }
            }
            return FileUtil.normalizePath(absPath);
        }

        @Override
        protected  FileObject normalizeFileObjectImpl(FileObject fileObject) {
            Collection<? extends FileSystemProvider> allProviders = Lookup.getDefault().lookupAll(FileSystemProvider.class);
            FileObject result;
            for (FileSystemProvider provider : allProviders) {
                result = provider.normalizeFileObjectImpl(fileObject);
                if (result != null) {
                    return result;
                }
            }
            String normalizedPath = FileUtil.normalizePath(fileObject.getPath());
            if (normalizedPath.equals(fileObject.getPath())) {
                result = fileObject;
            } else {
                result = FileUtil.toFileObject(new File(normalizedPath));;
            }
            return result;
        }

        @Override
        protected FileObject getFileObjectImpl(FileObject baseFileObject, String relativeOrAbsolutePath) {
            Collection<? extends FileSystemProvider> allProviders = Lookup.getDefault().lookupAll(FileSystemProvider.class);
            FileObject result;
            for (FileSystemProvider provider : allProviders) {
                result = provider.getFileObjectImpl(baseFileObject, relativeOrAbsolutePath);
                if (result != null) {
                    return result;
                }
            }
            return null;
        }
    }
}
