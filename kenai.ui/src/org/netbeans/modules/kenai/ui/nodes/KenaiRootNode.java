/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.kenai.ui.nodes;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.Action;
import org.netbeans.api.core.ide.ServicesTabNodeRegistration;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

/**
 * Root node in Services tab.
 * @author Jan Becicka
 */
public class KenaiRootNode extends AbstractNode {

    public static final String KENAI_NODE_NAME = "kenai"; // NOI18N
    private static final String ICON_BASE = "org/netbeans/modules/kenai/ui/resources/kenai-small.png"; // NOI18N
    
    @ServicesTabNodeRegistration(name=KENAI_NODE_NAME, displayName="#LBL_KenaiNode", iconResource=ICON_BASE, position=489)
    public static KenaiRootNode getDefault() {
        return new KenaiRootNode();
    }

    private KenaiRootNode() {
        super(Children.create(new RootNodeChildren(), true));
        setName(KENAI_NODE_NAME);
        setDisplayName(NbBundle.getMessage(KenaiRootNode.class, "LBL_KenaiNode"));
        setIconBaseWithExtension(ICON_BASE);
    }
    
    public @Override Action[] getActions(boolean context) {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new AddInstanceAction());
        return actions.toArray(new Action[actions.size()]);
    }

    private static class RootNodeChildren extends ChildFactory<KenaiInstance> implements PropertyChangeListener {
        
        public RootNodeChildren() {
            KenaiInstancesManager.getDefault().addPropertyChangeListener(this);
        }

        protected @Override Node createNodeForKey(KenaiInstance key) {
            return new KenaiInstanceNode(key);
        }
        
        protected boolean createKeys(List<KenaiInstance> toPopulate) {
            toPopulate.addAll(KenaiInstancesManager.getDefault().getInstances());
            Collections.sort(toPopulate);
            return true;
        }
        public void propertyChange(PropertyChangeEvent evt) {
            refresh(false);
        }
    }
}
