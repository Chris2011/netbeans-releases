/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/* $Id$ */

package org.netbeans.modules.form.palette;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.jar.*;
import java.beans.PropertyVetoException;
import java.text.MessageFormat;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import org.openide.*;
import org.openide.nodes.Node;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.netbeans.modules.form.FormLoaderSettings;
import org.netbeans.modules.clazz.ClassDataObject;

/**
 * Bean Installer
 * @author Petr Hamernik
 */
public final class BeanInstaller
{
    /** Text resources */
    private static final ResourceBundle bundle = NbBundle.getBundle(BeanInstaller.class);

    /** Last opened directory */
    private static File lastDirectory;

    /** Extension of jar archive where to find module */
    
    static String JAR_EXT = ".jar"; // NOI18N

    /** 
     * asks the user for a jar file, lets him choose available beans and
     * install them into the component palette
     */
    
    public static void installBean() {
        File jarFile = selectJarModule();
        if (jarFile == null)
            return;

        JarFileSystem jar = createJarForFile(jarFile);
        if (jar == null) {
            TopManager.getDefault().notify(new NotifyDescriptor.Message(
                bundle.getString(
                    "MSG_ErrorInFile"), NotifyDescriptor.ERROR_MESSAGE));
            return;
        }

        Collection beans = findJavaBeans(jar);

        if (beans.size() == 0) {
            TopManager.getDefault().notify(new NotifyDescriptor.Message(
                bundle.getString(
                    "MSG_noBeansInJar"), NotifyDescriptor.INFORMATION_MESSAGE));
            return;
        }

        BeanSelector sel = new BeanSelector(beans);
        DialogDescriptor desc = new DialogDescriptor(
            sel,
            bundle.getString("CTL_SelectJB"),
            true,
            null
            );
        desc.setHelpCtx(new HelpCtx(BeanInstaller.class.getName() + ".installBean")); // NOI18N

        TopManager.getDefault().createDialog(desc).show();
        if (desc.getValue() == NotifyDescriptor.OK_OPTION) {
            String cat = selectPaletteCategory();
            if (cat != null) {
                installBeans(jar, sel.getSelectedBeans(), cat);
            }
        }
    }

    public static void installBeans(Node[] nodes) {
        String cat = selectPaletteCategory();
        if (cat == null)
            return;

        ArrayList list = new ArrayList(nodes.length);
        for (int i = 0; i < nodes.length; i++) {
            DataObject dobj = (DataObject) nodes[i].getCookie(ClassDataObject.class);
            if (dobj != null)
                list.add(dobj);
            else {
                InstanceCookie ic =
                    (InstanceCookie) nodes[i].getCookie(InstanceCookie.class);
                if (ic != null)
                    list.add(ic);
            }
        }
        installBeans(null, list, cat);
    }

    /**
     * searches the jar for beans
     * @return Collection of founded beans represented as FileObjects
     */
    
