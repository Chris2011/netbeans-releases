/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * $Id$
 */
package org.netbeans.installer;

import java.io.File;
import java.util.Properties;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.netbeans.installer.product.ProductRegistry;
import org.netbeans.installer.utils.ErrorManager;
import org.netbeans.installer.utils.LogManager;
import org.netbeans.installer.wizard.Wizard;
import org.netbeans.installer.wizard.components.actions.FinalizeRegistryAction;
import org.netbeans.installer.wizard.components.actions.InitalizeRegistryAction;

import static org.netbeans.installer.utils.ErrorLevel.DEBUG;
import static org.netbeans.installer.utils.ErrorLevel.MESSAGE;
import static org.netbeans.installer.utils.ErrorLevel.WARNING;
import static org.netbeans.installer.utils.ErrorLevel.ERROR;
import static org.netbeans.installer.utils.ErrorLevel.CRITICAL;

/**
 * The main class of the NBBA installer framework. It represents the installer and
 * provides methods to start the installation/maintenance process as well as to
 * finish/cancel/break the installation.
 *
 * @author Kirill Sorokin
 */
public class Installer {
    /////////////////////////////////////////////////////////////////////////////////
    // Main
    /**
     * The main method. It gets an instance of <code>Installer</code> and calls the
     * <code>start</code> method, passing in the command line arguments.
     *
     * @param arguments The command line arguments
     * @see #start(String[])
     */
    public static void main(String[] arguments) {
        new Installer(arguments).start();        
    }
    
    /////////////////////////////////////////////////////////////////////////////////
    // Constants
    public static final String DEFAULT_LOCAL_DIRECTORY_PATH =
            System.getProperty("user.home") + File.separator + ".nbi";
    
    public static final String LOCAL_DIRECTORY_PATH_PROPERTY =
            "nbi.local.directory.path";
    
    public static final String DEFAULT_NBI_LOOK_AND_FEEL_CLASS_NAME = 
            UIManager.getSystemLookAndFeelClassName();
    
    public static final String NBI_LOOK_AND_FEEL_CLASS_NAME_PROPERTY = 
            "nbi.look.and.feel";
    
    /** Errorcode to be used at normal exit */
    public static final int NORMAL_ERRORCODE = 0;
    
    /** Errorcode to be used when the installer is canceled */
    public static final int CANCEL_ERRORCODE = 1;
    
    /** Errorcode to be used when the installer exits because of a critical error */
    public static final int CRITICAL_ERRORCODE = Integer.MAX_VALUE;
    
    /////////////////////////////////////////////////////////////////////////////////
    // Static
    private static Installer instance;
    
    /**
     * Returns an instance of <code>Installer</code>. If the instance does not
     * exist - it is created.
     *
     * @return An instance of <code>Installer</code>
     */
    public static synchronized Installer getInstance() {
        return instance;
    }
    
    /////////////////////////////////////////////////////////////////////////////////
    // Instance
    private File localDirectory =
            new File(DEFAULT_LOCAL_DIRECTORY_PATH).getAbsoluteFile();
    
    private LogManager   logManager   = LogManager.getInstance();
    private ErrorManager errorManager = ErrorManager.getInstance();
    
    // Constructor //////////////////////////////////////////////////////////////////
    /**
     * The only private constructor - we need to hide the default one as
     * <code>Installer is a singleton.
     */
    private Installer(String[] arguments) {
        logManager.log(MESSAGE, "initializing the installer engine");
        logManager.indent();
        
        dumpSystemInfo();
        
        parseArguments(arguments);
        
        setLookAndFeel();
        
        instance = this;
        
        logManager.log(MESSAGE, "initializing the local directory");
        if (System.getProperty(LOCAL_DIRECTORY_PATH_PROPERTY) != null) {
            localDirectory = new File(System.getProperty(
                    LOCAL_DIRECTORY_PATH_PROPERTY)).getAbsoluteFile();
        }
        
        
        logManager.unindent();
        logManager.log(MESSAGE, "... finished initializing the installer engine");
    }
    
    // Life cycle control methods ///////////////////////////////////////////////////
    /**
     * Starts the installer. This method parses the passed-in command line arguments,
     * initializes the wizard and the components registry.
     *
     * @param arguments The command line arguments
     */
    public void start() {
        if (!localDirectory.exists()) {
            if (!localDirectory.mkdirs()) {
                ErrorManager.getInstance().notify(CRITICAL, "Cannot create local directory: " + localDirectory);
            }
        } else if (localDirectory.isFile()) {
            ErrorManager.getInstance().notify(CRITICAL, "Local directory exists and is a file: " + localDirectory);
        } else if (!localDirectory.canRead()) {
            ErrorManager.getInstance().notify(CRITICAL, "Cannot read local directory - not enought permissions");
        } else if (!localDirectory.canWrite()) {
            ErrorManager.getInstance().notify(CRITICAL, "Cannot write to local directory - not enought permissions");
        }
        
        
        final Wizard wizard = Wizard.getInstance();
        
        wizard.open();
        wizard.executeAction(new InitalizeRegistryAction());
        wizard.next();
    }
    
