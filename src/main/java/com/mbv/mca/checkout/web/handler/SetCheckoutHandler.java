package com.mbv.mca.checkout.web.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import com.mbv.mca.checkout.core.CheckoutException;
import com.mbv.mca.checkout.core.CheckoutRequest;
import com.mbv.mca.checkout.core.CheckoutResponse;
import com.mbv.mca.checkout.core.Merchant;

@Component("setCheckoutHandler")
public class SetCheckoutHandler extends BaseHandler implements CheckoutHandler {
	
	final static Log LOG = LogFactory.getLog(SetCheckoutHandler.class);

	/*
	 * (non-Javadoc)
	 * @see com.mbv.mca.checkout.web.handler.CheckoutHandler#handle(com.mbv.mca.checkout.core.CheckoutRequest)
	 */
	public CheckoutResponse handle(CheckoutRequest request) throws CheckoutException {
		CheckoutResponse response = null;
		
		// Authenticate
		Merchant merchant = authenticate(request);
		
		// Generate token
		String checkoutId = generateCheckoutId(merchant);

		request.setCheckoutId(checkoutId);
		request.setMerchantId(merchant.getUsername());
		request.setStatus(CheckoutRequest.INTIAL);
		request.setMerchant(merchant);

		// Set checkout status to initial
		checkoutDAO.save(request);
		cache.set(checkoutId, request);
		
		// Make response
		response = new CheckoutResponse();
		response.setCheckoutId(checkoutId);
		response.setStatus(request.getStatus());

		return response;
	}

}
