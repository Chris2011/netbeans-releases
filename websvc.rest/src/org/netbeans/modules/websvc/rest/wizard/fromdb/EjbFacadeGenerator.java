/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.websvc.rest.wizard.fromdb;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.ElementFilter;

import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.ModificationResult;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.TreeUtilities;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.core.api.support.java.GenerationUtils;
import org.netbeans.modules.j2ee.core.api.support.java.JavaIdentifiers;
import org.netbeans.modules.j2ee.core.api.support.java.SourceUtils;
import org.netbeans.modules.j2ee.persistence.action.EntityManagerGenerator;
import org.netbeans.modules.j2ee.persistence.action.GenerationOptions;
import org.netbeans.modules.j2ee.persistence.dd.PersistenceUtils;
import org.netbeans.modules.j2ee.persistence.dd.common.Persistence;
import org.netbeans.modules.j2ee.persistence.spi.entitymanagergenerator.ContainerManagedJTAInjectableInEJB;
import org.netbeans.modules.j2ee.persistence.spi.entitymanagergenerator.EntityManagerGenerationStrategy;
import org.netbeans.modules.j2ee.persistence.wizard.fromdb.FacadeGenerator;
import org.netbeans.modules.websvc.rest.codegen.Constants;
import org.netbeans.modules.websvc.rest.model.api.RestConstants;
import org.netbeans.modules.websvc.rest.support.JavaSourceHelper;
import org.netbeans.modules.websvc.rest.wizard.Util;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author mkuchtiak
 */
public class EjbFacadeGenerator implements FacadeGenerator {
    private static final String REST_FACADE_SUFFIX = FACADE_SUFFIX+"REST"; //NOI18N
    private static final String FACADE_ABSTRACT = "AbstractFacade"; //NOI18N
    private static final String FACADE_REMOTE_SUFFIX = REST_FACADE_SUFFIX + "Remote"; //NOI18N
    private static final String FACADE_LOCAL_SUFFIX = REST_FACADE_SUFFIX + "Local"; //NOI18N
    private static final String EJB_LOCAL = "javax.ejb.Local"; //NOI18N
    private static final String EJB_REMOTE = "javax.ejb.Remote"; //NOI18N
    private static final String EJB_STATELESS = "javax.ejb.Stateless"; //NOI18N

