package com.fijo.cores.app.service;
import java.util.List;

import com.fijo.cores.app.model.ProcessStep;
import com.fijo.cores.service.IGenericAdvanceService;
import com.fijo.cores.app.model.ProcessHeader;
import com.fijo.cores.app.model.ProcessStep;
import com.fijo.cores.service.IGenericAdvanceService;

public interface IProcessHeaderAdvanceService extends IGenericAdvanceService<ProcessHeader,Integer> {
	
	/**
	 * 获取流程运行版本所有环节
	 */
	List<ProcessStep> getSteps(Integer headerId);

}
