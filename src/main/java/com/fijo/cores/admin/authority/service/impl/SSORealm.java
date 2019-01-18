/**
 * 
 */
package com.fijo.cores.admin.authority.service.impl;

import com.fijo.cores.admin.authority.model.SysUser;
import com.fijo.cores.web.filter.SSOAuthenticationToken;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;

import com.fijo.cores.admin.authority.model.SysUser;
import com.fijo.cores.web.filter.SSOAuthenticationToken;

/**
 * @author zhangbo
 * 
 *         多URL多Realm认证参考
 *         http://stackoverflow.com/questions/24546396/multiple-realms
 *         -to-handle-authentication-for-different-sets-of-urls-in-spring-mv
 *
 */
public class SSORealm extends AbstractShrioRealm {

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken authcToken) throws AuthenticationException {
		SSOAuthenticationToken token = (SSOAuthenticationToken) authcToken;
		if (token.getPrincipal() == null)
			return null;
		else {
			SysUser user = sysUserAdvanceService.getByUnique(token.getPrincipal().toString());
			return createAuthenticationInfo(user);
		}
	}

}
