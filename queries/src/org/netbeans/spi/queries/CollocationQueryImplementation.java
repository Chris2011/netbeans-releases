/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.spi.queries;

import java.io.File;

/**
 * A query which should typically be provided by a VCS to give information
 * about whether some files can be considered part of one logical directory tree.
 * This should be treated as a heuristic, useful when deciding whether to use
 * absolute or relative links between path locations.
 * <p>
 * The file names might refer to nonexistent files. A provider may or may not
 * be able to say anything useful about them in this case.
 * @see org.netbeans.api.queries.CollocationQuery
 * <p>
 * File names passed to this query will already have been normalized (e.g. no
 * ".." path components, and absolute) but not necessarily canonicalized (e.g.
 * may still contain symlink components).
 * @author Jesse Glick
 */
public interface CollocationQueryImplementation {
    
    /**
     * Check whether two files are logically part of one directory tree.
     * For example, if both files are stored in CVS, with the same server
     * (<code>CVSROOT</code>) they might be considered collocated.
     * If they are to be collocated their absolute paths must share a
     * prefix directory, i.e. they must be located in the same filesystem root.
     * If nothing is known about them, return false.
     * @param file1 one file
     * @param file2 another file
     * @return true if they are probably part of one logical tree
     */
    boolean areCollocated(File file1, File file2);
    
    /**
     * Find a root of a logical tree containing this file, if any.
     * The path of the root (if there is one) must be a prefix of the path of the file.
     * @param file a file on disk (must be an absolute URI)
     * @return an ancestor directory which is the root of a logical tree,
     *         if any (else null) (must be an absolute URI)
     */
    File findRoot(File file);
    
}
