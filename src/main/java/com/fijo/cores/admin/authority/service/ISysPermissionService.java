package com.fijo.cores.admin.authority.service;
import java.util.List;

import com.fijo.cores.admin.authority.model.SysPermission;
import com.fijo.cores.service.IGenericService;
import com.fijo.cores.admin.authority.model.SysPermission;
import com.fijo.cores.service.IGenericService;



public interface ISysPermissionService extends IGenericService<SysPermission,Integer> {
	
	SysPermission getRoot();
	
	List<SysPermission> getExcludeRoot();	
	
	List<SysPermission> getByRole(Integer roleId);	

	/**
	 * 系统扩展 基于用户-资源直接授权时使用
	 * @param userId
	 * @return
	 */
	List<SysPermission> getSysUserPermission(Integer userId);	

	int deleteSysUserPermissionByUserId(Integer userId);
	
	int createSysUserPermission(Integer userId, Integer permissionId);
	
	List<SysPermission> getByParent(Integer id);
	
	List<SysPermission> getByMoudle(String loginName);
	
	List<SysPermission> getByParentMenu(Integer parentId, String loginName);
	
	List<SysPermission> getMenu(Integer userId);
}
