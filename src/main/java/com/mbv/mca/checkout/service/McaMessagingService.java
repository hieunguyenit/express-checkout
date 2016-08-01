package com.mbv.mca.checkout.service;

import java.util.ArrayList;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mbv.content.PageView;
import com.mbv.mca.MCADataFilter;
import com.mbv.mca.MCAException;
import com.mbv.mca.MCAFolder;
import com.mbv.mca.MCAFolderFilter;
import com.mbv.mca.MCAMessage;
import com.mbv.mca.MCAMessageFilter;
import com.mbv.mca.ws.McaError;
import com.mbv.mca.ws.messaging.McaFault;
import com.mbv.mca.ws.messaging.McaFolderList;
import com.mbv.mca.ws.messaging.McaMessageList;
import com.mbv.mca.ws.messaging.McaMessaging;
import com.mbv.services.ServicePool;

/**
 * Wrapper of {@link McaMessagingService} to expose the required APIs 
 * @author Nam Pham
 *
 */
@Component("mcaMessagingService")
public class McaMessagingService extends AbstractService {
	private static final Log LOG = LogFactory.getLog(McaMessagingService.class);
	
	@Autowired
	@Resource(name="mcaMessagingPool")
	ServicePool<McaMessaging> mcaMessagingPool;
	
	McaMessaging acquireStub() throws MCAException {
		return super.acquireStub(mcaMessagingPool);
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
	
	public PageView<MCAFolder> searchFolder(MCAFolderFilter filter) throws MCAException{
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
		
		McaMessaging stub = null;
		try {
			stub = acquireStub();
			McaFolderList list = stub.searchFolder(f);
			PageView<MCAFolder> pv = new PageView<MCAFolder>(
					list.getItems()!=null?list.getItems():new ArrayList<MCAFolder>(0));
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
				mcaMessagingPool.release(stub);
		}
	}

	public PageView<MCAMessage> searchMessage(MCAMessageFilter filter) throws MCAException {
		MCADataFilter f = new MCADataFilter();
		f.setQuery(filter.getQuery());
		f.setPageNumber(filter.getPageNumber());
		f.setPageSize(filter.getPageSize());
		f.getProperties().putAll(filter.getProperties());
		
		if (filter.getOrderBy()!=null) {
			f.setValue("orderBy", filter.getOrderBy());
			f.setValue("orderAsc", filter.isAsc()?1:0);
		}
		
		if (filter.getOrigMessageId()!=null)
			f.setValue("origMessageId", filter.getOrigMessageId());
		
		
		McaMessaging stub = null;
		try {
			stub = acquireStub();
			McaMessageList list = stub.searchMessage(f);
			PageView<MCAMessage> pv = new PageView<MCAMessage>(
					list.getItems()!=null?list.getItems():new ArrayList<MCAMessage>(0));
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
				mcaMessagingPool.release(stub);
		}
	}

	public MCAMessage searchUnique(MCAMessageFilter filter) throws MCAException{
		PageView<MCAMessage> pv = searchMessage(filter);
		if (pv.getItems()==null || pv.getItems().size()==0)
			throw new MCAException("message not found");
		return pv.getItems().get(0);
	}

	/**
	 * save a single message
	 * @param message
	 * @throws MCAException
	 */
	public void saveMessage(MCAMessage message) throws MCAException{
		saveMessage(new MCAMessage[]{message});
	}
	
	/**
	 * save a batch of messages
	 * @param messages
	 * @throws MCAException
	 */
	public void saveMessage(MCAMessage[] messages) throws MCAException{
		McaMessaging stub = null;
		try {
			stub = acquireStub();
			stub.saveMessage(messages);
		}
		catch (Throwable ex) {
			throw generateException(ex);
		}
		finally {
			if (stub!=null)
				mcaMessagingPool.release(stub);
		}
	}

	public MCAMessage sendMessage(MCAMessage msg) throws MCAException {
		McaMessaging stub = null;
		try {
			stub = acquireStub();
			return stub.sendMessage(msg, true);
		}
		catch (Throwable ex) {
			throw generateException(ex);
		}
		finally {
			if (stub!=null)
				mcaMessagingPool.release(stub);
		}
	}
	
	public void postMessage(MCAMessage msg) throws MCAException {
		McaMessaging stub = null;
		try {
			stub = acquireStub();
			stub.sendMessage(msg, false);
		}
		catch (Throwable ex) {
			throw generateException(ex);
		}
		finally {
			if (stub!=null)
				mcaMessagingPool.release(stub);
		}
	}
}
