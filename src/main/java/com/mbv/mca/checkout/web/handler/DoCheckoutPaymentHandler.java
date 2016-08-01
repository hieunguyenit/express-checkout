package com.mbv.mca.checkout.web.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.mbv.mca.checkout.core.CheckoutException;
import com.mbv.mca.checkout.core.CheckoutRequest;
import com.mbv.mca.checkout.core.CheckoutResponse;
import com.mbv.mca.checkout.core.Settlement;
import com.mbv.mca.checkout.core.Transaction;
import com.mbv.mfs.data.Loan;
import com.mbv.mfs.data.LoanFilter;
import com.mbv.mfs.data.LoanList;
import com.mbv.mfs.service.client.MfsServiceClient;
import com.mbv.xacct.XAccount;
import com.mbv.xacct.XTransaction;

@Component("doCheckoutPaymentHandler")
public class DoCheckoutPaymentHandler extends BaseHandler implements
		CheckoutHandler {

	final static Log LOG = LogFactory.getLog(DoCheckoutPaymentHandler.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mbv.mca.checkout.web.handler.CheckoutHandler#handle(com.mbv.mca.checkout
	 * .core.CheckoutRequest)
	 */
	public CheckoutResponse handle(CheckoutRequest request)
			throws CheckoutException {

		CheckoutResponse response = null;

		try {

			if (request.getInvoice().getTermId() != null) {
				// Make loan in MFS based on product term id
				makeLoan(request);
			} else {
				// Lock fund in Xacct
				lockFund(request);
			}

			request.setStatus(CheckoutRequest.COMPLETED);

			// Initialize a settlement
			Settlement settlement = new Settlement();
			settlement.setCheckoutId(request.getCheckoutId());
			settlement.setXtranId(request.getTransaction().getXid());
			settlement.setAmount(request.getTransaction().getAmount());
			settlement.setDone(false);

			settlementDAO.save(settlement);

		} catch (Exception e) {
			request.setStatus(CheckoutRequest.FAILED);
			e.printStackTrace();
			LOG.debug("Calling XAccount :" + e);
		}

		// Update checkout status
		checkoutDAO.save(request);
		cache.set(request.getCheckoutId(), request);

		// Make response
		response = new CheckoutResponse();
		response.setCheckoutId(request.getCheckoutId());
		response.setStatus(request.getStatus());
		response.setRefId(request.getTransaction() == null ? null : request
				.getTransaction().getXid());

		return response;
	}

	/*
	 * Make a loan in MFS
	 */
	private Loan makeLoan(CheckoutRequest request) throws Exception {
		// Make loan
		Loan loan = new Loan();

		loan.setUserId(request.getCustomer().getUserId());
		loan.setOrgId(request.getCustomer().getOrgId());
		loan.setxAccountId(request.getCustomer().getAccountId());

		loan.setAmount((double) request.getInvoice().getPaidAmount());
		loan.setProductTermId(request.getInvoice().getTermId());
		loan.setStatus(Loan.Status.approved);

		// Get discount for nds
		long discount = request.getInvoice().getDiscount() == 0 ? request
				.getInvoice().getServiceFee() : request.getInvoice()
				.getDiscount();

		loan.setValue("ndsMerchantXAcct", request.getMerchant().getAccountId());
		loan.setValue("ndsCost", (double) (request.getInvoice().getPaidAmount()-discount));
		loan.setValue("-xtranDesc", request.getInvoice().getDescription());
		loan.setValue("-debtXtranDesc", request.getInvoice().getDescription());

		// Save loan in mfs
		loan = mfsAccess.saveLoan(loan);

		// Update transaction
		Transaction transaction = new Transaction();
		transaction.setXid("mfs:" + loan.getLoanId());
		transaction.setStatus(loan.getStatusString());
		transaction.setAmount((long) loan.getAmount());

		// Update checkout request
		request.setTransaction(transaction);
		checkoutDAO.save(request);
		
		return loan;
	}

	/**
	 * 
	 * @param loanId
	 * @param release
	 * @return
	 * @throws Exception
	 */
	public Loan captureOrReleaseLoan(String loanId, boolean release)
			throws Exception {
		LoanFilter filter = new LoanFilter();
		filter.setLoanId(loanId);

		LoanList list = mfsAccess.searchLoan(filter);

		if (list == null || list.getTotal() == 0) {
			throw new Exception("MFS_LOAN_NOT_FOUND");
		}

		Loan loan = list.getItems().get(0);

		if (release) {
			loan.setStatus(Loan.Status.denied);
		} else {
			loan.setStatus(Loan.Status.disbursed);
		}

		// Update loan in mfs
		loan = mfsAccess.saveLoan(loan);

		return loan;
	}

	/*
	 * Lock Fund in XAcct
	 */
	private XTransaction lockFund(CheckoutRequest request) throws Exception {
		XTransaction xtrans = null;

		// Get customer xacct
		XAccount customerAcct = xAcctAccess.get(request.getCustomer()
				.getAccountId());

		// Make transaction
		xtrans = new XTransaction();
		xtrans.setCrAcctId(customerAcct.getStringValue("ndsAcctId"));
		xtrans.setAmount((double) request.getInvoice().getPaidAmount());
		xtrans.setType("purchase");
		xtrans.setStatus(XTransaction.Status.authorized);
		xtrans.setDescription(request.getInvoice().getDescription());
		xtrans.setDrAcctId(request.getMerchant().getAccountId());

		// Get discount for nds
		long discount = request.getInvoice().getDiscount() == 0 ? request
				.getInvoice().getServiceFee() : request.getInvoice()
				.getDiscount();

		xtrans.setValue("ndsDiscount", (double) discount);

		// Execute transaction
		xtrans = xTransAccess.save(xtrans);

		// Update transaction status to authorized
		Transaction transaction = new Transaction();
		transaction.setXid(xtrans.getXtransId());
		transaction.setStatus(xtrans.getStatusString());
		transaction.setAmount((long) xtrans.getAmount());

		request.setTransaction(transaction);
		checkoutDAO.save(request);

		return xtrans;
	}

	/*
	 * Capture or Release Fund in XAcct
	 */
	public XTransaction captureOrReleaseFund(String xtranId, boolean release)
			throws Exception {
		XTransaction xtrans = null;

		xtrans = xTransAccess.get(xtranId);

		if (xtrans == null) {
			throw new Exception("XACCT_XTRAN_NOT_FOUND");
		}

		if (release) {
			xtrans.setStatus(XTransaction.Status.failed);
		} else {
			xtrans.setStatus(XTransaction.Status.complete);
		}

		xTransAccess.save(xtrans);

		return xtrans;
	}
}
