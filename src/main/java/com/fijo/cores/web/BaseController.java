/**
 * 
 */
package com.fijo.cores.web;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fijo.cores.exceptions.AppException;
import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.exceptions.ExceptionsMsgAlert;
import com.fijo.cores.exceptions.TransactionRollbackException;
import com.fijo.cores.model.JsonResponse;
import com.fijo.cores.service.IGenericService;
import com.fijo.cores.utils.DateUtil;
import com.fijo.cores.utils.ObjectUtil;
import com.fijo.cores.utils.annotations.LogAudit;
import com.fijo.cores.utils.configs.CoreConfig;
import com.fijo.cores.utils.editors.DateEditor;
import com.fijo.cores.utils.editors.StringNullEditor;
import com.fijo.cores.utils.pages.PageSupport;
import com.fijo.cores.utils.pages.PageSupport;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.fijo.cores.exceptions.AppException;
import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.exceptions.ExceptionsMsgAlert;
import com.fijo.cores.exceptions.TransactionRollbackException;
import com.fijo.cores.model.GenericModel;
import com.fijo.cores.model.JsonResponse;
import com.fijo.cores.service.IGenericService;
import com.fijo.cores.utils.DateUtil;
import com.fijo.cores.utils.ObjectUtil;
import com.fijo.cores.utils.annotations.LogAudit;
import com.fijo.cores.utils.configs.CoreConfig;
import com.fijo.cores.utils.editors.DateEditor;
import com.fijo.cores.utils.editors.StringNullEditor;

/**
 * 基础实体控制层
 * 
 * @author zhangbo
 *
 */
public class BaseController<T extends GenericModel<T>, PK extends Serializable> {
	public transient final Log log = LogFactory.getLog(getClass());
	
	protected Class<T> persistentClass;
	protected String listPage;
	protected String formPage;
	
	private IGenericService<T, PK> service;

	@Autowired
	protected CoreConfig config;
	
	/**
	 * 
	 * @param persistentClass 持久化对象
	 * @param listPage 列表页面
	 * @param formPage 表单页面
	 */
	public BaseController(Class<T> persistentClass, String listPage, String formPage) {
		super();
		this.persistentClass = persistentClass;
		this.listPage = listPage;
		this.formPage = formPage;
	}

	@InitBinder
	public void initBinder(ServletRequestDataBinder binder) {
		// bind empty strings as null
		binder.registerCustomEditor(String.class, new StringNullEditor());
		binder.registerCustomEditor(Date.class, new DateEditor());
	}

    @ApiOperation(value = "打开列表表单", httpMethod = "GET", notes = "打开列表表单", response = Map.class,
            consumes="application/x-www-form-urlencoded")
	public ModelAndView openListView(Date ssDate, Date eeDate) throws Exception {
		ModelAndView mav = new ModelAndView(listPage);
        mav.addObject("ssDate", ssDate==null?DateUtil.getCurrMonthFirstDay():DateUtil.getDate(ssDate));
      	mav.addObject("eeDate", eeDate==null?DateUtil.getNextMonthFirstDay():DateUtil.getDate(eeDate));
        return mav;
	}

    @ApiOperation(value = "打开编辑表单", httpMethod = "GET", notes = "打开编辑表单", response = Map.class,
            consumes="application/x-www-form-urlencoded")
	public ModelAndView openFormView(Date ssDate, Date eeDate) throws Exception {
		ModelAndView mav = new ModelAndView(formPage);
        mav.addObject("ssDate", ssDate==null?DateUtil.getCurrMonthFirstDay():DateUtil.getDate(ssDate));
      	mav.addObject("eeDate", eeDate==null?DateUtil.getNextMonthFirstDay():DateUtil.getDate(eeDate));
        return mav;
	}
	
