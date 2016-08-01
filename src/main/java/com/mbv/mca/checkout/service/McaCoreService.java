package com.mbv.mca.checkout.service;

import java.util.ArrayList;

import javax.annotation.Resource;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mbv.content.PageView;
import com.mbv.mca.MCADataFilter;
import com.mbv.mca.MCAException;
import com.mbv.mca.MCAGroup;
import com.mbv.mca.MCAGroupFilter;
import com.mbv.mca.MCAOrg;
import com.mbv.mca.MCAOrgFilter;
import com.mbv.mca.MCAUser;
import com.mbv.mca.MCAUserFilter;
import com.mbv.mca.ws.McaError;
import com.mbv.mca.ws.McaGroupList;
import com.mbv.mca.ws.McaOrgList;
import com.mbv.mca.ws.McaUserList;
import com.mbv.mca.ws.core.McaCore;
import com.mbv.mca.ws.core.McaFault;
import com.mbv.services.ServicePool;

/**
 * Wrapper of {@link McaCore} to expose required APIs.
 * @author Nam Pham
 *
 */
@Component("mcaCoreService")
public class McaCoreService extends AbstractService {
	
	private static final Log LOG = LogFactory.getLog(McaCoreService.class);
	
	@Autowired
	@Resource(name="mcaCorePool")
	ServicePool<McaCore> mcaCorePool;
	
	McaCore acquireStub() throws MCAException {
		return super.acquireStub(mcaCorePool);
	}
	
	@Override
	protected MCAException generateException(Throwable ex) { 
		if (ex instanceof McaFault) {
			McaFault fault = (McaFault)ex;
			McaError error = fault.getFaultMessage();
			String msg;
			if (error!=null) {
				msg = "McaError " + error.getMessage();
				LOG.error(msg + "\n" + error.getDetail());
			}
			else {
				msg = "McaFault " + fault.getMessage();
				LOG.error(msg, ex);
			}
			
			return new MCAException(msg, fault);
		}
		else {
			return super.generateException(ex);
		}
	}

	public PageView<MCAGroup> searchGroup(MCAGroupFilter filter) throws MCAException {
		MCADataFilter f = new MCADataFilter();
		f.setQuery(filter.getQuery());
		f.setPageNumber(filter.getPageNumber());
		f.setPageSize(filter.getPageSize());
		f.getProperties().putAll(filter.getProperties());
		
		if (filter.getOrderBy()!=null) {
			f.setValue("orderBy", filter.getOrderBy());
			f.setValue("orderAsc", filter.isAsc()?1:0);
		}
		
		if (filter.getName()!=null)
			f.setValue("name", filter.getName());
		if (filter.getOrgShortName()!=null)
			f.setValue("orgShortName", filter.getOrgShortName());
		if (filter.getGroupId()!=null)
			f.setValue("groupId", filter.getGroupId());
		
		McaCore stub = null;
		try {
			stub = acquireStub();
			
			long last = 0;
			if (LOG.isDebugEnabled()) {
				last = System.currentTimeMillis();
			}
			
			McaGroupList list = stub.searchGroup(f);
			PageView<MCAGroup> pv = new PageView<MCAGroup>(
					list.getItems()!=null?list.getItems():new ArrayList<MCAGroup>(0));
			
			if (LOG.isDebugEnabled()) {
				last = System.currentTimeMillis() - last;
				LOG.debug("searched groups " + ReflectionToStringBuilder.toString(f) 
						+ " in " + last + "ms");
			}
			
			pv.setTotal((int)list.getTotal());
			pv.setPageNumber(f.getPageNumber());
			pv.setPageSize(f.getPageSize());
			
			return pv;
		}
		catch (Throwable ex) {
			throw generateException(ex);
		}
		finally {
			if (stub!=null)
				mcaCorePool.release(stub);
		}
	}

	public void removeGroup(MCAGroup group) throws MCAException {
		throw new MCAException("TEMPORARILY UNAVAILABLE");
	}

	public MCAGroup saveGroup(MCAGroup group) throws MCAException{
		McaCore stub = null;
		try {
			stub = acquireStub();
			long last = 0;
			if (LOG.isDebugEnabled()) {
				last = System.currentTimeMillis();
			}
			group = stub.saveGroup(group);
			
			if (LOG.isDebugEnabled()) {
				last = System.currentTimeMillis() - last;
				LOG.debug("saved group " + group.getName() + " (" 
						+ group.getOrgShortName() + ") in " + last + "ms");
			}
			
			return group;
		}
		catch (Throwable ex) {
			throw generateException(ex);
		}
		finally {
			if (stub!=null)
				mcaCorePool.release(stub);
		}
	}

