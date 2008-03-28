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

package org.netbeans.modules.web.jsf.wizards;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import javax.swing.ComboBoxModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.j2ee.core.api.support.SourceGroups;
import org.netbeans.modules.j2ee.core.api.support.java.JavaIdentifiers;
import org.netbeans.modules.j2ee.persistence.wizard.fromdb.SourceGroupUISupport;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.api.webmodule.WebProjectConstants;
import org.netbeans.modules.web.jsf.JSFConfigUtilities;
import org.netbeans.modules.web.jsf.dialogs.BrowseFolders;
import org.netbeans.spi.java.project.support.ui.PackageView;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author  Pavel Buzek
 */
public class PersistenceClientSetupPanelVisual extends javax.swing.JPanel implements DocumentListener {
    
    private WizardDescriptor wizard;
    private Project project;
    private JTextComponent packageComboBoxEditor;
    private ChangeSupport changeSupport = new ChangeSupport(this);
    
    /** Creates new form CrudSetupPanel */
    public PersistenceClientSetupPanelVisual(WizardDescriptor wizard) {
        this.wizard = wizard;
        initComponents();
        
        packageComboBoxEditor = ((JTextComponent)packageComboBox.getEditor().getEditorComponent());
        Document packageComboBoxDocument = packageComboBoxEditor.getDocument();
        packageComboBoxDocument.addDocumentListener(this);
        jsfFolder.addKeyListener(new KeyListener(){
            public void keyPressed(KeyEvent e) {
                changeSupport.fireChange();
            }            
            public void keyReleased(KeyEvent e) {
                changeSupport.fireChange();
            } 
            public void keyTyped(KeyEvent e) {
                changeSupport.fireChange();
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

        jLabel2 = new javax.swing.JLabel();
        jsfFolder = new javax.swing.JTextField();
        browseFolderButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        projectLabel = new javax.swing.JLabel();
        projectTextField = new javax.swing.JTextField();
        locationLabel = new javax.swing.JLabel();
        locationComboBox = new javax.swing.JComboBox();
        packageLabel = new javax.swing.JLabel();
        packageComboBox = new javax.swing.JComboBox();

        setName(org.openide.util.NbBundle.getMessage(PersistenceClientSetupPanelVisual.class, "LBL_JSFPagesAndClasses")); // NOI18N

        jLabel2.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/jsf/wizards/Bundle").getString("MNE_JSF_Pages").charAt(0));
        jLabel2.setLabelFor(jsfFolder);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/web/jsf/wizards/Bundle"); // NOI18N
        jLabel2.setText(bundle.getString("LBL_JSF_pages_folder")); // NOI18N

        browseFolderButton.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/jsf/wizards/Bundle").getString("MNE_Browse2").charAt(0));
        browseFolderButton.setText(bundle.getString("LBL_Browse")); // NOI18N
        browseFolderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseFolderButtonActionPerformed(evt);
            }
        });

        jLabel3.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/jsf/wizards/Bundle").getString("MNE_Controller").charAt(0));
        jLabel3.setText(bundle.getString("LBL_JSF_Managed_bean")); // NOI18N

        jLabel4.setText(bundle.getString("MSG_Jsf_Pages_Location")); // NOI18N

        jLabel5.setText(bundle.getString("MSG_Folders")); // NOI18N

        projectLabel.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/jsf/wizards/Bundle").getString("MNE_Project").charAt(0));
        projectLabel.setLabelFor(projectTextField);
        projectLabel.setText(org.openide.util.NbBundle.getMessage(PersistenceClientSetupPanelVisual.class, "LBL_Project")); // NOI18N

        projectTextField.setEditable(false);

        locationLabel.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/jsf/wizards/Bundle").getString("MNE_Location").charAt(0));
        locationLabel.setLabelFor(locationComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(locationLabel, org.openide.util.NbBundle.getMessage(PersistenceClientSetupPanelVisual.class, "LBL_SrcLocation")); // NOI18N

        locationComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                locationComboBoxActionPerformed(evt);
            }
        });

        packageLabel.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/jsf/wizards/Bundle").getString("MNE_Package").charAt(0));
        packageLabel.setLabelFor(packageComboBox);
        packageLabel.setText(org.openide.util.NbBundle.getMessage(PersistenceClientSetupPanelVisual.class, "LBL_Package")); // NOI18N

        packageComboBox.setEditable(true);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jLabel4)
                .addContainerGap(331, Short.MAX_VALUE))
            .add(layout.createSequentialGroup()
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jsfFolder, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(browseFolderButton))
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(projectLabel)
                    .add(locationLabel)
                    .add(packageLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, locationComboBox, 0, 498, Short.MAX_VALUE)
                    .add(projectTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE)
                    .add(packageComboBox, 0, 498, Short.MAX_VALUE)))
            .add(layout.createSequentialGroup()
                .add(jLabel3)
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .add(jLabel5)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jLabel4)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(jsfFolder, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(browseFolderButton))
                .add(27, 27, 27)
                .add(jLabel5)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(projectLabel)
                    .add(projectTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(locationLabel)
                    .add(locationComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(packageLabel)
                    .add(packageComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel3)
                .addContainerGap(70, Short.MAX_VALUE))
        );

        jsfFolder.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PersistenceClientSetupPanelVisual.class, "ACSD_JSF_Pages")); // NOI18N
        browseFolderButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PersistenceClientSetupPanelVisual.class, "ACSD_Browser")); // NOI18N
        projectTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PersistenceClientSetupPanelVisual.class, "ACSD_Project")); // NOI18N
        locationComboBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PersistenceClientSetupPanelVisual.class, "ACSD_Location")); // NOI18N
        packageComboBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PersistenceClientSetupPanelVisual.class, "ACSD_Package")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void locationComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_locationComboBoxActionPerformed
        locationChanged();
    }//GEN-LAST:event_locationComboBoxActionPerformed
        
    private void browseFolderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseFolderButtonActionPerformed
        Sources s = (Sources) Templates.getProject(wizard).getLookup().lookup(Sources.class);
        org.netbeans.api.project.SourceGroup[] groups = s.getSourceGroups(WebProjectConstants.TYPE_DOC_ROOT);
        org.openide.filesystems.FileObject fo = BrowseFolders.showDialog(groups);
        if (fo!=null) {
            String res = "/"+JSFConfigUtilities.getResourcePath(groups,fo,'/',true);
            jsfFolder.setText(res);
        }
    }//GEN-LAST:event_browseFolderButtonActionPerformed
        
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseFolderButton;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JTextField jsfFolder;
    private javax.swing.JComboBox locationComboBox;
    private javax.swing.JLabel locationLabel;
    private javax.swing.JComboBox packageComboBox;
    private javax.swing.JLabel packageLabel;
    private javax.swing.JLabel projectLabel;
    private javax.swing.JTextField projectTextField;
    // End of variables declaration//GEN-END:variables
    
    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }
    
    boolean valid(WizardDescriptor wizard) {
//        List<Entity> entities = (List<Entity>) wizard.getProperty(WizardProperties.ENTITY_CLASS);
//        String controllerPkg = getPackage();
//        
//        boolean filesAlreadyExist = false;
//        String troubleMaker = "";
//        for (Entity entity : entities) {
//            String entityClass = entity.getClass2();
//            String simpleClassName = JSFClientGenerator.simpleClassName(entityClass);
//            String firstLower = simpleClassName.substring(0, 1).toLowerCase() + simpleClassName.substring(1);
//            String folder = jsfFolder.getText().endsWith("/") ? jsfFolder.getText() : jsfFolder.getText() + "/";
//            folder = folder + firstLower;
//            String controller = controllerPkg + "." + simpleClassName + "Controller";
//            String fqn = getPackage().length() > 0 ? getPackage().replace('.', '/') + "/" + simpleClassName : simpleClassName;
//            if (getLocationValue().getRootFolder().getFileObject(fqn + "Controller.java") != null) {
//                filesAlreadyExist = true;
//                troubleMaker = controllerPkg + "." + simpleClassName + "Controller.java";
//                break;
//            }
//            if (getLocationValue().getRootFolder().getFileObject(fqn + "Converter.java") != null) {
//                filesAlreadyExist = true;
//                troubleMaker = controllerPkg + "." + simpleClassName + "Converter.java";
//                break;
//            }
//        }
//        if (filesAlreadyExist) {
//            wizard.putProperty("WizardPanel_errorMessage",                                  // NOI18N
//                NbBundle.getMessage(PersistenceClientSetupPanelVisual.class, "MSG_FilesAlreadyExist", troubleMaker));
//            return false;
//        }
//        wizard.putProperty("WizardPanel_errorMessage", null); // NOI18N
        
            ClassPath cp = ClassPath.getClassPath(getLocationValue().getRootFolder(), ClassPath.COMPILE);
            ClassLoader cl = cp.getClassLoader(true);
            try {
                Class.forName("javax.transaction.UserTransaction", false, cl);
            }
            catch (ClassNotFoundException cnfe) {
                wizard.putProperty("WizardPanel_errorMessage", NbBundle.getMessage(PersistenceClientSetupPanelVisual.class, "ERR_UserTransactionUnavailable"));
                return false;
            }
        
            Sources srcs = (Sources) project.getLookup().lookup(Sources.class);
            SourceGroup sgWeb[] = srcs.getSourceGroups(WebProjectConstants.TYPE_DOC_ROOT);
            FileObject pagesRootFolder = sgWeb[0].getRootFolder();
            File pagesRootFolderAsFile = FileUtil.toFile(pagesRootFolder);
            String jsfFolderText = jsfFolder.getText();
            try {
                String canonPath = new File(pagesRootFolderAsFile, jsfFolderText).getCanonicalPath();
            }
            catch (IOException ioe) {
                wizard.putProperty("WizardPanel_errorMessage", NbBundle.getMessage(PersistenceClientSetupPanelVisual.class, "ERR_JsfTargetChooser_InvalidJsfFolder"));
                return false;
            }
        
            String packageName = getPackage();
            if (packageName.trim().equals("")) { // NOI18N
                wizard.putProperty("WizardPanel_errorMessage", NbBundle.getMessage(PersistenceClientSetupPanelVisual.class, "ERR_JavaTargetChooser_CantUseDefaultPackage"));
                return false;
            }

            if (!JavaIdentifiers.isValidPackageName(packageName)) {
                wizard.putProperty("WizardPanel_errorMessage", NbBundle.getMessage(PersistenceClientSetupPanelVisual.class,"ERR_JavaTargetChooser_InvalidPackage")); //NOI18N
                return false;
            }

            if (!SourceGroups.isFolderWritable(getLocationValue(), packageName)) {
                wizard.putProperty("WizardPanel_errorMessage", NbBundle.getMessage(PersistenceClientSetupPanelVisual.class, "ERR_JavaTargetChooser_UnwritablePackage")); //NOI18N
                return false;
            }
            wizard.putProperty("WizardPanel_errorMessage", null); // NOI18N
            return true;
    }
    
    public SourceGroup getLocationValue() {
        return (SourceGroup)locationComboBox.getSelectedItem();
    }

    public String getPackage() {
        return packageComboBoxEditor.getText();
    }

    private void locationChanged() {
        updateSourceGroupPackages();
//        changeSupport.fireChange();
    }
    
    void read(WizardDescriptor settings) {
        jsfFolder.setText((String) settings.getProperty(WizardProperties.JSF_FOLDER));
        
        project = Templates.getProject(settings);
        FileObject targetFolder = Templates.getTargetFolder(settings);
        
        projectTextField.setText(ProjectUtils.getInformation(project).getDisplayName());

         SourceGroup[] sourceGroups = SourceGroups.getJavaSourceGroups(project);      
         SourceGroupUISupport.connect(locationComboBox, sourceGroups);

        packageComboBox.setRenderer(PackageView.listRenderer());

        updateSourceGroupPackages();

        // set default source group and package cf. targetFolder
        if (targetFolder != null) {
//            SourceGroup targetSourceGroup = SourceGroupSupport.getFolderSourceGroup(sourceGroups, targetFolder);
//            if (targetSourceGroup != null) {
//                locationComboBox.setSelectedItem(targetSourceGroup);
//                String targetPackage = SourceGroupSupport.getPackageForFolder(targetSourceGroup, targetFolder);
//                if (targetPackage != null) {
//                    packageComboBoxEditor.setText(targetPackage);
//                }
//            }
            if (FileUtil.isParentOf(WebModule.getWebModule(
                    targetFolder).getDocumentBase(), targetFolder)) {
                Sources s = (Sources) Templates.getProject(wizard).getLookup().lookup(Sources.class);
                SourceGroup[] groups = s.getSourceGroups(WebProjectConstants.TYPE_DOC_ROOT);
                jsfFolder.setText("/"+JSFConfigUtilities.getResourcePath(groups,targetFolder,'/',true));
            }
        }
    }
    
    void store(WizardDescriptor settings) {
        settings.putProperty(WizardProperties.JSF_FOLDER, jsfFolder.getText());
        String pkg = getPackage();
        settings.putProperty(WizardProperties.JSF_CLASSES_PACKAGE, pkg);
        try {
            FileObject fo = getLocationValue().getRootFolder();
            String pkgSlashes = pkg.replace('.', '/');
            FileObject targetFolder = fo.getFileObject(pkgSlashes);
            if (targetFolder == null) {
                targetFolder = FileUtil.createFolder(fo, pkgSlashes);
            }
            Templates.setTargetFolder(settings, targetFolder);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void updateSourceGroupPackages() {
        SourceGroup sourceGroup = (SourceGroup)locationComboBox.getSelectedItem();
        ComboBoxModel model = PackageView.createListView(sourceGroup);
        if (model.getSelectedItem()!= null && model.getSelectedItem().toString().startsWith("META-INF")
                && model.getSize() > 1) { // NOI18N
            model.setSelectedItem(model.getElementAt(1));
        }
        packageComboBox.setModel(model);
    }
    
    public void insertUpdate(DocumentEvent e) {
        changeSupport.fireChange();
    }

    public void removeUpdate(DocumentEvent e) {
        changeSupport.fireChange();
    }

    public void changedUpdate(DocumentEvent e) {
        changeSupport.fireChange();
    }
    
}
