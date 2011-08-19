/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.debugger.jpda.visual;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.BooleanValue;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.ClassType;
import com.sun.jdi.DoubleValue;
import com.sun.jdi.Field;
import com.sun.jdi.FloatValue;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.InvocationException;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StringReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Type;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.jpda.JPDADebugger;
import org.netbeans.api.debugger.jpda.JPDAThread;
import org.netbeans.modules.debugger.jpda.JPDADebuggerImpl;
import org.netbeans.modules.debugger.jpda.expr.InvocationExceptionTranslated;
import org.netbeans.modules.debugger.jpda.models.JPDAThreadImpl;
import org.netbeans.modules.debugger.jpda.visual.RemoteServices.ServiceType;
import org.netbeans.modules.debugger.jpda.visual.actions.GoToSourceAction;
import org.netbeans.spi.debugger.visual.ComponentInfo;
import org.netbeans.spi.debugger.visual.RemoteScreenshot;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.nodes.Node.PropertySet;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

/**
 * Takes screenshot of a remote application.
 * 
 * @author Martin Entlicher
 */
public class RemoteFXScreenshot {
    
    private static final Logger logger = Logger.getLogger(RemoteFXScreenshot.class.getName());
    
    private static final String FXThreadName = "FX Access Thread";  // NOI18N
    
    private static final RemoteScreenshot[] NO_SCREENSHOTS = new RemoteScreenshot[] {};

    
    private RemoteFXScreenshot() {
    }
    
