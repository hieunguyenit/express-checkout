package com.mbv.mca.checkout.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * return the actual information about proxied server and client
 * @author Nam Pham
 *
 */
public class ProxiedRequestFilter implements Filter {
	String schemeHeader = "X-Forwarded-Scheme";
	String serverNameHeader = "X-Forwarded-Server-Name";
	String serverPortHeader = "X-Forwarded-Server-Port";
	String remoteAddrHeader = "X-Forwarded-Remote-Addr";
	
	
	class RequestWrapper extends HttpServletRequestWrapper {
		String scheme;
		String host;
		int port = -1;
		String remoteAddr;
		String requestURL;

		public RequestWrapper(HttpServletRequest request) {
			super(request);
			scheme = request.getHeader(schemeHeader);
			host = request.getHeader(serverNameHeader);
			String sPort = request.getHeader(serverPortHeader);
			if (sPort!=null && !sPort.isEmpty())
				port = Integer.parseInt(sPort);
			
			remoteAddr = request.getHeader(remoteAddrHeader);
			
			if (scheme!=null || host!=null || port!=-1) {
				int nPort = port!=-1?port:super.getServerPort();
				String qs = super.getQueryString();
				requestURL = (scheme!=null?scheme:super.getScheme()) + "://" + 
							(host!=null?host:super.getServerName()) + 
							(nPort!=80 && nPort!=443?":" + nPort:"") +
							super.getRequestURI() + (qs!=null?"?" + qs:"");
			}
			else {
				requestURL = super.getRequestURL().toString();
			}
		}
		
		@Override
		public int getServerPort() {
			return port!=-1?port:super.getServerPort();
		}
		
		@Override
		public String getScheme() {
			return scheme!=null?scheme:super.getScheme();
		}
		
		@Override
		public String getServerName() {
			return host!=null?host:super.getServerName();
		}
		
		@Override
		public String getRemoteAddr() {
			return remoteAddr!=null?remoteAddr:super.getRemoteAddr();
		}
		
		@Override
		public StringBuffer getRequestURL() {
			return requestURL!=null?new StringBuffer(requestURL):super.getRequestURL();
		}
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		if (filterConfig.getInitParameter("schemeHeader")!=null)
			schemeHeader = filterConfig.getInitParameter("schemeHeader");
		
		if  (filterConfig.getInitParameter("serverNameHeader")!=null)
			serverNameHeader = filterConfig.getInitParameter("serverNameHeader");
		
		if  (filterConfig.getInitParameter("serverPortHeader")!=null)
			serverPortHeader = filterConfig.getInitParameter("serverPortHeader");
		
		if (filterConfig.getInitParameter(remoteAddrHeader)!=null)
			remoteAddrHeader = filterConfig.getInitParameter(remoteAddrHeader);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		chain.doFilter(new RequestWrapper((HttpServletRequest) request), response);
	}

	@Override
	public void destroy() {
		// do nothing
	}

}
