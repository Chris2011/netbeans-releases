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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.debugger.ui.models;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;
import org.netbeans.api.debugger.Breakpoint;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.DebuggerManagerAdapter;
import org.netbeans.api.debugger.Properties;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.spi.viewmodel.ModelEvent;
import org.netbeans.spi.viewmodel.ModelListener;
import org.netbeans.spi.viewmodel.TreeModel;
import org.netbeans.spi.viewmodel.UnknownTypeException;
import org.openide.util.WeakListeners;


/**
 * @author   Jan Jancura
 */
public class BreakpointsTreeModel implements TreeModel {
    
    private static Logger logger = Logger.getLogger(BreakpointsTreeModel.class.getName());

    private Listener listener;
    private Vector listeners = new Vector ();
    private static final Properties bpProperties = Properties.getDefault().getProperties("Breakpoints");
    private PropertyChangeListener pchl, oppchl;
    private Reference<Object[]> lastGroupsAndBreakpoints = new SoftReference<Object[]>(null);
    private final Object lastGroupsAndBreakpointsLock = new Object();
    private final Set<Breakpoint> closedProjectsBreakpoints = new IdentityHashSet<>();
    
    /** 
     *
     * @return threads contained in this group of threads
     */
    public Object getRoot () {
        return ROOT;
    }
    
