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

package org.netbeans.modules.project.libraries;

import org.xml.sax.*;

public interface LibraryDeclarationConvertor {
    
    public java.net.URL parseLocation(final java.lang.String data) throws SAXException;
    
    public java.net.URL parseResource(final java.lang.String data) throws SAXException;
    
}

