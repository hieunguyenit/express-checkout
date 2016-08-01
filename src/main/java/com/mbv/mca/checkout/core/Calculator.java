package com.mbv.mca.checkout.core;

public class Calculator {

	/**
	 * 
	 * @param invoice
	 * @param merchant
	 */
	public static Invoice calculateInvoice(Invoice invoice, Merchant merchant) {
		// Calculate invoice amount
		long amount = 0;

		invoice.setTermId(null);

		for (InvoiceItem item : invoice.getItems()) {
			// Installment item
			if (item.getName().trim().indexOf(InvoiceItem.INSTALLMENT_ITEM_NAME) > -1) {
				String termId = item.getName();
				termId = termId.substring(termId.indexOf(':')+1);
				invoice.setTermId(termId.trim());
			}

			item.setAmount(item.getPrice() * item.quantity);
			amount += item.getAmount();
		}

		invoice.setAmount(amount);

		// Calculate paid amount
		long paidAmount = 0;
		long discount = 0;
		long serviceFee = 0;

		if (merchant.getDiscountRate() != 0) {
			for (InvoiceItem item : invoice.getItems()) {
				item.setDiscount(item.getAmount() * merchant.getDiscountRate() / 1000);
				item.setPaidAmount(item.getAmount());

				paidAmount += item.getPaidAmount();
				discount += item.getDiscount();
			}

		} else {
			for (InvoiceItem item : invoice.getItems()) {
				item.setServiceFee(item.getAmount() * merchant.getServiceFeeRate() / 1000);
				item.setPaidAmount(item.getAmount() + item.getServiceFee());

				paidAmount += item.getPaidAmount();
				serviceFee += item.getServiceFee();
			}
		}

		invoice.setPaidAmount(paidAmount);
		invoice.setDiscount(discount);
		invoice.setServiceFee(serviceFee);

		return invoice;
	}
}
