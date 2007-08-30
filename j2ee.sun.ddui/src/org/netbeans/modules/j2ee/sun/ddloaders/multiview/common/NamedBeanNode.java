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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.j2ee.sun.ddloaders.multiview.common;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.modules.j2ee.sun.dd.api.ASDDVersion;
import org.netbeans.modules.j2ee.sun.dd.api.CommonDDBean;
import org.netbeans.modules.j2ee.sun.ddloaders.SunDescriptorDataObject;
import org.netbeans.modules.j2ee.sun.ddloaders.Utils;
import org.netbeans.modules.j2ee.sun.ddloaders.multiview.BaseSectionNode;
import org.netbeans.modules.j2ee.sun.ddloaders.multiview.CustomSectionNodePanel;
import org.netbeans.modules.j2ee.sun.ddloaders.multiview.DDSectionNodeView;
import org.netbeans.modules.xml.multiview.XmlMultiViewDataObject;
import org.netbeans.modules.xml.multiview.XmlMultiViewDataSynchronizer;
import org.netbeans.modules.xml.multiview.ui.SectionNodeInnerPanel;
import org.netbeans.modules.xml.multiview.ui.SectionNodePanel;
import org.netbeans.modules.xml.multiview.ui.SectionNodeView;
import org.openide.ErrorManager;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;


/**
 * @author Peter Williams
 */
public abstract class NamedBeanNode extends BaseSectionNode {

    private static final String BOUND_ANNOTATION_ICON = 
            "org/netbeans/modules/j2ee/sun/ddloaders/resources/BoundAnnotation.png"; // NOI18N
    private static final String BOUND_STANDARD_ICON = 
            "org/netbeans/modules/j2ee/sun/ddloaders/resources/BoundStandardDD.png"; // NOI18N
    private static final String UNBOUND_DD_ICON = 
            "org/netbeans/modules/j2ee/sun/ddloaders/resources/UnBoundDD.png"; // NOI18N
    private static final String VIRTUAL_DD_ICON = 
            "org/netbeans/modules/j2ee/sun/ddloaders/resources/VirtualDD.png"; // NOI18N
            
    private DDBinding binding;
    private String beanNameProperty;
    private RemoveBeanAction removeBeanAction;
    private CustomSectionNodePanel customSectionNodePanel;

    protected NamedBeanNode(final SectionNodeView sectionNodeView, final DDBinding binding, 
            final String beanNameProperty, final String iconBase, final ASDDVersion version) {
        // !PW FIXME generateTitle is a total hack.  Figure out a better way to this.
        this(sectionNodeView, binding, beanNameProperty, generateTitle(binding.getSunBean(), beanNameProperty), iconBase, version);
    }
    
