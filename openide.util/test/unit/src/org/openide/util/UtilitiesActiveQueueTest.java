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

package org.openide.util;

import java.lang.ref.*;

import junit.framework.*;

import org.netbeans.junit.*;

public class UtilitiesActiveQueueTest extends NbTestCase {

    public UtilitiesActiveQueueTest(java.lang.String testName) {
        super(testName);
    }

    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(new NbTestSuite(UtilitiesActiveQueueTest.class));
    }

    public void testRunnableReferenceIsExecuted () throws Exception {
        Object obj = new Object ();
        RunnableRef ref = new RunnableRef (obj);
        synchronized (ref) {
            obj = null;
            assertGC ("Should be GCed quickly", ref);
            ref.wait ();
            assertTrue ("Run method has been executed", ref.executed);
        }
    }
    
    public void testRunnablesAreProcessedOneByOne () throws Exception {
        Object obj = new Object ();
        RunnableRef ref = new RunnableRef (obj);
        ref.wait = true;
        
        
        synchronized (ref) {
            obj = null;
            assertGC ("Is garbage collected", ref);
            ref.wait ();
            assertTrue ("Still not executed, it is blocked", !ref.executed);
        }    

        RunnableRef after = new RunnableRef (new Object ());
        synchronized (after) {
            assertGC ("Is garbage collected", after);
            after.wait (100); // will fail
            assertTrue ("Even if GCed, still not processed", !after.executed);
        }

        synchronized (after) {
            synchronized (ref) {
                ref.notify ();
                ref.wait ();
                assertTrue ("Processed", ref.executed);
            }
            after.wait ();
            assertTrue ("Processed too", after.executed);
        }
    }
    
    public void testCallingPublicMethodsThrowsExceptions () {
        try {
            Utilities.activeReferenceQueue().poll();
            fail ("One should not call public method from outside");
        } catch (RuntimeException ex) {
        }
        try {
            Utilities.activeReferenceQueue ().remove ();
            fail ("One should not call public method from outside");
        } catch (InterruptedException ex) {
        }
        try {
            Utilities.activeReferenceQueue ().remove (10);
            fail ("One should not call public method from outside");
        } catch (InterruptedException ex) {
        }
    }
    
    
    private static class RunnableRef extends WeakReference 
    implements Runnable {
        public boolean wait;
        public boolean entered;
        public boolean executed;
        
        public RunnableRef (Object o) {
            super (o, Utilities.activeReferenceQueue ());
        }
        
        public synchronized void run () {
            entered = true;
            if (wait) {
                // notify we are here
                notify ();
                try {
                    wait ();
                } catch (InterruptedException ex) {
                }
            }
            executed = true;
            
            notifyAll ();
        }
    }
}
