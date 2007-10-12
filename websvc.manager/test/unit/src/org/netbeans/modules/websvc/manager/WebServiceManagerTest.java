/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 * 
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.websvc.manager;

import java.io.File;
import java.net.URL;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.websvc.api.jaxws.wsdlmodel.WsdlModel;
import org.netbeans.modules.websvc.api.jaxws.wsdlmodel.WsdlModeler;
import org.netbeans.modules.websvc.api.jaxws.wsdlmodel.WsdlModelerFactory;
import org.netbeans.modules.websvc.manager.model.WebServiceData;
import org.netbeans.modules.websvc.manager.model.WebServiceGroup;
import org.netbeans.modules.websvc.manager.model.WebServiceListModel;
import org.netbeans.modules.websvc.manager.test.DialogDisplayerNotifier;
import org.netbeans.modules.websvc.manager.test.SetupData;
import org.netbeans.modules.websvc.manager.test.SetupUtil;
import org.openide.DialogDisplayer;

/**
 *
 * @author quynguyen
 */
public class WebServiceManagerTest extends NbTestCase {
    private static final String TEST_WSDL_REMOTE = "http://www.webservicemart.com/uszip.asmx.wsdl";
    
    private SetupData testData;
    
    public WebServiceManagerTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        clearWorkDir();
        File testRoot = getWorkDir();
        
        testData = SetupUtil.commonSetUp(testRoot);
        
        assertTrue("Could not set up user directory for test", testData.getWebsvcHome().exists());
        assertTrue("WSDL copy failed", testData.getLocalWsdlFile().exists());
        assertTrue("Catalog copy failed", testData.getLocalCatalogFile().exists());
        
        File endorsedDir = null;
        String endorsedDirs = System.getProperty("java.endorsed.dirs");
        if (endorsedDirs != null) {
            endorsedDir = new File(endorsedDirs);
        }
        
