package com.fijo.cores.admin.authority.service.impl;

import java.util.List;

import com.fijo.cores.admin.authority.model.SysPermission;
import com.fijo.cores.admin.authority.service.ISysPermissionService;
import com.fijo.cores.service.impl.GenericAdvanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import com.fijo.cores.admin.authority.model.SysPermission;
import com.fijo.cores.admin.authority.service.ISysOrgService;
import com.fijo.cores.admin.authority.service.ISysPermissionAdvanceService;
import com.fijo.cores.admin.authority.service.ISysPermissionService;
import com.fijo.cores.cache.IGenericCache;
import com.fijo.cores.service.impl.GenericAdvanceService;


@Service(value="sysPermissionAdvanceService")
@CacheConfig(cacheNames = {"runtime:"}, cacheResolver="genericCacheResolver", keyGenerator="genericKeyGenerator")
public class SysPermissionAdvanceService extends GenericAdvanceService<SysPermission,Integer> implements ISysPermissionAdvanceService{

	private ISysPermissionService sysPermissionService;
	
	@Autowired
	private ISysOrgService sysOrgService;
	
	@Autowired
	public SysPermissionAdvanceService(
			ISysPermissionService sysPermissionService,
			@Qualifier(value="sysPermissionCache")IGenericCache<SysPermission,Integer> sysPermissionCache) {
		super(sysPermissionService, sysPermissionCache);
		this.sysPermissionService = sysPermissionService;
	}
	
	@Override
	public SysPermission getRoot() {	
		return sysPermissionService.getRoot();
	}

	@Override
	public List<SysPermission> getExcludeRoot() {		
		return sysPermissionService.getExcludeRoot();
	}

	@Override
	public List<SysPermission> getByRole(Integer roleId) {
		return sysPermissionService.getByRole(roleId);
	}

	@Override
	public List<SysPermission> getSysUserPermission(Integer userId) {
		return sysPermissionService.getSysUserPermission(userId);
	}
	
	@Override
	public int deleteSysUserPermissionByUserId(Integer userId) {
		return sysPermissionService.deleteSysUserPermissionByUserId(userId);
	}

	@Override
	public int createSysUserPermission(Integer userId, Integer permissionId) {
		return sysPermissionService.createSysUserPermission(userId, permissionId);
	}
	
	@Override
	public List<SysPermission> getByParent(Integer id) {
		return sysPermissionService.getByParent(id);
	}

	@Override
	public List<SysPermission> getByMoudle(String loginName) {
		return sysPermissionService.getByMoudle(loginName);
	}

	@Override
	public List<SysPermission> getByParentMenu(Integer parentId,
			String loginName) {
		return sysPermissionService.getByParentMenu(parentId, loginName);
	}

	@Override
	public List<SysPermission> getMenu(Integer userId) {
		return sysPermissionService.getMenu(userId);
	}



}
