/**
 * 
 */
package com.fijo.cores.app.event;

import com.fijo.cores.app.model.ProcessTask;
import com.fijo.cores.app.model.ProcessTaskCallbackLog;
import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.service.IGenericService;
import com.fijo.cores.utils.DateUtil;
import com.fijo.cores.utils.annotations.AsyncEventListener;
import com.fijo.cores.app.model.ProcessTask;
import com.fijo.cores.app.model.ProcessTaskCallbackLog;
import com.fijo.cores.app.model.ProcessTaskCallbackRetry;
import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.service.IGenericService;
import com.fijo.cores.utils.DateUtil;
import com.fijo.cores.utils.annotations.AsyncEventListener;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author zhangbo
 *
 */
@Component
@AsyncEventListener
public class ProcessTaskCreateCallbackListener implements ApplicationListener<ProcessTaskCreateCallbackEvent> {
	private static transient final Log log = LogFactory.getLog(ProcessTaskCreateCallbackListener.class);

    @Autowired
    @Qualifier("processTaskCallbackRetryService")
    private IGenericService<ProcessTaskCallbackRetry, Integer> processTaskCallbackRetryService;

    @Autowired
    @Qualifier("processTaskCallbackLogService")
    private IGenericService<ProcessTaskCallbackLog, Integer> processTaskCallbackLogService;

    @Override
	public void onApplicationEvent(ProcessTaskCreateCallbackEvent event) {
        Date callbackStartDate = DateUtil.getCurrent();
        Boolean callbackResult = true;
        String callbackError = null;
        ProcessTask processTask = event.getProcessTask();
        try{
            event.getCreateCallback().execute(processTask);
        }catch(Exception e){
            ProcessTaskCallbackRetry processTaskCallbackRetry = new ProcessTaskCallbackRetry();
            processTaskCallbackRetry.setProcessServiceClass(event.getProcessService());
            processTaskCallbackRetry.setExecuteTimes(1);
            processTaskCallbackRetry.setLastExecuteDate(DateUtil.getCurrent());
            processTaskCallbackRetry.setCallbackType("CreateCallback");
            processTaskCallbackRetry.setTaskId(processTask.getId());
            processTaskCallbackRetry.setTypeId(processTask.getTypeId());
            processTaskCallbackRetry.setHeaderId(processTask.getHeaderId());
            processTaskCallbackRetry.setReceiptId(processTask.getReceiptId());
            processTaskCallbackRetry.setStepId(processTask.getStepId());
            processTaskCallbackRetry.setCurrentUserId(processTask.getCurrentUserId());
            int result1 = processTaskCallbackRetryService.create(processTaskCallbackRetry);
            log.debug(result1);
            callbackResult = false;
            callbackError = StringUtils.substring(Exceptions.getStackTraceAsString(e), 0, 1999);
        }finally {
            ProcessTaskCallbackLog processTaskCallbackLog = new ProcessTaskCallbackLog();
            processTaskCallbackLog.setTaskId(processTask.getId());
            processTaskCallbackLog.setCallbackType("CreateCallback");
            processTaskCallbackLog.setCallbackStartDate(callbackStartDate);
            processTaskCallbackLog.setCallbackEndDate(DateUtil.getCurrent());
            processTaskCallbackLog.setCallbackDuration(processTaskCallbackLog.getCallbackEndDate().getTime() - callbackStartDate.getTime());
            processTaskCallbackLog.setCallbackResult(callbackResult);
            processTaskCallbackLog.setCallbackError(callbackError);
            int result2 = processTaskCallbackLogService.create(processTaskCallbackLog);
            log.debug(result2);
        }
	}
}
