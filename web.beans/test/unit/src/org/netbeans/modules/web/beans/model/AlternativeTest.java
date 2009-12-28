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
package org.netbeans.modules.web.beans.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import org.netbeans.modules.j2ee.metadata.model.api.MetadataModel;
import org.netbeans.modules.j2ee.metadata.model.api.MetadataModelAction;
import org.netbeans.modules.j2ee.metadata.model.support.TestUtilities;
import org.netbeans.modules.web.beans.api.model.Result;
import org.netbeans.modules.web.beans.api.model.WebBeansModel;


/**
 * @author ads
 *
 */
public class AlternativeTest extends CommonTestCase {

    public AlternativeTest( String testName ) {
        super(testName);
    }

    public void testAlternativeDisabled() throws IOException{
        TestUtilities.copyStringToFileObject(srcFO, "beans.xml", 
                "<?xml  version='1.0' encoding='UTF-8'?> " +
                "<beans xmlns=\"http://java.sun.com/xml/ns/javaee\">" +
                "<alternatives>" +
                "</alternatives> " +
                "</beans>");
        
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

        
        TestUtilities.copyStringToFileObject(srcFO, "foo/One.java",
                "package foo; " +
                "public class One {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Two.java",
                "package foo; " +
                "@Binding1 "+
                "public class Two extends One {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Three.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "@Binding1 "+
                "@Alternative "+
                "public class Three extends One {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/One1.java",
                "package foo; " +
                "public class One1 {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Two1.java",
                "package foo; " +
                "@Binding1 @Binding2 "+
                "public class Two1 extends One1 {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Four.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "@Binding1 "+
                "@Stereotype1 "+
                "public class Four extends One1{}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Five.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "@Binding2 @Binding1 "+
                "@Stereotype2 "+
                "public class Five extends One1{}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Stereotype1.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "import static java.lang.annotation.ElementType.METHOD; "+
                "import static java.lang.annotation.ElementType.FIELD; "+
                "import static java.lang.annotation.ElementType.TYPE; "+
                "import static java.lang.annotation.RetentionPolicy.RUNTIME; "+
                "import java.lang.annotation.*; "+
                "@Target({METHOD, FIELD, TYPE}) "+  
                "@Retention(RUNTIME) "+
                "@Alternative "+
                "@Stereotype "+
                "public @interface Stereotype1 {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Stereotype2.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "import static java.lang.annotation.ElementType.METHOD; "+
                "import static java.lang.annotation.ElementType.FIELD; "+
                "import static java.lang.annotation.ElementType.TYPE; "+
                "import static java.lang.annotation.RetentionPolicy.RUNTIME; "+
                "import java.lang.annotation.*; "+
                "@Target({METHOD, FIELD, TYPE}) "+  
                "@Retention(RUNTIME) "+
                "@Stereotype "+
                "@Stereotype1 "+
                "public @interface Stereotype2 {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/TestClass.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "import javax.inject.*; "+
                "public class TestClass  {" +
                " @Inject @Binding1 One myField1; "+
                " @Inject @Binding1 One1 myField2; "+
                " @Inject @Binding1 @Binding2 One1 myField3; "+
                "}" );
        
        TestWebBeansModelImpl modelImpl = createModelImpl(true );
        MetadataModel<WebBeansModel> testModel = modelImpl.createTestModel();
        testModel.runReadAction( new MetadataModelAction<WebBeansModel,Void>(){

            public Void run( WebBeansModel model ) throws Exception {
                TypeMirror mirror = model.resolveType( "foo.TestClass" );
                Element clazz = ((DeclaredType)mirror).asElement();
                List<? extends Element> children = clazz.getEnclosedElements();
                List<VariableElement> injectionPoints = 
                    new ArrayList<VariableElement>( children.size());
                for (Element element : children) {
                    if ( element instanceof VariableElement ){
                        injectionPoints.add( (VariableElement)element);
                    }
                }
                Set<String> names = new HashSet<String>(); 
                for( VariableElement element : injectionPoints ){
                    names.add( element.getSimpleName().toString() );
                    if ( element.getSimpleName().contentEquals("myField1")){
                        check1( element , model );
                    }
                    else if ( element.getSimpleName().contentEquals("myField2")){
                        check2( element , model);
                    }
                    else if ( element.getSimpleName().contentEquals("myField3")){
                        check3( element , model);
                    }
                }
                
                assert names.contains("myField1");
                assert names.contains("myField2");
                assert names.contains("myField3");
                return null;
            }
        });
    }
    
