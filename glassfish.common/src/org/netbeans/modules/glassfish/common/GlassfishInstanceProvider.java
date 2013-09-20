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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.glassfish.common;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import javax.swing.event.ChangeListener;
import org.glassfish.tools.ide.GlassFishStatus;
import org.glassfish.tools.ide.admin.CommandSetProperty;
import org.glassfish.tools.ide.server.config.ConfigBuilderProvider;
import org.netbeans.api.server.ServerInstance;
import org.netbeans.modules.glassfish.common.utils.ServerUtils;
import org.netbeans.modules.glassfish.common.utils.Util;
import org.netbeans.modules.glassfish.spi.CommandFactory;
import org.netbeans.modules.glassfish.spi.GlassfishModule;
import org.netbeans.modules.glassfish.spi.RegisteredDDCatalog;
import org.netbeans.spi.server.ServerInstanceImplementation;
import org.netbeans.spi.server.ServerInstanceProvider;
import org.openide.filesystems.FileObject;
import org.openide.util.*;
import org.openide.util.lookup.Lookups;

/**
 * GlassFish server instances provider.
 * <p/>
 * Handles all registered GlassFish server instances. Implemented as singleton
 * because NetBeans GUI components require singleton implementing                                                         
 * {@link ServerInstanceProvider} interface.
 * <p/>
 * @author Peter Williams, Vince Kraemer, Tomas Kraus
 */
public final class GlassfishInstanceProvider implements ServerInstanceProvider, LookupListener {

    /** Local logger. */
    private static final Logger LOGGER
            = GlassFishLogger.get(GlassfishInstanceProvider.class);

    public static final String GLASSFISH_AUTOREGISTERED_INSTANCE = "glassfish_autoregistered_instance";

    private static final String AUTOINSTANCECOPIED = "autoinstance-copied"; // NOI18N

    private volatile static GlassfishInstanceProvider glassFishProvider;

    public static final String EE6_DEPLOYER_FRAGMENT = "deployer:gfv3ee6"; // NOI18N
    public static final String EE6WC_DEPLOYER_FRAGMENT = "deployer:gfv3ee6wc"; // NOI18N
    public static final String PRELUDE_DEPLOYER_FRAGMENT = "deployer:gfv3"; // NOI18N
    static private String EE6_INSTANCES_PATH = "/GlassFishEE6/Instances"; // NOI18N
    static private String EE6WC_INSTANCES_PATH = "/GlassFishEE6WC/Instances"; // NOI18N

    static public String PRELUDE_DEFAULT_NAME = "GlassFish_v3_Prelude"; //NOI18N
    static public String EE6WC_DEFAULT_NAME = "GlassFish_Server_3.1"; // NOI18N

    // GlassFish Tooling SDK configuration should be done before any server
    // instance is created and used.
    static {
        GlassFishSettings.toolingLibraryconfig();
    }

    public static GlassfishInstanceProvider getProvider() {
        if (glassFishProvider != null) {
            return glassFishProvider;
        }
        else {
            boolean runInit = false;
            synchronized(GlassfishInstanceProvider.class) {
                if (glassFishProvider == null) {
                    runInit = true;
                    glassFishProvider = new GlassfishInstanceProvider(
                            new String[]{EE6_DEPLOYER_FRAGMENT, EE6WC_DEPLOYER_FRAGMENT},
                            new String[]{EE6_INSTANCES_PATH, EE6WC_INSTANCES_PATH},
                            null,
                            true, 
                            new String[]{"--nopassword"}, // NOI18N
                            new CommandFactory()  {

                        @Override
                        public CommandSetProperty getSetPropertyCommand(
                                String property, String value) {
                            return new CommandSetProperty(property,
                                    value, "DEFAULT={0}={1}");
                        }

                    });
                }
            }
            if (runInit) {
                glassFishProvider.init();                
            }
            return glassFishProvider;
        }
    }

    public static final Set<String> activeRegistrationSet = Collections.synchronizedSet(new HashSet<String>());
    
    private final Map<String, GlassfishInstance> instanceMap =
            Collections.synchronizedMap(new HashMap<String, GlassfishInstance>());
    private static final Set<String> activeDisplayNames = Collections.synchronizedSet(new HashSet<String>());
    private final ChangeSupport support = new ChangeSupport(this);

