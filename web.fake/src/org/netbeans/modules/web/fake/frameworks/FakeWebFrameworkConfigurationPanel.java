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

package org.netbeans.modules.web.fake.frameworks;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import org.netbeans.modules.web.api.webmodule.WebFrameworks;
import org.netbeans.modules.web.spi.webmodule.WebFrameworkProvider;
import org.netbeans.modules.web.spi.webmodule.WebModuleExtender;
import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

/**
 * Provider for fake web module extenders. Able to download and enable the proper module
 * as well as delegate to the proper configuration panel.
 * @author Tomas Mysik
 */
public class FakeWebFrameworkConfigurationPanel extends JPanel {
    private static final long serialVersionUID = 2793723169621508L;

    private final FakeWebModuleExtender fakeExtender;
    private final String name;
    private final String codeNameBase;
    private JComponent panel;

    FakeWebFrameworkConfigurationPanel(FakeWebModuleExtender fakeExtender, final String name, final String codeNameBase) {
        assert fakeExtender != null;
        assert name != null;
        assert codeNameBase != null;

        this.fakeExtender = fakeExtender;
        this.name = name;
        this.codeNameBase = codeNameBase;

        initComponents();

        infoLabel.setText(NbBundle.getMessage(FakeWebFrameworkConfigurationPanel.class, "LBL_Info", name));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {


        infoLabel = new JLabel();
        downloadButton = new JButton();
        Mnemonics.setLocalizedText(infoLabel, "dummy");
        Mnemonics.setLocalizedText(downloadButton, NbBundle.getMessage(FakeWebFrameworkConfigurationPanel.class, "FakeWebFrameworkConfigurationPanel.downloadButton.text"));
        downloadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                downloadButtonActionPerformed(evt);
            }
        });

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(GroupLayout.LEADING)
                    .add(infoLabel)
                    .add(downloadButton))
                .addContainerGap(181, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(infoLabel)
                .addPreferredGap(LayoutStyle.RELATED)
                .add(downloadButton)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void downloadButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_downloadButtonActionPerformed
        // XXX download, install & enable the real module
        // real web framework provider
        WebFrameworkProvider webFrameworkProvider = getWebFrameworkProvider();
        assert webFrameworkProvider != null : String.format("Web framework provider must be found for %s (%s)", name, codeNameBase);
        assert !(webFrameworkProvider instanceof FakeWebFrameworkProvider.FakeWebFrameworkProviderImpl) : "Fake web framework provider found";
        fakeExtender.setWebFrameworkProvider(webFrameworkProvider);

        // real web framework configuration panel
        WebModuleExtender realExtender = fakeExtender.getDelegate();
        assert realExtender != null : String.format("Real web module extender must be found for %s (%s)", name, codeNameBase);
        panel = realExtender.getComponent();
        removeAll();
        if (panel != null) {
            setLayout(new BorderLayout());
            add(panel, BorderLayout.NORTH);
        }
        revalidate();
        repaint();
        fakeExtender.stateChanged(null);
    }//GEN-LAST:event_downloadButtonActionPerformed

    private WebFrameworkProvider getWebFrameworkProvider() {
        for (WebFrameworkProvider provider : WebFrameworks.getFrameworks()) {
            // XXX how to find out the correct WFP?
//            if (name.equals(provider.getName())) {
            if ("Struts 1.2.9".equals(provider.getName())) { // NOI18N
                return provider;
            }
        }
        return null;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton downloadButton;
    private JLabel infoLabel;
    // End of variables declaration//GEN-END:variables

}
