/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.lib.jsp.lexer;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.jsp.lexer.JspTokenId;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;

/**
 * Syntax class for JSP tags.
 *
 * @author Petr Jiricka
 * @author Marek Fukala
 *
 * @version 1.00
 */

public class JspLexer implements Lexer<JspTokenId> {

    private static final Logger LOGGER = Logger.getLogger(JspLexer.class.getName());
    private static final boolean LOG = Boolean.getBoolean("j2ee_lexer_debug"); //NOI18N


    private static final int EOF = LexerInput.EOF;

    private final LexerInput input;

    private final TokenFactory<JspTokenId> tokenFactory;

    public Object state() {
        return lexerState + lexerStateBeforeEL * 1000;
    }

    //main internal lexer state
    private int lexerState = INIT;

    //secondary internal state for EL expressions in JSP
    //is it used to eliminate a number of lexer states when EL is found -
    //we have 8 states just in attribute value so I would have to copy the EL
    //recognition code eight-times.
    private int lexerStateBeforeEL = INIT;

    // Internal analyzer states
    // general
    private static final int INIT                =  0;  // initial lexer state = content language
    private static final int ISI_ERROR           =  1; // when the fragment does not start with <
    private static final int ISA_LT              =  2; // after '<' char
    // tags and directives
    private static final int ISI_TAGNAME         =  3; // inside JSP tag name
    private static final int ISI_DIRNAME         =  4; // inside JSP directive name
    private static final int ISP_TAG             =  5; // after JSP tag name
    private static final int ISP_DIR             =  6; // after JSP directive name
    private static final int ISI_TAG_I_WS        =  7; // inside JSP tag after whitespace
    private static final int ISI_DIR_I_WS        =  8; // inside JSP directive after whitespace
    private static final int ISI_ENDTAG          =  9; // inside end JSP tag
    private static final int ISI_TAG_ATTR        = 10; // inside tag attribute
    private static final int ISI_DIR_ATTR        = 11; // inside directive attribute
    private static final int ISP_TAG_EQ          = 12; // just after '=' in tag
    private static final int ISP_DIR_EQ          = 13; // just after '=' in directive
    private static final int ISI_TAG_STRING      = 14; // inside string (value - "") in tag
    private static final int ISI_DIR_STRING      = 15; // inside string (value - "") in directive
    private static final int ISI_TAG_STRING_B    = 16; // inside string (value - "") after backslash in tag
    private static final int ISI_DIR_STRING_B    = 17; // inside string (value - "") after backslash in directive
    private static final int ISI_TAG_STRING2     = 18; // inside string (value - '') in tag
    private static final int ISI_DIR_STRING2     = 19; // inside string (value - '') in directive
    private static final int ISI_TAG_STRING2_B   = 20; // inside string (value - '') after backslash in tag
    private static final int ISI_DIR_STRING2_B   = 21; // inside string (value - '') after backslash in directive
    private static final int ISA_ENDSLASH        = 22; // after ending '/' in JSP tag
    private static final int ISA_ENDPC           = 23; // after ending '%' in JSP directive
    // comments (+directives)
    private static final int ISA_LT_PC           = 24; // after '<%' - comment or directive or scriptlet
    private static final int ISI_JSP_COMMENT     = 25; // after <%-

    private static final int ISI_JSP_COMMENT_M   = 26; // inside JSP comment after -
    private static final int ISI_JSP_COMMENT_MM  = 27; // inside JSP comment after --
    private static final int ISI_JSP_COMMENT_MMP = 28; // inside JSP comment after --%
    // end state
//    static final int ISA_END_JSP                 = 29; // JSP fragment has finished and control
    // should be returned to master syntax
    // more errors
    private static final int ISI_TAG_ERROR       = 30; // error in tag, can be cleared by > or \n
    private static final int ISI_DIR_ERROR       = 31; // error in directive, can be cleared by %>, \n, \t or space
    private static final int ISI_DIR_ERROR_P     = 32; // error in directive after %, can be cleared by > or \n

