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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.javaee.wildfly.nodes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.javaee.wildfly.WildFlyDeploymentManager;
import org.netbeans.modules.javaee.wildfly.nodes.actions.Refreshable;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;

/**
 * It describes children nodes of the enterprise application node.
 *
 * @author Michal Mocnak
 * @author Emmanuel Hugonnet (ehsavoie) <emmanuel.hugonnet@gmail.com>
 */
public class JBEarModulesChildren extends JBAsyncChildren implements Refreshable  {

    private static final Logger LOGGER = Logger.getLogger(JBEarApplicationsChildren.class.getName());

    private final Lookup lookup;
    private final String j2eeAppName;

    public JBEarModulesChildren(Lookup lookup, String j2eeAppName) {
        this.lookup = lookup;
        this.j2eeAppName = j2eeAppName;
    }

    @Override
    public void updateKeys(){
        setKeys(new Object[]{Util.WAIT_NODE});
        getExecutorService().submit(new WildflyEarModulesNodeUpdater(), 0);
    }
    
    class WildflyEarModulesNodeUpdater implements Runnable {

        List keys = new ArrayList();

        @Override
        public void run() {
            try {
                WildFlyDeploymentManager dm = lookup.lookup(WildFlyDeploymentManager.class);
                keys.addAll(dm.getClient().listEarSubModules(lookup, j2eeAppName));
            } catch (Exception ex) {
                LOGGER.log(Level.INFO, null, ex);
            }
            setKeys(keys);
        }
    }

    @Override
    protected void addNotify() {
        updateKeys();
    }

    @Override
    protected void removeNotify() {
        setKeys(java.util.Collections.EMPTY_SET);
    }

    @Override
    protected org.openide.nodes.Node[] createNodes(Object key) {
        if (key instanceof WildflyEjbModuleNode){
            return new Node[]{(WildflyEjbModuleNode)key};
        }

        if (key instanceof JBWebModuleNode){
            return new Node[]{(JBWebModuleNode)key};
        }

        if (key instanceof String && key.equals(Util.WAIT_NODE)){
            return new Node[]{Util.createWaitNode()};
        }

        return null;
    }
}
