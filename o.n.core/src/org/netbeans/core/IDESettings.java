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

package com.netbeans.developer.impl;

import java.beans.Introspector;
import java.beans.PropertyEditorManager;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.openide.NotifyDescriptor;
import org.openide.TopManager;
import org.openide.awt.HtmlBrowser;
import org.openide.options.SystemOption;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/** Global IDE settings.
*
* @author Ian Formanek
*/
public class IDESettings extends SystemOption {
  /** generated Serialized Version UID */
  static final long serialVersionUID = 801136840705717911L;

  /** outputLevel property name */
  public static String PROP_OUTPUT_LEVEL = "outputLevel";
  /** Look&feel property name */
  public static String PROP_LOOK_AND_FEEL = "lookAndFeel";
  /** showTipsOnStartup property name */
  public static String PROP_SHOW_TIPS_ON_STARTUP = "showTipsOnStartup";
  /** lastTip property name */
  public static String PROP_LAST_TIP = "lastTip";
  /** confirmDelete property name */
  public static String PROP_CONFIRM_DELETE = "confirmDelete";
  /** home page property name */
  public static String PROP_HOME_PAGE = "homePage";
  /** use proxy property name */
  public static String PROP_USE_PROXY = "useProxy";
  /** proxy host property name */
  public static String PROP_PROXY_HOST = "proxyHost";
  /** proxy port property name */
  public static String PROP_PROXY_PORT = "proxyPort";


  /** Minimum output detail level */
  public static final int OUTPUT_MINIMUM = 0;
  /** Normal output detail level */
  public static final int OUTPUT_NORMAL = 1;
  /** Maximum output detail level */
  public static final int OUTPUT_MAXIMUM = 2;

// ------------------------------------------
// properties

   private static boolean showTips = true;
   private static int lastTip = 1;
   private static int outputLevel = 1;
   private static boolean confirmDelete = true;

   private static Hashtable alreadyLoadedBeans = new Hashtable();

// ------------------------------------------
// property access methods

  /** A utility method to avoid unnecessary creation of second URL */
  public static URL getRealHomeURL () {
    return NetworkOptions.getStaticHomeURL();
  }

  /** Getter for OutputLevel property.
  * @return The level of output
  */
  public int getOutputLevel() {
    return outputLevel;
  }

  /** Setter for OutputLevel property.
  * @param value The new level of output
  */
  public void setOutputLevel(int value) {
    if (outputLevel == value) return;
    int oldValue = outputLevel;
    outputLevel = value;
    // fire the PropertyChange
    firePropertyChange (PROP_OUTPUT_LEVEL, new Integer (oldValue), new Integer (outputLevel));
  }

  /** Getter for ShowTipsOnStartup
   * @return true if dialog will be shown*/
  public boolean getShowTipsOnStartup() {
    return showTips;
  }

  /** Setter for ShowTipsOnStartup
  * @param value true if on the next start of corona the dialog will be shown
  *              false otherwise */
  public void setShowTipsOnStartup(boolean value) {
    if (showTips == value) return;
    showTips = value;
    // fire the PropertyChange
    firePropertyChange (PROP_SHOW_TIPS_ON_STARTUP, new Boolean (!showTips), new Boolean (showTips));
  }

  /** Getter for the LookAndFeel option */
  public LookAndFeel getLookAndFeel () {
    return UIManager.getLookAndFeel ();
  }

  /** Setter for the LookAndFeel option */
  public void setLookAndFeel (LookAndFeel value) {
    LookAndFeel oldValue = UIManager.getLookAndFeel ();
    if ((oldValue != null) && (value.getClass ().equals (oldValue.getClass ())))
      return;

    // we do not try to set the unsupported look & feel
    if (!value.isSupportedLookAndFeel ())
      return;

    // update the UI
    try {
      UIManager.setLookAndFeel(value);
    } catch (UnsupportedLookAndFeelException e) {
      TopManager.getDefault().notify(
        new NotifyDescriptor.Exception(e,
          NbBundle.getBundle(IDESettings.class).getString("MSG_UnsupportedLookAndFeel"))
        );
      return; // we do not update UI, nor we fire the property change now
    }
    org.openide.TopManager.getDefault ().getWindowManager ().updateUI ();

    // fire the PropertyChange
    firePropertyChange (PROP_LOOK_AND_FEEL, oldValue, value);
  }

  /** Getter for LastTip
   * @return index of the tip which should be shown on the next start of Corona*/
  public int getLastTip() {
    return lastTip;
  }