    /**
     * Generates the facade and the loca/remote interface(s) for thhe given
     * entity class.
     * <i>Package private visibility for tests</i>.
     * @param targetFolder the folder where the facade and interfaces are generated.
     * @param entityClass the FQN of the entity class for which the facade is generated.
     * @param pkg the package prefix for the generated facede.
     * @param hasRemote specifies whether a remote interface is generated.
     * @param hasLocal specifies whether a local interface is generated.
     * @param strategyClass the entity manager lookup strategy.
     *
     * @return a set containing the generated files.
     */
    @Override
    public Set<FileObject> generate(final Project project,
            final Map<String, String> entityNames,
            final FileObject targetFolder,
            final String entityFQN,
            final String idClass,
            final String pkg, 
            final boolean hasRemote,
            final boolean hasLocal,
            boolean overrideExisting) throws IOException {

        final Set<FileObject> createdFiles = new HashSet<FileObject>();
        final String entitySimpleName = JavaIdentifiers.unqualify(entityFQN);
        final String variableName = entitySimpleName.toLowerCase().charAt(0) + entitySimpleName.substring(1);

        //create the abstract facade class
        Task<CompilationController> waiter = null;
        final String afName = pkg + "." + FACADE_ABSTRACT;
        FileObject afFO = targetFolder.getFileObject(FACADE_ABSTRACT, "java");
        if (afFO == null){
            afFO = GenerationUtils.createClass(targetFolder, FACADE_ABSTRACT, null);
            createdFiles.add(afFO);

            JavaSource source = JavaSource.forFileObject(afFO);
            source.runModificationTask(new Task<WorkingCopy>(){
                @Override
                public void run(WorkingCopy workingCopy) throws Exception {
                    workingCopy.toPhase(Phase.RESOLVED);
                    ClassTree classTree = SourceUtils.getPublicTopLevelTree(workingCopy);
                    assert classTree != null;
                    TreeMaker maker = workingCopy.getTreeMaker();
                    GenerationUtils genUtils = GenerationUtils.newInstance(workingCopy);
                    TreePath classTreePath = workingCopy.getTrees().getPath(workingCopy.getCompilationUnit(), classTree);
                    TypeElement classElement = (TypeElement)workingCopy.getTrees().getElement(classTreePath);

                    String genericsTypeName = "T";      //NOI18N
                    List<GenerationOptions> methodOptions = getAbstractFacadeMethodOptions(entityNames, genericsTypeName, "entity"); //NOI18N
                    List<Tree> members = new ArrayList();
                    String entityClassVar = "entityClass";                                              //NOI18N
                    Tree classObjectTree = genUtils.createType("java.lang.Class<" + genericsTypeName + ">", classElement);     //NOI18N
                    members.add(maker.Variable(genUtils.createModifiers(Modifier.PRIVATE),entityClassVar,classObjectTree,null));
                    members.add(maker.Constructor(
                            genUtils.createModifiers(Modifier.PUBLIC),
                            Collections.EMPTY_LIST,
                            Arrays.asList(new VariableTree[]{genUtils.createVariable(entityClassVar,classObjectTree)}),
                            Collections.EMPTY_LIST,
                            "{this." + entityClassVar + " = " + entityClassVar + ";}"));    //NOI18N
                    for(GenerationOptions option: methodOptions) {
                        Tree returnType = (option.getReturnType() == null || option.getReturnType().equals("void"))?  //NOI18N
                                                maker.PrimitiveType(TypeKind.VOID):
                                                genUtils.createType(option.getReturnType(), classElement);
                        List<VariableTree> vars = option.getParameterName() == null ? Collections.EMPTY_LIST :
                            Arrays.asList(new VariableTree[] {
                            genUtils.createVariable(
                                    option.getParameterName(),
                                    genUtils.createType(option.getParameterType(), classElement)
                                    )
                        });

                        if (option.getOperation() == null){
                            members.add(maker.Method(
                                    maker.Modifiers(option.getModifiers()),
                                    option.getMethodName(),
                                    returnType,
                                    Collections.EMPTY_LIST,
                                    vars,
                                    (List<ExpressionTree>)Collections.EMPTY_LIST,
                                    (BlockTree)null,
                                 null));
                        } else {
                            members.add(maker.Method(
                                    maker.Modifiers(option.getModifiers()),
                                    option.getMethodName(),
                                    returnType,
                                    (List<TypeParameterTree>)Collections.EMPTY_LIST,
                                    vars,
                                    (List<ExpressionTree>)Collections.EMPTY_LIST,
                                    "{" + option.getCallLines("getEntityManager()", entityClassVar, project!=null ? PersistenceUtils.getJPAVersion(project) : Persistence.VERSION_1_0) + "}", //NOI18N
                                    null));
                        }
                    }

                    ClassTree newClassTree = maker.Class(
                            maker.Modifiers(EnumSet.of(Modifier.PUBLIC, Modifier.ABSTRACT)),
                            classTree.getSimpleName(),
                            Arrays.asList(maker.TypeParameter(genericsTypeName, Collections.EMPTY_LIST)),
                            null,
                            Collections.EMPTY_LIST,
                            members);

                    workingCopy.rewrite(classTree, newClassTree);
                }
            }).commit();

            waiter = new Task<CompilationController>(){
                public void run(CompilationController cc) throws Exception {
                    cc.toPhase(Phase.ELEMENTS_RESOLVED);
                }
            };
        }

        // create the facade
        FileObject existingFO = targetFolder.getFileObject(entitySimpleName + REST_FACADE_SUFFIX, "java");
        if (existingFO != null) {
            if (overrideExisting) {
                existingFO.delete();
            } else {
                throw new IOException("file alerady exists exception: "+existingFO);
            }
        }
        final FileObject facade = GenerationUtils.createClass(targetFolder, entitySimpleName + REST_FACADE_SUFFIX, null);
        createdFiles.add(facade);

        // create the interfaces
        final String localInterfaceFQN = pkg + "." + getUniqueClassName(entitySimpleName + FACADE_LOCAL_SUFFIX, targetFolder);
        final String remoteInterfaceFQN = pkg + "." + getUniqueClassName(entitySimpleName + FACADE_REMOTE_SUFFIX, targetFolder);

        List<GenerationOptions> intfOptions = getAbstractFacadeMethodOptions(entityNames, entityFQN, variableName);
        if (hasLocal) {
            FileObject local = createInterface(JavaIdentifiers.unqualify(localInterfaceFQN), EJB_LOCAL, targetFolder);
            addMethodToInterface(intfOptions, local);
            createdFiles.add(local);
        }
        if (hasRemote) {
            FileObject remote = createInterface(JavaIdentifiers.unqualify(remoteInterfaceFQN), EJB_REMOTE, targetFolder);
            addMethodToInterface(intfOptions, remote);
            createdFiles.add(remote);
        }

        // add the @stateless annotation
        // add implements and extends clauses to the facade
        Task<WorkingCopy> modificationTask = new Task<WorkingCopy>(){
            @Override
            public void run(WorkingCopy wc) throws Exception {
                wc.toPhase(Phase.RESOLVED);
                TypeElement classElement = wc.getElements().getTypeElement(pkg + "." + entitySimpleName + REST_FACADE_SUFFIX);
                ClassTree classTree = wc.getTrees().getTree(classElement);
                assert classTree != null;
                GenerationUtils genUtils = GenerationUtils.newInstance(wc);
                TreeMaker maker = wc.getTreeMaker();

                List<Tree> implementsClause = new ArrayList(classTree.getImplementsClause());
                if (hasLocal)
                    implementsClause.add(genUtils.createType(localInterfaceFQN, classElement));
                if (hasRemote)
                    implementsClause.add(genUtils.createType(remoteInterfaceFQN, classElement));
                
                List<Tree> members = new ArrayList<Tree>(classTree.getMembers());
                MethodTree constructor = maker.Constructor(
                        genUtils.createModifiers(Modifier.PUBLIC),
                        Collections.EMPTY_LIST,
                        Collections.EMPTY_LIST,
                        Collections.EMPTY_LIST,
                        "{super(" + entitySimpleName + ".class);}");            //NOI18N
                members.add(constructor);

                List<RestGenerationOptions> restGenerationOptions = getRestFacadeMethodOptions(entityFQN, idClass);

                ModifiersTree publicModifiers = genUtils.createModifiers(Modifier.PUBLIC);
                ModifiersTree paramModifier = maker.Modifiers(Collections.<Modifier>emptySet());
                for(RestGenerationOptions option: restGenerationOptions) {

                    ModifiersTree modifiersTree =
                            maker.addModifiersAnnotation(publicModifiers, genUtils.createAnnotation(option.getOperation().getMethod()));

                     // add @Path annotation
                    String uriPath = option.getOperation().getUriPath();
                    if (uriPath != null) {
                        ExpressionTree annArgument = maker.Literal(uriPath);
                        modifiersTree =
                                maker.addModifiersAnnotation(modifiersTree,
                                genUtils.createAnnotation(RestConstants.PATH, Collections.<ExpressionTree>singletonList(annArgument)));

                    }
                    
                    if ( option.getOperation().overrides() ){
                        modifiersTree =
                            maker.addModifiersAnnotation(modifiersTree,
                            genUtils.createAnnotation(Override.class.getCanonicalName()));
                    }
                    // add @Produces annotation
                    String[] produces = option.getProduces();
                    if (produces != null) {
                        ExpressionTree annArguments = null;
                        if (produces.length == 1) {
                            annArguments = maker.Literal(produces[0]);
                        } else {
                            List<LiteralTree> literals = new ArrayList<LiteralTree>();
                            for (int i=0; i< produces.length; i++) {
                                literals.add(maker.Literal(produces[i]));
                            }
                            annArguments = maker.NewArray(null, Collections.<ExpressionTree>emptyList(), literals);
                        }
                        modifiersTree =
                                maker.addModifiersAnnotation(modifiersTree,
                                genUtils.createAnnotation(RestConstants.PRODUCE_MIME, Collections.<ExpressionTree>singletonList(annArguments)));
                    }
                    // add @Consumes annotation
                    String[] consumes = option.getConsumes();
                    if (consumes != null) {
                        ExpressionTree annArguments = null;
                        if (consumes.length == 1) {
                            annArguments = maker.Literal(consumes[0]);
                        } else {
                            List<LiteralTree> literals = new ArrayList<LiteralTree>();
                            for (int i=0; i< consumes.length; i++) {
                                literals.add(maker.Literal(consumes[i]));
                            }
                            annArguments = maker.NewArray(null, Collections.<ExpressionTree>emptyList(), literals);
                        }
                        modifiersTree =
                                maker.addModifiersAnnotation(modifiersTree,
                                genUtils.createAnnotation(RestConstants.CONSUME_MIME, Collections.<ExpressionTree>singletonList(annArguments)));
                    }

                    // create arguments list
                    List<VariableTree> vars = new ArrayList<VariableTree>();
                    String[] paramNames = option.getParameterNames();
                    int paramLength = paramNames == null ? 0 : option.getParameterNames().length ;

                    if (paramLength > 0) {
                        String[] paramTypes = option.getParameterTypes();
                        String[] pathParams = option.getPathParams();
                        
                        for (int i = 0; i<paramLength; i++) {
                            ModifiersTree pathParamTree = paramModifier;
                            if (pathParams != null && pathParams[i] != null) {
                                List<ExpressionTree> annArguments = Collections.<ExpressionTree>singletonList(maker.Literal(pathParams[i]));
                                pathParamTree =
                                    maker.addModifiersAnnotation(paramModifier, genUtils.createAnnotation(RestConstants.PATH_PARAM, annArguments));
                            }
                            Tree paramTree = genUtils.createType(paramTypes[i], classElement);
                            VariableTree var = maker.Variable(pathParamTree, paramNames[i], paramTree, null); //NOI18N
                            vars.add(var);

                        }
                    }

                    Tree returnType = (option.getReturnType() == null || option.getReturnType().equals("void"))?  //NOI18N
                                            maker.PrimitiveType(TypeKind.VOID):
                                            genUtils.createType(option.getReturnType(), classElement);

                    members.add(
                                maker.Method(
                                modifiersTree,
                                option.getOperation().getMethodName(),
                                returnType,
                                Collections.EMPTY_LIST,
                                vars,
                                (List<ExpressionTree>)Collections.EMPTY_LIST,
                                "{"+option.getBody()+"}", //NOI18N
                                null)
                            );

                }

                ModifiersTree modifiersTree =
                        maker.addModifiersAnnotation(classTree.getModifiers(), genUtils.createAnnotation(EJB_STATELESS));

                ExpressionTree annArgument = maker.Literal(entityFQN.toLowerCase());
                modifiersTree =
                        maker.addModifiersAnnotation(modifiersTree, genUtils.createAnnotation(RestConstants.PATH, Collections.<ExpressionTree>singletonList(annArgument)));
                
                ClassTree newClassTree = maker.Class(
                        modifiersTree,
                        classTree.getSimpleName(),
                        classTree.getTypeParameters(),
                        maker.Type(wc.getTypes().getDeclaredType(
                            wc.getElements().getTypeElement(afName),
                            wc.getElements().getTypeElement(entityFQN).asType())),
                        implementsClause,
                        members);

                wc.rewrite(classTree, newClassTree);
            }
        };

        if (waiter != null){
            try {
                JavaSource.forFileObject(afFO).runWhenScanFinished(waiter, true).get();
            } catch (InterruptedException ex) {
            } catch (ExecutionException ex) {
            }
        }
        JavaSource.forFileObject(facade).runModificationTask(modificationTask).commit();
        
        // generate methods for the facade
        EntityManagerGenerator generator = new EntityManagerGenerator(facade, entityFQN);
        List<GenerationOptions> methodOptions = getMethodOptions(entityFQN, variableName);
        for (GenerationOptions each : methodOptions){
            generator.generate(each, ContainerManagedJTAInjectableInEJB.class);
        }
        modifyEntityManager( methodOptions, facade);

        return createdFiles;
    }

