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
package org.netbeans.spi.xml.cookies;

import org.xml.sax.InputSource;

import org.netbeans.api.xml.cookies.*;

/**
 * <code>CheckXMLCookie</code> implementation support simplifing cookie providers
 * based on <code>InputSource</code>s representing XML documents and entities.
 * <p>
 * <b>Primary use case</b> in a DataObject subclass (which primary file is XML):
 * <pre>
 *   CookieSet cookies = getCookieSet();
 *   InputSource in = DataObjectAdapters.inputSource(this);
 *   CheckXMLSupport cookieImpl = new CheckXMLSupport(in);
 *   cookies.add(cookieImpl);
 * </pre>
 * <p>
 * <b>Secondary use case:</b> Subclasses can customize the class by customization
 * protected methods. The customized subclass can be used according to
 * primary use case.
 *
 * @author Petr Kuzel
 * @deprecated XML tools SPI candidate
 */
public class CheckXMLSupport extends TestXMLSupport implements CheckXMLCookie {
        
    /**
     * General parsed entity strategy. This strategy works well only for
     * standalone (no entity reference) external entities.
     */
    public static final int CHECK_ENTITY_MODE = 1;
    
    /**
     * Parameter parsed entity strategy. This strategy is suitable for standalone
     * (no undeclared parameter entity reference) external DTDs.
     */
    public static final int CHECK_PARAMETER_ENTITY_MODE = 2;
    
    /**
     * XML document entity strategy. It is ordinary XML document processing mode.
     */
    public static final int DOCUMENT_MODE = 3;
    
    /** 
     * Create new CheckXMLSupport for given data object using DOCUMENT_MODE strategy.
     * @param inputSource Supported data object.
     */    
    public CheckXMLSupport(InputSource inputSource) {
        super(inputSource);
    }
    
    /** 
     * Create new CheckXMLSupport for given data object.
     * @param inputSource Supported data object.
     * @param strategy One of <code>*_MODE</code> constants.
     */    
    public CheckXMLSupport(InputSource inputSource, int strategy) {
        super(inputSource, strategy);
    }

    // inherit JavaDoc
    public boolean checkXML(CookieObserver l) {
        return super.checkXML(l);
    }
}

