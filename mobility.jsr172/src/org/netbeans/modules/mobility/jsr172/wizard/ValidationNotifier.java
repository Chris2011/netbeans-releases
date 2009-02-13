/*
 * ValidationNotifier.java
 *
 * Created on February 13, 2007, 7:04 PM
 */

package org.netbeans.modules.mobility.jsr172.wizard;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;
import org.netbeans.modules.e2e.api.wsdl.wsdl2java.WSDL2Java;
import org.netbeans.modules.e2e.api.wsdl.wsdl2java.WSDL2Java.ValidationResult.ErrorLevel;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author Michal Skvor
 */
public class ValidationNotifier extends javax.swing.JPanel {
    
    List<WSDL2Java.ValidationResult> validationResults;
    
    /** Creates new form ValidationNotifier */
    public ValidationNotifier( List<WSDL2Java.ValidationResult> validationResults ) {
        this.validationResults = validationResults;
        
        initComponents();
        
        initData(); 
        
        initAccessibility();
    }
    
    private void initData() {
        validationList.setModel( new IconListModel( validationResults ));
        validationList.setCellRenderer( new IconListRenderer());
    }
    
    private void initAccessibility() {
        getAccessibleContext().setAccessibleDescription( NbBundle.getMessage( ClientInfo.class, "ACSD_Validation_Results" ));
        
        validationList.getAccessibleContext().setAccessibleDescription( NbBundle.getMessage( ValidationNotifier.class, "ACSD_Validation_List" ));
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        validationList = new javax.swing.JList();

        jLabel1.setText(org.openide.util.NbBundle.getMessage(ValidationNotifier.class, "LBL_Validation_Results")); // NOI18N

        jScrollPane1.setViewportView(validationList);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
                    .add(jLabel1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList validationList;
    // End of variables declaration//GEN-END:variables
        
    private static final class IconListModel implements ListModel {

        private List<WSDL2Java.ValidationResult> validationResults;
        
        public IconListModel( List<WSDL2Java.ValidationResult> validationResults ) {
            this.validationResults = validationResults;
        }
    
        public int getSize() {
            return validationResults.size();
        }

        public Object getElementAt( int index ) {
            return validationResults.get( index );
        }

        public void addListDataListener(ListDataListener arg0) {
        }

        public void removeListDataListener(ListDataListener arg0) {
        }
    }
    
    private static final Icon ICON_WARNING  = ImageUtilities.loadImageIcon("org/netbeans/modules/mobility/jsr172/resources/warning.png", false);
    private static final Icon ICON_ERROR    = ImageUtilities.loadImageIcon("org/netbeans/modules/mobility/jsr172/resources/error.png", false);
    
    private static final class IconListRenderer implements ListCellRenderer {
    
        public Component getListCellRendererComponent(JList arg0, Object data,
            int arg2, boolean arg3, boolean arg4) 
        {
            WSDL2Java.ValidationResult rowData = (WSDL2Java.ValidationResult) data;
            JLabel row = new JLabel();
            row.setText( rowData.getMessage());
            
            if( ErrorLevel.FATAL.equals( rowData.getErrorLevel())) {
                row.setIcon( ICON_ERROR );
            } else if( ErrorLevel.WARNING.equals( rowData.getErrorLevel())) {
                row.setIcon( ICON_WARNING );
            }
            
            return row;
        }
}
    
}
