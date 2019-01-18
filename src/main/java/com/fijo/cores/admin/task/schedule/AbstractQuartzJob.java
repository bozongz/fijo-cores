/**
 * 
 */
package com.fijo.cores.admin.task.schedule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author zhangbo
 *
 */
public abstract class AbstractQuartzJob implements QuartzJob {
	
	protected transient final Log log = LogFactory.getLog(getClass());
	
}
