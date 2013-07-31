/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.html.knockout;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Iterator;
import javax.swing.ImageIcon;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.html.editor.lib.api.HtmlSource;
import org.netbeans.modules.html.editor.lib.api.SyntaxAnalyzer;
import org.netbeans.modules.html.editor.lib.api.elements.Element;
import static org.netbeans.modules.html.editor.lib.api.elements.ElementType.CLOSE_TAG;
import static org.netbeans.modules.html.editor.lib.api.elements.ElementType.OPEN_TAG;
import org.netbeans.modules.html.editor.lib.api.elements.OpenTag;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.web.common.api.LexerUtils;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author marekfukala
 */
public class KOUtils {
    
    public static final String JAVASCRIPT_MIMETYPE = "text/javascript"; //NOI18N
    
    public static final String KO_DATA_BIND_MIMETYPE = "text/ko-data-bind"; //NOI18N
    
    public static final String KO_DATA_BIND_ATTR_NAME = "data-bind"; //NOI18N
    
    public static final ImageIcon KO_ICON =
                ImageUtilities.loadImageIcon("org/netbeans/modules/html/knockout/knockout-icon.png", false); // NOI18N
    
    
    public static final Color KO_COLOR = Color.green.darker();
    
    private static final int URL_CONNECTION_TIMEOUT = 1000; //ms
    private static final int URL_READ_TIMEOUT = URL_CONNECTION_TIMEOUT * 3; //ms
     /**
     * Gets document range for the given from and to embedded offsets. 
     * 
     * Returns null if the converted document offsets are invalid.
     */
    public static OffsetRange getValidDocumentOffsetRange(int efrom, int eto, Snapshot snapshot) {
        if(efrom == -1 || eto == -1) {
            throw new IllegalArgumentException(String.format("bad range: %s - %s", efrom, eto));
        }
        int dfrom = snapshot.getOriginalOffset(efrom);
        int dto = snapshot.getOriginalOffset(eto);
        if(dfrom == -1 || dto == -1) {
            return null;
        }
        if(dfrom > dto) {
            return null;
        }
        
        return new OffsetRange(dfrom, dto);
    }
    
    public static String getContentAsString(URL url, Charset charset) throws IOException {
        StringWriter writer = new StringWriter();
        loadURL(url, writer, charset);
        return writer.getBuffer().toString();
       
    }
    
    public static void loadURL(URL url, Writer writer, Charset charset) throws IOException {
        if (charset == null) {
            charset = Charset.defaultCharset();
        }
        URLConnection con = url.openConnection();
        con.setConnectTimeout(URL_CONNECTION_TIMEOUT);
        con.setReadTimeout(URL_READ_TIMEOUT);
        con.connect();
        Reader r = new InputStreamReader(new BufferedInputStream(con.getInputStream()), charset);
        char[] buf = new char[2048];
        int read;
        while ((read = r.read(buf)) != -1) {
            writer.write(buf, 0, read);
        }
        r.close();
    }
    
    public static String getFileContent(File file) throws IOException {
        Reader r = new FileReader(file);
        char[] buf = new char[2048];
        int read;
        StringBuilder sb = new StringBuilder();
        while ((read = r.read(buf)) != -1) {
            sb.append(buf, 0, read);
        }
        r.close();
        return sb.toString();
    }
    
     /**
     * Finds the "content" section of the KO binding documentation.
     */
    @NbBundle.Messages("cannot_load_help=Cannot load help.")
    public static String getKnockoutDocumentationContent(String content) {
        int stripFrom = 0;
        int stripTo = content.length();
        HtmlSource source = new HtmlSource(content);
        Iterator<Element> elementsIterator = SyntaxAnalyzer.create(source).elementsIterator();
        
        boolean inContent = false;
        int depth = 0;
        elements: while (elementsIterator.hasNext()) {
            Element element = elementsIterator.next();
            switch (element.type()) {
                case OPEN_TAG:
                    OpenTag ot = (OpenTag) element;
                    if (LexerUtils.equals("div", ot.name(), true, true)) { //NOI18N
                        org.netbeans.modules.html.editor.lib.api.elements.Attribute attribute = ot.getAttribute("class"); //NOI18N
                        if (attribute != null) {
                            CharSequence unquotedValue = attribute.unquotedValue();
                            if (unquotedValue != null && LexerUtils.equals("content", unquotedValue, true, true)) { //NOI18N
                                //found the page content
                                stripFrom = element.to();
                                inContent = true;
                            }
                        }
                    }
                    if(inContent) {
                        depth++;
                    }
                    break;
                case CLOSE_TAG:
                    if(inContent) {
                        depth--;
                        if(depth == 0) {
                            //end of the content
                            stripTo = element.from();
                            break elements;
                        }
                    }
                    break;
            }
        }
        
        return content.substring(stripFrom, stripTo);
    }

    
}
