package com.mbv.mca.checkout.service;

import java.rmi.RemoteException;

import com.mbv.mca.MCAException;
import com.mbv.services.ServicePool;

public class AbstractService {
	
	protected <T> T acquireStub(ServicePool<T> pool) throws MCAException {
		try {
			T stub = pool.acquire(3000);
			if (stub==null)
				throw new MCAException("Timeout waiting for pooled object");
			return stub;
		} catch (InterruptedException e) {
			throw new MCAException("Interruped waiting for pooled object", e);
		}
	}

	protected MCAException generateException(Throwable ex) { 
		if (ex instanceof RemoteException) {
			return new MCAException(ex.getMessage()!=null?ex.getMessage():"RemoteException", ex);
		}
		else {
			return new MCAException(ex.getMessage()!=null?ex.getMessage():"UnknownException", ex);
		}
	}
}

