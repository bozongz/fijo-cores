package com.fijo.cores.shiro;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fijo.cores.admin.authority.model.ShiroUser;
import com.fijo.cores.admin.authority.model.SysUser;
import com.fijo.cores.cache.IGenericCache;
import com.fijo.cores.exceptions.UnLoginException;
import com.fijo.cores.utils.configs.CoreConfig;
import com.fijo.cores.web.filter.SSOAuthenticationToken;

/**
 * 获取用户Session信息
 * 
 * @author zhangbo
 *
 */

@Component
public class AppUserSession {
	public final Log log = LogFactory.getLog(AppUserSession.class);

	@Autowired
	private CoreConfig config;
	
	@Autowired
	@Qualifier("sysUserCache")
	private IGenericCache<SysUser,Integer> sysUserCache;
	
	public ShiroUser getCurrentUser(){
		if(Boolean.valueOf(config.getValue("app.mock.admin"))){
			SysUser admin = sysUserCache.loadByUnique(config.getValue("app.user.admin"));
			SSOAuthenticationToken ssoToken = new SSOAuthenticationToken(admin.getLoginName());
			SecurityUtils.getSubject().login(ssoToken);
		}
		Subject currentUser = SecurityUtils.getSubject();
		ShiroUser user = (ShiroUser) currentUser.getPrincipal();
		if(user == null){
			log.error("AppUserSession throw UnLoginException!");
			throw new UnLoginException("30006", "AppUserSession Should login first!");
		}
		return user;
	}
	
	public boolean isUserSessionTimeout(){
		ShiroUser currentUser = (ShiroUser)SecurityUtils.getSubject().getPrincipal();
		return currentUser == null ? true:false;
	}
	
	//以下方法配合解决  There is no session with id xxx
	//参考：http://stackoverflow.com/questions/14516851/shiro-complaining-there-is-no-session-with-id-xxx-with-defaultsecuritymanager
	// 1) Clean way to get the subject
	public Subject getSubject(){
	    Subject currentUser = ThreadContext.getSubject();// SecurityUtils.getSubject();
	    if (currentUser == null){
	        currentUser = SecurityUtils.getSubject();
	    }
	    return currentUser;
	}
	
	// 2) Logout the user fully before continuing.
	public void ensureUserIsLoggedOut(){
		try{
	        // Get the user if one is logged in.
	        Subject currentUser = getSubject();
	        if (currentUser == null)
	            return;
	        // Log the user out and kill their session if possible.
	        currentUser.logout();
	        Session session = currentUser.getSession(false);
	        if (session == null)
	            return;
	        session.stop();
	    }
	    catch (Exception e){
	        // Ignore all errors, as we're trying to silently 
	        // log the user out.
	    	log.warn("Some exception happen, but can forget!");
	    }
	}
}
