/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2013 Oracle and/or its affiliates. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2011 Sun
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

package org.netbeans.core.network.proxy;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.netbeans.core.ProxySettings;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jiri Rechtacek
 */
@ServiceProvider(service = ProxySelector.class, position = 1000)
public final class NbProxySelector extends ProxySelector {
    
    private final ProxySelector original;
    private static final Logger LOG = Logger.getLogger (NbProxySelector.class.getName ());
    private static Object useSystemProxies;
    private static final String DEFAULT_PROXY_SELECTOR_CLASS_NAME = "sun.net.spi.DefaultProxySelector";
        
    /** Creates a new instance of NbProxySelector */
    public NbProxySelector() {
        original = ProxySelector.getDefault();
        LOG.log(Level.FINE, "java.net.useSystemProxies has been set to {0}", useSystemProxies());
        if (original.getClass().getName().equals(DEFAULT_PROXY_SELECTOR_CLASS_NAME) || original == null) {
            NetworkProxyReloader.reloadNetworkProxy();
        }
        ProxySettings.addPreferenceChangeListener(new ProxySettingsListener());
        copySettingsToSystem();
    } 
    
    @Override
    public List<Proxy> select(URI uri) {
        List<Proxy> res = new ArrayList<Proxy> ();
        int proxyType = ProxySettings.getProxyType ();
        switch (proxyType) {
            case ProxySettings.DIRECT_CONNECTION:
                res = Collections.singletonList (Proxy.NO_PROXY);
                break;
            case ProxySettings.AUTO_DETECT_PROXY:
                if (!useSystemProxies ()) {                    
                    String protocol = uri.getScheme ();
                    assert protocol != null : "Invalid scheme of uri " + uri + ". Scheme cannot be null!";
                    if (dontUseProxy (ProxySettings.getSystemNonProxyHosts(), uri.getHost ())) {
                        res.add (Proxy.NO_PROXY);
                        break;
                    }
                    if (protocol.toLowerCase (Locale.US).startsWith("http")) {
                        String ports = ProxySettings.getSystemHttpPort();
                        if (ports != null && ports.length () > 0 && ProxySettings.getSystemHttpHost().length () > 0) {
                            int porti = Integer.parseInt(ports);
                            Proxy p = new Proxy (Proxy.Type.HTTP,  new InetSocketAddress (ProxySettings.getSystemHttpHost(), porti));
                            res.add (p);
                        }
                    } else { // supposed SOCKS
                        String ports = ProxySettings.getSystemSocksPort();
                        String hosts = ProxySettings.getSystemSocksHost();
                        if (ports != null && ports.length () > 0 && hosts.length () > 0) {
                            int porti = Integer.parseInt(ports);
                            Proxy p = new Proxy (Proxy.Type.SOCKS,  new InetSocketAddress (hosts, porti));
                            res.add (p);
                        }
                    }
                    if (original != null) {
                        res.addAll (original.select (uri));
                    }
                }
                break;
            case ProxySettings.MANUAL_SET_PROXY:
                String protocol = uri.getScheme ();
                assert protocol != null : "Invalid scheme of uri " + uri + ". Scheme cannot be null!";

                // handling nonProxyHosts first
                if (dontUseProxy (ProxySettings.getNonProxyHosts (), uri.getHost ())) {
                    res.add (Proxy.NO_PROXY);
                    break;
                }
                if (protocol.toLowerCase (Locale.US).startsWith("http")) {
                    String hosts = ProxySettings.getHttpHost ();
                    String ports = ProxySettings.getHttpPort ();
                    if (ports != null && ports.length () > 0 && hosts.length () > 0) {
                        int porti = Integer.parseInt(ports);
                        Proxy p = new Proxy (Proxy.Type.HTTP,  new InetSocketAddress (hosts, porti));
                        res.add (p);
                    } else {
                        LOG.log(Level.FINE, "Incomplete HTTP Proxy [{0}/{1}] found in ProxySelector[Type: {2}] for uri {3}. ", new Object[]{hosts, ports, ProxySettings.getProxyType (), uri});
                        if (original != null) {
                            LOG.log(Level.FINEST, "Fallback to the default ProxySelector which returns {0}", original.select (uri));
                            res.addAll (original.select (uri));
                        }
                    }
                } else { // supposed SOCKS
                    String ports = ProxySettings.getSocksPort ();
                    String hosts = ProxySettings.getSocksHost ();
                    if (ports != null && ports.length () > 0 && hosts.length () > 0) {
                        int porti = Integer.parseInt(ports);
                        Proxy p = new Proxy (Proxy.Type.SOCKS,  new InetSocketAddress (hosts, porti));
                        res.add (p);
                    } else {
                        LOG.log(Level.FINE, "Incomplete SOCKS Server [{0}/{1}] found in ProxySelector[Type: {2}] for uri {3}. ", new Object[]{hosts, ports, ProxySettings.getProxyType (), uri});
                        if (original != null) {
                            LOG.log(Level.FINEST, "Fallback to the default ProxySelector which returns {0}", original.select (uri));
                            res.addAll (original.select (uri));
                        }
                    }
                }
                res.add (Proxy.NO_PROXY);
                break;
            case ProxySettings.AUTO_DETECT_PAC:
                if (!useSystemProxies ()) {
                    // handling nonProxyHosts first
                    if (dontUseProxy (ProxySettings.getNonProxyHosts (), uri.getHost ())) {
                        res.add (Proxy.NO_PROXY);
                        break;
                    }
                    ProxyAutoConfig pac = ProxyAutoConfig.get(getPacFile());
                    assert pac != null : "Instance of ProxyAutoConfig found for " + getPacFile();
                    if (pac == null) {
                        LOG.log(Level.FINEST, "No instance of ProxyAutoConfig({0}) for URI {1}", new Object[]{getPacFile(), uri});
                        res.add(Proxy.NO_PROXY);
                    }
                    if (pac.getPacURI().getHost() == null) {
                        LOG.log(Level.FINEST, "Identifying proxy for URI {0}---{1}, PAC LOCAL URI: {2}", //NOI18N
                                new Object[] { uri.toString(), uri.getHost(), pac.getPacURI().toString() });
                        res.addAll(pac.findProxyForURL(uri));
                    } else if (pac.getPacURI().getHost().equals(uri.getHost())) {
                        // don't proxy PAC files
                        res.add(Proxy.NO_PROXY);
                    } else {
                        LOG.log(Level.FINEST, "Identifying proxy for URI {0}---{1}, PAC URI: {2}---{3}", //NOI18N
                                new Object[] { uri.toString(), uri.getHost(), pac.getPacURI().toString(), pac.getPacURI().getHost() });
                        res.addAll(pac.findProxyForURL(uri));
                    }                    
                }
                
                if (original != null) {
                    res.addAll (original.select (uri));
                }
                
                res.add (Proxy.NO_PROXY);
                break;
            case ProxySettings.MANUAL_SET_PAC:
                // handling nonProxyHosts first
                if (dontUseProxy (ProxySettings.getNonProxyHosts (), uri.getHost ())) {
                    res.add (Proxy.NO_PROXY);
                    break;
                }
                ProxyAutoConfig pac = ProxyAutoConfig.get(getPacFile());
                assert pac != null : "Instance of ProxyAutoConfig found for " + getPacFile();
                if (pac == null) {
                    LOG.log(Level.FINEST, "No instance of ProxyAutoConfig({0}) for URI {1}", new Object[]{getPacFile(), uri});
                    res.add(Proxy.NO_PROXY);
                }
                if (pac.getPacURI().getHost() == null) {
                        LOG.log(Level.FINEST, "Identifying proxy for URI {0}---{1}, PAC LOCAL URI: {2}", //NOI18N
                                new Object[] { uri.toString(), uri.getHost(), pac.getPacURI().toString() });
                        res.addAll(pac.findProxyForURL(uri));
                } else if (pac.getPacURI().getHost().equals(uri.getHost())) {
                    // don't proxy PAC files
                    res.add(Proxy.NO_PROXY);
                } else {
                    LOG.log(Level.FINEST, "Identifying proxy for URI {0}---{1}, PAC URI: {2}---{3}", //NOI18N
                            new Object[] { uri.toString(), uri.getHost(), pac.getPacURI().toString(), pac.getPacURI().getHost() });
                    res.addAll(pac.findProxyForURL(uri)); // NOI18N
                }
                res.add (Proxy.NO_PROXY);
                break;
            default:
                assert false : "Invalid proxy type: " + proxyType;
        }
        LOG.log(Level.FINEST, "NbProxySelector[Type: {0}, Use HTTP for all protocols: {1}] returns {2} for URI {3}", 
                new Object[]{ProxySettings.getProxyType (), ProxySettings.useProxyAllProtocols (), res, uri});
        return res;
    }
    
