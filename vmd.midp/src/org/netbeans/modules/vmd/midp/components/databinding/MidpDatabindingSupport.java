/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.vmd.midp.components.databinding;

import java.util.Collection;
import java.util.HashSet;
import org.netbeans.modules.vmd.api.model.DesignComponent;
import org.netbeans.modules.vmd.api.model.DesignDocument;
import org.netbeans.modules.vmd.midp.components.MidpDocumentSupport;
import org.netbeans.modules.vmd.midp.components.categories.DatabindingCategoryCD;

/**
 *
 * @author karolharezlak
 */
public final class MidpDatabindingSupport {

    private MidpDatabindingSupport() {
    }

    public static DesignComponent getConnector(DesignComponent bindedComponent, String bindedPropertyName) {
        DesignComponent category = MidpDocumentSupport.getCategoryComponent(bindedComponent.getDocument(), DatabindingCategoryCD.TYPEID);
        for (DesignComponent dataSet : category.getComponents()) {
            for (DesignComponent connector : dataSet.getComponents()) {
                String currentbindedPropertyName = (String) connector.readProperty(DataSetConnectorCD.PROP_BINDED_PROPERTY).getPrimitiveValue();
                Long id = (Long) connector.readProperty(DataSetConnectorCD.PROP_COMPONENT_ID).getPrimitiveValue();
                if (currentbindedPropertyName.equals(bindedPropertyName) && id != null && bindedComponent.getComponentID() == id) {
                    return connector;
                }
            }
        }
        return null;
    }

    public static Collection<DesignComponent> getAllRelatedConnectors(DesignComponent bindedComponent) {
        DesignComponent category = MidpDocumentSupport.getCategoryComponent(bindedComponent.getDocument(), DatabindingCategoryCD.TYPEID);
        HashSet<DesignComponent> connectors = new HashSet<DesignComponent>();
        for (DesignComponent dataSet : category.getComponents()) {
            for (DesignComponent connector : dataSet.getComponents()) {
                Long id = (Long) connector.readProperty(DataSetConnectorCD.PROP_COMPONENT_ID).getPrimitiveValue();
                if (id != null && bindedComponent.getComponentID() == id) {
                    connectors.add(connector);
                }
            }
        }
        return connectors;
    }

    public static Collection<DesignComponent> getAllConnectors(DesignDocument document) {
        DesignComponent category = MidpDocumentSupport.getCategoryComponent(document, DatabindingCategoryCD.TYPEID);
        HashSet<DesignComponent> connectors = new HashSet<DesignComponent>();
        for (DesignComponent dataSet : category.getComponents()) {
            connectors.addAll(dataSet.getComponents());
        }
        return connectors;
    }

    public static String getDatabaindingAsText(final DesignComponent component, final String propertyName) {
        if (component != null) {
            final boolean[] isConnector = new boolean[1];
            component.getDocument().getTransactionManager().readAccess(new Runnable() {

                public void run() {
                    if (MidpDatabindingSupport.getConnector(component, propertyName) != null) {
                        isConnector[0] = true;
                    }
                }
            });
            if (isConnector[0]) {
                return "<databinding>"; //NOI18N
            }
        }
        return null;
    }
}
