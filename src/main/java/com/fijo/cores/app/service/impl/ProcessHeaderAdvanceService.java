package com.fijo.cores.app.service.impl;

import java.util.List;

import com.fijo.cores.app.model.ProcessHeader;
import com.fijo.cores.app.model.ProcessStep;
import com.fijo.cores.app.service.IProcessHeaderAdvanceService;
import com.fijo.cores.exceptions.UnExpectedStepException;
import com.fijo.cores.service.IGenericService;
import com.fijo.cores.service.impl.GenericAdvanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fijo.cores.app.model.ProcessHeader;
import com.fijo.cores.app.model.ProcessStep;
import com.fijo.cores.app.service.IProcessHeaderAdvanceService;
import com.fijo.cores.cache.IGenericCache;
import com.fijo.cores.exceptions.UnExpectedStepException;
import com.fijo.cores.service.IGenericService;
import com.fijo.cores.service.impl.GenericAdvanceService;


@Service(value="processHeaderAdvanceService")
public class ProcessHeaderAdvanceService extends GenericAdvanceService<ProcessHeader,Integer> implements IProcessHeaderAdvanceService {
	
	@Autowired
	public ProcessHeaderAdvanceService(
			@Qualifier(value="processHeaderService")IGenericService<ProcessHeader, Integer> processHeaderService,
			@Qualifier(value="processHeaderCache")IGenericCache<ProcessHeader,Integer> processHeaderCache) {
		super(processHeaderService, processHeaderCache);
	}

	@Override
	public List<ProcessStep> getSteps(Integer headerId) {
		ProcessHeader header = getKeyHashOps().get(headerId);
		if(header == null){
			log.error(String.format("10005 Not found processHeader: %s", headerId ));
			throw new UnExpectedStepException("10005", String.format("10005 Not found processHeader: %s", headerId ));
		}
		return header.getSteps();
	}	
	
}
