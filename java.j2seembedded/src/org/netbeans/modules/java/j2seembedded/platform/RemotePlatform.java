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
package org.netbeans.modules.java.j2seembedded.platform;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.Specification;
import org.netbeans.api.java.queries.SourceLevelQuery;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.openide.filesystems.FileObject;
import org.openide.modules.SpecificationVersion;
import org.openide.util.NbBundle;
import org.openide.util.Parameters;

/**
 *
 * @author Tomas Zezula
 */
public final class RemotePlatform extends JavaPlatform {

    public static final String SPEC_NAME = "j2se-remote";  //NOI18N
    public static final String PROP_PROPERTIES="properties";                            //NOI18N
    public static final String PLAT_PROP_ANT_NAME="platform.ant.name";                  //NOI18N    
    private static final String PLAT_PROP_INSTALL_FOLDER = "platform.install.folder";   //NOI18N
    private static final String PLAT_PROP_WORK_FOLDER = "platform.work.folder";         //NOI18N
    private static final String PROP_VM_PROFILE = "platform.java.profile";                 //NOI18N
    private static final String PROP_VM_EXTENSIONS = "platform.java.extensions";        //NOI18N
    private static final String PROP_VM_TYPE = "platform.jvm.type";                     //NOI18N
    private static final String PROP_VM_TARGET = "platform.jvm.target";                 //NOI18N
    private static final String PROP_VM_DEBUG = "platform.jvm.debug";                   //NOI18N
    private static final String PROP_EXEC_DECORATOR = "platform.exec.decorator";        //NOI18N
    
    private static final Logger LOG = Logger.getLogger(RemotePlatform.class.getName());

    private final String displayName;
    private final Map<String,String> props;
    private final Specification spec;
    private volatile ConnectionMethod connectionMethod;

    private RemotePlatform(
        @NonNull final String displayName,
        @NonNull final Map<String,String> properties,
        @NonNull final Map<String,String> sysProperties) {
        Parameters.notNull("displayName", displayName); //NOI18N
        Parameters.notNull("properties", properties);   //NOI18N
        Parameters.notNull("sysProperties", sysProperties); //NOI18N
        this.displayName = displayName;
        this.props = new HashMap<>(properties);
        this.spec = new Specification(
            SPEC_NAME,
            createSpecificationVersion(sysProperties.get("java.specification.version")),   // NOI18N
            NbBundle.getMessage(RemotePlatform.class, "TXT_RemotePlatform"),
            null);
        setSystemProperties(sysProperties);
    }

    @NonNull
    public static RemotePlatform create(@NonNull final RemotePlatform prototype) {
        Parameters.notNull("prototype", prototype); //NOI18N
        return new RemotePlatform(
            prototype.getDisplayName(),
            prototype.getProperties(),
            prototype.getSystemProperties());
    }

    @NonNull
    public static RemotePlatform prototype(
        @NonNull final String displayName,
        @NonNull final Map<String,String> additionalProperties,
        @NonNull final Map<String,String> sysProps ) {
        Parameters.notNull("displayName", displayName); //NOI18N
        Parameters.notNull("additionalProperties", additionalProperties);       //NOI18N
        Parameters.notNull("sysProps", sysProps);       //NOI18N
        String currentDisplayName = displayName;
        String antName;
        for (int i=0;;i++) {
            antName = PropertyUtils.getUsablePropertyName(currentDisplayName);
            if (RemotePlatformProvider.isValidPlatformAntName(antName)) {
                break;
            }
            currentDisplayName = String.format(
                "%s %d",    //NOI18N
                displayName,
                i);
        }
        final Map<String,String> props = new HashMap<>();
        props.putAll(additionalProperties);
        props.put(PLAT_PROP_ANT_NAME, antName);
        return create(
            displayName,
            props,
            sysProps);
    }

    @NonNull
    static RemotePlatform create(
            @NonNull final String name,
            @NonNull final Map<String,String> properties,
            @NonNull final Map<String,String> sysProperties) {
        return new RemotePlatform(name, properties, sysProperties);
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(props);
    }

    @Override
    public ClassPath getBootstrapLibraries() {
        return ClassPath.EMPTY;
    }

    @Override
    public ClassPath getStandardLibraries() {
        return ClassPath.EMPTY;
    }

    @Override
    public String getVendor() {
        return getSystemProperties().get("java.vm.vendor"); //NOI18N
    }

    @Override
    public Specification getSpecification() {
        return spec;
    }

    @Override
    public Collection<FileObject> getInstallFolders() {
        return Collections.<FileObject>emptySet();
    }

    @Override
    public FileObject findTool(String toolName) {
        return null;
    }

