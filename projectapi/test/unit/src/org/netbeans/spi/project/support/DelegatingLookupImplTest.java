/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development and
 * Distribution License("CDDL") (collectively, the "License"). You may not use
 * this file except in compliance with the License. You can obtain a copy of
 * the License at http://www.netbeans.org/cddl-gplv2.html or
 * nbbuild/licenses/CDDL-GPL-2-CP. See the License for the specific language
 * governing permissions and limitations under the License. When distributing
 * the software, include this License Header Notice in each file and include
 * the License file at nbbuild/licenses/CDDL-GPL-2-CP. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided by
 * Oracle in the GPL Version 2 section of the License file that accompanied
 * this code. If applicable, add the following below the License Header, with
 * the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license." If you do not indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to its
 * licensees as provided above. However, if you add GPL Version 2 code and
 * therefore, elected the GPL Version 2 license, then the option applies only
 * if the new code is made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */

package org.netbeans.spi.project.support;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.netbeans.junit.NbTestCase;
import org.netbeans.spi.project.LookupMerger;
import org.netbeans.spi.project.LookupProvider;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;

public class DelegatingLookupImplTest extends NbTestCase {

    public DelegatingLookupImplTest(String name) {
        super(name);
    }

    public void testCreateCompositeLookup() {
        LookupMergerImpl merger = new LookupMergerImpl();
        Lookup base = Lookups.fixed(new JButton(), new JComboBox(), merger);
        LookupProviderImpl pro1 = new LookupProviderImpl();
        LookupProviderImpl pro2 = new LookupProviderImpl();
        LookupProviderImpl pro3 = new LookupProviderImpl();

        InstanceContent provInst = new InstanceContent();
        Lookup providers = new AbstractLookup(provInst);
        provInst.add(pro1);
        provInst.add(pro2);

        pro1.ic.add(new JTextField());
        pro2.ic.add(new JTextArea());

        DelegatingLookupImpl del = new DelegatingLookupImpl(base, providers, "<irrelevant>");

        assertNotNull(del.lookup(JTextArea.class));
        assertNotNull(del.lookup(JComboBox.class));

        // test merger..
        JButton butt = del.lookup(JButton.class);
        assertNotNull(butt);
        assertEquals("CORRECT", butt.getText());
        assertEquals(1, del.lookupAll(JButton.class).size());
        assertEquals(1, merger.expectedCount);

        pro3.ic.add(new JButton());
        pro3.ic.add(new JRadioButton());
        provInst.add(pro3);
        assertNotNull(del.lookup(JRadioButton.class));

        // test merger..
        assertEquals(butt, del.lookup(JButton.class));
        assertEquals(1, del.lookupAll(JButton.class).size());
        assertEquals(2, merger.expectedCount);

        pro1.ic.add(new JButton());

        // test merger..
        assertEquals(butt, del.lookup(JButton.class));
        assertEquals(1, del.lookupAll(JButton.class).size());
        assertEquals(3, merger.expectedCount);

    }

    private class LookupMergerImpl implements LookupMerger<JButton> {

        int expectedCount;

        @Override public Class<JButton> getMergeableClass() {
            return JButton.class;
        }

        @Override public JButton merge(final Lookup lookup) {
            expectedCount = lookup.lookupAll(JButton.class).size();
            lookup.lookupResult(JButton.class).addLookupListener(new LookupListener() {
                public @Override void resultChanged(LookupEvent ev) {
                    expectedCount = lookup.lookupAll(JButton.class).size();
                }
            });
            return new JButton("CORRECT");
        }

    }

    private class LookupProviderImpl implements LookupProvider {
        InstanceContent ic = new InstanceContent();
        boolean wasAlreadyCalled = false;
        @Override public Lookup createAdditionalLookup(Lookup baseContext) {
            assertNotNull(baseContext.lookup(JButton.class));
            assertNull(baseContext.lookup(JCheckBox.class));
            assertFalse(wasAlreadyCalled);
            wasAlreadyCalled = true;
            return new AbstractLookup(ic);
        }
    }

}
