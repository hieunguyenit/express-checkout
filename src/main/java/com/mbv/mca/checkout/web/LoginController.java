package com.mbv.mca.checkout.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.mbv.mca.MCAUser;
import com.mbv.mca.checkout.core.CheckoutException;
import com.mbv.mca.checkout.core.CheckoutRequest;
import com.mbv.mca.checkout.core.Customer;
import com.mbv.services.session.SessionInfo;

@Controller
public class LoginController extends BaseController {
	// Logger
	private static final Logger LOG = Logger.getLogger(LoginController.class);

	/*
	 * 
	 */
	@RequestMapping(value = "/login.htm", method = RequestMethod.GET)
	@ExceptionHandler(CheckoutException.class)
	public String processLogin(
			@RequestParam(value = "checkoutId") String checkoutId,
			HttpServletRequest request,
			HttpServletResponse response,
			ModelMap model) throws Exception{

		LOG.debug("Handle LOGIN request... Checkout Id = " + checkoutId);

		HttpSession hs = request.getSession(false);
		SessionInfo session = hs == null ? null : (SessionInfo) hs
				.getAttribute(SESS_SESSION_INFO);

		// 
		MCAUser customerUser = null;
		
		CheckoutRequest coRequest = null;
		
		coRequest = (CheckoutRequest) cache.get(checkoutId);
		
		try {
			// Get customer account info
			customerUser = getMcaUser(mcaCoreService, request,
											session.getLoginId());
			
			Customer customer = new Customer();
			customer.setAccountId(customerUser.getXAccountId());
			customer.setUserId(customerUser.getUserId());
			customer.setLoginId(session.getLoginId());
			customer.setName(customerUser.getFamilyName()+" " + customerUser.getGivenName());
			customer.setOrgId(customerUser.getOrgId());
				
			// Update checkout request
			coRequest.setCustomer(customer);
			coRequest.setStatus(CheckoutRequest.PLACED);
			
			// Set checkout status to placed
			checkoutDAO.save(coRequest);
			cache.set(coRequest.getCheckoutId(), coRequest);

		} catch (Exception e) {
			throw new CheckoutException(e.getMessage());
		}

		return "redirect:/web/review.htm?checkoutId="+coRequest.getCheckoutId();
	}
}
