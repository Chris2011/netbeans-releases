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

package org.netbeans.modules.j2ee.persistence.wizard.fromdb;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.TypeElement;
import javax.swing.AbstractAction;
import javax.swing.ComboBoxModel;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.j2ee.core.api.support.SourceGroups;
import org.netbeans.modules.j2ee.core.api.support.java.JavaIdentifiers;
import org.netbeans.modules.j2ee.persistence.dd.common.Persistence;
import org.netbeans.modules.j2ee.persistence.provider.InvalidPersistenceXmlException;
import org.netbeans.modules.j2ee.persistence.provider.Provider;
import org.netbeans.modules.j2ee.persistence.provider.ProviderUtil;
import org.netbeans.modules.j2ee.persistence.util.SourceLevelChecker;
import org.netbeans.modules.j2ee.persistence.wizard.Util;
import org.netbeans.modules.j2ee.persistence.wizard.library.PersistenceLibrarySupport;
import org.netbeans.spi.java.project.support.ui.PackageView;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.awt.Mnemonics;
import org.openide.filesystems.FileObject;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author Andrei Badea
 */
public class EntityClassesPanel extends javax.swing.JPanel {

    private final static Logger LOGGER = Logger.getLogger(EntityClassesPanel.class.getName());

    private final ChangeSupport changeSupport = new ChangeSupport(this);

    private JTextComponent packageComboBoxEditor;

    private PersistenceGenerator persistenceGen;
    private Project project;
    private boolean cmp;
    private String tableSourceName; //either Datasource or a connection

    private SelectedTables selectedTables;
    private final boolean puRequired;
    private final JMenuItem allToUpdateItem;
    private final JMenuItem allToRecreateItem;


    private EntityClassesPanel(boolean puRequired, boolean JAXBRequired) {
        this.puRequired = puRequired;

        initComponents();

        if (JAXBRequired) {
            generateJAXBCheckBox.setEnabled(false);
        }

        allToUpdateItem = tableActionsPopup.add(new AllToUpdateAction());
        allToRecreateItem = tableActionsPopup.add(new AllToRecreateAction());

        classNamesTable.getParent().setBackground(classNamesTable.getBackground());
        classNamesTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE); // NOI18N

