/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.css.editor.module;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import org.netbeans.modules.css.editor.module.spi.Browser;
import org.netbeans.modules.css.editor.module.spi.CssModule;
import org.netbeans.modules.css.editor.module.spi.Property;
import org.netbeans.modules.css.editor.module.spi.PropertySupportResolver;
import org.openide.util.NbBundle;

/**
 *
 * @author mfukala@netbeans.org
 */
public class BrowserSpecificDefinitionParser extends PropertySupportResolver {

    private String resourcePath;
    private Browser browser;
    private CssModule module;
    private final Set<String> supportedPropertiesNames = new HashSet<String>();
    private final Collection<Property> vendorSpecificProperties = new HashSet<Property>();

    public BrowserSpecificDefinitionParser(String resourcePath, Browser browser, CssModule module) {
        this.resourcePath = resourcePath;
        this.browser = browser;
        this.module = module;
        load();
    }

    private void load() {
        ResourceBundle bundle = NbBundle.getBundle(resourcePath);

        Enumeration<String> keys = bundle.getKeys();
        while (keys.hasMoreElements()) {
            String name = keys.nextElement();
            String value = bundle.getString(name).trim();

            if (value.isEmpty()) {
                continue; //ignore empty keys, the meaning is the same as if there was no such key in the file
            }
            char firstValueChar = value.charAt(0);

            //parse bundle key - there might be more properties separated by semicolons
            StringTokenizer nameTokenizer = new StringTokenizer(name, ";"); //NOI18N
            Collection<String> propertyNames = new ArrayList<String>();
            while (nameTokenizer.hasMoreTokens()) {
                String parsed_name = nameTokenizer.nextToken().trim();
                propertyNames.add(parsed_name);
            }

            for (String propertyName : propertyNames) {

                if (propertyName.startsWith(browser.getVendorSpecificPropertyPrefix())) {
                    //vendor specific property
                    vendorSpecificProperties.add(new Property(propertyName, value, module));
                    supportedPropertiesNames.add(propertyName);

                } else {
                    //standard property
                    switch (firstValueChar) {
                        case '!':
                            //experimental property only
                            String vendorSpecificPropertyName = createVendorSpecificPropertyName(browser.getVendorSpecificPropertyPrefix(), propertyName);
                            vendorSpecificProperties.add(new ProxyProperty(vendorSpecificPropertyName, propertyName));
                            supportedPropertiesNames.add(vendorSpecificPropertyName);
                            break;
                        case '+':
                            //standard property support only
                            supportedPropertiesNames.add(propertyName);
                            break;
                        case '*':
                            //standard + experimental property                            
                            vendorSpecificPropertyName = createVendorSpecificPropertyName(browser.getVendorSpecificPropertyPrefix(), propertyName);
                            vendorSpecificProperties.add(new ProxyProperty(vendorSpecificPropertyName, propertyName));
                            supportedPropertiesNames.add(propertyName);
                            supportedPropertiesNames.add(vendorSpecificPropertyName);
                            break;
                        case '-':
                            //discontinued support
                            //just ignore for now == not supported, later we may utilize the info somehow
                            break;

                        default:
                            //even standard property can be vendor specific (zoom for webkit)
                            vendorSpecificProperties.add(new Property(propertyName, value, module));
                            supportedPropertiesNames.add(propertyName);

                    }

                }

            }

        }

    }

    private String createVendorSpecificPropertyName(String prefix, String standardPropertyName) {
        return new StringBuilder().append(prefix).append(standardPropertyName).toString();
    }

    @Override
    public boolean isPropertySupported(String propertyName) {
        return supportedPropertiesNames.contains(propertyName);
    }

    public Collection<Property> getVendorSpecificProperties() {
        return vendorSpecificProperties;
    }

    private class ProxyProperty extends Property {

        private String delegateToPropertyName;

        public ProxyProperty(String name, String delegateToPropertyName) {
            super(name, null, null);
            this.delegateToPropertyName = delegateToPropertyName;
        }

        @Override
        public String getValueGrammar() {
            Property p = CssModuleSupport.getProperty(delegateToPropertyName);
            if (p == null) {
                Logger.getAnonymousLogger().warning(String.format("Cannot fine property %s referred in %s", delegateToPropertyName, resourcePath)); //NOI18N
                return ""; //return empty grammar definition
            }
            return p.getValueGrammar();
        }
    }
}
