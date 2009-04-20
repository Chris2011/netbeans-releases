/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
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

package org.netbeans.modules.debugger.jpda.ui.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.Action;

import org.netbeans.spi.debugger.ContextProvider;
import org.netbeans.api.debugger.jpda.Field;
import org.netbeans.api.debugger.jpda.ObjectVariable;
import org.netbeans.spi.viewmodel.NodeActionsProvider;
import org.netbeans.spi.viewmodel.NodeModel;
import org.netbeans.spi.viewmodel.TableModel;
import org.netbeans.spi.viewmodel.TreeModel;
import org.netbeans.spi.viewmodel.TreeModelFilter;
import org.netbeans.spi.viewmodel.ModelListener;
import org.netbeans.spi.viewmodel.UnknownTypeException;
//import org.netbeans.modules.debugger.jpda.ui.FixedWatch;
import org.openide.util.NbBundle;


/**
 * Filters some original tree of nodes (represented by {@link TreeModel}).
 *
 * @author   Jan Jancura
 */
public class VariablesTreeModelFilterSI implements TreeModelFilter, 
NodeModel, TableModel, NodeActionsProvider {

    public static final String INHERITED =
        "org/netbeans/modules/debugger/resources/watchesView/SuperVariable";
    public static final String STATIC =
        "org/netbeans/modules/debugger/resources/watchesView/SuperVariable";
    private static final Set ignore = new HashSet (Arrays.asList (new String[] {
        "java.lang.String",
        "java.lang.StringBuffer",
        "java.lang.Character",
        "java.lang.Integer",
        "java.lang.Float",
        "java.lang.Byte",
        "java.lang.Boolean",
        "java.lang.Double",
        "java.lang.Long",
        "java.lang.Short",

        "java.util.ArrayList",
        "java.util.HashSet",
        "java.util.LinkedHashSet",
        "java.util.LinkedList",
        "java.util.Stack",
        "java.util.TreeSet",
        "java.util.Vector",
        "java.util.Hashtable",
        "java.util.Hashtable$Entry",
        "java.util.HashMap",
        "java.util.HashMap$Entry",
        "java.util.IdentityHashMap",
        "java.util.AbstractMap$SimpleEntry",
        "java.util.TreeMap",
        "java.util.TreeMap$Entry",
        "java.util.WeakHashMap",
        "java.util.LinkedHashMap",
        "java.util.LinkedHashMap$Entry",
    }));
    private ContextProvider lookupProvider;
    
    
    public VariablesTreeModelFilterSI (ContextProvider lookupProvider) {
        this.lookupProvider = lookupProvider;
    }

    /** 
     * Returns filtered root of hierarchy.
     *
     * @param   original the original tree model
     * @return  filtered root of hierarchy
     */
    public Object getRoot (TreeModel original) {
        return original.getRoot ();
    }
    
    /** 
     * Returns filtered children for given parent on given indexes.
     *
     * @param   original the original tree model
     * @param   parent a parent of returned nodes
     * @throws  NoInformationException if the set of children can not be 
     *          resolved
     * @throws  ComputingException if the children resolving process 
     *          is time consuming, and will be performed off-line 
     * @throws  UnknownTypeException if this TreeModelFilter implementation is not
     *          able to resolve dchildren for given node type
     *
     * @return  children for given parent on given indexes
     */
    public Object[] getChildren (
        TreeModel   original, 
        Object      parent, 
        int         from, 
        int         to
    ) throws UnknownTypeException {
        //parent = switchParentIfFixedWatch(parent);
        if (parent instanceof ObjectVariable) {
            ObjectVariable variable = (ObjectVariable) parent;
            if (ignore.contains (variable.getType ()))
                return original.getChildren (parent, from, to);
            int tto = Math.min (to, original.getChildrenCount (parent));
            List l = new ArrayList (Arrays.asList (
                original.getChildren (parent, from, tto)
            ));
            if (l.size() < to && variable.getAllStaticFields(0, 0).length > 0)
                l.add (new StaticNode(variable));
            if (l.size() < to && variable.getInheritedFields(0, 0).length > 0)
                l.add (new InheritedNode(variable));
            return l.toArray ();
        } else if (parent instanceof SpecialNode) {
            return ((SpecialNode) parent).getChildren(0, 0);
        }
        return original.getChildren (parent, from, to);
    }

    /**
     * Returns number of filtered children for given variable.
     *
     * @param   original the original tree model
     * @param   parent a variable of returned fields
     *
     * @throws  NoInformationException if the set of children can not be 
     *          resolved
     * @throws  ComputingException if the children resolving process 
     *          is time consuming, and will be performed off-line 
     * @throws  UnknownTypeException if this TreeModelFilter implementation is not
     *          able to resolve dchildren for given node type
     *
     * @return  number of filtered children for given variable
     */
    public int getChildrenCount (
        TreeModel   original, 
        Object      parent
    ) throws UnknownTypeException {
        //parent = switchParentIfFixedWatch(parent);
        if (parent instanceof ObjectVariable) {
            ObjectVariable variable = (ObjectVariable) parent;
            if (ignore.contains (variable.getType ()))
                return original.getChildrenCount (parent);
            int i = original.getChildrenCount (parent);
            if (i == Integer.MAX_VALUE) {
                return i;
            }
            if (variable.getAllStaticFields (0, 0).length > 0) i++;
            if (variable.getInheritedFields (0, 0).length > 0) i++;
            return i;
        } else if (parent instanceof SpecialNode) {
            return ((SpecialNode) parent).getChildren(0, 0).length;
        }
        return original.getChildrenCount (parent);
    }

//    private Object switchParentIfFixedWatch (Object parent) {
//        if (parent instanceof FixedWatch) {
//            FixedWatch fw = (FixedWatch) parent;
//            if (fw.getVariable() instanceof ObjectVariable) {
//                return fw.getVariable();
//            }
//        }
//        return parent;
//    }

    /**
     * Returns true if node is leaf.
     * 
     * @param   original the original tree model
     * @throws  UnknownTypeException if this TreeModel implementation is not
     *          able to resolve dchildren for given node type
     * @return  true if node is leaf
     */
    public boolean isLeaf (
        TreeModel original, 
        Object node
    ) throws UnknownTypeException {
        return (node instanceof SpecialNode) ? false : original.isLeaf(node);
    }

    public void addModelListener (ModelListener l) {
    }

    public void removeModelListener (ModelListener l) {
    }
    
    
    // NodeModelFilter
    
    public String getDisplayName (Object node) 
    throws UnknownTypeException {
        if (node instanceof SpecialNode) return ((SpecialNode) node).getDisplayName();
        throw new UnknownTypeException (node);
    }
    
    public String getIconBase (Object node) 
    throws UnknownTypeException {
        if (node instanceof SpecialNode) return ((SpecialNode) node).getIconBase();
        throw new UnknownTypeException (node);
    }
    
    public String getShortDescription (Object node) 
    throws UnknownTypeException {
        if (node instanceof SpecialNode) return null;
        throw new UnknownTypeException (node);
    }
    
    
    // NodeActionsProviderFilter
    
    public Action[] getActions (
        Object node
    ) throws UnknownTypeException {
        if (node instanceof SpecialNode) return new Action [0];
        throw new UnknownTypeException (node);
    }
    
    public void performDefaultAction (
        Object node
    ) throws UnknownTypeException {
        if (node instanceof SpecialNode) return;
        throw new UnknownTypeException (node);
    }
    
    
    // TableModelFilter
    
    public Object getValueAt (
        Object row, 
        String columnID
    ) throws UnknownTypeException {
        if (row instanceof SpecialNode) return "";
        throw new UnknownTypeException (row);
    }
    
    public boolean isReadOnly (
        Object row, 
        String columnID
    ) throws UnknownTypeException {
        if (row instanceof SpecialNode) return true;
        throw new UnknownTypeException (row);
    }
    
    public void setValueAt (
        Object row, 
        String columnID, 
        Object value
    ) throws UnknownTypeException {
        if (row instanceof SpecialNode) return;
        throw new UnknownTypeException (row);
    }
    
    private static class StaticNode extends SpecialNode {

        StaticNode(ObjectVariable parent) {
            super(parent);
        }

        Field [] getChildren(int from, int to) {
            return object.getAllStaticFields(0, 0);
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof StaticNode)) return false;
            return object.equals(((StaticNode) o).object);
        }

        String getDisplayName() {
            return NbBundle.getBundle(VariablesTreeModelFilterSI.class).getString("MSG_VariablesFilter_StaticNode");    // NOI18N
        }

        String getIconBase() {
            return STATIC;
        }
    }

    private static class InheritedNode extends SpecialNode {

        InheritedNode(ObjectVariable object) {
            super(object);
        }

        Field [] getChildren(int from, int to) {
            return object.getInheritedFields(0, 0);
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof InheritedNode)) return false;
            return object.equals(((InheritedNode) o).object);
        }

        String getDisplayName() {
            return NbBundle.getBundle(VariablesTreeModelFilterSI.class).getString("MSG_VariablesFilter_InheritedNode");    // NOI18N
        }

        String getIconBase() {
            return INHERITED;
        }
    }

    private static abstract class SpecialNode {
        protected ObjectVariable object;

        protected SpecialNode(ObjectVariable parent) {
            this.object = parent;
        }

        public int hashCode() {
            return object.hashCode();
        }

        abstract Field [] getChildren(int from, int to);
        abstract String getDisplayName();
        abstract String getIconBase();
    }

    // helper methods ..........................................................
}
