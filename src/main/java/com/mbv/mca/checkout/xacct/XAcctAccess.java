package com.mbv.mca.checkout.xacct;

import com.mbv.xacct.XAccount;
import com.mbv.xacct.XTransaction;

/**
 * interface which profile access to {@link XTransaction}
 * @author Nam Pham
 *
 */
public interface XAcctAccess {
	
	/**
	 * 
	 * @param xtransId
	 * @return
	 * @throws XTransException 
	 */
	XAccount get(String acctId) throws XAcctException;
	
 }
