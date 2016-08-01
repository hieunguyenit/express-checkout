package com.mbv.mca.checkout.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.mbv.mca.MCAUser;
import com.mbv.mca.checkout.core.Calculator;
import com.mbv.mca.checkout.core.CheckoutErrorMsg;
import com.mbv.mca.checkout.core.CheckoutException;
import com.mbv.mca.checkout.core.CheckoutRequest;
import com.mbv.mca.checkout.core.CheckoutResponse;
import com.mbv.mca.checkout.core.InvoiceItem;
import com.mbv.mca.checkout.web.handler.CheckoutHandler;

@Controller
public class ReviewController extends BaseController {
	// Logger
	private static final Logger LOG = Logger.getLogger(ReviewController.class);

	/*
	 * 
	 */
	@RequestMapping(value = "/review.htm", method = RequestMethod.GET)
	public String processReview(
			@RequestParam(value = "checkoutId") String checkoutId,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) throws CheckoutException {

		LOG.debug("Handle REVIEW request... Checkout Id = " + checkoutId);

		HttpSession hs = request.getSession(false);
		MCAUser user = (MCAUser) hs.getAttribute(SESS_USER_INFO);

		// Get request
		CheckoutRequest coRequest = (CheckoutRequest) cache.get(checkoutId);

		// Calculate invoice paid amount
		coRequest.setInvoice(Calculator.calculateInvoice(
				coRequest.getInvoice(), coRequest.getMerchant()));

		// Update request status to reviewed
		coRequest.setStatus(CheckoutRequest.REVIEWED);
		checkoutDAO.save(coRequest);
		cache.set(checkoutId, coRequest);
		
		// Issue otp
		issueOtp(user, checkoutId);

		// Remove installment item
		List<InvoiceItem> items = coRequest.getInvoice().getItems();
		
		String installmentType = null;
		
		for (int index = 0; index < items.size(); index++){
			if (items.get(index).getName().indexOf(InvoiceItem.INSTALLMENT_ITEM_NAME) > -1) {
				installmentType = items.get(index).getName().substring(0, items.get(index).getName().indexOf(':'));
				items.remove(index);
				coRequest.getInvoice().setItems(items);
				break;
			}
		}
		
		// Model for view
		model.addAttribute("checkoutId", coRequest.getCheckoutId());
		model.addAttribute("invoice", coRequest.getInvoice());
		model.addAttribute("installment", installmentType != null ? 
							installmentMap.get(installmentType.toLowerCase()) : null);

		return "review";
	}

	/*
	 * 
	 */
	@RequestMapping(value = "/otp.htm", method = RequestMethod.GET)
	public String reIssueOtp(
			@RequestParam(value = "checkoutId") String checkoutId,
			HttpServletRequest request, ModelMap model)
			throws CheckoutException {

		HttpSession hs = request.getSession(false);
		MCAUser user = (MCAUser) hs.getAttribute(SESS_USER_INFO);

		// Issue Otp challenge
		boolean result = issueOtp(user,checkoutId);
		model.addAttribute("otpValidation", result == true ? "1" : "0");
		
		return "otp";
	}

	/*
	 * 
	 */
	@RequestMapping(value = "/otp.htm", method = RequestMethod.POST)
	public String verifyOtp(
			@RequestParam(value = "checkoutId") String checkoutId,
			@RequestParam(value = "otp") String otp,
			HttpServletRequest request, ModelMap model)
			throws CheckoutException {

		HttpSession hs = request.getSession(false);
		MCAUser user = (MCAUser) hs.getAttribute(SESS_USER_INFO);

		boolean otpValidation;

		try {
			otpValidation = otpService.verifyAuthozToken(user, checkoutId, otp);
			
		} catch (Exception e) {
			throw new CheckoutException(e.getMessage());
		}

		//
		if (otpValidation){
			hs.setAttribute(SESS_AUTHOZ_DATA, checkoutId+CheckoutRequest.VERIFIED);
		}
		
		model.addAttribute("otpValidation", otpValidation == true ? "1" : "0");

		return "otp";
	}

	/*
	 * 
	 */
	@RequestMapping(value = "/pay.htm", method = RequestMethod.GET)
	public String processPay(
			@RequestParam(value = "checkoutId") String checkoutId,
			HttpServletRequest request)
			throws CheckoutException {
		
		HttpSession hs = request.getSession(false);
		String authozData = (String) hs.getAttribute(SESS_AUTHOZ_DATA);
		if (authozData == null || !authozData.equals(checkoutId+CheckoutRequest.VERIFIED)){
			throw new CheckoutException(CheckoutErrorMsg.UNVERIFIED_CHECKOUT);
		}

		// Get request from cache
		CheckoutRequest coRequest = (CheckoutRequest) cache.get(checkoutId);

		// Update request status to processing
		coRequest.setStatus(CheckoutRequest.PROCESSING);
		checkoutDAO.save(coRequest);
		cache.set(checkoutId, coRequest);

		// Handle checkout payment
		CheckoutHandler coHandler = (CheckoutHandler) handlerMap
				.get("docheckoutpayment");

		CheckoutResponse coResponse = coHandler.handle(coRequest);

		//
		LOG.debug("Handle PAY request... Checkout Id =" + checkoutId
				+ "...Response = " + coResponse.getStatus() + " "
				+ coResponse.getRefId());

		return "redirect:" + coRequest.getReturnUrl();
	}

	/*
	 * 
	 */
	@RequestMapping(value = "/cancel.htm", method = RequestMethod.GET)
	public String processCancel(
			@RequestParam(value = "checkoutId") String checkoutId)
			throws CheckoutException {

		CheckoutRequest coRequest = (CheckoutRequest) cache.get(checkoutId);

		// Update request status to canceled
		coRequest.setStatus(CheckoutRequest.CANCELED);
		checkoutDAO.save(coRequest);
		cache.set(checkoutId, coRequest);

		return "redirect:" + coRequest.getCancelUrl();
	}

}
