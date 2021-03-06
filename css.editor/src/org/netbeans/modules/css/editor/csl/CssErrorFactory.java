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
package org.netbeans.modules.css.editor.csl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.netbeans.modules.csl.api.Error;
import org.netbeans.modules.csl.api.Severity;
import org.netbeans.modules.csl.spi.DefaultError;
import org.netbeans.modules.css.lib.api.FilterableError;
import org.openide.filesystems.FileObject;

/**
 *
 * @author marekfukala
 */
public class CssErrorFactory {

    public static FilterableError createError(String key, String displayName, String description, FileObject file, int start, int end, boolean lineError, Severity severity, Collection<FilterableError.SetFilterAction> enableFilter, FilterableError.SetFilterAction disableFilter) {
        return new CssDefaultError(key, displayName, description, file, start, end, lineError, severity, enableFilter, disableFilter);
    }
    
    public static FilterableError createError(String key, String displayName, String description, FileObject file, int start, int end, boolean lineError, Severity severity) {
        return createError(key, displayName, description, file, start, end, lineError, severity, Collections.<FilterableError.SetFilterAction>emptyList(), null);
    }
    
    private static class CssDefaultError extends DefaultError implements FilterableError, Error.Badging {

        private final Collection<SetFilterAction> enableFilter;
        private final SetFilterAction disableFilter;
        
        private CssDefaultError(String key, String displayName, String description, FileObject file, int start, int end, boolean lineError, Severity severity, Collection<SetFilterAction> enableFilter, SetFilterAction disableFilter) {
            super(key, displayName, description, file, start, end, lineError, severity);
            this.disableFilter = disableFilter;
            this.enableFilter = enableFilter;
        }

         @Override
        public boolean showExplorerBadge() {
            return getSeverity() == Severity.ERROR || getSeverity() == Severity.FATAL;
        }
        
        @Override
        public boolean isFiltered() {
            return disableFilter != null;
        }

        @Override
        public Collection<SetFilterAction> getEnableFilterActions() {
            return enableFilter;
        }

        @Override
        public SetFilterAction getDisableFilterAction() {
            return disableFilter;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final FilterableError other = (FilterableError) obj;
            if ((this.getDisplayName() == null) ? (other.getDisplayName() != null) : !this.getDisplayName().equals(other.getDisplayName())) {
                return false;
            }
            if ((this.getDescription() == null) ? (other.getDescription() != null) : !this.getDescription().equals(other.getDescription())) {
                return false;
            }
            if (this.getFile() != other.getFile() && (this.getFile() == null || !this.getFile().equals(other.getFile()))) {
                return false;
            }
            if (this.getStartPosition() != other.getStartPosition()) {
                return false;
            }
            if (this.getEndPosition() != other.getEndPosition()) {
                return false;
            }
            if (this.isLineError() != other.isLineError()) {
                return false;
            }
            if ((this.getKey() == null) ? (other.getKey() != null) : !this.getKey().equals(other.getKey())) {
                return false;
            }
            if (this.getSeverity() != other.getSeverity()) {
                return false;
            }
            if (this.isFiltered()!= other.isFiltered()) {
                return false;
            }
            
            if (!Arrays.deepEquals(this.getParameters(), other.getParameters())) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 29 * hash + (this.getDisplayName() != null ? this.getDisplayName().hashCode() : 0);
            hash = 29 * hash + (this.getDescription() != null ? this.getDescription().hashCode() : 0);
            hash = 29 * hash + (this.getFile() != null ? this.getFile().hashCode() : 0);
            hash = 29 * hash + this.getStartPosition();
            hash = 29 * hash + this.getEndPosition();
            hash = 29 * hash + (this.isLineError() ? 1 : 0);
            hash = 29 * hash + (this.getKey() != null ? this.getKey().hashCode() : 0);
            hash = 29 * hash + (this.getSeverity() != null ? this.getSeverity().hashCode() : 0);
            hash = 29 * hash + (this.isFiltered() ? 1 : 0);
            hash = 29 * hash + Arrays.deepHashCode(this.getParameters());
            return hash;
        }

        @Override
        public String toString() {
            return super.toString() + " (file:" + getFile() + ", from:" + getStartPosition() + ", to:" + getEndPosition() + ")";
        }
        
        
    }
}
