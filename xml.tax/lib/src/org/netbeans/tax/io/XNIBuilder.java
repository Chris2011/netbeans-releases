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
package org.netbeans.tax.io;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.text.MessageFormat;
import java.lang.reflect.*;

import org.xml.sax.*;
import org.xml.sax.helpers.LocatorImpl;

import org.apache.xerces.xni.*;
import org.apache.xerces.parsers.*;

import org.netbeans.tax.*;
import org.netbeans.tax.io.*;
import org.netbeans.tax.decl.*;
import java.util.List;


/**
 * Xerces Native Interface ("XNI") based implementation. It sets
 * namespace aware and non-validating features.
 * <p>
 * Do instantiate it directly, prefer TreeBuilder interface loaded  by TreeStreamSource
 * (i.e. ParserLoader). It will do necessary implementation isolation.
 * <p>
 * Every well-formed source must be possible to convert to tree structure.
 * //!!! A mechanism of supressing particular implemenation constrains will
 * be needed (JAXP validation on request could be a good approach).
 *
 * @author  Petr Kuzel
 * @version in progress...
 */
public final class XNIBuilder implements TreeBuilder {
        
    private static final boolean ASSERT = false;
    
    //      private static final PrintStream dbg = System.err;
    
    private static final String DTD_WRAPPER = "<!DOCTYPE DTD PUBLIC \"{0}\" \"{1}\">"; // NOI18N
    
    // TreeStreamSource defines
    private Class buildClass;  //DTD or XML [or Fragment]
    
    private InputSource inputSource;
    
    // interface for reporting errors during the tree construction
    private TreeStreamBuilderErrorHandler errorHandler;
    
    // do not forget to set to the parser
    private EntityResolver entityResolver;
    
    
    /** Creates new TreeStreamBuilderXercesImpl */
    public XNIBuilder (Class buildClass, InputSource inputSource, EntityResolver entityResolver, TreeStreamBuilderErrorHandler errorHandler) {
        init (buildClass, inputSource, entityResolver, errorHandler);
    }
    
    /** Initialize it */
    private void init (Class buildClass, InputSource inputSource, EntityResolver entityResolver, TreeStreamBuilderErrorHandler errorHandler) {
        this.inputSource    = inputSource;
        this.buildClass     = buildClass;
        this.errorHandler   = errorHandler;
        this.entityResolver = entityResolver;
    }
    
    /**
     * Build new TreeDocument by delegating to private class (hiding its
     * public XNI interfaces implementation).
     */
    public TreeDocumentRoot buildDocument () throws TreeException {
        
        boolean buildXML = true;
        InputSource builderSource = inputSource;
        EntityResolver builderResolver = entityResolver;
        
        /*
         * We are building DTD so wrap into auxiliary InputSource that
         * can be passed to XML parser.
         */
        if (buildClass == TreeDTD.class) {
            
            String src = MessageFormat.format (DTD_WRAPPER, new String[] {
                DTDEntityResolver.DTD_ID,
                inputSource.getSystemId ()
            });
            
            builderSource = new InputSource (inputSource.getSystemId ());
            builderSource.setCharacterStream (new StringReader (src));
            
            builderResolver = new DTDEntityResolver ();
            buildXML = false;
        }
        
        XMLBuilder builder = this.new XMLBuilder (buildXML);
        
        try {
            final String SAX_FEATURE = "http://xml.org/sax/features/"; // NOI18N
            final String XERCES_FEATURE = "http://apache.org/xml/features/"; // NOI18N
            
            builder.setFeature (SAX_FEATURE + "namespaces", true); // NOI18N
            builder.setFeature (SAX_FEATURE + "validation", false);  //!!! // NOI18N
            builder.setFeature (SAX_FEATURE + "external-general-entities", true); // NOI18N
            builder.setFeature (SAX_FEATURE + "external-parameter-entities", true); // NOI18N
            builder.setFeature (XERCES_FEATURE + "validation/warn-on-duplicate-attdef", true); // NOI18N
            builder.setFeature (XERCES_FEATURE + "validation/warn-on-undeclared-elemdef", true); // NOI18N
            builder.setFeature (XERCES_FEATURE + "allow-java-encodings", true); // NOI18N
            builder.setFeature (XERCES_FEATURE + "scanner/notify-char-refs", true); // NOI18N
            builder.setFeature (XERCES_FEATURE + "scanner/notify-builtin-refs", true); // NOI18N
            
            //            final String XERCES_PROPERTY = "http://apache.org/xml/properties/"; // NOI18N
            //            builder.setProperty(XERCES_PROPERTY + "internal/entity-resolver", builderResolver); // NOI18N
            
            builder.setEntityResolver (builderResolver);
            
            // the builder extends XNIDocumentParser that receives
            // error events directly
            
            builder.setErrorHandler (new ErrorHandler () {
                public void error (org.xml.sax.SAXParseException e) {}
                public void warning (org.xml.sax.SAXParseException e) {}
                public void fatalError (org.xml.sax.SAXParseException e) {}
            });
            builder.parse (builderSource);
            
        } catch (DTDStopException stop) {
            
            // we just stopped the parser at the end of standalone DTD
            
        } catch (SAXException sex) {
            
            // test whether wrapped exception is XNI one
            // if so it wrrap actual exception
            
            Exception exception = sex.getException ();
            
            if ((exception instanceof DTDStopException) == false ) {
                
                if ( Util.THIS.isLoggable() ) /* then */ Util.THIS.debug ("sex", sex); // NOI18N
                if ( Util.THIS.isLoggable() ) /* then */ Util.THIS.debug ("exception", exception); // NOI18N
                
                if (exception instanceof XNIException) {
                    exception = ((XNIException)exception).getException ();
                }
                if (exception != null) {
                    if (!!! (exception instanceof TreeException)) {
                        exception = new TreeException (sex);
                    }
                } else {
                    exception = new TreeException (sex);
                }
                throw (TreeException) exception;
            }
            
        } catch (IOException exc) {
            if ( Util.THIS.isLoggable() ) /* then */ Util.THIS.debug ("exc", exc); // NOI18N

            throw new TreeException (exc);
        }
        
        return builder.getDocumentRoot ();
    }
    
    
    /*
     * Resolve DTD to original InputSource, forward others.
     * DTD builder uses wrapping InputSource so XML parser can be used as DTD one.
     */
    private class DTDEntityResolver implements EntityResolver {
        
