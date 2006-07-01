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

package org.openide.filesystems.data;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.util.List;
import java.util.ResourceBundle;

import org.openide.nodes.*;
import org.openide.actions.*;
import org.openide.loaders.DataFolder;
import org.openide.util.datatransfer.NewType;
import org.openide.util.actions.SystemAction;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/** Dummy class, serves as a template for generating java files.
*/
public final class JavaSrc {

    /** Actions which this node supports */
    static SystemAction[] staticActions;
    /** Actions of this node when it is top level actions node */
    static SystemAction[] topStaticActions;

    private static final Node.PropertySet[] NO_PROPERTIES = new Node.PropertySet[0];

    public JavaSrc() {
    }

    /** Constructs this node with given node to filter.
    */
    JavaSrc(DataFolder folder) {
    }

    public HelpCtx getHelpCtx () {
        return new HelpCtx (Object.class);
    }

    /** Support for new types that can be created in this node.
    * @return array of new type operations that are allowed
    */
    public NewType[] getNewTypes () {
        return new NewType[0];
    }

    protected void createPasteTypes (Transferable t, List s) {
        s.clear ();
    }

    /** Actions.
    * @return array of actions for this node
    */
    public SystemAction[] getActions () {
        if (staticActions == null)
            topStaticActions = new SystemAction [] {
                                   SystemAction.get (FileSystemAction.class),
                                   null,
                                   SystemAction.get(ToolsAction.class),
                                   SystemAction.get(PropertiesAction.class),
                               };
        return topStaticActions;
    }

    /** Creates properties for this node */
    public Node.PropertySet[] getPropertySets () {
        return NO_PROPERTIES;
    }

    public boolean canDestroy () {
        return false;
    }

    public boolean canCut () {
        return false;
    }

    public boolean canRename () {
        return false;
    }

    /** Children for the JavaSrc. Creates JavaSrcs or
    * ItemNodes as filter subnodes...
    */
    static final class ActionsPoolChildren extends FilterNode.Children {

        /** @param or original node to take children from */
        public ActionsPoolChildren (DataFolder folder) {
            super(folder.getNodeDelegate ());
        }

        /** Overriden, returns JavaSrc filters of original nodes.
        *
        * @param node node to create copy of
        * @return JavaSrc filter of the original node
        */
        protected Node copyNode (Node node) {
            DataFolder df = (DataFolder)node.getCookie(DataFolder.class);
            if (df != null) {
                return null;
            }
            return new ActionItemNode(node);
        }

    }

    static final class ActionItemNode extends FilterNode {
        /** Icons for this node */
        static Image itemIcon;
        static Image itemIcon32;

        /** Actions which this node supports */
        static SystemAction[] staticActions;

        /** Constructs new filter node for Action item */
        ActionItemNode (Node filter) {
            super(filter, Children.LEAF);
        }

        /*
        public Image getIcon (int type) {
            if ((type == java.beans.BeanInfo.ICON_COLOR_16x16) ||
                    (type == java.beans.BeanInfo.ICON_MONO_16x16)) {
                if (itemIcon == null)
                    itemIcon = Toolkit.getDefaultToolkit ().getImage (
                                   getClass ().getResource ("/org/netbeans/core/resources/action.gif")); // NOI18N
                return itemIcon;
            } else {
                if (itemIcon32 == null)
                    itemIcon32 = Toolkit.getDefaultToolkit ().getImage (
                                     getClass ().getResource ("/org/netbeans/core/resources/action32.gif")); // NOI18N
                return itemIcon32;
            }
        }

        public Image getOpenedIcon (int type) {
            return getIcon (type);
        }
        */

        /** Actions.
        * @return array of actions for this node
        */
        public SystemAction[] getActions () {
            if (staticActions == null) {
                staticActions = new SystemAction [] {
                                    SystemAction.get(CopyAction.class),
                                    null,
                                    SystemAction.get(ToolsAction.class),
                                    SystemAction.get(PropertiesAction.class),
                                };
            }
            return staticActions;
        }

        /** Disallows renaming.
        */
        public boolean canRename () {
            return false;
        }

        public boolean canDestroy () {
            return false;
        }

        public boolean canCut () {
            return false;
        }

        /** Creates properties for this node */
        public Node.PropertySet[] getPropertySets () {
            /*
            ResourceBundle bundle = NbBundle.getBundle(Object.class);
            // default sheet with "properties" property set // NOI18N
            Sheet sheet = Sheet.createDefault();
            sheet.get(Sheet.PROPERTIES).put(
                new PropertySupport.Name(
                    this,
                    bundle.getString("PROP_ActionItemName"),
                    bundle.getString("HINT_ActionItemName")
                )
            );
            return sheet.toArray();
             */
            return new Node.PropertySet[] { };
        }

    } // end of ActionItemNode

}
