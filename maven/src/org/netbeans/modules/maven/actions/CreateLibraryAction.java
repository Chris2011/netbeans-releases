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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.maven.actions;

import org.codehaus.plexus.util.FileUtils;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import org.apache.maven.artifact.Artifact;
import org.netbeans.api.annotations.common.SuppressWarnings;
import org.netbeans.modules.maven.embedder.MavenEmbedder;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.project.libraries.LibrariesCustomizer;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.modules.maven.api.FileUtilities;
import org.netbeans.modules.maven.embedder.EmbedderFactory;
import org.netbeans.modules.maven.embedder.exec.ProgressTransferListener;
import org.netbeans.spi.java.project.support.JavadocAndSourceRootDetection;
import org.netbeans.spi.project.libraries.support.LibrariesSupport;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import static org.netbeans.modules.maven.actions.Bundle.*;

/**
 *
 * @author mkleint
 */
public class CreateLibraryAction extends AbstractAction implements LookupListener {
    private Lookup lookup;
    private Lookup.Result<DependencyNode> result;

    @Messages("ACT_Library=Create Library")
    public CreateLibraryAction(Lookup lkp) {
        this.lookup = lkp;
        putValue(NAME, ACT_Library());
        //TODO proper icon
        putValue(SMALL_ICON, ImageUtilities.image2Icon(ImageUtilities.loadImage("org/netbeans/modules/maven/actions/libraries.gif", true))); //NOI18N
        putValue("iconBase", "org/netbeans/modules/maven/actions/libraries.gif"); //NOI18N
        result = lookup.lookupResult(DependencyNode.class);
        setEnabled(result.allInstances().size() > 0);
        result.addLookupListener(this);

    }

    @Messages("LBL_CreateLibrary=Create a NetBeans Library from Maven metadata")
    public @Override void actionPerformed(ActionEvent e) {
        Iterator<? extends DependencyNode> roots = result.allInstances().iterator();
        if (!roots.hasNext()) {
            return;
        }
        final DependencyNode root = roots.next();
        final MavenProject project = lookup.lookup(MavenProject.class);
        final CreateLibraryPanel pnl = new CreateLibraryPanel(root);
        DialogDescriptor dd = new DialogDescriptor(pnl,  LBL_CreateLibrary());
        pnl.createValidations(dd);

        if (DialogDisplayer.getDefault().notify(dd) == DialogDescriptor.OK_OPTION) {
            RequestProcessor.getDefault().post(new Runnable() {
                public @Override void run() {
                    Library lib = createLibrary(pnl.getLibraryManager(), pnl.getLibraryName(), pnl.getIncludeArtifacts(), pnl.isAllSourceAndJavadoc(), project, pnl.getCopyDirectory());
                    if (lib != null) {
                        LibrariesCustomizer.showCustomizer(lib, pnl.getLibraryManager());
                    }
                }
            });
        }
    }

    public @Override void resultChanged(LookupEvent ev) {
        SwingUtilities.invokeLater(new Runnable() {
            public @Override void run() {
                setEnabled(result.allInstances().size() > 0);
            }
        });
    }

