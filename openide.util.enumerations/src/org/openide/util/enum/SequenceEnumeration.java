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


/**
 * Composes several enumerations into one.
 * @deprecated JDK 1.5 treats enum as a keyword so this class was
 *             replaced by {@link org.openide.util.Enumerations#concat}.
 * @author Jaroslav Tulach, Petr Nejedly
 */
public class SequenceEnumeration extends Object implements Enumeration {
    /** enumeration of Enumerations */
    private Enumeration en;

    /** current enumeration */
    private Enumeration current;

    /** is {@link #current} up-to-date and has more elements?
    * The combination <CODE>current == null</CODE> and
    * <CODE>checked == true means there are no more elements
    * in this enumeration.
    */
    private boolean checked = false;

    /** Constructs new enumeration from already existing. The elements
    * of <CODE>en</CODE> should be also enumerations. The resulting
    * enumeration contains elements of such enumerations.
    *
    * @param en enumeration of Enumerations that should be sequenced
    */
    public SequenceEnumeration(Enumeration en) {
        this.en = en;
    }

    /** Composes two enumerations into one.
    * @param first first enumeration
    * @param second second enumeration
    */
    public SequenceEnumeration(Enumeration first, Enumeration second) {
        this(new ArrayEnumeration(new Enumeration[] { first, second }));
    }

    /** Ensures that current enumeration is set. If there aren't more
    * elements in the Enumerations, sets the field <CODE>current</CODE> to null.
    */
    private void ensureCurrent() {
        while ((current == null) || !current.hasMoreElements()) {
            if (en.hasMoreElements()) {
                current = (Enumeration) en.nextElement();
            } else {
                // no next valid enumeration
                current = null;

                return;
            }
        }
    }

    /** @return true if we have more elements */
    public boolean hasMoreElements() {
        if (!checked) {
            ensureCurrent();
            checked = true;
        }

        return current != null;
    }

    /** @return next element
    * @exception NoSuchElementException if there is no next element
    */
    public synchronized Object nextElement() {
        if (!checked) {
            ensureCurrent();
        }

        if (current != null) {
            checked = false;

            return current.nextElement();
        } else {
            checked = true;
            throw new java.util.NoSuchElementException();
        }
    }
}
