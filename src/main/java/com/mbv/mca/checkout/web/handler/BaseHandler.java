package com.mbv.mca.checkout.web.handler;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.mbv.mca.checkout.core.CheckoutErrorMsg;
import com.mbv.mca.checkout.core.CheckoutException;
import com.mbv.mca.checkout.core.CheckoutRequest;
import com.mbv.mca.checkout.core.InvoiceItem;
import com.mbv.mca.checkout.core.Merchant;
import com.mbv.mca.checkout.jdo.CheckoutRequestDao;
import com.mbv.mca.checkout.jdo.SettlementDao;
import com.mbv.mca.checkout.service.McaCoreService;
import com.mbv.mca.checkout.service.MemcachedService;
import com.mbv.mca.checkout.service.MfsAccess;
import com.mbv.mca.checkout.utils.CryptoUtil;
import com.mbv.mca.checkout.utils.DateUtil;
import com.mbv.mca.checkout.xacct.XAcctAccess;
import com.mbv.mca.checkout.xacct.XTransAccess;

@Component("checkoutHandler")
public class BaseHandler {
	@Resource(name="merchantMap")
	Map<String, Merchant> merchantMap;
	
	@Autowired
	@Qualifier("xAcctAccess")
	XAcctAccess xAcctAccess;

	@Autowired
	@Qualifier("xTransAccess")
	XTransAccess xTransAccess;
	
	@Autowired
	@Qualifier("mfsAccess")
	MfsAccess mfsAccess;
	
	@Autowired
	@Qualifier("mcaCoreService")
	McaCoreService mcaCoreService;
	
	@Autowired
	@Qualifier("memcachedService")
	MemcachedService cache;

	@Autowired
	@Qualifier("checkoutDAO")
	CheckoutRequestDao checkoutDAO;
	
	@Autowired
	@Qualifier("settlementDAO")
	SettlementDao settlementDAO;
	
//	@Autowired
//	@Qualifier("mcaCorePool")
//	ServicePool<McaCoreClient> mcaCorePool;
	
//	@Autowired
//	@Qualifier("mbvidPool")
//	ServicePool<MbvIdServiceClient> mbvidPool;
	
	/*
	 * 
	 */
	protected Merchant authenticate(CheckoutRequest request) throws CheckoutException {
		Merchant result = null;
		
		Merchant sysMerchant = merchantMap.get(request.getMerchant().getUsername());
		
		if (sysMerchant == null) {
			throw new CheckoutException(CheckoutErrorMsg.NON_EXISTED_MERCHANT);
		}
		
		String hashedInfo = null;
		
		//
		if (CheckoutRequest.SET_CHECKOUT.equalsIgnoreCase(request.getMethod())) {
			String info = request.getInvoice().getInvoiceId();
			for (InvoiceItem item : request.getInvoice().getItems()) {
				info += item.getSku() + item.getPrice() + item.getQuantity();
			}
			
			hashedInfo = CryptoUtil.bytes2Hex(CryptoUtil.hashMsgWithAlg(
										CryptoUtil.SHA1, info.getBytes(), null));
		}
		else {
			hashedInfo = CryptoUtil.bytes2Hex(CryptoUtil.hashMsgWithAlg(
										CryptoUtil.SHA1, request.getCheckoutId().getBytes(), null));
		}
		
		//
		String data = hashedInfo + sysMerchant.getPassword();
		
		String hashedData = CryptoUtil.bytes2Hex(CryptoUtil.hashMsgWithAlg(
								CryptoUtil.SHA1, data.getBytes(), null));
		
		if (hashedData.equalsIgnoreCase(request.getMerchant().getSignature())) {
			result = sysMerchant;
			result.setSignature(request.getMerchant().getSignature());
		}
		else {
			throw new CheckoutException(CheckoutErrorMsg.WRONG_MERCHANT_CREDENTIALS);
		}
		
		return result;
	}
	
	/*
	 * 
	 */
	protected String generateCheckoutId(Merchant merchant) {
		String checkoutId = DateUtil.formatDate(new Date(),
				DateUtil.DATE_YYYY_MM_DD);

		checkoutId += CryptoUtil
				.generateOtpFromHexPin(generateToken(merchant));
		
		return checkoutId;
	}
	
	/*
	 * 
	 */
	protected String generateToken(Merchant credential) {
		String message = credential.getUsername();
		message += credential.getSignature();
		message += UUID.randomUUID().toString();

		String token = CryptoUtil.bytes2Hex(CryptoUtil.hashMsgWithAlg(
				CryptoUtil.SHA1, message.getBytes(), null));

		return token;
	}
	
//	/*
//	 * 
//	 */
//	private Merchant authenticateMerchant(Merchant merchant) throws Exception {
//		// Authenticate merchant user
//		MbvIdServiceClient mbvidService = mbvidPool.acquire();
//
//		AuthUser user = mbvidService.authenticate(merchant.getUsername(),
//				merchant.getPassword(), "shaHex");
//
//		// Get merchant account info
//		McaCoreClient mcaCoreService = mcaCorePool.acquire();
//		MCAUserFilter filter = new MCAUserFilter();
//		filter.setValue("loginId", "mobivi.vn:"+user.getUserId());
//		
//		List<MCAUser> mcaUsers = mcaCoreService.searchUser(filter).getItems();
//		
//		for (int i = 0; i < mcaUsers.size(); i++) {
//			MCAUser mcaUser = mcaUsers.get(i);
//			if (mcaUser.getStatus() == null) {
//				mcaUser.setStatus(MCAUser.Status.active);
//			}
//			if (mcaUser.getStatus().equals(MCAUser.Status.terminated)) {
//				mcaUsers.remove(i);
//				i--;
//			}
//		}
//
//		if (mcaUsers.size() == 0) {
//			merchant = null;
//			
//		} else {
//			MCAUser mcaUser = mcaUsers.get(0);
//			MCAUser.Status status = mcaUser.getStatus();
//
//			if (!status.equals(MCAUser.Status.active)) {
//
//			} else {
//				merchant.setAccountId(mcaUser.getXAccountId());
//			}
//		}
//		
//		return merchant;
//	}
}
