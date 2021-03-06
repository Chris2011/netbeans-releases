/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
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
 */
package org.netbeans.modules.php.twig.editor.typinghooks;

public class TwigTypedTextInterceptorTest extends TwigTypinghooksTestBase {

    public TwigTypedTextInterceptorTest(String testName) {
        super(testName);
    }

    private void insertChar(String original, char insertText, String expected) throws Exception {
        insertChar(original, insertText, expected, null);
    }

    private void insertChar(String original, char insertText, String expected, String selection) throws Exception {
        insertChar(original, insertText, expected, selection, false);
    }

    // surround with quotes or brackets
    public void testSurroundWithDoubleQuoteInBlock_01() throws Exception {
        String original = "{% test^ %}";
        String expected = "{% \"test\"^ %}";
        insertChar(original, '"', expected, "test");
    }

    public void testSurroundWithDoubleQuoteInBlock_02() throws Exception {
        String original = "{% test^ %}";
        String expected = "{% te\"st\"^ %}";
        insertChar(original, '"', expected, "st");
    }

    public void testSurroundWithDoubleQuoteInBlock_03() throws Exception {
        String original = "{% 'test'^ %}";
        String expected = "{% \"test\"^ %}";
        insertChar(original, '"', expected, "'test'");
    }

    public void testSurroundWithSingleQuoteInBlock_01() throws Exception {
        String original = "{% test^ %}";
        String expected = "{% 'test'^ %}";
        insertChar(original, '\'', expected, "test");
    }

    public void testSurroundWithSingleQuoteInBlock_02() throws Exception {
        String original = "{% test^ %}";
        String expected = "{% te'st'^ %}";
        insertChar(original, '\'', expected, "st");
    }

    public void testSurroundWithSingleQuoteInBlock_03() throws Exception {
        String original = "{% \"test\"^ %}";
        String expected = "{% 'test'^ %}";
        insertChar(original, '\'', expected, "\"test\"");
    }

    public void testSurroundWithCurlyInBlock_01() throws Exception {
        String original = "{% test^ %}";
        String expected = "{% {test}^ %}";
        insertChar(original, '{', expected, "test");
    }

    public void testSurroundWithCurlyInBlock_02() throws Exception {
        String original = "{% test^ %}";
        String expected = "{% te{st}^ %}";
        insertChar(original, '{', expected, "st");
    }

    public void testSurroundWithCurlyInBlock_03() throws Exception {
        String original = "{% \"test\"^ %}";
        String expected = "{% {test}^ %}";
        insertChar(original, '{', expected, "\"test\"");
    }

    public void testSurroundWithParenthesisInBlock_01() throws Exception {
        String original = "{% test^ %}";
        String expected = "{% (test)^ %}";
        insertChar(original, '(', expected, "test");
    }

    public void testSurroundWithParenthesisInBlock_02() throws Exception {
        String original = "{% test^ %}";
        String expected = "{% te(st)^ %}";
        insertChar(original, '(', expected, "st");
    }

    public void testSurroundWithParenthesisInBlock_03() throws Exception {
        String original = "{% \"test\"^ %}";
        String expected = "{% (test)^ %}";
        insertChar(original, '(', expected, "\"test\"");
    }

    public void testSurroundWithBracketInBlock_01() throws Exception {
        String original = "{% test^ %}";
        String expected = "{% [test]^ %}";
        insertChar(original, '[', expected, "test");
    }

    public void testSurroundWithBracketInBlock_02() throws Exception {
        String original = "{% tes^t %}";
        String expected = "{% t[es]^t %}";
        insertChar(original, '[', expected, "es");
    }

    public void testSurroundWithBracketInBlock_03() throws Exception {
        String original = "{% \"test\"^ %}";
        String expected = "{% [test]^ %}";
        insertChar(original, '[', expected, "\"test\"");
    }

    // complete quotes and brackets
    public void testInsertDoubleQuoteInBlock_01() throws Exception {
        String original = "{% ^ %}";
        String expected = "{% \"^\" %}";
        insertChar(original, '"', expected);
    }

