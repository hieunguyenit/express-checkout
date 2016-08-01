package com.mbv.mca.checkout.xacct;

import com.mbv.mca.checkout.web.session.McaException;
import com.mbv.xacct.XTransaction;

/**
 * This interface allow transaction routing and logic implementation.
 * @author Nam Pham
 * @since 0.1.0
 */
public interface XTransExecHandler {
	/**
	 * handle a transaction execution
	 * @param xTrans
	 * @throws XTransException
	 * @since 0.1.0
	 */
	void handle(XTransaction xTrans) throws McaException;
	
}
