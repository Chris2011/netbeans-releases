/*
 * CacheWriterTask.java
 *
 * Created on February 19, 2004, 12:07 AM
 */

package org.netbeans.imagecache;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.tools.ant.*;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

/**
 *
 * @author  tim
 */
public class CacheWriterTask extends Task {
    private File outDir = null;
    private List paths = new ArrayList();
    private boolean clean = true;
    /** Creates a new instance of CacheWriterTask */
    public CacheWriterTask() {
    }
    
    public void setDir (File dir) {
        paths.add(new Path(getProject(), dir.toString()));
    }
    
    public void setOutdir (File dir) {
        this.outDir = dir;
    }
    
    public void addPath(Path fs) {
        paths.add (fs);
    }
    
    public void setClean (boolean clean) {
        this.clean = clean;
    }
    
    public void execute() throws BuildException {
        if (paths.isEmpty()) {
            throw new BuildException ("Source dir or fileset required to scan for images");
        }
        if (outDir == null) {
            throw new BuildException ("Output directory for cache file must be specified");
        }
        
        try {
            CacheWriter writer = new CacheWriter();
            writer.setDir(outDir.toString(), clean);

            Iterator it = paths.iterator();
            while (it.hasNext()) {
                Path curr = (Path) it.next();
                String[] dirs = curr.list();
                for (int i=0; i < dirs.length; i++) {
                    System.err.println("WriteDir " + dirs[i]);
                    writer.writeDir(dirs[i], true);
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new BuildException (ioe.getMessage());
        }
    }
}
