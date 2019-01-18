/**
 * 
 */
package com.fijo.cores.admin.authority.service.impl;

import com.fijo.cores.admin.authority.model.SysUser;
import com.fijo.cores.utils.enums.SNSLoginType;
import com.fijo.cores.web.filter.SNSAuthenticationToken;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.springframework.util.StringUtils;

import com.fijo.cores.admin.authority.model.SysUser;
import com.fijo.cores.utils.enums.SNSLoginType;
import com.fijo.cores.web.filter.SNSAuthenticationToken;

/**
 * @author zhangbo
 * 
 *         多URL多Realm认证参考
 *         http://stackoverflow.com/questions/24546396/multiple-realms
 *         -to-handle-authentication-for-different-sets-of-urls-in-spring-mv
 *
 */
public class SNSRealm extends AbstractShrioRealm {

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken authcToken) throws AuthenticationException {
		SNSAuthenticationToken token = (SNSAuthenticationToken) authcToken;
		String ticket = null;
		SNSLoginType loginType = null;
		if(null == token.getPrincipal() || null == token.getCredentials() || StringUtils.isEmpty(token.getPrincipal()) || StringUtils.isEmpty(token.getCredentials())){
			return null;
		}else{
			ticket = token.getPrincipal().toString();
			loginType = (SNSLoginType) token.getCredentials();
		}
		
		SysUser user = null;
		switch (loginType) {
			case weixin: {
				user = sysUserAdvanceService.getByOpenid(ticket);
				break;
			}
			case accesstoken: {
				user = sysUserAdvanceService.getByAccesstoken(ticket);
				break;
			}
			default:
				break;
		}
		return createAuthenticationInfo(user);
	}


}
