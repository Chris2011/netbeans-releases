/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javascript.bower.file;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.netbeans.api.annotations.common.NullAllowed;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.util.Pair;

/**
 * Class representing project's <tt>bower.json</tt> file.
 */
@MIMEResolver.Registration(displayName = "bower.json", resource = "../resources/bower-resolver.xml", position = 128)
public final class BowerJson extends JsonFile {

    public static final String FILE_NAME = "bower.json"; // NOI18N
    public static final String PROP_DEPENDENCIES = "DEPENDENCIES"; // NOI18N
    public static final String PROP_DEV_DEPENDENCIES = "DEV_DEPENDENCIES"; // NOI18N
    // file content
    public static final String FIELD_DEPENDENCIES = "dependencies"; // NOI18N
    public static final String FIELD_DEV_DEPENDENCIES = "devDependencies"; // NOI18N


    public BowerJson(FileObject directory) {
        super(FILE_NAME, directory);
    }

    @Override
    List<Pair<String, String[]>> watchedFields() {
        return Arrays.asList(
                Pair.of(PROP_DEPENDENCIES, new String[] {FIELD_DEPENDENCIES}),
                Pair.of(PROP_DEV_DEPENDENCIES, new String[] {FIELD_DEV_DEPENDENCIES})
        );
    }

    public BowerDependencies getDependencies() {
        Map<String, String> dependencies = getContentValue(Map.class, BowerJson.FIELD_DEPENDENCIES);
        Map<String, String> devDependencies = getContentValue(Map.class, BowerJson.FIELD_DEV_DEPENDENCIES);
        return new BowerDependencies(dependencies, devDependencies);
    }

    //~ Inner classes

    public static final class BowerDependencies {

        public final Map<String, String> dependencies = new ConcurrentHashMap<>();
        public final Map<String, String> devDependencies = new ConcurrentHashMap<>();


        BowerDependencies(@NullAllowed Map<String, String> dependencies, @NullAllowed Map<String, String> devDependencies) {
            if (dependencies != null) {
                this.dependencies.putAll(dependencies);
            }
            if (devDependencies != null) {
                this.devDependencies.putAll(devDependencies);
            }
        }

        public boolean isEmpty() {
            return dependencies.isEmpty()
                    && devDependencies.isEmpty();
        }

        public int getCount() {
            return dependencies.size() + devDependencies.size();
        }

    }

}