        packageComboBoxEditor = ((JTextComponent)packageComboBox.getEditor().getEditorComponent());
        Document packageComboBoxDocument = packageComboBoxEditor.getDocument();
        packageComboBoxDocument.addDocumentListener(new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                packageChanged();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                packageChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                packageChanged();
            }
        });
    }

    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    boolean isBeanValidationSupported() {
        if (project == null) {
            return false;
        }
        
        final String notNullAnnotation = "javax.validation.constraints.NotNull";    //NOI18N
        Sources sources=ProjectUtils.getSources(project);
        SourceGroup groups[]=sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        if(groups == null || groups.length<1){
            return false;
        }
        SourceGroup firstGroup=groups[0];
        FileObject fo=firstGroup.getRootFolder();
        ClassPath compile=ClassPath.getClassPath(fo, ClassPath.COMPILE);
        if (compile == null) {
            return false;
        }
        return compile.findResource(notNullAnnotation.replace('.', '/')+".class")!=null;//NOI18N
    }

    public void initialize(PersistenceGenerator persistenceGen, Project project, boolean cmp, FileObject targetFolder) {
        this.persistenceGen = persistenceGen;
        this.project = project;
        this.cmp = cmp;

        projectTextField.setText(ProjectUtils.getInformation(project).getDisplayName());

        SourceGroup[] sourceGroups = SourceGroups.getJavaSourceGroups(project);
        SourceGroupUISupport.connect(locationComboBox, sourceGroups);

        packageComboBox.setRenderer(PackageView.listRenderer());

        updatePackageComboBox();

        if (targetFolder != null) {
            // set default source group and package cf. targetFolder
            SourceGroup targetSourceGroup = SourceGroups.getFolderSourceGroup(sourceGroups, targetFolder);
            if (targetSourceGroup != null) {
                locationComboBox.setSelectedItem(targetSourceGroup);
                String targetPackage = SourceGroups.getPackageForFolder(targetSourceGroup, targetFolder);
                if (targetPackage != null) {
                    packageComboBoxEditor.setText(targetPackage);
                }
            }
        }

        if (!cmp) {
            // change text of named query/finder checkbox
            Mnemonics.setLocalizedText(generateFinderMethodsCheckBox,
                    NbBundle.getMessage(EntityClassesPanel.class, "TXT_GenerateNamedQueryAnnotations"));
            // hide local interface checkbox
            cmpFieldsInInterfaceCheckBox.setVisible(false);
        }

        if (cmp) {
            classNamesLabel.setVisible(false);
            classNamesScrollPane.setVisible(false);
            spacerPanel.setVisible(false);
            
            setName(org.openide.util.NbBundle.getMessage(EntityClassesPanel.class, "LBL_EntityBeansLocation"));

            Mnemonics.setLocalizedText(specifyNamesLabel, org.openide.util.NbBundle.getMessage(EntityClassesPanel.class, "LBL_SpecifyBeansLocation"));
        }

        updatePersistenceUnitButton(true);
        Sources sources=ProjectUtils.getSources(project);
        SourceGroup groups[]=sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        SourceGroup firstGroup=groups[0];
        FileObject fo=firstGroup.getRootFolder();
        ClasspathInfo classpathInfo = ClasspathInfo.create(fo);
        JavaSource javaSource = JavaSource.create(classpathInfo);
        try {
            javaSource.runUserActionTask(new Task<CompilationController>() {

                @Override
                public void run(CompilationController controller) throws IOException {
                    controller.toPhase(JavaSource.Phase.ELEMENTS_RESOLVED);
                    TypeElement jc = controller.getElements().getTypeElement("javax.xml.bind.annotation.XmlTransient"); //NOI18N
                    if(jc == null){
                        generateJAXBCheckBox.setSelected(false);
                        generateJAXBCheckBox.setEnabled(false);
                    }
                }
            }, true);
        } catch (IOException ex) {
            //no need to throw exception as it just will not disable option possibly unsupported, it's not severe
            LOGGER.log(Level.FINE, "Fail to check if jaxb is supported");//NOI18N
        }
    }

    public void update(TableClosure tableClosure, String tableSourceName) {
        try {
            if (selectedTables == null) {
                selectedTables = new SelectedTables(persistenceGen, tableClosure, getLocationValue(), getPackageName());
                selectedTables.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent event) {
                        changeSupport.fireChange();
                    }
                });
            } else {
                selectedTables.setTableClosureAndTargetFolder(tableClosure, getLocationValue(), getPackageName());
            }