    @Override
    public ClassPath getSourceFolders() {
        return ClassPath.EMPTY;
    }

    @Override
    public List<URL> getJavadocFolders() {
        return Collections.<URL>emptyList();
    }

    // RemotePlatform specific methods:

    /**
     * Returns the remote platform install folder.
     * @return an install folder URI.
     * @throws IllegalStateException when no valid install folder.
     */
    @NonNull
    public URI getInstallFolder() {
        final String path = props.get(PLAT_PROP_INSTALL_FOLDER);
        if (path == null) {
            throw new IllegalStateException("No install folder.");  //NOI18N
        }
        try {
            return new URI(path);
        } catch (URISyntaxException ex) {
            throw  new IllegalStateException(ex);
        }
    }


    public void setInstallFolder(@NonNull final URI installFolder) {
        Parameters.notNull("installFolder", installFolder); //NOI18N
        props.put(PLAT_PROP_INSTALL_FOLDER, installFolder.toString());
        firePropertyChange(PROP_PROPERTIES, null, null);
    }

    /**
     * Returns the remote platform work folder.
     * @return an work folder URI.
     * @throws IllegalStateException when no valid work folder.
     */
    @NonNull
    public URI getWorkFolder() {
        final String path = props.get(PLAT_PROP_WORK_FOLDER);
        if (path == null) {
            throw new IllegalStateException("No work folder."); //NOI18N
        }
        try {
            return new URI(path);
        } catch (URISyntaxException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public void setWorkFolder(@NonNull final URI workDir) {
        Parameters.notNull("workDir", workDir); //NOI18N
        props.put(PLAT_PROP_WORK_FOLDER, workDir.toString());
        firePropertyChange(PROP_PROPERTIES, null, null);
    }

    @NonNull
    public ConnectionMethod getConnectionMethod() {
        ConnectionMethod cm = connectionMethod;
        if (cm == null) {
            connectionMethod = cm = ConnectionMethod.load(props);
        }
        return cm;
    }

    @NonNull
    public SourceLevelQuery.Profile getProfile() {
        SourceLevelQuery.Profile profile = SourceLevelQuery.Profile.forName(
            getProperties().get(PROP_VM_PROFILE));
        if (profile == null) {
            profile = SourceLevelQuery.Profile.DEFAULT;
        }
        return profile;
    }

    public void setConnectionMethod(@NonNull final ConnectionMethod cm) {
        Parameters.notNull("cm", cm); //NOI18N
        connectionMethod = cm;
        cm.store(props);
        firePropertyChange(PROP_PROPERTIES, null, null);
    }

    @NonNull
    Collection<String> getGlobalPropertyNames() {
        final Set<String> result = new HashSet<>();
        result.add(PLAT_PROP_INSTALL_FOLDER);
        result.add(PLAT_PROP_WORK_FOLDER);
        result.add(PROP_VM_DEBUG);
        result.add(PROP_EXEC_DECORATOR);
        result.addAll(getConnectionMethod().getGlobalPropertyNames());
        return Collections.unmodifiableSet(result);
    }    

    @CheckForNull
    String getVMType() {
        return getProperties().get(PROP_VM_TYPE);
    }

    @CheckForNull
    String getVMTarget() {
        return getProperties().get(PROP_VM_TARGET);
    }

    @CheckForNull
    String getExtensions() {
        return getProperties().get(PROP_VM_EXTENSIONS);
    }

    boolean canDebug() {
        final String prop = getProperties().get(PROP_VM_DEBUG);
        return prop == null ?
            true :
            Boolean.parseBoolean(prop);
    }

    @CheckForNull
    String getExecDecorator() {
        return props.get(PROP_EXEC_DECORATOR);
    }

    void setExecDecorator(@NullAllowed final String command) {
        if (command == null) {
            props.remove(PROP_EXEC_DECORATOR);
        } else {
            props.put(PROP_EXEC_DECORATOR, command);
        }
        firePropertyChange(PROP_PROPERTIES, null, null);
    }

    //Utility methods
    @NonNull
    private static SpecificationVersion createSpecificationVersion(
        @NullAllowed String version) {
        if (version != null) {
            try {
                return new SpecificationVersion(version);
            } catch (NumberFormatException nfe) {
                LOG.log(
                    Level.WARNING,
                    "Invalid specification version: {0}",   // NOI18N
                    version);
            }
            do {
                version = version.substring(0, version.length() - 1);
                try {
                    return new SpecificationVersion(version);
                } catch (NumberFormatException nfe) {
                    // ignore
                }
            } while (version.length() > 0);
        }
        //Nothing return lower bound JDK 1.1
        return new SpecificationVersion("1.1"); // NOI18N
    }

}
