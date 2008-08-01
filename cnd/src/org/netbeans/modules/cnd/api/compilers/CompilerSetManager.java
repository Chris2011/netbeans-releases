/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.cnd.api.compilers;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.SwingUtilities;
import org.netbeans.modules.cnd.api.compilers.CompilerSet.CompilerFlavor;
import org.netbeans.modules.cnd.api.compilers.ToolchainManager.ToolchainDescriptor;
import org.netbeans.modules.cnd.api.compilers.ToolchainManager.CompilerDescriptor;
import org.netbeans.modules.cnd.api.remote.ServerList;
import org.netbeans.modules.cnd.api.remote.ServerRecord;
import org.netbeans.modules.cnd.api.utils.IpeUtils;
import org.netbeans.modules.cnd.api.utils.Path;
import org.netbeans.modules.cnd.compilers.DefaultCompilerProvider;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.filesystems.FileUtil;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.RequestProcessor;

/**
 * Manage a set of CompilerSets. The CompilerSets are dynamically created based on which compilers
 * are found in the user's $PATH variable.
 */
public class CompilerSetManager {

    /* Legacy defines for CND 5.5 compiler set definitions */
    public static final int SUN_COMPILER_SET = 0;
    public static final int GNU_COMPILER_SET = 1;

    public static final Object STATE_PENDING = "state_pending"; // NOI18N
    public static final Object STATE_COMPLETE = "state_complete"; // NOI18N
    public static final Object STATE_UNINITIALIZED = "state_uninitialized"; // NOI18N

    public static final String LOCALHOST = "localhost"; // NOI18N

    /* Persistance information */
    private static final double csm_version = 1.1;
    private static final String CSM = "csm."; // NOI18N
    private static final String VERSION = "version"; // NOI18N
    private static final String NO_SETS = ".noOfSets"; // NOI18N
    private static final String SET_NAME = ".setName."; // NOI18N
    private static final String CURRENT_SET_NAME = ".currentSetName"; // NOI18N
    private static final String SET_FLAVOR = ".setFlavor."; // NOI18N
    private static final String SET_DIRECTORY = ".setDirectory."; // NOI18N
    private static final String SET_AUTO = ".autoGenerated."; // NOI18N
    private static final String SET_DEFAULT = ".defaultSet"; // NOI18N
    private static final String SET_PLATFORM = ".setPlatform."; // NOI18N
    private static final String NO_TOOLS = ".noOfTools."; // NOI18N
    private static final String TOOL_NAME = ".toolName."; // NOI18N
    private static final String TOOL_DISPLAYNAME = ".toolDisplayName."; // NOI18N
    private static final String TOOL_KIND = ".toolKind."; // NOI18N
    private static final String TOOL_PATH = ".toolPath."; // NOI18N
    private static final String TOOL_FLAVOR = ".toolFlavor."; // NOI18N

    private static HashMap<String, CompilerSetManager> managers = new HashMap<String, CompilerSetManager>();
    private final static Object MASTER_LOCK = new Object();
    private static CompilerProvider compilerProvider = null;

    public static final String Sun12 = "SunStudio_12"; // NOI18N
    public static final String Sun11 = "SunStudio_11"; // NOI18N
    public static final String Sun10 = "SunStudio_10"; // NOI18N
    public static final String Sun = "SunStudio"; // NOI18N
    public static final String GNU = "GNU"; // NOI18N

    private List<CompilerSet> sets = new ArrayList<CompilerSet>();
    private final String hkey;
    private Object state;
    private int platform = -1;
    private static final Logger log = Logger.getLogger("cnd.remote.logger"); // NOI18N

