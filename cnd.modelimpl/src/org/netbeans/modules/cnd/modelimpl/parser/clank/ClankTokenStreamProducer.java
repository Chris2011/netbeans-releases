/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2015 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2015 Sun Microsystems, Inc.
 */
package org.netbeans.modules.cnd.modelimpl.parser.clank;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.cnd.antlr.TokenStream;
import org.netbeans.modules.cnd.api.model.CsmInclude;
import org.netbeans.modules.cnd.api.model.CsmModelAccessor;
import org.netbeans.modules.cnd.api.model.xref.CsmReference;
import org.netbeans.modules.cnd.apt.support.APTHandlersSupport;
import org.netbeans.modules.cnd.apt.support.ClankDriver;
import org.netbeans.modules.cnd.apt.support.ClankDriver.ClankPreprocessorCallback;
import org.netbeans.modules.cnd.apt.support.ResolvedPath;
import org.netbeans.modules.cnd.apt.support.api.PPIncludeHandler;
import org.netbeans.modules.cnd.apt.support.api.PreprocHandler;
import org.netbeans.modules.cnd.apt.support.api.StartEntry;
import org.netbeans.modules.cnd.apt.support.lang.APTLanguageFilter;
import org.netbeans.modules.cnd.apt.utils.APTCommentsFilter;
import org.netbeans.modules.cnd.apt.utils.APTUtils;
import org.netbeans.modules.cnd.modelimpl.content.file.FileContent;
import org.netbeans.modules.cnd.modelimpl.csm.core.FileBuffer;
import org.netbeans.modules.cnd.modelimpl.csm.core.FileBufferFile;
import org.netbeans.modules.cnd.modelimpl.csm.core.FileImpl;
import org.netbeans.modules.cnd.modelimpl.csm.core.FilePreprocessorConditionState;
import org.netbeans.modules.cnd.modelimpl.csm.core.Line2Offset;
import org.netbeans.modules.cnd.modelimpl.csm.core.PreprocessorStatePair;
import org.netbeans.modules.cnd.modelimpl.csm.core.ProjectBase;
import org.netbeans.modules.cnd.modelimpl.debug.DiagnosticExceptoins;
import org.netbeans.modules.cnd.modelimpl.debug.TraceFlags;
import org.netbeans.modules.cnd.modelimpl.parser.clank.ClankToCsmSupport.UnresolvedIncludeDirectiveAnnotation;
import org.netbeans.modules.cnd.modelimpl.parser.clank.ClankToCsmSupport.UnresolvedIncludeDirectiveReason;
import org.netbeans.modules.cnd.modelimpl.parser.clank.ClankTokenStreamProducerParameters.YesNoInterested;
import org.netbeans.modules.cnd.modelimpl.parser.spi.TokenStreamProducer;
import org.netbeans.modules.cnd.support.Interrupter;
import org.netbeans.modules.cnd.utils.CndUtils;
import org.netbeans.modules.cnd.utils.cache.CharSequenceUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;

/**
 *
 * @author Vladimir Voskresensky
 */
public final class ClankTokenStreamProducer extends TokenStreamProducer {

    private int[] skipped;

    private ClankTokenStreamProducer(FileImpl file, FileContent newFileContent, boolean fromEnsureParsed) {
        super(file, newFileContent, fromEnsureParsed);
    }
    
    public static TokenStreamProducer createImpl(FileImpl file, FileContent newFileContent, boolean fromEnsureParsed) {
        return new ClankTokenStreamProducer(file, newFileContent, fromEnsureParsed);
    }

    public static List<CsmReference> getMacroUsages(FileImpl file, PreprocHandler handler, Interrupter interrupter) {
        FileContent newFileContent = FileContent.getHardReferenceBasedCopy(file.getCurrentFileContent(), true);
        // TODO: we do NOT need file content at all here
        ClankTokenStreamProducer tsp = new ClankTokenStreamProducer(file, newFileContent, false);
        PreprocHandler.State ppState = handler.getState();
        String contextLanguage = file.getContextLanguage(ppState);
        String contextLanguageFlavor = file.getContextLanguageFlavor(ppState);
        tsp.prepare(handler, contextLanguage, contextLanguageFlavor, false);
        ClankTokenStreamProducerParameters params = ClankTokenStreamProducerParameters.createForMacroUsages();
        List<CsmReference> res = tsp.getMacroUsages(params, interrupter);
        tsp.release();
        return res;
    }

    @Override
    public TokenStream getTokenStreamOfIncludedFile(PreprocHandler.State includeOwnerState, CsmInclude include, Interrupter interrupter) {
        FileImpl includeDirecitveFileOwner = getInterestedFile();
        FileImpl includedFile = (FileImpl) include.getIncludeFile();
        if (includedFile == null) {
            // error recovery
            return null;
        }
        ProjectBase projectImpl = includedFile.getProjectImpl(true);
        if (projectImpl == null) {
            // error recovery
            return null;
        }
        PPIncludeHandler.IncludeInfo inclInfo = createIncludeInfo(include);
        if (inclInfo == null) {
            // error recovery
            return null;
        }
        
        // prepare handler (which can be reset to default if smth goes wrong)
        PreprocHandler ppHandler = projectImpl.createPreprocHandlerFromState(includeDirecitveFileOwner.getAbsolutePath(), includeOwnerState);
        // retake state in case smth goes wrong above
        includeOwnerState = ppHandler.getState();
        LinkedList<PPIncludeHandler.IncludeInfo> includeChain = APTHandlersSupport.extractIncludeStack(includeOwnerState);
        if (CndUtils.isDebugMode()) {
            StartEntry startEntry = APTHandlersSupport.extractStartEntry(includeOwnerState);
            if (includeChain.isEmpty()) {
                assert startEntry.getFileSystem() == includeDirecitveFileOwner.getFileSystem();
                CndUtils.assertPathsEqualInConsole(startEntry.getStartFile(), includeDirecitveFileOwner.getAbsolutePath(), "different paths {0} vs. {1}", startEntry, includeDirecitveFileOwner);
            } else {
                PPIncludeHandler.IncludeInfo includer = includeChain.getLast();
                CndUtils.assertPathsEqualInConsole(includer.getIncludedPath(), includeDirecitveFileOwner.getAbsolutePath(), "different paths {0} vs. {1}", includer, includeDirecitveFileOwner);
                assert includer.getFileSystem() == includeDirecitveFileOwner.getFileSystem();
            }
        }
        // we've got include chain up to directive owner
        // add our include directive as the last entry point
        includeChain.addLast(inclInfo);
        
        // do preprocessing of include chain
        ClankTokenStreamProducerParameters params = ClankTokenStreamProducerParameters.createForIncludedTokenStream(getLanguage());
        VisitIncludeChainPreprocessorCallback callback = new VisitIncludeChainPreprocessorCallback(includeChain, params);
        boolean success = ClankDriver.preprocess(includeDirecitveFileOwner.getBuffer(), ppHandler, callback, interrupter);
        if (!success) {
            // error recovery
            return null;
        }
        ClankDriver.ClankPreprocessorOutput ppOutput = callback.getPreparedPreprocessorOutput();
        if (ppOutput == null) {
            // error recovery
            return null;
        }
        TokenStream tokenStream = ppOutput.getTokenStream();
        if (tokenStream == null) {
            // error recovery
            return null;
        }
        // unused, but init to prevent NPE
        skipped = new int[0];
        return tokenStream;
    }

