/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.html.editor.indent;

import javax.swing.text.Document;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.mimelookup.test.MockMimeLookup;
import org.netbeans.api.html.lexer.HTMLTokenId;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.jsp.lexer.JspTokenId;
import org.netbeans.api.lexer.Language;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.csl.api.Formatter;
import org.netbeans.modules.css.editor.indent.CssIndentTaskFactory;
import org.netbeans.modules.css.formatting.api.support.AbstractIndenter;
import org.netbeans.modules.css.lexer.api.CssTokenId;
import org.netbeans.modules.html.editor.HtmlKit;
import org.netbeans.modules.html.editor.test.TestBase2;
import org.netbeans.modules.java.source.save.Reformatter;
import org.netbeans.modules.web.core.syntax.EmbeddingProviderImpl;
import org.netbeans.modules.web.core.syntax.JspKit;
import org.netbeans.modules.web.core.syntax.indent.JspIndentTaskFactory;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;

public class HtmlIndenterTest extends TestBase2 {

    public HtmlIndenterTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        AbstractIndenter.inUnitTestRun = true;

        CssIndentTaskFactory cssFactory = new CssIndentTaskFactory();
        MockMimeLookup.setInstances(MimePath.parse("text/x-css"), cssFactory, CssTokenId.language());
        JspIndentTaskFactory jspReformatFactory = new JspIndentTaskFactory();
        MockMimeLookup.setInstances(MimePath.parse("text/x-jsp"), new JspKit("text/x-jsp"), jspReformatFactory, new EmbeddingProviderImpl.Factory(), JspTokenId.language());
        HtmlIndentTaskFactory htmlReformatFactory = new HtmlIndentTaskFactory();
        MockMimeLookup.setInstances(MimePath.parse("text/html"), htmlReformatFactory, new HtmlKit("text/html"), HTMLTokenId.language());
        Reformatter.Factory factory = new Reformatter.Factory();
        MockMimeLookup.setInstances(MimePath.parse("text/x-java"), factory, JavaTokenId.language());
    }

    @Override
    protected BaseDocument getDocument(FileObject fo, String mimeType, Language language) {
        // for some reason GsfTestBase is not using DataObjects for BaseDocument construction
        // which means that for example Java formatter which does call EditorCookie to retrieve
        // document will get difference instance of BaseDocument for indentation
        try {
             DataObject dobj = DataObject.find(fo);
             assertNotNull(dobj);

             EditorCookie ec = (EditorCookie)dobj.getCookie(EditorCookie.class);
             assertNotNull(ec);

             return (BaseDocument)ec.openDocument();
        }
        catch (Exception ex){
            fail(ex.toString());
            return null;
        }
    }

    @Override
    protected void configureIndenters(Document document, Formatter formatter, boolean indentOnly, String mimeType) {
        // override it because I've already done in setUp()
    }

    @Override
    protected boolean runInEQ() {
        return true;
    }

    public void testFormatting() throws Exception {
        // misc broken HTML:
        format(
            "<html>\n<xbody>\n<h1>Hello World!</h1>\n<p>text\n</body>",
            "<html>\n    <xbody>\n        <h1>Hello World!</h1>\n        <p>text\n    </body>", null);
        format("<html>\n<body>\n<div>\nSome text\n<!--\n     Some comment\n       * bullet\n       * bullet2\n-->\n</div>\n</body>\n</html>\n",
               "<html>\n    <body>\n        <div>\n            Some text\n            <!--\n                 Some comment\n                   * bullet\n                   * bullet2\n            -->\n        </div>\n    </body>\n</html>\n", null);
        format("<html>\n<body>\n<pre>Some\ntext which\n  should not be formatted.\n \n </pre>\n</body>\n</html>\n",
               "<html>\n    <body>\n        <pre>Some\ntext which\n  should not be formatted.\n \n        </pre>\n    </body>\n</html>\n", null);
        format("<html>\n<head id=someid\nclass=class/>\n<body>",
               "<html>\n    <head id=someid\n          class=class/>\n    <body>",null);

        // there was assertion failure discovered by this test:
        format("<html>\n        <head>\n<title>Localized Dates</title></head>",
               "<html>\n    <head>\n        <title>Localized Dates</title></head>", null);
        // TODO: impl this:
//        format("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"\n\"http://www.w3.org/TR/html4/loose.dtd\">\n<table>",
//                "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"\n        \"http://www.w3.org/TR/html4/loose.dtd\">\n<table>",null);

        // test tab character replacement:
        format(
            "<html>\n    <body>\n\t<table>",
            "<html>\n    <body>\n        <table>", null);

        // new line at the end used to cause NPE:
        format(
            "<a href=\"a\"><code>Validator</code></a>\n",
            "<a href=\"a\"><code>Validator</code></a>\n", null);

        // textarea is unformattable but within single line it should have no impact:
        format(
            "<p>\nA <a href=\"b\"><textarea>c</textarea></a>d\ne<textarea>f</textarea>g\nh\n</p>",
            "<p>\n    A <a href=\"b\"><textarea>c</textarea></a>d\n    e<textarea>f</textarea>g\n    h\n</p>", null);

        // unformattable content may contains other tags which should not be formatted:
        format(
            "<pre>\n       text\n          &quot;text2\n    <b>smth</b>\n</pre>",
            "<pre>\n       text\n          &quot;text2\n    <b>smth</b>\n</pre>", null);

        // unformattable content may contains other tags which should not be formatted:
        format(
            "<pre>\n       text\n          <textarea>text2\n    smth\n  </textarea>\n   text3\n  </pre>",
            "<pre>\n       text\n          <textarea>text2\n    smth\n  </textarea>\n   text3\n</pre>", null);

        // #161341
        format(
            "<!doctype html public \"unknown\">",
            "<!doctype html public \"unknown\">", null);

        // #161606
        format(
            "<div style=\"\"",
            "<div style=\"\"", null);
    }

    public void testFormattingHTML() throws Exception {
        reformatFileContents("testfiles/simple.html",new IndentPrefs(4,4));
    }

    public void testFormattingHTML01() throws Exception {
        reformatFileContents("testfiles/simple01.html",new IndentPrefs(4,4));
    }

    public void testFormattingHTML02() throws Exception {
        reformatFileContents("testfiles/simple02.html",new IndentPrefs(4,4));
    }

    public void testFormattingHTML03() throws Exception {
        reformatFileContents("testfiles/simple03.html",new IndentPrefs(4,4));
    }

    public void testIndentation() throws Exception {
        insertNewline("<html>^</html>", "<html>\n    ^\n</html>", null);
        insertNewline("        <table>\n            <tr>\n                <td>^</td>\n            </tr>\n</table>",
                      "        <table>\n            <tr>\n                <td>\n                    ^\n                </td>\n            </tr>\n</table>", null);

        insertNewline("  <html><table      color=aaa^", "  <html><table      color=aaa\n                    ^", null);
        // property tag indentation:
        insertNewline("<html>^<table>", "<html>\n    ^<table>", null);
        insertNewline("<html>^<table>\n<p>", "<html>\n    ^<table>\n<p>", null);
        insertNewline("<html>\n           <head>^", "<html>\n           <head>\n               ^", null);
        insertNewline("<html>\n           <body>^<table>", "<html>\n           <body>\n               ^<table>", null);
        insertNewline("<html><div/>^<table>", "<html><div/>\n    ^<table>", null);
        // tab attriutes indentation:
        insertNewline("<html><table^>", "<html><table\n        ^>", null);
        insertNewline("<html>^\n    <table>\n", "<html>\n    ^\n    <table>\n", null);

         //test that returning </body> tag matches opening one:
        insertNewline(
            "<html>\n  <body>\n        <h1>Hello World!</h1>\n                <p>text^</body>",
            "<html>\n  <body>\n        <h1>Hello World!</h1>\n                <p>text\n  ^</body>", null);
        insertNewline(
            "<html><body><table><tr>   <td>aa^</td></tr></table>",
            "<html><body><table><tr>   <td>aa\n                ^</td></tr></table>", null);
        insertNewline(
            "   <html><body><table><tr>   <td>aa^</td></tr></table>",
            "   <html><body><table><tr>   <td>aa\n                   ^</td></tr></table>", null);
        insertNewline(
            "   <html><body><table><tr>   <td\n                             style=\"xx\">aa^</td></tr></table>",
            "   <html><body><table><tr>   <td\n                             style=\"xx\">aa\n                   ^</td></tr></table>", null);
        insertNewline(
            "   <html><body><table><tr>   <td\n style=\"xx\">aa^</td></tr></table>",
            "   <html><body><table><tr>   <td\n style=\"xx\">aa\n                   ^</td></tr></table>", null);
        insertNewline(
            "<html>\n    <body><table><tr>   <td>a\n                ^</td></tr></table>",
            "<html>\n    <body><table><tr>   <td>a\n                \n                ^</td></tr></table>", null);

        // misc invalid HTML doc formatting:
        insertNewline(
            "<html>\n    <xbody>\n        <h1>Hello World!</h1>\n        <p>text\n^</body>",
            "<html>\n    <xbody>\n        <h1>Hello World!</h1>\n        <p>text\n\n    ^</body>", null);

        // #149719
        insertNewline(
            "<tr>some text^\n</tr>",
            "<tr>some text\n    ^\n</tr>", null);

        // #120136
        insertNewline(
            "<meta ^http-equiv=\"Content-Type\" content=\"text/html; charset=US-ASCII\">",
            "<meta \n    ^http-equiv=\"Content-Type\" content=\"text/html; charset=US-ASCII\">", null);

        // those two tests are for particular condition in AI.calculateLineIndent():
        insertNewline(
                "          <table><tr><td>a^</td>",
                "          <table><tr><td>a\n                  ^</td>", null);
        insertNewline(
                "          <table><tr><td>a\n                  ^</td>",
                "          <table><tr><td>a\n                  \n                  ^</td>", null);

        insertNewline(
                "   <p>\n     <table>\n      <tbody>^</table>",
                "   <p>\n     <table>\n      <tbody>\n     ^</table>", null);

        insertNewline(
            "<html> <!--^comment",
            "<html> <!--\n       ^comment", null);
        insertNewline(
            "<html> <!--\n             ^comment",
            "<html> <!--\n             \n       ^comment", null);
        insertNewline(
            "<html>\n    <!--\n    comment\n          -->\n^",
            "<html>\n    <!--\n    comment\n          -->\n\n          ^", null);

        insertNewline(
            "  <html\n     a=b\n       c=d^",
            "  <html\n     a=b\n       c=d\n       ^", null);
        insertNewline(
            "  <html\n     a=b\n       c=d>^",
            "  <html\n     a=b\n       c=d>\n      ^", null);

        // #160646 - check that unneeded tags are eliminated but
        // used to close tag with optional end:
        insertNewline(
            "  <p>\n  <table>\n  </table>^",
            "  <p>\n  <table>\n  </table>\n  ^", null);

        // #160651
        insertNewline(
            "<html>^<style>",
            "<html>\n    ^<style>", null);

        // #161105
        insertNewline(
            "<html><head><title>aa</title></head>\n    <body>\n        <table>\n            <tr>\n                <td><b>bold</b></td><td>^</td>",
            "<html><head><title>aa</title></head>\n    <body>\n        <table>\n            <tr>\n                <td><b>bold</b></td><td>\n                    ^\n                </td>", null);
        // test that table within p does not get eliminated and if it does it is handled properly;
        // there was a problem that eliminated p would try to close p tag at the end
        insertNewline(
            "<html>\n    <body>\n        <table>\n            <tr>\n                <td><table></table><p>text^",
            "<html>\n    <body>\n        <table>\n            <tr>\n                <td><table></table><p>text\n                        ^", null);
    }

}
