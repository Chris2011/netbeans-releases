/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.java.j2seplatform.platformdefinition;

import java.beans.*;
import java.io.*;
import java.lang.ref.*;
import java.util.*;

import org.openide.ErrorManager;
import org.openide.cookies.*;
import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.openide.nodes.Node;
import org.openide.nodes.BeanNode;
import org.openide.util.RequestProcessor;
import org.openide.util.Lookup;
import org.openide.util.lookup.*;
import org.openide.xml.*;

import org.xml.sax.*;

import org.netbeans.api.java.platform.*;

/**
 * Reads and writes the standard platform format implemented by PlatformImpl2.
 *
 * @author Svata Dedic
 */
public class PlatformConvertor 
implements Environment.Provider, InstanceCookie.Origin, InstanceCookie.Of,
        PropertyChangeListener, Runnable, InstanceContent.Convertor {
    
    private PlatformConvertor() {}

    public static PlatformConvertor createProvider(FileObject reg) {
        return new PlatformConvertor();
    }
    
    public Lookup getEnvironment(DataObject obj) {
        return new PlatformConvertor((XMLDataObject)obj).getLookup();
    }
    
    InstanceContent cookies = new InstanceContent();
    
    private XMLDataObject   holder;

    private boolean defaultPlatform;

    private Lookup  lookup;
    
    private RequestProcessor.Task    saveTask;
    
    private Reference   refPlatform = new WeakReference(null);
    
    private LinkedList keepAlive = new LinkedList();
    
    private PlatformConvertor(XMLDataObject  object) {
        this.holder = object;
        cookies = new InstanceContent();
        cookies.add(this);
        lookup = new AbstractLookup(cookies);
        cookies.add(Node.class, this);
    }
    
    Lookup getLookup() {
        return lookup;
    }
    
    public Class instanceClass() {
        return JavaPlatform.class;
    }
    
    public Object instanceCreate() throws java.io.IOException, ClassNotFoundException {
        synchronized (this) {
            Object o = refPlatform.get();
            if (o != null)
                return o;
            H handler = new H();
            try {
                XMLReader reader = XMLUtil.createXMLReader();
                InputSource is = new org.xml.sax.InputSource(
                    holder.getPrimaryFile().getInputStream());
                is.setSystemId(holder.getPrimaryFile().getURL().toExternalForm());
                reader.setContentHandler(handler);
                reader.setErrorHandler(handler);
                reader.setEntityResolver(EntityCatalog.getDefault());

                reader.parse(is);
            } catch (SAXException ex) {
                Exception x = ex.getException();
                ex.printStackTrace();
                if (x instanceof java.io.IOException)
                    throw (IOException)x;
                else
                    throw new java.io.IOException(ex.getMessage());
            }

            JavaPlatform inst = createPlatform(handler);
            refPlatform = new WeakReference(inst);
            return inst;
        }
    }
    
    JavaPlatform createPlatform(H handler) {
        JavaPlatform p;
        
        if (handler.isDefault) {
            String sourceFolder = handler.properties == null ? null : (String) handler.properties.get ("platform.src");
            String javadocFolder = handler.properties == null ? null : (String) handler.properties.get ("platform.javadoc");
            p = DefaultPlatformImpl.create(holder.getPrimaryFile(),sourceFolder,javadocFolder);
            defaultPlatform = true;
        } else {
            p = new J2SEPlatformImpl(handler.name,handler.properties, handler.sysProperties);
            defaultPlatform = false;
        }
        p.addPropertyChangeListener(this);
        return p;
    }
    
    public String instanceName() {
        return holder.getName();
    }
    
    public boolean instanceOf(Class type) {
        return (type.isAssignableFrom(JavaPlatform.class));
    }
    
    public FileObject instanceOrigin() {
        return holder.getPrimaryFile();
    }
    
    static int DELAY = 2000;
    
    public void propertyChange(PropertyChangeEvent evt) {
        synchronized (this) {
            if (saveTask == null)
                saveTask = RequestProcessor.getDefault().create(this);
        }
        synchronized (this) {
            keepAlive.add(evt);
        }
        saveTask.schedule(DELAY);
    }
    
    public void run() {
        PropertyChangeEvent e;
        
        synchronized (this) {
            e = (PropertyChangeEvent)keepAlive.removeFirst();
        }
        JavaPlatform plat = (JavaPlatform)e.getSource();
        try {
            holder.getPrimaryFile().getFileSystem().runAtomicAction(
                new W(plat, holder, defaultPlatform));
        } catch (java.io.IOException ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
        }
    }
    
    public Object convert(Object obj) {
        if (obj == Node.class) {
            Object p;
            
            try {
                p = instanceCreate();
                return new BeanNode (p) {
                    public String getDisplayName () {
                        return ((JavaPlatform)this.getBean()).getDisplayName();
                    }
                };
            } catch (IOException ex) {
                ErrorManager.getDefault().notify(ex);
            } catch (ClassNotFoundException ex) {
                ErrorManager.getDefault().notify(ex);
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
        return null;
    }
    
    public String displayName(Object obj) {
        return ((Class)obj).getName();
    }
    
    public String id(Object obj) {
        return obj.toString();
    }
    
    public Class type(Object obj) {
        return (Class)obj;
    }
    
    public static DataObject create(JavaPlatform plat, DataFolder f, String idName) throws IOException {
        W w = new W(plat, f, idName);
        f.getPrimaryFile().getFileSystem().runAtomicAction(w);
        return w.holder;
    }
    
    static class W implements FileSystem.AtomicAction {
        JavaPlatform instance;
        MultiDataObject holder;
        String name;
        DataFolder f;
        boolean defaultPlatform;

        W(JavaPlatform instance, MultiDataObject holder, boolean defaultPlatform) {
            this.instance = instance;
            this.holder = holder;
            this.defaultPlatform = defaultPlatform;
        }
        
        W(JavaPlatform instance, DataFolder f, String n) {
            this.instance = instance;
            this.name = n;
            this.f = f;
            this.defaultPlatform = false;
        }
        
        public void run() throws java.io.IOException {
            FileLock lck;
            FileObject data;
            
            if (holder != null) {
                data = holder.getPrimaryEntry().getFile();
                lck = holder.getPrimaryEntry().takeLock();
            } else {
                FileObject folder = f.getPrimaryFile();
                String fn = FileUtil.findFreeFileName(folder, name, "xml");
                data = folder.createData(fn, "xml");
                lck = data.lock();
            }
            try {
                OutputStream ostm = data.getOutputStream(lck);
                PrintWriter writer = new PrintWriter(
                    new OutputStreamWriter(ostm, "UTF8"));
                write(writer);
                writer.flush();
                writer.close();
                ostm.close();
            } finally {
                lck.releaseLock();
            }
            if (holder == null) {
                holder = (MultiDataObject)DataObject.find(data);
            }
        }
        
        void write(PrintWriter pw) throws IOException {
            pw.println("<?xml version='1.0'?>");
            pw.println(
            "<!DOCTYPE platform PUBLIC '-//NetBeans//DTD Java PlatformDefinition 1.0//EN' 'http://www.netbeans.org/dtds/java-platformdefinition-1_0.dtd'>");
            pw.println("<platform name='"
                + XMLUtil.toAttributeValue(instance.getDisplayName()) +
                "' default='" + (defaultPlatform ? "yes" : "no") +
                "'>");
            Map props = instance.getProperties();
            Map sysProps = instance.getSystemProperties();
            pw.println("  <properties>");
            writeProperties(props, pw);
            pw.println("  </properties>");
            if (!defaultPlatform) {
                pw.println("  <sysproperties>");
                writeProperties(sysProps, pw);
                pw.println("  </sysproperties>");
            }
            pw.println("</platform>");
        }
        
        void writeProperties(Map props, PrintWriter pw) throws IOException {
            Collection sortedProps = new TreeSet(props.keySet());
            for (Iterator it = sortedProps.iterator(); it.hasNext(); ) {
                String n = (String)it.next();
                String val = (String)props.get(n);
                pw.println("    <property name='" +
                    XMLUtil.toAttributeValue(n) + "' value='" +
                    XMLUtil.toAttributeValue(val) + "'/>");
            }
        }
    }
    
    static final String ELEMENT_PROPERTIES = "properties"; // NOI18N
    static final String ELEMENT_SYSPROPERTIES = "sysproperties"; // NOI18N
    static final String ELEMENT_PROPERTY = "property"; // NOI18N
    static final String ELEMENT_PLATFORM = "platform"; // NOI18N
    static final String ATTR_PLATFORM_NAME = "name"; // NOI18N
    static final String ATTR_PLATFORM_DEFAULT = "default"; // NOI18N
    static final String ATTR_PROPERTY_NAME = "name"; // NOI18N
    static final String ATTR_PROPERTY_VALUE = "value"; // NOI18N
    
    static class H extends org.xml.sax.helpers.DefaultHandler {
        Map     properties;
        Map     sysProperties;
        String  name;
        Map     propertyMap;
        boolean isDefault;
        
        public void startDocument () throws org.xml.sax.SAXException {
        }
        
        public void endDocument () throws org.xml.sax.SAXException {
        }
        
        public void startElement (String uri, String localName, String qName, org.xml.sax.Attributes attrs)
        throws org.xml.sax.SAXException {
            if (ELEMENT_PLATFORM.equals(qName)) {
                name = attrs.getValue(ATTR_PLATFORM_NAME);
                isDefault = "yes".equals(attrs.getValue(ATTR_PLATFORM_DEFAULT));
            } else if (ELEMENT_PROPERTIES.equals(qName)) {
                if (properties == null)
                    properties = new HashMap(17);
                propertyMap = properties;
            } else if (ELEMENT_SYSPROPERTIES.equals(qName)) {
                if (sysProperties == null)
                    sysProperties = new HashMap(17);
                propertyMap = sysProperties;
            } else if (ELEMENT_PROPERTY.equals(qName)) {
                if (propertyMap == null)
                    throw new SAXException("property w/o properties or sysproperties");
                String name = attrs.getValue(ATTR_PROPERTY_NAME);
                if (name == null || "".equals(name))
                    throw new SAXException("missing name");
                String val = attrs.getValue(ATTR_PROPERTY_VALUE);
                propertyMap.put(name, val);
            }
        }
        
        public void endElement (String uri, String localName, String qName) throws org.xml.sax.SAXException {
            if (ELEMENT_PROPERTIES.equals(qName) ||
                ELEMENT_SYSPROPERTIES.equals(qName))
                propertyMap = null;
        }
    }

}
