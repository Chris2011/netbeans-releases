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

package org.netbeans.modules.vmd.game.nbdialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.modules.vmd.game.dialog.AbstractImagePreviewComponent;
import org.netbeans.modules.vmd.game.dialog.FullImageGridPreview;
import org.netbeans.modules.vmd.game.dialog.PartialImageGridPreview;
import org.netbeans.modules.vmd.game.model.CodeUtils;
import org.netbeans.modules.vmd.game.model.GlobalRepository;
import org.netbeans.modules.vmd.game.model.ImageResource;
import org.netbeans.modules.vmd.game.model.Scene;
import org.netbeans.modules.vmd.midp.components.MidpProjectSupport;
import org.openide.DialogDescriptor;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author  kherink
 */
public class SpriteDialog extends javax.swing.JPanel implements ActionListener {
	
    private GlobalRepository gameDesign;
	
    private static final Icon ICON_ERROR = ImageUtilities.loadImageIcon("org/netbeans/modules/vmd/midp/resources/error.gif", false); // NOI18N
	
    private static final int DEFAULT_FRAMES = 5;
    private static final int DEFAULT_TILE_WIDTH = 18;
    private static final int DEFAULT_TILE_HEIGHT = 18;

    /** Creates new form NewTiledLayerDialog */
    public SpriteDialog(GlobalRepository gameDesign) {
        this.gameDesign = gameDesign;
        initComponents();
        init();
    }

    public SpriteDialog(Scene parent) {
        this(parent.getGameDesign());
        this.scene = parent;
    }

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupLayers = new javax.swing.ButtonGroup();
        panelCustomizer = new javax.swing.JPanel();
        labelImageFile = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listImageFileName = new javax.swing.JList();
        buttonImportImages = new javax.swing.JButton();
        panelPreview = new javax.swing.JPanel();
        labelImagePreview = new javax.swing.JLabel();
        panelImage = new javax.swing.JPanel();
        sliderWidth = new javax.swing.JSlider();
        sliderHeight = new javax.swing.JSlider();
        labelTileWidth = new javax.swing.JLabel();
        labelTileHeight = new javax.swing.JLabel();
        checkBoxZoom = new javax.swing.JCheckBox();
        panelError = new javax.swing.JPanel();
        labelError = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        panelLayerInfo = new javax.swing.JPanel();
        labelLayerName = new javax.swing.JLabel();
        fieldLayerName = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();

        labelImageFile.setLabelFor(listImageFileName);
        org.openide.awt.Mnemonics.setLocalizedText(labelImageFile, org.openide.util.NbBundle.getMessage(SpriteDialog.class, "SpriteDialog.labelSelectImage.txt")); // NOI18N

        listImageFileName.setModel(this.getImageListModel());
        listImageFileName.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(listImageFileName);

        org.openide.awt.Mnemonics.setLocalizedText(buttonImportImages, org.openide.util.NbBundle.getMessage(SpriteDialog.class, "SpriteDialog.buttonImportImages.txt")); // NOI18N