	@RequestMapping(value = "/get", method = RequestMethod.POST)
	@ResponseBody
	@LogAudit
    @ApiOperation(value = "获取记录详情", httpMethod = "POST", notes = "获取记录详情", response = Map.class,
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public Map<String, Object> get(@ApiParam(required=true, value="实体主键")PK id) throws Exception {
		Map<String, Object> map = Maps.newHashMap();
		T o = getService().getById(id);
		map.put("message", o != null ? "":"没有记录!");
		map.put("responseid", o != null ? 1:0);
		map.put("data", o != null ? o:null);
		return map;
	}
	
	@RequestMapping(value = "/getOne", method = RequestMethod.POST)
	@ResponseBody
	@LogAudit
    @ApiOperation(value = "获取单个记录详情", httpMethod = "POST", notes = "获取记录详情", response = JsonResponse.class,
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public JsonResponse getOne(@ApiParam(required=true, value="实体表单传参")T param) throws Exception {
		JsonResponse response = new JsonResponse();
		T o = getService().getOne(param);
		response.setMessage(o != null ? "":"没有记录!");
		response.setResponseid(o != null ? 1:0);
		response.setData(o);
		return response;
	}
	
	@RequestMapping(value = "/query", method = RequestMethod.POST)
	@ResponseBody
	@LogAudit
    @ApiOperation(value = "查询记录列表", httpMethod = "POST", notes = "获取记录详情", response = Map.class,
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public Map<String, Object> query(@ApiParam(required=true, value="实体表单传参")T o) throws Exception {
		Collection<T> list = getService().getAll(o);
		Map<String, Object> dataMap = wrapQueryResult((List<T>) list);
		Map<String, Object> result = Maps.newHashMap();
		result.put("data", dataMap);
		result.put("message", list!=null&&list.size()>0 ? "":"没有记录!");
		result.put("responseid", 1);
		return result;
	}
	
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@ResponseBody
	@LogAudit
    @ApiOperation(value = "创建记录", httpMethod = "POST", notes = "创建记录", response = Map.class,
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public Map<String, Object> create(@ApiParam(required=true, value="实体表单传参")T o) throws Exception {
		Map<String, Object> map = Maps.newHashMap();
		try {
			int ret = getService().create(o);
			map.put("message", ret > 0 ? "操作成功!":"操作失败!");
			map.put("responseid", ret > 0 ? 1:ret);
			map.put("data", o);
		}catch (NullPointerException e) {
			map.put("responseid", 0);
			map.put("message", "必填数据不允许为空: "+ExceptionsMsgAlert.alertDuplicateKeyException(persistentClass, Exceptions.getStackTraceAsString(e)));
			log.error("BaseController catch NullPointerException during create: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (DuplicateKeyException e) {
			map.put("responseid", 0);
			map.put("message", "唯一性字段不能为空或重复:"+ExceptionsMsgAlert.alertDuplicateKeyException(persistentClass, Exceptions.getStackTraceAsString(e)));
			log.error("BaseController catch DuplicateKeyException during create: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (DataIntegrityViolationException e) {
			map.put("responseid", 0);
			map.put("message", "请检查约束条件:"+ExceptionsMsgAlert.alertDataIntegrityViolationException(persistentClass, Exceptions.getStackTraceAsString(e)));
			log.error("BaseController catch DataIntegrityViolationException during create: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (TransactionRollbackException e) {
			map.put("responseid", 0);
			map.put("message", "提交事务异常!");
			log.error("BaseController catch TransactionRollbackException during create: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}
		catch (AppException e) {
			map.put("responseid", 0);
			map.put("message", "数据异常，请确认后重新尝试!");
			log.error("BaseController catch AppException during create: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (Exception e) {
			map.put("responseid", 0);
			map.put("message", "数据异常，请确认后重新尝试!");
			log.error("BaseController catch Exception during create: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}
		return map;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@ResponseBody
	@LogAudit
    @ApiOperation(value = "更新记录", httpMethod = "POST", notes = "更新记录", response = Map.class,
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public Map<String, Object> update(@ApiParam(required=true, value="实体表单传参")T o) throws Exception {
		Map<String, Object> map = Maps.newHashMap();
		try {
			int ret = getService().update(o);
			map.put("message", ret > 0 ? "操作成功!":"操作失败!");
			map.put("responseid", ret > 0 ? 1:ret);
			o = getService().getById((PK) ObjectUtil.getIdVaue(o));
			map.put("data", o);
		}catch (NullPointerException e) {
			map.put("responseid", 0);
			map.put("message", "必填数据不允许为空: "+ExceptionsMsgAlert.alertDuplicateKeyException(persistentClass, Exceptions.getStackTraceAsString(e)));
			log.error("BaseController catch NullPointerException during update: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (DuplicateKeyException e) {
			map.put("responseid", 0);
			map.put("message", "唯一性字段不能为空或重复:"+ExceptionsMsgAlert.alertDuplicateKeyException(persistentClass, Exceptions.getStackTraceAsString(e)));
			log.error("BaseController catch DuplicateKeyException during update: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (DataIntegrityViolationException e) {
			map.put("responseid", 0);
			map.put("message", "请检查约束条件:"+ExceptionsMsgAlert.alertDataIntegrityViolationException(persistentClass, Exceptions.getStackTraceAsString(e)));
			log.error("BaseController catch DataIntegrityViolationException during update: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (TransactionRollbackException e) {
			map.put("responseid", 0);
			map.put("message", "提交事务异常!");
			log.error("BaseController catch TransactionRollbackException during update: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (AppException e) {
			map.put("responseid", 0);
			map.put("message", "数据异常，请确认后重新尝试!");
			log.error("BaseController catch AppException during update: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (Exception e) {
			map.put("responseid", 0);
			map.put("message", "数据异常，请确认后重新尝试!");
			log.error("BaseController catch Exception during update: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}
		return map;
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	@LogAudit
    @ApiOperation(value = "通过主键删除记录", httpMethod = "POST", notes = "通过主键删除记录", response = Map.class,
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public Map<String, Object> delete(@ApiParam(required=true, value="实体主键")@RequestParam("id") PK id) throws Exception {
		Map<String, Object> map = Maps.newHashMap();
		try {
			int ret = getService().delete(id);
			map.put("message", ret > 0  ? "操作成功!":"操作失败!"); //返回值兼容批量更新
			map.put("responseid", ret > 0 ? 1:ret);
		}catch (NullPointerException e) {
			map.put("responseid", 0);
			map.put("message", "必填数据不允许为空: "+ExceptionsMsgAlert.alertDuplicateKeyException(persistentClass, Exceptions.getStackTraceAsString(e)));
			log.error("BaseController catch NullPointerException during delete: "+id);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (DataIntegrityViolationException e) {
			map.put("responseid", 0);
			map.put("message", "请检查约束条件:"+ExceptionsMsgAlert.alertDataIntegrityViolationException(persistentClass, Exceptions.getStackTraceAsString(e)));
			log.error("BaseController catch DataIntegrityViolationException during delete: "+id);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (TransactionRollbackException e) {
			map.put("responseid", 0);
			map.put("message", "提交事务异常!");
			log.error("BaseController catch TransactionRollbackException during delete: "+id);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (AppException e) {
			map.put("responseid", 0);
			map.put("message", "数据异常，请确认后重新尝试!");
			log.error("BaseController catch AppException during delete: "+id);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (Exception e) {
			map.put("responseid", 0);
			map.put("message", "数据异常，请确认后重新尝试!");
			log.error("BaseController catch Exception during delete: "+id);
			log.error(Exceptions.getStackTraceAsString(e));
		}
		return map;
	}
	
	@RequestMapping(value = "/deleteObj", method = RequestMethod.POST)
	@ResponseBody
	@LogAudit
    @ApiOperation(value = "通过实体属性删除记录", httpMethod = "POST", notes = "通过实体属性删除记录", response = Map.class,
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public Map<String, Object> deleteObj(@ApiParam(required=true, value="实体表单传参")T o) throws Exception {
		Map<String, Object> map = Maps.newHashMap();
		try {
			int ret = getService().delete(o);
			map.put("message", ret > 0  ? "操作成功!":"操作失败!"); //返回值兼容批量更新
			map.put("responseid", ret > 0 ? 1:ret);
		}catch (NullPointerException e) {
			map.put("responseid", 0);
			map.put("message", "必填数据不允许为空: "+ExceptionsMsgAlert.alertDuplicateKeyException(persistentClass, Exceptions.getStackTraceAsString(e)));
			log.error("BaseController catch NullPointerException during delete: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (DataIntegrityViolationException e) {
			map.put("responseid", 0);
			map.put("message", "请检查约束条件:"+ExceptionsMsgAlert.alertDataIntegrityViolationException(persistentClass, Exceptions.getStackTraceAsString(e)));
			log.error("BaseController catch DataIntegrityViolationException during delete: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (TransactionRollbackException e) {
			map.put("responseid", 0);
			map.put("message", "提交事务异常!");
			log.error("BaseController catch TransactionRollbackException during delete: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (AppException e) {
			map.put("responseid", 0);
			map.put("message", "数据异常，请确认后重新尝试!");
			log.error("BaseController catch AppException during delete: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (Exception e) {
			map.put("responseid", 0);
			map.put("message", "数据异常，请确认后重新尝试!");
			log.error("BaseController catch Exception during delete: "+o);
			log.error(Exceptions.getStackTraceAsString(e));
		}
		return map;
	}
		
	@RequestMapping(value = "/deletes", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	@ResponseBody
	@LogAudit
    @ApiOperation(value = "通过主键数组删除记录", httpMethod = "POST", notes = "通过主键数组删除记录", response = Map.class,
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public Map<String, Object> deletes(@ApiParam(required=true, value="实体主键数组")@RequestBody PK[] ids) throws Exception {
		Map<String, Object> map = Maps.newHashMap();
		try {
			Set<PK> idSet = new HashSet<PK>(Arrays.asList(ids));
			int ret = getService().batchDelete(idSet);
			map.put("message", ret > 0  ? "操作成功!":"操作失败!"); //返回值兼容批量更新
			map.put("responseid", ret > 0 ? 1:ret);
		}catch (NullPointerException e) {
			map.put("responseid", 0);
			map.put("message", "必填数据不允许为空: "+ExceptionsMsgAlert.alertDuplicateKeyException(persistentClass, Exceptions.getStackTraceAsString(e)));
			log.error("BaseController catch NullPointerException during bactch delete: "+Arrays.toString(ids));
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (DataIntegrityViolationException e) {
			map.put("responseid", 0);
			map.put("message", "请检查约束条件:"+ExceptionsMsgAlert.alertDataIntegrityViolationException(persistentClass, Exceptions.getStackTraceAsString(e)));
			log.error("BaseController catch DataIntegrityViolationException during bactch delete: "+Arrays.toString(ids));
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (TransactionRollbackException e) {
			map.put("responseid", 0);
			map.put("message", "提交事务异常!");
			log.error("BaseController catch TransactionRollbackException during bactch delete: "+Arrays.toString(ids));
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (AppException e) {
			map.put("responseid", 0);
			map.put("message", "数据异常，请确认后重新尝试!");
			log.error("BaseController catch AppException during bactch delete: "+Arrays.toString(ids));
			log.error(Exceptions.getStackTraceAsString(e));
		}catch (Exception e) {
			map.put("responseid", 0);
			map.put("message", "数据异常，请确认后重新尝试!");
			log.error("BaseController catch Exception during bactch delete: "+Arrays.toString(ids));
			log.error(Exceptions.getStackTraceAsString(e));
		}
		return map;
	}
	
	/**
	 * 子类自定义返回JSON响应对象
	 * @param sourceMap
	 * @param destMap
	 */
	protected void customJsonResponse(Map<String, Object> retMap, String key, Object value){
		retMap.put(key, value);
	}

	/**
	 * @param service the service to set
	 */
	public void setService(IGenericService<T, PK> service) {
		this.service = service;
	}

	/**
	 * @return the service
	 */
	public IGenericService<T, PK> getService() {
		return service;
	}
	
	protected Map<String, Object> wrapQueryResult(List<?> list){
		Map<String, Object> dataMap = Maps.newHashMap();
		PageInfo info = new PageInfo(list);		
		if(config.getValue("js.framework").equals("zjs")){
			//前端没有传递pageindex和pagesize参数，GenericMapperService和GenericSQLService没有按照分页模式查询，因此info.getList()返回空值
			if(info.getList() == null){ 
				dataMap.put("TotalPages", ((int)list.size()/20)+1);
				dataMap.put("TotalRows", list.size());
				dataMap.put("Datas", list);
			}else{
				dataMap.put("TotalPages", info.getPages());
				dataMap.put("TotalRows", info.getTotal());
				dataMap.put("Datas", info.getList());
			}	
		}
		return dataMap;
	}

    protected Map<String, Object> wrapQueryResult(PageSupport<?> ps) {
        Map<String, Object> dataMap = Maps.newHashMap();
        if (config.getValue("js.framework").equals("zjs")) {
            dataMap.put("TotalPages", ps.getTotalPages());
            dataMap.put("TotalRows", ps.getTotalRecords());
            dataMap.put("Datas", ps.getItems());
        }
        return dataMap;
    }
}