        static final String DTD_ID = "PRIVATE//AUXILIARY DTD ID//PRIVATE"; // NOI18N
        
        public InputSource resolveEntity (String publicId, String systemId) throws SAXException, IOException {
            
            if (DTD_ID.equals (publicId)) {
                return inputSource;
            } else {
                return entityResolver.resolveEntity (publicId, systemId);
            }
        }
        
    }
    
    /*
     * It is used to signal that we are parsing a DTD and we reached end of it.
     * So we can stop the parser by throwing it.
     */
    private class DTDStopException extends XNIException {
        
        /** Serial Version UID */
        private static final long serialVersionUID =4994054007367982021L;
        
        public DTDStopException () {
            super ("This exception is used to signal end of DTD."); // NOI18N
        };
        
        //
        // Look like wrapping exception, so it be converted so SAXException
        // that wraps this one.
        //
        public Exception getException () {
            return this;
        }
        
        public Throwable fillInStackTrace () {
            return this;
        }
    }
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
    /*
     * A pipeline of document components starts with a document source; is
     * followed by zero or more document filters; and ends with a document
     * handler.
     *
     * The document handler follows.
     */
    
    
    /**
     * Listens on XNI creating XML structure. It uses mini XNI pipe
     * featuring just with scanner. A validator is used but validity
     * are discarded since tree must be just well-formed ("WF").
     */
    private class XMLBuilder extends SAXParser implements XMLDTDContentModelHandler, XMLDocumentHandler, XMLDTDHandler {
        
        private TreeDocumentRoot returnDocument;    // initial parent
        private TreeDocumentRoot document;          // tmp variable
        
        private TreeDocumentType doctype;   // it will become parent node of DTD content
        private TreeNode tempNode;          // current working node
        
        private Stack   parentStack;        // parents' child lists stack
        private TreeObjectList parent;      // top of the stack
        private Stack parentNodeStack;      // some times we need nodes directly
        
        private Stack   elementStack;           // ??? it could be avoided
        private int entityCounter;              // how deep we entered
        
        private boolean isXMLDocument;          // do we parser XML or standalone DTD
        private boolean inCDATASection;         // we are in the middle of CDATA
        private boolean inDTD;                  // we are in DTD
        private boolean isCorrect;              // builder internal error
        private boolean inCharacterRef;         //
        
        private StringBuffer cdataSectionBuffer;    // working CDATA section buffer
        private QName tmpQName = new QName ();       // working Qname
        private TreeAttlistDecl attlistDecl = null; // latest attlistdecl
        
        private int errors = 0;  // fatal error counter
        
        private final String XML_ENTITY = "[xml]"; // name of entity that precedes startDocument call // NOI18N
        private final String DTD_ENTITY = "[dtd]"; // external DTD entity name // NOI18N
        
        private XMLLocator locator;
        
        private boolean hasExternalDTD = false;
        
        private RememberingReader rememberingReader;
        
        
        /**
         * Create a parser with standard configuration.
         * @param xmlDocument false if building standalone DTD
         */
        public XMLBuilder (boolean xmlDocument) {
            isXMLDocument = xmlDocument;
            entityCounter = 0;
            isCorrect = false;
            inCDATASection = false;
            inDTD = false;
            parentStack = new Stack ();
            parentNodeStack = new Stack ();
            elementStack = new Stack ();  //stacks all non-empty elements
            cdataSectionBuffer = new StringBuffer ();
            inCharacterRef = false;
        }
        
        
        /**
         * Sample user reader replacing it by remebering one suitable for
         * internal DTD remebering.
         */
        public void parse (InputSource in) throws IOException, SAXException {
            Reader reader = in.getCharacterStream ();
            if (reader == null) {
                doAssert (false);  //we must manage that Reader is passed so we can do remembeing
            } else {
                rememberingReader = new RememberingReader (reader);
                in.setCharacterStream (rememberingReader);
                rememberingReader.startRemembering ();  //remember internal DTD see startElement for end
            }
            
            super.parse (in);
        }
        
        //
        // XMLDocumentHandler methods
        //
        
        /**
         * The start of the document.
         *
         * @throws SAXException Thrown by handler to signal an error.
         */
        public void startDocument (XMLLocator locator, String encoding, Augmentations a) {
            
            trace ("startDocument()"); // NOI18N
            
            this.locator = locator;
            try {
                returnDocument = document = new TreeDocument (null,null,null);
                pushParentNode ((TreeDocument)document);
            } catch (TreeException exc) {
                throw new XNIException (exc);
            }
        } // startDocument()
        
