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

package org.netbeans.core.xml;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.ref.Reference;
import java.util.WeakHashMap;
import java.beans.PropertyChangeListener;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.w3c.dom.DocumentType;


import org.openide.TopManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.*;
import org.openide.cookies.InstanceCookie;
import org.openide.util.Lookup;
import org.openide.util.lookup.*;
import org.openide.xml.EntityCatalog;
import org.openide.util.WeakListener;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileAttributeEvent;


/** 
 * Entity resolver which loads entities (typically DTDs) from fixed
 * locations in the system file system, according to public ID.
 * <p>
 * It expects that PUBLIC has at maximum three "//" parts 
 * (standard // vendor // entity name // language). It is basically
 * converted to <tt>"/xml/entities/{vendor}/{entity_name}"</tt> resource name.
 * <p>
 * It also attaches <tt>Environment</tt> according to registrations
 * at <tt>/xml/lookups/</tt> area. There can be registered:
 * <tt>Environment.Provider</tt> or deprecated <tt>XMLDataObject.Processor</tt>
 * and <tt>XMLDataObject.Info</tt> instances.
 * <p>
 * All above are core implementation features.
 *
 * @author  Jaroslav Tulach
 */
public class FileEntityResolver extends EntityCatalog implements Environment.Provider {
    private static final String ENTITY_PREFIX = "/xml/entities"; // NOI18N
    private static final String LOOKUP_PREFIX = "/xml/lookups"; // NOI18N
    
    /** Constructor
     */
    public FileEntityResolver() {
    }
    
    /** Tries to find the entity on system file system.
     */
    public InputSource resolveEntity(String publicID, String systemID) throws IOException, SAXException {
        if (publicID == null) {
            return null;
        }


        String id = convertPublicId (publicID);
        
        StringBuffer sb = new StringBuffer (200);
        sb.append (ENTITY_PREFIX);
        sb.append (id);
        
        FileObject fo = Repository.getDefault ().getDefaultFileSystem ().findResource (sb.toString ());
        if (fo != null) {
            return new InputSource (fo.getInputStream ());
        } else {
            return null;
        }
    }
    
    /** A method that tries to find the correct lookup for given XMLDataObject.
     * @return the lookup
     */
    public Lookup getEnvironment(DataObject obj) {
        if (obj instanceof XMLDataObject) {
            XMLDataObject xml = (XMLDataObject)obj;
            
            String id = null;
            try {
                DocumentType domDTD = xml.getDocument ().getDoctype ();
                if (domDTD != null) id = domDTD.getPublicId ();
            } catch (IOException ex) {
                TopManager.getDefault ().getErrorManager().notify (ex);
                return null;
            } catch (org.xml.sax.SAXException ex) {
                TopManager.getDefault ().getErrorManager().notify (ex);
                return null;
            }

            if (id == null) {
                return null;
            }
            
            id = convertPublicId (id);
            
            return new Lkp (id, xml);
        }
        return null;
    }
    
    /** A method that extracts a listener from data object.
     * 
     * @param obj the data object that we are looking for environment of
     * @param source the obj that provides the environment
     * @return lookup provided by the obj or null if none has been found
     */
    private static Lookup findLookup (XMLDataObject obj, DataObject source) {
        try {
            InstanceCookie cookie = (InstanceCookie)source.getCookie (InstanceCookie.class);
            
            if (cookie != null) {
                Object inst = cookie.instanceCreate ();
                if (inst instanceof Environment.Provider) {
                    return ((Environment.Provider)inst).getEnvironment (obj);
                }

                if (inst instanceof XMLDataObject.Processor) {
                    // convert provider
                    XMLDataObject.Info info = new XMLDataObject.Info ();
                    info.addProcessorClass (inst.getClass ());
                    inst = info;
                }

                if (inst instanceof XMLDataObject.Info) {
                    return createInfoLookup (obj, ((XMLDataObject.Info)inst));
                }

            }
        } catch (IOException ex) {
            TopManager.getDefault ().getErrorManager ().notify (ex);
        } catch (ClassNotFoundException ex) {
            TopManager.getDefault ().getErrorManager ().notify (ex);
        }
        
        return null;
    }
        
    
    /** Ugly hack to get to openide hidden functionality.
     */
    private static java.lang.reflect.Method method;
    private static Lookup createInfoLookup (XMLDataObject obj, XMLDataObject.Info info) {
        // well, it is a wormhole, but just for default compatibility
        if (method == null) {
            try {
                method = XMLDataObject.class.getDeclaredMethod ("createInfoLookup", new Class[] { // NOI18N
                    XMLDataObject.class,
                    XMLDataObject.Info.class
                });
                method.setAccessible (true);
            } catch (Exception ex) {
                TopManager.getDefault ().getErrorManager ().notify (ex);
                return null;
            }
        }
        try {
            return (Lookup)method.invoke (null, new Object[] { obj, info });
        } catch (Exception ex) {
            TopManager.getDefault ().getErrorManager ().notify (ex);
            return null;
        }
    }

