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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.web.jsf.navigation.graph;

import java.awt.Point;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.netbeans.api.visual.vmd.VMDNodeWidget;
import org.netbeans.modules.web.jsf.navigation.Page;
import org.netbeans.modules.web.jsf.navigation.PageFlowToolbarUtilities;
import org.netbeans.modules.web.jsf.navigation.PageFlowToolbarUtilities.Scope;

/**
 *
 * @author joelle
 */
public class PageFlowSceneData {

    private final PageFlowToolbarUtilities utilities;
    //    private PageFlowScene scene;
    private final Map<String, PageData> facesConfigSceneData = new HashMap<String, PageData>();
    private final Map<String, PageData> projectSceneData = new HashMap<String, PageData>();
    private final Map<String, PageData> allFacesConfigSceneData = new HashMap<String, PageData>();

    /**
     * PageFlowSceneData keeps scene data for the facesConfigScope and the projectSceneScope.
     * Because the actual Page object is not necessarily the same, it uses the display name
     * as the key.
     * @param scene PageFlowScene
     * @param utilities PageFlowUtilites
     **/
    public PageFlowSceneData(PageFlowToolbarUtilities utilities) {
        this.utilities = utilities;
        //        this.scene = scene;
    }
    private static Logger LOG = Logger.getLogger(PageFlowSceneData.class.getName());

    /**
     * Saves the Scene Data for the Current Scene Scope
     **/
    public void saveCurrentSceneData(PageFlowScene scene) {
        switch (utilities.getCurrentScope()) {
            case SCOPE_FACESCONFIG:
                facesConfigSceneData.clear();
                facesConfigSceneData.putAll(createSceneInfo(scene));
                break;
            case SCOPE_PROJECT:
                projectSceneData.clear();
                projectSceneData.putAll(createSceneInfo(scene));
                break;
            case SCOPE_ALL_FACESCONFIG:
                allFacesConfigSceneData.clear();
                allFacesConfigSceneData.putAll(createSceneInfo(scene));
                break;
            default:
                LOG.fine("PageFlowSceneData: Unknown State");
        }
        //
        //        if ( utilities.getCurrentScope().equals( PageFlowUtilities.Scope.SCOPE_FACESCONFIG) ){
        //            facesConfigSceneData.clear();
        //            facesConfigSceneData.putAll( createSceneInfo(scene ) );
        //        } else if( utilities.getCurrentScope().equals( PageFlowUtilities.Scope.SCOPE_PROJECT)){
        //            projectSceneData.clear();
        //            projectSceneData.putAll( createSceneInfo(scene) );
        //        }
    }

    /**
     * Moves the Point for the oldDisplayName and to the newDisplayName
     * @param oldDisplayName String
     * @param newDisplayName String
     **/
    public void savePageWithNewName(String oldDisplayName, String newDisplayName) {
        replaceSceneInfo(facesConfigSceneData, oldDisplayName, newDisplayName);
        replaceSceneInfo(projectSceneData, oldDisplayName, newDisplayName);
        replaceSceneInfo(allFacesConfigSceneData, oldDisplayName, newDisplayName);
    }

    /**
     * Loads the Scene Data stored for the current scope into the Scene.
     **/
    public void loadSceneData(PageFlowScene scene) {
        loadSceneData(scene, getCurrentSceneData());
    }

    public PageData getPageData(String pageDisplayName) {
        Map<String, PageData> map = getCurrentSceneData();
        PageData data = null;
        if (map != null) {
            data = map.get(pageDisplayName);
        }
        return data;
    }
/*
    public Point getPageLocation(String pageDisplayName) {
        Map<String, PageData> map = getCurrentSceneData();
        Point p = null;
        if (map != null) {
            PageData data = map.get(pageDisplayName);
            if (data != null) {
                p = data.getPoint();
            }
        }
        return p;
    }

    public boolean isMinimized(String pageDisplayName) {
        Map<String, PageData> map = getCurrentSceneData();
        boolean isMinimized = false;
        ;
        if (map != null) {
            PageData data = getCurrentSceneData().get(pageDisplayName);
            if (data != null) {
                isMinimized = data.isMinimized();
            }
        }
        return isMinimized;
    }
 */

    private void loadSceneData(PageFlowScene scene, Map<String, PageData> sceneInfo) {
        if (sceneInfo == null) {
            return;
        }
        Collection<Page> pages = scene.getNodes();
        for (Page page : pages) {
            PageData data = sceneInfo.get(page.getDisplayName());
            if (data != null) {
                VMDNodeWidget pageWidget = (VMDNodeWidget) scene.findWidget(page);
                pageWidget.setPreferredLocation(data.getPoint());
                pageWidget.setMinimized(data.isMinimized());
            }
        }
    }

