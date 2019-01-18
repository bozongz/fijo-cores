package com.fijo.cores.exceptions;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 关于异常的工具类.
 * 
 * @author zhangbo
 */
public class Exceptions {

	protected final static Log log = LogFactory.getLog(Exceptions.class);
	
	/**
	 * 将CheckedException转换为UncheckedException.
	 */
	public static RuntimeException unchecked(Exception e) {
		if (e instanceof RuntimeException) {
			return (RuntimeException) e;
		} else {
			return new RuntimeException(e);
		}
	}

	/**
	 * 将ErrorStack转化为String.
	 */
	public static String getStackTraceAsString(Exception e) {
		StringWriter stringWriter = new StringWriter();
		e.printStackTrace(new PrintWriter(stringWriter));
		return stringWriter.toString();
	}

	/**
	 * 判断异常是否由某些底层的异常引起.
	 */
	public static boolean isCausedBy(Exception ex, @SuppressWarnings("unchecked") Class<? extends Exception>... causeExceptionClasses) {
		Throwable cause = ex;
		while (cause != null) {
			for (Class<? extends Exception> causeClass : causeExceptionClasses) {
				if (causeClass.isInstance(cause)) {
					return true;
				}
			}
			cause = cause.getCause();
		}
		return false;
	}
	
	public static void printException(Exception e){
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		e.printStackTrace(new PrintWriter(buf, true));
		String expMsg = buf.toString();
		log.error(expMsg);
	}
}

