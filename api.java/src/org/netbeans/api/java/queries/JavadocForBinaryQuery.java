/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.api.java.queries;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.spi.java.queries.JavadocForBinaryQueryImplementation;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.WeakListeners;

/**
 * A query to find Javadoc root for the given classpath root.
 * @author David Konecny, Jesse Glick
 * @since org.netbeans.api.java/1 1.4
 */
public class JavadocForBinaryQuery {
    
    private static final Lookup.Result/*<JavadocForBinaryQueryImplementation>*/ implementations =
        Lookup.getDefault().lookup (new Lookup.Template (JavadocForBinaryQueryImplementation.class));

    private JavadocForBinaryQuery () {
    }

    /**
     * Find Javadoc information for a classpath root containing Java classes.
     * @param binary URL of a classpath root
     * @return a result object encapsulating the answer (never null)
     */
    public static Result findJavadoc(URL binary) {
        assert !FileUtil.isArchiveFile(binary) : binary;
        List/*<Result>*/ results = new ArrayList();
        Iterator it = implementations.allInstances().iterator();
        while (it.hasNext()) {
            Result r = ((JavadocForBinaryQueryImplementation) it.next()).findJavadoc(binary);
            if (r != null) {
                results.add(r);
            }
        }
        if (!results.isEmpty()) {
            return new ProxyResult(results);
        } else {
            return EMPTY_RESULT;
        }
    }

    /**
     * Result of finding Javadoc, encapsulating the answer as well as the
     * ability to listen to it.
     */
    public interface Result {
        
        /**
         * Get the Javadoc roots.
         * Each root should contain the main <code>index.html</code>, so that
         * for a class <samp>pkg.Class</samp> the generated documentation would
         * have a path <samp>pkg/Class.html</samp> relative to one of the roots.
         * @return array of roots of Javadoc documentation (may be empty but not null)
         */
        URL[] getRoots();
        
        /**
         * Add a listener to changes in the roots.
         * @param l a listener to add
         */
        void addChangeListener(ChangeListener l);
        
        /**
         * Remove a listener to changes in the roots.
         * @param l a listener to remove
         */
        void removeChangeListener(ChangeListener l);
        
    }
    
    private static final Result EMPTY_RESULT = new EmptyResult();
    private static final class EmptyResult implements Result {
        private static final URL[] NO_ROOTS = new URL[0];
        EmptyResult() {}
        public URL[] getRoots() {
            return NO_ROOTS;
        }
        public void addChangeListener(ChangeListener l) {}
        public void removeChangeListener(ChangeListener l) {}
    }
    
    private static final class ProxyResult implements Result, ChangeListener {
        
        private final List/*<Result>*/ delegates;
        private List/*<ChangeListener>*/ listeners = null;
        private final ChangeListener listener = WeakListeners.change(this, null);
        
        ProxyResult(List/*<Result>*/ delegates) {
            this.delegates = delegates;
        }
        
        public URL[] getRoots() {
            List/*<URL>*/ roots = new ArrayList();
            Iterator it = delegates.iterator();
            while (it.hasNext()) {
                Result r = (Result) it.next();
                roots.addAll(Arrays.asList(r.getRoots()));
            }
            return (URL[]) roots.toArray(new URL[roots.size()]);
        }
        
        public void addChangeListener(ChangeListener l) {
            if (listeners == null) {
                listeners = new ArrayList();
                Iterator it = delegates.iterator();
                while (it.hasNext()) {
                    Result r = (Result) it.next();
                    r.addChangeListener(listener);
                }
            }
            listeners.add(l);
        }
        
        public void removeChangeListener(ChangeListener l) {
            listeners.remove(l);
            if (listeners.isEmpty()) {
                listeners = null;
                Iterator it = delegates.iterator();
                while (it.hasNext()) {
                    Result r = (Result) it.next();
                    r.removeChangeListener(listener);
                }
            }
        }
        
        public void stateChanged(ChangeEvent e) {
            if (listeners == null) {
                return;
            }
            ChangeEvent e2 = new ChangeEvent(this);
            Iterator it = listeners.iterator();
            while (it.hasNext()) {
                ((ChangeListener) it.next()).stateChanged(e2);
            }
        }
        
    }
    
}
