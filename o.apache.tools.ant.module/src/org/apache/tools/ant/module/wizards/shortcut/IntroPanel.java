/*
 *                         Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with 
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the Ant module
 * The Initial Developer of the Original Code is Jayme C. Edwards.
 * Portions created by Jayme C. Edwards are Copyright (c) 2000.
 * All Rights Reserved.
 *
 * Contributor(s): Jesse Glick.
 */

package org.apache.tools.ant.module.wizards.shortcut;

import java.awt.Component;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.loaders.TemplateWizard;

public class IntroPanel extends javax.swing.JPanel implements WizardDescriptor.Panel /* .FinishPanel */ {

    /** Create the wizard panel and set up some basic properties. */
    public IntroPanel () {
        initComponents ();
        initAccessibility();
        // Provide a name in the title bar.
        setName (NbBundle.getMessage (IntroPanel.class, "IP_LBL_cfg_basic_opts"));
    }

    // --- VISUAL DESIGN OF PANEL ---
    
    public void requestFocus () {
        super.requestFocus ();
        customizeCheck.requestFocus ();
    }

    
    private void initAccessibility () {    
        this.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(IntroPanel.class, "IP_TEXT_select_how_to_install_shortcut"));
        menuCheck.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(IntroPanel.class, "ACS_IP_LBL_add_menu_item"));
        toolbarCheck.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(IntroPanel.class, "ACS_IP_LBL_add_toolbar_button"));
        projectCheck.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(IntroPanel.class, "ACS_IP_LBL_add_to_project"));
        keyboardCheck.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(IntroPanel.class, "ACS_IP_LBL_add_kbd_shortcut"));        
        customizeCheck.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(IntroPanel.class, "ACS_IP_LBL_cust_code_checkbox"));
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        customizeCheck = new javax.swing.JCheckBox();
        menuCheck = new javax.swing.JCheckBox();
        toolbarCheck = new javax.swing.JCheckBox();
        projectCheck = new javax.swing.JCheckBox();
        keyboardCheck = new javax.swing.JCheckBox();
        hintsArea = new javax.swing.JTextArea();

        setLayout(new java.awt.GridBagLayout());

        customizeCheck.setMnemonic((NbBundle.getMessage(IntroPanel.class, "IP_LBL_cust_code_checkbox_mnem")).charAt(0));
        customizeCheck.setText(NbBundle.getMessage(IntroPanel.class, "IP_LBL_cust_code_checkbox"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(customizeCheck, gridBagConstraints);

        menuCheck.setMnemonic((NbBundle.getMessage(IntroPanel.class, "IP_LBL_add_menu_item_mnem")).charAt(0));
        menuCheck.setText(NbBundle.getMessage(IntroPanel.class, "IP_LBL_add_menu_item"));
        menuCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                someCheckboxClicked(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(menuCheck, gridBagConstraints);

        toolbarCheck.setMnemonic((NbBundle.getMessage(IntroPanel.class, "IP_LBL_add_toolbar_button_mnem")).charAt(0));
        toolbarCheck.setText(NbBundle.getMessage(IntroPanel.class, "IP_LBL_add_toolbar_button"));
        toolbarCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                someCheckboxClicked(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(toolbarCheck, gridBagConstraints);

        projectCheck.setMnemonic((NbBundle.getMessage(IntroPanel.class, "IP_LBL_add_to_project_mnem")).charAt(0)
        );
        projectCheck.setText(NbBundle.getMessage(IntroPanel.class, "IP_LBL_add_to_project"));
        projectCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                someCheckboxClicked(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(projectCheck, gridBagConstraints);

        keyboardCheck.setMnemonic((NbBundle.getMessage(IntroPanel.class, "IP_LBL_add_kbd_shortcut_mnem")).charAt(0)
        );
        keyboardCheck.setText(NbBundle.getMessage(IntroPanel.class, "IP_LBL_add_kbd_shortcut"));
        keyboardCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                someCheckboxClicked(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        add(keyboardCheck, gridBagConstraints);

        hintsArea.setBackground(new java.awt.Color(204, 204, 204));
        hintsArea.setEditable(false);
        hintsArea.setFont(javax.swing.UIManager.getFont ("Label.font"));
        hintsArea.setForeground(new java.awt.Color(102, 102, 153));
        hintsArea.setLineWrap(true);
        hintsArea.setText(NbBundle.getMessage(IntroPanel.class, "IP_TEXT_select_how_to_install_shortcut"));
        hintsArea.setWrapStyleWord(true);
        hintsArea.setDisabledTextColor(javax.swing.UIManager.getColor ("Label.foreground"));
        hintsArea.setEnabled(false);
        hintsArea.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(hintsArea, gridBagConstraints);

    }//GEN-END:initComponents

    private void someCheckboxClicked (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_someCheckboxClicked
        fireChangeEvent ();
    }//GEN-LAST:event_someCheckboxClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea hintsArea;
    private javax.swing.JCheckBox projectCheck;
    private javax.swing.JCheckBox menuCheck;
    private javax.swing.JCheckBox keyboardCheck;
    private javax.swing.JCheckBox toolbarCheck;
    private javax.swing.JCheckBox customizeCheck;
    // End of variables declaration//GEN-END:variables

    // --- WizardDescriptor.Panel METHODS ---

    // Get the visual component for the panel. In this template, the same class
    // serves as the component and the Panel interface, but you could keep
    // them separate if you wished.
    public Component getComponent () {
        return this;
    }

    public HelpCtx getHelp () {
        return new HelpCtx("ant.wizard.shortcut");
    }

    public boolean isValid () {
        return menuCheck.isSelected () ||
               toolbarCheck.isSelected () ||
               projectCheck.isSelected () ||
               keyboardCheck.isSelected ();
    }

    private final Set listeners = new HashSet (1); // Set<ChangeListener>
    public final void addChangeListener (ChangeListener l) {
        synchronized (listeners) {
            listeners.add (l);
        }
    }
    public final void removeChangeListener (ChangeListener l) {
        synchronized (listeners) {
            listeners.remove (l);
        }
    }
    protected final void fireChangeEvent () {
        Iterator it;
        synchronized (listeners) {
            it = new HashSet (listeners).iterator ();
        }
        ChangeEvent ev = new ChangeEvent (this);
        while (it.hasNext ()) {
            ((ChangeListener) it.next ()).stateChanged (ev);
        }
    }

    public void readSettings (Object settings) {
        // XXX should read checkboxes from settings... skip for now.
    }
    public void storeSettings (Object settings) {
        TemplateWizard wiz = (TemplateWizard) settings;
        wiz.putProperty (ShortcutIterator.PROP_SHOW_CUST, customizeCheck.isSelected () ? Boolean.TRUE : Boolean.FALSE);
        wiz.putProperty (ShortcutIterator.PROP_SHOW_MENU, menuCheck.isSelected () ? Boolean.TRUE : Boolean.FALSE);
        wiz.putProperty (ShortcutIterator.PROP_SHOW_TOOL, toolbarCheck.isSelected () ? Boolean.TRUE : Boolean.FALSE);
        wiz.putProperty (ShortcutIterator.PROP_SHOW_PROJ, projectCheck.isSelected () ? Boolean.TRUE : Boolean.FALSE);
        wiz.putProperty (ShortcutIterator.PROP_SHOW_KEYB, keyboardCheck.isSelected () ? Boolean.TRUE : Boolean.FALSE);
    }

}