    public void testInsertDoubleQuoteInBlock_02() throws Exception {
        String original = "{% \"^ %}";
        String expected = "{% \"\"^ %}";
        insertChar(original, '"', expected);
    }

    public void testInsertDoubleQuoteInBlock_03() throws Exception {
        String original = "{% ^\" %}";
        String expected = "{% \"^\" %}";
        insertChar(original, '"', expected);
    }

    public void testInsertDoubleQuoteInBlock_04() throws Exception {
        String original = "{% \"test^ %}";
        String expected = "{% \"test\"^ %}";
        insertChar(original, '"', expected);
    }

    public void testInsertDoubleQuoteInBlock_05() throws Exception {
        String original = "{% ^test\" %}";
        String expected = "{% \"^test\" %}";
        insertChar(original, '"', expected);
    }

    public void testInsertDoubleQuoteInBlock_06() throws Exception {
        String original = "{% \"test\" %}^";
        String expected = "{% \"test\" %}\"^";
        insertChar(original, '"', expected);
    }

    public void testInsertDoubleQuoteInBlock_07() throws Exception {
        String original = "{%^%}";
        String expected = "{%\"^\"%}";
        insertChar(original, '"', expected);
    }

    public void testInsertSingleQuoteInBlock_01() throws Exception {
        String original = "{% ^ %}";
        String expected = "{% '^' %}";
        insertChar(original, '\'', expected);
    }

    public void testInsertSingleQuoteInBlock_02() throws Exception {
        String original = "{% '^ %}";
        String expected = "{% ''^ %}";
        insertChar(original, '\'', expected);
    }

    public void testInsertSingleQuoteInBlock_03() throws Exception {
        String original = "{% ^' %}";
        String expected = "{% '^' %}";
        insertChar(original, '\'', expected);
    }

    public void testInsertSingleQuoteInBlock_04() throws Exception {
        String original = "{% 'test^ %}";
        String expected = "{% 'test'^ %}";
        insertChar(original, '\'', expected);
    }

    public void testInsertSingleQuoteInBlock_05() throws Exception {
        String original = "{% ^test' %}";
        String expected = "{% '^test' %}";
        insertChar(original, '\'', expected);
    }

    public void testInsertSingleQuoteInBlock_06() throws Exception {
        String original = "{% 'test' %}^";
        String expected = "{% 'test' %}'^";
        insertChar(original, '\'', expected);
    }

    public void testInsertSingleQuoteInBlock_07() throws Exception {
        String original = "{%^%}";
        String expected = "{%'^'%}";
        insertChar(original, '\'', expected);
    }

    public void testInsertCurlyInBlock_01() throws Exception {
        String original = "{% ^ %}";
        String expected = "{% {^} %}";
        insertChar(original, '{', expected);
    }

    public void testInsertCurlyInBlock_02() throws Exception {
        String original = "{%^%}";
        String expected = "{%{^}%}";
        insertChar(original, '{', expected);
    }

    public void testInsertParenthesisInBlock_01() throws Exception {
        String original = "{% ^ %}";
        String expected = "{% (^) %}";
        insertChar(original, '(', expected);
    }

    public void testInsertParenthesisInBlock_02() throws Exception {
        String original = "{%^%}";
        String expected = "{%(^)%}";
        insertChar(original, '(', expected);
    }

    public void testInsertBracketInBlock_01() throws Exception {
        String original = "{% ^ %}";
        String expected = "{% [^] %}";
        insertChar(original, '[', expected);
    }

    public void testInsertBracketInBlock_02() throws Exception {
        String original = "{%^%}";
        String expected = "{%[^]%}";
        insertChar(original, '[', expected);
    }

    // skip closing bracket
    public void testSkipClosingCurlyInBlock_01() throws Exception {
        String original = "{% {^} %}";
        String expected = "{% {}^ %}";
        insertChar(original, '}', expected);
    }

