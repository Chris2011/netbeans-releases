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

package org.netbeans.modules.debugger.ui.actions;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;

import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.modules.debugger.ui.Utils;
import org.netbeans.spi.debugger.ui.BreakpointType;
import org.netbeans.spi.debugger.ui.Controller;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;


/**
 * AddBreakpoint action.
 *
 * @author   Jan Jancura
 */
public class AddBreakpointAction extends AbstractAction {

    private static AddBreakpointDialogManager abdm;

    
    public AddBreakpointAction () {
        putValue (
            Action.NAME, 
            NbBundle.getMessage (
                AddBreakpointAction.class, 
                "CTL_AddBreakpoint"
            )
        );
    }

    public void actionPerformed (ActionEvent e) {
        DebuggerManager dm = DebuggerManager.getDebuggerManager ();
            
        if (dm.lookup (null, BreakpointType.class).size () == 0) 
            return; // no breakpoint events...

        // create Add Breakpoint Dialog for it
        if (abdm == null)
            abdm = new AddBreakpointDialogManager ();
        abdm.getDialog ().setVisible (true);
    }
    
    // innerclasses .........................................................................

    /**
    * Dialog manager for adding breakpoints.
    * This class is final only for performance reasons,
    * can be happily unfinaled if desired.
    */
    static final class AddBreakpointDialogManager extends Object
        implements ActionListener, PropertyChangeListener {

        /**
         * default <CODE>HelpCtx</CODE> to be used if the selected breakpoint
         * type's customizer does not provide any
         *
         * @see  Breakpoint.Event#getCustomizer()
         * @see  HelpCtx#findHelp
         */
        private final HelpCtx DEFAULT_HELP
            = new HelpCtx ("debug.add.breakpoint"); // NOI18N
        /** true if ok was pressed */
        private boolean okPressed;
        private Dialog dialog;
        private AddBreakpointPanel panel;
        private DialogDescriptor descriptor;
        private Controller controller;
        private JButton bOk;
        private JButton bCancel;

        /** Accessor for managed dialog instance */
        Dialog getDialog () {
            dialog = createDialog ();
            okPressed = false;
            setValid ();
            startListening ();
            panel.addPropertyChangeListener (this);
            return dialog;
        }

        /** Constructs managed dialog instance using TopManager.createDialog
        * and returnrs it */
        private Dialog createDialog () {
            ResourceBundle bundle = NbBundle.getBundle (AddBreakpointAction.class);

            panel = new AddBreakpointPanel ();
            // create dialog descriptor, create & return the dialog
            descriptor = new DialogDescriptor (
                panel,
                bundle.getString ("CTL_Breakpoint_Title"), // NOI18N
                true,
                this
            );
            updateHelpCtx();
            descriptor.setOptions (new JButton[] {
                bOk = new JButton (bundle.getString ("CTL_Ok")), // NOI18N
                bCancel = new JButton (bundle.getString ("CTL_Cancel")) // NOI18N
            });
            bOk.getAccessibleContext ().setAccessibleDescription (bundle.getString ("ACSD_CTL_Ok")); // NOI18N
            bCancel.getAccessibleContext ().setAccessibleDescription (bundle.getString ("ACSD_CTL_Cancel")); // NOI18N
            descriptor.setClosingOptions (new Object [0]);
            Dialog d = DialogDisplayer.getDefault ().createDialog (descriptor);
            d.pack ();
            return d;
        }

        /** Called when some dialog button was pressed */
        public void actionPerformed (ActionEvent evt) {
            okPressed = bOk.equals (evt.getSource ());
            Controller controller = panel.getController ();
            boolean close = false;
            if (okPressed)
                close = controller.ok ();
            else
                close = controller.cancel ();
                
            if (!close) return;
            panel.removePropertyChangeListener (this);
            stopListening ();
            dialog.setVisible (false);
            dialog.dispose ();
            dialog = null;
        }
        
        /**
         * Set the dialog's <CODE>HelpCtx</CODE> according to a selected
         * breakpoint type.
         */
        private void updateHelpCtx() {
            HelpCtx helpCtx = panel.getHelpCtx();
            if (helpCtx == null)
                helpCtx = DEFAULT_HELP;
            descriptor.setHelpCtx(helpCtx);
        }
        
        public void propertyChange (PropertyChangeEvent e) {
            if (e.getPropertyName () == AddBreakpointPanel.PROP_TYPE) {
                updateHelpCtx();
                stopListening ();
                setValid ();
                startListening ();
                
            } else
            if (e.getPropertyName () == Controller.PROP_VALID) {
                setValid ();
            }
        }
        
        void startListening () {
            controller = panel.getController ();
            if (controller == null) return;
            controller.addPropertyChangeListener (this);
        }
        
        void stopListening () {
            if (controller == null) return;
            controller.removePropertyChangeListener (this);
            controller = null;
        }
        
        void setValid () {
            Controller controller = panel.getController ();
            if (controller == null) {
                bOk.setEnabled (false);
                return;
            }
            bOk.setEnabled (controller.isValid ());
        }

        /** @return true if OK button was pressed in dialog,
        * false otherwise. */
        public boolean getOKPressed () {
            return okPressed;
        }
    }
}


