package com.fijo.cores.admin.authority.service.impl;

import java.util.List;

import com.fijo.cores.admin.authority.model.ShiroUser;
import com.fijo.cores.admin.authority.model.SysPermission;
import com.fijo.cores.admin.authority.model.SysRole;
import com.fijo.cores.admin.authority.model.SysUser;
import com.fijo.cores.admin.authority.service.ISysUserAdvanceService;
import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.exceptions.UnLoginException;
import com.fijo.cores.utils.Encodes;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import com.google.common.collect.Lists;
import com.fijo.cores.admin.authority.model.ShiroUser;
import com.fijo.cores.admin.authority.model.SysPermission;
import com.fijo.cores.admin.authority.model.SysRole;
import com.fijo.cores.admin.authority.model.SysUser;
import com.fijo.cores.admin.authority.service.ISysUserAdvanceService;
import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.exceptions.UnLoginException;
import com.fijo.cores.utils.Encodes;

public abstract class AbstractShrioRealm extends AuthorizingRealm {

	private static transient final Log log = LogFactory.getLog(AbstractShrioRealm.class);
	
	protected ISysUserAdvanceService sysUserAdvanceService;

	/**
	 * @param sysUserAdvanceService
	 *            the sysUserAdvanceService to set
	 */
	public void setSysUserAdvanceService(
			ISysUserAdvanceService sysUserAdvanceService) {
		this.sysUserAdvanceService = sysUserAdvanceService;
	}

	/**
	 * 账号认证回调函数, 每次登录时调用.
	 */
	protected abstract AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken authcToken);
	
	/**
	 * 角色权限授权回调函数, 需要鉴权时才调用.
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(
			PrincipalCollection principals) {
		ShiroUser shiroUser = (ShiroUser) principals.getPrimaryPrincipal();
		if(shiroUser == null)
			throw new UnLoginException("30006", "AppUserSession Should login first!");
		SysUser user = sysUserAdvanceService.getByUnique(shiroUser.loginName);
		if(user == null)
			throw new UnLoginException("30006", "AppUserSession Should login first!");
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		log.debug("SysUser####>"+user.getLoginName());
		// 用户角色的权限信息
		for (SysRole r : user.getRoleList()) {
			// 基于Role的鉴权验证
			info.addRole(r.getName());
			log.debug("SysRole---->"+r.getName());
			// 基于Permission的鉴权验证
			for (SysPermission p : r.getPermissionList()) {
				info.addStringPermission(p.getName());
				log.debug("SysPermission====>"+p.getName());
			}
		}
		// 用户的权限信息
		for (SysPermission p : user.getPermissionList()) {
			info.addStringPermission(p.getName());
			log.debug("SysPermission====>"+p.getName());
		}
		return info;
	}
	
	protected AuthenticationInfo createPasswordAuthenticationInfo(SysUser u){
		if (u != null) {
			byte[] salt = Encodes.decodeHex(u.getSalt());
			List<Integer> roleIds = Lists.newArrayList();
			for (SysRole role : u.getRoleList()) {
				roleIds.add(role.getId());
			}
			Object principal = new ShiroUser(u.getLoginName(), u.getUsername(),u.getUniqueCode(),
					u.getUserCode(), u.getId(), u.getSysOrg().getId(),
					u.getSysOrg().getOrgName(), roleIds,
					u.getHeadimgurl(), u.getAccesstoken(),
					u.getOpenid(), u.getUnionid(), u.getPhone(), u.getOwnerOrgId());
			AuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(principal,u.getPassword(),ByteSource.Util.bytes(salt), getName());
			return authenticationInfo;
		} else {
			return null;
		}
	}

	protected AuthenticationInfo createAuthenticationInfo(SysUser u){
		if (u != null) {
			List<Integer> roleIds = Lists.newArrayList();
			for (SysRole role : u.getRoleList()) {
				roleIds.add(role.getId());
			}
			AuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
					new ShiroUser(u.getLoginName(), u.getUsername(),u.getUniqueCode(),
							u.getUserCode(), u.getId(), u.getSysOrg()
									.getId(), u.getSysOrg().getOrgName(),
							roleIds, u.getHeadimgurl(), u.getAccesstoken(),
							u.getOpenid(), u.getUnionid(), u.getPhone(), u.getOwnerOrgId()),
					u.getPassword(), getName());
			return authenticationInfo;
		} else {
			return null;
		}
	}
	
	public boolean validateSimpleCredential(String username, String password) {
		boolean ret = false;
		UsernamePasswordToken token = new UsernamePasswordToken(username,
				password.toCharArray());
		try {
			AuthenticationInfo info = getAuthenticationInfo(token);
			ret = info == null ? false : true;
		} catch (IncorrectCredentialsException e) {
			Exceptions.printException(e);
		} catch (AuthenticationException e) {
			Exceptions.printException(e);
		}
		return ret;
	}

	@Override
	public void clearCachedAuthorizationInfo(PrincipalCollection principals) {
		super.clearCachedAuthorizationInfo(principals);
	}

	@Override
	public void clearCachedAuthenticationInfo(PrincipalCollection principals) {
		super.clearCachedAuthenticationInfo(principals);
	}

	@Override
	public void clearCache(PrincipalCollection principals) {
		super.clearCache(principals);
	}

	public void clearAllCachedAuthorizationInfo() {
		getAuthorizationCache().clear();
	}

	public void clearAllCachedAuthenticationInfo() {
		getAuthenticationCache().clear();
	}

	public void clearAllCache() {
		clearAllCachedAuthenticationInfo();
		clearAllCachedAuthorizationInfo();
	}

}
