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
package org.netbeans.modules.subversion.remote.ui.copy;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.JTextComponent;
import org.netbeans.modules.subversion.remote.RepositoryFile;
import org.netbeans.modules.subversion.remote.SvnModuleConfig;
import org.netbeans.modules.subversion.remote.api.SVNUrlUtils;
import org.netbeans.modules.remotefs.versioning.api.VCSFileProxySupport;
import org.netbeans.modules.versioning.core.api.VCSFileProxy;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.filesystems.FileSystem;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author Tomas Stupka
 */
public abstract class CopyDialog {

    private final DialogDescriptor dialogDescriptor;
    private final JButton okButton, cancelButton;
    private final JPanel panel;
    private static final Pattern TEMPLATE_PATTERN_BRANCH = Pattern.compile("^(.*/)*?((?:branches|tags)/(?:.+?))(/.*)+$"); //NOI18N
    private static final Pattern TEMPLATE_PATTERN_TRUNK = Pattern.compile("^(.*/)*?(trunk)(/.*)+$"); //NOI18N
    private static final String BRANCHES_FOLDER = "branches"; //NOI18N
    private static final String TRUNK_FOLDER = "trunk"; //NOI18N
    private static final String BRANCH_TEMPLATE = "[BRANCH_NAME]"; //NOI18N
    private static final String SEP = "----------"; //NOI18N
    private static final String MORE_BRANCHES = NbBundle.getMessage(CopyDialog.class, "LBL_CopyDialog.moreBranchesAndTags"); //NOI18N
    private Set<JComboBox> urlComboBoxes;
    private final FileSystem fileSystem;
    
