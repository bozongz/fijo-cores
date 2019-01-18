package com.fijo.cores.admin.authority.service.impl;

import java.util.List;

import com.fijo.cores.admin.authority.mapper.SysRoleMapper;
import com.fijo.cores.admin.authority.model.SysRole;
import com.fijo.cores.admin.authority.service.ISysRoleService;
import com.fijo.cores.service.impl.SystemService;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fijo.cores.admin.authority.mapper.SysRoleMapper;
import com.fijo.cores.admin.authority.model.SysRole;
import com.fijo.cores.admin.authority.service.ISysRoleService;
import com.fijo.cores.service.impl.SystemService;



@Service(value = "sysRoleService")
public class SysRoleService extends SystemService<SysRole,Integer> implements ISysRoleService {
	
	private SysRoleMapper mapper;

	@Autowired
    public SysRoleService(@Qualifier(value="sqlSessionTemplateSimple") SqlSession sqlSession) {
		super(sqlSession);
		this.mapper = sqlSession.getMapper(SysRoleMapper.class);
		super.setMapper(mapper);
    }
	
	@Override
	public int createSysRolePermission(Integer roleId,Integer permissionId){
		return mapper.createSysRolePermission(roleId,permissionId);
	}

	@Override
	public int createSysUserRole(Integer userId,Integer roleId){
		return mapper.createSysUserRole(userId,roleId);
	}
	
	@Override
	public int deleteSysRolePermissionByRoleId(Integer roleId) {
		return mapper.deleteSysRolePermissionByRoleId(roleId);
	}
	
	@Override
	public int deleteSysUserRoleByRoleId(Integer roleId) {
		return mapper.deleteSysUserRoleByRoleId(roleId);
	}

	@Override
	public List<SysRole> getByUser(Integer userId) {
		return mapper.getByUser(userId);
	}

	@Override
	public int deleteSysUserRoleByUserId(Integer userId) {		
		return mapper.deleteSysUserRoleByUserId(userId);
	}
}