    private static final int ISA_LT_PC_AT        = 33; // after '<%@' (directive)
    private static final int ISA_LT_SLASH        = 34; // after '</' sequence
    private static final int ISA_LT_PC_DASH      = 35; // after <%- ;not comment yet

    private static final int ISI_SCRIPTLET       = 36; // inside java scriptlet/declaration/expression
    private static final int ISP_SCRIPTLET_PC   = 37; // just after % in scriptlet

    //expression language

    //EL in content language
    private static final int ISA_EL_DELIM        = 38; //after $ or # in content language
    private static final int ISI_EL              = 39; //expression language in content (after ${ or #{ )

    private static final int ISA_BS             = 40; //after backslash in text - needed to disable EL by scaping # or $
    
    public JspLexer(LexerRestartInfo<JspTokenId> info) {
        this.input = info.input();
        this.tokenFactory = info.tokenFactory();
        if (info.state() == null) {
            this.lexerState = INIT;
        } else {
            int encoded = ((Integer) info.state()).intValue();
            lexerStateBeforeEL = encoded / 1000;
            lexerState = encoded % 1000;
        }
    }

    public boolean isIdentifierPart(char character) {
        return Character.isJavaIdentifierPart(character);
    }

    /** Determines whether a given string is a JSP tag. */
    protected boolean isJspTag(String tagName) {
        boolean canBeJsp = tagName.startsWith("jsp:");  // NOI18N
        //TODO handle custom tags from JSP parser here
        return canBeJsp;
    }

    /** Looks ahead into the character buffer and checks if a jsp tag name follows. */
    private boolean followsJspTag() {
        int actChar;
        int prev_read = input.readLength(); //remember the size of the read sequence
        int read = 0;
        while(true) {
            actChar = input.read();
            read++;
            if(!(Character.isLetter(actChar) ||
                    Character.isDigit(actChar) ||
                    (actChar == '_') ||
                    (actChar == '-') ||
                    (actChar == ':') ||
                    (actChar == '.')) ||
                    (actChar == EOF)) { // EOL or not alpha
                //end of tagname
                String tagName = input.readText().toString().substring(prev_read);
                input.backup(read); //put the lookahead text back to the buffer
                return isJspTag(tagName);
            }
        }
    }

