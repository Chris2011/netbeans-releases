/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

/*
 * SystemColorSchema.java
 *
 * Created on July 21, 2006, 11:39 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.microedition.lcdui.laf;

import java.util.Hashtable;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * System color schema - gets all colors from Display.getColor() method call.
 * @author breh
 */
public class SystemColorSchema extends ColorSchema {
    
    private static Hashtable /*<display,SystemColorSchema>*/ systemColorSchemas = new Hashtable(1); // usually one display is in charge
    
    private Display display;
    
    /**
     * Creates a new instance of SystemColorSchema
     */
    private SystemColorSchema(Display display) {        
        this.display = display;
    }

    /**
     * Gets SystemColorSchema for given display 
     */
    public static  SystemColorSchema getForDisplay(Display display) {
        if (display == null) throw new IllegalArgumentException("Display parameter cannot be null");
        SystemColorSchema schema = (SystemColorSchema)systemColorSchemas.get(display);
        if (schema == null) {
            schema = new SystemColorSchema(display);
            systemColorSchemas.put(display,schema);            
        }
        return schema;
    }
    
    
    public int getColor(int aColorSpecifier) {
        return display.getColor(aColorSpecifier);
    }

    public Image getBackgroundImage() {
        return null;
    }

    public int getBackgroundImageAnchorPoint() {
        return Graphics.TOP | Graphics.LEFT;
    }

    public boolean isBackgroundImageTiled() {
        return false;
    }

    public boolean isBackgroundTransparent() {
        return false;
    }
    
    
    
}