    @Override
    public TokenStream getTokenStreamForParsingAndCaching(Interrupter interrupter) {
        final ClankTokenStreamProducerParameters params = ClankTokenStreamProducerParameters.createForParsingAndTokenStreamCaching();
        assertParamsReadyForCache(params);
        return getTokenStreamForInterestedFile(params, interrupter);
    }

    @Override
    public TokenStream getTokenStreamForParsing(String language, Interrupter interrupter) {
        return getTokenStream(ClankTokenStreamProducerParameters.createForParsing(language), interrupter);
    }

    @Override
    public TokenStream getTokenStreamForCaching(Interrupter interrupter) {
        ClankTokenStreamProducerParameters params = ClankTokenStreamProducerParameters.createForTokenStreamCaching();
        assertParamsReadyForCache(params);
        return getTokenStreamForInterestedFile(params, interrupter);
    }

    private static void assertParamsReadyForCache(ClankTokenStreamProducerParameters params) {
        boolean ready = (params.needTokens != YesNoInterested.NEVER)
                && (params.needComments != YesNoInterested.NEVER)
                && (params.needMacroExpansion != YesNoInterested.NEVER);
        if (!ready) {
            CndUtils.assertTrue(false, "Should be ready for cahcing: " + params);
        }
    }

    private TokenStream getTokenStream(ClankTokenStreamProducerParameters parameters, Interrupter interrupter) {
        PreprocHandler ppHandler = getCurrentPreprocHandler();
        ClankDriver.ClankPreprocessorOutput ppOutput = ClankDriver.extractPreprocessorOutput(ppHandler);
        assert ppOutput != null;
        FileImpl fileImpl = getInterestedFile();
        if (!ppOutput.hasTokenStream()) {
          // do preprocessing
          FileTokenStreamCallback callback = new FileTokenStreamCallback(
                  ppHandler,
                  parameters,
                  getStartFile(),
                  fileImpl,
                  ppOutput.getFileIndex());
          FileBuffer buffer = fileImpl.getBuffer();
          if (getCodePatch() != null) {
              buffer = new PatchedFileBuffer(buffer, getCodePatch());
          }
          boolean tsFromClank = ClankDriver.preprocess(buffer, ppHandler, callback, interrupter);
          if (!tsFromClank) {
              return null;
          }
          ppOutput = callback.getPPOut();
          if (ppOutput == null) {
            return null;
          }
          cacheMacroUsagesInFileIfNeed(parameters, callback.getPPOut());
        }
        TokenStream tokenStream = ppOutput.getTokenStream();
        if (tokenStream == null) {
          return null;
        }
        if (super.isFromEnsureParsed()) {
          ClankToCsmSupport.addPreprocessorDirectives(fileImpl, getFileContent(), ppOutput);
          ClankToCsmSupport.addMacroExpansions(fileImpl, getFileContent(), getStartFile(), ppOutput);
          ClankToCsmSupport.setFileGuard(fileImpl, getFileContent(), ppOutput);
        }
        skipped = ppOutput.getSkippedRanges();
        if (parameters.applyLanguageFilter) {
          APTLanguageFilter languageFilter = fileImpl.getLanguageFilter(ppHandler.getState());
          tokenStream = languageFilter.getFilteredStream(new APTCommentsFilter(tokenStream));
        }
        return tokenStream;
    }

    private List<CsmReference> getMacroUsages(ClankTokenStreamProducerParameters parameters, Interrupter interrupter) {
        ClankDriver.ClankPreprocessorOutput foundFileInfo = getPreprocessorOutputForInterestedFile(parameters, interrupter);
        List<CsmReference> out = ClankToCsmSupport.getMacroUsages(getInterestedFile(), getStartFile(), foundFileInfo);
        return out;
    }

    private TokenStream getTokenStreamForInterestedFile(ClankTokenStreamProducerParameters parameters, Interrupter interrupter) {
        TokenStream tokenStream = null;
        FileImpl fileImpl = getInterestedFile();
        ClankDriver.ClankPreprocessorOutput ppOutput = getPreprocessorOutputForInterestedFile(parameters, interrupter);
        if (ppOutput != null) {
            cacheMacroUsagesInFileIfNeed(parameters, ppOutput);
            tokenStream = ppOutput.getTokenStream();
        }
        if (tokenStream == null) {
          return null;
        }
        if (super.isFromEnsureParsed()) {
          ClankToCsmSupport.addPreprocessorDirectives(fileImpl, getFileContent(), ppOutput);
          ClankToCsmSupport.addMacroExpansions(fileImpl, getFileContent(), getStartFile(), ppOutput);
          ClankToCsmSupport.setFileGuard(fileImpl, getFileContent(), ppOutput);
        }
        skipped = ppOutput.getSkippedRanges();
        if (parameters.applyLanguageFilter) {
          PreprocHandler ppHandler = getCurrentPreprocHandler();
          APTLanguageFilter languageFilter = fileImpl.getLanguageFilter(ppHandler.getState());
          tokenStream = languageFilter.getFilteredStream(new APTCommentsFilter(tokenStream));
        }
        return tokenStream;
    }
    
    private ClankDriver.ClankPreprocessorOutput getPreprocessorOutputForInterestedFile(ClankTokenStreamProducerParameters parameters, Interrupter interrupter) {
        PreprocHandler ppHandler = getCurrentPreprocHandler();
        FileImpl fileImpl = getInterestedFile();
        FileImpl startFile = getStartFile();
        InterestedFileImplPreprocessorCallback callback = new InterestedFileImplPreprocessorCallback(
                startFile,
                fileImpl,
                ppHandler,
                parameters);
        ClankDriver.ClankPreprocessorOutput out = null;
        // buffer might be patched externally
        FileBuffer buffer = fileImpl.getBuffer();
        if (getCodePatch() != null) {
            buffer = new PatchedFileBuffer(buffer, getCodePatch());
        }
        if (ClankDriver.preprocess(buffer, ppHandler, callback, interrupter)) {
            out = callback.getPreparedPreprocessorOutput();
        }
        return out;
    }

