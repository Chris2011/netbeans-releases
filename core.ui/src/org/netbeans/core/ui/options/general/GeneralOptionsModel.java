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
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
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

package org.netbeans.core.ui.options.general;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import org.netbeans.core.ProxySettings;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;

class GeneralOptionsModel {
    
    private static final Logger LOGGER = Logger.getLogger(GeneralOptionsModel.class.getName());
    
    private static final String TESTING_URL = "http://www.netbeans.org"; //NOI18N
    
    private static Preferences getProxyPreferences () {
        return NbPreferences.root ().node ("org/netbeans/core");
    }
    
    boolean getUsageStatistics () {
        String key = System.getProperty("nb.show.statistics.ui");
        if (key != null) {
            return getProxyPreferences ().getBoolean(key, Boolean.FALSE);
        } else {
            return false;
        }
    }

    void setUsageStatistics (boolean use) {
        String key = System.getProperty("nb.show.statistics.ui");
        if ((key != null) && (use != getUsageStatistics())) {
            getProxyPreferences  ().putBoolean(key, use);
        }
    }
    
    int getProxyType () {
        return getProxyPreferences ().getInt (ProxySettings.PROXY_TYPE, ProxySettings.AUTO_DETECT_PROXY);
    }
    
    void setProxyType (int proxyType) {
        if (proxyType != getProxyType ()) {
            if (ProxySettings.AUTO_DETECT_PROXY == proxyType) {
                getProxyPreferences  ().putInt (ProxySettings.PROXY_TYPE, usePAC() ? ProxySettings.AUTO_DETECT_PAC : ProxySettings.AUTO_DETECT_PROXY);
            } else {
                getProxyPreferences  ().putInt (ProxySettings.PROXY_TYPE, proxyType);
            }
        }
    }
    
    String getHttpProxyHost () {
        return ProxySettings.getHttpHost ();
    }
    
    void setHttpProxyHost (String proxyHost) {
        if (!proxyHost.equals(getHttpProxyHost ())) {
            getProxyPreferences ().put (ProxySettings.PROXY_HTTP_HOST, proxyHost);
        }
    }
    
    String getHttpProxyPort () {
        return ProxySettings.getHttpPort ();
    }
    
    void setHttpProxyPort (String proxyPort) {
        if (! proxyPort.equals (getHttpProxyPort())) {
            getProxyPreferences().put(ProxySettings.PROXY_HTTP_PORT, validatePort (proxyPort) ? proxyPort : "");
        }
    }
    
    String getHttpsProxyHost () {
        return ProxySettings.getHttpsHost ();
    }
    
    void setHttpsProxyHost (String proxyHost) {
        if (!proxyHost.equals(getHttpsProxyHost ())) {
            getProxyPreferences ().put (ProxySettings.PROXY_HTTPS_HOST, proxyHost);
        }
    }
    
    String getHttpsProxyPort () {
        return ProxySettings.getHttpsPort ();
    }
    
    void setHttpsProxyPort (String proxyPort) {
        if (! proxyPort.equals (getHttpsProxyPort())) {
            getProxyPreferences().put(ProxySettings.PROXY_HTTPS_PORT, validatePort (proxyPort) ? proxyPort : "");
        }
    }
    
    String getSocksHost () {
        return ProxySettings.getSocksHost ();
    }
    
    void setSocksHost (String socksHost) {
        if (! socksHost.equals (getSocksHost())) {
            getProxyPreferences ().put (ProxySettings.PROXY_SOCKS_HOST, socksHost);
        }
    }
    
    String getSocksPort () {
        return ProxySettings.getSocksPort ();
    }
    
    void setSocksPort (String socksPort) {
        if (! socksPort.equals (getSocksPort())) {
            getProxyPreferences ().put (ProxySettings.PROXY_SOCKS_PORT, validatePort (socksPort) ? socksPort : "");
        }
    }
    
    String getOriginalHttpsHost () {
        return getProxyPreferences ().get (ProxySettings.PROXY_HTTPS_HOST, "");
    }
    
    String getOriginalHttpsPort () {
        return getProxyPreferences ().get (ProxySettings.PROXY_HTTPS_PORT, "");
    }
    
