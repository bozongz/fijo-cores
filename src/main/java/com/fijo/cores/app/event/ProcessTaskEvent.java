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
public class ProcessTaskEvent<T extends ProcessModel<?>, PK extends Serializable> extends ApplicationEvent {

	public enum NoticMethod {  
		  Weixin, SMS, Email, OA, Null
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1874951213702565908L;	
	
	private ProcessJsonData<T,PK> processData;
	
	private NoticMethod noticeMethod;
	
	private Integer previousStepId;
	
	private ProcessTaskCreateCallback createCallback;
	
	private ProcessTaskRemoveCallback removeCallback;
	
	public ProcessTaskEvent(Object source, ProcessJsonData<T, PK> processData,
			NoticMethod noticeMethod,Integer previousStepId) {
		super(source);
		this.processData = processData;
		this.noticeMethod = noticeMethod;
		this.previousStepId = previousStepId;
	}

	public ProcessTaskEvent(Object source, ProcessJsonData<T, PK> processData,
			NoticMethod noticeMethod,Integer previousStepId, ProcessTaskCreateCallback createCallback) {
		this(source, processData, noticeMethod,previousStepId);
		this.createCallback = createCallback;
	}
	
	public ProcessTaskEvent(Object source, ProcessJsonData<T, PK> processData,
			NoticMethod noticeMethod,Integer previousStepId, ProcessTaskRemoveCallback removeCallback) {
		this(source, processData, noticeMethod,previousStepId);
		this.removeCallback = removeCallback;
	}
	
	public ProcessTaskEvent(Object source, ProcessJsonData<T, PK> processData,
			NoticMethod noticeMethod,Integer previousStepId, ProcessTaskCreateCallback createCallback,
			ProcessTaskRemoveCallback removeCallback) {
		this(source, processData, noticeMethod, previousStepId);
		this.createCallback = createCallback;
		this.removeCallback = removeCallback;
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
	 * @return the noticeMethod
	 */
	public NoticMethod getNoticeMethod() {
		return noticeMethod;
	}

	/**
	 * @param noticeMethod the noticeMethod to set
	 */
	public void setNoticeMethod(NoticMethod noticeMethod) {
		this.noticeMethod = noticeMethod;
	}

	/**
	 * @return the createCallback
	 */
	public ProcessTaskCreateCallback getCreateCallback() {
		return createCallback;
	}

	/**
	 * @param createCallback the createCallback to set
	 */
	public void setCreateCallback(ProcessTaskCreateCallback createCallback) {
		this.createCallback = createCallback;
	}

	/**
	 * @return the removeCallback
	 */
	public ProcessTaskRemoveCallback getRemoveCallback() {
		return removeCallback;
	}

	/**
	 * @param removeCallback the removeCallback to set
	 */
	public void setRemoveCallback(ProcessTaskRemoveCallback removeCallback) {
		this.removeCallback = removeCallback;
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
