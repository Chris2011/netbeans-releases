/*
 * LineBreakpointPanel.java
 *
 * Created on 24. b�ezen 2004, 16:18
 */

package org.netbeans.modules.debugger.jpda.ui.breakpoints;

import javax.swing.JPanel;
import org.netbeans.api.debugger.DebuggerManager;

import org.netbeans.api.debugger.jpda.MethodBreakpoint;
import org.netbeans.modules.debugger.jpda.ui.Context;
import org.netbeans.spi.debugger.ui.Controller;
import org.openide.util.NbBundle;


/**
 *
 * @author  Jan Jancura
 */
public class MethodBreakpointPanel extends JPanel implements Controller {
    
    private ActionsPanel                actionsPanel; 
    private MethodBreakpoint            breakpoint;
    private boolean                     createBreakpoint = false;
    
    
    private static MethodBreakpoint creteBreakpoint () {
        MethodBreakpoint mb = MethodBreakpoint.create (
            Context.getCurrentClassName (),
            Context.getCurrentMethodName ()
        );
        mb.setPrintText (
            NbBundle.getBundle (MethodBreakpointPanel.class).getString 
                ("CTL_Method_Breakpoint_Print_Text")
        );
        return mb;
    }
    
    
    /** Creates new form LineBreakpointPanel */
    public MethodBreakpointPanel () {
        this (creteBreakpoint ());
        createBreakpoint = true;
    }
    