    String getOriginalSocksHost () {
        return getProxyPreferences ().get (ProxySettings.PROXY_SOCKS_HOST, "");
    }
    
    String getOriginalSocksPort () {
        return getProxyPreferences ().get (ProxySettings.PROXY_SOCKS_PORT, "");
    }
    
    String getNonProxyHosts () {
        return code2view (ProxySettings.getNonProxyHosts ());
    }
    
    void setNonProxyHosts (String nonProxy) {
        if (!nonProxy.equals(getNonProxyHosts())) {
            getProxyPreferences ().put (ProxySettings.NOT_PROXY_HOSTS, view2code (nonProxy));
        }
    }
    
    boolean useProxyAuthentication () {
        return ProxySettings.useAuthentication ();
    }
    
    void setUseProxyAuthentication (boolean use) {
        if (use != useProxyAuthentication()) {
            getProxyPreferences ().putBoolean (ProxySettings.USE_PROXY_AUTHENTICATION, use);
        }
    }
    
    boolean useProxyAllProtocols () {
        return ProxySettings.useProxyAllProtocols ();
    }
    
    void setUseProxyAllProtocols (boolean use) {
        if (use != useProxyAllProtocols ()) {
            getProxyPreferences ().putBoolean (ProxySettings.USE_PROXY_ALL_PROTOCOLS, use);
        }
    }
    
    String getProxyAuthenticationUsername () {
        return ProxySettings.getAuthenticationUsername ();
    }

    void setAuthenticationUsername (String username) {
        getProxyPreferences ().put (ProxySettings.PROXY_AUTHENTICATION_USERNAME, username);
    }
    
    char [] getProxyAuthenticationPassword () {
        return ProxySettings.getAuthenticationPassword ();
    }
    
    void setAuthenticationPassword(char [] password) {
        ProxySettings.setAuthenticationPassword(password);
    }
    
    static boolean usePAC() {
        String pacUrl = getProxyPreferences().get(ProxySettings.SYSTEM_PAC, ""); // NOI18N
        return pacUrl != null && pacUrl.length() > 0;
    }
    
    static boolean testConnection(int proxyType, String proxyHost, String proxyPortString) {
        boolean result = false;
        URL testingUrl = null;
        
        try {
            testingUrl = new URL(TESTING_URL);
        } catch (MalformedURLException ex) {
            LOGGER.log(Level.SEVERE, "Cannot create url from string.", ex);
        }
        
        switch(proxyType) {
            case ProxySettings.DIRECT_CONNECTION:
                result = testHttpConnection(testingUrl, Proxy.NO_PROXY);
                break;
            case ProxySettings.AUTO_DETECT_PROXY:
            case ProxySettings.AUTO_DETECT_PAC:
                ProxySelector ps = ProxySelector.getDefault();            
                try {
                    result = testHttpConnection(testingUrl, ps.select(testingUrl.toURI()).get(0));
                } catch (URISyntaxException ex) {
                    LOGGER.log(Level.SEVERE, "Cannot connect via http protocol.", ex);                
                }
                break;
            case ProxySettings.MANUAL_SET_PROXY:
            case ProxySettings.MANUAL_SET_PAC:
                int proxyPort = Integer.valueOf(proxyPortString);
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
                result = testHttpConnection(testingUrl, proxy);
                break;
        }
        
        return result;
    }
    
    private static boolean testHttpConnection(URL url, Proxy proxy) {
        boolean result = false;
        
        try {
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection(proxy);

            httpConnection.connect();

            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                result = true;
            }

            httpConnection.disconnect();
        } catch (IOException ex) {
            LOGGER.log(Level.INFO, "Cannot connect via http protocol.", ex);
        }
        
        return result;
    }
    
    // private helper methods ..................................................

    private static boolean validatePort (String port) {
        if (port.trim ().length () == 0) return true;
        
        boolean ok = false;
        try {
            Integer.parseInt (port);
            ok = true;
        } catch (NumberFormatException nfe) {
            assert false : nfe;
        }
        return ok;
    }
    
    private static String code2view (String code) {
        return code == null ? code : code.replace ("|", ", ");
    }
    
    private static String view2code (String view) {
        return view == null ? view : view.replace (", ", "|");
    }
}
