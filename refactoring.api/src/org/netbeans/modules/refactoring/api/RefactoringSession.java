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
package org.netbeans.modules.refactoring.api;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.Document;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.refactoring.api.impl.ProgressSupport;
import org.netbeans.modules.refactoring.api.impl.SPIAccessor;
import org.netbeans.modules.refactoring.spi.RefactoringElementImplementation;
import org.netbeans.modules.refactoring.spi.RefactoringElementsBag;
import org.netbeans.modules.refactoring.spi.RefactoringCommit;
import org.netbeans.modules.refactoring.spi.Transaction;
import org.netbeans.modules.refactoring.spi.impl.UndoManager;
import org.netbeans.modules.refactoring.spi.impl.UndoableWrapper;
import org.netbeans.spi.editor.document.UndoableEditWrapper;
import org.openide.LifecycleManager;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.Parameters;


/** Class used to invoke refactorings.
 *
 * @author Martin Matula, Daniel Prusa, Jan Becicka
 */
public final class RefactoringSession {
    //private final LinkedList<RefactoringElementImplementation> internalList;
    private final ArrayList<RefactoringElementImplementation> internalList;
    private final RefactoringElementsBag bag;
    private final Collection<RefactoringElement> refactoringElements;
    private final String description;
    private ProgressSupport progressSupport;
    private UndoManager undoManager = UndoManager.getDefault();
    boolean realcommit = true;
    private AtomicBoolean finished = new AtomicBoolean(false);
    private AtomicBoolean prepareStarted = new AtomicBoolean(false);
    
    private RefactoringSession(String description) {
        //internalList = new LinkedList();
        internalList = new ArrayList<RefactoringElementImplementation>() ;
        bag = SPIAccessor.DEFAULT.createBag(this, internalList);
        this.description = description;
        this.refactoringElements = new ElementsCollection();
    }
    
    /** 
     * Creates a new refactoring session.
     * @param description textual description of this session
     * @return instance of RefactoringSession
     */
    @NonNull
    public static RefactoringSession create(@NonNull String description) {
        Parameters.notNull("description", description); // NOI18N
        return new RefactoringSession(description);
    }


