package com.fijo.cores.admin.authority.web;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.fijo.cores.shiro.AppUserSession;
import com.fijo.cores.utils.Constants;
import com.fijo.cores.utils.annotations.LogAudit;
import com.fijo.cores.utils.editors.SysPermissionEditor;
import com.fijo.cores.web.BaseController;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.fijo.cores.admin.authority.model.ShiroUser;
import com.fijo.cores.admin.authority.model.SysPermission;
import com.fijo.cores.admin.authority.service.ISysPermissionAdvanceService;
import com.fijo.cores.shiro.AppUserSession;
import com.fijo.cores.utils.Constants;
import com.fijo.cores.utils.annotations.LogAudit;
import com.fijo.cores.utils.editors.SysPermissionEditor;
import com.fijo.cores.web.BaseController;

@Controller
@RequestMapping(value = {"/action/sso/admin/authority/syspermission", //SSO跳转，Shrio不拦截
"/action/admin/authority/syspermission"}) //后台管理跳转，Shrio拦截校验权限
public class SysPermissionController extends BaseController<SysPermission, Integer> {
	public final Log log = LogFactory.getLog(SysPermissionController.class);

	@Autowired
	private AppUserSession appUserSession;
	
	@Autowired
	private ISysPermissionAdvanceService sysPermissionAdvanceService;

	public SysPermissionController() {
		super(SysPermission.class, "/action/admin/authority/syspermission/sysPermissionList","/action/admin/authority/syspermission/sysPermissionForm");
	}
	
	@PostConstruct
	private void initService() {
		setService(sysPermissionAdvanceService);
	}
	
	@InitBinder
	public void initBinder(ServletRequestDataBinder binder) {
		super.initBinder(binder);
		binder.registerCustomEditor(SysPermission.class, new SysPermissionEditor(sysPermissionAdvanceService));
	}
	
	@RequiresPermissions("admin:authority:syspermission:query")
	@RequestMapping(value = "/sysPermissionList", method = RequestMethod.GET)
	public ModelAndView openListView(Date ssDate, Date eeDate) throws Exception {
		return super.openListView(ssDate, eeDate);
	}

	@RequiresPermissions(value={"admin:authority:syspermission:create","admin:authority:syspermission:update"},logical=Logical.OR)
	@RequestMapping(value = "/sysPermissionForm", method = RequestMethod.GET)
	public ModelAndView openFormView(Date ssDate, Date eeDate) throws Exception {
		return super.openFormView(ssDate, eeDate);
	}
	
