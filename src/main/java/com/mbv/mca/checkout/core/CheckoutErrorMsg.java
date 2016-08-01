package com.mbv.mca.checkout.core;

public interface CheckoutErrorMsg {
	// Checkout Service Exception
	public static final String SERVICE_UNAVAILABLE 			= "SERVICE_UNAVAILABLE";
	public static final String NOT_SUPPORTED_METHOD 		= "NOT_SUPPORTED_METHOD";
	public static final String EXPIRED_CHECKOUT		 		= "EXPIRED_CHECKOUT";

	// MCA Service Exception
	public static final String MCA_USER_UNAVAILABLE		 	= "MCA_USER_UNAVAILABLE";
	public static final String MCA_USER_INACTIVE		 	= "MCA_USER_INACTIVE";
	public static final String MCA_USER_NOT_FOUND		 	= "MCA_USER_NOT_FOUND";
	
	// Pay Exception
	public static final String UNVERIFIED_CHECKOUT			= "UNVERIFIED_CHECKOUT";
	
	// Set checkout request
	public static final String NON_EXISTED_MERCHANT 		= "NON_EXISTED_MERCHANT";
	public static final String WRONG_MERCHANT_CREDENTIALS	= "WRONG_MERCHANT_CREDENTIALS";

	// Get checkout request
	public static final String CHECKOUT_NOT_FOUND 			= "CHECKOUT_NOT_FOUND";
	
	// Refund request
	public static final String REFUND_INVALID_AUTHEN 		= "REFUND_INVALID_AUTHEN";
	public static final String REFUND_NOT_SETTLED_XTRAN 	= "REFUND_NOT_SETTLED_XTRAN";

	
}
