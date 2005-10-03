/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.openide.loaders;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Enumeration;
import java.util.List;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import org.netbeans.junit.NbTestCase;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.util.Enumerations;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;


/** To simulate and fix 65543.
 * <pre>
 "Folder recognizer" daemon prio=1 tid=0x08297dd0 nid=0x380b in Object.wait() [aa46e000..aa46f238]
 at java.lang.Object.wait(Native Method)
 - waiting on <0xabdb6e50> (a org.openide.loaders.DataObjectPool)
 at java.lang.Object.wait(Object.java:429)
 at org.openide.loaders.DataObjectPool.waitNotified(DataObjectPool.java:470)
 - locked <0xabdb6e50> (a org.openide.loaders.DataObjectPool)
 at org.openide.loaders.DataObjectExistsException.getDataObject(DataObjectExistsException.java:43)
 at org.openide.loaders.MultiFileLoader.handleFindDataObject(MultiFileLoader.java:83)
 at org.openide.loaders.DataObjectPool.handleFindDataObject(DataObjectPool.java:111)
 at org.openide.loaders.DataLoader.findDataObject(DataLoader.java:362)
 at org.openide.loaders.DataLoaderPool.findDataObject(DataLoaderPool.java:402)
 at org.openide.loaders.FolderList.createBoth(FolderList.java:701)
 at org.openide.loaders.FolderList.getObjects(FolderList.java:512)
 at org.openide.loaders.FolderList.access$200(FolderList.java:50)
 at org.openide.loaders.FolderList$ListTask.run(FolderList.java:880)
 at org.openide.util.Task.run(Task.java:207)
 at org.openide.util.RequestProcessor$Task.run(RequestProcessor.java:469)
 at org.openide.util.RequestProcessor$Processor.run(RequestProcessor.java:870)
 
 "main" prio=1 tid=0x0805cbc0 nid=0x380b in Object.wait() [bfffb000..bfffca5c]
 at java.lang.Object.wait(Native Method)
 - waiting on <0xab8a2530> (a org.openide.util.RequestProcessor$Task)
 at java.lang.Object.wait(Object.java:429)
 at org.openide.util.Task.waitFinished(Task.java:99)
 - locked <0xab8a2530> (a org.openide.util.RequestProcessor$Task)
 at org.openide.util.RequestProcessor$Task.waitFinished(RequestProcessor.java:629)
 at org.openide.loaders.FolderInstance.waitFinished(FolderInstance.java:285)
 at org.openide.loaders.FolderInstance.instanceFinished(FolderInstance.java:265)
 at org.openide.loaders.FolderLookup$ProxyLkp.beforeLookup(FolderLookup.java:355)
 at org.openide.util.lookup.ProxyLookup.lookup(ProxyLookup.java:175)
 at org.openide.loaders.CanYouQueryFolderLookupFromHandleFindTest$MyLoader.findPrimaryFile(CanYouQueryFolderLookupFromHandleFindTest.java:140)
 at org.openide.loaders.MultiFileLoader.findPrimaryFileImpl(MultiFileLoader.java:262)
 at org.openide.loaders.MultiFileLoader.handleFindDataObject(MultiFileLoader.java:65)
 at org.openide.loaders.DataObjectPool.handleFindDataObject(DataObjectPool.java:111)
 at org.openide.loaders.DataLoader.findDataObject(DataLoader.java:362)
 at org.openide.loaders.DataLoaderPool.findDataObject(DataLoaderPool.java:402)
 at org.openide.loaders.DataLoaderPool.findDataObject(DataLoaderPool.java:362)
 at org.openide.loaders.DataObject.find(DataObject.java:459)
 at org.openide.loaders.CanYouQueryFolderLookupFromHandleFindTest.testTheDeadlock(CanYouQueryFolderLookupFromHandleFindTest.java:58)
 at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
 at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
 at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
 at java.lang.reflect.Method.invoke(Method.java:324)
 at junit.framework.TestCase.runTest(TestCase.java:154)
 at org.netbeans.junit.NbTestCase.runBare(NbTestCase.java:135)
 at junit.framework.TestResult$1.protect(TestResult.java:106)
 at junit.framework.TestResult.runProtected(TestResult.java:124)
 at junit.framework.TestResult.run(TestResult.java:109)
 at junit.framework.TestCase.run(TestCase.java:118)
 at org.netbeans.junit.NbTestCase.run(NbTestCase.java:122)
 at junit.framework.TestSuite.runTest(TestSuite.java:208)
 at junit.framework.TestSuite.run(TestSuite.java:203)
 at org.apache.tools.ant.taskdefs.optional.junit.JUnitTestRunner.run(JUnitTestRunner.java:297)
 at org.apache.tools.ant.taskdefs.optional.junit.JUnitTestRunner.launch(JUnitTestRunner.java:672)
 at org.apache.tools.ant.taskdefs.optional.junit.JUnitTestRunner.main(JUnitTestRunner.java:567)
 </pre>
 *
 * @author Jaroslav Tulach
 */
