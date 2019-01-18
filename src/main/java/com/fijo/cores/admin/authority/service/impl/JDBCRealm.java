package com.fijo.cores.admin.authority.service.impl;

import javax.annotation.PostConstruct;

import com.fijo.cores.admin.authority.model.SysUser;
import com.fijo.cores.exceptions.InvalidateAccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;

import com.fijo.cores.admin.authority.model.SysUser;
import com.fijo.cores.admin.authority.service.ISysUserService;
import com.fijo.cores.exceptions.InvalidateAccountException;

public class JDBCRealm extends AbstractShrioRealm {

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken authcToken) throws AuthenticationException {
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		SysUser user = sysUserAdvanceService.getByUnique(token.getUsername());
		if(user != null){
			if(!user.getEnabled()){
				throw new InvalidateAccountException("SYSUSER0001", String.format("User %s is not enabled......", user.getLoginName()));
			}
		}
		return createPasswordAuthenticationInfo(user);
	}


//	/**
//	 * 设定Password校验的Hash算法与迭代次数.
//	 */
//	@PostConstruct
//	public void initCredentialsMatcher() {
////		HashedCredentialsMatcher matcher = new HashedCredentialsMatcher(
////				ISysUserService.HASH_ALGORITHM);
////		matcher.setHashIterations(ISysUserService.HASH_INTERATIONS);
//		setCredentialsMatcher(getCredentialsMatcher());
//	}
}
