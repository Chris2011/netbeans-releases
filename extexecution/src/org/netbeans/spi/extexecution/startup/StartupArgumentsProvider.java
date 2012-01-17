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
package org.netbeans.spi.extexecution.startup;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.extexecution.startup.StartupArguments;
import org.openide.util.Lookup;

/**
 * Provides the additional arguments to server VM. Typically the server plugin
 * implementor or project will query the arguments via API counterpart
 * {@link StartupArguments}. Of course it is not mandatary to use such
 * arguments and there is no way to force it.
 *
 * @author Petr Hejl
 * @since 1.19
 * @see StartupArguments
 */
public interface StartupArgumentsProvider {

    /**
     * Returns the list of arguments to pass to the server VM for the given
     * start mode.
     *
     * @param context the lookup providing the contract between client
     *             and provider
     * @param mode the VM mode the client is going to use
     * @return the list of arguments to pass to the server VM
     */
    @NonNull
    List<String> getArguments(@NonNull Lookup context, @NonNull StartupArguments.StartMode mode);

    /**
     * Annotation used to properly register the SPI implementations.
     */
    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.TYPE, ElementType.METHOD})
    public @interface Registration {

        /**
         * The human readable description of the provider. May be a bundle key.
         * For example this might be "JRebel", "Profiler" etc.
         */
        String displayName();

        /**
         * Modes to which the provider will respond.
         */
        StartupArguments.StartMode[] startMode();

        /**
         * Position of the provider in the list of providers.
         */
        int position() default Integer.MAX_VALUE;

    }
}
