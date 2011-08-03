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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.hudson.ui.nodes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import javax.swing.Action;
import org.netbeans.modules.hudson.api.HudsonChangeListener;
import org.netbeans.modules.hudson.api.HudsonInstance;
import org.netbeans.modules.hudson.api.HudsonJob;
import org.netbeans.modules.hudson.api.HudsonJob.Color;
import org.netbeans.modules.hudson.api.HudsonVersion;
import org.netbeans.modules.hudson.api.HudsonView;
import org.netbeans.modules.hudson.api.Utilities;
import org.netbeans.modules.hudson.impl.HudsonInstanceImpl;
import org.netbeans.modules.hudson.impl.HudsonManagerImpl;
import static org.netbeans.modules.hudson.ui.nodes.Bundle.*;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.Lookups;

/**
 * Describes HudsonInstance in the Runtime Tab
 *
 * @author Michal Mocnak
 */
public class HudsonInstanceNode extends AbstractNode {
    
    private static final String ICON_BASE = "org/netbeans/modules/hudson/ui/resources/instance.png"; // NOI18N
    
    private HudsonInstanceImpl instance;
    private InstanceNodeChildren children;
    
    private boolean warn = false;
    private boolean run = false;
    private boolean alive = false;
    private boolean version = false;
    
    public HudsonInstanceNode(final HudsonInstanceImpl instance) {
        super(new Children.Array(), Lookups.singleton(instance));
        
        children = new InstanceNodeChildren(instance);

        setName(instance.getUrl());
        setDisplayName(instance.getName());
        setShortDescription(instance.getUrl());
        setIconBaseWithExtension(ICON_BASE);
        setValue("customDelete", true); // NOI18N
        
        this.instance = instance;
        
        instance.addHudsonChangeListener(new HudsonChangeListener() {
            public void stateChanged() {
                refreshState();
            }
            public void contentChanged() {
                refreshContent();
            }
        });
        instance.prefs().addPreferenceChangeListener(new PreferenceChangeListener() {
            public void preferenceChange(PreferenceChangeEvent evt) {
                refreshContent();
            }
        });
        
        // Refresh
        refreshState();
        refreshContent();
    }
    
    
    @Messages({
        "MSG_WrongVersion=[Older version than {0}]",
        "MSG_Disconnected=[Disconnected]",
        "HudsonInstanceNode.from_open_project=(from open project)"
    })
    @Override public String getHtmlDisplayName() {
        boolean pers = instance.isPersisted();
        String selectedView = instance.prefs().get(SELECTED_VIEW, null);
        return (run ? "<b>" : "") + (warn ? "<font color=\"#A40000\">" : "") + // NOI18N
                instance.getName() + (warn ? "</font>" : "") + (run ? "</b>" : "") + // NOI18N
                (selectedView != null ? " <font color='!controlShadow'>[" + selectedView + "]</font>" : "") + // NOI18N
                (alive ? (version ? "" : " <font color=\"#A40000\">" + // NOI18N
                    MSG_WrongVersion(HudsonVersion.SUPPORTED_VERSION) + "</font>") :
                    " <font color=\"#A40000\">" + // NOI18N
                MSG_Disconnected() + "</font>") +
                (!pers ? " <font color='!controlShadow'>" + // NOI18N
                    HudsonInstanceNode_from_open_project() + "</font>" : "");
    }
    
    public @Override Action[] getActions(boolean context) {
        return org.openide.util.Utilities.actionsForPath(HudsonInstance.ACTION_PATH).toArray(new Action[0]);
    }

    public @Override boolean canDestroy() {
        return instance.isPersisted();
    }

    public @Override void destroy() throws IOException {
        HudsonManagerImpl.getDefault().removeInstance(instance);
    }

    public @Override PropertySet[] getPropertySets() {
        return new PropertySet[] {instance.getProperties().getSheetSet()};
    }
    
    private synchronized void refreshState() {
        // Save html name
        String oldHtmlName = "";
        
        alive = instance.isConnected();
        version = Utilities.isSupportedVersion(instance.getVersion());
        
        // Refresh children
        if (!alive || !version)
            setChildren(new Children.Array());
        else if (getChildren().getNodesCount() == 0)
            setChildren(children);
        
        // Fire changes if any
        fireDisplayNameChange(oldHtmlName, getHtmlDisplayName());
    }
    
    private synchronized void refreshContent() {
        // Get HTML Display Name
        String oldHtmlName = null;
        
        // Clear flags
        warn = false;
        run = false;
        
        // Refresh state flags
        for (HudsonJob job : instance.getJobs()) {
            if (job.getColor().equals(Color.red) || job.getColor().equals(Color.red_anime))
                warn = true;
            
            if (job.getColor().equals(Color.blue_anime) || job.getColor().equals(Color.grey_anime)
                    || job.getColor().equals(Color.red_anime) || job.getColor().equals(Color.yellow_anime))
                run = true;
            
            if (warn && run)
                break; // it's not necessary to continue
        }
        // Fire changes if any
        fireDisplayNameChange(oldHtmlName, getHtmlDisplayName());
    }

    /**
     * Preferences key for currently display view.
     */
    public static final String SELECTED_VIEW = "view"; // NOI18N
    
    private static class InstanceNodeChildren extends Children.Keys<HudsonJob> implements HudsonChangeListener {
        
        private final HudsonInstance instance;
        
        public InstanceNodeChildren(HudsonInstance instance) {
            this.instance = instance;
            instance.addHudsonChangeListener(this);
            instance.prefs().addPreferenceChangeListener(new PreferenceChangeListener() {
                public void preferenceChange(PreferenceChangeEvent evt) {
                    refreshKeys();
                }
            });
        }
        
        protected Node[] createNodes(HudsonJob job) {
            return new Node[] {new HudsonJobNode(job)};
        }
        
        @Override
        protected void addNotify() {
            super.addNotify();
            refreshKeys();
        }
        
        @Override
        protected void removeNotify() {
            setKeys(Collections.<HudsonJob>emptySet());
            super.removeNotify();
        }
        
        private void refreshKeys() {
            List<HudsonJob> jobs = new ArrayList<HudsonJob>();
            HudsonView view = instance.getPrimaryView();
            String selectedView = instance.prefs().get(SELECTED_VIEW, null);
            if (selectedView != null) {
                for (HudsonView v : instance.getViews()) {
                    if (v.getName().equals(selectedView)) {
                        view = v;
                        break;
                    }
                }
            }
            for (HudsonJob job : instance.getJobs()) {
                if (!job.getViews().contains(view)) {
                    continue;
                }
                jobs.add(job);
            }
            setKeys(jobs);
        }
        
        public void stateChanged() {}
        
        public void contentChanged() {
            refreshKeys();
        }
    }

}
