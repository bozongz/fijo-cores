package com.fijo.cores.app.service.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fijo.cores.app.model.ProcessModel;
import com.fijo.cores.app.model.ProcessStep;
import com.fijo.cores.app.service.IProcessAuditAdvanceService;
import com.fijo.cores.app.service.IProcessStepAdvanceService;
import com.fijo.cores.app.service.IProcessTaskService;
import com.fijo.cores.exceptions.UnExpectedAuditException;
import com.fijo.cores.service.IGenericService;
import com.fijo.cores.service.impl.GenericAdvanceService;
import com.fijo.cores.shiro.AppUserSession;
import com.fijo.cores.utils.Constants;
import com.fijo.cores.utils.enums.ProcessEnum;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.fijo.cores.admin.authority.model.ShiroUser;
import com.fijo.cores.admin.authority.model.SysRole;
import com.fijo.cores.admin.authority.model.SysUser;
import com.fijo.cores.admin.authority.service.ISysRoleAdvanceService;
import com.fijo.cores.admin.authority.service.ISysUserAdvanceService;
import com.fijo.cores.app.model.ProcessAudit;
import com.fijo.cores.app.model.ProcessModel;
import com.fijo.cores.app.model.ProcessStep;
import com.fijo.cores.app.model.ProcessTask;
import com.fijo.cores.app.service.IProcessAuditAdvanceService;
import com.fijo.cores.app.service.IProcessStepAdvanceService;
import com.fijo.cores.app.service.IProcessTaskService;
import com.fijo.cores.cache.IGenericCache;
import com.fijo.cores.exceptions.UnExpectedAuditException;
import com.fijo.cores.service.IGenericService;
import com.fijo.cores.service.impl.GenericAdvanceService;
import com.fijo.cores.shiro.AppUserSession;
import com.fijo.cores.utils.Constants;
import com.fijo.cores.utils.enums.ProcessEnum;


@Service(value="processAuditAdvanceService")
public class ProcessAuditAdvanceService extends GenericAdvanceService<ProcessAudit,Integer> implements IProcessAuditAdvanceService {
	
	@Autowired
	private IProcessStepAdvanceService processStepAdvanceService;
	
	@Autowired
	private AppUserSession appUserSession;
	
	@Autowired
	private ISysUserAdvanceService sysUserAdvanceService;
	
	@Autowired
	private ISysRoleAdvanceService sysRoleAdvanceService;
	
	@Autowired	
	private IProcessTaskService processTaskService;
	
	//<redis的key, stepCode, <userId-orgId, auditId>>
	private BoundHashOperations<String, String, Map<String, Integer>> userOrgHashOps;
	
	@Autowired
	public ProcessAuditAdvanceService(
			@Qualifier(value="processAuditService")IGenericService<ProcessAudit, Integer> processAuditService,
			@Qualifier(value="processAuditCache")IGenericCache<ProcessAudit,Integer> processAuditCache) {
		super(processAuditService, processAuditCache);
		this.userOrgHashOps = getRedisTemplate().boundHashOps(getCoreConfig().getCtx()+Constants.COLON+ProcessAudit.class.getSimpleName()+Constants.COLON+"userId-orgId");
		Collection<ProcessAudit> itemList = getValues(); // 自定义缓存对象
		for (ProcessAudit o : itemList) {
			Map<String, Integer> userOrgAudits = userOrgHashOps.get(o.getProcessStep().getStepCode());
			if(userOrgAudits == null){
				userOrgAudits = Maps.newHashMap();
			}
			log.debug("Config:"+o.getSubmitUserId()+Constants.LINE+o.getSubmitOrgId()+" with:"+o);
			userOrgAudits.put(o.getSubmitUserId()+Constants.LINE+o.getSubmitOrgId(), o.getAuditId());
			userOrgHashOps.put(o.getProcessStep().getStepCode(), userOrgAudits);
		}
	}

	/**
	 * 根据环节Id,流程数据获取审批信息
	 * @param stepId 环节Id
	 * @param process 业务流程数据
	 * @return 返回审批节点
	 */
	@Override
	public ProcessAudit getAudit(Integer stepId, ProcessModel<?> process) {
		return getProcessAudit(processStepAdvanceService.loadByKey(stepId).getStepCode(), process);
	}