    public void testSkipClosingCurlyInBlock_02() throws Exception {
        String original = "{% {'foo' : {^}} %}";
        String expected = "{% {'foo' : {}^} %}";
        insertChar(original, '}', expected);
    }

    public void testSkipClosingCurlyInBlock_03() throws Exception {
        String original = "{% {'foo' : {^}} { %}";
        String expected = "{% {'foo' : {}^} { %}";
        insertChar(original, '}', expected);
    }

    public void testSkipClosingParenthesisInBlock_01() throws Exception {
        String original = "{% (^) %}";
        String expected = "{% ()^ %}";
        insertChar(original, ')', expected);
    }

    public void testSkipClosingParenthesisInBlock_02() throws Exception {
        String original = "{% ((^)) %}";
        String expected = "{% (()^) %}";
        insertChar(original, ')', expected);
    }

    public void testSkipClosingParenthesisInBlock_03() throws Exception {
        String original = "{% ((^)) ( %}";
        String expected = "{% (()^) ( %}";
        insertChar(original, ')', expected);
    }

    public void testSkipClosingBracketInBlock_01() throws Exception {
        String original = "{% [^] %}";
        String expected = "{% []^ %}";
        insertChar(original, ']', expected);
    }

    public void testSkipClosingBracketInBlock_02() throws Exception {
        String original = "{% [[^]] %}";
        String expected = "{% [[]^] %}";
        insertChar(original, ']', expected);
    }

    public void testSkipClosingBracketInBlock_03() throws Exception {
        String original = "{% [[(^]] [ %}";
        String expected = "{% [[(]^] [ %}";
        insertChar(original, ']', expected);
    }

    // missing closing bracket
    public void testMissingClosingCurlyInBlock_01() throws Exception {
        String original = "{% {'foo' : {^} %}";
        String expected = "{% {'foo' : {}^} %}";
        insertChar(original, '}', expected);
    }

    public void testMissingClosingCurlyInBlock_02() throws Exception {
        String original = "{% {'foo' : {}^ %}";
        String expected = "{% {'foo' : {}}^ %}";
        insertChar(original, '}', expected);
    }

    public void testMissingClosingCurlyInBlock_03() throws Exception {
        String original = "{% {'foo' : {}^ { %}";
        String expected = "{% {'foo' : {}}^ { %}";
        insertChar(original, '}', expected);
    }

    public void testMissingClosingParenthesisInBlock_01() throws Exception {
        String original = "{% ((^) %}";
        String expected = "{% (()^) %}";
        insertChar(original, ')', expected);
    }

    public void testMissingClosingParenthesisInBlock_02() throws Exception {
        String original = "{% (()^ %}";
        String expected = "{% (())^ %}";
        insertChar(original, ')', expected);
    }

    public void testMissingClosingParenthesisInBlock_03() throws Exception {
        String original = "{% (()^ () %}";
        String expected = "{% (())^ () %}";
        insertChar(original, ')', expected);
    }

    public void testMissingClosingBracketInBlock_01() throws Exception {
        String original = "{% [[^] %}";
        String expected = "{% [[]^] %}";
        insertChar(original, ']', expected);
    }

    public void testMissingClosingBracketInBlock_02() throws Exception {
        String original = "{% [[]^ %}";
        String expected = "{% [[]]^ %}";
        insertChar(original, ']', expected);
    }

    public void testMissingClosingBracketInBlock_03() throws Exception {
        String original = "{% [[]^ [] %}";
        String expected = "{% [[]]^ [] %}";
        insertChar(original, ']', expected);
    }

    // Variable
    // surround with quotes or brackets
    public void testSurroundWithDoubleQuoteInVariable_01() throws Exception {
        String original = "{{ test^ }}";
        String expected = "{{ \"test\"^ }}";
        insertChar(original, '"', expected, "test");
    }

