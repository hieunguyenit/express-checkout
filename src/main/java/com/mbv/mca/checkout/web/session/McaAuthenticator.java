package com.mbv.mca.checkout.web.session;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mbv.mca.MCAException;
import com.mbv.mca.MCAMessage;
import com.mbv.mca.MCAUser;
import com.mbv.mca.checkout.service.McaMessagingService;
import com.mbv.mca.checkout.web.SessionKeys;
import com.mbv.services.session.SessionInfo;

/**
 * implementation for authentication and authorization logics.
 * @author Nam Pham
 *
 */
@Component("mcaAuthenticator")
public class McaAuthenticator implements SessionKeys {
	private static final Log LOG = LogFactory.getLog(McaAuthenticator.class);
	
	@Autowired
	McaMessagingService messagingService;
	
	/**
	 * get authenticated session from servlet request
	 * @return
	 * @throws McaException
	 */
	public HttpSession getAuthenticatedSession(HttpServletRequest req) throws McaException {
		HttpSession ses = req.getSession(false);
		if (ses == null) throw new UnauthenticatedException("session not found");
		
		return ses;
	}

	public MCAUser getAuthenticatedUser(HttpSession ses) throws McaException {
		SessionInfo sess = (SessionInfo) ses.getAttribute(SESS_SESSION_INFO);
		MCAUser user = (MCAUser)ses.getAttribute(SESS_USER_INFO);
		if (sess==null || user==null || ! sess.getLoginId().equals(user.getLoginId()))
			throw new UnauthenticatedException("login id of session and user are different");
		return user;
	}
	
	/**
	 * 
	 * @param req
	 * @return
	 * @throws McaException
	 */
	public MCAUser getAuthenticatedUser(HttpServletRequest req) throws McaException {
		HttpSession ses = getAuthenticatedSession(req);
		return getAuthenticatedUser(ses);
	}
	
	/**
	 * validate if a thread request has been authorized with a specific type
	 * @param type authorization type, this can be null;
	 * @throws McaException
	 */
	public void validateAuthorization(HttpServletRequest req, AuthorizationType type) throws McaException {
		HttpSession sess= getAuthenticatedSession(req);
		Date expiredAt = (Date) sess.getAttribute(SESS_AUTHOZ_EXPIRED_AT);
		boolean validated = false;
		
		if (expiredAt==null || expiredAt.getTime()<System.currentTimeMillis()) {
			MCAUser user = getAuthenticatedUser(req);
			String msg = "authorization expired for user " + user.getUserId();
			LOG.info(msg);
		}
		else if (type!=null) {
			AuthorizationType at = (AuthorizationType) sess.getAttribute(SESS_AUTHOZ_TYPE);
			if (at==null || !at.equals(type)) {
				MCAUser user = getAuthenticatedUser(req);
				String msg = "authorization type is not " + type + " for user " +  user.getUserId();
				LOG.info(msg);
			}
			else {
				validated = true;
			}
		}
		else {
			validated = true;
		}
		
		if (validated) return;
		if (type==null)
			throw new AuthorizationRequiredException(this.getAuthorizationMethods(getAuthenticatedUser(req)), "authorization expired");
		else {
			String method = type.name().toLowerCase();
			requestAuthozChallenge(req, method);
			throw new AuthorizationRequiredException(Arrays.asList(method), "authorization expired");
		}
		
	}
	
	/**
	 * return possible authorization methods 
	 * @param user
	 * @return
	 */
	public List<String> getAuthorizationMethods(MCAUser user) {
		List<String> methods = new ArrayList<String>();
		
		@SuppressWarnings("unchecked")
		Map<String, Object> appData = (Map<String, Object>) user.getValue(".mca-user");
		
		String auth = appData!=null&&appData.containsKey("auth")?(String)appData.get("auth"):"any";
		
		@SuppressWarnings("unchecked")
		Map<String, Object> verified = (Map<String, Object>) user.getValue("verified");
		if (!StringUtils.isEmpty(user.getMobile()) && 
			user.getMobile().equals("" + verified.get("mobile")))
			if (auth.equals("any") || auth.equals("sms"))
				methods.add("sms");
		
		if (appData!=null && appData.containsKey("totp") &&
			(auth.equals("any") || auth.equals("totp"))) 
			methods.add("totp");
		
		return methods;
	}
	
