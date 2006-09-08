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

package org.netbeans.core.windows.model;


import org.openide.windows.TopComponent;

import java.util.Collection;
import java.util.Set;


/**
 *
 * @author  Peter Zavadsky
 */
interface TopComponentGroupModel {

    public String getName();

    public void open(
            Collection<TopComponent> openedTopComponents, 
            Collection<TopComponent> openedBeforeTopComponents);
    public void close();
    public boolean isOpened();

    public Set<TopComponent> getTopComponents();

    public Set<TopComponent> getOpenedTopComponents();
    public Set<TopComponent> getOpenedBeforeTopComponents();

    public Set<TopComponent> getOpeningTopComponents();
    public Set<TopComponent> getClosingTopComponents();
    
    public boolean addUnloadedTopComponent(String tcID);
    public boolean removeUnloadedTopComponent(String tcID);
    
    public boolean addOpeningTopComponent(TopComponent tc);
    public boolean removeOpeningTopComponent(TopComponent tc);
    
    public boolean addUnloadedOpeningTopComponent(String tcID);
    public boolean removeUnloadedOpeningTopComponent(String tcID);
    
    public boolean addUnloadedClosingTopComponent(String tcID);
    public boolean removeUnloadedClosingTopComponent(String tcID);
    
    // XXX
    public boolean addUnloadedOpenedTopComponent(String tcID);
    
    // XXX>>
    public Set<String> getTopComponentsIDs();
    public Set<String> getOpeningSetIDs();
    public Set<String> getClosingSetIDs();
    public Set<String> getOpenedTopComponentsIDs();
    // XXX<<
}
