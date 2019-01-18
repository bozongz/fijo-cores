package com.fijo.cores.exceptions;

/**
 * 账号注销不可用
 * @author zhangbo
 */
public class InvalidateAccountException extends AppException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5547161065911283763L;

	public InvalidateAccountException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	public InvalidateAccountException(String errorCode, String errorMessage,
			Throwable e) {
		super(errorCode, errorMessage, e);
	}
		
}
