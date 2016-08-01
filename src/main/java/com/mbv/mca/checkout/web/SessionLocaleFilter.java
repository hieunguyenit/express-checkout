package com.mbv.mca.checkout.web;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;


/**
 * assign read from cookie to assign to {@link SessionLocaleResolver#LOCALE_SESSION_ATTRIBUTE_NAME}
 * @author Nam Pham
 *
 */
public class SessionLocaleFilter implements Filter, SessionKeys {
	private static final Log LOG = LogFactory.getLog(SessionLocaleFilter.class);

	Locale defaultLocale;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String df = filterConfig.getInitParameter("default-locale");
		if (df==null) df = "vi_VN";
		defaultLocale = new Locale(df.substring(0, 2), df.substring(3, 5));
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		
		HttpSession sess = req.getSession(false);
		try {
			if (sess==null) return;
			try {
				Locale locale = (Locale) sess.getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
				if (locale!=null) {
					if (LOG.isTraceEnabled())
						LOG.trace("session  " + sess.getId() + " already has locale " + locale);
					return; 
				}
				if (req.getCookies()==null) return;
				for (Cookie c:req.getCookies()) {
					if (c.getName().equals("locale"))
						try {
							String lc = c.getValue();
							locale = new Locale(lc.substring(0, 2), lc.substring(3, 5));
							break;
						}
						catch (Throwable ex) {
							LOG.error("failed to parse locale from cookie " + c.getValue() + " " + ex.getMessage());
						}
				}
				// TODO read from configuration
				if (locale==null) locale = defaultLocale;
				sess.setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, locale);
				LOG.info("reset locale to " + locale + " for session " + sess.getId());
			}
			catch (ClassCastException ex) {
				LOG.error("Error retrieving locale " + ex.getMessage());
			}
		}
		finally {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {
		// do nothing
	}
}
