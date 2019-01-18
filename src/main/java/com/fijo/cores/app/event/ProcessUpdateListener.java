/**
 * 
 */
package com.fijo.cores.app.event;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.fijo.cores.app.model.ProcessAuditLog;
import com.fijo.cores.app.model.ProcessStatus;
import com.fijo.cores.app.service.IProcessStatusService;
import com.fijo.cores.service.IGenericService;

/**
 * @author zhangbo
 *
 */
@Component
public class ProcessUpdateListener implements ApplicationListener<ProcessUpdateEvent> {
	private static transient final Log log = LogFactory.getLog(ProcessUpdateListener.class);

	@Autowired
	private IProcessStatusService processStatusService;

	@Autowired
	@Qualifier("processAuditLogService")
	private IGenericService<ProcessAuditLog, Long> auditLogService;
	
	@Override
	public void onApplicationEvent(ProcessUpdateEvent event) {		
		Long previousReceiptId = event.getPreviousReceiptId(); //可能为null，因此不会导致日志和主单据信息被删除
		Long currentReceiptId = event.getCurrentReceiptId();	
		if(previousReceiptId != null && currentReceiptId != null){
			ProcessAuditLog auditLog = new ProcessAuditLog();
			auditLog.setTypeId(event.getProcessTypeId());
			auditLog.setHeaderId(event.getProcessHeaderId());		
			auditLog.setReceiptId(previousReceiptId);
			// 1.删除老单据所有历史审批日志
			auditLogService.delete(auditLog);	
			// 2.删除老单据历史主单据状态信息
			ProcessStatus status = new ProcessStatus();
			status.setProcessTypeId(event.getProcessTypeId());
			status.setProcessHeaderId(event.getProcessHeaderId());
			status.setReceiptId(previousReceiptId); 
			processStatusService.delete(status);
			
			Map<String, Object> params = Maps.newHashMap();
			params.put("previousReceiptId", previousReceiptId);
			params.put("processTypeId", event.getProcessTypeId());
			params.put("processHeaderId", event.getProcessHeaderId());
			params.put("currentReceiptId", currentReceiptId);
			// 3.将新单据的审批日志外键更换为原始单据外键
			int ret = auditLogService.update(params);
			log.debug(ret);
			// 4.将新单据的主单据状态信息外键更换为原始单据外键
			ret = processStatusService.update(params);
			log.debug(ret);
		}
	}
}
