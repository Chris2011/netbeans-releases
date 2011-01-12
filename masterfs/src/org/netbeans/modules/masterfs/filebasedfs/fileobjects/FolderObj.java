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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.masterfs.filebasedfs.fileobjects;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SyncFailedException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.netbeans.modules.masterfs.filebasedfs.FileBasedFileSystem;
import org.netbeans.modules.masterfs.filebasedfs.FileBasedFileSystem.FSCallable;
import org.netbeans.modules.masterfs.filebasedfs.children.ChildrenCache;
import org.netbeans.modules.masterfs.filebasedfs.children.ChildrenSupport;
import org.netbeans.modules.masterfs.filebasedfs.naming.FileName;
import org.netbeans.modules.masterfs.filebasedfs.naming.FileNaming;
import org.netbeans.modules.masterfs.filebasedfs.naming.NamingFactory;
import org.netbeans.modules.masterfs.filebasedfs.utils.FSException;
import org.netbeans.modules.masterfs.filebasedfs.utils.FileChangedManager;
import org.netbeans.modules.masterfs.filebasedfs.utils.FileInfo;
import org.netbeans.modules.masterfs.providers.ProvidedExtensions;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Mutex;

/**
 * @author rm111737
 */
public final class FolderObj extends BaseFileObj {    
    static final long serialVersionUID = -1022430210876356809L;
    private static final Logger LOG = Logger.getLogger(FolderObj.class.getName());

    private FolderChildrenCache folderChildren;
    boolean valid = true;
    private volatile FileObjectKeeper keeper;

    /**
     * Creates a new instance of FolderImpl
     */
    public FolderObj(final File file, final FileNaming name) {
        super(file, name);
        //valid = true;
    }

    public final boolean isFolder() {
        return true;
    }

