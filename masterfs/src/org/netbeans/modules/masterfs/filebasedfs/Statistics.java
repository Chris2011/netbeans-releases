/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.masterfs.filebasedfs;
import java.util.Iterator;
import org.netbeans.modules.masterfs.filebasedfs.naming.NamingFactory;

/**
 *
 * @author Radek Matous
 */
public final class Statistics {
    /** Creates a new instance of Statistics */

    public static final Statistics.TimeConsumer REFRESH_FOLDER =
            new Statistics.TimeConsumer("Folder refresh");//NOI18N
    public static final Statistics.TimeConsumer REFRESH_FILE =
            new Statistics.TimeConsumer("File refresh");//NOI18N
    public static final Statistics.TimeConsumer REFRESH_FS = 
            new Statistics.TimeConsumer("FileSystem refresh");//NOI18N    
    public static final Statistics.TimeConsumer LISTENERS_CALLS = 
            new Statistics.TimeConsumer("Invocation of FileChangeListeners");//NOI18N    
    
    
    private Statistics() {}
    
    
    public static Statistics.StopWatch getStopWatch(Statistics.TimeConsumer consumer) {
        return new Statistics.StopWatch(consumer);
    }
    
    public static int fileSystems() {
        return FileBasedFileSystem.getSize();
    }
    
    public static int fileNamings() {
        return NamingFactory.getSize();
    }
    
    public static int fileObjects() {
        int retVal = 0;
        Iterator it = FileBasedFileSystem.getInstances().iterator();
        for (int i = 0; it.hasNext(); i++) {
            FileBasedFileSystem fbs = (FileBasedFileSystem)it.next();
            retVal += fileObjectsPerFileSystem(fbs);
        }
        
        return retVal;
    }
    
    public static int fileObjectsPerFileSystem(FileBasedFileSystem fbs) {
        return fbs.getFactory().getSize();
    }

            
    public static final class TimeConsumer {
        private int elapsedTime;
        private int numberOfCalls;
        private final String description;
        
        private TimeConsumer(final String description) {
            this.description = description;
        }
        
        public int getConsumedTime() {
            return elapsedTime;
        }
        
        public int getNumberOfCalls() {
            return numberOfCalls;
        }        
        
        public void reset() {
    	    elapsedTime = 0;
            numberOfCalls = 0;
        }

        public String toString() {
            return description + ": " + numberOfCalls + " calls in " + elapsedTime + "ms";
        }
		
        private void incrementNumerOfCalls() {
            numberOfCalls++;
            
        }
    }
    
    public static final class StopWatch {
        private long startTime = 0;
        private final Statistics.TimeConsumer activity;
        
        
        /** Creates a new instance of ElapsedTime */
        private StopWatch(Statistics.TimeConsumer activity) {
            this.activity = activity;
        }
        
        
        public void start() {
            startTime = System.currentTimeMillis();
        }
        
        public void stop() {
            assert startTime != 0;
            activity.elapsedTime += (System.currentTimeMillis() - startTime);
            activity.incrementNumerOfCalls();
            startTime = 0;
        }
    }
}

