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
package org.netbeans.modules.php.editor.nav.hierarchy;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.netbeans.modules.csl.api.UiUtils;
import org.netbeans.modules.php.editor.api.ElementQuery.Index;
import org.netbeans.modules.php.editor.api.QualifiedName;
import org.netbeans.modules.php.editor.model.FileScope;
import org.netbeans.modules.php.editor.model.Model;
import org.netbeans.modules.php.editor.model.ModelUtils;
import org.openide.filesystems.FileObject;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.netbeans.modules.php.editor.api.elements.TreeElement;
import org.netbeans.modules.php.editor.api.elements.TypeElement;
import org.netbeans.modules.php.editor.api.elements.ClassElement;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;

/**
 * @author Radek Matous
 */
public class ClassHierarchyPanel extends JPanel implements HelpCtx.Provider {

    private static final int MAX_STACK_DEPTH = 250;
    private final JTree tree;
    private final DefaultTreeModel treeModel;
    private final MouseAdapter mouseListener = new MouseAdapter() {

        @Override
        public void mousePressed(MouseEvent e) {
            TreePath selPath = tree.getSelectionPath();
            if (selPath != null) {
                if (e.getClickCount() == 2) {
                    Object lastPathComponent = selPath.getLastPathComponent();
                    if (lastPathComponent instanceof TypeNode) {
                        final TypeNode typeNode = (TypeNode) lastPathComponent;
                        final FileObject fileObject = typeNode.getFileObject();
                        if (fileObject != null && fileObject.isValid()) {
                            UiUtils.open(fileObject, typeNode.getOffset());
                        }
                    }
                }
            }
        }
    };

    /** Creates new form ClassHierarchyPanel */
    public ClassHierarchyPanel(boolean isView) {
        initComponents();
        if (!isView) {
            toolBar.remove(0);
            toolBar.remove(0);
            subtypeButton.setFocusable(true);
            supertypeButton.setFocusable(true);
        }
        setName(NbBundle.getMessage(getClass(), "CTL_ClassHierarchyTopComponent")); // NOI18N
        setToolTipText(NbBundle.getMessage(getClass(), "HINT_ClassHierarchyTopComponent")); // NOI18N
        tree = new JTree();
        tree.setModel(treeModel = new DefaultTreeModel(new DefaultMutableTreeNode()));
        tree.setToggleClickCount(0);
        tree.setCellRenderer(new TreeRenderer());
        tree.putClientProperty("JTree.lineStyle", "Angled");  //NOI18N
        tree.expandRow(0);
        tree.setShowsRootHandles(true);
        tree.setSelectionRow(0);
        tree.setRootVisible(false);
        hierarchyPane.add(tree);
        hierarchyPane.setViewportView(tree);
        tree.addMouseListener(mouseListener);
    }

    void setModel(Model model) {
        treeModel.setRoot(createRoot(model, subtypeButton.isSelected()));
        expandAll(tree);
    }

    private static void expandAll(JTree tree) {
        int row = 0;
        while (row < tree.getRowCount()) {
            tree.expandRow(row);
            row++;
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        directionGroup = new javax.swing.ButtonGroup();
        toolBar = new javax.swing.JToolBar();
        refreshButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        supertypeButton = new javax.swing.JToggleButton();
        subtypeButton = new javax.swing.JToggleButton();
        jPanel2 = new javax.swing.JPanel();
        hierarchyPane = new javax.swing.JScrollPane();

        setLayout(new java.awt.GridBagLayout());

        toolBar.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        toolBar.setFloatable(false);
        toolBar.setBorderPainted(false);
        toolBar.setMaximumSize(new java.awt.Dimension(74, 26));
        toolBar.setMinimumSize(new java.awt.Dimension(74, 26));
        toolBar.setOpaque(false);

        refreshButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/php/editor/nav/resources/refresh.png"))); // NOI18N
        refreshButton.setToolTipText(org.openide.util.NbBundle.getMessage(ClassHierarchyPanel.class, "ClassHierarchyPanel.refreshButton.toolTipText")); // NOI18N
        refreshButton.setFocusable(false);
        refreshButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        refreshButton.setMaximumSize(new java.awt.Dimension(24, 24));
        refreshButton.setMinimumSize(new java.awt.Dimension(24, 24));
        refreshButton.setPreferredSize(new java.awt.Dimension(24, 24));
        refreshButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });
        toolBar.add(refreshButton);
        toolBar.add(jSeparator1);

