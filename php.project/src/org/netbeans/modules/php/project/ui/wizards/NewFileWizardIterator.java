/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.project.ui.wizards;

import java.awt.Component;
import java.io.IOException;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.modules.php.project.PhpProject;
import org.netbeans.modules.php.project.Utils;
import org.netbeans.modules.php.project.ui.customizer.PhpProjectProperties;
import org.netbeans.modules.project.ui.api.PageLayoutChooserFactory;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;

/**
 * Just as simple wrapper for the standard new file iterator as possible.
 * @author Tomas Mysik
 */
public final class NewFileWizardIterator implements WizardDescriptor.InstantiatingIterator<WizardDescriptor> {

    private static final long serialVersionUID = 2262026971167469147L;
    private WizardDescriptor wizard;
    private WizardDescriptor.Panel<WizardDescriptor>[] wizardPanels;
    private int index;

    public Set instantiate() throws IOException {
        FileObject dir = Templates.getTargetFolder(wizard);
        FileObject template = Templates.getTemplate(wizard);

        // If the finishing panel is PageLayoutChooserPanel, then use the selected Template
        if (PageLayoutChooserFactory.isPageLayoutChooserPanel(wizardPanels[index])) {
            template = PageLayoutChooserFactory.getSelectedTemplate(wizardPanels[index]);
            PageLayoutChooserFactory.copyResources(wizardPanels[index], dir);
        }
        
        DataFolder dataFolder = DataFolder.findFolder(dir);
        DataObject dataTemplate = DataObject.find(template);
        DataObject createdFile = dataTemplate.createFromTemplate(dataFolder, Templates.getTargetName(wizard));

        return Collections.<FileObject>singleton(createdFile.getPrimaryFile());
    }

    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
        if (Templates.getTargetFolder(wizard) == null) {
            Project project = Templates.getProject(wizard);
            assert project instanceof PhpProject;
            PhpProject phpProject = (PhpProject) project;
            FileObject srcDir = getFileObject(phpProject.getHelper(), PhpProjectProperties.SRC_DIR);
            if (srcDir != null) {
                Templates.setTargetFolder(wizard, srcDir);
            }
        }
        wizardPanels = getPanels();
        
        // Make sure list of steps is accurate.
        String[] beforeSteps = (String[]) wizard.getProperty("WizardPanel_contentData");
        int beforeStepLength = beforeSteps.length - 1;
        String[] steps = createSteps(beforeSteps);
        for (int i = 0; i < wizardPanels.length; i++) {
            Component c = wizardPanels[i].getComponent();
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent) c;
                jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i + beforeStepLength - 1)); // NOI18N
                jc.putClientProperty("WizardPanel_contentData", steps); // NOI18N
            }
        }
    }

    private String[] createSteps(String[] beforeSteps) {
        int beforeStepLength = beforeSteps.length - 1;
        String[] res = new String[beforeStepLength + wizardPanels.length];
        for (int i = 0; i < res.length; i++) {
            if (i < (beforeStepLength)) {
                res[i] = beforeSteps[i];
            } else {
                res[i] = wizardPanels[i - beforeStepLength].getComponent().getName();
            }
        }
        return res;
    }

    public void uninitialize(WizardDescriptor wizard) {
        wizardPanels = null;
    }

    public String name() {
        return ""; // NOI18N
    }

    /** Get the current panel.
     * @return the panel
     */
    public Panel<WizardDescriptor> current() {
        return wizardPanels[index];
    }

    /** Test whether there is a next panel.
     * @return <code>true</code> if so
     */
    public boolean hasNext() {
        return index < wizardPanels.length - 1;
    }

    /** Test whether there is a previous panel.
     * @return <code>true</code> if so
     */
    public boolean hasPrevious() {
        return index > 0;
    }

    /** Move to the next panel.
     * I.e. increment its index, need not actually change any GUI itself.
     * @exception NoSuchElementException if the panel does not exist
     */
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }

    /** Move to the previous panel.
     * I.e. decrement its index, need not actually change any GUI itself.
     * @exception NoSuchElementException if the panel does not exist
     */
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    public void addChangeListener(ChangeListener l) {
    }

    public void removeChangeListener(ChangeListener l) {
    }

    @SuppressWarnings("unchecked") // Generic Array Creation
    private WizardDescriptor.Panel<WizardDescriptor>[] getPanels() {
        Project p = Templates.getProject(wizard);
        final SourceGroup[] groups = Utils.getSourceGroups(p);
        WizardDescriptor.Panel<WizardDescriptor> simpleTargetChooserPanel = Templates.createSimpleTargetChooser(p, groups);
        FileObject template = Templates.getTemplate(wizard);
        if (hasPageLayouts(template)) {
            PageLayoutChooserFactory.getWizrdPanel(template);
            WizardDescriptor.Panel<WizardDescriptor> pageLayoutChooserPanel = PageLayoutChooserFactory.getWizrdPanel(template);
            return new WizardDescriptor.Panel[]{
                        simpleTargetChooserPanel,
                        pageLayoutChooserPanel
                    };
        } else {
            return new WizardDescriptor.Panel[]{
                        simpleTargetChooserPanel
                    };
        }
    }

    /**
     * Check if any Page Layouts available associated with this template
     * @param template
     * @return
     */
    private boolean hasPageLayouts(FileObject template) {
        String pageLayoutsFolderName = "PageLayouts/" + template.getName(); // NOI18N
        FileObject pageLayoutsFolder = Repository.getDefault().getDefaultFileSystem().findResource(pageLayoutsFolderName);
        if (pageLayoutsFolder != null) {
            return pageLayoutsFolder.getChildren().length > 0;
        } else {
            return false;
        }
    }

    private FileObject getFileObject(AntProjectHelper helper, String propname) {
        String prop = helper.getStandardPropertyEvaluator().getProperty(propname);
        if (prop != null) {
            return helper.resolveFileObject(prop);
        }
        return null;
    }
}
