/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only (GPL) or the Common
 * Development and Distribution License(CDDL) (collectively, the
 * License). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the Classpath exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * Portions Copyrighted [year] [name of copyright owner]
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
package org.netbeans.modules.css.lib.api;

/**
 * This enum should be updated once the ANTLR grammar has changed the set of rules.
 * 
 * It can be regexped from the generated Css3Parser.ruleNames field
 *
 * @author mfukala@netbeans.org
 */
public enum NodeType {
    
        attrib, 
        attrvalue, 
        attribute, 
        attrname, 
        bodylist, 
        bodyset, 
        combinator, 
        cssId, 
        cssClass, 
        declaration, 
        declarations, 
        elementSubsequent, 
        elementName, 
        esPred, 
        expr, 
        function, 
        function_name, 
        charSet, 
        imports, 
        invalidRule, 
        hexColor, 
        media, 
        mediaList,
        medium, 
        
        /**
         * Namespace declaration: @namespace prefix "http://myns";
         */
        namespace, 
        
        /**
         * Namespace prefix or wildcard prefix of the css qualified name: myns|E {} || *|E {}
         */
        namespace_wqname_prefix,
        
        /**
         * The namespace prefix
         */
        namespace_prefix,
        
        /**
         * The wildcard namespace prefix
         */
        namespace_wildcard_prefix,
        
        /**
         * syntactic predicate
         */
        nsPred, 
        operator, 
        page, 
        pseudoPage, 
        property, 
        prio, 
        pseudo, 
        
        /**
         * String or URI, used in namespace and import rules
         */
        resourceIdentifier,
        ruleSet, 
        selector, 
        simpleSelectorSequence, 
        synpred2_Css3, 
        selectorsGroup, 
        styleSheet, 
        /**
         * syntactic predicate
         */
        synpred1_Css3, 
        /**
         * syntactic predicate
         */
        synpred3_Css3, 
        term, 
        typeSelector, 
                
        //following node types don't correspond to any of the grammar rules (do not remove them!):
        root, //an artificial root node of each parse tree
        error, //an error node
        recovery, //an error node, but for errors recovered - skipped (resynced) content by syncToBitSet(...)
        token, //a token node
        unaryOperator;
        
}