    protected NamedBeanNode(final SectionNodeView sectionNodeView, final DDBinding binding, 
            final String beanNameProperty, final String beanTitle, final String iconBase, final ASDDVersion version) {
        super(sectionNodeView, new Children.Array(), binding.getSunBean(), version, beanTitle, iconBase);
        
        this.binding = binding;
        this.beanNameProperty = beanNameProperty;
        
        binding.getSunBean().addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                String oldDisplayName = getDisplayName();
                String newDisplayName = generateTitle();
                if (!oldDisplayName.equals(newDisplayName)) {
                    setDisplayName(newDisplayName);
                    firePropertyChange(Node.PROP_DISPLAY_NAME, oldDisplayName, newDisplayName);
                }
            }
        });
        
        helpProvider = true;
    }
    
    @Override
    protected abstract SectionNodeInnerPanel createNodeInnerPanel();
    
    @Override
    protected SectionNodePanel createSectionNodePanel() {
        return new CustomSectionNodePanel(this);
    }    
    
    @Override
    public SectionNodePanel getSectionNodePanel() {
        SectionNodePanel nodePanel = super.getSectionNodePanel();
        if(removeBeanAction != null && nodePanel.getHeaderButtons() == null) {
            nodePanel.setHeaderActions(new Action [] { removeBeanAction });
        }
        if(nodePanel instanceof CustomSectionNodePanel) {
            customSectionNodePanel = (CustomSectionNodePanel) nodePanel;
            updateIcon();
        }
        return nodePanel;
    }
    
    public void updateIcon() {
        if(customSectionNodePanel != null) {
            String bindingIcon;
            if(binding.isVirtual()) {
                bindingIcon = VIRTUAL_DD_ICON;
            } else if(binding.isBound()) {
                if(binding.isAnnotated()) {
                    bindingIcon = BOUND_ANNOTATION_ICON;
                } else {
                    bindingIcon = BOUND_STANDARD_ICON;
                }
            } else {
                bindingIcon = UNBOUND_DD_ICON;
            }

            customSectionNodePanel.setTitleIcon(bindingIcon);
        } else {
            ErrorManager.getDefault().log(ErrorManager.WARNING, 
                    "CustomSectionNodePanel is null for " + this.getClass().getSimpleName()); // NOI18N
        }
    }
    
    public DDBinding getBinding() {
        return binding;
    }
    
    public boolean addVirtualBean() {
        if(binding.isVirtual()) {
            Node parentNode = getParentNode();
            if(parentNode instanceof NamedBeanGroupNode) {
                NamedBeanGroupNode groupNode = (NamedBeanGroupNode) parentNode;
                binding.clearVirtual();
                updateIcon();
                groupNode.addBean(binding.getSunBean());
                
                // If parent of this group is it's own named bean (ie EjbNode),
                // then pass the add request up the chain.
                Node namedParentNode = groupNode.getParentNode();
                if(namedParentNode instanceof NamedBeanNode) {
                    ((NamedBeanNode) namedParentNode).addVirtualBean();
                }
                
                SunDescriptorDataObject dataObject = (SunDescriptorDataObject) getSectionNodeView().getDataObject();
                XmlMultiViewDataSynchronizer synchronizer = dataObject.getModelSynchronizer();
                synchronizer.requestUpdateData();
                return true;
            }
        }
        return false;
    }    
    
    /** Expected to be called from derived class constructor, if needed.
     */
    protected void enableRemoveAction() {
        removeBeanAction = new RemoveBeanAction(NbBundle.getMessage(NamedBeanNode.class, "LBL_Remove"));
    }
    
    protected String generateTitle() {
        return generateTitle((CommonDDBean) key, beanNameProperty);
    }
    
    static String generateTitle(CommonDDBean bean, String nameProperty) {
        return Utils.getBeanDisplayName(bean, nameProperty);
    }
    
    private class RemoveBeanAction extends AbstractAction {
        
        RemoveBeanAction(String actionText) {
            super(actionText);
//            char mnem = NbBundle.getMessage(NamedBeanNode.class,"MNE_Remove").charAt(0);
//            putValue(MNEMONIC_KEY, Integer.valueOf(mnem));
        }

        @Override
        public boolean isEnabled() {
            return !binding.isVirtual();
        }

        @Override
        public void setEnabled(boolean newValue) {
            newValue = newValue && !binding.isVirtual();
            super.setEnabled(newValue);
        }
        
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if(!isEnabled()) {
                ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, "<Remove> action should not be enabled for " + binding.toString());
            } else {
                SectionNodeView view = getSectionNodeView();
                if(view instanceof DDSectionNodeView) {
                    XmlMultiViewDataObject dObj = ((DDSectionNodeView) view).getDataObject();
                    if(dObj instanceof SunDescriptorDataObject) {
                        Node parentNode = getParentNode();
                        if(parentNode instanceof NamedBeanGroupNode) {
                            NamedBeanGroupNode groupNode = (NamedBeanGroupNode) parentNode;
                            groupNode.removeBean((CommonDDBean) key);
                        }
                        
                        SunDescriptorDataObject sunDO = (SunDescriptorDataObject) dObj;
                        sunDO.modelUpdatedFromUI();
//                        sunDO.setChangedFromUI(true);
                    }
                }
            }
        }
    }
    
}
