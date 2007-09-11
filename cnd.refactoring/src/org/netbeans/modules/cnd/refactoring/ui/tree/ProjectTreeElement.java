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

package org.netbeans.modules.cnd.refactoring.ui.tree;

import java.lang.ref.WeakReference;
import javax.swing.Icon;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.refactoring.spi.ui.*;
import org.openide.filesystems.FileObject;

/**
 * presentation of element for Project for presenting in C/C++ refactorings
 * 
 * @author Jan Becicka
 * @author Vladimir Voskresensky
 */
public class ProjectTreeElement implements TreeElement {

    private final String name;
    private final Icon icon;
    private final WeakReference<Project> prj;
    private final FileObject prjDir;
    /** Creates a new instance of ProjectTreeElement */
    public ProjectTreeElement(Project prj) {
        ProjectInformation pi = ProjectUtils.getInformation(prj);
        this.name = pi.getDisplayName();
        this.icon = pi.getIcon();
        this.prj = new WeakReference<Project>(prj);
        this.prjDir = prj.getProjectDirectory();
    }

    public TreeElement getParent(boolean isLogical) {
        return null;
    }

    public Icon getIcon() {
        return icon;
    }

    public String getText(boolean isLogical) {
        return name;
    }

    public Object getUserObject() {
        Project p = prj.get();
        if (p==null) {
            p = FileOwnerQuery.getOwner(prjDir);
        }
        return p;
    }
    
}