    @Override
    public void connectFailed (URI arg0, SocketAddress arg1, IOException arg2) {
        LOG.log  (Level.INFO, "connectionFailed(" + arg0 + ", " + arg1 +")", arg2);
    }

    // several modules listenes on these properties and propagates it futher
    private class ProxySettingsListener implements PreferenceChangeListener {
        @Override
        public void preferenceChange(PreferenceChangeEvent evt) {
            if (evt.getKey ().startsWith ("proxy") || evt.getKey ().startsWith ("useProxy")) {
                copySettingsToSystem ();
            }
        }
    }
    
    private void copySettingsToSystem () {
        String host = null, port = null, nonProxyHosts = null;
        String socksHost = null, socksPort = null;
        String httpsHost = null, httpsPort = null;
        int proxyType = ProxySettings.getProxyType ();
        switch (proxyType) {
            case ProxySettings.DIRECT_CONNECTION:
                host = null;
                port = null;
                httpsHost = null;
                httpsPort = null;
                nonProxyHosts = null;
                socksHost = null;
                socksPort = null;
                break;
            case ProxySettings.AUTO_DETECT_PROXY:
                host = ProxySettings.getSystemHttpHost();
                port = ProxySettings.getSystemHttpPort();
                httpsHost = ProxySettings.getSystemHttpsHost();
                httpsPort = ProxySettings.getSystemHttpsPort();
                socksHost = ProxySettings.getSystemSocksHost();
                socksPort = ProxySettings.getSystemSocksPort();
                nonProxyHosts = ProxySettings.getSystemNonProxyHosts();
                break;
            case ProxySettings.MANUAL_SET_PROXY:
                host = ProxySettings.getHttpHost ();
                port = ProxySettings.getHttpPort ();
                httpsHost = ProxySettings.getHttpsHost ();
                httpsPort = ProxySettings.getHttpsPort ();
                nonProxyHosts = ProxySettings.getNonProxyHosts ();
                socksHost = ProxySettings.getSocksHost ();
                socksPort = ProxySettings.getSocksPort ();
                break;
            case ProxySettings.AUTO_DETECT_PAC:
                host = null;
                port = null;
                httpsHost = null;
                httpsPort = null;
                nonProxyHosts = null;
                socksHost = null;
                socksPort = null;
                break;
            case ProxySettings.MANUAL_SET_PAC:
                host = null;
                port = null;
                httpsHost = null;
                httpsPort = null;
                nonProxyHosts = ProxySettings.getNonProxyHosts();
                socksHost = null;
                socksPort = null;
                break;
            default:
                assert false : "Invalid proxy type: " + proxyType;
        }
        setOrClearProperty ("http.proxyHost", host, false);
        setOrClearProperty ("http.proxyPort", port, true);
        setOrClearProperty ("http.nonProxyHosts", nonProxyHosts, false);
        setOrClearProperty ("https.proxyHost", httpsHost, false);
        setOrClearProperty ("https.proxyPort", httpsPort, true);
        setOrClearProperty ("https.nonProxyHosts", nonProxyHosts, false);
        setOrClearProperty ("socksProxyHost", socksHost, false);
        setOrClearProperty ("socksProxyPort", socksPort, true);
        LOG.log (Level.FINE, "Set System''s http.proxyHost/Port/NonProxyHost to {0}/{1}/{2}", new Object[]{host, port, nonProxyHosts});
        LOG.log (Level.FINE, "Set System''s https.proxyHost/Port to {0}/{1}", new Object[]{httpsHost, httpsPort});
        LOG.log (Level.FINE, "Set System''s socksProxyHost/Port to {0}/{1}", new Object[]{socksHost, socksPort});
    }
    
