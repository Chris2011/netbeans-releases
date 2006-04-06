/*
 * Highlightingpanel1.java
 *
 * Created on January 18, 2006, 11:36 AM
 */

package org.netbeans.modules.options.colors;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;


/**
 *
 * @author  Jan Jancura
 */
public class HighlightingPanel extends JPanel implements ActionListener, PropertyChangeListener {
    
    private ColorModel          colorModel = null;
    private boolean		listen = false;
    private String              currentProfile;
    /** cache Map (String (profile name) > Vector (AttributeSet)). */
    private Map                 profileToCategories = new HashMap ();
    /** Set (String (profile name)) of changed profile names. */
    private Set                 toBeSaved = new HashSet ();
    private boolean             changed = false;

    
    /** Creates new form Highlightingpanel1 */
    public HighlightingPanel () {
        initComponents ();

        // 1) init components
        lCategories.getAccessibleContext ().setAccessibleName (loc ("AN_Categories"));
        lCategories.getAccessibleContext ().setAccessibleDescription (loc ("AD_Categories"));
        cbForeground.getAccessibleContext ().setAccessibleName (loc ("AN_Foreground_Chooser"));
        cbForeground.getAccessibleContext ().setAccessibleDescription (loc ("AD_Foreground_Chooser"));
        cbBackground.getAccessibleContext ().setAccessibleName (loc ("AN_Background_Chooser"));
        cbBackground.getAccessibleContext ().setAccessibleDescription (loc ("AD_Background_Chooser"));
        ColorComboBox.init (cbForeground);
        ColorComboBox.init (cbBackground);
        lCategories.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
        lCategories.setVisibleRowCount (3);
        lCategories.addListSelectionListener (new ListSelectionListener () {
            public void valueChanged (ListSelectionEvent e) {
                if (!listen) return;
                refreshUI ();
            }
        });
        lCategories.setCellRenderer (new CategoryRenderer ());
        cbForeground.addActionListener (this);
        ((JComponent)cbForeground.getEditor()).addPropertyChangeListener (this);
        cbBackground.addActionListener (this);
        ((JComponent)cbBackground.getEditor()).addPropertyChangeListener (this);
        JLabel lCategory = new JLabel ();
        loc (lCategory, "CTL_Category");
        lCategory.setLabelFor (lCategories);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        lCategory = new javax.swing.JLabel();
        cpCategories = new javax.swing.JScrollPane();
        lCategories = new javax.swing.JList();
        lForeground = new javax.swing.JLabel();
        lBackground = new javax.swing.JLabel();
        cbBackground = new javax.swing.JComboBox();
        cbForeground = new javax.swing.JComboBox();

        lCategory.setText("Category:");

        cpCategories.setViewportView(lCategories);

        lForeground.setText("Foreground:");

        lBackground.setText("Background:");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(cpCategories, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lBackground)
                            .add(lForeground))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, cbBackground, 0, 53, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, cbForeground, 0, 53, Short.MAX_VALUE)))
                    .add(lCategory))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(lCategory)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lForeground)
                            .add(cbForeground, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lBackground)
                            .add(cbBackground, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(cpCategories, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cbBackground;
    private javax.swing.JComboBox cbForeground;
    private javax.swing.JScrollPane cpCategories;
    private javax.swing.JLabel lBackground;
    private javax.swing.JList lCategories;
    private javax.swing.JLabel lCategory;
    private javax.swing.JLabel lForeground;
    // End of variables declaration//GEN-END:variables
    
 
    public void actionPerformed (ActionEvent evt) {
        if (!listen) return;
        updateData ();
        changed = true;
    }
    
    public void propertyChange (PropertyChangeEvent evt) {
        if (!listen) return;
        if (evt.getPropertyName () == ColorComboBox.PROP_COLOR) {
            updateData ();
            changed = true;
        }
    }
    
    void update (ColorModel colorModel) {
        this.colorModel = colorModel;
        currentProfile = colorModel.getCurrentProfile ();
        listen = false;
        setCurrentProfile (currentProfile);
        lCategories.setListData (getCategories (currentProfile));
        lCategories.setSelectedIndex (0);
        refreshUI ();	
        listen = true;
        changed = false;
    }
    
    void cancel () {
        toBeSaved = new HashSet ();
        profileToCategories = new HashMap ();        
        changed = false;
    }
    
    public void applyChanges () {
        if (colorModel == null) return;
        Iterator it = toBeSaved.iterator ();
        while (it.hasNext ()) {
            String profile = (String) it.next ();
            colorModel.setHighlightings (profile, getCategories (profile));
        }
        toBeSaved = new HashSet ();
        profileToCategories = new HashMap ();
    }
    
    boolean isChanged () {
        return changed;
    }
    
    void setCurrentProfile (String currentProfile) {
        String oldScheme = this.currentProfile;
        this.currentProfile = currentProfile;
        if (!colorModel.getProfiles ().contains (currentProfile)) {
            // clone profile
            Vector categories = getCategories (oldScheme);
            profileToCategories.put (currentProfile, new Vector (categories));
            toBeSaved.add (currentProfile);
        }
        refreshUI ();
    }

    void deleteProfile (String profile) {
        if (colorModel.isCustomProfile (profile))
            profileToCategories.put (profile, null);
        else {
            profileToCategories.put (profile, getDefaults (profile));
            refreshUI ();
        }
        toBeSaved.add (profile);
    }
    
    // other methods ...........................................................
    
    Collection getHighlightings () {
        return getCategories (currentProfile);
    }
    
    private static String loc (String key) {
        return NbBundle.getMessage (SyntaxColoringPanel.class, key);
    }
    
    private static void loc (Component c, String key) {
        if (c instanceof AbstractButton)
            Mnemonics.setLocalizedText (
                (AbstractButton) c, 
                loc (key)
            );
        else
            Mnemonics.setLocalizedText (
                (JLabel) c, 
                loc (key)
            );
    }

    private void updateData () {
        if (lCategories.getSelectedIndex () < 0) return;
        Vector categories = getCategories (currentProfile);
	AttributeSet category = (AttributeSet) categories.get 
	    (lCategories.getSelectedIndex ());
        Color underline = null, 
              wave = null, 
              strikethrough = null;
        
        SimpleAttributeSet c = new SimpleAttributeSet (category);
        Color color = ((ColorValue) cbBackground.getSelectedItem ()).color;
        if (color != null)
            c.addAttribute (
                StyleConstants.Background,
                color
            );
        else
            c.removeAttribute (StyleConstants.Background);
        color = ((ColorValue) cbForeground.getSelectedItem ()).color;
        if (color != null)
            c.addAttribute (
                StyleConstants.Foreground,
                color
            );
        else
            c.removeAttribute (StyleConstants.Foreground);
        int i = lCategories.getSelectedIndex ();
        categories.set (i, c);
        
        toBeSaved.add (currentProfile);
    }
    
    private void refreshUI () {
        int index = lCategories.getSelectedIndex ();
        if (index < 0) {
            cbForeground.setEnabled (false);
            cbBackground.setEnabled (false);
            return;
        }
        cbForeground.setEnabled (true);
        cbBackground.setEnabled (true);
        
        Vector categories = getCategories (currentProfile);
	AttributeSet category = (AttributeSet) categories.get (index);
        
        // set values
        listen = false;
        ColorComboBox.setColor (
            cbForeground, 
            (Color) category.getAttribute (StyleConstants.Foreground)
        );
        ColorComboBox.setColor (
            cbBackground,
            (Color) category.getAttribute (StyleConstants.Background)
        );
        listen = true;
    }
    
    private Vector getCategories (String profile) {
        if (colorModel == null) return null;
        if (!profileToCategories.containsKey (profile)) {
            Collection c = colorModel.getHighlightings (profile);
            List l = new ArrayList (c);
            Collections.sort (l, new CategoryComparator ());
            profileToCategories.put (profile, new Vector (l));
        }
        return (Vector) profileToCategories.get (profile);
    }

    /** cache Map (String (profile name) > Vector (AttributeSet)). */
    private Map profileToDefaults = new HashMap ();
    
    private Vector getDefaults (String profile) {
        if (!profileToDefaults.containsKey (profile)) {
            Collection c = colorModel.getHighlightingDefaults (profile);
            List l = new ArrayList (c);
            Collections.sort (l, new CategoryComparator ());
            profileToDefaults.put (profile, new Vector (l));
        }
        return (Vector) profileToDefaults.get (profile);
    }
}