	/**
	 * request challenge for authorization
	 * @param request
	 * @param method
	 * @return
	 * @throws McaException
	 */
	public String requestAuthozChallenge(HttpServletRequest request, String method) throws McaException {
		HttpSession sess = getAuthenticatedSession(request);
		MCAUser user = getAuthenticatedUser(request);
		
		//@SuppressWarnings("unchecked")
		//Map<String, Object> appData = (Map<String, Object>) user.getValue(".mca-user");
		//String authMethod = appData!=null ? (String)appData.get("auth"):"any";
		//if (method!=null && !authMethod.equals("any") && !method.equals(authMethod))
		//	throw new AuthorizationRequiredException(Arrays.asList(new String[]{authMethod}), "invalid method: " + method);
		
		@SuppressWarnings("unchecked")
		Map<String, Object> data = (Map<String, Object>) sess.getAttribute(SESS_AUTHOZ_DATA);
		if (data==null) sess.setAttribute(SESS_AUTHOZ_DATA, data = new LinkedHashMap<String, Object>());
		String challenge;
		if (method.equals("sms")) {
			challenge = issueSmsChallenge(data, user);
		}
		else if (method.equals("totp"))
			challenge = issueTOTPChallenge(data, user);
		else
			throw new McaException("only sms and totp are implemented");
		
		sess.setAttribute(SESS_AUTHOZ_TYPE, AuthorizationType.valueOf(method.toUpperCase()));
		
		LOG.info("user " + user.getUserId() + " has request an authorization challenge of type " + method);
		
		return challenge;
	}
	
	/**
	 * 
	 * @param data
	 * @param user
	 * @return
	 */
	private String issueTOTPChallenge(Map<String, Object> data, MCAUser user) {
		@SuppressWarnings("unchecked")
		Map<String, Object> mcaUser = (Map<String, Object>) user.getValue(".mca-user");
		if (mcaUser==null || mcaUser.get("totp")==null)
			return "setup";
		return "ready";
	}

	/**
	 * send an SMS to user mobile as an authorization challenge
	 * @param sess
	 * @param user
	 * @return
	 * @throws McaException
	 */
	private String issueSmsChallenge(Map<String, Object> data, MCAUser user) throws McaException {
		String oldCode = (String) data.get("code");
		if (!StringUtils.isEmpty(oldCode)) {
			Date issuedAt = (Date) data.get("issuedAt");
			if (issuedAt!=null && issuedAt.getTime() + 2*60*1000 >= System.currentTimeMillis()) {
				LOG.info("issued sms is not expired for user " + user.getUserId());
				return (String) data.get("mobile");
			}
			
			// TODO store it in application-wide storage
			@SuppressWarnings("unchecked")
			List<String> codes = (List<String>) data.get("ignoredCodes");
			if (codes==null) data.put("ignoredCodes", codes = new ArrayList<String>(5));
			if (codes.size() < 5) {
				codes.add(oldCode);
			}
			else {
				LOG.warn("report error for " + user.getUserId() + " since too many code ignored"); 
				throw new McaException("too many ingored codes");
			}
		}
		
		String mobile = user.getMobile();
		if (StringUtils.isEmpty(mobile)) throw new McaException("user has no mobile " + user.getMobile());
		MCAMessage sms = new MCAMessage();
		sms.setFrom("sys:authorization");
		sms.setTo(mobile);
		sms.setType("text/outgoing-sms");
		String str = String.valueOf(Math.abs(new SecureRandom().nextInt())%10000);
		while (str.length()<4)
			str = "0" + str;
		sms.setBody("MCA Authorization Code: " + str);
		try {
			if (LOG.isDebugEnabled())
			LOG.debug("sending SMS code to user " + user.getUserId() + " " + str);
			messagingService.postMessage(sms);
		} catch (MCAException e) {
			String msg = "failed to send SMS message " + e.getMessage();
			LOG.error(msg, e);
			throw new McaException(msg, e);
		}
		data.put("mobile", mobile);
		data.put("code", str);
		data.put("failureCount", 0);
		data.put("issuedAt", new Date());
		return mobile;
	}
	