//            selectedTables.ensureUniqueClassNames();
        } catch (IOException e) {
            Exceptions.printStackTrace(e);
        }

        TableUISupport.connectClassNames(classNamesTable, selectedTables);
        this.tableSourceName = tableSourceName;
        updateSetAllButtons();
     }

    public SelectedTables getSelectedTables() {
        return selectedTables;
    }

    public SourceGroup getLocationValue() {
        return (SourceGroup)locationComboBox.getSelectedItem();
    }

    public String getPackageName() {
        return packageComboBoxEditor.getText();
    }

    public boolean getCmpFieldsInInterface() {
        return cmpFieldsInInterfaceCheckBox.isSelected();
    }

    public boolean getGenerateFinderMethods() {
        return generateFinderMethodsCheckBox.isSelected();
    }

    public boolean getGenerateJAXB() {
        return generateJAXBCheckBox.isSelected();
    }

    public boolean getGenerateValidationConstraints() {
        return isBeanValidationSupported();
    }
    
    public boolean getCreatePersistenceUnit() {
        return createPUCheckbox.isVisible() && createPUCheckbox.isSelected();
    }

    private void locationChanged() {
        updatePackageComboBox();
        updateSelectedTables();
        changeSupport.fireChange();
    }

    private void packageChanged() {
        updateSelectedTables();
        changeSupport.fireChange();
    }

    private void updatePackageComboBox() {
        SourceGroup sourceGroup = (SourceGroup)locationComboBox.getSelectedItem();
        if (sourceGroup != null) {
            ComboBoxModel model = PackageView.createListView(sourceGroup);
            if (model.getSelectedItem()!= null && model.getSelectedItem().toString().startsWith("META-INF")
                    && model.getSize() > 1) { // NOI18N
                model.setSelectedItem(model.getElementAt(1));
            }
            packageComboBox.setModel(model);
        }
    }

    private void updatePersistenceUnitButton(boolean initial) {
        String warning = " "; // NOI18N
        try{

            boolean showWarning = !cmp
                    && !ProviderUtil.persistenceExists(project);

            boolean isContainerManaged = Util.isContainerManaged(project);
            boolean canCreate = isContainerManaged || (ConnectionManager.getDefault().getConnections().length>0);//TODO  unhandled case if there is pu creation panel after this one, isn't the case for 7.0

            if(initial){
                createPUCheckbox.setVisible(showWarning && canCreate);
                createPUCheckbox.setSelected(showWarning && canCreate);
                createPUCheckbox.setEnabled(!puRequired && canCreate);
            }


            if (showWarning && !createPUCheckbox.isSelected()) {
                warning = NbBundle.getMessage(EntityClassesPanel.class, "ERR_NoPersistenceUnit");
            }

        } catch (InvalidPersistenceXmlException ipx){
            createPUCheckbox.setVisible(false);
            warning = NbBundle.getMessage(EntityClassesPanel.class, "ERR_InvalidPersistenceUnit", ipx.getPath());
        }

        if(warning.trim().length() == 0){//may need to show warning about sourc level
            if(getCreatePersistenceUnit()){
                String sourceLevel = SourceLevelChecker.getSourceLevel(project);
                if(sourceLevel !=null ){
                    if(sourceLevel.matches("1\\.[0-5]([^0-9].*)?")){//1.0-1.5
                        Provider provider = Util.getPreferredProvider(project);
                        String ver = ProviderUtil.getVersion(provider);
                        if(provider!=null && (ver!=null && !Persistence.VERSION_1_0.equals(ver))){
                            if(Util.isJPAVersionSupported(project, ver)){
                                warning  = NbBundle.getMessage(RelatedCMPWizard.class, "ERR_WrongSourceLevel", sourceLevel);
                            } else {
                                warning  = NbBundle.getMessage(RelatedCMPWizard.class, "ERR_UnsupportedJpaVersion", ver);
                            }
                        }
                    }
                }
            }
        }

        if (warning.trim().length() > 0) {
            Icon icon = ImageUtilities.loadImageIcon("org/netbeans/modules/j2ee/persistence/ui/resources/warning.gif", false);
            createPUWarningLabel.setIcon(icon);
            createPUWarningLabel.setText(warning);
            createPUWarningLabel.setToolTipText(warning);
        } else {
            createPUWarningLabel.setIcon(null);
            createPUWarningLabel.setText(" ");
            createPUWarningLabel.setToolTipText(null);
            
        }
    }

    private void updateSetAllButtons(){
        boolean update = false;
        boolean recreate = false;
        if(selectedTables!=null)
        {
            for (Table table : selectedTables.getTables()) {
                if(!selectedTables.getUpdateType(table).equals(UpdateType.NEW)){
                    if(selectedTables.getUpdateType(table).equals(UpdateType.UPDATE))recreate=true;
                    else update=true;
                    if(update && recreate)break;
                }
            }
        }
        tableActionsButton.setEnabled(update || recreate);
        allToUpdateItem.setEnabled(update);
        allToRecreateItem.setEnabled(recreate);
    }

    private void updateSelectedTables() {
        if (selectedTables != null) {
            try {
                selectedTables.setTargetFolder(getLocationValue(), getPackageName());
            } catch (IOException e) {
                Exceptions.printStackTrace(e);
            }
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tableActionsPopup = new javax.swing.JPopupMenu();
        specifyNamesLabel = new javax.swing.JLabel();
        classNamesLabel = new javax.swing.JLabel();
        classNamesScrollPane = new javax.swing.JScrollPane();
        classNamesTable = new TableUISupport.ClassNamesTable();
        projectLabel = new javax.swing.JLabel();
        projectTextField = new javax.swing.JTextField();
        locationLabel = new javax.swing.JLabel();
        locationComboBox = new javax.swing.JComboBox();
        packageLabel = new javax.swing.JLabel();
        packageComboBox = new javax.swing.JComboBox();
        generateFinderMethodsCheckBox = new javax.swing.JCheckBox();
        cmpFieldsInInterfaceCheckBox = new javax.swing.JCheckBox();
        spacerPanel = new javax.swing.JPanel();
        tableActionsButton = new javax.swing.JButton();
        createPUWarningLabel = new ShyLabel();
        createPUCheckbox = new javax.swing.JCheckBox();
        generateJAXBCheckBox = new javax.swing.JCheckBox();

        tableActionsPopup.setInvoker(tableActionsButton);

        setName(org.openide.util.NbBundle.getMessage(EntityClassesPanel.class, "LBL_EntityClasses")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(specifyNamesLabel, org.openide.util.NbBundle.getMessage(EntityClassesPanel.class, "LBL_SpecifyEntityClassNames")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(classNamesLabel, org.openide.util.NbBundle.getMessage(EntityClassesPanel.class, "LBL_ClassNames")); // NOI18N

        classNamesScrollPane.setMinimumSize(new java.awt.Dimension(23, 80));
        classNamesScrollPane.setViewportView(classNamesTable);

        org.openide.awt.Mnemonics.setLocalizedText(projectLabel, org.openide.util.NbBundle.getMessage(EntityClassesPanel.class, "LBL_Project")); // NOI18N

        projectTextField.setEditable(false);

        org.openide.awt.Mnemonics.setLocalizedText(locationLabel, org.openide.util.NbBundle.getMessage(EntityClassesPanel.class, "LBL_SrcLocation")); // NOI18N

        locationComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                locationComboBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(packageLabel, org.openide.util.NbBundle.getMessage(EntityClassesPanel.class, "LBL_Package")); // NOI18N

        packageComboBox.setEditable(true);

        generateFinderMethodsCheckBox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(generateFinderMethodsCheckBox, org.openide.util.NbBundle.getMessage(EntityClassesPanel.class, "TXT_GenerateFinderMethods")); // NOI18N
        generateFinderMethodsCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        cmpFieldsInInterfaceCheckBox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(cmpFieldsInInterfaceCheckBox, org.openide.util.NbBundle.getMessage(EntityClassesPanel.class, "TXT_AddFieldsToInterface")); // NOI18N
        cmpFieldsInInterfaceCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        spacerPanel.setPreferredSize(new java.awt.Dimension(377, 24));

        org.openide.awt.Mnemonics.setLocalizedText(tableActionsButton, "...");
        tableActionsButton.setMaximumSize(new java.awt.Dimension(24, 24));
        tableActionsButton.setMinimumSize(new java.awt.Dimension(24, 24));
        tableActionsButton.setPreferredSize(new java.awt.Dimension(24, 24));
        tableActionsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tableActionsButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout spacerPanelLayout = new javax.swing.GroupLayout(spacerPanel);
        spacerPanel.setLayout(spacerPanelLayout);
        spacerPanelLayout.setHorizontalGroup(
            spacerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, spacerPanelLayout.createSequentialGroup()
                .addContainerGap(404, Short.MAX_VALUE)
                .addComponent(tableActionsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        spacerPanelLayout.setVerticalGroup(
            spacerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(spacerPanelLayout.createSequentialGroup()
                .addComponent(tableActionsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.openide.awt.Mnemonics.setLocalizedText(createPUWarningLabel, "  ");
        createPUWarningLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        createPUWarningLabel.setMaximumSize(new java.awt.Dimension(1000, 29));

        createPUCheckbox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(createPUCheckbox, org.openide.util.NbBundle.getMessage(EntityClassesPanel.class, "LBL_CreatePersistenceUnit")); // NOI18N
        createPUCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        createPUCheckbox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                createPUCheckboxItemStateChanged(evt);
            }
        });

        generateJAXBCheckBox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(generateJAXBCheckBox, org.openide.util.NbBundle.getMessage(EntityClassesPanel.class, "TXT_GenerateJAXBAnnotations")); // NOI18N
        generateJAXBCheckBox.setToolTipText(org.openide.util.NbBundle.getMessage(EntityClassesPanel.class, "TXT_ToolTipJAXB")); // NOI18N
        generateJAXBCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(specifyNamesLabel)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(classNamesLabel)
                    .addComponent(projectLabel)
                    .addComponent(locationLabel)
                    .addComponent(packageLabel))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spacerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
                    .addComponent(packageComboBox, 0, 428, Short.MAX_VALUE)
                    .addComponent(locationComboBox, 0, 428, Short.MAX_VALUE)
                    .addComponent(projectTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
                    .addComponent(classNamesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)))
            .addGroup(layout.createSequentialGroup()
                .addComponent(generateJAXBCheckBox)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addComponent(generateFinderMethodsCheckBox)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addComponent(createPUCheckbox)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addComponent(cmpFieldsInInterfaceCheckBox)
                .addContainerGap())
            .addComponent(createPUWarningLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(specifyNamesLabel)
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(classNamesLabel)
                    .addComponent(classNamesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spacerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(projectTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(projectLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(locationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(locationLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(packageComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(packageLabel))
                .addGap(21, 21, 21)
                .addComponent(generateFinderMethodsCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(generateJAXBCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmpFieldsInInterfaceCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(createPUCheckbox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(createPUWarningLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void locationComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_locationComboBoxActionPerformed
        locationChanged();
    }//GEN-LAST:event_locationComboBoxActionPerformed

    private void createPUCheckboxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_createPUCheckboxItemStateChanged
        if(createPUCheckbox.isVisible() && createPUCheckbox.isSelected()){
        } else {
        }
        updatePersistenceUnitButton(false);
    }//GEN-LAST:event_createPUCheckboxItemStateChanged

    private void tableActionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tableActionsButtonActionPerformed
        Component component = ((Component)evt.getSource());
        Point loc = component.getLocationOnScreen();
        loc.y += component.getHeight()/2;
        loc.x += component.getWidth()/2;
        tableActionsPopup.setLocation(loc);
        tableActionsPopup.setVisible(true);
    }//GEN-LAST:event_tableActionsButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel classNamesLabel;
    private javax.swing.JScrollPane classNamesScrollPane;
    private javax.swing.JTable classNamesTable;
    private javax.swing.JCheckBox cmpFieldsInInterfaceCheckBox;
    private javax.swing.JCheckBox createPUCheckbox;
    private javax.swing.JLabel createPUWarningLabel;
    private javax.swing.JCheckBox generateFinderMethodsCheckBox;
    private javax.swing.JCheckBox generateJAXBCheckBox;
    private javax.swing.JComboBox locationComboBox;
    private javax.swing.JLabel locationLabel;
    private javax.swing.JComboBox packageComboBox;
    private javax.swing.JLabel packageLabel;
    private javax.swing.JLabel projectLabel;
    private javax.swing.JTextField projectTextField;
    private javax.swing.JPanel spacerPanel;
    private javax.swing.JLabel specifyNamesLabel;
    private javax.swing.JButton tableActionsButton;
    private javax.swing.JPopupMenu tableActionsPopup;
    // End of variables declaration//GEN-END:variables

    public static final class WizardPanel implements WizardDescriptor.Panel, WizardDescriptor.FinishablePanel, ChangeListener {

        private final ChangeSupport changeSupport = new ChangeSupport(this);

        private EntityClassesPanel component;
        private boolean componentInitialized;

        private WizardDescriptor wizardDescriptor;
        private Project project;
        private boolean cmp;
        private boolean puRequired;
        private boolean JAXBRequired;
        private boolean isFinishable;

        private List<Provider> providers;

        public WizardPanel(){
            this(false);
        }
        
        public WizardPanel(boolean persistenceUnitRequired, boolean JAXBRequired,
                boolean isFinishable )
        {
            puRequired = persistenceUnitRequired;
            this.JAXBRequired = JAXBRequired;
            this.isFinishable = isFinishable;
        }

        public WizardPanel(boolean persistenceUnitRequired, boolean JAXBRequired){
            this( persistenceUnitRequired , JAXBRequired , true );
        }

        public WizardPanel(boolean persistenceUnitRequired){
            this( persistenceUnitRequired , false , true );
        }
        
        @Override
        public EntityClassesPanel getComponent() {
            if (component == null) {
                component = new EntityClassesPanel(puRequired, JAXBRequired);
                component.addChangeListener(this);
            }
            return component;
        }

        @Override
        public void removeChangeListener(ChangeListener listener) {
            changeSupport.removeChangeListener(listener);
        }

        @Override
        public void addChangeListener(ChangeListener listener) {
            changeSupport.addChangeListener(listener);
        }

        @Override
        public HelpCtx getHelp() {
            if (cmp) {
                return new HelpCtx("org.netbeans.modules.j2ee.ejbcore.ejb.wizard.cmp." + EntityClassesPanel.class.getSimpleName()); // NOI18N
            } else {
                return new HelpCtx(EntityClassesPanel.class);
            }
        }

        @Override
        public void readSettings(Object settings) {
            wizardDescriptor = (WizardDescriptor)settings;
            
            RelatedCMPHelper helper = RelatedCMPWizard.getHelper(wizardDescriptor);

            if (!componentInitialized) {
                componentInitialized = true;

                PersistenceGenerator persistenceGen = helper.getPersistenceGenerator();
                project = Templates.getProject(wizardDescriptor);
                cmp = RelatedCMPWizard.isCMP(wizardDescriptor);
                FileObject targetFolder = Templates.getTargetFolder(wizardDescriptor);

                getComponent().initialize(persistenceGen, project, cmp, targetFolder);
            }

            TableSource tableSource = helper.getTableSource();
            String tableSourceName = null;
            if (tableSource != null) {
                // the name of the table source is only relevant if the source
                // was a data source of connection, since it will be sent to the
                // persistence unit panel, which only deals with data sources
                // or connections
                TableSource.Type tableSourceType = tableSource.getType();
                if (tableSourceType == TableSource.Type.DATA_SOURCE || tableSourceType == TableSource.Type.CONNECTION) {
                    tableSourceName = tableSource.getName();
                }
            }

            getComponent().update(helper.getTableClosure(), tableSourceName);
        }

        @Override
        public boolean isValid() {
            SourceGroup sourceGroup = getComponent().getLocationValue();
            if (sourceGroup == null) {
                setErrorMessage(NbBundle.getMessage(EntityClassesPanel.class, "ERR_JavaTargetChooser_SelectSourceGroup"));
                return false;
            }

            String packageName = getComponent().getPackageName();
            if (packageName.trim().equals("")) { // NOI18N
                setErrorMessage(NbBundle.getMessage(EntityClassesPanel.class, "ERR_JavaTargetChooser_CantUseDefaultPackage"));
                return false;
            }

            if (!JavaIdentifiers.isValidPackageName(packageName)) {
                setErrorMessage(NbBundle.getMessage(EntityClassesPanel.class,"ERR_JavaTargetChooser_InvalidPackage")); //NOI18N
                return false;
            }

            if (!SourceGroups.isFolderWritable(sourceGroup, packageName)) {
                setErrorMessage(NbBundle.getMessage(EntityClassesPanel.class, "ERR_JavaTargetChooser_UnwritablePackage")); //NOI18N
                return false;
            }

            // issue 92192: need to check that we will have a persistence provider
            // available to add to the classpath while generating entity classes (unless
            // the classpath already contains one)
            ClassPath classPath = null;
            try {
                FileObject packageFO = SourceGroups.getFolderForPackage(sourceGroup, packageName, false);
                if (packageFO == null) {
                    packageFO = sourceGroup.getRootFolder();
                }
                classPath = ClassPath.getClassPath(packageFO, ClassPath.COMPILE);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, null, e);
            }
            if (classPath != null) {
                if (classPath.findResource("javax/persistence/EntityManager.class") == null) { // NOI18N
                    // initialize the provider list lazily
                    if (providers == null) {
                        providers = PersistenceLibrarySupport.getProvidersFromLibraries();
                    }
                    if (providers.size() == 0) {
                        setErrorMessage(NbBundle.getMessage(EntityClassesPanel.class, "ERR_NoJavaPersistenceAPI")); // NOI18N
                        return false;
                    }
                }
            } else {
                LOGGER.warning("Cannot get a classpath for package " + packageName + " in " + sourceGroup); // NOI18N
            }

            SelectedTables selectedTables = getComponent().getSelectedTables();
            // check for null needed since isValid() can be called when
            // EntityClassesPanel.update() has not been called yet, e.g. from within
            // EntityClassesPanel.initialize()
            if (selectedTables != null) {
                String problem = selectedTables.getFirstProblemDisplayName();
                if (problem != null) {
                    setErrorMessage(problem);
                    return false;
                }
            }

            setErrorMessage(" "); // NOI18N
            return true;
        }

        @Override
        public void storeSettings(Object settings) {
            RelatedCMPHelper helper = RelatedCMPWizard.getHelper(wizardDescriptor);

            helper.setSelectedTables(getComponent().getSelectedTables());
            helper.setLocation(getComponent().getLocationValue());
            helper.setPackageName(getComponent().getPackageName());
            helper.setCmpFieldsInInterface(getComponent().getCmpFieldsInInterface());
            helper.setGenerateFinderMethods(getComponent().getGenerateFinderMethods());
            helper.setGenerateJAXBAnnotations(getComponent().getGenerateJAXB());
            helper.setGenerateValidationConstraints(getComponent().getGenerateValidationConstraints());
            helper.setCreatePU(getComponent().getCreatePersistenceUnit());
        }

        @Override
        public void stateChanged(ChangeEvent event) {
            changeSupport.fireChange();
        }

        private void setErrorMessage(String errorMessage) {
            wizardDescriptor.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, errorMessage); // NOI18N
        }

        @Override
        public boolean isFinishPanel() {
            return isFinishable;
        }
    }

    /**
     * A crude attempt at a label which doesn't expand its parent.
     */
    private static final class ShyLabel extends JLabel {

        @Override
        public Dimension getPreferredSize() {
            Dimension size = super.getPreferredSize();
            size.width = 0;
            return size;
        }

        @Override
        public Dimension getMinimumSize() {
            Dimension size = super.getMinimumSize();
            size.width = 0;
            return size;
        }
    }

    private class AllToUpdateAction extends AbstractAction {

        public AllToUpdateAction() {
            super(NbBundle.getMessage(EntityClassesPanel.class, "LBL_UpdateAction"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            for(Table table: selectedTables.getTables()){
                if(UpdateType.RECREATE.equals(selectedTables.getUpdateType(table))){
                    selectedTables.setUpdateType(table, UpdateType.UPDATE);
                }
            }
            TableUISupport.connectClassNames(classNamesTable, selectedTables);
            updateSetAllButtons();
        }

    }
    private class AllToRecreateAction extends AbstractAction{
        public AllToRecreateAction() {
            super(NbBundle.getMessage(EntityClassesPanel.class, "LBL_RecreateAction"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            for(Table table: selectedTables.getTables()){
                if(UpdateType.UPDATE.equals(selectedTables.getUpdateType(table))){
                    selectedTables.setUpdateType(table, UpdateType.RECREATE);
                }
            }
            TableUISupport.connectClassNames(classNamesTable, selectedTables);
            updateSetAllButtons();
        }

    }
}
