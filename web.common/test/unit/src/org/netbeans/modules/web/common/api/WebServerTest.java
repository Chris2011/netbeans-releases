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
package org.netbeans.modules.web.common.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import static org.junit.Assert.*;
import org.netbeans.api.project.Project;
import org.netbeans.junit.NbTestCase;
import org.netbeans.spi.project.FileOwnerQueryImplementation;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.test.MockLookup;

public class WebServerTest extends NbTestCase {

    private Project testProject1;
    private FileObject siteRoot1;
    private FileObject fooHtml;
    private Project testProject2;
    private FileObject siteRoot2;
    private FileObject barHtml;

    public WebServerTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MockLookup.init();
        FileObject fo = FileUtil.toFileObject(getWorkDir());
        fo = fo.createFolder(""+System.currentTimeMillis());
        FileObject proj1 = FileUtil.createFolder(fo, "proj1");
        siteRoot1 = FileUtil.createFolder(proj1, "site_a");
        OutputStream os = siteRoot1.createAndOpen("foo.html");
        os.write("I'm foo_l".getBytes());
        os.close();
        fooHtml = siteRoot1.getFileObject("foo.html");
        FileObject proj2 = FileUtil.createFolder(fo, "proj2");
        siteRoot2 = FileUtil.createFolder(proj2, "site_b");
        os = siteRoot2.createAndOpen("bar.html");
        os.write("I'm bar_ley".getBytes());
        os.close();
        barHtml = siteRoot2.getFileObject("bar.html");
        testProject1 = new TestProject(proj1);
        testProject2 = new TestProject(proj2);
        MockLookup.setInstances(new FileOwnerQueryImpl(testProject1, testProject2));
    }

    public void testWebServer() throws Exception {
        WebServer ws = WebServer.getWebserver();

        assertNull(ws.toServer(fooHtml));
        assertNull(ws.fromServer(new URL("http://localhost:8383/foo.html")));
        ws.start(testProject1, siteRoot1, "/");
        assertEquals(new URL("http://localhost:8383/foo.html"), ws.toServer(fooHtml));
        assertNull(ws.fromServer(new URL("http://localhost:8383/none.html")));
        assertEquals(fooHtml, ws.fromServer(new URL("http://localhost:8383/foo.html")));

        assertNull(ws.toServer(barHtml));
        assertNull(ws.fromServer(new URL("http://localhost:8383/xxx/bar.html")));
        ws.start(testProject2, siteRoot2, "/xxx");
        assertEquals(new URL("http://localhost:8383/xxx/bar.html"), ws.toServer(barHtml));
        assertNull(ws.fromServer(new URL("http://localhost:8383/xxx/none.html")));
        assertEquals(barHtml, ws.fromServer(new URL("http://localhost:8383/xxx/bar.html")));


        InputStream is = new URL("http://localhost:8383/foo.html").openStream();
        byte[] b = new byte[30];
        is.read(b);
        is.close();
        assertEquals("I'm foo_l", new String(b).trim());
        is = new URL("http://localhost:8383/xxx/bar.html").openStream();
        is.read(b);
        is.close();
        assertEquals("I'm bar_ley", new String(b).trim());

        boolean ok = false;
        try {
            is = new URL("http://localhost:8383/xxx/none.html").openStream();
            is.read(b);
        } catch (IOException ex) {
            ok = true;
        }
        assert(ok);

        ok = false;
        try {
            is = new URL("http://localhost:8383/none/a.html").openStream();
            is.read(b);
        } catch (IOException ex) {
            ok = true;
        }
        assert(ok);

        ws.stop(testProject1);
        assertNull(ws.toServer(fooHtml));
        assertNull(ws.fromServer(new URL("http://localhost:8383/foo.html")));
        assertEquals(new URL("http://localhost:8383/xxx/bar.html"), ws.toServer(barHtml));
        assertEquals(barHtml, ws.fromServer(new URL("http://localhost:8383/xxx/bar.html")));

        ws.stop(testProject2);
        assertNull(ws.toServer(fooHtml));
        assertNull(ws.fromServer(new URL("http://localhost:8383/foo.html")));
        assertNull(ws.toServer(barHtml));
        assertNull(ws.fromServer(new URL("http://localhost:8383/xxx/bar.html")));

        ok = false;
        try {
            is = new URL("http://localhost:8383/foo.html").openStream();
            is.read(b);
        } catch (IOException ex) {
            ok = true;
        }
        assert(ok);

        ok = false;
        try {
            is = new URL("http://localhost:8383/xxx/bar.html").openStream();
            is.read(b);
        } catch (IOException ex) {
            ok = true;
        }
        assert(ok);
    }

    private static class TestProject implements Project {

        private FileObject fo;

        public TestProject(FileObject fo) {
            this.fo = fo;
        }

        @Override
        public FileObject getProjectDirectory() {
            return fo;
        }

        @Override
        public Lookup getLookup() {
            return Lookup.EMPTY;
        }

    }

    private static class FileOwnerQueryImpl implements FileOwnerQueryImplementation {

        private Project testProject1;
        private Project testProject2;

        public FileOwnerQueryImpl(Project testProject1, Project testProject2) {
            this.testProject1 = testProject1;
            this.testProject2 = testProject2;
        }

        @Override
        public Project getOwner(URI file) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Project getOwner(FileObject file) {
            if (file.getParent().equals(testProject1.getProjectDirectory())) {
                return testProject1;
            }
            if (file.getParent().equals(testProject2.getProjectDirectory())) {
                return testProject2;
            }
            return null;
        }

    }
}