        DialogDisplayer dd = DialogDisplayer.getDefault();
        assertTrue("DialogDisplayer not set correctly", dd instanceof DialogDisplayerNotifier);
        assertTrue("java.endorsed.dirs needs to be set to a valid location", endorsedDir != null && endorsedDir.exists());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        SetupUtil.commonTearDown();
    }

    public void testLocalWebServiceData() throws Exception {
        System.out.println("localWebServiceAdd");
        WebServiceListModel listModel = WebServiceListModel.getInstance();
        
        URL wsdlUrl = testData.getLocalOriginalWsdl().toURI().toURL();
        
        WebServiceData wsData = createWebServiceData(
                testData.getLocalWsdlFile(), 
                wsdlUrl.toExternalForm(), 
                "websvc", 
                testData.getLocalCatalogFile(), 
                WebServiceListModel.DEFAULT_GROUP);
        
        addToListModel(wsData, listModel);
        
        WebServiceGroup defaultGroup = listModel.getWebServiceGroup(WebServiceListModel.DEFAULT_GROUP);
        
        assertEquals("WebServiceData stored in WebServiceListModel not retrieved", wsData, listModel.getWebService(wsData.getId()));
        assertTrue("WebServiceGroup does not link to WebServiceData", defaultGroup.getWebServiceIds().contains(wsData.getId()));
        
        WebServiceManager.getInstance().removeWebService(wsData);
        assertFalse("WSDL not deleted as expected", testData.getLocalWsdlFile().exists());
        assertFalse("Catalog file not deleted as expected", 
                testData.getLocalCatalogFile().exists() || testData.getLocalCatalogFile().getParentFile().exists());
        assertNull("WebServiceData not removed from list model", listModel.getWebService(wsData.getId()));
        assertTrue("Original WSDL deleted improperly", testData.getLocalOriginalWsdl().exists());
    }
    
    public void testRemoteWebServiceData() throws Exception {
        System.out.println("remoteWebServiceAdd");
        WebServiceListModel listModel = WebServiceListModel.getInstance();
        
        WebServiceData wsData = createWebServiceData(
                testData.getLocalWsdlFile(), 
                TEST_WSDL_REMOTE, 
                "websvc", 
                testData.getLocalCatalogFile(), 
                WebServiceListModel.DEFAULT_GROUP);
        
        addToListModel(wsData, listModel);
        
        WebServiceGroup defaultGroup = listModel.getWebServiceGroup(WebServiceListModel.DEFAULT_GROUP);
        
        assertEquals("WebServiceData stored in WebServiceListModel not retrieved", wsData, listModel.getWebService(wsData.getId()));
        assertTrue("WebServiceGroup does not link to WebServiceData", defaultGroup.getWebServiceIds().contains(wsData.getId()));
        
        WebServiceManager.getInstance().removeWebService(wsData);
        assertFalse("WSDL not deleted as expected", testData.getLocalWsdlFile().exists());
        assertFalse("Catalog file not deleted as expected", 
                testData.getLocalCatalogFile().exists() || testData.getLocalCatalogFile().getParentFile().exists());
        assertNull("WebServiceData not removed from list model", listModel.getWebService(wsData.getId()));
    }
    
    private WebServiceData createWebServiceData(File wsdlFile, String original, String packageName, File catalog, String groupId) throws Exception {
        URL wsdlUrl = wsdlFile.toURI().toURL();
        WsdlModeler wsdlModeler = WsdlModelerFactory.getDefault().getWsdlModeler(wsdlUrl);
        WsdlModel model = wsdlModeler.getAndWaitForWsdlModel();
        
        WebServiceData wsData = new WebServiceData(model.getServices().get(0), wsdlFile.getAbsolutePath(), original, groupId);        
        
        wsData.setPackageName(packageName);
        wsData.setCatalog(catalog.getAbsolutePath());
        
        return wsData;
    }
    
    private void addToListModel(WebServiceData wsData, WebServiceListModel listModel) {
        listModel.addWebService(wsData);
        listModel.getWebServiceGroup(wsData.getGroupId()).add(wsData.getId());
    }
    
    
    /**
     * Test of addWebService method, of class WebServiceManager.
     */
    /*
    public void testAddWebService() throws Exception {
        System.out.println("addWebService");

        String packageName = "websvc";
        String groupId = WebServiceListModel.DEFAULT_GROUP;
        URL wsdlUrl = localWsdlFile.toURI().toURL();
        String originalWsdl = localOriginalWsdl.toURI().toURL().toExternalForm();
        
        DialogDisplayerNotifier dd = (DialogDisplayerNotifier)DialogDisplayer.getDefault();
        
        // Objects used for multi-thread success/failure notification
        final TestFlag failBeforeCompile = new TestFlag();
        final TestFlag failAfterAdd = new TestFlag();
        final TestFlag successAfterCompile = new TestFlag();
        
        dd.addListener(new WebServiceManagerListener() {
            public void eventFired(WebServiceManagerEvent event) {
                failBeforeCompile.setValue(true);
                failBeforeCompile.msg = event.getData();
            }
        });
        
        WebServiceListModel.getInstance().addDefaultGroupListener(new WebServiceGroupListener() {

            public void webServiceAdded(WebServiceGroupEvent groupEvent) {
                WebServiceData wsData = WebServiceListModel.getInstance().getWebService(groupEvent.getWebServiceId());
                wsData.addWebServiceDataListener(new WebServiceDataListener() {
                    public void webServiceCompiled(WebServiceDataEvent evt) {
                        successAfterCompile.setValue(true);
                    }
                });
            }

            public void webServiceRemoved(WebServiceGroupEvent groupEvent) {
                failAfterAdd.setValue(true);
            }
        });
        
        
        WsdlModelListenerImpl listener = new WsdlModelListenerImpl(localWsdlFile, originalWsdl, packageName, groupId, localCatalogFile);
        WsdlModeler wsdlModeler = WsdlModelerFactory.getDefault().getWsdlModeler(wsdlUrl);
        listener.setWsdlModeler(wsdlModeler);
        wsdlModeler.setPackageName(packageName);
        wsdlModeler.generateWsdlModel(listener);
        
        // hack to wait until the processes are done
        int counter = 0;
        do {
            counter += 1000;
            Thread.sleep(1000);
        }while ( ! (failBeforeCompile.getValue() || failAfterAdd.getValue() || successAfterCompile.getValue()) &&
                 counter < TIMEOUT);
        
        assertFalse("Failure notified with the following message: " + failBeforeCompile.msg, failBeforeCompile.getValue());
        assertFalse("Failed during client creation", failAfterAdd.getValue());
        assertTrue("Test timed out after " + counter + "ms", counter < TIMEOUT);
    }
     
    private static final class TestFlag {
        private boolean value = false;
        private String msg = "";
        
        public void setValue(boolean v) {
            this.value = v;
        }
        
        public boolean getValue() {
            return value;
        }
    }
     */
}
