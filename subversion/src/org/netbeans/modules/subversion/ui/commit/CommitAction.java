/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.subversion.ui.commit;

import org.netbeans.modules.subversion.ui.actions.ContextAction;
import org.netbeans.modules.subversion.util.Context;
import org.netbeans.modules.subversion.*;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.nodes.Node;
import org.tigris.subversion.svnclientadapter.SVNClientException;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;
import org.netbeans.modules.subversion.util.SvnUtils;
import org.tigris.subversion.svnclientadapter.ISVNClientAdapter;
import org.tigris.subversion.svnclientadapter.SVNUrl;

/**
 * Commit action
 *
 * @author Petr Kuzel
 */
public class CommitAction extends ContextAction {

    protected String getBaseName(Node[] nodes) {
        return "CTL_MenuItem_Commit";    // NOI18N
    }

    /** Run commit action.  */
    public static void commit(Context ctx) {
        FileStatusCache cache = Subversion.getInstance().getStatusCache();
        File[] files = cache.listFiles(ctx, FileInformation.STATUS_LOCAL_CHANGE);

        if (files.length == 0) {
            return;
        }

        // show commit dialog
        CommitPanel panel = new CommitPanel();
        CommitTable data = new CommitTable(panel.filesLabel);
        SvnFileNode[] nodes;
        ArrayList nodesList = new ArrayList(files.length);

        for (int i = 0; i<files.length; i++) {
            File file = files[i];
            SvnFileNode node = new SvnFileNode(file);
            nodesList.add(node);
        }
        nodes = (SvnFileNode[]) nodesList.toArray(new SvnFileNode[files.length]);
        data.setNodes(nodes);

        JComponent component = data.getComponent();
        panel.filesPanel.setLayout(new BorderLayout());
        panel.filesPanel.add(component, BorderLayout.CENTER);

        DialogDescriptor dd = new DialogDescriptor(panel, "Commit");
        dd.setModal(true);
        JButton commitButton = new JButton("Commit");
        dd.setOptions(new Object[] {commitButton, "Cancel"});
        Dialog dialog = DialogDisplayer.getDefault().createDialog(dd);
        dialog.pack();
        dialog.setVisible(true);

        if (dd.getValue() == commitButton) {
            Map commitFiles = data.getCommitFiles();
            String message = panel.messageTextArea.getText();
            performCommit(message, commitFiles, ctx);
        }

        // if OK setup sequence of add, remove and commit calls
        
    }
    
    protected void performContextAction(Node[] nodes) {
        Context ctx = getContext(nodes);
        commit(ctx);
    }

    private static void performCommit(String message, Map commitFiles, Context ctx) {
        ProgressHandle progress = ProgressHandleFactory.createHandle("Committing...");
        try {
            progress.start();
                                               
            ISVNClientAdapter client;
            try {
                client = Subversion.getInstance().getClient(ctx);
            } catch (SVNClientException ex) {
                ex.printStackTrace(); // should not hapen
                return;
            }       
        
            List addCandidates = new ArrayList();
            List removeCandidates = new ArrayList();
            Set commitCandidates = new LinkedHashSet();

            Iterator it = commitFiles.keySet().iterator();
            while (it.hasNext()) {
                SvnFileNode node = (SvnFileNode) it.next();
                CommitOptions option = (CommitOptions) commitFiles.get(node);
                if (CommitOptions.ADD_BINARY == option) {
                    // set MIME property application/octet-stream
                    List l = listUnmanagedParents(node);  // FIXME coved scheduled but nor commited files!
                    Iterator dit = l.iterator();
                    while (dit.hasNext()) {
                        File file = (File) dit.next();
                        addCandidates.add(new SvnFileNode(file));
                        commitCandidates.add(file);
                    }
                    addCandidates.add(node);
                    commitCandidates.add(node.getFile());
                } else if (CommitOptions.ADD_TEXT == option) {
                    // assute no MIME property or startin gwith text
                    List l = listUnmanagedParents(node);
                    Iterator dit = l.iterator();
                    while (dit.hasNext()) {
                        File file = (File) dit.next();
                        addCandidates.add(new SvnFileNode(file));
                        commitCandidates.add(file);
                    }
                    addCandidates.add(node);
                    commitCandidates.add(node.getFile());
                } else if (CommitOptions.COMMIT_REMOVE == option) {
                    removeCandidates.add(node);
                    commitCandidates.add(node.getFile());
                } else if (CommitOptions.COMMIT == option) {
                    commitCandidates.add(node.getFile());
                }
            }

            // perform adds

            List addFiles = new ArrayList();
            List addDirs = new ArrayList();
            // XXX waht if user denied directory add but wants to add a file in it?
            it = addCandidates.iterator();
            while (it.hasNext()) {
                SvnFileNode svnFileNode = (SvnFileNode) it.next();
                File file = svnFileNode.getFile();
                if (file.isDirectory()) {
                    addDirs.add(file);
                } else if (file.isFile()) {
                    addFiles.add(file);
                }
            }

            it = addDirs.iterator();
            Set addedDirs = new HashSet();
            while (it.hasNext()) {
                File dir = (File) it.next();
                if (addedDirs.contains(dir)) {
                    continue;
                }
                client.addDirectory(dir, false);
                addedDirs.add(dir);
            }

            it = addFiles.iterator();
            while (it.hasNext()) {
                File file = (File) it.next();
                client.addFile(file);
            }

            // TODO perform removes

            // finally commit
            File[] files = (File[]) commitCandidates.toArray(new File[0]);
            client.commit(files, message, false);

            // XXX intercapt results and update cache

            FileStatusCache cache = Subversion.getInstance().getStatusCache();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                cache.refresh(file, FileStatusCache.REPOSITORY_STATUS_UNKNOWN);
            }
        } catch (SVNClientException ex) {
            ErrorManager.getDefault().notify(ex);
        } finally {
            progress.finish();
        }
    }

    private static List listUnmanagedParents(SvnFileNode node) {
        List unmanaged = new ArrayList();
        File file = node.getFile();
        File parent = file.getParentFile();
        while (true) {
            if (new File(parent, ".svn/entries").canRead() || new File(parent, "_svn/entries").canRead()) {
                break;
            }
            unmanaged.add(0, parent);
            parent = parent.getParentFile();
            if (parent == null) {
                break;
            }
        }

        List ret = new ArrayList();
        Iterator it = unmanaged.iterator();
        while (it.hasNext()) {
            File un = (File) it.next();
            ret.add(un);
        }

        return ret;
    }
}
