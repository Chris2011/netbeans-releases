/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.tax;

import junit.textui.TestRunner;
import org.netbeans.modules.xml.core.DTDDataObject;
import org.netbeans.modules.xml.core.XMLDataObject;
import org.netbeans.tests.xml.XTest;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;

/**
 * <P>
 * <P>
 * <FONT COLOR="#CC3333" FACE="Courier New, Monospaced" SIZE="+1">
 * <B>
 * <BR> XML Module API Test: CreateSimpleXML
 * </B>
 * </FONT>
 * <BR><BR><B>What it tests:</B><BR>
 *
 * This test creates simple XML document with DTD and writes it into output.
 *
 * <BR><BR><B>How it works:</B><BR>
 *
 * 1) create empty XML document from template<BR>
 * 2) create new Document Type and add it into document<BR>
 * 3) append XML elements<BR>
 * 4) write the document into output<BR>
 *
 * <BR><BR><B>Settings:</B><BR>
 * none<BR>
 *
 * <BR><BR><B>Output (Golden file):</B><BR>
 * XML document with DTD.<BR>
 *
 * <BR><B>To Do:</B><BR>
 * none<BR>
 *
 * <P>Created on December 20, 2000, 12:33 PM
 * <P>
 */
public class CreateXMLTest extends XTest {
    private static String XML_EXT = "xml";
    private static String DOCUMENT_NAME = "Delme";
    private static String DTD_SYS_ID = "simpleXXL.dtd";
    private static String INTERNAL_DTD = "internalDTD.dtd";
    
    /** Creates new CoreSettingsTest */
    public CreateXMLTest(String testName) {
        super(testName);
    }
    
    public void testCreateXML() throws Exception {
        String content
        = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n"
        + "<root/>\n";
        
        // delete document if exists
        DataObject dao = TestUtil.THIS.findData(DOCUMENT_NAME + '.' + XML_EXT);
        if (dao != null) dao.delete();
        // create new Data Object
        DataFolder dataFolder = DataFolder.findFolder(TestUtil.THIS.findData("").getPrimaryFile());
        XMLDataObject xmlDataObject = (XMLDataObject) TestUtil.createDataObject(dataFolder, DOCUMENT_NAME, XML_EXT, content);
        TreeDocument document = xmlDataObject.getTreeDocument();
        
        // Create Document Type
        DTDDataObject dtdDataObject = (DTDDataObject) TestUtil.THIS.findData(INTERNAL_DTD);
        TreeDTD treeDTD = dtdDataObject.getTreeDTD();
        //TreeDocumentType docType = document.getDocumentType();
        TreeDocumentType docType = new TreeDocumentType(DOCUMENT_NAME);
        docType.setSystemId(DTD_SYS_ID);
        TreeChild child = treeDTD.getFirstChild();
        while (child != null) {
            docType.appendChild((TreeChild) child.clone());
            child = child.getNextSibling();
        }
        document.setDocumentType(docType);
        
        // Create document
        TreeElement root = document.getDocumentElement();
        // Create root node
        root.addAttribute(new TreeAttribute("manager", "Tom Jerry"));
        root.addAttribute("id", "a");
        // Create node Product
        TreeElement product = new TreeElement("Product");
        root.appendChild(product);
        root.appendChild(new TreeText("\n"));
        product.addAttribute("isbn", "123456");
        product.addAttribute(new TreeAttribute("id", "b"));
        product.appendChild(new TreeText("\nXML Book\n"));
        // Create node Descript
        TreeElement descript = new TreeElement("Descript");
        product.appendChild(descript);
        product.appendChild(new TreeText("\n"));
        descript.addAttribute("lang", "Eng");
        descript.appendChild(new TreeText("\n"));
        descript.appendChild(new TreeText("The book describe how is using XML in"));
        descript.appendChild(new TreeText("\n"));
        descript.appendChild(new TreeGeneralEntityReference("company"));
        descript.appendChild(new TreeText("from "));
        descript.appendChild(new TreeGeneralEntityReference("cz"));
        descript.appendChild(new TreeText("\n"));
        descript.appendChild(new TreeText("Very important is author\n"));
        descript.appendChild(new TreeGeneralEntityReference("notice"));
        descript.appendChild(new TreeText("\n"));
        
        TestUtil.saveDataObject(xmlDataObject);
        ref(TestUtil.nodeToString(document));
        compareReferenceFiles();
    }
    
    /**
     * Performs this testsuite.
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        TestRunner.run(CreateXMLTest.class);
    }
}
