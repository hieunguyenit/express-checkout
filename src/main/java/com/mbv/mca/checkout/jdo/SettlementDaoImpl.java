package com.mbv.mca.checkout.jdo;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.mbv.mca.checkout.core.CheckoutException;
import com.mbv.mca.checkout.core.Settlement;

public class SettlementDaoImpl extends ObjectDaoImpl implements SettlementDao{

	@Override
	public Settlement get(Long id) throws CheckoutException {
		return get(Settlement.class, id);
	}

	@Override
	public void save(Settlement settlement) throws CheckoutException {
		if (settlement.getId() ==null) {
			settlement.setCreatedAt(new Date());
			super.save(settlement);
		}
		else {
			settlement.setUpdatedAt(new Date());
			super.merge(settlement);
		}
	}

	@Override
	public List<Settlement> search(Settlement settlement)
			throws CheckoutException {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(Settlement.class);
		
		if (settlement.getDone() != null ){
			criteria.add(Restrictions.eq("done", settlement.getDone()));
		}
		
		if (settlement.getCheckoutId() != null){
			criteria.add(Restrictions.eq("checkoutId", settlement.getCheckoutId()));
		}
		
		List<Settlement> items = findByCriteria(criteria);
		
		if (items.isEmpty())
			items = null;
		
		return items;
	}

	@Override
	public List<Settlement> searchUndoneSettlement(String checkoutId) throws CheckoutException {
		Settlement settlement = new Settlement();
		settlement.setDone(false);
		if (checkoutId != null)
			settlement.setCheckoutId(checkoutId);
		return search(settlement);
	}

}
