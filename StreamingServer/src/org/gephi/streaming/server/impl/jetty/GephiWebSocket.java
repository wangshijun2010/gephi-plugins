/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.streaming.server.impl.jetty;

import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.websocket.WebSocket;
import org.gephi.streaming.server.ClientManager;
import org.gephi.streaming.server.impl.ServerOperationExecutor;
import org.openide.util.Exceptions;

/**
 *
 * @author panisson
 */
public class GephiWebSocket implements WebSocket.OnTextMessage {
    
    private final HttpServletRequest request;
    private Connection connection;
    private final ServerOperationExecutor executor;
    private final ClientManager clientManager;
    private String clientId;
    
    public GephiWebSocket(HttpServletRequest request, ServerOperationExecutor executor, ClientManager clientManager) {
        this.request = request;
        this.executor = executor;
        this.clientManager = clientManager;
    }

    public void onMessage(String arg0) {
    }

    public void onOpen(Connection cnctn) {
        this.connection = cnctn;
        String format = request.getParameter("format");
        if(format==null) {
            // Default format is JSON
            format = "JSON";
        }
        try {
            clientId = clientManager.add(request);
            executor.executeGetGraph(format, clientId, new WSOutputStream());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void onClose(int i, String string) {
        clientManager.remove(clientId);
    }
    
    private class WSOutputStream extends OutputStream {

        @Override
        public void write(int b) throws IOException {
            connection.sendMessage(new byte[]{(byte)b}, 0, 1);
        }
        
        @Override
        public void write(byte b[], int off, int len) throws IOException {
            connection.sendMessage(b, 0, len);
        }
        
        @Override
        public void write(byte b[]) throws IOException {
            connection.sendMessage(b, 0, b.length);
        }
        
    }

}
