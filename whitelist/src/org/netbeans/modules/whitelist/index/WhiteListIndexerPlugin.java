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
package org.netbeans.modules.whitelist.index;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LineMap;
import com.sun.source.tree.Tree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.Trees;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.whitelist.WhiteListQuery;
import org.netbeans.modules.java.preprocessorbridge.spi.JavaIndexerPlugin;
import org.netbeans.modules.parsing.lucene.support.DocumentIndex;
import org.netbeans.modules.parsing.lucene.support.Index;
import org.netbeans.modules.parsing.lucene.support.IndexDocument;
import org.netbeans.modules.parsing.lucene.support.IndexManager;
import org.netbeans.modules.parsing.lucene.support.Queries.QueryKind;
import org.netbeans.modules.parsing.spi.indexing.Indexable;
import org.netbeans.api.whitelist.index.WhiteListIndex;
import org.netbeans.api.whitelist.support.WhiteListSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Zezula
 */
public class WhiteListIndexerPlugin implements JavaIndexerPlugin {

    private static final String WHITE_LIST_INDEX = "whitelist"; //NOI18N
    private static final String MSG = "msg";    //NOI18N
    private static final String NAME = "name";  //NOI18N
    private static final String LINE = "line";  //NOI18N
    private static Map<URL,File> roots2whiteListDirs = new ConcurrentHashMap<URL, File>();

    private final URL root;
    private final File whiteListDir;
    private final WhiteListQuery.WhiteList whiteList;
    private final DocumentIndex index;

    private WhiteListIndexerPlugin(
            @NonNull final URL root,
            @NonNull final WhiteListQuery.WhiteList whiteList,
            @NonNull final File whiteListDir) throws IOException {
        assert root != null;
        assert whiteList != null;
        assert whiteListDir != null;
        this.root = root;
        this.whiteList = whiteList;
        this.whiteListDir = whiteListDir;
        this.index = IndexManager.createDocumentIndex(whiteListDir);
    }

    @Override
    public void process(
            @NonNull final CompilationUnitTree toProcess,
            @NonNull final Indexable indexable,
            @NonNull final Lookup services) {
        final Trees trees = services.lookup(Trees.class);
        assert trees != null;
        final Map<? extends Tree, ? extends WhiteListQuery.Result> problems = WhiteListSupport.getWhiteListViolations(toProcess, whiteList, trees, null);
        assert problems != null;
        final LineMap lm = toProcess.getLineMap();
        final SourcePositions sp = trees.getSourcePositions();
        for (Map.Entry<? extends Tree, ? extends WhiteListQuery.Result> p : problems.entrySet()) {
            final int start = (int) sp.getStartPosition(toProcess, p.getKey());
            int ln;
            if (start>=0 && (ln=(int)lm.getLineNumber(start))>=0) {
                final IndexDocument doc = IndexManager.createDocument(indexable.getRelativePath());
                assert !p.getValue().isAllowed() : "only violations should be stored"; // NOI18N
                for (WhiteListQuery.RuleDescription rule : p.getValue().getViolatedRules()) {
                    doc.addPair(MSG, rule.getRuleDescription(), false, true);
                    // TODO: whitelist ID should be stored here as well, no?
                }
                doc.addPair(LINE, Integer.toString(ln), false, true);
                index.addDocument(doc);
            }
        }
    }

    @Override
    public void delete(@NonNull final Indexable indexable) {
        index.removeDocument(indexable.getRelativePath());
    }

