package com.mbv.mca.checkout.task;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.mbv.mca.checkout.core.Settlement;
import com.mbv.mca.checkout.jdo.SettlementDao;
import com.mbv.mca.checkout.web.handler.DoCheckoutPaymentHandler;

public class SettlementTask implements XTask {
	
	static final Logger LOG = Logger.getLogger(SettlementTask.class);
	
	@Autowired
	@Qualifier("settlementDAO")
	SettlementDao settlementDAO;
	
	@Autowired
	@Qualifier("doCheckoutPaymentHandler")
	DoCheckoutPaymentHandler paymentHandler;
	
	@Override
	public void execute() {
		String currentXtranId = null;
		try {
			// Get undone settlement
			List<Settlement> undoneList = settlementDAO.searchUndoneSettlement(null);
			
			if (undoneList == null)
				return;
			
			// Capture fund then Update settlement status to done
			for (Settlement settlement : undoneList){
				currentXtranId = settlement.getXtranId();
				
				if (currentXtranId.indexOf("mfs") > -1) {
					paymentHandler.captureOrReleaseLoan(currentXtranId.substring(4), false);
				}
				else {
					paymentHandler.captureOrReleaseFund(currentXtranId, false);
				}
				
				settlement.setDone(true);
				settlementDAO.save(settlement);
			}
			
		} catch (Exception e) {
			LOG.debug("Capturing: "+currentXtranId +"\n"+ e.getMessage(),
															e.getCause());
		}
	}

	@Override
	public TimeUnit getTimeUnit() {
		return TimeUnit.MINUTES;
	}

	@Override
	public long getNextTime() {
		return 1;
	}

}
