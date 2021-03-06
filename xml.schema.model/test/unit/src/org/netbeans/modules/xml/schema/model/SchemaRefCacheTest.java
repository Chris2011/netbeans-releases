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

package org.netbeans.modules.xml.schema.model;
import junit.framework.*;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collection;
import org.netbeans.modules.xml.schema.model.impl.RefCacheSupport;
import org.netbeans.modules.xml.schema.model.impl.SchemaModelImpl;
import org.netbeans.modules.xml.xam.Model;
import org.netbeans.modules.xml.xam.dom.NamedComponentReference;

/**
 * The JUnit based test is intended to check the class RefCacheSupport.java
 *
 * @author Nikita Krjukov
 */
public class SchemaRefCacheTest extends TestCase {
    
    private static String TEST_XSD = "resources/SchemaRefCacheTest_Referencing.xsd"; // NOI18N
    private static String TEST_BAD_XSD = "resources/SchemaRefCacheTest_Referencing_invalid.xsd"; // NOI18N
    private static String TEST_DEL_IMPORT_XSD = "resources/SchemaRefCacheTest_Referencing_del_import.xsd"; // NOI18N
    private static String TEST_DEL_INCLUDE_XSD = "resources/SchemaRefCacheTest_Referencing_del_include.xsd"; // NOI18N
    private static String TEST_DEL_REDEFINE_XSD = "resources/SchemaRefCacheTest_Referencing_del_redefine.xsd"; // NOI18N

    private static String TEST_IMPORTED_INVALID_XSD = "resources/SchemaRefCacheTest_imported_invalid.xsd"; // NOI18N
    private static String TEST_INCLUDED_INVALID_XSD = "resources/SchemaRefCacheTest_included_invalid.xsd"; // NOI18N
    private static String TEST_REDEFINED_INVALID_XSD = "resources/SchemaRefCacheTest_redefined_invalid.xsd"; // NOI18N
    