    private Map<String, PageData> createSceneInfo(PageFlowScene scene) {
        Map<String, PageData> sceneInfo = new HashMap<String, PageData>();
        Collection<Page> pages = scene.getNodes();
        for (Page page : pages) {
            VMDNodeWidget pageWidget = (VMDNodeWidget) scene.findWidget(page);
            if (scene.isValidated()) {
                sceneInfo.put(page.getDisplayName(), createPageData(pageWidget.getLocation(), pageWidget.isMinimized()));
            } else {
                sceneInfo.put(page.getDisplayName(), createPageData(pageWidget.getPreferredLocation(), pageWidget.isMinimized()));
            }
        }
        return sceneInfo;
    }

    public String getCurrentScopeStr() {
        return PageFlowToolbarUtilities.getScopeLabel(utilities.getCurrentScope());
    }

    public void setCurrentScope(Scope newScope) {
        utilities.setCurrentScope(newScope);
    }

    public void setScopeData(String scope, Map<String, PageData> map) {
        switch (PageFlowToolbarUtilities.getScope(scope)) {
            case SCOPE_FACESCONFIG:
                facesConfigSceneData.clear();
                facesConfigSceneData.putAll(map);
                break;
            case SCOPE_PROJECT:
                projectSceneData.clear();
                projectSceneData.putAll(map);
                break;
            case SCOPE_ALL_FACESCONFIG:
                allFacesConfigSceneData.clear();
                allFacesConfigSceneData.putAll(map);
                break;
        }
        //        if ( scope.equals( PageFlowUtilities.getScopeLabel(PageFlowUtilities.Scope.SCOPE_FACESCONFIG) ) ){
        //            facesConfigSceneData.clear();
        //            facesConfigSceneData.putAll(map);
        //        } else if( scope.equals(PageFlowUtilities.getScopeLabel(PageFlowUtilities.Scope.SCOPE_PROJECT))){
        //            projectSceneData.clear();
        //            projectSceneData.putAll(map);
        //        }
    }

    public Map<String, PageData> getScopeData(String scopeStr) {
        Map<String, PageData> sceneInfo = null;
        PageFlowToolbarUtilities.Scope scope = PageFlowToolbarUtilities.getScope(scopeStr);
        switch (scope) {
            case SCOPE_FACESCONFIG:
                sceneInfo = facesConfigSceneData;
                break;
            case SCOPE_PROJECT:
                sceneInfo = projectSceneData;
                break;
            case SCOPE_ALL_FACESCONFIG:
                sceneInfo = allFacesConfigSceneData;
        }
        //        if ( scope.equals( PageFlowUtilities.LBL_SCOPE_FACESCONFIG) ){
        //            sceneInfo = facesConfigSceneData;
        //        } else if( scope.equals(PageFlowUtilities.LBL_SCOPE_PROJECT)){
        //            sceneInfo = projectSceneData;
        //        }
        return sceneInfo;
    }

    private Map<String, PageData> getCurrentSceneData() {
        Map<String, PageData> currentSceneData = null;
        switch (utilities.getCurrentScope()) {
            case SCOPE_FACESCONFIG:
                currentSceneData = facesConfigSceneData;
                break;
            case SCOPE_PROJECT:
                currentSceneData = projectSceneData;
                break;
            case SCOPE_ALL_FACESCONFIG:
                currentSceneData = allFacesConfigSceneData;
                break;
            default:
                currentSceneData = null;
        }
        return currentSceneData;
    }

    private void replaceSceneInfo(Map<String, PageData> sceneInfo, String oldDisplayName, String newDisplayName) {
        assert oldDisplayName != null;
        assert newDisplayName != null;

        if (sceneInfo == null || sceneInfo.size() < 1) {
            return;
        }

        PageData data = sceneInfo.remove(oldDisplayName);
        if (data != null) {
            sceneInfo.put(newDisplayName, data);
        }
    }

    public static PageData createPageData(Point point, boolean isMinimized) {
        return new PageData(point, isMinimized);
    }

/* Can I always guarentee that Point is not null? */
    public static class PageData {

        private Point point;
        private boolean isMinimized = false;

        private PageData() {
        }

        public PageData(Point point) {
            if (point == null) {
                throw new IllegalArgumentException("Page Data does not exception null points");
            }
            this.point = point;
        }

        public PageData(Point point, boolean isMinimized) {
            this(point);
            this.isMinimized = isMinimized;
        }

        public Point getPoint() {
            return point;
        }

        public boolean isMinimized() {
            return isMinimized;
        }
    }
}
