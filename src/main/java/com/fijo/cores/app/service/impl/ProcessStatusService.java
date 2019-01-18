package com.fijo.cores.app.service.impl;

import java.util.List;

import com.fijo.cores.app.mapper.ProcessStatusMapper;
import com.fijo.cores.app.model.ProcessStatus;
import com.fijo.cores.app.service.IProcessStatusService;
import com.fijo.cores.service.impl.LogicService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fijo.cores.admin.authority.model.ShiroUser;
import com.fijo.cores.app.mapper.ProcessStatusMapper;
import com.fijo.cores.app.model.ProcessStatus;
import com.fijo.cores.app.service.IProcessStatusService;
import com.fijo.cores.service.impl.LogicService;

@Service(value = "processStatusService")
public class ProcessStatusService extends LogicService<ProcessStatus,Long> implements IProcessStatusService {
	private static transient final Log log = LogFactory.getLog(ProcessStatusService.class);	
	private ProcessStatusMapper mapper;
	
	public ProcessStatusService(SqlSession sqlSession,
			Class<ProcessStatus> persistentMapper) {
		super(sqlSession, persistentMapper);
	}
		
	@Autowired
	public ProcessStatusService(@Qualifier(value="sqlSessionTemplateSimple") SqlSession sqlSession) {
		super(sqlSession);
		this.mapper = sqlSession.getMapper(ProcessStatusMapper.class);
		super.setMapper(mapper);
	}

	@Override
	public ProcessStatus getOne(Integer processTypeId, Integer processHeaderId,
			Long receiptId) {
		return mapper.getOne(processTypeId, processHeaderId, receiptId);
	}

    @Override
    public Integer getJoinCount(Integer createUserId){
        return mapper.getJoinCount(createUserId);
    }

	@Override
	public List<ProcessStatus> getJoin() {
		Subject subject = SecurityUtils.getSubject();
		ShiroUser user = (ShiroUser) subject.getPrincipal();
		return mapper.getJoin(user.getUserId());
	}

	/**
	 * 创建时间、创建人、创建部门不进行处理，以各流程实体创建时间为主；
	 * 但更新时间、更新人、更新部门需要进行填值
	 */
	@Override
	protected void wrapCreateInfo(ProcessStatus o) {		
		wrapUpdateInfo(o);
	}

	@Override
	public boolean checkProcessRunning(Integer processTypeId,
			Integer processHeaderId, Integer processStepVersion) {
		int count = mapper.checkProcessRunning(processTypeId, processHeaderId, processStepVersion);
		return count>0 ? true:false;
	}

    @Override
    public int updateOnCompleted(ProcessStatus status) {
        return mapper.updateOnCompleted(status);
    }
}
