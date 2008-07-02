/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.javascript.libraries.util;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.libraries.LibraryChooser;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.modules.javascript.libraries.api.JavaScriptLibrarySupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.windows.WindowManager;

/**
 *
 * @author Quy Nguyen <quynguyen@netbeans.org>
 */
public final class JSLibraryProjectUtils {
    private static final String WEB_PROJECT = "org.netbeans.modules.web.project.WebProject"; // NOI18N
    private static final String WEB_PROJECT_DEFAULT_RELATIVE_PATH = "web/resources"; // NOI18N
    private static final String RUBY_PROJECT = "org.netbeans.modules.ruby.rubyproject.RubyProject"; // NOI18N
    private static final String RUBY_PROJECT_DEFAULT_RELATIVE_PATH = "public/resources"; // NOI18N
    private static final String PHP_PROJECT = "org.netbeans.modules.php.project"; // NOI18N
    private static final String OTHER_PROJECT_DEFAULT_RELATIVE_PATH = "javascript/resources"; // NOI18N

    private static final String LIBRARY_LIST_PROP = "javascript-libraries"; // NOI18N
    private static final String LIBRARY_ZIP_VOLUME = "scriptpath"; // NOI18N
    
    public static LibraryChooser.Filter createDefaultFilter() {
        return new LibraryChooser.Filter() {
            public boolean accept(Library library) {
                return library.getType().equals("javascript"); // NOI18N
            }
        };
    }
    
    public static LibraryChooser.Filter createDefaultFilter(final Set<Library> excludedLibraries) {
        return new LibraryChooser.Filter() {
            public boolean accept(Library library) {
                return library.getType().equals("javascript") && // NOI18N
                        !excludedLibraries.contains(library);
            }
        };
    }
    
    public static LibraryManager getLibraryManager(Project project) {
        JavaScriptLibrarySupport projectSupport = project.getLookup().lookup(JavaScriptLibrarySupport.class);
        LibraryManager manager = null;
        
        if (projectSupport != null) {
            manager = projectSupport.getLibraryManager();
        }
        
        return (manager == null) ? LibraryManager.getDefault() : manager;
    }
    
    public static LibraryChooser.LibraryImportHandler getSharedLibraryHandler(Project project) {
        JavaScriptLibrarySupport projectSupport = project.getLookup().lookup(JavaScriptLibrarySupport.class);
        LibraryChooser.LibraryImportHandler importHandler = null;
        
        if (projectSupport != null) {
            importHandler = projectSupport.getSharedLibraryImportHandler();
        }
        
        return importHandler;
    }    
    
    public static List<Library> getJSLibraries(Project project) {
        Set<String> libNames = getJSLibraryNames(project);
        List<Library> libraryList = new ArrayList<Library>();
        LibraryManager manager = getLibraryManager(project);
        
        for (String libName : libNames) {
            Library lib = manager.getLibrary(libName);
            if (lib == null) {
                // XXX unresolved reference?
            } else {
                libraryList.add(lib);
            }            
        }
        
        return libraryList;
    }
    
    public static String getJSLibrarySourcePath(Project project) {
        JavaScriptLibrarySupport projectSupport = project.getLookup().lookup(JavaScriptLibrarySupport.class);
        
        if (projectSupport != null) {
            return projectSupport.getJavaScriptLibrarySourcePath();
        } else {
            return getDefaultSourcePath(project);
        }
        
    }
    
    public static void addJSLibraryMetadata(final Project project, final Collection<Library> libraries) {
        modifyJSLibraries(project, false, libraries);
    }
    
    public static void removeJSLibraryMetadata(Project project, Collection<Library> libraries) {
        modifyJSLibraries(project, true, libraries);
    }

    public static void setJSLibraryMetadata(final Project project, final List<Library> libraries) {
        ProjectManager.mutex().writeAccess(
                new Runnable() {
                    public void run() {
                        Preferences prefs = ProjectUtils.getPreferences(project, JSLibraryProjectUtils.class, true);
                        assert prefs != null;

                        StringBuffer propValue = new StringBuffer();
                        for (Library library : libraries) {
                            String name = library.getName();
                            
                            if (propValue.length() == 0) {
                                propValue.append(name);
                            } else {
                                propValue.append(";");
                                propValue.append(name);
                            }
                        }

                        prefs.put(LIBRARY_LIST_PROP, propValue.toString());
                        try {
                            prefs.flush();
                        } catch (BackingStoreException ex) {
                            Log.getLogger().log(Level.SEVERE, "Could not write to project preferences", ex);
                        }


                    }
                }
        
        );
    }

