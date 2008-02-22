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

package org.netbeans.modules.spring.beans;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.spring.api.beans.ConfigFileGroup;
import org.netbeans.modules.spring.util.ConfigFiles;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.openide.filesystems.FileUtil;
import org.openide.util.ChangeSupport;
import org.openide.util.Mutex;
import org.openide.util.Mutex.Action;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Andrei Badea
 */
public class ProjectConfigFileManagerImpl implements ConfigFileManagerImplementation {

    private static final Logger LOGGER = Logger.getLogger(ProjectConfigFileManagerImpl.class.getName());

    private static final String SPRING_DATA = "spring-data"; // NOI18N
    private static final String CONFIG_FILES = "config-files"; // NOI18N
    private static final String CONFIG_FILE_GROUPS = "config-file-groups"; // NOI18N
    private static final String CONFIG_FILE_GROUP = "config-file-group"; // NOI18N
    private static final String NAME = "name"; // NOI18N
    private static final String CONFIG_FILE = "config-file"; // NOI18N
    private static final String SPRING_DATA_NS = "http://www.netbeans.org/ns/spring-data/1"; // NOI18N

    private final Project project;
    private final AuxiliaryConfiguration auxConfig;
    private final ChangeSupport changeSupport = new ChangeSupport(this);

    private List<File> files;
    private List<ConfigFileGroup> groups;

    public ProjectConfigFileManagerImpl(Project project) {
        this.project = project;
        auxConfig = project.getLookup().lookup(AuxiliaryConfiguration.class);
        if (auxConfig == null) {
            throw new IllegalStateException("Project " + project + " does not have an AuxiliaryConfiguration in its lookup");
        }
    }

    /**
     * Returns the mutex which protectes the access to this ConfigFileManager.
     *
     * @return the mutex; never null.
     */
    public Mutex mutex() {
        return ProjectManager.mutex();
    }

    /**
     * Returns the list of config files in this manager. The list is
     * modifiable and not live, therefore changes to the list do not
     * modify the contents of the manager.
     *
     * @return the list; never null.
     */
    public List<File> getConfigFiles() {
        return mutex().readAccess(new Action<List<File>>() {
            public List<File> run() {
                synchronized (ProjectConfigFileManagerImpl.this) {
                    if (files == null) {
                        readConfiguration();
                    }
                    assert files != null;
                }
                List<File> result = new ArrayList<File>(files.size());
                result.addAll(files);
                return result;
            }
        });
    }

    /**
     * Returns the list of config file groups in this manger. The list is
     * modifiable and not live, therefore changes to the list do not
     * modify the contents of the manager.
     *
     * @return the list; never null.
     */
    public List<ConfigFileGroup> getConfigFileGroups() {
        return mutex().readAccess(new Action<List<ConfigFileGroup>>() {
            public List<ConfigFileGroup> run() {
                synchronized (ProjectConfigFileManagerImpl.this) {
                    if (groups == null) {
                        readConfiguration();
                    }
                    assert groups != null;
                }
                List<ConfigFileGroup> result = new ArrayList<ConfigFileGroup>(groups.size());
                result.addAll(groups);
                return result;
            }
        });
    }

    /**
     * Modifies the list of config file groups. This method needs to be called
     * under {@code mutex()} write access.
     *
     * @throws IllegalStateException if the called does not hold {@code mutex()}
     *         write access.
     */
    public void putConfigFilesAndGroups(List<File> newFiles, List<ConfigFileGroup> newGroups) {
        assert mutex().isWriteAccess();
        writeConfiguration(newFiles, newGroups);
        files = new ArrayList<File>(newFiles.size());
        files.addAll(newFiles);
        groups = new ArrayList<ConfigFileGroup>(newGroups.size());
        groups.addAll(newGroups);
        changeSupport.fireChange();
    }

