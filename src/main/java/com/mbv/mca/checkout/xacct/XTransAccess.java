package com.mbv.mca.checkout.xacct;

import com.mbv.xacct.XPageView;
import com.mbv.xacct.XTransaction;
import com.mbv.xacct.XTransactionFilter;

/**
 * interface which profile access to {@link XTransaction}
 * @author Nam Pham
 *
 */
public interface XTransAccess {
	/**
	 * 
	 * @param xTrans
	 * @throws XTransException
	 */
	XTransaction save(XTransaction xTrans) throws XTransException;
	
	/**
	 * 
	 * @param xtransId
	 * @return
	 * @throws XTransException 
	 */
	XTransaction get(String xtransId) throws XTransException;
	
	/**
	 * 
	 * @param filter
	 * @return
	 * @throws XTransException
	 */
	XPageView<XTransaction> search(XTransactionFilter filter) throws XTransException;
 }