    public SchemaRefCacheTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
    }

    @Override
    protected void tearDown() throws Exception {
        TestCatalogModel.getDefault().clearDocumentPool();
        System.gc(); // it is required to clear schema models cache.
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new SchemaRefCacheTest("testReferencingInvalid")); // NOI18N
        suite.addTest(new SchemaRefCacheTest("testDeleteImport")); // NOI18N
        suite.addTest(new SchemaRefCacheTest("testDeleteInclude")); // NOI18N
        suite.addTest(new SchemaRefCacheTest("testDeleteRedefine")); // NOI18N
        suite.addTest(new SchemaRefCacheTest("testImportNamespaceModified")); // NOI18N
        suite.addTest(new SchemaRefCacheTest("testImportSchemaLocationModified")); // NOI18N
        suite.addTest(new SchemaRefCacheTest("testIncludeSchemaLocationModified")); // NOI18N
        suite.addTest(new SchemaRefCacheTest("testRedefineSchemaLocationModified")); // NOI18N
        suite.addTest(new SchemaRefCacheTest("testImportedInvalid")); // NOI18N
        suite.addTest(new SchemaRefCacheTest("testIncludedInvalid")); // NOI18N
        suite.addTest(new SchemaRefCacheTest("testRedefinedInvalid")); // NOI18N
        suite.addTest(new SchemaRefCacheTest("testImportedTargetNamespaceChanged")); // NOI18N
        suite.addTest(new SchemaRefCacheTest("testIncludedTargetNamespaceChanged")); // NOI18N
        suite.addTest(new SchemaRefCacheTest("testRedefinedTargetNamespaceChanged")); // NOI18N
        suite.addTest(new SchemaRefCacheTest("testImportedTargetNamespaceDeleted")); // NOI18N
        suite.addTest(new SchemaRefCacheTest("testDetachedSchemaGarbageCollected")); // NOI18N
        suite.addTest(new SchemaRefCacheTest("testCacheContainsRelevantComponents")); // NOI18N
        return suite;
    }

    /**
     * Loads 3 referenced (an imported, an included and a redefined).
     * RefCache shoul have 3 items after method is finished. 
     *
     * @param sm
     * @return
     */
    private RefCacheSupport loadReferencedModels(SchemaModel sm) {
        assertNotNull(sm);
        assert sm instanceof SchemaModelImpl;
        SchemaModelImpl smImpl = SchemaModelImpl.class.cast(sm);
        RefCacheSupport cache = smImpl.getRefCacheSupport();
        //
        Schema schema = smImpl.getSchema();
        assertNotNull(schema);
        //
        Collection<GlobalElement> gElements = schema.getElements();
        //
        assertEquals(0, cache.getCachedModelsSize());
        //
        assertEquals(3, gElements.size());
        for (GlobalElement gElem : gElements) {
            NamedComponentReference<? extends GlobalType> typeRef = gElem.getType();
            assertNotNull(typeRef);
            GlobalType gType = typeRef.get();
            assertNotNull(gType);
        }
        //
        assertEquals(3, cache.getCachedModelsSize());
        //
        return cache;
    }

    /**
     * Checks if the cache is discarded after the owning schema model
     * becomes invalid.
     * 
     * @throws java.lang.Exception
     */
    public void testReferencingInvalid() throws Exception {
        SchemaModel sm = Util.loadSchemaModel(TEST_XSD);
        RefCacheSupport cache = loadReferencedModels(sm);
        //
        Util.setDocumentContentTo(sm, TEST_BAD_XSD);
        try {
            sm.sync();
            assertFalse("Did not get IOException", true); // NOI18N
        } catch(IOException ioe) {
            assertEquals(sm.getState(), Model.State.NOT_WELL_FORMED);
        }
        //
        // Cache has to be empty after it was discarded
        assertEquals(0, cache.getCachedModelsSize());
    }

    /**
     * Checks if the imported schema model is deleted from cache after
     * the corresponding import declaration is deleted.
     */
    public void testDeleteImport() throws Exception {
        SchemaModel sm = Util.loadSchemaModel(TEST_XSD);
        RefCacheSupport cache = loadReferencedModels(sm);
        //
        Schema schema = sm.getSchema();
        assertNotNull(schema);
        //
        Collection<Import> imports = schema.getImports();
        assertEquals(1, imports.size());
        Import imp = imports.iterator().next();
        assertNotNull(imp);
        SchemaModelImpl importedSm = cache.getCachedModel(imp);
        assertNotNull(importedSm);
        //
        Util.setDocumentContentTo(sm, TEST_DEL_IMPORT_XSD);
        try {
            sm.sync();
            assertEquals(sm.getState(), Model.State.VALID);
        } catch(IOException ioe) {
            assertFalse("IOException", false);  // NOI18N
        }
        //
        // Check the model's cache is updated
        assertFalse(cache.contains(importedSm));
    }

    /**
     * Checks if the included schema model is deleted from cache after
     * the corresponding include declaration is deleted.
     */
    public void testDeleteInclude() throws Exception {
        SchemaModel sm = Util.loadSchemaModel(TEST_XSD);
        RefCacheSupport cache = loadReferencedModels(sm);
        //
        Schema schema = sm.getSchema();
        assertNotNull(schema);
        //
        Collection<Include> includes = schema.getIncludes();
        assertEquals(1, includes.size());
        Include incl = includes.iterator().next();
        assertNotNull(incl);
        SchemaModelImpl includedSm = cache.getCachedModel(incl);
        assertNotNull(includedSm);
        //
        Util.setDocumentContentTo(sm, TEST_DEL_INCLUDE_XSD);
        try {
            sm.sync();
            assertEquals(sm.getState(), Model.State.VALID);
        } catch(IOException ioe) {
            assertFalse("IOException", false);  // NOI18N
        }
        //
        // Check the model's cache is updated
        assertFalse(cache.contains(includedSm));
    }

    /**
     * Checks if the redefined schema model is deleted from cache after
     * the corresponding redefine declaration is deleted.
     */
    public void testDeleteRedefine() throws Exception {
        SchemaModel sm = Util.loadSchemaModel(TEST_XSD);
        RefCacheSupport cache = loadReferencedModels(sm);
        //
        Schema schema = sm.getSchema();
        assertNotNull(schema);
        //
        Collection<Redefine> redefines = schema.getRedefines();
        assertEquals(1, redefines.size());
        Redefine redef = redefines.iterator().next();
        assertNotNull(redef);
        SchemaModelImpl redefinedSm = cache.getCachedModel(redef);
        assertNotNull(redefinedSm);
        //
        Util.setDocumentContentTo(sm, TEST_DEL_REDEFINE_XSD);
        try {
            sm.sync();
            assertEquals(sm.getState(), Model.State.VALID);
        } catch(IOException ioe) {
            assertFalse("IOException", false);  // NOI18N
        }
        //
        // Check the model's cache is updated
        assertFalse(cache.contains(redefinedSm));
    }

    /**
     * Checks if the imported schema model is removed from the cache after
     * the namespace attribute is modified in the corresponding
     * import declaration.
     *
     * @throws java.lang.Exception
     */
    public void testImportNamespaceModified() throws Exception {
        SchemaModel sm = Util.loadSchemaModel(TEST_XSD);
        RefCacheSupport cache = loadReferencedModels(sm);
        //
        Schema schema = sm.getSchema();
        assertNotNull(schema);
        //
        Collection<Import> imports = schema.getImports();
        assertEquals(1, imports.size());
        Import imp = imports.iterator().next();
        assertNotNull(imp);
        SchemaModelImpl importedSm = cache.getCachedModel(imp);
        assertNotNull(importedSm);
        //
        sm.startTransaction();
        try {
            imp.setNamespace("modifiedNamespace"); // NOI18N
        } finally {
            sm.endTransaction();
        }
        //
        // Check the model's cache is updated
        assertFalse(cache.contains(importedSm));
    }

    /**
     * Checks if the imported schema model is removed from the cache after
     * the schemaLocation attribute is modified in the corresponding
     * import declaration.
     *
     * @throws java.lang.Exception
     */
    public void testImportSchemaLocationModified() throws Exception {
        SchemaModel sm = Util.loadSchemaModel(TEST_XSD);
        RefCacheSupport cache = loadReferencedModels(sm);
        //
        Schema schema = sm.getSchema();
        assertNotNull(schema);
        //
        Collection<Import> imports = schema.getImports();
        assertEquals(1, imports.size());
        Import imp = imports.iterator().next();
        assertNotNull(imp);
        SchemaModelImpl importedSm = cache.getCachedModel(imp);
        assertNotNull(importedSm);
        //
        sm.startTransaction();
        try {
            imp.setSchemaLocation("modifiedLocation"); // NOI18N
        } finally {
            sm.endTransaction();
        }
        //
        // Check the model's cache is updated
        assertFalse(cache.contains(importedSm));
    }

    /**
     * Checks if the included schema model is removed from the cache after
     * the schemaLocation attribute is modified in the corresponding
     * include declaration.
     *
     * @throws java.lang.Exception
     */
    public void testIncludeSchemaLocationModified() throws Exception {
        SchemaModel sm = Util.loadSchemaModel(TEST_XSD);
        RefCacheSupport cache = loadReferencedModels(sm);
        //
        Schema schema = sm.getSchema();
        assertNotNull(schema);
        //
        Collection<Include> includes = schema.getIncludes();
        assertEquals(1, includes.size());
        Include incl = includes.iterator().next();
        assertNotNull(incl);
        SchemaModelImpl includedSm = cache.getCachedModel(incl);
        assertNotNull(includedSm);
        //
        sm.startTransaction();
        try {
            incl.setSchemaLocation("modifiedLocation"); // NOI18N
        } finally {
            sm.endTransaction();
        }
        //
        // Check the model's cache is updated
        assertFalse(cache.contains(includedSm));
    }

    /**
     * Checks if the redefined schema model is removed from the cache after
     * the schemaLocation attribute is modified in the corresponding
     * redefine declaration.
     *
     * @throws java.lang.Exception
     */
    public void testRedefineSchemaLocationModified() throws Exception {
        SchemaModel sm = Util.loadSchemaModel(TEST_XSD);
        RefCacheSupport cache = loadReferencedModels(sm);
        //
        Schema schema = sm.getSchema();
        assertNotNull(schema);
        //
        Collection<Redefine> redefines = schema.getRedefines();
        assertEquals(1, redefines.size());
        Redefine redef = redefines.iterator().next();
        assertNotNull(redef);
        SchemaModelImpl redefinedSm = cache.getCachedModel(redef);
        assertNotNull(redefinedSm);
        //
        sm.startTransaction();
        try {
            redef.setSchemaLocation("modifiedLocation"); // NOI18N
        } finally {
            sm.endTransaction();
        }
        //
        // Check the model's cache is updated
        assertFalse(cache.contains(redefinedSm));
    }

    /**
     * Checks if the imported schema model is removed from the cache after
     * it becomes invalid. 
     *
     * @throws java.lang.Exception
     */
    public void testImportedInvalid() throws Exception {
        SchemaModel sm = Util.loadSchemaModel(TEST_XSD);
        RefCacheSupport cache = loadReferencedModels(sm);
        //
        Schema schema = sm.getSchema();
        assertNotNull(schema);
        //
        Collection<Import> imports = schema.getImports();
        assertEquals(1, imports.size());
        Import imp = imports.iterator().next();
        assertNotNull(imp);
        SchemaModelImpl importedSm = cache.getCachedModel(imp);
        assertNotNull(importedSm);
        //
        Util.setDocumentContentTo(importedSm, TEST_IMPORTED_INVALID_XSD);
        try {
            importedSm.sync();
            assertFalse("Did not get IOException", true); // NOI18N
        } catch(IOException ioe) {
            assertEquals(importedSm.getState(), Model.State.NOT_WELL_FORMED);
        }
        //
        // Check the model's cache is updated
        assertFalse(cache.contains(importedSm));
    }

    /**
     * Checks if the included schema model is removed from the cache after
     * it becomes invalid.
     *
     * @throws java.lang.Exception
     */
    public void testIncludedInvalid() throws Exception {
        SchemaModel sm = Util.loadSchemaModel(TEST_XSD);
        RefCacheSupport cache = loadReferencedModels(sm);
        //
        Schema schema = sm.getSchema();
        assertNotNull(schema);
        //
        Collection<Include> includes = schema.getIncludes();
        assertEquals(1, includes.size());
        Include incl = includes.iterator().next();
        assertNotNull(incl);
        SchemaModelImpl includedSm = cache.getCachedModel(incl);
        assertNotNull(includedSm);
        //
        Util.setDocumentContentTo(includedSm, TEST_INCLUDED_INVALID_XSD);
        try {
            includedSm.sync();
            assertFalse("Did not get IOException", true); // NOI18N
        } catch(IOException ioe) {
            assertEquals(includedSm.getState(), Model.State.NOT_WELL_FORMED);
        }
        //
        // Check the model's cache is updated
        assertFalse(cache.contains(includedSm));
    }

    /**
     * Checks if the redefined schema model is removed from the cache after
     * it becomes invalid.
     *
     * @throws java.lang.Exception
     */
    public void testRedefinedInvalid() throws Exception {
        SchemaModel sm = Util.loadSchemaModel(TEST_XSD);
        RefCacheSupport cache = loadReferencedModels(sm);
        //
        Schema schema = sm.getSchema();
        assertNotNull(schema);
        //
        Collection<Redefine> redefines = schema.getRedefines();
        assertEquals(1, redefines.size());
        Redefine redef = redefines.iterator().next();
        assertNotNull(redef);
        SchemaModelImpl redefinedSm = cache.getCachedModel(redef);
        assertNotNull(redefinedSm);
        //
        Util.setDocumentContentTo(redefinedSm, TEST_REDEFINED_INVALID_XSD);
        try {
            redefinedSm.sync();
            assertFalse("Did not get IOException", true); // NOI18N
        } catch(IOException ioe) {
            assertEquals(redefinedSm.getState(), Model.State.NOT_WELL_FORMED);
        }
        //
        // Check the model's cache is updated
        assertFalse(cache.contains(redefinedSm));
    }

    /**
     * Checks if the imported schema model is removed from the cache after
     * the targetNamespace attribute is changed (imported schema)
     *
     * @throws java.lang.Exception
     */
    public void testImportedTargetNamespaceChanged() throws Exception {
        SchemaModel sm = Util.loadSchemaModel(TEST_XSD);
        RefCacheSupport cache = loadReferencedModels(sm);
        //
        Schema schema = sm.getSchema();
        assertNotNull(schema);
        //
        Collection<Import> imports = schema.getImports();
        assertEquals(1, imports.size());
        Import imp = imports.iterator().next();
        assertNotNull(imp);
        SchemaModelImpl importedSm = cache.getCachedModel(imp);
        assertNotNull(importedSm);
        //
        importedSm.startTransaction();
        try {
            Schema importedSchema = importedSm.getSchema();
            assertNotNull(importedSchema);
            importedSchema.setTargetNamespace("modifiedTargetNamespace"); // NOI18N
        } finally {
            importedSm.endTransaction();
        }
        //
        // Check the model's cache is updated
        assertFalse(cache.contains(importedSm));
    }

    /**
     * Checks if the included schema model is removed from the cache after
     * the targetNamespace attribute is changed (included schema)
     *
     * @throws java.lang.Exception
     */
    public void testIncludedTargetNamespaceChanged() throws Exception {
        SchemaModel sm = Util.loadSchemaModel(TEST_XSD);
        RefCacheSupport cache = loadReferencedModels(sm);
        //
        Schema schema = sm.getSchema();
        assertNotNull(schema);
        //
        Collection<Include> includes = schema.getIncludes();
        assertEquals(1, includes.size());
        Include incl = includes.iterator().next();
        assertNotNull(incl);
        SchemaModelImpl includedSm = cache.getCachedModel(incl);
        assertNotNull(includedSm);
        //
        includedSm.startTransaction();
        try {
            Schema importedSchema = includedSm.getSchema();
            assertNotNull(importedSchema);
            importedSchema.setTargetNamespace("modifiedTargetNamespace"); // NOI18N
        } finally {
            includedSm.endTransaction();
        }
        //
        // Check the model's cache is updated
        assertFalse(cache.contains(includedSm));
    }

    /**
     * Checks if the redefinded schema model is removed from the cache after
     * the targetNamespace attribute is changed (redefined schema)
     *
     * @throws java.lang.Exception
     */
    public void testRedefinedTargetNamespaceChanged() throws Exception {
        SchemaModel sm = Util.loadSchemaModel(TEST_XSD);
        RefCacheSupport cache = loadReferencedModels(sm);
        //
        Schema schema = sm.getSchema();
        assertNotNull(schema);
        //
        Collection<Redefine> redefines = schema.getRedefines();
        assertEquals(1, redefines.size());
        Redefine redef = redefines.iterator().next();
        assertNotNull(redef);
        SchemaModelImpl redefinedSm = cache.getCachedModel(redef);
        assertNotNull(redefinedSm);
        //
        redefinedSm.startTransaction();
        try {
            Schema importedSchema = redefinedSm.getSchema();
            assertNotNull(importedSchema);
            importedSchema.setTargetNamespace("modifiedTargetNamespace"); // NOI18N
        } finally {
            redefinedSm.endTransaction();
        }
        //
        // Check the model's cache is updated
        assertFalse(cache.contains(redefinedSm));
    }

    /**
     * Checks if the imported schema model is removed from the cache after
     * the targetNamespace attribute is deleted (imported schema)
     *
     * @throws java.lang.Exception
     */
    public void testImportedTargetNamespaceDeleted() throws Exception {
        SchemaModel sm = Util.loadSchemaModel(TEST_XSD);
        RefCacheSupport cache = loadReferencedModels(sm);
        //
        Schema schema = sm.getSchema();
        assertNotNull(schema);
        //
        Collection<Import> imports = schema.getImports();
        assertEquals(1, imports.size());
        Import imp = imports.iterator().next();
        assertNotNull(imp);
        SchemaModelImpl importedSm = cache.getCachedModel(imp);
        assertNotNull(importedSm);
        //
        importedSm.startTransaction();
        try {
            Schema importedSchema = importedSm.getSchema();
            assertNotNull(importedSchema);
            importedSchema.setTargetNamespace(null); // NOI18N
        } finally {
            importedSm.endTransaction();
        }
        //
        // Check the model's cache is updated
        assertFalse(cache.contains(importedSm));
    }

    /**
     * Checks if the imported schema model is deleted from cache after
     * the corresponding import declaration is deleted.
     */
    public void testDetachedSchemaGarbageCollected() throws Exception {
        SchemaModel sm = Util.loadSchemaModel(TEST_XSD);
        RefCacheSupport cache = loadReferencedModels(sm);
        //
        Schema schema = sm.getSchema();
        assertNotNull(schema);
        //
        Collection<Import> imports = schema.getImports();
        assertEquals(1, imports.size());
        Import imp = imports.iterator().next();
        assertNotNull(imp);
        SchemaModelImpl importedSm = cache.getCachedModel(imp);
        assertNotNull(importedSm);
        //
        // Add weak reference to the base schema model which is going to be
        // garbage collected.
        WeakReference<SchemaModel> smWeakRef = new WeakReference<SchemaModel>(sm);
        //
        // Clear all reference to the base schema model to allow it to be
        // garbage collected.
        sm = null;
        cache = null;
        schema = null;
        imports = null;
        imp = null;
        //
        System.gc();
        System.gc();
        System.gc();
        System.gc();
        System.gc();
        Thread.sleep(100);
        //
        sm = smWeakRef.get();
        assertNull("Detached schema model has to be garbage collected", sm); // NOI18N
    }

    /**
     * Checks that a model caches only relevant schema model references.
     * See issue #168376
     * 
     * @throws java.lang.Exception
     */
    public void testCacheContainsRelevantComponents() throws Exception {
        SchemaModel sm;
        sm = Util.loadSchemaModel("resources/address.xsd"); // NOI18N // the source of incorrect components
        sm = Util.loadSchemaModel(TEST_XSD);
        RefCacheSupport cache = loadReferencedModels(sm);
        //
        // Cache has to contain 3 items
        assertEquals(3, cache.getCachedModelsSize());
        assertEquals(0, cache.checkKeys());
    }

}