    private static Collection findJavaBeans(JarFileSystem jar) {
        LinkedList beans = new LinkedList();

        Manifest manifest = jar.getManifest();
        Map entries = manifest.getEntries();

        Iterator it = entries.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            if (!key.endsWith(".class") && !key.endsWith(".ser"))
                continue;
            
            String value = ((Attributes) entries.get(key)).getValue("Java-Bean"); // NOI18N
            if ((value == null) || ! value.equalsIgnoreCase("True")) // NOI18N
                continue;

            String exts[] = { ".class", ".ser" }; // NOI18N
            for (int i = 0; i < exts.length; i++) {
                String ext = exts[i];
                if (!key.endsWith(ext))
                    continue;
                String resourcePath = key.replace('\\', '/');
                FileObject fo = jar.findResource(resourcePath);
                if (fo != null) {
                    beans.add(fo);
                }
            }
        }
        return beans;
    }

    /**
     * installs selected beans from a specified jar into a palette category
     * @param jar JarFileSystem - the source of beans to be mounted, or null if
     * not necessary
     * @param list Collection of FileObjects - selected JBs
     * @param cat palettecategory where to place beans.
     */
    
    private static void installBeans(JarFileSystem jar, Collection beans, String cat) {
        if (jar != null) addJarFileSystem(jar);

        if (cat == null) {
            cat = "Beans"; // default palette category // NOI18N
        }

        FileObject root = TopManager.getDefault().getRepository().getDefaultFileSystem().getRoot();
        FileObject paletteFolder = root.getFileObject("Palette"); // NOI18N
        if (paletteFolder == null) {
            return;
        }

        FileObject category = paletteFolder.getFileObject(cat);
        if (category == null) {
            try {
                category = paletteFolder.createFolder(cat);
            } catch (IOException e) {
                if (System.getProperty("netbeans.debug.exceptions") != null) e.printStackTrace();
                /* ignore */
                return;
            }
        }

        ClassLoader loader = TopManager.getDefault().currentClassLoader();
        Iterator it = beans.iterator();
        LinkedList paletteNodes = new LinkedList();

        while (it.hasNext()) {
            Object obj = it.next();
            String name = null;
            if (obj instanceof FileObject) {
                if ("class".equals(((FileObject)obj).getExt())) { // NOI18N
                    name =((FileObject)obj).getPackageName('.');
                    if (name != null) createInstance(category, name, null);
                } else {
                    createShadow(category,(FileObject)obj);
                }
            } else if (obj instanceof InstanceCookie) {
                name =((InstanceCookie)obj).instanceName();
                if (name != null) createInstance(category, name, null);
            } else if (obj instanceof ClassDataObject) {
                try {
                    ((ClassDataObject)obj).createShadow(DataFolder.findFolder(category));
                } catch (IOException ex) {
                    TopManager.getDefault().notifyException(ex);
                }
            }
        }
    }


    private static void addJarFileSystem(JarFileSystem jar) {
        // 1. store information about the JAR/ZIP into $NETBEANS_USER/beans/libs.properties
        //    so that it will be added to every new project's filesystems
        File localFolder = new File(System.getProperty("netbeans.user") + File.separator + "beans");
        if (!localFolder.exists()) {
            localFolder.mkdirs();
        }
        File installedLibsFile = new File(System.getProperty("netbeans.user") + File.separator + "beans" + File.separator + "libs.properties");
        Properties installedLibs = new Properties();
        try {
            installedLibs.load(new FileInputStream(installedLibsFile));
        } catch (IOException e) {
            if (System.getProperty("netbeans.debug.exceptions") != null) e.printStackTrace();
            /* ignore */
        }
        HashMap libsMap = new HashMap(installedLibs.size()*2);
        String newName;
        try {
            for (Enumeration e = installedLibs.propertyNames(); e.hasMoreElements();) {
                String lib = installedLibs.getProperty((String)e.nextElement());
                libsMap.put(lib, lib);
            }
            newName = jar.getJarFile().getCanonicalPath();
            if (libsMap.get(newName) == null) {
                libsMap.put(newName, newName);
                installedLibs.clear();
                int index = 1;
                for (Iterator it = libsMap.keySet().iterator(); it.hasNext();) {
                    installedLibs.setProperty("library"+index,(String)it.next());
                    index++;
                }
                installedLibs.store(new FileOutputStream(installedLibsFile), "");
            }
        } catch (IOException e) {
            if (System.getProperty("netbeans.debug.exceptions") != null) e.printStackTrace();
            /* ignore */
        }

        // 2. add it to the current project's filesystems
        boolean alreadyInstalled = false;
        if (jar != null) {
            Repository rep = TopManager.getDefault().getRepository();
            JarFileSystem jar2 =(JarFileSystem) rep.findFileSystem(jar.getSystemName());
            if (jar2 != null) {
                alreadyInstalled = true;
                jar = jar2;
            }

            if (!alreadyInstalled) {
                jar.setHidden(true);
                rep.addFileSystem(jar);
            }
        }
    }

    private static void createShadow(FileObject folder, FileObject original) {
        try {
            DataObject originalDO = DataObject.find(original);
            //      System.out.println("createShadow: "+folder + ", : " + original +", : "+originalDO); // NOI18N
            if (originalDO != null) {
                DataShadow.create(DataFolder.findFolder(folder), originalDO);
            }
        } catch (IOException e) {
            TopManager.getDefault().notifyException(e);
        }
    }

    private static void createInstance(FileObject folder, String className, String iconName) {
        String fileName = formatName(className);
        FileLock lock = null;
        try {
            if (folder.getFileObject(fileName+".instance") == null) { // NOI18N
                FileObject fo = folder.createData(fileName, "instance"); // NOI18N
                if (iconName != null) {
                    lock = fo.lock();
                    java.io.OutputStream os = fo.getOutputStream(lock);
                    String ic = "icon="+iconName; // NOI18N
                    os.write(ic.getBytes());
                }
                DataObject dobj = DataObject.find(fo);
                if (dobj != null) {
                    // enforce creation of node so that it is displayed
                    dobj.getNodeDelegate();
                }
            }
        } catch (java.io.IOException e) {
            if (System.getProperty("netbeans.debug.exceptions") != null) e.printStackTrace();
            /* ignore */
        } catch (Throwable t) {
            if (t instanceof ThreadDeath) {
                throw(ThreadDeath)t;
            } else {
                t.printStackTrace();
            }
        } finally {
            if (lock != null) {
                lock.releaseLock();
            }
        }
    }

    private static String formatName(String className) {
        String ret = className.substring(className.lastIndexOf(".") + 1) + "[" + className.replace('.', '-') + "]"; // NOI18N
        return ret;
    }

    /**
     * Opens dialog and lets user select category, where beans should be installed
     */
    
    private static String selectPaletteCategory() {
        CategorySelector sel = new CategorySelector();
        DialogDescriptor desc = new DialogDescriptor(
            sel,
            bundle.getString("CTL_SelectPalette"),
            true,
            null
            );
        desc.setHelpCtx(new HelpCtx(BeanInstaller.class.getName() + ".selectPaletteCategory")); // NOI18N

        TopManager.getDefault().createDialog(desc).show();
        if (desc.getValue() == NotifyDescriptor.OK_OPTION) {
            return sel.getSelectedCategory();
        } else {
            return null;
        }
    }

    /**
     * prompts user for the jar file
     * @return filename or null if operation was cancelled.
     */
    
    private static File selectJarModule() {
        JFileChooser chooser = new JFileChooser();

        chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return(f.isDirectory() || f.getName().endsWith(JAR_EXT));
            }
            public String getDescription() {
                return bundle.getString("CTL_JarArchivesMask");
            }
        });

        if (lastDirectory != null) {
            chooser.setCurrentDirectory(lastDirectory);
        }

        chooser.setDialogTitle(bundle.getString("CTL_SelectJar"));
        if (chooser.showDialog(TopManager.getDefault().getWindowManager().getMainWindow(),
                               bundle.getString("CTL_Select_Approve_Button"))
            == JFileChooser.APPROVE_OPTION)
        {
            File f = chooser.getSelectedFile();
            lastDirectory = chooser.getCurrentDirectory();
            if ((f != null) && f.isFile()) {
                if (f.getName().endsWith(JAR_EXT)) {
                    return f;
                } else {
                    TopManager.getDefault().notify(new NotifyDescriptor.Message(
                        bundle.getString("MSG_noBeansInJar"), NotifyDescriptor.INFORMATION_MESSAGE)
                                                   );
                }
            }
        }
        return null;
    }

    /**
     * @return jar FS for the given name or null if some problems occured
     */
    private static JarFileSystem createJarForFile(File jarFile) {
        try {
            JarFileSystem jar = new JarFileSystem();
            jar.setJarFile(jarFile);
            return jar;
        }
        catch (PropertyVetoException e) {
            if (System.getProperty("netbeans.debug.exceptions") != null)
                e.printStackTrace();
            return null;
        }
        catch (IOException e) {
            if (System.getProperty("netbeans.debug.exceptions") != null)
                e.printStackTrace();
            return null;
        }
    }

    //==============================================================================
    // Auto loading beans on startup
    //==============================================================================

    /** Auto loading all jars - beans */
    public static void autoLoadBeans() {
        FormLoaderSettings settings = new FormLoaderSettings();

        File globalFolder = new File(System.getProperty("netbeans.home") + File.separator + "beans");
        try {
            globalFolder = new File(globalFolder.getCanonicalPath());
        } catch (IOException e) {
            if (System.getProperty("netbeans.debug.exceptions") != null) e.printStackTrace();
            /* ignore */
        }

        File localFolder = new File(System.getProperty("netbeans.user") + File.separator + "beans");
        try {
            localFolder = new File(localFolder.getCanonicalPath());
        } catch (IOException e) {
            if (System.getProperty("netbeans.debug.exceptions") != null) e.printStackTrace();
            /* ignore */
        }

        if (autoLoadFolders(globalFolder, localFolder)) {
            /* JST: Why? A hack?
               java.awt.EventQueue.invokeLater(
               new Runnable() {
               public void run() {
               ComponentPalette.getDefault().updatePalette();
               }
               }
               );
            */      
        }
    }

    /** Loads the beans stored in the given folder.
     * @param folder - where to find jars
     */
    private static boolean autoLoadFolders(File globalFolder, File localFolder) {
        if (!(globalFolder.exists() || localFolder.exists()))
            return false;

        boolean modified = false;

        final String localBase = localFolder.getAbsolutePath() + File.separator;
        String globalBase = null; if (globalFolder != localFolder) globalBase = globalFolder.getAbsolutePath() + File.separator;

        File[] list = localFolder.listFiles();
        if (list == null) list = new File[0];
        if (globalBase != null) {
            File[] globalList = globalFolder.listFiles();
            if (globalList == null) globalList = new File[0];
            if (globalList.length > 0) {
                // add the list of jars in the global folder as well
                File[] newList = new File[list.length + globalList.length];
                System.arraycopy(list, 0, newList, 0, list.length);
                System.arraycopy(globalList, 0, newList, list.length, globalList.length);
                list = newList;
            }
        }

        // 1. load list of already installed beans
        FileInputStream fis2 = null;
        Properties alreadyInstalled = new Properties();
        try {
            alreadyInstalled.load(fis2 = new FileInputStream(localBase + "installed.properties")); // NOI18N
        } catch (IOException e) {
            /* ignore - the file just does not exist */
        } finally {
            if (fis2 != null) try { fis2.close(); } catch (IOException e) { /* ignore */ };
        }

        // 2. process local beans
        Properties details = new Properties();
        FileInputStream fis = null;
        try {
            details.load(fis = new FileInputStream(localBase + "beans.properties")); // NOI18N
        } catch (IOException e) {
            if (System.getProperty("netbeans.debug.exceptions") != null)
                System.err.println(e.getMessage());
            // ignore in this case
        } finally {
            if (fis != null) try { fis.close(); } catch (IOException e) { /* ignore */ };
        }

        // 3. process global beans
        if (globalBase != null) {
            FileInputStream fis3 = null;
            try {
                Properties globalDetails = new Properties();
                globalDetails.load(fis3 = new FileInputStream(globalBase + "beans.properties")); // NOI18N
                for (Enumeration e = globalDetails.propertyNames(); e.hasMoreElements();) {
                    String propName =(String)e.nextElement();
                    if (details.get(propName) == null) { // if not present in the local list, copy the <name, value> to it
                        details.put(propName, globalDetails.get(propName));
                    }
                }
            } catch (IOException e) {
                if (System.getProperty("netbeans.debug.exceptions") != null) e.printStackTrace();
                // ignore in this case
            } finally {
                if (fis3 != null) try { fis3.close(); } catch (IOException e) { /* ignore */ };
            }
        }

        // XXX(-tdt) ComponentPalette.<init> kicks off a thread to load
        // installed beans. This thread must be postponed until bean jars are
        // added to the repository
        // 
        // String[] categories = ComponentPalette.getDefault().getPaletteCategories();
        
        for (int i = 0; i < list.length; i++) {
            if (list[i].getName().endsWith(JAR_EXT)) {
                if (alreadyInstalled.get(list[i].getName()) == null) {
                    modified = true;
                    String withoutExt = list[i].getName().substring(0, list[i].getName().length() - JAR_EXT.length());
                    String categoryName = details.getProperty(withoutExt, withoutExt);
                    if (autoLoadJar(list[i], categoryName, details.getProperty(withoutExt + ".beans"))) {
                        alreadyInstalled.put(list[i].getName(), "true"); // NOI18N
                    }
                } else {
                    // ensure, that the filesystems are present
                    addJarFileSystem(createJarForFile(list[i]));
                }
            }
        }

        if (modified) {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(localBase + "installed.properties"); // NOI18N
                alreadyInstalled.store(fos, org.netbeans.modules.form.FormEditor.getFormBundle().getString("MSG_InstalledArchives"));
            } catch (IOException e) {
                if (System.getProperty("netbeans.debug.exceptions") != null) e.printStackTrace();
                // ignore
            } finally {
                if (fos != null) try { fos.close(); } catch (IOException e) { /* ignore */ };
            }
        }

        return modified;

    }

    /**
     * Loaded beans from the jar.
     * @param jarFile the jar File
     * @param palette category where to place the beans
     * @param selection the selection of beans which should be installed. If
     * null, all beans are loaded.
     */
    
    private static boolean autoLoadJar(File jarFile,
                                       String paletteCategory, String selection) {
        JarFileSystem jar = createJarForFile(jarFile);
        if (jar == null) {
            TopManager.getDefault().notify(
                new NotifyDescriptor.Message(bundle.getString("MSG_ErrorInFile"),
                                             NotifyDescriptor.ERROR_MESSAGE)
                );
            return false;
        }
        
        JarFileSystem jar2 =(JarFileSystem) TopManager.getDefault().getRepository().findFileSystem(jar.getSystemName());
        if (jar2 != null)
            jar = jar2;

        Collection beans = findJavaBeans(jar);
        if (selection == null) {
            installBeans(jar, beans, paletteCategory);
        }
        else {
            Vector dest = new Vector();
            StringTokenizer tok = new StringTokenizer(selection, ", ", false); // NOI18N
            while (tok.hasMoreTokens()) {
                String token = tok.nextToken();
                String clName = token;
                String clPack = ""; // NOI18N

                int lastDot = token.lastIndexOf('.');
                if ((lastDot != -1) &&(!token.endsWith("."))) { // NOI18N
                    clName = token.substring(lastDot + 1);
                    clPack = token.substring(0, lastDot);
                }
                FileObject fo = jar.find(clPack, clName, "class"); // NOI18N
                if (fo == null) { // if not found, try ser
                    fo = jar.find(clPack, clName, "ser"); // NOI18N
                }
                if (fo != null) {
                    for (Iterator iter = beans.iterator(); iter.hasNext();) {
                        FileObject fo2 = (FileObject) iter.next();
                        if (fo.equals(fo2)) {
                            dest.addElement(fo);
                        }
                    }
                }
            }
            installBeans(jar, dest, paletteCategory);
        }
        return true;
    }

    private static class CategorySelector extends JPanel {
        private JList list;

        static final long serialVersionUID =936459317386043582L;
        
        public CategorySelector() {
            String[] categories = ComponentPalette.getDefault().getPaletteCategories();

            list = new JList(categories);
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            /*      list.addListSelectionListener(new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent evt) {
                    okButton.setEnabled(!list.isSelectionEmpty());
                    }
                    });
            */
            /*      list.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                    closeDlg(true);
                    }
                    }
                    });
                    [PENDING - doubleclick]
            */
            setLayout(new BorderLayout(5, 5));
            add(new JLabel(bundle.getString("CTL_PaletteCategories")),
                BorderLayout.NORTH);
            add(new JScrollPane(list), BorderLayout.CENTER);
            setBorder(new EmptyBorder(5, 5, 5, 5));
        }

        public String getSelectedCategory() {
            return(String) list.getSelectedValue();
        }

        public Dimension getPreferredSize() {
            Dimension ret = super.getPreferredSize();
            ret.width = Math.max(ret.width, 350);
            ret.height = Math.max(ret.height, 250);
            return ret;
        }
    }

    private static class BeanSelector extends JPanel
    {
        static final long serialVersionUID = -6038414545631774041L;
        
        private JList list;
        
        /**
         * creates a new ExceptionBox for given exception descriptor.
         */
        
        public BeanSelector(Collection beans) {
            list = new JList(beans.toArray());
            list.setCellRenderer(new FileObjectRenderer());

            /*      list.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                    closeDlg(true);
                    }
                    }
                    });
                    [PENDING - doubleclick]
            */
            // [PENDING - OK/Cancel enabled accorning to selection
            
            setBorder(new EmptyBorder(5, 5, 5, 5));
            setLayout(new BorderLayout(5, 5));
            add(new JLabel(bundle.getString("CTL_SelectBeans")), BorderLayout.NORTH);
            add(new JScrollPane(list), BorderLayout.CENTER); // NOI18N
        }

        Collection getSelectedBeans() {
            Object[] sel = list.getSelectedValues();
            ArrayList al = new ArrayList(sel.length);
            for (int i = 0; i < sel.length; i++) {
                al.add(sel[i]);
            }
            return al;
        }

        public Dimension getPreferredSize() {
            Dimension ret = super.getPreferredSize();
            ret.width = Math.max(ret.width, 350);
            ret.height = Math.max(ret.height, 250);
            return ret;
        }
    }

    private static class FileObjectRenderer extends JLabel implements ListCellRenderer
    {
        static final long serialVersionUID = 832555965217675765L;

        private static final Border hasFocusBorder =
            new LineBorder(UIManager.getColor("List.focusCellHighlight")); // NOI18N
        private static final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
        
        /** Creates a new NetbeansListCellRenderer */
        public FileObjectRenderer() {
            setOpaque(true);
            setBorder(noFocusBorder);
        }

        public Component getListCellRendererComponent(JList list,
                                                      Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            if (!(value instanceof FileObject))
                return this;

            FileObject fo =(FileObject) value;

            setText(fo.getName());
            
            if (isSelected){
                setBackground(UIManager.getColor("List.selectionBackground")); // NOI18N
                setForeground(UIManager.getColor("List.selectionForeground")); // NOI18N
            }
            else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setBorder(cellHasFocus ? hasFocusBorder : noFocusBorder);
            return this;
        }
    }
}