    private void cacheMacroUsagesInFileIfNeed(ClankTokenStreamProducerParameters parameters, ClankDriver.ClankPreprocessorOutput foundFileInfo) {
        if (foundFileInfo == null) {
            return; // can this happen? should we assert? (softly!)
        }
        // TODO: shouldn't we introduce a special flag for this?
        if (parameters.needMacroExpansion == YesNoInterested.INTERESTED && parameters.needPPDirectives == YesNoInterested.INTERESTED) {
            FileImpl fileImpl = getInterestedFile();
            List<CsmReference> macroUsages = ClankToCsmSupport.getMacroUsages(fileImpl, getStartFile(), foundFileInfo);
            // FIXME: we should put found macro usages into FileContent, because we could be called in the loop and
            // in this case each iteration would overwrite result of previous
            fileImpl.setLastMacroUsages(macroUsages);
        }
    }
    
    @Override
    public FilePreprocessorConditionState release() {
        return FilePreprocessorConditionState.build(getInterestedFile().getAbsolutePath(), skipped);
    }
    
    private static final class FileTokenStreamCallback implements ClankPreprocessorCallback {
        private final ProjectBase startProject;
        private final PreprocHandler ppHandler;

        private final int stopAtIndex;
        private ClankDriver.ClankPreprocessorOutput preparedPreprocessorOutput;
        private final FileImpl startFile;
        private final FileImpl stopFileImpl;

        private enum State {
          INITIAL,
          SEEN,
          EXITED
        }
        private State alreadySeenInterestedFileEnter = State.INITIAL;
        private boolean insideInterestedFile = false;
        private boolean skipCurrentFileContentOptimization = false;
        private final ClankTokenStreamProducerParameters parameters;

        private final List<FileImpl> curFiles = new ArrayList<>();
        private final List<Boolean>  skipCurFileContentOptimizations = new ArrayList<>();

        private FileTokenStreamCallback(
                PreprocHandler ppHandler,
                ClankTokenStreamProducerParameters parameters,
                FileImpl startFileImpl,
                FileImpl stopFileImpl, 
                int stopAtIndex) {
            this.ppHandler = ppHandler;
            this.startFile = startFileImpl;
            this.startProject = startFileImpl.getProjectImpl(true);
            this.parameters = parameters;
            this.stopFileImpl = stopFileImpl;
            this.stopAtIndex = stopAtIndex;
        }

        boolean isTrace() {
          if (false && stopFileImpl.getName().toString().endsWith(".h")) {// NOI18N
            return true;
          }
          return false;
        }
        
        private boolean valueOf(/*YesNoInterested*/int param) {
            switch (param) {
                case YesNoInterested.ALWAYS:
                    return true;
                case YesNoInterested.NEVER:
                    return false;
                case YesNoInterested.INTERESTED:
                    return insideInterestedFile;
                default:
                    throw new AssertionError("unknown" + param);

            }
        }
        
        @Override
        public boolean needPPDirectives() {
            return !skipCurrentFileContentOptimization && valueOf(parameters.needPPDirectives);
        }

        @Override
        public boolean needTokens() {
            return !skipCurrentFileContentOptimization && valueOf(parameters.needTokens);
        }

        @Override
        public boolean needSkippedRanges() {
          return valueOf(parameters.needSkippedRanges);
        }

        @Override
        public boolean needMacroExpansion() {
            return valueOf(parameters.needMacroExpansion);
        }

        @Override
        public boolean needComments() {
            return !skipCurrentFileContentOptimization && valueOf(parameters.needComments);
        }

        /**
         * in the stack on tracked files return top one and pop if needed.
         * @param pop true to pop, false to peek only
         * @return non null top file
         */
        private FileImpl getCurFile(boolean pop) {
          assert curFiles.size() > 0;
          FileImpl curFile;
          if (pop) {
            curFile = curFiles.remove(curFiles.size() - 1);
          } else {
            curFile = curFiles.get(curFiles.size() - 1);
          }
          assert curFile != null;
          return curFile;
        }
        
        private void pushCurrentFile(FileImpl enteredToFileImpl, boolean canSkipFileContent) {
            curFiles.add(enteredToFileImpl);
            skipCurFileContentOptimizations.add(canSkipFileContent ? Boolean.TRUE : Boolean.FALSE);
        }
        
        private boolean getSkipCurrentFileContentOptimization(boolean pop) {
          assert skipCurFileContentOptimizations.size() > 0;
          Boolean curFileSkipOptimization;
          if (pop) {
            curFileSkipOptimization = skipCurFileContentOptimizations.remove(skipCurFileContentOptimizations.size() - 1);
          } else {
            curFileSkipOptimization = skipCurFileContentOptimizations.get(skipCurFileContentOptimizations.size() - 1);
          }
          assert curFileSkipOptimization != null;
          return curFileSkipOptimization;
        }

        private ProjectBase getStartProject() {
          return startProject;
        }
        