    protected CopyDialog(VCSFileProxy file, JPanel panel, String title, String okLabel) {                
        this.panel = panel;
        fileSystem = VCSFileProxySupport.getFileSystem(file);
        
        okButton = new JButton(okLabel);
        okButton.getAccessibleContext().setAccessibleDescription(okLabel);
        cancelButton = new JButton(org.openide.util.NbBundle.getMessage(CopyDialog.class, "CTL_Copy_Cancel"));                                      // NOI18N
        cancelButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CopyDialog.class, "CTL_Copy_Cancel"));    // NOI18N
       
        dialogDescriptor = new DialogDescriptor(panel, title, true, new Object[] {okButton, cancelButton},
              okButton, DialogDescriptor.DEFAULT_ALIGN, new HelpCtx(this.getClass()), null);
        okButton.setEnabled(false);

        Dialog dialog = DialogDisplayer.getDefault().createDialog(dialogDescriptor);
        dialog.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CopyDialog.class, "CTL_Title"));                // NOI18N
    }

    protected void setupUrlComboBox (RepositoryFile repositoryFile, JComboBox cbo) {
        if(cbo==null) {
            return;
        }
        List<String> recentFolders = new LinkedList<>(org.netbeans.modules.subversion.remote.versioning.util.Utils.getStringList(SvnModuleConfig.getDefault(fileSystem).getPreferences(), CopyDialog.class.getName()));
        Map<String, String> comboItems = setupModel(cbo, repositoryFile, recentFolders);
        cbo.setRenderer(new LocationRenderer(comboItems));
        getUrlComboBoxes().add(cbo);
    }    
    
    private Set<JComboBox> getUrlComboBoxes () {
        if(urlComboBoxes == null) {
            urlComboBoxes = new HashSet<>();
        }
        return urlComboBoxes;
    }
    
    protected JPanel getPanel() {
        return panel;
    }       
    
    public final boolean showDialog() {                        
        Dialog dialog = DialogDisplayer.getDefault().createDialog(dialogDescriptor);        
        dialog.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CopyDialog.class, "CTL_Title"));                     // NOI18N        
        
        dialog.setVisible(true);
        boolean ret = dialogDescriptor.getValue()==okButton;
        if(ret) {
            storeValidValues();
        }
        return ret;        
    }        
    
    private void storeValidValues() {
        for (JComboBox cbo : urlComboBoxes) {
            Object item = cbo.getEditor().getItem();
            if(item != null && !item.equals("")) { // NOI18N
                 org.netbeans.modules.versioning.util.Utils.insert(SvnModuleConfig.getDefault(fileSystem).getPreferences(), CopyDialog.class.getName(), (String) item, -1);
            }            
        }                
    }       
    
    protected JButton getOKButton() {
        return okButton;
    }

    /**
     * Model has three categories:
     *   trunk/path (1)
     *   -----
     *   branches and tags (2)
     *   More Branches and Tags
     *   -----
     *   relevant recent urls (3) - those ending with the same file name
     * @param cbo
     * @param repositoryFile
     * @param recentFolders
     * @return 
     */
    private Map<String, String> setupModel (JComboBox cbo, RepositoryFile repositoryFile, List<String> recentFolders) {
        Map<String, String> locations = new HashMap<>(Math.min(recentFolders.size(), 10));
        List<String> model = new LinkedList<>();
        // all category in the model is sorted by name ignoring case
        Comparator comparator = new Comparator<String>() {
            @Override
            public int compare (String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        };
        // category 3
        TreeMap<String, String> relatedLocations = new TreeMap<>(comparator);
        // category 2
        TreeMap<String, String> branchLocations = new TreeMap<>(comparator);
        String relativePath = SVNUrlUtils.getRelativePath(repositoryFile.getRepositoryUrl(), repositoryFile.getFileUrl());
        String fileName = repositoryFile.getName();
        String[] branchPathSegments = getBranchPathSegments(relativePath);
        String pathInBranch = branchPathSegments[2];
        String preselectedPath = null;
        for (String recentUrl : recentFolders) {
            if (pathInBranch != null && branchLocations.size() < 10) {
                // repository seems to have the recommended branch structure
                String[] pathSegments = getBranchPathSegments(recentUrl);
                if (branchPathSegments[0].equals(pathSegments[0]) && !TRUNK_FOLDER.equals(pathSegments[1])) {
                    // this is a branch or a tag, so get the branch or tag name and build the url relevant for the given file
                    String branchPrefix = pathSegments[0] + pathSegments[1];
                    String loc = branchPrefix + '/' + pathInBranch;
                    // we also emphasize branch/tag name in the renderer, so build the rendered string
                    branchLocations.put(loc, getHtmlVersion(branchPrefix, pathInBranch));
                    if (preselectedPath == null) {
                        preselectedPath = loc;
                    }
                }
            }
            if (recentUrl.endsWith("/" + fileName) && relatedLocations.size() < 10) { //NOI18N
                // this is a relevant (3) url, so add it
                if (pathInBranch == null && preselectedPath == null) {
                    preselectedPath = recentUrl;
                }
                relatedLocations.put(recentUrl, null);
            }
        }
        if (pathInBranch != null) {
            // now let's do some corrections
            // add the single item to cat 1
            String pref = TRUNK_FOLDER;
            if (branchPathSegments[0] != null) {
                pref = branchPathSegments[0] + pref;
            }
            String loc = pref + "/" + pathInBranch; //NOI18N
            locations.put(loc, getHtmlVersion(pref, pathInBranch));
            model.add(loc);
            model.add(SEP);
            // add cat 2 to the model
            model.addAll(branchLocations.keySet());
            locations.putAll(branchLocations);
            model.add(MORE_BRANCHES);
            if (preselectedPath == null) {
                pref = BRANCHES_FOLDER;
                if (branchPathSegments[0] != null) {
                    pref = branchPathSegments[0] + pref;
                }
                preselectedPath = pref + "/" + BRANCH_TEMPLATE + "/" + pathInBranch; //NOI18N
            }
            SelectionListener list = new SelectionListener(repositoryFile, cbo, branchPathSegments[0]);
            cbo.addActionListener(list);
            cbo.addPopupMenuListener(list);
        }
        // do not duplicate entries, so remove all items from (3) that are already in (2)
        relatedLocations.keySet().removeAll(locations.keySet());
        locations.putAll(relatedLocations);
        if (!model.isEmpty() && !relatedLocations.isEmpty()) {
            model.add(SEP);
        }
        model.addAll(relatedLocations.keySet());
        
        ComboBoxModel rootsModel = new DefaultComboBoxModel(model.toArray(new String[model.size()]));
        cbo.setModel(rootsModel);        
        JTextComponent comp = (JTextComponent) cbo.getEditor().getEditorComponent();
        if (preselectedPath != null) {
            comp.setText(preselectedPath);
            if (pathInBranch != null) {
                // select the branch name in the offered text - for easy editing
                String[] pathSegments = getBranchPathSegments(preselectedPath);
                String branchPrefix = pathSegments[0] + pathSegments[1];
                int pos = branchPrefix.lastIndexOf('/') + 1;
                if (pos > 0) {
                    comp.setCaretPosition(pos);
                    comp.moveCaretPosition(branchPrefix.length());
                }
            }
        }
        return locations;
    }

    /**
     * Splits a given relative path into segments:
     * <ol>
     * <li>folder prefix where branches/tags/trunk lies in the repository. <code>null</code> for url where trunk/branches/tags are directly in the root folder</li>
     * <li>name of a branch or tag prefixed with the branches or tags prefix, or just simply "trunk" if the url is part of the trunk</li>
     * <li>path to the resource inside trunk/branch/tag</li>
     * </ol>
     * @param relativePath
     * @return 
     */
    private static String[] getBranchPathSegments (String relativePath) {
        String prefix = null;
        String path = null;
        String branchName = null;
        for (Pattern p : new Pattern[] { TEMPLATE_PATTERN_BRANCH, TEMPLATE_PATTERN_TRUNK}) {
            Matcher m = p.matcher(relativePath);
            if (m.matches()) {
                prefix = m.group(1);
                if (prefix == null) {
                    prefix = ""; //NOI18N
                }
                branchName = m.group(2);
                path = m.group(3);
                break;
            }
        }
        if (path != null) {
            path = path.substring(1);
        }
        return new String[] { prefix, branchName, path };
    }
    
    private static String getHtmlVersion (String branchPrefix, String relativePathInBranch) {
        int branchStart = branchPrefix.lastIndexOf('/');
        if (branchStart > 0) {
            return new StringBuilder(2 * (branchPrefix.length() + relativePathInBranch.length())).append("<html>") //NOI18N
                    .append(branchPrefix.substring(0, branchStart + 1)).append("<strong>").append(branchPrefix.substring(branchStart + 1)) //NOI18N
                    .append("</strong>").append('/').append(relativePathInBranch).append("</html>").toString(); //NOI18N
        }
        return null;
    }

    private static class LocationRenderer extends DefaultListCellRenderer {
        private final Map<String, String> empLocations;

        public LocationRenderer (Map<String, String> locations) {
            this.empLocations = locations;
        }
        
        @Override
        public Component getListCellRendererComponent (JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            String html;
            if (value instanceof String && (html = empLocations.get(((String) value))) != null) {
                value = html;
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }

    private class SelectionListener implements ActionListener, PopupMenuListener {
        private final RepositoryFile repositoryFile;
        private final JComboBox combo;
        private boolean popupOn;
        private final String branchesFolderPath;

        public SelectionListener (RepositoryFile repositoryFile, JComboBox combo, String branchesFolderPath) {
            this.repositoryFile = repositoryFile;
            this.combo = combo;
            this.branchesFolderPath = branchesFolderPath;
        }

        @Override
        public void actionPerformed (ActionEvent e) {
            if (e.getSource() == combo) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run () {
                        if (!popupOn && (combo.getSelectedItem() == SEP || combo.getSelectedItem() == MORE_BRANCHES)) {
                            String text = ""; //NOI18N
                            if (combo.getSelectedItem() == MORE_BRANCHES) {
                                BranchPicker picker = new BranchPicker(fileSystem, repositoryFile, branchesFolderPath);
                                if (picker.openDialog()) {
                                    String relativePath = SVNUrlUtils.getRelativePath(repositoryFile.getRepositoryUrl(), repositoryFile.getFileUrl());
                                    text = picker.getSelectedPath() + "/" + getBranchPathSegments(relativePath)[2]; //NOI18N
                                }
                            }
                            ((JTextComponent) combo.getEditor().getEditorComponent()).setText(text); //NOI18N
                        }
                    }
                });
            }
        }

        @Override
        public void popupMenuWillBecomeVisible (PopupMenuEvent e) {
            popupOn = true;
        }

        @Override
        public void popupMenuWillBecomeInvisible (PopupMenuEvent e) {
            popupOn = false;
        }

        @Override
        public void popupMenuCanceled (PopupMenuEvent e) {
            popupOn = true;
        }
    }
}
