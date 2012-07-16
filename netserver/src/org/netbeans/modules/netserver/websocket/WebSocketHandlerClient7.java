/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */
package org.netbeans.modules.netserver.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.charset.Charset;
import java.util.List;


/**
 * @author ads
 *
 */
class WebSocketHandlerClient7 extends AbstractWSHandler7 {

    WebSocketHandlerClient7( WebSocketClient webSocketClient, int version ){
        client = webSocketClient;
        this.version = version;
    }

    /* (non-Javadoc)
     * @see org.netbeans.modules.netserver.websocket.WebSocketChanelHandler#sendHandshake()
     */
    @Override
    public void sendHandshake() {
        StringBuilder builder = new StringBuilder(Utils.GET);
        builder.append(' ');
        builder.append(getClient().getUri().getPath());
        builder.append(' ');
        builder.append( Utils.HTTP_11);
        builder.append(Utils.CRLF);
        
        builder.append(Utils.WS_UPGRADE);
        builder.append(Utils.CRLF);
        
        builder.append(Utils.HOST);
        builder.append(": ");                               // NOI18N
        builder.append(getClient().getUri().getHost());
        builder.append(Utils.CRLF);
        
        if ( version >= 7 && version<= 10){
            builder.append("Sec-WebSocket-Origin: ");
        }
        else {
            builder.append("Origin: ");
        }
        builder.append( Utils.getOrigin(getClient().getUri()));
        
        builder.append("Sec-WebSocket-Protocol: chat");     // NOI18N
        builder.append( Utils.CRLF );
        
        builder.append("Sec-WebSocket-Version: ");          // NOI18N
        if ( version == 7 ){
            builder.append( version );
        }
        else if ( version > 7 && version <13){
            builder.append( 8 );
        }
        else {
            builder.append( 13 );
        }
        builder.append( Utils.CRLF );
        
        builder.append( Utils.KEY);
        builder.append(": ");
        builder.append( generateKey());
        
        builder.append( Utils.CRLF );
        builder.append( Utils.CRLF );
        
        
        getClient().send(builder.toString().getBytes( 
                Charset.forName(Utils.UTF_8)), client.getKey() );
    }

    /* (non-Javadoc)
     * @see org.netbeans.modules.netserver.websocket.WebSocketChanelHandler#read(java.nio.ByteBuffer)
     */
    @Override
    public void read( ByteBuffer byteBuffer ) throws IOException {
        if ( handshakeRed ){
            super.read(byteBuffer);
        }
        else {
            readHandshake( byteBuffer );
            handshakeRed = true;
            getClient().getWebSocketReadHandler().accepted(getKey());
        }
    }

    /* (non-Javadoc)
     * @see org.netbeans.modules.netserver.websocket.AbstractWSHandler7#isClient()
     */
    @Override
    protected boolean isClient() {
        return true;
    }
    
    protected WebSocketClient getClient(){
        return client;
    }
    
    /* (non-Javadoc)
     * @see org.netbeans.modules.netserver.websocket.AbstractWSHandler7#close()
     */
    @Override
    protected void close() throws IOException {
        getClient().close( getKey());
    }
    
    /* (non-Javadoc)
     * @see org.netbeans.modules.netserver.websocket.AbstractWSHandler7#readDelegate(byte[], int)
     */
    @Override
    protected void readDelegate( byte[] bytes, int dataType ) {
        getClient().getWebSocketReadHandler().read(getKey(), bytes, dataType);
    }
    
    /* (non-Javadoc)
     * @see org.netbeans.modules.netserver.websocket.AbstractWSHandler7#getKey()
     */
    @Override
    protected SelectionKey getKey() {
        return getClient().getKey();
    }

    /* (non-Javadoc)
     * @see org.netbeans.modules.netserver.websocket.AbstractWSHandler7#verifyMask(boolean)
     */
    @Override
    protected boolean verifyMask( boolean hasMask ) throws IOException {
        return true;
    }
    
    /* (non-Javadoc)
     * @see org.netbeans.modules.netserver.websocket.AbstractWSHandler7#isStopped()
     */
    @Override
    protected boolean isStopped() {
        return getClient().isStopped();
    }
    
    private void readHandshake( ByteBuffer buffer ) throws IOException {
        List<String> headers = Utils.readHttpRequest(getClient().getChannel(), 
                buffer);
        String acceptKey =null;
        String accept = Utils.ACCEPT+':';
        for (String header : headers) {
            if ( header.startsWith(accept))
            {
                acceptKey = header.substring(accept.length()).trim();
            }
        }
        // TODO : check acceptKey against provided generatedKey at initial handshake 
        if ( acceptKey == null ){
            throw new IOException("Wrong accept key on handshake received");    // NOI18N
        }
    }
    
    private String generateKey() {
        // TODO write generation of random key 
        return "dGhlIHNhbXBsZSBub25jZQ==";
    }
    
    private boolean handshakeRed;
    private WebSocketClient client;
    private int version;

}
