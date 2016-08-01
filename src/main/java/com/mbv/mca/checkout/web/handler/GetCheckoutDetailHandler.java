package com.mbv.mca.checkout.web.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.mbv.mca.checkout.core.CheckoutErrorMsg;
import com.mbv.mca.checkout.core.CheckoutException;
import com.mbv.mca.checkout.core.CheckoutRequest;
import com.mbv.mca.checkout.core.CheckoutResponse;


@Component("getCheckoutDetailHandler")
public class GetCheckoutDetailHandler extends BaseHandler implements CheckoutHandler {
	
	final static Log LOG = LogFactory.getLog(GetCheckoutDetailHandler.class);

	/*
	 * (non-Javadoc)
	 * @see com.mbv.mca.checkout.web.handler.CheckoutHandler#handle(com.mbv.mca.checkout.core.CheckoutRequest)
	 */
	public CheckoutResponse handle(CheckoutRequest request) throws CheckoutException {
		CheckoutResponse response = new CheckoutResponse();
		response.setCheckoutId(request.getCheckoutId());
		
		String status = null;
		String refId = null;
		String detail = null;
		
		// Get request from memcached
		CheckoutRequest coRequest = (CheckoutRequest) cache.get(request.getCheckoutId());
		
		if (coRequest == null) {
			coRequest = checkoutDAO.searchByCheckoutId(request.getCheckoutId());
		}
		
		if (coRequest == null) {
			throw new CheckoutException(CheckoutErrorMsg.CHECKOUT_NOT_FOUND);
		}
		
		status = coRequest.getStatus();
		refId = coRequest.getTransaction() == null ? null : 
						coRequest.getTransaction().getXid();
		
		detail = coRequest.getCheckoutId();
		detail += coRequest.getCustomer() == null ? "" : ','+coRequest.getCustomer().getLoginId()+','+coRequest.getCustomer().getAccountId();

		// Create response
		response.setStatus(status);
		response.setRefId(refId);
		response.setDetail(detail);

		return response;
	}

}
