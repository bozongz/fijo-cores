package com.fijo.cores.admin.authority.web;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import com.fijo.cores.admin.authority.model.SysRole;
import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.utils.Constants;
import com.fijo.cores.utils.annotations.LogAudit;
import com.fijo.cores.web.BaseController;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.fijo.cores.admin.authority.model.SysRole;
import com.fijo.cores.admin.authority.service.ISysRoleAdvanceService;
import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.utils.Constants;
import com.fijo.cores.utils.annotations.LogAudit;
import com.fijo.cores.web.BaseController;

@Controller
@RequestMapping(value = {"/action/sso/admin/authority/sysrole", //SSO跳转，Shrio不拦截
"/action/admin/authority/sysrole"}) //后台管理跳转，Shrio拦截校验权限
public class SysRoleController extends BaseController<SysRole, Integer> {
	public final Log log = LogFactory.getLog(SysRoleController.class);

	@Autowired
	private ISysRoleAdvanceService sysRoleAdvanceService;
	
	public SysRoleController() {
		super(SysRole.class, "/action/admin/authority/sysrole/sysRoleList", "/action/admin/authority/sysrole/sysRoleForm");
	}
	
	@PostConstruct
	private void initService() {
		setService(sysRoleAdvanceService);
	}
	
	@RequiresPermissions("admin:authority:sysrole:query")
	@RequestMapping(value = "/sysRoleList", method = RequestMethod.GET)
	public ModelAndView openListView(Date ssDate, Date eeDate) throws Exception {
		return super.openListView(ssDate, eeDate);
	}

	@RequiresPermissions(value={"admin:authority:sysrole:create","admin:authority:sysrole:update"},logical=Logical.OR)
	@RequestMapping(value = "/sysRoleForm", method = RequestMethod.GET)
	public ModelAndView openFormView(Date ssDate, Date eeDate) throws Exception {
		return super.openFormView(ssDate, eeDate);
	}
	
