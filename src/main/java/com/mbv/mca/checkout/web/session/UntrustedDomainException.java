package com.mbv.mca.checkout.web.session;


@SuppressWarnings("serial")
public class UntrustedDomainException extends SessionException {
	public UntrustedDomainException(String message) {
		this(message, null);
	}
	
	public UntrustedDomainException(String message, Throwable cause) {
		super(message, cause);
	}
}
