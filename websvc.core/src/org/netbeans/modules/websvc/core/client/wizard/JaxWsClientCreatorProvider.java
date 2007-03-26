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

package org.netbeans.modules.websvc.core.client.wizard;
import org.netbeans.api.project.Project;
import org.netbeans.modules.websvc.core.ClientCreator;
import org.netbeans.modules.websvc.core.ClientCreatorProvider;
import org.netbeans.modules.websvc.core.ClientWizardProperties;
import org.netbeans.modules.websvc.core.dev.wizard.ProjectInfo;
import org.netbeans.modules.websvc.jaxws.api.JAXWSSupport;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Milan Kuchtiak
 */
public class JaxWsClientCreatorProvider implements ClientCreatorProvider {

    public JaxWsClientCreatorProvider() {
    }
    
    public ClientCreator getClientCreator(Project project, WizardDescriptor wiz) {
        String jaxVersion = (String) wiz.getProperty(ClientWizardProperties.JAX_VERSION);
        if (jaxVersion.equals(ClientWizardProperties.JAX_WS)) {
            return new JaxWsClientCreator(project, wiz);
        }
        ProjectInfo projectInfo = new ProjectInfo(project);
        int projectType = projectInfo.getProjectType();
        if (projectType == ProjectInfo.EJB_PROJECT_TYPE) {
                FileObject ddFolder = JAXWSSupport.getJAXWSSupport(project.getProjectDirectory()).getDeploymentDescriptorFolder();
                if (ddFolder==null || ddFolder.getFileObject("ejb-jar.xml")==null) { //NOI18N
                    return new JaxWsClientCreator(project, wiz);
                }
            }
        return null;
    }

}