    private void modifyEntityManager( List<GenerationOptions> methodOptions ,
            FileObject fileObject)  throws IOException 
    {
        final Set<String> methodNames = new HashSet<String>();
        for( GenerationOptions opt : methodOptions ){
            methodNames.add( opt.getMethodName());
        }
        Task<WorkingCopy> task = new Task<WorkingCopy>() {
            
            public void run(WorkingCopy workingCopy) throws Exception {
                
                workingCopy.toPhase(Phase.RESOLVED);
                CompilationUnitTree cut = workingCopy.getCompilationUnit();
                TreeMaker make = workingCopy.getTreeMaker();
                
                for (Tree typeDeclaration : cut.getTypeDecls()){
                    if (TreeUtilities.CLASS_TREE_KINDS.contains(typeDeclaration
                            .getKind()))
                    {
                        ClassTree clazz = (ClassTree) typeDeclaration;
                        TreePath path = workingCopy.getTrees().getPath(cut,
                                clazz);
                        Element element = workingCopy.getTrees().getElement(
                                path);
                        List<ExecutableElement> methods = ElementFilter
                                .methodsIn(element.getEnclosedElements());
                        for (ExecutableElement method : methods) {
                            if ( methodNames.contains(method.getSimpleName().
                                    toString()) )
                            {
                                MethodTree methodTree = workingCopy.getTrees().getTree( method );
                                Set<Modifier> modifiers = method.getModifiers();
                                AnnotationTree annotation = make.Annotation(
                                        make.Identifier(Override.class.getCanonicalName()),
                                        Collections.<ExpressionTree>emptyList());
                                ModifiersTree newModifs = make.Modifiers(
                                        modifiers, 
                                        Collections.singletonList(annotation));
                                workingCopy.rewrite(methodTree.getModifiers(), 
                                        newModifs);
                            }
                        }
                    }
                }
            }
        };
        
        JavaSource.forFileObject(fileObject).runModificationTask(task).commit();
    }

