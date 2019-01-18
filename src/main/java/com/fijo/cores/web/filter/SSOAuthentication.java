/**
 * 
 */
package com.fijo.cores.web.filter;

import javax.servlet.http.HttpServletRequest;

import com.fijo.cores.exceptions.SSOLoginFailedException;
import com.fijo.cores.exceptions.SSOLoginFailedException;

/**
 * SSO 单点登录认证
 * 
 * @author zhangbo
 *
 */
public interface SSOAuthentication{

	/**
	 * 从请求中验证单点登录，并返回用户登录帐号loginName
	 * @param request
	 * @return
	 * @throws SSOLoginFailedException
	 */
	String authenticate(HttpServletRequest request) throws SSOLoginFailedException;
}
