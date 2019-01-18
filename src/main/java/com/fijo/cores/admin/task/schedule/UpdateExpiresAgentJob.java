package com.fijo.cores.admin.task.schedule;

import java.util.Collection;

import com.fijo.cores.app.model.ProcessAgent;
import com.fijo.cores.app.service.IProcessAgentService;
import com.fijo.cores.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fijo.cores.app.model.ProcessAgent;
import com.fijo.cores.app.service.IProcessAgentService;
import com.fijo.cores.utils.DateUtil;


@Component
public class UpdateExpiresAgentJob extends AbstractQuartzJob{
	
	@Autowired
	private IProcessAgentService processAgentService;
	
	/**
	 * 定期检查待办代理，发现过期时间expires不为空的，检查其有效性
	 */
	@Override
	public void execute() {
		Collection<ProcessAgent> list = processAgentService.getExpiresAgent();
		for(ProcessAgent o:list){
			log.debug(DateUtil.addDays(o.getBeginDate(), o.getExpires()));
			log.debug(DateUtil.daysBetweenDates(DateUtil.getCurrent(), DateUtil.addDays(o.getBeginDate(), o.getExpires())));
			if(DateUtil.daysBetweenDates(DateUtil.getCurrent(), DateUtil.addDays(o.getBeginDate(), o.getExpires())) > 0){
				o.setValid(false);
				processAgentService.update(o);				
			}
		}
	}


	
}