    public Token<JspTokenId> nextToken() {
        int actChar;
        while (true) {
            actChar = input.read();

            if (actChar == EOF) {
                if(input.readLengthEOF() == 1) {
                    return null; //just EOL is read
                } else {
                    //there is something else in the buffer except EOL
                    //we will return last token now
                    input.backup(1); //backup the EOL, we will return null in next nextToken() call
                    break;
                }
            }

            switch (lexerState) {
                case INIT:
                    switch (actChar) {
//                        case '\n':
//                            return token(JspTokenId.EOL);
                        case '<':
                            lexerState = ISA_LT;
                            break;
//                        default:
//                            state = ISI_ERROR;
//                            break;
                        case '\\':
                                lexerState = ISA_BS;
                                break;
                        case '$':
                        case '#': //maybe expression language
                            lexerStateBeforeEL = lexerState; //remember main state
                            lexerState = ISA_EL_DELIM;
                            break;
                    }
                    break;

                case ISA_BS:
                    if(actChar != '\\') {
                        lexerState = INIT; //prevent scaped EL in text being recognized
                    }
                    break;
                    
                case ISA_EL_DELIM:
                    switch(actChar) {
                        case '{':
                            if(input.readLength() > 2) {
                                //we have something read except the '${' or '#{' => it's content language
                                input.backup(2); //backup the '$/#{'
                                lexerState = lexerStateBeforeEL; //we will read the '$/#{' again
                                lexerStateBeforeEL = INIT;
                                return token(JspTokenId.TEXT); //return the content language token
                            }
                            lexerState = ISI_EL;
                            break;
                        default:
                            lexerState = lexerStateBeforeEL;
                            lexerStateBeforeEL = INIT;
                    }
                    break;

                case ISI_EL:
                    if(actChar == '}') {
                        //return EL token
                        lexerState = lexerStateBeforeEL;
                        lexerStateBeforeEL = INIT;
                        return token(JspTokenId.EL);
                    }
                    //stay in EL
                    break;

                case ISA_LT:
                    if (Character.isLetter(actChar) ||
                            (actChar == '_')
                            ) { // possible tag begining
                        input.backup(1); //backup the read letter
                        if(followsJspTag()) { //test if a jsp tag follows
                            if(input.readLength() > 1) {
                                //we have something read except the '<' => it's content language
                                input.backup(1); //backup the '<'
                                lexerState = INIT; //we will read the '<' again
                                return token(JspTokenId.TEXT); //return the content language token
                            }
                            lexerState = ISI_TAGNAME;
                            break;
                        } else {
                            //just a content language
                            lexerState = INIT;
                            break;
                        }
//                        input.backup(1);
//                        return token(JspTokenId.SYMBOL);
                    }

                    switch (actChar) {
                        case '/':
                            lexerState = ISA_LT_SLASH;
                            break;
//                        case '\n':
//                            state = ISI_TAG_ERROR;
//                            input.backup(1);
//                            return token(JspTokenId.SYMBOL);
                        case '%':
                            lexerState = ISA_LT_PC;
                            break;
                        default:
                            lexerState = INIT; //just content
//                            state = ISI_TAG_ERROR;
//                            break;
                    }
                    break;

                case ISA_LT_SLASH:
                    if (Character.isLetter(actChar) ||
                            (actChar == '_')) {
                        //possible end tag beginning
                        input.backup(1); //backup the first letter
                        if(followsJspTag()) {
                            if(input.readLength() > 2) {
                                //we have something read except the '</' symbol
                                input.backup(2);
                                lexerState = INIT;
                                return token(JspTokenId.TEXT);
                            } else {
                                lexerState = ISI_ENDTAG;
                            }
                            break;
                        } else {
                            //just a content language
                            lexerState = INIT;
                            break;
                        }
                    }

                    //everyting alse is an error
                    lexerState = ISI_TAG_ERROR;
                    break;

                case ISI_TAGNAME:
                case ISI_DIRNAME:

                    if (!(Character.isLetter(actChar) ||
                            Character.isDigit(actChar) ||
                            (actChar == '_') ||
                            (actChar == '-') ||
                            (actChar == ':') ||
                            (actChar == '.'))) { // not alpha
                        switch(actChar) {
                            case '<':
                                lexerState = INIT;
                                input.backup(1);
                                break;
                            case '/':
                                input.backup(1);
                                lexerState = ((lexerState == ISI_TAGNAME) ? ISP_TAG : ISP_DIR);
                                break;
                            case '>':
                                lexerState = INIT;
                                break;
                            case ' ':
                                input.backup(1);
                                lexerState = ((lexerState == ISI_TAGNAME) ? ISP_TAG : ISP_DIR);
                                break;
                            default:
                                lexerState = ((lexerState == ISI_TAGNAME) ? ISP_TAG : ISP_DIR);
                        }
                        return token(JspTokenId.TAG);
                    }
                    break;

                case ISP_TAG:
                case ISP_DIR:
                    if (Character.isLetter(actChar) ||
                            (actChar == '_')
                            ) {
                        lexerState = ((lexerState == ISP_TAG) ? ISI_TAG_ATTR : ISI_DIR_ATTR);
                        break;
                    }
                    switch (actChar) {
                        case '\n':
//                            if (input.readLength() == 1) { // no char
                            return token(JspTokenId.EOL);
//                            } else { // return string first
//                                input.backup(1);
//                                return decide_jsp_tag_token();
//                            }
                        case '>': // for tags
                            if (lexerState == ISP_TAG) {
//                                if (input.readLength() == 1) {  // no char
//                                    state = ISA_END_JSP;
                                lexerState = INIT;
                                return token(JspTokenId.SYMBOL);
//                                } else { // return string first
//                                    input.backup(1);
//                                    return decide_jsp_tag_token();
//                                }
                            } else { // directive
                                //state = ISI_DIR_ERROR;
                                //commented out to minimize errors during the process of writing directives
                                break;
                            }
                        case '/': // for tags
                            if (lexerState == ISP_TAG) {
//                                if (input.readLength() == 1) {  // no char
                                lexerState = ISA_ENDSLASH;
                                break;
//                                } else { // return string first
//                                    input.backup(1);
//                                    return decide_jsp_tag_token();
//                                }
                            } else { // directive
                                //state = ISI_DIR_ERROR;
                                //commented out to minimize errors during the process of writing directives
                                break;
                            }
                        case '%': // for directives
                            if (lexerState == ISP_DIR) {
//                                if (input.readLength() == 1) {  // no char
                                lexerState = ISA_ENDPC;
                                break;
//                                } else { // return string first
//                                    input.backup(1);
//                                    return decide_jsp_tag_token();
//                                }
                            } else { // tag
                                lexerState = ISI_TAG_ERROR;
                                break;
                            }
                        case '=':
                            lexerState = ((lexerState == ISP_TAG) ? ISP_TAG_EQ : ISP_DIR_EQ);
                            return token(JspTokenId.SYMBOL);
                        case ' ':
                        case '\t':
                            lexerState = ((lexerState == ISP_TAG) ? ISI_TAG_I_WS : ISI_DIR_I_WS);
                            break;
                        case '<': // assume that this is the start of the next tag
//                            state=ISA_END_JSP;
                            lexerState = INIT;
                            input.backup(1);
                            return token(JspTokenId.TAG);
                        default: //numbers or illegal symbols
                            lexerState = ((lexerState == ISP_TAG) ? ISI_TAG_ERROR : ISI_DIR_ERROR);
                            break;
                    }
                    break;

                case ISI_TAG_I_WS:
                case ISI_DIR_I_WS:
                    switch (actChar) {
                        case ' ':
                        case '\t':
                            break;
                        case '<': //start of the next tag
//                            state = ISA_END_JSP;
                            lexerState = INIT;
                            input.backup(1);
                            return token(JspTokenId.TAG);
                        default:
                            lexerState = ((lexerState == ISI_TAG_I_WS) ? ISP_TAG : ISP_DIR);
                            input.backup(1);
                            return token(JspTokenId.WHITESPACE);
                    }
                    break;

                case ISI_ENDTAG:
                    if (!(Character.isLetter(actChar) ||
                            Character.isDigit(actChar) ||
                            (actChar == '_') ||
                            (actChar == '-') ||
                            (actChar == ':'))
                            ) { // not alpha
                        lexerState = ISP_TAG;
                        input.backup(1);
                        return token(JspTokenId.TAG);
                    }
                    break;

                case ISI_TAG_ATTR:
                case ISI_DIR_ATTR:
                    if (!(Character.isLetter(actChar) ||
                            Character.isDigit(actChar) ||
                            (actChar == '_') ||
                            (actChar == ':') ||
                            (actChar == '-'))
                            ) { // not alpha or '-' (http-equiv)
                        lexerState = ((lexerState == ISI_TAG_ATTR) ? ISP_TAG : ISP_DIR);
                        input.backup(1);
                        return token(JspTokenId.ATTRIBUTE);
                    }
                    break;

                case ISP_TAG_EQ:
                case ISP_DIR_EQ:
                    switch (actChar) {
                        case '\n':
//                            if (input.readLength() == 1) { // no char
                            return token(JspTokenId.EOL);
//                            } else { // return string first
//                                input.backup(1);
//                                return token(JspTokenId.ATTR_VALUE);
//                            }
                        case '"':
                            lexerState = ((lexerState == ISP_TAG_EQ) ? ISI_TAG_STRING : ISI_DIR_STRING);
                            break;
                        case '\'':
                            lexerState = ((lexerState == ISP_TAG_EQ) ? ISI_TAG_STRING2 : ISI_DIR_STRING2);
                            break;
                        case ' ':
                        case '\t':
                            // don't change the state
                            break;
                        default:
                            lexerState = ((lexerState == ISP_TAG_EQ) ? ISP_TAG : ISP_DIR);
                            input.backup(1);
                            //return token(JspTokenId.ATTR_VALUE);
                            break;
                    }
                    break;

                case ISI_TAG_STRING:
                case ISI_DIR_STRING:
                case ISI_TAG_STRING2:
                case ISI_DIR_STRING2:
                    if ((actChar == '"') && ((lexerState == ISI_TAG_STRING) || (lexerState == ISI_DIR_STRING))) {
                        lexerState = ((lexerState == ISI_TAG_STRING) ? ISP_TAG : ISP_DIR);
                        return token(JspTokenId.ATTR_VALUE);
                    }

                    if ((actChar == '\'') && ((lexerState == ISI_TAG_STRING2) || (lexerState == ISI_DIR_STRING2))) {
                        lexerState = ((lexerState == ISI_TAG_STRING2) ? ISP_TAG : ISP_DIR);
                        return token(JspTokenId.ATTR_VALUE);
                    }

                    switch (actChar) {
                        case '\\':
                            switch (lexerState) {
                                case ISI_TAG_STRING:
                                    lexerState = ISI_TAG_STRING_B;
                                    break;
                                case ISI_DIR_STRING:
                                    lexerState = ISI_DIR_STRING_B;
                                    break;
                                case ISI_TAG_STRING2:
                                    lexerState = ISI_TAG_STRING2_B;
                                    break;
                                case ISI_DIR_STRING2:
                                    lexerState = ISI_DIR_STRING2_B;
                                    break;
                            }
                            break;
                        case '\n':
//                            if (input.readLength() == 1) { // no char
                            return token(JspTokenId.EOL);
//
//                            } else { // return string first
//                                input.backup(1);
//                                return token(JspTokenId.ATTR_VALUE);
//                            }
                        case '$':
                        case '#':
                            if(input.readLength() > 1) {
                                //return part of the attribute value before EL
                                input.backup(1); //backup $ or #
                                return token(JspTokenId.ATTR_VALUE);
                            } else {
                                lexerStateBeforeEL = lexerState; //remember main state
                                lexerState = ISA_EL_DELIM;
                            }
                            break;

                        default:
                            break;//stay in ISI_TAG_STRING/2;

                    }
                    break;

                case ISI_TAG_STRING_B:
                case ISI_DIR_STRING_B:
                case ISI_TAG_STRING2_B:
                case ISI_DIR_STRING2_B:
                    switch (actChar) {
                        case '"':
                        case '\'':
                        case '\\':
                        case '$':
                        case '#':
                            break;
                        default:
                            input.backup(1);
                            break;
                    }
                    switch (lexerState) {
                        case ISI_TAG_STRING_B:
                            lexerState = ISI_TAG_STRING;
                            break;
                        case ISI_DIR_STRING_B:
                            lexerState = ISI_DIR_STRING;
                            break;
                        case ISI_TAG_STRING2_B:
                            lexerState = ISI_TAG_STRING2;
                            break;
                        case ISI_DIR_STRING2_B:
                            lexerState = ISI_DIR_STRING2;
                            break;
                    }
                    break;

                case ISA_ENDSLASH:
                    switch (actChar) {
                        case '>':
//                            state = ISA_END_JSP;
                            lexerState = INIT;
                            return token(JspTokenId.SYMBOL);
                        case '\n':
                            lexerState = ISI_TAG_ERROR;
                            input.backup(1);
                            return token(JspTokenId.SYMBOL);
                        default:
                            lexerState = ISP_TAG;
                            input.backup(1);
                            return token(JspTokenId.SYMBOL);
                    }
                    //break; not reached

                case ISA_ENDPC:
                    switch (actChar) {
                        case '>':
//                            state = ISA_END_JSP;
                            lexerState = INIT;
                            return token(JspTokenId.SYMBOL);
                        case '\n':
                            lexerState = ISI_DIR_ERROR;
                            input.backup(1);
                            return token(JspTokenId.SYMBOL);
                        default:
                            lexerState = ISP_DIR;
                            input.backup(1);
                            return token(JspTokenId.SYMBOL);
                    }
                    //break; not reached

                case ISA_LT_PC:
                    switch (actChar) {
                        case '@':
                            if(input.readLength() == 3) {
                                // just <%@ read
                                lexerState = ISA_LT_PC_AT;
                                return token(JspTokenId.SYMBOL);
                            } else {
                                //jsp symbol, but we also have content language in the buffer
                                input.backup(3); //backup <%@
                                lexerState = INIT;
                                return token(JspTokenId.TEXT); //return CL token
                            }
                        case '-': //may be JSP comment
                            lexerState = ISA_LT_PC_DASH;
                            break;
                        case '!': // java declaration
                        case '=': // java expression
                            if(input.readLength() == 3) {
                                // just <%! or <%= read
                                lexerState = ISI_SCRIPTLET;
                                return token(JspTokenId.SYMBOL2);
                            } else {
                                //jsp symbol, but we also have content language in the buffer
                                input.backup(3); //backup <%! or <%=
                                lexerState = INIT;
                                return token(JspTokenId.TEXT); //return CL token
                            }
                        default:  //java scriptlet delimiter '<%'
                            if(input.readLength() == 3) {
                                // just <% + something != [-,!,=,@] read
                                lexerState = ISI_SCRIPTLET;
                                input.backup(1); //backup the third character, it is a part of the java scriptlet
                                return token(JspTokenId.SYMBOL2);
                            } else {
                                //jsp symbol, but we also have content language in the buffer
                                input.backup(3); //backup <%@
                                lexerState = INIT;
                                return token(JspTokenId.TEXT); //return CL token
                            }
                    }
                    break;

                case ISI_SCRIPTLET:
                    switch(actChar) {
                        case '%':
                            lexerState = ISP_SCRIPTLET_PC;
                            break;
                    }
                    break;

                case ISP_SCRIPTLET_PC:
                    switch(actChar) {
                        case '>':
                            if(input.readLength() == 2) {
                                //just the '%>' symbol read
                                lexerState = INIT;
                                return token(JspTokenId.SYMBOL2);
                            } else {
                                //return the scriptlet content
                                input.backup(2); // backup '%>' we will read JUST them again
                                lexerState = ISI_SCRIPTLET;
                                return token(JspTokenId.SCRIPTLET);
                            }
                        default:
                            lexerState = ISI_SCRIPTLET;
                            break;
                    }
                    break;

                case ISA_LT_PC_DASH:
                    switch(actChar) {
                        case '-':
                            if(input.readLength() == 4) {
                                //just the '<%--' symbol read
                                lexerState = ISI_JSP_COMMENT;
                            } else {
                                //return the scriptlet content
                                input.backup(4); // backup '<%--', we will read it again
                                lexerState = INIT;
                                return token(JspTokenId.TEXT);
                            }
                            break;
                        default:
//                            state = ISA_END_JSP;
                            lexerState = INIT; //XXX how to handle content language?
                            return token(JspTokenId.TEXT); //marek: should I token here????
                    }

                    // JSP states
                case ISI_JSP_COMMENT:
                    switch (actChar) {
                        case '\n':
                            if (input.readLength() == 1) { // no char
                                return token(JspTokenId.EOL);
                            } else { // return block comment first
                                input.backup(1);
                                return token(JspTokenId.COMMENT);
                            }
                        case '-':
                            lexerState = ISI_JSP_COMMENT_M;
                            break;
                    }
                    break;

                case ISI_JSP_COMMENT_M:
                    switch (actChar) {
                        case '\n':
                            lexerState = ISI_JSP_COMMENT;
                            if (input.readLength() == 1) { // no char
                                return token(JspTokenId.EOL);
                            } else { // return block comment first
                                input.backup(1);
                                return token(JspTokenId.COMMENT);
                            }
                        case '-':
                            lexerState = ISI_JSP_COMMENT_MM;
                            break;
                        default:
                            lexerState = ISI_JSP_COMMENT;
                            break;
                    }
                    break;

                case ISI_JSP_COMMENT_MM:
                    switch (actChar) {
                        case '\n':
                            lexerState = ISI_JSP_COMMENT;
                            if (input.readLength() == 1) { // no char
                                return token(JspTokenId.EOL);
                            } else { // return block comment first
                                input.backup(1);
                                return token(JspTokenId.COMMENT);
                            }
                        case '%':
                            lexerState = ISI_JSP_COMMENT_MMP;
                            break;
                        case '-':
                            lexerState = ISI_JSP_COMMENT_MM;
                            break;
                        default:
                            lexerState = ISI_JSP_COMMENT;
                            break;
                    }
                    break;

                case ISI_JSP_COMMENT_MMP:
                    switch (actChar) {
                        case '\n':
                            lexerState = ISI_JSP_COMMENT;
                            if (input.readLength() == 1) { // no char
                                return token(JspTokenId.EOL);
                            } else { // return block comment first
                                input.backup(1);
                                return token(JspTokenId.COMMENT);
                            }
                        case '>':
//                            state = ISA_END_JSP;
                            lexerState = INIT;
                            return token(JspTokenId.COMMENT);
                        default:
                            lexerState = ISI_JSP_COMMENT;
                            break;
                    }
                    break;

                case ISI_ERROR:
                    switch (actChar) {
                        case '\n':
                            lexerState = INIT;
                            input.backup(1);
                            return token(JspTokenId.ERROR);
                        case '<':
                            lexerState = ISA_LT;
                            input.backup(1);
                            return token(JspTokenId.ERROR);
                    }
                    break;

                case ISI_TAG_ERROR:
                    switch (actChar) {
                        case '\n':
                            if (input.readLength() == 1) { // no char
                                lexerState = ISP_TAG;
                                return token(JspTokenId.EOL);
                            } else { // return error first
                                input.backup(1);
                                return token(JspTokenId.ERROR);
                            }
                        case '>':
                        case ' ':
                        case '\t':
                            lexerState = ISP_TAG;
                            input.backup(1);
                            return token(JspTokenId.ERROR);
                        default:
                            break;
                    }
                    break;

                case ISI_DIR_ERROR:
                    switch (actChar) {
                        case '\n':
                            if (input.readLength() == 1) { // no char
                                lexerState = ISI_DIR_I_WS;
                                return token(JspTokenId.EOL);
                            } else { // return error first
//                                input.backup(1);
                                return token(JspTokenId.ERROR);
                            }
                        case '%':
                        case '\t':
                        case ' ':
                            lexerState = ISP_DIR;
                            input.backup(1);
                            return token(JspTokenId.ERROR);
                        default:
                            break;
                    }
                    break;

                case ISI_DIR_ERROR_P:
                    switch (actChar) {
                        case '\n':
                            if (input.readLength() == 1) { // no char
                                lexerState = ISI_DIR_I_WS;
                                return token(JspTokenId.EOL);
                            } else { // return error first
                                input.backup(1);
                                return token(JspTokenId.ERROR);
                            }
                        case '>':
                            input.backup(2);
                            lexerState = ISI_DIR_I_WS;
                            return token(JspTokenId.ERROR);
                        default:
                            break;
                    }
                    break;

//                case ISA_END_JSP:
//                    if (input.readLength() == 1) {
//                        offset++;
//                        return JspTokenId.AFTER_UNEXPECTED_LT;
//                    }
//                    else {
//                        return JspTokenId.TEXT;
//                    }
//                    //break;

                    // added states
                case ISA_LT_PC_AT:
                    if (Character.isLetter(actChar) ||
                            (actChar == '_')
                            ) { // the directive starts
                        lexerState = ISI_DIRNAME;
//                        marek: why to create an empty tag token????
//                        input.backup(1);
//                        return decide_jsp_tag_token();
                    }

                    switch (actChar) {
                        case '\n':
                            if (input.readLength() == 1) { // no char
                                return token(JspTokenId.EOL);
                            } else {
                                input.backup(1);
                                return token(JspTokenId.TAG);
                            }
                        default:
                            break;
                    }
                    break;

            }

        }

        // At this stage there's no more text in the scanned buffer.
        // Scanner first checks whether this is completely the last
        // available buffer.

        switch(lexerState) {
            case INIT:
                if (input.readLength() == 0) {
                    return null;
                } else {
                    return token(JspTokenId.TEXT);
                }
            case ISI_ERROR:
            case ISI_TAG_ERROR:
                lexerState = INIT;
                return token(JspTokenId.ERROR);
            case ISI_DIR_ERROR:
            case ISI_DIR_ERROR_P:
                lexerState = INIT;
                return token(JspTokenId.ERROR);
            case ISA_LT:
            case ISA_LT_SLASH:
            case ISA_ENDSLASH:
            case ISP_TAG_EQ:
                lexerState = INIT;
                return token(JspTokenId.SYMBOL);
            case ISA_LT_PC:
            case ISA_LT_PC_DASH:
            case ISA_ENDPC:
            case ISP_DIR_EQ:
                lexerState = INIT;
                return token(JspTokenId.SYMBOL);
            case ISI_TAGNAME:
            case ISI_ENDTAG:
                lexerState = INIT;
                return token(JspTokenId.TAG);
            case ISI_DIRNAME:
                lexerState = INIT;
                return token(JspTokenId.TAG);
            case ISP_TAG:
            case ISI_TAG_I_WS:
                lexerState = INIT;
                return token(JspTokenId.TAG);
            case ISP_DIR:
            case ISI_DIR_I_WS:
            case ISA_LT_PC_AT:
                lexerState = INIT;
                return token(JspTokenId.TAG);
            case ISI_TAG_ATTR:
                lexerState = INIT;
                return token(JspTokenId.ATTRIBUTE);
            case ISI_DIR_ATTR:
                lexerState = INIT;
                return token(JspTokenId.ATTRIBUTE);
            case ISI_TAG_STRING:
            case ISI_TAG_STRING_B:
            case ISI_TAG_STRING2:
            case ISI_TAG_STRING2_B:
                lexerState = INIT;
                return token(JspTokenId.ATTR_VALUE);
            case ISI_DIR_STRING:
            case ISI_DIR_STRING_B:
            case ISI_DIR_STRING2:
            case ISI_DIR_STRING2_B:
                lexerState = INIT;
                return token(JspTokenId.ATTR_VALUE);
            case ISI_JSP_COMMENT:
            case ISI_JSP_COMMENT_M:
            case ISI_JSP_COMMENT_MM:
            case ISI_JSP_COMMENT_MMP:
                lexerState = INIT;
                return token(JspTokenId.COMMENT);
            case ISA_EL_DELIM:
                lexerState = INIT;
                return token(JspTokenId.TEXT);
            case ISI_EL:
                lexerState = INIT;
                return token(JspTokenId.EL);
            case ISP_SCRIPTLET_PC:
                lexerState = INIT;
                return token(JspTokenId.SYMBOL2);
            case ISI_SCRIPTLET:
                lexerState = INIT;
                return token(JspTokenId.SCRIPTLET);
            default:
                break;
        }

        return null;

    }

    private Token<JspTokenId> token(JspTokenId tokenId) {
        if(LOG) {
            if(input.readLength() == 0) {
                LOGGER.log(Level.INFO, "Found zero length token: ");
            }
            LOGGER.log(Level.INFO, "[" + this.getClass().getSimpleName() + "] token ('" + input.readText().toString() + "'; id=" + tokenId + "; state=" + state() + ")\n");
        }
        return tokenFactory.createToken(tokenId);
    }


}