        /**
         * Notifies of the presence of an XMLDecl line in the document. If
         * present, this method will be called immediately following the
         * startDocument call.
         *
         * @param version    The XML version.
         * @param encoding   The IANA encoding name of the document, or null if
         *                   not specified.
         * @param standalone The standalone value, or null if not specified.
         *
         * @throws SAXException Thrown by handler to signal an error.
         */
        public void xmlDecl (String version, String encoding, String standalone, Augmentations a) {
            
            trace ("xmlDecl()"); // NOI18N
            
            try {
                ((TreeDocument)document).setHeader (version, encoding, standalone);
            } catch (TreeException exc) {
                throw new XNIException (exc);
            }
        } // xmlDecl(String,String,String)
        
        
        public void textDecl (String version, String encoding, Augmentations a) {
            
            trace ("textDecl()"); // NOI18N
            
            // if we are DTD parser scanning base DTD document entity
            if (isXMLDocument == false && inDTD && inEntity () == false) {
                try {
                    ((TreeDTD)document).setHeader (version, encoding);
                } catch (TreeException ex) {
                    throw new XNIException (ex);
                }
            }
        }

        //??? DTDHAndler
        public void textDecl (String version, String encoding) {
            textDecl(version, encoding, null);
        }
        
        /**
         * Notifies of the presence of the DOCTYPE line in the document.
         */
        public void doctypeDecl (String rootElement, String publicId, String systemId, Augmentations a) {
            
            trace ("doctypeDecl(" + rootElement + "," + publicId + ")"); // NOI18N
            
            try {
                TreeDocumentType _doctype =
                new TreeDocumentType (rootElement, publicId, systemId);
                setBeginPosition (_doctype);
                ((TreeDocument)document).setDocumentType (_doctype);
                
                doctype = _doctype;
            } catch (TreeException exc) {
                throw new XNIException (exc);
            }
        } // doctypeDecl(String,String,String)
        
        
        /**
         * The start of an element.
         */
        public void startElement (QName element, XMLAttributes attributes, Augmentations a) {
            
            trace ("startElement(" + element + ")"); // NOI18N
            
            try {
                tempNode = new TreeElement (element.rawname);
                startElementImpl ((TreeElement) tempNode, attributes);
                
                pushParentNode ((TreeElement)tempNode);
                elementStack.push (tempNode);
                
            } catch (TreeException exc) {
                throw new XNIException (exc);
            }
        } // startElement(QName,XMLAttributes)
        
        /**
         * This callback represents &lt;.....<b>/</b>&gt;.
         */
        public void emptyElement (QName qName, XMLAttributes attributes, Augmentations a) {
            
            trace ("emptyElement(" + qName + ")"); // NOI18N
            
            try {
                tempNode = new TreeElement (qName.rawname, true);
                startElementImpl ((TreeElement) tempNode, attributes);
            } catch (TreeException exc) {
                throw new XNIException (exc);
            }
        }
        
