/**
 * 
 */
package com.fijo.cores.app.service;

import java.util.Collection;
import java.util.List;

import com.fijo.cores.app.model.ProcessStep;
import com.fijo.cores.service.IGenericService;
import com.fijo.cores.app.model.ProcessStep;
import com.fijo.cores.service.IGenericService;

/**
 * 修改业务流程
 * 
 * @author zhangbo
 *
 */
public interface IUpdateProcessService extends IGenericService<ProcessStep, Integer> {
	int saveSteps(List<ProcessStep> stepList) throws IllegalArgumentException, IllegalAccessException;
	
	Collection<ProcessStep> loadStepsByHeader(String headerCode);
}
