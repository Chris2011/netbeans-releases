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

package com.netbeans.developer.modules.search;

import org.openide.*;
import org.openide.modules.ModuleInstall;
import org.openide.loaders.*;

import org.openidex.util.*;

import com.netbeans.developer.modules.search.res.*;
import com.netbeans.developer.modules.search.types.*;

/** 
* During restored() hooks SearchPresenter on FindAction. 
* During uninstalled() frees such hook.
* Add RepositorySearchAction to Tools menu.
*
* @author  Petr Kuzel
* @version 1.0
*/
public class Installer extends ModuleInstall {

  private final static long serialVersionUID = 1;
  
  private final String MENU = "Tools";
  private final String MENUITEM = "Settings";
  private final String ACTION = "RepositorySearchAction";

  /** Holds hooking code. */
  private SearchHook hook;

  /** Install in tools menu. Place after Settings separated by separator.
  */
  public void installed() {

    try {
      
      Utilities2.createAction (
        RepositorySearchAction.class, 
        DataFolder.create (
          TopManager.getDefault ().getPlaces ().folders ().menus (), 
          MENU
        ), 
        MENUITEM, true, true, true, false
      );


      // add to action pool
      
      InstanceDataObject.create (
        DataFolder.create (
          TopManager.getDefault ().getPlaces ().folders ().actions (), 
          MENU
        ), 
        ACTION, 
        RepositorySearchAction.class.getName ()
      );
    
    } catch (Exception ex) {
      if (System.getProperty ("netbeans.debug.exceptions") != null) 
        ex.printStackTrace ();      
    }
    
    Registry.reorderBy(new Class[] {ObjectNameType.class, FullTextType.class} );
    
    restored();
  }
  
  /** Start listening at SELECTED_NODES.
  */
  public void restored () {
    hook = new SearchHook(new SearchPerformer());
    hook.hook();   
  }
  
  /** Unhook listening at SELECTED_NODES and remove itself from menu.
  */
  public void uninstalled () {
    try {
      hook.unhook();
      
      Utilities2.removeAction (
        RepositorySearchAction.class, 
        DataFolder.create (
          TopManager.getDefault ().getPlaces ().folders ().menus (), 
          MENU
        )
      );
      
      // remove from actions pool
      
      InstanceDataObject.remove (
        DataFolder.create (
          TopManager.getDefault ().getPlaces ().folders ().actions (), 
          MENU
        ),
        ACTION, 
        RepositorySearchAction.class.getName ()
      );
      
    } catch (Exception ex) {
      if (Boolean.getBoolean ("netbeans.debug.exceptions"))
        ex.printStackTrace ();         
    }
  }

}


/* 
* Log
*  7    Gandalf   1.6         1/5/00   Petr Kuzel      Margins used. Help 
*       contexts.
*  6    Gandalf   1.5         12/23/99 Petr Kuzel      Architecture improved.
*  5    Gandalf   1.4         12/17/99 Petr Kuzel      Bundling.
*  4    Gandalf   1.3         12/16/99 Petr Kuzel      
*  3    Gandalf   1.2         12/15/99 Petr Kuzel      
*  2    Gandalf   1.1         12/14/99 Petr Kuzel      Minor enhancements
*  1    Gandalf   1.0         12/14/99 Petr Kuzel      
* $ 
*/ 
  