        /**
         * Insert element and its attributes at hiearchy
         */
        private void startElementImpl (TreeElement elem, XMLAttributes attributes) throws TreeException {
            
            setBeginPosition (elem);
            
            //??? is it really neccessary
            if (currentParentNode () instanceof TreeDocument) {
                ((TreeDocument)currentParentNode ()).setDocumentElement (elem);
            } else {
                appendChild (elem);
            }
            
            // handle attributes
            
            int attrCount = attributes.getLength ();
            for (int i = 0; i < attrCount; i++) {
                boolean specified = attributes.isSpecified (i);
                
                if ( specified == true ) { // TEMPORARY -- not specified nodes will not be added into element
                    
                    attributes.getName (i, tmpQName);      //fill tmpQName
                    String val = attributes.getNonNormalizedValue (i);  //???getNonNormalizedValue
                    
                    TreeAttribute attr;  // to be filled
                    
                    if (val.indexOf ('&') < 0) {
                        
                        attr = new TreeAttribute (tmpQName.rawname, val, specified);
                        
                    } else {
                        
                        attr = new TreeAttribute (tmpQName.rawname, "", specified); // NOI18N
                        List list = attr.getValueList ();
                        list.clear ();
                        
                        // build attribute value, split content as refs and text
                        
                        int lastOffset = 0;  // offset
                        for (int offset = val.indexOf ('&'); offset >= 0; offset = val.indexOf ('&', offset + 1)) {
                            
                            int endOffset = val.indexOf (';', offset);
                            String name = val.substring (offset + 1,  endOffset);
                            
                            if (offset > lastOffset) {
                                // insert text
                                TreeText text =
                                new TreeText (val.substring (lastOffset, offset));
                                list.add (text);
                            }
                            
                            
                            if (name.startsWith ("#")) { // NOI18N
                                TreeCharacterReference chref =
                                new TreeCharacterReference (name);
                                list.add (chref);
                            } else {
                                TreeGeneralEntityReference gref =
                                new TreeGeneralEntityReference (name);
                                list.add (gref);
                            }
                            
                            lastOffset = endOffset + 1;
                        }
                        
                        if (val.length () > lastOffset) {
                            String lastText = val.substring (lastOffset);
                            list.add (new TreeText (lastText));
                        }
                    }
                    
                    if ( !!! specified ) {
                        setReadOnly (attr);
                    }
                    elem.addAttribute (attr);
                    
                } // if ( specified == true )
            }
            
            // recall remenbered internal DTD  //!!!
            
            StringBuffer mem = rememberingReader.stopRemembering ();
            if (mem == null) return;
            
            String idtd = mem.toString ();
            int start = -1, end = -1;  // results
            int now, last = -1;  // tmps
            char delimiter;
            
            if ( Util.THIS.isLoggable() ) /* then */ Util.THIS.debug ("TreeStreamBuilderXercesImpl: going to inspect:\n" + idtd);
            
            final String DOCTYPE = "<!DOCTYPE";
            int pos = idtd.lastIndexOf (DOCTYPE);
            if (pos == -1) {
                if ( Util.THIS.isLoggable() ) /* then */ Util.THIS.debug ("TreeStreamBuilderXercesImpl: no DOCTYPE detected.");

                return;
            }
            
            // skip root element name
            
            pos += DOCTYPE.length ();
            for (; StringUtil.isWS (idtd.charAt (pos)); pos ++);
            for (; StringUtil.isWS (idtd.charAt (pos)) == false; pos ++);
            for (; StringUtil.isWS (idtd.charAt (pos)); pos ++);
            
            // SYSTEM or PUBLIC or [
            
            if ( Util.THIS.isLoggable() ) /* then */ Util.THIS.debug ("\nTesting DOCTYPE kind-----\n" + idtd.substring (pos));
            
            if (idtd.charAt (pos) == '[') {  // just internal dtd
                start = ++pos;
            } else if (idtd.charAt (pos) == 'S') { //SYSTEM "" [
                for (; StringUtil.isWS (idtd.charAt (pos)) == false; pos ++);
                for (; StringUtil.isWS (idtd.charAt (pos)); pos ++);
                delimiter = idtd.charAt (pos++);
                for (; idtd.charAt (pos) != delimiter; pos ++);
                pos++;
                for (; StringUtil.isWS (idtd.charAt (pos)); pos ++);
                if (idtd.charAt (pos) == '[') {
                    start = ++pos;
                }
            } else if (idtd.charAt (pos) == 'P') {  // PUBLIC "" "" [
                for (; StringUtil.isWS (idtd.charAt (pos)) == false; pos ++);
                for (; StringUtil.isWS (idtd.charAt (pos)); pos ++);
                delimiter = idtd.charAt (pos++);
                for (; idtd.charAt (pos) != delimiter; pos ++);
                pos++;
                for (; StringUtil.isWS (idtd.charAt (pos)); pos ++);
                delimiter = idtd.charAt (pos++);
                for (; idtd.charAt (pos) != delimiter; pos ++);
                pos++;
                for (; StringUtil.isWS (idtd.charAt (pos)); pos ++);
                if (idtd.charAt (pos) == '[') {
                    start = ++pos;
                }
            }
            
            if (start == -1) {
                if ( Util.THIS.isLoggable() ) /* then */ Util.THIS.debug ("TreeStreamBuilderXercesImpl: it does not have internal DTD.");

                return;
            } else {
                if ( Util.THIS.isLoggable() ) /* then */ Util.THIS.debug ("\n---Analyzing internal DTD:\n" + idtd.substring (start));
            }
            
            // search for internal DTD end
            
            for (last = pos-1; idtd.startsWith ("]>", pos) == false && last < pos;) {
                
                last = pos;
                
                // skip comments and WS
                for (; StringUtil.isWS (idtd.charAt (pos)); pos ++);
                
                now = StringUtil.skipDelimited (idtd, pos, "<!--", "-->");
                if (now != -1) {
                    pos = now;
                    continue;
                }
                
                // skip PIs
                now = StringUtil.skipDelimited (idtd, pos, "<?", "?>");
                if (now != -1) {
                    pos = now;
                    continue;
                }
                
                // skip decls
                now = StringUtil.skipDelimited (idtd, pos, '<', '>' , "\"'");
                if (now != -1) {
                    pos = now;
                    continue;
                }
                
                // skip references
                now = StringUtil.skipDelimited (idtd, pos, '%', ';' , "");
                if (now != -1) {
                    pos = now;
                    continue;
                }
                
            }
            
            if (last == pos) {
                if ( Util.THIS.isLoggable() ) /* then */ Util.THIS.debug ("TreeStreamBuilderXercesImpl: end not reached");

                return;
            }
            
            String internalDTDText = idtd.substring (start, pos);

            if ( Util.THIS.isLoggable() ) /* then */ Util.THIS.debug ("Internal DTD:" + internalDTDText + "\n--");
            
            // use introspectio to set it
            
            try {
                if (doctype == null) return;
                Class klass = doctype.getClass ();
                Field field = klass.getDeclaredField ("internalDTDText");
                field.setAccessible (true);
                field.set (doctype, internalDTDText);
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception ex) {
                // ignore introspection exceptions
                if ( Util.THIS.isLoggable() ) /* then */ Util.THIS.debug ("TreeStreamBuilderXercesImpl.settingInternaDTDText", ex);
            }
            
        }
        
        
        /**
         * Character content.
         */
        public void characters (XMLString text, Augmentations a) {
            
            try {
                if (inCharacterRef == true) return; // ignore resolved
                
                if (inDTD) {
                    if (currentParentNode () instanceof TreeConditionalSection) {
                        if ( Util.THIS.isLoggable() ) /* then */ Util.THIS.debug ("\n*** TreeStreamBuilderXercesImpl::characters: XMLString = '" + text + "'"); // NOI18N
                        
                        ((TreeConditionalSection)currentParentNode ()).setIgnoredContent (
                        text.toString ()
                        );
                    }
                } else if (inCDATASection) {
                    cdataSectionBuffer.append (text.toString ());
                } else {
                    tempNode = new TreeText (text.toString ());
                    setBeginPosition (tempNode);
                    appendChild ((TreeText)tempNode);
                }
            } catch (TreeException exc) {
                throw new XNIException (exc);
            }
        } // characters(XMLString)
  
        
        //??? DTDHandler
        public void characters (XMLString text) {
            characters( text, null);
        }
        
        /**
         * Ignorable whitespace.
         */
        public void ignorableWhitespace (XMLString text, Augmentations a) {
            try {
                tempNode = new TreeText (text.toString ());  //???
                setBeginPosition (tempNode);
                appendChild ((TreeText)tempNode);
            } catch (TreeException exc) {
                throw new XNIException (exc);
            }
        } // ignorableWhitespace(XMLString)
        
