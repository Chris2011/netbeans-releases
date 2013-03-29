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
package org.netbeans.lib.profiler.results.locks;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.netbeans.lib.profiler.results.CCTNode;

/**
 *
 * @author Tomas Hurka
 * @author Jiri Sedlacek
 */
public abstract class LockCCTNode implements CCTNode {
    
    public static final int SORT_BY_NAME = 1;
    public static final int SORT_BY_TIME = 2;
    public static final int SORT_BY_WAITS = 3;

    private List<LockCCTNode> children;
    private final LockCCTNode parent;

    public LockCCTNode() { // temporary - only for testing
        parent = null;
    }

    LockCCTNode(LockCCTNode p) {
        parent = p;
    }

    @Override
    public LockCCTNode getChild(int index) {
        if (children == null) {
            computeChildren();
        }
        return children.get(index);
    }

    @Override
    public LockCCTNode[] getChildren() {
        if (children == null) {
            computeChildren();
        }
        return children.toArray(new LockCCTNode[children.size()]);
    }

    @Override
    public int getIndexOfChild(Object child) {
        if (children == null) {
            computeChildren();
        }
        return children.indexOf(child);
    }

    @Override
    public int getNChildren() {
        if (children == null) {
            computeChildren();
        }
        return children.size();
    }

    @Override
    public LockCCTNode getParent() {
        return parent;
    }

    void addChild(LockCCTNode child) {
        if (children == null) {
            computeChildren();
        }
        children.add(child);
    }

    void computeChildren() {
        children = new ArrayList();
    }

    public double getTimeInPerCent() {
        long allTime = getParent().getTime();
        long time = getTime();
        return 100.0 * time / allTime;
    }

    public abstract String getNodeName();

    public abstract long getTime();

    public abstract long getWaits();
    
    public boolean isThreadLockNode() { return false; }
    public boolean isMonitorNode() { return false; }
    
    
    public void sortChildren(int sortBy, boolean sortOrder) {
        if (children == null || getNChildren() < 2) return;
        doSortChildren(sortBy, sortOrder);
    }
    
    protected void doSortChildren(int sortBy, boolean sortOrder) {
        switch (sortBy) {
            case SORT_BY_NAME:
                sortChildrenByName(sortOrder);
                break;
            case SORT_BY_TIME:
                sortChildrenByTime(sortOrder);
                break;
            case SORT_BY_WAITS:
                sortChildrenByWaits(sortOrder);
                break;
        }
        
//        boolean main = getNodeName().startsWith("main");
//        if (main) System.err.println(">>> SORTING MAIN " + System.identityHashCode(this));
        for (LockCCTNode child : children) {
            child.sortChildren(sortBy, sortOrder);
//            if (main) System.err.println(">>>      Sorting child " + child.getNodeName());
        }
    }

    protected void sortChildrenByName(final boolean sortOrder) {
        Collections.sort(children, new Comparator<LockCCTNode>() {
            public int compare(LockCCTNode n1, LockCCTNode n2) {
                return sortOrder ?
                       n1.getNodeName().toLowerCase().compareTo(n2.getNodeName().toLowerCase()) :
                       n2.getNodeName().toLowerCase().compareTo(n1.getNodeName().toLowerCase());
            }
        });
    }

    protected void sortChildrenByTime(final boolean sortOrder) {
        Collections.sort(children, new Comparator<LockCCTNode>() {
            public int compare(LockCCTNode n1, LockCCTNode n2) {
                return sortOrder ?
                       (int)(n1.getTime() - n2.getTime()) :
                       (int)(n2.getTime() - n1.getTime());
            }
        });
    }
    
    protected void sortChildrenByWaits(final boolean sortOrder) {
        Collections.sort(children, new Comparator<LockCCTNode>() {
            public int compare(LockCCTNode n1, LockCCTNode n2) {
                return sortOrder ?
                       (int)(n1.getWaits() - n2.getWaits()) :
                       (int)(n2.getWaits() - n1.getWaits());
            }
        });
    }
    

    public void debug() {
        if (parent != null) {
            String offset = "";
            for (CCTNode p = parent; p != null; p = p.getParent()) {
                offset += "  ";
            }
            System.out.println(offset + getNodeName() + 
                    " Waits: " + getWaits() + 
                    " Time: " + getTime() + 
                    " " + NumberFormat.getPercentInstance().format(getTimeInPerCent()/100));
        }
        for (CCTNode ch : getChildren()) {
            if (ch instanceof LockCCTNode) {
                ((LockCCTNode) ch).debug();
            }
        }
    }
}