    /**
     * Cancels the installation. This method cancels the changes that were possibly
     * made to the components registry and exits with the cancel error code.
     *
     * @see #finish()
     * @see #criticalExit()
     */
    public void cancel() {
        // exit with the cancel error code
        System.exit(CANCEL_ERRORCODE);
    }
    
    /**
     * Finishes the installation. This method finalizes the changes made to the
     * components registry and exits with a normal error code.
     *
     * @see #cancel()
     * @see #criticalExit()
     */
    public void finish() {
        Wizard wizard = Wizard.getInstance();
        
        wizard.executeAction(new FinalizeRegistryAction());
        wizard.close();
        
        System.exit(NORMAL_ERRORCODE);
    }
    
    /**
     * Critically exists. No changes will be made to the components registry - it
     * will remain at the same state it was at the moment this method was called.
     *
     * @see #cancel()
     * @see #finish()
     */
    public void criticalExit() {
        // exit immediately, as the system is apparently in a crashed state
        System.exit(CRITICAL_ERRORCODE);
    }
    
    // Getters //////////////////////////////////////////////////////////////////////
    public File getLocalDirectory() {
        return localDirectory;
    }
    
    // Private stuff ////////////////////////////////////////////////////////////////
    /**
     * Parses the command line arguments passed to the installer. All unknown
     * arguments are ignored.
     *
     * @param arguments The command line arguments
     */
    private void parseArguments(String[] arguments) {
        logManager.log(MESSAGE, "parsing command-line arguments");
        logManager.indent();
        
        for (int i = 0; i < arguments.length; i++) {
            if (arguments[i].equalsIgnoreCase("--look-and-feel")) {
                logManager.log(MESSAGE, "parsing command line parameter \"--look-and-feel\"");
                if (i < arguments.length - 1) {
                    String value = arguments[i + 1];
                    System.setProperty(NBI_LOOK_AND_FEEL_CLASS_NAME_PROPERTY, value);
                    
                    i = i + 1;
                    
                    logManager.log(MESSAGE, "... class name: " + value);
                } else {
                    ErrorManager.getInstance().notify(WARNING, "Required parameter missing for command line argument \"--look-and-feel\". Should be \"--look-and-feel <look-and-feel-class-name>\".");
                }
                
                continue;
            }
            
            if (arguments[i].equalsIgnoreCase("--target")) {
                logManager.log(MESSAGE, "parsing command line parameter \"--target\"");
                if (i < arguments.length - 2) {
                    String uid = arguments[i + 1];
                    String version = arguments[i + 2];
                    System.setProperty(ProductRegistry.TARGET_COMPONENT_UID_PROPERTY, uid);
                    System.setProperty(ProductRegistry.TARGET_COMPONENT_VERSION_PROPERTY, version);
                    
                    i = i + 2;
                    
                    logManager.log(MESSAGE, "... uid:     " + uid);
                    logManager.log(MESSAGE, "... version: " + version);
                }
                
                continue;
            }
            
        }
        
        if (arguments.length == 0) {
            logManager.log(MESSAGE, "... no command line arguments were specified");
        }
        
        logManager.unindent();
        logManager.log(MESSAGE, "... finished parsing command line arguments");
    }
    
    private void setLookAndFeel() {
        logManager.log(MESSAGE, "setting the look and feel");
        logManager.indent();
        
        String className = System.getProperty(NBI_LOOK_AND_FEEL_CLASS_NAME_PROPERTY);
        if (className == null) {
            logManager.log(MESSAGE, "custom look and feel class name was not specified, using system default");
            className = DEFAULT_NBI_LOOK_AND_FEEL_CLASS_NAME;
        }
        
        logManager.log(MESSAGE, "... class name: " + className);
        
        try {
            UIManager.setLookAndFeel(className);
        } catch (ClassNotFoundException e) {
            ErrorManager.getInstance().notify(WARNING, "Could not set the look and feel.", e);
        } catch (InstantiationException e) {
            ErrorManager.getInstance().notify(WARNING, "Could not set the look and feel.", e);
        } catch (IllegalAccessException e) {
            ErrorManager.getInstance().notify(WARNING, "Could not set the look and feel.", e);
        } catch (UnsupportedLookAndFeelException e) {
            ErrorManager.getInstance().notify(WARNING, "Could not set the look and feel.", e);
        }
        
        logManager.unindent();
        logManager.log(MESSAGE, "... finished setting the look and feel");
    }
    
    private void dumpSystemInfo() {
        logManager.log(MESSAGE, "dumping target system information");
        logManager.indent();
        
        logManager.log(MESSAGE, "system properties");
        logManager.indent();
        
        Properties properties = System.getProperties();
        for (Object key: properties.keySet()) {
            logManager.log(MESSAGE, key.toString() + " => " + properties.get(key).toString());
        }
        
        logManager.unindent();
        logManager.log(MESSAGE, "... end of system properties");
        
        logManager.unindent();
        logManager.log(MESSAGE, "... end of target system information");
    }
}
