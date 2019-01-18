package com.fijo.cores.utils;

public interface SmsService {

	/**
	 * 发送短信验证码
	 * @param phone 手机号
	 * @param codeLength 验证码长度
	 * @param expireMinute 到期时间
	 * @return
	 */
	String sendRandomCode(String phone, int codeLength, int expireMinute);
}
