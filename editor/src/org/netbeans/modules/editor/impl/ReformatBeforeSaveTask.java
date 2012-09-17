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
package org.netbeans.modules.editor.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.undo.UndoableEdit;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.editor.settings.SimpleValueNames;
import org.netbeans.editor.GuardedDocument;
import org.netbeans.editor.MarkBlock;
import org.netbeans.lib.editor.util.GapList;
import org.netbeans.lib.editor.util.swing.BlockCompare;
import org.netbeans.lib.editor.util.swing.DocumentUtilities;
import org.netbeans.lib.editor.util.swing.PositionRegion;
import org.netbeans.modules.editor.indent.api.Reformat;
import org.netbeans.modules.editor.lib2.document.DocumentInternalUtils;
import org.netbeans.modules.editor.lib2.document.ModRootElement;
import org.netbeans.spi.editor.document.OnSaveTask;
import org.openide.util.Exceptions;

/**
 * Task doing reformatting done at save.
 *
 * @author Miloslav Metelka
 */
public class ReformatBeforeSaveTask implements OnSaveTask {

    // -J-Dorg.netbeans.modules.editor.impl.ReformatAtSaveTask.level=FINE
    private static final Logger LOG = Logger.getLogger(ReformatBeforeSaveTask.class.getName());

    private final Document doc;

    private Reformat reformat;

    private boolean modifiedLinesOnly;

    private List<PositionRegion> guardedBlocks;

    private int guardedBlockIndex;

    private Position guardedBlockStartPos;

    private Position guardedBlockEndPos;
    
    private AtomicBoolean canceled = new AtomicBoolean();

    ReformatBeforeSaveTask(Document doc) {
        this.doc = doc;
    }

    @Override
    public void performTask() {
        if (reformat != null) {
            reformat();
        }
    }

    @Override
    public void runLocked(Runnable run) {
        Preferences prefs = MimeLookup.getLookup(DocumentUtilities.getMimeType(doc)).lookup(Preferences.class);
        if (prefs.getBoolean(SimpleValueNames.ON_SAVE_USE_GLOBAL_SETTINGS, Boolean.TRUE)) {
            prefs = MimeLookup.getLookup(MimePath.EMPTY).lookup(Preferences.class);
        }
        String policy = prefs.get(SimpleValueNames.ON_SAVE_REFORMAT, "never"); //NOI18N
        if (!"never".equals(policy)) { //NOI18N
            modifiedLinesOnly = "modified-lines".equals(policy);
            reformat = Reformat.get(doc);
            reformat.lock();
            try {
                run.run();
            } finally {
                reformat.unlock();
            }
        } else {
            run.run();
        }
    }

    @Override
    public boolean cancel() {
        canceled.set(true);
        return true;
    }
    
    void reformat() {
        ModRootElement modRootElement = ModRootElement.get(doc);
        if (modRootElement != null) {
            boolean origEnabled = modRootElement.isEnabled();
            modRootElement.setEnabled(false);
            try {
                // Read all guarded blocks
                guardedBlocks = new GapList<PositionRegion>();
                if (doc instanceof GuardedDocument) {
                    MarkBlock block = ((GuardedDocument) doc).getGuardedBlockChain().getChain();
                    while (block != null) {
                        try {
                            guardedBlocks.add(new PositionRegion(doc, block.getStartOffset(), block.getEndOffset()));
                        } catch (BadLocationException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                        block = block.getNext();
                    }

                }

                guardedBlockIndex = 0;
                fetchNextGuardedBlock();
                Element modRootOrDocElement = (modifiedLinesOnly)
                        ? modRootElement
                        : DocumentInternalUtils.customElement(doc, 0, doc.getLength());
                int modElementCount = modRootOrDocElement.getElementCount();
                List<PositionRegion> formatBlocks = new ArrayList<PositionRegion>(modElementCount);
                for (int i = 0; i < modElementCount; i++) {
                    if (canceled.get()) {
                        return;
                    }
                    Element modElement = modRootOrDocElement.getElement(i);
                    boolean modElementFinished;
                    boolean add;
                    int startOffset = modElement.getStartOffset();
                    int modElementEndOffset = modElement.getEndOffset();
                    int endOffset = modElementEndOffset;
                    do {
                        if (guardedBlockStartPos != null) {
                            BlockCompare blockCompare = BlockCompare.get(
                                    startOffset,
                                    endOffset,
                                    guardedBlockStartPos.getOffset(),
                                    guardedBlockEndPos.getOffset());
                            if (blockCompare.before()) {
                                add = true;
                                modElementFinished = true;
                            } else if (blockCompare.after()) {
                                fetchNextGuardedBlock();
                                add = false;
                                modElementFinished = false;
                            } else if (blockCompare.equal()) {
                                fetchNextGuardedBlock();
                                add = false;
                                modElementFinished = true;
                            } else if (blockCompare.overlapStart()) {
                                endOffset = guardedBlockStartPos.getOffset();
                                add = true;
                                modElementFinished = true;
                            } else if (blockCompare.overlapEnd()) {
                                // Skip part covered by guarded block
                                endOffset = guardedBlockEndPos.getOffset();
                                fetchNextGuardedBlock();
                                add = false;
                                modElementFinished = false;
                            } else if (blockCompare.contains()) {
                                endOffset = guardedBlockStartPos.getOffset();
                                add = true;
                                modElementFinished = false;
                            } else if (blockCompare.inside()) {
                                add = false;
                                modElementFinished = true;
                            } else {
                                LOG.info("Unexpected blockCompare=" + blockCompare);
                                add = false;
                                modElementFinished = true;
                            }
                        } else {
                            add = true;
                            modElementFinished = true;
                        }
                        if (add) {
                            try {
                                if (startOffset != endOffset) {
                                    PositionRegion block = new PositionRegion(doc, startOffset, endOffset);
                                    if (LOG.isLoggable(Level.FINE)) {
                                        LOG.fine("Reformat-at-save: add block=" + block);
                                    }
                                    formatBlocks.add(block);
                                }
                            } catch (BadLocationException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                        }
                        startOffset = endOffset;
                        endOffset = modElementEndOffset;
                    } while (!modElementFinished);
                }

                try {
                    for (PositionRegion block : formatBlocks) {
                        if (canceled.get()) {
                            return;
                        }
                        reformat.reformat(block.getStartOffset(), block.getEndOffset());
                    }
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }

            } finally {
                modRootElement.setEnabled(origEnabled);
            }
        }
    }

    private void fetchNextGuardedBlock() {
        if (guardedBlockIndex < guardedBlocks.size()) {
            PositionRegion guardedBlock = guardedBlocks.get(guardedBlockIndex++);
            guardedBlockStartPos = guardedBlock.getStartPosition();
            guardedBlockEndPos = guardedBlock.getEndPosition();
        } else {
            guardedBlockEndPos = guardedBlockStartPos = null;
        }
    }

    @MimeRegistration(mimeType="", service=OnSaveTask.Factory.class, position=500)
    public static final class FactoryImpl implements Factory {

        @Override
        public OnSaveTask createTask(Context context) {
            return new ReformatBeforeSaveTask(context.getDocument());
        }

    }

}
