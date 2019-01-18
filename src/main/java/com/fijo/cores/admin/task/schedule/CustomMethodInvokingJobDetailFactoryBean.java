/**
 * 
 */
package com.fijo.cores.admin.task.schedule;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.utils.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.JobDetailImpl;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.support.ArgumentConvertingMethodInvoker;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MethodInvoker;

import com.google.common.collect.Maps;
import com.fijo.cores.admin.task.model.TaskTriggerDefinition;
import com.fijo.cores.cache.IGenericCache;
import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.service.IGenericService;
import com.fijo.cores.utils.DateUtil;

/**
 * 参考MethodInvokingJobDetailFactoryBean并增加上次Job未执行结束，不启动下次Job的控制
 * 
 * @author zhangbo
 *
 */
public class CustomMethodInvokingJobDetailFactoryBean extends
		ArgumentConvertingMethodInvoker implements FactoryBean<JobDetail>,
		BeanNameAware, BeanClassLoaderAware, BeanFactoryAware,
		InitializingBean {
	
	protected static final Log log = LogFactory.getLog(CustomMethodInvokingJobDetailFactoryBean.class);	
	
	private String name;

	private String group = Scheduler.DEFAULT_GROUP;

	private boolean concurrent = true;

	private String targetBeanName;

	private String beanName;

	private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

	private BeanFactory beanFactory;

	private JobDetail jobDetail;
	
	private String defaultTargetMethod = "execute";

	private static Date fireDay = DateUtil.getCurrent();
	
	private String jobName;
	
	private static Map<Class<? extends QuartzJob>,JobExecuteCounter> jobExecuteCounterHolder = Maps.newHashMap();  
	
	@Autowired
	@Qualifier(value="taskTriggerDefinitionCache")
	private IGenericCache<TaskTriggerDefinition, Integer> taskTriggerDefinitionCache;
	
	/**
	 * @param targetMethod the targetMethod to set
	 */
	public String getTargetMethod() {
		return defaultTargetMethod;
	}

	/**
	 * @return the jobName
	 */
	public String getJobName() {
		return jobName;
	}


	/**
	 * @param jobName the jobName to set
	 */
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}


	/**
	 * Set the name of the job.
	 * <p>
	 * Default is the bean name of this FactoryBean.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Set the group of the job.
	 * <p>
	 * Default is the default group of the Scheduler.
	 * 
	 * @see org.quartz.Scheduler#DEFAULT_GROUP
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	/**
	 * Specify whether or not multiple jobs should be run in a concurrent
	 * fashion. The behavior when one does not want concurrent jobs to be
	 * executed is realized through adding the
	 * {@code @PersistJobDataAfterExecution} and
	 * {@code @DisallowConcurrentExecution} markers. More information on
	 * stateful versus stateless jobs can be found <a href=
	 * "http://www.quartz-scheduler.org/documentation/quartz-2.1.x/tutorials/tutorial-lesson-03"
	 * >here</a>.
	 * <p>
	 * The default setting is to run jobs concurrently.
	 */
	public void setConcurrent(boolean concurrent) {
		this.concurrent = concurrent;
	}

	/**
	 * Set the name of the target bean in the Spring BeanFactory.
	 * <p>
	 * This is an alternative to specifying {@link #setTargetObject
	 * "targetObject"}, allowing for non-singleton beans to be invoked. Note
	 * that specified "targetObject" and {@link #setTargetClass "targetClass"}
	 * values will override the corresponding effect of this "targetBeanName"
	 * setting (i.e. statically pre-define the bean type or even the bean
	 * object).
	 */
	public void setTargetBeanName(String targetBeanName) {
		this.targetBeanName = targetBeanName;
	}

	@Override
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.beanClassLoader = classLoader;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	@Override
	protected Class<?> resolveClassName(String className)
			throws ClassNotFoundException {
		return ClassUtils.forName(className, this.beanClassLoader);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void afterPropertiesSet() throws ClassNotFoundException,
			NoSuchMethodException {
		prepare();

		// Use specific name if given, else fall back to bean name.
		String name = (this.name != null ? this.name : this.beanName);

		// Consider the concurrent flag to choose between stateful and stateless
		// job.
		Class<?> jobClass = (this.concurrent ? MethodInvokingJob.class
				: StatefulMethodInvokingJob.class);

		// Build JobDetail instance.
		JobDetailImpl jdi = new JobDetailImpl();
		jdi.setName(name);
		jdi.setGroup(this.group);
		jdi.setJobClass((Class) jobClass);
		jdi.setDurability(true);
		jdi.getJobDataMap().put("methodInvoker", this);
		//jdi.getJobDataMap().put("targetJob", getTargetObject());
		this.jobDetail = jdi;

		postProcessJobDetail(this.jobDetail);
		
		TaskTriggerDefinition def = taskTriggerDefinitionCache.loadByUnique(getJobName());
		QuartzJob targetJob = (QuartzJob) getTargetObject();
		jobExecuteCounterHolder.put(targetJob.getClass(), new JobExecuteCounter(def.getMaxFireTimes(), 0));
		//targetJob.setMaxFireTimes(def.getMaxFireTimes()); //获取每个Job的最大执行次数
	}

	/**
	 * Callback for post-processing the JobDetail to be exposed by this
	 * FactoryBean.
	 * <p>
	 * The default implementation is empty. Can be overridden in subclasses.
	 * 
	 * @param jobDetail
	 *            the JobDetail prepared by this FactoryBean
	 */
	protected void postProcessJobDetail(JobDetail jobDetail) {
	}

	/**
	 * Overridden to support the {@link #setTargetBeanName "targetBeanName"}
	 * feature.
	 */
	@Override
	public Class<?> getTargetClass() {
		Class<?> targetClass = super.getTargetClass();
		if (targetClass == null && this.targetBeanName != null) {
			Assert.state(this.beanFactory != null,
					"BeanFactory must be set when using 'targetBeanName'");
			targetClass = this.beanFactory.getType(this.targetBeanName);
		}
		return targetClass;
	}

	/**
	 * Overridden to support the {@link #setTargetBeanName "targetBeanName"}
	 * feature.
	 */
	@Override
	public Object getTargetObject() {
		Object targetObject = super.getTargetObject();
		if (targetObject == null && this.targetBeanName != null) {
			Assert.state(this.beanFactory != null,
					"BeanFactory must be set when using 'targetBeanName'");
			targetObject = this.beanFactory.getBean(this.targetBeanName);
		}
		return targetObject;
	}

	@Override
	public JobDetail getObject() {
		return this.jobDetail;
	}

	@Override
	public Class<? extends JobDetail> getObjectType() {
		return (this.jobDetail != null ? this.jobDetail.getClass()
				: JobDetail.class);
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	/**
	 * Quartz Job implementation that invokes a specified method. Automatically
	 * applied by MethodInvokingJobDetailFactoryBean.
	 */
	public static class MethodInvokingJob extends QuartzJobBean {

		private MethodInvoker methodInvoker;

//		private QuartzJob targetJob;
//		
//		/**
//		 * @param targetJob the targetJob to set
//		 */
//		public void setTargetJob(QuartzJob targetJob) {
//			this.targetJob = targetJob;
//		}

		/**
		 * Set the MethodInvoker to use.
		 */
		public void setMethodInvoker(MethodInvoker methodInvoker) {
			this.methodInvoker = methodInvoker;
		}

		/**
		 * @return the methodInvoker
		 */
		public MethodInvoker getMethodInvoker() {
			return methodInvoker;
		}

//		/**
//		 * @return the targetJob
//		 */
//		public QuartzJob getTargetJob() {
//			return targetJob;
//		}

		/**
		 * Invoke the method via the MethodInvoker.
		 */
		@Override
		protected void executeInternal(JobExecutionContext context)
				throws JobExecutionException {
			JobExecuteCounter counter = jobExecuteCounterHolder.get(methodInvoker.getTargetClass());
			Date today = DateUtil.getCurrent();
			if(DateUtil.daysBetweenDates(today, fireDay)>0){	
				counter.setFireCounter(0); //隔日起始计数清零
				fireDay = today;
			}			
			if (!isJobRunning(context, this)){
				if(counter.getMaxFireTimes()==null || counter.getFireCounter()< counter.getMaxFireTimes()){
					counter.setFireCounter(counter.getFireCounter() + executeTask(context, this));
					log.debug(methodInvoker.getTargetClass()+"current counter is: "+counter.getFireCounter() +", and the max fire times is: "+counter.getMaxFireTimes());
				}
			}
		}
	}

	/**
	 * Extension of the MethodInvokingJob, implementing the StatefulJob
	 * interface. Quartz checks whether or not jobs are stateful and if so,
	 * won't let jobs interfere with each other.
	 */
	@PersistJobDataAfterExecution
	@DisallowConcurrentExecution
	public static class StatefulMethodInvokingJob extends MethodInvokingJob {

		// No implementation, just an addition of the tag interface StatefulJob
		// in order to allow stateful method invoking jobs.
	}
	
	private static int executeTask(JobExecutionContext context, MethodInvokingJob job){
		try {
			context.setResult(job.methodInvoker.invoke()); //根据defaultTargetMethod执行execute方法
		} catch (Exception e) {
			log.error("Exception during execute job task......");
			log.error(Exceptions.getStackTraceAsString(e));
			return 0;
		}
		return 1;
	}
	
	private static boolean isJobRunning(JobExecutionContext context, MethodInvokingJob currentJob) {
		Map<Class<?>, Integer> runningJobClassMap = Maps.newHashMap(); 
		List<JobExecutionContext> jobCtxs;
		try {
			jobCtxs = context.getScheduler().getCurrentlyExecutingJobs();	
			for (JobExecutionContext jobCtx : jobCtxs) {
				Job obj = jobCtx.getJobInstance();
				if(obj!=null && obj instanceof MethodInvokingJob){
					MethodInvokingJob runningJob = (MethodInvokingJob)obj;	
					Integer counter = runningJobClassMap.get(runningJob.getMethodInvoker().getTargetClass());
					counter = counter == null? 0:counter+1;
					runningJobClassMap.put(runningJob.getMethodInvoker().getTargetClass(), counter);									
				}
				if (runningJobClassMap.get(currentJob.getMethodInvoker().getTargetClass()) > 1) {
					return true;
				}
			}
		} catch (SchedulerException e) {
			log.warn("Exception during check job running......");
		}
		catch (Exception e) {
			log.warn("Exception during check job running......");
		}
		return false;
	}
}
