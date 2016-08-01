package com.mbv.mca.checkout.web.session;

import java.io.Serializable;

public class McaException extends Exception implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5710144418617002537L;
	
	public McaException() {
		super();
	}
	
	public McaException(String msg) {
		super(msg);
	}
	
	public McaException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
