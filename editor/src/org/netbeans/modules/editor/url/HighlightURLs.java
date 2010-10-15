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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2009 Sun
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
package org.netbeans.modules.editor.url;

import java.util.ArrayList;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;
import javax.swing.text.Position.Bias;

import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.editor.settings.FontColorSettings;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.lib.editor.util.swing.DocumentUtilities;
import org.netbeans.spi.editor.highlighting.HighlightsLayer;
import org.netbeans.spi.editor.highlighting.HighlightsLayerFactory;
import org.netbeans.spi.editor.highlighting.ZOrder;
import org.netbeans.spi.editor.highlighting.support.OffsetsBag;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;


/**
 * Highlights URLs in the source editor
 * (This was based on the stripwhitespace module by Andrei Badea and the TODO highlighter)
 * Rewritten to search for the URLs on background due to #190188.
 *
 * @author Andrei Badea
 * @author Tor Norbye
 * @author Jan Lahoda
 */
public final class HighlightURLs implements DocumentListener, Runnable {

    private static final Logger LOG = Logger.getLogger(HighlightURLs.class.getName());
    private static final Object REGISTERED_KEY = new Object();

    static void ensureAttached(final BaseDocument doc) {
        if (doc.getProperty(REGISTERED_KEY) != null) {
            return;
        }

        final HighlightURLs h = new HighlightURLs(doc);

        doc.addDocumentListener(h);

        doc.render(new Runnable() {

            @Override
            public void run() {
                try {
                    h.modifiedSpans.add(new Position[] {
                        doc.createPosition(0, Bias.Backward),
                        doc.createPosition(doc.getLength(), Bias.Forward)
                    });
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });
        h.schedule();

        doc.putProperty(REGISTERED_KEY, true);
    }
    
    private HighlightURLs(BaseDocument doc) {
        this.doc = doc;
        String mimeType = DocumentUtilities.getMimeType(doc);
        FontColorSettings fcs = mimeType == null ? null : MimeLookup.getLookup(mimeType).lookup(FontColorSettings.class);
        this.coloring = fcs == null ? null : fcs.getTokenFontColors("url"); // NOI18N
    }

    private final BaseDocument doc;
    private final AttributeSet coloring;
    private final List<Position[]> modifiedSpans = new ArrayList<Position[]>();

    @Override
    public synchronized void insertUpdate(DocumentEvent e) {
        try {
            modifiedSpans.add(new Position[] {
                doc.createPosition(e.getOffset(), Bias.Backward),
                doc.createPosition(e.getOffset() + e.getLength(), Bias.Forward),
            });
        } catch (BadLocationException ex) {
            LOG.log(Level.FINE, null, ex);
        }

        schedule();
    }

    @Override
    public synchronized void removeUpdate(DocumentEvent e) {
        try {
            modifiedSpans.add(new Position[] {
                doc.createPosition(e.getOffset(), Bias.Backward),
                doc.createPosition(e.getOffset(), Bias.Forward),
            });
        } catch (BadLocationException ex) {
            LOG.log(Level.FINE, null, ex);
        }

        schedule();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }

    private static final RequestProcessor WORKER_THREAD = new RequestProcessor(HighlightURLs.class.getName(), 1, false, false);
    private final Task WORKER = WORKER_THREAD.create(this);

    private void schedule() {
        WORKER.schedule(100);
    }

    @Override
    public void run() {
        final Position[] span = new Position[2];
        final CharSequence[] text = new CharSequence[1];
        final long[] version = new long[1];
        final OffsetsBag workingBag = new OffsetsBag(doc);
        final int[] length = new int[1];

        doc.render(new Runnable() {
            @Override
            public void run() {
                synchronized (HighlightURLs.this) {
                    try {
                        int startOffset = Integer.MAX_VALUE;
                        int endOffset = -1;
                        for (Position[] sp : modifiedSpans) {
                            startOffset = Math.min(startOffset, sp[0].getOffset());
                            endOffset = Math.max(endOffset, sp[1].getOffset());
                        }

                        modifiedSpans.clear();
                        
                        if (endOffset == (-1)) return;
                        
                        startOffset = Utilities.getRowStart(doc, startOffset);
                        endOffset = Math.min(doc.getLength(), endOffset);
                        endOffset = Utilities.getRowEnd(doc, endOffset);

                        text[0] = DocumentUtilities.getText(doc, startOffset, endOffset - startOffset);
                        version[0] = DocumentUtilities.getDocumentVersion(doc);

                        workingBag.setHighlights(getBag(doc));
                        workingBag.removeHighlights(startOffset, endOffset + 1, false);

                        length[0] = text[0].length();
                        span[0] = doc.createPosition(startOffset, Bias.Backward);
                        span[1] = doc.createPosition(endOffset, Bias.Forward);
                    } catch (BadLocationException ex) {
                        LOG.log(Level.WARNING, null, ex);
                    }
                }
            }

        });

        if (span[0] == null) return; //nothing to do

        class Stop extends Error {}

        CharSequence seq = new CharSequence() {
            @Override
            public int length() {
                return length[0];
            }
            @Override
            public char charAt(final int index) {
                final char[] result = new char[1];
                doc.render(new Runnable() {
                    @Override
                    public void run() {
                        if (DocumentUtilities.getDocumentVersion(doc) != version[0]) {
                            throw new Stop();
                        }

                        result[0] = text[0].charAt(index);
                    }
                });
                return result[0];
            }
            @Override
            public CharSequence subSequence(int start, int end) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };

        try {
            final Iterable<int[]> toHighlight = Parser.recognizeURLs(seq);

            doc.render(new Runnable() {
                @Override
                public void run() {
                    if (DocumentUtilities.getDocumentVersion(doc) != version[0]) {
                        throw new Stop();
                    }

                    for (int[] s : toHighlight) {
                        workingBag.addHighlight(span[0].getOffset() + s[0], span[0].getOffset() + s[1], coloring);
                    }

                    getBag(doc).setHighlights(workingBag);
                }
            });
        } catch (Stop u) {
            synchronized (HighlightURLs.this) {
                modifiedSpans.add(span);
            }
            schedule();
        }
    }
    
    private static OffsetsBag getBag(Document doc) {
        OffsetsBag bag = (OffsetsBag) doc.getProperty(HighlightURLs.class);

        if (bag == null) {
            doc.putProperty(HighlightURLs.class, bag = new OffsetsBag(doc));
        }

        return bag;
    }

    @MimeRegistration(mimeType="", service=HighlightsLayerFactory.class)
    public static final class FactoryImpl implements HighlightsLayerFactory {

        @Override
        public HighlightsLayer[] createLayers(Context context) {
            Document doc = context.getDocument();
            
            if (!(doc instanceof BaseDocument)) {
                return null;
            } else {
                ensureAttached((BaseDocument) doc);
                
                return new HighlightsLayer[] {
                    HighlightsLayer.create(HighlightURLs.class.getName(), ZOrder.SYNTAX_RACK.forPosition(4950), false, getBag(doc)),
                };
            }
        }
        
    } // End of FactoryImpl class
}
