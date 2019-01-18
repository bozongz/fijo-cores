package com.fijo.cores.admin.authority.service.impl;

import java.util.List;

import com.fijo.cores.admin.authority.mapper.SysPermissionMapper;
import com.fijo.cores.admin.authority.model.SysPermission;
import com.fijo.cores.admin.authority.service.ISysPermissionService;
import com.fijo.cores.service.impl.SystemService;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fijo.cores.admin.authority.mapper.SysPermissionMapper;
import com.fijo.cores.admin.authority.model.SysPermission;
import com.fijo.cores.admin.authority.service.ISysPermissionService;
import com.fijo.cores.service.impl.SystemService;

/**
 * 
 * 角色授权
 * 
 * @author zhangbo
 *
 */

@Service(value = "sysPermissionService")
public class SysPermissionService extends SystemService<SysPermission,Integer> implements ISysPermissionService {

	private SysPermissionMapper mapper;
	
	@Autowired
    public SysPermissionService(@Qualifier(value="sqlSessionTemplateSimple") SqlSession sqlSession) {
		super(sqlSession);
		this.mapper = sqlSession.getMapper(SysPermissionMapper.class);
		super.setMapper(mapper);
    }

	@Override
	public SysPermission getRoot() {
		return mapper.getRoot();
	}

	@Override
	public List<SysPermission> getExcludeRoot() {
		return mapper.getExcludeRoot();
	}

	@Override
	public List<SysPermission> getByRole(Integer roleId) {
		return mapper.getByRole(roleId);
	}

	@Override
	public List<SysPermission> getSysUserPermission(Integer userId) {
		return mapper.getSysUserPermission(userId);
	}

	@Override
	public int deleteSysUserPermissionByUserId(Integer userId) {
		return mapper.deleteSysUserPermissionByUserId(userId);
	}

	@Override
	public int createSysUserPermission(Integer userId, Integer permissionId) {
		return mapper.createSysUserPermission(userId, permissionId);
	}
	
	@Override
	public List<SysPermission> getByParent(Integer id) {
		return mapper.getByParent(id);
	}

	@Override
	public List<SysPermission> getByMoudle(String loginName) {
		return mapper.getByMoudle(loginName);
	}

	@Override
	public List<SysPermission> getByParentMenu(Integer parentId,
			String loginName) {
		return mapper.getByParentMenu(parentId, loginName);
	}

	@Override
	public List<SysPermission> getMenu(Integer userId) {		
		return mapper.getMenu(userId);
	}


}
