package com.fijo.cores.app.web;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.fijo.cores.shiro.AppUserSession;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.fijo.cores.admin.authority.model.ShiroUser;
import com.fijo.cores.admin.authority.model.SysUser;
import com.fijo.cores.admin.authority.service.ISysUserAdvanceService;
import com.fijo.cores.app.model.ProcessAgent;
import com.fijo.cores.app.model.ProcessHeader;
import com.fijo.cores.app.service.IProcessAgentService;
import com.fijo.cores.app.service.IProcessHeaderAdvanceService;
import com.fijo.cores.shiro.AppUserSession;
import com.fijo.cores.utils.Constants;
import com.fijo.cores.utils.DateUtil;
import com.fijo.cores.web.BaseController;

@Controller
@RequestMapping(value = {"/action/sso/admin/paras/process/agent", //SSO跳转，Shrio不拦截
"/action/admin/paras/process/agent"}) //后台管理跳转，Shrio拦截校验权限
public class ProcessAgentController extends BaseController<ProcessAgent, Integer>{

	public final Log log = LogFactory.getLog(ProcessAgentController.class);
	
	@Autowired
	private IProcessAgentService service;	

	@Autowired
	private IProcessHeaderAdvanceService processHeaderAdvanceService;

	@Autowired
	private AppUserSession appUserSession;
	
	@Autowired
	private ISysUserAdvanceService sysUserAdvanceService;
	
	public ProcessAgentController() {
		super(ProcessAgent.class, null, null);	
	}
	
	@PostConstruct
	private void initService() {
		setService(service);
	}

	@Override
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@ResponseBody
    @ApiOperation(value = "新增代理", httpMethod = "POST", notes = "新增代理",
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public Map<String, Object> create(ProcessAgent o) throws Exception {
		Map<String, Object> map = Maps.newHashMap();
		String agentUserIds = o.getAgentUserIds();
		ShiroUser currentUser = appUserSession.getCurrentUser();
		if(StringUtils.isEmpty(agentUserIds)){
			map.put("message", "审批候选人不可为空!");
			map.put("responseid", 0);
		}else{
			List<ProcessAgent> agentList = Lists.newArrayList();
			String[] agentUserIdArray = agentUserIds.split(Constants.SPACE);
			List<String> agentUserIdList = Arrays.asList(agentUserIdArray);
			if(agentUserIdList.contains(String.valueOf(currentUser.getUserId()))){
				map.put("message", "审批候选人不能为自己!");
				map.put("responseid", 0);
			}else{
				ProcessHeader processHeader= processHeaderAdvanceService.loadByKey(o.getHeaderId());			
				Date beginDate = DateUtil.getCurrent();
				for(String agentUserId:agentUserIdArray){
					SysUser sysUser = sysUserAdvanceService.loadByKey(Integer.valueOf(agentUserId));
					ProcessAgent agent = new ProcessAgent(processHeader.getTypeId(), o.getHeaderId(), currentUser.getUserId(), 
							sysUser.getId(), sysUser.getUserCode(),sysUser.getUsername(), beginDate, o.getExpires());
					agentList.add(agent);
				}			
				int ret = service.batchCreate(agentList);
				map.put("message", ret > 0 ? "操作成功!":"操作失败!");
				map.put("responseid", ret > 0 ? 1:ret);
				map.put("data", o);
			}
			
		}
		return map;
	}
	
	@Override
    @ApiOperation(value = "查询代理", httpMethod = "POST", notes = "查询代理",
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public Map<String, Object> query(ProcessAgent o) throws Exception {
		ShiroUser currentUser = appUserSession.getCurrentUser();
		o.setUserId(currentUser.getUserId());
		Collection<ProcessAgent> list = getService().getAll(o);
		Map<String, Object> dataMap = super.wrapQueryResult((List<ProcessAgent>) list);
		Map<String, Object> result = Maps.newHashMap();
		result.put("data", dataMap);
		result.put("message", "");
		result.put("responseid", 1);
		return result;
	}
	
	@Override
    @ApiOperation(value = "删除代理", httpMethod = "POST", notes = "删除代理",
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public Map<String, Object> delete(Integer agentId) throws Exception {
		ProcessAgent o = new ProcessAgent();
		o.setAgentId(agentId);
		return super.deleteObj(o);
	}
}
