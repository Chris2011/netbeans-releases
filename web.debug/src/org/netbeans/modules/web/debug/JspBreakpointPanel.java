/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.web.debug;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor.Message;
import org.openide.util.NbBundle;

import javax.swing.*;

import org.netbeans.spi.debugger.ui.Controller;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.jpda.LineBreakpoint;

import org.netbeans.modules.web.debug.util.Utils;
import org.netbeans.modules.web.debug.breakpoints.ActionsPanel;

/**
* Customizer of JspLineBreakpoint
*
* @author Martin Grebac
*/
public class JspBreakpointPanel extends JPanel implements Controller {

    static final long serialVersionUID =-8164649328980808272L;

    private ActionsPanel actionsPanel;
    private LineBreakpoint breakpoint;
    private boolean createBreakpoint = false;

    public JspBreakpointPanel() {
        this(LineBreakpoint.create(Context.getCurrentURL(), Context.getCurrentLineNumber()));
        createBreakpoint = true;
    }        
    
    /** Creates new form JspBreakpointPanel */
    public JspBreakpointPanel(LineBreakpoint b) {

        System.err.println("JspLineBreakpointPanel constructor: " + b);

        breakpoint = b;
        initComponents ();
        putClientProperty("HelpID", "jsp_breakpoint");//NOI18N

        cboxJspSourcePath.setEditable(false);
        
        // a11y
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(JspBreakpointPanel.class, "ACSD_LineBreakpointPanel")); // NOI18N
 
        Object[] objs = Utils.getJsps();
        if (objs != null) {
            if (objs.length != 0) {
                cboxJspSourcePath.setModel(
                    new DefaultComboBoxModel(objs)
                );
            }
        }
        
//        String jspSourcePath = breakpoint.getJspName();
//        String contextRoot = breakpoint.getContextRoot();
//       
//        cboxJspSourcePath.setSelectedItem(jspSourcePath == null ? "" : jspSourcePath);
//
//        String value = (String)cboxJspSourcePath.getSelectedItem();
//        if (value != null && value.indexOf(':') > -1) {
//            String ctx = value.substring(0,value.indexOf(':')-1);
//            String name = value.substring(value.indexOf(':') + 2);
//            breakpoint.setContextRoot(ctx);
//            breakpoint.setJspName(name);
//        }
//        
        fillLineNumber();
        actionsPanel = new ActionsPanel(b);
        pActions.add(actionsPanel, "Center");
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        pSettings = new javax.swing.JPanel();
        lblJspSourcePath = new javax.swing.JLabel();
        cboxJspSourcePath = new javax.swing.JComboBox();
        lblLineNumber = new javax.swing.JLabel();
        tfLineNumber = new javax.swing.JTextField();
        pActions = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        pSettings.setLayout(new java.awt.GridBagLayout());

        pSettings.setBorder(new javax.swing.border.TitledBorder("Settings"));
        lblJspSourcePath.setText(NbBundle.getBundle(JspBreakpointPanel.class).getString("CTL_Source_name"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);
        pSettings.add(lblJspSourcePath, gridBagConstraints);
        lblJspSourcePath.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(JspBreakpointPanel.class).getString("ACSN_CTL_Source_name"));

        cboxJspSourcePath.setEditable(true);
        cboxJspSourcePath.getEditor().getEditorComponent().addFocusListener(new java.awt.event.FocusListener() {
            public void focusGained(java.awt.event.FocusEvent evt) {
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
            }
        });

        cboxJspSourcePath.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboxJspSourcePathItemStateChanged(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        pSettings.add(cboxJspSourcePath, gridBagConstraints);
        cboxJspSourcePath.getAccessibleContext().setAccessibleName(NbBundle.getBundle(JspBreakpointPanel.class).getString("ACSN_CTL_Source_name"));
        cboxJspSourcePath.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(JspBreakpointPanel.class).getString("ACSD_CTL_Source_name"));

        lblLineNumber.setLabelFor(tfLineNumber);
        lblLineNumber.setText(NbBundle.getBundle(JspBreakpointPanel.class).getString("CTL_Line_number"));
        lblLineNumber.setDisplayedMnemonic(NbBundle.getBundle(JspBreakpointPanel.class).getString("CTL_Line_number_mnemonic").charAt(0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);
        pSettings.add(lblLineNumber, gridBagConstraints);
        lblLineNumber.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(JspBreakpointPanel.class).getString("ACSD_CTL_Line_number"));