    @Override
    public FileObject getFileObject(String relativePath) {
        if(relativePath.indexOf('\\') != -1) {
            // #47885 - relative path must not contain back slashes
            return null;
        }
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }
        File file = new File(getFileName().getFile(), relativePath);
        FileObjectFactory factory = getFactory();
        return factory.getValidFileObject(file, FileObjectFactory.Caller.GetFileObject);
    }


    public final FileObject getFileObject(final String name, final String ext) {
        File file = BaseFileObj.getFile(getFileName().getFile(), name, ext);
        FileObjectFactory factory = getFactory();
        return (name.indexOf("/") == -1) ? factory.getValidFileObject(file, FileObjectFactory.Caller.GetFileObject) : null;
    }

  
    @Override
    public final FileObject[] getChildren() {
        final Map<FileNaming,FileObject> results = new LinkedHashMap<FileNaming,FileObject>();

        final ChildrenCache childrenCache = getChildrenCache();
        final Mutex.Privileged mutexPrivileged = childrenCache.getMutexPrivileged();

        mutexPrivileged.enterWriteAccess();

        Set<FileNaming> fileNames;
        try {
            fileNames = new HashSet<FileNaming>(childrenCache.getChildren(false));
        } finally {
            mutexPrivileged.exitWriteAccess();
        }

        final FileObjectFactory lfs = getFactory();        
        for (FileNaming fileName : fileNames) {
            FileInfo fInfo = new FileInfo (fileName.getFile(), 1);
            fInfo.setFileNaming(fileName);
            
            final FileObject fo = lfs.getFileObject(fInfo, FileObjectFactory.Caller.GetChildern);
            if (fo != null) {
                assert fileName == ((BaseFileObj)fo).getFileName() : 
                    dumpFileNaming(fileName) + "\n" + 
                    dumpFileNaming(((BaseFileObj)fo).getFileName()) + "\nfo: " +
                    fo + "\nContent of the nameMap cache:\n" +
                    NamingFactory.dumpId(fInfo.getID());
                results.put(fileName, fo);
            }
        }
        return results.values().toArray(new FileObject[0]);
    }
    
    private static String dumpFileNaming(FileNaming fn) {
        if (fn == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("FileName: ").append(fn).append("#").
           append(Integer.toHexString(fn.hashCode())).append("@").
           append(Integer.toHexString(System.identityHashCode(fn)))
           .append("\n");
        
        if (fn instanceof FileName) {
            ((FileName)fn).dumpCreation(sb);
        }
        return sb.toString();
    }

    public final FileObject createFolderImpl(final String name) throws java.io.IOException {
        if (name.indexOf('\\') != -1 || name.indexOf('/') != -1) {//NOI18N
            throw new IllegalArgumentException(name);
        }
        
        FolderObj retVal = null;
        File folder2Create;
        final ChildrenCache childrenCache = getChildrenCache();
        
        final Mutex.Privileged mutexPrivileged = childrenCache.getMutexPrivileged();

        mutexPrivileged.enterWriteAccess();

        try {
            final File myFile = getFileName().getFile();
            folder2Create = BaseFileObj.getFile( myFile, name, null);
            if (!myFile.canWrite()) {
                FSException.io("EXC_CannotCreateFolder", folder2Create.getName(), getPath());// NOI18N
            }
            createFolder(folder2Create, name);

            FileNaming childName = this.getChildrenCache().getChild(folder2Create.getName(), true);
            if (childName != null && !childName.isDirectory()) {
                childName = NamingFactory.fromFile(getFileName(), folder2Create, true);
            }            
            if (childName != null) {
                childName = NamingFactory.checkCaseSensitivity(childName, folder2Create);
            }
        } finally {
            mutexPrivileged.exitWriteAccess();
        }

        final FileObjectFactory factory = getFactory();
        if (factory != null) {
            BaseFileObj exists = factory.getValidFileObject(folder2Create, FileObjectFactory.Caller.Others);
            if (exists instanceof FolderObj) {
                retVal = (FolderObj)exists;
            } else {
                FSException.io("EXC_CannotCreateFolder", folder2Create.getName(), getPath());// NOI18N                           
            }
        }
        if (retVal != null) {
            retVal.fireFileFolderCreatedEvent(false);
        } else {
            FSException.io("EXC_CannotCreateFolder", folder2Create.getName(), getPath());// NOI18N                           
        }
        getProvidedExtensions().createSuccess(retVal);
        return retVal;
    }

    private void createFolder(final File folder2Create, final String name) throws IOException {
        boolean isSupported = new FileInfo(folder2Create).isSupportedFile();
        ProvidedExtensions extensions =  getProvidedExtensions();
        extensions.beforeCreate(this, folder2Create.getName(), true);

        if (!isSupported) { 
            extensions.createFailure(this, folder2Create.getName(), true);
            FSException.io("EXC_CannotCreateFolder", folder2Create.getName(), getPath());// NOI18N   
        } else if (FileChangedManager.getInstance().exists(folder2Create)) {
            extensions.createFailure(this, folder2Create.getName(), true);            
            throw new SyncFailedException(folder2Create.getAbsolutePath());// NOI18N               
        } else if (!folder2Create.mkdirs()) {
            extensions.createFailure(this, folder2Create.getName(), true);
            FSException.io("EXC_CannotCreateFolder", folder2Create.getName(), getPath());// NOI18N               
        }
        LogRecord r = new LogRecord(Level.FINEST, "FolderCreated: "+ folder2Create.getAbsolutePath());
        r.setParameters(new Object[] {folder2Create});
        Logger.getLogger("org.netbeans.modules.masterfs.filebasedfs.fileobjects.FolderObj").log(r);
    }

    public final FileObject createData(final String name, final String ext) throws java.io.IOException {
        FSCallable<FileObject> c = new FSCallable<FileObject>() {
            public FileObject call() throws IOException {
                return createDataImpl(name, ext);
            }
        };
        return FileBasedFileSystem.runAsInconsistent(c);
    }
    
    public final FileObject createFolder(final String name) throws java.io.IOException {
        FSCallable<FileObject> c = new FSCallable<FileObject>() {
            public FileObject call() throws IOException {
                return createFolderImpl(name);
            }
        };
        return FileBasedFileSystem.runAsInconsistent(c);        
    }
    
    
    public final FileObject createDataImpl(final String name, final String ext) throws java.io.IOException {
        if (name.indexOf('\\') != -1 || name.indexOf('/') != -1) {//NOI18N
            throw new IOException("Requested name contains invalid characters: " + name); // NOI18N
        }
        
        final ChildrenCache childrenCache = getChildrenCache();        
        final Mutex.Privileged mutexPrivileged = childrenCache.getMutexPrivileged();
        
        mutexPrivileged.enterWriteAccess();

        FileObj retVal;
        File file2Create;
        FileNaming childName;
        try {
            file2Create = BaseFileObj.getFile(getFileName().getFile(), name, ext);
            createData(file2Create);

            childName = getChildrenCache().getChild(file2Create.getName(), true);
            if (childName != null && childName.isDirectory()) {
                childName = NamingFactory.fromFile(getFileName(), file2Create, true);
            }
            if (childName != null) {
                childName = NamingFactory.checkCaseSensitivity(childName, file2Create);
            }

        } finally {
            mutexPrivileged.exitWriteAccess();
        }

        final FileObjectFactory factory = getFactory();
        retVal = null;
        if (factory != null) {
            final BaseFileObj fo = factory.getValidFileObject(file2Create, FileObjectFactory.Caller.Others);
            try {
                retVal = (FileObj) fo;
            } catch (ClassCastException ex) {
                boolean dir = file2Create.isDirectory();
                boolean file = file2Create.isFile();
                Exceptions.attachMessage(ex, "isDir: " + dir); // NOI18N
                Exceptions.attachMessage(ex, "isFile: " + file); // NOI18N
                Exceptions.attachMessage(ex, "file: " + file2Create); // NOI18N
                Exceptions.attachMessage(ex, "fo: " + fo); // NOI18N
                Exceptions.attachMessage(ex, "fn: " + Integer.toHexString(System.identityHashCode(childName))); // NOI18N
                Exceptions.attachMessage(ex, "dump: " + NamingFactory.dumpId(childName.getId())); // NOI18N
                throw ex;
            }
        }

        if (retVal != null) {            
            if (retVal instanceof FileObj) {
                retVal.setLastModified(file2Create.lastModified(), file2Create, false);
            }
            retVal.fireFileDataCreatedEvent(false);
        } else {
            FSException.io("EXC_CannotCreateData", file2Create.getName(), getPath());// NOI18N
        }
        getProvidedExtensions().createSuccess(retVal);
        return retVal;
    }

    private void createData(final File file2Create) throws IOException {
        boolean isSupported = new FileInfo(file2Create).isSupportedFile();                        
        ProvidedExtensions extensions =  getProvidedExtensions();
        extensions.beforeCreate(this, file2Create.getName(), false);
        
        if (!isSupported) {             
            extensions.createFailure(this, file2Create.getName(), false);
            FSException.io("EXC_CannotCreateData", file2Create.getName(), getPath());// NOI18N
        } else if (FileChangedManager.getInstance().exists(file2Create)) {
            extensions.createFailure(this, file2Create.getName(), false);
            throw new SyncFailedException(file2Create.getAbsolutePath());// NOI18N               
        } else if (!file2Create.createNewFile()) {
            extensions.createFailure(this, file2Create.getName(), false);            
            FSException.io("EXC_CannotCreateData", file2Create.getName(), getPath());// NOI18N
        }        
        LogRecord r = new LogRecord(Level.FINEST, "DataCreated: "+ file2Create.getAbsolutePath());
        r.setParameters(new Object[] {file2Create});
        Logger.getLogger("org.netbeans.modules.masterfs.filebasedfs.fileobjects.FolderObj").log(r);        
    }

    @Override
    public void delete(final FileLock lock, ProvidedExtensions.DeleteHandler deleteHandler) throws IOException {
        final LinkedList<FileObject> all = new LinkedList<FileObject>();

        final File file = getFileName().getFile();
        if (!deleteFile(file, all, getFactory(), deleteHandler)) {
            FileObject parent = getExistingParent();
            String parentPath = (parent != null) ? parent.getPath() : file.getParentFile().getAbsolutePath();
            FSException.io("EXC_CannotDelete", file.getName(), parentPath);// NOI18N            
        }

        BaseFileObj.attribs.deleteAttributes(file.getAbsolutePath().replace('\\', '/'));//NOI18N
        setValid(false);
        for (int i = 0; i < all.size(); i++) {
            final BaseFileObj toDel = (BaseFileObj) all.get(i);            
            final FolderObj existingParent = toDel.getExistingParent();            
            final ChildrenCache childrenCache = (existingParent != null) ? existingParent.getChildrenCache() : null;            
            if (childrenCache != null) {
                final Mutex.Privileged mutexPrivileged = (childrenCache != null) ? childrenCache.getMutexPrivileged() : null;
                if (mutexPrivileged != null) {
                    mutexPrivileged.enterWriteAccess();
                }
                try {      
                    if (deleteHandler != null) {
                        childrenCache.removeChild(toDel.getFileName());
                    } else {
                        childrenCache.getChild(BaseFileObj.getNameExt(file), true);
                    }
                    
                    
                } finally {
                    if (mutexPrivileged != null) {
                        mutexPrivileged.exitWriteAccess();
                    }
                }
            }                
            toDel.setValid(false);
            toDel.fireFileDeletedEvent(false);
        }        
    }

    @Override
    public void refreshImpl(final boolean expected, boolean fire) {
        final ChildrenCache cache = getChildrenCache();
        final Mutex.Privileged mutexPrivileged = cache.getMutexPrivileged();
        final long previous = keeper == null ? -1 : keeper.childrenLastModified();

        Set<FileNaming> oldChildren = null;
        Map<FileNaming, Integer> refreshResult = null;
        mutexPrivileged.enterWriteAccess();
        try {
            oldChildren = new HashSet<FileNaming>(cache.getCachedChildren());
            refreshResult = cache.refresh();
        } finally {
            mutexPrivileged.exitWriteAccess();
        }

        LOG.log(Level.FINER, "refreshImpl for {0} expected: {1} fire: {2} previous: {3}", new Object[]{this, expected, fire, previous});

        oldChildren.removeAll(refreshResult.keySet());
        for (final FileNaming child : oldChildren) {
            final BaseFileObj childObj = getFactory().getCachedOnly(child.getFile());
            if (childObj != null && childObj.isData()) {
                ((FileObj) childObj).refresh(expected);
            }
        }

        final FileObjectFactory factory = getFactory();
        for (final Map.Entry<FileNaming, Integer> entry : refreshResult.entrySet()) {
            final FileNaming child = entry.getKey();
            final Integer operationId = entry.getValue();

            BaseFileObj newChild = (operationId == ChildrenCache.ADDED_CHILD) ? factory.getFileObject(new FileInfo(child.getFile()), FileObjectFactory.Caller.Others) : factory.getCachedOnly(child.getFile());
            newChild = (BaseFileObj) ((newChild != null) ? newChild : getFileObject(child.getName()));
            if (operationId == ChildrenCache.ADDED_CHILD && newChild != null) {

                if (newChild.isFolder()) {
                    if (fire) {
                        getProvidedExtensions().createdExternally(newChild);
                        newChild.fireFileFolderCreatedEvent(expected);
                    }
                } else {
                    if (fire) {
                        getProvidedExtensions().createdExternally(newChild);
                        newChild.fireFileDataCreatedEvent(expected);
                    }
                }

            } else if (operationId == ChildrenCache.REMOVED_CHILD) {
                if (newChild != null) {
                    if (newChild.isValid()) {
                        newChild.setValid(false);
                        if (newChild instanceof FolderObj) {
                            getProvidedExtensions().deletedExternally(newChild);
                            ((FolderObj)newChild).refreshImpl(expected, fire);
                        } else {
                            if (fire) {
                                getProvidedExtensions().deletedExternally(newChild);
                                newChild.fireFileDeletedEvent(expected);
                            }
                        }
                    }
                } else {
                    //TODO: should be rechecked
                    //assert false;
                    final File f = child.getFile();
                    if (!(new FileInfo(f).isConvertibleToFileObject())) {
                        final BaseFileObj fakeInvalid;
                        if (child.isFile()) {
                            fakeInvalid = new FileObj(f, child);
                        } else {
                            fakeInvalid = new FolderObj(f, child);
                        }

                        fakeInvalid.setValid(false);
                        if (fire) {
                            fakeInvalid.fireFileDeletedEvent(expected);
                        }
                    }
                }

            } 

        }
        boolean validityFlag = FileChangedManager.getInstance().exists(getFileName().getFile());
        if (!validityFlag) {
            //fileobject is invalidated                
            setValid(false);
            if (fire) {
                fireFileDeletedEvent(expected);
            }
        }

        if (previous != -1) {
            assert keeper != null;
            keeper.init(previous, factory, expected);
        }
    }

    @Override
    public final void refresh(final boolean expected) {
        refresh(expected, true);
    }
    
    //TODO: rewrite partly and check FileLocks for existing FileObjects
    private boolean deleteFile(final File file, final LinkedList<FileObject> all, final FileObjectFactory factory, ProvidedExtensions.DeleteHandler deleteHandler) throws IOException {
        final boolean ret = (deleteHandler != null) ? deleteHandler.delete(file) : file.delete();

        if (ret) {
            final FileObject aliveFo = factory.getCachedOnly(file);
            if (aliveFo != null) {
                all.addFirst(aliveFo);
            }
            return true;
        }

        if (!FileChangedManager.getInstance().exists(file)) {
            return false;
        }

        if (file.isDirectory()) {
            // first of all delete whole content
            final File[] arr = file.listFiles();
            if (arr != null) {  // check for null in case of I/O errors
                for (int i = 0; i < arr.length; i++) {
                    final File f2Delete = arr[i];
                    if (!deleteFile(f2Delete, all, factory, deleteHandler)) {
                        return false;
                    }
                }
            }
        } 
        
        // delete the file itself
        //super.delete(lock());
        

        final boolean retVal = (deleteHandler != null) ? deleteHandler.delete(file) : file.delete();
        if (retVal) {
            final FileObject aliveFo = factory.getCachedOnly(file);
            if (aliveFo != null) {
                all.addFirst(aliveFo);
            }
        }


        return true;
    }

    protected void setValid(final boolean valid) {
        if (valid) {
            //I can't make valid fileobject when it was one invalidated
            assert isValid() : this.toString();
        } else {
            this.valid = false;
        }        
        
    }

    public boolean isValid() {
        //assert checkCacheState(valid, getFileName().getFile());        
        return valid;
    }

    public final InputStream getInputStream() throws FileNotFoundException {
        throw new FileNotFoundException(getPath());
    }

    public final OutputStream getOutputStream(final FileLock lock) throws IOException {
        throw new IOException(getPath());
    }


    public final FileLock lock() throws IOException {
        return new FileLock();
    }

    final boolean checkLock(final FileLock lock) throws IOException {
        return true;
    }

    public final ChildrenCache getChildrenCache() {
        synchronized (FolderChildrenCache.class) {
            if (folderChildren == null) {
                folderChildren = new FolderChildrenCache();
            }
            return folderChildren;
        }
    }
    
    public final boolean hasRecursiveListener() {
        FileObjectKeeper k = keeper;
        return k != null && k.isOn();
    }

    final synchronized FileObjectKeeper getKeeper(Collection<? super File> arr) {
        if (keeper == null) {
            keeper = new FileObjectKeeper(this);
            List<File> ch = keeper.init(-1, null, false);
            if (arr != null) {
                arr.addAll(ch);
            }
        } else if (arr != null) {
            List<File> ch = keeper.init(keeper.childrenLastModified(), null, false);
            arr.addAll(ch);
        }
        return keeper;
    }

    @Override
    public final void addRecursiveListener(FileChangeListener fcl) {
        getKeeper(null).addRecursiveListener(fcl);
    }

    @Override
    public final void removeRecursiveListener(FileChangeListener fcl) {
        getKeeper(null).removeRecursiveListener(fcl);
    }


    public final class FolderChildrenCache extends ChildrenSupport implements ChildrenCache {
        @Override
        public final Set<FileNaming> getChildren(final boolean rescan) {
            return getChildren(getFileName(), rescan);
        }

        @Override
        public final FileNaming getChild(final String childName, final boolean rescan) {
            return getChild(childName, getFileName(), rescan);
        }

        @Override
        public final Map<FileNaming, Integer> refresh() {
            return refresh(getFileName());
        }

        @Override
        public final String toString() {
            return getFileName().toString();
        }

        @Override
        public void removeChild(FileNaming childName) {
            removeChild(getFileName(), childName);
        }
    }

}
