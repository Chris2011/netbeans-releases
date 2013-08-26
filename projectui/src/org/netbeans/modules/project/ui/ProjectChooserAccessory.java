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

package org.netbeans.modules.project.ui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.Collator;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.queries.CollocationQuery;
import org.netbeans.spi.project.SubprojectProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;

/**
 * Special component on side of project filechooser.
 */
public class ProjectChooserAccessory extends javax.swing.JPanel
    implements ActionListener, PropertyChangeListener {

    private RequestProcessor.Task updateSubprojectsTask;
    private RequestProcessor.Task displayNameTask;
    private RequestProcessor RP;
    private RequestProcessor RP2;
    
    ModelUpdater modelUpdater;  //#101227 -> non-private

    private Map<Project,Set<? extends Project>> subprojectsCache = new HashMap<Project,Set<? extends Project>>(); // #59098
    /** Creates new form ProjectChooserAccessory */
    public ProjectChooserAccessory(JFileChooser chooser, boolean isOpenSubprojects) {
        initComponents();

        modelUpdater = new ModelUpdater();
        //#98080
        RP = new RequestProcessor(ModelUpdater.class.getName(), 1);
        RP2 = new RequestProcessor(ModelUpdater.class.getName(), 1);
        updateSubprojectsTask = RP.create(modelUpdater);
        updateSubprojectsTask.setPriority( Thread.MIN_PRIORITY );

        // Listen on the subproject checkbox to change the option accordingly
        jCheckBoxSubprojects.setSelected( isOpenSubprojects );
        jCheckBoxSubprojects.addActionListener( this );

        // Listen on the chooser to update the Accessory
        chooser.addPropertyChangeListener( this );

        // Set default list model for the subprojects list
        jListSubprojects.setModel( new DefaultListModel() );

        // Disable the Accessory. JFileChooser does not select a file
        // by default
        setAccessoryEnablement( false, 0 );
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabelProjectName = new javax.swing.JLabel();
        jTextFieldProjectName = new javax.swing.JTextField();
        jCheckBoxSubprojects = new javax.swing.JCheckBox();
        jScrollPaneSubprojects = new javax.swing.JScrollPane();
        jListSubprojects = new javax.swing.JList();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 12, 0, 0));
        setLayout(new java.awt.GridBagLayout());

        jLabelProjectName.setLabelFor(jTextFieldProjectName);
        org.openide.awt.Mnemonics.setLocalizedText(jLabelProjectName, org.openide.util.NbBundle.getMessage(ProjectChooserAccessory.class, "LBL_PrjChooser_ProjectName_Label")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        add(jLabelProjectName, gridBagConstraints);
        jLabelProjectName.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ProjectChooserAccessory.class, "AN_ProjectName")); // NOI18N
        jLabelProjectName.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ProjectChooserAccessory.class, "AD_ProjectName")); // NOI18N

        jTextFieldProjectName.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        add(jTextFieldProjectName, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxSubprojects, org.openide.util.NbBundle.getMessage(ProjectChooserAccessory.class, "LBL_PrjChooser_Subprojects_CheckBox")); // NOI18N
        jCheckBoxSubprojects.setMargin(new java.awt.Insets(2, 0, 2, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        add(jCheckBoxSubprojects, gridBagConstraints);
        jCheckBoxSubprojects.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ProjectChooserAccessory.class, "ACSD_ProjectChooserAccessory_jCheckBoxSubprojects")); // NOI18N

        jListSubprojects.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListSubprojects.setEnabled(false);
        jScrollPaneSubprojects.setViewportView(jListSubprojects);
        jListSubprojects.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ProjectChooserAccessory.class, "ACSN_ProjectChooserAccessory_jListSubprojects")); // NOI18N
        jListSubprojects.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ProjectChooserAccessory.class, "ACSD_ProjectChooserAccessory_jListSubprojects")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPaneSubprojects, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCheckBoxSubprojects;
    private javax.swing.JLabel jLabelProjectName;
    private javax.swing.JList jListSubprojects;
    private javax.swing.JScrollPane jScrollPaneSubprojects;
    private javax.swing.JTextField jTextFieldProjectName;
    // End of variables declaration//GEN-END:variables

    // Implementation of action listener ---------------------------------------

    @Override
    public void actionPerformed( ActionEvent e ) {
        if ( e.getSource() == jCheckBoxSubprojects ) {
            OpenProjectListSettings.getInstance().setOpenSubprojects( jCheckBoxSubprojects.isSelected() );
        }
    }

    @Override
    public void propertyChange( PropertyChangeEvent e ) {
        if ( JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals( e.getPropertyName() ) ||
             JFileChooser.SELECTED_FILES_CHANGED_PROPERTY.equals( e.getPropertyName() ) ) {

            // We have to update the Accessory
            JFileChooser chooser = (JFileChooser)e.getSource();
            final ListModel spListModel = jListSubprojects.getModel();


            final File[] projectDirs;
            if ( chooser.isMultiSelectionEnabled() ) {
                projectDirs = chooser.getSelectedFiles();
            }
            else {
                projectDirs = new File[] { chooser.getSelectedFile() };
            }

            // #87119: do not block EQ loading projects
            jTextFieldProjectName.setText(NbBundle.getMessage(ProjectChooserAccessory.class, "MSG_PrjChooser_WaitMessage"));

            if (displayNameTask != null) {
                displayNameTask.cancel();
            }

            displayNameTask = RP2.post(new Runnable() {
                @Override
                public void run() {

            final List<Project> projects = new ArrayList<Project>( projectDirs.length );
            //#155766 load the display names off the AWT thead.
            final List<String> projectNames = new ArrayList<String>(projectDirs.length);
            for (File dir : projectDirs) {
                if (dir != null) {
                    if (Thread.interrupted()) {
                        return;
                    }
                    Project project = getProject(FileUtil.normalizeFile(dir));
                    if ( project != null ) {
                        projects.add( project );
                        projectNames.add(ProjectUtils.getInformation(project).getDisplayName());
                    }
                }
            }
            if (Thread.interrupted()) {
                return;
            }
            EventQueue.invokeLater(new Runnable() {
                        @Override
                public void run() {

            if ( !projects.isEmpty() ) {
                // Enable all components acessory
                setAccessoryEnablement( true, projects.size() );

                if ( projects.size() == 1 ) {
                    String projectName = projectNames.get(0);
                    jTextFieldProjectName.setText( projectName );
                    jTextFieldProjectName.setToolTipText( projectName );
                }
                else {
                    jTextFieldProjectName.setText(NbBundle.getMessage(ProjectChooserAccessory.class, "LBL_PrjChooser_Multiselection", projects.size()));

                    StringBuffer toolTipText = new StringBuffer( "<html>" ); // NOI18N
                    for(String str : projectNames) {
                        toolTipText.append( str );
                        toolTipText.append( "<br>" ); // NOI18N
                    }
                    toolTipText.setLength(toolTipText.length() - "<br>".length());
                    toolTipText.append( "</html>" ); // NOI18N
                    jTextFieldProjectName.setToolTipText( toolTipText.toString() );
                }

                if (spListModel instanceof DefaultListModel) {
                    ((DefaultListModel)spListModel).clear();
                } else {
                    jListSubprojects.setListData (new String[0]);
                }

                if (modelUpdater != null) { // #72495
                    modelUpdater.projects = projects;
                    updateSubprojectsTask.schedule( 100 );
                }
            }
            else {
                // Clear the accessory data if the dir is not project dir
                jTextFieldProjectName.setText( "" ); // NOI18N
                if (modelUpdater != null) { // #72495
                    modelUpdater.projects = null;
                }

                if (spListModel instanceof DefaultListModel) {
                    ((DefaultListModel)spListModel).clear();
                } else {
                    jListSubprojects.setListData (new String[0]);
                }

                // Disable all components in accessory
                setAccessoryEnablement( false, 0 );

                // But, in case it is a load error, show that:
                if (projectDirs.length == 1 && projectDirs[0] != null) {
                    File dir = FileUtil.normalizeFile(projectDirs[0]);
                    FileObject fo = FileUtil.toFileObject(dir);
                    ProjectManager.getDefault().clearNonProjectCache(); // #113976: otherwise isProject will be false
                    if (fo != null && fo.isFolder() && ProjectManager.getDefault().isProject(fo)) {
                        try {
                            Project prj = ProjectManager.getDefault().findProject(fo);
                            if (prj == null) {
                                jTextFieldProjectName.setText(NbBundle.getMessage(ProjectChooserAccessory.class, "LBL_PrjChooser_Unrecognized"));
                                // Only so it can be focussed and message scrolled accessibly:
                                jLabelProjectName.setEnabled(true);
                                jTextFieldProjectName.setEnabled(true);
                            }
                        } catch (IOException x) {
                            String msg = Exceptions.findLocalizedMessage(x);
                            if (msg == null) {
                                msg = x.getLocalizedMessage();
                            }
                            jTextFieldProjectName.setText(msg);
                            jTextFieldProjectName.setCaretPosition(0);
                            Color error = UIManager.getColor("nb.errorForeground"); // NOI18N
                            if (error != null) {
                                jTextFieldProjectName.setForeground(error);
                            }
                            // Only so it can be focussed and message scrolled accessibly:
                            jLabelProjectName.setEnabled(true);
                            jTextFieldProjectName.setEnabled(true);
                        }
                    }
                }
            }

                        }
                    });
                }
            }, 100, Thread.MIN_PRIORITY);
        }
        else if ( JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals( e.getPropertyName() ) ) {
            // Selection lost => disable accessory
            setAccessoryEnablement( false, 0 );
        }
    }


    // Private methods ---------------------------------------------------------

    private static Project getProject( File dir ) {
        return OpenProjectList.fileToProject( dir );
    }

    private static ProjectManager.Result getProjectResult(File dir) {
        FileObject fo = FileUtil.toFileObject(dir);
        if (fo != null && /* #60518 */ fo.isFolder()) {
            return ProjectManager.getDefault().isProject2(fo);
        } else {
            return null;
        }

    }

    private void setAccessoryEnablement( boolean enable, int numberOfProjects ) {
        jLabelProjectName.setEnabled( enable );
        jTextFieldProjectName.setEnabled( enable );
        jTextFieldProjectName.setForeground(/* i.e. L&F default */null);
        jCheckBoxSubprojects.setEnabled( enable );
        jScrollPaneSubprojects.setEnabled( enable );
    }


    /**
     * Get a slash-separated relative path from f1 to f2, if they are collocated
     * and this is possible.
     * May return null.
     */
    private static String relativizePath(File f1, File f2) {
        if (f1 == null || f2 == null) {
            return null;
        }
        if (!CollocationQuery.areCollocated(f1, f2)) {
            return null;
        }
        // Copied from PropertyUtils.relativizeFile, more or less:
        StringBuffer b = new StringBuffer();
        File base = f1;
        String filepath = f2.getAbsolutePath();
        while (!filepath.startsWith(slashify(base.getAbsolutePath()))) {
            base = base.getParentFile();
            if (base == null) {
                return null;
            }
            if (base.equals(f2)) {
                // #61687: file is a parent of basedir
                b.append(".."); // NOI18N
                return b.toString();
            }
            b.append("../"); // NOI18N
        }
        URI u = Utilities.toURI(base).relativize(Utilities.toURI(f2));
        assert !u.isAbsolute() : u + " from " + f1 + " and " + f2 + " with common root " + base;
        b.append(u.getPath());
        if (b.charAt(b.length() - 1) == '/') {
            // file is an existing directory and file.toURI ends in /
            // we do not want the trailing slash
            b.setLength(b.length() - 1);
        }
        return b.toString();
    }
    private static String slashify(String path) {
        if (path.endsWith(File.separator)) {
            return path;
        } else {
            return path + File.separatorChar;
        }
    }


    // Other methods -----------------------------------------------------------

    /** Factory method for project chooser
     */
    public static JFileChooser createProjectChooser( boolean defaultAccessory ) {

        ProjectManager.getDefault().clearNonProjectCache(); // #41882

        OpenProjectListSettings opls = OpenProjectListSettings.getInstance();
        JFileChooser chooser = new ProjectFileChooser();
        chooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );

        if ("GTK".equals(javax.swing.UIManager.getLookAndFeel().getID())) { // NOI18N
            // see BugTraq #5027268
            chooser.putClientProperty("GTKFileChooser.showDirectoryIcons", Boolean.TRUE); // NOI18N
            //chooser.putClientProperty("GTKFileChooser.showFileIcons", Boolean.TRUE); // NOI18N
        }

        chooser.setApproveButtonText( NbBundle.getMessage( ProjectChooserAccessory.class, "BTN_PrjChooser_ApproveButtonText" ) ); // NOI18N
        chooser.setApproveButtonMnemonic( NbBundle.getMessage( ProjectChooserAccessory.class, "MNM_PrjChooser_ApproveButtonText" ).charAt (0) ); // NOI18N
        chooser.setApproveButtonToolTipText (NbBundle.getMessage( ProjectChooserAccessory.class, "BTN_PrjChooser_ApproveButtonTooltipText")); // NOI18N
        // chooser.setMultiSelectionEnabled( true );
        chooser.setDialogTitle( NbBundle.getMessage( ProjectChooserAccessory.class, "LBL_PrjChooser_Title" ) ); // NOI18N
        //#61789 on old macosx (jdk 1.4.1) these two method need to be called in this order.
        chooser.setAcceptAllFileFilterUsed( false );
        chooser.setFileFilter( ProjectDirFilter.INSTANCE );

        // A11Y
        chooser.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ProjectChooserAccessory.class, "AN_ProjectChooserAccessory"));
        chooser.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ProjectChooserAccessory.class, "AD_ProjectChooserAccessory"));


        if ( defaultAccessory ) {
            chooser.setAccessory(new ProjectChooserAccessory(chooser, opls.isOpenSubprojects()));
        }

        File currDir = null;
        String dir = opls.getLastOpenProjectDir();
        if ( dir != null ) {
            File d = new File( dir );
            if ( d.exists() && d.isDirectory() ) {
                currDir = d;
            }
        }

        FileUtil.preventFileChooserSymlinkTraversal(chooser, currDir);
        new ProjectFileView(chooser);

        return chooser;

    }

    @Override
    public void removeNotify() { // #72006
        super.removeNotify();
        if (modelUpdater != null) { // #101286 - might be already null
            modelUpdater.cancel();
        }
        if (updateSubprojectsTask != null) {
            updateSubprojectsTask.cancel();
        }

        if (displayNameTask != null) {
            displayNameTask.cancel();
        }

        modelUpdater = null;
        subprojectsCache = null;
        updateSubprojectsTask = null;
        displayNameTask = null;
    }

    // Aditional innerclasses for the file chooser -----------------------------

    private static class ProjectFileChooser extends JFileChooser {

        @Override
        public void approveSelection() {
            File selectedFile = getSelectedFile();
            if (selectedFile != null) {
                File dir = FileUtil.normalizeFile(selectedFile);
                FileObject fo = FileUtil.toFileObject(dir);
                if (fo != null && fo.isFolder() && ProjectManager.getDefault().isProject(fo)) {
                    super.approveSelection();
                }
                else {
                    setCurrentDirectory( dir );
                }
            }
        }


    }

    private static class ProjectDirFilter extends FileFilter {

        private static final FileFilter INSTANCE = new ProjectDirFilter( );

        @Override
        public boolean accept( File f ) {

            if ( f.isDirectory() ) {
                //#114765
                if ("CVS".equalsIgnoreCase(f.getName()) && new File(f, "Entries").exists()) { //NOI18N
                    return false;
                }
                return true; // Directory selected
            }

            return false;
        }

        @Override
        public String getDescription() {
            return NbBundle.getMessage( ProjectDirFilter.class, "LBL_PrjChooser_ProjectDirectoryFilter_Name" ); // NOI18N
        }

    }

    private static class ProjectFileView extends FileView implements Runnable {

        private final JFileChooser chooser;
        private final Map<File,Icon> knownProjectIcons = new HashMap<File,Icon>();
        private final RequestProcessor.Task task = Hacks.RP.create(this);
        private File lookingForIcon;

        public ProjectFileView(JFileChooser chooser) {
            this.chooser = chooser;
            chooser.setFileView(this);
            task.setPriority(Thread.MIN_PRIORITY);
        }

        @Override
        public Icon getIcon(File f) {
            synchronized (this) { //#233480 to reduce number of calls to IO layer
                Icon icon = knownProjectIcons.get(f);
                if (icon != null) {
                    return icon;
                }
            }
            if (!f.exists()) {
                //#159646: Workaround for JDK issue #6357445
                // Can happen when a file was deleted on disk while project
                // dialog was still open. In that case, throws an exception
                // repeatedly from FSV.gSI during repaint.
                return null;
            }
            if ( 
                   !f.toString().matches("/[^/]+") && // Unix: /net, /proc, etc.
                    f.getParentFile() != null && // do not consider drive roots
                    f.isDirectory()) { // #173958: do not call ProjectManager.isProject now, could block
                synchronized (this) {
                    if (lookingForIcon == null) {
                        lookingForIcon = f;
                        task.schedule(20);
                        // Only calculate one at a time.
                        // When the view refreshes, the next unknown icon
                        // should trigger the task to be reloaded.
                    }
                }
            }
            return chooser.getFileSystemView().getSystemIcon(f);
        }

        public @Override void run() {
            File d = FileUtil.normalizeFile(lookingForIcon);
            ProjectManager.Result r = getProjectResult(d);
            Icon icon;
            if (r != null) {
                icon = r.getIcon();
                if (icon == null) {
                    Project p = getProject(d);
                    if (p != null) {
                        icon = ProjectUtils.getInformation(p).getIcon();
                    } else {
                        icon = chooser.getFileSystemView().getSystemIcon(lookingForIcon);
                    }
                }
            } else {
                icon = chooser.getFileSystemView().getSystemIcon(lookingForIcon);
            }
            synchronized (this) {
                knownProjectIcons.put(lookingForIcon, icon);
                lookingForIcon = null;
            }
            chooser.repaint();
        }

    }

    class ModelUpdater implements Runnable, Cancellable { //#101227 -> non-private
        // volatile Project project;
        volatile List<Project> projects;
        private DefaultListModel subprojectsToSet;
        private boolean cancel = false;

        @Override
        public void run() {

            if ( !SwingUtilities.isEventDispatchThread() ) {
                if (cancel) {
                    return;
                }
                List<Project> currentProjects = projects;
                if ( currentProjects == null ) {
                    return;
                }
                Map<Project,Set<? extends Project>> cache = subprojectsCache;
                if (cache == null) {
                    return;
                }

                jListSubprojects.setListData (new String [] {NbBundle.getMessage (ProjectChooserAccessory.class, "MSG_PrjChooser_WaitMessage")});

                List<Project> subprojects = new ArrayList<Project>(currentProjects.size() * 5);
                for (Project p : currentProjects) {
                    if (cancel) {
                        return;
                    }
                    addSubprojects(p, subprojects, cache); // Find the projects recursively
                }

                if (cancel) {
                    return;
                }
                List<String> subprojectNames = new ArrayList<String>(subprojects.size());
                if ( !subprojects.isEmpty() ) {
                    String pattern = NbBundle.getMessage( ProjectChooserAccessory.class, "LBL_PrjChooser_SubprojectName_Format" ); // NOI18N
                    File pDir = currentProjects.size() == 1 ?
                                FileUtil.toFile( currentProjects.get(0).getProjectDirectory() ) :
                                null;

                    // Replace projects in the list with formated names
                    for (Project p : subprojects) {
                        if (cancel) {
                            return;
                        }
                        FileObject spDir = p.getProjectDirectory();

                        // Try to compute relative path
                        String relPath = null;
                        if ( pDir != null ) { // If only one project is selected
                            relPath = relativizePath(pDir, FileUtil.toFile( spDir ));
                        }

                        if (relPath == null) {
                            // Cannot get a relative path; display it as absolute.
                            relPath = FileUtil.getFileDisplayName(spDir);
                        }
                        String displayName = MessageFormat.format(
                            pattern,
                            ProjectUtils.getInformation(p).getDisplayName(),
                            relPath);
                        subprojectNames.add(displayName);
                    }

                    // Sort the list
                    Collections.sort( subprojectNames, Collator.getInstance() );
                }
                if (currentProjects != projects || cancel) {
                    return;
                }
                DefaultListModel listModel = new DefaultListModel();
                // Put all the strings into the list model
                for (String displayName : subprojectNames) {
                    listModel.addElement(displayName);
                }
                subprojectsToSet = listModel;
                if (cancel) {
                    return;
                }
                SwingUtilities.invokeLater( this );
                return;
            }
            else {
                if ( projects == null ) {
                    ListModel spListModel = jListSubprojects.getModel();
                    if (spListModel instanceof DefaultListModel) {
                        ((DefaultListModel)spListModel).clear();
                    } else {
                        jListSubprojects.setListData (new String[0]);
                    }
                    jCheckBoxSubprojects.setEnabled( false );
                }
                else {
                    jListSubprojects.setModel(subprojectsToSet);
                    // If no soubprojects checkbox should be disabled
                    jCheckBoxSubprojects.setEnabled( !subprojectsToSet.isEmpty() );
                    projects = null;
                }
            }

        }
        
        /** Gets all subprojects recursively
         */
        void addSubprojects(Project p, List<Project> result, Map<Project,Set<? extends Project>> cache) {
            if (cancel) {
                return;
            }
            Set<? extends Project> subprojects = cache.get(p);
            if (subprojects == null) {
                SubprojectProvider spp = p.getLookup().lookup(SubprojectProvider.class);
                if (spp != null) {
                    if (cancel) {
                        return;
                    }
                    subprojects = spp.getSubprojects();
                } else {
                    subprojects = Collections.emptySet();
                }
                cache.put(p, subprojects);
            }
            for (Project sp : subprojects) {
                if (cancel) {
                    return;
                }
                if ( !result.contains( sp ) ) {
                    result.add( sp );

                    //#70029: only add sp's subprojects if sp is not already in result,
                    //to prevent StackOverflow caused by misconfigured projects:
                    addSubprojects(sp, result, cache);
                }
            }

        }
        

        @Override
        public boolean cancel() {
            cancel = true;
            // we don't really care that much to wait for cancelation here..
            return true;
        }


    }


}
