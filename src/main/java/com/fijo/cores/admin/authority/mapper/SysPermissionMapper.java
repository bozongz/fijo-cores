package com.fijo.cores.admin.authority.mapper;

import java.util.List;

import com.fijo.cores.mapper.ISystemMapper;
import org.apache.ibatis.annotations.Param;

import com.fijo.cores.admin.authority.model.SysPermission;
import com.fijo.cores.mapper.ISystemMapper;



public interface SysPermissionMapper extends ISystemMapper<SysPermission,Integer> {
	
	SysPermission getRoot();
	
	List<SysPermission> getExcludeRoot();	
	
	List<SysPermission> getByRole(Integer roleId);	

	/** 系统扩展 基于用户-资源直接授权时使用 Start **/
	List<SysPermission> getSysUserPermission(Integer userId);	

	int deleteSysUserPermissionByUserId(@Param(value = "userId")Integer userId);
	
	int createSysUserPermission(@Param(value = "userId")Integer userId, @Param(value = "permissionId")Integer permissionId);
	/** 系统扩展 基于用户-资源直接授权时使用 End **/
	
	List<SysPermission> getByParent(Integer id);
	
	List<SysPermission> getByMoudle(@Param(value = "loginName")String loginName);
	
	List<SysPermission> getByParentMenu(@Param(value = "parentId")Integer parentId, @Param(value = "loginName")String loginName);
	
	List<SysPermission> getMenu(Integer userId);	
}
