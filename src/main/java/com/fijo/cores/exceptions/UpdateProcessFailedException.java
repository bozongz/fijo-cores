package com.fijo.cores.exceptions;

/**
 * 更新业务流程失败
 * @author zhangbo
 */
public class UpdateProcessFailedException extends AppException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1586795526592547368L;

	public UpdateProcessFailedException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	public UpdateProcessFailedException(String errorCode, String errorMessage,
			Throwable e) {
		super(errorCode, errorMessage, e);
	}
	
}
