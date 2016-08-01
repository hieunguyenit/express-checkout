package com.mbv.mca.checkout.web.session;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mbv.mca.MCAUser;
import com.mbv.mca.checkout.web.SessionKeys;
import com.mbv.mca.checkout.xacct.XAcctAccessSoapImpl;
import com.mbv.mca.checkout.xacct.XAcctException;
import com.mbv.xacct.XAccount;

/**
 * 
 * implementation for validation logics.
 * 
 * @author hoang.phan
 *
 */

@Component("mcaValidator")
public class McaValidator implements SessionKeys {
	private static final Log LOG = LogFactory.getLog(McaValidator.class);
	
	@Autowired
	XAcctAccessSoapImpl xAcctAccess;
	
	@Autowired
	McaAuthenticator authenticator;
	
	public void validateBalance(HttpServletRequest req, double paymentAmount) throws McaException {
		MCAUser mcauser = authenticator.getAuthenticatedUser(req);
		
		String eccXAcctId = mcauser.getXAccountId();
		
		try {
			if(eccXAcctId!=null && !eccXAcctId.isEmpty()){
				XAccount eccAcct = xAcctAccess.get(eccXAcctId);
				if(mcauser.getCreditLimit() - eccAcct.getBalRunning() < paymentAmount) {
					throw new McaException("OVER_CREDIT_LIMIT");
				}
			}
		} catch (XAcctException e) {
			LOG.error("Failed to get eccAccount + " + eccXAcctId,e);
		}
		
	}
}