    /**
     * Find or create a default CompilerSetManager for the given key. A default
     * CSM is one which is active in the system. A non-default is one which gets
     * created but has no affect unless its made default.
     *
     * For instance, the Build Tools tab (on C/C++ Tools->Options) creates a non-Default
     * CSM and only makes it default if the OK button is pressed. If Cancel is pressed,
     * it never becomes default.
     *
     * @param key Either user@host or localhost
     * @return A default CompilerSetManager for the given key
     */
    public static CompilerSetManager getDefault(String key) {
        CompilerSetManager csm = null;
        boolean no_compilers = false;

        synchronized (MASTER_LOCK) {
            csm = managers.get(key);
            if (csm == null) {
                csm = restoreFromDisk(key);
                if (csm != null && csm.getDefaultCompilerSet() == null) {
                    csm.initDefaltCompilerSet();
                    csm.saveToDisk();
                }
            }
            if (csm == null) {
                csm = new CompilerSetManager(key);
                if (csm.isValid()) {
                    csm.saveToDisk();
                } else if (!csm.isPending() && !csm.isUninitialized()) {
                    no_compilers = true;
                }
            }
            if (csm != null) {
                managers.put(key, csm);
            }
        }
        
        if (csm.state == STATE_UNINITIALIZED && !SwingUtilities.isEventDispatchThread()) {
            log.fine("CSM.getDefault: Doing deferred remote setup");
            csm.sets.clear();
            csm.initRemoteCompilerSets(key);
        }
        if (no_compilers) {
            DialogDescriptor dialogDescriptor = new DialogDescriptor(
                new NoCompilersPanel(),
                getString("NO_COMPILERS_FOUND_TITLE"),
                true,
                new Object[]{DialogDescriptor.OK_OPTION},
                DialogDescriptor.OK_OPTION,
                DialogDescriptor.BOTTOM_ALIGN,
                null,
                null);
            DialogDisplayer.getDefault().notify(dialogDescriptor);
        }
        return csm;
    }

    public static CompilerSetManager getDefault() {
	return getDefault(LOCALHOST);
    }

    /** Create a CompilerSetManager which may be registered at a later time via CompilerSetManager.setDefault() */
    public static CompilerSetManager create() {
        CompilerSetManager csm;
        synchronized (MASTER_LOCK) {
            csm = new CompilerSetManager(LOCALHOST);
        }
        return csm;
    }

    /** Replace the default CompilerSetManager. Let registered listeners know its been updated */
    public static synchronized void setDefault(CompilerSetManager csm) {
        if (csm.getCompilerSets().size() == 0) { // No compilers found
            csm.add(CompilerSet.createEmptyCompilerSet(csm.getPlatform()));
        }
        synchronized (MASTER_LOCK) {
            csm.saveToDisk();
            managers.put(csm.hkey, csm);
        }
    }

    private CompilerSetManager(String key) {
        hkey = key;
        state = STATE_PENDING;
        init();
    }

    private CompilerSetManager(String hkey, List<CompilerSet> sets, int platform) {
        this.hkey = hkey;
        this.sets = sets;
        this.state = STATE_COMPLETE;
        this.platform = platform;
    }

    private void init() {
        if (hkey.equals(LOCALHOST)) {
            platform = computeLocalPlatform();
            initCompilerSets(Path.getPath());
            state = STATE_COMPLETE;
        } else {
            log.fine("CSM.init: initializing remote compiler set for: " + hkey);
            initRemoteCompilerSets(hkey);
        }
    }

    public boolean isValid() {
        return sets.size() > 0 && !sets.get(0).getName().equals(CompilerSet.None);
    }

    public boolean isPending() {
        return state == STATE_PENDING;
    }

    public boolean isUninitialized() {
        return state == STATE_UNINITIALIZED;
    }

    public int getPlatform() {
        if (platform < 0) {
            if (hkey.equals(LOCALHOST)) {
                platform = computeLocalPlatform();
            } else {
                waitForCompletion();
            }
        }
        return platform;
    }

