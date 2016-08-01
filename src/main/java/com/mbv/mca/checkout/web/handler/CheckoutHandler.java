package com.mbv.mca.checkout.web.handler;

import com.mbv.mca.checkout.core.CheckoutException;
import com.mbv.mca.checkout.core.CheckoutRequest;
import com.mbv.mca.checkout.core.CheckoutResponse;

public interface CheckoutHandler {
	public CheckoutResponse handle(CheckoutRequest request) throws CheckoutException;
}
