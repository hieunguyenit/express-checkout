package com.mbv.mca.checkout.jdo;

import java.util.List;

import com.mbv.mca.checkout.core.CheckoutException;
import com.mbv.mca.checkout.core.CheckoutRequest;

public interface CheckoutRequestDao {
	
	public CheckoutRequest get(Long id) throws CheckoutException;
	
	public void save(CheckoutRequest request) throws CheckoutException;
	
	public List<CheckoutRequest> search(CheckoutRequestFilter filter) throws CheckoutException;
	
	public CheckoutRequest searchByCheckoutId(String checkoutId) throws CheckoutException;
	
	public List<CheckoutRequest> searchCompletedCheckout(Long id) throws CheckoutException;
}
