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

package org.netbeans.modules.project.ui;

import java.text.MessageFormat;

import org.openide.WizardDescriptor;
//import org.netbeans.spi.project.ui.templates.support.InstantiatingIterator;
import org.openide.filesystems.FileObject;

import org.openide.loaders.TemplateWizard;
import org.openide.util.NbBundle;

public final class NewProjectWizard extends TemplateWizard {

    private FileObject templatesFO;

    public NewProjectWizard (FileObject fo) {
        this.templatesFO = fo;
        putProperty (TemplatesPanelGUI.TEMPLATES_FOLDER, templatesFO);
        setTitleFormat( new MessageFormat( "{0}") );
    }
        
    protected WizardDescriptor.Panel createTemplateChooser() {
        return new TemplatesPanel ();
    }          
    

    public String getTitle () {
        return NbBundle.getBundle (NewProjectWizard.class).getString ("LBL_NewProjectWizard_Title"); // NOI18N
    }
    
}
