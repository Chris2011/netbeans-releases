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

/*
 * PostInitializationException.java
 *
 * Created on August 28, 2003, 4:38 PM
 */

package org.netbeans.xtest.xmlserializer;

/**
 *
 * @author  mb115822
 */
public class PostInitializationException extends XMLSerializeException {
    
    /**
     * Creates a new instance of <code>PostInitializationException</code> without detail message.
     */
    public PostInitializationException() {
    }
    
    
    /**
     * Constructs an instance of <code>PostInitializationException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public PostInitializationException(String msg) {
        super(msg);
    }
    
    public PostInitializationException(String msg, Throwable cause) {
        super(msg, cause);
    }    
}