        @Override
        public void onInclusionDirective(ClankDriver.ClankFileInfo directiveOwner, ClankDriver.ClankInclusionDirective directive) {
            // always resolve path to have behavior like in APT, where file resolution
            // includes query to library manager which creates libraries on demand
            ResolvedPath resolvedPath = directive.getResolvedPath();
            if (resolvedPath == null) {
                // broken #include path
                directive.setAnnotation(UnresolvedIncludeDirectiveReason.NULL_PATH);
                return;
            }
            // peek file from onEnter
            FileImpl curFile = getCurFile(false);
            CharSequence path = resolvedPath.getPath();
            ProjectBase aStartProject = startProject;
            if (aStartProject != null) {
                // resolve if not interrupted
                if (aStartProject.isValid() && curFile.isValid()) {
                    ProjectBase inclFileOwner = aStartProject.getLibraryManager().resolveFileProjectOnInclude(aStartProject, curFile, resolvedPath);
                    if (inclFileOwner == null) {
                        // resolveFileProjectOnInclude() javadoc reads: "Can return NULL !"; and it asserts itself
                        if (aStartProject.getFileSystem() == resolvedPath.getFileSystem()) {
                            // if file systems do match, then use start project as fallback
                            inclFileOwner = aStartProject;
                        }
                    }
                    if (inclFileOwner == null) {
                        // error case
                        directive.setAnnotation(new UnresolvedIncludeDirectiveAnnotation(UnresolvedIncludeDirectiveReason.UNRESOLVED_FILE_OWNER, resolvedPath));
                        return;
                    }
                    if (CndUtils.isDebugMode()) {
                        CndUtils.assertTrue(inclFileOwner.getFileSystem() == resolvedPath.getFileSystem(), "Different FS for " + path + ": " + inclFileOwner.getFileSystem() + " vs " + resolvedPath.getFileSystem()); // NOI18N
                    }
                    // when owner of included file is detected we can ask it for FileImpl instance
                    FileImpl includedFile = inclFileOwner.prepareIncludedFile(aStartProject, path, ppHandler);
                    if (includedFile == null) {
                        if (CsmModelAccessor.isModelAlive() && inclFileOwner.isValid()) {
                            if (aStartProject.isValid()) {
                                // error case
                                APTUtils.LOG.log(Level.INFO, "something wrong when including {0} from {1}", new Object[]{path, curFile});
                                directive.setAnnotation(new UnresolvedIncludeDirectiveAnnotation(UnresolvedIncludeDirectiveReason.START_PROJECT_CLOSED, startProject, resolvedPath, curFile));
                            } else {
                                // error case
                                APTUtils.LOG.log(Level.INFO, "invalid start project {0} when including {1} from {2}", new Object[]{aStartProject, path, curFile});
                                directive.setAnnotation(new UnresolvedIncludeDirectiveAnnotation(UnresolvedIncludeDirectiveReason.INVALID_START_PROJECT, aStartProject, resolvedPath, curFile));
                            }
                        } else {
                            // error case
                            APTUtils.LOG.log(Level.INFO, "Start project {0} can not create by path {1} from {2}", new Object[]{aStartProject, path, curFile});
                            directive.setAnnotation(new UnresolvedIncludeDirectiveAnnotation(UnresolvedIncludeDirectiveReason.START_PROJECT_CANNOT_CREATE_FILE, aStartProject, resolvedPath, curFile));
                        }
                    } else {
                        // The only one good branch:
                        // annotated include directive to have access to FileImpl from onEnter which follows all resolved #includes
                        directive.setAnnotation(includedFile);
                    }
                } else {
                    // error case
                    APTUtils.LOG.log(Level.INFO, "invalid start project {0} or file when including {1} from {2}", new Object[]{aStartProject, path, curFile});
                    directive.setAnnotation(new UnresolvedIncludeDirectiveAnnotation(UnresolvedIncludeDirectiveReason.INVALID_START_PROJECT, aStartProject, resolvedPath, curFile));
                    // assert false : "invalid start project when including " + path + " from " + curFile;
                }
            } else {
                // error case
                APTUtils.LOG.log(Level.SEVERE, "FileTokenStreamCallback: file {0} without project!!!", new Object[]{path});// NOI18N
                directive.setAnnotation(new UnresolvedIncludeDirectiveAnnotation(UnresolvedIncludeDirectiveReason.NULL_START_PROJECT, resolvedPath, curFile));
            }
        }

        private static final boolean ALLOW_TO_SKIP_TOKENS_BETWEEN_DIRECTIVES = Boolean.valueOf(System.getProperty("clank.callback.allow.skip.token", "false")); //NativeTrace.DEBUG;
        @Override
        public boolean onEnter(ClankDriver.ClankFileInfo enteredFrom, ClankDriver.ClankFileInfo enteredTo) {
            assert enteredTo != null;
            ClankDriver.ClankInclusionDirective enteredAsInclusion = enteredTo.getInclusionDirective();
            assert (enteredFrom == null) == (enteredAsInclusion == null) : "inclusion directive is null if and only if entering main file " + enteredFrom + " vs. " + enteredAsInclusion;
//            assert (enteredFrom == null) == (enteredTo.getFileIndex() == 0) : "file index is zero if and only if entering main file";
            boolean onEnterIntoInterestedFile = (enteredTo.getFileIndex() == stopAtIndex);
            FileImpl enteredToFileImpl;
            // prepare "entered to file"
            if (enteredFrom == null) {
                // main file case: entered to start file
                enteredToFileImpl = startFile;
            } else {
                // entered through #include directive: ask file from annotation initialized in onInclusionDirective
                Object inclusionAnnotation = enteredAsInclusion.getAnnotation();
                if (inclusionAnnotation instanceof FileImpl) {
                    // successfully resolved #include followed by this onEnter call
                    enteredToFileImpl = (FileImpl)inclusionAnnotation;
                } else {
                    // it is suspicious to see unresolved include followed by onEnter hook
                    // it might be in case of cancelled/interrupted query
                    // error recovery is: report and full stop
                    APTUtils.LOG.log(Level.INFO, inclusionAnnotation.toString());
                    return false;
                }
            }

            boolean canSkipFileContent = false;
            // when parse TU stopAtIndex is 0, so the branch below is met first and switch mode to SEEN
            if (onEnterIntoInterestedFile) {
                // entered into interested file, it can be only once
                // because even without guards other enter int header has different unique file-index
                assert alreadySeenInterestedFileEnter == State.INITIAL;
                alreadySeenInterestedFileEnter = State.SEEN;
                // FIXME: when stopAtIndex is not zero, then expected file and entered file
                // might differ when restoring by include stack goes through changed files
                CndUtils.assertPathsEqualInConsole(enteredTo.getFilePath(), stopFileImpl.getAbsolutePath(),
                        "{0}\n vs. \n{1}", enteredTo, stopFileImpl);// NOI18N
                // we entered target file and after that we can
                // handle inclusive #includes
            } else {
                // first must be switched to SEEN state in the branch above;
                // we can skip till interested file, i.e. when restore TS for some file in deep inclusion stack;
                if ((alreadySeenInterestedFileEnter == State.SEEN) && parameters.triggerParsingActivity) {
                    // in parsing mode we need all entered files info
                    // PERF: do the best to reduce work in parsing phase to be done on include of resolved file
                    if (enteredToFileImpl.checkIfFileWasIncludedBeforeWithBetterOrEqualContent(ppHandler)) {
                        // i.e. no need to keep tokens, because postInclude would discard included file
                        CndUtils.assertTrueInConsole(!onEnterIntoInterestedFile, "how can we skip interested file?", enteredToFileImpl);
                        canSkipFileContent = ALLOW_TO_SKIP_TOKENS_BETWEEN_DIRECTIVES;
                    }
                }
            }
            pushCurrentFile(enteredToFileImpl, canSkipFileContent);
            insideInterestedFile = onEnterIntoInterestedFile;
            skipCurrentFileContentOptimization = canSkipFileContent;
            return true;
        }