        /**
         * The end of an element.
         */
        public void endElement (QName element, Augmentations a) {
            trace ("endElement(" + element + ")"); // NOI18N
            
            try {
                TreeElement el = (TreeElement) elementStack.pop ();
                el.normalize ();  //??? parser return multiline text as multiple characters()
                popParentNode ();
            } catch (TreeException exc) {
                throw new XNIException (exc);
            }
        } // endElement(QName)
        
        
        /**
         * The start of a CDATA section. Buffer its content.
         */
        public void startCDATA (Augmentations a) {
            inCDATASection = true;
            cdataSectionBuffer.delete (0, cdataSectionBuffer.length ());
            //!!! save position
        } // startCDATA()
        
        /**
         * The end of a CDATA section.
         */
        public void endCDATA (Augmentations a) {
            
            inCDATASection = false;
            
            try {
                tempNode = new TreeCDATASection (cdataSectionBuffer.toString ());
                setBeginPosition (tempNode);  //!!! error
                appendChild ((TreeCDATASection)tempNode);
            } catch (TreeException exc) {
                throw new XNIException (exc);
            }
        } // endCDATA()
        
        /**
         * The end of the document.
         */
        public void endDocument (Augmentations a) {
            trace ("endDocument()"); // NOI18N
            
            if (parentStack.isEmpty () == false) {
                if ( Util.THIS.isLoggable() ) /* then */ Util.THIS.debug ("Inconsistency at parentStack: " + parentStack ); // NOI18N
            } else if (elementStack.isEmpty () == false) {
                if ( Util.THIS.isLoggable() ) /* then */ Util.THIS.debug ("Inconsistency at elementStack: " + parentStack ); // NOI18N
            } else {
                isCorrect = true;
            }
        } // endDocument()
        
        //
        // XMLDocumentHandler and XMLDTDHandler methods
        //
        
        public void endPrefixMapping (String prefix, Augmentations a) {
            // not interested
        }
        
        public void startPrefixMapping (String prefix, String uri, Augmentations a) {
            // not interested
        }
        
        /**
         * This method notifies of the start of an entity. The document entity
         * has the pseudo-name of "[xml]"; The DTD has the pseudo-name of "[dtd];
         * parameter entity names start with '%'; and general entity names are
         * just the entity name.
         */
        public void startEntity (String name, String publicId, String systemId,
        String baseSystemId, String encoding, Augmentations a) {
            
            trace ("startEntity(" + name + ")"); // NOI18N
            
            try {
                
                // do not theat these as external entities
                // DTD is wrapped intentionally
                
                if (XML_ENTITY.equals (name)) return;
                if (isXMLDocument == false && DTD_ENTITY.equals (name)) return;
                
                
                if (DTD_ENTITY.equals (name) && isXMLDocument) {
                    
                    hasExternalDTD = true;
                    
                    // we are entering external DTD attach all to DOCTYPE ObjectList
                    pushParent (doctype.getExternalDTD ());
                    
                } else if (name.startsWith ("#")) { // NOI18N
                    
                    tempNode = new TreeCharacterReference (name);
                    appendChild (tempNode);
                    setBeginPosition (tempNode);
                    inCharacterRef = true;
                    
                } else if ( "lt".equals (name) || "gt".equals (name) || "amp".equals (name) // NOI18N
                || "apos".equals (name) || "quot".equals (name)) { // NOI18N
                    
                    tempNode = new TreeGeneralEntityReference (name);
                    appendChild (tempNode);
                    setBeginPosition (tempNode);
                    inCharacterRef = true;
                    
                } else if (name.startsWith ("%")) { // NOI18N
                    
                    if ("IGNORE".equals (encoding)) { // NOI18N
                        // skip entities in markup, place the into unattached list
                        name = name.substring (1);
                        pushParentNode (new TreeParameterEntityReference (name));
                        
                    } else {
                        name = name.substring (1);
                        tempNode = new TreeParameterEntityReference (name);  //??? external entities
                        appendChild ((TreeParameterEntityReference)tempNode);
                        setBeginPosition (tempNode);
                        pushParentNode ((TreeEntityReference)tempNode);
                    }
                    
                } else {
                    
                    tempNode = new TreeGeneralEntityReference (name);  //??? external entities
                    appendChild ((TreeGeneralEntityReference)tempNode);
                    setBeginPosition (tempNode);
                    pushParentNode ((TreeEntityReference)tempNode);
                    
                }
                
                enterEntity ();
                
            } catch (TreeException exc) {
                throw new XNIException (exc);
            }
        } // startEntity(String,String,String,String)
        

        //??? DTDHandler
        public void startEntity (String name, String publicId, String systemId,
        String baseSystemId, String encoding) {
            startEntity(name, publicId, systemId, baseSystemId, encoding, null);
        }
        
        /**
         * A comment.
         */
        public void comment (XMLString text, Augmentations a) {
            
            trace ("comment()"); // NOI18N
            
            try {
                tempNode = new TreeComment (text.toString ());
                setBeginPosition (tempNode);
                appendChild ((TreeComment)tempNode);
            } catch (TreeException exc) {
                throw new XNIException (exc);
            }
        } // comment(XMLString)
        
        //??? DTDHandler
        public void comment (XMLString text) {
            comment(text, null);
        }
        
        /**
         * A processing instruction. Processing instructions consist of a
         * target name and, optionally, text data. The data is only meaningful
         * to the application.
         */
        public void processingInstruction (String target, XMLString data, Augmentations a) {
            
            trace ("processingInstruction(" + target + ")"); // NOI18N
            
            try {
                tempNode = new TreeProcessingInstruction (target, data.toString ());
                setBeginPosition (tempNode);
                appendChild ((TreeProcessingInstruction)tempNode);
            } catch (TreeException exc) {
                throw new XNIException (exc);
            }
        } // processingInstruction(String,XMLString)

        //??? DTDHandler
        public void processingInstruction (String target, XMLString data) {
            processingInstruction( target, data, null);
        }
        
