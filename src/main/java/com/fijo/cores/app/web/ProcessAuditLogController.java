package com.fijo.cores.app.web;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.google.common.collect.Sets;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.fijo.cores.app.model.ProcessAgent;
import com.fijo.cores.app.model.ProcessAuditLog;
import com.fijo.cores.app.model.ProcessStep;
import com.fijo.cores.app.service.IProcessStepAdvanceService;
import com.fijo.cores.exceptions.AppException;
import com.fijo.cores.service.IGenericService;
import com.fijo.cores.utils.decorators.EnumsDecorator;
import com.fijo.cores.web.BaseController;

@Controller
@RequestMapping(value = {"/action/sso/admin/paras/process/auditLog", //SSO跳转，Shrio不拦截
"/action/admin/paras/process/auditLog"}) //后台管理跳转，Shrio拦截校验权限
public class ProcessAuditLogController extends BaseController<ProcessAuditLog, Long>{

	public final Log log = LogFactory.getLog(ProcessAuditLogController.class);
	
	@Autowired
	@Qualifier("processAuditLogService")
	private IGenericService<ProcessAuditLog, Long> service;

	@Autowired
	@Qualifier("enumsDecorator")
	private EnumsDecorator decorator;
	
	@Autowired
	private IProcessStepAdvanceService processStepAdvanceService;
	
	public ProcessAuditLogController() {
		super(ProcessAuditLog.class, null, null);	
	}
	
	@PostConstruct
	private void initService() {
		setService(service);
	}
		
	/**
	 * 查询审批记录（至少需要传递typeId、headerId、receiptId）
	 */
	@RequestMapping(value = "/queryLog", method = RequestMethod.POST)
	@ResponseBody
    @ApiOperation(value = "查询审批日志", httpMethod = "POST", response = Map.class, notes = "查询审批日志",
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public Map<String, Object> queryLog(ProcessAuditLog o) throws Exception {
		if(o.getTypeId() == null || o.getHeaderId()==null || o.getReceiptId()==null)
			throw new AppException("100", String.format("Query with invilidate paremeters typeId:%s headerId:%s receiptId:%s", o.getTypeId(),o.getHeaderId(),o.getReceiptId()));			
		Collection<ProcessAuditLog> list = getService().getAll(o);
		Map<String, Object> dataMap = super.wrapQueryResult((List<ProcessAuditLog>) list);
		Map<String, Object> result = Maps.newHashMap();
		result.put("data", dataMap);
		result.put("message", "");
		result.put("responseid", 1);
		return result;
	}
	
	/**
	 * 工作流查询最后一次审批链条各环节审批意见
	 * @param o
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/queryLastLogs", method = RequestMethod.POST)
	@ResponseBody
    @ApiOperation(value = "工作流查询最后一次审批链条各环节审批意见", httpMethod = "POST", response = Map.class, notes = "工作流查询最后一次审批链条各环节审批意见",
            produces="application/json",consumes="application/x-www-form-urlencoded")
	@SuppressWarnings("unchecked")
	public Map<String, Object> queryLastLogs(ProcessAuditLog o) throws Exception {
		Map<String, Object> result = queryLog(o);		
		Map<String, Object> dataMap = (Map<String, Object>)result.get("data");
		List<ProcessAuditLog> list = (List<ProcessAuditLog>)dataMap.get("Datas");
		List<ProcessAuditLog> lastList = Lists.newArrayList();
		ProcessStep startStep = processStepAdvanceService.getStartStep(o.getHeaderId());
        ProcessStep firstStep = processStepAdvanceService.getFirstStep(o.getHeaderId());
        Collection<String> lastStepIds = Sets.newHashSet();
        for(ProcessAuditLog l : list){
//			int compare = l.getStepId().compareTo(startStep.getStepId());
//			//审批日志记录倒序从数据库查询出来，因此只去最后一次日志循环需要与启动环节进行比较
//			if(compare == -1){ //驳回变成firstStep，退出循环，重头开始
//				lastList.add(l);
//				break;
//			}
//			if(compare >= 1){ //凡是大于启动环节的环节都予以保留
//				lastList.add(l);
//				continue;
//			}
//			else if(compare == 0){ //直到到达最后一次启动环节停止
//				lastList.add(l);
//				break;
//			}



            //审批日志记录倒序从数据库查询出来，因此只取最后一次日志轨迹，就需要与启动环节进行比较
            if(l.getStepId().equals(firstStep.getStepId())){
                //驳回变成firstStep，退出循环，重头开始
				lastList.add(l);
				break;
            }else {
                if (!l.getStepId().equals(startStep.getStepId())) {  //凡是不等于启动环节的环节都予以保留
                    if(!lastStepIds.contains(l.getStepId())) {
                        lastList.add(l);
                        continue;
                    }
                } else {
                    //审批日志记录指针到达上次审批链条被驳回的启动日志记录位置，跳出循环，返回最后一次审批轨迹
                    lastList.add(l);
                    break;
                }
            }
		}
		Collections.reverse(lastList);
		dataMap.put("Datas", lastList);
		return result;
	}
}
