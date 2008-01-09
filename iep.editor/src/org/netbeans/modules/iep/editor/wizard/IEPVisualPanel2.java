/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.iep.editor.wizard;

import java.awt.BorderLayout;
import javax.swing.JPanel;

import org.netbeans.api.project.Project;
import org.openide.util.NbBundle;

public final class IEPVisualPanel2 extends JPanel {

    private Project mProject;
    
    
    public IEPVisualPanel2(Project project) {
        this.mProject = project;
        initComponents();
        initGUI();
    }
            
    @Override
    public String getName() {
    	return NbBundle.getMessage(IEPVisualPanel1.class, "IEPVisualPanel2_title");
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    private void initGUI() {
        ElementOrTypeChooserPanel panel = new ElementOrTypeChooserPanel(this.mProject);
        this.setLayout(new BorderLayout());
        
        this.add(panel, BorderLayout.CENTER);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}

