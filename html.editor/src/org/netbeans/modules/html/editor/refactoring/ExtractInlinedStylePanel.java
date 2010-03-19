/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.html.editor.refactoring;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.ListCellRenderer;
import javax.swing.text.BadLocationException;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.css.refactoring.api.CssRefactoring;
import org.netbeans.modules.html.editor.indexing.HtmlLinkEntry;
import org.netbeans.modules.html.editor.refactoring.api.ExtractInlinedStyleRefactoring.Mode;
import org.netbeans.modules.html.editor.refactoring.api.ExtractInlinedStyleRefactoring.SelectorType;
import org.netbeans.modules.refactoring.spi.ui.CustomRefactoringPanel;
import org.netbeans.modules.web.common.spi.ProjectWebRootQuery;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 * Extract Inlined Style Panel
 *
 * @author mfukala@netbeans.org
 */
public class ExtractInlinedStylePanel extends JPanel implements CustomRefactoringPanel {

    private List<FileObject> allStylesheets;
    private RefactoringContext context;
    private Mode selection;
    private SelectorType selectorType;
    private ResolveDeclarationsPanel resolveIdsPanel, resolveClassesPanel, current;

    /** Creates new form RenamePanelName */
    public ExtractInlinedStylePanel(RefactoringContext context) {
        this.context = context;
        this.allStylesheets = new ArrayList<FileObject>(CssRefactoring.findAllStyleSheets(context.getFile()));

        Collection<ResolveDeclarationItem> idItems = context.getIdSelectorsToResolve().values();
        this.resolveIdsPanel = idItems.isEmpty() ? null : new ResolveDeclarationsPanel(idItems);
        Collection<ResolveDeclarationItem> classItems = context.getClassSelectorsToResolve().values();
        this.resolveClassesPanel = classItems.isEmpty() ? null : new ResolveDeclarationsPanel(classItems);

        initComponents();
        initUI();
    }

