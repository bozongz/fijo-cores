package com.fijo.cores.app.web;

import java.util.Map;

import javax.annotation.PostConstruct;

import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.fijo.cores.admin.authority.model.ShiroUser;
import com.fijo.cores.app.model.ProcessHeader;
import com.fijo.cores.app.model.ProcessModel;
import com.fijo.cores.app.model.ProcessStatus;
import com.fijo.cores.app.model.ProcessStatusView;
import com.fijo.cores.app.service.IProcessAuditAdvanceService;
import com.fijo.cores.app.service.IProcessDraftService;
import com.fijo.cores.app.service.IProcessHeaderAdvanceService;
import com.fijo.cores.app.service.IProcessStatusService;
import com.fijo.cores.app.service.IProcessStepAdvanceService;
import com.fijo.cores.shiro.AppUserSession;
import com.fijo.cores.utils.enums.ProcessEnum;
import com.fijo.cores.web.BaseController;

@Controller
@RequestMapping(value = {"/action/sso/process", //SSO跳转，Shrio不拦截
"/action/process"}) //后台管理跳转，Shrio拦截校验权限
public class ProcessStatusController extends BaseController<ProcessStatus, Long>{

	public final Log log = LogFactory.getLog(ProcessStatusController.class);
	
	@Autowired
	private AppUserSession appUserSession;
	
	@Autowired
	private IProcessStatusService processStatusService;
	
	@Autowired
	private IProcessDraftService processDraftService;
	
	@Autowired
	private IProcessHeaderAdvanceService processHeaderAdvanceService;

	@Autowired
	private IProcessStepAdvanceService processStepAdvanceService;
	
	@Autowired
	private IProcessAuditAdvanceService processAuditAdvanceService;
	
	/**构建一个以ProcessHeaderCode为Key的流程映射**/
	public static Map<ProcessEnum, ProcessStatusView> viewMap = Maps.newHashMap();
	
	public ProcessStatusController() {
		super(ProcessStatus.class, null, null);	
	}
	
	@PostConstruct
	private void initService() {
		setService(processStatusService);
		viewMap.put(ProcessEnum.GLOBAL, new ProcessStatusView(processStatusService, ProcessStatus.class));
	}
	
	/**
	 * 处理各流程
	 * @param view
	 * @param action
	 * @param processHeaderId
	 * @param processStepId
	 * @param receiptId
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/handleProcess/{action}/{processTypeId}/{processHeaderId}/{processStepId}/{receiptId}", method = RequestMethod.POST)
	@ResponseBody
    @ApiOperation(value = "处理各流程", httpMethod = "POST", notes = "处理各流程", response = Map.class,
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public Map<String, Object> handleProcess(@PathVariable("action") String action,
			@PathVariable("processTypeId") Integer processTypeId,@PathVariable("processHeaderId") Integer processHeaderId,
			@PathVariable("processStepId") Integer processStepId,@PathVariable("receiptId") Long receiptId) throws Exception {		
		if(action.equals("read")){
			return returnJsonData(1, null);
		}else {
			ProcessModel<?> process = processStatusService.getOne(processTypeId, processHeaderId, receiptId);
			if(process == null){ //如果通过主单据查询为空，则说明数据来源于我的草稿
				process = processDraftService.getOne(processTypeId, processHeaderId, receiptId);
			}
			ShiroUser currentUser = appUserSession.getCurrentUser();
			ProcessHeader processHeader = processHeaderAdvanceService.loadByKey(processHeaderId);
			Integer startStepId = processStepAdvanceService.getStartStep(processHeader.getHeaderId()).getStepId();
			Integer currentStepId = process.getProcessStepId();			
			if(processStepAdvanceService.isFinish(currentStepId)){
				return returnJsonData(0, "该申请已结束!");
			}else{
				Integer nextStepId = processStepAdvanceService.getProcessStatusCheckNextStep(currentStepId, ProcessEnum.pass).getStepId();
				if(action.equals("delete")){
					if(process.getCreateUserId().equals(currentUser.getUserId()) && 
							(startStepId.equals(currentStepId) ||  //第一个审批阶段(位于启动环节)可以撤回
									startStepId.equals(nextStepId) //驳回发起修改阶段可以撤回									
					)){
						return returnJsonData(1, null);
					}else{
						return returnJsonData(0, "仅发起人在发起环节可撤回申请!");
					}
				}else if(action.equals("edit")){
					if(process.getCreateUserId().equals(currentUser.getUserId()) && 
							(startStepId.equals(currentStepId) ||  //第一个审批阶段(位于启动环节)可以修改
									startStepId.equals(nextStepId) //驳回发起修改阶段可以修改									
					)){
						return returnJsonData(1, null);
					}else{
						return returnJsonData(0, "仅发起人在发起环节可修改申请!");
					}
				}else if(action.equals("audit")){
					//执行检查登录用户是否有权限处理该环节，如果权限不足将抛出UnauthorizedException
					boolean hasAuth = true;
					try{
						processAuditAdvanceService.checkCurrentUserAudit(process.getProcessTypeId(), process.getProcessHeaderId(), receiptId);
					}catch(UnauthorizedException e){
						hasAuth = false;
					}	
					if(!hasAuth){
						return returnJsonData(0, "您没有当前环节处理权限!");
					}else{
						return returnJsonData(1, null);
					}	
				} 
			}		
		}
		return returnJsonData(0, "错误：未知的操作请求!");
	}
	
	private Map<String, Object> returnJsonData(int ret, String mes){
		Map<String, Object> map = Maps.newHashMap();
		map.put("responseid", ret);
		map.put("message", mes);
		return map;
	}
	
	/**
	 * 检查是否还有在途流程正在执行中
	 * @param processHeaderCode
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/checkProcessRunning/{processHeaderCode}", method = RequestMethod.POST)
	@ResponseBody
    @ApiOperation(value = "检查是否还有在途流程正在执行中", httpMethod = "POST", notes = "检查是否还有在途流程正在执行中", response = Boolean.class,
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public Boolean checkProcessRunning(@PathVariable("processHeaderCode") String processHeaderCode) throws Exception {
		ProcessHeader h = processHeaderAdvanceService.loadByUnique(processHeaderCode);
		return processStatusService.checkProcessRunning(h.getTypeId(), h.getHeaderId(), h.getHversion());
	}
}
