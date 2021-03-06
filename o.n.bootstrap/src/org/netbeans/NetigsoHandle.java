/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Jaroslav Tulach <jtulach@netbeans.org>
 */
final class NetigsoHandle {
    private final ModuleManager mgr;
    // @GuardedBy("toEnable")
    private final ArrayList<Module> toEnable = new ArrayList<Module>();
    // @GuardedBy("NetigsoFramework.class")    
    private NetigsoFramework framework;
    // @GuardedBy("this")
    private List<NetigsoModule> toInit = new ArrayList<NetigsoModule>();
    
    NetigsoHandle(ModuleManager mgr) {
        this.mgr = mgr;
    }
    
    final NetigsoFramework getDefault() {
        return getDefault(null);
    }

    private NetigsoFramework getDefault(Lookup lkp) {
        synchronized (NetigsoFramework.class) {
            if (framework != null) {
                return framework;
            }
        }
        
        NetigsoFramework created = null;
        if (lkp != null) {
            NetigsoFramework prototype = lkp.lookup(NetigsoFramework.class);
            if (prototype == null) {
                throw new IllegalStateException("No NetigsoFramework found, is org.netbeans.core.netigso module enabled?"); // NOI18N
            }
            created = prototype.bindTo(mgr);
        }
        
        synchronized (NetigsoFramework.class) {
            if (framework == null && created != null) {
                framework = created;
            }
            return framework;
        }
    }
    /** Used on shutdown */
    final void shutdownFramework() {
        NetigsoFramework f;
        synchronized (NetigsoFramework.class) {
            f = framework;
            framework = null;
            toInit = new ArrayList<NetigsoModule>();
            synchronized (toEnable) {
                toEnable.clear();
            }
        }
        if (f != null) {
            f.shutdown();
        }
    }

    final void willEnable(List<Module> newlyEnabling) {
        synchronized (toEnable) {
            toEnable.addAll(newlyEnabling);
        }
    }

    final Set<Module> turnOn(ClassLoader findNetigsoFrameworkIn, Collection<Module> allModules) throws InvalidException {
        boolean found = false;
        if (getDefault() == null) {
            synchronized (toEnable) {
                for (Module m : toEnable) {
                    if (m instanceof NetigsoModule) {
                        found = true;
                        break;
                    }
                }
            }
        } else {
            found = true;
        }
        if (!found) {
            return Collections.emptySet();
        }
        final Lookup lkp = Lookups.metaInfServices(findNetigsoFrameworkIn);
        getDefault(lkp).prepare(lkp, allModules);
        synchronized (toEnable) {
            toEnable.clear();
            toEnable.trimToSize();
        }
        delayedInit(mgr);
        Set<String> cnbs = getDefault().start(allModules);
        if (cnbs == null) {
            return Collections.emptySet();
        }

        Set<Module> additional = new HashSet<Module>();
        for (Module m : allModules) {
            if (!m.isEnabled() && cnbs.contains(m.getCodeNameBase())) {
                additional.add(m);
            }
        }
        return additional;
    }

    private boolean delayedInit(ModuleManager mgr) throws InvalidException {
        List<NetigsoModule> init;
        synchronized (this) {
            init = toInit;
            toInit = null;
            if (init == null || init.isEmpty()) {
                return true;
            }
        }
        Set<NetigsoModule> problematic = new HashSet<NetigsoModule>();
        for (NetigsoModule nm : init) {
            try {
                nm.start();
            } catch (IOException ex) {
                nm.setEnabled(false);
                InvalidException invalid = new InvalidException(nm, ex.getMessage());
                nm.setProblem(invalid);
                problematic.add(nm);
            }
        }
        if (!problematic.isEmpty()) {
            mgr.getEvents().log(Events.FAILED_INSTALL_NEW, problematic);
        }
        
        return problematic.isEmpty();
    }

    synchronized void classLoaderUp(NetigsoModule nm) throws IOException {
        if (toInit != null) {
            toInit.add(nm);
            return;
        }
        List<Module> clone;
        synchronized (toEnable) {
            clone = (List<Module>) toEnable.clone();
            toEnable.clear();
        }
        if (!clone.isEmpty()) {
            getDefault().prepare(Lookup.getDefault(), clone);
        }
        nm.start();
    }

    synchronized void classLoaderDown(NetigsoModule nm) {
        if (toInit != null) {
            toInit.remove(nm);
        }
    }

    final void startFramework() {
        if (getDefault() != null) {
            getDefault().start();
        }
    }


    final ClassLoader findFallbackLoader() {
        NetigsoFramework f = getDefault();
        if (f == null) {
            return null;
        }
        
        ClassLoader frameworkLoader = f.findFrameworkClassLoader();
        
        Class[] stack = TopSecurityManager.getStack();
        for (int i = 0; i < stack.length; i++) {
            ClassLoader sl = stack[i].getClassLoader();
            if (sl == null) {
                continue;
            }
            if (sl.getClass().getClassLoader() == frameworkLoader) {
                return stack[i].getClassLoader();
            }
        }
        return null;
    }
    
}
