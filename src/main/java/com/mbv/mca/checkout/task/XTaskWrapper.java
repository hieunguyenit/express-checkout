package com.mbv.mca.checkout.task;


import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * {@link XTask} implementation wrapper class for transaction scope configuration.
 * 
 * @author Nam Pham
 *
 */
@Component("xTaskWrapper")
@Scope("prototype")
public class XTaskWrapper implements XTaskInner {
	
	XTask innerTask;
	
	@Override
	public void setInnerTask(XTask innerTask) {
		this.innerTask = innerTask;
	}
	
	@Override
	public XTask getInnerTask() {
		return innerTask;
	}

	@Override
	public void execute() {
		innerTask.execute();
	}

	@Override
	public TimeUnit getTimeUnit() {
		return innerTask.getTimeUnit();
	}

	@Override
	public long getNextTime() {
		return innerTask.getNextTime();
	}
	
}