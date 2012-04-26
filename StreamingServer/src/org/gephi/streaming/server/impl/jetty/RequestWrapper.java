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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

/**
 * @author panisson
 *
 */
public class RequestWrapper implements HttpServletRequest {
    
    private final HttpServletRequest request;
    
    public RequestWrapper(HttpServletRequest request) {
        this.request = request;
    }
    
    public ServletInputStream getInputStream() throws IOException {
        request.getInputStream().reset();
        return request.getInputStream();
    }

    public boolean authenticate(HttpServletResponse arg0) throws IOException,
            ServletException {
        return request.authenticate(arg0);
    }

    public AsyncContext getAsyncContext() {
        return request.getAsyncContext();
    }

    public Object getAttribute(String arg0) {
        return request.getAttribute(arg0);
    }

    public Enumeration<String> getAttributeNames() {
        return request.getAttributeNames();
    }

    public String getAuthType() {
        return request.getAuthType();
    }

    public String getCharacterEncoding() {
        return request.getCharacterEncoding();
    }

    public int getContentLength() {
        return request.getContentLength();
    }

    public String getContentType() {
        return request.getContentType();
    }

    public String getContextPath() {
        return request.getContextPath();
    }

    public Cookie[] getCookies() {
        return request.getCookies();
    }

    public long getDateHeader(String arg0) {
        return request.getDateHeader(arg0);
    }

    public DispatcherType getDispatcherType() {
        return request.getDispatcherType();
    }

    public String getHeader(String arg0) {
        return request.getHeader(arg0);
    }

    public Enumeration<String> getHeaderNames() {
        return request.getHeaderNames();
    }

    public Enumeration<String> getHeaders(String arg0) {
        return request.getHeaders(arg0);
    }

    public int getIntHeader(String arg0) {
        return request.getIntHeader(arg0);
    }

    public String getLocalAddr() {
        return request.getLocalAddr();
    }

    public String getLocalName() {
        return request.getLocalName();
    }

    public int getLocalPort() {
        return request.getLocalPort();
    }

    public Locale getLocale() {
        return request.getLocale();
    }

    public Enumeration<Locale> getLocales() {
        return request.getLocales();
    }

    public String getMethod() {
        return request.getMethod();
    }

    public String getParameter(String arg0) {
        return request.getParameter(arg0);
    }

    public Map<String, String[]> getParameterMap() {
        return request.getParameterMap();
    }

    public Enumeration<String> getParameterNames() {
        return request.getParameterNames();
    }

    public String[] getParameterValues(String arg0) {
        return request.getParameterValues(arg0);
    }

    public Part getPart(String arg0) throws IOException, ServletException {
        return request.getPart(arg0);
    }

    public Collection<Part> getParts() throws IOException, ServletException {
        return request.getParts();
    }

    public String getPathInfo() {
        return request.getPathInfo();
    }

    public String getPathTranslated() {
        return request.getPathTranslated();
    }

    public String getProtocol() {
        return request.getProtocol();
    }

    public String getQueryString() {
        return request.getQueryString();
    }

    public BufferedReader getReader() throws IOException {
        return request.getReader();
    }

    public String getRealPath(String arg0) {
        return request.getRealPath(arg0);
    }

    public String getRemoteAddr() {
        return request.getRemoteAddr();
    }

    public String getRemoteHost() {
        return request.getRemoteHost();
    }

    public int getRemotePort() {
        return request.getRemotePort();
    }

    public String getRemoteUser() {
        return request.getRemoteUser();
    }

    public RequestDispatcher getRequestDispatcher(String arg0) {
        return request.getRequestDispatcher(arg0);
    }

    public String getRequestURI() {
        return request.getRequestURI();
    }

    public StringBuffer getRequestURL() {
        return request.getRequestURL();
    }

    public String getRequestedSessionId() {
        return request.getRequestedSessionId();
    }

    public String getScheme() {
        return request.getScheme();
    }

    public String getServerName() {
        return request.getServerName();
    }

    public int getServerPort() {
        return request.getServerPort();
    }

    public ServletContext getServletContext() {
        return request.getServletContext();
    }

    public String getServletPath() {
        return request.getServletPath();
    }

    public HttpSession getSession() {
        return request.getSession();
    }

    public HttpSession getSession(boolean arg0) {
        return request.getSession(arg0);
    }

    public Principal getUserPrincipal() {
        return request.getUserPrincipal();
    }

    public boolean isAsyncStarted() {
        return request.isAsyncStarted();
    }

    public boolean isAsyncSupported() {
        return request.isAsyncSupported();
    }

    public boolean isRequestedSessionIdFromCookie() {
        return request.isRequestedSessionIdFromCookie();
    }

    public boolean isRequestedSessionIdFromURL() {
        return request.isRequestedSessionIdFromURL();
    }

    public boolean isRequestedSessionIdFromUrl() {
        return request.isRequestedSessionIdFromUrl();
    }

    public boolean isRequestedSessionIdValid() {
        return request.isRequestedSessionIdValid();
    }

    public boolean isSecure() {
        return request.isSecure();
    }

    public boolean isUserInRole(String arg0) {
        return request.isUserInRole(arg0);
    }

    public void login(String arg0, String arg1) throws ServletException {
        request.login(arg0, arg1);
    }

    public void logout() throws ServletException {
        request.logout();
    }

    public void removeAttribute(String arg0) {
        request.removeAttribute(arg0);
    }

    public void setAttribute(String arg0, Object arg1) {
        request.setAttribute(arg0, arg1);
    }

    public void setCharacterEncoding(String arg0)
            throws UnsupportedEncodingException {
        request.setCharacterEncoding(arg0);
    }

    public AsyncContext startAsync() throws IllegalStateException {
        return request.startAsync();
    }

    public AsyncContext startAsync(ServletRequest arg0, ServletResponse arg1)
            throws IllegalStateException {
        return request.startAsync(arg0, arg1);
    }
    


//
//    /* (non-Javadoc)
//     * @see org.gephi.streaming.server.impl.Request#getParameter(java.lang.String)
//     */
//    public String getParameter(String arg0) throws IOException {
//        return request.getParameter(arg0);
//    }
//
//    /* (non-Javadoc)
//     * @see org.gephi.streaming.server.impl.Request#getHeader(java.lang.String)
//     */
//    public String getHeader(String arg0) {
//        return request.getHeader(arg0);
//    }
//
//    public String getClientAddress() {
//        return request.getRemoteAddr();
//    }
//
//    public Enumeration getAttributeNames() {
//        return request.getAttributeNames();
//    }
//    
//    public Object getAttribute(String name) {
//        return request.getAttribute(name);
//    }
//    
//    public void setAttribute(String name, Object value) {
//        request.setAttribute(name, value);
//    }

}
