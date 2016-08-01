package com.mbv.mca.checkout.web.session;

import javax.servlet.ServletException;

@SuppressWarnings("serial")
public class SessionException extends ServletException {
	public SessionException(String message) {
		this(message, null);
	}
	
	public SessionException(String message, Throwable cause) {
		super(message, cause);
	}
}
