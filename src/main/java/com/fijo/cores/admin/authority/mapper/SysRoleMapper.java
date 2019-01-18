package com.fijo.cores.admin.authority.mapper;

import java.util.List;

import com.fijo.cores.admin.authority.model.SysRole;
import com.fijo.cores.mapper.ISystemMapper;
import org.apache.ibatis.annotations.Param;

import com.fijo.cores.admin.authority.model.SysRole;
import com.fijo.cores.mapper.ISystemMapper;


public interface SysRoleMapper extends ISystemMapper<SysRole,Integer> {
	
	/**
	 * 用户角色授权时，根据角色，删除角色与用户关联表
	 * @param roleId
	 * @return
	 */
	int deleteSysUserRoleByRoleId(@Param(value = "roleId")Integer roleId);
	
	int deleteSysUserRoleByUserId(@Param(value = "userId")Integer userId);
	
	/**
	 * 用户角色授权时，根据角色和用户，添加角色与用户关联表
	 * @param userId
	 * @param roleId
	 * @return
	 */
	int createSysUserRole(@Param(value = "userId")Integer userId,@Param(value = "roleId")Integer roleId);
	
	/**
	 * 角色资源授权时，根据角色，删除角色与资源关联表
	 * @param roleId
	 * @return
	 */
	int deleteSysRolePermissionByRoleId(@Param(value = "roleId")Integer roleId);
	
	/**
	 * 角色资源授权时，根据角色和资源，添加角色与资源关联表
	 * @param roleId
	 * @param permissionId
	 * @return
	 */
	int createSysRolePermission(@Param(value = "roleId")Integer roleId,@Param(value = "permissionId")Integer permissionId);
	
	List<SysRole> getByUser(@Param(value = "userId")Integer userId);
}
