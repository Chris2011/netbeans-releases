/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.viewmodel;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyEditor;
import java.lang.IllegalAccessException;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.WeakHashMap;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import org.netbeans.spi.viewmodel.ColumnModel;
import org.netbeans.spi.viewmodel.ComputingException;
import org.netbeans.spi.viewmodel.NoInformationException;
import org.netbeans.spi.viewmodel.UnknownTypeException;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.lookup.Lookups;


/**
 *
 * @author   Jan Jancura
 */
public class TreeModelNode extends AbstractNode {

    
    // variables ...............................................................

    private CompoundModel       model;
    private TreeModelRoot       treeModelRoot;
    private Object              object;

    
    // init ....................................................................

    /**
    * Creates root of call stack for given producer.
    */
    public TreeModelNode ( 
        CompoundModel model, 
        TreeModelRoot treeModelRoot,
        Object object
    ) {
        super (
            createChildren (model, treeModelRoot, object),
            Lookups.singleton (object)
        );
        this.model = model;
        this.treeModelRoot = treeModelRoot;
        this.object = object;
        treeModelRoot.registerNode (object, this); 
        refresh ();
        initProperties ();
    }
    
    private void initProperties () {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set ps = Sheet.createPropertiesSet ();
        ColumnModel[] columns = model.getColumns ();
        int i, k = columns.length;
        for (i = 0; i < k; i++)
            ps.put (new MyProperty (columns [i]));
        sheet.put (ps);
        setSheet (sheet);
    }
    
    private static Children createChildren (
        CompoundModel model, 
        TreeModelRoot treeModelRoot,
        Object object
    ) {
        if (object == null) 
            throw new NullPointerException ();
        try {
            return model.isLeaf (object) ? 
                Children.LEAF : 
                new TreeModelChildren (model, treeModelRoot, object);
        } catch (UnknownTypeException e) {
            e.printStackTrace ();
            return Children.LEAF;
        }
    }
    
    public String getName () {
        try {
            return model.getDisplayName (object);
        } catch (UnknownTypeException e) {
            e.printStackTrace ();
            return object.toString ();
        } catch (ComputingException e) {
            return "Computing";
        }
    }
    
    public String getDisplayName () {
        try {
            return model.getDisplayName (object);
        } catch (UnknownTypeException e) {
            e.printStackTrace ();
            return object.toString ();
        } catch (ComputingException e) {
            return "Computing";
        }
    }
    
    public String getShortDescription () {
        try {
            return model.getShortDescription (object);
        } catch (UnknownTypeException e) {
            e.printStackTrace ();
            System.out.println (model);
            System.out.println ();
            return null;
        } catch (ComputingException e) {
            return "Computing";
        }
    }

    void refresh () {
//        try {
//            setDisplayName (model.getDisplayName (object));
//        } catch (UnknownTypeException e) {
//            setDisplayName (object.toString ());
//            e.printStackTrace ();
//        } catch (ComputingException e) {
//            setDisplayName ("Computing");
//        }
        try {
            String iconBase = model.getIconBase (object);
            if (iconBase != null)
                setIconBase (iconBase);
            else
                setIconBase ("org/openide/resources/actions/empty");
        } catch (UnknownTypeException e) {
            e.printStackTrace ();
            System.out.println (model);
            System.out.println ();
        } catch (ComputingException e) {
            setIconBase ("org/openide/resources/actions/empty");
        }
        Children ch = getChildren ();
        if (ch instanceof TreeModelChildren)
            ((TreeModelChildren) ch).refreshChildren ();
    }
    
    public Action[] getActions (boolean context) {
        if (context) 
            return treeModelRoot.getRootNode ().getActions (false);
        try {
            return model.getActions (object);
        } catch (UnknownTypeException e) {
//            e.printStackTrace ();
//            System.out.println (model);
//            System.out.println ();
            return new Action [0];
        }
    }
    
    public Action getPreferredAction () {
        return new AbstractAction () {
            public void actionPerformed (ActionEvent e) {
                try {
                    model.performDefaultAction (object);
                } catch (UnknownTypeException ex) {
                    ex.printStackTrace ();
                    System.out.println (model);
                    System.out.println ();
                }
            }
        };
    }
    
    void setObject (Object o) {
        object = o;
        Children ch = getChildren ();
        if (ch instanceof TreeModelChildren)
            ((TreeModelChildren) ch).object = o;
        refresh ();
    }
    
    public Object getObject () {
        return object;
    }
    
    public boolean canDestroy () {
        try {
            Action[] as = model.getActions (object);
            int i, k = as.length;
            for (i = 0; i < k; i++) {
                if (as [i] == null) continue;
                Object key = as [i].getValue (Action.ACCELERATOR_KEY);
                if ( (key != null) &&
                     (key.equals (KeyStroke.getKeyStroke ("DELETE")))
                ) return as [i].isEnabled ();
            }
            return false;
        } catch (UnknownTypeException e) {
//            e.printStackTrace ();
//            System.out.println (model);
//            System.out.println ();
            return false;
        }
    }
    
    public boolean canCopy () {
        return false;
    }
    
    public boolean canCut () {
        return false;
    }
    
