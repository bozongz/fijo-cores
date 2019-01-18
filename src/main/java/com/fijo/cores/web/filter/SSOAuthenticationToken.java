/**
 * 
 */
package com.fijo.cores.web.filter;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author zhangbo
 *
 */
public class SSOAuthenticationToken implements AuthenticationToken {


	/**
	 * 
	 */
	private static final long serialVersionUID = 2532974662500311459L;
	private String loginName;

	public SSOAuthenticationToken(String loginName) {
		super();
		this.loginName = loginName;
	}

	@Override
	public Object getPrincipal() {
		return this.loginName;
	}

	@Override
	public Object getCredentials() {
		return null;
	}
}