    private List<GenerationOptions> getAbstractFacadeMethodOptions(Map<String, String> entityNames, String entityFQN, String variableName){

        GenerationOptions getEMOptions = new GenerationOptions();
        getEMOptions.setMethodName("getEntityManager"); //NOI18N
        getEMOptions.setReturnType("javax.persistence.EntityManager");//NOI18N
        getEMOptions.setModifiers(EnumSet.of(Modifier.PROTECTED, Modifier.ABSTRACT));

        //implemented methods
        GenerationOptions createOptions = new GenerationOptions();
        createOptions.setMethodName("create"); //NOI18N
        createOptions.setOperation(GenerationOptions.Operation.PERSIST);
        createOptions.setReturnType("void");//NOI18N
        createOptions.setParameterName(variableName);
        createOptions.setParameterType(entityFQN);

        GenerationOptions editOptions = new GenerationOptions();
        editOptions.setMethodName("edit");//NOI18N
        editOptions.setOperation(GenerationOptions.Operation.MERGE);
        editOptions.setReturnType("void");//NOI18N
        editOptions.setParameterName(variableName);
        editOptions.setParameterType(entityFQN);

        GenerationOptions destroyOptions = new GenerationOptions();
        destroyOptions.setMethodName("remove");//NOI18N
        destroyOptions.setOperation(GenerationOptions.Operation.REMOVE);
        destroyOptions.setReturnType("void");//NOI18N
        destroyOptions.setParameterName(variableName);
        destroyOptions.setParameterType(entityFQN);

        GenerationOptions findOptions = new GenerationOptions();
        findOptions.setMethodName("find");//NOI18N
        findOptions.setOperation(GenerationOptions.Operation.FIND);
        findOptions.setReturnType(entityFQN);//NOI18N
        findOptions.setParameterName("id");//NOI18N
        findOptions.setParameterType("Object");//NOI18N

        GenerationOptions findAllOptions = new GenerationOptions();
        findAllOptions.setMethodName("findAll");//NOI18N
        findAllOptions.setOperation(GenerationOptions.Operation.FIND_ALL);
        findAllOptions.setReturnType("java.util.List<" + entityFQN + ">");//NOI18N
        findAllOptions.setQueryAttribute(getEntityName(entityNames, entityFQN));

        GenerationOptions findSubOptions = new GenerationOptions();
        findSubOptions.setMethodName("findRange");//NOI18N
        findSubOptions.setOperation(GenerationOptions.Operation.FIND_SUBSET);
        findSubOptions.setReturnType("java.util.List<" + entityFQN + ">");//NOI18N
        findSubOptions.setQueryAttribute(getEntityName(entityNames, entityFQN));
        findSubOptions.setParameterName("range");//NOI18N
        findSubOptions.setParameterType("int[]");//NOI18N

        GenerationOptions countOptions = new GenerationOptions();
        countOptions.setMethodName("count");//NOI18N
        countOptions.setOperation(GenerationOptions.Operation.COUNT);
        countOptions.setReturnType("int");//NOI18N
        countOptions.setQueryAttribute(getEntityName(entityNames, entityFQN));

        return Arrays.<GenerationOptions>asList(getEMOptions, createOptions, editOptions, destroyOptions, findOptions, findAllOptions, findSubOptions, countOptions);
    }

