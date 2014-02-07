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
package org.netbeans.modules.csl.core;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.netbeans.api.java.classpath.ClassPath;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.ExtensionList;
import org.openide.loaders.FileEntry;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.UniFileLoader;
import org.openide.util.MapFormat;
import org.openide.util.NbBundle;


/**
 * Loader for recognizing languages handled by the generic scripting framework
 *
 * @author Tor Norbye
 */
public class GsfDataLoader extends UniFileLoader {
    boolean initialized;
    volatile Set<String> registeredMimes = Collections.emptySet();
    
    public GsfDataLoader() {
        super("org.netbeans.modules.csl.core.GsfDataObject");
        initExtensions();
    }

    final void initExtensions() {
        ExtensionList list = new ExtensionList();
        Set mimes = new HashSet<String>();
        for (Language language : LanguageRegistry.getInstance()) {
            
            if(language.useCustomEditorKit()) {
                //do not try to load files which has their own editor support and dataobject
                continue;
            }
            mimes.add(language.getMimeType());
            list.addMimeType(language.getMimeType());
        }
        setExtensions(list);
        registeredMimes = mimes;
        initialized = true;
    }

    /**
     * In addition to mimetype list, checks base mime type if the mime is compound.
     * This is a workaround for loaders implementation do not currently support compound MIME types.
     * See defect #
     * 
     * @param fo file object to recognize 
     * @return primary file / null
     */
    @Override
    protected FileObject findPrimaryFile(FileObject fo) {
        FileObject pf = super.findPrimaryFile(fo);
        if (pf != null) {
            return pf;
        }
        String mime = fo.getMIMEType();
        int slash = -1;
        int l = mime.length();
        for (int i = 0; i < l; i++) {
            char c = mime.charAt(i);
            if (c == '/') { // NOI18N
                slash = i;
            } else if (c == '+') { // NOI18N
                if (slash == -1) {
                    return null;
                }
                String baseMime = mime.substring(0, slash + 1) + mime.substring(i + 1);
                if (registeredMimes.contains(baseMime)) {
                    return fo;
                }
            }
        }
        return null;
    }

    @Override
    protected MultiDataObject createMultiObject(FileObject primaryFile)
        throws DataObjectExistsException, IOException {
        Language language =
            LanguageRegistry.getInstance().getLanguageByMimeType(primaryFile.getMIMEType());

        return new GsfDataObject(primaryFile, this, language);
    }

    @Override
    protected String defaultDisplayName() {
        // Create a list of languages to include in the display
        StringBuilder sb = new StringBuilder();

        for (Language language : LanguageRegistry.getInstance()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }

            sb.append(language.getDisplayName());
        }

        return NbBundle.getMessage(GsfDataLoader.class, "GenericLoaderName", sb.toString());
    }

    @Override
    protected MultiDataObject.Entry createPrimaryEntry (MultiDataObject obj, FileObject primaryFile) {
        FileEntry.Format entry = new FileEntry.Format(obj, primaryFile) {
            @Override
            protected java.text.Format createFormat (FileObject target, String n, String e) {
                ClassPath cp = ClassPath.getClassPath(target, ClassPath.SOURCE);
                String resourcePath = "";
                if (cp != null) {
                    resourcePath = cp.getResourceName(target);
                    if (resourcePath == null) {
                        resourcePath = ""; // NOI18N
                    }
                } else {
                    ErrorManager.getDefault().log(ErrorManager.WARNING, "No classpath was found for folder: "+target);
                }
                Map<String,String> m = new HashMap<String,String>();
                m.put("NAME", n ); //NOI18N
                String capitalizedName;
                if (n.length() > 1) {
                    capitalizedName = Character.toUpperCase(n.charAt(0))+n.substring(1);
                } else if (n.length() == 1) {
                    capitalizedName = ""+Character.toUpperCase(n.charAt(0));
                } else {
                    capitalizedName = "";
                }
                m.put("CAPITALIZEDNAME", capitalizedName); //NOI18N
                m.put("LOWERNAME", n.toLowerCase()); //NOI18N
                m.put("UPPERNAME", n.toUpperCase()); //NOI18N

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < n.length(); i++) {
                    char c = n.charAt(i);
                    if (Character.isJavaIdentifierPart(c)) {
                        sb.append(c);
                    }
                }
                String identifier = sb.toString();
                m.put("IDENTIFIER", identifier); // NOI18N
                sb.setCharAt(0, Character.toUpperCase(identifier.charAt(0)));
                m.put("CAPITALIZEDIDENTIFIER", sb.toString()); // NOI18N
                m.put("LOWERIDENTIFIER", identifier.toLowerCase()); //NOI18N
                
                // Yes, this is package sans filename (target is a folder).
                String packageName = resourcePath.replace('/', '.');
                m.put("PACKAGE", packageName); // NOI18N
                String capitalizedPkgName;
                if (packageName == null || packageName.length() == 0) {
                    packageName = "";
                    capitalizedPkgName = "";
                } else if (packageName.length() > 1) {
                    capitalizedPkgName = Character.toUpperCase(packageName.charAt(0))+packageName.substring(1);
                } else {
                    capitalizedPkgName = ""+Character.toUpperCase(packageName.charAt(0));
                }
                m.put("CAPITALIZEDPACKAGE", capitalizedPkgName); // NOI18N
                m.put("PACKAGE_SLASHES", resourcePath); // NOI18N
                // Fully-qualified name:
                if (target.isRoot ()) {
                    m.put ("PACKAGE_AND_NAME", n); // NOI18N
                    m.put ("PACKAGE_AND_NAME_SLASHES", n); // NOI18N
                } else {
                    m.put ("PACKAGE_AND_NAME", resourcePath.replace('/', '.') + '.' + n); // NOI18N
                    m.put ("PACKAGE_AND_NAME_SLASHES", resourcePath + '/' + n); // NOI18N
                }
                m.put("DATE", DateFormat.getDateInstance(DateFormat.LONG).format(new Date())); // NOI18N
                m.put("TIME", DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date())); // NOI18N
                MapFormat f = new MapFormat(m);
                f.setLeftBrace( "__" ); //NOI18N
                f.setRightBrace( "__" ); //NOI18N
                f.setExactMatch(false);

                return f;
            }
        };
        return entry;
    }
}