    /** Creates new form LineBreakpointPanel */
    public MethodBreakpointPanel (MethodBreakpoint b) {
        breakpoint = b;
        initComponents ();
        
        String className = "";
        String[] fs = b.getClassFilters ();
        if (fs.length > 0) className = fs [0];
        int i = className.lastIndexOf ('.');
        if (i < 0) {
            tfPackageName.setText ("");
            tfClassName.setText (className);
        } else {
            tfPackageName.setText (className.substring (0, i));
            tfClassName.setText (className.substring (i + 1, className.length ()));
        }
        tfMethodName.setText (b.getMethodName ());
        cbAllMethods.setSelected (b.getMethodName().equals (""));
//        cbInnerClasses.setSelected (b.getApplyToAnonymousInnerClasses ());
        tfCondition.setText (b.getCondition ());
        
        actionsPanel = new ActionsPanel (b);
        pActions.add (actionsPanel, "Center");
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents()//GEN-BEGIN:initComponents
    {
        java.awt.GridBagConstraints gridBagConstraints;

        pSettings = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        tfCondition = new javax.swing.JTextField();
        tfPackageName = new javax.swing.JTextField();
        tfClassName = new javax.swing.JTextField();
        cbInnerClasses = new javax.swing.JCheckBox();
        cbAllMethods = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        tfMethodName = new javax.swing.JTextField();
        pActions = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        pSettings.setLayout(new java.awt.GridBagLayout());

        pSettings.setBorder(new javax.swing.border.TitledBorder("Settings"));
        jLabel2.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle").getString("MN_L_Method_Breakpoint_Package_Name").charAt(0));
        jLabel2.setLabelFor(tfPackageName);
        jLabel2.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle").getString("L_Method_Breakpoint_Package_Name"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pSettings.add(jLabel2, gridBagConstraints);
        jLabel2.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle").getString("ACSD_L_Method_Breakpoint_Package_Name"));

        jLabel3.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle").getString("MN_L_Method_Breakpoint_Class_Name").charAt(0));
        jLabel3.setLabelFor(tfClassName);
        jLabel3.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle").getString("L_Method_Breakpoint_Class_Name"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pSettings.add(jLabel3, gridBagConstraints);
        jLabel3.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle").getString("ACSD_L_Method_Breakpoint_Class_Name"));

        jLabel5.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle").getString("MN_L_Method_Breakpoint_Condition").charAt(0));
        jLabel5.setLabelFor(tfCondition);
        jLabel5.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle").getString("L_Method_Breakpoint_Condition"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pSettings.add(jLabel5, gridBagConstraints);
        jLabel5.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle").getString("ACSD_L_Method_Breakpoint_Condition"));

        tfCondition.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle").getString("TTT_TF_Method_Breakpoint_Condition"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pSettings.add(tfCondition, gridBagConstraints);
        tfCondition.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle").getString("ACSD_TF_Method_Breakpoint_Condition"));

        tfPackageName.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle").getString("TTT_TF_Method_Breakpoint_Package_Name"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pSettings.add(tfPackageName, gridBagConstraints);
        tfPackageName.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle").getString("ACSD_TF_Method_Breakpoint_Package_Name"));

        tfClassName.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle").getString("TTT_TF_Method_Breakpoint_Class_Name"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pSettings.add(tfClassName, gridBagConstraints);
        tfClassName.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle").getString("ACSD_TF_Method_Breakpoint_Class_Name"));

        cbInnerClasses.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle").getString("MN_CB_Method_Breakpoint_Inner_Classes").charAt(0));
        cbInnerClasses.setSelected(true);
        cbInnerClasses.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle").getString("CB_Method_Breakpoint_Inner_Classes"));
        cbInnerClasses.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle").getString("TTT_CB_Method_Breakpoint_Inner_Classes"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pSettings.add(cbInnerClasses, gridBagConstraints);
        cbInnerClasses.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle").getString("ACSD_CB_Method_Breakpoint_Inner_Classes"));

        cbAllMethods.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle").getString("MN_CB_Method_Breakpoint_All_Methods").charAt(0));
        cbAllMethods.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle").getString("CB_Method_Breakpoint_All_Methods"));
        cbAllMethods.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle").getString("TTT_CB_Method_Breakpoint_All_Methods"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pSettings.add(cbAllMethods, gridBagConstraints);
        cbAllMethods.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle").getString("ACSD_CB_Method_Breakpoint_All_Methods"));

        jLabel1.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle").getString("MN_L_Method_Breakpoint_Method_Name").charAt(0));
        jLabel1.setLabelFor(tfMethodName);
        jLabel1.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle").getString("L_Method_Breakpoint_Method_Name"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pSettings.add(jLabel1, gridBagConstraints);
        jLabel1.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle").getString("ACSD_L_Method_Breakpoint_Method_Name"));

        tfMethodName.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle").getString("TTT_TF_Method_Breakpoint_Method_Name"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pSettings.add(tfMethodName, gridBagConstraints);
        tfMethodName.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle").getString("ACSD_TF_Method_Breakpoint_Method_Name"));

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

    
    // Controller implementation ...............................................
    
    /**
     * Called when "Ok" button is pressed.
     *
     * @return whether customizer can be closed
     */
    public boolean ok () {
        actionsPanel.ok ();
        String className = ((String) tfPackageName.getText ()).trim ();
        if (className.length () > 0)
            className += '.';
        className += tfClassName.getText ().trim ();
        breakpoint.setClassFilters (new String[] {className});
        breakpoint.setMethodName (tfMethodName.getText ().trim ());
//        breakpoint.setApplyToAnonymousInnerClasses (cbInnerClasses.isSelected ());
        breakpoint.setCondition (tfCondition.getText ());
        
        if (createBreakpoint) 
            DebuggerManager.getDebuggerManager ().addBreakpoint (breakpoint);
        return true;
    }
    
    /**
     * Called when "Cancel" button is pressed.
     *
     * @return whether customizer can be closed
     */
    public boolean cancel () {
        return true;
    }
    
    /**
     * Return <code>true</code> whether value of this customizer 
     * is valid (and OK button can be enabled).
     *
     * @return <code>true</code> whether value of this customizer 
     * is valid
     */
    public boolean isValid () {
        return true;
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cbAllMethods;
    private javax.swing.JCheckBox cbInnerClasses;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel pActions;
    private javax.swing.JPanel pSettings;
    private javax.swing.JTextField tfClassName;
    private javax.swing.JTextField tfCondition;
    private javax.swing.JTextField tfMethodName;
    private javax.swing.JTextField tfPackageName;
    // End of variables declaration//GEN-END:variables
    
}
