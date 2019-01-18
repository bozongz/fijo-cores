/**
 * 
 */
package com.fijo.cores.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fijo.cores.exceptions.*;
import com.fijo.cores.model.JsonResponse;
import com.fijo.cores.utils.configs.CoreConfig;
import com.fijo.cores.exceptions.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import com.fijo.cores.admin.authority.model.SysPermission;
import com.fijo.cores.admin.authority.model.SysRole;
import com.fijo.cores.admin.authority.service.ISysPermissionAdvanceService;
import com.fijo.cores.admin.authority.service.ISysRoleAdvanceService;
import com.fijo.cores.model.JsonResponse;
import com.fijo.cores.utils.configs.CoreConfig;

/**
 * @author zhangbo
 *
 */
@ControllerAdvice
public class ExceptionControllerAdvice {
	public final Log log = LogFactory.getLog(ExceptionControllerAdvice.class);
	
	@Autowired
	private ISysPermissionAdvanceService sysPermissionService;
	
	@Autowired
	private ISysRoleAdvanceService sysRoleService;
	
	@Autowired
	private CoreConfig config;
	
	/**
	 * 上传文件超过最大限额
	 * @param e
	 * @return
	 */
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	@ResponseBody
	public String uploadError(HttpServletRequest request, MaxUploadSizeExceededException e) {	
		log.error(request.getRequestURI());
		Exceptions.printException(e);
		String errmsg = "<script type=\"text/javascript\">parent.imageMessage=\"上传文件过大!\";</script>";
		return errmsg;
	}
	
	/**
	 * 期望一条数据，但数据库不止一条记录
	 * @param request
	 * @param e
	 * @return
	 */
	@ExceptionHandler(FoundMoreThanOneException.class)
	@ResponseBody
	public JsonResponse foundMoreThanOneException(HttpServletRequest request, FoundMoreThanOneException e) {
		log.error(request.getRequestURI());
		Exceptions.printException(e);
		JsonResponse response = new JsonResponse();	
		response.setMessage("发现存在不止一条记录！");
		response.setResponseid(0);
		return response;
	}

    @ExceptionHandler(CodeLengthOutOfLimitException.class)
    @ResponseBody
    public JsonResponse codeLengthOutOfLimitException(HttpServletRequest request, CodeLengthOutOfLimitException e) {
        log.error(request.getRequestURI());
        Exceptions.printException(e);
        JsonResponse response = new JsonResponse();
        response.setMessage("流程编号长度越界！");
        response.setResponseid(0);
        return response;
    }

    @ExceptionHandler(CodePersistFailedException.class)
    @ResponseBody
    public JsonResponse codePersistFailedException(HttpServletRequest request, CodePersistFailedException e) {
        log.error(request.getRequestURI());
        Exceptions.printException(e);
        JsonResponse response = new JsonResponse();
        response.setMessage("流程编号持久化失败！");
        response.setResponseid(0);
        return response;
    }

    @ExceptionHandler(RSAFailedException.class)
    @ResponseBody
    public JsonResponse rsaFailedException(HttpServletRequest request, RSAFailedException e) {
        log.error(request.getRequestURI());
        Exceptions.printException(e);
        JsonResponse response = new JsonResponse();
        response.setMessage("密文解密失败！");
        response.setResponseid(0);
        return response;
    }