        javax.swing.GroupLayout panelCustomizerLayout = new javax.swing.GroupLayout(panelCustomizer);
        panelCustomizer.setLayout(panelCustomizerLayout);
        panelCustomizerLayout.setHorizontalGroup(
            panelCustomizerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCustomizerLayout.createSequentialGroup()
                .addGroup(panelCustomizerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelImageFile)
                    .addComponent(buttonImportImages)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelCustomizerLayout.setVerticalGroup(
            panelCustomizerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCustomizerLayout.createSequentialGroup()
                .addComponent(labelImageFile)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonImportImages))
        );

        labelImageFile.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SpriteDialog.class, "SpriteDialog.labelSelectImage.accessible.name")); // NOI18N
        labelImageFile.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SpriteDialog.class, "SpriteDialog.labelSelectImage.accessible.description")); // NOI18N
        buttonImportImages.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SpriteDialog.class, "SpriteDialog.buttonImportImages.accessible.name")); // NOI18N
        buttonImportImages.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SpriteDialog.class, "SpriteDialog.buttonImportImages.accessible.description")); // NOI18N

        labelImagePreview.setLabelFor(panelImage);
        labelImagePreview.setText(org.openide.util.NbBundle.getMessage(SpriteDialog.class, "SpriteDialog.labelAdjustTileSize.txt")); // NOI18N

        panelImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        panelImage.setLayout(new java.awt.BorderLayout());

        sliderHeight.setOrientation(javax.swing.JSlider.VERTICAL);

        labelTileWidth.setText(org.openide.util.NbBundle.getMessage(SpriteDialog.class, "SpriteDialog.labelTilewidth.txt", new Object[] {0})); // NOI18N

        labelTileHeight.setText(org.openide.util.NbBundle.getMessage(SpriteDialog.class, "SpriteDialog.labelTileheight.txt", new Object[] {0 // NOI18N
        }));

        org.openide.awt.Mnemonics.setLocalizedText(checkBoxZoom, org.openide.util.NbBundle.getMessage(SpriteDialog.class, "SpriteDialog.labelZoom.txt")); // NOI18N
        checkBoxZoom.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        checkBoxZoom.setMargin(new java.awt.Insets(0, 0, 0, 0));

        javax.swing.GroupLayout panelPreviewLayout = new javax.swing.GroupLayout(panelPreview);
        panelPreview.setLayout(panelPreviewLayout);
        panelPreviewLayout.setHorizontalGroup(
            panelPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPreviewLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPreviewLayout.createSequentialGroup()
                        .addComponent(labelTileWidth)
                        .addGap(40, 40, 40)
                        .addComponent(labelTileHeight)
                        .addContainerGap(217, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPreviewLayout.createSequentialGroup()
                        .addGroup(panelPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelPreviewLayout.createSequentialGroup()
                                .addComponent(labelImagePreview)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 262, Short.MAX_VALUE)
                                .addComponent(checkBoxZoom))
                            .addComponent(panelImage, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
                            .addComponent(sliderWidth, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sliderHeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        panelPreviewLayout.setVerticalGroup(
            panelPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPreviewLayout.createSequentialGroup()
                .addGroup(panelPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelImagePreview)
                    .addComponent(checkBoxZoom))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sliderHeight, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                    .addComponent(panelImage, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sliderWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelTileHeight)
                    .addComponent(labelTileWidth, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        labelImagePreview.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SpriteDialog.class, "SpriteDialog.labelAdjustTileSize.accessible.name")); // NOI18N
        labelImagePreview.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SpriteDialog.class, "SpriteDialog.labelAdjustTileSize.accessible.description")); // NOI18N
        sliderWidth.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SpriteDialog.class, "SpriteDialog.sliderWidth.accessible.name")); // NOI18N
        sliderWidth.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SpriteDialog.class, "SpriteDialog.sliderWidth.accessible.description")); // NOI18N
        sliderHeight.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SpriteDialog.class, "SpriteDialog.sliderHeight.accessible.name")); // NOI18N
        sliderHeight.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SpriteDialog.class, "SpriteDialog.sliderHeight.accessible.description")); // NOI18N
        checkBoxZoom.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SpriteDialog.class, "SpriteDialog.labelZoom.accessible.name")); // NOI18N
        checkBoxZoom.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SpriteDialog.class, "SpriteDialog.labelZoom.accessible.description")); // NOI18N

        labelError.setForeground(new java.awt.Color(255, 0, 0));

        javax.swing.GroupLayout panelErrorLayout = new javax.swing.GroupLayout(panelError);
        panelError.setLayout(panelErrorLayout);
        panelErrorLayout.setHorizontalGroup(
            panelErrorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labelError, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 787, Short.MAX_VALUE)
        );
        panelErrorLayout.setVerticalGroup(
            panelErrorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelErrorLayout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addComponent(labelError, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        labelLayerName.setLabelFor(fieldLayerName);
        org.openide.awt.Mnemonics.setLocalizedText(labelLayerName, org.openide.util.NbBundle.getMessage(SpriteDialog.class, "SpriteDialog.labelSpriteName.txt")); // NOI18N

        javax.swing.GroupLayout panelLayerInfoLayout = new javax.swing.GroupLayout(panelLayerInfo);
        panelLayerInfo.setLayout(panelLayerInfoLayout);
        panelLayerInfoLayout.setHorizontalGroup(
            panelLayerInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayerInfoLayout.createSequentialGroup()
                .addComponent(labelLayerName, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fieldLayerName, javax.swing.GroupLayout.DEFAULT_SIZE, 689, Short.MAX_VALUE))
        );
        panelLayerInfoLayout.setVerticalGroup(
            panelLayerInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayerInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLayerInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelLayerName)
                    .addComponent(fieldLayerName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        labelLayerName.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SpriteDialog.class, "SpriteDialog.labelSpriteName.accessible.name")); // NOI18N
        labelLayerName.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SpriteDialog.class, "SpriteDialog.labelSpriteName.accessible.description")); // NOI18N

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelLayerInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelError, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 787, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelCustomizer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelPreview, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelLayerInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelPreview, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                    .addComponent(panelCustomizer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelError, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupLayers;
    private javax.swing.JButton buttonImportImages;
    private javax.swing.JCheckBox checkBoxZoom;
    private javax.swing.JTextField fieldLayerName;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel labelError;
    private javax.swing.JLabel labelImageFile;
    private javax.swing.JLabel labelImagePreview;
    private javax.swing.JLabel labelLayerName;
    private javax.swing.JLabel labelTileHeight;
    private javax.swing.JLabel labelTileWidth;
    private javax.swing.JList listImageFileName;
    private javax.swing.JPanel panelCustomizer;
    private javax.swing.JPanel panelError;
    private javax.swing.JPanel panelImage;
    private javax.swing.JPanel panelLayerInfo;
    private javax.swing.JPanel panelPreview;
    private javax.swing.JSlider sliderHeight;
    private javax.swing.JSlider sliderWidth;
    // End of variables declaration//GEN-END:variables
	
	
    public static final boolean DEBUG = false;
    
    private DialogDescriptor dd;
    private SliderListener sliderListener = new SliderListener();
    private AbstractImagePreviewComponent imagePreview;
    private PartialImageGridPreview partialImagePreview = new PartialImageGridPreview();
    private FullImageGridPreview fullImagePreview = new FullImageGridPreview();
    private Scene scene;
    private List<Integer> tileWidths;
    private List<Integer> tileHeigths;
	
	
    public void setDialogDescriptor(DialogDescriptor dd) {
        this.dd = dd;
    }
	
    private void init() {
        HelpCtx.setHelpIDString(this, "org.netbeans.modules.vmd.game.nbdialog.SpriteDialog");
        this.getAccessibleContext().setAccessibleName(
                NbBundle.getMessage(TiledLayerDialog.class, "SpriteDialog.accessible.name"));
        this.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(TiledLayerDialog.class, "SpriteDialog.accessible.description"));

        this.labelError.setIcon(ICON_ERROR);

        SpinnerNumberModel snm = new SpinnerNumberModel();
        snm.setMinimum(1);
        snm.setMaximum(256);
        snm.setStepSize(1);
        snm.setValue(5);

        this.fieldLayerName.getDocument().addDocumentListener(new LayerFieldListener());
        this.fieldLayerName.addFocusListener(new LayerFieldListener());
		
        this.listImageFileName.addListSelectionListener(new ImageListListener());
        this.listImageFileName.setCellRenderer(new DefaultListCellRenderer() {

            public Component getListCellRendererComponent(JList src, Object value, 
                    int index, boolean isSelected, boolean hasfocus) {
                Map.Entry<FileObject, String> entry = (Map.Entry<FileObject, String>) value;
                return super.getListCellRendererComponent(src, entry.getValue(), index, isSelected, hasfocus);
            }
        });
		
        this.sliderWidth.setModel(new DefaultBoundedRangeModel());
        this.sliderHeight.setModel(new DefaultBoundedRangeModel());

        this.sliderWidth.addChangeListener(sliderListener);
        this.sliderHeight.addChangeListener(sliderListener);

        this.sliderWidth.setValue(0);
        this.sliderHeight.setValue(0);

        this.sliderWidth.setPaintLabels(true);
        this.sliderHeight.setPaintLabels(true);

        this.sliderWidth.setSnapToTicks(true);
        this.sliderHeight.setSnapToTicks(true);

        this.sliderWidth.setEnabled(false);
        this.sliderHeight.setEnabled(false);
        this.checkBoxZoom.setEnabled(false);

        this.buttonImportImages.addActionListener(this);

        this.setPreviewFull();
		
        this.checkBoxZoom.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (checkBoxZoom.isSelected()) {
                    setPreviewPartial();
                } else {
                    setPreviewFull();
                }
            }
        });
    }
	
    private void setPreviewPartial() {
        if (this.imagePreview != null) {
            try {
                if (DEBUG) {
                    System.out.println("setPreviewPartial"); // NOI18N

                }
                this.partialImagePreview.setImageURL(this.imagePreview.getImageURL());
            } catch (MalformedURLException e) {
                this.labelError.setText(NbBundle.getMessage(SpriteDialog.class, "SpriteDialog.labelInvalidImgLoc.txt"));
                e.printStackTrace();
                return;
            } catch (IllegalArgumentException iae) {
                this.labelError.setText(NbBundle.getMessage(SpriteDialog.class, "SpriteDialog.labelInvalidImgFomat.txt"));
                iae.printStackTrace();
                return;
            }
            this.partialImagePreview.setTileWidth(this.imagePreview.getTileWidth());
            this.partialImagePreview.setTileHeight(this.imagePreview.getTileHeight());
        }
        this.panelImage.removeAll();
        this.panelImage.add(this.partialImagePreview, BorderLayout.CENTER);
        this.imagePreview = this.partialImagePreview;
        this.repaint();
        this.validate();
    }
	
    private void setPreviewFull() {
        if (this.imagePreview != null) {
            try {
                if (DEBUG) {
                    System.out.println("setPreviewFull"); // NOI18N

                }
                this.fullImagePreview.setImageURL(this.imagePreview.getImageURL());
            } catch (MalformedURLException e) {
                this.labelError.setText(NbBundle.getMessage(SpriteDialog.class, "SpriteDialog.labelInvalidImgLoc.txt"));
                e.printStackTrace();
                return;
            } catch (IllegalArgumentException iae) {
                this.labelError.setText(NbBundle.getMessage(SpriteDialog.class, "SpriteDialog.labelInvalidImgFomat.txt"));
                iae.printStackTrace();
                return;
            }
            this.fullImagePreview.setTileWidth(this.imagePreview.getTileWidth());
            this.fullImagePreview.setTileHeight(this.imagePreview.getTileHeight());
        }
        this.panelImage.removeAll();
        JScrollPane scroll = new JScrollPane(this.fullImagePreview);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        this.panelImage.add(scroll, BorderLayout.CENTER);
        this.imagePreview = this.fullImagePreview;
        this.repaint();
        this.validate();
    }
			
	
    private List<Map.Entry<FileObject, String>> getImageList() {
        // get all PNG images (Note: only PNG)
        Map<FileObject, String> imgMap = MidpProjectSupport.
                getImagesForProject(this.gameDesign.getDesignDocument(), true);
        List<Map.Entry<FileObject, String>> list = new ArrayList<Map.Entry<FileObject, String>>();
        list.addAll(imgMap.entrySet());
        Collections.sort(list, new Comparator() {
            public int compare(Object a, Object b) {
                Map.Entry<FileObject, String> ea = (Map.Entry<FileObject, String>) a;
                Map.Entry<FileObject, String> eb = (Map.Entry<FileObject, String>) b;
                return ea.getValue().compareTo(eb.getValue());
            }
        });
        return list;
    }
	
    private DefaultListModel getImageListModel() {
        DefaultListModel dlm = new DefaultListModel();
        List<Map.Entry<FileObject, String>> images = this.getImageList();
        for (Map.Entry<FileObject, String> imageEntry : images) {
            dlm.addElement(imageEntry);
        }
        return dlm;
    }
	
    private class SliderListener implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            //System.out.println("Slider state changed");
            int tileWidth = SpriteDialog.this.tileWidths.get(
                    ((Integer) SpriteDialog.this.sliderWidth.getValue()).intValue());
            int tileHeight = SpriteDialog.this.tileHeigths.get(
                    ((Integer) SpriteDialog.this.sliderHeight.getValue()).intValue());

            if (e.getSource() == SpriteDialog.this.sliderHeight) {
                SpriteDialog.this.imagePreview.setTileHeight(tileHeight);
                SpriteDialog.this.labelTileHeight.setText(NbBundle.getMessage(
                        SpriteDialog.class, "SpriteDialog.labelTileheight.txt", tileHeight));
            } else if (e.getSource() == SpriteDialog.this.sliderWidth) {
                SpriteDialog.this.imagePreview.setTileWidth(tileWidth);
                SpriteDialog.this.labelTileWidth.setText(NbBundle.getMessage(
                        SpriteDialog.class, "SpriteDialog.labelTilewidth.txt", tileWidth));
            } else {
                if (DEBUG) {
                    System.out.println("ERR: ChangeEvent came from " + e.getSource()); // NOI18N
                }
            }
        }
    }
	
    private class LayerFieldListener implements DocumentListener, FocusListener {

        public void insertUpdate(DocumentEvent e) {
            this.handleTextContentChange(e);
        }

        public void removeUpdate(DocumentEvent e) {
            this.handleTextContentChange(e);
        }

        public void changedUpdate(DocumentEvent e) {
            this.handleTextContentChange(e);
        }

        private void handleTextContentChange(DocumentEvent e) {
            String err = getFieldLayerNameError();
            if (e.getDocument() == SpriteDialog.this.fieldLayerName.getDocument()) {
                if (err == null) {
                    err = getFieldImageFileNameError();
                }
                SpriteDialog.this.labelError.setText(err);
            }
            if (err == null) {
                SpriteDialog.this.setOKButtonEnabled(true);
            } else {
                SpriteDialog.this.setOKButtonEnabled(false);
            }
        }

        public void focusGained(FocusEvent e) {
            if (e.getComponent() == SpriteDialog.this.fieldLayerName) {
                SpriteDialog.this.labelError.setText(getFieldLayerNameError());
            }
            if (getFieldLayerNameError() == null && getFieldImageFileNameError() == null) {
                SpriteDialog.this.setOKButtonEnabled(true);
            } else {
                SpriteDialog.this.setOKButtonEnabled(false);
            }
        }

        public void focusLost(FocusEvent e) {
        }
    }
	
    private String getFieldLayerNameError() {
        String illegalIdentifierName = NbBundle.getMessage(SpriteDialog.class, "SpriteDialog.labelInvalidName.txt");
        String errMsg = null;
        String layerName = this.fieldLayerName.getText();
        if (layerName.equals("")) {
            errMsg = NbBundle.getMessage(SpriteDialog.class, "SpriteDialog.labelEnterName.txt");
        } else if (!this.gameDesign.isComponentNameAvailable(layerName)) {
            errMsg = NbBundle.getMessage(SpriteDialog.class, "SpriteDialog.labelNameExists.txt");
        } else if (!isValidJavaIdentifier(layerName)) {
            errMsg = illegalIdentifierName;
        }
        return errMsg;
    }
	
    private static boolean isValidJavaIdentifier(String str) {
        if (!Character.isJavaIdentifierStart(str.charAt(0))) {
            return false;
        }
        for (int i = 1; i < str.length(); i++) {
            if (!Character.isJavaIdentifierPart(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
	
	
    public void setOKButtonEnabled(boolean enable) {
        if (!enable) {
            this.labelError.setIcon(ICON_ERROR);
        } else {
            this.labelError.setIcon(null);
        }
        this.dd.setValid(enable);
    }
	
    private String getFieldImageFileNameError() {
        String errMsg = null;
        if (this.listImageFileName.getModel().getSize() == 0) {
            errMsg = NbBundle.getMessage(SpriteDialog.class, "SpriteDialog.labelNoImages.txt");
        } else if (this.listImageFileName.getSelectedValue() == null) {
            errMsg = NbBundle.getMessage(SpriteDialog.class, "SpriteDialog.labelSelectImgFile.txt");
        } else {
            Map.Entry<FileObject, String> entry = 
                    (Map.Entry<FileObject, String>) this.listImageFileName.getSelectedValue();
            URL imageURL = null;
            try {
                imageURL = entry.getKey().getURL();
            } catch (Exception e) {
                e.printStackTrace();
            }
            String relativeResourcePath = entry.getValue();

            assert (imageURL != null);
            assert (relativeResourcePath != null);

            String imgName = CodeUtils.getIdealImageName(relativeResourcePath);

            List<String> derivedImageNames = GlobalRepository.deriveUsedNames(imgName);
            for (String derivedName : derivedImageNames) {
                if (derivedName.equals(this.fieldLayerName.getText())) {
                    errMsg = NbBundle.getMessage(SpriteDialog.class, "SpriteDialog.imgFileSameAsLayerName.txt");
                }
            }

        }
        return errMsg;
    }
	
    private class ImageListListener implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            SpriteDialog.this.handleImageStateChange();
        }
    }

    private void handleImageStateChange() {
        String errMsg = null;

        boolean imgSelected = !listImageFileName.isSelectionEmpty();

        this.sliderWidth.setEnabled(imgSelected);
        this.sliderHeight.setEnabled(imgSelected);
        this.checkBoxZoom.setEnabled(imgSelected);

        errMsg = SpriteDialog.this.getFieldLayerNameError();
        if (!imgSelected) {
            clearImagePreview();
        } else {
            try {
                SpriteDialog.this.loadImagePreview();
            } catch (MalformedURLException e) {
                errMsg = NbBundle.getMessage(SpriteDialog.class, "SpriteDialog.labelInvalidImgLoc.txt");
                e.printStackTrace();
            } catch (IllegalArgumentException iae) {
                errMsg = NbBundle.getMessage(SpriteDialog.class, "SpriteDialog.labelInvalidImgFomat.txt");
                iae.printStackTrace();
            }
        }

        if (errMsg == null) {
            errMsg = SpriteDialog.this.getFieldImageFileNameError();
        }

        if (errMsg != null) {
            SpriteDialog.this.labelError.setText(errMsg);
            SpriteDialog.this.setOKButtonEnabled(false);
        } else {
            SpriteDialog.this.labelError.setText("");
            SpriteDialog.this.setOKButtonEnabled(true);
        }
    }

    private void clearImagePreview() {
        this.sliderWidth.removeChangeListener(this.sliderListener);
        this.sliderHeight.removeChangeListener(this.sliderListener);

        this.sliderWidth.setModel(new DefaultBoundedRangeModel());
        this.sliderHeight.setModel(new DefaultBoundedRangeModel());

        this.sliderWidth.setValue(0);
        this.sliderHeight.setValue(0);

        this.sliderWidth.addChangeListener(sliderListener);
        this.sliderHeight.addChangeListener(sliderListener);
    }

    private void loadImagePreview() throws MalformedURLException, IllegalArgumentException {
        if (DEBUG) {
            System.out.println("load image preview"); // NOI18N

        }
        Map.Entry<FileObject, String> entry = 
                (Map.Entry<FileObject, String>) this.listImageFileName.getSelectedValue();
        URL imageURL = null;
        try {
            imageURL = entry.getKey().getURL();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //assert(imageURL != null);

        if (imageURL == null) {
            return;
        }

        this.sliderWidth.removeChangeListener(this.sliderListener);
        this.sliderHeight.removeChangeListener(this.sliderListener);

        this.imagePreview.setImageURL(imageURL);

        this.tileWidths = this.imagePreview.getValidTileWidths();
        this.tileHeigths = this.imagePreview.getValidTileHeights();

        DefaultBoundedRangeModel modelWidth = 
                new DefaultBoundedRangeModel(tileWidths.size() - 1, 0, 0, tileWidths.size() - 1);
        DefaultBoundedRangeModel modelHeight = 
                new DefaultBoundedRangeModel(tileHeigths.size() - 1, 0, 0, tileHeigths.size() - 1);
        this.sliderWidth.setModel(modelWidth);
        this.sliderHeight.setModel(modelHeight);

        this.sliderWidth.setValue(
                this.tileWidths.indexOf(getNearestValue(DEFAULT_TILE_WIDTH, tileWidths)));
        this.sliderHeight.setValue(
                this.tileHeigths.indexOf(getNearestValue(DEFAULT_TILE_HEIGHT, tileHeigths)));


        //set labels
        int tileWidth = this.tileWidths.get(((Integer) this.sliderWidth.getValue()).intValue());
        int tileHeight = this.tileHeigths.get(((Integer) this.sliderHeight.getValue()).intValue());

        this.labelTileHeight.setText(NbBundle.getMessage(
                SpriteDialog.class, "SpriteDialog.labelTileheight.txt", tileHeight));
        this.labelTileWidth.setText(NbBundle.getMessage(
                SpriteDialog.class, "SpriteDialog.labelTilewidth.txt", tileWidth));

        this.imagePreview.setTileWidth(tileWidth);
        this.imagePreview.setTileHeight(tileHeight);

        this.repaint();

        this.sliderWidth.addChangeListener(sliderListener);
        this.sliderHeight.addChangeListener(sliderListener);

    }
	
    private static int getNearestValue(int mark, List<Integer> values) {
        int nearest = Integer.MAX_VALUE;
        for (Integer value : values) {
            int nearestDiff = Math.abs(mark - nearest);
            int valueDiff = Math.abs(mark - value);
            if (valueDiff < nearestDiff || (valueDiff == nearestDiff && value > nearest)) {
                nearest = value;
            }
        }
        return nearest;
    }

    public void actionPerformed(ActionEvent e) {
        //if OK button pressed create the new layer
        if (e.getSource() == NotifyDescriptor.OK_OPTION) {
            this.handleOKButton();
        } else if (e.getSource() == this.buttonImportImages) {
            try {
                this.handleImportImagesButton();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void handleImportImagesButton() throws IOException {
        InputStream inImgPlatformTiles = SpriteDialog.class.getResourceAsStream("res/platform_tiles.png"); // NOI18N

        assert inImgPlatformTiles != null;
        InputStream inImgTopViewTiles = SpriteDialog.class.getResourceAsStream("res/topview_tiles.png"); // NOI18N

        assert inImgTopViewTiles != null;

        Project p = MidpProjectSupport.getProjectForDocument(this.gameDesign.getDesignDocument());
        SourceGroup sg = MidpProjectSupport.getSourceGroup(p);
        FileObject foSrc = sg.getRootFolder();

        OutputStream topViewOut = null;
        OutputStream platformOut = null;
        try {
            FileObject foPlatform = FileUtil.createData(foSrc, "platform_tiles.png"); // NOI18N

            FileObject foTop = FileUtil.createData(foSrc, "topview_tiles.png"); // NOI18N

            platformOut = foPlatform.getOutputStream();
            FileUtil.copy(inImgPlatformTiles, platformOut);
            topViewOut = foTop.getOutputStream();
            FileUtil.copy(inImgTopViewTiles, topViewOut);
        } finally {
            try {
                if (platformOut != null) {
                    platformOut.close();
                }
                if (topViewOut != null) {
                    topViewOut.close();
                }
            } catch (Exception ex) {
            }
        }
        this.listImageFileName.setModel(this.getImageListModel());
        handleImageStateChange();
    }
	
    private void handleOKButton() {
        String name = this.fieldLayerName.getText();

        int tileWidth = SpriteDialog.this.tileWidths.get(((Integer) SpriteDialog.this.sliderWidth.getValue()).intValue());
        int tileHeight = SpriteDialog.this.tileHeigths.get(((Integer) SpriteDialog.this.sliderHeight.getValue()).intValue());

        Map.Entry<FileObject, String> entry = (Map.Entry<FileObject, String>) this.listImageFileName.getSelectedValue();

        URL imageURL = null;
        try {
            imageURL = entry.getKey().getURL();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String relativeResourcePath = entry.getValue();

        assert (imageURL != null);
        assert (relativeResourcePath != null);

        ImageResource imgRes = this.gameDesign.getImageResource(imageURL, relativeResourcePath);

        if (this.scene != null) {
            this.scene.createSprite(name, imgRes, DEFAULT_FRAMES, tileWidth, tileHeight);
        } else {
            this.gameDesign.createSprite(name, imgRes, DEFAULT_FRAMES, tileWidth, tileHeight);
        }
    }

}

