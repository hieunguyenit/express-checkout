package com.mbv.mca.checkout.task;



/**
 * this interface is to provide method access from AspectJ
 * 
 *
 */
public interface XTaskInner extends XTask {
	/**
	 * 
	 * @return
	 */
	XTask getInnerTask();
	
	/**
	 * 
	 * @param task
	 */
	void setInnerTask(XTask task);
}
