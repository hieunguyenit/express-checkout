package com.mbv.mca.checkout.xacct;

import java.util.Map;

import com.mbv.xacct.XTransaction;

/**
 * Define all event relating to {@link XTransaction} operations
 * @author Nam Pham
 *
 * @param <T>
 */
public class XTransEvent extends XEvent<XTransaction> {
	
	/**
	 * fired when a transaction is being saved, should be fired by {@link XTransSaveHandler}
	 */
	public static final String SAVING = "saving";
	
	/**
	 * fired right after a transaction is saved, outside transaction scope
	 */
	public static final String SAVED = "saved";
	
	public XTransEvent(XTransaction src, String name, Map<String, Object> params) {
		super(src, params);
		super.setName(name);
	}
	
}