    final private String[] instancesDirNames;
    final private String displayName;
    final private String[] uriFragments;
    final private boolean needsJdk6;
    final private List<String> noPasswordOptions;
    final private CommandFactory cf;
    final private Lookup.Result<RegisteredDDCatalog> lookupResult = Lookups.forPath(Util.GF_LOOKUP_PATH).lookupResult(RegisteredDDCatalog.class);
    
    @SuppressWarnings("LeakingThisInConstructor")
    private GlassfishInstanceProvider(
            String[] uriFragments, 
            String[] instancesDirNames,
            String displayName, 
            boolean needsJdk6,
            String[] noPasswordOptionsArray, 
            CommandFactory cf 
            ) {
        this.instancesDirNames = instancesDirNames;
        this.displayName = displayName;
        this.uriFragments = uriFragments;
        this.needsJdk6 = needsJdk6;
        this.noPasswordOptions = new ArrayList<String>();
        if (null != noPasswordOptionsArray) {
            noPasswordOptions.addAll(Arrays.asList(noPasswordOptionsArray));
        }
        this.cf = cf;
        lookupResult.allInstances();
        
        lookupResult.addLookupListener(this); 
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        LOGGER.log(Level.FINE, "***** resultChanged fired ********  {0}", hashCode()); // NOI18N
        RegisteredDDCatalog catalog = getDDCatalog();
        if (null != catalog) {
            catalog.registerEE6RunTimeDDCatalog(this);
        }
        refreshCatalogFromFirstInstance(this, getDDCatalog());
    }

    /**
     * Check providers initialization status.
     * <p>
     * @return <code>true</code> when at least one of the providers
     *         is initialized or <code>false</code> otherwise.
     */
    public static synchronized boolean initialized() {
        return glassFishProvider != null;
    }

    private static RegisteredDDCatalog getDDCatalog() {
        return Lookups.forPath(Util.GF_LOOKUP_PATH).lookup(RegisteredDDCatalog.class);
    }

    private static void refreshCatalogFromFirstInstance(GlassfishInstanceProvider gip, RegisteredDDCatalog catalog) {
        GlassfishInstance firstInstance = gip.getFirstServerInstance();
        if (null != firstInstance) {
            catalog.refreshRunTimeDDCatalog(gip, firstInstance.getGlassfishRoot());
        }
    }

    /**
     * Get API representation of GlassFish server instance matching
     * provided internal server URI.
     * <p/>
     * @param uri Internal server URI used as key to find
     *            {@link ServerInstance}.
     * @return {@link ServerInstance} matching given URI.
     */
    public static ServerInstance getInstanceByUri(String uri) {
        return getProvider().getInstance(uri);
    }
        
    /**
     * Get {@link GlassfishInstance} matching provided internal
     * server URI.
     * <p/>
     * @param uri Internal server URI used as key to find
     *            {@link GlassfishInstance}.
     * @return {@link GlassfishInstance} matching provided internal server URI
     *         or <code>null</code> when no matching object was found.
     */
    public static GlassfishInstance getGlassFishInstanceByUri(String uri) {
        return getProvider().getGlassfishInstance(uri);
    }

    private GlassfishInstance getFirstServerInstance() {
        if (!instanceMap.isEmpty()) {
            return instanceMap.values().iterator().next();
        }
        return null;
    }

    /**
     * Retrieve {@link GlassfishInstance} matching provided
     * internal server URI.
     * <p/>
     * @param uri Internal server URI used as key to find
     *            {@link GlassfishInstance}.
     * @return {@link GlassfishInstance} matching provided internal server URI
     *         or <code>null</code> when no matching object was found.
     */
    public GlassfishInstance getGlassfishInstance(String uri) {
        synchronized(instanceMap) {
            return instanceMap.get(uri);
        }
    }

    public void addServerInstance(GlassfishInstance si) {
        synchronized(instanceMap) {
            try {
                instanceMap.put(si.getDeployerUri(), si);
                activeDisplayNames.add(si.getDisplayName());
                if (instanceMap.size() == 1) { // only need to do if this first of this type
                    RegisteredDDCatalog catalog = getDDCatalog();
                    if (null != catalog) {
                        catalog.refreshRunTimeDDCatalog(this, si.getGlassfishRoot());
                    }
                }
                GlassfishInstance.writeInstanceToFile(si);
            } catch(IOException ex) {
                LOGGER.log(Level.INFO,
                        "Could not store GlassFish server attributes", ex);
            }
        }

        support.fireChange();
    }

