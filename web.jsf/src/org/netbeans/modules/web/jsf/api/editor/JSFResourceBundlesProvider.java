/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */

package org.netbeans.modules.web.jsf.api.editor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.modules.j2ee.core.api.support.SourceGroups;
import org.netbeans.modules.j2ee.metadata.model.api.MetadataModel;
import org.netbeans.modules.j2ee.metadata.model.api.MetadataModelAction;
import org.netbeans.modules.j2ee.metadata.model.api.MetadataModelException;
import org.netbeans.modules.web.el.spi.ResourceBundle;
import org.netbeans.modules.web.jsf.api.facesmodel.Application;
import org.netbeans.modules.web.jsf.api.metamodel.JsfModel;
import org.netbeans.modules.web.jsf.api.metamodel.JsfModelFactory;
import org.openide.filesystems.FileObject;

/**
 *
 * @author marekfukala
 */
public class JSFResourceBundlesProvider {

    private static final Logger LOGGER = Logger.getLogger(JSFResourceBundlesProvider.class.getName());

    public static List<ResourceBundle> getResourceBundles(final Project project) {
       MetadataModel<JsfModel> model = JsfModelFactory.getModel(project);
        if (model == null) {
            return Collections.emptyList();
        }
        try {
            return model.runReadAction(new MetadataModelAction<JsfModel, List<ResourceBundle>>() {

                @Override
                public List<ResourceBundle> run(JsfModel metadata) throws Exception {
                    List<Application> applications = metadata.getElements(Application.class);
                    List<ResourceBundle> result = new ArrayList<ResourceBundle>();
                    for (Application application : applications) {
                        for (org.netbeans.modules.web.jsf.api.facesmodel.ResourceBundle bundle : application.getResourceBundles()) {
                            if (bundle.getBaseName() == null) {
                                continue;
                            }

                            List<FileObject> files = new ArrayList<FileObject>();
                            for (SourceGroup sourceGroup : SourceGroups.getJavaSourceGroups(project)) {
                                int lastDot = bundle.getBaseName().lastIndexOf("."); //NOI18N
                                String parentPkg = lastDot == -1
                                        ? bundle.getBaseName()
                                        : bundle.getBaseName().replace(".", "/").substring(0, lastDot); //NOI18N
                                String bundleName = bundle.getBaseName().substring(lastDot + 1);
                                FileObject fileObject = sourceGroup.getRootFolder().getFileObject(parentPkg);
                                if (fileObject != null && fileObject.isValid() && fileObject.isFolder()) {
                                    for (FileObject fo : fileObject.getChildren()) {
                                        if (fo.getName().startsWith(bundleName) && "properties".equals(fo.getExt())) { //NOI18N
                                            files.add(fo);
                                        }
                                    }
                                }
                            }
                            result.add(new ResourceBundle(bundle.getBaseName(), bundle.getVar(), files));
                        }
                    }
                    return result;
                }
            });
        } catch (MetadataModelException ex) {
            LOGGER.log(Level.INFO, "Failed to read resource bundles for " + project, ex);
        } catch (IOException ex) {
            LOGGER.log(Level.INFO, "Failed to read resource bundles for " + project, ex);
        } catch (IllegalStateException ise) {
            // thrown from xdm (underlying the jsf model) when the model is broken
            LOGGER.log(Level.INFO, "Failed to read resource bundles for " + project, ise);
        }
        return Collections.emptyList();
    }

}
