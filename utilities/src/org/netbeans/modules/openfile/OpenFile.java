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

package com.netbeans.developer.modules.openfile;

import java.beans.*;
import java.io.*;
import java.net.InetAddress;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import java.text.MessageFormat;
import javax.swing.*;
import javax.swing.event.*;

import org.openide.*;
import org.openide.cookies.*;
import org.openide.filesystems.*;
import org.openide.filesystems.FileSystem;
import org.openide.loaders.*;
import org.openide.nodes.*;
import org.openide.util.HelpCtx;

/** Opens files when requested. Main functionality.
*
* @author Jaroslav Tulach, Jesse Glick
*/
class OpenFile extends Object {
  
  /** Open the file either by calling {@link OpenCookie} ({@link ViewCookie}), or by
  * showing it in the Explorer.
  * Uses {@link #find} to figure out what the right file object is.
  * @param f file on local disk
  * @param wait whether to wait until requested to return a status
  * @param addr address to send reply to, if waiting
  * @param port port to send reply to, if waiting
  */
  static void open (File f, final boolean wait, InetAddress addr, int port) {
    FileObject fo = find (f);
    
    if (fo != null) {
      try {
        DataObject obj = DataObject.find (fo);
        final OpenCookie open = (OpenCookie) obj.getCookie (OpenCookie.class);
        final ViewCookie view = (ViewCookie) obj.getCookie (ViewCookie.class);
        if (open != null || view != null) {
          TopManager.getDefault ().setStatusText (SettingsBeanInfo.getString (wait ? "MSG_openingAndWaiting" : "MSG_opening", f.toString ()));
          if (open != null)
            open.open ();
          else
            view.view ();
          TopManager.getDefault ().setStatusText ("");
          if (wait) {
            // Could look for a SaveCookie just to see, but need not.
            Server.waitFor (obj, addr, port);
          }
        } else {
          Node n = obj.getNodeDelegate ();
          if (fo.isRoot ()) {
            // Try to get the node used in the usual Repository, which
            // has a non-blank display name and is thus nicer.
            FileSystem fs = fo.getFileSystem ();
            Node reponode = TopManager.getDefault ().getPlaces ().nodes ().repository ();
            Children repokids = reponode.getChildren ();
            Enumeration fsenum = repokids.nodes ();
            while (fsenum.hasMoreElements ()) {
              Node fsnode = (Node) fsenum.nextElement ();
              DataFolder df = (DataFolder) fsnode.getCookie (DataFolder.class);
              if (df != null && df.getPrimaryFile ().getFileSystem ().equals (fs)) {
                n = fsnode;
                break;
              }
            }
          }
          TopManager.getDefault ().getNodeOperation ().explore (n);
          if (wait)
              TopManager.getDefault ().notify (new NotifyDescriptor.Message (SettingsBeanInfo.getString ("MSG_cannotOpenWillClose", f)));
        }
      } catch (IOException ioe) {
        TopManager.getDefault ().notifyException (ioe);
      }
    }
  }

