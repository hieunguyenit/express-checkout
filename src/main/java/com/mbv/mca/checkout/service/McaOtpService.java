package com.mbv.mca.checkout.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.mbv.mca.MCAException;
import com.mbv.mca.MCAMessage;
import com.mbv.mca.MCAUser;
import com.mbv.mca.checkout.web.SessionKeys;
import com.mbv.mca.checkout.web.session.McaException;
import com.mbv.mca.checkout.web.session.TOTP;


@Component("mcaOtpService")
public class McaOtpService implements SessionKeys{
	private static final Log LOG = LogFactory.getLog(McaOtpService.class);
	
	public static final String OTP = "otp";
	
	@Autowired
	McaMessagingService messagingService;
	
	@Autowired
	@Qualifier("memcachedService")
	MemcachedService cache;
	
	
	/**
	 * 
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
	 * 
	 * @param request
	 * @param method
	 * @return
	 * @throws McaException
	 */
	public void requestAuthozChallenge(MCAUser user, String checkoutId) throws McaException {
		
		String challenge;
		
		String method = getAuthorizationMethods(user).get(0);
		
		if (method.equals("sms")) {
			challenge = issueSmsChallenge(user);
		}
		else if (method.equals("totp"))
			challenge = issueTOTPChallenge(user);
		else
			throw new McaException("only sms and totp are implemented");
		
		
		// Set otp to memcached
		cache.set(OTP+checkoutId, 900, challenge+",0");
		
		LOG.info("user " + user.getUserId() + " has request an authorization challenge of type " + method);
		
	}
	
	/*
	 * 
	 */
	private String issueTOTPChallenge(MCAUser user) {
		@SuppressWarnings("unchecked")
		Map<String, Object> mcaUser = (Map<String, Object>) user.getValue(".mca-user");
		if (mcaUser==null || mcaUser.get("totp")==null)
			return "setup";
		return "ready";
	}
	
	/*
	 * 
	 */
	private String issueSmsChallenge(MCAUser user) throws McaException {
		
		String mobile = user.getMobile();
		
		if (StringUtils.isEmpty(mobile)) throw new McaException("user has no mobile " + user.getMobile());
		MCAMessage sms = new MCAMessage();
		sms.setFrom("sys:authorization");
		sms.setTo(mobile);
		sms.setType("text/outgoing-sms");
		String str = String.valueOf(Math.abs(new SecureRandom().nextInt())%10000);
		while (str.length()<4)
			str = "0" + str;
		sms.setBody("Ma xac thuc/MCA Code: " + str + ". Mobivi");
		
		try {
			if (LOG.isDebugEnabled())
			LOG.debug("sending SMS code to user " + user.getUserId() + " " + str);
			messagingService.postMessage(sms);
		} catch (MCAException e) {
			String msg = "failed to send SMS message " + e.getMessage();
			LOG.error(msg, e);
			throw new McaException(msg, e);
		}
		
		return str;
	}
	
	/**
	 * 
	 * @param request
	 * @param token
	 * @return
	 * @throws McaException
	 */
	public boolean verifyAuthozToken(MCAUser user, String checkoutId, String token) throws McaException {
		String method = getAuthorizationMethods(user).get(0);
		
		String challenge = (String)cache.get(OTP+checkoutId);
		
		String[] info = challenge.split(",");
		
		int count = Integer.parseInt(info[1]);
		
		if (count > 4)
			throw new McaException("too many wrong verification");
		
		boolean verified;
		
		if (method.equals("sms")) {
			verified = verifySmsChallenge(info[0], token);
		}
		else if (method.equals("totp"))
			verified = verifyTOTPChallenge(user, token);
		else
			throw new McaException("only sms and totp are implemented");
		
		// Update challenge
		count++;
		challenge = info[0] + "," + count;
		cache.set(OTP+checkoutId, 900, challenge);
		
		return verified;
	}
	
	/*
	 * 
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
	
	/*
	 * 
	 */
	private boolean verifySmsChallenge(String code, String token) throws McaException {
		
		if (!code.equals(token)){
			return false;
		}
		
		return true;
	}
}
