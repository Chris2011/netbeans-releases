/*
 * DataSetDatabindingElement.java
 *
 * Created on June 3, 2008, 4:20 PM
 */
package org.netbeans.modules.vmd.midp.propertyeditors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataListener;
import org.netbeans.modules.vmd.api.model.DesignComponent;
import org.netbeans.modules.vmd.api.model.DesignDocument;
import org.netbeans.modules.vmd.api.model.PropertyValue;
import org.netbeans.modules.vmd.api.model.TypeID;
import org.netbeans.modules.vmd.api.properties.DesignPropertyEditor;
import org.netbeans.modules.vmd.midp.components.MidpDocumentSupport;
import org.netbeans.modules.vmd.midp.components.MidpTypes;
import org.netbeans.modules.vmd.midp.components.categories.CommandsCategoryCD;
import org.netbeans.modules.vmd.midp.components.categories.DatabindingCategoryCD;
import org.netbeans.modules.vmd.midp.components.databinding.DataSetConnectorCD;
import org.netbeans.modules.vmd.midp.components.databinding.MidpDatabindingSupport;
import org.netbeans.modules.vmd.midp.components.general.ClassCD;

/**
 *
 * @author Karol Harezlak
 */
class DatabindingElementUI extends javax.swing.JPanel {

    private static String NULL = "<null>"; //TODO Localized
    private DesignPropertyEditor propertyEditor;

    /** Creates new form DataSetDatabindingElement */
    DatabindingElementUI(DesignPropertyEditor propertyEditor) {
        this.propertyEditor = propertyEditor;
      
        initComponents();
        jTextFieldExpression.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                jLabelPreview.setText(jComboBoxDatasets.getSelectedItem().toString()+"."+ jTextFieldExpression.getText());
            }
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jComboBoxDatasets = new javax.swing.JComboBox();
        jTextFieldExpression = new javax.swing.JTextField();
        jLabelPreview = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jComboBoxCommands = new javax.swing.JComboBox();