    private void initUI() {
        refactorToTypeButtonGroup.getSelection();

        ItemListener embeddedSectionsItemListener = new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                EmbeddedSectionItem selected =
                        (EmbeddedSectionItem) existingEmbeddedSectionsComboBox.getSelectedItem();

                if (selected instanceof CreateNewEmbeddedSectionItem) {
                    selection = Mode.refactorToNewEmbeddedSection;
                } else {
                    selection = Mode.refactorToExistingEmbeddedSection;
                }
            }
        };
        embeddedSectionRB.addItemListener(embeddedSectionsItemListener);
        existingEmbeddedSectionsComboBox.addItemListener(embeddedSectionsItemListener);

        ItemListener externalStylesheetsItemListener = new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                ExternalStyleSheetItem selected =
                        (ExternalStyleSheetItem) externalSheetsComboBox.getSelectedItem();

                if (selected instanceof ReferedExternalStyleSheetItem) {
                    selection = Mode.refactorToReferedExternalSheet;
                } else {
                    selection = Mode.refactorToExistingExternalSheet;
                }
            }
        };

        externalSheetRB.addItemListener(externalStylesheetsItemListener);
        externalSheetsComboBox.addItemListener(externalStylesheetsItemListener);

        ItemListener refactorToSelectorTypeItemListener = new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                Object s = e.getSource();
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (s == classSelectorTypeRB) {
                        selectorType = SelectorType.CLASS;
                    } else if (s == idSelectorTypeRB) {
                        selectorType = SelectorType.ID;
                    } else {
                        assert false;
                    }

                    setResolveDeclarationsPanel(selectorType);
                }
            }
        };

        idSelectorTypeRB.addItemListener(refactorToSelectorTypeItemListener);
        classSelectorTypeRB.addItemListener(refactorToSelectorTypeItemListener);


        //TODO store the choices in settings!
        setButtonsGroupSelection(Mode.refactorToExistingEmbeddedSection);
        setSelectorType(SelectorType.ID);

    }

    private void setResolveDeclarationsPanel(SelectorType type) {
        ResolveDeclarationsPanel panel = type == SelectorType.CLASS ? resolveClassesPanel : resolveIdsPanel;

        if (current != null) {
            jScrollPane1.setViewportView(null);
            jScrollPane1.setEnabled(false);

        }
        current = panel;

        if (current != null) {
            //enable the 'you must resolve this' label text
            resolveDeclarationsLabel.setEnabled(true);
            jScrollPane1.setEnabled(true);
            //and add the panel
            jScrollPane1.setViewportView(panel);
        } else {
            //disable the 'you must resolve this' label text
            resolveDeclarationsLabel.setEnabled(false);
            jScrollPane1.setEnabled(false);
        }

        revalidate();
        repaint();
    }

    private void setButtonsGroupSelection(Mode mode) {
        this.selection = mode;
        JRadioButton select;
        switch (selection) {
            case refactorToExistingEmbeddedSection:
            case refactorToNewEmbeddedSection:
                select = embeddedSectionRB;
                break;
            case refactorToExistingExternalSheet:
            case refactorToReferedExternalSheet:
                select = externalSheetRB;
                break;
            default:
                select = null;
                assert false;
        }
        select.setSelected(true);
    }

    private void setSelectorType(SelectorType type) {
        if (type == SelectorType.ID) {
            idSelectorTypeRB.setSelected(true);
        } else if (type == SelectorType.CLASS) {
            classSelectorTypeRB.setSelected(true);
        } else {
            assert false;
        }
    }

    Mode getSelectedMode() {
        return selection;
    }

    SelectorType getSelectorType() {
        return selectorType;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        refactorToTypeButtonGroup = new javax.swing.ButtonGroup();
        selectorTypeButtonGroup = new javax.swing.ButtonGroup();
        label = new javax.swing.JLabel();
        embeddedSectionRB = new javax.swing.JRadioButton();
        externalSheetRB = new javax.swing.JRadioButton();
        existingEmbeddedSectionsComboBox = new javax.swing.JComboBox();
        externalSheetsComboBox = new javax.swing.JComboBox();
        generatedSelectorTypeLabel = new javax.swing.JLabel();
        idSelectorTypeRB = new javax.swing.JRadioButton();
        classSelectorTypeRB = new javax.swing.JRadioButton();
        resolveDeclarationsLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 11, 11));
        setRequestFocusEnabled(false);

        label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        org.openide.awt.Mnemonics.setLocalizedText(label, org.openide.util.NbBundle.getMessage(ExtractInlinedStylePanel.class, "LBL_ExtractInlinedStyleToLabel")); // NOI18N

        refactorToTypeButtonGroup.add(embeddedSectionRB);
        org.openide.awt.Mnemonics.setLocalizedText(embeddedSectionRB, org.openide.util.NbBundle.getMessage(ExtractInlinedStylePanel.class, "MSG_ExtractToEmbeddedSection")); // NOI18N
        embeddedSectionRB.setEnabled(!context.getExistingEmbeddedCssSections().isEmpty());

        refactorToTypeButtonGroup.add(externalSheetRB);
        org.openide.awt.Mnemonics.setLocalizedText(externalSheetRB, org.openide.util.NbBundle.getMessage(ExtractInlinedStylePanel.class, "MSG_ExternalStyleSheet")); // NOI18N
        externalSheetRB.setEnabled(!context.getLinkedExternalStylesheets().isEmpty());

        existingEmbeddedSectionsComboBox.setModel(createEmbeddedCssSectionsModel());
        existingEmbeddedSectionsComboBox.setEnabled(!context.getExistingEmbeddedCssSections().isEmpty());
        existingEmbeddedSectionsComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                existingEmbeddedSectionsComboBoxActionPerformed(evt);
            }
        });

        externalSheetsComboBox.setModel(createExternalStylesheetsModel());
        externalSheetsComboBox.setEnabled(!context.getLinkedExternalStylesheets().isEmpty());
        externalSheetsComboBox.setRenderer(new ExternalStylesheetsListCellRenderer(externalSheetsComboBox.getRenderer()));
        externalSheetsComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                externalSheetsComboBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(generatedSelectorTypeLabel, org.openide.util.NbBundle.getMessage(ExtractInlinedStylePanel.class, "MSG_GenerateSelectorType")); // NOI18N

        selectorTypeButtonGroup.add(idSelectorTypeRB);
        org.openide.awt.Mnemonics.setLocalizedText(idSelectorTypeRB, org.openide.util.NbBundle.getMessage(ExtractInlinedStylePanel.class, "MSG_IdType")); // NOI18N

        selectorTypeButtonGroup.add(classSelectorTypeRB);
        org.openide.awt.Mnemonics.setLocalizedText(classSelectorTypeRB, org.openide.util.NbBundle.getMessage(ExtractInlinedStylePanel.class, "MSG_ClassType")); // NOI18N

        resolveDeclarationsLabel.setLabelFor(jScrollPane1);
        org.openide.awt.Mnemonics.setLocalizedText(resolveDeclarationsLabel, org.openide.util.NbBundle.getMessage(ExtractInlinedStylePanel.class, "MSG_UnresolvedDeclarationsLabel")); // NOI18N
        resolveDeclarationsLabel.setToolTipText(org.openide.util.NbBundle.getMessage(ExtractInlinedStylePanel.class, "TT_UnresolvedDeclarationsLabel")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label)
            .addGroup(layout.createSequentialGroup()
                .addComponent(generatedSelectorTypeLabel)
                .addGap(18, 18, 18)
                .addComponent(idSelectorTypeRB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(classSelectorTypeRB))
            .addComponent(resolveDeclarationsLabel)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(externalSheetRB)
                    .addComponent(embeddedSectionRB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(externalSheetsComboBox, 0, 345, Short.MAX_VALUE)
                    .addComponent(existingEmbeddedSectionsComboBox, 0, 345, Short.MAX_VALUE)))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(embeddedSectionRB)
                    .addComponent(existingEmbeddedSectionsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(externalSheetRB)
                    .addComponent(externalSheetsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(generatedSelectorTypeLabel)
                    .addComponent(idSelectorTypeRB)
                    .addComponent(classSelectorTypeRB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(resolveDeclarationsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void existingEmbeddedSectionsComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_existingEmbeddedSectionsComboBoxActionPerformed
        embeddedSectionRB.setSelected(true);
    }//GEN-LAST:event_existingEmbeddedSectionsComboBoxActionPerformed

    private void externalSheetsComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_externalSheetsComboBoxActionPerformed
        externalSheetRB.setSelected(true);
    }//GEN-LAST:event_externalSheetsComboBoxActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton classSelectorTypeRB;
    private javax.swing.JRadioButton embeddedSectionRB;
    private javax.swing.JComboBox existingEmbeddedSectionsComboBox;
    private javax.swing.JRadioButton externalSheetRB;
    private javax.swing.JComboBox externalSheetsComboBox;
    private javax.swing.JLabel generatedSelectorTypeLabel;
    private javax.swing.JRadioButton idSelectorTypeRB;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label;
    private javax.swing.ButtonGroup refactorToTypeButtonGroup;
    private javax.swing.JLabel resolveDeclarationsLabel;
    private javax.swing.ButtonGroup selectorTypeButtonGroup;
    // End of variables declaration//GEN-END:variables

    private ComboBoxModel createEmbeddedCssSectionsModel() {
        List<OffsetRange> ranges = context.getExistingEmbeddedCssSections();
        EmbeddedSectionItem[] values = new EmbeddedSectionItem[ranges.size() + 1];

        //new embedded section item
        values[0] = new CreateNewEmbeddedSectionItem();

        //existing sections
        for (int i = 0; i < values.length - 1; i++) {
            OffsetRange range = ranges.get(i);
            values[i + 1] = new EmbeddedSectionItem(range,
                    getRenderStringFromOffsetRange(range));
        }

        return new DefaultComboBoxModel(values);
    }

    private ComboBoxModel createExternalStylesheetsModel() {
        List<HtmlLinkEntry> links = context.getLinkedExternalStylesheets();
        Collection<FileObject> linkedObjects = new HashSet<FileObject>();
        for (HtmlLinkEntry entry : links) {
            linkedObjects.add(entry.getFileReference().target());
        }

        List<ExternalStyleSheetItem> items = new ArrayList<ExternalStyleSheetItem>();
        for (FileObject stylesheet : allStylesheets) {
            if (linkedObjects.contains(stylesheet)) {
                items.add(new ReferedExternalStyleSheetItem(stylesheet));
            } else {
                items.add(new ExternalStyleSheetItem(stylesheet));
            }
        }

        return new DefaultComboBoxModel(items.toArray(new ExternalStyleSheetItem[0]));
    }

    OffsetRange getSelectedEmbeddedSection() {
        EmbeddedSectionItem selected =
                (EmbeddedSectionItem) existingEmbeddedSectionsComboBox.getSelectedItem();
        return selected.range;
    }

    FileObject getSelectedExternalStyleSheet() {
        ExternalStyleSheetItem selected =
                (ExternalStyleSheetItem) externalSheetsComboBox.getSelectedItem();
        return selected.getFile();
    }

    String getRenderStringFromOffsetRange(final OffsetRange range) {
        //compute lines for each offset
        final AtomicReference<OffsetRange> ret = new AtomicReference<OffsetRange>();
        context.getDocument().render(new Runnable() {

            @Override
            public void run() {
                try {
                    int firstLine = Utilities.getLineOffset((BaseDocument) context.getDocument(), range.getStart());
                    int lastLine = Utilities.getLineOffset((BaseDocument) context.getDocument(), range.getEnd());
                    ret.set(new OffsetRange(firstLine, lastLine));
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });

        OffsetRange line = ret.get();

        return new StringBuilder().append("Section from line ").
                append(line.getStart() + 1). //lines in editor are counted from 1
                append(" to ").
                append(line.getEnd() + 1). //lines in editor are counted from 1
                toString();
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void initialize() {
        //put initialization code here
        //when is this called???
    }

    private static class EmbeddedSectionItem {

        public OffsetRange range;
        public String displayName;

        public EmbeddedSectionItem(OffsetRange range, String displayName) {
            this.range = range;
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    private static class CreateNewEmbeddedSectionItem extends EmbeddedSectionItem {

        private static final String TEXT =
                NbBundle.getMessage(ExtractInlinedStylePanel.class, "MSG_createNewEmbeddedSection");

        public CreateNewEmbeddedSectionItem() {
            super(null, TEXT);
        }
    }

    private static class ExternalStyleSheetItem implements Comparable {

        private FileObject file;
        private String displayName;

        public ExternalStyleSheetItem(FileObject file) {
            this.file = file;
            FileObject webRoot = ProjectWebRootQuery.getWebRoot(file);
            //XXX may be null if out of web root
            displayName = FileUtil.getRelativePath(webRoot, file);
        }

        public FileObject getFile() {
            return file;
        }

        @Override
        public String toString() {
            return displayName;
        }

        //sort by file pathnames
        @Override
        public int compareTo(Object o) {
            if (!(o instanceof ExternalStyleSheetItem)) {
                throw new ClassCastException();
            }
            ExternalStyleSheetItem esi = (ExternalStyleSheetItem) o;
            return getFile().getPath().compareTo(esi.getFile().getPath());
        }
    }

    private static class ReferedExternalStyleSheetItem extends ExternalStyleSheetItem {

        public ReferedExternalStyleSheetItem(FileObject file) {
            super(file);
        }

        @Override
        public int compareTo(Object o) {
            if (o instanceof ReferedExternalStyleSheetItem) {
                return super.compareTo(o);
            } else if (o instanceof ExternalStyleSheetItem) {
                return +1; //refered external stylesheet is always before normal stylesheet
            } else {
                throw new ClassCastException();
            }
        }
    }

    private class ExternalStylesheetsListCellRenderer implements ListCellRenderer {

        private ListCellRenderer orig;

        public ExternalStylesheetsListCellRenderer(ListCellRenderer orig) {
            this.orig = orig;
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component res = orig.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof ReferedExternalStyleSheetItem) {
                if (res instanceof JLabel) {
                    Font font = res.getFont();
                    if (!font.isBold()) {
                        res.setFont(font.deriveFont(Font.BOLD));
                    }
                }
            }
            return res;
        }
    }
}
