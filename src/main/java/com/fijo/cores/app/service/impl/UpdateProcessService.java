package com.fijo.cores.app.service.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fijo.cores.app.mapper.*;
import com.fijo.cores.app.model.ProcessHeader;
import com.fijo.cores.app.model.ProcessStep;
import com.fijo.cores.app.model.ProcessStepCondition;
import com.fijo.cores.app.service.IProcessAuditAdvanceService;
import com.fijo.cores.app.service.IProcessHeaderAdvanceService;
import com.fijo.cores.app.service.IProcessStepAdvanceService;
import com.fijo.cores.app.service.IUpdateProcessService;
import com.fijo.cores.exceptions.UpdateProcessFailedException;
import com.fijo.cores.service.impl.GenericMapperService;
import com.fijo.cores.utils.Constants;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.fijo.cores.app.mapper.ProcessAuditMapper;
import com.fijo.cores.app.mapper.ProcessHeaderMapper;
import com.fijo.cores.app.mapper.ProcessStepConditionMapper;
import com.fijo.cores.app.mapper.ProcessStepConfigurationMapper;
import com.fijo.cores.app.mapper.ProcessStepMapper;
import com.fijo.cores.app.model.ProcessAudit;
import com.fijo.cores.app.model.ProcessHeader;
import com.fijo.cores.app.model.ProcessStep;
import com.fijo.cores.app.model.ProcessStepCondition;
import com.fijo.cores.app.model.ProcessStepConfiguration;
import com.fijo.cores.app.service.IProcessAuditAdvanceService;
import com.fijo.cores.app.service.IProcessHeaderAdvanceService;
import com.fijo.cores.app.service.IProcessStepAdvanceService;
import com.fijo.cores.app.service.IUpdateProcessService;
import com.fijo.cores.exceptions.UpdateProcessFailedException;
import com.fijo.cores.service.impl.GenericMapperService;
import com.fijo.cores.utils.Constants;
import com.fijo.cores.utils.enums.ProcessEnum;

/**
 * 前端可以提交多个以空格分隔提交人submitUserIds或者提交组织部门submitOrgIds，但后端是单独保存保存提交人submitUserId或者提交组织部门submitOrgId
 * 
 * @author Li
 *
 */
@Service(value = "updateProcessService")
public class UpdateProcessService extends GenericMapperService<ProcessStep,Integer> implements IUpdateProcessService {

	private ProcessHeaderMapper headerMapper;
	private ProcessStepMapper mapper;
	private ProcessAuditMapper auditMapper;
	private ProcessStepConfigurationMapper configurationMapper;
	private ProcessStepConditionMapper conditionMapper;

	@Autowired
	private IProcessHeaderAdvanceService processHeaderAdvanceService;

	@Autowired
	private IProcessStepAdvanceService processStepCache;
	
	@Autowired
	private IProcessAuditAdvanceService processAuditCache;
	
	@Autowired
	public UpdateProcessService(@Qualifier(value="sqlSessionTemplateSimple") SqlSession sqlSession) {
		super(sqlSession);
		this.headerMapper = sqlSession.getMapper(ProcessHeaderMapper.class);
		this.mapper = sqlSession.getMapper(ProcessStepMapper.class);
		this.auditMapper = sqlSession.getMapper(ProcessAuditMapper.class);
		this.configurationMapper = sqlSession.getMapper(ProcessStepConfigurationMapper.class);
		this.conditionMapper = sqlSession.getMapper(ProcessStepConditionMapper.class);
		super.setMapper(mapper);
	}
	