    private static String getDefaultSourcePath(Project project) {
        Project p = project.getLookup().lookup(Project.class);
        if (p == null) {
            Log.getLogger().warning("project.getLookup().lookup(Project.class) returned null for project: " + project);
            p = project;
        }
        
        String projectClassName = p.getClass().getName();
        String relativePath;
        if (projectClassName.startsWith(PHP_PROJECT)) {
            relativePath = WEB_PROJECT_DEFAULT_RELATIVE_PATH;
        } else if (projectClassName.equals(WEB_PROJECT)) {
            relativePath = WEB_PROJECT_DEFAULT_RELATIVE_PATH;
        } else if (projectClassName.equals(RUBY_PROJECT)) {
            relativePath = RUBY_PROJECT_DEFAULT_RELATIVE_PATH;
        } else {
            relativePath = OTHER_PROJECT_DEFAULT_RELATIVE_PATH;
        }
        
        File projectDirFile = FileUtil.toFile(p.getProjectDirectory());
        assert projectDirFile != null;
        
        return new File(projectDirFile, relativePath).getAbsolutePath();
    }
    
    private static void modifyJSLibraries(final Project project, final boolean remove, final Collection<Library> libraries) {
        final Set<String> libNames = getJSLibraryNames(project);
        
        ProjectManager.mutex().writeAccess(
                new Runnable() {
                    public void run() {
                        Preferences prefs = ProjectUtils.getPreferences(project, JSLibraryProjectUtils.class, true);
                        assert prefs != null;
                        
                        boolean modified = false;
                        for (Library library : libraries) {
                            String name = library.getName();
                            
                            if (remove && libNames.contains(name)) {
                                modified = true;
                                libNames.remove(name);
                            } else if (!remove && !libNames.contains(name)) {
                                modified = true;
                                libNames.add(name);
                            }
                        }
                        
                        if (modified) {
                            StringBuffer propValue = new StringBuffer();
                            for (String name : libNames) {
                                if (propValue.length() == 0) {
                                    propValue.append(name);
                                } else {
                                    propValue.append(";");
                                    propValue.append(name);
                                }
                            }
                            
                            prefs.put(LIBRARY_LIST_PROP, propValue.toString());
                            try {
                                prefs.flush();
                            } catch (BackingStoreException ex) {
                                Log.getLogger().log(Level.SEVERE, "Could not write to project preferences", ex);
                            }
                        }
                        
                    }
                }
        
        );
    }
    
    private static Set<String> getJSLibraryNames(Project project) {
        Preferences prefs = ProjectUtils.getPreferences(project, JSLibraryProjectUtils.class, true);
        assert prefs != null;
        
        String libraries = prefs.get(LIBRARY_LIST_PROP, "");
        String[] tokens = libraries.split(";");
        
        Set<String> librarySet = new LinkedHashSet<String>();
        for (String token : tokens) {
            librarySet.add(token);
        }
        
        return librarySet;
    }
    
    public static boolean extractLibrariesWithProgress(Project project, final Collection<Library> libraries, final String path) {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException("Cannot invoke JSLibraryProjectUtils.extractLibrariesWithProgress() outside event dispatch thread");
        }
        
        ResourceBundle bundle = NbBundle.getBundle(JSLibraryProjectUtils.class);
        final ProgressHandle handle = ProgressHandleFactory.createHandle(bundle.getString("LBL_Add_Libraries_progress"));
        JComponent component = ProgressHandleFactory.createProgressComponent(handle);
        Frame mainWindow = WindowManager.getDefault().getMainWindow();
        final JDialog dialog = new JDialog(mainWindow, bundle.getString("LBL_Add_Libraries_Title"), true);
        
        JSLibraryModificationPanel panel = new JSLibraryModificationPanel(component, bundle.getString("LBL_Add_Libraries_Msg"));

        dialog.getContentPane().add(panel);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.pack();

        Rectangle bounds = mainWindow.getBounds();
        int middleX = bounds.x + bounds.width / 2;
        int middleY = bounds.y + bounds.height / 2;
        Dimension size = dialog.getPreferredSize();
        dialog.setBounds(middleX - size.width / 2, middleY - size.height / 2, size.width, size.height);
        
