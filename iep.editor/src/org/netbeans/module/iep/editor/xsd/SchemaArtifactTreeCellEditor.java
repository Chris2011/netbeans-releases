/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.module.iep.editor.xsd;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import org.netbeans.module.iep.editor.xsd.nodes.SelectableTreeNode;


/**
 *
 * @author radval
 */
public class SchemaArtifactTreeCellEditor extends AbstractCellEditor implements TreeCellEditor { 

    private JTree mTree;
    
    private TreeCellRenderer mRenderer;
    
    private SelectableTreeNode mCellValue;
    
    public SchemaArtifactTreeCellEditor(JTree tree,TreeCellRenderer renderer) { 
        this.mTree = tree;
        this.mRenderer = renderer;
    }

    
    public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
        Component editor = this.mRenderer.getTreeCellRendererComponent(tree, value,
        true, expanded, leaf, row, true);

        if(value instanceof SelectableTreeNode) {
            mCellValue = (SelectableTreeNode) value;
            
            if (editor instanceof JCheckBox) {
                JCheckBox checkBox = (JCheckBox) editor;
                // editor always selected and focused
                ItemListener itemListener = new CheckBoxItemListener(mCellValue, checkBox);
                checkBox.addItemListener(itemListener);
            }
        }

        return editor;
    }

    
    @Override
    public boolean shouldSelectCell(EventObject anEvent) { 
	return true; 
    }

    @Override
    public boolean isCellEditable(EventObject event) {
        boolean returnValue = false;
        if (event instanceof MouseEvent) {
          MouseEvent mouseEvent = (MouseEvent) event;
          TreePath path = mTree.getPathForLocation(mouseEvent.getX(),
              mouseEvent.getY());
          if (path != null) {
            Object node = path.getLastPathComponent();
            if ((node != null) && (node instanceof SelectableTreeNode)) {
              returnValue = true;
            }
          }
        }
        
        return returnValue;
    }

    public Object getCellEditorValue() {
        return mCellValue.getUserObject();
    }
    
    
    class CheckBoxItemListener implements ItemListener {
        
        private SelectableTreeNode mNode;
        
        private JCheckBox mCheckBox;
        public CheckBoxItemListener(SelectableTreeNode node, JCheckBox checkBox) {
            this.mNode = node;
            this.mCheckBox = checkBox;
        }
        
        public void itemStateChanged(ItemEvent itemEvent) {
            mNode.setSelected(itemEvent.getStateChange() == ItemEvent.SELECTED ? true : false );
            mCheckBox.removeItemListener(this);
            stopCellEditing();
          }
        
    }
}
