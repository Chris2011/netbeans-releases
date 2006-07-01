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

package org.openide.util.lookup;

import org.openide.util.*;

import java.lang.ref.WeakReference;
import java.util.*;
import junit.framework.*;
import org.netbeans.junit.*;
import java.io.Serializable;

public class InitializationBug44134Test extends NbTestCase {
    public InitializationBug44134Test (java.lang.String testName) {
        super(testName);
    }

    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(new NbTestSuite(InitializationBug44134Test.class));
    }

    public void testThereShouldBe18Integers () throws Exception {
        FooManifestLookup foo = new FooManifestLookup ();
        
        Collection items = foo.lookup (new Lookup.Template (Integer.class)).allItems ();
        
        assertEquals ("18 of them", 18, items.size ());
        
        Iterator it = items.iterator ();
        while (it.hasNext()) {
            Lookup.Item t = (Lookup.Item)it.next ();
            assertEquals ("Is Integer", Integer.class, t.getInstance ().getClass ());
        }
    }

    
    public class FooManifestLookup extends AbstractLookup {
        public FooManifestLookup() {
            super();
        }
        
        protected void initialize() {
            for (int i=0; i<18; i++) {
                try {
                    String id= "__" + i;
                    
                    addPair(new FooLookupItem(new Integer(i),id));
                }
                catch (Exception e) {
                }
            }
        }
        
        public class FooLookupItem extends AbstractLookup.Pair {
            public FooLookupItem(Integer data, String id) {
                super();
                this.data=data;
                this.id=id;
            }
            
            protected boolean creatorOf(Object obj) {
                return obj == data;
            }
            
            public String getDisplayName() {
                return data.toString();
            }
            
            public Class getType () {
                return Integer.class;
            }
            
            protected boolean instanceOf (Class c) {
                return c.isInstance(data);
            }
            
            public Object getInstance() {
                return data;
            }
            
            public String getId() {
                return id;
            }
            
            private Integer data;
            private String id;
        }
    }
    
}
