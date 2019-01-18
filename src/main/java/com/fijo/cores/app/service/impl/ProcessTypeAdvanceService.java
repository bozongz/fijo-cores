package com.fijo.cores.app.service.impl;

import com.fijo.cores.app.service.IProcessTypeAdvanceService;
import com.fijo.cores.service.IGenericService;
import com.fijo.cores.service.impl.GenericAdvanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fijo.cores.app.model.ProcessType;
import com.fijo.cores.app.service.IProcessTypeAdvanceService;
import com.fijo.cores.cache.IGenericCache;
import com.fijo.cores.service.IGenericService;
import com.fijo.cores.service.impl.GenericAdvanceService;


@Service(value="processTypeAdvanceService")
public class ProcessTypeAdvanceService extends GenericAdvanceService<ProcessType,Integer> implements IProcessTypeAdvanceService {
	
	@Autowired
	public ProcessTypeAdvanceService(
			@Qualifier(value="processTypeService")IGenericService<ProcessType, Integer> processTypeService,
			@Qualifier(value="processTypeCache")IGenericCache<ProcessType,Integer> processTypeCache) {
		super(processTypeService, processTypeCache);
	}
	
}
