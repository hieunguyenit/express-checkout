package com.mbv.mca.checkout.web.session;


@SuppressWarnings("serial")
public class UserNotMatchException extends SessionException {
	public UserNotMatchException(String message) {
		this(message, null);
	}
	
	public UserNotMatchException(String message, Throwable cause) {
		super(message, cause);
	}
}
