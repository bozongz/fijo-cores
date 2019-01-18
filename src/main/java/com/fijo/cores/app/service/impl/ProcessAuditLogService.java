package com.fijo.cores.app.service.impl;

import com.fijo.cores.app.mapper.ProcessAuditLogMapper;
import com.fijo.cores.app.model.ProcessAuditLog;
import com.fijo.cores.app.service.IProcessAuditLogService;
import com.fijo.cores.service.impl.GenericMapperService;
import com.fijo.cores.app.mapper.ProcessAuditLogMapper;
import com.fijo.cores.app.model.ProcessAuditLog;
import com.fijo.cores.app.model.ProcessAuditLog;
import com.fijo.cores.app.service.IProcessAuditLogService;
import com.fijo.cores.app.service.IProcessAuditLogService;
import com.fijo.cores.service.impl.GenericMapperService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service(value = "processAuditLogService")
public class ProcessAuditLogService extends GenericMapperService<ProcessAuditLog, Long> implements IProcessAuditLogService {
	private static transient final Log log = LogFactory.getLog(ProcessAuditLogService.class);	
	private ProcessAuditLogMapper mapper;
	
	public ProcessAuditLogService(SqlSession sqlSession,
                                  Class<ProcessAuditLog> persistentMapper) {
		super(sqlSession, persistentMapper);
	}
		
	@Autowired
	public ProcessAuditLogService(@Qualifier(value="sqlSessionTemplateSimple") SqlSession sqlSession) {
		super(sqlSession);
		this.mapper = sqlSession.getMapper(ProcessAuditLogMapper.class);
		super.setMapper(mapper);
	}


    @Override
    public int deleteCreationLog(ProcessAuditLog o) {
        return mapper.deleteCreationLog(o);
    }

    @Override
    public int updateLogOpnion(ProcessAuditLog o) {
        return mapper.updateLogOpnion(o);
    }
}