        @Override
        public boolean onExit(ClankDriver.ClankFileInfo exitedFrom, ClankDriver.ClankFileInfo exitedTo) {
            assert exitedFrom != null;
            // on exit pop current file from stack and optimization mode as well
            FileImpl curFile = getCurFile(true);
            skipCurrentFileContentOptimization = getSkipCurrentFileContentOptimization(true);
            assert curFile != null;
            ClankDriver.ClankInclusionDirective exitedInclusion = exitedFrom.getInclusionDirective();
            assert (exitedInclusion == null) == (exitedTo == null) : "inclusion directive is null if and only if exiting main file " + exitedTo + " vs. " + exitedInclusion;
            // fast paths
            if (alreadySeenInterestedFileEnter == State.EXITED) {
                // stop all activity recursively
                return false;
            } else if (alreadySeenInterestedFileEnter == State.INITIAL) {
                // continue till onEnter meets interested file and switch to SEEN
                return true;
            }
            insideInterestedFile = (exitedTo != null) && (exitedTo.getFileIndex() == stopAtIndex);
            if (stopAtIndex == exitedFrom.getFileIndex()) {
                // on exit must always be correct, otherwise on enter hasn't tracked correctly erroneous enter
                CndUtils.assertPathsEqualInConsole(exitedFrom.getFilePath(), stopFileImpl.getAbsolutePath(),
                        "{0} expected {1}", stopFileImpl.getAbsolutePath(), exitedFrom);// NOI18N
                preparedPreprocessorOutput = ClankDriver.extractPreparedPreprocessorOutput(exitedFrom);
                assert parameters.needTokens == YesNoInterested.NEVER || preparedPreprocessorOutput.hasTokenStream();
                // stop all activity
                alreadySeenInterestedFileEnter = State.EXITED;
                return false;
            } else if (parameters.triggerParsingActivity) {
                assert alreadySeenInterestedFileEnter == State.SEEN;
                // check if onEnter we decided to skip this file content
                if (!skipCurrentFileContentOptimization) {
                    try {
                        assert ClankDriver.extractPreprocessorOutput(ppHandler).hasTokenStream();
                        PreprocHandler.State inclState = ppHandler.getState();
                        assert !inclState.isCleaned();
                        CharSequence inclPath = curFile.getAbsolutePath();
                        ProjectBase inclFileOwner = curFile.getProjectImpl(true);
                        ProjectBase aStartProject = getStartProject();
                        if (inclFileOwner.isDisposing() || aStartProject.isDisposing()) {
                            if (TraceFlags.TRACE_VALIDATION || TraceFlags.TRACE_MODEL_STATE) {
                                System.err.printf("onFileIncluded: %s file [%s] is interrupted on disposing project%n", inclPath, inclFileOwner.getName());
                            }
                            return false;
                        } else {
                            FilePreprocessorConditionState pcState = FilePreprocessorConditionState.build(inclPath, exitedFrom.getSkippedRanges());
                            PreprocessorStatePair ppStatePair = new PreprocessorStatePair(inclState, pcState);
                            inclFileOwner.postIncludeFile(aStartProject, curFile, inclPath, ppStatePair, null);
                        }
                    } catch (Exception ex) {
                        APTUtils.LOG.log(Level.SEVERE, "MyClankPreprocessorCallback: error on including {0}:%n{1}", new Object[]{exitedFrom.getFilePath(), ex});
                        DiagnosticExceptoins.register(ex);
                        return false;
                    }
                }
            }
            return true;
        }

        private ClankDriver.ClankPreprocessorOutput getPPOut() {
            return preparedPreprocessorOutput;
        }
    }
    
    private static class VisitIncludeChainPreprocessorCallback implements ClankPreprocessorCallback {
        // parameters to be collected by preprocessor inside interested file
        private final ClankTokenStreamProducerParameters parameters;
        
        // include chain we need to go till interested file
        private final LinkedList<PPIncludeHandler.IncludeInfo> remainingChainToInterestedFile;
        private PPIncludeHandler.IncludeInfo seekEnterToThisIncludeInfo;

        private ClankDriver.ClankFileInfo waitExitFromThisFileInfo = null;
        private ClankDriver.ClankFileInfo seenInterestedFileInfo = null;
        
        private ClankDriver.ClankPreprocessorOutput preparedPreprocessorOutput = null;
        private boolean insideInterestedFile = false;

        protected enum State {
          WAIT_COMPILATION_UNIT_FILE, // before Compilation Unit
          WAIT_EXIT_FROM_FILE, // uses waitExitFromThisFileInfo
          SEEK_ENTER_TO_INCLUDED_FILE, // uses seekEnterToThisIncludeInfo
          INSIDE_INITERESTED_FILE, // seenInterestedFileInfo
          CORRUPTED_INCLUDE_CHAIN, // smth goes wrong
          DONE // chain is OK and TokenStream is collected
        }
        
        private State state;
        private VisitIncludeChainPreprocessorCallback(LinkedList<PPIncludeHandler.IncludeInfo> includeChain,
                ClankTokenStreamProducerParameters params) {
            this.remainingChainToInterestedFile = includeChain;
            this.state = State.WAIT_COMPILATION_UNIT_FILE;
            this.parameters = params;
        }

        private boolean valueOf(/*YesNoInterested*/int param) {
            switch (param) {
                case YesNoInterested.ALWAYS:
                    return true;
                case YesNoInterested.NEVER:
                    return false;
                case YesNoInterested.INTERESTED:
                    return insideInterestedFile;
                default:
                    throw new AssertionError("unknown" + param);
            }
        }
        
        public boolean isCorruptedIncludeChain() {
            return state == State.CORRUPTED_INCLUDE_CHAIN;
        }
        
        @Override
        public boolean needPPDirectives() {
            return valueOf(parameters.needPPDirectives);
        }

        @Override
        public boolean needTokens() {
            return valueOf(parameters.needTokens);
        }

        @Override
        public boolean needSkippedRanges() {
          return valueOf(parameters.needSkippedRanges);
        }

        @Override
        public boolean needMacroExpansion() {
            return valueOf(parameters.needMacroExpansion);
        }

        @Override
        public boolean needComments() {
            return valueOf(parameters.needComments);
        }

        protected final boolean isInsideInterestedFile() {
            return this.insideInterestedFile;
        }
        
        protected boolean pushEnteredFile(ClankDriver.ClankFileInfo enteredFrom, ClankDriver.ClankFileInfo enteredTo) {
            return true;
        }
        
        protected void popExitedFile(ClankDriver.ClankFileInfo exitedFrom, ClankDriver.ClankFileInfo exitedTo, State state, boolean exitingFromInterestedFile) {
        }

        protected void include(ClankDriver.ClankInclusionDirective directive) {
            
        }
        
