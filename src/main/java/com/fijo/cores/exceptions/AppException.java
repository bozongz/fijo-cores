package com.fijo.cores.exceptions;

/**
 * 应用系统异常
 * @author zhangbo
 * 100 	    Invalidate Query
 * 101	    Invalidate Type
 * 102	    Transaction Submit Failed
 * 
 * 10000	权限不足
 * 10001	根据审批结果未找到下一流程环节	
 * 10002	根据流程环节与组织信息未找到审批配置
 * 10003	当前登录用户没有处理该组织下该环节的权限
 * 10004	当前登录用户所处角色没有处理该组织下该环节的权限
 * 10005    没有找到业务流程头
 * 10006    业务流程头没有配置起始环节
 * 10007    业务流程头配置了多个起始环节
 * 10008    业务流程头没有配置发起环节
 * 10009    流程环节不允许配置多个角色进行审批
 * 10010    流程环节不允许配置为同时满足多个自定义跃迁的配置信息
 * 10011    当前流程配置不可用
 * 10012    更新业务流程失败
 * 10013    流程环节审批人无效
 * 11001	当日没有足够的日期序列码
 * 11002	当日没有足够的日期时间序列码
 * 12000	反序列化解析JSON数据异常
 * 12001	序列化解析JSON数据异常
 *
 * 30000    与微信服务器连接超时
 * 30001    与微信服务器数据交互发生异常
 * 30002	解析AccessToken失败
 * 30003 	获取AccessToken失败
 * 30004    尝试解析AccessToken超过最大次数
 * 30005    创建微信菜单失败
 * 30006    非认证员工访问
 * 30007	发送微信消息失败
 */
public class AppException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1607063540894022535L;
	private String errorCode;
	private String errorMessage;

	public AppException(){
		super();
	}

    public AppException(Throwable cause){
        super(cause);
    }

	/**
	 * 
	 * @param errorCode
	 * @param errorMessage
	 */
	public AppException(String errorCode, String errorMessage) {
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
	public AppException(String errorCode, String errorMessage, Throwable e) {
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