    public void save() throws IOException {
        assert mutex().isWriteAccess();
        ProjectManager.getDefault().saveProject(project);
    }

    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    private void readConfiguration() {
        assert mutex().isReadAccess();
        File projectDir = FileUtil.toFile(project.getProjectDirectory());
        if (projectDir == null) {
            LOGGER.warning("The directory of project "+ project + "is null");
            files = Collections.emptyList();
            groups = Collections.emptyList();
        }
        List<File> newFiles = new ArrayList<File>();
        List<ConfigFileGroup> newGroups = new ArrayList<ConfigFileGroup>();
        Element springConfigEl = auxConfig.getConfigurationFragment(SPRING_DATA, SPRING_DATA_NS, true);
        if (springConfigEl != null) {
            NodeList list = springConfigEl.getElementsByTagNameNS(SPRING_DATA_NS, CONFIG_FILES);
            if (list.getLength() > 0) {
                Element configFilesEl = (Element)list.item(0);
                list = configFilesEl.getElementsByTagNameNS(SPRING_DATA_NS, CONFIG_FILE);
                readFiles(list, projectDir, newFiles);
            }
            list = springConfigEl.getElementsByTagNameNS(SPRING_DATA_NS, CONFIG_FILE_GROUPS);
            if (list.getLength() > 0) {
                Element configFileGroupsEl = (Element)list.item(0);
                list = configFileGroupsEl.getElementsByTagNameNS(SPRING_DATA_NS, CONFIG_FILE_GROUP);
                readGroups(list, projectDir, newGroups);
            }
        }
        files = newFiles;
        groups = newGroups;
    }

    private void readFiles(NodeList configFileEls, File basedir, List<File> files) {
        for (int i = 0; i < configFileEls.getLength(); i++) {
            Element configFileEl = (Element)configFileEls.item(i);
            File file = ConfigFiles.resolveFile(basedir, configFileEl.getTextContent());
            if (file != null) {
                files.add(file);
            }
        }
    }

    private void readGroups(NodeList configFileGroupEls, File basedir, List<ConfigFileGroup> groups) {
        for (int i = 0; i < configFileGroupEls.getLength(); i++) {
            Element configFileGroupEl = (Element)configFileGroupEls.item(i);
            String name = configFileGroupEl.getAttribute(NAME);
            NodeList configFileEls = configFileGroupEl.getElementsByTagNameNS(SPRING_DATA_NS, CONFIG_FILE);
            List<File> configFiles = new ArrayList<File>(configFileEls.getLength());
            readFiles(configFileEls, basedir, configFiles);
            groups.add(ConfigFileGroup.create(name, configFiles));
        }
    }

    private void writeConfiguration(List<File> files, List<ConfigFileGroup> groups) {
        assert mutex().isWriteAccess();
        File projectDir = FileUtil.toFile(project.getProjectDirectory());
        if (projectDir == null) {
            LOGGER.warning("The directory of project "+ project + "is null");
            return;
        }
        Document doc = XMLUtil.createDocument(SPRING_DATA, SPRING_DATA_NS, null, null);
        Element springConfigEl = doc.getDocumentElement();
        Element configFilesEl = springConfigEl.getOwnerDocument().createElementNS(SPRING_DATA_NS, CONFIG_FILES);
        springConfigEl.appendChild(configFilesEl);
        writeFiles(files, projectDir, configFilesEl);
        Element configFileGroupsEl = springConfigEl.getOwnerDocument().createElementNS(SPRING_DATA_NS, CONFIG_FILE_GROUPS);
        springConfigEl.appendChild(configFileGroupsEl);
        writeGroups(groups, projectDir, configFileGroupsEl);
        auxConfig.putConfigurationFragment(springConfigEl, true);
    }

    private void writeFiles(List<File> files, File basedir, Element parentEl) {
        for (File file : files) {
            Element configFileEl = parentEl.getOwnerDocument().createElementNS(SPRING_DATA_NS, CONFIG_FILE);
            configFileEl.appendChild(configFileEl.getOwnerDocument().createTextNode(ConfigFiles.getRelativePath(basedir, file)));
            parentEl.appendChild(configFileEl);
        }
    }

    private void writeGroups(List<ConfigFileGroup> groups, File basedir, Element configFileGroupsEl) {
        for (ConfigFileGroup group : groups) {
            Element configFileGroupEl = configFileGroupsEl.getOwnerDocument().createElementNS(SPRING_DATA_NS, CONFIG_FILE_GROUP);
            String name = group.getName();
            if (name != null && name.length() > 0) {
                configFileGroupEl.setAttribute(NAME, name);
            }
            writeFiles(group.getFiles(), basedir, configFileGroupEl);
            configFileGroupsEl.appendChild(configFileGroupEl);
        }
    }
}
