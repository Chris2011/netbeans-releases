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
package org.netbeans.modules.java.classpath;

import java.util.List;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import org.netbeans.spi.java.classpath.ClassPathImplementation;
import org.netbeans.spi.java.classpath.PathResourceImplementation;


public class SimpleClassPathImplementation implements ClassPathImplementation {
    
    List entries;
    
    public SimpleClassPathImplementation() {
        this(new ArrayList());
    }

    public SimpleClassPathImplementation(List entries) {
        this.entries = entries;
    }
    
    public List /*<PathResourceImplementation>*/ getResources() {
        return Collections.unmodifiableList(entries);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        // XXX TBD
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        // XXX TBD
    }
    
}
