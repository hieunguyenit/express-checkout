package com.mbv.mca.checkout.task;


import java.util.concurrent.TimeUnit;

/**
 * This interface must be configured with AspectJ to make sure that transaction is required for
 * {@link #execute()}
 *  
 */
public interface XTask {
	/**
	 * execute the task
	 */
	void execute();
	
	/**
	 * get {@link TimeUnit} for next execution time
	 * @return
	 */
	TimeUnit getTimeUnit();
	
	/**
	 * Get the time for next execution. This method is invoked right after each execution. To 
	 * stop execution, simply return {@link Long#MAX_VALUE}
	 * @return
	 */
	long getNextTime();
}
