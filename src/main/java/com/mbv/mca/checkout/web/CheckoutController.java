package com.mbv.mca.checkout.web;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.mbv.mca.checkout.core.CheckoutErrorMsg;
import com.mbv.mca.checkout.core.CheckoutException;
import com.mbv.mca.checkout.core.CheckoutRequest;
import com.mbv.mca.checkout.core.CheckoutResponse;
import com.mbv.mca.checkout.web.handler.CheckoutHandler;

@Controller
public class CheckoutController extends BaseController {
	// Logger
	private static final Logger LOG = Logger
			.getLogger(CheckoutController.class);


	/**
	 * 
	 * @param coRequest
	 * @param response
	 * @param webRequest
	 * @return
	 */
	@RequestMapping(value = "/checkout.htm", method = RequestMethod.POST)
	public ModelAndView processCheckout(@RequestBody CheckoutRequest coRequest,
												HttpServletResponse response) {

		LOG.debug("Handle CHECKOUT request... ");

		CheckoutResponse coResponse = null;

		try {

			CheckoutHandler coHandler = (CheckoutHandler) handlerMap
					.get(coRequest.getMethod().toLowerCase());
			
			if (coHandler == null) throw new CheckoutException(CheckoutErrorMsg.NOT_SUPPORTED_METHOD);

			coResponse = coHandler.handle(coRequest);
			
			if (coResponse == null) throw new CheckoutException(CheckoutErrorMsg.SERVICE_UNAVAILABLE);

		} catch (Exception e) {
			String msg = "%1$s";
			return createErrorResponse(String.format(msg, e.getMessage()));
		}

		// Set HTTP response code
		response.setStatus(HttpStatus.CREATED.value());

		return new ModelAndView(jsonView, DATA_FIELD, coResponse);
	}

}
