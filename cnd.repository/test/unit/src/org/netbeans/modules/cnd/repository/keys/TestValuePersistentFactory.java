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
package org.netbeans.modules.cnd.repository.keys;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;
import org.netbeans.modules.cnd.repository.spi.Persistent;
import org.netbeans.modules.cnd.repository.spi.PersistentFactory;
import org.netbeans.modules.cnd.repository.spi.RepositoryDataInput;
import org.netbeans.modules.cnd.repository.spi.RepositoryDataOutput;

/**
 *
 * @author akrasny
 */
public final class TestValuePersistentFactory implements PersistentFactory {

    private static final TestValuePersistentFactory instance = new TestValuePersistentFactory();
    private final CopyOnWriteArraySet<PersistentFactoryListener> listeners = new CopyOnWriteArraySet<PersistentFactoryListener>();

    private TestValuePersistentFactory() {
    }

    public void addPersistentFactoryListener(PersistentFactoryListener listener) {
        listeners.add(listener);
    }

    public void removePersistentFactoryListener(PersistentFactoryListener listener) {
        listeners.remove(listener);
    }

    public static TestValuePersistentFactory getInstance() {
        return instance;
    }

    @Override
    public void write(RepositoryDataOutput out, Persistent obj) throws IOException {
        if (obj == null) {
            out.writeUTF("\0");  // NOI18N
            for (PersistentFactoryListener l : listeners) {
                l.onRemoveHook();
            }
        } else {
            assert obj instanceof TestValue;
            out.writeUTF(((TestValue) obj).getValue());
            for (PersistentFactoryListener l : listeners) {
                l.onWriteHook();
            }
        }
    }

    @Override
    public Persistent read(RepositoryDataInput in) throws IOException {
        String value = in.readUTF();
        if ("\0".equals(value)) {
            return null;
        }
        TestValue result = new TestValue(value);
        for (PersistentFactoryListener l : listeners) {
            l.onReadHook();
        }
        return result;
    }
}