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

package org.netbeans.modules.cnd.loaders;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.netbeans.modules.cnd.test.BaseTestCase;
import org.netbeans.modules.cnd.utils.MIMENames;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;

/**
 *
 * @author Vladimir Voskresensky
 */
public class CndDataObjectTestCase extends BaseTestCase {
    
    public CndDataObjectTestCase(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testCDataObject() throws Exception {
        File newFile = new File(super.getWorkDir(), "file.c"); // NOI18N
        newFile.createNewFile();
        assertTrue("Not created file " + newFile, newFile.exists());
        FileObject fo = FileUtil.toFileObject(newFile);
        assertNotNull("Not found file object for file" + newFile, fo);
        DataObject dob = DataObject.find(fo);
        assertTrue("data object is not recognized by default infrastructure", dob instanceof CDataObject);
    }
    
    public void testCCDataObject() throws Exception {
        File newFile = new File(super.getWorkDir(), "file.cc"); // NOI18N
        newFile.createNewFile();
        assertTrue("Not created file " + newFile, newFile.exists());
        FileObject fo = FileUtil.toFileObject(newFile);
        assertNotNull("Not found file object for file" + newFile, fo);
        DataObject dob = DataObject.find(fo);
        assertTrue("data object is not recognized by default infrastructure", dob instanceof CCDataObject);
    }

    public void testHDataObject() throws Exception {
        File newFile = new File(super.getWorkDir(), "file.h"); // NOI18N
        newFile.createNewFile();
        assertTrue("Not created file " + newFile, newFile.exists());
        FileObject fo = FileUtil.toFileObject(newFile);
        assertNotNull("Not found file object for file" + newFile, fo);
        DataObject dob = DataObject.find(fo);
        assertTrue("data object is not recognized by default infrastructure", dob instanceof HDataObject);
    }

    public void testHDataObjectWithoutExtension() throws Exception {
        checkHeaderWithoutExtension("headerWithComments", "//    -*- C++ -*-    \n"); // NOI18N
        checkHeaderWithoutExtension("headerWithStandardComments", "//    standard header\n"); // NOI18N
        checkHeaderWithoutExtension("headerWithInclude", "\n\n#include <stdio>\n"); // NOI18N
        checkHeaderWithoutExtension("headerWithPragma", "\n#pragma once\n"); // NOI18N
    }

    private void checkHeaderWithoutExtension(String fileName, CharSequence content) throws Exception {
        File newFile = new File(super.getWorkDir(), fileName); // NOI18N
        newFile.createNewFile();
        assertTrue("Not created file " + newFile, newFile.exists());
        FileObject fo = FileUtil.toFileObject(newFile);
        Writer writer = new OutputStreamWriter(fo.getOutputStream());
        try {
            writer.append(content);
            writer.flush();
        } finally {
            writer.close();
        }
        assertNotNull("Not found file object for file" + newFile, fo);
        String mime = FileUtil.getMIMEType(fo, MIMENames.HEADER_MIME_TYPE);
        assertEquals("header with content " + content + " is not recognized ", MIMENames.HEADER_MIME_TYPE, mime);
        DataObject dob = DataObject.find(fo);
        assertTrue("data object is not recognized by default infrastructure", dob instanceof HDataObject);
    }
}
