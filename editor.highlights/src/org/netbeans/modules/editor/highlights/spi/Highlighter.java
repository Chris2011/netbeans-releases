/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.editor.highlights.spi;

import java.util.Collection;
import java.util.ArrayList;
import org.netbeans.modules.editor.highlights.HighlighterImpl;
import org.openide.filesystems.FileObject;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Jan Lahoda
 */
public final class Highlighter {

    private static final Highlighter INSTANCE = new Highlighter();

    public static Highlighter getDefault() {
        return INSTANCE;
    }

    private RequestProcessor WORKER = new RequestProcessor("Highlighter worker");

    /** Creates a new instance of Highlighter */
    private Highlighter() {
    }

    public void setHighlights(final FileObject fo, final String type, Collection/*<Highlight>*/ highlights) {
        final Collection highlightsCopy = new ArrayList(highlights);
        WORKER.post(new Runnable() {
            public void run() {
                HighlighterImpl.getDefault().setHighlights(fo, type, highlightsCopy);
            }
        });
    }
    
}
