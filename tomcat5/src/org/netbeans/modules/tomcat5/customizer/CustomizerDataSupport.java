/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tomcat5.customizer;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.java.platform.Specification;
import org.netbeans.modules.tomcat5.TomcatManager;
import org.netbeans.modules.tomcat5.util.TomcatProperties;
import org.openide.ErrorManager;


/**
 * Customizer data support keeps models for all the customizer components, 
 * initializes them, tracks model changes and performs save.
 *
 * @author sherold
 */
public class CustomizerDataSupport {
    
    // models    
    private DefaultComboBoxModel    jvmModel;
    private Document                javaOptsModel;
    private ButtonModel             secManagerModel;        
    private Document                catalinaHomeModel;
    private Document                catalinaBaseModel;    
    private Document                usernameModel;
    private Document                passwordModel;    
    private Document                scriptPathModel;
    private ButtonModel             customScriptModel;
    private ButtonModel             forceStopModel;    
    private ButtonModel             sharedMemModel;
    private ButtonModel             socketModel;    
    private ButtonModel             monitorModel;    
    private Document                sharedMemNameModel;    
    private PathModel               sourceModel;
    private PathModel               classModel;
    private PathModel               javadocModel;
    private SpinnerNumberModel      serverPortModel;
    private SpinnerNumberModel      shutdownPortModel;
    private SpinnerNumberModel      debugPortModel;
    
    // model dirty flags    
    private boolean jvmModelFlag;
    private boolean javaOptsModelFlag;
    private boolean secManagerModelFlag;
    private boolean usernameModelFlag;
    private boolean passwordModelFlag;
    private boolean scriptPathModelFlag;
    private boolean customScriptModelFlag;
    private boolean forceStopModelFlag;
    private boolean sharedMemModelFlag;
    private boolean socketModelFlag;
    private boolean monitorModelFlag;
    private boolean sharedMemNameModelFlag;
    private boolean sourceModelFlag;
    private boolean javadocModelFlag;
    private boolean serverPortModelFlag;
    private boolean shutdownPortModelFlag;
    private boolean debugPortModelFlag;
    
    private TomcatProperties tp;
    private TomcatManager tm;
    
    /**
     * Creates a new instance of CustomizerDataSupport 
     */
    public CustomizerDataSupport(TomcatManager tm) {
        this.tm = tm;
        tp = tm.getTomcatProperties();
        init();
    }
    
