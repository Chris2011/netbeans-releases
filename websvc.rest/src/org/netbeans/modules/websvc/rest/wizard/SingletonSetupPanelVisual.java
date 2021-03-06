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
package org.netbeans.modules.websvc.rest.wizard;

import java.awt.event.KeyAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.TypeElement;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import org.netbeans.api.java.source.ClassIndex.NameKind;
import org.netbeans.api.java.source.ClassIndex.SearchScope;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.ui.TypeElementFinder;
import org.openide.util.Utilities;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.modules.websvc.rest.codegen.Constants;
import org.netbeans.modules.websvc.rest.codegen.Constants.MimeType;
import org.netbeans.modules.websvc.rest.codegen.model.GenericResourceBean;
import org.netbeans.modules.websvc.rest.support.SourceGroupSupport;
import org.netbeans.spi.java.project.support.ui.PackageView;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author  Nam Nguyen
 */
public class SingletonSetupPanelVisual extends javax.swing.JPanel 
    implements AbstractPanel.Settings, SourcePanel 
{

    private Project project;
    private List<ChangeListener> listeners;
    private boolean resourceClassNameOveridden;

    /** Creates new form CrudSetupPanel */
    public SingletonSetupPanelVisual(String name) {
        setName(name);
        this.listeners = new ArrayList<ChangeListener>();
        initComponents();
        packageComboBox.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {

            public void keyReleased(java.awt.event.KeyEvent evt) {
                fireChange();
            }
        });
        medaTypeComboBox.setModel(new DefaultComboBoxModel(GenericResourceBean.getSupportedMimeTypes()));
        ((JTextComponent) packageComboBox.getEditor().getEditorComponent()).getDocument().addDocumentListener(
                new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                fireChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                fireChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                fireChange();
            }
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        classLabel = new javax.swing.JLabel();
        classTextField = new javax.swing.JTextField();
        uriLabel = new javax.swing.JLabel();
        uriTextField = new javax.swing.JTextField();
        projectLabel = new javax.swing.JLabel();
        projectTextField = new javax.swing.JTextField();
        locationLabel = new javax.swing.JLabel();
        locationComboBox = new javax.swing.JComboBox();
        packageLabel = new javax.swing.JLabel();
        packageComboBox = new javax.swing.JComboBox();
        medaTypeComboBox = new javax.swing.JComboBox();
        mediaTypeLabel = new javax.swing.JLabel();
        contentClassLabel = new javax.swing.JLabel();
        selectClassButton = new javax.swing.JButton();
        contentClassTextField = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();

        setName(""); // NOI18N
        setPreferredSize(new java.awt.Dimension(450, 193));

        classLabel.setLabelFor(classTextField);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/websvc/rest/wizard/Bundle"); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(classLabel, bundle.getString("MSG_ClassName")); // NOI18N

        classTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                classTextFieldKeyReleased(evt);
            }
        });

        uriLabel.setLabelFor(uriTextField);
        org.openide.awt.Mnemonics.setLocalizedText(uriLabel, org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "LBL_UriTemplate")); // NOI18N

        uriTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                uriTextFieldKeyReleased(evt);
            }
        });

        projectLabel.setLabelFor(projectTextField);
        org.openide.awt.Mnemonics.setLocalizedText(projectLabel, org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "LBL_Project")); // NOI18N

        projectTextField.setEditable(false);

        locationLabel.setLabelFor(locationComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(locationLabel, org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "LBL_SrcLocation")); // NOI18N

        locationComboBox.setMinimumSize(new java.awt.Dimension(4, 20));
        locationComboBox.setPreferredSize(new java.awt.Dimension(130, 25));
        locationComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                locationComboBoxActionPerformed(evt);
            }
        });

        packageLabel.setLabelFor(packageComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(packageLabel, org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "LBL_Package")); // NOI18N

        packageComboBox.setEditable(true);
        packageComboBox.setMinimumSize(new java.awt.Dimension(4, 20));
        packageComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                packageChanged(evt);
            }
        });
        packageComboBox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                packageComboBoxKeyReleased(evt);
            }
        });

        medaTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        medaTypeComboBox.setMinimumSize(new java.awt.Dimension(4, 20));

        mediaTypeLabel.setLabelFor(medaTypeComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(mediaTypeLabel, org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "LBL_MimeType")); // NOI18N

        contentClassLabel.setLabelFor(contentClassTextField);
        org.openide.awt.Mnemonics.setLocalizedText(contentClassLabel, org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "LBL_RepresentationClass")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(selectClassButton, org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "LBL_Select")); // NOI18N
        selectClassButton.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        selectClassButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectClassButtonActionPerformed(evt);
            }
        });
        selectClassButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mouseClickHandler(evt);
            }
        });

        contentClassTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                representationClassChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(projectLabel)
                    .addComponent(contentClassLabel)
                    .addComponent(classLabel)
                    .addComponent(uriLabel)
                    .addComponent(mediaTypeLabel)
                    .addComponent(packageLabel)
                    .addComponent(locationLabel))
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(classTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                    .addComponent(medaTypeComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, 303, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(contentClassTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(selectClassButton))
                    .addComponent(uriTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                    .addComponent(locationComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, 303, Short.MAX_VALUE)
                    .addComponent(packageComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, 303, Short.MAX_VALUE)
                    .addComponent(projectTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(projectLabel)
                    .addComponent(projectTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(locationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(locationLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(packageComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(packageLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(uriLabel)
                    .addComponent(uriTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(classLabel)
                    .addComponent(classTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mediaTypeLabel)
                    .addComponent(medaTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(contentClassTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(contentClassLabel)
                        .addComponent(selectClassButton))))
        );

        classLabel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "ClassName")); // NOI18N
        classLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "DESC_ClassName")); // NOI18N
        classTextField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "ClassName")); // NOI18N
        classTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "DESC_ClassName")); // NOI18N
        uriLabel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "UirTemplate")); // NOI18N
        uriLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "DESC_Uri")); // NOI18N
        uriTextField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "UriTemplate")); // NOI18N
        uriTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "DESC_Uri")); // NOI18N
        projectLabel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "Project")); // NOI18N
        projectLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "DESC_Project")); // NOI18N
        projectTextField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "Project")); // NOI18N
        projectTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "DESC_Project")); // NOI18N
        locationLabel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "Location")); // NOI18N
        locationLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "DESC_Location")); // NOI18N
        locationComboBox.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "Location")); // NOI18N
        locationComboBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "DESC_Location")); // NOI18N
        packageLabel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "ResourcePackage")); // NOI18N
        packageLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "DESC_ResourcePackage")); // NOI18N
        packageComboBox.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "ResourcePackage")); // NOI18N
        packageComboBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "DESC_ResourcePackage")); // NOI18N
        medaTypeComboBox.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "MimeType")); // NOI18N
        medaTypeComboBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "DESC_MimeType")); // NOI18N
        mediaTypeLabel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "MimeType")); // NOI18N
        mediaTypeLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "DESC_MimeType")); // NOI18N
        contentClassLabel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "RepresentationClass")); // NOI18N
        contentClassLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "DESC_RepresentationClass")); // NOI18N
        selectClassButton.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "SelectRepresentationClass")); // NOI18N
        selectClassButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "DESC_SelectRepresenationClass")); // NOI18N
        contentClassTextField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "RepresentationClass")); // NOI18N
        contentClassTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "DESC_RepresentationClass")); // NOI18N

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "LBL_Specify_Resource_Class")); // NOI18N
        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SingletonSetupPanelVisual.class, "LBL_Specify_Resource_Class")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