    /**
     * process all elements from elements bags,
     * do all fileChanges
     * and call all commits
     * @param saveAfterDone save all if true
     * @return instance of Problem or null, if everything is OK
     */
    @CheckForNull
    public Problem doRefactoring(boolean saveAfterDone) {
        long time = System.currentTimeMillis();
        
        Iterator it = internalList.iterator();
        fireProgressListenerStart(0, internalList.size()+1);
        if (realcommit) {
            undoManager.transactionStarted();
            undoManager.setUndoDescription(description);
        }
        try {
            try {
                while (it.hasNext()) {
                    fireProgressListenerStep();
                    RefactoringElementImplementation element = (RefactoringElementImplementation) it.next();
                    if (element.isEnabled() && !((element.getStatus() == RefactoringElement.GUARDED) || (element.getStatus() == RefactoringElement.READ_ONLY))) {
                        element.performChange();
                    }
                }
            } finally {
                for (Transaction commit:SPIAccessor.DEFAULT.getCommits(bag)) {
                    SPIAccessor.DEFAULT.check(commit, false);
                }

                UndoableWrapper wrapper = MimeLookup.getLookup("").lookup(UndoableWrapper.class);
                for (Transaction commit:SPIAccessor.DEFAULT.getCommits(bag)) {
                    if (wrapper !=null)
                        setWrappers(commit, wrapper);
                    
                    commit.commit();
                    if (wrapper !=null)
                        unsetWrappers(commit, wrapper);
                }
                if (wrapper !=null)
                    wrapper.close();
                for (Transaction commit : SPIAccessor.DEFAULT.getCommits(bag)) {
                    SPIAccessor.DEFAULT.sum(commit);
                }
                
            }
            if (saveAfterDone) {
                LifecycleManager.getDefault().saveAll();
                for (DataObject dob:DataObject.getRegistry().getModified()) {
                    SaveCookie cookie = dob.getCookie(SaveCookie.class);
                    try {
                        cookie.save();
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
            for (RefactoringElementImplementation fileChange:SPIAccessor.DEFAULT.getFileChanges(bag)) {
                if (fileChange.isEnabled()) {
                    fileChange.performChange();
                }
            }
            fireProgressListenerStep();
        } finally {
            fireProgressListenerStop();
            if (realcommit) {
                undoManager.addItem(this);
                undoManager.transactionEnded(false);
                realcommit=false;
            }
        }
        Logger timer = Logger.getLogger("TIMER.RefactoringSession");
        if (timer.isLoggable(Level.FINE)) {
            time = System.currentTimeMillis() - time;
            timer.log(Level.FINE, "refactoringSession.doRefactoring", new Object[] { description, RefactoringSession.this, time } );
        }
        return null;
    }
    
    /**
     * do undo of previous doRefactoring()
     * @param saveAfterDone save all if true
     * @return instance of Problem or null, if everything is OK
     */
    @CheckForNull
    public Problem undoRefactoring(boolean saveAfterDone) {
        try {
            ListIterator it = internalList.listIterator(internalList.size());
            fireProgressListenerStart(0, internalList.size()+1);
            ArrayList<RefactoringElementImplementation> fileChanges = SPIAccessor.DEFAULT.getFileChanges(bag);
            ArrayList<Transaction> commits = SPIAccessor.DEFAULT.getCommits(bag);
            for (ListIterator<RefactoringElementImplementation> fileChangeIterator = fileChanges.listIterator(fileChanges.size()); fileChangeIterator.hasPrevious();) {
                RefactoringElementImplementation f = fileChangeIterator.previous();
                if (f.isEnabled()) {
                    f.undoChange();
                }
            }
            for (Transaction commit : SPIAccessor.DEFAULT.getCommits(bag)) {
                SPIAccessor.DEFAULT.check(commit, true);
            }
            UndoableWrapper wrapper = MimeLookup.getLookup("").lookup(UndoableWrapper.class);
            for (ListIterator<Transaction> commitIterator = commits.listIterator(commits.size()); commitIterator.hasPrevious();) {
                final Transaction commit = commitIterator.previous();
                setWrappers(commit, wrapper);
                commit.rollback();
                unsetWrappers(commit, wrapper);
            }
            wrapper.close();
            for (Transaction commit : SPIAccessor.DEFAULT.getCommits(bag)) {
                SPIAccessor.DEFAULT.sum(commit);
            }

            while (it.hasPrevious()) {
                fireProgressListenerStep();
                RefactoringElementImplementation element = (RefactoringElementImplementation) it.previous();
                if (element.isEnabled() && !((element.getStatus() == RefactoringElement.GUARDED) || (element.getStatus() == RefactoringElement.READ_ONLY))) {
                    element.undoChange();
                }
            }
            if (saveAfterDone) {
                LifecycleManager.getDefault().saveAll();
            }
            fireProgressListenerStep();
        } finally {
            fireProgressListenerStop();
        }
        return null;
    }
    
    /**
     * get elements from session
     * @return collection of RefactoringElements
     */
    @NonNull
    public Collection<RefactoringElement> getRefactoringElements() {
        if (!prepareStarted.get())
            return Collections.emptyList();
        return refactoringElements;
    }
    
    void started() {
        prepareStarted.set(true);
    }
    
    void finished() {
        finished.set(true);
    }
    
    /**
     *  Adds progress listener to this RefactoringSession
     * @param listener to add
     */
    public synchronized void addProgressListener(@NonNull ProgressListener listener) {
        Parameters.notNull("listener", listener); // NOI18N
        if (progressSupport == null ) {
            progressSupport = new ProgressSupport();
        }
        progressSupport.addProgressListener(listener);
    }

    /**
     * Remove progress listener from this RefactoringSession
     * @param listener to remove
     */
    public synchronized void removeProgressListener(@NonNull ProgressListener listener) {
        Parameters.notNull("listener", listener); // NOI18N
        if (progressSupport != null ) {
            progressSupport.removeProgressListener(listener); 
        }
    }

    RefactoringElementsBag getElementsBag() {
        return bag;
    }

    private void fireProgressListenerStart(int type, int count) {
        if (progressSupport != null) {
            progressSupport.fireProgressListenerStart(this, type, count);
        }
    }

    private void fireProgressListenerStep() {
        if (progressSupport != null) {
            progressSupport.fireProgressListenerStep(this);
        }
    }

    private void fireProgressListenerStop() {
        if (progressSupport != null) {
            progressSupport.fireProgressListenerStop(this);
        }
    }
    
    private void setWrappers(Transaction commit, UndoableWrapper wrap) {
        wrap.setActive(true);
        
        //        if (!(commit instanceof RefactoringCommit))
        //            return;
        //        for (FileObject f:((RefactoringCommit) commit).getModifiedFiles()) {
        //            Document doc = getDocument(f);
        //            if (doc!=null)
        //                doc.putProperty(BaseDocument.UndoableEditWrapper.class, wrap);
        //        }
    }

    private void unsetWrappers(Transaction commit, UndoableWrapper wrap) {
        wrap.setActive(false);

        //        setWrappers(commit, null);
    }

    private Document getDocument(FileObject f) {
        try {
            DataObject dob = DataObject.find(f);
            EditorCookie cookie = dob.getLookup().lookup(EditorCookie.class);
            if (cookie == null)
                return null;
            return cookie.getDocument();
        } catch (DataObjectNotFoundException ex) {
            return null;
        }
    }

    
    private class ElementsCollection extends AbstractCollection<RefactoringElement> {
        @Override
        public Iterator<RefactoringElement> iterator() {
            return new Iterator() {
                //private final Iterator<RefactoringElementImplementation> inner = internalList.iterator();
                private final Iterator<RefactoringElementImplementation> inner2 = SPIAccessor.DEFAULT.getFileChanges(bag).iterator();
                private int index = 0;

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
                
                @Override
                public RefactoringElement next() {
                    if (index < internalList.size()) {
                        return new RefactoringElement(internalList.get(index++));
                    } else {
                        return new RefactoringElement(inner2.next());
                    }
                }
                
                @Override
                public boolean hasNext() {
                    boolean hasNext = index < internalList.size();
                    if (hasNext) {
                        return hasNext;
                    }
                    while (!finished.get()) {
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                        hasNext = index < internalList.size();
                        if (hasNext)
                            return hasNext;
                    }
                    return index < internalList.size() || inner2.hasNext();
                }
            };
        }

        @Override
        public int size() {
            return internalList.size() + SPIAccessor.DEFAULT.getFileChanges(bag).size();
        }
    }
}
