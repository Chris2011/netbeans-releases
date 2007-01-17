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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.refactoring.spi.impl;

import java.io.IOException;
import java.util.Enumeration;
import javax.swing.Icon;
import javax.swing.tree.*;
import org.netbeans.modules.refactoring.spi.ui.TreeElement;
//[retouche]import org.netbeans.jmi.javamodel.Element;
//[retouche]import org.netbeans.jmi.javamodel.JavaPackage;
import org.openide.text.PositionBounds;
//[retouche]import org.netbeans.modules.javacore.api.JavaModel;
import org.netbeans.modules.refactoring.api.RefactoringElement;
import org.openide.filesystems.FileObject;

/**
 * @author Pavel Flaska
 */
public class CheckNode extends DefaultMutableTreeNode {

    public final static int SINGLE_SELECTION = 0;
    public final static int DIG_IN_SELECTION = 4;
  
    private int selectionMode;
    private boolean isSelected;

    private String nodeLabel;
    private Icon icon;
    
    private boolean disabled = false;
    private boolean needsRefresh = false;
    
    private PositionBounds bounds = null;
    private String resourceName;
    
    public CheckNode(Object userObject, String nodeLabel, Icon icon) {
        super(userObject, !(userObject instanceof RefactoringElement));
        this.isSelected = true;
        setSelectionMode(DIG_IN_SELECTION);
        this.nodeLabel = nodeLabel;
        this.icon = icon;
        if (userObject instanceof RefactoringElement) {
            RefactoringElement ree = (RefactoringElement) userObject;
            FileObject fo = ree.getParentFile();
            if (fo == null) {
//[retouche]                Element el = ree.getJavaElement();
//[retouche]                fo = JavaModel.getFileObject(el.getResource());
                if (fo == null) {
                    resourceName = null;
                    return;
                }
            }
//            ClassPath cp = ClassPath.getClassPath(fo, ClassPath.SOURCE);
//            if (cp != null) {
//                resourceName = cp.getResourceName(fo);
//            } else {
//                resourceName = null;
//            }
        }
    }
    
    String getLabel() {
        return nodeLabel;
    }
    
    Icon getIcon() {
        return icon;
    }
    
    public void setDisabled() {
        disabled = true;
        isSelected = false;
        removeAllChildren();
    }
    
    boolean isDisabled() {
        return disabled;
    }

    void setNeedsRefresh() {
        needsRefresh = true;
        setDisabled();
    }
    
    boolean needsRefresh() {
        return needsRefresh;
    }
    
    public void setSelectionMode(int mode) {
        selectionMode = mode;
    }

    public int getSelectionMode() {
        return selectionMode;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
        if (userObject instanceof TreeElement) {
            Object ob = ((TreeElement) userObject).getUserObject();
            if (ob instanceof RefactoringElement) {
                    ((RefactoringElement) ob).setEnabled(isSelected);
            }
        }
        if ((selectionMode == DIG_IN_SELECTION) && (children != null)) {
            Enumeration e = children.elements();      
            while (e.hasMoreElements()) {
                CheckNode node = (CheckNode)e.nextElement();
                node.setSelected(isSelected);
            }
        }
    }

    public boolean isSelected() {
        if (userObject instanceof RefactoringElement) {
            return ((RefactoringElement) userObject).isEnabled() &&
                !((((RefactoringElement) userObject).getStatus() == RefactoringElement.GUARDED) || (((RefactoringElement) userObject).getStatus() == RefactoringElement.READ_ONLY));
        } else {
            return isSelected;
        }
    }
    
    public PositionBounds getPosition() {
        if (userObject instanceof RefactoringElement)
            return ((RefactoringElement) userObject).getPosition();
        return null;
    }
    
    private String tooltip;
    public String getToolTip() {
        if (tooltip==null) {
            if (userObject instanceof TreeElement) {
                Object re = ((TreeElement) userObject).getUserObject();
                if (re instanceof RefactoringElement) {
                    tooltip = ((RefactoringElement) re).getDisplayText();
                }
            }
//            if ((resourceName != null) && (userObject instanceof RefactoringElement)) {
//                RefactoringElement ree = (RefactoringElement) userObject;
//                PositionBounds bounds = getPosition();
//                if (bounds != null) {
//                    int line;
//                    try {
//                        line = bounds.getBegin().getLine() + 1;
//                    } catch (IOException ioe) {
//                        return null;
//                    }
//                    tooltip = resourceName + ':' + line;
//                }
//            }
            return null;
        }
        return tooltip;
    }
}
