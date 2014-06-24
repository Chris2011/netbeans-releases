/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.git.ui.fetch;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jgit.transport.RefSpec;
import org.netbeans.libs.git.GitBranch;
import org.netbeans.libs.git.GitException;
import org.netbeans.libs.git.GitRefUpdateResult;
import org.netbeans.libs.git.GitRemoteConfig;
import org.netbeans.libs.git.GitTransportUpdate;
import org.netbeans.libs.git.GitTransportUpdate.Type;
import org.netbeans.libs.git.progress.ProgressMonitor;
import org.netbeans.modules.git.client.GitClient;
import org.netbeans.modules.git.client.GitProgressSupport;
import org.netbeans.modules.git.ui.branch.BranchSynchronizer;
import org.netbeans.modules.git.ui.output.OutputLogger;
import org.netbeans.modules.git.ui.repository.RepositoryInfo;
import org.netbeans.modules.git.ui.repository.Revision;
import org.netbeans.modules.git.utils.GitUtils;
import org.netbeans.modules.git.utils.LogUtils;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author ondra
 */
public final class FetchUtils {
    
    private static final Set<GitRefUpdateResult> UPDATED_STATUSES = new HashSet<>(Arrays.asList(
            GitRefUpdateResult.FAST_FORWARD,
            GitRefUpdateResult.FORCED,
            GitRefUpdateResult.NEW,
            GitRefUpdateResult.OK,
            GitRefUpdateResult.RENAMED
    ));
    private static final String TMP_REFS_PREFIX = "netbeans_tmp"; //NOI18N

    @Messages({
        "MSG_GetRemoteChangesAction.updates.noChange=No update",
        "# {0} - branch name",
        "# {1} - previous branch head",
        "# {2} - expected new branch head",
        "# {3} - real current branch head",
        "MSG_GetRemoteChangesAction.updates.updateBranch=Branch  : {0}\nOld Id : {1}\nNew Id : {2}\nResult : {3}",
        "# {0} - tag name",
        "# {1} - revision id",
        "MSG_GetRemoteChangesAction.updates.updateTag=Tag    : {0}\nResult : {1}\n"
    })
    static void log (File repository, Map<String, GitTransportUpdate> updates, OutputLogger logger) {
        if (updates.isEmpty()) {
            logger.outputLine(Bundle.MSG_GetRemoteChangesAction_updates_noChange()); //NOI18N
        } else {
            for (Map.Entry<String, GitTransportUpdate> e : updates.entrySet()) {
                GitTransportUpdate update = e.getValue();
                if (UPDATED_STATUSES.contains(update.getResult())) {
                    if (update.getType() == Type.BRANCH) {
                        logger.outputLine(Bundle.MSG_GetRemoteChangesAction_updates_updateBranch(update.getLocalName(), update.getOldObjectId(), update.getNewObjectId(), update.getResult()));
                        String oldId = update.getOldObjectId();
                        String newId = update.getNewObjectId();
                        String branchName = update.getLocalName();
                        LogUtils.logBranchUpdateReview(repository, branchName, oldId, newId, logger);
                    } else {
                        logger.outputLine(Bundle.MSG_GetRemoteChangesAction_updates_updateTag(update.getLocalName(), update.getResult()));
                    }
                }
            }
        }
    }
        
    private static String parseRemote (String branchName) {
        int pos = branchName.indexOf('/');
        String remoteName = null;
        if (pos > 0) {
            remoteName = branchName.substring(0, pos);
        }
        return remoteName;
    }

    @Messages({"# {0} - branch name", "MSG_Err.noRemote=No remote found for branch {0}",
        "# {0} - branch name", "MSG_Err.noUri=No URI specified for remote {0}",
        "# {0} - branch name", "MSG_Err.noSpecs=No fetch ref specs specified for remote {0}"})
    static GitRemoteConfig getRemoteConfigForActiveBranch (GitBranch trackedBranch, RepositoryInfo info, String errorLabel) {
        Map<String, GitRemoteConfig> remotes = info.getRemotes();
        String remoteName = parseRemote(trackedBranch.getName());
        GitRemoteConfig cfg = remoteName == null ? null : remotes.get(remoteName);
        if (cfg == null) {
            if (errorLabel != null) {
                GitUtils.notifyError(errorLabel, Bundle.MSG_Err_noRemote(trackedBranch.getName()));
            }
            return null;
        }
        if (cfg.getUris().isEmpty()) {
            if (errorLabel != null) {
                GitUtils.notifyError(errorLabel, Bundle.MSG_Err_noUri(cfg.getRemoteName()));
            }
            return null;
        }
        if (cfg.getFetchRefSpecs().isEmpty()) {
            if (errorLabel != null) {
                GitUtils.notifyError(errorLabel, Bundle.MSG_Err_noSpecs(cfg.getRemoteName()));
            }
            return null;
        }
        return cfg;
    }
    
