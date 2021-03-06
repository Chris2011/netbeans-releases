/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.j2ee.ejbcore.ui.logicalview.ejb.session;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.java.source.ui.ElementOpen;
import org.netbeans.modules.j2ee.api.ejbjar.EjbJar;
import org.netbeans.modules.j2ee.core.api.support.java.method.MethodModel;
import org.netbeans.modules.j2ee.core.api.support.java.method.MethodModelSupport;
import org.netbeans.modules.j2ee.ejbcore.api.methodcontroller.SessionMethodController;
import org.netbeans.modules.j2ee.ejbcore.ui.logicalview.ejb.shared.ComponentMethodModel;
import org.netbeans.modules.j2ee.ejbcore.ui.logicalview.ejb.shared.ComponentMethodViewStrategy;
import org.netbeans.modules.j2ee.ejbcore.ui.logicalview.ejb.shared.MethodsNode;
import org.netbeans.modules.j2ee.ejbcore.ui.logicalview.ejb.shared.MethodsNode.ViewType;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Chris Webster
 * @author Martin Adamek
 */
public class MethodChildren extends ComponentMethodModel {
    private static final Logger LOG = Logger.getLogger(MethodChildren.class.getName());
    private ComponentMethodViewStrategy mvs;
    private final SessionMethodController controller;
    private final MethodsNode.ViewType viewType;
    private final SessionChildren session;
    
    public MethodChildren(SessionChildren session, ClasspathInfo cpInfo, EjbJar ejbModule, SessionMethodController smc, MethodsNode.ViewType viewType) {
        super(cpInfo, ejbModule, smc.getBeanClass(), getHomeInterface(smc, viewType));
        controller = smc;
        this.viewType = viewType;
        this.session = session;
        mvs = new SessionStrategy();
    }

    private static String getHomeInterface(SessionMethodController smc, MethodsNode.ViewType viewType) {
        if (viewType == ViewType.NO_INTERFACE) {
            return null;
        } else {
            return viewType == ViewType.LOCAL ? smc.getLocalHome() : smc.getHome();
        }
    }

    @Override
    protected Collection<String> getInterfaces() {
        if (viewType == ViewType.LOCAL) {
            return controller.getLocalInterfaces();
        } else if (viewType == ViewType.REMOTE) {
            return controller.getRemoteInterfaces();
        } else {
            return controller.getLocalInterfaces();
        }
    }
    
    @Override
    public ComponentMethodViewStrategy createViewStrategy() {
        return mvs;
    }

    @Override
    public void fireTypeChange() {
        session.propertyChange(new PropertyChangeEvent(this, TYPE_CHANGE, "", "")); //NOI18N
    }

    private class SessionStrategy implements ComponentMethodViewStrategy {
        
        @Override
        public void deleteImplMethod(MethodModel me, String implClass, FileObject implClassFO) throws IOException {
            switch (viewType){
                case NO_INTERFACE:{
                    controller.delete(me);
                    break;
                }
                case LOCAL:
                case REMOTE:{
                    controller.delete(me, viewType == viewType.LOCAL);
                    break;
                }
            }
        }

        @Override
        public Image getBadge(MethodModel me) {
            return null;
        }

        @Override
        public Image getIcon(MethodModel me) {
            IconVisitor iv = new IconVisitor();
            return ImageUtilities.loadImage(iv.getIconUrl(controller.getMethodTypeFromInterface(me)));
        }

        @Override
        public void openMethod(final MethodModel me, final String implClass, FileObject implClassFO) {
            final List<ElementHandle<ExecutableElement>> methodHandle = new ArrayList<ElementHandle<ExecutableElement>>();
            try {
                if (implClassFO == null) {
                    LOG.log(Level.WARNING, "No fileObject found for class={0}.", implClass);
                }
                JavaSource javaSource = JavaSource.forFileObject(implClassFO);
                javaSource.runUserActionTask(new Task<CompilationController>() {
                    @Override
                    public void run(CompilationController controller) throws IOException {
                        controller.toPhase(JavaSource.Phase.ELEMENTS_RESOLVED);
                        TypeElement typeElement = controller.getElements().getTypeElement(implClass);
                        for (ExecutableElement executableElement : ElementFilter.methodsIn(typeElement.getEnclosedElements())) {
                            if (MethodModelSupport.isSameMethod(controller, executableElement, me)) {
                                methodHandle.add(ElementHandle.create(executableElement));
                            }
                        }
                    }
                }, true);
            } catch (IOException ioe) {
                Exceptions.printStackTrace(ioe);
            }
            if (methodHandle.size() > 0) {
                ElementOpen.open(implClassFO, methodHandle.get(0));
            }
        }

    }

}