        @Override
        public final void onInclusionDirective(ClankDriver.ClankFileInfo directiveOwner, ClankDriver.ClankInclusionDirective directive) {
            switch (state) {
                case WAIT_EXIT_FROM_FILE:
                case SEEK_ENTER_TO_INCLUDED_FILE:
                case INSIDE_INITERESTED_FILE:
                    include(directive);
                    break;
                case WAIT_COMPILATION_UNIT_FILE:
                    assert false;
                    break;
                case CORRUPTED_INCLUDE_CHAIN:
                case DONE:
                    // no need to handle extra include when work is done or chain is broken
                    return;
                default:
                    throw new AssertionError(state.name());
                
            }
        }
        
        @Override
        public final boolean onEnter(ClankDriver.ClankFileInfo enteredFrom, ClankDriver.ClankFileInfo enteredTo) {            
            assert enteredTo != null;
            insideInterestedFile = false;
            assert state != null : "null state when enter from\n" + enteredFrom + "\nTo\n" + enteredTo;
            switch (state) {
                case CORRUPTED_INCLUDE_CHAIN:
                    // keep state and dont' allow to enter
                    return false;
                case DONE:
                    // all activity was done; keep state and no need to enter
                    return false;
                case WAIT_COMPILATION_UNIT_FILE:
                    // enter compilation unit
                    assert enteredFrom == null : "expected null instead of " + enteredFrom;
                    assert this.waitExitFromThisFileInfo == null : "expected null instead of " + this.waitExitFromThisFileInfo;
                    if (remainingChainToInterestedFile.isEmpty()) {
                        // main file itself is what we are looking for
                        state = State.INSIDE_INITERESTED_FILE;
                        seenInterestedFileInfo = enteredTo;
                        insideInterestedFile = true;
                    } else {
                        // inside compilation unit file we are going to seek entrance into the head of include chain
                        // remove it from chain
                        seekEnterToThisIncludeInfo = this.remainingChainToInterestedFile.removeFirst();
                        assert seekEnterToThisIncludeInfo != null;
                        // need to find entrance into include chain
                        state = State.SEEK_ENTER_TO_INCLUDED_FILE;
                    }
                    // allow to enter
                    break;
                case INSIDE_INITERESTED_FILE:
                    assert this.remainingChainToInterestedFile.isEmpty() : "we are inside interested file only when walked whole chain: " + remainingChainToInterestedFile;
                    // inside interested file we met #include directive
                    // visit full include branch and come back to our file
                    assert this.waitExitFromThisFileInfo == null : "expected null instead of " + this.waitExitFromThisFileInfo;
                    // set up exit-from marker object
                    this.waitExitFromThisFileInfo = enteredTo;
                    state = State.WAIT_EXIT_FROM_FILE;
                    // enter included file, state will be changed in onExit
                    break;
                case WAIT_EXIT_FROM_FILE:
                    // we are inside #include branch which is before or inside interested #include
                    // this state is changed only in onExit
                    assert this.waitExitFromThisFileInfo != null;
                    assert this.waitExitFromThisFileInfo != enteredTo : "unexpected to enter into file " + enteredTo + " which we wait to exit from";
                    // continue traversing this include path
                    // state will be changed in onExit
                    break;
                case SEEK_ENTER_TO_INCLUDED_FILE:
                    assert this.seekEnterToThisIncludeInfo != null;
                    assert this.waitExitFromThisFileInfo == null : "expected null instead of " + this.waitExitFromThisFileInfo;
                    assert enteredFrom != null;
                    ClankDriver.ClankInclusionDirective inclDirective = enteredTo.getInclusionDirective();
                    assert inclDirective != null : "main file is the only one without include directive, but had to be handled above " + enteredTo;
                    // see if met onEnter into interested #include directive from include chain
                    int includeDirectiveIndex = inclDirective.getIncludeDirectiveIndex();
                    if (includeDirectiveIndex == seekEnterToThisIncludeInfo.getIncludeDirectiveIndex()) {
                        // consistency check that included file is as expected
                        if (CharSequenceUtils.contentEquals(seekEnterToThisIncludeInfo.getIncludedPath(), enteredTo.getFilePath())) {
                            if (this.remainingChainToInterestedFile.isEmpty()) {
                                assert seenInterestedFileInfo == null : "can not enter twice " + seenInterestedFileInfo;
                                // update state
                                state = State.INSIDE_INITERESTED_FILE;
                                seenInterestedFileInfo = enteredTo;
                                insideInterestedFile = true;
                            } else {
                                // remove new entrance from chain
                                seekEnterToThisIncludeInfo = this.remainingChainToInterestedFile.removeFirst();
                                assert seekEnterToThisIncludeInfo != null;
                                // need to find entrance into next level of include chain
                                // keep seeking state
                                state = State.SEEK_ENTER_TO_INCLUDED_FILE;
                            }
                        } else {
                            // this is corrupted include stack, we don't want to go this way anymore
                            state = State.CORRUPTED_INCLUDE_CHAIN;
                            assert preparedPreprocessorOutput == null;
                            return false;
                        }
                    } else {
                        assert includeDirectiveIndex < seekEnterToThisIncludeInfo.getIncludeDirectiveIndex() : "why hasn't stopped after interested file? " + seekEnterToThisIncludeInfo;
                        // before interested file we met #include directive
                        // have to visit full include branch and come back to our file
                        assert this.waitExitFromThisFileInfo == null : "expected null instead of " + this.waitExitFromThisFileInfo;
                        // set up exit-from marker object
                        waitExitFromThisFileInfo = enteredTo;
                        state = State.WAIT_EXIT_FROM_FILE;
                    }   
                    // let's enter
                    break;
                default:
                    assert false : "unexpected state = " + state;
                    state = State.CORRUPTED_INCLUDE_CHAIN;
                    return false;
            }
            // let's enter
            return pushEnteredFile(enteredFrom, enteredTo);
        }

