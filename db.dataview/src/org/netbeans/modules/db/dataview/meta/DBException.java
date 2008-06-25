/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.db.dataview.meta;

import java.sql.SQLException;

/**
 * Super class for all application exceptions. All SQL specific Exceptions will Extends
 * this Exception This Exception class will contain More methods and functionalities in
 * future.
 * 
 * @author Sudhi Seshachala
 * @author Ahimanikya Satapathy
 */
public final class DBException extends Exception {

    /**
     * Should not be called in program, just for loading purpose
     */
    public DBException() {
    }

    /**
     * Creates a new instance of DataViewException
     * 
     * @param message Message for this exception
     */
    public DBException(String message) {
        super(message);
    }

    /**
     * @param message message identifying this exception.
     * @param cause cause identifying this exception.
     */
    public DBException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor for DataViewException
     * 
     * @param cause of this exception.
     */
    public DBException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        StringBuffer buf = new StringBuffer();

        Throwable t = this;
        //we are getting only the first exception which is wrapped,
        //should we get messages from all the exceptions in the chain?
        if (t.getCause() != null) {
            t = t.getCause();
        }

        if (t != this) {
            if (t instanceof SQLException) {
                SQLException e = (SQLException) t;
                buf.append("Error code ").append(e.getErrorCode());
                buf.append(", SQL state ").append(e.getSQLState());
            }
            buf.append(super.getMessage()).append(" -- ").append(t.toString());
        } else {
            buf.append(super.getMessage());
        }
        return buf.toString();
    }

    public static String getMessage(Throwable t) {
        StringBuffer buf = new StringBuffer();
        if (t instanceof SQLException) {
            SQLException e = (SQLException) t;
            buf.append("Error code ").append(e.getErrorCode());
            buf.append(", SQL state ").append(e.getSQLState());
        }
        buf.append(" -- ").append(t.getMessage());
        return buf.toString();
    }
}