        tfLineNumber.setColumns(7);
        tfLineNumber.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tfLineNumberFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfLineNumberFocusLost(evt);
            }
        });
        tfLineNumber.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tfLineNumberKeyTyped(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        pSettings.add(tfLineNumber, gridBagConstraints);
        tfLineNumber.getAccessibleContext().setAccessibleName(NbBundle.getBundle(JspBreakpointPanel.class).getString("ACSN_CTL_Line_number"));
        tfLineNumber.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(JspBreakpointPanel.class).getString("ACSD_CTL_Line_number"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(pSettings, gridBagConstraints);

        pActions.setLayout(new java.awt.BorderLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(pActions, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);

    }//GEN-END:initComponents

    private void cboxJspSourcePathItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboxJspSourcePathItemStateChanged
        String value = (String)cboxJspSourcePath.getSelectedItem();
        if (value != null && value.indexOf(':') > -1) {
            String ctx = value.substring(0,value.indexOf(':')-1);
            String name = value.substring(value.indexOf(':') + 2);
//            event.setContextRoot(ctx);
//            event.setJspName(name);
        }
        
    }//GEN-LAST:event_cboxJspSourcePathItemStateChanged

    private void tfLineNumberKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfLineNumberKeyTyped
        // Add your handling code here:
    }//GEN-LAST:event_tfLineNumberKeyTyped

    private void tfLineNumberFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfLineNumberFocusGained
        if (!evt.isTemporary()) {
            ((JTextField) evt.getComponent()).selectAll();
        }
    }//GEN-LAST:event_tfLineNumberFocusGained

    private void tfLineNumberFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfLineNumberFocusLost
//        if (!evt.isTemporary()) {
//            if (tfLineNumber.getText().trim().length() > 0) {
//                try {
//                    int i = Integer.parseInt(tfLineNumber.getText ());
//                    if (i < 1) {
//                        DialogDisplayer.getDefault().notify (
//                            new Message (
//                                NbBundle.getBundle(JspBreakpointPanel.class).getString("CTL_Bad_line_number"),  //NOI18N
//                                NotifyDescriptor.ERROR_MESSAGE
//                            )
//                        );
//                    } else if (event != null) {
//                            event.setLineNumber(i);
//                    }                    
//                } catch (NumberFormatException e) {
//                    DialogDisplayer.getDefault().notify (
//                        new Message (
//                            NbBundle.getBundle(JspBreakpointPanel.class).getString("CTL_Bad_line_number"),  //NOI18N
//                            NotifyDescriptor.ERROR_MESSAGE
//                        )
//                    );
//                }
//            }
//        }
    }//GEN-LAST:event_tfLineNumberFocusLost

    private void fillLineNumber () {
//        if (!isAcceptableDataObject()) {
//            return;
        int lnum = breakpoint.getLineNumber();
        if (lnum < 1)  {
            tfLineNumber.setText ("");  //NOI18N
        } else {
            tfLineNumber.setText(Integer.toString(lnum));
        }
    }
    
    /******************************/
    /* CONTROLLER:                */
    /******************************/
    
    //interface org.netbeans.modules.debugger.Controller
    public boolean ok() {
        
        int line = -1;
        actionsPanel.ok ();

        String ln = tfLineNumber.getText().trim();
        String jsp = (cboxJspSourcePath.getSelectedItem() == null) ? "" : cboxJspSourcePath.getSelectedItem().toString().trim();
        
        if (ln.length() > 0) {
            try {
                line = Integer.parseInt (ln);
            } catch (NumberFormatException e) {
            }
        }

        breakpoint.setLineNumber(line);
        //breakpoint.setURL(???); TODO
        if (createBreakpoint) {
            DebuggerManager.getDebuggerManager().addBreakpoint(breakpoint);
        }
        return true;
    }
    
    //interface org.netbeans.modules.debugger.Controller
    public boolean cancel() {
        return true;
    }
    
    //interface org.netbeans.modules.debugger.Controller
    public boolean isValid() {
        return true;
    }
        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cboxJspSourcePath;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblJspSourcePath;
    private javax.swing.JLabel lblLineNumber;
    private javax.swing.JPanel pActions;
    private javax.swing.JPanel pSettings;
    private javax.swing.JTextField tfLineNumber;
    // End of variables declaration//GEN-END:variables

}
