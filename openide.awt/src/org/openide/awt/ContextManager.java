/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.openide.awt;

import java.awt.event.ActionEvent;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Item;
import org.openide.util.Lookup.Result;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Mutex;
import org.openide.util.Utilities;
import org.openide.util.WeakSet;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;


/** Listener on a global context.
 */
class ContextManager extends Object {
    
    private static final Map<LookupRef, Reference<ContextManager>> CACHE = new HashMap<LookupRef, Reference<ContextManager>>();
    private static final Map<LookupRef, Reference<ContextManager>> SURVIVE = new HashMap<LookupRef, Reference<ContextManager>>();

    private Map<Class,LSet> listeners;
    private Lookup lookup;
    private LSet<Lookup.Provider> selectionAll;
    
    private ContextManager(Lookup lookup) {
        this.listeners = new HashMap<Class,LSet>();
        this.lookup = lookup;
    }
    
    public static ContextManager findManager(Lookup context, boolean survive) {
        synchronized (CACHE) {
            Map<LookupRef, Reference<ContextManager>> map = survive ? SURVIVE : CACHE;
            LookupRef lr = new LookupRef(context);
            Reference<ContextManager> ref = map.get(lr);
            ContextManager g = ref == null ? null : ref.get();
            if (g == null) {
                g = survive ? new SurviveManager(context) : new ContextManager(context);
                ref = new GMReference(g, lr, survive);
                map.put(lr, ref);
            }
            return g;
        }
    }
    
    static void clearCache(LookupRef lr, GMReference ref, boolean survive) {
        synchronized (CACHE) {
            Map<LookupRef, Reference<ContextManager>> map = survive ? SURVIVE : CACHE;
            if (map.get(lr) == ref) {
                map.remove(lr);
            }
        }
    }
    
    public <T> void registerListener(Class<T> type, ContextAction<T> a) {
        synchronized (CACHE) {
            LSet<T> existing = findLSet(type);
            if (existing == null) {
                Lookup.Result<T> result = createResult(lookup.lookupResult(type));
                existing = new LSet<T>(result, type);
                listeners.put(type, existing);
            }
            existing.add(a);
            // TBD: a.updateState(new ActionMap(), actionMap.get());
            
            if (a.selectMode == ContextSelection.ALL) {
                if (selectionAll == null) {
                    Lookup.Result<Lookup.Provider> result = createResult(
                        lookup.lookupResult(Lookup.Provider.class)
                    );
                    selectionAll = new LSet<Lookup.Provider>(result, Lookup.Provider.class);
                }
                selectionAll.add(a);
            }
        }
    }

    public <T> void unregisterListener(Class<T> type, ContextAction<T> a) {
        synchronized (CACHE) {
            Set<ContextAction> existing = findLSet(type);
            if (existing != null) {
                existing.remove(a);
                if (existing.isEmpty()) {
                    listeners.remove(type);
                }
            }
            if (a.selectMode == ContextSelection.ALL && selectionAll != null) {
                selectionAll.remove(a);
                if (selectionAll.isEmpty()) {
                    selectionAll = null;
                }
            }
        }
    }
    
    /** Does not survive focus change */
    public boolean isSurvive() {
        return false;
    }

    /** Checks whether a type is enabled.
     */
    public <T> boolean isEnabled(Class<T> type, ContextSelection selectMode, ContextAction.Performer<? super T> enabler) {
        Lookup.Result<T> result = findResult(type);
        
        boolean e = isEnabledOnData(result, type, selectMode);
        if (e && enabler != null) {
            e = enabler.enabled(listFromResult(result));
        }
        
        return e;
    }
    