        final Map<Library, LibraryData> libData = new HashMap<Library, LibraryData>();
        for (Library library : libraries) {
            try {
                String libName = library.getName().replaceAll(" ", "_");
                File folderPath = new File(path, libName);
                FileObject jsFolder = FileUtil.createFolder(folderPath);

                LibraryData data = new LibraryData();
                data.setDestinationFolder(jsFolder);
                libData.put(library, data);
            } catch (IOException ex) {
                Log.getLogger().log(Level.SEVERE, "Unable to create folder for javascript library", ex);
            }
        }
        
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                try {
                    int totalSize = 0;
                    for (Library library : libraries) {
                        Collection<ZipFile> zips = getJSLibraryZips(library);
                        libData.get(library).setZipFiles(zips);
                        for (ZipFile zipFile : zips) {
                            totalSize += zipFile.size();
                        }
                    }

                    handle.start(totalSize);

                    int currentSize = 0;
                    for (Library library : libraries) {
                        LibraryData data = libData.get(library);
                        Collection<ZipFile> zipFiles = data.getZipFiles();
                        File destination = FileUtil.toFile(data.getDestinationFolder());
                        if (destination == null) {
                            Log.getLogger().severe("No File for FileObject: " + FileUtil.getFileDisplayName(data.getDestinationFolder()));
                            continue;
                        }

                        for (ZipFile zip : zipFiles) {
                            try {
                                currentSize = extractZip(destination, zip, handle, currentSize);
                            } catch (IOException ex) {
                                Log.getLogger().log(Level.SEVERE, "Unable to extract zip file", ex);
                            }
                        }
                    }

                    handle.finish();
                } finally {
                    SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                            dialog.setVisible(false);
                            dialog.dispose();
                        }
                    });
                }
            }
        });

        dialog.setVisible(true);
        return true;
    }
    
    public static boolean deleteLibrariesWithProgress(Project project, Collection<Library> libraries, String path) {
        return true;
    }
    
    public static boolean deleteLibraryFromProject(Project project ,Library library) {
        // TODO implement file deletion
        return true;
    }
    
    public static boolean isLibraryFolderEmpty(Project project, Library library) {
        String path = getJSLibrarySourcePath(project);
        return isLibraryFolderEmpty(project, library, path);
    }
    
    public static boolean isLibraryFolderEmpty(Project project, Library library, String path) {
        String libName = library.getName().replaceAll(" ", "_");
        FileObject fo = FileUtil.toFileObject(FileUtil.normalizeFile(new File(path, libName)));

        return fo == null || fo.getChildren().length == 0;
    }
    
    private static Collection<ZipFile> getJSLibraryZips(Library library) {
        ArrayList<ZipFile> result = new ArrayList<ZipFile>();

        try {
            for (URL url : library.getContent(LIBRARY_ZIP_VOLUME)) {
                URL archiveURL = FileUtil.isArchiveFile(url) ? url : FileUtil.getArchiveFile(url);
                FileObject archiveFO = URLMapper.findFileObject(archiveURL);

                File zipFile = (archiveFO != null) ? FileUtil.toFile(archiveFO) : null;
                if (zipFile != null) {
                    result.add(new ZipFile(zipFile));
                }
            }
        } catch (IOException ex) {
            Log.getLogger().log(Level.SEVERE, "Unable to load zip file", ex);
        }

        return result;
    }
    
    private static int extractZip(File outDir, ZipFile zipFile, ProgressHandle handle, int currentTotal) throws IOException {
        
        try {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                String entryName = zipEntry.getName();
                if (zipEntry.isDirectory()) {
                    File newFolder = new File(outDir, entryName);
                    newFolder.mkdirs();
                    
                    handle.progress(++currentTotal);
                } else {
                    File file = new File(outDir, entryName);
                    BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file));
                    InputStream input = zipFile.getInputStream(zipEntry);
                    
                    try {
                        final byte[] buffer = new byte[4096];
                        int len;
                        while ((len = input.read(buffer)) >= 0) {
                            output.write(buffer, 0, len);
                        }
                    } finally {
                        output.close();
                        input.close();
                        
                        handle.progress(++currentTotal);
                    }
                }
            }
        } finally {
            zipFile.close();
        }

        return currentTotal;
    }
    
    private static final class LibraryData {
        private Collection<ZipFile> zipFiles;
        private FileObject destinationFolder;
        
        public FileObject getDestinationFolder() {
            return destinationFolder;
        }

        public void setDestinationFolder(FileObject destinationFolder) {
            this.destinationFolder = destinationFolder;
        }

        public Collection<ZipFile> getZipFiles() {
            return zipFiles;
        }

        public void setZipFiles(Collection<ZipFile> zipFiles) {
            this.zipFiles = zipFiles;
        }
    }
}
