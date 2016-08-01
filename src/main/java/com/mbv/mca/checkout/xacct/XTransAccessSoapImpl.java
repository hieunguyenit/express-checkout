package com.mbv.mca.checkout.xacct;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mbv.mca.checkout.xacct.XTransAccess;
import com.mbv.mca.checkout.xacct.XTransException;
import com.mbv.services.ServicePool;
import com.mbv.xacct.XPageView;
import com.mbv.xacct.XTransaction;
import com.mbv.xacct.XTransactionFilter;
import com.mbv.xacct.ws.XError;
import com.mbv.xacct.ws.XFault;
import com.mbv.xacct.ws.XTransactionList;
import com.mbv.xacct.ws.client.XAcctClient;

/**
 * 
 * @author Nam Pham
 *
 */

@Component("xTransAccess")
public class XTransAccessSoapImpl implements XTransAccess {
	private static final Log LOG = LogFactory.getLog(XTransAccessSoapImpl.class);
	
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
	XTransException generateException(Throwable ex) {
		LOG.error("XTransaction access exception: " + ex.getMessage(), ex);
		XTransException x = null;
		if (ex instanceof XFault) {
			XFault fault = (XFault)ex;
			XError error = fault.getFaultMessage();
			if (error!=null) {
				x = new XTransException(error.getCode()!=null?error.getCode():error.getStrace(), ex);
			}
		}
		if (x==null) {
			x = new XTransException(ex.getMessage()!=null?ex.getMessage():ex.toString(), ex);
		}
		return x;
	}
	
	@Override
	public XTransaction save(XTransaction xTrans) throws XTransException {
		XAcctClient client = null;
		try {
			client = xAcctPool.acquire();
			return client.saveXTrans(xTrans);
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
	public XPageView<XTransaction> search(XTransactionFilter filter)
			throws XTransException {
		XAcctClient client = null;
		try {
			client = xAcctPool.acquire();
			XTransactionList list = client.searchXTrans(filter);
			XPageView<XTransaction> pv = new XPageView<XTransaction>(list.getItems(), list.getTotalCount());
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
	public XTransaction get(String xtransId) throws XTransException {
		if (xtransId == null || xtransId.isEmpty()) 
			throw new IllegalArgumentException("xtransId not specified");
		XTransactionFilter filter = new XTransactionFilter();
		filter.setId(xtransId);
		filter.setPageNumber(1);
		filter.setPageSize(1);
		XPageView<XTransaction> pv = search(filter);
		if (pv.getTotalCount()==0)
			throw new XTransException("transaction not found");
		return pv.getItems().get(0);
	}

}
