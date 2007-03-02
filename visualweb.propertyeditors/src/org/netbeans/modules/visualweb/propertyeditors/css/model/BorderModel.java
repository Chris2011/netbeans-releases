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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.visualweb.propertyeditors.css.model;

import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author  Winston Prakash
 */
public class BorderModel {
    
    public DefaultComboBoxModel getStyleList(){
        return new StyleList();
    }
    
    public DefaultComboBoxModel getWidthList(){
        return new WidthList();
    }
    
    public DefaultComboBoxModel getWidthUnitList(){
        return new WidthUnitList();
    }
    
    public class StyleList extends DefaultComboBoxModel{
        public StyleList(){
            addElement(CssStyleData.NOT_SET);
            addElement("solid"); //NOI18N
            addElement("double"); //NOI18N
            addElement("dotted"); //NOI18N
            addElement("dashed"); //NOI18N
            addElement("groove"); //NOI18N
            addElement("ridge"); //NOI18N
            addElement("inset"); //NOI18N
            addElement("outset"); //NOI18N
        }
    }
    
    public class WidthList extends DefaultComboBoxModel{
        public WidthList(){
            addElement(CssStyleData.NOT_SET);
            addElement("1"); //NOI18N
            addElement("2"); //NOI18N
            addElement("3"); //NOI18N
            addElement("4"); //NOI18N
            addElement("5"); //NOI18N
            addElement("6"); //NOI18N
            addElement("8"); //NOI18N
            addElement("10"); //NOI18N
            addElement(CssStyleData.VALUE);
        }
    }
    
    public class WidthUnitList extends DefaultComboBoxModel{
        public WidthUnitList(){
            addElement("px"); //NOI18N
            addElement("%"); //NOI18N
            addElement("in"); //NOI18N
            addElement("cm"); //NOI18N
            addElement("mm"); //NOI18N
            addElement("em"); //NOI18N
            addElement("ex"); //NOI18N
            addElement("picas"); //NOI18N
        }
    }
}