private void containerTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_containerTextFieldActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_containerTextFieldActionPerformed

private void representationClassChanged(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_representationClassChanged
    fireChange();
}//GEN-LAST:event_representationClassChanged

    private void selectClassButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectClassButtonActionPerformed
        fireChange();

}//GEN-LAST:event_selectClassButtonActionPerformed

    private void mouseClickHandler(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mouseClickHandler
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                final ElementHandle<TypeElement> handle = TypeElementFinder.find(Util.getClasspathInfo(project), new TypeElementFinder.Customizer() {

                    public Set<ElementHandle<TypeElement>> query(ClasspathInfo classpathInfo, String textForQuery, NameKind nameKind, Set<SearchScope> searchScopes) {
                        return classpathInfo.getClassIndex().getDeclaredTypes(textForQuery, nameKind, searchScopes);
                    }

                    public boolean accept(ElementHandle<TypeElement> typeHandle) {
                        return true;
                    }
                });

                if (handle != null) {
                    contentClassTextField.setText(handle.getQualifiedName());
                    fireChange();
                }
            }
        });
}//GEN-LAST:event_mouseClickHandler

    private void locationComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_locationComboBoxActionPerformed
        locationChanged();
    }//GEN-LAST:event_locationComboBoxActionPerformed

    private void classTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_classTextFieldKeyReleased
        resourceClassNameOveridden = true;
        fireChange();
    }//GEN-LAST:event_classTextFieldKeyReleased

    private void packageComboBoxKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_packageComboBoxKeyReleased
        fireChange();
    }//GEN-LAST:event_packageComboBoxKeyReleased

