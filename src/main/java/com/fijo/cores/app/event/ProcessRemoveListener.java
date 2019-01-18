/**
 * 
 */
package com.fijo.cores.app.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.fijo.cores.app.model.ProcessAuditLog;
import com.fijo.cores.app.model.ProcessModel;
import com.fijo.cores.app.model.ProcessStatus;
import com.fijo.cores.app.model.ProcessTask;
import com.fijo.cores.app.service.IProcessStatusService;
import com.fijo.cores.service.IGenericService;

/**
 * @author zhangbo
 *
 */
@Component
public class ProcessRemoveListener implements ApplicationListener<ProcessRemoveEvent> {
	
	@Autowired
	@Qualifier("processTaskService")
	private IGenericService<ProcessTask, Long> taskService;

	@Autowired
	private IProcessStatusService processStatusService;

	@Autowired
	@Qualifier("processAuditLogService")
	private IGenericService<ProcessAuditLog, Long> auditLogService;
	
	@Override
	public void onApplicationEvent(ProcessRemoveEvent event) {
		ProcessModel<?> process = event.getProcess();
		ProcessTask processTask = new ProcessTask();
		processTask.setTypeId(process.getProcessTypeId());
		processTask.setHeaderId(process.getProcessHeaderId());		
		processTask.setReceiptId(process.getId());
		// 删除该流程所有代办
		taskService.delete(processTask);		
		ProcessAuditLog auditLog = new ProcessAuditLog();
		auditLog.setTypeId(process.getProcessTypeId());
		auditLog.setHeaderId(process.getProcessHeaderId());		
		auditLog.setReceiptId(process.getId());
		// 删除该流程所有审批日志
		auditLogService.delete(auditLog);	
		// 删除主单据信息
		ProcessStatus status = new ProcessStatus();
		status.setProcessTypeId(process.getProcessTypeId());
		status.setProcessHeaderId(process.getProcessHeaderId());
		status.setReceiptId(process.getId());
		processStatusService.delete(status);
		
	}
}
