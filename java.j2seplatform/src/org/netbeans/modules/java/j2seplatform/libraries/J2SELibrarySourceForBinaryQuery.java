/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.java.j2seplatform.libraries;


import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.spi.java.queries.SourceForBinaryQueryImplementation;
import org.netbeans.modules.java.j2seplatform.platformdefinition.Util;

/**
 *
 * @author  tom
 */
public class J2SELibrarySourceForBinaryQuery implements SourceForBinaryQueryImplementation {
    
    /** Creates a new instance of J2SELibrarySourceForBinaryQuery */
    public J2SELibrarySourceForBinaryQuery() {
    }

    /**
     *
     * @param binaryRoot supports file and jar schema
     * @return FileObject[], never returns null
     */
    public org.openide.filesystems.FileObject[] findSourceRoot(java.net.URL binaryRoot) {
        LibraryManager lm = LibraryManager.getDefault ();
        Library[] libs = lm.getLibraries();
        for (int i=0; i< libs.length; i++) {
            String type = libs[i].getType ();
            if (J2SELibraryTypeProvider.LIBRARY_TYPE.equalsIgnoreCase(type)) {
                List classes = Util.getResourcesRoots(libs[i].getContent("classpath"));    //NOI18N
                for (Iterator it = classes.iterator(); it.hasNext();) {
                    URL entry = (URL) it.next();
                    if (entry.equals(binaryRoot)) {
                        List src = Util.getResourcesRoots(libs[i].getContent("src"));              //NOI18N
                        List result = new ArrayList ();
                        for (Iterator sit = src.iterator(); sit.hasNext();) {
                            FileObject sourceRootURL = FileUtil.findFileObject((URL) sit.next());
                            if (sourceRootURL!=null) {
                                result.add (sourceRootURL);
                            }
                        }
                        return (FileObject[]) result.toArray(new FileObject[result.size()]);
                    }
                }
            }
        }
        return new FileObject[0];
    }
}
