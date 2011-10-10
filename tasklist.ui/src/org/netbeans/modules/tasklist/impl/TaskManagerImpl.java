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

package org.netbeans.modules.tasklist.impl;

import java.util.List;
import org.netbeans.modules.tasklist.filter.TaskFilter;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.parsing.api.indexing.IndexingManager;
import org.netbeans.modules.parsing.spi.indexing.support.QuerySupport;
import org.netbeans.modules.tasklist.trampoline.TaskManager;
import org.netbeans.spi.tasklist.FileTaskScanner;
import org.netbeans.spi.tasklist.PushTaskScanner;
import org.netbeans.spi.tasklist.Task;
import org.netbeans.spi.tasklist.TaskScanningScope;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;
import org.openide.util.RequestProcessor;

/**
 * Default implementation of Task Manager
 *
 * @author S. Aubrecht
 */
public class TaskManagerImpl extends TaskManager {
    
    public static final String PROP_SCOPE = "taskScanningScope"; //NOI18N
    
    public static final String PROP_FILTER = "filter"; //NOI18N

    public static final String PROP_WORKING_STATUS = "workingStatus"; //NOI18N
    
    private PropertyChangeSupport propertySupport = new PropertyChangeSupport( this );
    
    private TaskList taskList = new TaskList();
    private TaskScanningScope scope = Accessor.getEmptyScope();
    private TaskFilter filter = TaskFilter.EMPTY;
    
    private static TaskManagerImpl theInstance;
    
    private final Set<PushTaskScanner> workingScanners = new HashSet<PushTaskScanner>(10);
    private boolean isLoadingFromCache = false;
    private boolean workingStatus = false;
    private Loader loader;

    public static final RequestProcessor RP = new RequestProcessor("TaskList"); //NOI18N

    public static TaskManagerImpl getInstance() {
        if( null == theInstance )
            theInstance = new TaskManagerImpl();
        return theInstance;
    }

    public void observe( final TaskScanningScope newScope, final TaskFilter newFilter ) {
        RP.post( new Runnable() {
            @Override
            public void run() {
                doObserve( newScope, newFilter );
            }
        });
    }
    
    private void doObserve( TaskScanningScope newScope, TaskFilter newFilter ) {
        TaskScanningScope oldScope = scope;
        TaskFilter oldFilter = filter;
        synchronized( this ) {
            if( null == newScope || Accessor.getEmptyScope().equals( newScope ) ) {
                scope.attach( null );
                //turn off
                stopLoading();
                
                workingScanners.clear();
                isLoadingFromCache = false;
                
                //detach simple/file scanners
                for( PushTaskScanner scanner : ScannerList.getPushScannerList().getScanners() ) {
                    scanner.setScope( null, null );
                }
                for( FileTaskScanner scanner : ScannerList.getFileScannerList().getScanners() ) {
                    scanner.attach( null );
                }
                scope = Accessor.getEmptyScope();
                filter = TaskFilter.EMPTY;

                taskList.clear();

                setWorkingStatus(false);
            } else {
                boolean dirtyCache = NbPreferences.forModule(TaskManagerImpl.class).getBoolean("dirtyCache", false);
                NbPreferences.forModule(TaskManagerImpl.class).putBoolean("dirtyCache", false);
                
                //turn on or switch scope/filter
                if( null == newFilter )
                    newFilter = TaskFilter.EMPTY;
                
                if( !scope.equals(newScope) || !filter.equals(newFilter) ) {
                    taskList.clear();
                    
                    if( !newScope.equals( scope ) ) {
                        scope.attach( null );
                        newScope.attach( Accessor.createCallback( this, newScope ) );
                    }
                    
                    workingScanners.clear();
                    isLoadingFromCache = false;
                    setWorkingStatus( false );

                    boolean scannersHaveChanged = compareScanners( filter, newFilter );
                
                    scope = newScope;
                    filter = newFilter;
                    
                    attachFileScanners( newFilter );
                    attachPushScanners( newScope, newFilter );

                    if( scannersHaveChanged || dirtyCache ) {
                        clearCache();
                    } else {
                        startLoading();
                    }
                }
            }
        }
        propertySupport.firePropertyChange( PROP_SCOPE, oldScope, newScope );
        propertySupport.firePropertyChange( PROP_FILTER, oldFilter, newFilter );
    }

