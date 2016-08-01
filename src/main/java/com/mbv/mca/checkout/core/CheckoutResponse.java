package com.mbv.mca.checkout.core;

import java.io.Serializable;

public class CheckoutResponse implements Serializable {

	private static final long serialVersionUID = 6210548404195197163L;

	String checkoutId;

	String status;

	String refId;
	
	String detail;

	public String getCheckoutId() {
		return checkoutId;
	}

	public void setCheckoutId(String checkoutId) {
		this.checkoutId = checkoutId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}
}
