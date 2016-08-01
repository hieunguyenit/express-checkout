package com.mbv.mca.checkout.xacct;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract class for event publishing.
 * @author Nam Pham
 *
 * @param <T>
 */
public abstract class XEventSource<T> {
	private final Log LOG = LogFactory.getLog(this.getClass());
	
	protected final List<XEventHandler<T>> handlers = new ArrayList<XEventHandler<T>>();
	
	/**
	 * get an iterator for list of handler
	 * @return
	 */
	public Iterator<XEventHandler<T>> getHandlers() {
		return handlers.iterator();
	}
	
	/**
	 * add an event handler
	 * @param handler
	 */
	public void addHandler(XEventHandler<T> handler) {
		if (handler==null) throw new NullPointerException("handler must not be null");
		handlers.add(handler);
		if (LOG.isDebugEnabled())
			LOG.debug("added event handler " + handler);
	}
	
	/**
	 * remove an event handler
	 * @param handler
	 */
	public void removeHandler(XEventHandler<T> handler) {
		if (handler==null) return;
		handlers.remove(handler);
		if (LOG.isDebugEnabled())
			LOG.debug("removed event handler " + handler);
	}
	
	/**
	 * fire an event with no exception expected
	 * @param event
	 */
	public void fireSafeEvent(XEvent<T> event) {
		for (XEventHandler<T> handler:handlers) {
			try {
				handler.handle(event);
			}
			catch (Throwable ex) {
				LOG.error("Error handling event " + event.getName() + ": " + event.getSource() + " " + event.getParams(), ex);
			}
		}
	}
	
	/**
	 * fire an event with 
	 * @param event
	 * @param ignoreError
	 * @throws XEventException
	 */
	public void fireEvent(XEvent<T> event, boolean ignoreError) throws XEventException {
		if (ignoreError) {
			fireSafeEvent(event);
		}
		else {
			for (XEventHandler<T> handler:handlers) {
				handler.handle(event);
			}
		}
	}
}
