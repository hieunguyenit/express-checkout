package com.mbv.mca.checkout.web.session;

import java.util.List;

public class AuthorizationRequiredException extends McaException {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -8821710816583046337L;
	
	
	List<String> methods;
	
	public AuthorizationRequiredException() {
	}
	
	public AuthorizationRequiredException(List<String> methods) {
		this(methods, null);
		
	}
	
	public AuthorizationRequiredException(List<String> methods, String msg) {
		this(methods, msg, null);
	}
	
	public AuthorizationRequiredException(List<String> methods, String msg, Throwable cause) {
		super(msg!=null?msg:("Accepted methods: " + methods), cause);
		this.methods = methods;
	}

	public List<String> getMethods() {
		return methods;
	}

	public void setMethods(List<String> methods) {
		this.methods = methods;
	}
}
