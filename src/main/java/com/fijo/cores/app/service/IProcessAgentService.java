package com.fijo.cores.app.service;

import java.util.Collection;

import com.fijo.cores.app.model.ProcessAgent;
import com.fijo.cores.service.IGenericService;
import com.fijo.cores.app.model.ProcessAgent;
import com.fijo.cores.service.IGenericService;

public interface IProcessAgentService extends IGenericService<ProcessAgent, Integer> {

	Collection<ProcessAgent> getExpiresAgent();
	
}
