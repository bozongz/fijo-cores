package com.fijo.cores.app.aop;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.dao.DataAccessException;
import org.apache.shiro.subject.Subject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;
import com.fijo.cores.admin.authority.model.ShiroUser;
import com.fijo.cores.admin.authority.model.SysPermission;
import com.fijo.cores.admin.authority.service.ISysPermissionAdvanceService;
import com.fijo.cores.admin.syslog.model.SysOperateInfo;
import com.fijo.cores.admin.syslog.service.ISysOperateInfoService;
import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.utils.annotations.LogAudit;
import com.fijo.cores.utils.configs.CoreConfig;
import com.fijo.cores.web.BaseController;

/**
 * 拦截记录所有Controller操作日志，通过SysPermission进行关联
 * 
 * @author zhangbo
 *
 */
@Aspect
public class SysOperateInfoAspect {
	public final Log log = LogFactory.getLog(getClass());
	
	private Map<String,Method> baseControllerMethods = Maps.newHashMap();
	
	private Set<String> baseControllerMethodNames;
	
	@Resource(name = "sysOperateInfoService")
	private ISysOperateInfoService sysOperateInfoService;
	
	@Autowired
	private ISysPermissionAdvanceService sysPermissionAdvanceService;
	
	private Boolean record;
	
	@Autowired
	private void initRecord(CoreConfig coreConfig){
		record = Boolean.valueOf(coreConfig.getValue("app.record.log"));
	}
	
	public SysOperateInfoAspect() {
		super();
		for(Method method : BaseController.class.getDeclaredMethods()){
			baseControllerMethods.put(method.getName(), method);
		}
		baseControllerMethodNames = baseControllerMethods.keySet();
	}

	@After("execution(* *..web..*Controller.*(..))")
	// 在方法执行之后执行的代码. 无论该方法是否出现异常
	public void afterMethod(JoinPoint jp) {		
		Signature signature = jp.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method targetMethod = methodSignature.getMethod();
		//Class<? extends Method> clazz = targetMethod.getClass(); 
		if(record){
			if(targetMethod.getAnnotation(LogAudit.class) != null){
				saveOperateLog(targetMethod);
			}
			else if(baseControllerMethodNames.contains(targetMethod.getName())){
				Method parentMethod = baseControllerMethods.get(targetMethod.getName());
				if(parentMethod.getAnnotation(LogAudit.class) != null){
					saveOperateLog(targetMethod);
				}
			}
		}
	}
	
	private void saveOperateLog(Method method){
		RequiresPermissions resource = (RequiresPermissions)method.getAnnotation(RequiresPermissions.class);
		if(resource != null){
			SysPermission p = sysPermissionAdvanceService.loadByUnique(resource.value()[0]);
			if(p != null){
	    		Subject currentUser = SecurityUtils.getSubject();
	    		if (!currentUser.hasRole("Supervisor")) {
	    			ShiroUser principal = (ShiroUser)currentUser.getPrincipal();
		    		SysOperateInfo info  = new SysOperateInfo();
		    		info.setSessionid(currentUser.getSession().getId().toString());
		    		info.setClientIp(currentUser.getSession().getHost());
		    		info.setLoginname(principal.loginName);
		    		info.setUsername(principal.userName);
		    		info.setOperatetime(new Date());	    		    		
		    		info.setFunCode(p.getUrl());	    		
		    		info.setFunName(p.getDescription());
		    		info.setMoudleCode(p.getParent().getUrl());	    		
		    		info.setMoudleName(p.getParent().getDescription());
		    		try{
		    			sysOperateInfoService.insert(info);
		    		}catch(DataAccessException e){
		    			log.error(Exceptions.getStackTraceAsString(e));
		    		}	
	    		}	    						    			    			    		
	    	}
		}				
	}

}
