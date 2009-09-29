/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.groovy.editor.api;

import java.util.Collections;
import java.util.Map;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.modules.groovy.editor.test.GroovyTestBase;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Martin Adamek
 */
public class GroovyDeclarationFinderTest extends GroovyTestBase {
    
    private final String TEST_BASE = "testfiles/declarationfinder/";

    public GroovyDeclarationFinderTest(String testName) {
        super(testName);
    }

    protected @Override Map<String, ClassPath> createClassPathsForTest() {
        return Collections.singletonMap(
            ClassPath.SOURCE,
            ClassPathSupport.createClassPath(new FileObject[] {
                FileUtil.toFileObject(getDataFile("/testfiles/declarationfinder"))
            })
        );
    }

    // this test is for variables defined and used in the same CU.
    
    public void testField1() throws Exception {
        checkDeclaration(TEST_BASE + "Script.groovy", "        println va^r1", "    def ^var1 = 'aaa'");
    }
    
    // we gotta have a test for class usage:
    
    public void testClass1() throws Exception {
        checkDeclaration(TEST_BASE + "Consumer.groovy", "        Fin^der finder = new Finder()", "class ^Finder {");
    }

    public void testMethod1() throws Exception {
        checkDeclaration(TEST_BASE + "Methods1.groovy", "println get^Name(x)", "^def getName(a) {");
    }

    public void testMethod2() throws Exception {
        checkDeclaration(TEST_BASE + "Methods2.groovy", "println get^Name()", "^def getName() {");
    }
}
