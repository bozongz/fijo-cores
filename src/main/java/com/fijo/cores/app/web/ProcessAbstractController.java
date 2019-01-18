/**
 * 
 */
package com.fijo.cores.app.web;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.fijo.cores.model.JsonResponse;
import com.fijo.cores.model.JsonResponse;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.fijo.cores.admin.authority.model.ShiroUser;
import com.fijo.cores.app.model.ProcessJsonData;
import com.fijo.cores.app.model.ProcessModel;
import com.fijo.cores.app.service.IProcessService;
import com.fijo.cores.exceptions.AppException;
import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.exceptions.ExceptionsMsgAlert;
import com.fijo.cores.exceptions.TransactionRollbackException;
import com.fijo.cores.model.KeyValue;
import com.fijo.cores.shiro.AppUserSession;
import com.fijo.cores.utils.ObjectUtil;
import com.fijo.cores.utils.annotations.LogAudit;
import com.fijo.cores.utils.decorators.BeanDecoratorExecutor;
import com.fijo.cores.utils.decorators.ProcessStepDecorator;
import com.fijo.cores.utils.decorators.ProcessSubjectsDescDecorator;
import com.fijo.cores.utils.enums.ProcessEnum;
import com.fijo.cores.web.LogicController;

/**
 * 逻辑实体控制层
 * 
 * @author zhangbo
 *
 */
