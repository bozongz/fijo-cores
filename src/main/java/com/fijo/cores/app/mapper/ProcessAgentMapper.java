package com.fijo.cores.app.mapper;

import java.util.Collection;

import com.fijo.cores.app.model.ProcessAgent;
import com.fijo.cores.mapper.IGenericMapper;
import com.fijo.cores.app.model.ProcessAgent;
import com.fijo.cores.mapper.IGenericMapper;

public interface ProcessAgentMapper extends IGenericMapper<ProcessAgent,Integer> {
	
	Collection<ProcessAgent> getExpiresAgent();
	
}