    /** Initialize the customizer models. */
    private void init() {
        
        // jvmModel
        jvmModel = new DefaultComboBoxModel();
        loadJvmModel();
        jvmModel.addListDataListener(new ModelChangeAdapter() {
            public void modelChanged() {
                jvmModelFlag = true;
                store(); // This is just temporary until the server manager has OK and Cancel buttons
            }
        });
        
        // javaOptions
        javaOptsModel = createDocument(tp.getJavaOpts());
        javaOptsModel.addDocumentListener(new ModelChangeAdapter() {
            public void modelChanged() {
                javaOptsModelFlag = true;
                store(); // This is just temporary until the server manager has OK and Cancel buttons
            }
        });
        
        // catalinaHomeModel
        catalinaHomeModel = createDocument(tp.getCatalinaHome().toString());
        
        // catalinaBaseModel
        catalinaBaseModel = createDocument(tp.getCatalinaDir().toString());
        
        // usernameModel
        usernameModel = createDocument(tp.getUsername());
        usernameModel.addDocumentListener(new ModelChangeAdapter() {
            public void modelChanged() {
                usernameModelFlag = true;
                store(); // This is just temporary until the server manager has OK and Cancel buttons
            }
        });
        
        // passwordModel
        passwordModel = createDocument(tp.getPassword());
        passwordModel.addDocumentListener(new ModelChangeAdapter() {
            public void modelChanged() {
                passwordModelFlag = true;
                store(); // This is just temporary until the server manager has OK and Cancel buttons
            }
        });
        
        // sharedMemNameModel
        sharedMemNameModel = createDocument(tp.getSharedMem());
        sharedMemNameModel.addDocumentListener(new ModelChangeAdapter() {
            public void modelChanged() {
                sharedMemNameModelFlag = true;
                store(); // This is just temporary until the server manager has OK and Cancel buttons
            }
        });

        // scriptPathModel
        scriptPathModel = createDocument(tp.getScriptPath());
        scriptPathModel.addDocumentListener(new ModelChangeAdapter() {
            public void modelChanged() {
                scriptPathModelFlag = true;
                store(); // This is just temporary until the server manager has OK and Cancel buttons
            }
        });
        
        // secManagerModel
        secManagerModel = createToggleButtonModel(tp.getSecManager());
        secManagerModel.addItemListener(new ModelChangeAdapter() {
            public void modelChanged() {
                secManagerModelFlag = true;
                store(); // This is just temporary until the server manager has OK and Cancel buttons
            }
        });
        
        // customScriptModel
        customScriptModel = createToggleButtonModel(tp.getCustomScript());
        customScriptModel.addItemListener(new ModelChangeAdapter() {
            public void modelChanged() {
                customScriptModelFlag = true;
                store(); // This is just temporary until the server manager has OK and Cancel buttons
            }
        });
        
        // forceStopModel
        forceStopModel = createToggleButtonModel(tp.getForceStop());
        forceStopModel.addItemListener(new ModelChangeAdapter() {
            public void modelChanged() {
                forceStopModelFlag = true;
                store(); // This is just temporary until the server manager has OK and Cancel buttons
            }
        });
        
        // monitorModel
        monitorModel = createToggleButtonModel(tp.getMonitor());
        monitorModel.addItemListener(new ModelChangeAdapter() {
            public void modelChanged() {
                monitorModelFlag = true;
                store(); // This is just temporary until the server manager has OK and Cancel buttons
            }
        });

        // classModel
        classModel = new PathModel(tp.getClasses());
        
        // sourceModel
        sourceModel = new PathModel(tp.getSources());
        sourceModel.addListDataListener(new ModelChangeAdapter() {
            public void modelChanged() {
                sourceModelFlag = true;
                store(); // This is just temporary until the server manager has OK and Cancel buttons
            }
        });
        
        // javadocModel
        javadocModel = new PathModel(tp.getJavadocs());
        javadocModel.addListDataListener(new ModelChangeAdapter() {
            public void modelChanged() {
                javadocModelFlag = true;
                store(); // This is just temporary until the server manager has OK and Cancel buttons
            }
        });
        
        // serverPortModel
        serverPortModel = new SpinnerNumberModel(tm.getServerPort(), 0, 65535, 1);
        serverPortModel.addChangeListener(new ModelChangeAdapter() {
            public void modelChanged() {
                serverPortModelFlag = true;
                store(); // This is just temporary until the server manager has OK and Cancel buttons
            }
        });
        
        // shutdownPortModel
        shutdownPortModel = new SpinnerNumberModel(tm.getShutdownPort(), 0, 65535, 1);
        shutdownPortModel.addChangeListener(new ModelChangeAdapter() {
            public void modelChanged() {
                shutdownPortModelFlag = true;
                store(); // This is just temporary until the server manager has OK and Cancel buttons
            }
        });
        
        // debugPortModel
        debugPortModel = new SpinnerNumberModel(tp.getDebugPort(), 0, 65535, 1);
        debugPortModel.addChangeListener(new ModelChangeAdapter() {
            public void modelChanged() {
                debugPortModelFlag = true;
                store(); // This is just temporary until the server manager has OK and Cancel buttons
            }
        });
        
        ButtonGroup debugButtonGroup = new ButtonGroup();
        
        // socketModel
        socketModel = new JToggleButton.ToggleButtonModel();
        socketModel.setGroup(debugButtonGroup);
        socketModel.addItemListener(new ModelChangeAdapter() {
            public void modelChanged() {
                socketModelFlag = true;
                store(); // This is just temporary until the server manager has OK and Cancel buttons
            }
        });
        
        // sharedMemModel
        sharedMemModel = new JToggleButton.ToggleButtonModel();
        sharedMemModel.setGroup(debugButtonGroup);
        sharedMemModel.addItemListener(new ModelChangeAdapter() {
            public void modelChanged() {
                sharedMemModelFlag = true;
                store(); // This is just temporary until the server manager has OK and Cancel buttons
            }
        });
        
        boolean socketEnabled = TomcatProperties.DEBUG_TYPE_SOCKET.equalsIgnoreCase(tp.getDebugType());
        debugButtonGroup.setSelected(socketEnabled ? socketModel : sharedMemModel, true);
    }
    