	/**
	 * 根据环节编码,流程数据获取审批信息
	 * @param stepCode 环节编码
	 * @param process 业务流程数据
	 * @return 返回审批节点
	 */
	@Override
	public ProcessAudit getProcessAudit(String stepCode, ProcessModel<?> process) {
		ProcessStep step = processStepAdvanceService.loadByUnique(stepCode);
		if(step.getStepType().equals(ProcessEnum.special)){
			ProcessAudit audit = processStepAdvanceService.getConfigurationAudit(step, process);
			if(null == audit){
				log.error(String.format("10002 Not such audit configuration of process step %s: ", stepCode));
				throw new UnExpectedAuditException("10002", String.format("10002 Not such audit configuration of process step %s: ", stepCode));
			}else{
				return audit;
			}
		}else{
			Map<String, Integer> audits = userOrgHashOps.get(stepCode);
			if (step == null || audits == null || audits.size() == 0){ //传入错误的审批环节Id情况
				log.error(String.format("10002 Not such audit configuration of process step id %s: ", stepCode));
				throw new UnExpectedAuditException("10002", String.format("10002 Not such audit configuration of process step id %s: ", stepCode));
			}else{
				ShiroUser user = appUserSession.getCurrentUser();			
				log.debug(user.getUserId()+Constants.LINE+user.getOrgId());
				log.debug(audits);
				//特别重要Important:
				//1.用户和组织同时匹配优先，但一般不会出现该配置，因为一旦定位用户则无须再关注部门
				//2.前端如果传参既包括用户，又包括部门，那么需要分别写入用户和部门的审批记录，保证用户和部门不会同时不为空
				if(audits.containsKey(user.getUserId()+Constants.LINE+user.getOrgId())){ 
					Integer auditId = audits.get(user.getUserId()+Constants.LINE+user.getOrgId());
					return loadByKey(auditId);
				}else if(audits.containsKey(user.getUserId()+"-null")){  //用户匹配优先
					Integer auditId = audits.get(user.getUserId()+"-null");
					return loadByKey(auditId);
				}else if(audits.containsKey("null-"+user.getOrgId())){   //组织匹配优先
					Integer auditId = audits.get("null-"+user.getOrgId());
					return loadByKey(auditId);
				}else if(audits.containsKey("null-null")){   //默认配置
					Integer auditId = audits.get("null-null");
					return loadByKey(auditId);
				}else{
					log.error(String.format("10002 Not such audit configuration of process step %s: ", stepCode));
					throw new UnExpectedAuditException("10002", String.format("10002 Not such audit configuration of process step %s: ", stepCode));
				}
			}
		}
	}

	/**
	 * 根据环节编码，获取该环节的可选审批人或审批角色
	 * @param stepCode 环节编码
	 * @return 返回审批人或审批角色
	 */
	@Override
	public List<Integer> getAuditors(String stepCode) {
		ProcessAudit audit = getProcessAudit(stepCode, null);
		return getAuditors(audit.getSubjectType(), audit.getSubjects());
	}

	/**
	 * 用于获取可选审批人或审批角色, 在ProcessTaskListener发起待办时，或者各流程环节选择审批领导时（下拉框、树形菜单）
	 * @param sujectType 审批类型
     * @param subjects  审批对象
     * @return 返回审批人或审批角色
	 */
	@Override
	public List<Integer> getAuditors(ProcessEnum sujectType, String subjects){
		List<Integer> auditList = Lists.newArrayList();
		if(sujectType.equals(ProcessEnum.audit_role)){
			String[] roleIds = subjects.split(Constants.SPACE);		
			List<SysUser> usersCollector = Lists.newArrayList();
			for(String roleId:roleIds){
				Collection<SysUser> userList = sysUserAdvanceService.getByRole(Integer.valueOf(roleId));
				usersCollector.addAll(userList);
			}	
			for(SysUser sysUser:usersCollector){
				auditList.add(sysUser.getId());
			}
		}else if(sujectType.equals(ProcessEnum.audit_user)){
			String[] userIds = subjects.split(Constants.SPACE);	
			for(String userId:userIds){
				auditList.add(Integer.valueOf(userId));
			}
		}	
		return auditList;
	}

