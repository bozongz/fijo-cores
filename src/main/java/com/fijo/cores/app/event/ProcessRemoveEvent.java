/**
 * 
 */
package com.fijo.cores.app.event;

import org.springframework.context.ApplicationEvent;

import com.fijo.cores.app.model.ProcessModel;

/**
 * @author zhangbo
 *
 */
public class ProcessRemoveEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8815144213977107102L;
	private ProcessModel<?> process;

	/**
	 * 
	 * @param source 事件
	 * @param process 业务流程
	 */
	public ProcessRemoveEvent(Object source, ProcessModel<?> process) {
		super(source);
		this.process = process;
	}

	/**
	 * @return the process
	 */
	public ProcessModel<?> getProcess() {
		return process;
	}
	
}