        @Override
        public final boolean onExit(ClankDriver.ClankFileInfo exitedFrom, ClankDriver.ClankFileInfo exitedTo) {
            boolean exitingFromInterestedFile = insideInterestedFile;
            insideInterestedFile = false;
            boolean continuePreprocessing;
            if (preparedPreprocessorOutput != null) {
                // already gathered token stream
                assert state == State.DONE;
                // can stop all
                continuePreprocessing = false;
            } else if (state == State.CORRUPTED_INCLUDE_CHAIN) {
                assert preparedPreprocessorOutput == null;
                // continue exit
                continuePreprocessing = false;
            } else {
                assert exitedFrom != null;
                assert seenInterestedFileInfo != null || waitExitFromThisFileInfo != null : "we exit from enexpected include branch ? " + exitedFrom + "\nback to\n" + exitedTo;            
                if (exitedFrom == seenInterestedFileInfo) {
                    assert waitExitFromThisFileInfo == null;
                    // stop all activity on exit from interested file
                    preparedPreprocessorOutput = ClankDriver.extractPreparedPreprocessorOutput(exitedFrom);
                    assert preparedPreprocessorOutput != null;
                    state = State.DONE;
                    // stop after exit
                    assert exitingFromInterestedFile;
                    continuePreprocessing = false;
                } else if (exitedFrom == waitExitFromThisFileInfo) {
                    assert (state == State.WAIT_EXIT_FROM_FILE);
                    // clear exit-from marker object 
                    waitExitFromThisFileInfo = null;
                    if (seenInterestedFileInfo == null) {
                        // we met #include before expected include chain entry point
                        // switch back to seek of entry point
                        state = State.SEEK_ENTER_TO_INCLUDED_FILE;
                    } else {
                        assert seenInterestedFileInfo != null;
                        assert exitedTo == seenInterestedFileInfo : "unexpected to exit back to file " + exitedTo + "\nwhen we wait to exit into " + seenInterestedFileInfo;
                        assert this.remainingChainToInterestedFile.isEmpty() : "we are inside interested file only when walked whole chain: " + remainingChainToInterestedFile;
                        state = State.INSIDE_INITERESTED_FILE;
                        // gather information again when come back to interested file
                        insideInterestedFile = true;
                    }
                    continuePreprocessing = true;
                } else {
                    // in all other cases exit but continue 
                    // till we meet FileInfoForExitFrom or seenInterestedFile
                    // state can be update in further onEnter hook as well
                    continuePreprocessing = true;
                }
            }
            popExitedFile(exitedFrom, exitedTo, state, exitingFromInterestedFile);
            return continuePreprocessing;
        }

        public ClankDriver.ClankPreprocessorOutput getPreparedPreprocessorOutput() {
            return preparedPreprocessorOutput;
        }
    }
    
    private static final class InterestedFileImplPreprocessorCallback extends VisitIncludeChainPreprocessorCallback {
        private final FileImpl startFile;
        private final FileImpl interestedFile;
        private final ProjectBase startProject;
        private final PreprocHandler ppHandler;

        // chain of current include stack as FileImpls
        private final List<FileImpl> curFiles = new ArrayList<>();
                
        public InterestedFileImplPreprocessorCallback(FileImpl startFile, FileImpl interestedFile, 
                PreprocHandler ppHandler, 
                ClankTokenStreamProducerParameters params) {
            super(APTHandlersSupport.extractIncludeStack(ppHandler.getState()), params);
            this.startFile = startFile;
            this.interestedFile = interestedFile;
            this.startProject = startFile.getProjectImpl(true);
            this.ppHandler = ppHandler;
        }

        /**
         * in the stack on tracked files return top one and pop if needed.
         * @param pop true to pop, false to peek only
         * @return non null top file
         */
        private FileImpl getCurFile(boolean pop) {
          assert curFiles.size() > 0;
          FileImpl curFile;
          if (pop) {
            curFile = curFiles.remove(curFiles.size() - 1);
          } else {
            curFile = curFiles.get(curFiles.size() - 1);
          }
          assert curFile != null;
          return curFile;
        }
        
        private void pushCurrentFile(FileImpl enteredToFileImpl) {
            curFiles.add(enteredToFileImpl);
        }
        
        @Override
        public void include(ClankDriver.ClankInclusionDirective directive) {
            // always resolve path to have behavior like in APT, where file resolution
            // includes query to library manager which creates libraries on demand
            ResolvedPath resolvedPath = directive.getResolvedPath();
            if (resolvedPath == null) {
                // broken #include path
                directive.setAnnotation(UnresolvedIncludeDirectiveReason.NULL_PATH);
                return;
            }
            // peek file from onEnter
            FileImpl curFile = getCurFile(false);
            CharSequence path = resolvedPath.getPath();
            ProjectBase aStartProject = startProject;
            if (aStartProject != null) {
                // resolve if not interrupted
                if (aStartProject.isValid() && curFile.isValid()) {
                    ProjectBase inclFileOwner = aStartProject.getLibraryManager().resolveFileProjectOnInclude(aStartProject, curFile, resolvedPath);
                    if (inclFileOwner == null) {
                        // resolveFileProjectOnInclude() javadoc reads: "Can return NULL !"; and it asserts itself
                        if (aStartProject.getFileSystem() == resolvedPath.getFileSystem()) {
                            // if file systems do match, then use start project as fallback
                            inclFileOwner = aStartProject;
                        }
                    }
                    if (inclFileOwner == null) {
                        // error case
                        directive.setAnnotation(new UnresolvedIncludeDirectiveAnnotation(UnresolvedIncludeDirectiveReason.UNRESOLVED_FILE_OWNER, resolvedPath));
                        return;
                    }
                    if (CndUtils.isDebugMode()) {
                        CndUtils.assertTrue(inclFileOwner.getFileSystem() == resolvedPath.getFileSystem(), "Different FS for " + path + ": " + inclFileOwner.getFileSystem() + " vs " + resolvedPath.getFileSystem()); // NOI18N
                    }
                    // when owner of included file is detected we can ask it for FileImpl instance
                    FileImpl includedFile = inclFileOwner.prepareIncludedFile(aStartProject, path, ppHandler);
                    if (includedFile == null) {
                        if (CsmModelAccessor.isModelAlive() && inclFileOwner.isValid()) {
                            if (aStartProject.isValid()) {
                                // error case
                                APTUtils.LOG.log(Level.INFO, "something wrong when including {0} from {1}", new Object[]{path, curFile});
                                directive.setAnnotation(new UnresolvedIncludeDirectiveAnnotation(UnresolvedIncludeDirectiveReason.START_PROJECT_CLOSED, startProject, resolvedPath, curFile));
                            } else {
                                // error case
                                APTUtils.LOG.log(Level.INFO, "invalid start project {0} when including {1} from {2}", new Object[]{aStartProject, path, curFile});
                                directive.setAnnotation(new UnresolvedIncludeDirectiveAnnotation(UnresolvedIncludeDirectiveReason.INVALID_START_PROJECT, aStartProject, resolvedPath, curFile));
                            }
                        } else {
                            // error case
                            APTUtils.LOG.log(Level.INFO, "Start project {0} can not create by path {1} from {2}", new Object[]{aStartProject, path, curFile});
                            directive.setAnnotation(new UnresolvedIncludeDirectiveAnnotation(UnresolvedIncludeDirectiveReason.START_PROJECT_CANNOT_CREATE_FILE, aStartProject, resolvedPath, curFile));
                        }
                    } else {
                        // The only one good branch:
                        // annotated include directive to have access to FileImpl from onEnter which follows all resolved #includes
                        directive.setAnnotation(includedFile);
                    }
                } else {
                    // error case
                    APTUtils.LOG.log(Level.INFO, "invalid start project {0} or file when including {1} from {2}", new Object[]{aStartProject, path, curFile});
                    directive.setAnnotation(new UnresolvedIncludeDirectiveAnnotation(UnresolvedIncludeDirectiveReason.INVALID_START_PROJECT, aStartProject, resolvedPath, curFile));
                    // assert false : "invalid start project when including " + path + " from " + curFile;
                }
            } else {
                // error case
                APTUtils.LOG.log(Level.SEVERE, "FileTokenStreamCallback: file {0} without project!!!", new Object[]{path});// NOI18N
                directive.setAnnotation(new UnresolvedIncludeDirectiveAnnotation(UnresolvedIncludeDirectiveReason.NULL_START_PROJECT, resolvedPath, curFile));
            }            
        }

