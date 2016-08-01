package com.mbv.mca.checkout.core;

public class InvoiceItem extends GsonDataObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4600420573847312472L;
	
	public static final String INSTALLMENT_ITEM_NAME = "tra_gop";

	String sku;
	String name;
	int price;
	int quantity;
	long amount;
	
	long paidAmount;
	long discount;
	long serviceFee;
	
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	public long getDiscount() {
		return discount;
	}
	public void setDiscount(long discount) {
		this.discount = discount;
	}
	public long getServiceFee() {
		return serviceFee;
	}
	public void setServiceFee(long serviceFee) {
		this.serviceFee = serviceFee;
	}
	public long getPaidAmount() {
		return paidAmount;
	}
	public void setPaidAmount(long paidAmount) {
		this.paidAmount = paidAmount;
	}
}
