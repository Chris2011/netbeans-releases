/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.modules.properties;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.openide.util.WeakListeners;


/** 
 * Structure of a bundle of <code>.properties</code> files.
 * Provides structure of entries (one entry per one .properties file)
 * for one <code>PropertiesDataObject</code>.
 * <p>
 * This structure provides support for sorting entries and fast mapping
 * of integers to <code>entries</code>.
 * <p>
 * The sorting support in this class is a design flaw&nbsp;-
 * consider it deprecated.
 *
 * @author Petr Jiricka
 */
public class BundleStructure {
    
    /**
     * <code>PropertiesDataObject</code> whose structure is described
     * by this object
     */
    PropertiesDataObject obj;

    /**
     * file entries of the <code>PropertiesDataObject</code>.
     * The first entry always represents the primary file.
     * The other entries represent secondary files and are sorted
     * by the corresponding files' names.
     *
     * @see  #updateEntries
     */
    private PropertiesFileEntry[] entries;

    /**
     * sorted list of non-escaped keys from all entries
     *
     * @see  #buildKeySet
     */
    private List keyList;
    
    /**
     * Compartor which sorts keylist.
     * Default set is sort according keys in file order.
     */
    private KeyComparator comparator = new KeyComparator();

    /**
     * registry of <code>PropertyBundleListener</code>s and support
     * for firing <code>PropertyBundleEvent</code>s.
     * Methods for registering and notification of listeners delegate to it.
     */
    private PropertyBundleSupport propBundleSupport
            = new PropertyBundleSupport(this);

    /** listens to changes on the underlying <code>PropertyDataObject</code> */
    private PropertyChangeListener propListener;
    