  /** Try to find the file object corresponding to a given file on disk.
  * Can produce a folder, mount directories, etc. as needed.
  * @param f the file on local disk
  * @return file object or <code>null</code> if not found
  */
  private static FileObject find (File f) {
    String fileName = f.toString ();
    String fileNameUpper = fileName.toUpperCase ();
    // Handle ZIP/JAR files by mounting and displaying.
    if (fileNameUpper.endsWith (".ZIP") || fileNameUpper.endsWith (".JAR")) {
      JarFileSystem jfs = new JarFileSystem ();
      try {
        jfs.setJarFile (f);
      } catch (IOException e5) {
        TopManager.getDefault ().notifyException (e5);
        return null;
      } catch (PropertyVetoException e6) {
        TopManager.getDefault ().notifyException (e6);
        return null;
      }
      Repository repo2 = TopManager.getDefault ().getRepository ();
      FileSystem exist = repo2.findFileSystem (jfs.getSystemName ());
      if (exist == null) {
        if (TopManager.getDefault ().notify (new NotifyDescriptor.Confirmation (SettingsBeanInfo.getString ("MSG_mountArchiveConfirm", f),
                                                                                SettingsBeanInfo.getString ("LBL_mountArchiveConfirm")))
          .equals (NotifyDescriptor.OK_OPTION)) {
          repo2.addFileSystem (jfs);
          exist = jfs;
        } else {
          return null;
        }
      }
      // The root folder will be displayed in the Explorer:
      return exist.getRoot ();
    }
    // Next see if it is present in an existing LocalFileSystem.
    // enumeration of file systems
    Enumeration en = TopManager.getDefault ().getRepository ().getFileSystems ();
    while (en.hasMoreElements ()) {
      FileSystem fs = (FileSystem)en.nextElement ();
      if (fs instanceof LocalFileSystem) {
        LocalFileSystem lfs = (LocalFileSystem)fs;
        File root = lfs.getRootDirectory ();
        String rootName = root.toString ().toUpperCase ();
        if (fileNameUpper.startsWith (rootName)) {
          // the filesystem can contain the file
          String resource = fileName.substring (rootName.length ()).replace (File.separatorChar, '/');
          if (resource.startsWith ("/"))
            resource = resource.substring (1);
          else if (resource.length () > 0)
            continue;           // e.g. root = /tmp/foo but file = /tmp/foobar
          FileObject fo = fs.findResource (resource);
          if (fo != null) {
            return fo;
          } else {
            TopManager.getDefault ().notify (new NotifyDescriptor.Message ("Should have found " + fileName + " in " + root));
            return null;
          }
        }
      }
    }
    // Not found. For Java files, it is reasonable to mount the package root.
    String pkg = null;
    // packageKnown will only be true if
    // a .java is used and its package decl
    // indicates a real dir
    boolean packageKnown = false;
    if (fileNameUpper.endsWith (".JAVA")) {
      // Try to find the package name and then infer a directory to mount.
      BufferedReader rd = null;
      try {
        rd = new BufferedReader (new InputStreamReader (new FileInputStream (f)));
      scan:
        while (true) {
          String line = rd.readLine ();
          if (line == null) {
            packageKnown = true; // i.e. valid termination of search, default pkg
            break;
          }
          // Will not handle package statements broken across lines, oh well.
          if (line.indexOf ("package") == -1) continue;
          StringTokenizer tok = new StringTokenizer (line, " \t;");
          boolean gotPackage = false;
          while (tok.hasMoreTokens ()) {
            String theTok = tok.nextToken ();
            if (gotPackage) {
              // Hopefully the package name, but first a sanity check...
              StringTokenizer ptok = new StringTokenizer (theTok, ".");
              boolean ok = ptok.hasMoreTokens ();
              while (ptok.hasMoreTokens ()) {
                String component = ptok.nextToken ();
                if (component.length () == 0) {
                  ok = false;
                  break;
                }
                if (! Character.isJavaIdentifierStart (component.charAt (0))) {
                  ok = false;
                  break;
                }
                for (int pos = 1; pos < component.length (); pos++) {
                  if (! Character.isJavaIdentifierPart (component.charAt (pos))) {
                    ok = false;
                    break;
                  }
                }
              }
              if (ok) {
                pkg = theTok;
                packageKnown = true;
                break scan;
              } else {
                // Keep on looking for valid package statement.
                gotPackage = false;
                continue;
              }
            } else if (theTok.equals ("package")) {
              gotPackage = true;
            } else if (theTok.equals ("{")) {
              // Most likely we can stop if hit opening brace of class def.
              // Usually people leave spaces around it.
              packageKnown = true; // valid end of search, default pkg
              break scan;
            }
          }
        }
      } catch (IOException e1) {
        TopManager.getDefault ().notifyException (e1);
      } finally {
        try {
          if (rd != null) rd.close ();
        } catch (IOException e2) {
          TopManager.getDefault ().notifyException (e2);
        }
      }
    }
    // Now try to go through the package name piece by piece and get the right parent directory.
    if (pkg == null) {
      pkg = ""; // assume default package
    }
    String prefix = pkg.replace ('.', File.separatorChar);
    File dir = f.getParentFile ();
    String pkgtouse = "";
    while (! pkg.equals ("") && dir != null) {
      int lastdot = pkg.lastIndexOf ('.');
      String trypkg;
      String trypart;
      if (lastdot == -1) {
        trypkg = "";
        trypart = pkg;
      } else {
        trypkg = pkg.substring (0, lastdot);
        trypart = pkg.substring (lastdot + 1);
      }
      if (dir.getName ().equals (trypart) && dir.getParentFile () != null) {
        // Worked so far.
        dir = dir.getParentFile ();
        pkg = trypkg;
        if (pkgtouse.equals (""))
          pkgtouse = trypart;
        else
          pkgtouse = trypart + "." + pkgtouse;
      } else {
        // No dice.
        packageKnown = false;
        break;
      }
    }
    // Ask what to mount (if anything). Prompt appropriately with the possible
    // mount points, as well as the recommended one if there is one (i.e. for valid *.java).
    File[] dirToMount = new File[] { null };
    String[] mountPackage = new String[] { null };
    int pkgLevel = 0;
    if (! pkgtouse.equals ("")) {
      int pos = -1;
      do {
        pos = pkgtouse.indexOf ('.', pos + 1);
        pkgLevel++;
      } while (pos != -1);
    }
    if (! packageKnown) pkgLevel = -1;
    askForMountPoint (f, pkgLevel, dirToMount, mountPackage);
    if (dirToMount[0] == null) return null;
    // Mount it.
    LocalFileSystem fs = new LocalFileSystem ();
    try {
      fs.setRootDirectory (dirToMount[0]);
    } catch (PropertyVetoException e3) {
      TopManager.getDefault ().notifyException (e3);
      return null;
    } catch (IOException e4) {
      TopManager.getDefault ().notifyException (e4);
      return null;
    }
    Repository repo = TopManager.getDefault ().getRepository ();
    if (repo.findFileSystem (fs.getSystemName ()) != null) {
      TopManager.getDefault ().notify (new NotifyDescriptor.Message (fs.getSystemName () + " was already mounted??"));
      return null;
    }
    repo.addFileSystem (fs);
    return fs.findResource (mountPackage[0].replace ('.', '/') + (mountPackage[0].equals ("") ? "" : "/") + f.getName ());
  }
  