    public void waitForCompletion() {
        while (isPending()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }
        }
    }

    private static int computeLocalPlatform() {
        String os = System.getProperty("os.name"); // NOI18N

        if (os.equals("SunOS")) {
            return System.getProperty("os.arch").equals("x86") ? PlatformTypes.PLATFORM_SOLARIS_INTEL : PlatformTypes.PLATFORM_SOLARIS_SPARC; // NOI18N
        } else if (os.startsWith("Windows ")) {
            return PlatformTypes.PLATFORM_WINDOWS;
        } else if (os.toLowerCase().contains("linux")) {
            return PlatformTypes.PLATFORM_LINUX;
        } else if (os.toLowerCase().contains("mac")) {
            return PlatformTypes.PLATFORM_MACOSX;
        } else {
            return PlatformTypes.PLATFORM_GENERIC;
        }
    }

    public CompilerSetManager deepCopy() {
        waitForCompletion(); // in case its a remote connection...
        List<CompilerSet> setsCopy =  new ArrayList<CompilerSet>();
        for (CompilerSet set : getCompilerSets()) {
            setsCopy.add(set.createCopy());
        }
        CompilerSetManager copy = new CompilerSetManager(this.hkey, setsCopy, this.platform);
        return copy;
    }

    public String getUniqueCompilerSetName(String baseName) {
        int n = 0;
        String suggestedName = baseName;
        while (true) {
            suggestedName = baseName + (n > 0 ? ("_" + n) : ""); // NOI18N
            if (getCompilerSet(suggestedName) != null) {
                n++;
            }
            else {
                break;
            }
        }
        return suggestedName;
    }

    /** Search $PATH for all desired compiler sets and initialize cbCompilerSet and spCompilerSets */
    private void initCompilerSets(ArrayList<String> dirlist) {
        Set<CompilerFlavor> flavors = new HashSet<CompilerFlavor>();
        initKnownCompilers(getPlatform(), flavors);
        for (String path : dirlist) {
            if (path.equals("/usr/ucb")) { // NOI18N
                // Don't look here.
                continue;
            }
            if (!IpeUtils.isPathAbsolute(path)) {
                path = FileUtil.normalizeFile(new File(path)).getAbsolutePath();
            }
            File dir = new File(path);
            if (dir.isDirectory()) {
                for(CompilerFlavor flavor : CompilerSet.getCompilerSetFlavor(dir.getAbsolutePath(), getPlatform())) {
                    if (!flavors.contains(flavor)) {
                        flavors.add(flavor);
                        CompilerSet cs = CompilerSet.getCustomCompilerSet(dir.getAbsolutePath(), flavor, flavor.toString());
                        cs.setAutoGenerated(true);
                        initCompilerSet(path, cs);
                        add(cs);
                    }
                }
            }
        }
        completeCompilerSets();
    }

    private void initDefaltCompilerSet() {
        // for now just use the first one
        // but we should choose "GNU vs SS" based on "NB vs SS" knowledge
        if (!sets.isEmpty()) {
            setDefault(sets.get(0));
        }
    }

    private void initKnownCompilers(int platform, Set<CompilerFlavor> flavors){
        for(ToolchainDescriptor d : ToolchainManager.getInstance().getToolchains(platform)) {
            String base = ToolchainManager.getInstance().getBaseFolder(d, platform);
            if (base != null) {
                File folder = new File(base);
                if (folder.exists() && folder.isDirectory()){
                    CompilerFlavor flavor = CompilerFlavor.toFlavor(d.getName(), platform);
                    flavors.add(flavor);
                    CompilerSet cs = CompilerSet.getCustomCompilerSet(folder.getAbsolutePath(), flavor, flavor.toString());
                    cs.setAutoGenerated(true);
                    initCompilerSet(base, cs);
                    add(cs);
                }
            }
        }
    }

    /** Initialize remote CompilerSets */
    private void initRemoteCompilerSets(final String key) {
        final CompilerSetProvider provider = Lookup.getDefault().lookup(CompilerSetProvider.class);
        ServerList registry = (ServerList) Lookup.getDefault().lookup(ServerList.class);
        assert registry != null;
        assert provider != null;
        ServerRecord record = registry.get(key);
        assert record != null;

        record.validate();
        if (record.isOnline()) {
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    provider.init(key);
                    platform = provider.getPlatform();
                    log.fine("CSM.initRemoteCompileSets: platform = " + platform);
                    getPreferences().putInt(CSM + hkey + SET_PLATFORM, platform);
                    while (provider.hasMoreCompilerSets()) {
                        String data = provider.getNextCompilerSetData();
                        log.fine("CSM.initRemoteCompileSets: line = [" + data + "]");
                        int i1 = data.indexOf(';');
                        int i2 = data.indexOf(';', i1 + 1);
                        String flavor = data.substring(0, i1);
                        String path = data.substring(i1 + 1, i2);
                        String tools = data.substring(i2 + 1);
                        CompilerSet cs = new CompilerSet(CompilerFlavor.toFlavor(flavor, platform), path, flavor);
                        StringTokenizer st = new StringTokenizer(tools, ";"); // NOI18N
                        while (st.hasMoreTokens()) {
                            String name = st.nextToken();
                            int kind = -1;
                            String p = path + '/' + name;
                            if (flavor.startsWith("Sun")) { // NOI18N
                                if (name.equals("cc")) { // NOI18N
                                    kind = Tool.CCompiler;
                                } else if (name.equals("CC")) { // NOI18N
                                    kind = Tool.CCCompiler;
                                } else if (name.equals("dmake")) { // NOI18N
                                    kind = Tool.MakeTool;
                                } else if (name.startsWith("gdb=")) { // NOI18N
                                    kind = Tool.DebuggerTool;
                                    i1 = name.indexOf('=');
                                    p = name.substring(i1 + 1);
                                }
                            } else {
                                if (name.equals("gcc")) { // NOI18N
                                    kind = Tool.CCompiler;
                                } else if (name.equals("g++")) { // NOI18N
                                    kind = Tool.CCCompiler;
                                } else if (name.equals("make") ||  // NOI18N
                                        ((platform == PlatformTypes.PLATFORM_SOLARIS_INTEL || platform == PlatformTypes.PLATFORM_SOLARIS_SPARC) &&
                                                name.equals("gmake"))) { // NOI18N
                                    kind = Tool.MakeTool;
                                } else if (name.equals("gdb")) { // NOI18N
                                    kind = Tool.DebuggerTool;
                                } else if (name.startsWith("gdb=")) { // NOI18N
                                    kind = Tool.DebuggerTool;
                                    i1 = name.indexOf('=');
                                    p = name.substring(i1 + 1);
                                }
                            }
                            if (kind != -1) {
                                cs.addTool(key, name, p, kind);
                            }
                        }
                        add(cs);
                    }
                    List<CompilerSet> setsCopy;
                    if (sets instanceof ArrayList) {
                        setsCopy = (List<CompilerSet>)((ArrayList<CompilerSet>)sets).clone();
                    } else {
                        // this will never be called in current impl but interface allows any List so:
                        setsCopy = new ArrayList<CompilerSet>();
                        setsCopy.addAll(sets);
                    }
                    provider.loadCompilerSetData(setsCopy);
                    // TODO: this should be upgraded to error reporting
                    // about absence of tool chain on remote host
                    // also compilersetmanager without compiler sets
                    // should be handled gracefully
                    log.fine("CSM.initRemoteCompilerSets: Found " + sets.size() + " compiler sets");
                    state = STATE_COMPLETE;
                }
            });
        } else {
            // create empty CSM
            log.fine("CSM.initRemoteCompilerSets: Adding empty CS to OFFLINE host " + key);
            add(CompilerSet.createEmptyCompilerSet(PlatformTypes.PLATFORM_NONE));
            state = STATE_UNINITIALIZED;
        }
    }

    public void initCompilerSet(CompilerSet cs) {
        initCompilerSet(cs.getDirectory(), cs);
        completeCompilerSet(hkey, cs, sets);
    }

    public void reInitCompilerSet(CompilerSet cs, String path) {
        cs.reparent(path);
        initCompilerSet(cs);
    }

    private void initCompilerSet(String path, CompilerSet cs) {
        CompilerFlavor flavor = cs.getCompilerFlavor();
        ToolchainDescriptor d = flavor.getToolchainDescriptor();
        if (d != null && ToolchainManager.getInstance().isMyFolder(path, d, getPlatform())) {
            CompilerDescriptor compiler = d.getC();
            if (compiler != null) {
                initCompiler(Tool.CCompiler, path, cs, compiler.getNames());
            }
            compiler = d.getCpp();
            if (compiler != null) {
                initCompiler(Tool.CCCompiler, path, cs, compiler.getNames());
            }
            compiler = d.getFortran();
            if (compiler != null) {
                initCompiler(Tool.FortranCompiler, path, cs, compiler.getNames());
            }
            initCompiler(Tool.MakeTool, path, cs, d.getMake().getNames());
            initCompiler(Tool.DebuggerTool, path, cs, d.getDebugger().getNames());
        }
    }

    private void initCompiler(int kind, String path, CompilerSet cs, String[] names) {
        File dir = new File(path);
        if (cs.findTool(kind) != null) {
            // Only one tool of each kind in a cs
            return;
        }
        for (String name : names) {
            File file = new File(dir, name);
            if (file.exists() && !file.isDirectory()) {
                cs.addTool(hkey, name, file.getAbsolutePath(), kind);
                return;
            }
            file = new File(dir, name+".exe"); // NOI18N
            if (file.exists() && !file.isDirectory()) {
                cs.addTool(hkey, name, file.getAbsolutePath(), kind);
                return;
            }
        }
    }

    /**
     * If a compiler set doesn't have one of each compiler types, add a "No compiler"
     * tool. If selected, this will tell the build validation things are OK.
     */
    private void completeCompilerSets() {
        for (CompilerSet cs : sets) {
            completeCompilerSet(hkey, cs, sets);
        }
        if (sets.size() == 0) { // No compilers found
            add(CompilerSet.createEmptyCompilerSet(getPlatform()));
        } else {
            initDefaltCompilerSet();
        }
    }

    private static void completeCompilerSet(String hkey, CompilerSet cs, List<CompilerSet> sets) {
        if (cs.getTool(Tool.CCompiler) == null) {
            cs.addTool(hkey, "", "", Tool.CCompiler);
        }
        if (cs.getTool(Tool.CCCompiler) == null) {
            cs.addTool(hkey, "", "", Tool.CCCompiler);
        }
        if (cs.getTool(Tool.FortranCompiler) == null) {
            cs.addTool(hkey, "", "", Tool.FortranCompiler);
        }
        if (cs.findTool(Tool.MakeTool) == null) {
            Tool other = null;
            for (CompilerSet set : sets) {
                other = set.findTool(Tool.MakeTool);
                if (other != null) {
                    break;
                }
            }
            if (other != null) {
                cs.addNewTool(hkey, other.getName(), other.getPath(), Tool.MakeTool);
            } else {
                String path = Path.findCommand("make"); // NOI18N
                if (path != null) {
                    cs.addNewTool(hkey, IpeUtils.getBaseName(path), path, Tool.MakeTool);
                } else {
                    path = Path.findCommand("gmake"); // NOI18N
                    if (path != null) {
                        cs.addNewTool(hkey, IpeUtils.getBaseName(path), path, Tool.MakeTool);
                    }
                }
            }
        }
        if (cs.getTool(Tool.MakeTool) == null) {
                cs.addTool(hkey, "", "", Tool.MakeTool);
        }
        if (cs.findTool(Tool.DebuggerTool) == null) {
            String path;
            if (IpeUtils.isGdbEnabled()) {
                path = Path.findCommand("gdb"); // NOI18N
            }
            else {
                path = Path.findCommand("dbx"); // NOI18N
            }
            if (path != null)
                cs.addNewTool(hkey, IpeUtils.getBaseName(path), path, Tool.DebuggerTool);
        }
        if (cs.getTool(Tool.DebuggerTool) == null) {
                cs.addTool(hkey, "", "", Tool.DebuggerTool);
        }

    }

    /**
     * Add a CompilerSet to this CompilerSetManager. Make sure it doesn't get added multiple times.
     *
     * @param cs The CompilerSet to (possibly) add
     */
    public void add(CompilerSet cs) {
//        String csdir = cs.getDirectory();

        if (sets.size() == 1 && sets.get(0).getName().equals(CompilerSet.None)) {
            sets.remove(0);
        }
//        if (cs.isAutoGenerated()) {
//            for (CompilerSet cs2 : sets) {
//                if (cs2.getDirectory().equals(csdir)) {
//                    return;
//                }
//            }
//        }
        sets.add(cs);
        if (sets.size() == 1) {
            setDefault(cs);
        }
    }

    /**
     * Remove a CompilerSet from this CompilerSetManager. Use caution with this method. Its primary
     * use is to remove temporary CompilerSets which were added to represent missing compiler sets. In
     * that context, they're removed immediately after showing the ToolsPanel after project open.
     *
     * @param cs The CompilerSet to (possibly) remove
     */
    public void remove(CompilerSet cs) {
        int idx = sets.indexOf(cs);
        if (idx >= 0) {
            sets.remove(idx);
        }
    }

    public CompilerSet getCompilerSet(CompilerFlavor flavor) {
        return getCompilerSet(flavor.toString());
    }

    public CompilerSet getCompilerSet(String name) {
        for (CompilerSet cs : sets) {
            if (cs.getName().equals(name)) {
                return cs;
            }
        }
        return null;
    }

    public CompilerSet getCompilerSetByDisplayName(String name) {
        for (CompilerSet cs : sets) {
            if (cs.getDisplayName().equals(name)) {
                return cs;
            }
        }
        return null;
    }

    public CompilerSet getCompilerSetByPath(String path) {
        for (CompilerSet cs : sets) {
            if (cs.getDirectory().equals(path)) {
                return cs;
            }
        }
        return null;
    }

    public CompilerSet getCompilerSet(String name, String dname) {
        waitForCompletion();
        for (CompilerSet cs : sets) {
            if (cs.getName().equals(name) && cs.getDisplayName().equals(dname)) {
                return cs;
            }
        }
        return null;
    }

    public CompilerSet getCompilerSet(int idx) {
        waitForCompletion();
        if (idx >= 0 && idx < sets.size())
            return sets.get(idx);
        else
            return null;
    }

    public List<CompilerSet> getCompilerSets() {
        return sets;
    }

    public List<String> getCompilerSetDisplayNames() {
        List<String> names = new ArrayList<String>();
        for (CompilerSet cs : getCompilerSets()) {
            names.add(cs.getDisplayName());
        }
        return names;
    }

    public List<String> getCompilerSetNames() {
        List<String> names = new ArrayList<String>();
        for (CompilerSet cs : getCompilerSets()) {
            names.add(cs.getName());
        }
        return names;
    }

    public void setDefault(CompilerSet newDefault) {
        boolean set = false;
        for (CompilerSet cs : getCompilerSets()) {
            cs.setAsDefault(false);
            if (cs == newDefault) {
                newDefault.setAsDefault(true);
                set = true;
            }
        }
        if (!set && sets.size() > 0) {
            getCompilerSet(0).setAsDefault(true);
        }
    }

    public CompilerSet getDefaultCompilerSet() {
        for (CompilerSet cs : getCompilerSets()) {
            if (cs.isDefault())
                return cs;
        }
        return null;
    }

    /**
     * Check if the gdb module is enabled. Don't show the gdb line if it isn't.
     *
     * @return true if the gdb module is enabled, false if missing or disabled
     */
    protected boolean isGdbEnabled() {
        Iterator iter = Lookup.getDefault().lookup(new Lookup.Template(ModuleInfo.class)).allInstances().iterator();
        while (iter.hasNext()) {
            ModuleInfo info = (ModuleInfo) iter.next();
            if (info.getCodeNameBase().equals("org.netbeans.modules.cnd.debugger.gdb") && info.isEnabled()) { // NOI18N
                return true;
            }
        }
        return false;
    }

    /** Special FilenameFilter which should recognize different variations of supported compilers */
    private class CompilerFilenameFilter implements FilenameFilter {

        Pattern pc = null;

        public CompilerFilenameFilter(String pattern) {
            try {
                pc = Pattern.compile(pattern);
            } catch (PatternSyntaxException ex) {
            }
        }

        public boolean accept(File dir, String name) {
            return pc != null && pc.matcher(name).matches();
        }
    }

    private static CompilerProvider getCompilerProvider() {
        if (compilerProvider == null) {
            compilerProvider = Lookup.getDefault().lookup(CompilerProvider.class);
        }
        if (compilerProvider == null) {
            compilerProvider = new DefaultCompilerProvider();
        }
        return compilerProvider;
    }

    /*
     * Persistence ...
     */
    private static Preferences getPreferences() {
        return NbPreferences.forModule(CompilerSetManager.class);
    }

    public void saveToDisk() {
        if (!sets.isEmpty() && getPlatform() != PlatformTypes.PLATFORM_GENERIC) {
            getPreferences().putDouble(CSM + VERSION, csm_version);
            getPreferences().putInt(CSM + hkey + NO_SETS, sets.size());
            getPreferences().putInt(CSM + hkey + SET_PLATFORM, getPlatform());
            int setCount = 0;
            for (CompilerSet cs : getCompilerSets()) {
                getPreferences().put(CSM + hkey + SET_NAME + setCount, cs.getName());
                getPreferences().put(CSM + hkey + SET_FLAVOR + setCount, cs.getCompilerFlavor().toString());
                getPreferences().put(CSM + hkey + SET_DIRECTORY + setCount, cs.getDirectory());
                getPreferences().putBoolean(CSM + hkey + SET_AUTO + setCount, cs.isAutoGenerated());
                getPreferences().putBoolean(CSM + hkey + SET_DEFAULT + setCount, cs.isDefault());
                List<Tool> tools = cs.getTools();
                getPreferences().putInt(CSM + hkey + NO_TOOLS + setCount, tools.size());
                int toolCount = 0;
                for (Tool tool : tools) {
                    getPreferences().put(CSM + hkey + TOOL_NAME + setCount+ '.' + toolCount, tool.getName());
                    getPreferences().put(CSM + hkey + TOOL_DISPLAYNAME + '-' + setCount+ '.' + toolCount, tool.getDisplayName());
                    getPreferences().putInt(CSM + hkey + TOOL_KIND + setCount+ '.' + toolCount, tool.getKind());
                    getPreferences().put(CSM + hkey + TOOL_PATH + setCount+ '.' + toolCount, tool.getPath());
                    getPreferences().put(CSM + hkey + TOOL_FLAVOR + setCount+ '.' + toolCount, tool.getFlavor().toString());
                    toolCount++;
                }
                setCount++;
            }
        }
    }

    public static CompilerSetManager restoreFromDisk(String hkey) {
        double version = getPreferences().getDouble(CSM + VERSION, 1.0);
        if (version == 1.0 && hkey.equals(LOCALHOST)) {
            return restoreFromDisk10();
        }

        int noSets = getPreferences().getInt(CSM + hkey + NO_SETS, -1);
        if (noSets < 0) {
            return null;
        }
        int pform = getPreferences().getInt(CSM + hkey + SET_PLATFORM, -1);
        if (pform < 0) {
            if (hkey.equals(LOCALHOST)) {
                pform = computeLocalPlatform();
            }
        }

        ArrayList<CompilerSet> css = new ArrayList<CompilerSet>();
        for (int setCount = 0; setCount < noSets; setCount++) {
            String setName = getPreferences().get(CSM + hkey + SET_NAME + setCount, null);
            String setFlavorName = getPreferences().get(CSM + hkey + SET_FLAVOR + setCount, null);
            CompilerFlavor flavor = null;
            if (setFlavorName != null) {
                flavor = CompilerFlavor.toFlavor(setFlavorName, pform);
            }
            String setDirectory = getPreferences().get(CSM + hkey + SET_DIRECTORY + setCount, null);
            if (setName == null || setFlavorName == null || flavor == null) {
                // FIXUP: error
                continue;
            }
            Boolean auto = getPreferences().getBoolean(CSM + hkey + SET_AUTO + setCount, false);
            Boolean isDefault = getPreferences().getBoolean(CSM + hkey + SET_DEFAULT + setCount, false);
            CompilerSet cs = new CompilerSet(flavor, setDirectory, setName);
            cs.setAutoGenerated(auto);
            cs.setAsDefault(isDefault);
            int noTools = getPreferences().getInt(CSM + hkey + NO_TOOLS + setCount, -1);
            for (int toolCount = 0; toolCount < noTools; toolCount++) {
                String toolName = getPreferences().get(CSM + hkey + TOOL_NAME + setCount + '.' + toolCount, null);
                String toolDisplayName = getPreferences().get(CSM + hkey + TOOL_DISPLAYNAME + '-' + setCount+ '.' + toolCount, null);
                int toolKind = getPreferences().getInt(CSM + hkey + TOOL_KIND + setCount + '.' + toolCount, -1);
                String toolPath = getPreferences().get(CSM + hkey + TOOL_PATH + setCount + '.' + toolCount, null);
                String toolFlavorName = getPreferences().get(CSM + hkey + TOOL_FLAVOR + setCount + '.' + toolCount, null);
                CompilerFlavor toolFlavor = null;
                if (toolFlavorName != null) {
                    toolFlavor = CompilerFlavor.toFlavor(toolFlavorName, pform);
                }
                Tool tool = getCompilerProvider().createCompiler(hkey, toolFlavor, toolKind, "", toolDisplayName, toolPath);
                tool.setName(toolName);
                cs.addTool(tool);
            }
            completeCompilerSet(hkey, cs, css);
            css.add(cs);
        }

        CompilerSetManager csm = new CompilerSetManager(hkey, css, pform);
        return csm;
    }

    public static CompilerSetManager restoreFromDisk10() {
        int noSets = getPreferences().getInt(CSM + NO_SETS, -1);
        if (noSets < 0) {
            return null;
        }

        ArrayList<CompilerSet> css = new ArrayList<CompilerSet>();
        getPreferences().remove(CSM + NO_SETS);
        for (int setCount = 0; setCount < noSets; setCount++) {
            String setName = getPreferences().get(CSM + SET_NAME + setCount, null);
            getPreferences().remove(CSM + SET_NAME + setCount);
            String setFlavorName = getPreferences().get(CSM + SET_FLAVOR + setCount, null);
            getPreferences().remove(CSM + SET_FLAVOR + setCount);
            CompilerFlavor flavor = null;
            if (setFlavorName != null) {
                flavor = CompilerFlavor.toFlavor(setFlavorName, PlatformTypes.getDefaultPlatform());
            }
            String setDirectory = getPreferences().get(CSM + SET_DIRECTORY + setCount, null);
            getPreferences().remove(CSM + SET_DIRECTORY + setCount);
            if (setName == null || setFlavorName == null || flavor == null) {
                // FIXUP: error
                continue;
            }
            Boolean auto = getPreferences().getBoolean(CSM + SET_AUTO + setCount, false);
            getPreferences().remove(CSM + SET_AUTO + setCount);
            CompilerSet cs = new CompilerSet(flavor, setDirectory, setName);
            cs.setAutoGenerated(auto);
            int noTools = getPreferences().getInt(CSM + NO_TOOLS + setCount, -1);
            getPreferences().remove(CSM + NO_TOOLS + setCount);
            for (int toolCount = 0; toolCount < noTools; toolCount++) {
                String toolName = getPreferences().get(CSM + TOOL_NAME + setCount + '.' + toolCount, null);
                String toolDisplayName = getPreferences().get(CSM + TOOL_DISPLAYNAME + '-' + setCount + '.' + toolCount, null);
                int toolKind = getPreferences().getInt(CSM + TOOL_KIND + setCount + '.' + toolCount, -1);
                String toolPath = getPreferences().get(CSM + TOOL_PATH + setCount + '.' + toolCount, null);
                String toolFlavorName = getPreferences().get(CSM + TOOL_FLAVOR + setCount + '.' + toolCount, null);
                getPreferences().remove(CSM + TOOL_NAME + setCount + '.' + toolCount);
                getPreferences().remove(CSM + TOOL_DISPLAYNAME + '-' + setCount + '.' + toolCount);
                getPreferences().remove(CSM + TOOL_KIND + setCount + '.' + toolCount);
                getPreferences().remove(CSM + TOOL_PATH + setCount + '.' + toolCount);
                getPreferences().remove(CSM + TOOL_FLAVOR + setCount + '.' + toolCount);
                CompilerFlavor toolFlavor = null;
                if (toolFlavorName != null) {
                    toolFlavor = CompilerFlavor.toFlavor(toolFlavorName, PlatformTypes.getDefaultPlatform());
                }
                Tool tool = getCompilerProvider().createCompiler(LOCALHOST, toolFlavor, toolKind, "", toolDisplayName, toolPath);
                tool.setName(toolName);
                cs.addTool(tool);
            }
            completeCompilerSet(CompilerSetManager.LOCALHOST, cs, css);
            css.add(cs);
        }
        CompilerSetManager csm = new CompilerSetManager(LOCALHOST, css, computeLocalPlatform());
        return csm;
    }

    /** Look up i18n strings here */
    private static String getString(String s) {
        return NbBundle.getMessage(CompilerSetManager.class, s);
    }

}