    /**
     * Creates a new instance describing a given
     * <code>PropertiesDataObject</code>.
     *
     * @param  obj  <code>PropertiesDataObject</code> to be desribed
     * @param ch children container for the node
     */
    public BundleStructure(PropertiesDataObject obj) {
        this.obj = obj;
        updateEntries();

        // Listen on the PropertiesDataObject.
        propListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(
                        PropertiesDataObject.PROP_FILES)) {
                    updateEntries();
                    propBundleSupport.fireBundleStructureChanged();
                }
            }
        };
        obj.addPropertyChangeListener(
                WeakListeners.propertyChange(propListener, obj));
    }

    
    /**
     * Retrieves n-th entry from the list, indexed from <code>0</code>.
     * The first entry is always the primary entry.
     *
     * @param  index  index of entry to be retrieved, starting at <code>0</code>
     * @return  entry at the specified index;
     *          or <code>null</code> if the index is out of bounds
     */
    public PropertiesFileEntry getNthEntry(int index) {
        if (entries == null) {
            notifyEntriesNotInitialized();
        }
        if (index >= 0 && index < entries.length) {
            return entries[index];
        } else {
            return null;
        }
    }

    /**
     * Retrieves an index of a file entry representing the given file.
     *
     * @param  fileName  simple name (excl. path, incl. extension) of the
     *                   primary or secondary file
     * @return  index of the entry representing a file with the given filename;
     *          or <code>-1</code> if no such entry is found
     * @exception  java.lang.IllegalStateException
     *             if the list of entries has not been initialized yet
     * @see  #getEntryByFileName
     */
    public int getEntryIndexByFileName(String fileName) {
        if (entries == null) {
            notifyEntriesNotInitialized();
        }            
        for (int i = 0; i < getEntryCount(); i++) {
            if (entries[i].getFile().getName().equals(fileName)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Retrieves a file entry representing the given file
     *
     * @param  fileName  simple name (excl. path, incl. extension) of the
     *                   primary or secondary file
     * @return  entry representing the given file;
     *          or <code>null</code> if not such entry is found
     * @exception  java.lang.IllegalStateException
     *             if the list of entries has not been initialized yet
     * @see  #getEntryIndexByFileName
     */
    public PropertiesFileEntry getEntryByFileName(String fileName) {
        int index = getEntryIndexByFileName(fileName);
        return ((index == -1) ? null : entries[index]);
    }

    /**
     * Retrieves number of file entries.
     *
     * @return  number of file entries
     * @exception  java.lang.IllegalStateException
     *             if the list of entries has not been initialized yet
     */
    public int getEntryCount() {
        if (entries == null) {
            notifyEntriesNotInitialized();
        }
        return entries.length;
    }

    // Sorted keys management ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Retrieves all un-escaped keys in bundle.
     *
     * @return  sorted array of non-escaped keys
     * @exception  java.lang.IllegalStateException
     *             if the list of keys has not been initialized yet
     * @see  #sort
     */
    public String[] getKeys() {
        if (keyList == null) {
            notifyKeyListNotInitialized();
        }
        return (String[]) keyList.toArray(new String[0]);
    }

    /**
     * Retrieves the n-th bundle key from the list, indexed from <code>0</code>.
     *
     * @param  keyIndex  index according to the current order of keys
     * @return  non-escaped key at the given position;
     *          or <code>null</code> if the given index is out of range
     * @exception  java.lang.IllegalStateException
     *             if the list of keys has not been initialized yet
     */
    public String keyAt(int keyIndex) {
        if (keyList == null) {
            notifyKeyListNotInitialized();
        }
        if (keyIndex < 0 || keyIndex >= keyList.size()) {
            return null;
        } else {
            return (String) keyList.get(keyIndex);
        }
    }

    /**
     * Returns the index of the given key within the sorted list of keys
     *
     * @param  key  non-escaped key
     * @return  position of the given key in the bundle;
     *          or <code>-1</code> if the key was not found
     * @exception  java.lang.IllegalStateException
     *             if the list of keys has not been initialized yet
     */
    public int getKeyIndexByName(String key) {
        if (keyList == null) {
            notifyKeyListNotInitialized();
        }
        return keyList.indexOf(key);
    }

    /**
     * Retrieves keyIndex-th key in the entryIndex-th entry from the list,
     * indexed from <code>0</code>.
     *
     * @return  item  for keyIndex-th key in the entryIndex-th entry;
     *                or <code>null</code> if the entry does not contain
     *                the key or entry doesn't exist
     */
    public Element.ItemElem getItem(int entryIndex, int keyIndex) {
        String key = keyAt(keyIndex);
        return getItem(entryIndex, key);
    }

    /**
     * Returns a property item having a given key, from a given file entry.
     *
     * @param  entryIndex  index of the file entry to get the item from
     * @param  key  key of the property to receive
     * @return  item from the given file entry, having the given key;
     *          or <code>null</code> if one of the following is true:
     *          <ul>
     *              <li>entry index is out of bounds</li>
     *              <li><code>null</code> was passed as a key</li>
     *              <li>the given key was not found in the given entry</li>
     *              <li>structure of the given file entry is not available
     *                  because of an error while reading the entry
     *                  or because parsing of the file entry was stopped
     *                  for some reason</li>
     *          </ul>
     * @see  org.netbeans.modules.properties.Element.ItemElem
     */
    public Element.ItemElem getItem(int entryIndex, String key) {
        if (key == null) {
            return null;
        }
        PropertiesFileEntry pfe = getNthEntry(entryIndex);
        if (pfe == null) {
            return null;
        }
        PropertiesStructure ps = pfe.getHandler().getStructure();
        if (ps != null) {
            return ps.getItem(key);
        } else {
            return null;
        }
    }

    /**
     * Returns count of all unique keys found in all file entries.
     *
     * @return  size of a union of keys from all entries
     * @exception  java.lang.IllegalStateException
     *             if the list of keys has not been initialized yet
     */
    public int getKeyCount() {
        if (keyList != null) {
            return keyList.size();
        } else {
            notifyKeyListNotInitialized();
            return 0;       //will not happen
        }
    }
    
    /**
     * Sorts the keylist according the values of entry which index is given
     * to this method.
     *
     * @param  index  sorts accordinng nth-1 entry values, <code>0</code> means
     *                sort by keys, if less than <code>0</code> it re-compares
     *                keylist with the same un-changed comparator.
     */
    public void sort(int index) {
        if (index >= 0) {
            comparator.setIndex(index);
        }
        Collections.sort(keyList, comparator);
        propBundleSupport.fireBundleDataChanged();
    }

    /**
     * Gets index accoring which is bundle key list sorted.
     *
     * @return  index, <code>0</code> means according keys,
     *                 <code>-1</code> means sorting as in default
     * properties file
     */
    public int getSortIndex() {
        return comparator.getIndex();
    }
    
    /**
     * Gets current order of sort.
     *
     * @return  true  if ascending, alse descending order
     *                (until sort index is <code>-1</code>, then unsorted)
     */
    public boolean getSortOrder() {
        return comparator.isAscending();
    }

    /**
     * Builds (or rebuilds) a sorted list of entries of the underlying
     * <code>PropertiesDataObject<code> and a sorted list of keys gathered
     * from all the entries.
     *
     * @see  #entries
     * @see  #keyList
     */
    private void updateEntries() {
        TreeMap tm = new TreeMap(
                PropertiesDataObject.getSecondaryFilesComparator());
        for (Iterator it = obj.secondaryEntries().iterator(); it.hasNext(); ) {
            PropertiesFileEntry entry = (PropertiesFileEntry) it.next();
            tm.put(entry.getFile().getName(), entry);
        }

        synchronized (this) {
            // Move the entries.
            int entriesCount = tm.size();
            entries = new PropertiesFileEntry[entriesCount + 1];
            entries[0] = (PropertiesFileEntry) obj.getPrimaryEntry();
            
            int index = 0;
            for (Iterator i = tm.entrySet().iterator(); i.hasNext(); ) {
                entries[++index] = (PropertiesFileEntry)
                                   ((Map.Entry) i.next()).getValue();
            }
        }
        buildKeySet();
    }

    /**
     * Constructs a sorted list of all keys gathered from all entries.
     *
     * @see  #keyList
     */
    private synchronized void buildKeySet() {
        List keyList = new ArrayList() {
            public boolean equals(Object obj) {
                if (!(obj instanceof ArrayList)) {
                    return false;
                }
                ArrayList list2 = (ArrayList) obj;
                
                if (this.size() != list2.size()) {
                    return false;
                }
                for (int i = 0; i < this.size(); i++) {
                    if (!this.contains(list2.get(i))
                            || !list2.contains(this.get(i))) {
                        return false;
                    }
                }
                return true;
            }
        };

        // for all entries add all keys
        int entriesCount = getEntryCount();
        for (int index = 0; index < entriesCount; index++) {
            PropertiesFileEntry entry = getNthEntry(index);
            PropertiesStructure ps = entry.getHandler().getStructure();
            if (ps != null) {
                for (Iterator it = ps.allItems(); it.hasNext(); ) {
                    Element.ItemElem item = (Element.ItemElem) it.next();
                    if (item == null) {
                        continue;
                    }
                    String key = item.getKey();
                    if (key != null && !(keyList.contains(key))) {
                        keyList.add(item.getKey());
                    }
                }
            }
        }
        
        Collections.sort(keyList, comparator);
        this.keyList = keyList;
    }
    
    boolean isReadOnly() {
        boolean canWrite = false;
        for (int i=0; i < getEntryCount(); i++) {
            PropertiesFileEntry entry = getNthEntry(i);
            canWrite |= entry.getFile().canWrite();
        }
        return !canWrite;
    }

    /**
     * Registers a given listener so that it will receive notifications
     * about changes in a property bundle.
     * If the given listener is already registered, a duplicite registration
     * will be performed, so that it will get notifications multiple times.
     *
     * @param  l  listener to be registered
     * @see  #removePropertyBundleListener
     */
    public void addPropertyBundleListener(PropertyBundleListener l) {
        propBundleSupport.addPropertyBundleListener(l);
    }

    /**
     * Unregisters a given listener so that it will no more receive
     * notifications about changes in a property bundle.
     * If the given listener has been registered multiple times,
     * only one registration item will be removed.
     *
     * @param	l		the PropertyBundleListener
     * @see  #addPropertyBundleListener
     */
    public void removePropertyBundleListener(PropertyBundleListener l) {
        propBundleSupport.removePropertyBundleListener(l);
    }

    /**
     * Notifies registered listeners of a change of a single item
     * in a single file entry.
     *
     * @param  struct  object describing the file entry
     * @param  item  changed item (within the entry)
     * @see  #addPropertyBundleListener
     */
    void notifyItemChanged(PropertiesStructure struct, Element.ItemElem item) {
        propBundleSupport.fireItemChanged(
            struct.getParent().getEntry().getFile().getName(),
            item.getKey()
        );
    }

    /**
     * Notifies registered listeners of a change in a single file entry.
     * Depending whether a list of keys has changed, either an event
     * for a single file is fired (if the list of keys has remained unchanged)
     * or a notification of a complex change is fired.
     *
     * @param  handler  handler of an object keeping structure of the modified
     *                  file (entry)
     */
    void notifyOneFileChanged(StructHandler handler) {
        // PENDING - events should be finer
        // find out whether global key table has changed and fire a change
        // according to that
        List oldKeyList = keyList;         
        
        buildKeySet();
        if (!keyList.equals(oldKeyList)) {
            propBundleSupport.fireBundleDataChanged();
        } else {
            propBundleSupport.fireFileChanged(
                    handler.getEntry().getFile().getName());
        }
    }

    /**
     * Notifies registered listeners of a change in a single file entry.
     * The <code>Map</code> arguments are actually list of items,
     * each <code>Map</code> entry is a pair &lt;item&nbsp;key, item&gt;.
     *
     * @param  handler  handler of an object keeping structure of the modified
     *                  file (entry)
     * @param  itemsChanged  list of modified items in the entry
     * @param  itemsAdded    list of items added to the entry
     * @param  itemsDeleted  list of items removed from the entry
     */
    void notifyOneFileChanged(StructHandler handler,
                              Map itemsChanged,
                              Map itemsAdded,
                              Map itemsDeleted) {
        // PENDING - events should be finer
        // find out whether global key table has changed
        // should use a faster algorithm of building the keyset
        buildKeySet();
        propBundleSupport.fireBundleDataChanged();
    }

    /**
     * Throws a runtime exception with a message that the list of bundle keys
     * has not been initialized yet.
     *
     * @exception  java.lang.IllegalStateException  thrown always
     * @see  #buildKeySet
     */
    private void notifyKeyListNotInitialized() {
        throw new IllegalStateException(
                "Resource Bundles: KeyList not initialized");           //NOI18N
    }
    
    /**
     * Throws a runtime exception with a message that the entries
     * have not been initialized yet.
     *
     * @exception  java.lang.IllegalStateException  thrown always
     * @see  #updateEntries
     */
    private void notifyEntriesNotInitialized() {
        throw new IllegalStateException(
                "Resource Bundles: Entries not initialized");           //NOI18N
    }
    
    /**
     * Comparator which compares keys according which locale (column in table was selected).
     */
    private final class KeyComparator implements Comparator {

        /** Index of column to compare with. */
        private int index;
        
        /** Flag if ascending order should be performed. */
        private boolean ascending;

        
        /** Constructor. */
        public KeyComparator() {
            this.index = -1;
            ascending = false;
        }
        
        
        /**
         * Setter for <code>index</code> property.
         * ascending -&gt; descending -&gt; primary file key order -&gt; ....
         *
         * @param  index  interval <code>0</code> .. entry count
         */
        public void setIndex(int index) {
            if (index == -1) {
                throw new IllegalArgumentException();
            }
            // if same column toggle order
            if (this.index == index) {
                if (ascending) {
                    ascending = false;
                } else {
                    // sort as in properties file
                    index = -1;
                    ascending = true;
                }
            } else {
                ascending = true;
            }
            this.index = index;
        }

        /**
         * Getter for <code>index</code> property.
         *
         * @return  <code>-1</code>..entry count, <code>-1</code> means unsorted
         * */
        public int getIndex() {
            return index;
        }
        
        /** Getter for <code>ascending</code> property. */
        public boolean isAscending() {
            return ascending;
        }

        /**
         * It's strange as it access just being compared list
         */
        public int compare(Object o1, Object o2) {
            String str1;
            String str2;
            
            // sort as in default properties file
            if (index < 0) {
                Element.ItemElem item1 = getItem(0, (String) o1);
                Element.ItemElem item2 = getItem(0, (String) o2);
                if (item1 != null && item2 != null) {
                    int i1 = item1.getBounds().getBegin().getOffset();
                    int i2 = item2.getBounds().getBegin().getOffset();
                    return i1 - i2;
                } else if (item1 != null) {
                    return -1;
                } else if (item2 != null) {
                    return 1;
                } else {
                    /*
                     * None of the keys is in the default (primary) properties
                     * file. Order the files by name.
                     */
                    str1 = (String) o1;
                    str2 = (String) o2;
                }
            }
            // key column
            if (index == 0) {
                str1 = (String) o1;
                str2 = (String) o2;
            } else {
                Element.ItemElem item1 = getItem(index - 1, (String) o1);
                Element.ItemElem item2 = getItem(index - 1, (String) o2);
                if (item1 == null) {
                    if (item2 == null) {
                        return 0;
                    } else {
                        return ascending ? 1 : -1;
                    }
                } else {
                    if (item2 == null) {
                        return ascending ? -1 : 1;
                    }
                }
                str1 = item1.getValue();
                str2 = item2.getValue();
            }

            if (str1 == null) {
                if (str2 == null) {
                    return 0;
                } else {
                    return ascending ? 1 : -1;
                }
            } else if (str2 == null) {
                return ascending ? -1 : 1;
            }
            int res = str1.compareToIgnoreCase(str2);

            return ascending ? res : -res;
        }
        
    } // End of inner class KeyComparator.
    
}
