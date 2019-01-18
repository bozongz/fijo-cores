/**
 * 
 */
package com.fijo.cores.messages;

import com.fijo.cores.app.model.ProcessModel;

/**
 * 流程待办消息通知接口
 * 
 * @author zhangbo
 *
 */
public interface MessageImplementor {
	/**
	 * 
	 * @param title 主题
	 * @param content 内容
	 * @param fromUserId 发送方
	 * @param toUserId 接收方
	 * @param process 业务流程
	 */
	void postMessage(String title, String content, Integer fromUserId, Integer toUserId, ProcessModel<?> process) throws Exception;
}
