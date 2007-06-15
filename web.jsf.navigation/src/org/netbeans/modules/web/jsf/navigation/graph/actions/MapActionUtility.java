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

package org.netbeans.modules.web.jsf.navigation.graph.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.lang.Object;
import java.util.HashSet;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JOptionPane;
import org.netbeans.modules.web.jsf.navigation.PageFlowController;
import org.netbeans.modules.web.jsf.navigation.graph.PageFlowScene;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import java.lang.*;
import java.lang.Object;
import javax.swing.KeyStroke;
import org.netbeans.modules.web.jsf.navigation.graph.PageFlowSceneElement;
import org.netbeans.modules.web.jsf.navigation.graph.layout.SceneElementComparator;
import org.openide.cookies.OpenCookie;
/**
 *
 * @author joelle
 */
public class MapActionUtility {
    
    /** Creates a new instance of MapActionUtility */
    public MapActionUtility() {
    }
    
    //    public static ActionMap initActionMap() {
    //        ActionMap actionMap = new ActionMap();
    //        //        // Install the actions
    //        actionMap.put("handleTab", handleTab);
    //        //        actionMap.put("handleEscape", handleEscape);
    //        //
    //        //        actionMap.put("handleLinkStart", handleLinkStart);
    //        //        actionMap.put("handleLinkEnd", handleLinkEnd);
    //        //
    //        //        actionMap.put("handleZoomPage", handleZoomPage);
    //        //        actionMap.put("handleUnZoomPage", handleUnZoomPage);
    //        //        actionMap.put("handleOpenPage", handleOpenPage);
    //
    //        actionMap.put("handleNewWebForm", handleNewWebForm);
    //        //
    //        //        actionMap.put("handleLeftArrowKey", handleLeftArrowKey);
    //        //        actionMap.put("handleRightArrowKey", handleRightArrowKey);
    //        //        actionMap.put("handleUpArrowKey", handleUpArrowKey);
    //        //        actionMap.put("handleDownArrowKey", handleDownArrowKey);
    //        //
    //        //        actionMap.put("handleAddCommandButton", handleAddCommandButton);
    //        //        actionMap.put("handleAddCommandLink", handleAddCommandLink);
    //        //        actionMap.put("handleAddImageHyperLink", handleAddImageHyperLink);
    //        //        actionMap.put("handlePopupMenu", handlePopupMenu);
    //        //        actionMap.put("handleDeleteKey", handleDeleteKey);
    //        return actionMap;
    //    }
    //
    public static ActionMap initActionMap() {
        ActionMap actionMap = new ActionMap();
        // Install the actions
        actionMap.put("handleTab", handleTab);
//        actionMap.put("handleEscape", new TestAction("handleEscape"));
        
        //        actionMap.put("handleLinkStart", new TestAction("handleLinkStart"));  // new TestAction(""));
        //        actionMap.put("handleLinkEnd", new TestAction("handleLinkEnd"));
        //
        //        actionMap.put("handleZoomPage", new TestAction("handleZoomPage"));
        //        actionMap.put("handleUnZoomPage", new TestAction("handleUnZoomPage"));
        actionMap.put("handleOpenPage", handleOpenPage);
        //
        //        actionMap.put("handleNewWebForm", new TestAction("handleNewWebForm"));
        //
        //        actionMap.put("handleLeftArrowKey", new TestAction("handleLeftArrowKey"));
        //        actionMap.put("handleRightArrowKey", new TestAction("handleRightArrowKey"));
        //        actionMap.put("handleUpArrowKey", new TestAction("handleUpArrowKey"));
        //        actionMap.put("handleDownArrowKey", new TestAction("handleDownArrowKey"));
        //
        //        actionMap.put("handleAddCommandButton", new TestAction("handleAddCommandButton"));
        //        actionMap.put("handleAddCommandLink", new TestAction("handleAddCommandLink"));
        //        actionMap.put("handleAddImageHyperLink", new TestAction("handleAddImageHyperLink"));
        //        actionMap.put("handlePopupMenu", new TestAction("handlePopupMenu"));
        //        actionMap.put("handleDeleteKey", handleDeleteKey);
        return actionMap;
    }
    
    public static InputMap initInputMap() {
        InputMap inputMap = new InputMap();
        // Tab Key
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB,0), "handleTab");
        // Esc Key
