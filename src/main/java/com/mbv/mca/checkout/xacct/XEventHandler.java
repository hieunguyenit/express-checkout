package com.mbv.mca.checkout.xacct;

/**
 * Event handler processes event data. Implementation of this interface should be 
 * aware that XEventException is not expected or must know how caller handle thrown
 * exception.
 * @author Nam Pham
 *
 * @param <T>
 */
public interface XEventHandler<T> {
	/**
	 * handle an event 
	 * @param event
	 * @throws XEventException
	 */
	void handle(XEvent<T> event) throws XEventException;
}
