/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.xml.tax.beans.editor;

import javax.swing.JPanel;

import org.openide.explorer.propertysheet.editors.EnhancedCustomPropertyEditor;

/**
 *
 * @author  Libor Kramolis
 * @version 
 */
public class NullStringCustomEditor extends JPanel implements EnhancedCustomPropertyEditor {
    
    /** Serial Version UID */
    private static final long serialVersionUID =-7120244860529998751L;    

    //
    // init
    //

    /** Creates new customizer NullStringCustomEditor */
    public NullStringCustomEditor (NullStringEditor editor) {
        initComponents ();

        textArea.setText (editor.getAsText());
        textArea.setEditable (editor.isEditable());
    }


    //
    // EnhancedCustomPropertyEditor
    //

    /**
     * @return Returns the property value that is result of the CustomPropertyEditor.
     * @exception InvalidStateException when the custom property editor does not represent valid property value
     *            (and thus it should not be set)
     */
    public Object getPropertyValue () throws IllegalStateException {
	String text = textArea.getText();

	if ( NullStringEditor.DEFAULT_NULL.equals (text) ) {
	    return null;
	} else if ( text.length() == 0 ) {
	    return null;
	} else {
	    return text;
	}
    }

    //
    // form
    //

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        textAreaScroll = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();
        
        setLayout(new java.awt.BorderLayout());
        
        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(6, 6, 6, 6)));
        setPreferredSize(new java.awt.Dimension(500, 50));
        textAreaScroll.setViewportView(textArea);
        
        add(textAreaScroll, java.awt.BorderLayout.CENTER);
        
    }//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane textAreaScroll;
    private javax.swing.JTextArea textArea;
    // End of variables declaration//GEN-END:variables

}