    public boolean removeServerInstance(GlassfishInstance si) {
        boolean result = false;
        synchronized(instanceMap) {
            if(instanceMap.remove(si.getDeployerUri()) != null) {
                result = true;
                removeInstanceFromFile(si.getDeployerUri());
                activeDisplayNames.remove(si.getDisplayName());
                // If this was the last of its type, need to remove the
                // resolver catalog contents
                if (instanceMap.isEmpty()) {
                    RegisteredDDCatalog catalog = getDDCatalog();
                    if (null != catalog) {
                        catalog.refreshRunTimeDDCatalog(this, null);
                    }
                }
            }
        }
        GlassFishStatus.remove(si);
        if(result) {
            ConfigBuilderProvider.destroyBuilder(si);
            support.fireChange();
        }

        return result;
    }
    
    public Lookup getLookupFor(ServerInstance instance) {
        synchronized (instanceMap) {
            for (GlassfishInstance gfInstance : instanceMap.values()) {
                if (gfInstance.getCommonInstance().equals(instance)) {
                    return gfInstance.getLookup();
                }
            }
            return null;
        }
    }
    
    public ServerInstanceImplementation getInternalInstance(String uri) {
        return instanceMap.get(uri);
    }

    public <T> T getInstanceByCapability(String uri, Class <T> serverFacadeClass) {
        T result = null;
        GlassfishInstance instance = instanceMap.get(uri);
        if(instance != null) {
            result = instance.getLookup().lookup(serverFacadeClass);
        }
        return result;
    }
    
    public <T> List<T> getInstancesByCapability(Class<T> serverFacadeClass) {
        List<T> result = new ArrayList<T>();
        synchronized (instanceMap) {
            for (GlassfishInstance instance : instanceMap.values()) {
                T serverFacade = instance.getLookup().lookup(serverFacadeClass);
                if(serverFacade != null) {
                    result.add(serverFacade);
                }
            }
        }
        return result;
    }

    // ------------------------------------------------------------------------
    // ServerInstanceProvider interface implementation
    // ------------------------------------------------------------------------
    @Override
    public List<ServerInstance> getInstances() {
        List<ServerInstance> result = new  ArrayList<ServerInstance>();
        synchronized (instanceMap) {
            for (GlassfishInstance instance : instanceMap.values()) {
                ServerInstance si = instance.getCommonInstance();
                if (null != si) {
                    result.add(si);
                } else {
                    String message = "invalid commonInstance for " + instance.getDeployerUri(); // NOI18N
                    LOGGER.log(Level.WARNING, message);   // NOI18N
                    if (null != instance.getDeployerUri())
                        instanceMap.remove(instance.getDeployerUri());
                }
            }
        }
        return result;
    }
    
    @Override
    public void addChangeListener(ChangeListener listener) {
        support.addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
        support.removeChangeListener(listener);
    }

    // Additional interesting API's
    public boolean hasServer(String uri) {
        return getInstance(uri) != null;
    }
    
    public ServerInstance getInstance(String uri) {
        ServerInstance rv = null;
        GlassfishInstance instance = instanceMap.get(uri);
        if (null != instance) {
            rv = instance.getCommonInstance();
            if (null == rv) {
                String message = "invalid commonInstance for " + instance.getDeployerUri(); // NOI18N
                LOGGER.log(Level.WARNING, message);
                if (null != instance.getDeployerUri())
                    instanceMap.remove(instance.getDeployerUri());
            }
        }
        return rv;
    }

    String getInstancesDirFirstName() {
        return instancesDirNames[0];
    }

    // ------------------------------------------------------------------------
    // Internal use only.  Used by Installer.close() to quickly identify and
    // shutdown any instances we started during this IDE session.
    // ------------------------------------------------------------------------
    Collection<GlassfishInstance> getInternalInstances() {
        return instanceMap.values();
    }

    boolean requiresJdk6OrHigher() {
        return needsJdk6;
    }

