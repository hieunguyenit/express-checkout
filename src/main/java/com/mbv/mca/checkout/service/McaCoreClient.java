package com.mbv.mca.checkout.service;

import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.mbv.mca.MCADataFilter;
import com.mbv.mca.MCAGroup;
import com.mbv.mca.MCAUser;
import com.mbv.mca.ws.McaBillStatementList;
import com.mbv.mca.ws.McaEventList;
import com.mbv.mca.ws.McaGroupList;
import com.mbv.mca.ws.McaInvoiceList;
import com.mbv.mca.ws.McaOrgList;
import com.mbv.mca.ws.McaUserList;
import com.mbv.mca.ws.core.McaCore;
import com.mbv.mca.ws.core.McaCoreStub;
import com.mbv.mca.ws.core.McaFault;
import com.mbv.services.AbstractService;

public class McaCoreClient extends AbstractService implements McaCore, InitializingBean, DisposableBean {
	McaCoreStub stub;
	
	@Override
	public void destroy() throws Exception {
		super.detachStub(stub);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.attachStub(stub);
	}
	
	protected McaCoreClient(String url, long timeoutMillis) throws AxisFault {
		super(timeoutMillis);
		stub = new McaCoreStub(url);
	}
	
	@Override
	public MCAGroup saveGroup(MCAGroup group) throws RemoteException, McaFault {
		return stub.saveGroup(group);
	}

	@Override
	public MCAUser saveUser(MCAUser user) throws RemoteException, McaFault {
		return stub.saveUser(user);
	}

	@Override
	public McaGroupList searchGroup(MCADataFilter filter) throws RemoteException,
			McaFault {
		return stub.searchGroup(filter);
	}

	@Override
	public McaOrgList searchOrg(MCADataFilter filter) throws RemoteException,
			McaFault {
		return stub.searchOrg(filter);
	}

	@Override
	public McaUserList searchUser(MCADataFilter filter) throws RemoteException,
			McaFault {
		return stub.searchUser(filter);
	}

	@Override
	public McaBillStatementList searchBillStatement(MCADataFilter Filter)
			throws RemoteException, McaFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public McaEventList searchEvent(MCADataFilter Filter)
			throws RemoteException, McaFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public McaInvoiceList searchInvoice(MCADataFilter Filter)
			throws RemoteException, McaFault {
		// TODO Auto-generated method stub
		return null;
	}

}
