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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import org.gephi.streaming.server.Request;
import org.openide.util.Exceptions;

/**
 * @author panisson
 *
 */
public class RequestWrapper implements Request {

    public final static String SOCKET_REFERENCE_KEY = "SOCKET_REFERENCE_KEY";
    
    private HttpServletRequest request;
    private byte[] data;
    
    public RequestWrapper(HttpServletRequest request) {
        InputStream inputStream = null;
        try {
            this.request = request;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            inputStream = request.getInputStream();
            int read;
            while ((read = inputStream.read())!=-1) {
                baos.write(read);
            }
            data = baos.toByteArray();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                inputStream.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.gephi.streaming.server.impl.Request#getInputStream()
     */
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(data); //request.getInputStream();
    }

    /* (non-Javadoc)
     * @see org.gephi.streaming.server.impl.Request#getParameter(java.lang.String)
     */
    public String getParameter(String arg0) throws IOException {
        return request.getParameter(arg0);
    }

    /* (non-Javadoc)
     * @see org.gephi.streaming.server.impl.Request#getHeader(java.lang.String)
     */
    public String getHeader(String arg0) {
        return request.getHeader(arg0);
    }

    public String getClientAddress() {
        return request.getRemoteAddr();
    }

    public Enumeration getAttributeNames() {
        return request.getAttributeNames();
    }
    
    public Object getAttribute(String name) {
        return request.getAttribute(name);
    }
    
    public void setAttribute(String name, Object value) {
        request.setAttribute(name, value);
    }

}
