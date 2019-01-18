/**
 * 
 */
package com.fijo.cores.app.event;

import java.io.Serializable;

import com.fijo.cores.app.model.ProcessJsonData;
import com.fijo.cores.app.model.ProcessModel;
import org.springframework.context.ApplicationEvent;

import com.fijo.cores.app.model.ProcessJsonData;
import com.fijo.cores.app.model.ProcessModel;

/**
 * @author zhangbo
 *
 */
public class ProcessAuditLogEvent<T extends ProcessModel<?>, PK extends Serializable> extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6021098597021902220L;
	private ProcessJsonData<T,PK> processData;
	private Integer previousStepId;

	/**
	 * 
	 * @param source 事件
	 * @param processData 业务数据及审批信息
	 * @param previousStepId 已处理流程环节Id
	 */
	public ProcessAuditLogEvent(Object source, ProcessJsonData<T,PK> processData, Integer previousStepId) {
		super(source);
		this.processData = processData;
		this.previousStepId = previousStepId;
	}

	/**
	 * @return the processData
	 */
	public ProcessJsonData<T, PK> getProcessData() {
		return processData;
	}

	/**
	 * @param processData the processData to set
	 */
	public void setProcessData(ProcessJsonData<T, PK> processData) {
		this.processData = processData;
	}

	/**
	 * @return the previousStepId
	 */
	public Integer getPreviousStepId() {
		return previousStepId;
	}

	/**
	 * @param previousStepId the previousStepId to set
	 */
	public void setPreviousStepId(Integer previousStepId) {
		this.previousStepId = previousStepId;
	}

	
}
