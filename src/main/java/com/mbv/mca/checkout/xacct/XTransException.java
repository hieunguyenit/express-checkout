package com.mbv.mca.checkout.xacct;

import com.mbv.mca.checkout.web.session.McaException;



/**
 * Data exception is thrown when reading or writing data to XAcct
 * @author Nam Pham
 *
 */
@SuppressWarnings("serial")
public class XTransException extends McaException {
	public XTransException() {
		super();
	}
	
	public XTransException(String message) {
		super(message);
	}
	
	public XTransException(String message, Throwable cause) {
		super(message, cause);
	}
}
