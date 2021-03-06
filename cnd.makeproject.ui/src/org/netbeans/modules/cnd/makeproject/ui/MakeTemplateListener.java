/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.cnd.makeproject.ui;

import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.cnd.utils.CndPathUtilities;
import org.netbeans.modules.cnd.makeproject.api.ProjectSupport;
import org.netbeans.modules.cnd.makeproject.api.configurations.ConfigurationDescriptorProvider;
import org.netbeans.modules.cnd.makeproject.api.configurations.Folder;
import org.netbeans.modules.cnd.makeproject.api.configurations.Item;
import org.netbeans.modules.cnd.makeproject.api.configurations.Item.ItemFactory;
import org.netbeans.modules.cnd.makeproject.api.configurations.MakeConfigurationDescriptor;
import org.netbeans.modules.cnd.utils.MIMENames;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataLoaderPool;
import org.openide.loaders.DataObject;
import org.openide.loaders.OperationEvent;
import org.openide.loaders.OperationListener;
import org.openide.util.Utilities;


/*
 * see issue #64393
 */
public class MakeTemplateListener implements OperationListener {

    private static final ErrorManager ERR = ErrorManager.getDefault().getInstance(MakeTemplateListener.class.getName());

    private static final String ADD_TO_LOGICAL_FOLDER_ATTRIBUTE = "addToLogicalFolder"; // NOI18N
    
    private static MakeTemplateListener INSTANCE;
    private Project contextProject;
    private MakeConfigurationDescriptor contextCD;

    static MakeTemplateListener getInstance() {
        return INSTANCE;
    }