    /** Update the jvm model */
    public void loadJvmModel() {
        JavaPlatformManager jpm = JavaPlatformManager.getDefault();
        JavaPlatformAdapter curJvm = (JavaPlatformAdapter)jvmModel.getSelectedItem();
        String curPlatformName = null;
        if (curJvm != null) {
            curPlatformName = curJvm.getName();
        } else {
            curPlatformName = (String)tp.getJavaPlatform().getProperties().get(TomcatProperties.PLAT_PROP_ANT_NAME);
        }

        jvmModel.removeAllElements();
        
        // feed the combo with sorted platform list
        JavaPlatform[] j2sePlatforms = jpm.getPlatforms(null, new Specification("J2SE", null)); // NOI18N
        JavaPlatformAdapter[] platformAdapters = new JavaPlatformAdapter[j2sePlatforms.length];
        for (int i = 0; i < platformAdapters.length; i++) {
            platformAdapters[i] = new JavaPlatformAdapter(j2sePlatforms[i]);
        }
        Arrays.sort(platformAdapters);
        for (int i = 0; i < platformAdapters.length; i++) {
            JavaPlatformAdapter platformAdapter = platformAdapters[i];
            jvmModel.addElement(platformAdapter);
            // try to set selected item
            if (curPlatformName != null) {
                if (curPlatformName.equals(platformAdapter.getName())) {
                    jvmModel.setSelectedItem(platformAdapter);
                }
            }   
        }
    }
    
    // model getters ----------------------------------------------------------
        
    public DefaultComboBoxModel getJvmModel() {
        return jvmModel;
    }
    
    public Document getJavaOptsModel() {
        return javaOptsModel;
    }
    
    public Document getCatalinaHomeModel() {
        return catalinaHomeModel;
    }
    
    public Document getCatalinaBaseModel() {
        return catalinaBaseModel;
    }
    
    public Document getUsernameModel() {
        return usernameModel;
    }    
    
    public Document getPasswordModel() {
        return passwordModel;
    }
    
    public ButtonModel getCustomScriptModel() {
        return customScriptModel;
    }
    
    public ButtonModel getForceStopModel() {
        return forceStopModel;
    }
    
    public Document getScriptPathModel() {
        return scriptPathModel;
    }
    
    public ButtonModel getSharedMemModel() {
        return sharedMemModel;
    }
    
    public ButtonModel getSocketModel() {
        return socketModel;
    }
    
    public ButtonModel getMonitorModel() {
        return monitorModel;
    }
    
    public ButtonModel getSecManagerModel() {
        return secManagerModel;
    }
    
    public Document getSharedMemNameModel() {
        return sharedMemNameModel;
    }
    
    public PathModel getClassModel() {
        return classModel;
    }
    
    public PathModel getSourceModel() {
        return sourceModel;
    }
    
    public PathModel getJavadocsModel() {
        return javadocModel;
    }
    
    public SpinnerNumberModel getServerPortModel() {
        return serverPortModel;
    }
    
    public SpinnerNumberModel getShutdownPortModel() {
        return shutdownPortModel;
    }
    
    public SpinnerNumberModel getDebugPortModel() {
        return debugPortModel;
    }
    
    // private helper methods -------------------------------------------------
    