    private <T> boolean isEnabledOnData(Lookup.Result<T> result, Class<T> type, ContextSelection selectMode) {
        switch (selectMode) {
            case EXACTLY_ONE:
                Collection<Lookup.Item<T>> instances = new HashSet<Lookup.Item<T>>(result.allItems());
                return instances.size() == 1;
            case ANY:
                return !result.allItems().isEmpty();
            case EACH: {
                if (result.allItems().isEmpty()) {
                    return false;
                }
                Lookup.Result<Lookup.Provider> items = lookup.lookupResult(Lookup.Provider.class);
                if (result.allItems().size() != items.allItems().size()) {
                    return false;
                }
                Lookup.Template<T> template = new Lookup.Template<T>(type);
                for (Lookup.Provider prov : items.allInstances()) {
                    if (prov.getLookup().lookupItem(template) == null) {
                        return false;
                    }
                }
                return true;
            }
            case ALL: {
                if (result.allItems().isEmpty()) {
                    return false;
                }
                Lookup.Result<Lookup.Provider> items = lookup.lookupResult(Lookup.Provider.class);
                if (result.allItems().size() < items.allItems().size()) {
                    return false;
                }
                Lookup.Template<T> template = new Lookup.Template<T>(type);
                for (Lookup.Provider prov : items.allInstances()) {
                    if (prov.getLookup().lookupItem(template) == null) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private <T> LSet<T> findLSet(Class<T> type) {
        synchronized (CACHE) {
            return listeners.get(type);
        }
    }
    private <T> Lookup.Result<T> findResult(final Class<T> type) {
        LSet<T> lset = findLSet(type);
        Lookup.Result<T> result;
        if (lset != null) {
            result = lset.result;
        } else {
            result = lookup.lookupResult(type);
        }
        return result;
    }
    
    protected <T> Lookup.Result<T> createResult(Lookup.Result<T> res) {
        return res;
    }
    
    public <T> void actionPerformed(final ActionEvent e, ContextAction.Performer<? super T> perf, final Class<T> type, ContextSelection selectMode) {
        Lookup.Result<T> result = findResult(type);
        final List<? extends T> all = listFromResult(result);

        class LkpAE implements Lookup.Provider {
            private Lookup lookup;
            public Lookup getLookup() {
                if (lookup == null) {
                    lookup = new ProxyLookup(
                        Lookups.fixed(all.toArray()),
                        Lookups.exclude(ContextManager.this.lookup, type)
                    );
                }
                return lookup;
            }
        }

        perf.actionPerformed(e, Collections.unmodifiableList(all), new LkpAE());
    }

    @SuppressWarnings("unchecked") // XXX cast Collection<? extends T> -> List<? extends T> should not be unsafe, right? maybe javac bug
    private <T> List<? extends T> listFromResult(Lookup.Result<T> result) {
        List<? extends T> all;
        Collection<? extends T> col = result.allInstances();
        if (col instanceof List) {
            all = (List<? extends T>)col;
        } else {
            ArrayList<T> arr = new ArrayList<T>();
            arr.addAll(col);
            all = arr;
        }
        return all;
    }

    

    private static final class GMReference extends WeakReference<ContextManager> 
    implements Runnable {
        private LookupRef context;
        private boolean survive;
        
        public GMReference(ContextManager m, LookupRef context, boolean survive) {
            super(m, Utilities.activeReferenceQueue());
            this.context = context;
            this.survive = survive;
        }
        
        public void run() {
            clearCache(context, this, survive);
        }
    } // end of GMReference

    /** Manager with special behaviour.
     */
    private static final class SurviveManager extends ContextManager {
        private SurviveManager(Lookup context) {
            super(context);
        }
        
        @Override
        public boolean isSurvive() {
            return true;
        }

        @Override
        protected <T> Result<T> createResult(Result<T> res) {
            return new NeverEmptyResult<T>(res);
        }
    }
    
    private static final class NeverEmptyResult<T> extends Lookup.Result<T> {
        private final Lookup.Result<T> delegate;
        private Collection<? extends Item<T>> allItems = Collections.emptyList();
        private Collection<? extends T> allInstances = Collections.emptyList();
        private Set<Class<? extends T>> allClasses = Collections.emptySet();

        public NeverEmptyResult(Result<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public void addLookupListener(LookupListener l) {
            delegate.addLookupListener(l);
        }

        @Override
        public void removeLookupListener(LookupListener l) {
            delegate.removeLookupListener(l);
        }

        @Override
        public Collection<? extends Item<T>> allItems() {
            Collection<? extends Item<T>> res = delegate.allItems();
            if (!res.isEmpty()) {
                allItems = res;
            }
            return allItems;
        }

        @Override
        public Collection<? extends T> allInstances() {
            Collection<? extends T> res = delegate.allInstances();
            if (!res.isEmpty()) {
                allInstances = res;
            }
            return allInstances;
        }

        @Override
        public Set<Class<? extends T>> allClasses() {
            Set<Class<? extends T>> res = delegate.allClasses();
            if (!res.isEmpty()) {
                allClasses = res;
            }
            return allClasses;
        }

    } // end of NeverEmptyResult
    
    /** Special set, that is weakly holding its actions, but also
     * listens on changes in lookup.
     */
    private static final class LSet<T> extends WeakSet<ContextAction> 
    implements LookupListener, Runnable {
        final Lookup.Result<T> result;
        
        public LSet(Lookup.Result<T> context, Class<T> type) {
            this.result = context;
            this.result.addLookupListener(this);
            // activate listener
            this.result.allItems();
        }

        @Override
        public void resultChanged(LookupEvent ev) {
            Mutex.EVENT.readAccess(this);
        }
        
        @Override
        public void run() {
            ContextAction[] arr;
            synchronized (CACHE) {
                arr = toArray(new ContextAction[0]);
            }
            for (ContextAction a : arr) {
                a.updateState();
            }
        }
    }

    static class LookupRef extends WeakReference<Lookup> {
        private final int hashCode;

        public LookupRef(Lookup referent) {
            super(referent);
            hashCode = System.identityHashCode(referent);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof LookupRef) {
                LookupRef lr = (LookupRef)obj;
                return get() == lr.get();
            }
            return false;
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

    }
}

