package com.fijo.cores.app.web;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.fijo.cores.app.model.ProcessStep;
import com.fijo.cores.app.service.IProcessStepAdvanceService;
import com.fijo.cores.app.service.IUpdateProcessService;
import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.service.IGenericService;
import com.fijo.cores.utils.decorators.BeanDecoratorExecutor;
import com.fijo.cores.utils.decorators.ProcessStepDecorator;
import com.fijo.cores.utils.json.JacksonUtils;
import com.fijo.cores.web.BaseController;

@Controller
@RequestMapping(value = {"/action/sso/admin/paras/process/step", //SSO跳转，Shrio不拦截
"/action/admin/paras/process/step"}) //后台管理跳转，Shrio拦截校验权限
public class ProcessStepController extends BaseController<ProcessStep, Integer>{

	public final Log log = LogFactory.getLog(ProcessStepController.class);
	
	@Autowired
	@Qualifier("processStepService")
	private IGenericService<ProcessStep, Integer> service;
	
	@Autowired
	private IUpdateProcessService updateProcessService;
	
	@Autowired
	@Qualifier("processStepDecorator")
	private ProcessStepDecorator decorator;
	
	@Autowired
	private IProcessStepAdvanceService processStepCache;
	
	public ProcessStepController() {
		super(ProcessStep.class, null, null);		
	}
	
	@PostConstruct
	private void initService() {
		setService(service);
	}

	/**
	 * 
	 * @param headerCode
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/loadStepsByHeader", method = RequestMethod.POST)
	@ResponseBody
    @ApiOperation(value = "通过流程头获取所有流程环节", httpMethod = "POST", notes = "通过流程头获取所有流程环节", response = Map.class,
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public Map<String, Object> loadStepsByHeader(String headerCode) throws Exception {
		Map<String, Object> result = Maps.newHashMap();
		Collection<ProcessStep> list = updateProcessService.loadStepsByHeader(headerCode);		
		if(list.size() > 0){
			List<Object[]> decorators = Lists.newArrayList();
			decorators.add(new Object[]{decorator, "passId", "passStep"});
			decorators.add(new Object[]{decorator, "failId", "failStep"});
			decorators.add(new Object[]{decorator, "stopId", "stopStep"});
			BeanDecoratorExecutor.populates(list, decorators);
		}
		Map<String, Object> dataMap = super.wrapQueryResult((List<ProcessStep>) list);
		result.put("data", dataMap);
		result.put("message", "");
		result.put("responseid", 1);
		return result;
	}

	@RequestMapping(value = "/saveSteps", method = RequestMethod.POST)
	@ResponseBody
    @ApiOperation(value = "保存流程环节", httpMethod = "POST", notes = "保存流程环节", response = Map.class,
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public Map<String, Object> saveSteps(ProcessStep o) throws Exception {
		List<ProcessStep> processStepList = JacksonUtils.readListValue(o.getProcess(), new TypeReference<List<ProcessStep>>(){});
		Map<String, Object> map = Maps.newHashMap();
		try {
			int ret = updateProcessService.saveSteps(processStepList);
			map.put("message", ret > 0 ? "操作成功!":"操作失败!");
			map.put("responseid", ret > 0 ? 1:ret);
			map.put("data", o);
		}catch (Exception e) {
			map.put("responseid", 0);
			map.put("message", "数据异常！");
			log.error(Exceptions.getStackTraceAsString(e));
		}
		return map;
	}
}
