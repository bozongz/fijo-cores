package com.fijo.cores.web.filter;

import java.io.IOException;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.exceptions.SSOLoginFailedException;
import com.fijo.cores.utils.configs.CoreConfig;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.dao.DataAccessException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fijo.cores.admin.authority.model.ShiroUser;
import com.fijo.cores.admin.authority.model.SysUser;
import com.fijo.cores.admin.authority.service.ISysUserAdvanceService;
import com.fijo.cores.admin.syslog.model.SysLoginInfo;
import com.fijo.cores.admin.syslog.service.ISysLoginInfoService;
import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.exceptions.SSOLoginFailedException;
import com.fijo.cores.utils.configs.CoreConfig;

/**
 * 单点登录请求过滤器
 * 
 * @author zhangbo
 *
 */
public class SSOAuthorizationFilter extends OncePerRequestFilter {

	@Resource(name = "sysLoginInfoService")
	private ISysLoginInfoService sysLoginInfoService;
	
	@Autowired
	private ISysUserAdvanceService sysUserService;
	
	private SSOAuthentication authentication;
	
	private Boolean record;
	
	@Autowired
	private void initRecord(CoreConfig coreConfig){
		record = Boolean.valueOf(coreConfig.getValue("app.record.log"));
	}
	
	public void setAuthentication(SSOAuthentication authentication) {
		this.authentication = authentication;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {	
		String username = authentication.authenticate(request);
		SSOAuthenticationToken ssoToken = new SSOAuthenticationToken(username);
		try{
			SecurityUtils.getSubject().login(ssoToken);
		}catch(AuthenticationException e){
			throw new SSOLoginFailedException("SSO_LOGIN_FAILED", Exceptions.getStackTraceAsString(e));
		}
		
		Subject currentUser = SecurityUtils.getSubject();
    	ShiroUser principal = (ShiroUser)currentUser.getPrincipal();
    	if (!currentUser.hasRole("Supervisor")) {
    		if(principal != null){ // 已登陆成功不再登陆	
    			if(record){
		    		//记录登陆信息
					SysUser sysUser = sysUserService.getByUnique(principal.loginName);
					SysLoginInfo record = new SysLoginInfo();
					record.setSessionid(currentUser.getSession().getId().toString());
					record.setLoginip(currentUser.getSession().getHost());
					record.setLogintime(new Date());
					record.setUseraccount(sysUser.getLoginName());
					record.setUsername(sysUser.getUsername());
					try{
						sysLoginInfoService.insert(record);
					}catch(DataAccessException e){
		    			Exceptions.printException(e);
		    		}
    			}
    		}
    	}
    	
		filterChain.doFilter(request, response);
	}

}
