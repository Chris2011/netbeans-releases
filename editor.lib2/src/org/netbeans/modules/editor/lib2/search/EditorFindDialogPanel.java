/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.editor.lib2.search;

import java.awt.Dimension;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.JCheckBox;
import org.openide.util.NbBundle;

/**
 *
 * @author Miloslav Metelka, Petr Nejedly
 * @version 1.0
 */
public class EditorFindDialogPanel extends javax.swing.JPanel {

    static final long serialVersionUID =5048601763767383114L;

    private final ResourceBundle bundle = NbBundle.getBundle(EditorFindDialogPanel.class);

    /** Initializes the Form */
    public EditorFindDialogPanel() {
        initComponents ();
        getAccessibleContext().setAccessibleName(bundle.getString("find-title")); // NOI18N
        getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_find")); // NOI18N
        findWhat.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_" + EditorFindSupport.FIND_WHAT)); // NOI18N
        replaceWith.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_" + EditorFindSupport.FIND_REPLACE_WITH)); // NOI18N
        
        // #71956
        Dimension findPrefSize = findWhat.getPreferredSize();
        Dimension replacePrefSize = replaceWith.getPreferredSize();
        if (findPrefSize != null){
            findWhat.setPreferredSize(new Dimension((int)findPrefSize.getWidth(), (int)findPrefSize.getHeight()));
        }
        if (replacePrefSize != null){
            replaceWith.setPreferredSize(new Dimension((int)replacePrefSize.getWidth(), (int)replacePrefSize.getHeight()));
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        findWhatPanel = new javax.swing.JPanel();
        findWhatLabel = new javax.swing.JLabel();
        findWhat = new javax.swing.JComboBox();
        replaceWithLabel = new javax.swing.JLabel();
        replaceWith = new javax.swing.JComboBox();
        highlightSearch = createCheckBox( EditorFindSupport.FIND_HIGHLIGHT_SEARCH, 'H' );
        incSearch = createCheckBox( EditorFindSupport.FIND_INC_SEARCH, 'I' );
        matchCase = createCheckBox( EditorFindSupport.FIND_MATCH_CASE, 'C' );
        wholeWords = createCheckBox( EditorFindSupport.FIND_WHOLE_WORDS, 'W' );
        bwdSearch = createCheckBox( EditorFindSupport.FIND_BACKWARD_SEARCH, 'B' );
        wrapSearch = createCheckBox( EditorFindSupport.FIND_WRAP_SEARCH, 'p' );
        regExp = createCheckBox( EditorFindSupport.FIND_REG_EXP, 'E' );
        blockSearch = createCheckBox( EditorFindSupport.FIND_BLOCK_SEARCH, 'l' );

        setLayout(new java.awt.GridBagLayout());

        findWhatPanel.setLayout(new java.awt.GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        add(findWhatPanel, gridBagConstraints);

        findWhatLabel.setLabelFor(findWhat);
        findWhatLabel.setText(bundle.getString(EditorFindSupport.FIND_WHAT ) );
        findWhatLabel.setDisplayedMnemonic(bundle.getString(EditorFindSupport.FIND_WHAT + "-mnemonic").charAt(0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 5, 0);
        add(findWhatLabel, gridBagConstraints);

        findWhat.setEditable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 11, 9, 10);
        add(findWhat, gridBagConstraints);

        replaceWithLabel.setLabelFor(replaceWith);
        replaceWithLabel.setText(bundle.getString(EditorFindSupport.FIND_REPLACE_WITH ) );
        replaceWithLabel.setDisplayedMnemonic(bundle.getString(EditorFindSupport.FIND_REPLACE_WITH + "-mnemonic").charAt(0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 9, 0);
        add(replaceWithLabel, gridBagConstraints);

        replaceWith.setEditable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 11, 9, 10);
        add(replaceWith, gridBagConstraints);

        highlightSearch.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 11, 11, 0);
        add(highlightSearch, gridBagConstraints);

        incSearch.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 11, 11, 10);
        add(incSearch, gridBagConstraints);

        matchCase.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 11, 0, 0);
        add(matchCase, gridBagConstraints);

        wholeWords.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 11, 0, 0);
        add(wholeWords, gridBagConstraints);

        bwdSearch.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 11, 0, 10);
        add(bwdSearch, gridBagConstraints);

        wrapSearch.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 11, 0, 10);
        add(wrapSearch, gridBagConstraints);

        regExp.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 11, 0, 0);
        add(regExp, gridBagConstraints);

        blockSearch.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 11, 0, 10);
        add(blockSearch, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JCheckBox blockSearch;
    protected javax.swing.JCheckBox bwdSearch;
    protected javax.swing.JComboBox findWhat;
    protected javax.swing.JLabel findWhatLabel;
    protected javax.swing.JPanel findWhatPanel;
    protected javax.swing.JCheckBox highlightSearch;
    protected javax.swing.JCheckBox incSearch;
    protected javax.swing.JCheckBox matchCase;
    protected javax.swing.JCheckBox regExp;
    protected javax.swing.JComboBox replaceWith;
    protected javax.swing.JLabel replaceWithLabel;
    protected javax.swing.JCheckBox wholeWords;
    protected javax.swing.JCheckBox wrapSearch;
    // End of variables declaration//GEN-END:variables


    private JCheckBox createCheckBox( String key, char mnemonic ) {
        JCheckBox box = new JCheckBox( bundle.getString( key ) );
        box.setToolTipText( bundle.getString( key + "-tooltip" ) );
        char mnemonicChar;
        try {
            mnemonicChar = bundle.getString( key + "-mnemonic").charAt(0);
        } catch (MissingResourceException e) {
            mnemonicChar = mnemonic;
        }
        box.setMnemonic(mnemonicChar); // NOI18N
        return box;
    }
    
}
