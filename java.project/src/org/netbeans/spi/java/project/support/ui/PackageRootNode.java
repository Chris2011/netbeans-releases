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

package org.netbeans.spi.java.project.support.ui;

import java.awt.Component;
import java.awt.Image;
import java.awt.Panel;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.nodes.NodeNotFoundException;
import org.openide.nodes.NodeOp;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/** Node displaying a packages in given SourceGroup
 * @author Petr Hrebejk
 */
final class PackageRootNode extends AbstractNode {

    private static Image PACKAGE_BADGE = Utilities.loadImage( "org/netbeans/spi/java/project/support/ui/packageBadge.gif" ); // NOI18N
        
    private static Action actions[]; 

    private SourceGroup group;
        
    PackageRootNode( SourceGroup group ) {
        super( new PackageViewChildren( group.getRootFolder() ), createLookup( group ) );                
        this.group = group;
        setName( group.getName() );
        setDisplayName( group.getDisplayName() );
        // setIconBase("org/netbeans/modules/java/j2seproject/ui/resources/packageRoot");
    }

    public Image getIcon( int type ) {        
        return computeIcon( false, type );
    }
        
    public Image getOpenedIcon( int type ) {
        return computeIcon( true, type );
    }
    
    public Action[] getActions( boolean context ) {

        if ( actions == null ) {
            actions = new Action[] {
                CommonProjectActions.newFileAction(),
                null,
                org.openide.util.actions.SystemAction.get( org.openide.actions.FileSystemRefreshAction.class ),
                org.openide.util.actions.SystemAction.get( org.openide.actions.FindAction.class ),
                null,
                org.openide.util.actions.SystemAction.get( org.openide.actions.PasteAction.class ),
                null,
                org.openide.util.actions.SystemAction.get( org.openide.actions.ToolsAction.class ),
                null,                    
                org.openide.util.actions.SystemAction.get( org.openide.actions.PropertiesAction.class ),
            };
        }
        return actions;            
    }

    // Show properties of the DataFolder
    public PropertySet[] getPropertySets() {            
        return getDataFolderNodeDelegate().getPropertySets();
    }

    // XXX Paste types - probably not very nice 
    public void createPasteTypes( Transferable t, List list ) {            
        list.addAll( Arrays.asList( getDataFolderNodeDelegate().getPasteTypes( t ) ) );
    }

    // Private methods ---------------------------------------------------------
    
    private Node getDataFolderNodeDelegate() {
        return ((DataFolder)getLookup().lookup( DataFolder.class )).getNodeDelegate();
    }
    
    private Image computeIcon( boolean opened, int type ) {
        Image image;
        Icon icon = group.getIcon( opened );
        
        if ( icon == null ) {
            image = opened ? getDataFolderNodeDelegate().getOpenedIcon( type ) : 
                             getDataFolderNodeDelegate().getIcon( type );
            image = Utilities.mergeImages( image, PACKAGE_BADGE, 7, 7 );
        }
        else {
            if ( icon instanceof ImageIcon ) {
                image = ((ImageIcon)icon).getImage();
            }
            else {
                image = icon2image( icon );
            }
        }
        
        return image;        
    }
    
    private static Component CONVERTOR_COMPONENT = new Panel();
    
    private static Image icon2image( Icon icon ) {
        int height = icon.getIconHeight();
        int width = icon.getIconWidth();
        
        BufferedImage bImage = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
        icon.paintIcon( CONVERTOR_COMPONENT, bImage.getGraphics(), 0, 0 );
        
        return bImage;
    }
            
    private static Lookup createLookup( SourceGroup group ) {
        // XXX Remove DataFolder when paste, find and refresh are reimplemented
        FileObject rootFolder = group.getRootFolder();
        DataFolder dataFolder = DataFolder.findFolder( rootFolder );        
        return Lookups.fixed( new Object[]{ dataFolder, new PathFinder( group ) } );
    }
    
    /** If contained in the lookup can perform the search for a node
     */    
    public static class PathFinder {
        
        private SourceGroup group;
        
        public PathFinder( SourceGroup group ) {
            this.group = group;
        }
        
        public Node findPath( Node root, Object object ) {
            FileObject fo;
            if (object instanceof FileObject) {
                fo = (FileObject) object;
            } else if (object instanceof DataObject) {
                fo = ((DataObject) object).getPrimaryFile();
            } else {
                return null;
            }
            
            FileObject groupRoot = group.getRootFolder();
            if ( FileUtil.isParentOf( groupRoot, fo ) /* && group.contains( fo ) */ ) {
                // The group contains the object

                String relPath = FileUtil.getRelativePath( groupRoot, fo );
                int lastSlashIndex = relPath.lastIndexOf( '/' ); // NOI18N

                String[] path = null;
                if ( fo.isFolder() ) {
                    String packageName = relPath.replace( '/', '.' ); // NOI18N
                    path = new String[] { packageName };
                }
                else if ( lastSlashIndex == -1 ) {
                    path = new String[] { "", fo.getName() };
                }
                else {
                    String packageName = relPath.substring( 0, lastSlashIndex ).replace( '/', '.' ); // NOI18N
                    path = new String[] { packageName, fo.getName() };                    
                } 
                try {
                    // XXX if there are two files differing only by extension in the package,
                    // this will be wrong...
                    return NodeOp.findPath( root, path );
                }
                catch ( NodeNotFoundException e ) {
                    if (!fo.isFolder()) {
                        // If it is a DefaultDataObject, the node name contains the extension.
                        if (lastSlashIndex == -1) {
                            path = new String[] {"", fo.getNameExt()};
                        } else {
                            String packageName = relPath.substring(0, lastSlashIndex).replace('/', '.'); // NOI18N
                            path = new String[] {packageName, fo.getNameExt()};
                        }
                        try {
                            return NodeOp.findPath(root, path);
                        } catch (NodeNotFoundException e2) {
                            // already handled
                        }
                    }
                    // did not manage to find it after all... why?
                    return null;
                }
            }   
            else if ( groupRoot.equals( fo ) ) {
                // First try to find default package
                try {
                    return NodeOp.findPath( root, new String[] { "" } ); // NOI18N
                }
                catch ( NodeNotFoundException e ) {
                    // If it does not exists return this node
                }                        
                return root;
            }

            return null;
        }
        
        public String toString() {
            return "PathFinder[" + group + "]"; // NOI18N
        }
                    
    }
    
}
