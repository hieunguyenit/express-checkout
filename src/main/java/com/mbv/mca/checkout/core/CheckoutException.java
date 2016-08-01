package com.mbv.mca.checkout.core;

@SuppressWarnings("serial")
public class CheckoutException extends Exception {
	public CheckoutException() {
		super();
	}
	
	public CheckoutException(String message) {
		super(message);
	}
	
	public CheckoutException(String message, Throwable cause) {
		super(message, cause);
	}

}
