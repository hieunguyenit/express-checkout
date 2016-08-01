package com.mbv.mca.checkout.web;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.NestedServletException;

/**
 * translate exception
 * @author Nam Pham
 *
 */

public class ExceptionTranslationFilter extends GenericFilterBean 
implements InitializingBean {
	private static final Log LOG = LogFactory.getLog(ExceptionTranslationFilter.class);
	
	Map<Class<?>, ExceptionHandler> exceptionHandlers;
	
	ExceptionHandler defaultHandler;
	
	public void setExceptionHandlerMap(
			Map<Class<?>, ExceptionHandler> exceptionHandlers) {
		this.exceptionHandlers = exceptionHandlers;
	}
	
	public void setDefaultHandler(ExceptionHandler defaultHandler) {
		this.defaultHandler = defaultHandler;
	}

	public void setExceptionHandlers(
			Map<String, ExceptionHandler> handlerMap) throws ClassNotFoundException {
		
		this.exceptionHandlers = new LinkedHashMap<Class<?>, ExceptionHandler>();
		if (handlerMap!=null)
			for (Map.Entry<String, ExceptionHandler> e:handlerMap.entrySet())
				exceptionHandlers.put(Class.forName(e.getKey()), e.getValue());
	}
	
	public void afterPropertiesSet() throws ServletException {
		super.afterPropertiesSet();
		if (exceptionHandlers==null)
			throw new IllegalStateException("exceptionHandlers is not set");
		
		if (LOG.isDebugEnabled())
			LOG.debug("create exception translation " + exceptionHandlers);
	}
	
	ExceptionHandler getExceptionHandler(Throwable ex) {
		Class<?> c = ex.getClass();
		if (exceptionHandlers.containsKey(c))
			return exceptionHandlers.get(c);
		else {
			for (Map.Entry<Class<?>, ExceptionHandler> e:exceptionHandlers.entrySet()) {
				if (e.getKey().isAssignableFrom(c))
					return e.getValue();
			}
		}
		if (defaultHandler==null)
			return defaultHandler;
		return null;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		try {
			LOG.debug("Exception Translation Filter " + request.getParameter("checkoutId"));
			chain.doFilter(request, response);
		}
		catch (Throwable ex) {
			Throwable cause = ex;
			if (ex instanceof NestedServletException && ex.getCause()!=null) {
				cause = ex.getCause();
			}
			ExceptionHandler handler = getExceptionHandler(cause);
			
			if (handler==null) {
				if (LOG.isDebugEnabled())
					LOG.debug("unhandled exception " + cause.getMessage(), cause);
				if (cause instanceof RuntimeException)
					throw (RuntimeException) cause;
				else
					throw new ServletException("failed to handle request", cause);
			}
			handler.handle((HttpServletRequest)request, (HttpServletResponse)response, cause);
		}
	}

}
