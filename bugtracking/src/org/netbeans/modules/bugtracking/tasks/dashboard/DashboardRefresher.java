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
package org.netbeans.modules.bugtracking.tasks.dashboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.netbeans.modules.bugtracking.IssueImpl;
import org.netbeans.modules.bugtracking.QueryImpl;
import org.netbeans.modules.bugtracking.RepositoryImpl;
import org.netbeans.modules.bugtracking.spi.IssueStatusProvider;
import org.netbeans.modules.bugtracking.tasks.Category;
import org.netbeans.modules.bugtracking.settings.DashboardSettings;
import org.netbeans.modules.bugtracking.tasks.DashboardUtils;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;

/**
 *
 * @author jpeska
 */
public class DashboardRefresher {

    private static final RequestProcessor RP = new RequestProcessor(DashboardRefresher.class.getName());
    private final Task refreshDashboard;
    private final Task refreshSchedule;
    private static DashboardRefresher instance;
    private boolean refreshEnabled;
    private boolean dashboardBusy = false;
    private boolean refreshWaiting = false;

    private DashboardRefresher() {
        refreshDashboard = RP.create(new Runnable() {
            @Override
            public void run() {
                if (dashboardBusy || !refreshEnabled) {
                    refreshWaiting = true;
                    return;
                }
                try {
                    refreshDashboard();
                } finally {
                    setupDashboardRefresh();
                }
            }
        });

        refreshSchedule = RP.create(new Runnable() {
            @Override
            public void run() {
                try {
                    DashboardViewer.getInstance().updateScheduleCategories();
                } finally {
                    setupScheduleRefresh();
                }
            }
        });
    }

    public static DashboardRefresher getInstance() {
        if (instance == null) {
            instance = new DashboardRefresher();
        }
        return instance;
    }

    public void setupDashboardRefresh() {
        final DashboardSettings settings = DashboardSettings.getInstance();
        if (!settings.isAutoSync() || !refreshEnabled) {
            return;
        }
        refreshDashboard.cancel();
        scheduleDashboardRefresh();
    }

    public void setupScheduleRefresh() {
        refreshSchedule.cancel();
        refreshSchedule.schedule(DashboardUtils.getMillisToTomorrow());
    }

    private void scheduleDashboardRefresh() {
        final DashboardSettings settings = DashboardSettings.getInstance();
        int delay = settings.getAutoSyncValue();
        refreshDashboard.schedule(delay * 60 * 1000); // given in minutes
    }

    public void setRefreshEnabled(boolean refreshEnabled) {
        this.refreshEnabled = refreshEnabled;
    }

    public void setDashboardBusy(boolean dashboardBusy) {
        this.dashboardBusy = dashboardBusy;
        if (!dashboardBusy && refreshWaiting) {
            refreshWaiting = false;
            refreshDashboard.schedule(0);
        }
    }

    private void refreshDashboard() {
        List<RepositoryImpl> repositories = DashboardViewer.getInstance().getRepositories(true);
        List<Category> categories = DashboardViewer.getInstance().getCategories(true, true);
        for (RepositoryImpl<?, ?, ?> repository : repositories) {
            for (QueryImpl query : repository.getQueries()) {
                query.refresh();
            }
        }

        for (Category category : categories) {
            category.refresh();
        }
    }
}