    @NbBundle.Messages({
        "# {0} - branch name", "MSG_FetchUtils.noTrackingBranch=No tracking branch for \"{0}\"",
        "MSG_FetchUtils.noRemoteConfig=Remote configuration not found",
        "# {0} - remote name", "MSG_FetchUtils.noRemoteUrl=No remote URL for {0}"
    })
    public static Revision fetchToTemp (GitClient client, ProgressMonitor pm, GitBranch branch) throws GitException {
        if (!branch.isRemote()) {
            GitBranch trackedBranch = branch.getTrackedBranch();
            if (trackedBranch == null || !trackedBranch.isRemote()) {
                throw new GitException(Bundle.MSG_FetchUtils_noTrackingBranch(branch.getName()));
            }
            branch = trackedBranch;
        }
        GitRemoteConfig cfg = FetchUtils.getRemoteConfigForActiveBranch(branch, RepositoryInfo.getInstance(client.getRepositoryRoot()), null);
        if (cfg == null) {
            throw new GitException(Bundle.MSG_FetchUtils_noRemoteConfig());
        }
        if (cfg.getUris().isEmpty()) {
            throw new GitException(Bundle.MSG_FetchUtils_noRemoteUrl(cfg.getRemoteName()));
        }
        String remotePeer = findRemotePeer(cfg.getFetchRefSpecs(), branch);
        if (remotePeer == null) {
            // try backup, the same name
            remotePeer = branch.getName().split("/", 0)[1];
        }
        client.deleteBranch(TMP_REFS_PREFIX + "/" + remotePeer, true, GitUtils.NULL_PROGRESS_MONITOR);
        Map<String, GitTransportUpdate> updates = client.fetch(cfg.getUris().get(0), Collections.singletonList("+refs/heads/" + remotePeer + ":refs/" + TMP_REFS_PREFIX + "/" + remotePeer), pm);
        GitTransportUpdate upd = updates.get(TMP_REFS_PREFIX + "/" + remotePeer);
        if (upd != null) {
            client.deleteBranch(upd.getLocalName(), true, GitUtils.NULL_PROGRESS_MONITOR);
            new File(GitUtils.getGitFolderForRoot(client.getRepositoryRoot()), "refs/" + TMP_REFS_PREFIX).delete();
            return new Revision(upd.getNewObjectId(), upd.getLocalName());
        }
        return null;
    }
    
    private FetchUtils() {
        
    }

    static void syncTrackingBranches (File repository, Map<String, GitTransportUpdate> updates, GitProgressSupport supp) {
        List<String> branchNames = new ArrayList<>();
        RepositoryInfo info = RepositoryInfo.getInstance(repository);
        Map<String, GitBranch> branches = info.getBranches();
        RepositoryInfo.NBGitConfig cfg = info.getNetbeansConfig();
        for (Map.Entry<String, GitTransportUpdate> e : updates.entrySet()) {
            GitTransportUpdate update = e.getValue();
            if (UPDATED_STATUSES.contains(update.getResult())) {
                if (update.getType() == Type.BRANCH) {
                    String remoteBranchName = e.getValue().getLocalName();
                    for (GitBranch b : branches.values())  {
                        if (!b.isRemote() && !b.isActive() && b.getTrackedBranch() != null
                                && b.getTrackedBranch().getName().equals(remoteBranchName)
                                && cfg.getAutoSyncBranch(b.getName())) {
                            // this branch is not active, is local and tracks the remote branch
                            branchNames.add(b.getName());
                        }
                    }
                }
            }
        }
        try {
            new BranchSynchronizer().syncBranches(repository, branchNames.toArray(new String[branchNames.size()]), supp);
        } catch (GitException ex) {
            Logger.getLogger(FetchUtils.class.getName()).log(Level.INFO, null, ex);
        }
    }

    private static String findRemotePeer (List<String> fetchRefSpecs, GitBranch branch) {
        for (String refSpec : fetchRefSpecs) {
            RefSpec spec = new RefSpec(refSpec);
            if (spec.matchDestination(GitUtils.PREFIX_R_REMOTES + branch.getName())) {
                return spec.getSource().substring(GitUtils.PREFIX_R_HEADS.length());
            }
        }
        return null;
    }
}
