package com.mbv.mca.checkout.core;

public class Transaction extends GsonDataObject {
	private static final long serialVersionUID = 34258689214443913L;
	
	String xid;
	String status;
	long amount;
	String feeXid;
	int fee;
	
	public String getXid() {
		return xid;
	}
	public void setXid(String xid) {
		this.xid = xid;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	public String getFeeXid() {
		return feeXid;
	}
	public void setFeeXid(String feeXid) {
		this.feeXid = feeXid;
	}
	public int getFee() {
		return fee;
	}
	public void setFee(int fee) {
		this.fee = fee;
	}
	
}
