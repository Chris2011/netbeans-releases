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

package org.netbeans.modules.cnd.debugger.common2;

import java.io.File;

import org.openide.util.Utilities;
import org.openide.loaders.DataNode;
import org.openide.windows.InputOutput;

import org.netbeans.modules.cnd.makeproject.api.ProjectActionEvent;
import org.netbeans.modules.cnd.makeproject.api.configurations.Configuration;

import org.netbeans.modules.cnd.debugger.common2.debugger.DebuggerManager;
import org.netbeans.modules.cnd.debugger.common2.debugger.remote.CndRemote;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import org.netbeans.modules.cnd.makeproject.api.ProjectActionHandler;
import org.netbeans.modules.nativeexecution.api.ExecutionListener;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;

/**
 * Implements debug-related actions on a project.
 */

public class DbgActionHandler implements ProjectActionHandler {
    private Collection<ExecutionListener> listeners = new CopyOnWriteArrayList<ExecutionListener>();

    protected ProjectActionEvent pae;

    public void init(ProjectActionEvent pae, ProjectActionEvent[] paes) {
        this.pae = pae;
    }

    public void addExecutionListener(ExecutionListener l) {
        listeners.add(l);
    }

    public void removeExecutionListener(ExecutionListener l) {
        listeners.remove(l);
    }

    public boolean canCancel() {
        return false;
    }

    /*
     * Called when user cancels execution from progressbar in output window
     */
    public void cancel() {
        // Do nothing for now. See IZ 130827 Cancel running task does not work
    }

    // interface CustomProjectActionHandler
    public void execute(final InputOutput io) {

	// The executable file is already checked and adjusted by the
	// Project system, ProjectActionSupport$HandleEvents.checkExecutable(),
	// before being passed to us.
        final String executable = pae.getExecutable();
	final DebuggerManager dm = DebuggerManager.get();
	dm.setIO(io);
	String hostName = CndRemote.userhostFromConfiguration(pae.getConfiguration());
	CndRemote.validate(hostName, new Runnable() {
		public void run() {
			doExecute(executable, dm, io);
		}
	});
    }

    private void doExecute(final String executable, final DebuggerManager dm, final InputOutput io) {
	final Configuration configuration = pae.getConfiguration();
	// DefaultProjectActionHandler's executionStarted is a no-op.

	executionStarted();

        Runnable loadProgram = new Runnable() {
            public void run() {
                if (pae.getType() == ProjectActionEvent.PredefinedType.DEBUG) {
		    dm.setAction(DebuggerManager.RUN);
		    dm.removeAction(DebuggerManager.STEP);
		    DebuggerManager.get().debug(executable,
						configuration,
						CndRemote.userhostFromConfiguration(configuration),
                                                io);

                } else if (pae.getType() == ProjectActionEvent.PredefinedType.DEBUG_STEPINTO) {
		    dm.setAction(DebuggerManager.STEP);
		    dm.removeAction(DebuggerManager.RUN);
		    DebuggerManager.get().debug(executable,
						configuration,
						CndRemote.userhostFromConfiguration(configuration),
                                                io);
		} else {
                    assert false;
                }
            }
        };
        javax.swing.SwingUtilities.invokeLater(loadProgram);
	executionFinished(0);
    }

    private void executionStarted() {
        for (ExecutionListener listener : listeners) {
            listener.executionStarted(ExecutionListener.UNKNOWN_PID);
        }
    }

    public void executionFinished(int rc) {
	// FIXUP: executionFinished should be called when debugging is really
	// done. CND hangs on to output window until released by
	// executionFinished. Debugging is done asychronysly so
	// executionFinished is called right after it starts releasing
	// the output window prematurely.
	// This is also causing another problem with the progress bar.
	// Apparently the progress bar cannot be dismissed right after it
	// has been started. Adding an artificial sleep here before calling
	// listener.executionFinished fixes this problem.
	// The problem with the progress bar should really be fixed in CND
	// but it is too late to do that in 6.1. It will get wixed in
	// whetever release SS with be released on so consider the
	// sleep a work-arounb for now.
	// Moving call to executionFinished to when debugger is done
	// will also fix it.
        try {
	    Thread.sleep(500);
	}
	catch(Exception e){}

        for (ExecutionListener listener : listeners) {
            listener.executionFinished(rc);
        }
    }

    private static DataNode findDebuggableNode(String filePath) {
        FileObject fo = FileUtil.toFileObject(FileUtil.normalizeFile(new File(filePath)));
        if (fo == null) {
            return null; // FIXUP
        }
        DataObject dataObject = null;
        try {
            dataObject = DataObject.find(fo);
        } catch (Exception e) {
            // FIXUP
        }
        if (dataObject == null) {
            return null; // FIXUP
        }
        Node node = dataObject.getNodeDelegate();
        if (node == null) {
            return null; // FIXUP
        }
        if (!(node instanceof DataNode)) {
            return null;
        }
        return (DataNode)node;
    }
}