	/**
	 * =======================新增、变更维护================================================================================
	 */
	@Override
	public int saveSteps(List<ProcessStep> stepList) throws IllegalArgumentException, IllegalAccessException{
		if(stepList != null && stepList.size() > 0){
			ProcessHeader processHeader = stepList.get(0).getHeader();
			if(!processHeader.getEnabled()){
				return 0;
			}else{
				int ret = 0;
				Integer version = processHeader.getHversion()+1;
				//第1步，更新业务流程头的版本号
				processHeader.setHversion(version);
				ret = headerMapper.update(processHeader);
				if(ret <= 0)
					throw new UpdateProcessFailedException("10012", String.format("10012, Update Process failed!"));
				//第2步，循环逐个写入新的流程环节
				for(ProcessStep newStep : stepList){
					newStep.setSversion(version);
					ret = mapper.create(newStep); //写入环节后，生成新的主键
					if(ret==1 && newStep.getStepId()>0){
						//第3步，批量写入该流程环节的审批配置
						batchCreateAudit(newStep, version);
						//第4步，批量写入该流程环节的特殊审批配置
						batchCreateConfiguration(newStep, version);
					}else{
						throw new UpdateProcessFailedException("10012", String.format("10012, Update Process failed!"));
					}
				}
				//第3步，循环写入后，重新加载缓存
				processStepCache.removeAll();
				processStepCache.initLoad();
				processAuditCache.removeAll();
				processAuditCache.initLoad();
				//第4步，更新业务流程头的可用性
				processHeader.setEnabled(true);
				ret = headerMapper.update(processHeader);
				//第5步，重置业务流程头的缓存数据
				if(ret > 0){
					processHeaderAdvanceService.removeAll();
					processHeaderAdvanceService.initLoad();
				}else{
					throw new UpdateProcessFailedException("10012", String.format("10012, Update Process failed!"));
				}
				return ret;
			}			
		}else{
			return 0;
		}
	}
	
	private void batchCreateAudit(ProcessStep step, Integer version){
		List<ProcessAudit> audits = makeManytoOne(step, version);
		if(audits!=null && audits.size()>=0){
			int ret = auditMapper.batchCreate(step.getAudits());
			if(ret <= 0)
				throw new UpdateProcessFailedException("10012", String.format("10012, Update Process failed!"));
		}
	}
	
	private void batchCreateConfiguration(ProcessStep step, Integer version){
		List<ProcessStepConfiguration> configurations = step.getConfigurations();
		if(configurations!=null && configurations.size()>0){
			for(ProcessStepConfiguration configuration : configurations){
				configuration.setCversion(version);
				int ret = configurationMapper.create(configuration);
				if(ret > 0 && configuration.getConfigurationId() > 0){
					List<ProcessStepCondition> conditions = configuration.getConditions();
					if(conditions!=null && conditions.size()>0){
						ret = conditionMapper.batchCreate(conditions);
						if(ret <= 0)
							throw new UpdateProcessFailedException("10012", String.format("10012, Update Process failed!"));
					}
				}else{
					throw new UpdateProcessFailedException("10012", String.format("10012, Update Process failed!"));
				}				
			}
			
		}
	}
	
	/**
	 * 将提交时空格分隔的submitOrgIds和submitUserIds查分为单个submitOrgId和submitUserId进行逐条保存
	 * @param step
	 * @param version
	 * @return
	 */
	private List<ProcessAudit> makeManytoOne(ProcessStep step, Integer version){
		List<ProcessAudit> oneAuditList = Lists.newArrayList();
		//获取前端提交的审批配置，分解前端提交的提交人、提交部门后，写入数据库
		for(ProcessAudit audit :step.getAudits()){
			//有提交人，无提交部门
			if(!StringUtils.isEmpty(audit.getSubmitUserIds()) && StringUtils.isEmpty(audit.getSubmitOrgIds())){
				String[] userIds = audit.getSubmitUserIds().split(Constants.SPACE);
				for(String userId:userIds){
					setToOneAudit(audit, new ProcessAudit(), userId, null, version, step, oneAuditList);
				}
			}
			//有部门，无提交人
			if(!StringUtils.isEmpty(audit.getSubmitOrgIds()) && StringUtils.isEmpty(audit.getSubmitUserIds())){
				String[] orgIds = audit.getSubmitOrgIds().split(Constants.SPACE);
				for(String orgId:orgIds){
					setToOneAudit(audit, new ProcessAudit(), null, orgId, version, step, oneAuditList);
				}
			}
			//无部门、无提交人
			if(StringUtils.isEmpty(audit.getSubmitOrgIds()) && StringUtils.isEmpty(audit.getSubmitUserIds())){
				setToOneAudit(audit, new ProcessAudit(), null, null, version, step, oneAuditList);
			}
			//有部门、有提交人
			if(!StringUtils.isEmpty(audit.getSubmitOrgIds()) && !StringUtils.isEmpty(audit.getSubmitUserIds())){
				String[] userIds = audit.getSubmitUserIds().split(Constants.SPACE);
				for(String userId:userIds){ //先保存用户
					setToOneAudit(audit, new ProcessAudit(), userId, null, version, step, oneAuditList);
				}
				String[] orgIds = audit.getSubmitOrgIds().split(Constants.SPACE);
				for(String orgId:orgIds){ //再保存部门
					setToOneAudit(audit, new ProcessAudit(), null, orgId, version, step, oneAuditList);
				}
			}
		}
		//重置环节的审批配置，以便写入数据库
		step.setAudits(null);
		step.setAudits(oneAuditList);
		return oneAuditList;
	}
	
