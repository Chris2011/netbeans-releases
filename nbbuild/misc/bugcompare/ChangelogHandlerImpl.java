package nbbuild.misc.bugcompare;


/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

import java.util.*;
public class ChangelogHandlerImpl_1 implements ChangelogHandler {
    public static final boolean DEBUG = false;
    
    Map map;
    
    int number;
    
    public ChangelogHandlerImpl_1(Map map) {
        this.map = map;
        number = 0;
    }
    
    public void handle_name(final java.lang.String data, final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException {
        if (DEBUG) System.err.println("handle_name: " + data);
    }
    
    public void handle_msg(final java.lang.String data, final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException {
        int number = 0;
        for (int i = 0; i < data.length(); i++) {
            int c = data.charAt(i);
            if ((c >= '0') && (c <= '9')) {
                if (number == 0) {
                    number = c - '0';
                }
                else {
                    number = number * 10;
                    number += (c - '0');
                }
            }
            else {
                if ((number > 1000) && (number < 15000)) {
                    map.put(new Integer(number), data);
                }
                number = 0;
            }
        }
    }
    
    public void handle_time(final java.lang.String data, final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException {
        if (DEBUG) System.err.println("handle_time: " + data);
    }
    
    public void handle_file(final java.lang.String data, final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException {
        if (DEBUG) System.err.println("handle_file: " + data);
    }
    
    public void handle_date(final java.lang.String data, final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException {
        if (DEBUG) System.err.println("handle_date: " + data);
    }
    
    public void handle_author(final java.lang.String data, final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException {
        if (DEBUG) System.err.println("handle_author: " + data);
    }
    
    public void handle_commondir(final java.lang.String data, final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException {
        if (DEBUG) System.err.println("handle_commondir: " + data);
    }
    
    public void handle_utag(final java.lang.String data, final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException {
        if (DEBUG) System.err.println("handle_utag: " + data);
    }
    
    public void handle_weekday(final java.lang.String data, final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException {
        if (DEBUG) System.err.println("handle_weekday: " + data);
    }
    
    public void handle_changelog(final java.lang.String data, final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException {
        if (DEBUG) System.err.println("handle_changelog: " + data);
    }
    
    public void handle_revision(final java.lang.String data, final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException {
        if (DEBUG) System.err.println("handle_revision: " + data);
    }
    
    public void handle_entry(final java.lang.String data, final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException {
        if (DEBUG) System.err.println("handle_entry: " + data);
    }
    
    public void handle_branch(final java.lang.String data, final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException {
        if (DEBUG) System.err.println("handle_branch: " + data);
    }
    
    public void handle_branchroot(final java.lang.String data, final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException {
        if (DEBUG) System.err.println("handle_branchroot: " + data);
    }
    
    public void handle_tag(final java.lang.String data, final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException {
        if (DEBUG) System.err.println("handle_tag: " + data);
    }
    
    
}