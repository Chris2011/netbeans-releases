/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is Forte for Java, Community Edition. The Initial
 * Developer of the Original Code is Sun Microsystems, Inc. Portions
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 */

package com.netbeans.developer.modules.javadoc.search;

import java.net.URL;

import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;

import com.netbeans.ide.windows.TopComponent;
import com.netbeans.ide.util.RequestProcessor;
import com.netbeans.ide.TopManager;

public class IndexSearch extends TopComponent {

  private DefaultListModel listModel;
  
  private SearchEngine searchEngine = null;

  //private SearchThread searchThread = null;
  //private RequestProcessor.Task rpTask = null;
  //private ShowShortDoc ssd = null;
  
  /** Initializes the Form */
  public IndexSearch() {

    initComponents ();

    /*
    shortHelpText.setLineWrap( true );
    ssd = new ShowShortDoc( indexList, shortHelpText );
    indexList.getSelectionModel().addListSelectionListener( ssd );
    */  
    listModel = new DefaultListModel();
    IndexListCellRenderer cr = new IndexListCellRenderer();
    indexList.setCellRenderer( cr );
    indexList.setModel( listModel );
    //indexScrollPane.setViewportView (indexList);
    indexScrollPane.validate();

    //pack ();
  }

  /** This method is called from within the init() method to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the FormEditor.
   */
  private void initComponents () {//GEN-BEGIN:initComponents
    // This code was developed using a non-commercially licensed version of NetBeans Developer 2.x.
    // For details, see http://www.netbeans.com/non_commercial.html

    /*
    addWindowListener (new java.awt.event.WindowAdapter () {
        public void windowClosing (java.awt.event.WindowEvent evt) {
          closeDialog (evt);
        }
      }
    );
    */

    setLayout (new java.awt.BorderLayout ());

    indexScrollPane = new javax.swing.JScrollPane ();
    indexScrollPane.setPreferredSize (new java.awt.Dimension(350, 200));
    indexScrollPane.setBorder (new javax.swing.border.EtchedBorder ());

      indexList = new javax.swing.JList ();
      indexList.setSelectionMode (javax.swing.ListSelectionModel.SINGLE_SELECTION);
      indexScrollPane.add (indexList);
      indexList.addMouseListener (new java.awt.event.MouseAdapter () {
          public void mouseClicked (java.awt.event.MouseEvent evt) {
            indexListMouseClicked (evt);
          }
         }
        );

    indexScrollPane.setViewportView (indexList);
    add (indexScrollPane, "Center");

    topPanel = new javax.swing.JPanel ();
    topPanel.setLayout (new java.awt.FlowLayout (1, 1, 1));

      searchField = new javax.swing.JTextField ();
      searchField.setPreferredSize (new java.awt.Dimension(200, 21));
      searchField.setMinimumSize (new java.awt.Dimension(150, 21));
      topPanel.add (searchField);

      searchButton = new javax.swing.JButton ();
      searchButton.setText ("Search");
      searchButton.addActionListener (new java.awt.event.ActionListener () {
          public void actionPerformed (java.awt.event.ActionEvent evt) {
            searchButtonActionPerformed (evt);
          }
        }
      );
      topPanel.add (searchButton);

    add (topPanel, "North");

    
    //shortHelpText = new javax.swing.JTextArea ();
    //shortHelpText.setPreferredSize (new java.awt.Dimension(0, 100));
    //shortHelpText.setBackground (new java.awt.Color (255, 255, 204));
    //shortHelpText.setBorder (new javax.swing.border.BevelBorder (1));
    //add (shortHelpText, "South");

  }//GEN-END:initComponents

  private void searchButtonActionPerformed (java.awt.event.ActionEvent evt) {
    if ( searchEngine == null ) {
      searchEngine = new SearchEngine();
      searchEngine.go();
    }
    else {
      searchEngine.stop();
      searchEngine = null;
    }
  }

  private void searchStoped() {
    searchEngine = null;
    searchButton.setText( "Search" );
    indexScrollPane.validate();
  }

  private void indexListMouseClicked( java.awt.event.MouseEvent me ) {
    if ( me.getClickCount() == 2 ) {
       DocIndexItem  dii = (DocIndexItem)indexList.getModel().getElementAt( indexList.getMinSelectionIndex() );
    
      try {
        
        //URL url = new URL( "file:///s:/Development/javadoc/java/jdk12/api/index-files/" + dii.reference );
        URL url = dii.getURL();
        System.out.println ( "the URL " + url );
        TopManager.getDefault().showUrl( url );  
      }
      catch ( java.net.MalformedURLException ex ) {
        System.out.println ( "Malformed URL" );
      } 
    }
  }


// Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JScrollPane indexScrollPane;
  private javax.swing.JPanel topPanel;
//  private javax.swing.JTextArea shortHelpText;
  private javax.swing.JList indexList;
  private javax.swing.JTextField searchField;
  private javax.swing.JButton searchButton;
// End of variables declaration//GEN-END:variables

  /*
  public static void main(java.lang.String[] args) {
    new IndexSearch (new java.awt.Frame (), false).show ();
  }
  */

  void searchEnded() {
    /*
    searchThread = null;
    SwingUtilities.invokeLater( new Runnable() {
      public void run() {
        searchButton.setText( "OK" );
      }
    } );
    */
  }

  private class SearchEngine {

    private ArrayList tasks;
    private DocFileSystem[] docSystems; 
    private IndexSearchThread.DocIndexItemConsumer diiConsumer;

    SearchEngine() {
      docSystems = DocFileSystem.getFolders();
      tasks = new ArrayList( docSystems.length );

      diiConsumer = new IndexSearchThread.DocIndexItemConsumer() {
        public void addDocIndexItem( final DocIndexItem dii ) {
          javax.swing.SwingUtilities.invokeLater( new Runnable() { 
            public void run() { 
              ((DefaultListModel)indexList.getModel()).addElement( dii ); 
            } 
          } );                
        }

        public void indexSearchThreadFinished( IndexSearchThread t ) {
          tasks.remove( t );
          if ( tasks.isEmpty() )
            searchStoped();
        }
      };
    }

    /** Starts searching */

    void go() {
      ((DefaultListModel)indexList.getModel()).clear();
      for( int i = 0; i < docSystems.length; i++ ) {        
        IndexSearchThread searchThread = new SearchThreadJdk12( searchField.getText(),  docSystems[i].getIndexFile() , diiConsumer );
        tasks.add( searchThread );
        searchThread.go();
      }
      searchButton.setText( "Stop" );
    }

    /** Stops the search */
  
    void stop() { 
      for( int i = 0; i < tasks.size(); i++ ) {     
        SearchThreadJdk12 searchThread = (SearchThreadJdk12)tasks.get( i );
        searchThread.finish();
      }
      
    }

  }

}
