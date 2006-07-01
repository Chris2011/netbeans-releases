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

package org.openide.filesystems;

import java.beans.PropertyVetoException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.Reference;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple implementation of memory file system.
 * @author Jaroslav Tulach
 */
final class MemoryFileSystem extends AbstractFileSystem implements AbstractFileSystem.Info, AbstractFileSystem.Change, AbstractFileSystem.List, AbstractFileSystem.Attr {
    private static final Logger ERR = Logger.getLogger(MemoryFileSystem.class.getName());
    
    /** time when the filesystem was created. It is supposed to be the default
     * time of modification for all resources that has not been modified yet
     */
    private java.util.Date created = new java.util.Date();

    /** maps String to Entry */
    private Map<String, Entry> entries = initEntry();
    
    @SuppressWarnings("deprecation") // need to set it for compat
    private void _setSystemName(String s) throws PropertyVetoException {
        setSystemName(s);
    }

    /** Creates new MemoryFS */
    public MemoryFileSystem() {
        attr = this;
        list = this;
        change = this;
        info = this;

        
        try {
            _setSystemName("MemoryFileSystem" + String.valueOf(System.identityHashCode(this)));
        } catch (PropertyVetoException ex) {
            ex.printStackTrace();
        }
    }

    /** Creates MemoryFS with data */
    public MemoryFileSystem(String[] resources) {
        this();

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < resources.length; i++) {
            sb.append(resources[i]);

            if (resources[i].endsWith("/")) {
                // folder
                getOrCreateEntry(resources[i]).data = null;
            } else {
                getOrCreateEntry(resources[i]).data = new byte[0];
            }
        }
    }

    /** finds entry for given name */
    private Entry getOrCreateEntry(String n) {
        if ((n.length() > 0) && (n.charAt(0) == '/')) {
            n = n.substring(1);
        }

        boolean isValidEntry = isValidEntry(n);
        synchronized(entries) {
            Entry x = (Entry) entries.get(n);

            if (x == null || !isValidEntry) {
                x = new Entry(n);
                entries.put(n, x);
            }
        
            return x;
        }
    }

	

    private boolean isValidEntry(String n) {
	return isValidEntry(n, null);
    }
    
    /** finds whether there already is this name */
    private boolean isValidEntry(String n, Boolean expectedResult) {
        boolean retval = false;
        
        if ((n.length() > 0) && (n.charAt(0) == '/')) {
            n = n.substring(1);
        }

        Entry x = (Entry) entries.get(n);
	FileObject fo = null;
        
        if (x != null) {
            Reference ref = findReference(n);
            if (ref != null) {
                fo = (FileObject)ref.get();
                retval = (fo != null) ? fo.isValid() : true;
            }   
        }

	if (ERR.isLoggable(Level.FINE) && expectedResult != null && retval != expectedResult.booleanValue()) {
	    logMessage("entry: " + x +  " isValidReference.fo: " + ((fo == null) ? "null" : //NOI18N
		(fo.isValid() ? "valid" : "invalid")));//NOI18N
	}
	
        return (retval);
    }

    public String getDisplayName() {
        return "MemoryFileSystem";
    }

    public boolean isReadOnly() {
        return false;
    }

    public Enumeration<String> attributes(String name) {
        if (!isValidEntry(name)) {
            return org.openide.util.Enumerations.empty();
        }
        return Collections.enumeration(getOrCreateEntry(name).attrs.keySet());
    }

    public String[] children(String f) {
        if ((f.length() > 0) && (f.charAt(0) == '/')) {
            f = f.substring(1);
        }

        if ((f.length() > 0) && !f.endsWith("/")) {
            f = f + "/";
        }

        Set<String> l = new HashSet<String>();

        //System.out.println("Folder: " + f);
        synchronized(entries) {
            Iterator it = entries.keySet().iterator();

            while (it.hasNext()) {
                String name = (String) it.next();

                if (name.startsWith(f) || (f.trim().length() == 0)) {
                    int i = name.indexOf('/', f.length());
                    String child = null;

                    if (i > 0) {
                        child = name.substring(f.length(), i);
                    } else {
                        child = name.substring(f.length());
                    }

                    if (child.trim().length() > 0) {
                        l.add(child);
                    }
                }
            }

            return (String[]) l.toArray(new String[0]);
        }
    }

    public void createData(String name) throws IOException {
        if (isValidEntry(name, Boolean.FALSE)) {
	    StringBuffer message = new StringBuffer();
	    message.append("File already exists: ").append(name);
            throw new IOException(message.toString());//NOI18N
        }

        getOrCreateEntry(name).data = new byte[0];
    }

    public void createFolder(String name) throws java.io.IOException {
        if (isValidEntry(name, Boolean.FALSE)) {
	    StringBuffer message = new StringBuffer();
	    message.append("Folder already exists: ").append(name);
            throw new IOException(message.toString());//NOI18N
        }

        getOrCreateEntry(name).data = null;
    }

    public void delete(String name) throws IOException {
        if (entries.remove(name) == null) {
            throw new IOException("No file to delete: " + name); // NOI18N
        }
    }

    public void deleteAttributes(String name) {
    }

    public boolean folder(String name) {
        return getOrCreateEntry(name).data == null;
    }

    public InputStream inputStream(String name) throws java.io.FileNotFoundException {
        byte[] arr = getOrCreateEntry(name).data;

        if (arr == null) {
            arr = new byte[0];
        }

        return new ByteArrayInputStream(arr);
    }

    public java.util.Date lastModified(String name) {
        java.util.Date d = getOrCreateEntry(name).last;

        return (d == null) ? created : d;
    }

    public void lock(String name) throws IOException {
    }

    public void markUnimportant(String name) {
    }

    public String mimeType(String name) {
        return (String) getOrCreateEntry(name).attrs.get("mimeType");
    }

    public OutputStream outputStream(final String name)
    throws java.io.IOException {
        class Out extends ByteArrayOutputStream {
            public void close() throws IOException {
                super.close();

                getOrCreateEntry(name).data = toByteArray();
                getOrCreateEntry(name).last = new Date();
            }
        }

        return new Out();
    }

    public Object readAttribute(String name, String attrName) {
        return isValidEntry(name) ? getOrCreateEntry(name).attrs.get(attrName) : null;
    }

    public boolean readOnly(String name) {
        return false;
    }

    public void rename(String oldName, String newName)
    throws IOException {
        if (!isValidEntry(oldName)) {
            throw new IOException("The file to rename does not exist.");
        }

        if (isValidEntry(newName)) {
            throw new IOException("Cannot rename to existing file");
        }

        if ((newName.length() > 0) && (newName.charAt(0) == '/')) {
            newName = newName.substring(1);
        }

        Entry e = getOrCreateEntry(oldName);
        entries.remove(oldName);
        entries.put(newName, e);
    }

    public void renameAttributes(String oldName, String newName) {
    }

    public long size(String name) {
        byte[] d = getOrCreateEntry(name).data;

        return (d == null) ? 0 : d.length;
    }

    public void unlock(String name) {
    }

    public void writeAttribute(String name, String attrName, Object value)
    throws IOException {
        getOrCreateEntry(name).attrs.put(attrName, value);
    }

    private Map<String, Entry> initEntry() {
        if (!ERR.isLoggable(Level.FINE)) {
            return new ConcurrentHashMap<String, MemoryFileSystem.Entry>();
        }

	return new ConcurrentHashMap<String, MemoryFileSystem.Entry>() {
	    public MemoryFileSystem.Entry get(String key) {
		MemoryFileSystem.Entry retval = super.get(key);
		logMessage("called: GET" + " key: "+key + " result: " + retval);//NOI18N    		
		return retval;
	    }

	    public MemoryFileSystem.Entry put(String key, MemoryFileSystem.Entry value) {
		MemoryFileSystem.Entry retval = super.put(key, value);
		logMessage("called: PUT" + " key: "+key  + " value: "+value+ " result: " + retval);//NOI18N		
		return retval;            
	    }        

	    public MemoryFileSystem.Entry remove(String key) {
		MemoryFileSystem.Entry retval = super.remove(key);
		logMessage("called: REMOVE" + " key: "+key + " result: " + retval);//NOI18N		
		return retval;
	    }
	};
    }
    
    static final class Entry {
        /** String, Object */
        public Map<String, Object> attrs = new HashMap<String, Object>();
        public byte[] data;
        public java.util.Date last;
	private final String entryName;

	Entry(String entryName) {
	    this.entryName = entryName;
	}
	

	public String toString() {
	    StringBuffer sb = new StringBuffer();
	    sb.append(" [").append(entryName);//NOI18N
	    sb.append(" -> ").append(super.toString());//NOI18N
	    sb.append("] ");
	    return sb.toString();
	}
    }
    
    
    private static void logMessage(final String message) {
        StringBuffer sb = new StringBuffer();
        sb.append(" -> ").append(message);

        //ucomment if necessary
        /*ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(bos);
        new Exception().printStackTrace(pw);
        pw.close();
        sb.append(bos.toString());
         */
        ERR.fine(sb.toString());
    }    
    
}