    /** 
     *
     * @return groups and breakpoints contained in this group of breakpoints
     */
    public Object[] getChildren (Object parent, int from, int to)
    throws UnknownTypeException {
        if (parent == ROOT) {
            if (listener == null) {
                listener = new Listener (this);
            }
            if (pchl == null) {
                pchl = new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        fireTreeChanged();
                    }
                };
                bpProperties.addPropertyChangeListener(WeakListeners.propertyChange(pchl, bpProperties));
            }
            boolean openProjectsOnly = bpProperties.getBoolean(BreakpointGroup.PROP_FROM_OPEN_PROJECTS, true);
            if (openProjectsOnly) {
                oppchl = WeakListeners.propertyChange(pchl, OpenProjects.getDefault());
                OpenProjects.getDefault().addPropertyChangeListener(oppchl);
            } else {
                if (oppchl != null) {
                    OpenProjects.getDefault().removePropertyChangeListener(oppchl);
                }
                oppchl = null;
            }
            Object[] groupsAndBreakpoints;
            synchronized (lastGroupsAndBreakpointsLock) {
                groupsAndBreakpoints = lastGroupsAndBreakpoints.get();
            }
            if (groupsAndBreakpoints == null) {
                Set<Breakpoint> cpb = new IdentityHashSet<>();
                groupsAndBreakpoints = BreakpointGroup.createGroups(bpProperties, cpb);
                synchronized (lastGroupsAndBreakpointsLock) {
                    lastGroupsAndBreakpoints = new SoftReference<Object[]>(groupsAndBreakpoints);
                    closedProjectsBreakpoints.clear();
                    closedProjectsBreakpoints.addAll(cpb);
                }
            }
            if (to == 0 || to >= groupsAndBreakpoints.length && from == 0) {
                return groupsAndBreakpoints;
            } else {
                int n = groupsAndBreakpoints.length;
                to = Math.min(n, to);
                from = Math.min(n, from);
                Object[] r = new Object[to - from];
                System.arraycopy(groupsAndBreakpoints, from, r, 0, r.length);
                return r;
            }
        } else if (parent instanceof BreakpointGroup) {
            return ((BreakpointGroup) parent).getGroupsAndBreakpoints();
        } else
        throw new UnknownTypeException (parent);
    }
    
    /**
     * Returns number of children for given node.
     * 
     * @param   node the parent node
     * @throws  UnknownTypeException if this TreeModel implementation is not
     *          able to resolve children for given node type
     *
     * @return  true if node is leaf
     */
    public int getChildrenCount (Object node) throws UnknownTypeException {
        if (node == ROOT) {
            // Performance, see issue #59058.
            return Integer.MAX_VALUE;
            //return getChildren (node, 0, 0).length;
        } else
        if (node instanceof BreakpointGroup) {
            // Performance, see issue #59058.
            return Integer.MAX_VALUE;
            //return getChildren (node, 0, 0).length;
        } else
        throw new UnknownTypeException (node);
    }
    
    public boolean isLeaf (Object node) throws UnknownTypeException {
        if (node == ROOT) return false;
        if (node instanceof Breakpoint) return true;
        if (node instanceof BreakpointGroup) return false;
        throw new UnknownTypeException (node);
    }

    public void addModelListener (ModelListener l) {
        listeners.add (l);
    }

    public void removeModelListener (ModelListener l) {
        listeners.remove (l);
    }
    
    private void fireTreeChanged () {
        synchronized (lastGroupsAndBreakpointsLock) {
            lastGroupsAndBreakpoints = new SoftReference<Object[]>(null);
        }
        Vector v = (Vector) listeners.clone ();
        int i, k = v.size ();
        for (i = 0; i < k; i++)
            ((ModelListener) v.get (i)).modelChanged (
                new ModelEvent.TreeChanged (this)
            );
    }
    
    private void fireTreeChanged (ModelEvent me) {
        Vector v = (Vector) listeners.clone ();
        int i, k = v.size ();
        for (i = 0; i < k; i++)
            ((ModelListener) v.get (i)).modelChanged (me);
    }
    
    private boolean isClosedProjectBreakpoint(Breakpoint b) {
        synchronized (lastGroupsAndBreakpointsLock) {
            return closedProjectsBreakpoints.contains(b);
        }
    }
    
    
    // innerclasses ............................................................
    
    private static class Listener extends DebuggerManagerAdapter implements 
    PropertyChangeListener {
        
        private WeakReference model;
        
        public Listener (
            BreakpointsTreeModel tm
        ) {
            model = new WeakReference (tm);
            DebuggerManager.getDebuggerManager ().addDebuggerListener (
                DebuggerManager.PROP_BREAKPOINTS,
                this
            );
            DebuggerManager.getDebuggerManager ().addDebuggerListener (
                DebuggerManager.PROP_CURRENT_SESSION,
                this
            );
            Breakpoint[] bs = DebuggerManager.getDebuggerManager ().
                getBreakpoints ();
            int i, k = bs.length;
            for (i = 0; i < k; i++)
                bs [i].addPropertyChangeListener (this);
        }
        
        private BreakpointsTreeModel getModel () {
            BreakpointsTreeModel m = (BreakpointsTreeModel) model.get ();
            if (m == null) {
                DebuggerManager.getDebuggerManager ().removeDebuggerListener (
                    DebuggerManager.PROP_BREAKPOINTS,
                    this
                );
                DebuggerManager.getDebuggerManager ().removeDebuggerListener (
                    DebuggerManager.PROP_CURRENT_SESSION,
                    this
                );
                Breakpoint[] bs = DebuggerManager.getDebuggerManager ().
                    getBreakpoints ();
                int i, k = bs.length;
                for (i = 0; i < k; i++)
                    bs [i].removePropertyChangeListener (this);
            }
            return m;
        }

        @Override
        public void breakpointAdded (Breakpoint breakpoint) {
            BreakpointsTreeModel m = getModel ();
            if (m == null) return;
            breakpoint.addPropertyChangeListener (this);
            m.fireTreeChanged ();
        }
        
        @Override
        public void breakpointRemoved (Breakpoint breakpoint) {
            BreakpointsTreeModel m = getModel ();
            if (m == null) return;
            breakpoint.removePropertyChangeListener (this);
            m.fireTreeChanged ();
        }
    
        @Override
        public void propertyChange (PropertyChangeEvent evt) {
            BreakpointsTreeModel m = getModel ();
            if (m == null) return;
            String propertyName = evt.getPropertyName();
            if (propertyName == DebuggerManager.PROP_CURRENT_SESSION) {
                m.fireTreeChanged ();
            }
            if (! (evt.getSource () instanceof Breakpoint))
                return;
            if (propertyName == Breakpoint.PROP_GROUP_NAME) {
                m.fireTreeChanged ();
            } else {
                if (propertyName == Breakpoint.PROP_VALIDITY) {
                    Breakpoint b = (Breakpoint) evt.getSource();
                    if (m.isClosedProjectBreakpoint(b)) {
                        m.fireTreeChanged ();
                        return ;
                    }
                }

                m.fireTreeChanged (new ModelEvent.NodeChanged(
                        m, evt.getSource ()));
                if (propertyName == Breakpoint.PROP_ENABLED) {
                    Breakpoint bp = (Breakpoint) evt.getSource ();
                    String groupName = bp.getGroupName();
                    if (groupName != null) {
                        m.fireTreeChanged (new ModelEvent.NodeChanged(
                            m, groupName));
                    }
                }
            }
        }
    }
    
}