    @Override
    public void finish() {
        try {
            index.store(true);
            roots2whiteListDirs.put(root, whiteListDir);
            WhiteListIndexAccessor.getInstance().refresh(root);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                index.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    @CheckForNull
    private static DocumentIndex getIndex(@NonNull final FileObject root) {
        try {
            final File whiteListFolder = roots2whiteListDirs.get(root.getURL());
            if (whiteListFolder != null) {
                final DocumentIndex index = IndexManager.createDocumentIndex(whiteListFolder);
                return index.getStatus() == Index.Status.VALID ? index : null;
            }
        } catch (IOException ioe) {
            Exceptions.printStackTrace(ioe);
        }
        return null;
    }

    @NonNull
    public static Collection<? extends WhiteListIndex.Problem> getWhiteListViolations(
            @NonNull final FileObject root,
            @NullAllowed final FileObject resource) {
        final List<WhiteListIndex.Problem> result = new ArrayList<WhiteListIndex.Problem>();
        try {
            IndexManager.readAccess(new IndexManager.Action<Void>() {
                @Override
                public Void run() throws IOException, InterruptedException {
                    final DocumentIndex index = getIndex(root);
                    if (index != null) {
                        try {
                            for (IndexDocument doc : index.findByPrimaryKey(
                                    resource == null ? "" : FileUtil.getRelativePath(root,resource),    //NOI18N
                                    QueryKind.PREFIX)) {
                                try {
                                    final String key = doc.getPrimaryKey();
                                    String wlName = doc.getValue(NAME);
                                    if (wlName == null) {
                                        wlName = "";    //NOI18N
                                    }
                                    final String wlDesc[] = doc.getValues(MSG);
                                    assert wlDesc.length > 0 : "";
                                    List<WhiteListQuery.RuleDescription> violatedRules = new ArrayList<WhiteListQuery.RuleDescription>();
                                    for (String desc : wlDesc) {
                                        // TODO: whitelist ID is not stored currently is that ok?
                                        // TODO: how is whitelist name stored?? I did not find it anywhere
                                        violatedRules.add(new WhiteListQuery.RuleDescription(wlName, desc, null));
                                    }
                                    final int line = Integer.parseInt(doc.getValue(LINE));
                                    final WhiteListQuery.Result wr = new WhiteListQuery.Result(violatedRules);
                                    result.add(WhiteListIndexAccessor.getInstance().createProblem(wr, root, key, line));
                                } catch (ArithmeticException ae) {
                                    Exceptions.printStackTrace(ae);
                                }
                            }
                        } finally {
                            index.close();
                        }
                    }
                    return null;
                }
            });
        } catch (IOException e) {
            Exceptions.printStackTrace(e);
        } catch (InterruptedException e) {
            Exceptions.printStackTrace(e);
        }
        return result;
    }

    @MimeRegistration(mimeType="text/x-java",service=JavaIndexerPlugin.Factory.class)
    public static class Factory implements JavaIndexerPlugin.Factory {
        @Override
        public JavaIndexerPlugin create(final URL root, final FileObject cacheFolder) {
            try {
                File whiteListDir = roots2whiteListDirs.get(root);
                if (whiteListDir == null) {
                    //First time
                    final FileObject whiteListFolder = FileUtil.createFolder(cacheFolder, WHITE_LIST_INDEX);
                    whiteListDir = FileUtil.toFile(whiteListFolder);
                    if (whiteListDir == null) {
                        return null;
                    }
                }
                final FileObject rootFo = URLMapper.findFileObject(root);
                if (rootFo == null) {
                    delete(whiteListDir);
                    return null;
                } else {
                    final WhiteListQuery.WhiteList wl = WhiteListQuery.getWhiteList(rootFo);
                    if (wl == null) {
                        return null;
                    }
                    return new WhiteListIndexerPlugin(
                        root,
                        wl,
                        whiteListDir);
                }
            } catch (IOException ioe) {
                Exceptions.printStackTrace(ioe);
                return null;
            }
        }

        private static void delete(@NonNull final File folder) throws IOException {
            try {
                IndexManager.writeAccess(new IndexManager.Action<Void>(){
                    @Override
                    public Void run() throws IOException, InterruptedException {
                        deleteImpl(folder);
                        return null;
                    }
                });
            } catch (InterruptedException ex) {
                throw new IOException(ex);
            }
        }

        private static void deleteImpl (final File folder) {
            final File[] children = folder.listFiles();
            if (children != null) {
                for (File child : children) {
                    if (child.isDirectory()) {
                        deleteImpl(child);
                    }
                    child.delete();
                }
            }
        }
    }

}
