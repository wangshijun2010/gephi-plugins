/*
Copyright 2008-2010 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>, Andre Panisson <panisson@gmail.com>
Website : http://www.gephi.org

This file is part of Gephi.

DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

Copyright 2011 Gephi Consortium. All rights reserved.

The contents of this file are subject to the terms of either the GNU
General Public License Version 3 only ("GPL") or the Common
Development and Distribution License("CDDL") (collectively, the
"License"). You may not use this file except in compliance with the
License. You can obtain a copy of the License at
http://gephi.org/about/legal/license-notice/
or /cddl-1.0.txt and /gpl-3.0.txt. See the License for the
specific language governing permissions and limitations under the
License.  When distributing the software, include this License Header
Notice in each file and include the License files at
/cddl-1.0.txt and /gpl-3.0.txt. If applicable, add the following below the
License Header, with the fields enclosed by brackets [] replaced by
your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 3, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 3] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 3 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 3 code and therefore, elected the GPL
Version 3 license, then the option applies only if the new code is
made subject to such option by the copyright holder.

Contributor(s):

Portions Copyrighted 2011 Gephi Consortium.
 */
package org.gephi.streaming.server.impl.jetty;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.SocketException;
import java.nio.channels.SocketChannel;

import javax.servlet.http.HttpServletResponse;
import org.gephi.streaming.server.Response;


/**
 * A simple wrapper for a org.simpleframework.http.Response object.
 * 
 * @author panisson
 *
 */
public class ResponseWrapper implements Response {
    
    private final HttpServletResponse response;
    private SocketChannel channel;
    private boolean isClosed = false;
    
    /**
     * Creates a wrapper using this response
     * 
     * @param response - the response to be used as delegate
     */
    public ResponseWrapper(HttpServletResponse response, SocketChannel channel) {
        this.response = response;
        this.channel = channel;
    }

    /* (non-Javadoc)
     * @see org.gephi.streaming.server.impl.Response#close()
     */
    public void close() throws IOException {
        response.getOutputStream().close();
    }

    /* (non-Javadoc)
     * @see org.gephi.streaming.server.impl.Response#getOutputStream()
     */
    public OutputStream getOutputStream() throws IOException {
        return new OutputStreamWrapper(response.getOutputStream());
    }

    /* (non-Javadoc)
     * @see org.gephi.streaming.server.impl.Response#getPrintStream()
     */
    public PrintStream getPrintStream() throws IOException {
        return new PrintStream(getOutputStream());
//        return response.getPrintStream();
    }

    /* (non-Javadoc)
     * @see org.gephi.streaming.server.impl.Response#setCode(int)
     */
    public void setCode(int arg0) {
        response.setStatus(arg0);
    }

    /* (non-Javadoc)
     * @see org.gephi.streaming.server.impl.Response#setText(java.lang.String)
     */
    public void setText(String arg0) {
        //throw new UnsupportedOperationException();
    }

    @Override
    public void add(String name,
            String value) {
        response.addHeader(name, value);
    }

    @Override
    public void setDate(String name, long date) {
        response.setDateHeader(name, date);
    }

    @Override
    public void commit() throws IOException {
        response.flushBuffer();
    }
    
    public boolean isClosed() {
        return isClosed;
    }

    /**
     * OutputStream wrapper that is able to detect when the socket
     * was closed.
     * Workaround to the fact that the OutputStream in the simpleframework
     * implementation is buffered and does not throws an exception in the
     * calling thread when the socket was closed by the client.
     */
    private class OutputStreamWrapper extends OutputStream {

        private final OutputStream out;

        public OutputStreamWrapper(OutputStream out) {
            this.out = out;
        }

        @Override
        public void write(int b) throws IOException {
//            if (!channel.isConnected()) {
//                throw new SocketException("Socket closed");
//            }
            out.write(b);
        }

        @Override
        public void close() throws IOException {
            out.close();
            isClosed = true;
        }

        @Override
        public void flush() throws IOException {
//            if (!channel.isConnected()) {
//                throw new SocketException("Socket closed");
//            }
            out.flush();
        }

    }

}
