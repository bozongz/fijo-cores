package com.fijo.cores.exceptions;

/**
 * 非认证用户访问异常
 * @author zhangbo
 */
public class InvalidateSNSUserException extends AppException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 145077013474954938L;

	public InvalidateSNSUserException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	public InvalidateSNSUserException(String errorCode, String errorMessage,
			Throwable e) {
		super(errorCode, errorMessage, e);
	}
		
}
