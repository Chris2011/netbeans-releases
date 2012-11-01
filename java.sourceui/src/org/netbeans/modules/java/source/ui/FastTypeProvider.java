/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.source.ui;

import java.awt.Toolkit;
import java.lang.ref.Reference;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.lang.model.element.ElementKind;
import javax.swing.Icon;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.java.ui.Icons;
import org.netbeans.spi.jumpto.support.NameMatcherFactory;
import org.netbeans.spi.jumpto.type.TypeDescriptor;
import org.netbeans.spi.jumpto.type.TypeProvider;
import org.openide.cookies.EditCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/** 
 * Fast & dirty type provider, which fakes Java types from filenames.
 * The provider is used just after project loads, and before the source roots
 * are indexed by a proper Java indexer.
 * 
 * <p/>
 * The TypeProvider uses {@link OpenProjectFastIndex} as data source.
 *
 * @author sdedic
 */
@org.openide.util.lookup.ServiceProvider(service=org.netbeans.spi.jumpto.type.TypeProvider.class)
public final class FastTypeProvider implements TypeProvider {
    private static final Logger LOG = Logger.getLogger(FastTypeProvider.class.getName());
    
    /**
     * Cancel flag, set by the infrastructure.
     */
    private AtomicBoolean cancel = new AtomicBoolean();
    
    /**
     * Fast index instance
     */
    private OpenProjectFastIndex fastIndex;
    
    /**
     * Cached class icon for results; we do not use other icons :)
     */
    private Icon classIcon;
    
    public FastTypeProvider() {
        this(OpenProjectFastIndex.getDefault());
    }
    
    // used from unit tests
    FastTypeProvider(OpenProjectFastIndex fastIndex) {
        this.fastIndex = fastIndex;
    }
    
    private Icon getClassIcon() {
        if (classIcon == null) {
            classIcon = Icons.getElementIcon (ElementKind.CLASS, null);
        }
        return classIcon;
    }
    
    @Override
    public void cancel() {
        cancel.set(true);
    }

    @Override
    public void cleanup() {
        // no cleanup needed
    }

    @Override
    public void computeTypeNames(Context context, Result result) {
        StringBuilder pattern = new StringBuilder();
        boolean sensitive = true;
        
        String quotedText = Pattern.quote(context.getText());
        
        switch (context.getSearchType()) {
            case CASE_INSENSITIVE_EXACT_NAME:
                sensitive = false;
            case CAMEL_CASE:
                pattern.append(createCamelCaseRegExp(context.getText(), sensitive));
                break;
            case EXACT_NAME:
                pattern.append("^").append(quotedText).append("$"); // NOI18N
                break;
            case CASE_INSENSITIVE_PREFIX:
                sensitive = false;
            case PREFIX:
                pattern.append("^").append(quotedText); // NOI18N
                break;
            case CASE_INSENSITIVE_REGEXP:
                sensitive = false;
            case REGEXP:
                pattern.append(
                        NameMatcherFactory.wildcardsToRegexp(
                            JavaTypeProvider.removeNonJavaChars(context.getText()),
                            false
                        )
                );
                break;
        }
        Pattern searchPattern = Pattern.compile(
                pattern.toString(), 
                Pattern.MULTILINE + 
                    (sensitive ? 0 : Pattern.CASE_INSENSITIVE));
        
        for (Map.Entry<FileObject, OpenProjectFastIndex.NameIndex> one : fastIndex.copyIndexes().entrySet()) {
            FileObject root = one.getKey();
            Project p = FileOwnerQuery.getOwner(root);
            
            if (context.getProject() != null && !context.getProject().equals(p)) {
                continue;
            }
            OpenProjectFastIndex.NameIndex fileIndex = one.getValue();

            Matcher m = searchPattern.matcher(fileIndex.files());
            while (m.find()) {
                if (cancel.get()) {
                    LOG.fine("Search canceled");
                    return;
                }
                CharSequence f = fileIndex.getFilename(m.start(), m.end());
                CharSequence pkg = fileIndex.findPath(m.start());
                SimpleDescriptor desc = new SimpleDescriptor(p, root, f, pkg);
                result.addResult(desc);
            }
        }
    }

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(FastTypeProvider.class, "LBL_FastJavaIndex"); // NOI18N
    }

    @Override
    public String name() {
        return "fastJavaIndex";  // NOI18N
    }
    
    private class SimpleDescriptor extends TypeDescriptor {
        public static final String JAVA_EXTENSION = ".java"; // NOI18N
        private FileObject      root;
        private String          simpleName;
        private String          pkgName;
        private Project         project;

        public SimpleDescriptor(Project project, FileObject root, CharSequence simpleName, CharSequence pkgName) {
            this.root = root;
            this.simpleName = simpleName.toString();
            this.pkgName = pkgName.toString();
            this.project = project;
        }

        @Override
        public String getContextName() {
            return NbBundle.getMessage(FastTypeProvider.class, "FMT_TypeContextName",
                pkgName == null ? NbBundle.getMessage(FastTypeProvider.class, "LBL_DefaultPackage") : pkgName);
        }

        @Override
        @CheckForNull
        public FileObject getFileObject() {
            String s = simpleName;
            
            if (pkgName != null && !"".equals(pkgName)) {
                StringBuilder sb = new StringBuilder();
                s = sb.append(pkgName).append('.').append(simpleName).toString().replaceAll("\\.", "/"); // NOI18N
            }
            return root.getFileObject(s + JAVA_EXTENSION);
        }

        @Override
        public Icon getIcon() {
            return getClassIcon(); 
        }

        @Override
        public int getOffset() {
            return -1;
        }

        @Override
        public String getOuterName() {
            return null;
        }

        @Override
        public Icon getProjectIcon() {
            return fastIndex.getProjectIcon(project);
        }

        @Override
        public String getProjectName() {
            return fastIndex.getProjectName(project);
        }

        @Override
        public String getSimpleName() {
            return simpleName.toString();
        }

        @Override
        public String getTypeName() {
            return simpleName;
        }
        
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(simpleName).append(" (");
            if (pkgName == null || "".equals(pkgName)) {
                sb.append("Default Package");
            } else {
                sb.append(pkgName);
            }
            sb.append(")");
            return sb.toString();
        }

        @Override
        public void open() {
            boolean success = false;
            try {
                final FileObject fo = getFileObject();
                if (fo != null) {
                    final DataObject d = DataObject.find(fo);
                    final EditCookie cake = d.getCookie(EditCookie.class);
                    if (cake != null) {
                        cake.edit();
                        success = true;
                    }
                }
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
            if (!success) {
                Toolkit.getDefaultToolkit().beep();
            }
        }
        
        
    }

    private static String createCamelCaseRegExp(final String camel, final boolean caseSensitive) {
        final StringBuilder sb = new StringBuilder();
        int lastIndex = 0;
        int index;
        do {
            index = findNextUpper(camel, lastIndex + 1);
            String token = camel.substring(lastIndex, index == -1 ? camel.length(): index);
            sb.append(Pattern.quote(caseSensitive ? token : token.toLowerCase()));
            sb.append( index != -1 ?  "[\\p{javaLowerCase}\\p{Digit}_\\$]*" : ".*"); // NOI18N
            lastIndex = index;
        } while(index != -1);
        return sb.toString();
    }
    
    private static int findNextUpper(String text, int offset ) {
        for( int i = offset; i < text.length(); i++ ) {
            if ( Character.isUpperCase(text.charAt(i)) ) {
                return i;
            }
        }
        return -1;
    }
        
}
