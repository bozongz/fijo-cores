package com.fijo.cores.app.mapper;

import java.util.List;

import com.fijo.cores.mapper.IGenericMapper;
import org.apache.ibatis.annotations.Param;

import com.fijo.cores.app.model.ProcessAudit;
import com.fijo.cores.mapper.IGenericMapper;

public interface ProcessAuditMapper extends IGenericMapper<ProcessAudit,Integer> {
	
	List<ProcessAudit> getByStep(@Param("processStepId") Integer processStepId);
	
}