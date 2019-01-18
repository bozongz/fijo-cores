package com.fijo.cores.exceptions;

import org.apache.shiro.ShiroException;

/**
 * 单点登录异常
 * @author zhangbo
 */
public class SSOLoginFailedException extends ShiroException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2853029237535780131L;
	private String errorCode;
	private String errorMessage;

	/**
	 * 
	 * @param errorCode
	 * @param errorMessage
	 */
	public SSOLoginFailedException(String errorCode, String errorMessage) {
		super(errorMessage);
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * 
	 * @param errorCode
	 * @param errorMessage
	 * @param e
	 */
	public SSOLoginFailedException(String errorCode, String errorMessage, Throwable e) {
		super(errorMessage, e);
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
		
}