        /**
         * This method notifies the end of an entity. The document entity has
         * the pseudo-name of "[xml]"; the DTD has the pseudo-name of "[dtd];
         * parameter entity names start with '%'; and general entity names are
         * just the entity name.
         */
        public void endEntity (String name, Augmentations a) {
            trace ("endEntity(" + name + ")");  // NOI18N
            
            // skip for root entities of XML documents and
            // standalone DTDs parsed by DTD parser
            
            if (XML_ENTITY.equals (name)) return;
            if (isXMLDocument == false && DTD_ENTITY.equals (name)) return;
            
            exitEntity ();
            
            if (inCharacterRef == true) {
                inCharacterRef = false;
                return;
            }
            
            if (isXMLDocument && DTD_ENTITY.equals (name)) {
                popParent ();  // DOCTYPE ObjectList
            } else {
                popParentNode ();
            }
            
        } // endEntity(String)

        
        //??? DTDHandler
        public void endEntity (String name) {
            endEntity(name, null);
        }
        
        // ~~~~~~~~~~~~~ XMLDTDHandler methods ~~~~~~~~~~~~~~~~~~~~~~~~~
        
        /**
         * The start of the DTD (external part of it is reported by startEntity).
         */
        public void startDTD ( XMLLocator locator) {
            trace ("startDTD()");  // NOI18N
            
            try {
                inDTD = true;
                
                if (isXMLDocument) {
                    
                    pushParentNode (doctype);
                    
                } else {
                    
                    // replace returnDocument
                    returnDocument = document = new TreeDTD (null,null);
                    pushParentNode ((TreeDTD)document);
                }
            } catch (TreeException exc) {
                throw new XNIException (exc);
            }
        } // startDTD()
        
        /**
         * An element declaration.
         */
        public void elementDecl (String name, String cM) {
            trace ("elementDecl(" + name + ")"); // NOI18N
            if (ASSERT)
                doAssert (inDTD);
            
            try {
                appendChild (new TreeElementDecl (name, this.contentModel));
                this.contentModel = null;
            } catch (TreeException exc) {
                throw new XNIException (exc);
            }
            
        } // elementDecl(String,String)
        
        /**
         * The start of an attribute list.
         */
        public void startAttlist (String elementName) {
            
            trace ("startAttlist(" + elementName + ")"); // NOI18N
            
            try {
                tempNode = new TreeAttlistDecl (elementName);
                attlistDecl = (TreeAttlistDecl) tempNode;
                appendChild (attlistDecl);
            } catch (TreeException exc) {
                throw new XNIException (exc);
            }
        } // startAttlist(String)
        
        /**
         * An attribute declaration.
         */
        public void attributeDecl (String elementName, String attributeName,
                                   String type, String[] enumeration,
                                   String defaultType, XMLString defaultValue) {
            
            trace ("attributeDecl(" + attributeName + ")"); // NOI18N
            
            try {
                TreeAttlistDecl list;
                
                if (attlistDecl != null) {
                    list = attlistDecl;
                } else {
                    list = new TreeAttlistDecl (elementName);
                }
                if ( type.equals ("ENUMERATION") ) { // NOI18N
                    type = null;
                }

                short shortDefaultType = TreeAttlistDeclAttributeDef.findDefaultType (defaultType);
                String newDefaultValue = null;
                if ( ( shortDefaultType == TreeAttlistDeclAttributeDef.DEFAULT_TYPE_NULL ) ||
                     ( shortDefaultType == TreeAttlistDeclAttributeDef.DEFAULT_TYPE_FIXED ) ) {
                    newDefaultValue = defaultValue.toString ();
                }
                TreeAttlistDeclAttributeDef decl =
                new TreeAttlistDeclAttributeDef (attributeName, TreeAttlistDeclAttributeDef.findType (type),
                                                 enumeration, shortDefaultType, newDefaultValue);
                
                list.setAttributeDef (decl);
            } catch (TreeException exc) {
                //Util.dumpContext("TreeAttlistDecl.setReadOnly(true)"); // NOI18N
                throw new XNIException (exc);
            }
        } // attributeDecl(String,String,String,String[],String,XMLString)
        
        /**
         * The end of an attribute list.
         */
        public void endAttlist () {
            
            trace ("endAttlist()"); // NOI18N
            
            attlistDecl = null;
        } // endAttlist()
        
        /**
         * An internal entity declaration.
         *
         * @param name The name of the entity. Parameter entity names start with
         *             '%', whereas the name of a general entity is just the
         *             entity name.
         */
        public void internalEntityDecl (String name, XMLString text, XMLString nonNormalizedText)  {
            
            trace ("internalEntityDecl(" + name + ")"); // NOI18N
            
            try {
                boolean par = name.startsWith ("%"); // NOI18N
                if (par) {
                    name = name.substring (1);
                }
                appendChild (new TreeEntityDecl (par, name, text.toString ()));
            } catch (TreeException exc) {
                throw new XNIException (exc);
            }
        } // internalEntityDecl(String,XMLString)
        
        /**
         * An external entity declaration.
         *
         * @param name     The name of the entity. Parameter entity names start
         *                 with '%', whereas the name of a general entity is just
         *                 the entity name.
         */
        public void externalEntityDecl (String name, String publicId,
        String systemId, String baseSystemId) {
            
            trace ("externalEntityDecl(" + name + ")"); // NOI18N
            
            try {
                boolean par = name.startsWith ("%"); // NOI18N
                if (par) {
                    name = name.substring (1);
                }
                
                appendChild (new TreeEntityDecl (par, name, publicId, systemId));
            } catch (TreeException exc) {
                throw new XNIException (exc);
            }
        } // externalEntityDecl(String,String,String)
        