  /** Setter for LastTip
   * @param value sets index of the tip which will be shown on the next start of Corona*/
  public void setLastTip(int value) {
    if (value == lastTip) return;
    Integer oldValue = new Integer (lastTip);
    lastTip = value;
    // fire the PropertyChange
    firePropertyChange (PROP_LAST_TIP, oldValue, new Integer (lastTip));
  }

  /** Getter for ConfirmDelete
   * @param true if the user should asked for confirmation of object delete, false otherwise */
  public boolean getConfirmDelete() {
    return confirmDelete;
  }

  /** Setter for ConfirmDelete
   * @param value if true the user is asked for confirmation of object delete, not if false */
  public void setConfirmDelete(boolean value) {
    if (value == confirmDelete) return;
    Boolean oldValue = new Boolean (confirmDelete);
    confirmDelete = value;
    // fire the PropertyChange
    firePropertyChange (PROP_CONFIRM_DELETE, oldValue, new Boolean (confirmDelete));
  }

  /** This method must be overriden. It returns display name of this options.
  */
  public String displayName () {
    return NbBundle.getBundle(IDESettings.class).getString("CTL_IDESettings");
  }

  public HelpCtx getHelpCtx () {
    return new HelpCtx (IDESettings.class);
  }

  /** Getter for Hashtable of loaded jars with beans in previous Netbeans session.
  * Names of Jars which are not in this table will be auto loaded in next Netbeans
  * startup.
  */
  public Hashtable getLoadedBeans() {
    return alreadyLoadedBeans;
  }

  /** Setter for Hashtable of loaded jars with beans in previous Netbeans session.
  * Names of Jars which are not in this table will be auto loaded in next Netbeans
  * startup.
  */
  public void setLoadedBeans(Hashtable table) {
    alreadyLoadedBeans = table;
  }

  /** Getter for home page used in html viewer.
  */
  public String getHomePage () {
    return HtmlBrowser.getHomePage ();
  }
  
  /** Setter for home page used in html viewer.
  */
  public void setHomePage (String homePage) {
    HtmlBrowser.setHomePage (homePage);
  }

  /** Getter for proxy set flag.
  */
  public boolean getUseProxy () {
    String host = System.getProperty ("proxySet");
    if ((host != null) && (host.equals ("true"))) return true;
    else return false;
  }
  
  /** Setter for proxy set flag.
  */
  public void setUseProxy (boolean value) {
    if (value) {
      System.setProperty ("proxySet", "true");
    } else {
      System.setProperty ("proxySet", "false");
    }
  }

  /** Getter for proxy host.
  */
  public String getProxyHost () {
    String host = System.getProperty ("proxyHost");
    if (host == null) host = "";
    return host;
  }
  
  /** Setter for proxy host.
  */
  public void setProxyHost (String value) {
    System.setProperty ("proxyHost", value);
  }

  /** Getter for proxy port.
  */
  public String getProxyPort () {
    String port = System.getProperty ("proxyPort");
    if (port == null) port = "";
    return port;
  }
  
  /** Setter for proxy port.
  */
  public void setProxyPort (String value) {
    System.setProperty ("proxyPort", value);
  }
}

/*
 * Log
 *  10   Gandalf   1.9         7/28/99  Jan Jancura     Bug in useProxy property
 *  9    Gandalf   1.8         7/21/99  Ian Formanek    settings for proxy, 
 *       property output detail level hidden
 *  8    Gandalf   1.7         7/20/99  Ian Formanek    Removed 
 *       PropertyEditorSearchPath and BeanInfoSearchPath properties
 *  7    Gandalf   1.6         7/19/99  Jan Jancura     
 *  6    Gandalf   1.5         7/2/99   Jesse Glick     More help IDs.
 *  5    Gandalf   1.4         6/8/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  4    Gandalf   1.3         4/8/99   Ian Formanek    Undone last change
 *  3    Gandalf   1.2         4/8/99   Ian Formanek    Removed SearchPath 
 *       properties
 *  2    Gandalf   1.1         3/26/99  Ian Formanek    Fixed use of obsoleted 
 *       NbBundle.getBundle (this)
 *  1    Gandalf   1.0         1/5/99   Ian Formanek    
 * $
 * Beta Change History:
 *  0    Tuborg    0.11        --/--/98 Jan Palka       add ShowTipsOnStartup property and LastTip property
 *  0    Tuborg    0.12        --/--/98 Jan Formanek    improved
 */
