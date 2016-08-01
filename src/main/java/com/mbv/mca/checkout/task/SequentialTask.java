package com.mbv.mca.checkout.task;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This implementation of {@link XTask} assures that a list of them are executed 
 * sequentially.
 * 
 * <p>This class is required for {@link XTask}s which depends on other executions.</p>
 *
 */
public class SequentialTask implements XTask, InitializingBean {
	private static final Log LOG = LogFactory.getLog(SequentialTask.class);

	@Autowired
	XTaskExecutor executor;

	TimeUnit timeUnit = TimeUnit.MINUTES;
	
	long nextTime = 1;
	
	List<XTask> tasks;
	
	boolean executed = false;
	
	boolean repeative = false;
	
	boolean autoSubmit = false;
	
	public void setAutoSubmit(boolean autoSubmit) {
		this.autoSubmit = autoSubmit;
	}

	public void setRepeative(boolean repeative) {
		this.repeative = repeative;
	}

	public void setTasks(List<XTask> tasks) {
		this.tasks = tasks;
	}

	public void setExecutor(XTaskExecutor executor) {
		this.executor = executor;
	}

	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}

	public void setNextTime(long nextTime) {
		this.nextTime = nextTime;
	}
	
	public void afterPropertiesSet() throws Exception {
		if (tasks==null)
			throw new IllegalStateException("tasks are not set");
		
		executed = false;
		
		if (autoSubmit)
			executor.submitXTask(this);
	}

	@Override
	public void execute() {
		for (XTask task:tasks) {
			try {
				task.execute();
			}
			catch (Throwable ex) {
				LOG.error("Failed to execute task: " + ex.getMessage());
				return;
			}
		}
		executed = true;
	}

	@Override
	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	@Override
	public long getNextTime() {
		return executed && !repeative?Long.MAX_VALUE:nextTime;
	}
	
	
}
