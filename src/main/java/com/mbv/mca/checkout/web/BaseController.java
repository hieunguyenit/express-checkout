package com.mbv.mca.checkout.web;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.mbv.account.soap.session.SessionService;
import com.mbv.mca.MCAUser;
import com.mbv.mca.MCAUserFilter;
import com.mbv.mca.checkout.core.CheckoutErrorMsg;
import com.mbv.mca.checkout.core.CheckoutException;
import com.mbv.mca.checkout.jdo.CheckoutRequestDao;
import com.mbv.mca.checkout.service.McaCoreService;
import com.mbv.mca.checkout.service.McaOtpService;
import com.mbv.mca.checkout.service.MemcachedService;
import com.mbv.services.ServicePool;
import com.mbv.services.session.SessionInfo;

@Controller
public class BaseController implements SessionKeys {
	// Logger
	private static final Logger LOG = Logger.getLogger(BaseController.class);

	static final String DATA_FIELD = "data";
	static final String ERROR_FIELD = "error";

	@Autowired
	View jsonView;
	
	// Command Type
	@Resource(name = "checkoutHandlerMap")
	Map<String, Object> handlerMap;
	
	// Installment Type
	@Resource(name = "installmentMap")
	Map<String, String> installmentMap;

	// Memcached
	@Autowired
	@Qualifier("memcachedService")
	MemcachedService cache;
	
	// Checkout DAO
	@Autowired
	@Qualifier("checkoutDAO")
	CheckoutRequestDao checkoutDAO;

	@Autowired
	@Qualifier("mcaCoreService")
	McaCoreService mcaCoreService;
	
	@Autowired
	@Qualifier("mcaOtpService")
	McaOtpService otpService;

	// Account Session Pool
	@Autowired
	@Qualifier("sessionPool")
	ServicePool<SessionService> sessionPool;

	/**
	 * Create an error REST response.
	 * 
	 * @param sMessage
	 *            the s message
	 * @return the model and view
	 */
	ModelAndView createErrorResponse(String sMessage) {
		return new ModelAndView(jsonView, ERROR_FIELD, sMessage);
	}
	

	/*
	 * 
	 */
	 boolean issueOtp(MCAUser user, String checkoutId){
		boolean result;
		
		try {
			otpService.requestAuthozChallenge(user, checkoutId);
			result = true;

		} catch (Exception e) {
			result = false;
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param mcaCoreService
	 * @param request
	 * @param loginId
	 * @return
	 * @throws Exception
	 */
	MCAUser getMcaUser(McaCoreService mcaCoreService,
			HttpServletRequest request, String loginId) throws Exception {
		MCAUser result = null;

		//
		MCAUserFilter filter = new MCAUserFilter();
		filter.setValue("loginId", loginId);
		filter.setPageSize(100);

		List<MCAUser> users = null;
		
		try {
			 users = mcaCoreService.searchUser(filter).getItems();
		} catch (Exception e) {
			throw new CheckoutException(CheckoutErrorMsg.MCA_USER_UNAVAILABLE);
		}
		
		//
		for (int i = 0; i < users.size(); i++) {
			MCAUser user = users.get(i);
			if (user.getStatus() == null) {
				user.setStatus(MCAUser.Status.active);
			}
			if (user.getStatus().equals(MCAUser.Status.terminated)) {
				users.remove(i);
				i--;
			}
		}

		if (users.size() == 0) {
			// perform session logout
			logout(request);
			throw new CheckoutException(CheckoutErrorMsg.MCA_USER_NOT_FOUND);
		} 
		else {
			MCAUser user = users.get(0);
			MCAUser.Status status = user.getStatus();

			if (!status.equals(MCAUser.Status.active)) {
				throw new CheckoutException(CheckoutErrorMsg.MCA_USER_INACTIVE);
			} else {
				loginUser(request, user);
			}

			result = user;
		}

		return result;
	}

	/*
	 * 
	 */
	void loginUser(HttpServletRequest req, MCAUser user) {
		// TODO do something to make user session destroyable by admin
		// check if user mobile has bean verified
		boolean mobileVerified = true;

		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> verified = (Map<String, Object>) user
					.getMapValue("verified");

			if (verified == null || !verified.containsKey("mobile")
					|| !verified.get("mobile").equals(user.getMobile())) {
				mobileVerified = false;
			}

		} catch (Throwable ex) {
			LOG.warn(".verified of " + user.getUserId() + " is not a map: "
					+ user.getValue("verified"));
			mobileVerified = false;
		}

		// set user to session
		req.getSession().setAttribute(SESS_USER_INFO, user);

	}

	/*
	 * 
	 */
	void logout(HttpServletRequest request) {
		HttpSession hs = request.getSession(false);

		if (hs != null) {
			SessionInfo sess = (SessionInfo) hs.getAttribute(SESS_SESSION_INFO);

			if (sess != null) {
				SessionService ss = null;

				try {
					ss = sessionPool.acquire();
					sess.setExpiredAt(new Date(0));
					ss.setSession(sess);

				} catch (Throwable ex) {
					LOG.warn("failed to logout session " + sess.getSessionId(),
							ex);
				} finally {
					if (ss != null)
						sessionPool.release(ss);
				}
			}
			hs.invalidate();
		}

	}

}
