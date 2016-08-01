package com.mbv.mca.checkout.core;

import java.util.List;

public class Invoice extends GsonDataObject{
	private static final long serialVersionUID = -3407198116519881625L;
	
	String invoiceId;
	List<InvoiceItem> items;
	String description;
	
	long amount;
	long paidAmount;
	long discount;
	long serviceFee;
	
	String termId;
	
	public String getInvoiceId() {
		return invoiceId;
	}
	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}
	public List<InvoiceItem> getItems() {
		return items;
	}
	public void setItems(List<InvoiceItem> items) {
		this.items = items;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	public long getPaidAmount() {
		return paidAmount;
	}
	public void setPaidAmount(long paidAmount) {
		this.paidAmount = paidAmount;
	}
	public long getServiceFee() {
		return serviceFee;
	}
	public void setServiceFee(long serviceFee) {
		this.serviceFee = serviceFee;
	}
	public long getDiscount() {
		return discount;
	}
	public void setDiscount(long discount) {
		this.discount = discount;
	}
	public String getTermId() {
		return termId;
	}
	public void setTermId(String termId) {
		this.termId = termId;
	}
	

}
