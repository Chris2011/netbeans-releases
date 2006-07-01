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
package org.openide.util.enum;

import java.util.Enumeration;
import java.util.NoSuchElementException;


/**
 * Enumeration that represents a queue. It allows by redefining
 * method <CODE>process</CODE> each outputed object to add other to the end of
 * queue of waiting objects by a call to <CODE>put</CODE>.
 * @deprecated JDK 1.5 treats enum as a keyword so this class was
 *             replaced by {@link org.openide.util.Enumerations#queue}.
 * @author Jaroslav Tulach, Petr Hamernik
 */
public class QueueEnumeration extends Object implements Enumeration {
    /** next object to be returned */
    private ListItem next = null;

    /** last object in the queue */
    private ListItem last = null;

    /** Processes object before it is returned from nextElement method.
    * This method allows to add other object to the end of the queue
    * by a call to <CODE>put</CODE> method. This implementation does
    * nothing.
    *
    * @see #put
    * @param o the object to be processed
    */
    protected void process(Object o) {
    }

    /** Put adds new object to the end of queue.
    * @param o the object to add
    */
    public synchronized void put(Object o) {
        if (last != null) {
            ListItem li = new ListItem(o);
            last.next = li;
            last = li;
        } else {
            next = last = new ListItem(o);
        }
    }

    /** Adds array of objects into the queue.
    * @param arr array of objects to put into the queue
    */
    public synchronized void put(Object[] arr) {
        for (int i = 0; i < arr.length; i++) {
            put(arr[i]);
        }
    }

    /** Is there any next object?
    * @return true if there is next object, false otherwise
    */
    public boolean hasMoreElements() {
        return next != null;
    }

    /** @return next object in enumeration
    * @exception NoSuchElementException if there is no next object
    */
    public synchronized Object nextElement() {
        if (next == null) {
            throw new NoSuchElementException();
        }

        Object res = next.object;

        if ((next = next.next) == null) {
            last = null;
        }

        ;
        process(res);

        return res;
    }

    /** item in linked list of Objects */
    private static final class ListItem {
        Object object;
        ListItem next;

        /** @param o the object for this item */
        ListItem(Object o) {
            object = o;
        }
    }
}