    private boolean compareScanners(TaskFilter oldFilter, TaskFilter newFilter) {
        if( null == oldFilter || oldFilter == TaskFilter.EMPTY )
            return false;
        List<FileTaskScanner> oldScanners = ScannerList.getFileScanners(oldFilter);
        List<FileTaskScanner> newScanners = ScannerList.getFileScanners(newFilter);
        if( oldScanners.size() > 0 && oldScanners.size() != newScanners.size() )
            return true;

        for( FileTaskScanner scanner : oldScanners ) {
            if( !newScanners.contains(scanner) )
                return true;
        }
        return false;
    }
    
    private void attachFileScanners( TaskFilter newFilter ) {
        for( FileTaskScanner scanner : getFileScanners() ) {
            if( !newFilter.isEnabled( scanner ) )
                scanner.attach( null );
            else if( newFilter.isEnabled( scanner ) )
                scanner.attach( Accessor.createCallback( this, scanner ) );
        }
    }
    
    private void attachPushScanners( TaskScanningScope newScope, TaskFilter newFilter ) {
        for( PushTaskScanner scanner : getPushScanners() ) {
            if( !newFilter.isEnabled( scanner ) ){
                scanner.setScope( null, null );
            }else if( newFilter.isEnabled( scanner ) ){
                scanner.setScope( newScope, Accessor.createCallback( this, scanner ) );
            }
        }
    }
    
    Iterable<? extends FileTaskScanner> getFileScanners() {
        return ScannerList.getFileScannerList().getScanners();
    }
    
    Iterable<? extends PushTaskScanner> getPushScanners() {
        return ScannerList.getPushScannerList().getScanners();
    }

    public void abort() {
        RP.post( new Runnable() {
            @Override
            public void run() {
                doAbort();
            }
        });
    }

    private void doAbort() {
        stopLoading();

        for( PushTaskScanner scanner : ScannerList.getPushScannerList().getScanners() ) {
            scanner.setScope( null, null );
        }

        workingScanners.clear();
        setWorkingStatus( false );
    }
    
    boolean isObserved() {
        return !Accessor.getEmptyScope().equals( getScope() );
    }
    
    public TaskScanningScope getScope() {
        return scope;
    }
    
    public TaskList getTasks() {
        return taskList;
    }
    
    public void addPropertyChangeListener( PropertyChangeListener listener ) {
        propertySupport.addPropertyChangeListener( listener );
    }
    
    public void addPropertyChangeListener( String propName, PropertyChangeListener listener ) {
        propertySupport.addPropertyChangeListener( propName, listener );
    }
    
    public void removePropertyChangeListener( PropertyChangeListener listener ) {
        propertySupport.removePropertyChangeListener( listener );
    }
    
    public void removePropertyChangeListener( String propName, PropertyChangeListener listener ) {
        propertySupport.removePropertyChangeListener( propName, listener );
    }
    
    private void startLoading() {
        if( null != loader )
            loader.cancel();

        loader = new Loader( scope, filter, taskList );
        RP.post(loader);
    }
    
    private void stopLoading() {
        if( null != loader )
            loader.cancel();
        loader = null;
        isLoadingFromCache = false;
        setWorkingStatus(isWorking());
    }
    
    public TaskFilter getFilter() {
        return filter;
    }

    @Override
    public void refresh( final FileTaskScanner scanner, final FileObject... resources) {
        try {
            synchronized( this ) {
                taskList.clear( scanner, resources );
            }
            ArrayList<URL> toRefresh = new ArrayList<URL>(1);
            for( FileObject fo : resources ) {
                toRefresh.clear();
                toRefresh.add(fo.getURL());
                Collection<FileObject> roots = QuerySupport.findRoots(fo, null, null, null);
                for( FileObject root : roots ) {
                    IndexingManager.getDefault().refreshIndex(root.getURL(), toRefresh);
                }
            }
        } catch( IOException ioE ) {
            getLogger().log(Level.INFO, "Error while refreshing files.", ioE);
        }
    }
    
    @Override
    public void refresh( FileTaskScanner scanner ) {
        synchronized( this ) {
            taskList.clear( scanner );
            clearCache();
        }
    }

    public void clearCache() {
        IndexingManager.getDefault().refreshAllIndices(TaskIndexerFactory.INDEXER_NAME);
    }

