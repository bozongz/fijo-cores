package com.fijo.cores.exceptions;

/**
 * 未登录异常
 * @author zhangbo
 */
public class UnLoginException extends AppException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3474049410795198525L;

	public UnLoginException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	public UnLoginException(String errorCode, String errorMessage,
			Throwable e) {
		super(errorCode, errorMessage, e);
	}
	
}
