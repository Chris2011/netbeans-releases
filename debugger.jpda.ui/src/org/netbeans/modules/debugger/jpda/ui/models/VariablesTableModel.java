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

import java.util.WeakHashMap;
import org.netbeans.api.debugger.jpda.Field;
import org.netbeans.api.debugger.jpda.InvalidExpressionException;
import org.netbeans.api.debugger.jpda.JPDAWatch;
import org.netbeans.api.debugger.jpda.LocalVariable;
import org.netbeans.api.debugger.jpda.ObjectVariable;
import org.netbeans.api.debugger.jpda.Super;
import org.netbeans.api.debugger.jpda.This;
import org.netbeans.api.debugger.jpda.Variable;
import org.netbeans.spi.debugger.ui.Constants;
import org.netbeans.spi.viewmodel.TableModel;
import org.netbeans.spi.viewmodel.TreeModelListener;
import org.netbeans.spi.viewmodel.UnknownTypeException;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;


/**
 *
 * @author   Jan Jancura
 */
public class VariablesTableModel implements TableModel, Constants {

    
    public Object getValueAt (Object row, String columnID) throws 
    UnknownTypeException {
        
        if ( columnID.equals (LOCALS_TO_STRING_COLUMN_ID) ||
             columnID.equals (WATCH_TO_STRING_COLUMN_ID)
        ) {
            if (row instanceof Super)
                return "";
            else

            if (row instanceof ObjectVariable)
                try {
                    return bold (
                        row,
                        ((ObjectVariable) row).getToStringValue ()
                    );
                } catch (InvalidExpressionException ex) {
                    return getMessage (ex);
                }
            else
            if (row instanceof Variable)
                return bold (
                    row, 
                    ((Variable) row).getValue ()
                );
        } else
        if ( columnID.equals (LOCALS_TYPE_COLUMN_ID) ||
             columnID.equals (WATCH_TYPE_COLUMN_ID)
        ) {
            if (row instanceof Variable)
                return getShort (((Variable) row).getType ());
        } else
        if ( columnID.equals (LOCALS_VALUE_COLUMN_ID) ||
             columnID.equals (WATCH_VALUE_COLUMN_ID)
        ) {
            if (row instanceof JPDAWatch) {
                JPDAWatch w = (JPDAWatch) row;
                String e = w.getExceptionDescription ();
                if (e != null)
                    return ">" + e + "<";
                return bold (w, w.getValue ());
            } else 
            if (row instanceof Variable)
                return bold (row, ((Variable) row).getValue ());
        }
        throw new UnknownTypeException (row);
    }
    
    public boolean isReadOnly (Object row, String columnID) throws 
    UnknownTypeException {
        if (row instanceof Variable) {
            if ( columnID.equals (LOCALS_TO_STRING_COLUMN_ID) ||
                 columnID.equals (WATCH_TO_STRING_COLUMN_ID) ||
                 columnID.equals (LOCALS_TYPE_COLUMN_ID) ||
                 columnID.equals (WATCH_TYPE_COLUMN_ID)
            ) return true;
            if ( columnID.equals (LOCALS_VALUE_COLUMN_ID) ||
                 columnID.equals (WATCH_VALUE_COLUMN_ID) 
            ) {
                if (row instanceof This)
                    return true;
                else
                if ( row instanceof LocalVariable ||
                     row instanceof Field ||
                     row instanceof JPDAWatch
                )
                    return false;
            }
        }
        throw new UnknownTypeException (row);
    }
    
    public void setValueAt (Object row, String columnID, Object value) 
    throws UnknownTypeException {
        if (row instanceof LocalVariable) {
            if (columnID.equals (LOCALS_VALUE_COLUMN_ID)) {
                try {
                    ((LocalVariable) row).setValue ((String) value);
                } catch (InvalidExpressionException e) {
                    NotifyDescriptor.Message descriptor = new NotifyDescriptor.Message (
                        e.getLocalizedMessage (), 
                        NotifyDescriptor.WARNING_MESSAGE
                    );
                    DialogDisplayer.getDefault ().notify (descriptor);
                }
                return;
            }
        }
        if (row instanceof Field) {
            if ( columnID.equals (LOCALS_VALUE_COLUMN_ID) ||
                 columnID.equals (WATCH_VALUE_COLUMN_ID)
            ) {
                try {
                    ((Field) row).setValue ((String) value);
                } catch (InvalidExpressionException e) {
                    NotifyDescriptor.Message descriptor = new NotifyDescriptor.Message (
                        e.getLocalizedMessage (), 
                        NotifyDescriptor.WARNING_MESSAGE
                    );
                    DialogDisplayer.getDefault ().notify (descriptor);
                }
                return;
            }
        }
        if (row instanceof JPDAWatch) {
            if ( columnID.equals (LOCALS_VALUE_COLUMN_ID) ||
                 columnID.equals (WATCH_VALUE_COLUMN_ID)
            ) {
                try {
                    ((JPDAWatch) row).setValue ((String) value);
                } catch (InvalidExpressionException e) {
                    NotifyDescriptor.Message descriptor = new NotifyDescriptor.Message (
                        e.getLocalizedMessage (), 
                        NotifyDescriptor.WARNING_MESSAGE
                    );
                    DialogDisplayer.getDefault ().notify (descriptor);
                }
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
    
    private static String getShort (String c) {
        int i = c.lastIndexOf ('.');
        if (i < 0) return c;
        return c.substring (i + 1);
    }
    
    private static String getMessage (InvalidExpressionException e) {
        String m = e.getLocalizedMessage ();
        if (m == null)
            m = e.getMessage ();
        return ">" + m + "<";
    }
    
    private WeakHashMap variableToValue = new WeakHashMap ();
    
    private String bold (Object variable, String value) {
        return value;
//        if (variableToValue.containsKey (variable)) {
//            String oldValue = (String) variableToValue.get (variable);
//            System.out.println("bold " + value + " : contains " + oldValue);
//            Thread.dumpStack();
//            System.out.println("");
//            if (oldValue == value) return value;
//            if ( (oldValue != null) && 
//                 oldValue.equals (value)
//            )   return value;
//            variableToValue.put (variable, value);
//            return "<html><b>" + value + "</b></html>";
//        } else {
//            System.out.println("bold " + value + " : new ");
//            Thread.dumpStack();
//            System.out.println("");
//            variableToValue.put (variable, value);
//            return "<html><b>" + value + "</b></html>";
//        }
    }
}