    void makeCacheDirty() {
        synchronized( this ) {
            NbPreferences.forModule(TaskManagerImpl.class).putBoolean("dirtyCache", true);
        }
    }

    @Override
    public void refresh( final TaskScanningScope scopeToRefresh ) {
        if( this.scope.equals( scopeToRefresh ) ) {
            RP.post( new Runnable() {
                @Override
                public void run() {
                    doRefresh( scopeToRefresh );
                }
            });
        }
    }
    
    private void doRefresh( TaskScanningScope scopeToRefresh ) {
        synchronized( this ) {
            if( this.scope.equals( scopeToRefresh ) ) {
                taskList.clear();
                if( isObserved() ) {
                    for( PushTaskScanner scanner : ScannerList.getPushScannerList().getScanners() ) {
                        scanner.setScope( null, null );
                        if( getFilter().isEnabled( scanner ) )
                            scanner.setScope( scopeToRefresh, Accessor.createCallback( this, scanner ) );
                    }
                    startLoading();
                }
            }
        }
    }

    @Override
    public void started(PushTaskScanner scanner) {
        synchronized( workingScanners ) {
            workingScanners.add( scanner );
            setWorkingStatus( true );
        }
    }

    @Override
    public void finished(PushTaskScanner scanner) {
        synchronized( workingScanners ) {
            workingScanners.remove( scanner );
            setWorkingStatus( isWorking() );
        }
    }

    @Override
    public void setTasks( PushTaskScanner scanner, FileObject resource, List<? extends Task> tasks ) {
        if( isObserved() && scope.isInScope( resource ) ) {
            try {
                taskList.setTasks(scanner, resource, tasks, filter);
            } catch( IOException ioE ) {
                getLogger().log(Level.INFO, "Error while updating tasks from " + Accessor.getDisplayName(scanner), ioE);
            }
        }
    }

    @Override
    public void setTasks( PushTaskScanner scanner, List<? extends Task> tasks ) {
        if( isObserved() ) {
            try {
                taskList.setTasks(scanner, null, tasks, filter);
            } catch( IOException ioE ) {
                getLogger().log(Level.INFO, "Error while updating tasks from " + Accessor.getDisplayName(scanner), ioE);
            }
        }
    }
    
    @Override
    public void clearAllTasks( PushTaskScanner scanner ) {
        taskList.clear( scanner );
    }
    
    private Logger getLogger() {
        return Logger.getLogger( TaskManagerImpl.class.getName() );
    }
    
    private boolean isWorking() {
        synchronized( workingScanners ) {
            return !workingScanners.isEmpty() || isLoadingFromCache;
        }
    }

    void setLoadingStatus( Loader loader, boolean isLoading ) {
        synchronized( this ) {
            if( this.loader != loader )
                return;
            isLoadingFromCache = isLoading;
        }
        setWorkingStatus(isWorking());
    }
    
    private void setWorkingStatus( boolean newStatus ) {
        synchronized( workingScanners ) {
            if( newStatus != workingStatus ) {
                boolean oldStatus = workingStatus;
                workingStatus = newStatus;
                Logger.getLogger("org.netbeans.log.startup").log(Level.FINE,  // NOI18N
                        newStatus ? "start" : "end", TaskManagerImpl.class.getName()); // NOI18N

                propertySupport.firePropertyChange( PROP_WORKING_STATUS, oldStatus, newStatus );
                //for unit testing
                if( !workingStatus ) {
                    workingScanners.notifyAll();
                }
            }
        }
    }
    /**
     * For unit testing only
     */
    void waitFinished() {
        synchronized( workingScanners ) {
            if( !isWorking() )
                return;
            _waitFinished();
        }
    }
    
    /**
     * For unit testing only
     */
    void _waitFinished() {
        synchronized( workingScanners ) {
            try {
                workingScanners.wait();
            }
            catch( InterruptedException e ) {
                Exceptions.printStackTrace( e );
            }
        }
    }
    
    void rootScanned(final FileObject root) {        
        if (scope.isInScope(root)) {
            RP.post( new Runnable() {
                @Override
                public void run() {
                    if (scope.isInScope(root)) {
                        loader = new Loader( scope, filter, taskList );
                        loader.refresh(root);
                    }
                }
            });            
        }
    }
}