    public void testSurroundWithDoubleQuoteInVariable_02() throws Exception {
        String original = "{{ test^ }}";
        String expected = "{{ te\"st\"^ }}";
        insertChar(original, '"', expected, "st");
    }

    public void testSurroundWithDoubleQuoteInVariable_03() throws Exception {
        String original = "{{ 'test'^ }}";
        String expected = "{{ \"test\"^ }}";
        insertChar(original, '"', expected, "'test'");
    }

    public void testSurroundWithSingleQuoteInVariable_01() throws Exception {
        String original = "{{ test^ }}";
        String expected = "{{ 'test'^ }}";
        insertChar(original, '\'', expected, "test");
    }

    public void testSurroundWithSingleQuoteInVariable_02() throws Exception {
        String original = "{{ test^ }}";
        String expected = "{{ te'st'^ }}";
        insertChar(original, '\'', expected, "st");
    }

    public void testSurroundWithSingleQuoteInVariable_03() throws Exception {
        String original = "{{ \"test\"^ }}";
        String expected = "{{ 'test'^ }}";
        insertChar(original, '\'', expected, "\"test\"");
    }

    public void testSurroundWithCurlyInVariable_01() throws Exception {
        String original = "{{ test^ }}";
        String expected = "{{ {test}^ }}";
        insertChar(original, '{', expected, "test");
    }

    public void testSurroundWithCurlyInVariable_02() throws Exception {
        String original = "{{ test^ }}";
        String expected = "{{ te{st}^ }}";
        insertChar(original, '{', expected, "st");
    }

    public void testSurroundWithCurlyInVariable_03() throws Exception {
        String original = "{{ \"test\"^ }}";
        String expected = "{{ {test}^ }}";
        insertChar(original, '{', expected, "\"test\"");
    }

    public void testSurroundWithParenthesisInVariable_01() throws Exception {
        String original = "{{ test^ }}";
        String expected = "{{ (test)^ }}";
        insertChar(original, '(', expected, "test");
    }

    public void testSurroundWithParenthesisInVariable_02() throws Exception {
        String original = "{{ test^ }}";
        String expected = "{{ te(st)^ }}";
        insertChar(original, '(', expected, "st");
    }

    public void testSurroundWithParenthesisInVariable_03() throws Exception {
        String original = "{{ \"test\"^ }}";
        String expected = "{{ (test)^ }}";
        insertChar(original, '(', expected, "\"test\"");
    }

    public void testSurroundWithBracketInVariable_01() throws Exception {
        String original = "{{ test^ }}";
        String expected = "{{ [test]^ }}";
        insertChar(original, '[', expected, "test");
    }

    public void testSurroundWithBracketInVariable_02() throws Exception {
        String original = "{{ tes^t }}";
        String expected = "{{ t[es]^t }}";
        insertChar(original, '[', expected, "es");
    }

    public void testSurroundWithBracketInVariable_03() throws Exception {
        String original = "{{ \"test\"^ }}";
        String expected = "{{ [test]^ }}";
        insertChar(original, '[', expected, "\"test\"");
    }

    // complete quotes and brackets
    public void testInsertDoubleQuoteInVariable_01() throws Exception {
        String original = "{{ ^ }}";
        String expected = "{{ \"^\" }}";
        insertChar(original, '"', expected);
    }

    public void testInsertDoubleQuoteInVariable_02() throws Exception {
        String original = "{{ \"^ }}";
        String expected = "{{ \"\"^ }}";
        insertChar(original, '"', expected);
    }

    public void testInsertDoubleQuoteInVariable_03() throws Exception {
        String original = "{{ ^\" }}";
        String expected = "{{ \"^\" }}";
        insertChar(original, '"', expected);
    }

    public void testInsertDoubleQuoteInVariable_04() throws Exception {
        String original = "{{ \"test^ }}";
        String expected = "{{ \"test\"^ }}";
        insertChar(original, '"', expected);
    }

    public void testInsertDoubleQuoteInVariable_05() throws Exception {
        String original = "{{ ^test\" }}";
        String expected = "{{ \"^test\" }}";
        insertChar(original, '"', expected);
    }