	/**
	 * verify authorization token
	 * @param request
	 * @param token
	 * @throws McaException
	 */
	public Date verifyAuthozToken(HttpServletRequest request, String token) throws McaException {
		HttpSession sess = getAuthenticatedSession(request);
		MCAUser user = getAuthenticatedUser(request);
		AuthorizationType type = (AuthorizationType) sess.getAttribute(SESS_AUTHOZ_TYPE);
		boolean verified;
		if (type.equals(AuthorizationType.SMS))
			verified = verifySmsChallenge(sess, token);
		else if (type.equals(AuthorizationType.TOTP))
			verified = verifyTOTPChallenge(user, token);
		else
			throw new McaException("only sms and totp are implemented");
		
		if (!verified) {
			if (LOG.isDebugEnabled())
				LOG.debug("user " + user.getUserId() + " failed to authorized with " + type);
			return null;
		}
		
		LOG.info("user " + user.getUserId() + " has authorize session successfully"); 
		
		// set expired as 15 mins
		Date expiredAt = new Date(System.currentTimeMillis() + 15*60*1000);
		sess.setAttribute(SESS_AUTHOZ_EXPIRED_AT, expiredAt);
		
		return expiredAt;
	}
	
	
	/**
	 * 
	 * @param sess
	 * @param token
	 * @throws McaException
	 */
	private boolean verifyTOTPChallenge(MCAUser user, String token) throws McaException {
		@SuppressWarnings("unchecked")
		Map<String, Object> mcaUser = (Map<String, Object>) user.getValue(".mca-user");
		if (mcaUser==null) throw new McaException(".mca-user data is not found in user object");
		@SuppressWarnings("unchecked")
		Map<String, Object> totp = (Map<String, Object>) mcaUser.get("totp");
		if (totp==null) throw new McaException(".mca-user.totp data is not found in user object");
		
		String b32key = (String)totp.get("b32key");
		int offset = ((Number)totp.get("offset")).intValue();
		long counter =  System.currentTimeMillis() / 30000L;
		
		// generate code and compare
		try {
			long otp = Long.parseLong(token.trim());
			return TOTP.checkOTP(b32key, otp, counter + offset, 1);
		}
		catch (Throwable ex) {
			LOG.warn("failed to verify otp for user " + user.getUserId() + ": " + ex.getMessage());
			return false;
		}
	}

	/**
	 * verify if SMS token is correct 
	 * @param sess
	 * @param token
	 * @throws McaException
	 */
	private boolean verifySmsChallenge(HttpSession sess, String token) throws McaException {
		@SuppressWarnings("unchecked")
		Map<String, Object> data = (Map<String, Object>) sess.getAttribute(SESS_AUTHOZ_DATA);
	
		if (data==null) throw new McaException("SMS authorization data not found");
		String code = (String) data.get("code");
		if (StringUtils.isEmpty(code)) throw new McaException("SMS authorization code is not found");
		if (!code.equals(token)) {
			int failureCount = ((Number)data.get("failureCount")).intValue() + 1;
			if (failureCount==5) {
				// force regeneration
				data.remove("issuedAt");
				issueSmsChallenge(data, getAuthenticatedUser(sess));
			}
			else
				data.put("failureCount", failureCount);
			return false;
		}
		
		return true;
	}

	
}
