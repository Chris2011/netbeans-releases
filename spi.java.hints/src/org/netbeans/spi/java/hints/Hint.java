/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2008-2010 Sun Microsystems, Inc.
 */

package org.netbeans.spi.java.hints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.prefs.Preferences;
import javax.swing.JComponent;

/** Description of a hint.
 * When applied to a class, any enclosed method marked with a trigger
 * will be considered to be part of this hint. When applied to a method, only this specific
 * method will be considered to the part of the hint.
 * Currently recognized triggers include {@link TriggerPattern} and {@link TriggerTreeKind}.
 * @author lahvac, Petr Hrebejk
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface Hint {
    /**Manually specify the hint's id. Use only reorganizing code to keep compatibility with settings
     * from previous version. Id will be generated automatically is not specified.
     */
    public String id() default "";
    /** The hint's display name.
     */
    public String displayName();
    /** The hint's long description.
     */
    public String description();
    /**Category where the hint belongs.
     */
    public String category();
    /**Should the hint be enabled by default?*/
    public boolean enabled() default true;
    /**Default severity of the hint*/
    public Severity severity() default Severity.WARNING;
    /**Suppress warnings keys that should automatically suppress the hint.*/
    public String[] suppressWarnings() default {};
    /**A customizer that allows to customize hint's preferences. The given class should have
     * a publicly accessible constructor, taking one parameter of type {@link Preferences}.
     */
    public Class<? extends JComponent> customizerProvider() default JComponent.class;
    /**Whether the hint should be considered a {@link Kind#HINT hint}, e.g. it detects a code smell,
     * or otherwise leads to improving the code, or a {@link Kind#SUGGESTION}, which is simply
     * an offer to do automatically do something for the user.
     */
    public Kind hintKind() default Kind.HINT;
    /**Specify various options for the hint*/
    public Options[] options() default {};

    /**Whether the hint should be considered a {@link Kind#HINT hint}, e.g. it
     * detects a code smell, or otherwise leads to improving the code, or a {@link Kind#SUGGESTION},
     * which is simply an offer to do automatically do something for the user.
     */
   public enum Kind {
       /**The hint represents a code-smell detector, or alike. It marks code that
        * is not correct (in some sense).
        */
       HINT,
       
       /**The hint represents an offer to the user to automatically alter the code.
        * The transformation is not intended to improve the code, only allow the
        * user to do some kind of code transformation quickly.
        *
        * The only meaningful severity for suggestions if {@link Severity#CURRENT_LINE_WARNING}.
        */
       SUGGESTION;
    }

   /**Various options to altering the behavior of the hint.
    */
    public enum Options {
        /**The hint does not produce any automatic transformations that could be run
         * inside the Inspect&Refactor dialog.
         */
        QUERY,
        /**The hint cannot be run inside the Inspect&Refactor dialog.
         */
        NO_BATCH;
    }

    /** Severity of hint
     *  <li><code>ERROR</code>  - will show up as error
     *  <li><code>WARNING</code>  - will show up as warning
     *  <li><code>CURRENT_LINE_WARNING</code>  - will only show up when the caret is placed in the erroneous element
     * @author Petr Hrebejk
     */
    public enum Severity {
        /**Will show as an error*/
        ERROR,
        /**Will show as a warning*/
        WARNING,
        /**Will be shown only for the line with the caret*/
        CURRENT_LINE_WARNING;

        public org.netbeans.spi.editor.hints.Severity toEditorSeverity() {
            switch ( this ) {
                case ERROR:
                    return org.netbeans.spi.editor.hints.Severity.ERROR;
                case WARNING:
                    return org.netbeans.spi.editor.hints.Severity.VERIFIER;
                case CURRENT_LINE_WARNING:
                    return org.netbeans.spi.editor.hints.Severity.HINT;
                default:
                    return null;
            }
        }
    }
}
