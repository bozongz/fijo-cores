package com.fijo.cores.admin.task.trigger;

import java.io.Serializable;

import com.fijo.cores.admin.task.model.TaskTriggerDefinition;
import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;

import com.fijo.cores.admin.task.model.TaskTriggerDefinition;
import com.fijo.cores.cache.IGenericCache;

public class DbCronTrigger extends CronTriggerFactoryBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2208672813940997090L;

	@Autowired
	@Qualifier(value="taskTriggerDefinitionCache")
	private IGenericCache<TaskTriggerDefinition, Integer> taskTriggerDefinitionCache;
	
	private String jobName;
	
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	/**
	 * @return the jobName
	 */
	public String getJobName() {
		return jobName;
	}

	@Override
	public void setJobDetail(JobDetail jobDetail) {
		setCronExpression(getCronExpressionFromDB());
		super.setJobDetail(jobDetail);		
	} 
	
	/**
	 * 每隔一分钟调用的表达式为： "0 0/1 * * * ?" 参考 TaskTriggerDefinition
	 * @return
	 */
	private String getCronExpressionFromDB() {
		TaskTriggerDefinition def = taskTriggerDefinitionCache.loadByUnique(getJobName());
		return " 0 "+def.getWhenminute()+" "+ def.getWhenhour()+" "+def.getWhenday()+" "+def.getWhenmonth()+" ? *";
	}
}
