package com.fijo.cores.exceptions;

/**
 * 业务流程不可用
 * @author zhangbo
 */
public class ProcessUnavailableException extends AppException{


	/**
	 * 
	 */
	private static final long serialVersionUID = -7185765371544963448L;

	public ProcessUnavailableException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	public ProcessUnavailableException(String errorCode, String errorMessage,
			Throwable e) {
		super(errorCode, errorMessage, e);
	}
	
}