        directionGroup.add(supertypeButton);
        supertypeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/php/editor/nav/resources/supertypehierarchy.gif"))); // NOI18N
        supertypeButton.setSelected(true);
        supertypeButton.setToolTipText(org.openide.util.NbBundle.getMessage(ClassHierarchyPanel.class, "ClassHierarchyPanel.supertypeButton.toolTipText")); // NOI18N
        supertypeButton.setFocusable(false);
        supertypeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        supertypeButton.setMaximumSize(new java.awt.Dimension(24, 24));
        supertypeButton.setMinimumSize(new java.awt.Dimension(24, 24));
        supertypeButton.setPreferredSize(new java.awt.Dimension(24, 24));
        supertypeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        supertypeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                supertypeButtonActionPerformed(evt);
            }
        });
        toolBar.add(supertypeButton);

        directionGroup.add(subtypeButton);
        subtypeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/php/editor/nav/resources/subtypehierarchy.gif"))); // NOI18N
        subtypeButton.setSelected(false);
        subtypeButton.setToolTipText(org.openide.util.NbBundle.getMessage(ClassHierarchyPanel.class, "ClassHierarchyPanel.subtypeButton.toolTipText")); // NOI18N
        subtypeButton.setFocusable(false);
        subtypeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        subtypeButton.setMaximumSize(new java.awt.Dimension(24, 24));
        subtypeButton.setMinimumSize(new java.awt.Dimension(24, 24));
        subtypeButton.setPreferredSize(new java.awt.Dimension(24, 24));
        subtypeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        subtypeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subtypeButtonActionPerformed(evt);
            }
        });
        toolBar.add(subtypeButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 0);
        add(toolBar, gridBagConstraints);

        jPanel2.setFocusable(false);
        jPanel2.setMinimumSize(new java.awt.Dimension(1, 1));
        jPanel2.setPreferredSize(new java.awt.Dimension(1, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jPanel2, gridBagConstraints);

        hierarchyPane.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(hierarchyPane, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void refresh() {
        PhpHierarchyTopComponent view = PhpHierarchyTopComponent.findInstance();
        if (view != null) {
            view.refresh();
        }
    }

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        refresh();
    }//GEN-LAST:event_refreshButtonActionPerformed

    private void subtypeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subtypeButtonActionPerformed
        refresh();
    }//GEN-LAST:event_subtypeButtonActionPerformed

    private void supertypeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_supertypeButtonActionPerformed
        refresh();
    }//GEN-LAST:event_supertypeButtonActionPerformed

    @Override
    public boolean requestFocusInWindow() {
        super.requestFocusInWindow();
        return hierarchyPane.requestFocusInWindow();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup directionGroup;
    private javax.swing.JScrollPane hierarchyPane;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JButton refreshButton;
    private javax.swing.JToggleButton subtypeButton;
    private javax.swing.JToggleButton supertypeButton;
    private javax.swing.JToolBar toolBar;
    // End of variables declaration//GEN-END:variables

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx("PhpTypeView"); // NOI18N
    }

    protected static abstract class AbstractTypeNode implements TreeNode {

        static String ICON_BASE = "org/netbeans/modules/php/editor/resources/";     //NOI18N
        static String ICON_EXTENSION = ".png";  //NOI18N
        private AbstractTypeNode[] childern;

        protected AbstractTypeNode() {
            childern = new AbstractTypeNode[0];
        }

        @Override
        public Enumeration children() {
            return new Enumeration() {

                int idx = 0;

                @Override
                public boolean hasMoreElements() {
                    return idx < getChildCount();
                }

                @Override
                public Object nextElement() {
                    return getChildAt(idx++);
                }
            };
        }

        @Override
        public boolean getAllowsChildren() {
            return !isLeaf();
        }

        @Override
        public TreeNode getChildAt(int childIndex) {
            return this.childern[childIndex];
        }

        @Override
        public int getChildCount() {
            return this.childern.length;
        }

        @Override
        public int getIndex(TreeNode node) {
            for (int i = 0; i < childern.length; i++) {
                AbstractTypeNode classNode = childern[i];
                if (classNode == node) {
                    return i;
                }
            }
            return -1;
        }

        @Override
        public boolean isLeaf() {
            return getChildCount() == 0;
        }

        public void setChildern(AbstractTypeNode[] childern) {
            this.childern = childern;
        }

        //these methods are used rendering
        public abstract Image getIcon();

        public abstract String toStringAsHtml();
    }

    private static TypeNode[] sortTypes(final TypeNode[] types) {
        Arrays.<TypeNode>sort(types, new Comparator<TypeNode>() {
            @Override
            public int compare(TypeNode o1, TypeNode o2) {
                int compareTo = new Boolean(o1.isClass).compareTo(o2.isClass);
                return compareTo == 0 ? o1.toString().compareToIgnoreCase(o2.toString()) : compareTo;
            }
        });
        return types;
    }

    protected TreeNode createRoot(final Model model, final boolean subDirection) {
        final FileRootNode retval = new FileRootNode(model);
        FileScope fileScope = model.getFileScope();
        Set<TypeElement> types = new HashSet<TypeElement>();
        Set<TypeElement> recursionDetection = new HashSet<TypeElement>();
        types.addAll(ModelUtils.getDeclaredClasses(fileScope));
        types.addAll(ModelUtils.getDeclaredInterfaces(fileScope));
        TypeNode[] childernNodes = new TypeNode[types.size()];
        if (types.size() > 0) {
            Index index = fileScope.getIndexScope().getIndex();
            Iterator<TypeElement> iterator = types.iterator();
            for (int i = 0; iterator.hasNext(); i++) {
                TypeElement type = iterator.next();
                TreeElement<TypeElement> treeType = subDirection
                        ? index.getInheritedByTypesAsTree(type, types)
                        : index.getInheritedTypesAsTree(type, types);
                recursionDetection.add(type);
                childernNodes[i] = createTypeNode(retval, treeType, recursionDetection, Integer.valueOf(0));
            }
        }
        retval.setChildern(sortTypes(childernNodes));
        return retval;
    }

    private static TypeNode createTypeNode(
            final TreeNode parent, final TreeElement<TypeElement> classElement, Set<TypeElement> recursionDetection, Integer stackDepth) {
        stackDepth++;
        final TypeNode retval = new TypeNode(parent, classElement);
        final Set<TreeElement<TypeElement>> children = classElement.children();
        ArrayList<TypeNode> childernList = new ArrayList<TypeNode>();
        if (stackDepth <= MAX_STACK_DEPTH) {
            for (TreeElement<TypeElement> child : children) {
                if (recursionDetection.add(child.getElement())) {
                    childernList.add(createTypeNode(retval, child, recursionDetection, stackDepth));
                }
            }
        } else {
            childernList.add(new ErrTypeNode(parent, classElement));
        }
        stackDepth--;
        retval.setChildern(sortTypes(childernList.toArray(new TypeNode[childernList.size()])));
        return retval;
    }

    private static final class FileRootNode extends AbstractTypeNode {

        private final String filename;

        private FileRootNode(final Model model) {
            filename = model.getFileScope().getFileObject().getNameExt();
        }

        @Override
        public TreeNode getParent() {
            return null;
        }

        @Override
        public String toString() {
            return filename;
        }

        //not supposed to be visible
        @Override
        public Image getIcon() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String toStringAsHtml() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private static class TypeNode extends AbstractTypeNode {
        private static final String FONT_GRAY_COLOR = "<font color=\"#999999\">"; //NOI18N
        private static final String CLOSE_FONT = "</font>";//NOI18N

        private final TreeNode parent;
        private final String name;
        private final List<String> superTypes;
        private final FileObject fileObject;
        private final int offset;
        private final boolean isClass;

        private TypeNode(final TreeNode parent, final TreeElement<TypeElement> classElement) {
            this.parent = parent;
            TypeElement type = classElement.getElement();
            this.name = type.getName();
            this.superTypes = new ArrayList<String>();
            this.fileObject = type.getFileObject();
            this.offset = type.getOffset();
            this.isClass = type.isClass();
            if (type instanceof ClassElement) {
                ClassElement clz = (ClassElement) type;
                QualifiedName superClassName = clz.getSuperClassName();
                if (superClassName != null) {
                    this.superTypes.add(superClassName.toString());
                }
            }
            Set<QualifiedName> superInterfaces = type.getSuperInterfaces();
            for (QualifiedName supeIfaceName : superInterfaces) {
                if (supeIfaceName != null) {
                    this.superTypes.add(supeIfaceName.toString());
                }
            }
        }

        @Override
        public TreeNode getParent() {
            return parent;
        }

        @Override
        public String toString() {
            return String.format("%s%s", name, superTypes);//NOI18N
        }


        @Override
        public Image getIcon() {
            return ImageUtilities.loadImage(ICON_BASE + (isClass ? "class" : "interface") + ICON_EXTENSION); //NOI18N
        }

        @Override
        public String toStringAsHtml() {
            StringBuilder superTypeString = new StringBuilder();
            for (String superTypeName : superTypes) {
                if (superTypeString.length() != 0) {
                    superTypeString.append(", ");//NOI18N
                } else {
                    superTypeString.append("::");//NOI18N
                }
                superTypeString.append(superTypeName);
            }
            return String.format("<html>%s%s%s%s </html>", name,//NOI18N
                    TypeNode.FONT_GRAY_COLOR, superTypeString.toString(),
                    TypeNode.CLOSE_FONT);
        }

        /**
         * @return the fileObject
         */
        public FileObject getFileObject() {
            return fileObject;
        }

        /**
         * @return the offset
         */
        public int getOffset() {
            return offset;
        }
    }

    @Messages({
        "# {0} - max number of childs",
        "TooManyChilds=Too many childs detected (max {0})."
    })
    private static class ErrTypeNode extends TypeNode {

        public ErrTypeNode(TreeNode parent, TreeElement<TypeElement> classElement) {
            super(parent, classElement);
        }

        @Override
        public String toString() {
            return Bundle.TooManyChilds(MAX_STACK_DEPTH);
        }

        @Override
        public String toStringAsHtml() {
            return "<html><span style='color: red; font-size: 0.9em;'>" + Bundle.TooManyChilds(MAX_STACK_DEPTH) + "</html>"; //NOI18N
        }

        @Override
        public Image getIcon() {
            return ImageUtilities.loadImage(ICON_BASE + "error-glyph.gif"); //NOI18N
        }

    }

    public static class TreeRenderer extends JPanel implements TreeCellRenderer {
        private static final JList LIST_FOR_COLORS = new JList();
        protected JLabel label;

        public TreeRenderer() {
            setLayout(new BorderLayout());
            setOpaque(true);
            this.label = new JLabel();
            add(label, BorderLayout.CENTER);
            label.setOpaque(false);
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean isSelected, boolean expanded,
                boolean leaf, int row, boolean hasFocus) {
            String stringValue = tree.convertValueToText(value, isSelected,
                    expanded, leaf, row, hasFocus);
            setEnabled(tree.isEnabled());
            if (value instanceof TypeNode) {
                TypeNode n = (TypeNode) value;
                stringValue = n.toStringAsHtml();
                label.setIcon(new ImageIcon(n.getIcon()));
            }
            if (isSelected) {
                label.setForeground(LIST_FOR_COLORS.getSelectionForeground());
                setOpaque(true);
                setBackground(LIST_FOR_COLORS.getSelectionBackground());
            } else {
                label.setForeground(tree.getForeground());
                setOpaque(false);
            }
            label.setText(stringValue);
            return this;
        }
    }
}
