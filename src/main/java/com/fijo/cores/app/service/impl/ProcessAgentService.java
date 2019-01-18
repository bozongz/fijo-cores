package com.fijo.cores.app.service.impl;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fijo.cores.app.mapper.ProcessAgentMapper;
import com.fijo.cores.app.model.ProcessAgent;
import com.fijo.cores.app.service.IProcessAgentService;
import com.fijo.cores.service.impl.GenericMapperService;

@Service(value = "processAgentService")
public class ProcessAgentService extends GenericMapperService<ProcessAgent,Integer> implements IProcessAgentService{
	private static transient final Log log = LogFactory.getLog(ProcessAgentService.class);	
	private ProcessAgentMapper mapper;
	
	public ProcessAgentService(SqlSession sqlSession,
			Class<ProcessAgent> persistentMapper) {
		super(sqlSession, persistentMapper);
	}
		
	@Autowired
	public ProcessAgentService(@Qualifier(value="sqlSessionTemplateSimple") SqlSession sqlSession) {
		super(sqlSession);
		this.mapper = sqlSession.getMapper(ProcessAgentMapper.class);
		super.setMapper(mapper);
	}

	@Override
	public Collection<ProcessAgent> getExpiresAgent() {
		return mapper.getExpiresAgent();
	}
}