	@RequiresPermissions("admin:authority:sysrole:query")
	@RequestMapping(value = "/get", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	@ResponseBody
	@LogAudit
    @ApiOperation(value = "获取角色", httpMethod = "POST", notes = "获取角色", response = Map.class,
            produces="application/json",consumes="application/json")
	public Map<String, Object> get(@RequestBody SysRole o) throws Exception {
		return super.get(o.getId());
	}
	
	@RequiresPermissions("admin:authority:sysrole:query")
	@RequestMapping(value = "/query", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	@ResponseBody
	@LogAudit
	@Override
    @ApiOperation(value = "查询角色", httpMethod = "POST", response = Map.class, notes = "获取多个角色",
            produces="application/json",consumes="application/json")
	public Map<String, Object> query(@RequestBody SysRole o) throws Exception {
		Map<String, Object> result = Maps.newHashMap();		
		Collection<SysRole> list = sysRoleAdvanceService.getAll(o);
		Map<String, Object> dataMap = super.wrapQueryResult((List<SysRole>) list);
		result.put("data", dataMap);
		result.put("message", "");
		result.put("responseid", 1);
		return result;
	}
	
	@RequiresPermissions("admin:authority:sysrole:create")
	@RequiresRoles(value={"Administrator","Supervisor"},logical=Logical.OR)
	@RequestMapping(value = "/create", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	@ResponseBody
	@LogAudit
	@Override
    @ApiOperation(value = "创建角色", httpMethod = "POST", response = Map.class, notes = "创建角色",
            produces="application/json",consumes="application/json")
	public Map<String, Object> create(@RequestBody SysRole o) throws Exception {
		return super.create(o);		
	}

	@RequiresPermissions("admin:authority:sysrole:update")
	@RequiresRoles(value={"Administrator","Supervisor"},logical=Logical.OR)
	@RequestMapping(value = "/update", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	@ResponseBody
	@LogAudit
	@Override
    @ApiOperation(value = "更新角色", httpMethod = "POST", response = Map.class, notes = "更新角色",
            produces="application/json",consumes="application/json")
	public Map<String, Object> update(@RequestBody SysRole o) throws Exception {
		return super.update(o);
	}

	@RequiresPermissions("admin:authority:sysrole:delete")
	@RequiresRoles(value={"Administrator","Supervisor"},logical=Logical.OR)
	@RequestMapping(value = "/delete", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	@ResponseBody
	@LogAudit
    @ApiOperation(value = "删除角色", httpMethod = "POST", response = Map.class, notes = "通过主键删除角色",
            produces="application/json",consumes="application/json")
	public Map<String, Object> delete(@RequestBody SysRole o) throws Exception {
		return super.delete(o.getId());
	}
	
	@RequiresPermissions("admin:authority:sysrole:delete")
	@RequiresRoles(value={"Administrator","Supervisor"},logical=Logical.OR)
	@RequestMapping(value = "/deletes", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	@ResponseBody
	@LogAudit
	@Override
    @ApiOperation(value = "删除角色", httpMethod = "POST", response = Map.class, notes = "通过主键数组删除角色",
            produces="application/json",consumes="application/json")
	public Map<String, Object> deletes(@RequestBody Integer[] ids) throws Exception {
		return super.deletes(ids);
	}
	
	/**
	 * 加载角色列表(按角色分别对人员和权限资源关联，从而完成授权。没有考虑人员直接对权限资源授权)
	 * @return
	 */
	@RequiresPermissions("admin:authority:sysrole:query")
	@RequestMapping(value = "/rolePermission/querySysRole", method = RequestMethod.GET)
	@ResponseBody
	@LogAudit
    @ApiOperation(value = "加载角色列表", httpMethod = "GET", response = Map.class, notes = "加载角色列表",
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public Map<Integer, String> querySysRole() {
		Map<Integer, String> maps = Maps.newLinkedHashMap();
		Collection<SysRole> rolelists = sysRoleAdvanceService.getValues();
		for (SysRole role : rolelists) {
			maps.put(role.getId(), role.getDescription());
		}
		return maps;
	}

	/**
	 * 角色资源授权
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions(value={"admin:authority:sysrole:create","admin:authority:sysrole:update"},logical=Logical.OR)
	@RequestMapping(value = "/rolePermissionView", method = RequestMethod.GET)
    @ApiOperation(value = "打开角色资源授权页面", httpMethod = "GET", response = Map.class, notes = "打开角色资源授权页面",
            consumes="application/x-www-form-urlencoded")
	public ModelAndView rolePermissionView() throws Exception {
		return new ModelAndView("/action/admin/authority/sysrole/rolePermissionView");
	}
	
	@RequiresPermissions("admin:authority:sysrole:query")
	@RequestMapping(value = "/querySysRoleByUser", method = RequestMethod.POST)
	@ResponseBody
    @ApiOperation(value = "通过用户Id获取角色，空格分隔", httpMethod = "POST", response = Map.class, notes = "通过用户Id获取角色，空格分隔",
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public Map<String, String> querySysRoleByUser(@ApiParam(required=true, value="用户Id")@RequestParam("id") Integer userId) throws Exception {
		Map<String, String> map = Maps.newHashMap();
		StringBuffer sb = new StringBuffer();
		List<SysRole> list = sysRoleAdvanceService.getByUser(userId);
		for(SysRole p: list){
			sb.append(p.getId()+Constants.SPACE);
		}
		map.put("ids", sb.toString());
		return map;
	}
	
	/**
	 * 构造权限树(按角色对人员和权限授权，没有考虑人员直接对权限授权)
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions("admin:authority:sysrole:query")
	@RequestMapping(value = "/rolePermission/getPermissionsTreeData/{roleId}", method = RequestMethod.GET)
	@ResponseBody
    @ApiOperation(value = "构造权限树", httpMethod = "GET", response = Map.class, notes = "构造权限树",
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public Map<String, Object> getPermissionsTreeData(@ApiParam(required=true, value="角色Id")@PathVariable("roleId") Integer roleId) throws Exception {
		return sysRoleAdvanceService.getPermissionsTreeData(roleId);
	}

	/**
	 * 提交保存权限树
	 * @param roleId 角色Id
	 * @param permissionIds 资源权限Id
	 * @return
	 */
	@RequiresPermissions("admin:authority:sysuser:userRole:update")
	@RequiresRoles(value={"Administrator","Supervisor"},logical=Logical.OR)
	@RequestMapping(value = "/rolePermission/submit", method = RequestMethod.POST)
	@ResponseBody
    @ApiOperation(value = "提交保存权限", httpMethod = "POST", response = Map.class, notes = "提交保存权限",
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public Map<String, Object> submit(@ApiParam(required=true, value="角色Id")@RequestParam("roleId") Integer roleId,
                                      @ApiParam(required=true, value="权限Id数组")@RequestParam("permissionIds") String permissionIds) {
		Map<String, Object> map = Maps.newHashMap();
		try {
			if (roleId == null) { // 删除角色所有权限
				map.put("responseid", 0);
				map.put("message", "请选择有效角色!");
			} else {
                sysRoleAdvanceService.clearCacheHolder();
				if(StringUtils.isEmpty(permissionIds)){
					sysRoleAdvanceService.deleteSysRolePermissionByRoleId(roleId); // 根据roleId删除该角色下面的所有权限
				}else{
					String[] permissionStrings = permissionIds.split(Constants.SPACE);
					if (permissionStrings != null && permissionStrings.length > 0) {
						Set<String> permissionSet = new HashSet<String>();
						sysRoleAdvanceService.deleteSysRolePermissionByRoleId(roleId); //根据角色删除所有权限
						for (String add : permissionStrings) {
							permissionSet.add(add); //使用Set接口过滤去重权限
						}
						for (String permission : permissionSet) {
							sysRoleAdvanceService.createSysRolePermission(roleId, Integer.parseInt(permission));
						}
					}
				}
			}
			map.put("responseid", 1);
			map.put("message", "保存角色权限成功!");
		} catch (Exception e) {
			map.put("responseid", 0);
			map.put("message", "保存角色权限失败!");
			log.error(Exceptions.getStackTraceAsString(e));
		}
		return map;
	}

}