    public void testInsertDoubleQuoteInVariable_06() throws Exception {
        String original = "{{ \"test\" }}^";
        String expected = "{{ \"test\" }}\"^";
        insertChar(original, '"', expected);
    }

    public void testInsertDoubleQuoteInVariable_07() throws Exception {
        String original = "{{^}}";
        String expected = "{{\"^\"}}";
        insertChar(original, '"', expected);
    }

    public void testInsertSingleQuoteInVariable_01() throws Exception {
        String original = "{{ ^ }}";
        String expected = "{{ '^' }}";
        insertChar(original, '\'', expected);
    }

    public void testInsertSingleQuoteInVariable_02() throws Exception {
        String original = "{{ '^ }}";
        String expected = "{{ ''^ }}";
        insertChar(original, '\'', expected);
    }

    public void testInsertSingleQuoteInVariable_03() throws Exception {
        String original = "{{ ^' }}";
        String expected = "{{ '^' }}";
        insertChar(original, '\'', expected);
    }

    public void testInsertSingleQuoteInVariable_04() throws Exception {
        String original = "{{ 'test^ }}";
        String expected = "{{ 'test'^ }}";
        insertChar(original, '\'', expected);
    }

    public void testInsertSingleQuoteInVariable_05() throws Exception {
        String original = "{{ ^test' }}";
        String expected = "{{ '^test' }}";
        insertChar(original, '\'', expected);
    }

    public void testInsertSingleQuoteInVariable_06() throws Exception {
        String original = "{{ 'test' }}^";
        String expected = "{{ 'test' }}'^";
        insertChar(original, '\'', expected);
    }

    public void testInsertSingleQuoteInVariable_07() throws Exception {
        String original = "{{^}}";
        String expected = "{{'^'}}";
        insertChar(original, '\'', expected);
    }

    public void testInsertCurlyInVariable_01() throws Exception {
        String original = "{{ ^ }}";
        String expected = "{{ {^} }}";
        insertChar(original, '{', expected);
    }

    public void testInsertCurlyInVariable_02() throws Exception {
        String original = "{{^}}";
        String expected = "{{{^}}}";
        insertChar(original, '{', expected);
    }

    public void testInsertParenthesisInVariable_01() throws Exception {
        String original = "{{ ^ }}";
        String expected = "{{ (^) }}";
        insertChar(original, '(', expected);
    }

    public void testInsertParenthesisInVariable_02() throws Exception {
        String original = "{{^}}";
        String expected = "{{(^)}}";
        insertChar(original, '(', expected);
    }

    public void testInsertBracketInVariable_01() throws Exception {
        String original = "{{ ^ }}";
        String expected = "{{ [^] }}";
        insertChar(original, '[', expected);
    }

    public void testInsertBracketInVariable_02() throws Exception {
        String original = "{{^}}";
        String expected = "{{[^]}}";
        insertChar(original, '[', expected);
    }

    // skip closing bracket
    // curly can be used in variable?
    public void testSkipClosingCurlyInVariable_01() throws Exception {
        String original = "{{ {^} }}";
        String expected = "{{ {}^ }}";
        insertChar(original, '}', expected);
    }

    public void testSkipClosingCurlyInVariable_02() throws Exception {
        String original = "{{ {'foo' : {^}} }}";
        String expected = "{{ {'foo' : {}^} }}";
        insertChar(original, '}', expected);
    }

    public void testSkipClosingCurlyInVariable_03() throws Exception {
        String original = "{{ {'foo' : {^}} { }}";
        String expected = "{{ {'foo' : {}^} { }}";
        insertChar(original, '}', expected);
    }

    public void testSkipClosingParenthesisInVariable_01() throws Exception {
        String original = "{{ (^) }}";
        String expected = "{{ ()^ }}";
        insertChar(original, ')', expected);
    }

