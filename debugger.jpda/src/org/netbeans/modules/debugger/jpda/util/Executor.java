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

package com.netbeans.developer.modules.debugger.jpda.util;

/**
* Executor of some Event.
*
* @author   Jan Jancura
*/
public interface Executor {

  /**
  * Executes event.
  */
  public void exec (com.sun.jdi.event.Event event);
}

/*
 * Log
 *  3    Jaga      1.1.1.0     3/2/00   Jan Jancura     
 *  2    Gandalf   1.1         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  1    Gandalf   1.0         7/13/99  Jan Jancura     
 * $
 */