    /**
     * @return the options representing the methods for a facade, i.e. create/edit/
     * find/remove/findAll.
     */
    private List<GenerationOptions> getMethodOptions(String entityFQN, String variableName){

        GenerationOptions getEMOptions = new GenerationOptions();
        getEMOptions.setMethodName("getEntityManager"); //NOI18N
        getEMOptions.setOperation(GenerationOptions.Operation.GET_EM);
        getEMOptions.setReturnType("javax.persistence.EntityManager");//NOI18N
        getEMOptions.setModifiers(EnumSet.of(Modifier.PROTECTED));

        return Arrays.<GenerationOptions>asList(getEMOptions);
    }

    /**
     *@return the name for the given <code>entityFQN</code>.
     */
    private String getEntityName(Map<String, String> entityNames, String entityFQN){
        String result = entityNames.get(entityFQN);
        return result != null ? result : JavaIdentifiers.unqualify(entityFQN);
    }

    /**
     * Creates an interface with the given <code>name</code>, annotated with an annotation
     * of the given <code>annotationType</code>. <i>Package private visibility just because of tests</i>.
     *
     * @param name the name for the interface
     * @param annotationType the FQN of the annotation
     * @param targetFolder the folder to which the interface is generated
     *
     * @return the generated interface.
     */
    private FileObject createInterface(String name, final String annotationType, FileObject targetFolder) throws IOException {
        FileObject sourceFile = GenerationUtils.createInterface(targetFolder, name, null);
        JavaSource source = JavaSource.forFileObject(sourceFile);
        ModificationResult result = source.runModificationTask(new Task<WorkingCopy>() {

            @Override
            public void run(WorkingCopy workingCopy) throws Exception {
                workingCopy.toPhase(Phase.RESOLVED);
                ClassTree clazz = SourceUtils.getPublicTopLevelTree(workingCopy);
                assert clazz != null;
                GenerationUtils genUtils = GenerationUtils.newInstance(workingCopy);
                TreeMaker make = workingCopy.getTreeMaker();
                AnnotationTree annotations = genUtils.createAnnotation(annotationType);
                ModifiersTree modifiers = make.Modifiers(clazz.getModifiers(), Collections.<AnnotationTree>singletonList(annotations));
                ClassTree modifiedClass = make.Class(modifiers, clazz.getSimpleName(), clazz.getTypeParameters(), clazz.getExtendsClause(), Collections.<ExpressionTree>emptyList(), Collections.<Tree>emptyList());
                workingCopy.rewrite(clazz, modifiedClass);
            }
        });
        result.commit();
        return source.getFileObjects().iterator().next();
    }

