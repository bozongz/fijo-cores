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
public class ProcessTaskRemoveCallbackEvent extends ApplicationEvent {

    private static final long serialVersionUID = 2638402376063654042L;
    private ProcessTaskRemoveCallback removeCallback;

    private ProcessTask processTask;


	public ProcessTaskRemoveCallbackEvent(Object source, ProcessTaskRemoveCallback removeCallback, ProcessTask processTask) {
		super(source);
		this.removeCallback = removeCallback;
        this.processTask = processTask;
	}

    public ProcessTaskRemoveCallback getRemoveCallback() {
        return removeCallback;
    }

    public void setRemoveCallback(ProcessTaskRemoveCallback removeCallback) {
        this.removeCallback = removeCallback;
    }

    public ProcessTask getProcessTask() {
        return processTask;
    }

    public void setProcessTask(ProcessTask processTask) {
        this.processTask = processTask;
    }
}
