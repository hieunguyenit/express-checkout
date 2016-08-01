package com.mbv.mca.checkout.jdo;

import java.util.List;

import com.mbv.mca.checkout.core.CheckoutException;
import com.mbv.mca.checkout.core.Settlement;

public interface SettlementDao {
	public Settlement get(Long id) throws CheckoutException;
	
	public void save(Settlement settlement) throws CheckoutException;
	
	public List<Settlement> search(Settlement settlement) throws CheckoutException;
	
	public List<Settlement> searchUndoneSettlement(String checkoutId) throws CheckoutException;
	
}
