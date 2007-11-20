/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.j2ee.sun.ide.runtime.nodes;

import javax.swing.Action;


import org.openide.util.actions.SystemAction;

import org.netbeans.modules.j2ee.sun.bridge.apis.AppserverMgmtController;
import org.netbeans.modules.j2ee.sun.ide.runtime.actions.DeleteResourceAction;
import org.netbeans.modules.j2ee.sun.util.NodeTypes;
import org.openide.actions.PropertiesAction;

/**
 *
 *
 */
public class JDBCResourceNode extends ResourceLeafNode {

    private static final String NODE_TYPE = NodeTypes.JDBC_RESOURCE;
    
    /**
     * Constructor for creating an instance of a JDBC leaf node.
     *
     * @param controller
     * @param jdbcName
     */
    public JDBCResourceNode(final AppserverMgmtController controller, 
            final String jdbcName) {
        super(controller, NODE_TYPE, jdbcName);
    }
    
    
    /**
     * Return the actions associated with the menu drop down seen when
     * a user right-clicks on a node in the plugin.
     *
     * @param boolean true/false
     * @return An array of Action objects.
     */
    public Action[] getActions(boolean flag) {
        if(getResourceName().equals("jdbc/__TimerPool")) {
            return new SystemAction[] {
                SystemAction.get(PropertiesAction.class)
            };
        } else {
            return new SystemAction[] {
                SystemAction.get(DeleteResourceAction.class),
                SystemAction.get(PropertiesAction.class)
            };
        }
    }
    
    
    /**
     * Return the SheetProperties to be displayed for this JDBC entry.
     *
     * @return A java.util.Map containing all JDBC properties.
     */
    protected java.util.Map getSheetProperties() {
        return getAppserverMgmtController().
            getJDBCResourceProperties(getResourceName(), 
                getPropertiesToIgnore());
        
    }
    
    
    /**
     * Sets the property as an attribute to the underlying AMX mbeans. It 
     * usually will delegate to the controller object which is responsible for
     * finding the correct AMX mbean objectname in order to execute a 
     * JMX setAttribute.
     *
     * @param attrName The name of the property to be set.
     * @param value The value retrieved from the property sheet to be set in the
     *        backend.
     * @returns the updated Attribute accessed from the Sheet.
     */
    public javax.management.Attribute setSheetProperty(String attrName, Object value) {
        return getAppserverMgmtController().
            setJDBCResourceProperty(getResourceName(), attrName, value);
    }
    
    /**
     * Removes resource.
     */
    public void remove() {
        getAppserverMgmtController().
            deleteJDBCResource(getResourceName());
    }
    
}
