/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.modules.i18n.wizard;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Map;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ChangeListener;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.netbeans.modules.i18n.HardCodedString;
import org.netbeans.modules.i18n.I18nSupport;
import org.netbeans.modules.i18n.I18nUtil;

import org.openide.loaders.DataObject;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.WizardDescriptor;


/**
 * <code>WizardDescriptor.Panel</code> used provide additional values modifying if avialble.
 * E.g. typically offers creation resource field or method in source used by replacing code.
 * It is the third panel of I18N Wizard.
 *
 * @author  Peter Zavadsky
 * @see Panel
 */
public class AdditionalWizardPanel extends JPanel {

    /** Local copy of i18n wizard data. */
    private final Map sourceMap = new TreeMap(new SourceData.DataObjectComparator());
    
    /** Sources on which additional modifications coudl happen in this panel. */
    private final Set viewedSources = new HashSet(0);

    /** Additional component. */
    private JComponent additionalComponent = EMPTY_COMPONENT;
    
    /** Empty component to show when no additional values are supported. */
    private static final JLabel EMPTY_COMPONENT = new JLabel(NbBundle.getBundle(ResourceWizardPanel.class).getString("TXT_HasNoAdditonal"));
    
    
    /** Creates new form HardCodedStringsPanel */
    private AdditionalWizardPanel() {
        initComponents();
        
        postInitComponents();

        addAdditionalComponent();

        sourceCombo.setModel(new DefaultComboBoxModel(sourceMap.keySet().toArray()));
    }


    /** Does additional init of components. */
    private void postInitComponents() {
        sourceLabel.setLabelFor(sourceCombo);
        sourceLabel.setDisplayedMnemonic(NbBundle.getBundle(getClass()).getString("LBL_Source_Mnem").charAt(0));
    }
    
    /** Getter for <code>sourceMap</code> property. */
    public Map getSourceMap() {
        return sourceMap;
    }

    /** Getter for <code>viewedSources</code> property. */
    public Set getViewedSources() {
        return viewedSources;
    }
    
    /** Setter for <code>resources</code> property. */
    public void setSourceMap(Map sourceMap) {
        this.sourceMap.clear();
        this.sourceMap.putAll(sourceMap);
        
        sourceCombo.setModel(new DefaultComboBoxModel(sourceMap.keySet().toArray()));
    }
    
