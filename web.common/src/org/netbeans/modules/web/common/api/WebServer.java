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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 * Simple Web Server supporting only GET command on project's source files.
 */
public final class WebServer {

    private static int PORT = 8383;
    
    private static final Logger LOGGER = Logger.getLogger(WebServer.class.getName());

    private WeakHashMap<Project, Pair> deployedApps = new WeakHashMap<Project, Pair>();
    private boolean init = false;
    private Server server;
    private static WebServer webServer;
    
    private WebServer() {
    }

    public static synchronized WebServer getWebserver() {
        if (webServer == null) {
            webServer = new WebServer();
        }
        return webServer;
    }

    private synchronized void checkStartedServer() {
        if (!init) {
            init = true;
            startServer();
        }
    }
    
    /**
     * Start serving project's sources under given web context root.
     */
    public void start(Project p, FileObject siteRoot, String webContextRoot) {
        checkStartedServer();
        deployedApps.remove(p);
        deployedApps.put(p, new Pair(webContextRoot, siteRoot));
    }

    private static class Pair {
        String webContextRoot;
        FileObject siteRoot;

        public Pair(String webContextRoot, FileObject siteRoot) {
            this.webContextRoot = webContextRoot;
            this.siteRoot = siteRoot;
        }
    }
    
    /**
     * Stop serving project's sources.
     */
    public void stop(Project p) {
        deployedApps.remove(p);
        // TODO: if deployedApps is empty we can stop the server
    }

    /**
     * Port server is running on.
     */
    public int getPort() {
        checkStartedServer();
        return server.getPort();
    }

    /**
     * Converts project's file into server URL.
     * @return returns null if project is not currently served
     */
    public URL toServer(FileObject projectFile) {
        Project p = FileOwnerQuery.getOwner(projectFile);
        if (p != null) {
            Pair pair = deployedApps.get(p);
            if (pair != null) {
                String path = pair.webContextRoot + (pair.webContextRoot.equals("/") ? "" : "/") +  //NOI18N
                        FileUtil.getRelativePath(pair.siteRoot, projectFile);
                return WebUtils.stringToUrl("http://localhost:"+getPort()+path); //NOI18N
            }
        } else {
            // fallback if project was not found:
            for (Map.Entry<Project, Pair> entry : deployedApps.entrySet()) {
                Pair pair = entry.getValue();
                String relPath = FileUtil.getRelativePath(pair.siteRoot, projectFile);
                if (relPath != null) {
                    String path = pair.webContextRoot + (pair.webContextRoot.equals("/") ? "" : "/") +  //NOI18N
                            relPath;
                    return WebUtils.stringToUrl("http://localhost:"+getPort()+path); //NOI18N
                }
            }
        }
        return null;
    }

    /**
     * Converts server URL back into project's source file.
     */
    public FileObject fromServer(URL serverURL) {
        String path;
        try {
            path = serverURL.toURI().getPath();
        } catch (URISyntaxException ex) {
            path = serverURL.getPath(); // fallback
        }
        return fromServer(path);
    }

    private FileObject fromServer(String serverURLPath) {
        Map.Entry<Project, Pair> rootEntry = null;
        for (Map.Entry<Project, Pair> entry : deployedApps.entrySet()) {
            if ("/".equals(entry.getValue().webContextRoot)) { //NOI18N
                rootEntry = entry;
                // process this one as last one:
                continue;
            }
            if (serverURLPath.startsWith(entry.getValue().webContextRoot+"/")) { //NOI18N
                return findFile(entry, serverURLPath);
            }
        }
        if (rootEntry != null) {
            return findFile(rootEntry, serverURLPath);
        }
        return null;
    }

    private FileObject findFile(Entry<Project, Pair> entry, String serverURL) {
        Project p = entry.getKey();
        int index = entry.getValue().webContextRoot.length()+1;
        if (entry.getValue().webContextRoot.equals("/")) { //NOI18N
            index = 1;
        }
        String file = serverURL.substring(index);
        return entry.getValue().siteRoot.getFileObject(file);
    }

    private void startServer() {
        server = new Server();
        new Thread( server ).start();
        Thread shutdown = new Thread(){
            @Override
            public void run() {
                server.stop();
            }
        };
        Runtime.getRuntime().addShutdownHook( shutdown);
    }

    private static class Server implements Runnable {

        private boolean stop = false;
        private ServerSocket sock;
        private int port;

        public Server() {
            port = PORT;
            while (true) {
                try {
                    sock = new ServerSocket(port);
                } catch (IOException ex) {
                    // port used:
                    port++;
                    continue;
                }
                break;
            }
        }
        
        @Override
        public void run() {
            try {
                while (!stop) {
                    Socket s = sock.accept();
                    if (stop) {
                        break;
                    }
                    read(s.getInputStream(), s.getOutputStream());
                }
            } catch (SocketException ex) {
                if (!stop) {
                    Exceptions.printStackTrace(ex);
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        private void stop() {
            stop = true;
            try {
                sock.close();
            } catch (IOException ex) {
            }
        }

        public int getPort() {
            return port;
        }
        
        private void read(InputStream inputStream, OutputStream outputStream) throws IOException {
            BufferedReader r = null;
            DataOutputStream out = null;
            InputStream fis = null;
            try {
                r = new BufferedReader(new InputStreamReader(inputStream));
                String line = r.readLine();
                if (line == null || line.length() == 0) {
                    return;
                }
                if (line.startsWith("GET ")) { //NOI18N
                    StringTokenizer st = new StringTokenizer(line, " "); //NOI18N
                    st.nextToken();
                    String file = st.nextToken();
                    try {
                        file = URLDecoder.decode(file, "UTF-8"); //NOI18N
                    } catch (IllegalArgumentException ex) {
                        // #222858 - IllegalArgumentException: URLDecoder: Illegal hex characters in escape (%) pattern - For input string: "%2"
                        // silently ignore
                        LOGGER.log(Level.FINE, "cannot decode '"+file+"'", ex); // NOI18N
                    }
                    FileObject fo = getWebserver().fromServer(file);
                    if (fo != null && fo.isFolder()) {
                        fo = fo.getFileObject("index", "html"); //NOI18N
                    }
                    if (fo != null) {
                        fis = fo.getInputStream();
                        out = new DataOutputStream(outputStream);
                        String mime = fo.getMIMEType();
                        if ("content/unknown".equals(mime)) { //NOI18N
                            mime = "text/plain"; //NOI18N
                        }
                        try {
                            out.writeBytes("HTTP/1.1 200 OK\nContent-Length: "+fo.getSize()+"\n" //NOI18N
                                    + "Content-Type: "+mime+"\n\n"); //NOI18N
                            FileUtil.copy(fis, out);
                        } catch (SocketException se) {
                            // browser refused to accept data or closed the connection;
                            // not much we can do about this
                        }
                    }
                }
            } finally {
                if (fis != null) {
                    fis.close();
                }
                if (r != null) {
                    r.close();
                }
                if (out != null) {
                    out.close();
                }
            }
        }
    
    }
    
}
