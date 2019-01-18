/**
 * 
 */
package com.fijo.cores.app.event;

import com.fijo.cores.app.model.*;
import com.fijo.cores.app.service.*;
import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.exceptions.UnExpectedAuditUserException;
import com.fijo.cores.messages.MessageImplementor;
import com.fijo.cores.service.IGenericService;
import com.fijo.cores.shiro.AppUserSession;
import com.fijo.cores.utils.Constants;
import com.fijo.cores.utils.DateUtil;
import com.fijo.cores.utils.configs.CoreConfig;
import com.fijo.cores.utils.enums.ProcessEnum;
import com.fijo.cores.admin.authority.model.ShiroUser;
import com.fijo.cores.admin.authority.model.SysUser;
import com.fijo.cores.admin.authority.service.ISysUserAdvanceService;
import com.fijo.cores.app.model.*;
import com.fijo.cores.app.service.*;
import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.exceptions.UnExpectedAuditUserException;
import com.fijo.cores.messages.MessageImplementor;
import com.fijo.cores.service.IGenericService;
import com.fijo.cores.shiro.AppUserSession;
import com.fijo.cores.utils.Constants;
import com.fijo.cores.utils.DateUtil;
import com.fijo.cores.utils.configs.CoreConfig;
import com.fijo.cores.utils.enums.ProcessEnum;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author zhangbo
 *
 */
@Component
public class ProcessTaskListener<T extends ProcessModel<T>, PK extends Serializable> extends ApplicationObjectSupport implements ApplicationListener<ProcessTaskEvent<T,PK>> {
	private static transient final Log log = LogFactory.getLog(ProcessTaskListener.class);
	
	@Autowired
	private IProcessHeaderAdvanceService processHeaderAdvanceService;

	@Autowired
	private IProcessStepAdvanceService processStepAdvanceService;
	
	@Autowired
	private IProcessAuditAdvanceService processAuditAdvanceService;
	
	@Autowired
	@Qualifier("processTaskService")
	private IGenericService<ProcessTask, Long> processTaskService;

	@Autowired
	public IProcessTrackService processTrackService;
	
	@Autowired
	private IProcessAgentService processAgentService;
	
	@Autowired
	private IProcessStatusService processStatusService;
	
	@Autowired
	private IProcessDraftService processDraftService;
	
	@Autowired
	private CoreConfig config;
	
	@Autowired
	private AppUserSession appUserSession;
	
	@Autowired
	private ISysUserAdvanceService sysUserAdvanceService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;
	
