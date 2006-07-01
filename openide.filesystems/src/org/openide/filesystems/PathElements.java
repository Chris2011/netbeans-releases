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
package org.openide.filesystems;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;


/**
 * @author Ales Novak
 */
final class PathElements {
    private static final String DELIMITER = "/"; // NOI18N

    /** Original name */
    private String name;

    /** tokenizer */
    private StringTokenizer tokenizer;

    /** tokens */
    private List<String> tokens;

    /** Creates new PathElements */
    public PathElements(String name) {
        this.name = name;
        tokenizer = new StringTokenizer(name, DELIMITER);
        tokens = new ArrayList<String>(10);
    }

    /**
     * @return original name
     */
    public String getOriginalName() {
        return name;
    }

    public Enumeration<String> getEnumeration() {
        return new EnumerationImpl(this);
    }

    boolean contains(int i) {
        if (tokens.size() <= i) {
            scanUpTo(i);
        }

        return (tokens.size() > i);
    }

    String get(int i) throws NoSuchElementException {
        if (tokens.size() <= i) {
            scanUpTo(i);
        }

        if (tokens.size() <= i) {
            throw new NoSuchElementException();
        }

        return tokens.get(i);
    }

    private synchronized void scanUpTo(int i) {
        if (tokenizer == null) {
            return;
        }

        if (tokens.size() > i) {
            return;
        }

        for (int k = tokens.size() - 1; (k < i) && tokenizer.hasMoreTokens(); k++) {
            tokens.add(tokenizer.nextToken());
        }

        if (!tokenizer.hasMoreTokens()) {
            tokenizer = null;
        }
    }

    /** Impl of enumeration */
    static final class EnumerationImpl implements Enumeration<String> {
        private PathElements elements;
        private int pos;

        EnumerationImpl(PathElements elements) {
            this.elements = elements;
            this.pos = 0;
        }

        /** From Enumeration */
        public boolean hasMoreElements() {
            return elements.contains(pos);
        }

        /** From Enumeration */
        public String nextElement() throws NoSuchElementException {
            return elements.get(pos++);
        }
    }
}