//        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0), "handleEscape");
        //
        //        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A,InputEvent.CTRL_MASK), "handleNewWebForm");
        //
        //        //Lower Case s,e,z,u
        //        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S,0), "handleLinkStart");
        //        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_E,0), "handleLinkEnd");
        //        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z,0), "handleZoomPage");
        //        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_U,0), "handleUnZoomPage");
        //
        //        //Keys enter, b,l,i
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0), "handleOpenPage");
        //        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_B,0), "handleAddCommandButton");
        //        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_L,0), "handleAddCommandLink");
        //        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_I,0), "handleAddImageHyperLink");
        //
        //        // Upper Case S,E,Z,U
        //        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.SHIFT_MASK), "handleLinkStart");
        //        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_E,InputEvent.SHIFT_MASK), "handleLinkEnd");
        //        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z,InputEvent.SHIFT_MASK), "handleZoomPage");
        //        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_U,InputEvent.SHIFT_MASK), "handleUnZoomPage");
        //
        //        // Keys B,L, I
        //        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_B,InputEvent.SHIFT_MASK), "handleAddCommandButton");
        //        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_L,InputEvent.SHIFT_MASK), "handleAddCommandLink");
        //        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_I,InputEvent.SHIFT_MASK), "handleAddImageHyperLink");
        //
        //        // Non Numeric Key Pad arrow keys
        //        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,0), "handleLeftArrowKey");
        //        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,0), "handleRightArrowKey");
        //        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP,0), "handleUpArrowKey");
        //        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,0), "handleDownArrowKey");
        //
        //        // Numeric Key Pad arrow keys
        //        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_LEFT,0), "handleLeftArrowKey");
        //        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_RIGHT,0), "handleRightArrowKey");
        //        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_UP,0), "handleUpArrowKey");
        //        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_DOWN,0), "handleDownArrowKey");
        //
        //        // SHIFT + F10
        //        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F10,InputEvent.SHIFT_MASK), "handlePopupMenu");
        //
        //
        //        //Add File
        //        inputMap.put(KeyStroke.getKeyStroke( KeyEvent.VK_A, 0, false), "handleNewWebForm");
        
        //                //DELETE
        //                inputMap.put(KeyStroke.getKeyStroke( KeyEvent.VK_DELETE , 0), "handleDeleteKey");
        return inputMap;
    }
    
//    // Handle Escape - cancels the link action
//    public static Action handleEscape = new AbstractAction() {
//        public void actionPerformed(ActionEvent e) {
//            /* Cancel A11y Linking */
//            if( linkStartNode != null ){
//                if( linkStartNode instanceof NavigationGraphNode){
//                    ((NavigationGraphNode)linkStartNode).setZoomed(false);
//                }
//                linkStartNode=null;
//            }
//            setRelink(null,null,true,null);
//            viewPresenter.scheduleRepaint();
//        }
//    };
    
    public static final Action handleTab = new AbstractAction() {
        
        public void actionPerformed(ActionEvent e) {
            Object sourceObj = e.getSource();
            if( !(sourceObj instanceof PageFlowScene) ){
                return;
            }
            PageFlowScene scene = (PageFlowScene)sourceObj;
            
            PageFlowSceneElement nextElement = SceneElementComparator.getNextSelectableElement(scene);
            if( nextElement != null ){
                Set<PageFlowSceneElement> set = new HashSet<PageFlowSceneElement>();
                set.add(nextElement);
                scene.setSelectedObjects(set);
            }
        }
    };
    
    
    public static final Action handleOpenPage = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            Object sourceObj = e.getSource();
            if( sourceObj instanceof PageFlowScene ){
                PageFlowScene scene = (PageFlowScene)sourceObj;
                Set<? extends Object> selectedObjs = new HashSet(scene.getSelectedObjects());
                
                for( Object obj : selectedObjs ){
                    if( obj instanceof PageFlowSceneElement ){
                        OpenCookie openCookie = ((PageFlowSceneElement)obj).getNode().getCookie(OpenCookie.class);
                        if(openCookie != null ){
                            openCookie.open();
                        }
                    }
                }
            }
        }
    };
    
    
    public static Action handleNewWebForm = new AbstractAction() {
        PageFlowScene scene;
        public void actionPerformed(ActionEvent e) {
            //            This would work if we wanted to use the wizard.
            //            Action newFileAction = CommonProjectActions.newFileAction();
            
            
            //            JOptionPane.showMessageDialog(null, "Source: " + e.getSource());
            Object obj = e.getSource();
            if ( obj instanceof PageFlowScene ){
                try             {
                    scene = (PageFlowScene) obj;
                    PageFlowController pfc = scene.getPageFlowView().getPageFlowController();
                    
                    FileObject webFileObject = pfc.getWebFolder();
                    
                    String name = FileUtil.findFreeFileName(webFileObject, "page", "jsp");
                    name = JOptionPane.showInputDialog("Select Page Name", name);
                    
                    createIndexJSP(webFileObject, name);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
                
            }
        }
        
        /**
         * Creates a JSP in the
         * @param name
         * @throws java.io.IOException
         */
        private void createIndexJSP(FileObject targetFolder, String name ) throws IOException {
            //            FileOwnerQuery.getOwner(webFolder)
            //            FileObject webFO = fo.createFolder(DEFAULT_DOC_BASE_FOLDER);
            //            FileObject parentFolder = project.getProjectDirectory();
            //            FileObject webFileObject = parentFolder.getFileObject("web");
            
            FileObject jspTemplate = Repository.getDefault().getDefaultFileSystem().findResource( "Templates/JSP_Servlet/JSP.jsp" ); // NOI18N
            
            if (jspTemplate == null)
                return; // Don't know the template
            
            
            DataObject mt = DataObject.find(jspTemplate);
            DataFolder webDf = DataFolder.findFolder(targetFolder);
            mt.createFromTemplate(webDf, name); // NOI18N
        }
        
        
        private static final String DEFAULT_DOC_BASE_FOLDER = "web"; //NOI18N
        
    };
    
    
}

