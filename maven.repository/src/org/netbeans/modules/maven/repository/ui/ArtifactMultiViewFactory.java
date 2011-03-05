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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.maven.repository.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.swing.Action;
import javax.swing.JButton;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.DefaultMavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Profile;
import org.apache.maven.plugin.LegacySupport;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.netbeans.api.progress.aggregate.AggregateProgressFactory;
import org.netbeans.api.progress.aggregate.AggregateProgressHandle;
import org.netbeans.api.progress.aggregate.ProgressContributor;
import org.netbeans.api.project.Project;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewFactory;
import org.netbeans.modules.maven.api.CommonArtifactActions;
import org.netbeans.modules.maven.api.NbMavenProject;
import org.netbeans.modules.maven.embedder.DependencyTreeFactory;
import org.netbeans.modules.maven.embedder.EmbedderFactory;
import org.netbeans.modules.maven.embedder.MavenEmbedder;
import org.netbeans.modules.maven.embedder.exec.ProgressTransferListener;
import org.netbeans.modules.maven.indexer.api.NBVersionInfo;
import org.netbeans.modules.maven.indexer.api.RepositoryInfo;
import org.netbeans.modules.maven.indexer.api.RepositoryPreferences;
import org.netbeans.modules.maven.indexer.api.RepositoryUtil;
import org.netbeans.modules.maven.indexer.spi.ui.ArtifactViewerFactory;
import org.netbeans.modules.maven.indexer.spi.ui.ArtifactViewerPanelProvider;
import org.netbeans.modules.maven.model.Utilities;
import org.netbeans.modules.maven.model.pom.POMModel;
import org.netbeans.modules.maven.model.pom.POMModelFactory;
import org.netbeans.modules.maven.repository.dependency.AddAsDependencyAction;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.modules.InstalledFileLocator;
import org.netbeans.modules.xml.xam.ModelSource;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.sonatype.aether.impl.internal.SimpleLocalRepositoryManager;
import org.sonatype.aether.util.DefaultRepositorySystemSession;

/**
 *
 * @author mkleint
 */
@ServiceProvider( service=ArtifactViewerFactory.class )
public final class ArtifactMultiViewFactory implements ArtifactViewerFactory {

    private static final RequestProcessor RP = new RequestProcessor(ArtifactMultiViewFactory.class);

    public TopComponent createTopComponent(Artifact artifact, List<ArtifactRepository> repos) {
        return createTopComponent(null, null, artifact, repos);
    }
    public TopComponent createTopComponent(NBVersionInfo info) {
        return createTopComponent(null, info, null, null);
    }

    public TopComponent createTopComponent(Project prj) {
        return createTopComponent(prj, null, null, null);
    }

