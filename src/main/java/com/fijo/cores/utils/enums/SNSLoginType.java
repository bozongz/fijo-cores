package com.fijo.cores.utils.enums;

/**
 * 
 * @author zhangbo
 *
 */
public enum SNSLoginType implements GenericEnum{

	weixin("微信登录"), weibo("微博登录"), qq("QQ登录"), accesstoken("App登陸");
	
	private String value;

	private SNSLoginType(String value) {
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
}