	@Override
	public void onApplicationEvent(ProcessTaskEvent<T, PK> event) {
		ProcessTask processTask = new ProcessTask();
		ProcessJsonData<T, PK> processData = event.getProcessData();
		ProcessModel<?> process = processData.getBusinessData();
		processTask.setTypeId(process.getProcessTypeId());
		processTask.setHeaderId(process.getProcessHeaderId());		
		processTask.setReceiptId(process.getId());
		processTask.setTitle(process.getTitle());
		processTask.setStepId(process.getProcessStepId());
		processTask.setStepCode(process.getProcessStepCode());
		processTask.setCreateOrgId(process.getOrgId());
		processTask.setCreateOrgName(process.getOrgName());
		processTask.setCreateUserId(process.getCreateUserId());
		processTask.setCreateUserCode(process.getCreateUserCode());
		processTask.setCreateUserName(process.getCreateUserName());
		processTask.setCreateDate(process.getCreateDate());
		processTask.setGenerateDate(DateUtil.getCurrent());
		ShiroUser user = appUserSession.getCurrentUser();
		processTask.setPreviousOrgId(user.getOrgId());
		processTask.setPreviousOrgName(user.getOrgName());
		processTask.setPreviousUserId(user.getUserId());
		processTask.setPreviousUserCode(user.getUserCode());
		processTask.setPreviousUserName(user.getUserName());
		processTask.setPreviousDate(DateUtil.getCurrent());				
				
		// 每次记录代办前，先根据业务类型、业务头信息、业务主单据Id删除之前的代办, 再重新写入新的代办
		ProcessTask deleteTasks = new ProcessTask();
		deleteTasks.setTypeId(processTask.getTypeId());
		deleteTasks.setHeaderId(processTask.getHeaderId());
		deleteTasks.setReceiptId(processTask.getReceiptId());
		ProcessStep previousStep = processStepAdvanceService.loadByKey(event.getPreviousStepId());
		if(previousStep!=null && previousStep.getStepClass().equals(ProcessEnum.fork)){ //上一环节为分支环节
			ProcessTask currUserTask = processAuditAdvanceService.checkCurrentUserAudit(process.getProcessTypeId(), process.getProcessHeaderId(), process.getId());
			//删除上一环节待办(实为用户在ProcessTask中的处理环节)
			deleteTasks.setStepId(currUserTask.getStepId());
            //删除上一环节待办(相同环节多人处理时，删除当前处理人处理过的待办)
            deleteTasks.setCurrentUserId(user.getUserId());
		}else{
			//删除上一环节待办
			deleteTasks.setStepId(event.getPreviousStepId());	
		}
		//先 撤销OA待办
		if(event.getRemoveCallback() != null){
            Collection<ProcessTask> delOaTasks = processTaskService.getAll(deleteTasks);
            for(ProcessTask task: delOaTasks) {
                eventPublisher.publishEvent(new ProcessTaskRemoveCallbackEvent(this, event.getRemoveCallback(), task));
            }
		}
		//再 删除系统待办
		int ret = processTaskService.delete(deleteTasks);
		log.debug(ret);

					
		//如果是启动环节，检查草稿数据，若存在则删除草稿数据
		if(processStepAdvanceService.isStart(process.getProcessHeaderId(), process.getProcessStepId())){
			processDraftService.delete(getProcessDraft(process));
		}
		
		//流程未结束，则生成新的待办
		if (!processStepAdvanceService.isFinish(process.getProcessStepId())) {
			if(previousStep!=null && previousStep.getStepClass().equals(ProcessEnum.fork)){// 从上一分支环节进行下一环节时，读取所有从分支环节fork出的并行环节，生成多个待办
				ProcessTrack track = new ProcessTrack(process.getProcessTypeId(), process.getProcessHeaderId(), process.getId(), previousStep.getStepCode());
				boolean forkTojoin = processTrackService.checkAllParallelToJoin(track, previousStep.getPassId());
				if(forkTojoin){
					createTask(event, process.getSubjectType(), process.getSubjects(), processTask, process);
				}
                else{
					// fork出来的节点执行完未到join，不生成新的待办
				}
			}else{
				createTask(event, process.getSubjectType(), process.getSubjects(), processTask, process);
			}
		}
		//流程结束，通知发起人
		else{
			try{
				noticeTask(process, event.getNoticeMethod(), processTask.getPreviousUserId(), process.getCreateUserId());
			}catch(Exception e){
                log.error("Notice task execute failed..............................");
                Exceptions.printException(e);
			}
		}
		
		// 每次生成待办时，需要插入或更新主单据信息
		ret = processStatusService.create(getProcessStatus(process)); 
		log.debug(ret);
	}
	
	/**
	 * 获取最新单据信息
	 * @param process
	 * @return
	 */
	public ProcessStatus getProcessStatus(ProcessModel<?> process){
		ProcessStatus status = new ProcessStatus();
		BeanUtils.copyProperties(process,status);
		status.setProcessStepType(processStepAdvanceService.loadByKey(process.getProcessStepId()).getStepType());
		status.setProcessStepVersion(processStepAdvanceService.loadByKey(process.getProcessStepId()).getSversion());
		status.setReceiptId(process.getId()); //业务单据Id作为主流程状态实体表的单据Id
		status.setId(null); //基于xml实现upsert
		return status;
	}
	
	/**
	 * 获取单据草稿
	 * @param process
	 * @return
	 */
	public ProcessDraft getProcessDraft(ProcessModel<?> process){
		ProcessDraft draft = new ProcessDraft();
		BeanUtils.copyProperties(process,draft);
		draft.setReceiptId(process.getId()); //业务单据Id作为主流程状态实体表的单据Id
		draft.setId(null);
		return draft;
	}

