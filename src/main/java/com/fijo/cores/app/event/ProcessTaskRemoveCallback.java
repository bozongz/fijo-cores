/**
 * 
 */
package com.fijo.cores.app.event;

import com.fijo.cores.app.model.ProcessTask;
import com.fijo.cores.app.model.ProcessTask;

/**
 * 流程待办删除时回调
 * 
 * @author zhangbo
 *
 */
public interface ProcessTaskRemoveCallback {
	
	void execute(ProcessTask processTask);
	
}
