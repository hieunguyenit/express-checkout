package com.mbv.mca.checkout.jdo;

public class CheckoutRequestFilter extends DataFilter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5124931867026470469L;
	
	Long id;
	
	Long greaterThanId;
	
	String checkoutId;
	
	String method;
	
	String status;
	
	public String getCheckoutId() {
		return checkoutId;
	}

	public void setCheckoutId(String checkoutId) {
		this.checkoutId = checkoutId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getGreaterThanId() {
		return greaterThanId;
	}

	public void setGreaterThanId(Long greaterThanId) {
		this.greaterThanId = greaterThanId;
	}

	

}
