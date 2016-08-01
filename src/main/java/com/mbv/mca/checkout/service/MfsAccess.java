package com.mbv.mca.checkout.service;

import java.rmi.RemoteException;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mbv.mfs.data.Loan;
import com.mbv.mfs.data.LoanFilter;
import com.mbv.mfs.data.LoanList;
import com.mbv.mfs.data.ProductConfigFilter;
import com.mbv.mfs.service.client.MfsService;
import com.mbv.mfs.service.client.MfsServiceCallbackHandler;
import com.mbv.mfs.service.client.ProductConfigList;
import com.mbv.services.ServicePool;

@Component("mfsAccess")
public class MfsAccess implements MfsService {
	
	@Autowired
	@Resource(name = "mfsServicePool")
	ServicePool<MfsService> mfsPool;

	@Override
	public ProductConfigList searchProduct(ProductConfigFilter data) throws RemoteException {
		MfsService mfsService = null;
		try {
			mfsService = mfsPool.acquire(10000);
			return mfsService.searchProduct(data);
		} catch (InterruptedException e) {
			throw new RemoteException("cannot acquire mfs service from ");
		} finally {
			if(mfsService != null) {
				mfsPool.release(mfsService);
			}
		}
	}

	@Override
	public void startSearchProduct(ProductConfigFilter data, MfsServiceCallbackHandler callback) throws RemoteException {
		MfsService mfsService = null;
		try {
			mfsService = mfsPool.acquire(10000);
			mfsService.startSearchProduct(data, callback);
		} catch (InterruptedException e) {
			throw new RemoteException("cannot acquire mfs service from ");
		} finally {
			if(mfsService != null) {
				mfsPool.release(mfsService);
			}
		}
	}

	@Override
	public LoanList searchLoan(LoanFilter data) throws RemoteException {
		MfsService mfsService = null;
		try {
			mfsService = mfsPool.acquire(10000);
			return mfsService.searchLoan(data);
		} catch (InterruptedException e) {
			throw new RemoteException("cannot acquire mfs service from ");
		} finally {
			if(mfsService != null) {
				mfsPool.release(mfsService);
			}
		}
	}

	@Override
	public void startSearchLoan(LoanFilter data, MfsServiceCallbackHandler callback) throws RemoteException {
		MfsService mfsService = null;
		try {
			mfsService = mfsPool.acquire(10000);
			mfsService.startSearchLoan(data, callback);
		} catch (InterruptedException e) {
			throw new RemoteException("cannot acquire mfs service from ");
		} finally {
			if(mfsService != null) {
				mfsPool.release(mfsService);
			}
		}
	}

	@Override
	public Loan saveLoan(Loan data) throws RemoteException {
		MfsService mfsService = null;
		try {
			mfsService = mfsPool.acquire(10000);
			return mfsService.saveLoan(data);
		} catch (InterruptedException e) {
			throw new RemoteException("cannot acquire mfs service from ");
		} finally {
			if(mfsService != null) {
				mfsPool.release(mfsService);
			}
		}
	}

	@Override
	public void startsaveLoan(Loan data, MfsServiceCallbackHandler callback) throws RemoteException {
		MfsService mfsService = null;
		try {
			mfsService = mfsPool.acquire(10000);
			mfsService.startsaveLoan(data, callback);
		} catch (InterruptedException e) {
			throw new RemoteException("cannot acquire mfs service from ");
		} finally {
			if(mfsService != null) {
				mfsPool.release(mfsService);
			}
		}
	}
}