	private void setToOneAudit(ProcessAudit src, ProcessAudit desc, String userId, String orgId, Integer version, ProcessStep step, List<ProcessAudit> oneAuditList){
		desc.setSubmitUserId(userId);
		desc.setSubmitOrgId(orgId);
		desc.setSubjectType(src.getSubjectType());
		desc.setSubjects(src.getSubjects());
		desc.setAversion(version);
		desc.setProcessStep(step);
		oneAuditList.add(desc);
	}
	
	/**
	 * =======================查询================================================================================
	 */
	@Override
	public Collection<ProcessStep> loadStepsByHeader(String headerCode){
		ProcessHeader processHeader = processHeaderAdvanceService.loadByUnique(headerCode);
		ProcessStep o = new ProcessStep();
		o.setHeader(processHeader);
		o.setSversion(processHeader.getHversion());
		Collection<ProcessStep> stepList = mapper.getAll(o);
		for(ProcessStep step:stepList){
			for(ProcessAudit audit :step.getAudits()){
				audit.setSubmitUserIds(audit.getSubmitUserId());
				audit.setSubmitOrgIds(audit.getSubmitOrgId());
			}
		}
//		makeOneToMany(stepList);
		return stepList;
	}
	
	/**
	 * 以ProcessAudit审批类型SubjectType和审批对象Subjects作为Key值，将多个submitUserId和submitOrgId组合在一起
	 * 注：理论上
	 * @param stepList
	 */
	/*private void makeOneToMany(Collection<ProcessStep> stepList){
		for(ProcessStep step:stepList){
			//1.审批类型与审批对象相同的配置归为一类<subjectType-subjects, List<ProcessAudit>>
			Map<String,List<ProcessAudit>> typeAndSubjectsMap = Maps.newHashMap();			
			for(ProcessAudit audit :step.getAudits()){
				if(typeAndSubjectsMap.containsKey(audit.getSubjectType()+Constants.LINE+audit.getSubjects())){
					typeAndSubjectsMap.get(audit.getSubjectType()+Constants.LINE+audit.getSubjects()).add(audit);
				}else{
					List<ProcessAudit> auditList = Lists.newArrayList();
					auditList.add(audit);
					typeAndSubjectsMap.put(audit.getSubjectType()+Constants.LINE+audit.getSubjects(), auditList);
				}			
			}
			//2.将审批类型与审批对象相同的配置由一个对象合并为空格分隔的对象
			List<ProcessAudit> manyAuditList = Lists.newArrayList();
			Iterator<Entry<String, List<ProcessAudit>>> iter = typeAndSubjectsMap.entrySet().iterator();
			while(iter.hasNext()){
				StringBuffer userIds = new StringBuffer();
				StringBuffer orgIds = new StringBuffer();
				Entry<String, List<ProcessAudit>> entry = iter.next();
				for(ProcessAudit audit: entry.getValue()){//获取共同的审批对象类型和审批对象下的所有提交人/提交部门
					if(audit.getSubmitUserId()	!= null)
						userIds.append(audit.getSubmitUserId()+Constants.SPACE);
					if(audit.getSubmitOrgId()	!= null)
						orgIds.append(audit.getSubmitOrgId()+Constants.SPACE);
				}
				ProcessAudit manyAudit = new ProcessAudit();
				manyAudit.setSubmitUserIds(userIds.toString());
				manyAudit.setSubmitOrgIds(orgIds.toString());
				String[] typeAndSubjects =entry.getKey().split(Constants.LINE); //共同的审批对象类型和审批对象
				manyAudit.setSubjectType(Enum.valueOf(ProcessEnum.class, typeAndSubjects[0]));
				manyAudit.setSubjects(typeAndSubjects[1]);
				manyAudit.setProcessStep(step);
				manyAuditList.add(manyAudit);
			}
			//重置环节的审批配置，以便返回前端
			step.setAudits(null);
			step.setAudits(manyAuditList);
		}
	}*/
}
