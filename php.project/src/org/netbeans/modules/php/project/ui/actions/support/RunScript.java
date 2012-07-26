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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.project.ui.actions.support;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import org.netbeans.api.queries.FileEncodingQuery;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExecutionDescriptor.InputProcessorFactory;
import org.netbeans.api.extexecution.ExternalProcessBuilder;
import org.netbeans.api.extexecution.input.InputProcessor;
import org.netbeans.api.extexecution.input.InputProcessors;
import org.netbeans.modules.php.project.deprecated.PhpProgram;
import org.netbeans.modules.php.project.ui.options.PhpOptions;
import org.netbeans.modules.php.project.util.PhpProjectUtils;
import org.openide.awt.HtmlBrowser;
import org.openide.filesystems.FileUtil;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;

/**
 * @author Radek Matous, Tomas Mysik
 */
public class RunScript {
    protected static final Logger LOGGER = Logger.getLogger(RunScript.class.getName());
    protected static final RequestProcessor RP = new RequestProcessor(RunScript.class);

    private final Provider provider;

    public RunScript(Provider provider) {
        assert provider != null;

        this.provider = provider;
    }

    public void run() {
        RP.post(new Runnable() {
            @Override
            public void run() {
                try {
                    getCallable().call();
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });
    }

    protected final Callable<Cancellable> getCallable()  {
        return new Callable<Cancellable>() {
            @Override
            public Cancellable call() throws Exception {
                if (!provider.isValid()) {
                    LOGGER.info("RunScript provider is not valid");
                    return new Cancellable() {
                        @Override
                        public boolean cancel() {
                            return true;
                        }
                    };
                }

                final Future<Integer> result = PhpProgram.executeLater(getProcessBuilder(), getDescriptor(), getOutputTabTitle());
                // #155251, #155741
//                try {
//                    result.get();
//                } catch (ExecutionException exc) {
//                    CommandUtils.processExecutionException(exc);
//                }
                return new Cancellable() {
                    @Override
                    public boolean cancel() {
                        return result.cancel(true);
                    }
                };
            }
        };
    }

    protected boolean isControllable() {
        return true;
    }

    protected ExecutionDescriptor getDescriptor() throws IOException {
        return provider.getDescriptor().controllable(isControllable());
    }

    protected ExternalProcessBuilder getProcessBuilder() {
        return provider.getProcessBuilder();
    }

    protected String getOutputTabTitle() {
        return provider.getOutputTabTitle();
    }

    public static final class InOutPostRedirector implements InputProcessorFactory, Runnable {
        private final File tmpFile;
        private final Charset encoding;
        private BufferedWriter fileWriter;

        public InOutPostRedirector(File scriptFile) throws IOException {
            assert scriptFile != null;

            tmpFile = FileUtil.normalizeFile(tempFileForScript(scriptFile));
            encoding = FileEncodingQuery.getEncoding(FileUtil.toFileObject(scriptFile));
        }

        @Override
        public InputProcessor newInputProcessor(final InputProcessor defaultProcessor) {
            return InputProcessors.proxy(defaultProcessor, new InputProcessor() {

                @Override
                public void processInput(char[] chars) throws IOException {
                    getFileWriter().write(chars);
                }

                @Override
                public void reset() throws IOException {
                    defaultProcessor.reset();
                }

                @Override
                public void close() throws IOException {
                    getFileWriter().flush();
                    getFileWriter().close();

                    defaultProcessor.close();
                }
            });
        }

        @Override
        public void run() {
            try {
                PhpOptions options = PhpOptions.getInstance();
                if (options.isOpenResultInBrowser()) {
                    HtmlBrowser.URLDisplayer.getDefault().showURL(tmpFile.toURI().toURL());
                }
                if (options.isOpenResultInEditor()) {
                    PhpProjectUtils.openFile(tmpFile);
                }
            } catch (MalformedURLException ex) {
                Exceptions.printStackTrace(ex);
            } catch (CancellationException ex) {
                // ignored
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                setFileWriter(null);
            }
        }

        public synchronized BufferedWriter getFileWriter() throws FileNotFoundException {
            if (fileWriter == null) {
                fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmpFile), encoding));
            }
            return fileWriter;
        }

        public synchronized void setFileWriter(BufferedWriter fileWriter) {
            this.fileWriter = fileWriter;
        }

        private static File tempFileForScript(File scriptFile) throws IOException {
            File retval = File.createTempFile(scriptFile.getName(), ".html"); // NOI18N
            retval.deleteOnExit();
            return retval;
        }
    }

    public interface Provider {
        ExecutionDescriptor getDescriptor() throws IOException;
        ExternalProcessBuilder getProcessBuilder();
        String getOutputTabTitle();
        boolean isValid();
    }
}