    /** Converts the publicID into filesystem friendly name.
     * <p>
     * It expects that PUBLIC has at maximum three "//" parts 
     * (standard // vendor // entity name // language). It is basically
     * converted to "vendor/entity_name" resource name.
     *
     * @see EntityCatalog
     */
    private static String convertPublicId (String publicID) {
        char[] arr = publicID.toCharArray ();


        int numberofslashes = 0;
        int state = 0;
        int write = 0;
        OUT: for (int i = 0; i < arr.length; i++) {
            char ch = arr[i];

            switch (state) {
            case 0:
                // initial state 
                if (ch == '+' || ch == '-' || ch == 'I' || ch == 'S' || ch == 'O') {
                    // do not write that char
                    continue;
                }
                // switch to regular state
                state = 1;
                // fallthru
            case 1:
                // regular state expecting any character
                if (ch == '/') {
                    state = 2;
                    if (++numberofslashes == 3) {
                        // last part of the ID, exit
                        break OUT;
                    }
                    arr[write++] = '/';
                    continue;
                }
                break;
            case 2:
                // previous character was /
                if (ch == '/') {
                    // ignore second / and write nothing
                    continue;
                }
                state = 1;
                break;
            }

            // write the char into the array
            if (ch >= 'A' && ch <= 'Z' || ch >= 'a' && ch <= 'z' || ch >= '0' && ch <= '9') {
                arr[write++] = ch;
            } else {
                arr[write++] = '_';
            }
        }

        return new String (arr, 0, write);
    }
        
    
    /** Finds a fileobject for given ID.
     * @param id string id
     * @param last[0] will be filled with last file object we should listen on
     * @return file object that should represent it
     */
    private static FileObject findObject (String id, FileObject[] last) {
        StringBuffer sb = new StringBuffer (200);
        sb.append (LOOKUP_PREFIX);
        sb.append (id);
        int len = sb.length ();
        // at least for now
        sb.append (".instance"); // NOI18N 

        FileObject root = Repository.getDefault ().getDefaultFileSystem ().getRoot ();
     
        String toSearch1 = sb.toString ();
        int indx = searchFolder (root, toSearch1, last);
        if (indx == -1) {
            // not possible to find folders
            return null;
        }

        FileObject fo = last[0].getFileObject (toSearch1.substring (indx));
        
        if (fo == null) {
            // try to find a file with xml extension
            sb.setLength (len);
            sb.append (".xml"); // NOI18N
            
            fo = last[0].getFileObject (sb.toString ().substring (indx));
        }
        
        return fo;
    }
    
