/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * NbExecutor.java
 *
 * Created on March 28, 2001, 6:57 PM
 */

package org.netbeans.xtest;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;
import org.apache.tools.ant.taskdefs.*;

import java.util.*;
import java.io.*;

/**
 *
 * @author  lm97939
 * @version 
 */
public class NbExecutor extends Task {
    
    String targetName = null;
    String targetParamModule   = null;
    String targetParamTestType = null;
    String targetParamTestAttributes = null;
    String mode = null;
    
    private static final String ERRORS_PROP = "xtest.errors";
    
    public void setTargetName(String name) {
        this.targetName = name;
    }
    
    public void setTargetParamModule(String param) {
        this.targetParamModule = param;
    }
    
    public void setTargetParamTesttype(String param) {
        this.targetParamTestType = param;
    }

    public void setTargetParamTestAttributes(String param) {
        this.targetParamTestAttributes = param;
    }
    
    public void setRunningMode(String mode) {
        this.mode = mode;
    }
    
    public void execute () throws BuildException {
        if (null == targetName || 0 == targetName.length())
            throw new BuildException("Attribute 'targetname' has to be set.");
        if (null == targetParamModule || 0 == targetParamModule.length())
            throw new BuildException("Attribute 'targetParamModule' has to be set.");
        if (null == targetParamTestType || 0 == targetParamTestType.length())
            throw new BuildException("Attribute 'targetParamTestType' has to be set.");
        
        MConfig cfg = NbTestConfig.getMConfig();                
        
        if (null == cfg)
            throw new BuildException("XTest configuration wasn't chosen, use call xtestconfig task first.", getLocation());        
        
        
        MConfig.Setup setup = cfg.getConfigSetup();
        if (mode.equalsIgnoreCase("run") && setup != null) executeStart(setup);

        Enumeration all_tests = cfg.getAllTests();
        while(all_tests.hasMoreElements()) {
          try {
            MConfig.TestGroup test_group = (MConfig.TestGroup)all_tests.nextElement();
            
            MConfig.Setup msetup = test_group.getSetup();
            if (mode.equalsIgnoreCase("run") && msetup != null) executeStart(msetup);
            
            Hashtable props = test_group.getProperties();
            
            Enumeration tests = test_group.getTests();

            while(tests.hasMoreElements()) {
              MConfig.Test test = (MConfig.Test) tests.nextElement();
              File outputfile = null;
              try {  
                Ant   callee = (Ant) getProject().createTask( "ant" );
                String       pattern;
                
                callee.setOwningTarget(target);
                callee.setTaskName(getTaskName());
                callee.setLocation(location);
                callee.init();

                callee.setTarget(targetName);
                callee.setDir(project.getBaseDir());
                callee.setAntfile(project.getProperty("ant.file"));

                Property paramModule = callee.createProperty();
                Property paramTestType = callee.createProperty();
                Property paramTestAttribute = callee.createProperty();
                
                paramModule.setName(targetParamModule);
                paramModule.setValue(test.getModule());
                paramTestType.setName(targetParamTestType);
                paramTestType.setValue(test.getType());
                paramTestAttribute.setName(targetParamTestAttributes);
                paramTestAttribute.setValue(test.getAttributesAsString());

                Set set = props.entrySet();
                Iterator it = set.iterator();
                while (it.hasNext()) {
                    Map.Entry map = (Map.Entry) it.next();
                    Property newproperty = callee.createProperty();
                    newproperty.setName((String)map.getKey());
                    newproperty.setValue((String)map.getValue());
                }
                
                
                outputfile = getLogFile(test.getModule() + "_" + test.getType());
                if (outputfile != null) 
                    callee.setOutput(outputfile.getAbsolutePath());
                callee.execute(); 
              }
              catch (BuildException e) {
                  log("Exception during executiong test (module="+test.getModule()+",type="+test.getType()+"):\n"+e.toString(),Project.MSG_ERR);
                  logError(e,outputfile);
              }
            }
            if (mode.equalsIgnoreCase("run") && msetup != null) executeStop(msetup);
          }
          catch (BuildException e) {
              log("Exception during executiong test:\n"+e.toString(),Project.MSG_ERR);
              logError(e,null);
          }
        } 
        if (mode.equalsIgnoreCase("run") && setup != null) executeStop(setup);
    } 
    
    private File getLogFile(String prefix) {
        String testrundir = project.getProperty("xtest.results.testrun.dir");
        if (testrundir == null) return null;
        String dir = testrundir + File.separator + "logs";
        File dirfile = new File(dir);
        if (!dirfile.exists()) dirfile.mkdirs();
        File file = new File(dirfile, prefix + ".log");
        int c = 1;
        while (file.exists()) {
            file = new File(dirfile, prefix + "_"+ c + ".log");
            c++;
        }
        return file;
    }
    
    private void logError(Exception e, File f) {
        String prop = System.getProperty(ERRORS_PROP,"");   
        prop = prop + "\n" + e.toString();
        System.setProperty(ERRORS_PROP,prop);
        if (f != null) {
          try {  
            BufferedWriter wr = new BufferedWriter(new FileWriter(f.getAbsolutePath().substring(0,f.getAbsolutePath().length()-3)+"errors"));
            wr.write(e.toString());
            wr.close();
          }
          catch (IOException exp) {
            log(exp.toString(),Project.MSG_ERR);
          }
        }
    }
    
    private void executeStart(MConfig.Setup setup) throws BuildException {
        executeSetup(setup.getName()+"_start", setup.getStartDir(), setup.getStartAntfile(), setup.getStartTarget(), setup.getStartOnBackground(), setup.getStartDelay());
    }

    private void executeStop(MConfig.Setup setup) throws BuildException {
        executeSetup(setup.getName()+"_stop", setup.getStopDir(), setup.getStopAntfile(), setup.getStopTarget(), setup.getStopOnBackground(), setup.getStopDelay());
    }
    
    private void executeSetup(String name, File dir, String antfile, String targetname, boolean onBackground, int delay) throws BuildException {
        if (antfile == null && targetname == null) return;
        final Ant ant = (Ant) project.createTask("ant");
        ant.setOwningTarget(target);
        ant.setLocation(location);
        ant.init();
        ant.setDir(dir);
        ant.setAntfile(antfile);
        ant.setTarget(targetname);
        final File outputfile = getLogFile(name);
        if (outputfile != null)
            ant.setOutput(outputfile.getAbsolutePath());
        if (onBackground) {
           Thread thread = new Thread() {
               public void run() {
                 try {
                   ant.execute();
                 }
                 catch (BuildException e) {
                     log("Exception during executiong setup:\n"+e.toString(),Project.MSG_ERR);
                     logError(e,outputfile);
                 }
               }
           };
           thread.start();
           if (delay != 0) {
               try { Thread.currentThread().sleep(delay); }
               catch (InterruptedException e) { throw new BuildException(e);}
           }
        }
        else {
            try {
               ant.execute();
            }              
            catch (BuildException e) {
                log("Exception during executiong setup:\n"+e.toString(),Project.MSG_ERR);
                logError(e,outputfile);
            }
        }
    }
        
}
