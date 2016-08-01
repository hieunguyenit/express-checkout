package com.mbv.mca.checkout.service;

import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.mbv.mca.MCADataFilter;
import com.mbv.mca.MCAFolder;
import com.mbv.mca.MCAMessage;
import com.mbv.mca.ws.messaging.McaFault;
import com.mbv.mca.ws.messaging.McaFolderList;
import com.mbv.mca.ws.messaging.McaMessageList;
import com.mbv.mca.ws.messaging.McaMessaging;
import com.mbv.mca.ws.messaging.McaMessagingStub;
import com.mbv.services.AbstractService;

public class McaMessagingClient extends AbstractService implements McaMessaging, InitializingBean, DisposableBean {
	McaMessagingStub stub;
	
	protected McaMessagingClient(String url, long timeoutMillis) throws AxisFault {
		super(timeoutMillis);
		stub = new McaMessagingStub(url);
	}
	
	@Override
	public void destroy() throws Exception {
		super.detachStub(stub);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.attachStub(stub);
	}
	
	@Override
	public MCAFolder saveFolder(MCAFolder folder) throws RemoteException,
			McaFault {
		return stub.saveFolder(folder);
	}

	@Override
	public void saveMessage(MCAMessage[] messages) throws RemoteException,
			McaFault {
		stub.saveMessage(messages);
	}

	@Override
	public McaFolderList searchFolder(MCADataFilter filter)
			throws RemoteException, McaFault {
		return stub.searchFolder(filter);
	}

	@Override
	public McaMessageList searchMessage(MCADataFilter filter)
			throws RemoteException, McaFault {
		return stub.searchMessage(filter);
	}

	@Override
	public MCAMessage sendMessage(MCAMessage message, boolean wait)
			throws RemoteException, McaFault {
		return stub.sendMessage(message, wait);
	}
	
	

}