	@RequiresPermissions("admin:authority:syspermission:query")
	@RequestMapping(value = "/get", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	@ResponseBody
	@LogAudit
    @ApiOperation(value = "获取资源权限", httpMethod = "POST", notes = "获取资源权限", response = Map.class,
            produces="application/json",consumes="application/json")
	public Map<String, Object> get(@RequestBody SysPermission o) throws Exception {
		return super.get(o.getId());
	}
	
	@RequiresPermissions("admin:authority:syspermission:query")
	@RequestMapping(value = "/query", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	@ResponseBody
	@LogAudit
	@Override
    @ApiOperation(value = "查询资源权限", httpMethod = "POST", response = Map.class, notes = "获取多个资源权限",
            produces="application/json",consumes="application/json")
	public Map<String, Object> query(@RequestBody SysPermission o) throws Exception {
		Map<String, Object> result = Maps.newHashMap();
		Collection<SysPermission> list = sysPermissionAdvanceService.getAll(o);
		Map<String, Object> dataMap = super.wrapQueryResult((List<SysPermission>) list);
		result.put("data", dataMap);
		result.put("message", "");
		result.put("responseid", 1);
		return result;
	}

	@RequiresPermissions("admin:authority:syspermission:create")
	@RequestMapping(value = "/create", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	@ResponseBody
	@LogAudit
	@Override
    @ApiOperation(value = "创建资源权限", httpMethod = "POST", response = Map.class, notes = "创建资源权限",
            produces="application/json",consumes="application/json")
	public Map<String, Object> create(@RequestBody SysPermission o) throws Exception {
		SecurityUtils.getSubject().checkRole("Supervisor");
		if(o.getParentId() != null){
			o.setParent(new SysPermission(o.getParentId()));
		}
		Map<String, Object> map = super.create(o);
		map.put("data", sysPermissionAdvanceService.getById(o.getId()));		
		return map;
	}

	@RequiresPermissions("admin:authority:syspermission:update")
	@RequestMapping(value = "/update", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	@ResponseBody
	@LogAudit
	@Override
    @ApiOperation(value = "更新资源权限", httpMethod = "POST", response = Map.class, notes = "更新资源权限",
            produces="application/json",consumes="application/json")
	public Map<String, Object> update(@RequestBody SysPermission o) throws Exception {
		SecurityUtils.getSubject().checkRole("Supervisor");
		Map<String, Object> map = Maps.newHashMap();
		if(o.getId().equals(o.getParentId())){
			map.put("message", "操作失败: 上级权限不允许是自身！");
			map.put("responseid", 0);
		}else{
			if(o.getParentId() != null){
				o.setParent(new SysPermission(o.getParentId()));
			}
			map = super.update(o);
			map.put("data", sysPermissionAdvanceService.getById(o.getId()));
		}
		return map;
	}

	@RequiresPermissions("admin:authority:syspermission:delete")
	@RequestMapping(value = "/delete", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	@ResponseBody
	@LogAudit
    @ApiOperation(value = "删除资源权限", httpMethod = "POST", response = Map.class, notes = "通过主键删除资源权限",
            produces="application/json",consumes="application/json")
	public Map<String, Object> delete(@RequestBody SysPermission o) throws Exception {
		SecurityUtils.getSubject().checkRole("Supervisor");
		Map<String, Object> map = super.delete(o.getId());
		return map;
	}
	
	@RequiresPermissions("admin:authority:syspermission:delete")
	@RequestMapping(value = "/deletes", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	@ResponseBody
	@LogAudit
	@Override
    @ApiOperation(value = "删除资源权限", httpMethod = "POST", response = Map.class, notes = "通过主键数组删除资源权限",
            produces="application/json",consumes="application/json")
	public Map<String, Object> deletes(@RequestBody Integer[] ids) throws Exception {
		SecurityUtils.getSubject().checkRole("Supervisor");
		return super.deletes(ids);
	}

	@RequestMapping(value = "/getSysPermission", method = RequestMethod.GET)
	@ResponseBody
    @ApiOperation(value = "获取资源权限下拉框", httpMethod = "GET", response = Map.class, notes = "获取资源权限下拉框",
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public Map<Integer, String> getSysPermission() throws Exception {
		Map<Integer, String> map = Maps.newLinkedHashMap();
		Collection<SysPermission> list = sysPermissionAdvanceService.getAll();
		for(SysPermission o : list){
			map.put(o.getId(), o.getDescription());
		}
		return map;
	}	
	
	@RequestMapping(value = "/getSysPermissionByRole", method = RequestMethod.POST)
	@ResponseBody
    @ApiOperation(value = "获取资源权限下拉框", httpMethod = "POST", response = Map.class, notes = "通过角色ID获取资源权限下拉框",
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public Map<String, String> getSysPermissionByRole(@RequestParam("id") Integer roleId) throws Exception {
		Map<String, String> map = Maps.newHashMap();
		StringBuffer sb = new StringBuffer();
		List<SysPermission> list = sysPermissionAdvanceService.getByRole(roleId);
		for(SysPermission p: list){
			sb.append(p.getId()+Constants.SPACE);
		}
		map.put("ids", sb.toString());
		return map;
	}
	
	@RequestMapping(value = "/getMenu")
	@ResponseBody
    @ApiOperation(value = "获取资源权限下拉框", httpMethod = "GET", response = Map.class, notes = "获取登陆人权限菜单",
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public Map<String, Object> getMenu() throws Exception {
		ShiroUser user = appUserSession.getCurrentUser();	
		Map<String, Object> result = Maps.newHashMap();
		Map<String, Object> dataMap = Maps.newHashMap();
		List<SysPermission> list = sysPermissionAdvanceService.getMenu(user.getUserId());
		dataMap.put("shiroUser", user);
		dataMap.put("Datas", list);
		result.put("data", dataMap);
		result.put("message", "");
		result.put("responseid", 1);
		return result;
	}	
	
}