    public void testAlternativeEnabled() throws IOException{
        TestUtilities.copyStringToFileObject(srcFO, "beans.xml", 
                "<?xml  version='1.0' encoding='UTF-8'?> " +
                "<beans xmlns=\"http://java.sun.com/xml/ns/javaee\">" +
                "<alternatives>" +
                    "<class>foo.Three</class> "+
                    "<stereotype>foo.Stereotype1</stereotype> "+
                "</alternatives> " +
                "</beans>");
        
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
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/One.java",
                "package foo; " +
                "public class One {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Two.java",
                "package foo; " +
                "@Binding1 "+
                "public class Two extends One {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Three.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "@Binding1 @Binding2 "+
                "@Alternative "+
                "public class Three extends One {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/One1.java",
                "package foo; " +
                "@Binding2 "+
                "@Stereotype3 "+
                "public class One1 {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Four.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "@Binding2 "+
                "@Stereotype1 "+
                "public class Four extends One1 {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Five.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "@Binding2 @Binding1 "+
                "@Stereotype2 "+
                "public class Five extends One1 {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/TestClass.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "import javax.inject.*; "+
                "public class TestClass  {" +
                " @Inject @Binding1 @Binding2 One myField1; "+
                " @Inject @Binding2 One1 myField2; "+
                " @Inject @Binding2 @Binding1 One1 myField3; "+
                "}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Stereotype1.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "import static java.lang.annotation.ElementType.METHOD; "+
                "import static java.lang.annotation.ElementType.FIELD; "+
                "import static java.lang.annotation.ElementType.TYPE; "+
                "import static java.lang.annotation.RetentionPolicy.RUNTIME; "+
                "import java.lang.annotation.*; "+
                "@Target({METHOD, FIELD, TYPE}) "+  
                "@Retention(RUNTIME) "+
                "@Alternative "+
                "@Stereotype "+
                "public @interface Stereotype1 {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Stereotype2.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "import static java.lang.annotation.ElementType.METHOD; "+
                "import static java.lang.annotation.ElementType.FIELD; "+
                "import static java.lang.annotation.ElementType.TYPE; "+
                "import static java.lang.annotation.RetentionPolicy.RUNTIME; "+
                "import java.lang.annotation.*; "+
                "@Target({METHOD, FIELD, TYPE}) "+  
                "@Retention(RUNTIME) "+
                "@Stereotype "+
                "@Stereotype1 "+
                "public @interface Stereotype2 {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Stereotype3.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "import static java.lang.annotation.ElementType.METHOD; "+
                "import static java.lang.annotation.ElementType.FIELD; "+
                "import static java.lang.annotation.ElementType.TYPE; "+
                "import static java.lang.annotation.RetentionPolicy.RUNTIME; "+
                "import java.lang.annotation.*; "+
                "@Target({METHOD, FIELD, TYPE}) "+  
                "@Retention(RUNTIME) "+
                "@Alternative "+
                "@Stereotype "+
                "public @interface Stereotype3 {}" );
        
