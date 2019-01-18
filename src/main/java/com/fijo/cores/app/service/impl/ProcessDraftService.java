package com.fijo.cores.app.service.impl;

import com.fijo.cores.app.mapper.ProcessDraftMapper;
import com.fijo.cores.app.model.ProcessDraft;
import com.fijo.cores.app.service.IProcessDraftService;
import com.fijo.cores.service.impl.LogicService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fijo.cores.app.mapper.ProcessDraftMapper;
import com.fijo.cores.app.model.ProcessDraft;
import com.fijo.cores.app.service.IProcessDraftService;
import com.fijo.cores.service.impl.LogicService;

@Service(value = "processDraftService")
public class ProcessDraftService extends LogicService<ProcessDraft,Long> implements IProcessDraftService {
	private static transient final Log log = LogFactory.getLog(ProcessDraftService.class);	
	private ProcessDraftMapper mapper;
	
	public ProcessDraftService(SqlSession sqlSession,
			Class<ProcessDraft> persistentMapper) {
		super(sqlSession, persistentMapper);
	}
		
	@Autowired
	public ProcessDraftService(@Qualifier(value="sqlSessionTemplateSimple") SqlSession sqlSession) {
		super(sqlSession);
		this.mapper = sqlSession.getMapper(ProcessDraftMapper.class);
		super.setMapper(mapper);
	}

	/**
	 * 不做任何处理，创建时间以各流程实体创建时间为主
	 */
	@Override
	protected void wrapCreateInfo(ProcessDraft o) {		
	}

	@Override
	public ProcessDraft getOne(Integer processTypeId, Integer processHeaderId,
			Long receiptId) {
		return mapper.getOne(processTypeId, processHeaderId, receiptId);
	}
}
