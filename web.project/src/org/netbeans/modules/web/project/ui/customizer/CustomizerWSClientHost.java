/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.web.project.ui.customizer;

import java.util.Collections;
import java.util.List;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;
import javax.swing.JLabel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.filesystems.FileObject;

import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.modules.web.project.ProjectWebModule;
import org.netbeans.modules.web.project.WebProjectType;

import org.netbeans.modules.websvc.spi.webservices.WebServicesConstants;
import org.netbeans.modules.websvc.api.webservices.WsCompileEditorSupport;


/** Host for WsCompile features editor for editing the features enabled for
 *  running WsCompile on a web service or a web service client.
 *
 *  property format: 'webservice.client.[servicename].features=xxx,yyy,zzz
 *
 * @author Peter Williams
 */
public class CustomizerWSClientHost extends javax.swing.JPanel implements PropertyChangeListener, HelpCtx.Provider {
    
    private WebProjectProperties webProperties;
    private WsCompileEditorSupport.Panel wsCompileEditor;

    private List serviceSettings;
    
    public CustomizerWSClientHost(WebProjectProperties webProperties, List serviceSettings) {
//        System.out.println("WSClientCustomizer: constructor");
        assert serviceSettings != null;
        initComponents();

        this.webProperties = webProperties;
        this.wsCompileEditor = null;
        this.serviceSettings = serviceSettings;

        if (serviceSettings.size() > 0)
            initValues();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents

        setLayout(new java.awt.BorderLayout());

    }//GEN-END:initComponents

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
    public void addNotify() {
        super.addNotify();
        
//        System.out.println("WSClientCustomizer: addNotify (" + this.getComponentCount() + " subcomponents)");
        JPanel component = wsCompileEditor.getComponent();

        removeAll(); // !PW is this necessary?
        add(component);
        
        component.addPropertyChangeListener(WsCompileEditorSupport.PROP_FEATURES_CHANGED, this);
    }
    
    public void removeNotify() {
        super.removeNotify();
        
//        System.out.println("WSClientCustomizer: removeNotify");
        JPanel component = wsCompileEditor.getComponent();
        component.removePropertyChangeListener(WsCompileEditorSupport.PROP_FEATURES_CHANGED, this);
    }
   
    public void initValues() {
//        System.out.println("WSClientCustomizer: initValues");
        if(wsCompileEditor == null) {
			WsCompileEditorSupport editorSupport = (WsCompileEditorSupport) Lookup.getDefault().lookup(WsCompileEditorSupport.class);
            wsCompileEditor = editorSupport.getWsCompileSupport();
        }
        
        wsCompileEditor.initValues(serviceSettings, WsCompileEditorSupport.TYPE_CLIENT);
    }   
    
//    public void validatePanel() throws WizardValidationException {
//        System.out.println("WSClientCustomizer: validatePanel ");
//        if(wsCompileEditor != null) {
//            wsCompileEditor.validatePanel();
//        }
//    }
    
    public void propertyChange(PropertyChangeEvent evt) {
//        System.out.println("WSClientCustomizer: propertyChange - " + evt.getPropertyName());
        
        WsCompileEditorSupport.FeatureDescriptor newFeatureDesc = (WsCompileEditorSupport.FeatureDescriptor) evt.getNewValue();
        String propertyName = "wscompile.client." + newFeatureDesc.getServiceName() + ".features";
        webProperties.putAdditionalProperty(propertyName, newFeatureDesc.getFeatures());
    }
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx(CustomizerWSClientHost.class);
    }
    
}
