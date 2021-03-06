/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */

package org.netbeans.modules.maven.execute;

import java.io.Reader;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

/**
 * workarounds execution API which at some point in time calls select() on the IO.
 * @author mkleint
 */
public class ProxyNonSelectableInputOutput implements InputOutput {
    private final InputOutput delegate;
    
    public ProxyNonSelectableInputOutput(InputOutput delegate) {
        this.delegate = delegate;
    }

    @Override
    public OutputWriter getOut() {
        return delegate.getOut();
    }

    @Override
    public Reader getIn() {
        return delegate.getIn();
    }

    @Override
    public OutputWriter getErr() {
        return delegate.getErr();
    }

    @Override
    public void closeInputOutput() {
        delegate.closeInputOutput();
    }

    @Override
    public boolean isClosed() {
        return delegate.isClosed();
    }

    @Override
    public void setOutputVisible(boolean value) {
        delegate.setOutputVisible(value);
    }

    @Override
    public void setErrVisible(boolean value) {
        delegate.setErrVisible(value);
    }

    @Override
    public void setInputVisible(boolean value) {
        delegate.setInputVisible(value);
    }

    @Override
    public void select() {
        //do not delegate!
    }

    @Override
    public boolean isErrSeparated() {
        return delegate.isErrSeparated();
    }

    @Override
    public void setErrSeparated(boolean value) {
        delegate.setErrSeparated(value);
    }

    @Override
    public boolean isFocusTaken() {
        return delegate.isFocusTaken();
    }

    @Override
    public void setFocusTaken(boolean value) {
        delegate.setFocusTaken(value);
    }

    @Override
    public Reader flushReader() {
        return delegate.flushReader();
    }
    
}
