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
package org.openide.util.io;

import java.io.*;


/**
* This class convert Reader to InputStream. It works by converting
* the characters to the encoding specified in constructor parameter.
*
* @author   Petr Hamernik, David Strupl
*/
public class ReaderInputStream extends InputStream {
    /** Input Reader class. */
    private Reader reader;
    private PipedOutputStream pos;
    private PipedInputStream pis;
    private OutputStreamWriter osw;

    /** Creates new input stream from the given reader.
     * Uses the platform default encoding.
    * @param reader Input reader
    */
    public ReaderInputStream(Reader reader) throws IOException {
        this.reader = reader;
        pos = new PipedOutputStream();
        pis = new PipedInputStream(pos);
        osw = new OutputStreamWriter(pos);
    }

    /** Creates new input stream from the given reader and encoding.
     * @param reader Input reader
     * @param encoding
     */
    public ReaderInputStream(Reader reader, String encoding)
    throws IOException {
        this.reader = reader;
        pos = new PipedOutputStream();
        pis = new PipedInputStream(pos);
        osw = new OutputStreamWriter(pos, encoding);
    }

    public int read() throws IOException {
        if (pis.available() > 0) {
            return pis.read();
        }

        int c = reader.read();

        if (c == -1) {
            return c;
        }

        osw.write(c);
        osw.flush();
        pos.flush();

        return pis.read();
    }

    public int read(byte[] b, int off, int len) throws IOException {
        int c = read();

        if (c == -1) {
            return -1;
        }

        b[off] = (byte) c;

        int i = 1;

        // Don't try to fill up the buffer if the reader is waiting.
        for (; (i < len) && reader.ready(); i++) {
            c = read();

            if (c == -1) {
                return i;
            }

            b[off + i] = (byte) c;
        }

        return i;
    }

    public int available() throws IOException {
        int i = pis.available();

        if (i > 0) {
            return i;
        }

        if (reader.ready()) {
            // Char must produce at least one byte.
            return 1;
        } else {
            return 0;
        }
    }

    public void close() throws IOException {
        reader.close();
        osw.close();
        pis.close();
    }
}
