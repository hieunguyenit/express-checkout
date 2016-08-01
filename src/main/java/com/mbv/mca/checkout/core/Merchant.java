package com.mbv.mca.checkout.core;

public class Merchant extends GsonDataObject {

	private static final long serialVersionUID = 5731169038969730317L;
	
	String username;
	String password;
	String signature;
	String accountId;
	int discountRate;
	int serviceFeeRate;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public int getDiscountRate() {
		return discountRate;
	}
	public void setDiscountRate(int discountRate) {
		this.discountRate = discountRate;
	}
	public int getServiceFeeRate() {
		return serviceFeeRate;
	}
	public void setServiceFeeRate(int serviceFeeRate) {
		this.serviceFeeRate = serviceFeeRate;
	}
}