private void uriTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_uriTextFieldKeyReleased
// TODO add your handling code here:
    String uri = uriTextField.getText();
    if (!resourceClassNameOveridden) {
        classTextField.setText(findFreeClassName(uri));
    }
    fireChange();
}//GEN-LAST:event_uriTextFieldKeyReleased

    private void packageChanged(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_packageChanged
        fireChange();
    }//GEN-LAST:event_packageChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel classLabel;
    private javax.swing.JTextField classTextField;
    private javax.swing.JLabel contentClassLabel;
    private javax.swing.JTextField contentClassTextField;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JComboBox locationComboBox;
    private javax.swing.JLabel locationLabel;
    private javax.swing.JComboBox medaTypeComboBox;
    private javax.swing.JLabel mediaTypeLabel;
    private javax.swing.JComboBox packageComboBox;
    private javax.swing.JLabel packageLabel;
    private javax.swing.JLabel projectLabel;
    private javax.swing.JTextField projectTextField;
    private javax.swing.JButton selectClassButton;
    private javax.swing.JLabel uriLabel;
    private javax.swing.JTextField uriTextField;
    // End of variables declaration//GEN-END:variables

    public void addChangeListener(ChangeListener listener) {
        listeners.add(listener);
    }

    public void fireChange() {
        ChangeEvent event = new ChangeEvent(this);

        for (ChangeListener listener : listeners) {
            listener.stateChanged(event);
        }
    }

    public boolean valid(WizardDescriptor wizard) {
        AbstractPanel.clearErrorMessage(wizard);
        String resourceUri = uriTextField.getText().trim();
        String packageName = getPackage();
        String className = classTextField.getText().trim();
        SourceGroup[] groups = SourceGroupSupport.getJavaSourceGroups(project);

        if (groups == null || groups.length < 1) {
            AbstractPanel.setErrorMessage(wizard, "MSG_NoJavaSourceRoots");
            return false;
        } else if (className.length() == 0 || !Utilities.isJavaIdentifier(className)) {
            AbstractPanel.setErrorMessage(wizard, "MSG_InvalidResourceClassName");
            return false;
        } else if (resourceUri.length() == 0) {
            AbstractPanel.setErrorMessage(wizard, "MSG_EmptyUriTemplate");
            return false;
        } else if (!Util.isValidPackageName(packageName)) {
            AbstractPanel.setErrorMessage(wizard, "MSG_InvalidPackageName");
            return false;
        } else if (getResourceClassFile() != null) {
            AbstractPanel.setErrorMessage(wizard, "MSG_ExistingClass");
            return false;
        } else if (!Util.isValidUri(resourceUri)) {
            AbstractPanel.setErrorMessage(wizard, "MSG_IncorrectUriTemplate");
            return false;
        }
        return true;
    }

    public SourceGroup getLocationValue() {
        return (SourceGroup) locationComboBox.getSelectedItem();
    }

    public String getPackage() {
        return ((JTextComponent) packageComboBox.getEditor().getEditorComponent()).getText();
    }

    private void locationChanged() {
        updateSourceGroupPackages();
        fireChange();
    }

    private String getResourceClassName() {
        return classTextField.getText();
    }

    private FileObject getResourceClassFile() {
        FileObject folder = null;
        try {
            folder = SourceGroupSupport.getFolderForPackage(getLocationValue(), getPackage());
            if (folder != null) {
                return folder.getFileObject(getResourceClassName(), Constants.JAVA_EXT);
            }
        } catch (IOException ex) {
            //OK just return null
        }
        return null;
    }
    public static final String DEFAULT_URI = "generic";

    public void read(WizardDescriptor settings) {
        project = Templates.getProject(settings);
        FileObject targetFolder = Templates.getTargetFolder(settings);

        projectTextField.setText(ProjectUtils.getInformation(project).getDisplayName());

        SourceGroup[] sourceGroups = SourceGroupSupport.getJavaSourceGroups(project);
        SourceGroupUISupport.connect(locationComboBox, sourceGroups);

        packageComboBox.setRenderer(PackageView.listRenderer());

        updateSourceGroupPackages();

        // set default source group and package cf. targetFolder
        if (targetFolder != null) {
            SourceGroup targetSourceGroup = SourceGroupSupport.findSourceGroupForFile(sourceGroups, targetFolder);
//            if (targetSourceGroup == null) {
//                targetSourceGroup = getLocationValue();
//                targetFolder = targetSourceGroup.getRootFolder();
//            }
            if (targetSourceGroup != null) {
                locationComboBox.setSelectedItem(targetSourceGroup);
                String targetPackage = SourceGroupSupport.getPackageForFolder(targetSourceGroup, targetFolder);
                if (targetPackage != null) {
                    ((JTextComponent) packageComboBox.getEditor().getEditorComponent()).setText(targetPackage);
                }
            }
        } else {
            String targetPackage = (String) settings.getProperty(WizardProperties.TARGET_PACKAGE);
            if (targetPackage != null) {
                ((JTextComponent) packageComboBox.getEditor().getEditorComponent()).setText(targetPackage);
            }
        }

        String value = (String) settings.getProperty(WizardProperties.RESOURCE_URI);
        if (value == null || value.trim().length() == 0) {
            uriTextField.setText(DEFAULT_URI);
            classTextField.setText(findFreeClassName(DEFAULT_URI));
            //uriTextField.setText("/" + Util.pluralize(Util.lowerFirstChar(getResourceName())) + "/{name}"); //NOI18N
            contentClassTextField.setText(GenericResourceBean.getDefaultRepresetationClass((MimeType) medaTypeComboBox.getSelectedItem()));
        } else {
            uriTextField.setText(value);
            classTextField.setText((String) settings.getProperty(WizardProperties.RESOURCE_CLASS));
            medaTypeComboBox.setSelectedItem(((MimeType[]) settings.getProperty(WizardProperties.MIME_TYPES))[0]);
            String[] types = (String[]) settings.getProperty(WizardProperties.REPRESENTATION_TYPES);
            if (types != null && types.length > 0) {
                contentClassTextField.setText(types[0]);
            }
        }

    }

    public void store(WizardDescriptor settings) {
        settings.putProperty(WizardProperties.RESOURCE_PACKAGE, getPackage());
        settings.putProperty(WizardProperties.RESOURCE_CLASS, classTextField.getText());
        settings.putProperty(WizardProperties.RESOURCE_URI, uriTextField.getText());
        settings.putProperty(WizardProperties.MIME_TYPES, new MimeType[]{(MimeType) medaTypeComboBox.getSelectedItem()});
        settings.putProperty(WizardProperties.REPRESENTATION_TYPES, new String[]{contentClassTextField.getText()});
        settings.putProperty(WizardProperties.SOURCE_GROUP, getLocationValue());

        try {
            FileObject packageFO = SourceGroupSupport.getFolderForPackage(getLocationValue(), getPackage(), false);

            if (packageFO != null) {
                Templates.setTargetFolder(settings, packageFO);
            } else {
                Templates.setTargetFolder(settings, null);
                settings.putProperty(WizardProperties.TARGET_PACKAGE, getPackage());
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public SourceGroup getSourceGroup() {
        return (SourceGroup) locationComboBox.getSelectedItem();
    }

    @Override
    public String getPackageName() {
        return ((JTextComponent) packageComboBox.getEditor().getEditorComponent()).getText();
    }
    
    public double getRenderedHeight(){
        return selectClassButton.getLocation().getY()+
                selectClassButton.getSize().getHeight()+getGap();
    }
    
    private double getGap(){
        double gap = contentClassTextField.getLocation().getY();
        gap = gap - (medaTypeComboBox.getLocation().getY() +medaTypeComboBox.getHeight());
        return gap;
    }

    private void updateSourceGroupPackages() {
        SourceGroup sourceGroup = getSourceGroup();
        if (sourceGroup != null) {
            ComboBoxModel model = PackageView.createListView(sourceGroup);
            if (model.getSelectedItem() != null && model.getSelectedItem().toString().startsWith("META-INF") && model.getSize() > 1) { // NOI18N

                model.setSelectedItem(model.getElementAt(1));
            }
            packageComboBox.setModel(model);
        }
    }

    private String findFreeClassName(String uri) {
        try {
            FileObject folder = SourceGroupSupport.getFolderForPackage(getLocationValue(), getPackage());
            if (folder != null) {
                return FileUtil.findFreeFileName(folder, Util.deriveResourceClassName(uri), Constants.JAVA_EXT);
            }
        } catch (IOException ex) {
            //OK just return null
            Exceptions.printStackTrace(ex);
        }
        return null;
    }
}
