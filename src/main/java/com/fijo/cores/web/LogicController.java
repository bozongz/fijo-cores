/**
 * 
 */
package com.fijo.cores.web;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fijo.cores.exceptions.AppException;
import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.exceptions.ExceptionsMsgAlert;
import com.fijo.cores.exceptions.TransactionRollbackException;
import com.fijo.cores.service.ILogicService;
import com.fijo.cores.shiro.AppUserSession;
import com.fijo.cores.utils.annotations.LogAudit;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.fijo.cores.exceptions.AppException;
import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.exceptions.ExceptionsMsgAlert;
import com.fijo.cores.exceptions.TransactionRollbackException;
import com.fijo.cores.model.LogicModel;
import com.fijo.cores.service.ILogicService;
import com.fijo.cores.shiro.AppUserSession;
import com.fijo.cores.utils.annotations.LogAudit;
import org.springframework.web.servlet.ModelAndView;

/**
 * 逻辑实体控制层
 * 
 * @author zhangbo
 *
 */
public class LogicController<T extends LogicModel<T>, PK extends Serializable>
		extends BaseController<T, PK> {


	@Autowired
	private AppUserSession appUserSession;
	
	/**
	 * 
	 * @param persistentClass 持久化对象
	 * @param listPage 列表页面
	 * @param formPage 表单页面
	 */
	public LogicController(Class<T> persistentClass, String listPage, String formPage) {
		super(persistentClass, listPage, formPage);
	}

	@RequestMapping(value = "/updateEnable", method = RequestMethod.POST)
	@ResponseBody
	@LogAudit
    @ApiOperation(value = "逻辑更新实体可用性", httpMethod = "POST", notes = "逻辑更新实体可用性", response = Map.class,
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public Map<String, Object> updateEnable(boolean enabled, @RequestParam("ids") List<PK> ids) throws Exception {
		Map<String, Object> map = Maps.newHashMap();
		try {			 
			int ret = ((ILogicService<?, PK>)getService()).updateEnable(enabled, ids);
			map.put("message", ret > 0  ? "操作成功!":"操作失败!"); //返回值兼容批量更新
			map.put("responseid", ret > 0 ? 1:ret);
		}catch (NullPointerException e) {
			map.put("responseid", 0);
			map.put("message", "唯一性字段不能为空或重复:"+ExceptionsMsgAlert.alertDuplicateKeyException(persistentClass, Exceptions.getStackTraceAsString(e)));
			log.error("LogicController catch NullPointerException during updateEnable: "+ids);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (DataIntegrityViolationException e) {
			map.put("responseid", 0);
			map.put("message", "请检查约束条件:"+ExceptionsMsgAlert.alertDataIntegrityViolationException(persistentClass, Exceptions.getStackTraceAsString(e)));
			log.error("LogicController catch DataIntegrityViolationException during updateEnable: "+ids);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (TransactionRollbackException e) {
			map.put("responseid", 0);
			map.put("message", "提交事务异常!");
			log.error("LogicController catch TransactionRollbackException during create: "+ids);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (AppException e) {
			map.put("responseid", 0);
			map.put("message", "数据异常，请确认后重新尝试!");
			log.error("LogicController catch AppException during updateEnable: "+ids);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (Exception e) {
			map.put("responseid", 0);
			map.put("message", "数据异常，请确认后重新尝试!");
			log.error("LogicController catch Exception during updateEnable: "+ids);
			log.error(Exceptions.getStackTraceAsString(e));
		}
		return map;
	}
	
	@Override
    @ApiOperation(value = "逻辑更新实体", httpMethod = "POST", notes = "逻辑更新实体", response = Map.class,
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public Map<String, Object> update(@ApiParam(required=true, value="实体表单")T o) throws Exception {
		if(!o.validate()){
			Map<String, Object> map = Maps.newHashMap();
			map.put("responseid", 0);
			map.put("message", "请先启用该数据，再进行修改!");
			return map;
		}
		else{
			return super.update(o);
		}
	}
}
