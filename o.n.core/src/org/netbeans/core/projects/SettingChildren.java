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

package org.netbeans.core.projects;

import org.openide.nodes.*;
import org.openide.loaders.InstanceDataObject;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

import org.netbeans.beaninfo.editors.ListImageEditor;

import java.awt.Image;
import java.beans.PropertyEditor;
import java.beans.PropertyChangeEvent;
import java.lang.reflect.InvocationTargetException;

/** Filters nodes under the session node (displayed in Options dialog), adds special
 * properties to Nodes of particular settings to show/edit positions wher the
 * setting is defined on DefaultFileSystem.
 *
 * @author  Vitezslav Stejskal
 */
public final class SettingChildren extends FilterNode.Children {
    /** Name of Node.Property indicating if setting is defined on Project layer */
    public static final String PROP_INDICATOR = "Indicator"; // NOI18N
    /** Name of Node.Property showing status of Project layer according to the setting */
    public static final String PROP_LAYER_PROJECT = "Project-Layer"; // NOI18N
    /** Name of Node.Property showing status of Session layer according to the setting */
    public static final String PROP_LAYER_SESSION = "Session-Layer"; // NOI18N
    /** Name of Node.Property showing status of Modules layer according to the setting */
    public static final String PROP_LAYER_MODULES = "Modules-Layer"; // NOI18N

    public SettingChildren (Node original) {
        super (original);
    }
    
    protected Node copyNode (Node node) {
        InstanceDataObject ido = (InstanceDataObject) node.getCookie (InstanceDataObject.class);
        return ido != null ? new SettingFilterNode (node) : new FilterNode (node, new SettingChildren (node));
    }

    /** Property allowing display/manipulation of setting status for one specific layer. */
    public static class FileStateProperty extends PropertySupport {
        private FileObject primaryFile = null;
        private int layer;

        public FileStateProperty (String name) {
            this (null, 0, name, true);
        }

        public FileStateProperty (FileObject primaryFile, int layer, String name, boolean readonly) {
            super (name, Integer.class,
                NbBundle.getMessage (FileStateProperty.class, "LBL_FSP_" + name), // NOI18N
                NbBundle.getMessage (FileStateProperty.class, "LBL_FSP_Desc_" + name), // NOI18N
                true, !readonly);

            this.primaryFile = primaryFile;
            this.layer = layer;

            setValue (ListImageEditor.PROP_VALUES, new Integer [] {
                new Integer (FilePositionManager.FSTATE_DEFINED),
                new Integer (FilePositionManager.FSTATE_IGNORED),
                new Integer (FilePositionManager.FSTATE_INHERITED),
                new Integer (FilePositionManager.FSTATE_UNDEFINED)
            });

            setValue (ListImageEditor.PROP_IMAGES, new Image [] {
                Utilities.loadImage ("/org/netbeans/core/resources/setting-defined.gif"), // NOI18N
                Utilities.loadImage ("/org/netbeans/core/resources/setting-ignored.gif"), // NOI18N
                Utilities.loadImage ("/org/netbeans/core/resources/setting-inherited.gif"), // NOI18N
                Utilities.loadImage ("/org/netbeans/core/resources/empty.gif") // NOI18N
            });
        }

        public Object getValue () throws IllegalAccessException, InvocationTargetException {
            FilePositionManager fpm = FilePositionManager.getDefault ();
            return new Integer (fpm.getFileState (primaryFile, layer));
        }

        public void setValue (Object val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            throw new IllegalAccessException ();
        }

        public PropertyEditor getPropertyEditor () {
            return new ListImageEditor ();
        }
    }

    /** Read-only property indicating if specific setting is defined on Project layer. */
    public static class IndicatorProperty extends PropertySupport {
        private FileObject primaryFile = null;

        public IndicatorProperty () {
            this (null);
        }

