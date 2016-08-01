package com.mbv.mca.checkout.service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mbv.mca.checkout.web.ExceptionHandler;
import com.mbv.mca.checkout.web.session.AuthorizationRequiredException;
import com.mbv.mca.checkout.web.session.SessionException;
import com.mbv.mca.checkout.web.session.UnauthenticatedException;


public class ServiceExceptionHandler implements ExceptionHandler {
	private static final Log LOG = LogFactory.getLog(ServiceExceptionHandler.class);

	@Override
	public void handle(HttpServletRequest request,
			HttpServletResponse response, Throwable cause)
			throws ServletException {
		
		int code = 500;
		String message = cause.getMessage();
		if (cause instanceof UnauthenticatedException) {
			code = 401;
		}else if(cause instanceof SessionException){
			code = 401;
		}else if (cause instanceof AuthorizationRequiredException) {
			String[] methods = ((AuthorizationRequiredException)cause).getMethods().toArray(new String[] {});
			response.setHeader("X-Authoz-Methods", StringUtils.join(methods, ','));
			code = 403;
		}
		else {
			StringBuffer buffer = new StringBuffer();
			while (cause!=null) {
				buffer.append("\nCaused by:").append(cause.getClass().getName());
				if (cause.getStackTrace()!=null) {
					for (StackTraceElement trace:cause.getStackTrace()) {
						buffer.append("\n").append(trace.getClassName())
							.append(".").append(trace.getMethodName())
							.append(":").append(trace.getLineNumber());
					}
					if (cause!=cause.getCause())
						cause = cause.getCause();
				}
			}
			message += buffer.toString();
		}
		response.setStatus(code);
		try {
			response.getWriter().write(message);
		}
		catch (Throwable ex) {
			LOG.warn("failed to write error " + ex.getMessage() + ": " + message);
		}
	}

}