    private static RemoteScreenshot createRemoteFXScreenshot(DebuggerEngine engine, VirtualMachine vm, ThreadReference tr, String title, ObjectReference window, SGComponentInfo componentInfo) throws InvalidTypeException, ClassNotLoadedException, IncompatibleThreadStateException, InvocationException {
        final ClassType imageClass = getClass(vm, "java.awt.image.BufferedImage");
        final ClassType toolkitClass = getClass(vm, "com.sun.javafx.tk.Toolkit");
        final ClassType sceneClass = getClass(vm, "javafx.scene.Scene");
        final ClassType windowClass = getClass(vm, "javafx.stage.Window");

        final Method getDefaultTk = toolkitClass.concreteMethodByName("getToolkit", "()Lcom/sun/javafx/tk/Toolkit;");
        final Method convertImage = toolkitClass.methodsByName("toExternalImage", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;").get(0);
        final Method renderImage = sceneClass.concreteMethodByName("renderToImage", "(Ljava/lang/Object;FZ)Ljava/lang/Object;");
        final Method getScene = windowClass.concreteMethodByName("getScene", "()Ljavafx/scene/Scene;");

        ObjectReference scene = (ObjectReference) window.invokeMethod(tr, getScene, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);

        FloatValue factor = vm.mirrorOf(1.0f);
        BooleanValue syncNeeded = vm.mirrorOf(false);

        ObjectReference image = (ObjectReference)scene.invokeMethod(tr, renderImage, Arrays.asList(null, factor, syncNeeded), ObjectReference.INVOKE_SINGLE_THREADED);
        ObjectReference toolkit = (ObjectReference)toolkitClass.invokeMethod(tr, getDefaultTk, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
        ObjectReference bufImage = (ObjectReference)toolkit.invokeMethod(tr, convertImage, Arrays.asList(image, imageClass.classObject()), ObjectReference.INVOKE_SINGLE_THREADED);

        Method getData = ((ClassType)bufImage.referenceType()).concreteMethodByName("getData", "()Ljava/awt/image/Raster;");
        ObjectReference rasterRef = (ObjectReference) bufImage.invokeMethod(tr, getData, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);

        ClassType rasterType = (ClassType)rasterRef.referenceType();
        Method getWidth = rasterType.concreteMethodByName("getWidth", "()I");
        Method getHeight = rasterType.concreteMethodByName("getHeight", "()I");
        Method getDataElements = rasterType.concreteMethodByName("getDataElements", "(IIIILjava/lang/Object;)Ljava/lang/Object;");
        IntegerValue zero = vm.mirrorOf(0);
        IntegerValue width = (IntegerValue)rasterRef.invokeMethod(tr, getWidth, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
        IntegerValue height = (IntegerValue)rasterRef.invokeMethod(tr, getHeight, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
        ArrayReference data = (ArrayReference) rasterRef.invokeMethod(tr, getDataElements, Arrays.asList(zero, zero, width, height, null), ObjectReference.INVOKE_SINGLE_THREADED);

        logger.log(Level.FINE, "Image data length = {0}", data.length());

        List<Value> dataValues = data.getValues();
        int[] dataArray = new int[data.length()];
        int i = 0;
        for (Value v : dataValues) {
            dataArray[i++] = ((IntegerValue) v).value();
        }
        final BufferedImage bi = new BufferedImage(width.value(), height.value(), BufferedImage.TYPE_INT_ARGB);
        WritableRaster raster = bi.getRaster();
        raster.setDataElements(0, 0, width.intValue(), height.intValue(), dataArray);
        return new RemoteScreenshot(engine, title, width.intValue(), height.intValue(), bi, componentInfo);
    }
    
    public static RemoteScreenshot[] takeCurrent() throws RetrievalException {
        DebuggerEngine engine = DebuggerManager.getDebuggerManager().getCurrentEngine();
        if (engine != null) {
            JPDADebugger debugger = engine.lookupFirst(null, JPDADebugger.class);
            takeCurrent(debugger);
        }
        return NO_SCREENSHOTS;
    }
    
    public static RemoteScreenshot[] takeCurrent(JPDADebugger debugger) throws RetrievalException {
        logger.log(Level.FINE, "Debugger = {0}", debugger);
        if (debugger != null) {
            DebuggerEngine engine = ((JPDADebuggerImpl) debugger).getSession().getCurrentEngine();
            List<JPDAThread> allThreads = debugger.getThreadsCollector().getAllThreads();
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Threads = {0}", allThreads);
            }
            for (JPDAThread t : allThreads) {
                if (t.getName().startsWith(FXThreadName)) {
                    return take(t, engine, (JPDADebuggerImpl)debugger);
                }
            }
        }

        return NO_SCREENSHOTS;
    }
    
    public static RemoteScreenshot[] take(final JPDAThread t, final DebuggerEngine engine, final JPDADebuggerImpl d) throws RetrievalException {//throws ClassNotLoadedException, IncompatibleThreadStateException, InvalidTypeException, InvocationException {
        //RemoteScreenshot[] screenshots = NO_SCREENSHOTS;
        final ThreadReference tawt = ((JPDAThreadImpl) t).getThreadReference();
        final VirtualMachine vm = tawt.virtualMachine();
        final ClassType windowClass = getClass(vm, "javafx.stage.Window");
        
        if (windowClass == null) {
            logger.fine("No Window");
            return NO_SCREENSHOTS;
        }

        //Method getWindows = null;//windowClass.concreteMethodByName("getOwnerlessWindows", "()[Ljava/awt/Window;");
        final Method getWindows = windowClass.concreteMethodByName("impl_getWindows", "()Ljava/util/Iterator;");
        if (getWindows == null) {
            logger.fine("No getWindows() method!");
            String msg = NbBundle.getMessage(RemoteFXScreenshot.class, "MSG_ScreenshotNotTaken_MissingMethod", "java.awt.Window.getWindows()");
            throw new RetrievalException(msg);
        }

        final List<RemoteScreenshot> screenshots = new ArrayList<RemoteScreenshot>();
        final RetrievalException[] retrievalExceptionPtr = new RetrievalException[] { null };
        
        final RetrievalException[] thrown = new RetrievalException[1];
        try {
            RemoteServices.runOnStoppedThread(t, new Runnable() {
                @Override
                public void run() {
                    try {
                        pauseAll(tawt, vm);
                        retrieveScreenshots((JPDAThreadImpl)t, tawt, vm, engine, d, screenshots);
                    } catch (RetrievalException e) {
                        thrown[0] = e;
                    } finally {
                        try {
                            resumeAll(tawt, vm);
                        } catch (RetrievalException e) {
                            thrown[0] = e;
                        }
                    }
                }
            }, RemoteServices.ServiceType.FX);
        } catch (PropertyVetoException pve) {
            throw new RetrievalException(pve.getMessage(), pve);
        }
        if (thrown[0] != null) {
            throw thrown[0];
        }
        
        if (retrievalExceptionPtr[0] != null) {
            throw retrievalExceptionPtr[0];
        }
        return screenshots.toArray(new RemoteScreenshot[] {});
    }
    
    private static void retrieveScreenshots(JPDAThreadImpl t, final ThreadReference tr, VirtualMachine vm, DebuggerEngine engine, JPDADebuggerImpl d, final List<RemoteScreenshot> screenshots) throws RetrievalException {
        try {
            final ClassType windowClass = getClass(vm, "javafx.stage.Window");
            
            Method getWindows = windowClass.concreteMethodByName("impl_getWindows", "()Ljava/util/Iterator;");
            Method windowName = windowClass.concreteMethodByName("impl_getMXWindowType", "()Ljava/lang/String;");

            ObjectReference iterator = (ObjectReference)windowClass.invokeMethod(tr, getWindows, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
            ClassType iteratorClass = (ClassType)iterator.referenceType();
            Method hasNext = iteratorClass.concreteMethodByName("hasNext", "()Z");
            Method next = iteratorClass.concreteMethodByName("next", "()Ljava/lang/Object;");
            
            boolean nextFlag = false;
            do {
                BooleanValue bv = (BooleanValue)iterator.invokeMethod(tr, hasNext, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
                nextFlag = bv.booleanValue();
                if (nextFlag) {
                    ObjectReference window = (ObjectReference)iterator.invokeMethod(tr, next, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
                    StringReference name = (StringReference)window.invokeMethod(tr, windowName, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
                    SGComponentInfo windowInfo = new SGComponentInfo(t, window);
                    
                    screenshots.add(createRemoteFXScreenshot(engine, vm, tr, name.value(), window, windowInfo));
                }
            } while (nextFlag);
        } catch (Exception e) {
            throw new RetrievalException(e.getMessage(), e);
        }
    }
    
    private static boolean pauseAll(ThreadReference tr, VirtualMachine vm) throws RetrievalException {
        final ClassType toolkitClass = getClass(vm, "com.sun.javafx.tk.Toolkit");
        
        if (toolkitClass == null) {
            logger.fine("No Toolkiit");
            return false;
        }
                
        final Method getDefaultTk = toolkitClass.concreteMethodByName("getToolkit", "()Lcom/sun/javafx/tk/Toolkit;");
        if (getDefaultTk == null) {
            logger.fine("No getToolkit() method!");
            String msg = NbBundle.getMessage(RemoteFXScreenshot.class, "MSG_ScreenshotNotTaken_MissingMethod", "com.sun.javafx.tk.Toolkit.getToolkit()");
            throw new RetrievalException(msg);
        }

        final Method pauseScenes = toolkitClass.concreteMethodByName("pauseScenes", "()V");
        if (pauseScenes == null) {
            logger.fine("No pauseScenes() method!");
            String msg = NbBundle.getMessage(RemoteFXScreenshot.class, "MSG_ScreenshotNotTaken_MissingMethod", "com.sun.javafx.tk.Toolkit.pauseScenes()");
            throw new RetrievalException(msg);
        }
        
        try {
            ObjectReference tk = (ObjectReference)toolkitClass.invokeMethod(tr, getDefaultTk, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
            tk.invokeMethod(tr, pauseScenes, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
            
            pauseMedia(tr, vm);
            return true;
        } catch (InvalidTypeException e) {
            throw new RetrievalException(e.getMessage(), e);
        } catch (ClassNotLoadedException e) {
            throw new RetrievalException(e.getMessage(), e);
        } catch (IncompatibleThreadStateException e) {
            throw new RetrievalException(e.getMessage(), e);
        } catch (InvocationException e) {
            throw new RetrievalException(e.getMessage(), e);
        }
    }
    
    private static boolean resumeAll(ThreadReference tr, VirtualMachine vm) throws RetrievalException {
        final ClassType toolkitClass = getClass(vm, "com.sun.javafx.tk.Toolkit");
        
        if (toolkitClass == null) {
            logger.fine("No Toolkiit");
            return false;
        }
                
        final Method getDefaultTk = toolkitClass.concreteMethodByName("getToolkit", "()Lcom/sun/javafx/tk/Toolkit;");
        if (getDefaultTk == null) {
            logger.fine("No getToolkit() method!");
            String msg = NbBundle.getMessage(RemoteFXScreenshot.class, "MSG_ScreenshotNotTaken_MissingMethod", "com.sun.javafx.tk.Toolkit.getToolkit()");
            throw new RetrievalException(msg);
        }
        
        final Method resumeScenes = toolkitClass.concreteMethodByName("resumeScenes", "()V");
        if (resumeScenes == null) {
            logger.fine("No pauseScenes() method!");
            String msg = NbBundle.getMessage(RemoteFXScreenshot.class, "MSG_ScreenshotNotTaken_MissingMethod", "com.sun.javafx.tk.Toolkit.resumeScenes()");
            throw new RetrievalException(msg);
        }
        
        try {
            ObjectReference tk = (ObjectReference)toolkitClass.invokeMethod(tr, getDefaultTk, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
            tk.invokeMethod(tr, resumeScenes, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
            
            resumeMedia(tr, vm);
            return true;
        } catch (InvalidTypeException e) {
            throw new RetrievalException(e.getMessage(), e);
        } catch (ClassNotLoadedException e) {
            throw new RetrievalException(e.getMessage(), e);
        } catch (IncompatibleThreadStateException e) {
            throw new RetrievalException(e.getMessage(), e);
        } catch (InvocationException e) {
            throw new RetrievalException(e.getMessage(), e);
        }
    }
    
    private static final Collection<ObjectReference> pausedPlayers = new ArrayList<ObjectReference>();
    
    private static void pauseMedia(ThreadReference tr, VirtualMachine vm) throws InvalidTypeException, ClassNotLoadedException, IncompatibleThreadStateException, InvocationException {
        final ClassType audioClipClass = getClass(vm, "com.sun.media.jfxmedia.AudioClip");
        final ClassType mediaManagerClass = getClass(vm, "com.sun.media.jfxmedia.MediaManager");
        final ClassType mediaPlayerClass = getClass(vm, "com.sun.media.jfxmedia.MediaPlayer");
        final ClassType playerStateEnum = getClass(vm, "com.sun.media.jfxmedia.events.PlayerStateEvent.PlayerState");
        
        if (audioClipClass != null) {
            Method stopAllClips = audioClipClass.concreteMethodByName("stopAllClips", "()V");
            audioClipClass.invokeMethod(tr, stopAllClips, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
        }
        
        if (mediaManagerClass != null && mediaPlayerClass != null && playerStateEnum != null) {
            Method getAllPlayers = mediaManagerClass.concreteMethodByName("getAllMediaPlayers", "()Ljava/util/List;");

            ObjectReference plList = (ObjectReference)mediaManagerClass.invokeMethod(tr, getAllPlayers, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);

            ClassType listType = (ClassType)plList.referenceType();
            Method iterator = listType.concreteMethodByName("iterator", "()Ljava/util/Iterator;");
            ObjectReference plIter = (ObjectReference)plList.invokeMethod(tr, iterator, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);

            ClassType iterType = (ClassType)plIter.referenceType();
            Method hasNext = iterType.concreteMethodByName("hasNext", "()Z");
            Method next = iterType.concreteMethodByName("next", "()Ljava/lang/Object;");


            Field playingState = playerStateEnum.fieldByName("PLAYING");

            Method getState = mediaPlayerClass.concreteMethodByName("getState", "()Lcom/sun/media/jfxmedia/events/PlayerStateEvent$PlayerState;");
            Method pausePlayer = mediaPlayerClass.concreteMethodByName("pause", "()V");
            boolean hasNextFlag = false;
            do {
                BooleanValue v = (BooleanValue)plIter.invokeMethod(tr, hasNext, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
                hasNextFlag = v.booleanValue();
                if (hasNextFlag) {
                    ObjectReference player = (ObjectReference)plIter.invokeMethod(tr, next, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
                    ObjectReference curState = (ObjectReference)player.invokeMethod(tr, getState, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
                    if (playingState.equals(curState)) {
                        player.invokeMethod(tr, pausePlayer, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
                        pausedPlayers.add(player);
                    }
                }
            } while (hasNextFlag);
        }
    }
    
    private static void resumeMedia(ThreadReference tr, VirtualMachine vm) throws InvalidTypeException, ClassNotLoadedException, IncompatibleThreadStateException, InvocationException {
        if (!pausedPlayers.isEmpty()) {
            final ClassType mediaPlayerClass = getClass(vm, "com.sun.media.jfxmedia.MediaPlayer");
            Method play = mediaPlayerClass.concreteMethodByName("play", "()V");
            for(ObjectReference pR : pausedPlayers) {
                pR.invokeMethod(tr, play, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
            }
        }
    }
    
    private static ClassType getClass(VirtualMachine vm, String name) {
        List<ReferenceType> classList = vm.classesByName(name);
        ReferenceType clazz = null;
        for (ReferenceType c : classList) {
            clazz = c;
            break;
        }
        return (ClassType) clazz;
    }
    
    public static class SGComponentInfo extends JavaComponentInfo {
        public SGComponentInfo(JPDAThreadImpl t, ObjectReference component) throws RetrievalException {
            super(t, component, ServiceType.FX);
            init();
        }
        
        @Override
        protected void retrieve() throws RetrievalException {
            VirtualMachine vm = getThread().getDebugger().getVirtualMachine();
            ThreadReference tr = getThread().getThreadReference();
            ClassType compClass = (ClassType)getComponent().referenceType();
            try {
                if (compClass.name().equals("javafx.stage.Window") ||
                    compClass.name().equals("javafx.stage.Stage")) {
                    Method getTitle = compClass.concreteMethodByName("getTitle", "()Ljava/lang/String;");
                    if (getTitle != null) {
                        StringReference nameR = (StringReference)getComponent().invokeMethod(tr, getTitle, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
                        setName(nameR != null ? nameR.value() : "");
                    }
                    Method getScene = compClass.concreteMethodByName("getScene", "()Ljavafx/scene/Scene;");
                    ObjectReference scene = (ObjectReference)getComponent().invokeMethod(tr, getScene, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
                    ClassType sceneClass = (ClassType)scene.referenceType();
                    Method getX = sceneClass.concreteMethodByName("getX", "()D");
                    Method getY = sceneClass.concreteMethodByName("getY", "()D");
                    Method getWidth = sceneClass.concreteMethodByName("getWidth", "()D");
                    Method getHeight = sceneClass.concreteMethodByName("getHeight", "()D");
                    DoubleValue x = (DoubleValue) scene.invokeMethod(tr, getX, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
                    DoubleValue y = (DoubleValue) scene.invokeMethod(tr, getY, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
                    DoubleValue width = (DoubleValue) scene.invokeMethod(tr, getWidth, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
                    DoubleValue height = (DoubleValue) scene.invokeMethod(tr, getHeight, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
                    Rectangle b = new Rectangle(x.intValue(), y.intValue(), width.intValue(), height.intValue());
                    b.x = 0;
                    b.y = 0;
                    setWindowBounds(b);
                    setBounds(b);
                    
                    Method getRoot = sceneClass.concreteMethodByName("getRoot", "()Ljavafx/scene/Parent;");
                    ObjectReference root = (ObjectReference)scene.invokeMethod(tr, getRoot, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
                    SGComponentInfo rootInfo = new SGComponentInfo(getThread(), root);
                    
                    setSubComponents(new JavaComponentInfo[]{
                        rootInfo
                    });
                } else {
                    Method getId = compClass.concreteMethodByName("getId", "()Ljava/lang/String;");
                    if (getId != null) {
                        StringReference id = (StringReference)getComponent().invokeMethod(tr, getId, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
                        setName(id != null ? id.value() : ""); // NOI18N
                    }
//                    Method getRelBounds = compClass.concreteMethodByName("getBoundsInParent", "()Ljavafx/geometry/Bounds;");
                    Method getLocalBounds = compClass.concreteMethodByName("getBoundsInLocal", "()Ljavafx/geometry/Bounds;");
                    Method local2scene = compClass.concreteMethodByName("localToScene", "(Ljavafx/geometry/Bounds;)Ljavafx/geometry/Bounds;");
                    Method local2parent = compClass.concreteMethodByName("localToParent", "(Ljavafx/geometry/Bounds;)Ljavafx/geometry/Bounds;");
//                    ObjectReference relBounds = (ObjectReference)getComponent().invokeMethod(tr, getRelBounds, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
                    ObjectReference locBounds = (ObjectReference)getComponent().invokeMethod(tr, getLocalBounds, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
                    ObjectReference relBounds = (ObjectReference)getComponent().invokeMethod(tr, local2parent, Arrays.asList(locBounds), ObjectReference.INVOKE_SINGLE_THREADED);
                    ObjectReference absBounds = (ObjectReference)getComponent().invokeMethod(tr, local2scene, Arrays.asList(locBounds), ObjectReference.INVOKE_SINGLE_THREADED);
                    
                    setBounds(convertBounds(relBounds));
                    setWindowBounds(convertBounds(absBounds));
                    
                    Field children = compClass.fieldByName("children");
                    if (children != null) {
                        ObjectReference childrenList = (ObjectReference)getComponent().getValue(children);
                        ClassType listClass = (ClassType)childrenList.referenceType();
                        Method size = listClass.concreteMethodByName("size", "()I");
                        Method get = listClass.concreteMethodByName("get", "(I)Ljava/lang/Object;");
                        int cnt = ((IntegerValue)childrenList.invokeMethod(tr, size, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED)).intValue();
                        JavaComponentInfo[] cs = new JavaComponentInfo[cnt];
                        for(int i=0;i<cnt;i++) {
                            ObjectReference sub = (ObjectReference)childrenList.invokeMethod(tr, get, Arrays.asList(vm.mirrorOf(i)), ObjectReference.INVOKE_SINGLE_THREADED);
                            cs[i] = new SGComponentInfo(getThread(), sub);
                        }
                        setSubComponents(cs);
                    }
                }
            } catch (InvalidTypeException e) {
            } catch (ClassNotLoadedException e) {
            } catch (IncompatibleThreadStateException e) {
            } catch (InvocationException e) {
            }
        }
        
        private static Rectangle convertBounds(ObjectReference bounds) {
            ClassType boundsClass = (ClassType)bounds.referenceType();
            Field minX = boundsClass.fieldByName("minX");
            Field minY = boundsClass.fieldByName("minY");
            Field width = boundsClass.fieldByName("width");
            Field height = boundsClass.fieldByName("height");

            return new Rectangle(((DoubleValue)bounds.getValue(minX)).intValue(), 
               ((DoubleValue)bounds.getValue(minY)).intValue(), 
               ((DoubleValue)bounds.getValue(width)).intValue(), 
               ((DoubleValue)bounds.getValue(height)).intValue()
            );
        }
                
        @Override
        public Action[] getActions(boolean context) {
            return new SystemAction[] { GoToSourceAction.get(GoToSourceAction.class) };
        }
        
        @Override
        public ComponentInfo findAt(int x, int y) {
            Rectangle bounds = getWindowBounds();
            if (!bounds.contains(x, y)) {
                return null;
            }

            ComponentInfo[] subComponents = getSubComponents();
            if (subComponents != null) {
                Rectangle tempRect = null;
                ComponentInfo tempRslt = null;
                for (int i = 0; i < subComponents.length; i++) {
                    Rectangle sb = subComponents[i].getWindowBounds();
                    if (sb.contains(x, y)) {
                        tempRect = sb;
                        tempRslt = subComponents[i];
                        ComponentInfo tci = subComponents[i].findAt(x, y);
                        if (tci != null) {
                            Rectangle tbounds = tci.getWindowBounds();
                            if (tempRect.intersects(tbounds)) {
                                tempRect = tbounds;
                                tempRslt = tci;
                            }
                        }
                    }
                }
                return tempRslt;
            }
            return this;
        }
    }
}