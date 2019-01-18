package com.fijo.cores.admin.authority.service;
import java.util.List;

import com.fijo.cores.admin.authority.model.SysRole;
import com.fijo.cores.service.IGenericService;
import com.fijo.cores.admin.authority.model.SysRole;
import com.fijo.cores.service.IGenericService;



public interface ISysRoleService extends IGenericService<SysRole,Integer> {
	
	/**
	 * 用户角色授权时，根据角色，删除角色与用户关联表
	 * @param roleId
	 * @return
	 */
	int deleteSysUserRoleByRoleId(Integer roleId);
	
	int deleteSysUserRoleByUserId(Integer userId);
	
	/**
	 * 用户角色授权时，根据角色和用户，添加角色与用户关联表
	 * @param userId
	 * @param roleId
	 * @return
	 */
	int createSysUserRole(Integer userId,Integer roleId);
	
	/**
	 * 角色资源授权时，根据角色，删除角色与资源关联表
	 * @param roleId
	 * @return
	 */
	int deleteSysRolePermissionByRoleId(Integer roleId);
	
	/**
	 * 角色资源授权时，根据角色和资源，添加角色与资源关联表
	 * @param roleId
	 * @param permissionId
	 * @return
	 */
	int createSysRolePermission(Integer roleId,Integer permissionId);
	
	List<SysRole> getByUser(Integer userId);
}
