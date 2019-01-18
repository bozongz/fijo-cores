package com.fijo.cores.app.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.fijo.cores.shiro.AppUserSession;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.fijo.cores.app.model.ProcessAudit;
import com.fijo.cores.app.service.IProcessAuditAdvanceService;
import com.fijo.cores.service.IGenericService;
import com.fijo.cores.shiro.AppUserSession;
import com.fijo.cores.utils.decorators.BeanDecoratorExecutor;
import com.fijo.cores.utils.decorators.ProcessSubjectsDescDecorator;
import com.fijo.cores.web.BaseController;

@Controller
@RequestMapping(value = {"/action/sso/admin/paras/process/audit", //SSO跳转，Shrio不拦截
"/action/admin/paras/process/audit"}) //后台管理跳转，Shrio拦截校验权限
public class ProcessAuditController extends BaseController<ProcessAudit, Integer>{

	public final Log log = LogFactory.getLog(ProcessAuditController.class);
	
	@Autowired
	@Qualifier("processAuditService")
	private IGenericService<ProcessAudit, Integer> service;
	
	@Autowired
	private IProcessAuditAdvanceService processAuditCache;
	
	@Autowired
	private AppUserSession appUserSession;
	
	@Autowired
	private ProcessSubjectsDescDecorator decorator;
	
	public ProcessAuditController() {
		super(ProcessAudit.class, null, null);	
	}
	
	@PostConstruct
	private void initService() {
		setService(service);
	}

	/**
	 * 显示各流程审批配置
	 */
    @ApiOperation(value = "查询所有审批人", httpMethod = "POST", notes = "查询所有审批人", response = Map.class,
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public Map<String, Object> query(ProcessAudit o) throws Exception {		
		Map<String, Object> result = Maps.newHashMap();
		List<ProcessAudit> list = new ArrayList<ProcessAudit>(processAuditCache.getValues());
		Collections.sort(list);
		if(list.size() > 0){
			List<Object[]> decorators = Lists.newArrayList();
			decorators.add(new Object[]{decorator, "subjects", "subjectsDesc"});
			BeanDecoratorExecutor.populates(list, decorators);
		}
		Map<String, Object> dataMap = super.wrapQueryResult((List<ProcessAudit>) list);
		result.put("data", dataMap);
		result.put("message", "");
		result.put("responseid", 1);
		return result;
	}
	
}
