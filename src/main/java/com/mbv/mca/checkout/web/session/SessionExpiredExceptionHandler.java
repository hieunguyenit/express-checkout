package com.mbv.mca.checkout.web.session;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.mbv.mca.checkout.web.ExceptionHandler;

/**
 * Redirect session exception to login URL
 * 
 * @author Nam Pham
 *
 */
@Component("sessionExpiredExceptionHandler")
public class SessionExpiredExceptionHandler implements ExceptionHandler {
	private static final Log LOG = LogFactory.getLog(SessionExpiredExceptionHandler.class);
	
	// Unauthorized
	int statusCode = 401;
	
	String statusMessage = "Session Expired";
	
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public void setStatusMessage(String errorMessage) {
		this.statusMessage = errorMessage;
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Throwable exception) throws ServletException{
		// Unauthorized
		try {
			response.sendError(401, statusMessage);
		} catch (IOException e) {
			String msg = "failed to send error " + e.getMessage();
			LOG.error(msg, e);
			throw new ServletException(msg, e);
		}
	}
}