        @Override
        protected boolean pushEnteredFile(ClankDriver.ClankFileInfo enteredFrom, ClankDriver.ClankFileInfo enteredTo) {
            assert enteredTo != null;
            ClankDriver.ClankInclusionDirective enteredAsInclusion = enteredTo.getInclusionDirective();
            assert (enteredFrom == null) == (enteredAsInclusion == null) : "inclusion directive is null if and only if entering main file " + enteredFrom + " vs. " + enteredAsInclusion;
            FileImpl enteredToFileImpl;
            // prepare "entered to file"
            if (enteredFrom == null) {
                // main file case: entered to start file
                enteredToFileImpl = startFile;
            } else {
                // entered through #include directive: ask file from annotation initialized in onInclusionDirective/include
                Object inclusionAnnotation = enteredAsInclusion.getAnnotation();
                if (inclusionAnnotation instanceof FileImpl) {
                    // successfully resolved #include followed by this onEnter call
                    enteredToFileImpl = (FileImpl)inclusionAnnotation;
                } else {
                    // it is suspicious to see unresolved include followed by onEnter hook
                    // it might be in case of cancelled/interrupted query
                    // error recovery is: report and full stop
                    APTUtils.LOG.log(Level.INFO, inclusionAnnotation.toString());
                    return false;
                }
            }
            if (CndUtils.isDebugMode()) {
                CndUtils.assertTrueInConsole(!isInsideInterestedFile() || this.interestedFile.equals(enteredToFileImpl), "" + isInsideInterestedFile() + ": inconsistency between " + this.interestedFile + " and ", enteredToFileImpl);
            }
            pushCurrentFile(enteredToFileImpl);
            return true;
        }

        @Override
        protected void popExitedFile(ClankDriver.ClankFileInfo exitedFrom, ClankDriver.ClankFileInfo exitedTo, State state, boolean exitingFromInterestedFile) {
            assert exitedFrom != null;
            // on exit pop current file from stack
            FileImpl curFile = getCurFile(true);
            assert curFile != null;
            if (CndUtils.isDebugMode()) {
                ClankDriver.ClankInclusionDirective exitedInclusion = exitedFrom.getInclusionDirective();
                assert (exitedInclusion == null) == (exitedTo == null) : "inclusion directive is null if and only if exiting main file " + exitedTo + " vs. " + exitedInclusion;
                if (exitingFromInterestedFile) {
                    // on exit must always be correct, otherwise on enter hasn't tracked correctly erroneous enter
                    CndUtils.assertPathsEqualInConsole(exitedFrom.getFilePath(), interestedFile.getAbsolutePath(),
                            "{0} expected {1}", interestedFile.getAbsolutePath(), exitedFrom);// NOI18N
                    // stop all activity
                    assert state == State.DONE : "expected DONE instead of " + state + " for " + this.interestedFile;
                }
            }
        }
    }    

    private static final class PatchedFileBuffer implements FileBuffer {
        private final FileBuffer delegate;
        private final CodePatch codePatch;
        private char[] res;
        private Line2Offset lines;

        private PatchedFileBuffer(FileBuffer delegate, CodePatch patchCode) {
            this.delegate = delegate;
            this.codePatch = patchCode;
        }

        @Override
        public void addChangeListener(ChangeListener listener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeChangeListener(ChangeListener listener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isFileBased() {
            return delegate.isFileBased();
        }

        @Override
        public FileObject getFileObject() {
            return delegate.getFileObject();
        }

        @Override
        public CharSequence getUrl() {
            return delegate.getUrl();
        }

        @Override
        public String getText(int start, int end) throws IOException {
            return new String(getCharBuffer(), start, end - start);
        }

        @Override
        public CharSequence getText() throws IOException {
            return new FileBufferFile.MyCharSequence(getCharBuffer());
        }

        @Override
        public long lastModified() {
            return delegate.lastModified()+1;
        }

        @Override
        public long getCRC() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int[] getLineColumnByOffset(int offset) throws IOException {
            if (lines == null) {
                lines = new Line2Offset(getCharBuffer());
            }

            return lines.getLineColumnByOffset(offset);
        }

        @Override
        public int getLineCount() throws IOException {
            if (lines == null) {
                lines = new Line2Offset(getCharBuffer());
            }
            return lines.getLineCount();
        }

        @Override
        public int getOffsetByLineColumn(int line, int column) throws IOException {
            if (lines == null) {
                lines = new Line2Offset(getCharBuffer());
            }
            return lines.getOffsetByLineColumn(line, column);
        }

        @Override
        public CharSequence getAbsolutePath() {
            return delegate.getAbsolutePath();
        }

        @Override
        public FileSystem getFileSystem() {
            return delegate.getFileSystem();
        }

        @Override
        public char[] getCharBuffer() throws IOException {
            if (res == null) {
                char[] charBuffer = delegate.getCharBuffer();
                char[] patch = codePatch.getPatch().toCharArray();
                res = new char[charBuffer.length-(codePatch.getEndOffset()-codePatch.getStartOffset())+patch.length];
                System.arraycopy(charBuffer, 0, res, 0, codePatch.getStartOffset());
                System.arraycopy(patch, 0, res, codePatch.getStartOffset(), patch.length);
                System.arraycopy(charBuffer, codePatch.getEndOffset(), res, codePatch.getStartOffset()+patch.length, charBuffer.length - codePatch.getEndOffset());
            }
            return res;
        }

        @Override
        public BufferType getType() {
            return delegate.getType();
        }

    }
}