    private void setOrClearProperty (String key, String value, boolean isInteger) {
        assert key != null;
        if (value == null || value.length () == 0) {
            System.clearProperty (key);
        } else {
            if (isInteger) {
                try {
                    Integer.parseInt (value);
                } catch (NumberFormatException nfe) {
                    LOG.log (Level.INFO, nfe.getMessage(), nfe);
                }
            }
            System.setProperty (key, value);
        }
    }

    // package-private for unit-testing
    static boolean dontUseProxy (String nonProxyHosts, String host) {
        if (host == null) {
            return false;
        }
        
        // try IP adress first
        if (dontUseIp (nonProxyHosts, host)) {
            return true;
        } else {
            return dontUseHostName (nonProxyHosts, host);
        }

    }
    
    private static boolean dontUseHostName (String nonProxyHosts, String host) {
        if (host == null) {
            return false;
        }
        
        boolean dontUseProxy = false;
        StringTokenizer st = new StringTokenizer (nonProxyHosts, "|", false);
        while (st.hasMoreTokens () && !dontUseProxy) {
            String token = st.nextToken ().trim();
            int star = token.indexOf ("*");
            if (star == -1) {
                dontUseProxy = token.equals (host);
                if (dontUseProxy) {
                    LOG.log(Level.FINEST, "NbProxySelector[Type: {0}]. Host {1} found in nonProxyHosts: {2}", new Object[]{ProxySettings.getProxyType (), host, nonProxyHosts});
                }
            } else {
                String start = token.substring (0, star - 1 < 0 ? 0 : star - 1);
                String end = token.substring (star + 1 > token.length () ? token.length () : star + 1);

                //Compare left of * if and only if * is not first character in token
                boolean compareStart = star > 0; // not first character
                //Compare right of * if and only if * is not the last character in token
                boolean compareEnd = star < (token.length() - 1); // not last character
                dontUseProxy = (compareStart && host.startsWith(start)) || (compareEnd && host.endsWith(end));

                if (dontUseProxy) {
                    LOG.log(Level.FINEST, "NbProxySelector[Type: {0}]. Host {1} found in nonProxyHosts: {2}", new Object[]{ProxySettings.getProxyType (), host, nonProxyHosts});
                }
            }
        }
        return dontUseProxy;
    }
    
