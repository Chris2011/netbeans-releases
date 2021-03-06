/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2004, 2016 Oracle and/or its affiliates. All rights reserved.
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
 */
/*
 * AbstractEngine.java
 *
 * Created on January 24, 2004, 1:36 PM
 */

package org.netbeans.actions.engine.spi;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import org.netbeans.actions.api.ContextProvider;
import org.netbeans.actions.api.Engine;
import org.netbeans.actions.engine.spi.MenuFactory;
import org.netbeans.actions.engine.spi.ToolbarFactory;
import org.netbeans.actions.spi.ActionProvider;
import org.netbeans.actions.spi.ContainerProvider;

/** Convenience basic impl of Engine. Mostly it connects individual
 * infrastructure pieces with each other.
 *
 * @author  Tim Boudreau
 */
public abstract class AbstractEngine extends Engine {
    private ActionProvider actionProvider;
    private ContainerProvider containerProvider;
    private MenuFactory menuFactory = null;
    private ToolbarFactory toolbarFactory = null;
    private ActionFactory actionFactory = null;
    
    /** Creates a new instance of AbstractEngine */
    protected AbstractEngine(ActionProvider actionProvider, ContainerProvider containerProvider) {
        this.actionProvider = actionProvider;
        this.containerProvider = containerProvider;
    }
    
    private ContextProvider contextProvider;
    public void setContextProvider (ContextProvider ctx) {
        this.contextProvider = ctx;
    }
    
    public ContextProvider getContextProvider() {
        return contextProvider;
    }
    
    protected final ActionProvider getActionProvider() {
        return actionProvider;
    }
    
    protected final ContainerProvider getContainerProvider() {
        return containerProvider;
    }
    
    protected Action getAction (String containerCtx, String action) {
        return getActionFactory().getAction(action, containerCtx, getContextProvider().getContext());
    }
    
    protected abstract MenuFactory createMenuFactory();
    
    protected abstract ToolbarFactory createToolbarFactory();
    
    protected abstract ActionFactory createActionFactory();
    
    protected abstract ContextMenuFactory createContextMenuFactory();
    
    void notifyWillPerform (String action, String containerCtx) {
        
    }
    
    void notifyPerformed (String action, String containerCtx) {
        update();
    }
    
    private ContextMenuFactory contextMenuFactory = null;
    public final ContextMenuFactory getContextMenuFactory() {
        if (contextMenuFactory == null) {
            contextMenuFactory = createContextMenuFactory();
        }
        return contextMenuFactory;
    }
    
    public final MenuFactory getMenuFactory() {
        if (menuFactory == null) {
            menuFactory = createMenuFactory();
        }
        return menuFactory;
    }
    
    public final ToolbarFactory getToolbarFactory() {
        if (toolbarFactory == null) {
            toolbarFactory = createToolbarFactory();
        }
        return toolbarFactory;
    }
    
    public final ActionFactory getActionFactory() {
        if (actionFactory == null) {
            actionFactory = createActionFactory();
        }
        return actionFactory;
    }
    

    void notifyOpened (String containerCtx, Object type) {
        
    }
    
    void notifyClosed (String containerCtx, Object type) {
        
    }
    
    boolean isOpen(String containerCtx, Object type) {
        if (type == ContainerProvider.TYPE_MENU) {
            return showingMenus.contains(containerCtx);
        } else if (type == ContainerProvider.TYPE_TOOLBAR){
            return showingToolbars.contains(containerCtx);
        } else {
            return false;
        }
    }

    
    /** Indicates a menu has been displayed onscreen, not that it has been opened.
     */
    protected void notifyMenuShown (String containerCtx, JMenu menu) {
        showingMenus.add(containerCtx);
    }
    
    protected void notifyMenuHidden (String containerCtx, JMenu menu) {
        showingMenus.remove (containerCtx);
    }
    
    protected void notifyToolbarShown (String containerCtx, JToolBar toolbar) {
        showingToolbars.add (containerCtx);
    }
    
    protected void notifyToolbarHidden (String containerCtx, JToolBar toolbar) {
        showingToolbars.remove (containerCtx);
    }
    
    public void update() {
        updateToolbars();
        updateMenus();
    }
    
    protected void updateToolbars() {
        int count = showingToolbars.size();
        String[] toolbars = new String[count];
        toolbars = (String[]) showingToolbars.toArray(toolbars);
        for (int i=0; i < count; i++) {
            getToolbarFactory().update (toolbars[i]);
        }
    }
    
    protected void updateMenus() {
        int count = showingMenus.size();
        String[] menus = new String[count];
        menus = (String[]) showingMenus.toArray(menus);
        for (int i=0; i < count; i++) {
            getMenuFactory().update (menus[i]);
        }
    }
    
    public final JMenuBar createMenuBar() {
        JMenuBar result = new JMenuBar();
        ContainerProvider cp = getContainerProvider();
        String[] menus = cp.getMenuContainerContexts();
        for (int i=0; i < menus.length; i++) {
            JMenu menu = getMenuFactory().createMenu(menus[i]);
            result.add (menu);
        }
        return result;
    }
    
    public final JToolBar[] createToolbars() {
        ContainerProvider cp = getContainerProvider();
        String[] toolbars = cp.getToolbarContainerContexts();
        JToolBar[] result = new JToolBar[toolbars.length];
        for (int i=0; i < toolbars.length; i++) {
            result[i] = getToolbarFactory().createToolbar(toolbars[i]);
        }
        return result;
    }
    
    public JPopupMenu createPopupMenu() {
        return getContextMenuFactory().createMenu(getContextProvider().getContext());
    }
    
    private Set showingMenus = new HashSet();
    private Set showingToolbars = new HashSet();
    
}
