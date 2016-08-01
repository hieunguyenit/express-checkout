package com.mbv.mca.checkout.task;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.stereotype.Component;


/**
 * Provide a registry for {@link XTask} execution. This implementation organizes
 * {@link XTask} with a {@link ScheduledExecutorService}. 
 *
 */
@Component("xTaskExecutor")
public class XTaskExecutor implements InitializingBean, DisposableBean, BeanFactoryAware  {
	static final Log LOG = LogFactory.getLog(XTaskExecutor.class);
	@Autowired
	ScheduledExecutorService scheduledExecutorService;
	
	boolean isShutdown = true;
	
	final List<XTaskRunnable> runnableList = new ArrayList<XTaskRunnable>(100);
	
	BeanFactory beanFactory;
	
	String taskWrapperName = "xTaskWrapper";
	Method setInnerTaskMethod;
	Method getInnerTaskMethod;
	
	@Override
	public void setBeanFactory(BeanFactory bf) throws BeansException {
		beanFactory = bf;
	}
	
	public void setTaskWrapperName(String taskWrapperName) {
		this.taskWrapperName = taskWrapperName;
	}

	public void setScheduledExecutorService(
			ScheduledExecutorService scheduledExecutorService) {
		this.scheduledExecutorService = scheduledExecutorService;
	}
	
	public void setXTaskList(List<XTask> tasks) {
		synchronized (runnableList) {
			runnableList.clear();
			for (XTask task:tasks) {
				runnableList.add(new XTaskRunnable(task));
			}
		}
		if (!isShutdown)
			startAllTasks();
	}
	
	/**
	 * Submit a {@link XTask} for execution
	 * @param task
	 */
	public void submitXTask(XTask task) {
		// create wrapper (transactional)
		Object wrapper = beanFactory.getBean(taskWrapperName);
		try {
			setInnerTaskMethod.invoke(wrapper, task);
		} catch (Throwable e) {
			throw new RuntimeException("failed to set inner task", e);
		}
		
		// create runnable
		XTaskRunnable runnable = new XTaskRunnable(wrapper);
		synchronized(runnableList) {
			runnableList.add(runnable);
		}
		if (!isShutdown)
			scheduleXTaskRunnable(runnable);
	}
	
	/**
	 * Runnable task
	 *
	 */
	class XTaskRunnable implements Runnable, XTask {
		Object objTask; 
		XTask task;
		
		XTaskRunnable(Object task) {
			this.objTask = task;
			if (task instanceof XTask)
				this.task = (XTask) task;
			else  {
				try {
					this.task = (XTask)getInnerTaskMethod.invoke(task);
				}
				catch (Throwable ex) {
					throw new RuntimeException("failed to get inner task", ex);
				}
			}
		}
		
		@Override
		public void run() {
			if (isShutdown) return;
			this.execute();
		}

		@Override
		public void execute() {
			Thread current = Thread.currentThread();
			String name = current.getName();
			try {
				try {
					current.setName("XTask-" + objTask.getClass().getSimpleName());
					if (objTask instanceof XTask)
						((XTask)objTask).execute();
					else {
						XTask.class.getMethod("execute").invoke(objTask);
					}
				}
				catch (Throwable ex) {
					if (LOG.isDebugEnabled())
						LOG.error("unexpected exception thrown by " + objTask + " " + ex.getMessage(), ex);
					else 
						LOG.error("unexpected exception thrown by " + objTask + " " + ex.getMessage());
				}
				scheduleXTaskRunnable(this);
			}
			finally {
				current.setName(name);
			}
			
		}

		@Override
		public TimeUnit getTimeUnit() {
			return task.getTimeUnit();
		}

		@Override
		public long getNextTime() {
			return task.getNextTime();
		}
	}
	
	/**
	 * schedule a task for future execution
	 * @param runnable
	 */
	void scheduleXTaskRunnable(XTaskRunnable runnable) {
		long next = runnable.getNextTime();
		
		if (next == Long.MAX_VALUE) {
			if (LOG.isDebugEnabled())
				LOG.debug("discarding task " + runnable.task);
			synchronized (runnableList) {
				runnableList.remove(runnable);
			}
			return;
		}
		TimeUnit unit = runnable.getTimeUnit();
		if (LOG.isTraceEnabled()) {
			LOG.trace("scheduling task to run in " + next + "(" + unit + ") " + runnable.task);
		}
		
		scheduledExecutorService.schedule(runnable, next, unit);
	}

	/**
	 * Destruction 
	 * @throws Exception
	 */
	@Override
	public void destroy() throws Exception {
		// terminate schedule loop
		isShutdown = true;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		isShutdown = false;
		if (beanFactory==null)
			throw new IllegalStateException("beanFactory is not set");
		
		if (beanFactory instanceof BeanDefinitionRegistry) {
			BeanDefinitionRegistry def = (BeanDefinitionRegistry)beanFactory;
			BeanDefinition beanDef = def.getBeanDefinition(taskWrapperName);
			
			// validate task wrapper type
			@SuppressWarnings("static-access")
			Class<?> klass = this.getClass().forName(beanDef.getBeanClassName());
			if (!XTaskWrapper.class.isAssignableFrom(klass))
				throw new IllegalArgumentException(taskWrapperName + " is not of type XTask");
			
			// validate task wrapper scope
			if (!beanDef.isPrototype())
				throw new IllegalArgumentException(taskWrapperName + "'scope is not prototype");
		
			// acquire setInnerTaskMethod
			Class<?> proxy = beanFactory.getBean(taskWrapperName).getClass();
			setInnerTaskMethod = proxy.getMethod("setInnerTask", XTask.class);
			getInnerTaskMethod = proxy.getMethod("getInnerTask");
		}
		else {
			LOG.warn("failed to validate " + taskWrapperName + " scope because beanFactory is not BeanDefinitionRegistry: " + beanFactory.getClass().getName());
		}
		
		startAllTasks();
	}
	
	/**
	 * statt all task in {@link #runnableList}
	 */
	void startAllTasks() {
		synchronized(runnableList) {
			for (XTaskRunnable runnable:runnableList) {
				scheduleXTaskRunnable(runnable);
			}
		}
	}

	
}