	/**
	 * 创建待办
	 * @param subjectType
	 * @param subjects
	 * @param processTask
	 * @param originalProcess  原始流程（流程可能变成分支并行多个，所以原始流程不能变化）
	 * @param event
	 */
	private void createTask(ProcessTaskEvent<T, PK> event, ProcessEnum subjectType, String subjects, ProcessTask processTask, ProcessModel<?> originalProcess){
        ProcessModel process = null;
        try {
            process = originalProcess.getClass().newInstance();
        } catch (InstantiationException e) {
            log.error("get process instance failed..............................");
            Exceptions.printException(e);
        } catch (IllegalAccessException e) {
            log.error("get process instance failed..............................");
            Exceptions.printException(e);
        }
        BeanUtils.copyProperties(originalProcess,process);
        if (subjectType.equals(ProcessEnum.audit_role)) { // 审批类型为角色
			String[] roles = subjects.split(Constants.SPACE);
			for (String roleId : roles) {
				if(roleId.equals(config.getValue("app.role.applicant"))) { // 拟制人角色需要通知代办到具体发起人
					SysUser user = sysUserAdvanceService.loadByKey(process.getCreateUserId());
					createUserTask(event, processTask, user.getSysOrg().getId(),user.getSysOrg().getOrgName(), user.getId(), user.getUserCode(), user.getUsername(), event.getNoticeMethod(), process);
				}else{
					createRoleTask(event, processTask, Integer.valueOf(roleId), event.getNoticeMethod(), process);
				}
			}
		}
		else if (subjectType.equals(ProcessEnum.audit_user)) { // 审批类型为用户		
			List<Integer> auditUsers = processAuditAdvanceService.getAuditors(ProcessEnum.audit_user, subjects);
			for (Integer userId : auditUsers) {
				SysUser user = sysUserAdvanceService.loadByKey(userId);
				createUserTask(event, processTask, user.getSysOrg().getId(),user.getSysOrg().getOrgName(), user.getId(), user.getUserCode(), user.getUsername(), event.getNoticeMethod(), process);
			}
		}else if (subjectType.equals(ProcessEnum.audit_both)) { // 审批类型为分支审批
			ProcessStep currStep = processStepAdvanceService.loadByKey(process.getProcessStepId());
			if(currStep.getStepClass().equals(ProcessEnum.fork)){ //分支
				ProcessStep params = new ProcessStep();
				params.setForkFromId(currStep.getStepCode());
				params.setStepClass(ProcessEnum.parallel);
				Collection<ProcessStep> forkedParallelSteps = processStepAdvanceService.getAll(params);
				for(ProcessStep s : forkedParallelSteps){
					process.setProcessStepId(s.getStepId());
					process.setProcessStepCode(s.getStepCode());
					processTask.setStepId(s.getStepId());
					processTask.setStepCode(s.getStepCode());
					ProcessAudit audit = processAuditAdvanceService.getProcessAudit(s.getStepCode(), process);					
					this.createTask(event, audit.getSubjectType(), audit.getSubjects(), processTask, process);
				}
			}else if(currStep.getStepClass().equals(ProcessEnum.join)){ //汇聚
				ProcessAudit audit = processAuditAdvanceService.getProcessAudit(currStep.getStepCode(), process);
				this.createTask(event, audit.getSubjectType(), audit.getSubjects(), processTask, process);
			}
		}
	}
	
	/**
	 * 创建角色待办
	 * @param processTask
	 * @param roleId
	 */
	private void createRoleTask(ProcessTaskEvent<T, PK> event, ProcessTask processTask, Integer roleId, ProcessTaskEvent.NoticMethod noticeMethod, ProcessModel<?> process) {
		Collection<SysUser> userList = sysUserAdvanceService.getByRole(roleId);
		for(SysUser user: userList){
			createUserTask(event, processTask, user.getSysOrg().getId(),user.getSysOrg().getOrgName(), user.getId(), user.getUserCode(), user.getUsername(), noticeMethod, process);
		}
	}