    private void init() {
        synchronized (instanceMap) {
            try {
                loadServerInstances();
            } catch (RuntimeException ex) {
                LOGGER.log(Level.INFO, null, ex);
            }
            RegisteredDDCatalog catalog = getDDCatalog();
            if (null != catalog) {
                    catalog.registerEE6RunTimeDDCatalog(this);
                refreshCatalogFromFirstInstance(this, catalog);
            }
        }
        for (GlassfishInstance gi : instanceMap.values()) {
            GlassfishInstance.updateModuleSupport(gi);
        }
    }
    
    // ------------------------------------------------------------------------
    // Persistence for server instances.
    // ------------------------------------------------------------------------
    private void loadServerInstances() {
        FileObject installedInstance = null;
        int savedj = -1;
        for (int j = 0; j < instancesDirNames.length; j++) {
            FileObject dir
                    = ServerUtils.getRepositoryDir(instancesDirNames[j], false);
            if (dir != null) {
                FileObject[] instanceFOs = dir.getChildren();
                if (instanceFOs != null && instanceFOs.length > 0) {
                    for (int i = 0; i < instanceFOs.length; i++) {
                        try {
                            if (GLASSFISH_AUTOREGISTERED_INSTANCE
                                    .equals(instanceFOs[i].getName())) {
                                installedInstance = instanceFOs[i];
                                savedj = j;
                                continue;
                            }
                            GlassfishInstance si = GlassfishInstance
                                    .readInstanceFromFile(instanceFOs[i]);
                            if (si != null) {
                                activeDisplayNames.add(si.getDisplayName());
                            } else {
                                LOGGER.log(Level.FINER,
                                        "Unable to create glassfish instance for {0}", // NOI18N
                                        instanceFOs[i].getPath());
                            }
                        } catch (IOException ex) {
                            LOGGER.log(Level.INFO, null, ex);
                        }
                    }
                }
            }
        }
        if (null != installedInstance
                && null == NbPreferences.forModule(this.getClass())
                .get(AUTOINSTANCECOPIED, null)) {
            try {
                GlassfishInstance igi = GlassfishInstance.
                        readInstanceFromFile(installedInstance);
                try {
                    NbPreferences.forModule(this.getClass())
                            .put(AUTOINSTANCECOPIED, "true"); // NOI18N
                    NbPreferences.forModule(this.getClass()).flush();
                } catch (BackingStoreException ex) {
                    LOGGER.log(Level.INFO,
                            "auto-registered instance may reappear", ex); // NOI18N
                }
                activeDisplayNames.add(igi.getDisplayName());
            } catch (IOException ex) {
                LOGGER.log(Level.INFO, null, ex);
            }
        }
    }

    private void removeInstanceFromFile(String url) {
        FileObject instanceFO = getInstanceFileObject(url);
        if(instanceFO != null && instanceFO.isValid()) {
            try {
                instanceFO.delete();
            } catch(IOException ex) {
                LOGGER.log(Level.INFO, null, ex);
            }
        }
    }

    private FileObject getInstanceFileObject(String url) {
        for (String instancesDirName : instancesDirNames) {
            FileObject dir = ServerUtils.getRepositoryDir(
                    instancesDirName, false);
            if(dir != null) {
                FileObject[] installedServers = dir.getChildren();
                for(int i = 0; i < installedServers.length; i++) {
                    String val = ServerUtils.getStringAttribute(
                            installedServers[i], GlassfishModule.URL_ATTR);
                    if(val != null && val.equals(url) &&
                            !GLASSFISH_AUTOREGISTERED_INSTANCE
                            .equals(installedServers[i].getName())) {
                        return installedServers[i];
                    }
                }
            }
        }
        return null;
    }

    String[] getNoPasswordCreatDomainCommand(String startScript, String jarLocation, 
            String domainDir, String portBase, String uname, String domain) {
            List<String> retVal = new ArrayList<String>();
        retVal.addAll(Arrays.asList(new String[] {startScript,
                    "-client",  // NOI18N
                    "-jar",  // NOI18N
                    jarLocation,
                    "create-domain", //NOI18N
                    "--user", //NOI18N
                    uname,
                    "--domaindir", //NOI18N
                    domainDir}));
        if (null != portBase) {
            retVal.add("--portbase"); //NOI18N
            retVal.add(portBase);
        }
        if (noPasswordOptions.size() > 0) {
            retVal.addAll(noPasswordOptions);
        }
        retVal.add(domain);
        return retVal.toArray(new String[retVal.size()]);
    }

    public CommandFactory getCommandFactory() {
       return cf;
    }

}
