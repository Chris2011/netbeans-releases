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

package org.netbeans.modules.masterfs.providers;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import javax.swing.Action;
import org.netbeans.junit.NbTestCase;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Radek Matous
 */
public class InterceptionListenerTest extends NbTestCase  {
    private InterceptionListenerImpl iListener;
    protected void setUp() throws Exception {
        super.setUp();
        iListener = lookupImpl();
        assertNotNull(iListener);
        iListener.clear();
        clearWorkDir();
    }

    private InterceptionListenerImpl lookupImpl() {
        Lookup.Result result = Lookups.metaInfServices(Thread.currentThread().getContextClassLoader()).
                lookup(new Lookup.Template(AnnotationProvider.class));
        Collection all = result.allInstances();
        for (Iterator it = all.iterator(); it.hasNext();) {
            AnnotationProvider ap = (AnnotationProvider) it.next();
            InterceptionListener iil = ap.getInterceptionListener();
            if (iil != null && !(iil instanceof ProvidedExtensions)) {
                return (InterceptionListenerImpl)iil;
            }            
        }
        return null;
    }
    
    public InterceptionListenerTest(String testName) {
        super(testName);
    }
    

    public void testIssue71724() throws IOException {
        FileObject fo = FileUtil.toFileObject(getWorkDir());
        assertNotNull(fo);
        assertNotNull(iListener);
        assertEquals(0,iListener.beforeCreateCalls);
        assertEquals(0,iListener.createSuccessCalls);
        
        fo.addFileChangeListener(new FileChangeAdapter() {
            public void fileDataCreated(FileEvent fe) {
                super.fileDataCreated(fe);
                throw new RuntimeException();
            }
            
        });
        try {
            assertNotNull(fo.createData(getName()));
        } catch (RuntimeException ex) {}
    }
    
    public void testBeforeCreate() throws IOException {
        FileObject fo = FileUtil.toFileObject(getWorkDir());
        assertNotNull(fo);
        assertNotNull(iListener);
        assertEquals(0,iListener.beforeCreateCalls);
        assertEquals(0,iListener.createSuccessCalls);
        
        assertNotNull(fo.createData("aa"));
        assertEquals(1,iListener.beforeCreateCalls);
        assertEquals(1,iListener.createSuccessCalls);
        
        iListener.clear();
        try {
            assertEquals(0,iListener.createSuccessCalls);
            assertEquals(0,iListener.createFailureCalls);
            assertNotNull(fo.createData("aa"));
            fail();
        } catch (IOException ex) {
            assertEquals(0,iListener.createSuccessCalls);
            assertEquals(1,iListener.createFailureCalls);
        }
    }
    
    public void testBeforeDelete() throws IOException {
        FileObject fo = FileUtil.toFileObject(getWorkDir());
        assertNotNull(fo);
        FileObject toDel = fo.createData("aa");
        assertNotNull(toDel);
        iListener.clear();
        
        assertNotNull(iListener);
        assertEquals(0,iListener.beforeDeleteCalls);
        assertEquals(0,iListener.deleteSuccessCalls);
        toDel.delete();
        assertFalse(toDel.isValid());
        assertEquals(1,iListener.beforeDeleteCalls);
        assertEquals(1,iListener.deleteSuccessCalls);
        
        iListener.clear();
        try {
            assertEquals(0,iListener.deleteSuccessCalls);
            assertEquals(0,iListener.deleteFailureCalls);
            toDel.delete();
            fail();
        } catch (IOException ex) {
            assertEquals(0,iListener.deleteSuccessCalls);
            assertEquals(1,iListener.deleteFailureCalls);
        }
    }
    
    public static class AnnotationProviderImpl extends AnnotationProvider  {
        private InterceptionListenerImpl impl = new InterceptionListenerImpl();
        public String annotateName(String name, java.util.Set files) {
            java.lang.StringBuffer sb = new StringBuffer(name);
            Iterator it = files.iterator();
            while (it.hasNext()) {
                FileObject fo = (FileObject)it.next();
                try {
                    sb.append("," +fo.getNameExt());//NOI18N
                } catch (Exception ex) {
                    fail();
                }
            }
            
            return sb.toString() ;
        }
        
        public java.awt.Image annotateIcon(java.awt.Image icon, int iconType, java.util.Set files) {
            return icon;
        }
        
        public String annotateNameHtml(String name, Set files) {
            return annotateName(name, files);
        }
        
        public Action[] actions(Set files) {
            return new Action[]{};
        }
        
        public InterceptionListener getInterceptionListener() {
            return impl;
        }
    }
    
    public static class InterceptionListenerImpl implements InterceptionListener {
        private int beforeCreateCalls = 0;
        private int createFailureCalls = 0;
        private int createSuccessCalls = 0;
        private int beforeDeleteCalls = 0;
        private int deleteSuccessCalls = 0;
        private int deleteFailureCalls = 0;
        
        public void clear() {
            beforeCreateCalls = 0;
            createFailureCalls = 0;
            createSuccessCalls = 0;
            beforeDeleteCalls = 0;
            deleteSuccessCalls = 0;
            deleteFailureCalls = 0;
        }
        
        public void beforeCreate(org.openide.filesystems.FileObject parent, java.lang.String name, boolean isFolder) {
            beforeCreateCalls++;
        }
        
        public void createSuccess(org.openide.filesystems.FileObject fo) {
            assertNotNull(fo);
            createSuccessCalls++;
        }
        
        public void createFailure(org.openide.filesystems.FileObject parent, java.lang.String name, boolean isFolder) {
            createFailureCalls++;
        }
        
        public void beforeDelete(org.openide.filesystems.FileObject fo) {
            beforeDeleteCalls++;
        }
        
        public void deleteSuccess(org.openide.filesystems.FileObject fo) {
            deleteSuccessCalls++;
        }
        
        public void deleteFailure(org.openide.filesystems.FileObject fo) {
            deleteFailureCalls++;
        }
    }
}