    @ExceptionHandler({ProcessUnavailableException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public JsonResponse processProcessUnavailableException(HttpServletRequest request, ProcessUnavailableException e) {
		log.error(request.getRequestURI());
		Exceptions.printException(e);
        JsonResponse response = new JsonResponse();
        response.setMessage("当前流程状态为禁用模式!");
		response.setResponseid(0);
		return response;
    }
    
    @ExceptionHandler({UpdateProcessFailedException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public JsonResponse processUpdateProcessFailedException(HttpServletRequest request, UpdateProcessFailedException e) {
		log.error(request.getRequestURI());
		Exceptions.printException(e);
    	JsonResponse response = new JsonResponse();		    	
		response.setMessage("编辑更新流程失败!");
		response.setResponseid(0);
		return response;
    }
    
    @ExceptionHandler({UnExpectedAuditException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public JsonResponse processUnExpectedAuditException(HttpServletRequest request, UnExpectedAuditException e) {
		log.error(request.getRequestURI());
		Exceptions.printException(e);
    	JsonResponse response = new JsonResponse();		    	
		response.setMessage("加载当前环节审批配置信息失败!");
		response.setResponseid(0);
		return response;
    }

    @ExceptionHandler({UnExpectedAuditUserException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public JsonResponse processUnExpectedAuditUserException(HttpServletRequest request, UnExpectedAuditUserException e) {
        log.error(request.getRequestURI());
        Exceptions.printException(e);
        JsonResponse response = new JsonResponse();
        response.setMessage("下一环节处理人无效，流程不可提交!");
        response.setResponseid(0);
        return response;
    }

    @ExceptionHandler({UnExpectedStepException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public JsonResponse processUnExpectedStepException(HttpServletRequest request, UnExpectedStepException e) {
		log.error(request.getRequestURI());
		Exceptions.printException(e);
    	JsonResponse response = new JsonResponse();		    	
		response.setMessage("加载流程审批环节失败!");
		response.setResponseid(0);
		return response;
    }
    
    /**
     * 页面找不到404
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler({NoSuchRequestHandlingMethodException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public JsonResponse noSuchRequestHandlingMethod(HttpServletRequest request, Exception e) {
		log.error(request.getRequestURI());
		Exceptions.printException(e);
    	JsonResponse response = new JsonResponse();		    	
		response.setMessage("请求的资源不存在!");
		response.setResponseid(0);
		return response;
    }
    
    @ExceptionHandler({UnauthorizedException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public JsonResponse processUnauthenticatedException(HttpServletRequest request, UnauthorizedException e) {
		log.error(request.getRequestURI());
		Exceptions.printException(e);
    	String message = "权限不足！";
    	String errors = e.getMessage();
    	int begin = errors.indexOf("[")+1;
    	int end = errors.indexOf("]");
    	if(begin>0 && end!=-1){
	    	String key = errors.substring(begin, end);
	    	if(StringUtils.contains(errors, "role")){
	    		SysRole sysRole = sysRoleService.getByUnique(key);
	    		if(sysRole != null)
	    			message = "您没有授权'"+sysRole.getDescription()+"'角色!";
	    	}
	    	if(StringUtils.contains(errors, "permission")){
	    		SysPermission sysPermission = sysPermissionService.getByUnique(key);
	    		if(sysPermission != null)
	    			message = "您没有授权'"+sysPermission.getDescription()+"'操作权限!";
	    	}
    	}
    	JsonResponse response = new JsonResponse();		    	
		response.setMessage(message +"");
		response.setResponseid(0);
		return response;
    }

    @ExceptionHandler({UnLoginException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public JsonResponse processUnLoginException(HttpServletRequest request, UnLoginException e) {
		log.error(request.getRequestURI());
		Exceptions.printException(e);
    	JsonResponse response = new JsonResponse();		    	
		response.setMessage("请先登录!");
		response.setResponseid(200);
		return response;
    }
    
    @ExceptionHandler({UnknownAccountException.class})
    @ResponseBody
    public JsonResponse processUnknownAccountException(HttpServletRequest request, UnknownAccountException e) {
		log.error(request.getRequestURI());
		Exceptions.printException(e);
    	JsonResponse response = new JsonResponse();		    	
		response.setMessage("用户名不存在!");
		response.setResponseid(0);
		return response;
    }
    
    @ExceptionHandler({AuthenticationException.class})
    @ResponseBody
    public JsonResponse processAuthenticationException(HttpServletRequest request, AuthenticationException e) {
		log.error(request.getRequestURI());
		Exceptions.printException(e);
    	JsonResponse response = new JsonResponse();		    	
		response.setMessage("用户名/密码错误!");
		response.setResponseid(0);
		return response;
    }
    
    @ExceptionHandler({InvalidateSNSAdminUserException.class})
    public String processInvalidateSNSAdminUserException(HttpServletRequest request, InvalidateSNSAdminUserException e) {
		log.error(request.getRequestURI());
		Exceptions.printException(e);
		return "redirect:/html/errors/InvalidateSNSAdminUserException.html";
    }
    
    @ExceptionHandler({SNSUserBindFailedLoginException.class})
    @ResponseBody
    public JsonResponse processSNSUserBindFailedLoginException(HttpServletRequest request, SNSUserBindFailedLoginException e) {
		log.error(request.getRequestURI());
		Exceptions.printException(e);
    	JsonResponse response = new JsonResponse();
		response.setMessage("绑定用户账号异常!");
		response.setResponseid(0);
		return response;
    }

    @ExceptionHandler({NotAllowUploadFileTypeException.class})
    @ResponseBody
    public String processNotAllowUploadFileTypeException(HttpServletRequest request, NotAllowUploadFileTypeException e) {
        log.error(request.getRequestURI());
        Exceptions.printException(e);
        return "<script type=\"text/javascript\">parent.imageMessage=\"不支持此文件类型!\";</script>";
    }

    /**
     * 应用异常
     * @param e
     * @return
     * @throws IOException 
     */
    @ExceptionHandler({AppException.class })
    @ResponseBody
	public JsonResponse processAppException(HttpServletRequest request, HttpServletResponse response, AppException e) throws IOException {
		log.error(request.getRequestURI());
		Exceptions.printException(e);
		JsonResponse res = new JsonResponse();
        res.setResponseid(0);
        res.setMessage(getUnKnowErrorMsg());
		return res;
    }
    
	/**
	 * 系统未知异常
	 * @param e
	 * @return
	 * @throws IOException 
	 */
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public JsonResponse processUnknownException(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException {	
		log.error(request.getRequestURI());
		Exceptions.printException(e);
        JsonResponse res = new JsonResponse();
        res.setResponseid(0);
        res.setMessage(getUnKnowErrorMsg());
        return res;
	}
	
	private String getUnKnowErrorMsg(){
		String unKnowErrorMsg = StringUtils.isNotEmpty(config.getValue("app.unKnowErrorMsg"))?config.getValue("app.unKnowErrorMsg"):"服务器忙，请稍后尝试!";
		return unKnowErrorMsg;
	}
}
