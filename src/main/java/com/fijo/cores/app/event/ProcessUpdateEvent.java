/**
 * 
 */
package com.fijo.cores.app.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author zhangbo
 *
 */
public class ProcessUpdateEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3011506598014311050L;
	private Integer processTypeId;
	private Integer processHeaderId;
	private Long currentReceiptId;
	private Long previousReceiptId;

	/**
	 * 
	 * @param source 事件
	 * @param processTypeId 业务类型
	 * @param processHeaderId 业务流程头
	 * @param currentReceiptId 当前新的单据Id
	 * @param previousReceiptId 历史单据Id
	 */
	public ProcessUpdateEvent(Object source,Integer processTypeId, Integer processHeaderId, Long currentReceiptId, Long previousReceiptId) {
		super(source);
		this.processTypeId = processTypeId;
		this.processHeaderId = processHeaderId;
		this.currentReceiptId = currentReceiptId;
		this.previousReceiptId = previousReceiptId;
	}

	public Integer getProcessTypeId() {
		return processTypeId;
	}

	public void setProcessTypeId(Integer processTypeId) {
		this.processTypeId = processTypeId;
	}

	public Integer getProcessHeaderId() {
		return processHeaderId;
	}

	public void setProcessHeaderId(Integer processHeaderId) {
		this.processHeaderId = processHeaderId;
	}

	public Long getCurrentReceiptId() {
		return currentReceiptId;
	}

	public void setCurrentReceiptId(Long currentReceiptId) {
		this.currentReceiptId = currentReceiptId;
	}

	public Long getPreviousReceiptId() {
		return previousReceiptId;
	}

	public void setPreviousReceiptId(Long previousReceiptId) {
		this.previousReceiptId = previousReceiptId;
	}
	
	
}
