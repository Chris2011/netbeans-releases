/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.versioning.util;

import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.keyring.Keyring;

/**
 * Bridge between VCSs and the Keyring API
 * 
 * @author ondra
 */
public class KeyringSupport {

    private static final Logger LOG = Logger.getLogger("versioning.util.KeyringSupport"); //NOI18N
    private static final boolean PRINT_PASSWORDS = "true".equals(System.getProperty("versioning.keyring.logpassword", "false")); //NOI18N
    private static final Set<String> NULL_HASHED_RECORDS = Collections.synchronizedSet(new HashSet<String>());

    /**
     * Saves password for a key constructed from keyPrefix and key
     * @param keyPrefix key prefix for each versioning system
     * @param key will be hashed and used with keyPrefix as a key for the keyring
     * @param password password, value will be nulled. If <code>null</code> the old record will be permanently deleted
     * @param description can be null
     */
    public static void save (String keyPrefix, String key, char[] password, String description) {
        String hashedKey = getKeyringKeyHashed(keyPrefix, key);
        if (password == null) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "Deleting password for {0}:{1}", new String[] {keyPrefix, key}); //NOI18N
            }
            Keyring.delete(getKeyringKey(keyPrefix, key));
            Keyring.delete(hashedKey);
            NULL_HASHED_RECORDS.add(hashedKey);
        } else {
            if (description == null) {
                description = key;
            }
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "Saving password for {0}:{1}", new String[] {keyPrefix, key}); //NOI18N
                if (PRINT_PASSWORDS) {
                    LOG.log(Level.FINEST, "Saving password: \"{0}\"", new String(password)); //NOI18N
                }
            }
            Keyring.save(getKeyringKey(keyPrefix, key), password, description);
            Keyring.delete(hashedKey);
            NULL_HASHED_RECORDS.remove(hashedKey);
        }
    }

    /**
     * Returns a stored password for the key constructed from keyPrefix and key
     * @param keyPrefix key prefix for each versioning system
     * @param key will be hashed and used with keyPrefix as a key for the keyring. If set to <code>null</code> only keyPrefix is used as the final key
     * @return stored password or null
     */
    public static char[] read (String keyPrefix, String key) {
        char[] retval = Keyring.read(getKeyringKey(keyPrefix, key));
        if (retval == null) {
            retval = migrateRecord(keyPrefix, key);
        }
        if (LOG.isLoggable(Level.FINE)) {
            if (retval == null) {
                LOG.log(Level.FINE, "No password for {0}:{1}", new String[] {keyPrefix, key}); //NOI18N
            } else if (PRINT_PASSWORDS) {
                LOG.log(Level.FINEST, "Getting password: {0}:{1} -- \"{2}\"", new Object[] { keyPrefix, key, new String(retval) }); //NOI18N
            }
        }
        return retval;
    }

    /**
     * package-private for tests
     */
    static String getKeyringKeyHashed (String keyPrefix, String keyToHash) {
        String keyPart = ""; //NOI18N
        if (keyToHash != null) {
            try {
                keyPart = Utils.getHash("SHA-1", keyToHash.getBytes()); //NOI18N
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(KeyringSupport.class.getName()).log(Level.INFO, null, ex);
                keyPart = keyToHash;
            }
        }
        return keyPrefix + keyPart;
    }

    /**
     * package-private for tests
     */
    static String getKeyringKey (String keyPrefix, String key) {
        return keyPrefix + key;
    }

    private static char[] migrateRecord (String keyPrefix, String key) {
        String hashedKey = getKeyringKeyHashed(keyPrefix, key);
        if (NULL_HASHED_RECORDS.contains(hashedKey)) {
            return null;
        }
        char[] password = Keyring.read(hashedKey);
        if (password != null) {
            String fullKey = getKeyringKey(keyPrefix, key);
            Keyring.save(fullKey, password.clone(), fullKey);
            Keyring.delete(hashedKey);
        }
        // the record is not present, do not try to migrate next time
        NULL_HASHED_RECORDS.add(hashedKey);
        return password;
    }
}
