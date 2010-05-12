/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.nativeexecution.support.hostinfo.impl;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import org.netbeans.modules.nativeexecution.api.HostInfo;
import org.netbeans.modules.nativeexecution.api.HostInfo.Bitness;
import org.netbeans.modules.nativeexecution.api.HostInfo.CpuFamily;
import org.netbeans.modules.nativeexecution.api.HostInfo.OS;
import org.netbeans.modules.nativeexecution.api.HostInfo.OSFamily;
import org.netbeans.modules.nativeexecution.api.util.WindowsSupport;

public final class HostInfoFactory {

    private static final String UNKNOWN = "UNKNOWN"; // NOI18N

    private HostInfoFactory() {
    }

    static HostInfo newHostInfo(Properties initData, Map<String, String> environment) {
        HostInfoImpl info = new HostInfoImpl();

        OSImpl _os = new OSImpl();
        _os.setBitness(getInt(initData, "BITNESS", 32)); // NOI18N
        _os.setFamily(initData.getProperty("OSFAMILY", UNKNOWN));
        _os.setName(initData.getProperty("OSNAME", UNKNOWN));
        _os.setVersion(initData.getProperty("OSBUILD", UNKNOWN)); // NOI18N
        info.os = _os;

        info.hostname = initData.getProperty("HOSTNAME", UNKNOWN); // NOI18N

        try {
            info.cpuFamily = CpuFamily.valueOf(initData.getProperty("CPUFAMILY", UNKNOWN).toUpperCase()); // NOI18N
        } catch (IllegalArgumentException ex) {
            info.cpuFamily = CpuFamily.UNKNOWN;
        }

        info.shell = initData.getProperty("SH", UNKNOWN); // NOI18N
        info.tempDir = initData.getProperty("TMPDIRBASE", UNKNOWN); // NOI18N
        info.userDir = initData.getProperty("USERDIRBASE", UNKNOWN); // NOI18N
        info.cpuNum = getInt(initData, "CPUNUM", 1); // NOI18N
        info.envFile = initData.getProperty("ENVFILE", "/dev/null"); // NOI18N

        if (environment == null) {
            info.environment = Collections.unmodifiableMap(Collections.<String, String>emptyMap());
        } else {
            info.environment = Collections.unmodifiableMap(environment);
        }

        if (initData.containsKey("LOCALTIME")) { // NOI18N
            long localTime = (Long) initData.get("LOCALTIME"); // NOI18N
            long remoteTime = getTime(initData, "DATETIME", localTime); // NOI18N
            info.clockSkew = remoteTime - localTime;
        }

        return info;
    }

    private static int getInt(Properties props, String key, int defaultValue) {
        int result = defaultValue;
        String value = props.getProperty(key, null);
        if (value != null) {
            try {
                result = Integer.parseInt(value);
            } catch (NumberFormatException ex) {
            }
        }

        return result;
    }

    private static long getTime(Properties props, String key, long defaultValue) {
        long result = defaultValue;
        String value = props.getProperty(key, null);
        if (value != null) {
            try {
                DateFormat df = new SimpleDateFormat("y-M-d H:m:s"); // NOI18N
                df.setTimeZone(TimeZone.getTimeZone("GMT")); // NOI18N
                Date date = df.parse(value);
                result = date.getTime();
            } catch (ParseException ex) {
            }
        }
        return result;
    }

    static private class HostInfoImpl implements HostInfo {

        private OS os;
        private CpuFamily cpuFamily;
        private String hostname;
        private String shell;
        private String tempDir;
        private String userDir;
        private int cpuNum;
        private long clockSkew;
        private String envFile;
        private Map<String, String> environment;

        @Override
        public OS getOS() {
            return os;
        }

        @Override
        public CpuFamily getCpuFamily() {
            return cpuFamily;
        }

        @Override
        public int getCpuNum() {
            return cpuNum;
        }

        @Override
        public OSFamily getOSFamily() {
            return os.getFamily();
        }

        @Override
        public String getHostname() {
            return hostname;
        }

        @Override
        public String getShell() {
            return shell;
        }

        @Override
        public String getTempDir() {
            return tempDir;
        }

        @Override
        public String getUserDir() {
            return userDir;
        }

        @Override
        public File getUserDirFile() {
            if (getOSFamily() == OSFamily.WINDOWS) {
                return new File(WindowsSupport.getInstance().convertToWindowsPath(userDir));
            } else {
                return new File(userDir);
            }
        }

        @Override
        public File getTempDirFile() {
            if (getOSFamily() == OSFamily.WINDOWS) {
                return new File(WindowsSupport.getInstance().convertToWindowsPath(tempDir));
            } else {
                return new File(tempDir);
            }
        }

        @Override
        public long getClockSkew() {
            return clockSkew;
        }

        @Override
        public String getEnvFile() {
            return envFile;
        }

        @Override
        public Map<String, String> getEnvironment() {
            return environment;
        }
    }

    static final class OSImpl implements OS {

        private OSFamily family = OSFamily.UNKNOWN;
        private String name = UNKNOWN;
        private String version = UNKNOWN;
        private Bitness bitness = Bitness._32;

        @Override
        public Bitness getBitness() {
            return bitness;
        }

        @Override
        public String getVersion() {
            return version;
        }

        @Override
        public OSFamily getFamily() {
            return family;
        }

        @Override
        public String getName() {
            return name;
        }

        private void setVersion(String version) {
            this.version = version;
        }

        private void setBitness(int bitness) {
            this.bitness = bitness == 64 ? Bitness._64 : Bitness._32;
        }

        private void setFamily(String family) {
            try {
                this.family = OSFamily.valueOf(family.toUpperCase());
            } catch (IllegalArgumentException ex) {
            }
        }

        private void setName(String name) {
            this.name = name;
        }
    }

    /**
     * @return unique key of the current NB instance, introduced to fix bug #176526
     */
    /*package-local*/ static String getNBKey() {
        // use NB userdir to prevent local collisions
        int hashCode = System.getProperty("netbeans.user", "").hashCode();
        try {
            // use host name to prevent remote collisions
            InetAddress localhost = InetAddress.getLocalHost();
            hashCode = 3 * hashCode + 5 * localhost.getHostName().hashCode();
        } catch (UnknownHostException ex) {
        }
        return Integer.toHexString(hashCode);
    }
}
