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

/*
 * BorderData.java
 *
 * Created on October 22, 2004, 2:32 PM
 */

package org.netbeans.modules.css.visual.model;

import java.util.StringTokenizer;
import javax.swing.DefaultComboBoxModel;

/**
 * Data Structure for the Border Data
 * @author  Winston Prakash
 * @version 1.0
 */
public class BorderData extends PropertyData{

    PropertyData styleValue = new PropertyData();
    PropertyData colorValue = new PropertyData();
    PropertyWithUnitData widthValue = new PropertyWithUnitData();

    ColorModel colorModel = new ColorModel();

    public static final int ALL = 0;
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int TOP = 3;
    public static final int BOTTOM = 4;

    private int borderSide = ALL;

    public void setBorder(String boderStr){
        setBorder(boderStr, 0);
    }

    public void setBorder(String boderStr, int side){
        borderSide = side;
        if(boderStr != null){
            // Bug fix: boder data parse fails if rgb has space
            // i.e convert rgb(255, 245, 125) to rgb(255,245,125)
            boderStr = boderStr.toLowerCase();
            if(boderStr.indexOf("rgb") >= 0){ //NOI18N
                String borderColor = boderStr.substring(boderStr.indexOf("rgb")); //NOI18N
                String borderColorTrimmed = borderColor.replaceAll(" ","");
                boderStr =  boderStr.substring(0,boderStr.indexOf("rgb")) + " " + borderColorTrimmed; //NOI18N
            }

            StringTokenizer st = new StringTokenizer(boderStr);

            if(st.hasMoreTokens()){
                setWidth(st.nextToken());
            }
            if(st.hasMoreTokens()){
                setStyle(st.nextToken());
            }
            if(st.hasMoreTokens()){
                setColor(st.nextToken());
            }
        }
    }

    public void setWidth(String widthStr){
        widthValue.setUnit(getUnit(widthStr));
        widthValue.setValue(widthStr.replaceAll(widthValue.getUnit(),"").trim());
    }

    private String getUnit(String positionStr){
        DefaultComboBoxModel unitList = new BorderModel().getWidthUnitList();
        for(int i=0; i< unitList.getSize(); i++){
            String unit = (String)unitList.getElementAt(i);
            if(positionStr.trim().endsWith(unit)){
                return unit;
            }
        }
        return "";
    }

    public void setStyle(String style){
        styleValue.setValue(style);
    }

    public void setColor(String color){
        if(color.toLowerCase().trim().startsWith("rgb")){ //NOI18N
           color = color.replaceAll(" ","");
        }
        colorValue.setValue(color);
    }

    public void setWidthValue(String width){
        widthValue.setValue(width);
    }

    public void setWidthUnit(String widthUnit){
        widthValue.setUnit(widthUnit);
    }

    public String getStyle(){
        return styleValue.getValue();
    }

    public String getColor(){
        return colorValue.getValue();
    }

    public String getWidthValue(){
        return widthValue.getValue();
    }

    public String getWidthUnit(){
        return widthValue.getUnit();
    }

    public String toString(){
        String borderString = "";
        if (!widthValue.toString().equals("")){
            borderString +=  " " + widthValue.toString();
        }
        if (!styleValue.toString().equals("")){
            borderString +=  " " + styleValue.toString();
        }
        if (!colorValue.toString().equals("")){
            borderString +=  " " + colorValue.toString();
        }
        return borderString.trim();
    }

}