        setMaximumSize(new java.awt.Dimension(0, 0));
        setMinimumSize(new java.awt.Dimension(100, 100));
        setPreferredSize(new java.awt.Dimension(400, 200));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(DatabindingElementUI.class, "DatabindingElementUI.jPanel1.border.title"))); // NOI18N

        jLabel1.setText(org.openide.util.NbBundle.getMessage(DatabindingElementUI.class, "DatabindingElementUI.jLabel1.text")); // NOI18N

        jLabel2.setText(org.openide.util.NbBundle.getMessage(DatabindingElementUI.class, "DatabindingElementUI.jLabel2.text")); // NOI18N

        jLabel3.setText(org.openide.util.NbBundle.getMessage(DatabindingElementUI.class, "DatabindingElementUI.jLabel3.text")); // NOI18N

        jComboBoxDatasets.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jTextFieldExpression.setText(org.openide.util.NbBundle.getMessage(DatabindingElementUI.class, "DatabindingElementUI.jTextFieldExpression.text")); // NOI18N

        jLabelPreview.setText(org.openide.util.NbBundle.getMessage(DatabindingElementUI.class, "DatabindingElementUI.jLabelPreview.text_1")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabelPreview, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
                    .add(jTextFieldExpression, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
                    .add(jComboBoxDatasets, 0, 292, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jComboBoxDatasets, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(jTextFieldExpression, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(jLabelPreview)))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Dataset Property"));

        jLabel8.setText(org.openide.util.NbBundle.getMessage(DatabindingElementUI.class, "DatabindingElementUI.jLabel8.text")); // NOI18N

        jLabel5.setText(org.openide.util.NbBundle.getMessage(DatabindingElementUI.class, "DatabindingElementUI.jLabel5.text")); // NOI18N

        jComboBoxCommands.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(67, 67, 67)
                .add(jLabel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE))
            .add(jPanel2Layout.createSequentialGroup()
                .add(jLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 77, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(jComboBoxCommands, 0, 293, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(jComboBoxCommands, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(41, 41, 41)
                .add(jLabel8))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 56, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents



    void updateComponent(final DesignComponent component,final JRadioButton radiobutton) {
        component.getDocument().getTransactionManager().readAccess(new Runnable() {

            public void run() {
                final DesignDocument document = component.getDocument();

                if (document == null) {
                    return;
                }
                jComboBoxDatasets.setModel(new Model(component, DatabindingCategoryCD.TYPEID));
                jComboBoxCommands.setModel(new Model(component, CommandsCategoryCD.TYPEID));
                DesignComponent connector = MidpDatabindingSupport.getConnector(component, propertyEditor.getPropertyNames().get(0));
                if (connector != null) {
                    radiobutton.setSelected(true);
                    jComboBoxDatasets.setSelectedItem(connector.getParentComponent().readProperty(ClassCD.PROP_INSTANCE_NAME).getPrimitiveValue());
                    jTextFieldExpression.setText((String) connector.readProperty(DataSetConnectorCD.PROP_EXPRESSION).getPrimitiveValue());
                    DesignComponent command = connector.readProperty(DataSetConnectorCD.PROP_UPDATE_COMMAND).getComponent();
                    if (command != null) {
                        String commandName = (String) command.readProperty(ClassCD.PROP_INSTANCE_NAME).getPrimitiveValue();
                        jComboBoxCommands.setSelectedItem(commandName);
                    }
                    jLabelPreview.setText(jComboBoxDatasets.getSelectedItem().toString()+"."+jTextFieldExpression.getText());
                }
            }
        });
    }

    void saveToModel(final DesignComponent component) {
        final DesignDocument document = component.getDocument();
        document.getTransactionManager().writeAccess(new Runnable() {

            public void run() {
                DesignComponent connector = MidpDatabindingSupport.getConnector(component, propertyEditor.getPropertyNames().get(0));
                String selectedDataSet = (String) jComboBoxDatasets.getSelectedItem();
                String selectedUpdateCommand = (String) jComboBoxCommands.getSelectedItem();
                Collection<DesignComponent> dataSets = MidpDocumentSupport.getCategoryComponent(document, DatabindingCategoryCD.TYPEID).getComponents();
                for (DesignComponent dataSet : dataSets) {
                    if (dataSet.readProperty(ClassCD.PROP_INSTANCE_NAME).getPrimitiveValue().equals(selectedDataSet)) {
                        if (connector == null) {
                            connector = document.createComponent(DataSetConnectorCD.TYPEID);
                            connector.writeProperty(DataSetConnectorCD.PROP_BINDED_PROPERTY, MidpTypes.createStringValue(propertyEditor.getPropertyNames().get(0)));
                            dataSet.addComponent(connector);
                        }
                        connector.writeProperty(DataSetConnectorCD.PROP_COMPONENT_ID, MidpTypes.createLongValue(component.getComponentID()));
                        connector.writeProperty(DataSetConnectorCD.PROP_EXPRESSION, MidpTypes.createStringValue(jTextFieldExpression.getText())); //NOI18N
                        if (selectedUpdateCommand != null && !selectedDataSet.equalsIgnoreCase(NULL)) {
                            Collection<DesignComponent> commands = MidpDocumentSupport.getCategoryComponent(document, CommandsCategoryCD.TYPEID).getComponents();
                            for (DesignComponent command : commands) {
                                if (command.readProperty(ClassCD.PROP_INSTANCE_NAME).getPrimitiveValue().equals(selectedUpdateCommand)) {
                                    connector.writeProperty(DataSetConnectorCD.PROP_UPDATE_COMMAND, PropertyValue.createComponentReference(command));
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
            }
        });
    }

    void resetValuesInModel(final DesignComponent component) {
        final DesignDocument document = component.getDocument();
        document.getTransactionManager().writeAccess(new Runnable() {

            public void run() {
                DesignComponent connector = MidpDatabindingSupport.getConnector(component, propertyEditor.getPropertyNames().get(0));
                if (connector != null) {
                    document.deleteComponent(connector);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBoxCommands;
    private javax.swing.JComboBox jComboBoxDatasets;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabelPreview;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jTextFieldExpression;
    // End of variables declaration//GEN-END:variables

    

    private void enableComponents(String name) {
        jComboBoxDatasets.setSelectedItem(name);
        //jLabelExpression.setText(name + "."); //NOI18N

        jTextFieldExpression.setEnabled(true);
    }

    private void disableComponents() {
        jComboBoxDatasets.setSelectedItem(null);
        jTextFieldExpression.setText(null);
        //jLabelExpression.setText(NULL + "."); //NOI18N

        jTextFieldExpression.setEnabled(false);
    }

    private void safeRepaint() {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                DatabindingElementUI.this.repaint();
            }
        });
    }

    private class Model implements ComboBoxModel {

        private final List<String> names;
        private WeakReference compRef;
        private String selectedItem;
        private TypeID categoryType;

        Model(DesignComponent component, TypeID categoryType) {
            this.categoryType = categoryType;
            this.compRef = new WeakReference(component);
            this.names = new ArrayList<String>();
            this.names.add(NULL);
            Collection<DesignComponent> components = MidpDocumentSupport.getCategoryComponent(component.getDocument(), categoryType).getComponents();
            for (DesignComponent c : components) {
                String name = (String) c.readProperty(ClassCD.PROP_INSTANCE_NAME).getPrimitiveValue();
                if (name != null || !name.trim().equals("")) { //NOI18N
                    names.add(name);
                }
            }
            
        }

        public void setSelectedItem(final Object item) {
            final DesignComponent component = (DesignComponent) compRef.get();
            if (component == null) {
                return;
            }
            if (item instanceof String) {
                component.getDocument().getTransactionManager().readAccess(new Runnable() {

                    public void run() {
                        String n = (String) item;
                        Collection<DesignComponent> components = MidpDocumentSupport.getCategoryComponent(component.getDocument(), categoryType).getComponents();
                        for (DesignComponent c : components) {
                            if (c.readProperty(ClassCD.PROP_INSTANCE_NAME).getPrimitiveValue().equals(n) || n.equals(NULL)) {
                                selectedItem = (String) item;
                                break;
                            }
                        }
                    }
                });
            } else if (item == null) {
                this.selectedItem = NULL;
            } else {
                throw new IllegalArgumentException("Setting argumant is not String type"); //NOI18N

            }
        }

        public Object getSelectedItem() {
            return this.selectedItem;

        }

        public int getSize() {
            return names.size();
        }

        public Object getElementAt(int index) {
            return names.get(index);
        }

        public void addListDataListener(ListDataListener l) {
        }

        public void removeListDataListener(ListDataListener l) {
        }
    }
}