    private String getUniqueClassName(String candidateName, FileObject targetFolder){
        return FileUtil.findFreeFileName(targetFolder, candidateName, "java"); //NOI18N
    }
    /**
     * Adds a method to the given interface.
     *
     * @param name the name of the method.
     * @param returnType the return type of the method.
     * @param parameterName the name of the parameter for the method.
     * @param parameterType the FQN type of the parameter.
     * @param target the target interface.
     */
    private void addMethodToInterface(final List<GenerationOptions> options, final FileObject target) throws IOException {

        JavaSource source = JavaSource.forFileObject(target);
        ModificationResult result = source.runModificationTask(new Task<WorkingCopy>() {

            @Override
            public void run(WorkingCopy copy) throws Exception {
                copy.toPhase(Phase.RESOLVED);
                GenerationUtils utils = GenerationUtils.newInstance(copy);
                TypeElement typeElement = SourceUtils.getPublicTopLevelElement(copy);
                assert typeElement != null;
                ClassTree original = copy.getTrees().getTree(typeElement);
                ClassTree modifiedClass = original;
                TreeMaker make = copy.getTreeMaker();
                for (GenerationOptions each : options) {
                    if (each.getModifiers().size() == 1 && each.getModifiers().contains(Modifier.PUBLIC)){
                        MethodTree method = make.Method(make.Modifiers(Collections.<Modifier>emptySet()),
                            each.getMethodName(), utils.createType(each.getReturnType(), typeElement),
                            Collections.<TypeParameterTree>emptyList(), getParameterList(each, make, utils, typeElement),
                            Collections.<ExpressionTree>emptyList(), (BlockTree) null, null);
                        modifiedClass = make.addClassMember(modifiedClass, method);
                    }
                }
                copy.rewrite(original, modifiedClass);
            }
        });
        result.commit();
    }