    /**
     * 创建用户待办
     * @param event
     * @param processTask
     * @param orgId
     * @param orgName
     * @param userId
     * @param userCode
     * @param userName
     * @param noticeMethod
     * @param process
     */
	private void createUserTask(ProcessTaskEvent<T, PK> event, ProcessTask processTask, Integer orgId, String orgName, Integer userId, String userCode, String userName, 
			ProcessTaskEvent.NoticMethod noticeMethod, ProcessModel<?> process) {	
		SysUser currentUser = sysUserAdvanceService.loadByKey(userId);
		if(currentUser.validate()){
			processTask.setCurrentOrgId(orgId);
			processTask.setCurrentOrgName(orgName);
			processTask.setCurrentUserId(userId);
			processTask.setCurrentUserCode(userCode);
			processTask.setCurrentUserName(userName);
			int ret1 = processTaskService.create(processTask);
			noticeTask(process, noticeMethod, processTask.getPreviousUserId(), processTask.getCurrentUserId());
			if(ret1>0 && event.getCreateCallback()!=null){
                eventPublisher.publishEvent(new ProcessTaskCreateCallbackEvent(this,
                        event.getSource().getClass().getName(), event.getCreateCallback(), processTask));
            }
			//检查代理秘书,并给秘书发送待办
			ProcessAgent param = new ProcessAgent(processTask.getHeaderId(),userId,true);
			Collection<ProcessAgent> agentList = processAgentService.getAll(param);
			for(ProcessAgent agent:agentList){
				ProcessTask agentTask = new ProcessTask();
				BeanUtils.copyProperties(processTask, agentTask);
				SysUser agentUser = sysUserAdvanceService.loadByKey(agent.getAgentUserId());
				agentTask.setCurrentOrgId(agentUser.getSysOrg().getId());
				agentTask.setCurrentOrgName(agentUser.getSysOrg().getOrgName());
				agentTask.setCurrentUserId(agentUser.getId());
				agentTask.setCurrentUserCode(agentUser.getUserCode());
				agentTask.setCurrentUserName(agentUser.getUsername());
				int ret2 = processTaskService.create(agentTask);
				noticeTask(process, noticeMethod, agentTask.getPreviousUserId(), agentTask.getCurrentUserId());
				if(ret2>0 && event.getCreateCallback()!=null){
					try{
						event.getCreateCallback().execute(agentTask);
					}catch(Exception e){
                        log.error("create callback execute failed..............................");
						Exceptions.printException(e);
					}
				}
			}
		}else{
            throw new UnExpectedAuditUserException("10013", String.format("10013 Current process step %s audit user  %s is unavailable!", processTask.getStepCode()+processTask.getStepDesc(), userId));
        }
	}

    /**
     * 根据需要，选择不同的方式通知任务待办人或者任务发起人
     * @param process
     * @param noticeMethod
     * @param fromUserId
     * @param toUserId
     */
	private void noticeTask(ProcessModel<?> process, ProcessTaskEvent.NoticMethod noticeMethod, Integer fromUserId, Integer toUserId){
		try{
			MessageImplementor sender = null;
			String title = processHeaderAdvanceService.loadByKey(process.getProcessHeaderId()).getHeaderDesc()+Constants.LINE+process.getTitle();
			String content = processStepAdvanceService.loadByKey(process.getProcessStepId()).getStepDesc();			
			switch(noticeMethod){
				case Weixin:{
					sender = (MessageImplementor) getApplicationContext().getBean("weixinMessageImplementor"); //应用桥接实现
					break;
				}
				case SMS:{
					sender = (MessageImplementor) getApplicationContext().getBean("smsMessageImplementor"); //应用桥接实现
					break;
				}
				case Email:{
					sender = (MessageImplementor) getApplicationContext().getBean("emailMessageImplementor"); //应用桥接实现
					break;
				}
				case OA:{					
					sender = (MessageImplementor) getApplicationContext().getBean("oaMessageImplementor"); //应用桥接实现		
					break;
				}
				default:
			}
			if(sender != null){
				sender.postMessage(title, content, fromUserId, toUserId, process);
			}
		}catch(Exception e){
            log.error("notice task execute failed..............................");
			Exceptions.printException(e);
		}
	}
}