	/**
	 * 我的申请中查看审批对象描述信息
	 * @param subjects 审批对象
	 * @param subjectType 审批对象类型
	 * @param createUserId 如果基于角色审批，并且该角色是拟制人角色，那么根据单据createUserId显示拟制人姓名
	 * @return  返回审批对象描述信息
	 */
	@Override
	public String getSubjectsDesc(String subjects, ProcessEnum subjectType, Integer createUserId){
		StringBuffer sb = new StringBuffer();
		if (subjectType.equals(ProcessEnum.audit_role)) {
			String[] roleList = subjects.split(Constants.SPACE);
			for (String roleId : roleList) {
				SysRole role = sysRoleAdvanceService.loadByKey(Integer.valueOf(roleId));
				if (role != null) {
					//如果是拟制人，需要关联createUserId，显示用户姓名
					if(createUserId != null && role.getId().equals(Integer.valueOf(getCoreConfig().getValue("app.role.applicant")))){
						SysUser user = sysUserAdvanceService.loadByKey(createUserId);
                        if(user != null)
						    sb.append(user.getUsername());
					}else{ //否则显示角色下所有用户姓名
//						List<SysUser> users = sysUserService.getByRole(role.getId());
//						for(SysUser u:users){
//							sb.append(u.getUsername());
//							sb.append(Constants.COMMA);
//						}
						//直接显示角色名称(拟制人)
						sb.append(role.getDescription()); 
					}
				}
			}
		}else if (subjectType.equals(ProcessEnum.audit_user)) {
			String[] userList = subjects.split(Constants.SPACE);
			for (String userId : userList) {
				SysUser user = sysUserAdvanceService.loadByKey(Integer.valueOf(userId));
				if (user != null) {
					sb.append(user.getUsername()+Constants.SPACE);
				}
			}
		}else{
			//sb.append(ProcessEnum.fork.getValue());
		}
		return StringUtils.removeEnd(sb.toString(), Constants.SPACE);
	}
	
	/**
	 * 从待办列表中检查是否为审批人进行审批
	 * @param processTypeId 流程类型Id
	 * @param processHeaderId 流程头Id
	 * @param processReceiptId 主单据Id
	 */
	@Override
	public ProcessTask checkCurrentUserAudit(Integer processTypeId, Integer processHeaderId, Long processReceiptId) {		
		ProcessTask task = processTaskService.getCurrentUserTask(processTypeId, processHeaderId, processReceiptId, appUserSession.getCurrentUser().getUserId());		
		if(task == null){
			log.error("10004 Not found audit certification of current user!");
			throw new UnExpectedAuditException("10004", "Not found audit certification of current user!");
		}
		return task;
	}

    public void deleteUserOrgHashOps(String stepCode, Integer oldAuditId){
    	Map<String, Integer> userOrgMap = userOrgHashOps.get(stepCode);
        for (Entry<String, Integer> entry : userOrgMap.entrySet()) {
            if (entry.getValue().equals(oldAuditId)) {
                userOrgMap.remove(entry.getKey());
                log.debug(userOrgHashOps.get(stepCode));
                userOrgHashOps.put(stepCode, userOrgMap);
                log.debug(userOrgHashOps.get(stepCode));
                break;
            }
        }
    }

    public void createUserOrgHashOps(ProcessAudit o){
        Map<String, Integer> userOrgAudits = userOrgHashOps.get(o.getProcessStep().getStepCode());
        if(userOrgAudits == null){
            userOrgAudits = Maps.newHashMap();
        }
        log.debug("Config:"+o.getSubmitUserId()+Constants.LINE+o.getSubmitOrgId()+" with:"+o);
        userOrgAudits.put(o.getSubmitUserId()+Constants.LINE+o.getSubmitOrgId(), o.getAuditId());
        userOrgHashOps.put(o.getProcessStep().getStepCode(), userOrgAudits);
    }

}
