package com.mbv.mca.checkout.xacct;

import com.mbv.xacct.XException;



/**
 * Data exception is thrown when reading or writing data to XAcct
 * @author Nam Pham
 *
 */
@SuppressWarnings("serial")
public class XEventException extends XException {
	public XEventException() {
		super();
	}
	
	public XEventException(String message) {
		super(message);
	}
	
	public XEventException(String message, Throwable cause) {
		super(message, cause);
	}
}