        /**
         * An unparsed entity declaration.
         */
        public void unparsedEntityDecl (String name,
        String publicId, String systemId,
        String notation) {
            
            trace ("unparsedEntityDecl(" + name + ")"); // NOI18N
            
            try {
                appendChild (new TreeEntityDecl (name, publicId, systemId, notation));
            } catch (TreeException exc) {
                throw new XNIException (exc);
            }
        } // unparsedEntityDecl(String,String,String,String)
        
        /**
         * A notation declaration
         */
        public void notationDecl (String name, String publicId, String systemId) {
            
            trace ("notationDecl(" + name + ")"); // NOI18N
            
            try {
                appendChild (new TreeNotationDecl (name, publicId, systemId));
            } catch (TreeException exc) {
                throw new XNIException (exc);
            }
        } // notationDecl(String,String,String)
        
        /**
         * The start of a conditional section.
         *
         * @param type The type of the conditional section. This value will
         *             either be CONDITIONAL_INCLUDE or CONDITIONAL_IGNORE.
         */
        public void startConditional (short type) {
            trace ("startConditional(" + type + ")"); // NOI18N
            if (ASSERT)
                doAssert (inDTD);
            
            if (type == CONDITIONAL_INCLUDE) {
                tempNode = new TreeConditionalSection (true);
            } else {
                tempNode = new TreeConditionalSection (false);
            }
            
            appendChild ((TreeConditionalSection) tempNode);
            setBeginPosition (tempNode);
            pushParentNode ((TreeConditionalSection) tempNode);
            
        } // startConditional(short)
        
        /**
         * The end of a conditional section.
         */
        public void endConditional () {
            trace ("endConditional()");  // NOI18N
            
            popParentNode ();
        } // endConditional()
        
        /**
         * The end of the DTD.
         *
         * @throws SAXException Thrown by handler to signal an error.
         */
        public void endDTD () {
            trace ("endDTD()");  // NOI18N
            
            if (isXMLDocument) {
                
                popParentNode ();
                
            } else {
                
                popParentNode ();
                
                //??? Xerces miss '<' at the end of entity
                // so such documents are reported as correct
                
                isCorrect = errors == 0;
                throw new DTDStopException ();
                
            }
            
            inDTD = false;
        } // endDTD()
        
        
        // ~~~~~~~~~~~~~~~~~ Content Model parser ~~~~~~~~~~~~~~~~~~~
        
        
        private TreeElementDecl.ContentType lastType;     // occurence operators are applied on this
        private TreeElementDecl.ContentType contentModel; // OUTPUT result field
        private Stack contentModelMembersStack;           // stack of parent group members
        
        public void startContentModel (String elementName) {
            
            if ( Util.THIS.isLoggable() ) /* then */ Util.THIS.debug ("startContentModel(" + elementName + ")"); // NOI18N
            
            lastType = null;
            contentModelMembersStack = new Stack ();
            
        }
        
        public void any () {
            contentModel = new ANYType ();
        }
        
        public void empty () {
            contentModel = new EMPTYType ();
        }
        
        public void pcdata () {
            setMembersType (new MixedType ());
        }
        
        
        // it is not called for mixed type
        public void startGroup () {
            if ( Util.THIS.isLoggable() ) /* then */ Util.THIS.debug ("startGroup()"); // NOI18N

            startMembers ();
        }
        
        public void element (String elementName) {
            
            if ( Util.THIS.isLoggable() ) /* then */ Util.THIS.debug ("element(" + elementName + ")"); // NOI18N
            
            lastType = new NameType (elementName);
            addMember (lastType);
        }
        
        // determine type of content model group
        public void separator (short separator) {
            if ( Util.THIS.isLoggable() ) /* then */ Util.THIS.debug ("childrenSeparator()"); // NOI18N
            
            switch (separator) {
                case SEPARATOR_SEQUENCE:
                    setMembersType (new SequenceType ());
                    break;
                case SEPARATOR_CHOICE:
                    setMembersType (new ChoiceType ());
                    break;
                default:
                    doAssert (false);
            }
        }
        
        //
        // INPUT lastType field
        //
        public void occurrence (short occurrence) {
            if ( Util.THIS.isLoggable() ) /* then */ Util.THIS.debug ("childrenOccurrence()"); // NOI18N
            
            switch (occurrence) {
                case OCCURS_ZERO_OR_ONE:
                    lastType.setMultiplicity ('?');
                    break;
                case OCCURS_ZERO_OR_MORE:
                    lastType.setMultiplicity ('*');
                    break;
                case OCCURS_ONE_OR_MORE:
                    lastType.setMultiplicity ('+');
                    break;
                default:
                    doAssert (false);
            }
            
        }
        
        public void endGroup () {
            if ( Util.THIS.isLoggable() ) /* then */ Util.THIS.debug ("childrenEndGroup()"); // NOI18N

            ChildrenType group = getMembersType ();
            group.addTypes (endMembers ());
            lastType = group;
            addMember (lastType);
        }
        
        public void endContentModel () {
            if ( Util.THIS.isLoggable() ) /* then */ Util.THIS.debug ("endContentModel()"); // NOI18N

            if (contentModel == null && lastType == null) { // #PCDATA
                contentModel = new MixedType ();
            } else if (contentModel == null) {  // we are of CHILDREN_TYPE or mixed type
                contentModel = lastType;
                if (contentModel instanceof MixedType) {
                    contentModel.setMultiplicity ('*');
                }
            }
        }
        
        
        
        private void startMembers () {
            contentModelMembersStack.push (new Members (13));
        }
        
        private void addMember (TreeElementDecl.ContentType child) {
            
            // we are at top level of content model, lastType becomes it
            if (contentModelMembersStack.isEmpty ()) return;
            
            Collection members = (Collection) contentModelMembersStack.peek ();
            members.add (child);
        }
        