  /** Ask what dir to mount to access a given file.
  * @param f the file which should be accessible
  * @param pkgLevel the suggested depth of the package; 0 = default, 1 = single component, 2 = foo.bar, etc.; -1 if no suggested package
  * @param dirToMount 0th elt will contain the directory to mount (null to cancel the mount)
  * @param mountPackage 0th elt will contain the name of the package (possibly empty, not null) the file will be in
  */
  private static void askForMountPoint (File f, int pkgLevel, final File[] dirToMount, final String[] mountPackage) {
    final JPanel panel = new JPanel ();
    panel.setLayout (new BorderLayout (0, 5));
    panel.setBorder (new javax.swing.border.EmptyBorder (8, 8, 8, 8));
    
    JTextArea textArea = new JTextArea ();
    textArea.setBackground (Color.lightGray);
    textArea.setFont (new Font ("SansSerif", Font.PLAIN, 11));
    textArea.setText (SettingsBeanInfo.getString (pkgLevel == -1 ? "TXT_whereMountNoSuggest" : "TXT_whereMountSuggest", f.getName ()));
    textArea.setEditable (false);
    textArea.setLineWrap (true);
    textArea.setWrapStyleWord (true);
    panel.add (textArea, BorderLayout.NORTH);
    
    final Vector dirs = new Vector (); // list of mountable dir names; Vector<File>
    final Vector pkgs = new Vector (); // list of resulting package names; Vector<String>
    String pkg = "";
    for (File dir = f.getParentFile (); dir != null; dir = dir.getParentFile ()) {
      dirs.add (dir);
      pkgs.add (pkg);
      if (! pkg.equals ("")) pkg = "." + pkg;
      pkg = dir.getName () + pkg;
    }
    final JList list = new JList (dirs);
    list.setVisibleRowCount (5);
    list.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
    if (pkgLevel != -1) list.setSelectedIndex (pkgLevel);
    panel.add (new JScrollPane (list), BorderLayout.CENTER);
    
    final JLabel label = new JLabel ();
    label.setFont (new Font ("Monospaced", Font.PLAIN, 12));
    panel.add (label, BorderLayout.SOUTH);
    panel.setPreferredSize (new Dimension (450, 300));

    final JButton okButton = new JButton (SettingsBeanInfo.getString ("LBL_okButton"));
    JButton cancelButton = new JButton (SettingsBeanInfo.getString ("LBL_cancelButton"));
    
    list.addListSelectionListener (new ListSelectionListener () {
      public void valueChanged (ListSelectionEvent ev) {
        updateLabelEtcFromList (label, list, pkgs, okButton);
      }
    });
    updateLabelEtcFromList (label, list, pkgs, okButton);

    final Dialog[] dialog = new Dialog[1];
    dialog[0] = TopManager.getDefault ().createDialog
      (new DialogDescriptor
       (panel,                   // object
        SettingsBeanInfo.getString ("LBL_wizTitle"), // title
        true,                    // modal
        new Object[] { okButton, cancelButton }, // options
        okButton,                // initial
        DialogDescriptor.DEFAULT_ALIGN, // align
        new HelpCtx (OpenFile.class.getName () + ".dialog"), // help
        new ActionListener () { // listener
          public void actionPerformed (ActionEvent evt) {
            dialog[0].dispose ();
            if (evt.getSource () == okButton) {
              int idx = list.getSelectedIndex ();
              if (idx != -1) {
                dirToMount[0] = (File) dirs.elementAt (idx);
                mountPackage[0] = (String) pkgs.elementAt (idx);
              } else {
                System.err.println ("Should not have accepted OK button");
              }
            }
          }
        }));
    dialog[0].show ();
    
  }

  private static void updateLabelEtcFromList (JLabel label, JList list, Vector pkgs, JButton okButton) {
    int idx = list.getSelectedIndex ();
    if (idx == -1) {
      label.setText (" ");
      okButton.setEnabled (false);
    } else {
      String pkg = (String) pkgs.elementAt (idx);
      if (pkg.equals (""))
        label.setText (SettingsBeanInfo.getString ("LBL_packageWillBeDefault"));
      else
        label.setText (SettingsBeanInfo.getString ("LBL_packageWillBe", pkg));
      okButton.setEnabled (true);
    }
  }
  
  /** Test run of askForMountPoint. */
  public static void main (String[] ign) {
    JFileChooser chooser = new JFileChooser ();
    chooser.showOpenDialog (null);
    File f = chooser.getSelectedFile ();
    int lvl = Integer.parseInt (JOptionPane.showInputDialog ("Level"));
    File[] mount = new File[] { null };
    String[] pkg = new String[] { null };
    askForMountPoint (f, lvl, mount, pkg);
    System.out.println ("Mount dir: " + mount[0] + " package: " + pkg[0]);
  }
  
}