    @Messages({
        "MSG_Create_Library=Create Library",
        "MSG_Downloading=Maven: downloading {0}",
        "MSG_Downloading_javadoc=Maven: downloading Javadoc {0}",
        "MSG_Downloading_sources=Maven: downloading sources {0}"
    })
    @SuppressWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE") // baseFolder.mkdirs; will throw IOE later from getJarUri
    private Library createLibrary(LibraryManager libraryManager, String libraryName, List<Artifact> includeArtifacts, boolean allSourceAndJavadoc, MavenProject project, String copyTo) {
        ProgressHandle handle = ProgressHandleFactory.createHandle(MSG_Create_Library(),
                ProgressTransferListener.cancellable());
        int count = includeArtifacts.size() * (allSourceAndJavadoc ? 3 : 1) + 5;
        handle.start(count);
        try {
            MavenEmbedder online = EmbedderFactory.getOnlineEmbedder();
            int index = 1;
            List<Artifact> failed = new ArrayList<Artifact>();
            List<URI> classpathVolume = new ArrayList<URI>();
            List<URI> javadocVolume = new ArrayList<URI>();
            List<URI> sourceVolume = new ArrayList<URI>();
            Map<String, List<URI>> volumes = new HashMap<String, List<URI>>();
            File baseFolder = null;
            File nonDefaultLibBase = null;
            if (copyTo != null) {
                //resolve there to copy files
                URL libRoot = libraryManager.getLocation();
                File base = null;
                if (libRoot != null) {
                    try {
                        base = new File(libRoot.toURI());
                        //getLocation() points to a file
                        base = base.getParentFile();
                        nonDefaultLibBase = base;
                    } catch (URISyntaxException ex) {
                        Exceptions.printStackTrace(ex);
                        base = new File(System.getProperty("netbeans.user"), "libraries");
                    }
                }  else {
                    base = new File(System.getProperty("netbeans.user"), "libraries");
                }
                base = FileUtil.normalizeFile(base);
                baseFolder = FileUtilities.resolveFilePath(base, copyTo);
                baseFolder.mkdirs();
            }
            volumes.put("classpath", classpathVolume); //NOI18N
            if (allSourceAndJavadoc) {
                volumes.put("javadoc", javadocVolume); //NOI18N
                volumes.put("src", sourceVolume); //NOI18N
            }
            for (Artifact a : includeArtifacts) {
                handle.progress(MSG_Downloading(a.getId()), index);
                try {
                    online.resolve(a, project.getRemoteArtifactRepositories(), online.getLocalRepository());
                    classpathVolume.add(getJarUri(a, baseFolder, nonDefaultLibBase, TYPE_BINARY));
                    try {
                        if (allSourceAndJavadoc) {
                            handle.progress(MSG_Downloading_javadoc(a.getId()), index + 1);
                            Artifact javadoc = online.createArtifactWithClassifier(
                                    a.getGroupId(),
                                    a.getArtifactId(),
                                    a.getVersion(),
                                    a.getType(),
                                    "javadoc"); //NOI18N
                            online.resolve(javadoc, project.getRemoteArtifactRepositories(), online.getLocalRepository());
                            if (javadoc.getFile().exists()) {
                                URI javadocUri = getJarUri(javadoc, baseFolder, nonDefaultLibBase, TYPE_JAVADOC);
                                javadocVolume.add(javadocUri);
                            }

                            handle.progress(MSG_Downloading_sources(a.getId()), index + 2);
                            Artifact sources = online.createArtifactWithClassifier(
                                    a.getGroupId(),
                                    a.getArtifactId(),
                                    a.getVersion(),
                                    a.getType(),
                                    "sources"); //NOI18N
                            online.resolve(sources, project.getRemoteArtifactRepositories(), online.getLocalRepository());
                            if (sources.getFile().exists()) {
                                sourceVolume.add(getJarUri(sources, baseFolder, nonDefaultLibBase, TYPE_SOURCE));
                            }
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(CreateLibraryAction.class.getName()).log(Level.FINE, "Failed to download artifact", ex);
                    }

                } catch (Exception ex) {
                    failed.add(a);
                    Logger.getLogger(CreateLibraryAction.class.getName()).log(Level.FINE, "Failed to download artifact", ex);
                }
                index = index + (allSourceAndJavadoc ? 3 : 1);
            }
            try {
                handle.progress("Adding library",  index + 4);
                return  libraryManager.createURILibrary("j2se", libraryName, volumes); //NOI18N
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        } catch (ThreadDeath d) { // download interrupted
        } finally {
            handle.finish();
        }
        return null;
    }

    /** append path to given jar root uri */
    private static URI appendJarFolder(URI u, String jarFolder) {
        try {
            if (u.isAbsolute()) {
                return new URI("jar:" + u.toString() + "!/" + (jarFolder == null ? "" : jarFolder.replace('\\', '/'))); // NOI18N
            } else {
                return new URI(u.toString() + "!/" + (jarFolder == null ? "" : jarFolder.replace('\\', '/'))); // NOI18N
            }
        } catch (URISyntaxException e) {
            throw new AssertionError(e);
        }
    }


    private int TYPE_BINARY = 0;
    private int TYPE_JAVADOC = 1;
    private int TYPE_SOURCE = 2;

    URI getJarUri(Artifact a, File copyTo, File nonDefaultLibBase, int type) throws IOException {
        File res = a.getFile();
        URI uri = res.toURI();
        String jarPath = null;
        if (copyTo != null) {
            res = new File(copyTo, a.getFile().getName());
            FileUtils.copyFile(a.getFile(), res);
            if (nonDefaultLibBase != null) {
                String path = FileUtilities.getRelativePath(nonDefaultLibBase, res);
                if (path != null) {
                    uri = LibrariesSupport.convertFilePathToURI(path);
                }
            }
        }
        FileUtil.refreshFor(res);
        FileObject fo = FileUtil.toFileObject(res);
        if (type == TYPE_JAVADOC && FileUtil.isArchiveFile(fo)) {
            fo = FileUtil.getArchiveRoot(fo);
            FileObject docRoot = JavadocAndSourceRootDetection.findJavadocRoot(fo);
            if (docRoot != null) {
                jarPath = FileUtil.getRelativePath(fo, docRoot);
            }
        } else if (type == TYPE_SOURCE && FileUtil.isArchiveFile(fo)) {
            fo = FileUtil.getArchiveRoot(fo);
            FileObject srcRoot = JavadocAndSourceRootDetection.findSourceRoot(fo);
            if (srcRoot != null) {
                jarPath = FileUtil.getRelativePath(fo, srcRoot);
            }
        }
        return appendJarFolder(uri, jarPath);
    }

}
