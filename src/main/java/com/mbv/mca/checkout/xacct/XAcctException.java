package com.mbv.mca.checkout.xacct;

import com.mbv.mca.checkout.web.session.McaException;



/**
 * Data exception is thrown when reading or writing data to XAcct
 * @author Nam Pham
 *
 */
@SuppressWarnings("serial")
public class XAcctException extends McaException {
	public XAcctException() {
		super();
	}
	
	public XAcctException(String message) {
		super(message);
	}
	
	public XAcctException(String message, Throwable cause) {
		super(message, cause);
	}
}