    /** Inits additonal component. */
    private void addAdditionalComponent() {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(additionalComponent, gridBagConstraints);
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        sourceLabel = new javax.swing.JLabel();
        sourceCombo = new javax.swing.JComboBox();
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        
        sourceLabel.setText(NbBundle.getBundle(AdditionalWizardPanel.class).getString("LBL_Source"));
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(sourceLabel, gridBagConstraints1);
        
        
        sourceCombo.setRenderer(new SourceWizardPanel.DataObjectListCellRenderer());
        sourceCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sourceComboActionPerformed(evt);
            }
        }
        );
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(0, 11, 0, 0);
        gridBagConstraints1.weightx = 1.0;
        add(sourceCombo, gridBagConstraints1);
        
    }//GEN-END:initComponents

    private void sourceComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sourceComboActionPerformed
        Object selected = sourceCombo.getSelectedItem();

        if(selected == null)
            return;

        I18nSupport support = ((SourceData)sourceMap.get(selected)).getSupport();

        remove(additionalComponent);
        
        if(support.hasAdditionalCustomizer()) {
            additionalComponent = support.getAdditionalCustomizer();
            viewedSources.add(selected);
        } else {
            additionalComponent = EMPTY_COMPONENT;
        }

        addAdditionalComponent();
        
        revalidate();
    }//GEN-LAST:event_sourceComboActionPerformed

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel sourceLabel;
    private javax.swing.JComboBox sourceCombo;
    // End of variables declaration//GEN-END:variables

    /** <code>WizardDescriptor.Panel</code> used for <code>AdditionalPanel</code>. 
     * @see org.openide.WizardDescriptor.Panel*/
    public static class Panel extends I18nWizardDescriptor.Panel {

        /** Component. */
        private final AdditionalWizardPanel additionalPanel = new AdditionalWizardPanel();
        
        
        /** Gets component to display. Implements superclass abstract method. 
         * @return <code>AdditionalPanel</code> instance */
        protected Component createComponent() {
            JPanel panel = new JPanel();
            
            panel.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(2)); // NOI18N
            panel.setName(NbBundle.getBundle(getClass()).getString("TXT_ModifyAdditional"));
            panel.setPreferredSize(I18nWizardDescriptor.PREFERRED_DIMENSION);

            panel.setLayout(new GridBagLayout());
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.weightx = 1.0;
            constraints.weighty = 1.0;
            constraints.fill = GridBagConstraints.BOTH;
            panel.add(additionalPanel, constraints);
            
            return panel;
        }

        /** Reads settings at the start when the panel comes to play. Overrides superclass method. */
        public void readSettings(Object settings) {
            additionalPanel.setSourceMap((Map)settings);
        }

        /** Stores settings at the end of panel show. Overrides superclass abstract method. */
        public void storeSettings(Object settings) {
            
            // Alter i18n string values if changing additional values could affect them.
            Map sourceMap = additionalPanel.getSourceMap();
            Iterator it = additionalPanel.getViewedSources().iterator();
            
            while(it.hasNext()) {
                SourceData sourceData = (SourceData)sourceMap.get(it.next());
                
                Object[] hcStrings = sourceData.getStringMap().keySet().toArray();
                
                for(int i=0; i<hcStrings.length; i++) {
                    // Actual replacing of default values.
                    sourceData.getStringMap().put(hcStrings[i], sourceData.getSupport().getDefaultI18nString((HardCodedString)hcStrings[i]));
                }
            }
            
            // Update sources.
            ((Map)settings).clear();
            ((Map)settings).putAll(sourceMap);
        }
        
        /** Searches hard coded strings in sources and puts found hard coded string - i18n string pairs
         * into settings. Implements <code>ProgressMonitor</code> interface method. */
        public void doLongTimeChanges() {
            // Replace panel.
            ProgressWizardPanel progressPanel = new ProgressWizardPanel(true);
            
            showProgressPanel(progressPanel);
            
            progressPanel.setMainText(NbBundle.getBundle(getClass()).getString("LBL_AdditionalIn"));
            progressPanel.setMainProgress(0);
            
            // Alter i18n string values if changing additional values could affect them.
            Map sourceMap = ((AdditionalWizardPanel)getComponent()).getSourceMap();
            Iterator it = ((AdditionalWizardPanel)getComponent()).getViewedSources().iterator();
            
            for(int i=0; it.hasNext(); i++) {
                DataObject source = (DataObject)it.next();
                
                SourceData sourceData = (SourceData)sourceMap.get(source);

                progressPanel.setMainText(NbBundle.getBundle(getClass()).getString("LBL_AdditionalIn")+" "+source.getPrimaryFile().getPackageName('.')); // NOI18N
                
                Object[] hcStrings = sourceData.getStringMap().keySet().toArray();
                
                for(int j=0; i<hcStrings.length; j++) {
                    // Actual replacing of default values.
                    sourceData.getStringMap().put(hcStrings[j], sourceData.getSupport().getDefaultI18nString((HardCodedString)hcStrings[j]));
                } // End of inner for.
                
                progressPanel.setMainProgress((int)((i+1)/(float)sourceMap.size() * 100));                
            } // End of outer for.
        }

        /** Helper method. Places progress panel for monitoring search. */
        private void showProgressPanel(ProgressWizardPanel progressPanel) {
            ((Container)getComponent()).remove(additionalPanel);
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.weightx = 1.0;
            constraints.weighty = 1.0;
            constraints.fill = GridBagConstraints.BOTH;
            ((Container)getComponent()).add(progressPanel, constraints);
            ((JComponent)getComponent()).revalidate();
            getComponent().repaint();
        }
        
        /** Resets panel back after monitoring search. Implements <code>ProgressMonitor</code> interface method. */
        public void reset() {
            Container container = (Container)getComponent();
            
            if(!container.isAncestorOf(additionalPanel)) {
                container.removeAll();
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.weightx = 1.0;
                constraints.weighty = 1.0;
                constraints.fill = GridBagConstraints.BOTH;
                container.add(additionalPanel, constraints);
            }
        }
        
        /** Gets help. Implements superclass abstract method. */
        public HelpCtx getHelp() {
            return new HelpCtx(I18nUtil.HELP_ID_WIZARD);
        }
        
    } // End of nested Panel class.
    
}