    private TopComponent createTopComponent(final Project prj, final NBVersionInfo info, Artifact artifact, final List<ArtifactRepository> fRepos) {
        assert info != null || artifact != null || prj != null;
        final InstanceContent ic = new InstanceContent();
        AbstractLookup lookup = new AbstractLookup(ic);
        if (artifact == null && info != null) {
            artifact = RepositoryUtil.createArtifact(info);
        }
        if (artifact == null && prj != null) {
            NbMavenProject mvPrj = prj.getLookup().lookup(NbMavenProject.class);
            MavenProject mvn = mvPrj.getMavenProject();
            ic.add(prj);
            artifact = mvn.getArtifact();
        }
        assert artifact != null;
        ic.add(artifact);
        if (info != null) {
            ic.add(info);
        }
        final Artifact fArt = artifact;

        TopComponent existing = findExistingTc(artifact);
        if (existing != null) {
            return existing;
        }

        RP.post(new Runnable() {
            public void run() {
                MavenEmbedder embedder = EmbedderFactory.getOnlineEmbedder();
                MavenProject mvnprj;
                AggregateProgressHandle hndl = AggregateProgressFactory.createHandle(NbBundle.getMessage(NbMavenProject.class, "Progress_Download"),
                            new ProgressContributor[] {
                                AggregateProgressFactory.createProgressContributor("zaloha") },  //NOI18N
                            ProgressTransferListener.cancellable(), null);
                ProgressTransferListener.setAggregateHandle(hndl);
                hndl.start();
                try {
                    if (prj == null) {
                        List<ArtifactRepository> repos = new ArrayList<ArtifactRepository>();
                        if (fRepos != null) {
                            repos.addAll(fRepos);
                        }
                        if (repos.size() == 0) {
                            //add central repo
                            repos.add(EmbedderFactory.createRemoteRepository(embedder, RepositoryPreferences.REPO_CENTRAL, "central")); //NOI18N
                            //add repository form info
                            if (info != null && !"central".equals(info.getRepoId())) { //NOI18N
                                RepositoryInfo rinfo = RepositoryPreferences.getInstance().getRepositoryInfoById(info.getRepoId());
                                String url = rinfo.getRepositoryUrl();
                                if (url != null) {
                                    repos.add(EmbedderFactory.createRemoteRepository(embedder, url, rinfo.getId()));
                                }
                            }
                        }
                        mvnprj = readMavenProject(embedder, fArt, repos);
                    } else {
                        NbMavenProject im = prj.getLookup().lookup(NbMavenProject.class);
                        @SuppressWarnings("unchecked")
                        List<Profile> profiles = im.getMavenProject().getActiveProfiles();
                        List<String> profileIds = new ArrayList<String>();
                        for (Profile p : profiles) {
                            profileIds.add(p.getId());
                        }
                        mvnprj = im.loadAlternateMavenProject(embedder, profileIds, new Properties());
                        FileObject fo = prj.getLookup().lookup(FileObject.class);
                        if (fo != null) {
                            ModelSource ms = Utilities.createModelSource(fo);
                            if (ms.isEditable()) {
                                POMModel model = POMModelFactory.getDefault().getModel(ms);
                                if (model != null) {
                                    ic.add(model);
                                }
                            }
                        }
                    }

                    if(mvnprj != null){
                        ic.add(mvnprj);
                        DependencyNode root = DependencyTreeFactory.createDependencyTree(mvnprj, embedder, Artifact.SCOPE_TEST);
                        ic.add(root);
                    }

                } catch (ProjectBuildingException ex) {
                    ErrorPanel pnl = new ErrorPanel(ex);
                    DialogDescriptor dd = new DialogDescriptor(pnl, NbBundle.getMessage(ArtifactMultiViewFactory.class, "TIT_Error"));
                    JButton close = new JButton();
                    org.openide.awt.Mnemonics.setLocalizedText(close, NbBundle.getMessage(ArtifactMultiViewFactory.class, "BTN_CLOSE"));
                    dd.setOptions(new Object[] { close });
                    dd.setClosingOptions(new Object[] { close });
                    DialogDisplayer.getDefault().notify(dd);
                    File fallback = InstalledFileLocator.getDefault().locate("modules/ext/maven/fallback_pom.xml", "org.netbeans.modules.maven.embedder", false); //NOI18N
                    try {
                        MavenProject m = embedder.readProject(fallback);
                        m.setDescription(null);
                        ic.add(m);
                    } catch (Exception x) {
                        // oh well..
                        //NOPMD
                    }
                } catch (ThreadDeath d) { // download interrupted
                } finally {
                    hndl.finish();
                    ProgressTransferListener.clearAggregateHandle();
                }
            }
        });

        Action[] toolbarActions = new Action[] {
            new AddAsDependencyAction(fArt),
            CommonArtifactActions.createScmCheckoutAction(lookup),
            CommonArtifactActions.createLibraryAction(lookup)
        };
        ic.add(toolbarActions);

        Collection<? extends ArtifactViewerPanelProvider> provs = Lookup.getDefault().lookupAll(ArtifactViewerPanelProvider.class);
        MultiViewDescription[] panels = new MultiViewDescription[provs.size()];
        int i = 0;
        for (ArtifactViewerPanelProvider prov : provs) {
            panels[i] = prov.createPanel(lookup);
            i = i + 1;
        }
        TopComponent tc = MultiViewFactory.createMultiView(panels, panels[0]);
        tc.setDisplayName(artifact.getArtifactId() + ":" + artifact.getVersion()); //NOI18N
        tc.setToolTipText(artifact.getGroupId() + ":" + artifact.getArtifactId() + ":" + artifact.getVersion()); //NOI18N
        tc.putClientProperty(MAVEN_TC_PROPERTY, getTcId(artifact));
        return tc;
    }

    private static MavenProject readMavenProject(MavenEmbedder embedder, Artifact artifact, List<ArtifactRepository> remoteRepos) throws  ProjectBuildingException {
        //TODO rewrite
        MavenProjectBuilder bldr = embedder.lookupComponent(MavenProjectBuilder.class);
        assert bldr !=null : "MavenProjectBuilder component not found in maven";
        DefaultRepositorySystemSession session = new DefaultRepositorySystemSession();
        session.setLocalRepositoryManager(new SimpleLocalRepositoryManager(embedder.getLocalRepository().getBasedir()));
        embedder.lookupComponent(LegacySupport.class).setSession(new MavenSession(embedder.getPlexus(), session, new DefaultMavenExecutionRequest(), new DefaultMavenExecutionResult()));
        return bldr.buildFromRepository(artifact, remoteRepos, embedder.getLocalRepository()) ;
    }
    
    private static final String MAVEN_TC_PROPERTY = "mvn_tc_id";

    private static TopComponent findExistingTc(Artifact artifact) {
        String id = getTcId(artifact);
        Set<TopComponent> tcs = WindowManager.getDefault().getRegistry().getOpened();
        for (TopComponent tc : tcs) {
            if (id.equals(tc.getClientProperty(MAVEN_TC_PROPERTY))) {
                return tc;
            }
        }
        return null;
    }

    private static String getTcId(Artifact artifact) {
        return artifact.getGroupId() + ":" + artifact.getArtifactId() +
                ":" + artifact.getVersion();
    }

}
