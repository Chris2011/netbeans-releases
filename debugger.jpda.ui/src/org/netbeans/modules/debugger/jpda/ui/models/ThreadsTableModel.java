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

package org.netbeans.modules.debugger.jpda.ui.models;

import org.netbeans.api.debugger.jpda.JPDAThread;
import org.netbeans.spi.debugger.ui.Constants;
import org.netbeans.spi.viewmodel.TableModel;
import org.netbeans.spi.viewmodel.TreeModelListener;
import org.netbeans.spi.viewmodel.UnknownTypeException;
import org.openide.util.NbBundle;



/**
 *
 * @author   Jan Jancura
 */
public class ThreadsTableModel implements TableModel, Constants {

    public Object getValueAt (Object row, String columnID) throws 
    UnknownTypeException {
        if (row instanceof MonitorModel.ThreadWithBordel) 
            row = ((MonitorModel.ThreadWithBordel) row).originalThread;
        if (row instanceof JPDAThread) {
            if (columnID.equals (THREAD_STATE_COLUMN_ID)) 
                switch (((JPDAThread) row).getState ()) {
                    case JPDAThread.STATE_MONITOR:
                        return NbBundle.getMessage (
                            ThreadsTableModel.class, 
                            "CTL_Thread_State_OnMonitor"
                        );
                    case JPDAThread.STATE_NOT_STARTED:
                        return NbBundle.getMessage (
                            ThreadsTableModel.class, 
                            "CTL_Thread_State_NotStarted"
                        );
                    case JPDAThread.STATE_RUNNING:
                        return NbBundle.getMessage (
                            ThreadsTableModel.class, 
                            "CTL_Thread_State_Running"
                        );
                    case JPDAThread.STATE_SLEEPING:
                        return NbBundle.getMessage (
                            ThreadsTableModel.class, 
                            "CTL_Thread_State_Sleeping"
                        );
                    case JPDAThread.STATE_UNKNOWN:
                        return NbBundle.getMessage (
                            ThreadsTableModel.class, 
                            "CTL_Thread_State_Unknown"
                        );
                    case JPDAThread.STATE_WAIT:
                        return NbBundle.getMessage (
                            ThreadsTableModel.class, 
                            "CTL_Thread_State_Waiting"
                        );
                    case JPDAThread.STATE_ZOMBIE:
                        return NbBundle.getMessage (
                            ThreadsTableModel.class, 
                            "CTL_Thread_State_Zombie"
                        );
                }
            else
            if (columnID.equals (THREAD_SUSPENDED_COLUMN_ID))
                return Boolean.valueOf (((JPDAThread) row).isSuspended ());
        }
        throw new UnknownTypeException (row);
    }
    
    public boolean isReadOnly (Object row, String columnID) throws 
    UnknownTypeException {
        if (row instanceof MonitorModel.ThreadWithBordel) 
            row = ((MonitorModel.ThreadWithBordel) row).originalThread;
        if (row instanceof JPDAThread) {
            if (columnID.equals (THREAD_STATE_COLUMN_ID))
                return true;
            else
            if (columnID.equals (THREAD_SUSPENDED_COLUMN_ID))
                return false;
        }
        throw new UnknownTypeException (row);
    }
    
    public void setValueAt (Object row, String columnID, Object value) 
    throws UnknownTypeException {
        if (row instanceof MonitorModel.ThreadWithBordel) 
            row = ((MonitorModel.ThreadWithBordel) row).originalThread;
        if (row instanceof JPDAThread) {
            if (columnID.equals (THREAD_SUSPENDED_COLUMN_ID)) {
                if (value.equals (Boolean.TRUE))
                    ((JPDAThread) row).suspend ();
                else 
                    ((JPDAThread) row).resume ();
                return;
            }
        }
        throw new UnknownTypeException (row);
    }
    
    /** 
     * Registers given listener.
     * 
     * @param l the listener to add
     */
    public void addTreeModelListener (TreeModelListener l) {
    }

    /** 
     * Unregisters given listener.
     *
     * @param l the listener to remove
     */
    public void removeTreeModelListener (TreeModelListener l) {
    }
}