        private Collection endMembers () {
            return (Collection) contentModelMembersStack.pop ();
        }
        
        // we can predict member group now, if know balk it
        private void setMembersType (ChildrenType group) {
            
            // we are at top level of content model, lastType becomes it
            if (contentModelMembersStack.isEmpty ()) return;
            
            Members members = (Members) contentModelMembersStack.peek ();
            if (members.group == null) members.group = group;
        }
        
        private ChildrenType getMembersType () {
            Members members = (Members) contentModelMembersStack.peek ();
            if (members.group == null) {
                return new ChoiceType ();
            } else {
                return members.group;
            }
        }
        
        //
        // Hold additional information about group that holds these members
        //
        private class Members extends ArrayList {
            
            private ChildrenType group;
            
            private static final long serialVersionUID =4614355994187952965L;
            
            public Members (int initSize) {
                super (initSize);
                group = null;
            }
        }
        
        // ~~~~~~~~~~~~~~~~~ ERROR HANDLER ~~~~~~~~~~~~~~~~~~~~~~~~~~~
        
        
        public void error (org.xml.sax.SAXParseException e) {
            trace (e.getMessage ());

            errorHandler.message (TreeStreamBuilderErrorHandler.ERROR_ERROR, e);
        }
        
        public void warning (org.xml.sax.SAXParseException e) {
            trace (e.getMessage ());

            errorHandler.message (TreeStreamBuilderErrorHandler.ERROR_WARNING, e);
        }
        
        public void fatalError (org.xml.sax.SAXParseException e) {
            trace (e.getMessage ());

            errors++;
            errorHandler.message (TreeStreamBuilderErrorHandler.ERROR_FATAL_ERROR, e);
        }
        
        // ~~~~~~~~~~~~~~~~~~ UTILITY ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        
        /**
         */
        private void setReadOnly (TreeObject treeObject) {
            setReadOnly (treeObject, true);
        }
        
        
        private void setReadOnly (TreeObject treeObject, boolean value) {
            try {
                Method setReadOnlyMethod = TreeObject.class.getDeclaredMethod ("setReadOnly", new Class[] { Boolean.TYPE }); // NOI18N
                setReadOnlyMethod.setAccessible (true);
                setReadOnlyMethod.invoke (treeObject, new Object[] { value == true ? Boolean.TRUE : Boolean.FALSE});
            } catch (NoSuchMethodException exc) {
            } catch (IllegalAccessException exc) {
            } catch (InvocationTargetException exc) {
            }
        }
        
        /**
         * As positons will be supported
         */
        private void setBeginPosition (TreeNode n) {
            //!!!
        }
        
        
        /**
         * @return TreeDocument or null if fatal errors occured
         */
        private TreeDocumentRoot getDocumentRoot () {
            TreeDocumentRoot doc = (TreeDocumentRoot) (errors > 0 ? null : returnDocument);
            
            if ( Util.THIS.isLoggable() ) /* then */ Util.THIS.debug ("TreeStreamBuilderXercesImpl returns: " + doc); // NOI18N
            
            return doc;
        }
        
        
        /**
         * Shortcut - retrieves child list and pushes it at stack
         */
        private void pushParentNode (TreeParentNode parent) {
            parentNodeStack.push (parent);
            pushParent (parent.getChildNodes ());
        }
        
        /**
         * Set new parent list pushing original one to node stack
         */
        private void pushParent (TreeObjectList parentList) {
            if (parentList == null)
                throw new NullPointerException ("Null parent is not allowed."); // NOI18N
            if (parent != null)
                parentStack.push (parent);
            parent = parentList;
        }
        
        /**
         * Restore current children list poping it from stack.
         */
        private void popParent () {
            parent = (TreeObjectList) parentStack.pop ();
        }
        
        /**
         * Resotore parent node and its list from stack
         */
        private void popParentNode () {
            popParent ();
            TreeParentNode parentNode = (TreeParentNode) parentNodeStack.pop ();
            
            // referenced things and DTD things are read only
            
            if ( parentNode instanceof TreeGeneralEntityReference ) {  // entities in XML doc
                
                setReadOnly (parentNode.getChildNodes ());
                
            } else if ( parentNode instanceof TreeDTD ) {  // whole DTD
                
                setReadOnly (parentNode);
                
            } else if ( parentNode instanceof TreeDocumentType ) {
                
                setReadOnly (parentNode.getChildNodes ());
                setReadOnly (((TreeDocumentType)parentNode).getExternalDTD ());
            }
        }
        
        private TreeParentNode currentParentNode () {
            return (TreeParentNode) parentNodeStack.peek ();
        }
        
        /**
         * Add child to current parent list.
         */
        private void appendChild (TreeObject child) {
            parent.add (child);
        }
        
        /**
         * Enter entity, following events origanes from entity resolution
         */
        private void enterEntity () {
            entityCounter++;
        }
        
        /**
         * Exit entity.
         */
        private void exitEntity () {
            entityCounter--;
        }
        
        /**
         * Test whether we are in entity, i.e. creating readonly nodes.
         */
        private boolean inEntity () {
            return entityCounter > 0;
        }
        
        private void trace (String msg) {
            String location = "";
            if (locator != null) {
                String entity = locator.getSystemId ();
                int index = entity.lastIndexOf ('/');
                entity = entity.substring (index > 0 ? index : 0);
                location =  entity + "/" + locator.getLineNumber () + ":" + locator.getColumnNumber () ;
            }
            
            if ( Util.THIS.isLoggable() ) /* then */ Util.THIS.debug ("X2T " + location + " " + msg);  // NOI18N
        }
        
        private void doAssert (boolean asrt) {
            if (asrt == false) {
                throw new IllegalStateException ("ASSERT"); // NOI18N
            }
        }
        
    }
    
    
    
}