        public IndicatorProperty (FileObject primaryFile) {
            super (PROP_INDICATOR, Integer.class,
                NbBundle.getMessage (IndicatorProperty.class, "LBL_IndicatorProperty_Name"), // NOI18N
                NbBundle.getMessage (IndicatorProperty.class, "LBL_IndicatorProperty_Description"), // NOI18N
                true, false);

            this.primaryFile = primaryFile;

            setValue (ListImageEditor.PROP_VALUES, new Integer [] {
                new Integer (0),
                new Integer (1)
            });

            setValue (ListImageEditor.PROP_IMAGES, new Image [] {
                Utilities.loadImage ("/org/netbeans/core/resources/empty.gif"), // NOI18N
                Utilities.loadImage ("/org/netbeans/core/resources/project.gif") // NOI18N
            });
        }

        public Object getValue () throws IllegalAccessException, InvocationTargetException {
            FilePositionManager fpm = FilePositionManager.getDefault ();
            if (FilePositionManager.FSTATE_DEFINED == fpm.getFileState (primaryFile, 
                    FilePositionManager.LAYER_PROJECT)) {
                return new Integer (1);
            }

            return new Integer (0);
        }

        public void setValue (Object val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            throw new IllegalAccessException ();
        }

        public PropertyEditor getPropertyEditor () {
            return new ListImageEditor ();
        }
    }

    /** Filter node used for adding special status related properties to setting nodes. */
    private static final class SettingFilterNode extends FilterNode {
        private Sheet sheet = null;

        public SettingFilterNode (Node original) {
            super (original);
        }
        
        public PropertySet[] getPropertySets () {
            if (sheet == null) {
                sheet = cloneSheet (super.getPropertySets ());
            }
            return sheet.toArray ();
        }

        private Sheet cloneSheet (PropertySet orig []) {
            Sheet s = new Sheet ();
            Sheet.Set hidden = null;
            for (int i = 0; i < orig.length; i++) {
                Sheet.Set ss = new Sheet.Set ();
                ss.put (orig[i].getProperties ());
                ss.setName (orig[i].getName ());
                ss.setDisplayName (orig[i].getDisplayName ());
                ss.setShortDescription (orig[i].getShortDescription ());
                ss.setHidden (orig[i].isHidden ());
                ss.setExpert (orig[i].isExpert ());
                ss.setPreferred (orig[i].isPreferred ());

                // modifies the set if it contains name of object property
                if (ss.isHidden ())
                    hidden = ss;

                s.put (ss);
            }

            if (hidden == null) {
                hidden = new Sheet.Set ();
                hidden.setHidden (true);
                s.put (hidden);
            }

            hidden.setHidden (false);

            Node.Property p = hidden.get (PROP_INDICATOR);
            if (p == null) {
                InstanceDataObject ido = (InstanceDataObject) getCookie (InstanceDataObject.class);
                hidden.put (new IndicatorProperty (ido.getPrimaryFile ()));
            }

            addProperty (hidden, PROP_LAYER_PROJECT, FilePositionManager.LAYER_PROJECT);
            addProperty (hidden, PROP_LAYER_SESSION, FilePositionManager.LAYER_SESSION);
            addProperty (hidden, PROP_LAYER_MODULES, FilePositionManager.LAYER_MODULES);

            return s;
        }

        private void addProperty (Sheet.Set s, String propName, int layer) {
            Node.Property p = s.get (propName);
            if (p == null) {
                InstanceDataObject ido = (InstanceDataObject) getCookie (InstanceDataObject.class);
                s.put (new FileStateProperty (ido.getPrimaryFile (), layer, propName, true));
            }
        }
        
        protected NodeListener createNodeListener () {
            return new NA (this);
        }
        
        private static class NA extends NodeAdapter {
            public NA (SettingFilterNode sfn) {
                super (sfn);
            }
            
            protected void propertyChange (FilterNode fn, PropertyChangeEvent ev) {
                if (Node.PROP_PROPERTY_SETS.equals (ev.getPropertyName ())) {
                    ((SettingFilterNode)fn).sheet = null;
                }
                super.propertyChange (fn, ev);
            }
        }
    }
}
