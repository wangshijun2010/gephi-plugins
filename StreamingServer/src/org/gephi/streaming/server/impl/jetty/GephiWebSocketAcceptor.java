/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.streaming.server.impl.jetty;

import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketFactory;
import org.gephi.streaming.server.ClientManager;
import org.gephi.streaming.server.impl.ServerOperationExecutor;

/**
 *
 * @author panisson
 */
public class GephiWebSocketAcceptor implements WebSocketFactory.Acceptor {
    private ServerOperationExecutor executor;
    private ClientManager clientManager;

    public GephiWebSocketAcceptor(ServerOperationExecutor executor, ClientManager clientManager) {
        this.executor = executor;
        this.clientManager = clientManager;
    }

    public WebSocket doWebSocketConnect(HttpServletRequest hsr, String string) {
        return new GephiWebSocket(hsr, executor, clientManager);
    }

    public boolean checkOrigin(HttpServletRequest hsr, String string) {
        // Allow all origins
        return true;
    }
    
}
