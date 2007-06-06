/*
 * PageFlowDeleteAction.java
 *
 * Created on April 12, 2007, 12:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.web.jsf.navigation.graph.actions;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import org.netbeans.modules.web.jsf.navigation.NavigationCaseEdge;
import org.netbeans.modules.web.jsf.navigation.PageFlowUtilities;
import org.netbeans.modules.web.jsf.navigation.Pin;
import org.netbeans.modules.web.jsf.navigation.graph.PageFlowScene;
import org.netbeans.modules.web.jsf.navigation.graph.PageFlowSceneElement;
import org.openide.util.Exceptions;

/**
 *
 * @author joelle
 */
public class PageFlowDeleteAction extends AbstractAction{
    private PageFlowScene scene;
    private static final Logger LOG = Logger.getLogger("org.netbeans.modules.web.jsf.navigation.graph.actions.PageFlowDeleteAction");
    //    private final static Logger LOG = Logger.getLogger("org.netbeans.modules.web.jsf.navigation.graph.actions.PageFlowDeleteAction");
    //    static {
    //        LOG.setLevel(Level.FINEST);
    //    }
    
    
    /** Creates a new instance of PageFlowDeleteAction
     * @param scene
     */
    public PageFlowDeleteAction(PageFlowScene scene) {
        this.scene = scene;
    }
    
    
    @Override
    public boolean isEnabled() {
        //Workaround: Temporarily Wrapping Collection because of Issue: 100127
        Set<? extends Object> selectedObjs = scene.getSelectedObjects();
        if (selectedObjs.size() == 0 ){
            return false;
        }
        
        for( Object selectedObj : selectedObjs ){
            /* HACK until PinNode is made a Node */
            if(!( selectedObj instanceof PageFlowSceneElement )  ){
                return false;
            }
            /* Can usually assume the case is in the config file unless we are dealing with the SCOPE_ALL_FACESCONFIG. */
            if( selectedObj instanceof NavigationCaseEdge && !((NavigationCaseEdge)selectedObj).isModifiable()) {
                    return false;
            }
        }
        
        return super.isEnabled();
    }
    
    public void actionPerformed(ActionEvent event) {
        
        Queue<PageFlowSceneElement> deleteNodesList = new LinkedList<PageFlowSceneElement>();
        //Workaround: Temporarily Wrapping Collection because of Issue: 100127
        Set<Object> selectedObjects = new HashSet<Object>(scene.getSelectedObjects());
        LOG.fine("Selected Objects: " + selectedObjects);
        LOG.finest("Scene: \n" +
                "Nodes: " + scene.getNodes() + "\n" +
                "Edges: " + scene.getEdges()+ "\n" +
                "Pins: " + scene.getPins());
        
        /*When deleteing only one item. */
        if (selectedObjects.size() == 1){
            Object myObj = selectedObjects.toArray()[0];
            if( myObj instanceof PageFlowSceneElement ) {
                deleteNodesList.add((PageFlowSceneElement)myObj);
                deleteNodes(deleteNodesList);
                return;
            }
        }
        
        Set<NavigationCaseEdge> selectedEdges = new HashSet<NavigationCaseEdge>();
        Set<PageFlowSceneElement> selectedNonEdges = new HashSet<PageFlowSceneElement>();
        
        /* When deleting multiple objects, make sure delete all the links first. */
        Set<Object> nonEdgeSelectedObjects = new HashSet<Object>();
        for( Object selectedObj : selectedObjects ){
            if( selectedObj instanceof PageFlowSceneElement ){
                if( scene.isEdge(selectedObj) ){
                    assert !scene.isPin(selectedObj);                    
                    selectedEdges.add((NavigationCaseEdge)selectedObj);
                } else {
                    assert scene.isNode(selectedObj) || scene.isPin(selectedObj);                    
                    selectedNonEdges.add((PageFlowSceneElement)selectedObj);
                }
            }
        }
        
        /* I can not call deleteNodes on them separate because I need to guarentee that the edges are always deleted before anything else. */
        deleteNodesList.addAll(selectedEdges);
        deleteNodesList.addAll(selectedNonEdges);
        
//        for( Object selectedObj : nonEdgeSelectedObjects ){
//            deleteNodesList.add((PageFlowSceneElement)selectedObj);
//        }
        deleteNodes(deleteNodesList);
        
    }
    
    //        public Queue<Node> myDeleteNodes;
    private void deleteNodes( Queue<PageFlowSceneElement> deleteNodes ){
        final Queue<PageFlowSceneElement> myDeleteNodes = deleteNodes;
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    //This should walk through in order.
                    for( PageFlowSceneElement deleteNode : myDeleteNodes ){
                        if( deleteNode.canDestroy() ){
                            
                            if( deleteNode instanceof NavigationCaseEdge ){
                                updateSourcePins((NavigationCaseEdge)deleteNode);
                            }
                            
                            
                            deleteNode.destroy();
                        }
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });
    }
    
    private void updateSourcePins(NavigationCaseEdge navCaseNode) {
        Pin source = scene.getEdgeSource(navCaseNode);
        if( source != null && !source.isDefault()) {
            source.setFromOutcome(null);
        }
        return;
    }
    
    
}