        TestWebBeansModelImpl modelImpl = createModelImpl(true );
        MetadataModel<WebBeansModel> testModel = modelImpl.createTestModel();
        testModel.runReadAction( new MetadataModelAction<WebBeansModel,Void>(){

            public Void run( WebBeansModel model ) throws Exception {
                TypeMirror mirror = model.resolveType( "foo.TestClass" );
                Element clazz = ((DeclaredType)mirror).asElement();
                List<? extends Element> children = clazz.getEnclosedElements();
                List<VariableElement> injectionPoints = 
                    new ArrayList<VariableElement>( children.size());
                for (Element element : children) {
                    if ( element instanceof VariableElement ){
                        injectionPoints.add( (VariableElement)element);
                    }
                }
                Set<String> names = new HashSet<String>(); 
                for( VariableElement element : injectionPoints ){
                    names.add( element.getSimpleName().toString() );
                    if ( element.getSimpleName().contentEquals("myField1")){
                        checkEnabled1( element , model );
                    }
                    else if ( element.getSimpleName().contentEquals("myField2")){
                        checkEnabled2( element , model);
                    }
                    else if ( element.getSimpleName().contentEquals("myField3")){
                        checkEnabled3( element , model);
                    }
                }
                
                assert names.contains("myField1");
                assert names.contains("myField2");
                assert names.contains("myField3");
                return null;
            }

        });
    }

    private void check2( VariableElement element, WebBeansModel model )
    {
        Result result = model.getInjectable(element, null);
        
        assertNotNull( result );
        
        assertEquals( Result.ResultKind.INJECTABLE_RESOLVED, result.getKind());
        
        assertTrue( result instanceof Result.InjectableResult );
        assertTrue( result instanceof Result.ApplicableResult );
        assertTrue( result instanceof Result.ResolutionResult );
        
        Element injectable = ((Result.InjectableResult)result).getElement();
        
        assertTrue( injectable instanceof TypeElement );
        String name = ((TypeElement) injectable).getQualifiedName().toString();
        
        assertEquals( "foo.Two1", name );
        
        Set<Element> productions = ((Result.ApplicableResult)result).getProductions();
        Set<TypeElement> typeElements = ((Result.ApplicableResult)result).getTypeElements();
        
        assertEquals( 0 , productions.size());
        assertEquals( 3 , typeElements.size());
        
        boolean twoFound = false;
        boolean fourFound = false;
        boolean fiveFound = false;
        TypeElement two = null;
        TypeElement four = null;
        TypeElement five = null;
        for( TypeElement typeElement : typeElements ){
            String typeName = typeElement.getQualifiedName().toString();
            if ( "foo.Two1".equals(typeName)){
                twoFound = true;
                two = typeElement;
            }
            if ( "foo.Four".equals( typeName)){
                fourFound = true;
                four = typeElement;
            }
            if ( "foo.Five".equals( typeName)){
                fiveFound = true;
                five = typeElement;
            }
        }
        
        assertFalse ( "foo.Two1 is not an Alternative", 
                ((Result.ResolutionResult)result).isAlternative( two ));
        assertTrue ( "foo.Four is an Alternative", 
                ((Result.ResolutionResult)result).isAlternative( four ));
        assertTrue ( "foo.Five is an Alternative", 
                ((Result.ResolutionResult)result).isAlternative( five ));
        
        assertTrue( "foo.Two1 should be available via ApplicableResult interface", 
                twoFound );
        assertTrue( "foo.Four should be available via ApplicableResult interface", 
                fourFound );
        assertTrue( "foo.Five should be available via ApplicableResult interface", 
                fiveFound );
        
        assertFalse( "foo.Two1 should be enabled", 
                ((Result.ApplicableResult)result).isDisabled(two));
        assertTrue( "foo.Four should be disabled", 
                ((Result.ApplicableResult)result).isDisabled(four));
        assertTrue( "foo.Five should be disabled", 
                ((Result.ApplicableResult)result).isDisabled(five));
        
    }
    
    private void check1( VariableElement element, WebBeansModel model )
    {
        Result result = model.getInjectable(element, null);
        
        assertNotNull( result );
        
        assertEquals( Result.ResultKind.INJECTABLE_RESOLVED, result.getKind());
        
        assertTrue( result instanceof Result.InjectableResult );
        assertTrue( result instanceof Result.ApplicableResult );
        assertTrue( result instanceof Result.ResolutionResult );
        
        Element injectable = ((Result.InjectableResult)result).getElement();
        
        assertTrue( injectable instanceof TypeElement );
        String name = ((TypeElement) injectable).getQualifiedName().toString();
        
        assertEquals( "foo.Two", name );
        
        Set<Element> productions = ((Result.ApplicableResult)result).getProductions();
        Set<TypeElement> typeElements = ((Result.ApplicableResult)result).getTypeElements();
        
        assertEquals( 0 , productions.size());
        assertEquals( 2 , typeElements.size());
        
        boolean twoFound = false;
        boolean threeFound = false;
        TypeElement two = null;
        TypeElement three = null;
        for( TypeElement typeElement : typeElements ){
            String typeName = typeElement.getQualifiedName().toString();
            if ( "foo.Two".equals(typeName)){
                twoFound = true;
                two = typeElement;
            }
            if ( "foo.Three".equals( typeName)){
                threeFound = true;
                three = typeElement;
            }
        }
        assertTrue( "foo.Two should be available via ApplicableResult interface", 
                twoFound );
        assertTrue( "foo.Three should be available via ApplicableResult interface", 
                threeFound );
        
        assertFalse( "foo.Two should be enabled", 
                ((Result.ApplicableResult)result).isDisabled(two));
        assertTrue( "foo.Three should be disabled", 
                ((Result.ApplicableResult)result).isDisabled(three));
        
        assertFalse ( "foo.Two is not an Alternative", 
                ((Result.ResolutionResult)result).isAlternative( two ));
        assertTrue ( "foo.Three is an Alternative", 
                ((Result.ResolutionResult)result).isAlternative( three ));
    }
    
    private void check3( VariableElement element, WebBeansModel model )
    {
        Result result = model.getInjectable(element, null);
        
        assertNotNull( result );
        
        assertEquals( Result.ResultKind.INJECTABLE_RESOLVED, result.getKind());
        
        assertTrue( result instanceof Result.InjectableResult );
        assertTrue( result instanceof Result.ApplicableResult );
        assertTrue( result instanceof Result.ResolutionResult );
        
        Element injectable = ((Result.InjectableResult)result).getElement();
        
        assertTrue( injectable instanceof TypeElement );
        String name = ((TypeElement) injectable).getQualifiedName().toString();
        
        assertEquals( "foo.Two1", name );
        
        Set<Element> productions = ((Result.ApplicableResult)result).getProductions();
        Set<TypeElement> typeElements = ((Result.ApplicableResult)result).getTypeElements();
        
        assertEquals( 0 , productions.size());
        assertEquals( 2 , typeElements.size());
        
        boolean twoFound = false;
        boolean fiveFound = false;
        TypeElement two = null;
        TypeElement five = null;
        for( TypeElement typeElement : typeElements ){
            String typeName = typeElement.getQualifiedName().toString();
            if ( "foo.Two1".equals(typeName)){
                twoFound = true;
                two = typeElement;
            }
            if ( "foo.Five".equals( typeName)){
                fiveFound = true;
                five = typeElement;
            }
        }
        assertTrue( "foo.Two1 should be available via ApplicableResult interface", 
                twoFound );
        assertTrue( "foo.Five should be available via ApplicableResult interface", 
                fiveFound );
        
        assertFalse( "foo.Two1 should be enabled", 
                ((Result.ApplicableResult)result).isDisabled(two));
        assertTrue( "foo.Five should be disabled", 
                ((Result.ApplicableResult)result).isDisabled(five));
        
        assertFalse ( "foo.Two1 is not an Alternative", 
                ((Result.ResolutionResult)result).isAlternative( two ));
        assertTrue ( "foo.Five is an Alternative", 
                ((Result.ResolutionResult)result).isAlternative( five ));
    }
    
    private void checkEnabled1( VariableElement element, WebBeansModel model )
    {
        Result result = model.getInjectable(element, null);
        
        assertNotNull( result );
        
        assertEquals( Result.ResultKind.INJECTABLE_RESOLVED, result.getKind());
        
        assertTrue( result instanceof Result.InjectableResult );
        assertTrue( result instanceof Result.ApplicableResult );
        assertTrue( result instanceof Result.ResolutionResult );
        
        Element injectable = ((Result.InjectableResult)result).getElement();
        
        assertTrue( injectable instanceof TypeElement );
        String name = ((TypeElement) injectable).getQualifiedName().toString();
        
        assertEquals( "foo.Three", name );
        
        Set<Element> productions = ((Result.ApplicableResult)result).getProductions();
        Set<TypeElement> typeElements = ((Result.ApplicableResult)result).getTypeElements();
        
        assertEquals( 0 , productions.size());
        assertEquals( 1 , typeElements.size());
        
        assertEquals("foo.Three", 
                typeElements.iterator().next().getQualifiedName().toString() );
        
        assertFalse( "foo.Three should be enabled", 
                ((Result.ApplicableResult)result).isDisabled(injectable));
        
        assertTrue ( "foo.Three is an Alternative", 
                ((Result.ResolutionResult)result).isAlternative( injectable ));
    }
    
    private void checkEnabled2( VariableElement element, WebBeansModel model )
    {
        Result result = model.getInjectable(element, null);
        
        assertNotNull( result );
        
        assertEquals( Result.ResultKind.RESOLUTION_ERROR, result.getKind());
        
        assertTrue( result instanceof Result.ApplicableResult );
        assertTrue( result instanceof Result.ResolutionResult );
        
        Set<Element> productions = ((Result.ApplicableResult)result).getProductions();
        Set<TypeElement> typeElements = ((Result.ApplicableResult)result).getTypeElements();
        
        assertEquals( 0 , productions.size());
        assertEquals( 3 , typeElements.size());
        
        boolean oneFound = false;
        boolean fourFound = false;
        boolean fiveFound = false;
        TypeElement one = null;
        TypeElement five = null;
        TypeElement four = null;
        for( TypeElement typeElement : typeElements ){
            String typeName = typeElement.getQualifiedName().toString();
            if ( "foo.One1".equals(typeName)){
                oneFound = true;
                one = typeElement;
            }
            if ( "foo.Four".equals( typeName)){
                fourFound = true;
                four = typeElement;
            }
            if ( "foo.Five".equals( typeName)){
                fiveFound = true;
                five = typeElement;
            }
        }
        
        assertTrue( "foo.One1 should be available via ApplicableResult interface", 
                oneFound );
        assertTrue( "foo.Four should be available via ApplicableResult interface", 
                fourFound );
        assertTrue( "foo.Five should be available via ApplicableResult interface", 
                fiveFound );
        
        assertFalse( "foo.Four should be enabled", 
                ((Result.ApplicableResult)result).isDisabled(four));
        assertFalse( "foo.Five should be enabled", 
                ((Result.ApplicableResult)result).isDisabled(five));
        assertTrue( "foo.One1 should be disabled", 
                ((Result.ApplicableResult)result).isDisabled(one));    
        
        assertTrue ( "foo.One1 is an Alternative", 
                ((Result.ResolutionResult)result).isAlternative( one ));
        assertTrue ( "foo.Four is an Alternative", 
                ((Result.ResolutionResult)result).isAlternative( four ));
        assertTrue ( "foo.Five is an Alternative", 
                ((Result.ResolutionResult)result).isAlternative( five ));
        
    }
    
    private void checkEnabled3( VariableElement element, WebBeansModel model )
    {
        Result result = model.getInjectable(element, null);
        
        assertNotNull( result );
        
        assertEquals( Result.ResultKind.INJECTABLE_RESOLVED, result.getKind());
        
        assertTrue( result instanceof Result.InjectableResult );
        assertTrue( result instanceof Result.ApplicableResult );
        assertTrue( result instanceof Result.ResolutionResult );
        
        Element injectable = ((Result.InjectableResult)result).getElement();
        
        assertTrue( injectable instanceof TypeElement );
        String name = ((TypeElement) injectable).getQualifiedName().toString();
        
        assertEquals( "foo.Five", name );
        
        Set<Element> productions = ((Result.ApplicableResult)result).getProductions();
        Set<TypeElement> typeElements = ((Result.ApplicableResult)result).getTypeElements();
        
        assertEquals( 0 , productions.size());
        assertEquals( 1 , typeElements.size());
        
        assertEquals("foo.Five", 
                typeElements.iterator().next().getQualifiedName().toString() );
        
        assertFalse( "foo.Five should be enabled", 
                ((Result.ApplicableResult)result).isDisabled(injectable));
        
        assertTrue ( "foo.Five is an Alternative", 
                ((Result.ResolutionResult)result).isAlternative( injectable ));
    }
}