public class CanYouQueryFolderLookupFromHandleFindTest extends NbTestCase {
    
    /** Creates a new instance of CanYouQueryFolderLookupFromHandleFindTest */
    public CanYouQueryFolderLookupFromHandleFindTest(String s) {
        super(s);
    }
    
    protected void setUp() {
        System.setProperty("org.openide.util.Lookup", Lkp.class.getName());
        assertEquals("Lookup registered", Lkp.class, Lookup.getDefault().getClass());
    }
    
    public void testTheDeadlock() throws Exception {
        MyLoader m = (MyLoader)MyLoader.getLoader(MyLoader.class);
        m.button = FileUtil.createFolder(Repository.getDefault().getDefaultFileSystem().getRoot(), "FolderLookup");
        DataObject instance = InstanceDataObject.create(DataFolder.findFolder(m.button), "SomeName", JButton.class);
        m.instanceFile = instance.getPrimaryFile();
        
        WeakReference ref = new WeakReference(instance);
        instance = null;
        assertGC("Object must disappear first", ref);
        
        DataLoaderPool.setPreferredLoader(m.instanceFile, m);
        
        FileObject any = Repository.getDefault().getDefaultFileSystem().getRoot().createData("Ahoj.txt");
        DataObject obj = DataObject.find(any);
        
        assertEquals("The right object found", m, obj.getLoader());
        assertEquals("Null value then", null, m.v);
        assertNotNull("Lookup created", m.lookup);
        Object v = m.lookup.lookup(JButton.class);
        assertNotNull("Now the value can be found", v);
        
        
        instance = DataObject.find(m.instanceFile);
        InstanceCookie ic = (InstanceCookie)instance.getCookie(InstanceCookie.class);
        assertNotNull("InstanceCookie is there", ic);
        assertEquals("Is the same as from instance", v, ic.instanceCreate());
        
        if (DataFolderIndexTest.ErrManager.messages.indexOf("Preventing deadlock") == -1) {
            fail("There should be a warning in the log: " + DataFolderIndexTest.ErrManager.messages);
        }
        if (DataFolderIndexTest.ErrManager.messages.indexOf("65543") == -1) {
            fail("There should be a warning in the log: " + DataFolderIndexTest.ErrManager.messages);
        }
    }
    
    
    public static final class Lkp extends AbstractLookup {
        public Lkp() {
            this(new InstanceContent());
        }
        
        private Lkp(InstanceContent ic) {
            super(ic);
            ic.add(new Pool());
            ic.add(new DataFolderIndexTest.ErrManager ());
        }
    }
    
    public static final class MyLoader extends UniFileLoader implements Runnable {
        public FileObject button;
        public Object v;
        public Lookup lookup;
        
        public InstanceDataObject created;
        
        private FileObject instanceFile;
        
        private DataObject middleCreation;
        
        public MyLoader() throws IOException {
            super("org.openide.loaders.MultiDataObject");
        }
        
        protected FileObject findPrimaryFile(FileObject fo) {
            if (fo.hasExt("instance") && !FolderList.isFolderRecognizerThread()) {
                // this is the trick - it will cause DataObjectExistsException to be thrown later
                try {
                    created = new InstanceDataObject(fo, this);
                    synchronized (this) {
                        try {
                            notifyAll();
                            wait();
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                } catch (DataObjectExistsException ex) {
                    ex.printStackTrace();
                    fail("Now exception now");
                }
                
                // we do not recognize it
                return null;
            }
            if (!fo.hasExt("txt")) {
                return null;
            }
            
            assertNull("First invocation", lookup);
            
            org.openide.util.RequestProcessor.Task t;
            synchronized (this) {
                t = org.openide.util.RequestProcessor.getDefault().post(this);
                try {
                    wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                    fail("No exceptions please");
                }
            }
            
            FolderLookup l = new FolderLookup(DataFolder.findFolder(button));
            lookup = l.getLookup();
            v = lookup.lookup(JButton.class);
            assertEquals("We cannot create the instance currently", null, v);
            
            synchronized (this) {
                notifyAll();
            }
            // wait till the other task finishes
            t.waitFinished();
            
            return fo;
        }
        
        protected MultiDataObject createMultiObject(FileObject primaryFile) throws DataObjectExistsException, IOException {
            return new MultiDataObject(primaryFile, this);
        }
        
        public void run() {
            try {
                middleCreation = DataObject.find(instanceFile);
            } catch (DataObjectNotFoundException ex) {
                fail("Exception: " + ex.getMessage());
            }
        }
    }
    
    private static final class Pool extends DataLoaderPool {
        static List loaders;
        
        public Pool() {
        }
        
        public Enumeration loaders() {
            return Enumerations.singleton(DataLoader.getLoader(MyLoader.class));
        }
    }
    
}