    /** Save all changes */
    private void store() {
        
        if (jvmModelFlag) {
            JavaPlatformAdapter platformAdapter = (JavaPlatformAdapter)jvmModel.getSelectedItem();
            tp.setJavaPlatform(platformAdapter.getJavaPlatform());
            jvmModelFlag = false;
        }
        
        if (javaOptsModelFlag) {
            tp.setJavaOpts(getText(javaOptsModel));
            javaOptsModelFlag = false;
        }
        
        if (secManagerModelFlag) {
            tp.setSecManager(secManagerModel.isSelected());
            secManagerModelFlag = false;
        }
        
        if (usernameModelFlag) {
            tp.setUsername(getText(usernameModel));
            usernameModelFlag = false;
        }
        
        if (passwordModelFlag) {
            tp.setPassword(getText(passwordModel));
            passwordModelFlag = false;
        }
        
        if (scriptPathModelFlag) {
            tp.setScriptPath(getText(scriptPathModel));
            scriptPathModelFlag = false;
        }
        
        if (customScriptModelFlag) {
            tp.setCustomScript(customScriptModel.isSelected());
            customScriptModelFlag = false;
        }
        
        if (forceStopModelFlag) {
            tp.setForceStop(forceStopModel.isSelected());
            forceStopModelFlag = false;
        }
        
        if (sharedMemModelFlag || socketModelFlag) {
            tp.setDebugType(sharedMemModel.isSelected() ? TomcatProperties.DEBUG_TYPE_SHARED 
                                                        : TomcatProperties.DEBUG_TYPE_SOCKET);
            sharedMemModelFlag = false;
            socketModelFlag = false;
        }
        
        if (monitorModelFlag) {
            tp.setMonitor(monitorModel.isSelected());
            monitorModelFlag = false;
        }
        
        if (sharedMemNameModelFlag) {
            tp.setSharedMem(getText(sharedMemNameModel));
            sharedMemNameModelFlag = false;
        }
        
        if (sourceModelFlag) {
            tp.setSources(sourceModel.getData());
            sourceModelFlag = false;
        }
        
        if (javadocModelFlag) {
            tp.setJavadocs(javadocModel.getData());
            javadocModelFlag = false;
        }
        
        if (serverPortModelFlag) {
            tm.setServerPort(((Integer)serverPortModel.getValue()).intValue());
            serverPortModelFlag = false;
        }
        
        if (shutdownPortModelFlag) {
            tm.setShutdownPort(((Integer)shutdownPortModel.getValue()).intValue());
            shutdownPortModelFlag = false;
        }
        
        if (debugPortModelFlag) {
            tp.setDebugPort(((Integer)debugPortModel.getValue()).intValue());
            debugPortModelFlag = false;
        }
    }
    
    /** Create a Document initialized by the specified text parameter, which may be null */
    private Document createDocument(String text) {
        PlainDocument doc = new PlainDocument();
        if (text != null) {
            try {
                doc.insertString(0, text, null);
            } catch(BadLocationException e) {
                ErrorManager.getDefault().notify(e);
            }
        }
        return doc;
    }
    
    /** Create a ToggleButtonModel inilialized by the specified selected parameter. */
    private JToggleButton.ToggleButtonModel createToggleButtonModel(boolean selected) {
        JToggleButton.ToggleButtonModel model = new JToggleButton.ToggleButtonModel();
        model.setSelected(selected);
        return model;
    }
    
    /** Get the text value from the document */
    private String getText(Document doc) {
        try {
            return doc.getText(0, doc.getLength());
        } catch(BadLocationException e) {
            ErrorManager.getDefault().notify(e);
            return null;
        }
    }
        
    // private helper class ---------------------------------------------------
    
    /** 
     * Adapter that implements several listeners, which is useful for dirty model
     * monitoring.
     */
    private abstract class ModelChangeAdapter implements ListDataListener, 
            DocumentListener, ItemListener, ChangeListener {
        
        public abstract void modelChanged();
        
        public void contentsChanged(ListDataEvent e) {
            modelChanged();
        }

        public void intervalAdded(ListDataEvent e) {
            modelChanged();
        }

        public void intervalRemoved(ListDataEvent e) {
            modelChanged();
        }

        public void changedUpdate(DocumentEvent e) {
            modelChanged();
        }

        public void removeUpdate(DocumentEvent e) {
            modelChanged();
        }

        public void insertUpdate(DocumentEvent e) {
            modelChanged();
        }

        public void itemStateChanged(ItemEvent e) {
            modelChanged();
        }

        public void stateChanged(javax.swing.event.ChangeEvent e) {
            modelChanged();
        }
    }
    
    /** Java platform combo box model helper */
    private static class JavaPlatformAdapter implements Comparable {
        private JavaPlatform platform;
        
        public JavaPlatformAdapter(JavaPlatform platform) {
            this.platform = platform;
        }
        
        public JavaPlatform getJavaPlatform() {
            return platform;
        }
        
        public String getName() {
            return (String)platform.getProperties().get(TomcatProperties.PLAT_PROP_ANT_NAME);
        }
        
        public String toString() {
            return platform.getDisplayName();
        }
        
        public int compareTo(Object o) {
            return toString().compareTo(o.toString());
        }
    }
}
