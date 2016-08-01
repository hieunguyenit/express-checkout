package com.mbv.mca.checkout.web.session;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import com.mbv.account.soap.session.SessionExceptionFault;
import com.mbv.account.soap.session.SessionService;
import com.mbv.mca.MCAUser;
import com.mbv.mca.checkout.web.SessionKeys;
import com.mbv.services.ServicePool;
import com.mbv.services.session.SessionInfo;

/**
 * verivy if {@link SessionInfo} match {@link MCAUser#getLoginId()}
 * @author Nam Pham
 *
 */
@Component("mcaUserFilter")
public class McaUserFilter extends GenericFilterBean implements SessionKeys {
	private static final Log LOG = LogFactory.getLog(McaUserFilter.class);
	
	ServicePool<SessionService> sessionPool;
	
	String sessionCookieName = "mbvsess";
	
	String sessionParamName = "mbvsess";
	
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
	
	void updateSession(HttpServletRequest req, String sessId) throws IOException, ServletException {
		SessionService ss = null;
		SessionInfo session = null;
		try {
			ss = sessionPool.acquire();
			session = ss.getSession(sessId);
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
				req.getSession(true).setAttribute(SESS_SESSION_INFO, session);
			}
		}
	}
		
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		HttpSession http = req.getSession(false);
		if (http==null) throw new SessionException("HttpSession is not created");
		
		SessionInfo sess = (SessionInfo)http.getAttribute(SESS_SESSION_INFO);
		if (sess==null) throw new SessionException("SessionInfo is not found");
		if (!sess.isAuthenticated()) throw new SessionException("SessionInfo is not authenticated");
		
//		MCAUser user = (MCAUser)http.getAttribute(SESS_USER_INFO);
//		if (user==null) throw new SessionException("MCAUser is not found");
//		
//		if (!user.getLoginId().equals(sess.getLoginId())) {
//			String msg = "loginId of MCAUser and SessionInfo does not match: " 
//					+ user.getLoginId() + " vs " + sess.getLoginId();
//			throw new UserNotMatchException(msg);
//		}
		
		LOG.debug("MCA User Filter " + request.getParameter("checkoutId"));
		
		chain.doFilter(request, response);
	}

}
