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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.web.refactoring;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;
import org.netbeans.api.fileinfo.NonRecursiveFolder;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.modules.refactoring.api.MoveRefactoring;
import org.netbeans.modules.refactoring.api.Problem;
import org.netbeans.modules.refactoring.api.RenameRefactoring;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Parameters;

/**
 * Utility methods for refactoring operations.
 *
 * <i>TODO: need to introduce a common utility module for JPA/EJB/Web refactorings.</i>.
 *
 * @author Erno Mononen
 */
public class RefactoringUtil {
    
    private static final String JAVA_MIME_TYPE = "text/x-java"; //NO18N
    
    private RefactoringUtil() {
    }
    
    /**
     * Sets the given <code>toAdd</code> as the following problem for
     * the given <code>existing</code> problem.
     *
     * @param toAdd the problem to add, may be null.
     * @param existing the problem whose following problem should be set, may be null.
     *
     * @return the existing problem with its following problem
     * set to the given problem or null if both of the params
     * were null.
     *
     */
    public static Problem addToEnd(Problem toAdd, Problem existing){
        if (existing == null){
            return toAdd;
        }
        if (toAdd == null){
            return existing;
        }
        
        Problem tail = existing;
        while(tail.getNext() != null){
            tail = tail.getNext();
        }
        tail.setNext(toAdd);
        
        return tail;
    }
    
    // copied from o.n.m.java.refactoring.RetoucheUtils
    public static boolean isJavaFile(FileObject fileObject) {
        return JAVA_MIME_TYPE.equals(fileObject.getMIMEType()); //NOI18N
    }
    
    /**
     * Gets the fully qualified name for the given <code>fileObject</code>. If it
     * represents a java package, will return the name of the package (with dots as separators).
     *
     *@param fileObject the file object whose FQN should be get. Must belong to
     * a project.
     *@return the FQN for the given file object.
     */
    public static String getQualifiedName(FileObject fileObject){
        Project project = FileOwnerQuery.getOwner(fileObject);
        assert project != null;
        ClassPathProvider classPathProvider = project.getLookup().lookup(ClassPathProvider.class);
        assert classPathProvider != null;
        return classPathProvider.findClassPath(fileObject, ClassPath.SOURCE).getResourceName(fileObject, '.', false);
        
    }
    
    /**
     * Constructs a new fully qualified name for the given <code>newName</code>.
     *
     * @param originalFullyQualifiedName the old fully qualified name of the class.
     * @param newName the new unqualified name of the class.
     *
     * @return the new fully qualified name of the class.
     */
    public static String renameClass(String originalFullyQualifiedName, String newName){
        Parameters.notEmpty("originalFullyQualifiedName", originalFullyQualifiedName); //NO18N
        Parameters.notEmpty("newName", newName); //NO18N
        int lastDot = originalFullyQualifiedName.lastIndexOf('.');
        return (lastDot <= 0) ? newName : originalFullyQualifiedName.substring(0, lastDot + 1) + newName;
    }
    
    
    // copied from o.n.m.java.refactoring.RetoucheUtils
    public static boolean isOnSourceClasspath(FileObject fo) {
        Project p = FileOwnerQuery.getOwner(fo);
        if (p==null) return false;
        Project[] opened = OpenProjects.getDefault().getOpenProjects();
        for (int i = 0; i<opened.length; i++) {
            if (p==opened[i]) {
                SourceGroup[] gr = ProjectUtils.getSources(p).getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
                for (int j = 0; j < gr.length; j++) {
                    if (fo==gr[j].getRootFolder()) return true;
                    if (FileUtil.isParentOf(gr[j].getRootFolder(), fo))
                        return true;
                }
                return false;
            }
        }
        return false;
    }
    
    
    /**
     * Recursively collects the java files from the given folder into the
     * given <code>result</code>.
     */
    public static void collectChildren(FileObject folder, List<FileObject> result){
        for(FileObject child : folder.getChildren()){
            if (isJavaFile(child)){
                result.add(child);
            } else if (child.isFolder()){
                collectChildren(child, result);
            }
        }
    }
    
    /**
     * @return true if the given refactoring represents a package rename.
     */
    public static boolean isPackage(RenameRefactoring rename){
        return rename.getRefactoringSource().lookup(NonRecursiveFolder.class) != null;
    }
    
    /**
     * Unqualifies the given FQN.
     *
     * @param fqn the fully qualified name.
     * @return the unqualified name.
     */
    public static String unqualify(String fqn){
        int lastDot = fqn.lastIndexOf(".");
        if (lastDot < 0){
            return fqn;
        }
        return fqn.substring(lastDot + 1);
    }
    
    /**
     * Gets the new refactored name for the given <code>javaFile</code>.
     *
     * @param javaFile the file object for the class being renamed. Excepts that
     * the target class is the public top level class in the file.
     * @param rename the refactoring, must represent either package or folder rename.
     *
     * @return the new fully qualified name for the class being refactored.
     */
    public static String constructNewName(FileObject javaFile, RenameRefactoring rename){
        
        String fqn = getQualifiedName(javaFile);
        
        if (isPackage(rename)){
            return rename.getNewName() + "." + unqualify(fqn);
        }
        
        FileObject folder = rename.getRefactoringSource().lookup(FileObject.class);
        ClassPath classPath = ClassPath.getClassPath(folder, ClassPath.SOURCE);
        FileObject root = classPath.findOwnerRoot(folder);
        
        String prefix = FileUtil.getRelativePath(root, folder.getParent()).replace('/','.');
        String oldName = buildName(prefix, folder.getName());
        String newName = buildName(prefix, rename.getNewName());
        int oldNameIndex = fqn.lastIndexOf(oldName) + oldName.length();
        return newName + fqn.substring(oldNameIndex, fqn.length());
        
    }
    
    private static String buildName(String prefix, String name){
        if (prefix.length() == 0){
            return name;
        }
        return prefix + "." + name;
    }
    
    /**
     *@return the new fully qualified name for the given refactoring.
     */
    public static String constructNewName(MoveRefactoring move){
        String newPkg = getPackageName(move.getTarget().lookup(URL.class));
        String uqn = move.getRefactoringSource().lookup(FileObject.class).getName();
        return newPkg + "." + uqn;
    }
    
    // copied from o.n.m.java.refactoring.RetoucheUtils
    private static String getPackageName(URL url) {
        File f = null;
        try {
            f = FileUtil.normalizeFile(new File(url.toURI()));
        } catch (URISyntaxException uRISyntaxException) {
            throw new IllegalArgumentException("Cannot create package name for url " + url);
        }
        String suffix = "";
        
        do {
            FileObject fo = FileUtil.toFileObject(f);
            if (fo != null) {
                if ("".equals(suffix))
                    return getPackageName(fo);
                String prefix = getPackageName(fo);
                return prefix + ("".equals(prefix)?"":".") + suffix;
            }
            if (!"".equals(suffix)) {
                suffix = "." + suffix;
            }
            suffix = URLDecoder.decode(f.getPath().substring(f.getPath().lastIndexOf(File.separatorChar)+1)) + suffix;
            f = f.getParentFile();
        } while (f!=null);
        throw new IllegalArgumentException("Cannot create package name for url " + url);
    }
    
    // copied from o.n.m.java.refactoring.RetoucheUtils
    private static String getPackageName(FileObject folder) {
        assert folder.isFolder() : "argument must be folder";
        return ClassPath.getClassPath(
                folder, ClassPath.SOURCE)
                .getResourceName(folder, '.', false);
    }
    
    
}
