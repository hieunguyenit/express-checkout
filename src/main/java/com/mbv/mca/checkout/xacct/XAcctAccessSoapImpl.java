package com.mbv.mca.checkout.xacct;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mbv.services.ServicePool;
import com.mbv.xacct.XAccount;
import com.mbv.xacct.XAccountFilter;
import com.mbv.xacct.XPageView;
import com.mbv.xacct.ws.XAccountList;
import com.mbv.xacct.ws.XError;
import com.mbv.xacct.ws.XFault;
import com.mbv.xacct.ws.client.XAcctClient;

/**
 * 
 * @author Nam Pham
 *
 */
@Component("xAcctAccess")
public class XAcctAccessSoapImpl implements XAcctAccess {
	private static final Log LOG = LogFactory.getLog(XAcctAccessSoapImpl.class);
	
	ServicePool<XAcctClient> xAcctPool;
	
	@Autowired
	@Resource(name="xacctPool")
	public void setAcctPool(ServicePool<XAcctClient> xAcctPool) {
		this.xAcctPool = xAcctPool;
	}

	/**
	 * generate XTransException
	 * @param ex
	 * @return
	 */
	XAcctException generateException(Throwable ex) {
		LOG.error("XAccount access exception: " + ex.getMessage(), ex);
		XAcctException x = null;
		if (ex instanceof XFault) {
			XFault fault = (XFault)ex;
			XError error = fault.getFaultMessage();
			if (error!=null) {
				x = new XAcctException(error.getCode()!=null?error.getCode():error.getStrace(), ex);
			}
		}
		if (x==null) {
			x = new XAcctException(ex.getMessage()!=null?ex.getMessage():ex.toString(), ex);
		}
		return x;
	}
	
	public XPageView<XAccount> search(XAccountFilter filter) throws XAcctException {
		XAcctClient client = null;
		try {
			client = xAcctPool.acquire();
			XAccountList list = client.searchXAccount(filter);
			XPageView<XAccount> pv = new XPageView<XAccount>(list.getItems(), list.getTotalCount());
			return pv;
		}
		catch (Throwable ex) {
			throw generateException(ex);
		}
		finally {
			if (client!=null)
				xAcctPool.release(client);
		}
	}
	
	@Override
	public XAccount get(String acctId) throws XAcctException {
		if (acctId == null || acctId.isEmpty()) 
			throw new IllegalArgumentException("acctId not specified");
		XAccountFilter filter = new XAccountFilter();
		filter.setId(acctId);
		filter.setPageNumber(1);
		filter.setPageSize(1);
		XPageView<XAccount> pv = search(filter);
		if (pv.getTotalCount()==0)
			throw new XAcctException("account not found");
		return pv.getItems().get(0);
	}
	
}
