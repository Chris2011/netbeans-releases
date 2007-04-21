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

package org.netbeans.modules.vmd.game.editor.tiledlayer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;
import javax.swing.JScrollPane;
import org.netbeans.modules.vmd.game.dialog.NewAnimatedTileDialog;
import org.netbeans.modules.vmd.game.model.Layer;
import org.netbeans.modules.vmd.game.model.Tile;
import org.netbeans.modules.vmd.game.model.TiledLayer;
import org.netbeans.modules.vmd.game.model.TiledLayerListener;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
/**
 *
 * @author  kherink
 */
public class TiledLayerEditor extends javax.swing.JPanel implements TiledLayerListener, PropertyChangeListener {
	
	private TiledLayer tiledLayer;
	private TiledLayerEditorComponent editorComponent;
	private JScrollPane editorScroll;
	
	/**
     * Creates new form TiledLayerEditor
     */
	public TiledLayerEditor(final TiledLayer tiledLayer) {
		this.tiledLayer = tiledLayer;
		this.tiledLayer.addTiledLayerListener(this);
		this.tiledLayer.addPropertyChangeListener(this);
		this.editorComponent = new TiledLayerEditorComponent(this.tiledLayer);
		initComponents();
		
		this.toggleButtonPaint.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				if (toggleButtonPaint.isSelected()) {
					editorComponent.setEditMode(TiledLayerEditorComponent.EDIT_MODE_PAINT);
				}
            }
		});
		this.toggleButtonSelect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				if (toggleButtonSelect.isSelected()) {
					editorComponent.setEditMode(TiledLayerEditorComponent.EDIT_MODE_SELECT);
				}
            }
		});
		
		this.toggleButtonPaint.setSelected(true);
		
		this.buttonAddAnimatedTile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NewAnimatedTileDialog dialog = new NewAnimatedTileDialog(tiledLayer.getImageResource(), tiledLayer.getTileWidth(), tiledLayer.getTileHeight());
				DialogDescriptor dd = new DialogDescriptor(dialog, "Create new Animated Tile");
				dd.setButtonListener(dialog);
				dd.setValid(false);
				dialog.setDialogDescriptor(dd);
				Dialog d = DialogDisplayer.getDefault().createDialog(dd);
				d.setVisible(true);
			}
		});
		this.textFieldName.setBackground(this.textFieldName.getParent().getBackground());
		this.textFieldRows.setBackground(this.textFieldRows.getParent().getBackground());
		this.textFieldCols.setBackground(this.textFieldCols.getParent().getBackground());
		
		this.editorScroll = new JScrollPane();
		this.editorScroll.getViewport().setBackground(Color.WHITE);
		this.editorScroll.setViewportView(this.editorComponent);
		this.editorScroll.setColumnHeaderView(this.editorComponent.rulerHorizontal);
		this.editorScroll.setRowHeaderView(this.editorComponent.rulerVertical);
		this.editorScroll.setCorner(JScrollPane.UPPER_LEFT_CORNER, this.editorComponent.getGridButton());
		this.jPanel2.add(this.editorScroll, BorderLayout.CENTER);
		
		JScrollPane scrollAnimTiles = new JScrollPane(new AnimatedTileList(editorComponent));
		scrollAnimTiles.setBorder(null);
		this.panelAnimatedTiles.add(scrollAnimTiles, BorderLayout.CENTER);
	}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupMouseMode = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        textFieldName = new javax.swing.JTextField();
        toggleButtonPaint = new javax.swing.JToggleButton();
        toggleButtonSelect = new javax.swing.JToggleButton();
        jLabel2 = new javax.swing.JLabel();
        textFieldRows = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        textFieldCols = new javax.swing.JTextField();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        panelAnimatedTiles = new javax.swing.JPanel();
        buttonAddAnimatedTile = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jLabel1.setText("Tiled Layer:");

        textFieldName.setEditable(false);
        textFieldName.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        textFieldName.setText(this.tiledLayer.getName());
        textFieldName.setBorder(null);

        buttonGroupMouseMode.add(toggleButtonPaint);
        toggleButtonPaint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/vmd/game/editor/tiledlayer/res/layered_pane_16.png"))); // NOI18N
        toggleButtonPaint.setToolTipText("Paint mode");
        toggleButtonPaint.setBorder(null);
        toggleButtonPaint.setBorderPainted(false);
        toggleButtonPaint.setRolloverEnabled(true);

        buttonGroupMouseMode.add(toggleButtonSelect);
        toggleButtonSelect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/vmd/game/editor/tiledlayer/res/selection_mode.png"))); // NOI18N
        toggleButtonSelect.setToolTipText("Selection mode");
        toggleButtonSelect.setBorder(null);
        toggleButtonSelect.setBorderPainted(false);
        toggleButtonSelect.setRolloverEnabled(true);

        jLabel2.setText("Rows:");

        textFieldRows.setEditable(false);
        textFieldRows.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        textFieldRows.setText(Integer.toString(this.tiledLayer.getRowCount()));
        textFieldRows.setBorder(null);

        jLabel3.setText("Cols:");

        textFieldCols.setEditable(false);
        textFieldCols.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        textFieldCols.setText(Integer.toString(this.tiledLayer.getColumnCount()));
        textFieldCols.setBorder(null);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(toggleButtonPaint, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(toggleButtonSelect, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(textFieldName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
                .add(36, 36, 36)
                .add(jLabel2)
                .add(6, 6, 6)
                .add(textFieldRows, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 41, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel3)
                .add(6, 6, 6)
                .add(textFieldCols, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 41, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1Layout.linkSize(new java.awt.Component[] {toggleButtonPaint, toggleButtonSelect}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(5, 5, 5)
                .add(textFieldRows, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(jPanel1Layout.createSequentialGroup()
                .add(5, 5, 5)
                .add(jLabel2))
            .add(jPanel1Layout.createSequentialGroup()
                .add(5, 5, 5)
                .add(jLabel3))
            .add(jPanel1Layout.createSequentialGroup()
                .add(5, 5, 5)
                .add(textFieldCols, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(jPanel1Layout.createSequentialGroup()
                .add(5, 5, 5)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, textFieldName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel1)))
            .add(toggleButtonSelect, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(toggleButtonPaint, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        add(jPanel1, java.awt.BorderLayout.NORTH);

        jSplitPane1.setBorder(null);
        jSplitPane1.setDividerSize(5);
        jSplitPane1.setResizeWeight(1.0);
        jSplitPane1.setContinuousLayout(true);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setToolTipText("Paint mode");
        jPanel2.setPreferredSize(new java.awt.Dimension(10000, 10000));
        jPanel2.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setLeftComponent(jPanel2);

        panelAnimatedTiles.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panelAnimatedTiles.setLayout(new java.awt.BorderLayout());

        buttonAddAnimatedTile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/vmd/game/editor/tiledlayer/res/new_animated_tile_16.png"))); // NOI18N
        buttonAddAnimatedTile.setText("New Animated Tile");
        buttonAddAnimatedTile.setToolTipText("Create new Animated Tile");
        panelAnimatedTiles.add(buttonAddAnimatedTile, java.awt.BorderLayout.NORTH);

        jSplitPane1.setRightComponent(panelAnimatedTiles);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton buttonAddAnimatedTile;
    public javax.swing.ButtonGroup buttonGroupMouseMode;
    public javax.swing.JLabel jLabel1;
    public javax.swing.JLabel jLabel2;
    public javax.swing.JLabel jLabel3;
    public javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    public javax.swing.JSplitPane jSplitPane1;
    public javax.swing.JPanel panelAnimatedTiles;
    public javax.swing.JTextField textFieldCols;
    public javax.swing.JTextField textFieldName;
    public javax.swing.JTextField textFieldRows;
    public javax.swing.JToggleButton toggleButtonPaint;
    public javax.swing.JToggleButton toggleButtonSelect;
    // End of variables declaration//GEN-END:variables
	
	public void setPaintTile(Tile tile) {
		this.editorComponent.setPaintTileIndex(tile.getIndex());
	}
	
    public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == this.tiledLayer) {
			if (evt.getPropertyName().equals(Layer.PROPERTY_LAYER_NAME)) {
				textFieldName.setText(this.tiledLayer.getName());
			}
		}
    }

    public void updateTextLabels() {
		textFieldRows.setText(Integer.toString(this.tiledLayer.getRowCount()));
		textFieldCols.setText(Integer.toString(this.tiledLayer.getColumnCount()));
    }

    public void tileChanged(TiledLayer t, int row, int col) {
    }

    public void tilesChanged(TiledLayer t, Set positions) {
    }

    public void columnsInserted(TiledLayer t, int index, int count) {
		this.updateTextLabels();
    }

    public void columnsRemoved(TiledLayer t, int index, int count) {
		this.updateTextLabels();
    }

    public void rowsInserted(TiledLayer t, int index, int count) {
		this.updateTextLabels();
    }

    public void rowsRemoved(TiledLayer t, int index, int count) {
		this.updateTextLabels();
    }

}
