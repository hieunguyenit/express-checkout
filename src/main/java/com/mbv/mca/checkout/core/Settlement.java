package com.mbv.mca.checkout.core;

public class Settlement extends DataObject {

	private static final long serialVersionUID = -6939958466634142171L;

	String checkoutId;
	String xtranId;
	Long amount;
	Boolean done;
	
	public String getCheckoutId() {
		return checkoutId;
	}
	public void setCheckoutId(String checkoutId) {
		this.checkoutId = checkoutId;
	}
	public String getXtranId() {
		return xtranId;
	}
	public void setXtranId(String xtranId) {
		this.xtranId = xtranId;
	}
	public Long getAmount() {
		return amount;
	}
	public void setAmount(Long amount) {
		this.amount = amount;
	}
	public Boolean getDone() {
		return done;
	}
	public void setDone(Boolean done) {
		this.done = done;
	}
	
	
}