    /** Find last folder for resourceName.
     * @param fo file object to search from
     * @param resourceName name of file to find
     * @param last last[0] will be filled with the last found name
     * @return position of last / if everything has been searched, or -1 if some files are missing
     */
    private static int searchFolder (FileObject fo, String resourceName, FileObject[] last) {
        int pos = 0;
        
        for (;;) {
            int next = resourceName.indexOf('/', pos);
            if (next == -1) {
                // end of the search
                last[0] = fo;
                return pos;
            }
            
            if (next == pos) {
                pos++;
                continue;
            }
            
            FileObject nf = fo.getFileObject(resourceName.substring (pos, next));
            if (nf == null) {
                // not found a continuation
                last[0] = fo;
                return -1;
            }
            
            // proceed to next one
            pos = next + 1;
            fo = nf;
        }
    }
    
    
    /** A special lookup associated with id.
     */
    private static final class Lkp extends ProxyLookup
    implements PropertyChangeListener, FileChangeListener {
        /** converted ID we are associated with */
        private String id;
        /** for this data object we initialized this lookup */
        private XMLDataObject xml;
        
        /** last file folder we are listening on. Initialized lazily */
        private FileObject folder;
        /** a data object that produces values Initialized lazily */
        private DataObject obj;
        
        /** @param id the id to work on */
        public Lkp (String id, XMLDataObject xml) {
            super (new Lookup[0]);
            this.id = id;
            this.xml = xml;
        }
     
        /** Check whether all necessary values are updated.
         */
        protected void beforeLookup (Template t) {
            if (folder == null && obj == null) {
                update ();
            }
        }
        
        /** Updates current state of the lookup.
         */
        private void update () {
            FileObject[] last = new FileObject[1];
            FileObject fo = findObject (id, last);
            
            DataObject o = null;
            
            if (fo != null) {
                try {
                    o = DataObject.find (fo);
                } catch (org.openide.loaders.DataObjectNotFoundException ex) {
                    TopManager.getDefault ().getErrorManager ().notify (ex);
                }
            }
        
            if (o == obj) {
                // the data object is still the same as used to be
                // 
                Lookup l = findLookup (xml, o);
                if (o != null && l != null) {
                    // just update the lookups
                    setLookups (new Lookup[] { l });
                    // and exit
                    return;
                } 
            } else {
                // data object changed
                obj = o;
                
                Lookup l = findLookup(xml, o);
                
                if (o != null && l != null) {
                    // add listener to changes of the data object
                    o.addPropertyChangeListener (
                        WeakListener.propertyChange (this, o)
                    );
                    // update the lookups
                    setLookups (new Lookup[] { l });
                    // and exit
                    return;
                }
            }
            
            // object is null => there are no lookups
            setLookups (new Lookup[0]);
            
            // and start listening on latest existing folder 
            // if we did not do it yet
            if (folder != last[0]) {
                folder = last[0];
                last[0].addFileChangeListener (
                    WeakListener.fileChange (this, last[0])
                );
            }
        }
        
        /** Fired when a file is deleted.
         * @param fe the event describing context where action has taken place
         */
        public void fileDeleted(FileEvent fe) {
            update ();
        }
        
        /** Fired when a new folder is created. This action can only be
         * listened to in folders containing the created folder up to the root of
         * file system.
         *
         * @param fe the event describing context where action has taken place
         */
        public void fileFolderCreated(FileEvent fe) {
            update ();
        }
        
        /** Fired when a new file is created. This action can only be
         * listened in folders containing the created file up to the root of
         * file system.
         *
         * @param fe the event describing context where action has taken place
         */
        public void fileDataCreated(FileEvent fe) {
            update ();
        }
        
        /** Fired when a file attribute is changed.
         * @param fe the event describing context where action has taken place,
         *          the name of attribute and the old and new values.
         */
        public void fileAttributeChanged(FileAttributeEvent fe) {
        }
        
        public void propertyChange(java.beans.PropertyChangeEvent ev) {
            String name = ev.getPropertyName();
            
            if (DataObject.PROP_COOKIE == name) {
                update ();
            }
            
            if (DataObject.PROP_VALID == name) {
                update ();
            }
        }
        
        /** Fired when a file is renamed.
         * @param fe the event describing context where action has taken place
         *          and the original name and extension.
         */
        public void fileRenamed(FileRenameEvent fe) {
            update ();
        }
        
        /** Fired when a file is changed.
         * @param fe the event describing context where action has taken place
         */
        public void fileChanged(FileEvent fe) {
        }
        
    }
}