    public void destroy () {
        try {
            Action[] as = model.getActions (object);
            int i, k = as.length;
            for (i = 0; i < k; i++) {
                if (as [i] == null) continue;
                Object key = as [i].getValue (Action.ACCELERATOR_KEY);
                if ( (key != null) &&
                     (key.equals (KeyStroke.getKeyStroke ("DELETE")))
                ) {
                    as [i].actionPerformed (null);
                    return;
                }
            }
        } catch (UnknownTypeException e) {
            e.printStackTrace ();
            System.out.println (model);
            System.out.println ();
        }
    }
    
    
    // innerclasses ............................................................
    
    /** Special locals subnodes (children) */
    private static final class TreeModelChildren extends Children.Keys {
            
        private boolean             initialezed = false;
        private CompoundModel       model;
        private TreeModelRoot       treeModelRoot;
        private Object              object;
        private WeakHashMap         objectToNode = new WeakHashMap ();
        
        
        TreeModelChildren (
            CompoundModel model,
            TreeModelRoot treeModelRoot,
            Object object
        ) {
            this.model = model;
            this.treeModelRoot = treeModelRoot;
            this.object = object;
        }
        
        protected void addNotify () {
            initialezed = true;
            refreshChildren ();
        }
        
        protected void removeNotify () {
            initialezed = false;
            setKeys (Collections.EMPTY_SET);
        }
        
        void refreshChildren () {
            if (!initialezed) return;
            try {
                Object[] ch = model.getChildren (
                    object, 
                    0, 
                    model.getChildrenCount (object)
                );
                int i, k = ch.length; 
                WeakHashMap newObjectToNode = new WeakHashMap ();
                for (i = 0; i < k; i++) {
                    if (ch [i] == null) {
                        System.out.println("model: " + model);
                        System.out.println("parent: " + object);
                        throw new NullPointerException ();
                    }
                    WeakReference wr = (WeakReference) objectToNode.get 
                        (ch [i]);
                    if (wr == null) continue;
                    TreeModelNode tmn = (TreeModelNode) wr.get ();
                    if (tmn == null) continue;
                    tmn.setObject (ch [i]);
                    newObjectToNode.put (ch [i], wr);
                }
                objectToNode = newObjectToNode;
                setKeys (ch);
            } catch (UnknownTypeException e) {
                setKeys (new Object [0]);
                e.printStackTrace ();
                System.out.println (model);
                System.out.println ();
            } catch (NoInformationException e) {
                setKeys (new Object[] {e});
            } catch (ComputingException e) {
                setKeys (new Object[] {e});
            }
        }
        
//        protected void destroyNodes (Node[] nodes) {
//            int i, k = nodes.length;
//            for (i = 0; i < k; i++) {
//                TreeModelNode tmn = (TreeModelNode) nodes [i];
//                String name = null;
//                try {
//                    name = model.getDisplayName (tmn.object);
//                } catch (ComputingException e) {
//                } catch (UnknownTypeException e) {
//                }
//                if (name != null)
//                    nameToChild.remove (name);
//            }
//        }
        
        protected Node[] createNodes (Object object) {
            if (object instanceof Exception)
                return new Node[] {
                    new ExceptionNode ((Exception) object)
                };
            TreeModelNode tmn = new TreeModelNode (
                model, 
                treeModelRoot, 
                object
            );
            objectToNode.put (object, new WeakReference (tmn));
            return new Node[] {tmn};
        }
    } // ItemChildren
    
    private class MyProperty extends PropertySupport {
        
        private String      id;
        private ColumnModel columnModel;
        
        
        MyProperty (
            ColumnModel columnModel
        ) {
            super (
                columnModel.getID (),
                columnModel.getType (),
                columnModel.getDisplayName (),
                columnModel.getShortDescription (), 
                true,
                true
            );
            this.columnModel = columnModel;
            id = columnModel.getID ();
        }
        

        /* Can write the value of the property.
        * Returns the value passed into constructor.
        * @return <CODE>true</CODE> if the read of the value is supported
        */
        public boolean canWrite () {
            try {
                return !model.isReadOnly (object, columnModel.getID ());
            } catch (UnknownTypeException e) {
                e.printStackTrace ();
                System.out.println("  Column id:" + columnModel.getID ());
                System.out.println (model);
                System.out.println ();
                return false;
            }
        }
        
        public Object getValue () {
            try {
                return model.getValueAt (object, id);
            } catch (ComputingException e) {
            } catch (UnknownTypeException e) {
                e.printStackTrace ();
                System.out.println("  Column id:" + columnModel.getID ());
                System.out.println (model);
                System.out.println ();
            }
            return null;
        }
        
        public void setValue (Object v) throws IllegalAccessException, 
        IllegalArgumentException, java.lang.reflect.InvocationTargetException {
            try {
                model.setValueAt (object, id, v);
                TreeModelNode.this.firePropertyChange (null, null, null);
            } catch (UnknownTypeException e) {
                e.printStackTrace ();
                System.out.println("  Column id:" + columnModel.getID ());
                System.out.println (model);
                System.out.println ();
            }
        }
        
        public PropertyEditor getPropertyEditor () {
            return columnModel.getPropertyEditor ();
        }
    }
}

