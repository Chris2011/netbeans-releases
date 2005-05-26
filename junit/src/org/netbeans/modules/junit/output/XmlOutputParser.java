/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.junit.output;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.regex.Pattern;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.xml.XMLUtil;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Marian Petras
 */
final class XmlOutputParser extends DefaultHandler {
    
    /** */
    private static final int STATE_OUT_OF_SCOPE = 1;
    /** */
    private static final int STATE_TESTSUITE = 2;
    /** */
    private static final int STATE_PROPERTIES = 3;
    /** */
    private static final int STATE_PROPERTY = 4;
    /** */
    private static final int STATE_TESTCASE = 8;
    /** */
    private static final int STATE_FAILURE = 12;
    /** */
    private static final int STATE_ERROR = 13;
    /** */
    private static final int STATE_OUTPUT_STD = 16;
    /** */
    private static final int STATE_OUTPUT_ERR = 17;
    
    /** */
    private int state = STATE_OUT_OF_SCOPE;
    /** */
    int unknownElemNestLevel = 0;
    
    /** */
    private final XMLReader xmlReader;
    /** */
    private Report report;
    /** */
    private Report.Testcase testcase;
    /** */
    private Report.Trouble trouble;
    /** */
    private StringBuffer charactersBuf;
    
    /** */
    private final RegexpUtils regexp;
    
    /**
     *
     * @exception  org.xml.sax.SAXException
     *             if initialization of the parser failed
     */
    static Report parseXmlOutput(String xmlOutput) throws SAXException {
        XmlOutputParser parser = new XmlOutputParser();
        try {
           parser.xmlReader.parse(new InputSource(new StringReader(xmlOutput)));
        } catch (SAXException ex) {
            String message = ex.getMessage();
            int severity = ErrorManager.INFORMATIONAL;
            if ((message != null)
                    && ErrorManager.getDefault().isLoggable(severity)) {
                ErrorManager.getDefault().log(
                       severity,
                       "Exception while parsing XML output from JUnit: "//NOI18N
                           + message);
            }
        } catch (IOException ex) {
            assert false;            /* should never happen */
        }
        return parser.report;
    }
    
    /** Creates a new instance of XMLOutputParser */
    private XmlOutputParser() throws SAXException {
        xmlReader = XMLUtil.createXMLReader();
        xmlReader.setContentHandler(this);
        
        regexp = RegexpUtils.getInstance();
    }
    
    /**
     */
    public void startElement(String uri,
                             String localName,
                             String qName,
                             Attributes attrs) throws SAXException {
        switch (state) {
            //<editor-fold defaultstate="collapsed" desc="STATE_PROPERTIES">
            case STATE_PROPERTIES:
                if (qName.equals("property")) {
                    state = STATE_PROPERTY;
                } else {
                    startUnknownElem();
                }
                break;  //</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="STATE_TESTSUITE">
            case STATE_TESTSUITE:
                if (qName.equals("testcase")) {
                    testcase = createTestcaseReport(
                            attrs.getValue("classname"),
                            attrs.getValue("name"),
                            attrs.getValue("time"));
                    state = STATE_TESTCASE;
                } else if (qName.equals("system-out")) {
                    state = STATE_OUTPUT_STD;
                } else if (qName.equals("system-err")) {
                    state = STATE_OUTPUT_ERR;
                } else if (qName.equals("properties")) {
                    state = STATE_PROPERTIES;
                } else {
                    startUnknownElem();
                }
                break;  //</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="STATE_TESTCASE">
            case STATE_TESTCASE:
                if (qName.equals("failure")) {
                    state = STATE_FAILURE;
                } else if (qName.equals("error")) {
                    state = STATE_ERROR;
                } else {
                    startUnknownElem();
                }
                if (state >= 0) {     //i.e. the element is "failure" or "error"
                    assert testcase != null;
                    
                    trouble = createTroubleReport(
                            state == STATE_ERROR,
                            attrs.getValue("type"),
                            attrs.getValue("message"));
                }
                break;  //</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="STATE_OUT_OF_SCOPE">
            case STATE_OUT_OF_SCOPE:
                if (qName.equals("testsuite")) {
                    report = createReport(attrs.getValue("name"),
                                          attrs.getValue("tests"),
                                          attrs.getValue("failures"),
                                          attrs.getValue("errors"),
                                          attrs.getValue("time"));
                    state = STATE_TESTSUITE;
                } else {
                    startUnknownElem();
                }
                break;  //</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="STATE_xxx (other)">
            case STATE_PROPERTY:
            case STATE_FAILURE:
            case STATE_ERROR:
            case STATE_OUTPUT_STD:
            case STATE_OUTPUT_ERR:
                startUnknownElem();
                break;  //</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="default">
            default:
                assert state < 0;
                unknownElemNestLevel++;
                break;  //</editor-fold>
        }
    }
    