public abstract class ProcessAbstractController<T extends ProcessModel<T>, PK extends Serializable>
		extends LogicController<T, PK> {
	
	@Autowired
	private AppUserSession appUserSession;
	
	@Autowired
	private ProcessSubjectsDescDecorator processSubjectsDescDecorator;
	
	@Autowired
	@Qualifier("processStepDecorator")
	private ProcessStepDecorator processStepDecorator;
	
	private IProcessService<T, PK> service;
	
	/**
	 * 
	 * @param persistentClass 持久化对象
	 * @param listPage 列表页面
	 * @param formPage 表单页面
	 */
	public ProcessAbstractController(Class<T> persistentClass, String listPage, String formPage) {
		super(persistentClass, listPage, formPage);
	}

    /**
     * 获取流程编码
     * @return
     */
    @RequestMapping(value="getProcessSeqCode",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "获取流程下一序列号", httpMethod = "POST", notes = "获取流程下一序列号",
            produces="application/json",consumes="application/x-www-form-urlencoded")
    public JsonResponse getProcessSeqCode(){
        Map<String, Object> map = Maps.newHashMap();
        map.put("code",service.getProcessSeqCode());
        JsonResponse res = new JsonResponse();
        res.setData(map);
        res.setResponseid(1);
        return res;
    }

	@RequestMapping(value = "/createArchive", method = RequestMethod.POST)
	@ResponseBody
    @ApiOperation(value = "创建归档流程", httpMethod = "POST", notes = "创建归档流程",
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public Map<String, Object> createArchive(T o) throws Exception {
		Map<String, Object> map = Maps.newHashMap();
		T ret;
		try {			
			if(o.getIscg())	{
				ret = service.createArchiveDraft(o);	
			}else{
				ret = service.createArchive(o);
			}
			map.put("message", null!=ret ? "操作成功!":"操作失败!");
			map.put("responseid", null!=ret ? 1:0);
			map.put("data", ret);
		}catch (NullPointerException e) {
			map.put("responseid", 0);
			map.put("message", "必填数据不允许为空: "+ExceptionsMsgAlert.alertDuplicateKeyException(persistentClass, Exceptions.getStackTraceAsString(e)));
			log.error("ProcessAbstractController catch NullPointerException during createArchive: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (DuplicateKeyException e) {
			map.put("responseid", 0);
			map.put("message", "唯一性字段不允许重复: "+ExceptionsMsgAlert.alertDuplicateKeyException(persistentClass, Exceptions.getStackTraceAsString(e)));
			log.error("ProcessAbstractController catch DuplicateKeyException during createArchive: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (DataIntegrityViolationException e) {
			map.put("responseid", 0);
			map.put("message", "操作失败，请检查必填字段:"+ExceptionsMsgAlert.alertDataIntegrityViolationException(persistentClass, Exceptions.getStackTraceAsString(e)));
			log.error("ProcessAbstractController catch DataIntegrityViolationException during createArchive: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (TransactionRollbackException e) {
			map.put("responseid", 0);
			map.put("message", e.getErrorMessage());
			log.error("ProcessAbstractController catch TransactionRollbackException during createArchive: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (AppException e) {
			map.put("responseid", 0);
			map.put("message", "数据异常，请确认后重新尝试!");
			log.error("ProcessAbstractController catch AppException during createArchive: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (Exception e) {
			map.put("responseid", 0);
			map.put("message", "数据异常，请确认后重新尝试!");
			log.error("ProcessAbstractController catch Exception during createArchive: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}
		return map;
	}
	
	@RequestMapping(value = "/updateArchive", method = RequestMethod.POST)
	@ResponseBody
    @ApiOperation(value = "修改归档流程", httpMethod = "POST", notes = "修改归档流程",
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public Map<String, Object> updateArchive(T o) throws Exception {
		Map<String, Object> map = Maps.newHashMap();
		T ret;
		try {			
			if(o.getIscg())	{
				ret = service.updateArchiveDraft(o);	
			}else{
				ret = service.updateArchive(o);
			}
            map.put("message", null!=ret ? "操作成功!":"操作失败!");
            map.put("responseid", null!=ret ? 1:0);
			map.put("data", ret);
		}catch (NullPointerException e) {
			map.put("responseid", 0);
			map.put("message", "必填数据不允许为空: "+ExceptionsMsgAlert.alertDuplicateKeyException(persistentClass, Exceptions.getStackTraceAsString(e)));
			log.error("ProcessAbstractController catch NullPointerException during updateArchive: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (DuplicateKeyException e) {
			map.put("responseid", 0);
			map.put("message", "唯一性字段不允许重复: "+ExceptionsMsgAlert.alertDuplicateKeyException(persistentClass, Exceptions.getStackTraceAsString(e)));
			log.error("ProcessAbstractController catch DuplicateKeyException during updateArchive: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (DataIntegrityViolationException e) {
			map.put("responseid", 0);
			map.put("message", "操作失败，请检查必填字段:"+ExceptionsMsgAlert.alertDataIntegrityViolationException(persistentClass, Exceptions.getStackTraceAsString(e)));
			log.error("ProcessAbstractController catch DataIntegrityViolationException during updateArchive: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (TransactionRollbackException e) {
			map.put("responseid", 0);
			map.put("message", e.getErrorMessage());
			log.error("ProcessAbstractController catch TransactionRollbackException during updateArchive: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (AppException e) {
			map.put("responseid", 0);
			map.put("message", "数据异常，请确认后重新尝试!");
			log.error("ProcessAbstractController catch AppException during updateArchive: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (Exception e) {
			map.put("responseid", 0);
			map.put("message", "数据异常，请确认后重新尝试!");
			log.error("ProcessAbstractController catch Exception during updateArchive: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}
		return map;
	}
	
	@RequestMapping(value = "/updateArchiveStep", method = RequestMethod.POST)
	@ResponseBody
    @ApiOperation(value = "审批归档流程", httpMethod = "POST", notes = "审批归档流程",
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public Map<String, Object> updateArchiveStep(@RequestParam("id")PK id, @RequestParam("result")ProcessEnum result, 
			@RequestParam("opinion")String opinion, String auditUser, String auditRole) throws Exception {
		Map<String, Object> map = Maps.newHashMap();
		T returnProcess = null;
		try {
			T o = getService().getById(id);
			if(StringUtils.isNotEmpty(auditUser)){
				o.setAuditUser(auditUser);
			}
			if(StringUtils.isNotEmpty(auditRole)){
				o.setAuditRole(auditRole);
			}	
			ProcessJsonData<T, PK> data = new ProcessJsonData<>(result, opinion, o);
			returnProcess = service.updateArchiveStep(data);
			map.put("message", returnProcess!=null ? "操作成功!":"操作失败!");
			map.put("responseid", returnProcess!=null ? 1:0);
			map.put("data", returnProcess);
		}catch (TransactionRollbackException e) {
			map.put("responseid", 0);
			map.put("message", e.getErrorMessage());
			log.error(String.format("ProcessAbstractController catch TransactionRollbackException during updateArchiveStep: id %s result %s opinion %s", id,result,opinion));
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (Exception e) {
			map.put("message", "操作失败!");
			map.put("responseid", 0);
			map.put("data", returnProcess);
			log.error(String.format("ProcessAbstractController catch Exception during updateArchiveStep: id %s result %s opinion %s", id,result,opinion));
			log.error(Exceptions.getStackTraceAsString(e));
		}
		return map;
	}
	
	@RequestMapping(value = "/updateArchiveStepJson", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	@ResponseBody
    @ApiOperation(value = "审批归档流程", httpMethod = "POST", notes = "审批归档流程",
            produces="application/json",consumes="application/json")
	public Map<String, Object> updateArchiveStepJson(@RequestBody ProcessJsonData<T, PK> data) throws Exception {	
		T returnProcess = null;
		Map<String, Object> map = Maps.newHashMap();		
		try {
			@SuppressWarnings("unchecked")
			T target = getService().getById((PK) data.getBusinessData().getId());
			//前端表单数据覆盖数据库数据，但不包含流程相关字段
			BeanUtils.copyProperties(data.getBusinessData(), target, ObjectUtil.getProcessFieldNames(data.getBusinessData()));
			data.setBusinessData(target);
			returnProcess = service.updateArchiveStep(data);
			map.put("message", returnProcess!=null ? "操作成功!":"操作失败!");
			map.put("responseid", returnProcess!=null ? 1:0);
			map.put("data", returnProcess);
		}catch (TransactionRollbackException e) {
			map.put("responseid", 0);
			map.put("message", e.getErrorMessage());
			log.error("ProcessAbstractController catch TransactionRollbackException during updateArchiveStepJson:"+data);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (Exception e) {
			map.put("message", "操作失败!");
			map.put("responseid", 0);
			map.put("data", returnProcess);
			log.error("ProcessAbstractController catch Exception during updateArchiveStepJson:"+data);
			log.error(Exceptions.getStackTraceAsString(e));
		}
		return map;
	}
	
	@RequestMapping(value = "/createOnce", method = RequestMethod.POST)
	@ResponseBody
    @ApiOperation(value = "创建流水流程", httpMethod = "POST", notes = "创建流水流程",
            produces="application/json",consumes="application/application/x-www-form-urlencoded")
	public Map<String, Object> createOnce(T o) throws Exception {
		Map<String, Object> map = Maps.newHashMap();
		T ret;
		try {			
			if(o.getIscg())	{
				ret = service.createOnceDraft(o);	
			}else{
				ret = service.createOnce(o);
			}
            map.put("message", null!=ret ? "操作成功!":"操作失败!");
            map.put("responseid", null!=ret ? 1:0);
			map.put("data", ret);
		}catch (NullPointerException e) {
			map.put("responseid", 0);
			map.put("message", "必填数据不允许为空: "+ExceptionsMsgAlert.alertDuplicateKeyException(persistentClass, Exceptions.getStackTraceAsString(e)));
			log.error("ProcessAbstractController catch NullPointerException during createOnce: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (DuplicateKeyException e) {
			map.put("responseid", 0);
			map.put("message", "唯一性字段不能为空或重复:"+ExceptionsMsgAlert.alertDuplicateKeyException(persistentClass, Exceptions.getStackTraceAsString(e)));
			log.error("ProcessAbstractController catch DuplicateKeyException during createOnce: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (DataIntegrityViolationException e) {
			map.put("responseid", 0);
			map.put("message", "请检查约束条件:"+ExceptionsMsgAlert.alertDataIntegrityViolationException(persistentClass, Exceptions.getStackTraceAsString(e)));
			log.error("ProcessAbstractController catch DataIntegrityViolationException during createOnce: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (TransactionRollbackException e) {
			map.put("responseid", 0);
			map.put("message", e.getErrorMessage());
			log.error("ProcessAbstractController catch TransactionRollbackException during createOnce:"+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (AppException e) {
			map.put("responseid", 0);
			map.put("message", "数据异常，请确认后重新尝试!");
			log.error("ProcessAbstractController catch AppException during createOnce: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (Exception e) {
			map.put("responseid", 0);
			map.put("message", "数据异常，请确认后重新尝试!");
			log.error("ProcessAbstractController catch Exception during createOnce: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}
		return map;
	}
	
	@RequestMapping(value = "/updateOnce", method = RequestMethod.POST)
	@ResponseBody
    @ApiOperation(value = "修改流水流程", httpMethod = "POST", notes = "修改流水流程",
            produces="application/json",consumes="application/application/x-www-form-urlencoded")
	public Map<String, Object> updateOnce(T o) throws Exception {
		Map<String, Object> map = Maps.newHashMap();
		T ret;
		try {			
			if(o.getIscg())	{
				ret = service.updateOnceDraft(o);	
			}else{
				ret = service.updateOnce(o);
			}
            map.put("message", null!=ret ? "操作成功!":"操作失败!");
            map.put("responseid", null!=ret ? 1:0);
			map.put("data", ret);
		}catch (NullPointerException e) {
			map.put("responseid", 0);
			map.put("message", "必填数据不允许为空: "+ExceptionsMsgAlert.alertDuplicateKeyException(persistentClass, Exceptions.getStackTraceAsString(e)));
			log.error("ProcessAbstractController catch NullPointerException during updateOnce: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (DuplicateKeyException e) {
			map.put("responseid", 0);
			map.put("message", "唯一性字段不允许重复: "+ExceptionsMsgAlert.alertDuplicateKeyException(persistentClass, Exceptions.getStackTraceAsString(e)));
			log.error("ProcessAbstractController catch DuplicateKeyException during updateOnce: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (DataIntegrityViolationException e) {
			map.put("responseid", 0);
			map.put("message", "请检查约束条件:"+ExceptionsMsgAlert.alertDataIntegrityViolationException(persistentClass, Exceptions.getStackTraceAsString(e)));
			log.error("ProcessAbstractController catch DataIntegrityViolationException during updateOnce: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (TransactionRollbackException e) {
			map.put("responseid", 0);
			map.put("message", e.getErrorMessage());
			log.error("ProcessAbstractController catch TransactionRollbackException during updateOnce:"+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (AppException e) {
			map.put("responseid", 0);
			map.put("message", "数据异常，请确认后重新尝试!");
			log.error("ProcessAbstractController catch AppException during updateOnce: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (Exception e) {
			map.put("responseid", 0);
			map.put("message", "数据异常，请确认后重新尝试!");
			log.error("ProcessAbstractController catch Exception during updateOnce: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}
		return map;
	}
	
	@RequestMapping(value = "/updateOnceStep", method = RequestMethod.POST)
	@ResponseBody
    @ApiOperation(value = "审批流水流程", httpMethod = "POST", notes = "审批流水流程",
            produces="application/json",consumes="application/application/x-www-form-urlencoded")
	public Map<String, Object> updateOnceStep(@RequestParam("id")PK id, @RequestParam("result")ProcessEnum result, 
			@RequestParam("opinion")String opinion, String auditUser, String auditRole) throws Exception {
		Map<String, Object> map = Maps.newHashMap();
		T returnProcess;
		try {
			T o = getService().getById(id);
			if(StringUtils.isNotEmpty(auditUser)){
				o.setAuditUser(auditUser);
			}
			if(StringUtils.isNotEmpty(auditRole)){
				o.setAuditRole(auditRole);
			}	
			ProcessJsonData<T, PK> data = new ProcessJsonData<>(result, opinion, o);
			returnProcess = service.updateOnceStep(data);
			map.put("message", returnProcess!=null ? "操作成功!":"操作失败!");
			map.put("responseid", returnProcess!=null ? 1:0);
			map.put("data", returnProcess);
		}catch (TransactionRollbackException e) {
			map.put("responseid", 0);
			map.put("message", e.getErrorMessage());
			log.error(String.format("ProcessAbstractController catch TransactionRollbackException during updateOnceStep: id %s result %s opinion %s", id,result,opinion));
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (Exception e) {
			map.put("message", "操作失败!");
			map.put("responseid", 0);
			log.error(String.format("ProcessAbstractController catch Exception during updateOnceStep: id %s result %s opinion %s", id,result,opinion));
			log.error(Exceptions.getStackTraceAsString(e));
		}
		return map;
	}
	
	@RequestMapping(value = "/updateOnceStepJson", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	@ResponseBody
    @ApiOperation(value = "审批流水流程", httpMethod = "POST", notes = "审批流水流程",
            produces="application/json",consumes="application/json")
	public Map<String, Object> updateOnceStepJson(@RequestBody ProcessJsonData<T, PK> data) throws Exception {	
		T returnProcess = null;
		Map<String, Object> map = Maps.newHashMap();		
		try {
			@SuppressWarnings("unchecked")
			T target = getService().getById((PK) data.getBusinessData().getId());
			//前端表单数据覆盖数据库数据，但不包含流程相关字段
			BeanUtils.copyProperties(data.getBusinessData(), target, ObjectUtil.getProcessFieldNames(data.getBusinessData()));
			data.setBusinessData(target);
			returnProcess = service.updateOnceStep(data);
			map.put("message", returnProcess!=null ? "操作成功!":"操作失败!");
			map.put("responseid", returnProcess!=null ? 1:0);
			map.put("data", returnProcess);
		}catch (TransactionRollbackException e) {
			map.put("responseid", 0);
			map.put("message", e.getErrorMessage());
			log.error("ProcessAbstractController catch TransactionRollbackException during updateOnceStepJson:"+data);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (Exception e) {
			map.put("message", "操作失败!");
			map.put("responseid", 0);
			map.put("data", returnProcess);
			log.error("ProcessAbstractController catch Exception during updateOnceStepJson:"+data);
			log.error(Exceptions.getStackTraceAsString(e));
		}
		return map;
	}

	@SuppressWarnings("deprecation")
    @RequestMapping(value = "/queryAll", method = RequestMethod.POST)
	@ResponseBody
    @ApiOperation(value = "查询所有流程", httpMethod = "POST", notes = "查询所有流程",
            produces="application/json",consumes="application/application/x-www-form-urlencoded")
	public Map<String, Object> queryAll(T o) throws Exception {
		return queryArchive(o);
	}

    @SuppressWarnings("deprecation")
	@RequestMapping(value = "/queryMy", method = RequestMethod.POST)
	@ResponseBody
    @ApiOperation(value = "查询我的流程", httpMethod = "POST", notes = "查询我的流程",
            produces="application/json",consumes="application/application/x-www-form-urlencoded")
	public Map<String, Object> queryMy(T o) throws Exception {
		ShiroUser currentUser = appUserSession.getCurrentUser();
		o.setCreateUserId(currentUser.getUserId());// 只查个人单据	
		return queryArchive(o);
	}
	
	/**
	 * 适用于一次性申请，只查询本人申请单据
	 */
	@Override
	@Deprecated
    @ApiOperation(value = "查询我的流程", httpMethod = "POST", notes = "查询我的流程",
            produces="application/json",consumes="application/application/x-www-form-urlencoded")
	public Map<String, Object> query(T o) throws Exception {
		return queryMy(o);
	}
	
	/**
	 * 适用于档案性申请，可查不同人提交的单据申请
	 * @param o
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/queryArchive", method = RequestMethod.POST)
	@ResponseBody
	@Deprecated
    @ApiOperation(value = "查询所有归档流程", httpMethod = "POST", notes = "查询所有归档流程",
            produces="application/json",consumes="application/application/x-www-form-urlencoded")
	public Map<String, Object> queryArchive(T o) throws Exception {
		Map<String, Object> result = Maps.newHashMap();
		if(null != o.getIscg()){
			if(o.getIscg())
				o.setEnabled(false); //只要是草稿，状态必然是false
			else
				o.setEnabled(true);
		}
		Collection<T> list = service.getAll(o);
		if(list.size() > 0){
			List<Object[]> decorators = Lists.newArrayList();
			decorators.add(new Object[]{processSubjectsDescDecorator, "subjects", "subjectsDesc"});
			decorators.add(new Object[]{processStepDecorator, "processStepCode", "stepDesc"});
			BeanDecoratorExecutor.populates(list, decorators);
		}
		Map<String, Object> dataMap = super.wrapQueryResult((List<T>) list);
		result.put("data", dataMap);
		result.put("message", "");
		result.put("responseid", 1);
		return result;
	}
	
	/**
	 * 重载父类，加载流程相关其他数据
	 */
	@Override
    @ApiOperation(value = "查询流程详情", httpMethod = "POST", notes = "查询流程详情",
            produces="application/json",consumes="application/application/x-www-form-urlencoded")
	public Map<String, Object> get(PK id) throws Exception {
		Map<String, Object> map = Maps.newHashMap();
		T o = getService().getById(id);
		service.loadRelationData(o);
		map.put("message", o != null ? "":"操作失败!");
		map.put("responseid", o != null ? 1:0);
		map.put("data", o != null ? o:null);
		return map;
	}
	
	/**
	 * 重载父类，只更新业务流程时，不更改任何流程相关数据
	 */
	@Override
    @ApiOperation(value = "修改流程详情", httpMethod = "POST", notes = "修改流程详情",
            produces="application/json",consumes="application/application/x-www-form-urlencoded")
	public Map<String, Object> update(T o) throws Exception {
		Map<String, Object> map = Maps.newHashMap();
		try {
			@SuppressWarnings("unchecked")
			T dbData = getService().getById((PK) o.getId());
			BeanUtils.copyProperties(o, dbData, "iscg", "code", "processTypeId", "processHeaderId", "processStepId", "processStepCode", "subjects", "subjectType");
			int ret = getService().update(dbData);
			map.put("message", ret > 0 ? "操作成功!":"操作失败!");
			map.put("responseid", ret > 0 ? 1:ret);
			map.put("data", o);
		}catch (NullPointerException e) {
			map.put("responseid", 0);
			map.put("message", "必填数据不允许为空: "+ExceptionsMsgAlert.alertDuplicateKeyException(persistentClass, Exceptions.getStackTraceAsString(e)));
			log.error("ProcessAbstractController catch NullPointerException during update: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (DuplicateKeyException e) {
			map.put("responseid", 0);
			map.put("message", "唯一性字段不能为空或重复:"+ExceptionsMsgAlert.alertDuplicateKeyException(persistentClass, Exceptions.getStackTraceAsString(e)));
			log.error("ProcessAbstractController catch DuplicateKeyException during update: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (DataIntegrityViolationException e) {
			map.put("responseid", 0);
			map.put("message", "请检查约束条件:"+ExceptionsMsgAlert.alertDataIntegrityViolationException(persistentClass, Exceptions.getStackTraceAsString(e)));
			log.error("ProcessAbstractController catch DataIntegrityViolationException during update: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (TransactionRollbackException e) {
			map.put("responseid", 0);
			map.put("message", e.getErrorMessage());
			log.error("ProcessAbstractController catch TransactionRollbackException during update:"+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (AppException e) {
			map.put("responseid", 0);
			map.put("message", "数据异常，请确认后重新尝试!");
			log.error("ProcessAbstractController catch AppException during update: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (Exception e) {
			map.put("responseid", 0);
			map.put("message", "数据异常，请确认后重新尝试!");
			log.error("ProcessAbstractController catch Exception during update: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}
		return map;
	}
	
	/**
	 * 重载父类，不支持直接创建业务数据
	 */
	@Override
    @ApiOperation(value = "不可直接新增流程", httpMethod = "POST", notes = "不可直接新增流程")
	public Map<String, Object> create(T o) throws Exception {
		Map<String, Object> map = Maps.newHashMap();
		map.put("responseid", 0);
		map.put("message", "不支持此项操作!");
		return map;
	}
	
	/**
	 * 重载父类，撤销申请时，删除业务数据及流程相关信息
	 */
	@Override
    @ApiOperation(value = "删除流程详情", httpMethod = "POST", notes = "撤销申请时，删除业务数据及流程相关信息",
            produces="application/json",consumes="application/application/x-www-form-urlencoded")
	public Map<String, Object> delete(@RequestParam("id") PK id) throws Exception{
		Map<String, Object> map = Maps.newHashMap();
		try {
			int ret =service.deleteApply(id);
			map.put("message", ret>0  ? "操作成功!":"操作失败!"); //返回值兼容批量更新
			map.put("responseid", ret>0 ? 1:ret);
		}catch (NullPointerException e) {
			map.put("responseid", 0);
			map.put("message", "必填数据不允许为空: "+ExceptionsMsgAlert.alertDuplicateKeyException(persistentClass, Exceptions.getStackTraceAsString(e)));
			log.error("ProcessAbstractController catch NullPointerException during delete: "+id);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (DataIntegrityViolationException e) {
			map.put("responseid", 0);
			map.put("message", "请检查约束条件:"+ExceptionsMsgAlert.alertDataIntegrityViolationException(persistentClass, Exceptions.getStackTraceAsString(e)));
			log.error("ProcessAbstractController catch DataIntegrityViolationException during delete: "+id);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (TransactionRollbackException e) {
			map.put("responseid", 0);
			map.put("message", e.getErrorMessage());
			log.error("ProcessAbstractController catch TransactionRollbackException during delete:"+id);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (AppException e) {
			map.put("responseid", 0);
			map.put("message", "数据异常，请确认后重新尝试!");
			log.error("ProcessAbstractController catch AppException during delete: "+id);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (Exception e) {
			map.put("responseid", 0);
			map.put("message", "数据异常，请确认后重新尝试!");
			log.error("ProcessAbstractController catch Exception during delete: "+id);
			log.error(Exceptions.getStackTraceAsString(e));
		}
		return map;
	}
	
	@RequestMapping(value = "/deleteDraft", method = RequestMethod.POST)
	@ResponseBody
	@LogAudit
    @ApiOperation(value = "删除草稿", httpMethod = "POST", notes = "删除草稿",
            produces="application/json",consumes="application/application/x-www-form-urlencoded")
	public Map<String, Object> deleteDraft(@RequestParam("id") PK id) throws Exception{
		Map<String, Object> map = Maps.newHashMap();
		try {
			int ret =service.deleteDraft(id);
			map.put("message", ret>0  ? "操作成功!":"操作失败!"); //返回值兼容批量更新
			map.put("responseid", ret>0 ? 1:ret);
		}catch (NullPointerException e) {
			map.put("responseid", 0);
			map.put("message", "必填数据不允许为空: "+ExceptionsMsgAlert.alertDuplicateKeyException(persistentClass, Exceptions.getStackTraceAsString(e)));
			log.error("ProcessAbstractController catch NullPointerException during delete: "+id);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (DataIntegrityViolationException e) {
			map.put("responseid", 0);
			map.put("message", "请检查约束条件:"+ExceptionsMsgAlert.alertDataIntegrityViolationException(persistentClass, Exceptions.getStackTraceAsString(e)));
			log.error("ProcessAbstractController catch DataIntegrityViolationException during delete: "+id);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (TransactionRollbackException e) {
			map.put("responseid", 0);
			map.put("message", e.getErrorMessage());
			log.error("ProcessAbstractController catch TransactionRollbackException during delete:"+id);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (AppException e) {
			map.put("responseid", 0);
			map.put("message", "数据异常，请确认后重新尝试!");
			log.error("ProcessAbstractController catch AppException during delete: "+id);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (Exception e) {
			map.put("responseid", 0);
			map.put("message", "数据异常，请确认后重新尝试!");
			log.error("ProcessAbstractController catch Exception during delete: "+id);
			log.error(Exceptions.getStackTraceAsString(e));
		}
		return map;
	}
	
	/**
	 * 选择审批人信息(下拉框形式)
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getAuditUsers", method = RequestMethod.POST)
	@ResponseBody
    @ApiOperation(value = "获取待办审批人", httpMethod = "POST", notes = "获取待办审批人",
            produces="application/json",consumes="application/application/x-www-form-urlencoded")
	public Map<String, Object> getAuditUsers(@RequestParam("id")PK id) throws Exception {
		Map<String, Object> result = Maps.newHashMap();
		Collection<KeyValue<Integer,String>> optionsList = service.getAuditUsers(id);
		Map<String, Object> dataMap = super.wrapQueryResult((List<?>) optionsList);
		result.put("data", dataMap);
		result.put("message", "");
		result.put("responseid", 1);
		return result;
	}
	
	/**
	 * 选择审批人信息(树形菜单形式)
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getAuditUsersTree", method = RequestMethod.POST)
	@ResponseBody
    @ApiOperation(value = "选择审批人信息", httpMethod = "POST", notes = "选择审批人信息(树形菜单形式)",
            produces="application/json",consumes="application/application/x-www-form-urlencoded")
	public Map<String, Object> getAuditUsersTree(@RequestParam("id")PK id) throws Exception {
		return service.getAuditUsersTree(id);
	}

	public void setService(IProcessService<T, PK> service) {
		this.service = service;
		super.setService(service);
	}

	/**
	 * @return the service
	 */
	public IProcessService<T, PK> getService() {
		return service;
	}
	
	
}
