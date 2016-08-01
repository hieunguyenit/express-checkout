package com.mbv.mca.checkout.jdo;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bouncycastle.asn1.isismtt.x509.Restriction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mbv.mca.checkout.core.CheckoutException;
import com.mbv.mca.checkout.core.CheckoutRequest;
import com.mbv.mca.checkout.core.Customer;
import com.mbv.mca.checkout.core.Invoice;
import com.mbv.mca.checkout.core.Merchant;
import com.mbv.mca.checkout.core.Transaction;
import com.google.gson.Gson;

public class CheckoutRequestDaoImpl extends ObjectDaoImpl implements
		CheckoutRequestDao {

	// Logger
	private static final Logger LOG = Logger
			.getLogger(CheckoutRequestDaoImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mbv.mca.checkout.jdo.CheckoutRequestDao#get(java.lang.Long)
	 */
	public CheckoutRequest get(Long id) throws CheckoutException {

		CheckoutRequest request = get(CheckoutRequest.class, id);

		convertFromJson(request);

		return request;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mbv.mca.checkout.jdo.CheckoutRequestDao#save(com.mbv.mca.checkout
	 * .core.CheckoutRequest)
	 */
	public void save(CheckoutRequest request) throws CheckoutException {

		convertToJson(request);

		if (request.getId() == null) {
			request.setCreatedAt(new Date());
			super.save(request);
		} else {
			request.setUpdatedAt(new Date());
			super.merge(request);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mbv.mca.checkout.jdo.CheckoutRequestDao#search(com.mbv.mca.checkout
	 * .jdo.CheckoutRequestFilter)
	 */
	public List<CheckoutRequest> search(CheckoutRequestFilter filter)
			throws CheckoutException {

		// Make criteria
		DetachedCriteria criteria = DetachedCriteria
				.forClass(CheckoutRequest.class);
		criteria.addOrder(Order.desc("id"));
		addCriteria(criteria, filter);

		List<CheckoutRequest> items = findByCriteria(criteria,
				filter.getPageNumber() - 1, filter.getPageSize());

		return items;
	}

	/*
	 * 
	 */
	private void addCriteria(DetachedCriteria criteria,
			CheckoutRequestFilter filter) {

		if (filter.getId() != null) {
			criteria.add(Restrictions.eq("id", filter.getId()));
		}

		if (filter.getGreaterThanId() != null) {
			criteria.add(Restrictions.gt("id", filter.getGreaterThanId()));
		}

		if (filter.getCheckoutId() != null) {
			criteria.add(Restrictions.eq("checkoutId", filter.getCheckoutId()));
		}

		if (filter.getMethod() != null) {
			criteria.add(Restrictions.eq("method", filter.getMethod()));
		}

		if (filter.getStatus() != null) {
			criteria.add(Restrictions.eq("status", filter.getStatus()));
		}
	}

	/*
	 * 
	 */
	private void convertToJson(CheckoutRequest request) {
		Gson gson = new Gson();

		if (request.getMerchant() != null) {
			request.setMerchantString(gson.toJson(request.getMerchant()));
		}

		if (request.getCustomer() != null) {
			request.setCustomerString(gson.toJson(request.getCustomer()));
		}

		if (request.getInvoice() != null) {
			request.setInvoiceString(gson.toJson(request.getInvoice()));
		}

		if (request.getTransaction() != null) {
			request.setTransactionString(gson.toJson(request.getTransaction()));
		}
	}

	/*
	 * 
	 */
	private void convertFromJson(CheckoutRequest request) {
		Gson gson = new Gson();

		if (!request.getMerchantString().isEmpty()) {
			request.setMerchant(gson.fromJson(request.getMerchantString(),
					Merchant.class));
		}

		if (!request.getCustomerString().isEmpty()) {
			request.setCustomer(gson.fromJson(request.getCustomerString(),
					Customer.class));
		}

		if (!request.getInvoiceString().isEmpty()) {
			request.setInvoice(gson.fromJson(request.getInvoiceString(),
					Invoice.class));
		}

		if (!request.getTransactionString().isEmpty()) {
			request.setTransaction(gson.fromJson(
					request.getTransactionString(), Transaction.class));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mbv.mca.checkout.jdo.CheckoutRequestDao#searchByCheckoutId(java.lang
	 * .String)
	 */
	public CheckoutRequest searchByCheckoutId(String checkoutId)
			throws CheckoutException {
		CheckoutRequest result = null;

		CheckoutRequestFilter filter = new CheckoutRequestFilter();
		filter.setCheckoutId(checkoutId);
		filter.setMethod(CheckoutRequest.SET_CHECKOUT);

		List<CheckoutRequest> list = search(filter);

		if (!list.isEmpty()) {
			result = list.get(0);
			convertFromJson(result);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mbv.mca.checkout.jdo.CheckoutRequestDao#searchCompletedCheckout()
	 */
	public List<CheckoutRequest> searchCompletedCheckout(Long id)
			throws CheckoutException {

		List<CheckoutRequest> result = null;

		CheckoutRequestFilter filter = new CheckoutRequestFilter();
		filter.setGreaterThanId(id);
		filter.setStatus(CheckoutRequest.COMPLETED);
		filter.setMethod(CheckoutRequest.SET_CHECKOUT);

		List<CheckoutRequest> list = search(filter);

		if (!list.isEmpty()) {
			for (int index = 0; index < list.size(); index++){
				convertFromJson(list.get(index));
			}
			result = list;
		}

		return result;
	}

}
