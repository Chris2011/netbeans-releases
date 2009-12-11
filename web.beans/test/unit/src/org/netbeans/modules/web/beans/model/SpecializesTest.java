/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.web.beans.model;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import org.netbeans.modules.j2ee.metadata.model.api.MetadataModel;
import org.netbeans.modules.j2ee.metadata.model.api.MetadataModelAction;
import org.netbeans.modules.j2ee.metadata.model.support.TestUtilities;
import org.netbeans.modules.web.beans.api.model.Result;
import org.netbeans.modules.web.beans.api.model.WebBeansModel;
import org.netbeans.modules.web.beans.impl.model.results.ResultImpl;


/**
 * @author ads
 *
 */
public class SpecializesTest extends CommonTestCase {

    public SpecializesTest( String testName ) {
        super(testName);
    }
    
    public void testA(){
    }


    public void testSimpleTypeSpecializes() throws IOException, InterruptedException{
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/CustomBinding.java",
                "package foo; " +
                "import static java.lang.annotation.ElementType.METHOD; "+
                "import static java.lang.annotation.ElementType.FIELD; "+
                "import static java.lang.annotation.ElementType.PARAMETER; "+
                "import static java.lang.annotation.ElementType.TYPE; "+
                "import static java.lang.annotation.RetentionPolicy.RUNTIME; "+
                "import javax.enterprise.inject.*; "+
                "import javax.inject.*; "+
                "import java.lang.annotation.*; "+
                "@Qualifier " +
                "@Retention(RUNTIME) "+
                "@Target({METHOD, FIELD, PARAMETER, TYPE}) "+
                "public @interface CustomBinding  {}");
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/CustomClass.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "import javax.inject.*; "+
                "public class CustomClass  {" +
                " @Inject @CustomBinding One myField; "+
                "}");
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/One.java",
                "package foo; " +
                "public class One  {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Two.java",
                "package foo; " +
                "@CustomBinding "+
                "public class Two  extends One {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Three.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "@Specializes "+
                "public class Three extends Two {}" );
        
        inform("start simple specializes test");
        
        
        TestWebBeansModelImpl modelImpl = createModelImpl();
        final TestWebBeansModelProviderImpl provider = modelImpl.getProvider();
        MetadataModel<WebBeansModel> testModel = modelImpl.createTestModel();
        
        testModel.runReadAction( new MetadataModelAction<WebBeansModel,Void>(){

            public Void run( WebBeansModel model ) throws Exception {
                TypeMirror mirror = model.resolveType( "foo.CustomClass" );
                Element clazz = ((DeclaredType)mirror).asElement();
                List<? extends Element> children = clazz.getEnclosedElements();
                for (Element element : children) {
                    if (element instanceof VariableElement) {
                        assert element.getSimpleName().contentEquals("myField");
                        inform("test injectables for 'myField'");
                        Result result = provider.findVariableInjectable(
                                    (VariableElement)element, null);
                        
                        assertNotNull(result);
                        assertTrue(result instanceof ResultImpl);
                        Set<TypeElement> typeElements = ((ResultImpl) result).getTypeElements();
                        Set<Element> productions = ((ResultImpl) result).getProductions();

                        assertEquals(2, typeElements.size());
                        assertEquals(0, productions.size());
                        
                        boolean twoFound = false;
                        boolean threeFound = false;
                        for (TypeElement injectable : typeElements) {
                            Name qualifiedName = injectable.getQualifiedName();
                            if (qualifiedName.contentEquals("foo.Two")) {
                                twoFound = true;
                            }
                            else if (qualifiedName.contentEquals("foo.Three")) {
                                threeFound = true;
                            }
                        }
                        assertTrue("foo.Two is eligible for injection , "
                                + "but not found", twoFound);
                        assertTrue("foo.Three is eligible for injection , "
                                + "but not found", threeFound);
                    }
                }
                return null;
            }
        });
    }
    
    public void testMergeBindingsSpecializes() throws IOException, InterruptedException{
        TestUtilities.copyStringToFileObject(srcFO, "foo/CustomBinding.java",
                "package foo; " +
                "import static java.lang.annotation.ElementType.METHOD; "+
                "import static java.lang.annotation.ElementType.FIELD; "+
                "import static java.lang.annotation.ElementType.PARAMETER; "+
                "import static java.lang.annotation.ElementType.TYPE; "+
                "import static java.lang.annotation.RetentionPolicy.RUNTIME; "+
                "import javax.enterprise.inject.*; "+
                "import java.lang.annotation.*; "+
                "import javax.inject.*; "+
                "@Qualifier " +
                "@Retention(RUNTIME) "+
                "@Target({METHOD, FIELD, PARAMETER, TYPE}) "+
                "public @interface CustomBinding  {}");
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Binding1.java",
                "package foo; " +
                "import static java.lang.annotation.ElementType.METHOD; "+
                "import static java.lang.annotation.ElementType.FIELD; "+
                "import static java.lang.annotation.ElementType.PARAMETER; "+
                "import static java.lang.annotation.ElementType.TYPE; "+
                "import static java.lang.annotation.RetentionPolicy.RUNTIME; "+
                "import javax.enterprise.inject.*; "+
                "import java.lang.annotation.*; "+
                "import javax.inject.*; "+
                "@Qualifier " +
                "@Retention(RUNTIME) "+
                "@Target({METHOD, FIELD, PARAMETER, TYPE}) "+
                "public @interface Binding1  {}");
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Binding2.java",
                "package foo; " +
                "import static java.lang.annotation.ElementType.METHOD; "+
                "import static java.lang.annotation.ElementType.FIELD; "+
                "import static java.lang.annotation.ElementType.PARAMETER; "+
                "import static java.lang.annotation.ElementType.TYPE; "+
                "import static java.lang.annotation.RetentionPolicy.RUNTIME; "+
                "import javax.enterprise.inject.*; "+
                "import java.lang.annotation.*; "+
                "import javax.inject.*; "+
                "@Qualifier " +
                "@Retention(RUNTIME) "+
                "@Target({METHOD, FIELD, PARAMETER, TYPE}) "+
                "public @interface Binding2  {}");
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/CustomClass.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "import javax.inject.*; "+
                "public class CustomClass  {" +
                " @Inject @CustomBinding @Binding1 @Binding2 One myField; "+
                "}");
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/One.java",
                "package foo; " +
                "@Binding1 " +
                "public class One  {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Two.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "@Specializes "+
                "@Binding2 "+
                "public class Two  extends One {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Three.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "@Specializes "+
                "@CustomBinding "+
                "public class Three extends Two {}" );
        
        inform("start merged specializes test");
        
        
        
        TestWebBeansModelImpl modelImpl = createModelImpl();
        final TestWebBeansModelProviderImpl provider = modelImpl.getProvider();
        MetadataModel<WebBeansModel> testModel = modelImpl.createTestModel();
        
        testModel.runReadAction( new MetadataModelAction<WebBeansModel,Void>(){

            public Void run( WebBeansModel model ) throws Exception {
                TypeMirror mirror = model.resolveType( "foo.CustomClass" );
                Element clazz = ((DeclaredType)mirror).asElement();
                List<? extends Element> children = clazz.getEnclosedElements();
                for (Element element : children) {
                    if (element instanceof VariableElement) {
                        assert element.getSimpleName().contentEquals("myField");
                        inform("test injectables for 'myField'");
                        
                        Result result = provider.findVariableInjectable(
                                (VariableElement)element, null);

                        assertNotNull(result);
                        assertTrue(result instanceof ResultImpl);
                        Set<TypeElement> typeElements = ((ResultImpl) result).getTypeElements();
                        Set<Element> productions = ((ResultImpl) result).getProductions();

                        assertEquals(1, typeElements.size());
                        assertEquals(0, productions.size());

                        TypeElement injectable = typeElements.iterator().next();
                        assertNotNull(injectable);
                        assertEquals("foo.Three", ((TypeElement) injectable)
                                .getQualifiedName().toString());
                    }
                }
                return null;
            }
        });
    }
    
    public void testDefaultSpecializes() throws IOException, InterruptedException{
        TestUtilities.copyStringToFileObject(srcFO, "foo/Binding1.java",
                "package foo; " +
                "import static java.lang.annotation.ElementType.METHOD; "+
                "import static java.lang.annotation.ElementType.FIELD; "+
                "import static java.lang.annotation.ElementType.PARAMETER; "+
                "import static java.lang.annotation.ElementType.TYPE; "+
                "import static java.lang.annotation.RetentionPolicy.RUNTIME; "+
                "import javax.enterprise.inject.*; "+
                "import java.lang.annotation.*; "+
                "import javax.inject.*; "+
                "@Qualifier " +
                "@Retention(RUNTIME) "+
                "@Target({METHOD, FIELD, PARAMETER, TYPE}) "+
                "public @interface Binding1  {}");
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Binding2.java",
                "package foo; " +
                "import static java.lang.annotation.ElementType.METHOD; "+
                "import static java.lang.annotation.ElementType.FIELD; "+
                "import static java.lang.annotation.ElementType.PARAMETER; "+
                "import static java.lang.annotation.ElementType.TYPE; "+
                "import static java.lang.annotation.RetentionPolicy.RUNTIME; "+
                "import javax.enterprise.inject.*; "+
                "import java.lang.annotation.*; "+
                "import javax.inject.*; "+
                "@Qualifier " +
                "@Retention(RUNTIME) "+
                "@Target({METHOD, FIELD, PARAMETER, TYPE}) "+
                "public @interface Binding2  {}");
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/CustomClass.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "import javax.inject.*; "+
                "public class CustomClass  {" +
                " @Inject @Default Two myField1; "+
                " @Inject @Default Three myField2; "+
                " @Inject @Default @Binding2 @Binding1 One1 myField3; "+
                "}");
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/One.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "@Default " +
                "public class One  {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Two.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "@Specializes "+
                "@Binding2 "+
                "public class Two  extends One {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/One1.java",
                "package foo; " +
                "@Binding1 "+
                "public class One1  {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Two1.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "@Specializes "+
                "@Binding2 "+
                "public class Two1  extends One1 {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Three.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "@Specializes "+
                "public class Three  extends Two1 {}" );
        
        inform("start @Default specializes test");
        
        TestWebBeansModelImpl modelImpl = createModelImpl();
        final TestWebBeansModelProviderImpl provider = modelImpl.getProvider();
        MetadataModel<WebBeansModel> testModel = modelImpl.createTestModel();
        
        testModel.runReadAction( new MetadataModelAction<WebBeansModel,Void>(){

            public Void run( WebBeansModel model ) throws Exception {
                TypeMirror mirror = model.resolveType( "foo.CustomClass" );
                Element clazz = ((DeclaredType)mirror).asElement();
                List<? extends Element> children = clazz.getEnclosedElements();
                Set<String> names = new HashSet<String>();
                for (Element element : children) {
                    if ( element instanceof VariableElement ){
                        names.add( element.getSimpleName().toString());
                        if ( element.getSimpleName().contentEquals("myField1")){
                            check1( (VariableElement)element , provider );
                        }
                        else if ( element.getSimpleName().contentEquals("myField2")){
                            check2( (VariableElement)element , provider );
                        }
                        else if ( element.getSimpleName().contentEquals("myField3")){
                            check3( (VariableElement)element , provider );
                        }
                    }
                }
                assert names.contains("myField1");
                assert names.contains("myField2");
                assert names.contains("myField3");
                return null;
            }
        });
    }

    protected void check1( VariableElement element , 
            TestWebBeansModelProviderImpl provider ) 
    {
        Result result = provider.findVariableInjectable(element, null);

        assertNotNull(result);
        assertTrue(result instanceof ResultImpl);
        Set<TypeElement> typeElements = ((ResultImpl) result).getTypeElements();
        Set<Element> productions = ((ResultImpl) result).getProductions();

        assertEquals(1, typeElements.size());
        assertEquals(0, productions.size());

        TypeElement injectable = typeElements.iterator().next();
        assertNotNull(injectable);
        
        assertNotNull( injectable );
        assertEquals( "foo.Two", injectable.getQualifiedName().toString());        
    }
    
    protected void check2( VariableElement element, 
            TestWebBeansModelProviderImpl provider)

    {
        Result result = provider.findVariableInjectable(element, null);

        assertNotNull(result);
        assertTrue(result instanceof ResultImpl);
        Set<TypeElement> typeElements = ((ResultImpl) result).getTypeElements();
        Set<Element> productions = ((ResultImpl) result).getProductions();

        assertEquals(1, typeElements.size());
        assertEquals(0, productions.size());

        TypeElement injectable = typeElements.iterator().next();
        assertNotNull(injectable);
        
        assertEquals("foo.Three", injectable.getQualifiedName()
                .toString());
    }
    
    protected void check3( VariableElement element, 
            TestWebBeansModelProviderImpl provider )
    {
        check2(element, provider);
    }
    
    public void testSimpleProductionSpecializes() throws IOException, InterruptedException{
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/CustomBinding.java",
                "package foo; " +
                "import static java.lang.annotation.ElementType.METHOD; "+
                "import static java.lang.annotation.ElementType.FIELD; "+
                "import static java.lang.annotation.ElementType.PARAMETER; "+
                "import static java.lang.annotation.ElementType.TYPE; "+
                "import static java.lang.annotation.RetentionPolicy.RUNTIME; "+
                "import javax.enterprise.inject.*; "+
                "import java.lang.annotation.*; "+
                "import javax.inject.*; "+
                "@Qualifier " +
                "@Retention(RUNTIME) "+
                "@Target({METHOD, FIELD, PARAMETER, TYPE}) "+
                "public @interface CustomBinding  {}");
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/CustomClass.java",
                "package foo; " +
                "import javax.inject.*; "+
                "public class CustomClass  {" +
                " @Inject @CustomBinding int myField; "+
                "}");
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/One.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "import javax.inject.*; "+
                "public class One  {" +
                " @CustomBinding @Produces int getIndex(){ return 0;} "+
                "}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Two.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "public class Two  extends One {" +
                " @Produces @Specializes int getIndex(){return 0;} "+
                "}" );
        
        inform("start simple specializes test for production methods");
        
        TestWebBeansModelImpl modelImpl = createModelImpl();
        final TestWebBeansModelProviderImpl provider = modelImpl.getProvider();
        MetadataModel<WebBeansModel> testModel = modelImpl.createTestModel();
        
        testModel.runReadAction( new MetadataModelAction<WebBeansModel,Void>(){

            public Void run( WebBeansModel model ) throws Exception {
                TypeMirror mirror = model.resolveType( "foo.CustomClass" );
                Element clazz = ((DeclaredType)mirror).asElement();
                List<? extends Element> children = clazz.getEnclosedElements();
                for (Element element : children) {
                    if (element instanceof VariableElement) {
                        assert element.getSimpleName().contentEquals("myField");
                        inform("test injectables for 'myField'");
                        
                        Result result = provider.findVariableInjectable(
                                (VariableElement)element, null);

                        assertNotNull(result);
                        assertTrue(result instanceof ResultImpl);
                        Set<TypeElement> typeElements = ((ResultImpl) result).
                            getTypeElements();
                        Set<Element> productions = ((ResultImpl) result).
                            getProductions();

                        assertEquals(0, typeElements.size());
                        assertEquals(2, productions.size());
                        
                        for (Element injectable : productions) {
                            assertTrue("injectbale " + element
                                    + " should be production method ",
                                    injectable instanceof ExecutableElement);
                            Name qualifiedName = injectable.getSimpleName();
                            assertEquals("getIndex", qualifiedName.toString());
                        }
                    }
                }
                return null;
            }
        });
    }
    
    public void testMergeProductionSpecializes() throws IOException, InterruptedException{
        TestUtilities.copyStringToFileObject(srcFO, "foo/CustomBinding.java",
                "package foo; " +
                "import static java.lang.annotation.ElementType.METHOD; "+
                "import static java.lang.annotation.ElementType.FIELD; "+
                "import static java.lang.annotation.ElementType.PARAMETER; "+
                "import static java.lang.annotation.ElementType.TYPE; "+
                "import static java.lang.annotation.RetentionPolicy.RUNTIME; "+
                "import javax.enterprise.inject.*; "+
                "import javax.inject.*; "+
                "import java.lang.annotation.*; "+
                "@Qualifier " +
                "@Retention(RUNTIME) "+
                "@Target({METHOD, FIELD, PARAMETER, TYPE}) "+
                "public @interface CustomBinding  {}");
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Binding1.java",
                "package foo; " +
                "import static java.lang.annotation.ElementType.METHOD; "+
                "import static java.lang.annotation.ElementType.FIELD; "+
                "import static java.lang.annotation.ElementType.PARAMETER; "+
                "import static java.lang.annotation.ElementType.TYPE; "+
                "import static java.lang.annotation.RetentionPolicy.RUNTIME; "+
                "import javax.enterprise.inject.*; "+
                "import java.lang.annotation.*; "+
                "import javax.inject.*; "+
                "@Qualifier " +
                "@Retention(RUNTIME) "+
                "@Target({METHOD, FIELD, PARAMETER, TYPE}) "+
                "public @interface Binding1  {}");
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Binding2.java",
                "package foo; " +
                "import static java.lang.annotation.ElementType.METHOD; "+
                "import static java.lang.annotation.ElementType.FIELD; "+
                "import static java.lang.annotation.ElementType.PARAMETER; "+
                "import static java.lang.annotation.ElementType.TYPE; "+
                "import static java.lang.annotation.RetentionPolicy.RUNTIME; "+
                "import javax.enterprise.inject.*; "+
                "import java.lang.annotation.*; "+
                "import javax.inject.*; "+
                "@Qualifier " +
                "@Retention(RUNTIME) "+
                "@Target({METHOD, FIELD, PARAMETER, TYPE}) "+
                "public @interface Binding2  {}");
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/CustomClass.java",
                "package foo; " +
                "import javax.inject.*; "+
                "public class CustomClass  {" +
                " @Inject @CustomBinding @Binding1 @Binding2 int myField; "+
                "}");
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/One.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "public class One  {" +
                " @Produces @CustomBinding int getIndex(){ return 0; } " +
                "}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Two.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "public class Two  extends One {" +
                " @Produces @Specializes @Binding1 int getIndex(){ return 0; } " +
                "}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Three.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "public class Three extends Two {" +
                " @Produces @Specializes @Binding2 int getIndex(){ return 0; } " +
                "}" );
        
        inform("start merged specializes test for production method");
        
        TestWebBeansModelImpl modelImpl = createModelImpl();
        final TestWebBeansModelProviderImpl provider = modelImpl.getProvider();
        MetadataModel<WebBeansModel> testModel = modelImpl.createTestModel();
        
        testModel.runReadAction(
                new MetadataModelAction<WebBeansModel, Void>() {

                    public Void run( WebBeansModel model ) throws Exception {
                        TypeMirror mirror = model
                                .resolveType("foo.CustomClass");
                        Element clazz = ((DeclaredType) mirror).asElement();
                        List<? extends Element> children = clazz
                                .getEnclosedElements();
                        for (Element element : children) {
                            if (element instanceof VariableElement) {
                                assert element.getSimpleName().contentEquals(
                                        "myField");
                                inform("test injectables for 'myField'");
                                
                                Result result = provider.findVariableInjectable(
                                        (VariableElement)element, null);

                                assertNotNull(result);
                                assertTrue(result instanceof ResultImpl);
                                Set<TypeElement> typeElements = ((ResultImpl) 
                                        result).getTypeElements();
                                Set<Element> productions = ((ResultImpl) 
                                        result).getProductions();

                                assertEquals(0, typeElements.size());
                                assertEquals(1, productions.size());
                                
                                Element injectable = productions.iterator().next();
                                
                                assertNotNull( injectable );
                                
                                assertTrue ("Injectable element should be " +
                                        "a production method",
                                        injectable instanceof ExecutableElement );
                                assertEquals( "getIndex",
                                        injectable.getSimpleName().toString());

                                Element enclosingElement = injectable.getEnclosingElement();
                                assertTrue( enclosingElement instanceof TypeElement);
                                
                                assertEquals("foo.Three",
                                        ((TypeElement) enclosingElement)
                                                .getQualifiedName().toString());
                            }
                        }
                        return null;
                    }
                });
    }
    
    public void testDefaultProductionSpecializes() throws IOException, InterruptedException{
        TestUtilities.copyStringToFileObject(srcFO, "foo/Binding1.java",
                "package foo; " +
                "import static java.lang.annotation.ElementType.METHOD; "+
                "import static java.lang.annotation.ElementType.FIELD; "+
                "import static java.lang.annotation.ElementType.PARAMETER; "+
                "import static java.lang.annotation.ElementType.TYPE; "+
                "import static java.lang.annotation.RetentionPolicy.RUNTIME; "+
                "import javax.enterprise.inject.*; "+
                "import javax.inject.*; "+
                "import java.lang.annotation.*; "+
                "@Qualifier " +
                "@Retention(RUNTIME) "+
                "@Target({METHOD, FIELD, PARAMETER, TYPE}) "+
                "public @interface Binding1  {}");
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Binding2.java",
                "package foo; " +
                "import static java.lang.annotation.ElementType.METHOD; "+
                "import static java.lang.annotation.ElementType.FIELD; "+
                "import static java.lang.annotation.ElementType.PARAMETER; "+
                "import static java.lang.annotation.ElementType.TYPE; "+
                "import static java.lang.annotation.RetentionPolicy.RUNTIME; "+
                "import javax.enterprise.inject.*; "+
                "import javax.inject.*; "+
                "import java.lang.annotation.*; "+
                "@Qualifier " +
                "@Retention(RUNTIME) "+
                "@Target({METHOD, FIELD, PARAMETER, TYPE}) "+
                "public @interface Binding2  {}");
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/CustomClass.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "import javax.inject.*; "+
                "public class CustomClass  {" +
                " @Inject @Default @Binding1 int myField1; "+
                " @Inject @Default @Binding2 @Binding1 boolean myField2; "+
                "}");
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/One.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "public class One  {" +
                " @Produces @Default int getIndex(){ return 0;} "+
                "}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Two.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "public class Two  extends One {" +
                " @Produces @Specializes @Binding1 int getIndex(){ return 0;} "+
                "}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/One1.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "public class One1  {" +
                " @Produces @Binding1 boolean isNull(){ return true;} "+
                "}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Two1.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "public class Two1  extends One1 {" +
                " @Produces @Specializes @Binding2 boolean isNull(){ return true;} "+
                "}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Three.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "public class Three  extends Two1 {" +
                " @Produces @Specializes boolean isNull(){ return true;} "+
                "}" );
        
        inform("start @Default specializes test for production method");
        
        TestWebBeansModelImpl modelImpl = createModelImpl();
        final TestWebBeansModelProviderImpl provider = modelImpl.getProvider();
        MetadataModel<WebBeansModel> testModel = modelImpl.createTestModel();
        
        testModel.runReadAction( new MetadataModelAction<WebBeansModel,Void>(){

            public Void run( WebBeansModel model ) throws Exception {
                TypeMirror mirror = model.resolveType( "foo.CustomClass" );
                Element clazz = ((DeclaredType)mirror).asElement();
                List<? extends Element> children = clazz.getEnclosedElements();
                Set<String> names = new HashSet<String>();
                for (Element element : children) {
                    if ( element instanceof VariableElement ){
                        names.add( element.getSimpleName().toString());
                        if ( element.getSimpleName().contentEquals("myField1")){
                            checkProduces1( (VariableElement)element , provider );
                        }
                        else if ( element.getSimpleName().contentEquals("myField2")){
                            checkProduces2( (VariableElement)element , provider );
                        }
                    }
                }
                assert names.contains("myField1");
                assert names.contains("myField2");
                return null;
            }
        });
    }
    
    protected void checkProduces1( VariableElement element , 
            TestWebBeansModelProviderImpl provider )
    {
        Result result = provider.findVariableInjectable(element, null);

        assertNotNull(result);
        assertTrue(result instanceof ResultImpl);
        Set<TypeElement> typeElements = ((ResultImpl) result).getTypeElements();
        Set<Element> productions = ((ResultImpl) result).getProductions();

        assertEquals(0, typeElements.size());
        assertEquals(1, productions.size());
        
        Element injectable = productions.iterator().next();
        assertNotNull(injectable);
        assertTrue("Injectable element should be a production method",
                injectable instanceof ExecutableElement);
        assertEquals("getIndex", injectable.getSimpleName().toString());
        
        Element enclosingElement = injectable.getEnclosingElement();
        assert enclosingElement instanceof TypeElement;
        
        assertEquals("foo.Two",  
                ((TypeElement)enclosingElement).getQualifiedName().toString());
    }
    
    protected void checkProduces2( VariableElement element , 
            TestWebBeansModelProviderImpl provider )
    {
        Result result = provider.findVariableInjectable(element, null);

        assertNotNull(result);
        assertTrue(result instanceof ResultImpl);
        Set<TypeElement> typeElements = ((ResultImpl) result).getTypeElements();
        Set<Element> productions = ((ResultImpl) result).getProductions();

        assertEquals(0, typeElements.size());
        assertEquals(1, productions.size());
        
        Element injectable = productions.iterator().next();
        assertNotNull(injectable);
        
        assertTrue("Injectable element should be a production method",
                injectable instanceof ExecutableElement);
        assertEquals("isNull", injectable.getSimpleName().toString());

        Element enclosingElement = injectable.getEnclosingElement();
        assert enclosingElement instanceof TypeElement;

        assertEquals("foo.Three", ((TypeElement) enclosingElement)
                .getQualifiedName().toString());
    }
}