	public PageView<MCAUser> searchUser(MCAUserFilter filter) throws MCAException {
		MCADataFilter f = new MCADataFilter();
		f.setQuery(filter.getQuery());
		f.setPageNumber(filter.getPageNumber());
		f.setPageSize(filter.getPageSize());
		f.getProperties().putAll(filter.getProperties());
		
		if (filter.getOrderBy()!=null) {
			f.setValue("orderBy", filter.getOrderBy());
			f.setValue("orderAsc", filter.isAsc()?1:0);
		}
		
		if (filter.getOrgShortName()!=null)
			f.setValue("orgShortName", filter.getOrgShortName());
		if (filter.getGroupId()!=null)
			f.setValue("groupId", filter.getGroupId());
		if (filter.getLoginId()!=null)
			f.setValue("loginId", filter.getLoginId());
		if (filter.getEmail()!=null)
			f.setValue("email", filter.getEmail());
		if (filter.getMobile()!=null)
			f.setValue("mobile", filter.getMobile());
		if (filter.getUserId()!=null)
			f.setValue("userId", filter.getUserId());
		if (filter.getOrgAdmin()!=null)
			f.setValue("orgAdmin", filter.getOrgAdmin()?1:0);
		
		McaCore stub = null;
		try {
			stub = acquireStub();
			McaUserList list = stub.searchUser(f);
			
			long last = 0;
			if (LOG.isDebugEnabled())
				last = System.currentTimeMillis();
			
			PageView<MCAUser> pv = new PageView<MCAUser>(
					list.getItems()!=null?list.getItems():new ArrayList<MCAUser>(0));
			
			if (LOG.isDebugEnabled()) {
				last = System.currentTimeMillis() - last;
				LOG.debug("searched users " + ReflectionToStringBuilder.toString(f) 
						+ " in " + last + "ms");
			}
			
			pv.setTotal((int)list.getTotal());
			pv.setPageNumber(f.getPageNumber());
			pv.setPageSize(f.getPageSize());
			
			return pv;
		}
		catch (Throwable ex) {
			throw generateException(ex);
		}
		finally {
			if (stub!=null)
				mcaCorePool.release(stub);
		}
	}

	public MCAUser saveUser(MCAUser user) throws MCAException {
		McaCore stub = null;
		try {
			stub = acquireStub();
			
			long last = 0;
			if (LOG.isDebugEnabled()) {
				last = System.currentTimeMillis();
			}
			
			user = stub.saveUser(user);
			
			if (LOG.isDebugEnabled()) {
				last = System.currentTimeMillis() - last;
				LOG.debug("saved user " + user.getUserId() + " in " + last + "ms");
			}
			return user;
		}
		catch (Throwable ex) {
			throw generateException(ex);
		}
		finally {
			if (stub!=null)
				mcaCorePool.release(stub);
		}
	}

	public PageView<MCAOrg> searchOrg(MCAOrgFilter filter) throws MCAException {
		MCADataFilter f = new MCADataFilter();
		f.setQuery(filter.getQuery());
		f.setPageNumber(filter.getPageNumber());
		f.setPageSize(filter.getPageSize());
		f.getProperties().putAll(filter.getProperties());
		
		if (filter.getOrderBy()!=null) {
			f.setValue("orderBy", filter.getOrderBy());
			f.setValue("orderAsc", filter.isAsc()?1:0);
		}
		
		if (filter.getShortName()!=null)
			f.setValue("shortName", filter.getShortName());
		if (filter.getLongName()!=null)
			f.setValue("longName", filter.getLongName());
		if (filter.getType()!=null)
			f.setValue("type", filter.getType());
		if (filter.getXAccountId()!=null)
			f.setValue("xAccountId", filter.getXAccountId());
		
		McaCore stub = null;
		try {
			stub = acquireStub();
			McaOrgList list = stub.searchOrg(f);
			
			long last = 0;
			if (LOG.isDebugEnabled()) {
				last = System.currentTimeMillis();
			}
			PageView<MCAOrg> pv = new PageView<MCAOrg>(
					list.getItems()!=null?list.getItems():new ArrayList<MCAOrg>(0));
			
			if (LOG.isDebugEnabled()) {
				last = System.currentTimeMillis() - last;
				LOG.debug("saerch orgs " + ReflectionToStringBuilder.toString(f) 
						+ " in " + last + "ms");
			}
			
			pv.setTotal((int)list.getTotal());
			pv.setPageNumber(f.getPageNumber());
			pv.setPageSize(f.getPageSize());
			
			return pv;
		}
		catch (Throwable ex) {
			throw generateException(ex);
		}
		finally {
			if (stub!=null)
				mcaCorePool.release(stub);
		}
	}
	
}
