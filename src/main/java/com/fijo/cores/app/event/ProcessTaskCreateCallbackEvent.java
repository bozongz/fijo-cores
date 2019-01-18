/**
 * 
 */
package com.fijo.cores.app.event;

import com.fijo.cores.app.model.ProcessTask;
import com.fijo.cores.app.model.ProcessTask;
import org.springframework.context.ApplicationEvent;

/**
 * @author zhangbo
 *
 */
public class ProcessTaskCreateCallbackEvent extends ApplicationEvent {


    private static final long serialVersionUID = 5604270358795528321L;

    private ProcessTaskCreateCallback createCallback;

    private ProcessTask processTask;

    private String processService;

	public ProcessTaskCreateCallbackEvent(Object source, String processService, ProcessTaskCreateCallback createCallback, ProcessTask processTask) {
		super(source);
        this.processService = processService;
		this.createCallback = createCallback;
        this.processTask = processTask;
	}

    public ProcessTaskCreateCallback getCreateCallback() {
        return createCallback;
    }

    public void setCreateCallback(ProcessTaskCreateCallback createCallback) {
        this.createCallback = createCallback;
    }

    public ProcessTask getProcessTask() {
        return processTask;
    }

    public void setProcessTask(ProcessTask processTask) {
        this.processTask = processTask;
    }

    public String getProcessService() {
        return processService;
    }

    public void setProcessService(String processService) {
        this.processService = processService;
    }
}
