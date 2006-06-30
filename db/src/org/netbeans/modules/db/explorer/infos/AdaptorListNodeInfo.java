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

package org.netbeans.modules.db.explorer.infos;

import java.util.Vector;
import org.netbeans.api.db.explorer.DatabaseException;

public class AdaptorListNodeInfo extends DatabaseNodeInfo
{
    static final long serialVersionUID =1895162778653251095L;
    protected void initChildren(Vector children)
    throws DatabaseException
    {
        /*		Vector cons = RootNode.getOption().getAvailableDrivers();
        		if (cons != null) {
        			try {
        				Enumeration cons_e = cons.elements();
        				while (cons_e.hasMoreElements()) {
        					DatabaseDriver drv = (DatabaseDriver)cons_e.nextElement();
        					DriverNodeInfo chinfo = (DriverNodeInfo)DatabaseNodeInfo.createNodeInfo(this, DatabaseNode.DRIVER);
        					if (chinfo != null && drv != null) {
        						chinfo.setDatabaseDriver(drv);
        						children.add(chinfo);
        					} else throw new Exception("driver "+drv);
        				}
        			} catch (Exception e) {
        				e.printStackTrace();
        			}
        		}
        */	}
}
