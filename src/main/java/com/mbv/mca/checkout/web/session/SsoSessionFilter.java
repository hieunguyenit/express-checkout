package com.mbv.mca.checkout.web.session;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import com.mbv.account.soap.session.SessionExceptionFault;
import com.mbv.account.soap.session.SessionService;
import com.mbv.mca.checkout.web.SessionKeys;
import com.mbv.services.ServicePool;
import com.mbv.services.session.SessionInfo;

/**
 * read session information from SingleSignOn service
 * @author Nam Pham
 *
 */
@Component("ssoSessionFilter")
public class SsoSessionFilter extends GenericFilterBean implements SessionKeys {
	private static final Log LOG = LogFactory.getLog(SsoSessionFilter.class);
	
	ServicePool<SessionService> sessionPool;
	
	String sessionCookieName = "mbvsess";
	
	String sessionParamName = "mbvsess";
	
	List<String> allowedLoginDomains;
	
	@Autowired
	@Resource(name="allowedLoginDomains") 
	public void setAllowedLoginDomains(List<String> allowedLoginDomains) {
		this.allowedLoginDomains = allowedLoginDomains;
	}
	
	public void setSessionCookieName(String sessionCookieName) {
		this.sessionCookieName = sessionCookieName;
	}

	public void setSessionParamName(String sessionParamName) {
		this.sessionParamName = sessionParamName;
	}

	@Autowired
	@Resource(name="sessionPool") 
	public void setSessionPool(ServicePool<SessionService> pool) {
		this.sessionPool = pool;
	}
	
	void updateSession(HttpServletRequest req, HttpServletResponse res, String sessId) throws IOException, ServletException {
		SessionService ss = null;
		SessionInfo session = null;
		try {
			ss = sessionPool.acquire();
			SessionInfo sess = ss.getSession(sessId);
			
			String loginId = sess.getLoginId();
			int prefix = loginId.indexOf(':');
			if (prefix==-1) {
				String domainLogin = (String) sess.getMetadata("domain");
				if (domainLogin==null || !allowedLoginDomains.contains(domainLogin)) {
					domainLogin = "mobivi.vn:";
				}else{
					domainLogin = domainLogin+":";
				}
				String added = "end-user".equals(sess.getMetadata("user_type") + "")?
						domainLogin:"unknown:";
				prefix = added.length() - 1;
				loginId = added + loginId;
				sess.setLoginId(loginId);
			}
			String domain = loginId.substring(0, prefix);
			if (!allowedLoginDomains.contains(domain)) {
				String msg = "Untrusted domain: " + domain + " expecting " + allowedLoginDomains;
				LOG.error(msg);
				throw new UntrustedDomainException(msg);
			}
			session = sess;
		}
		catch (RemoteException  ex) {
			LOG.info("Removing session info because of exception " + ex.getMessage());
		}
		catch (SessionExceptionFault ex) {
			LOG.info("Removing session info because of exception " + ex.getMessage());
		}
		catch (InterruptedException ex) {
			LOG.error("Failed to read session: " + ex.getMessage(), ex);
		}
		finally {
			if (ss!=null)
				sessionPool.release(ss);
			if (session==null) {
				HttpSession hs = req.getSession(false);
				if (hs!=null)
					hs.removeAttribute(SESS_SESSION_INFO);
			}
			else {
				Cookie cookie = new Cookie(sessionCookieName, sessId);
				cookie.setPath(req.getContextPath());
				res.addCookie(cookie);
				req.getSession(true).setAttribute(SESS_SESSION_INFO, session);
			}
		}
	}
		
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		String sessionId = req.getParameter(sessionParamName);
		if (sessionId==null || sessionId.isEmpty()) {
			if (req.getCookies()!=null) {
				for (Cookie c:req.getCookies()) {
					if (!c.getName().equals(sessionCookieName)) continue;
					sessionId = c.getValue();
					break;
				}
			}
		}
		if (sessionId != null) {
			HttpServletResponse res = (HttpServletResponse)response;
			updateSession(req, res, sessionId);
		}
		
		LOG.debug("SSO Session Filter " + request.getParameter("checkoutId"));
		
		chain.doFilter(request, response);
	}

}