    static synchronized MakeTemplateListener createInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MakeTemplateListener();
            DataLoaderPool.getDefault().addOperationListener(INSTANCE);
        }
        return INSTANCE;
    }

    private MakeTemplateListener() {
    }

    @Override
    public void operationPostCreate(OperationEvent operationEvent) {
    }

    @Override
    public void operationCopy(OperationEvent.Copy copy) {
    }

    @Override
    public void operationMove(OperationEvent.Move move) {
    }

    @Override
    public void operationDelete(OperationEvent operationEvent) {
    }

    @Override
    public void operationRename(OperationEvent.Rename rename) {
    }

    @Override
    public void operationCreateShadow(OperationEvent.Copy copy) {
    }

    private MakeConfigurationDescriptor getMakeConfigurationDescriptor(Project p) {
        ConfigurationDescriptorProvider pdp = p.getLookup().lookup(ConfigurationDescriptorProvider.class);

        if (pdp == null) {
            return null;
        }

        return pdp.getConfigurationDescriptor();
    }

    void setContext(Project contextProject, MakeConfigurationDescriptor contextCD){
        this.contextProject = contextProject;
        this.contextCD = contextCD;
    }

    void clearContext(){
        this.contextProject = null;
        this.contextCD = null;
    }
    
    @Override
    public void operationCreateFromTemplate(OperationEvent.Copy copy) {
        Folder folder = Utilities.actionsGlobalContext().lookup(Folder.class);
        Project project;
        if (folder != null) {
            project = folder.getProject();
        } else {
            project = Utilities.actionsGlobalContext().lookup(Project.class);
        }
        
        DataObject originalDataObject = copy.getOriginalDataObject();
        if(originalDataObject != null) {
            FileObject originalPrimaryFile = originalDataObject.getPrimaryFile();
            if(originalPrimaryFile != null) {
                if(originalPrimaryFile.getAttribute(ADD_TO_LOGICAL_FOLDER_ATTRIBUTE) != null) {
                    boolean addToLogicalFolder = (Boolean)originalPrimaryFile.getAttribute(ADD_TO_LOGICAL_FOLDER_ATTRIBUTE);
                    if(!addToLogicalFolder) {
                        return;
                    }
                }
            }
        }
        MakeConfigurationDescriptor makeConfigurationDescriptor;
        if (folder == null || project == null) {
            //maybe a file belonging into a project is selected. Try:
            DataObject od = Utilities.actionsGlobalContext().lookup(DataObject.class);
            if (od == null) {
                od = copy.getObject();
            }
            if (od == null) {
                //no file:
                return;
            }
            FileObject file = od.getPrimaryFile();
            if (file == null) {
                return;
            }
            if (contextProject != null && contextCD != null) {
                project = contextProject;
                makeConfigurationDescriptor = contextCD;
            } else {
                project = FileOwnerQuery.getOwner(file);
                if (project == null) {
                    //no project:
                    return;
                }
                //check if the project is a Makefile project:
                makeConfigurationDescriptor = getMakeConfigurationDescriptor(project);
                if (makeConfigurationDescriptor == null) {
                    //not make project:
                    return;
                }
            }
            folder = makeConfigurationDescriptor.getLogicalFolders();
        } else {
            makeConfigurationDescriptor = getMakeConfigurationDescriptor(project);
            if (makeConfigurationDescriptor == null) {
                //not make project:
                return;
            }
        }
        FileObject file = copy.getObject().getPrimaryFile();
        Project owner = FileOwnerQuery.getOwner(file);

        if (ERR.isLoggable(ErrorManager.INFORMATIONAL)) {
            ERR.log(ErrorManager.INFORMATIONAL, "processing file=" + file); // NOI18N
            ERR.log(ErrorManager.INFORMATIONAL, "FileUtil.toFile(file.getPrimaryFile())=" + FileUtil.toFile(file)); // NOI18N
            ERR.log(ErrorManager.INFORMATIONAL, "into folder = " + folder); // NOI18N
            ERR.log(ErrorManager.INFORMATIONAL, "in project = " + project.getProjectDirectory()); // NOI18N
        }

        if (owner != null && owner.getProjectDirectory() == project.getProjectDirectory()) { // See 193227
            if (file.isFolder()) {
                 // don't add directories.
                return;
            }
            if (!makeConfigurationDescriptor.okToChange()) {
                return;
            }
            String itemPath = ProjectSupport.toProperPath(makeConfigurationDescriptor.getBaseDirFileObject(), file, project);
            itemPath = CndPathUtilities.normalizeSlashes(itemPath);
            Item item = ItemFactory.getDefault().createInFileSystem(makeConfigurationDescriptor.getBaseDirFileSystem(), itemPath);
            if (!folder.isDiskFolder()) {
                folder = changeFolder(folder, file);
            }
            folder.addItemAction(item);
            makeConfigurationDescriptor.save();

            if (ERR.isLoggable(ErrorManager.INFORMATIONAL)) {
                ERR.log(ErrorManager.INFORMATIONAL, "folder: " + folder + ", added: " + file); // NOI18N
            }
        } else {
            if (ERR.isLoggable(ErrorManager.INFORMATIONAL)) {
                ERR.log(ErrorManager.INFORMATIONAL, "not adding: " + file + " because it is not owned by this project"); // NOI18N
            }
        } 
    }

    private Folder changeFolder(Folder folder, FileObject file) {
        if (folder.getKind() == Folder.Kind.ROOT) {
            String preferedFolder = preferedFolder(file);
            if (preferedFolder != null) {
                for(Folder f : folder.getFolders()) {
                    if (f.getKind() == Folder.Kind.SOURCE_LOGICAL_FOLDER) {
                        if (preferedFolder.equals(f.getName())) {
                            folder = f;
                            break;
                        }
                    }
                }
            }
        } else if (folder.getKind() == Folder.Kind.SOURCE_LOGICAL_FOLDER) {
            String preferedFolder = preferedFolder(file);
            if (preferedFolder != null) {
                Folder parent = folder.getParent();
                if (parent != null) {
                    for(Folder f : parent.getFolders()) {
                        if (f.getKind() == Folder.Kind.SOURCE_LOGICAL_FOLDER) {
                            if (preferedFolder.equals(f.getName())) {
                                folder = f;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return folder;
    }
    
    private String preferedFolder(FileObject fo) {
        String mimeType = fo.getMIMEType();
        if (MIMENames.isCppOrCOrFortran(mimeType)) {
            return MakeConfigurationDescriptor.SOURCE_FILES_FOLDER;
        } else if (MIMENames.isHeader(mimeType)) {
            return MakeConfigurationDescriptor.HEADER_FILES_FOLDER;
        } else if (MIMENames.QT_UI_MIME_TYPE.equals(mimeType) ||
                   MIMENames.QT_RESOURCE_MIME_TYPE.equals(mimeType) ||
                   MIMENames.QT_TRANSLATION_MIME_TYPE.equals(mimeType)) {
             return MakeConfigurationDescriptor.RESOURCE_FILES_FOLDER;
        }
        return null;
    }
}
