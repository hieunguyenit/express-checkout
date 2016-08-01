package com.mbv.mca.checkout.web.handler;

import java.util.List;

import org.springframework.stereotype.Component;

import com.mbv.mca.checkout.core.CheckoutErrorMsg;
import com.mbv.mca.checkout.core.CheckoutException;
import com.mbv.mca.checkout.core.CheckoutRequest;
import com.mbv.mca.checkout.core.CheckoutResponse;
import com.mbv.mca.checkout.core.Settlement;
import com.mbv.mca.checkout.core.Transaction;
import com.mbv.mfs.data.Loan;
import com.mbv.mfs.data.LoanFilter;
import com.mbv.mfs.data.LoanList;
import com.mbv.xacct.XTransaction;

@Component("refundCheckoutHandler")
public class RefundCheckoutHandler extends BaseHandler implements CheckoutHandler {

	/*
	 * (non-Javadoc)
	 * @see com.mbv.mca.checkout.web.handler.CheckoutHandler#handle(com.mbv.mca.checkout.core.CheckoutRequest)
	 */
	public CheckoutResponse handle(CheckoutRequest request)
									throws CheckoutException {
		
		if (authenticate(request) == null)
			throw new CheckoutException(CheckoutErrorMsg.REFUND_INVALID_AUTHEN);
		
		List<Settlement> settlementList = settlementDAO.searchUndoneSettlement(request.getCheckoutId());
		
		if (settlementList != null){
			throw new CheckoutException(CheckoutErrorMsg.REFUND_NOT_SETTLED_XTRAN);
		}
		
		CheckoutRequest coRequest = checkoutDAO.searchByCheckoutId(request.getCheckoutId());
		
		CheckoutRequest newRequest = createRefundRequest(coRequest);
	
		try {
			String originalXtransId = coRequest.getTransaction().getXid();
			
			Transaction transaction = new Transaction();
			
			if (originalXtransId.indexOf("mfs") > -1) {
				// Refund installment transaction
				LoanFilter filter = new LoanFilter();
				filter.setLoanId(originalXtransId.substring(4));
				
				LoanList list = mfsAccess.searchLoan(filter);
				Loan loan = list.getItems().get(0);
				loan.setStatus(Loan.Status.closed);
				loan.setValue("-refund", (double)coRequest.getTransaction().getAmount());
				
				loan.setValue("-xtranDesc", coRequest.getInvoice().getDescription());
				loan.setValue("-debtXtranDesc", coRequest.getInvoice().getDescription());

				
				loan = mfsAccess.saveLoan(loan);
				
				// Update transaction status based on mfs result
				transaction.setXid("mfs:"+loan.getLoanId());
				transaction.setStatus(loan.getStatusString());
				transaction.setAmount((long)loan.getAmount());
				
			}
			else {
				// Refund transaction in Xacct
				XTransaction origXtran = xTransAccess.get(originalXtransId);
				
				XTransaction xtran = new XTransaction();
				xtran.setStatus(XTransaction.Status.complete);
				xtran.setType("refund");
				xtran.setValue("origXID", origXtran.getXtransId());
				xtran.setValue("ndsOrderId", coRequest.getCheckoutId());
				xtran.setAmount((double)coRequest.getInvoice().getPaidAmount());
				
				xtran = xTransAccess.save(xtran);
				
				// Update transaction status based on xacct result
				transaction.setXid(xtran.getXtransId());
				transaction.setStatus(xtran.getStatusString());
				transaction.setAmount((long)xtran.getAmount());
			}
						
			
			newRequest.setTransaction(transaction);
			newRequest.setStatus(CheckoutRequest.REFUNDED);
			
		} catch (Exception e) {
			newRequest.setTransaction(null);
			newRequest.setStatus(CheckoutRequest.FAILED);
		}
		
		// Update
		if (CheckoutRequest.REFUNDED.equalsIgnoreCase(newRequest.getStatus())){
			coRequest.setStatus(CheckoutRequest.REFUNDED);
			checkoutDAO.save(coRequest);
			cache.set(coRequest.getCheckoutId(), coRequest);
		}
		
		// Save new request to db
		checkoutDAO.save(newRequest);
		
		// Make resonse
		CheckoutResponse response = new CheckoutResponse();
		response.setCheckoutId(newRequest.getCheckoutId());
		response.setStatus(newRequest.getStatus());
		response.setRefId(newRequest.getTransaction() == null ? null : 
								newRequest.getTransaction().getXid());
		
		return response;
	}

	/*
	 * 
	 */
	private CheckoutRequest createRefundRequest(CheckoutRequest coRequest) {
		CheckoutRequest newRequest = new CheckoutRequest();
		
		newRequest.setCheckoutId(coRequest.getCheckoutId());
		newRequest.setMerchantId(coRequest.getMerchantId());
		newRequest.setMethod(CheckoutRequest.REFUND_CHECKOUT);
		newRequest.setReturnUrl(coRequest.getReturnUrl());
		newRequest.setCancelUrl(coRequest.getCancelUrl());
		newRequest.setMerchant(coRequest.getMerchant());
		newRequest.setInvoice(coRequest.getInvoice());
		newRequest.setCustomer(coRequest.getCustomer());
		
		return newRequest;
	}

}
