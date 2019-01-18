package com.fijo.cores.exceptions;

/**
 * 非认证管理员访问异常
 * @author zhangbo
 */
public class InvalidateSNSAdminUserLoginException extends AppException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5906654867549866311L;

	public InvalidateSNSAdminUserLoginException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	public InvalidateSNSAdminUserLoginException(String errorCode, String errorMessage,
			Throwable e) {
		super(errorCode, errorMessage, e);
	}
		
}