    public void testSkipClosingParenthesisInVariable_02() throws Exception {
        String original = "{{ ((^)) }}";
        String expected = "{{ (()^) }}";
        insertChar(original, ')', expected);
    }

    public void testSkipClosingParenthesisInVariable_03() throws Exception {
        String original = "{{ ((^)) ( }}";
        String expected = "{{ (()^) ( }}";
        insertChar(original, ')', expected);
    }

    public void testSkipClosingBracketInVariable_01() throws Exception {
        String original = "{{ [^] }}";
        String expected = "{{ []^ }}";
        insertChar(original, ']', expected);
    }

    public void testSkipClosingBracketInVariable_02() throws Exception {
        String original = "{{ [[^]] }}";
        String expected = "{{ [[]^] }}";
        insertChar(original, ']', expected);
    }

    public void testSkipClosingBracketInVariable_03() throws Exception {
        String original = "{{ [[(^]] [ }}";
        String expected = "{{ [[(]^] [ }}";
        insertChar(original, ']', expected);
    }

    // missing closing bracket
    public void testMissingClosingCurlyInVariable_01() throws Exception {
        String original = "{{ {'foo' : {^} }}";
        String expected = "{{ {'foo' : {}^} }}";
        insertChar(original, '}', expected);
    }

    public void testMissingClosingCurlyInVariable_02() throws Exception {
        String original = "{{ {'foo' : {}^ }}";
        String expected = "{{ {'foo' : {}}^ }}";
        insertChar(original, '}', expected);
    }

    public void testMissingClosingCurlyInVariable_03() throws Exception {
        String original = "{{ {'foo' : {}^ { }}";
        String expected = "{{ {'foo' : {}}^ { }}";
        insertChar(original, '}', expected);
    }

    public void testMissingClosingParenthesisInVariable_01() throws Exception {
        String original = "{{ ((^) }}";
        String expected = "{{ (()^) }}";
        insertChar(original, ')', expected);
    }

    public void testMissingClosingParenthesisInVariable_02() throws Exception {
        String original = "{{ (()^ }}";
        String expected = "{{ (())^ }}";
        insertChar(original, ')', expected);
    }

    public void testMissingClosingParenthesisInVariable_03() throws Exception {
        String original = "{{ (()^ () }}";
        String expected = "{{ (())^ () }}";
        insertChar(original, ')', expected);
    }

    public void testMissingClosingBracketInVariable_01() throws Exception {
        String original = "{{ [[^] }}";
        String expected = "{{ [[]^] }}";
        insertChar(original, ']', expected);
    }

    public void testMissingClosingBracketInVariable_02() throws Exception {
        String original = "{{ [[]^ }}";
        String expected = "{{ [[]]^ }}";
        insertChar(original, ']', expected);
    }

    public void testMissingClosingBracketInVariable_03() throws Exception {
        String original = "{{ [[]^ [] }}";
        String expected = "{{ [[]]^ [] }}";
        insertChar(original, ']', expected);
    }

    public void testInsertCurlyInHtml() throws Exception {
        String original = "{% {} %}^";
        String expected = "{% {} %}{^";
        insertChar(original, '{', expected);
    }

    public void testInsertParenthesisInHtml() throws Exception {
        String original = "{% () %}^";
        String expected = "{% () %}(^";
        insertChar(original, '(', expected);
    }

    public void testInsertBracketInHtml() throws Exception {
        String original = "{% [] %}^";
        String expected = "{% [] %}[^";
        insertChar(original, '[', expected);
    }

    public void testCurlyInHtml() throws Exception {
        String original = "<html><body>^</body></html>";
        String expected = "<html><body>{^</body></html>";
        insertChar(original, '{', expected);
    }

    public void testVariableInHtml() throws Exception {
        String original = "<html><body>{^</body></html>";
        String expected = "<html><body>{{ ^ }}</body></html>";
        insertChar(original, '{', expected);
    }

    public void testBlockInHtml() throws Exception {
        String original = "<html><body>{^</body></html>";
        String expected = "<html><body>{% ^ %}</body></html>";
        insertChar(original, '%', expected);
    }

