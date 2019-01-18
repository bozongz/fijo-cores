package com.fijo.cores.web.filter;

import java.io.Serializable;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.utils.configs.CoreConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.dao.DataAccessException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.fijo.cores.admin.authority.model.ShiroUser;
import com.fijo.cores.admin.syslog.model.SysLoginInfo;
import com.fijo.cores.admin.syslog.model.SysLoginInfoExample;
import com.fijo.cores.admin.syslog.model.SysLoginInfoExample.Criteria;
import com.fijo.cores.admin.syslog.service.ISysLoginInfoService;
import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.utils.configs.CoreConfig;

/**
 * 相同账号登陆被踢出后，记录登陆信息
 * 
 * @author zhangbo
 *
 */
public class KickoutSessionControlFilter extends AccessControlFilter {
	public final Log log = LogFactory.getLog(KickoutSessionControlFilter.class);
	
	@Resource(name = "sysLoginInfoService")
	private ISysLoginInfoService sysLoginInfoService;
	
	private String kickoutUrl; // 踢出后到的地址
	private boolean kickoutAfter = false; // 踢出之前登录的/之后登录的用户 默认踢出之前登录的用户
	private int maxSession = 1; // 同一个帐号最大会话数 默认1

	private SessionManager sessionManager;
	private Cache<String, Deque<Serializable>> cache;
	
	private Boolean record;
	
	@Autowired
	private void initRecord(CoreConfig coreConfig){
		record = Boolean.valueOf(coreConfig.getValue("app.record.log"));
	}
	
	public void setKickoutUrl(String kickoutUrl) {
		this.kickoutUrl = kickoutUrl;
	}

	public void setKickoutAfter(boolean kickoutAfter) {
		this.kickoutAfter = kickoutAfter;
	}

	public void setMaxSession(int maxSession) {
		this.maxSession = maxSession;
	}

	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cache = cacheManager.getCache("shiro-kickout-session");
	}

	@Override
	protected boolean isAccessAllowed(ServletRequest request,
			ServletResponse response, Object mappedValue) throws Exception {
		return false;
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request,
			ServletResponse response) throws Exception {
		Subject subject = getSubject(request, response);
		if (!subject.isAuthenticated() && !subject.isRemembered()) {
			// 如果没有登录，直接进行之后的流程
			return true;
		}

		Session session = subject.getSession();
		ShiroUser user = (ShiroUser) subject.getPrincipal();
		String loginName = user.getLoginName();
		Serializable sessionId = session.getId();

		// TODO 同步控制
		Deque<Serializable> deque = cache.get(loginName);
		if (deque == null) {
			deque = new LinkedList<Serializable>();
			cache.put(loginName, deque);
		}

		// 如果队列里没有此sessionId，且用户没有被踢出；放入队列
		if (!deque.contains(sessionId)
				&& session.getAttribute("kickout") == null) {
			deque.push(sessionId);
		}

		// 如果队列里的sessionId数超出最大会话数，开始踢人
		while (deque.size() > maxSession) {
			Serializable kickoutSessionId = null;
			if (kickoutAfter) { // 如果踢出后者
				kickoutSessionId = deque.removeFirst();
			} else { // 否则踢出前者
				kickoutSessionId = deque.removeLast();
			}
			try {
				Session kickoutSession = sessionManager
						.getSession(new DefaultSessionKey(kickoutSessionId));
				if (kickoutSession != null) {
					// 设置会话的kickout属性表示踢出了
					kickoutSession.setAttribute("kickout", true);
				}
			} catch (Exception e) {// ignore exception
			}
		}

		// 如果被踢出了，直接退出，重定向到踢出后的地址
		if (session.getAttribute("kickout") != null) {
			ShiroUser principal = (ShiroUser)subject.getPrincipal();
			if(principal != null){ // 已登陆成功不再登陆
				if(record){
					SysLoginInfoExample ex = new SysLoginInfoExample();
					Criteria c = ex.createCriteria();
					c.andSessionidEqualTo(subject.getSession().getId().toString());			
					List<SysLoginInfo> list = sysLoginInfoService.selectByExample(ex);
					if(list != null && list.size()==1){
						SysLoginInfo info = list.get(0);
						info.setLogouttime(new Date());
						try{
							sysLoginInfoService.updateByPrimaryKeySelective(info);
						}catch(DataAccessException e){
			    			log.error(Exceptions.getStackTraceAsString(e));
			    		}
					}
				}
			}	
			// 会话被踢出了
			try {
				subject.logout();
			} catch (Exception e) { // ignore
			}
			saveRequest(request);
			WebUtils.issueRedirect(request, response, kickoutUrl);
			return false;
		}

		return true;
	}
}