    /**
     */
    public void endElement(String uri,
                           String localName,
                           String qName) throws SAXException {
        switch (state) {
            //<editor-fold defaultstate="collapsed" desc="STATE_PROPERTIES">
            case STATE_PROPERTIES:
                assert qName.equals("properties");
                state = STATE_TESTSUITE;
                break;                                          //</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="STATE_TESTSUITE">
            case STATE_TESTSUITE:
                assert qName.equals("testsuite");
                state = STATE_OUT_OF_SCOPE;
                break;                                          //</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="STATE_TESTCASE">
            case STATE_TESTCASE:
                assert qName.equals("testcase");
                
                assert testcase != null;
                report.reportTestcase(testcase);
                testcase = null;
                state = STATE_TESTSUITE;
                break;                                          //</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="STATE_OUT_OF_SCOPE">
            case STATE_OUT_OF_SCOPE:
                assert false;
                break;                                          //</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="STATE_PROPERTY">
            case STATE_PROPERTY:
                assert qName.equals("property");
                state = STATE_PROPERTIES;
                break;                                          //</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="STATE_FAILURE or STATE_ERROR">
            case STATE_FAILURE:
            case STATE_ERROR:
                assert (state == STATE_FAILURE && qName.equals("failure"))
                       || (state == STATE_ERROR && qName.equals("error"));
                
                assert testcase != null;
                assert trouble != null;
                if (charactersBuf != null) {
                   trouble.stackTrace = getStackTrace(charactersBuf.toString());
                   charactersBuf = null;
                }
                testcase.trouble = trouble;
                trouble = null;
                state = STATE_TESTCASE;
                break;                                          //</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="STATE_OUTPUT_STD or STATE_OUTPUT_ERR">
            case STATE_OUTPUT_STD:
            case STATE_OUTPUT_ERR:
                assert (state == STATE_OUTPUT_STD && qName.equals("system-out"))
                   || (state == STATE_OUTPUT_ERR && qName.equals("system-err"));
                if (charactersBuf != null) {
                    String[] output = getOutput(charactersBuf.toString());
                    if (state == STATE_OUTPUT_STD) {
                        report.outputStd = output;
                    } else {
                        report.outputErr = output;
                    }
                    charactersBuf = null;
                }
                state = STATE_TESTSUITE;
                break;                                          //</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="default">
            default:
                assert state < 0;
                if (--unknownElemNestLevel == 0) {
                    state = -state;
                }
                break;                                          //</editor-fold>
        }
    }
    
    /**
     */
    private void startUnknownElem() {
        state = -state;
        unknownElemNestLevel++;
    }
    
    /**
     */
    private Report createReport(String suiteName,
                                String testsCountStr,
                                String failuresStr,
                                String errorsStr,
                                String timeStr) {
        /* Parse the testsuite name: */
        if (suiteName == null) {
            suiteName = NbBundle.getMessage(XmlOutputParser.class,
                                            "UNNKOWN_NAME");            //NOI18N
        }
        
        /* Parse the test counts: */
        final String[] numberStrings = new String[] { testsCountStr,
                                                      failuresStr,
                                                      errorsStr };
        final int[] numbers = new int[numberStrings.length];
        for (int i = 0; i < numberStrings.length; i++) {
            boolean ok;
            String numberStr = numberStrings[i];
            if (numberStr == null) {
                ok = false;
            } else {
                try {
                    numbers[i] = Integer.parseInt(numberStrings[i]);
                    ok = (numbers[i] >= 0);
                } catch (NumberFormatException ex) {
                    ok = false;
                }
            }
            if (!ok) {
                numbers[i] = -1;
            }
        }
        
        /* Parse the elapsed time: */
        int timeMillis = regexp.parseTimeMillisNoNFE(timeStr);
        
        /* Create a report: */
        Report report = new Report(suiteName);
        report.totalTests = numbers[0];
        report.failures = numbers[1];
        report.errors = numbers[2];
        report.elapsedTimeMillis = timeMillis;
        
        return report;
    }
    
    /**
     */
    private Report.Testcase createTestcaseReport(String className,
                                                 String name,
                                                 String timeStr) {
        Report.Testcase testcase = new Report.Testcase();
        testcase.className = className;
        testcase.name = name;
        testcase.timeMillis = regexp.parseTimeMillisNoNFE(timeStr);
        
        return testcase;
    }
    
    /**
     */
    private Report.Trouble createTroubleReport(boolean error,
                                               String exceptionClsName,
                                               String message) {
        Report.Trouble trouble = new Report.Trouble(error);
        trouble.exceptionClsName = exceptionClsName;
        trouble.message = message;
        
        return trouble;
    }
    
    /**
     */
    public void characters(char[] ch,
                           int start,
                           int length) throws SAXException {
        switch (state) {
            case STATE_FAILURE:
            case STATE_ERROR:
            case STATE_OUTPUT_STD:
            case STATE_OUTPUT_ERR:
                if (charactersBuf == null) {
                    charactersBuf = new StringBuffer(512);
                }
                charactersBuf.append(ch, start, length);
                break;
        }
    }
    
    /**
     */
    private String[] getStackTrace(String string) {
        final String[] lines = string.split("[\\r\\n]+");               //NOI18N
        
        int startIndex = -1;
        int endIndex = -1;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String trimmed = RegexpUtils.specialTrim(line);
            if (trimmed.startsWith(RegexpUtils.CALLSTACK_LINE_PREFIX)
                   && regexp.getCallstackLinePattern().matcher(line).matches()){
                lines[i] = trimmed.substring(
                        RegexpUtils.CALLSTACK_LINE_PREFIX.length());
                if (startIndex == -1) {
                    startIndex = i;
                }
            } else {
                lines[i] = null;
                if (startIndex != -1) {
                    endIndex = i;
                    break;
                }
            }
        }
        if (startIndex == -1) {                 //no callstack line
            return null;
        }
        if (endIndex == -1) {
            endIndex = lines.length;
        }
        
        String[] stacktrace;
        if ((startIndex == 0) && (endIndex == lines.length)) {
            stacktrace = lines;
        } else {
            int count = endIndex - startIndex;
            stacktrace = new String[count];
            System.arraycopy(lines, startIndex, stacktrace, 0, count);
        }
        return stacktrace;
    }
    
    /**
     */
    private String[] getOutput(String string) {
        return string.split("(?:\\r|\\r\\n|\\n)");                      //NOI18N
    }
    
}
