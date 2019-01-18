/**
 * 
 */
package com.fijo.cores.app.event;

import com.fijo.cores.app.model.ProcessTask;
import com.fijo.cores.app.model.ProcessTask;

/**
 * 流程待办创建时回调
 * 
 * @author zhangbo
 *
 */
public interface ProcessTaskCreateCallback {
	
	void execute(ProcessTask processTask);
	
}
