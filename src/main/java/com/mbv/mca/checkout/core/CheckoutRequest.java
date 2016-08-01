package com.mbv.mca.checkout.core;

public class CheckoutRequest extends DataObject{
	private static final long serialVersionUID = 1710050085043882199L;
	public static final String INTIAL 		= "initial";
	public static final String PLACED 		= "placed";
	public static final String REVIEWED 	= "reviewed";
	public static final String PROCESSING 	= "processing";
	public static final String VERIFIED		= "verified";
	public static final String COMPLETED 	= "completed";
	public static final String FAILED 		= "failed";
	public static final String CANCELED 	= "canceled";
	public static final String REFUNDED 	= "refunded";
	
	public static final String SET_CHECKOUT		= "setcheckout";
	public static final String REFUND_CHECKOUT	= "refundcheckout";

	String checkoutId;
	String merchantId;
	String method;
	String returnUrl;
	String cancelUrl;
	String status;
	
	Merchant merchant;
	Customer customer;
	Invoice invoice;
	Transaction transaction;
	
	String merchantString;
	String customerString;
	String invoiceString;
	String transactionString;
	
	public String getCheckoutId() {
		return checkoutId;
	}
	public void setCheckoutId(String checkoutId) {
		this.checkoutId = checkoutId;
	}
	public String getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getReturnUrl() {
		return returnUrl;
	}
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
	public String getCancelUrl() {
		return cancelUrl;
	}
	public void setCancelUrl(String cancelUrl) {
		this.cancelUrl = cancelUrl;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Merchant getMerchant() {
		return merchant;
	}
	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public Invoice getInvoice() {
		return invoice;
	}
	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}
	public Transaction getTransaction() {
		return transaction;
	}
	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}
	public String getMerchantString() {
		return merchantString;
	}
	public void setMerchantString(String merchantString) {
		this.merchantString = merchantString;
	}
	public String getCustomerString() {
		return customerString;
	}
	public void setCustomerString(String customerString) {
		this.customerString = customerString;
	}
	public String getInvoiceString() {
		return invoiceString;
	}
	public void setInvoiceString(String invoiceString) {
		this.invoiceString = invoiceString;
	}
	public String getTransactionString() {
		return transactionString;
	}
	public void setTransactionString(String transactionString) {
		this.transactionString = transactionString;
	}
}
