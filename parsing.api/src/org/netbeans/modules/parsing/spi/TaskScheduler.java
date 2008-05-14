/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.parsing.spi;

import java.util.Collection;

import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.impl.CurrentDocumentTaskScheduller;
import org.netbeans.modules.parsing.impl.CursorSensitiveTaskScheduller;
import org.netbeans.modules.parsing.impl.Scheduler;
import org.netbeans.modules.parsing.impl.SelectedNodesTaskScheduller;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;


/**
 * TaskScheduler defines when tasks should be started. Some {@link Task}s (like syntax
 * coloring) are current document sensitive only. It means that such {@link Task} 
 * is automatically scheduled when currently edited document is changed.
 * Other tasks may listen on different events. Implementation of TaskScheduler 
 * just listens on various IDE events, and call {@link #scheduleTasks()} method
 * when something interesting happens. Implementation of Parsing API just finds
 * all {@link Task}s registerred for this TaskScheduler and reschedules them.
 * Implementation of this class should be registerred in your manifest.xml file
 * in "Editors" folder.
 * 
 * @author Jan Jancura
 */
public abstract class TaskScheduler {
    
    private Collection<Source> sources;
    
    /**
     * This implementations of {@link TaskScheduler} reschedules all tasks when:
     * <ol>
     * <li>current document is changed (file opened, closed, editor tab switched), </li>
     * <li>text in the current document is changed, </li>
     * <li>cusor position is changed</li>
     * </ol>
     */
    public static final Class<? extends TaskScheduler> CURSOR_SENSITIVE_TASK_SCHEDULER = CursorSensitiveTaskScheduller.class;
    
    /**
     * This implementations of {@link TaskScheduler} reschedules all tasks when:
     * <ol>
     * <li>current document is changed (file opened, closed, editor tab switched), </li>
     * <li>text in the current document is changed</li>
     * </ol>
     */
    public static final Class<? extends TaskScheduler> EDITOR_SENSITIVE_TASK_SCHEDULER = CurrentDocumentTaskScheduller.class;
    
    /**
     * This implementations of {@link TaskScheduler} reschedules all tasks when
     * nodes selected in editor are changed.
     */
    public static final Class<? extends TaskScheduler> SELECTED_NODES_SENSITIVE_TASK_SCHEDULER = SelectedNodesTaskScheduller.class;

    /**
     * Reschedule all tasks registered for <code>this</code> TaskScheduler (see
     * {@link ParserResultTask#getScheduler()}.
     */
    public final void scheduleTasks () {
        scheduleTasks (sources);
    }

    private Task task;
    
    /**
     * Reschedule all tasks registered for <code>this</code> TaskScheduler (see
     * {@link ParserResultTask#getScheduler()}, and sets new {@link Source}s for them.
     * 
     * @param sources       A collection of {@link Source}s.
     */
    public final void scheduleTasks (Collection<Source> sources) {
        if (task != null)
            task.cancel ();
        this.sources = sources;
        task = RequestProcessor.getDefault ().post (new Runnable () {
            public void run () {
                Scheduler.schedule (TaskScheduler.this, TaskScheduler.this.sources);
            }
        }, 1000);
    }
}






