package com.mbv.mca.checkout.web;

public interface SessionKeys {
	static enum AuthorizationType {
		/**
		 * SMS-based authorization
		 */
		SMS,
		/**
		 * Time-based OTP
		 */
		TOTP,
		/**
		 * Counter/Challenge-base OTP
		 */
		COTP,
		
	}
	
	/**
	 * key to store {@link com.mbv.services.session.SessionInfo}
	 */
	public static final String SESS_SESSION_INFO = "ssoSessionInfo";
	
	/**
	 * key to store {@link com.mbv.mca.MCAUser}
	 */
	public static final String SESS_USER_INFO = "mcaUserInfo";
	
	/**
	 * key to store {@link com.mbv.xacct.XAccount}
	 */
	public static final String SESS_ECC_ACCT = "eccXAccount";
	
	/**
	 * key to store {@link com.mbv.xacct.XAccount}
	 */
	public static final String SESS_CCC_ACCT = "cccXAccount";
	
	/**
	 * key to store authorization type, possible value are "SMS, TOTP (time-based), COTP (challenge-based)"
	 */
	public static final String SESS_AUTHOZ_TYPE = "authozType";
	
	/**
	 * key to store authorization data, this can be a map of String-Date (SMS), 
	 * or a {@link java.util.Date} for  time-based OTP authorization, or 
	 * any Object which represents challenge
	 */
	public static final String SESS_AUTHOZ_DATA = "authozData";
	
	/**
	 * key to store {@link java.util.Date} at which authorization expires. Authorization 
	 * check must be aware that this value can be null.
	 */
	public static final String SESS_AUTHOZ_EXPIRED_AT = "authozExpiredAt";
}