    private static boolean dontUseIp (String nonProxyHosts, String host) {
        if (host == null) {
            return false;
        }
        
        String ip = null;
        try {
            ip = InetAddress.getByName (host).getHostAddress ();
        } catch (UnknownHostException ex) {
            LOG.log (Level.FINE, ex.getLocalizedMessage (), ex);
        }
        
        if (ip == null) {
            return false;
        }

        boolean dontUseProxy = false;
        StringTokenizer st = new StringTokenizer (nonProxyHosts, "|", false);
        while (st.hasMoreTokens () && !dontUseProxy) {
            String nonProxyHost = st.nextToken ().trim();
            int star = nonProxyHost.indexOf ("*");
            if (star == -1) {
                dontUseProxy = nonProxyHost.equals (ip);
                if (dontUseProxy) {
                    LOG.log(Level.FINEST, "NbProxySelector[Type: {0}]. Host''s IP {1} found in nonProxyHosts: {2}", new Object[]{ProxySettings.getProxyType (), ip, nonProxyHosts});
                }
            } else {
                // match with given dotted-quad IP
                try {
                    dontUseProxy = Pattern.matches (nonProxyHost, ip);
                    if (dontUseProxy) {
                        LOG.log(Level.FINEST, "NbProxySelector[Type: {0}]. Host''s IP{1} found in nonProxyHosts: {2}", new Object[]{ProxySettings.getProxyType (), ip, nonProxyHosts});
                    }
                } catch (PatternSyntaxException pse) {
                    // may ignore it here
                }
            }
        }
        return dontUseProxy;
    }
    
    // NetProperties is JDK vendor specific, access only by reflection
    static boolean useSystemProxies () {
        if (useSystemProxies == null) {
            try {
                Class<?> clazz = Class.forName ("sun.net.NetProperties");
                Method getBoolean = clazz.getMethod ("getBoolean", String.class);
                useSystemProxies = getBoolean.invoke (null, "java.net.useSystemProxies");
            } catch (Exception x) {
                LOG.log (Level.FINEST, "Cannot get value of java.net.useSystemProxies bacause " + x.getMessage(), x);
            }
        }
        return useSystemProxies != null && "true".equalsIgnoreCase (useSystemProxies.toString ());
    }
    
    static boolean usePAC() {
        String pacFile = ProxySettings.getSystemPac();
        return pacFile != null;
    }
    
    private static String getPacFile() {
        return ProxySettings.getSystemPac();
    }
}