    public void testCurlyInHtmlAttribute() throws Exception {
        String original
                = "<html>\n"
                + "    <body>\n"
                + "        <ul id=\"navigation\">\n"
                + "        {% for item in navigation %}\n"
                + "            <li><a href=\"^\">{{ item.caption }}</a></li>\n" // here
                + "        {% endfor %}\n"
                + "        </ul>\n"
                + "    </body>\n"
                + "</html>";
        String expected
                = "<html>\n"
                + "    <body>\n"
                + "        <ul id=\"navigation\">\n"
                + "        {% for item in navigation %}\n"
                + "            <li><a href=\"{^\">{{ item.caption }}</a></li>\n" // here
                + "        {% endfor %}\n"
                + "        </ul>\n"
                + "    </body>\n"
                + "</html>";
        insertChar(original, '{', expected);
    }

    public void testVariableInHtmlAttribute() throws Exception {
        String original
                = "<html>\n"
                + "    <body>\n"
                + "        <ul id=\"navigation\">\n"
                + "        {% for item in navigation %}\n"
                + "            <li><a href=\"{^\">{{ item.caption }}</a></li>\n" // here
                + "        {% endfor %}\n"
                + "        </ul>\n"
                + "    </body>\n"
                + "</html>";
        String expected
                = "<html>\n"
                + "    <body>\n"
                + "        <ul id=\"navigation\">\n"
                + "        {% for item in navigation %}\n"
                + "            <li><a href=\"{{ ^ }}\">{{ item.caption }}</a></li>\n" // here
                + "        {% endfor %}\n"
                + "        </ul>\n"
                + "    </body>\n"
                + "</html>";
        insertChar(original, '{', expected);
    }

    public void testCurlyInTwigBlock() throws Exception {
        String original
                = "<html>\n"
                + "    <body>\n"
                + "        <ul id=\"navigation\">\n"
                + "        {% for item in navigation %}\n"
                + "            ^\n" // here
                + "        {% endfor %}\n"
                + "        </ul>\n"
                + "    </body>\n"
                + "</html>";
        String expected
                = "<html>\n"
                + "    <body>\n"
                + "        <ul id=\"navigation\">\n"
                + "        {% for item in navigation %}\n"
                + "            {^\n" // here
                + "        {% endfor %}\n"
                + "        </ul>\n"
                + "    </body>\n"
                + "</html>";
        insertChar(original, '{', expected);
    }

    public void testVariableInTwigBlock() throws Exception {
        String original
                = "<html>\n"
                + "    <body>\n"
                + "        <ul id=\"navigation\">\n"
                + "        {% for item in navigation %}\n"
                + "            {^\n" // here
                + "        {% endfor %}\n"
                + "        </ul>\n"
                + "    </body>\n"
                + "</html>";
        String expected
                = "<html>\n"
                + "    <body>\n"
                + "        <ul id=\"navigation\">\n"
                + "        {% for item in navigation %}\n"
                + "            {{ ^ }}\n" // here
                + "        {% endfor %}\n"
                + "        </ul>\n"
                + "    </body>\n"
                + "</html>";
        insertChar(original, '{', expected);
    }

    public void testBlockInTwigBlock() throws Exception {
        String original
                = "<html>\n"
                + "    <body>\n"
                + "        <ul id=\"navigation\">\n"
                + "        {% for item in navigation %}\n"
                + "            {^\n" // here
                + "        {% endfor %}\n"
                + "        </ul>\n"
                + "    </body>\n"
                + "</html>";
        String expected
                = "<html>\n"
                + "    <body>\n"
                + "        <ul id=\"navigation\">\n"
                + "        {% for item in navigation %}\n"
                + "            {% ^ %}\n" // here
                + "        {% endfor %}\n"
                + "        </ul>\n"
                + "    </body>\n"
                + "</html>";
        insertChar(original, '%', expected);
    }

}
