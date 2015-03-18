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
package org.netbeans.modules.j2ee.jboss4.ide.ui;

import java.io.File;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import java.awt.Component;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.j2ee.jboss4.ide.ui.JBPluginUtils.Version;
import org.openide.util.NbBundle;

/**
 *
 * @author Ivan Sidorkin
 */
public class AddServerLocationPanel implements WizardDescriptor.Panel, ChangeListener {
    
    private JBInstantiatingIterator instantiatingIterator;
    
    private AddServerLocationVisualPanel component;
    private WizardDescriptor wizard;
    private transient Set listeners = new HashSet(1);
    
    public AddServerLocationPanel(JBInstantiatingIterator instantiatingIterator){
        this.instantiatingIterator = instantiatingIterator;
    }
    
    public void stateChanged(ChangeEvent ev) {
        fireChangeEvent(ev);
    }
    
    private void fireChangeEvent(ChangeEvent ev) {
        Iterator it;
        synchronized (listeners) {
            it = new HashSet(listeners).iterator();
        }
        while (it.hasNext()) {
            ((ChangeListener)it.next()).stateChanged(ev);
        }
    }
    
    public Component getComponent() {
        if (component == null) {
            component = new AddServerLocationVisualPanel();
            component.addChangeListener(this);
        }
        return component;
    }
    
    public HelpCtx getHelp() {
        return new HelpCtx("j2eeplugins_registering_app_server_jboss_location"); //NOI18N
    }

    @Override
    public boolean isValid() {
        String locationStr = component.getInstallLocation();
        if (locationStr == null || locationStr.trim().length() < 1) {
            wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE,
                    NbBundle.getMessage(AddServerLocationPanel.class, "MSG_SpecifyServerLocation")); // NOI18N
            return false;
        }

        File f = new File(locationStr);
        Version version = JBPluginUtils.getServerVersion(new File(locationStr));

        if (!JBPluginUtils.isGoodJBServerLocation(f, version)) {
            wizard.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE,
                    NbBundle.getMessage(AddServerLocationPanel.class, "MSG_InvalidServerLocation")); // NOI18N
            return false;
        }

        if (version != null
                && version.compareToIgnoreUpdate(JBPluginUtils.JBOSS_7_0_0) >= 0
                && version.compareToIgnoreUpdate(JBPluginUtils.JBOSS_7_1_0) < 0) {
            wizard.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE,
                    NbBundle.getMessage(AddServerLocationPanel.class, "MSG_InvalidJBoss7Versio")); // NOI18N
            return false;
        }
                
        wizard.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, null);
        wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);
        JBPluginProperties.getInstance().setInstallLocation(component.getInstallLocation());
        JBPluginProperties.getInstance().saveProperties();
        instantiatingIterator.setInstallLocation(locationStr);
        return true;
    }

    public void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }
    
    public void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }
    
    public void readSettings(Object settings) {
        if (wizard == null) {
            wizard = (WizardDescriptor) settings;
        }
    }
    
    public void storeSettings(Object settings) {
        instantiatingIterator.setInstallLocation(
                ((AddServerLocationVisualPanel) getComponent()).getInstallLocation());
    }
}