    private List<VariableTree> getParameterList(GenerationOptions options, TreeMaker make, GenerationUtils utils, TypeElement scope){
        if (options.getParameterName() == null){
            return Collections.<VariableTree>emptyList();
        }
        VariableTree vt = make.Variable(make.Modifiers(Collections.<Modifier>emptySet()),
                options.getParameterName(), utils.createType(options.getParameterType(), scope), null);
        return Collections.<VariableTree>singletonList(vt);
    }
    
    private List<RestGenerationOptions> getRestFacadeMethodOptions(String entityFQN, String idClass){

        String paramArg = "java.lang.Character".equals(idClass) ? "id.charAt(0)" : "id"; //NOI18N
        String idType = "id".equals(paramArg) ? idClass : "java.lang.String"; //NOI18N

        RestGenerationOptions createOptions = new RestGenerationOptions();
        createOptions.setOperation(RestGenerationOptions.Operation.CREATE);
        createOptions.setReturnType("void"); //NOI18N
        createOptions.setParameterNames(new String[]{"entity"}); //NOI18N
        createOptions.setParameterTypes(new String[]{entityFQN});
        createOptions.setConsumes(new String[]{"application/xml", "application/json"}); //NOI18N
        createOptions.setBody("super.create(entity);"); //NOI18N

        RestGenerationOptions editOptions = new RestGenerationOptions();
        editOptions.setOperation(RestGenerationOptions.Operation.EDIT);
        editOptions.setReturnType("void");//NOI18N
        editOptions.setParameterNames(new String[]{"entity"}); //NOI18N
        editOptions.setParameterTypes(new String[]{entityFQN}); //NOI18N
        editOptions.setConsumes(new String[]{"application/xml", "application/json"}); //NOI18N
        editOptions.setBody("super.edit(entity);"); //NOI18N

        RestGenerationOptions destroyOptions = new RestGenerationOptions();
        destroyOptions.setOperation(RestGenerationOptions.Operation.REMOVE);
        destroyOptions.setReturnType("void");//NOI18N
        destroyOptions.setParameterNames(new String[]{"id"}); //NOI18N
        destroyOptions.setParameterTypes(new String[]{idType}); //NOI18N
        destroyOptions.setPathParams(new String[]{"id"}); //NOI18N
        destroyOptions.setBody("super.remove(super.find("+paramArg+"));"); //NOI18N

        RestGenerationOptions findOptions = new RestGenerationOptions();
        findOptions.setOperation(RestGenerationOptions.Operation.FIND);
        findOptions.setReturnType(entityFQN);//NOI18N
        findOptions.setProduces(new String[]{"application/xml", "application/json"}); //NOI18N
        findOptions.setParameterNames(new String[]{"id"}); //NOI18N
        findOptions.setParameterTypes(new String[]{idType}); //NOI18N
        findOptions.setPathParams(new String[]{"id"}); //NOI18N
        findOptions.setBody("return super.find("+paramArg+");"); //NOI18N

        RestGenerationOptions findAllOptions = new RestGenerationOptions();
        findAllOptions.setOperation(RestGenerationOptions.Operation.FIND_ALL);
        findAllOptions.setReturnType("java.util.List<" + entityFQN + ">");//NOI18N
        findAllOptions.setProduces(new String[]{"application/xml", "application/json"});
        findAllOptions.setBody("return super.findAll();");

        RestGenerationOptions findSubOptions = new RestGenerationOptions();
        findSubOptions.setOperation(RestGenerationOptions.Operation.FIND_RANGE);
        findSubOptions.setReturnType("java.util.List<" + entityFQN + ">");//NOI18N
        findSubOptions.setProduces(new String[]{"application/xml", "application/json"}); //NOI18N
        findSubOptions.setParameterNames(new String[]{"from", "to"}); //NOI18N
        findSubOptions.setParameterTypes(new String[]{"java.lang.Integer", "java.lang.Integer"}); //NOI18N
        findSubOptions.setPathParams(new String[]{"from", "to"}); //NOI18N
        findSubOptions.setBody("return super.findRange(new int[] {from, to});"); //NOI18N

        RestGenerationOptions countOptions = new RestGenerationOptions();
        countOptions.setOperation(RestGenerationOptions.Operation.COUNT);
        countOptions.setReturnType("java.lang.String");//NOI18N
        countOptions.setProduces(new String[]{"text/plain"}); //NOI18N
        countOptions.setBody("return String.valueOf(super.count());"); //NOI18N

        return Arrays.<RestGenerationOptions>asList(
                createOptions,
                editOptions,
                destroyOptions,
                findOptions,
                findAllOptions,
                findSubOptions,
                countOptions);
    }


}
