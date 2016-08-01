package com.mbv.mca.checkout.web.session;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mbv.mca.checkout.web.ExceptionHandler;

/**
 * Redirect session exception to login URL
 * 
 * @author Nam Pham
 *
 */
@Component("sessionLoginExceptionHandler")
public class SessionLoginExceptionHandler implements ExceptionHandler {
	private static final Log LOG = LogFactory.getLog(SessionLoginExceptionHandler.class);
	
	String loginUrl;
	
	String continueParamName="continue";
	
	String accountLoginUrl;
	
	String schemeHeader = "X-Forwarded-Scheme";
	String hostHeader = "X-Forwarded-Server-Name";
	String portHeader = "X-Forwarded-Server-Port";
	String schemeHostPortHeader = "X-Forwarded-SHP";
	
	public void setSchemeHeader(String schemeHeader) {
		this.schemeHeader = schemeHeader;
	}

	public void setHostHeader(String hostHeader) {
		this.hostHeader = hostHeader;
	}

	public void setPortHeader(String portHeader) {
		this.portHeader = portHeader;
	}

	public void setSchemeHostPortHeader(String schemeHostPortHeader) {
		this.schemeHostPortHeader = schemeHostPortHeader;
	}

	@Autowired
	@Value("${login.url:/web/login.htm}")
	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}
	
	@Autowired
	@Value("${account.url}/login?service=mca")
	public void setAccountLoginUrl(String url) {
		accountLoginUrl = url;
	}

	public void setContinueParamName(String continueParamName) {
		this.continueParamName = continueParamName;
	}


	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Throwable exception) throws ServletException{
		String backUrl;
		try {
			//schemeHostPort
			String schemeHostPort = request.getHeader(schemeHostPortHeader);
			if (schemeHostPort==null || schemeHostPort.isEmpty()) {
				String scheme = request.getHeader(schemeHeader);
				scheme = scheme!=null?scheme:request.getScheme();
				String port = request.getHeader(portHeader);
				port = (port!=null)?
						(port.equals("80") || port.equals("443")?"":(":" + port)):
						(request.getServerPort()==80 || request.getServerPort()==443?"":(":" + request.getServerPort()));
				String host = request.getHeader(hostHeader);
				if (host==null || host.isEmpty())
					host = request.getServerName();
				schemeHostPort = scheme + "://" + host + port;
			}
			
			// value to continue after account site redirection
			String accessUrl = request.getParameter(continueParamName);
			
			// get current url
			if (accessUrl==null) {
				accessUrl = schemeHostPort + request.getRequestURI() 
					+ (request.getQueryString()==null || request.getQueryString().isEmpty()?"":"?" + request.getQueryString());
			}
			// continue param
//			String contParam =  continueParamName + "=" + URLEncoder.encode(accessUrl, "UTF-8");
//			
//			backUrl = schemeHostPort + request.getContextPath() + "?" + contParam;
			
			backUrl = accessUrl;
			backUrl = URLEncoder.encode(backUrl, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("this can't happen", e);
		}
		
		String redirect = accountLoginUrl  
				+ (accountLoginUrl.indexOf('?')==-1? "?":"&") 
				+ "continue=" + backUrl; 
		
		try {
			response.sendRedirect(redirect);
		} catch (IOException e) {
			String msg = "Failed to send redirect to " + redirect;
			LOG.debug(msg, e);
			throw new ServletException(msg, e);
		}
	}
}
