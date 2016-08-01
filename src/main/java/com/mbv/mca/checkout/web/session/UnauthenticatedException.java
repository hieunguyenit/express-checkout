package com.mbv.mca.checkout.web.session;


public class UnauthenticatedException extends McaException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -861219101045260417L;

	public UnauthenticatedException() {
		
	}
	
	public UnauthenticatedException(String msg) {
		super(msg);
	}
	
	public UnauthenticatedException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
