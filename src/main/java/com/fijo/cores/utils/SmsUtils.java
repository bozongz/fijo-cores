package com.fijo.cores.utils;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 简单的短信发送
 * 
 * @author zhangbo
 * 
 */
@Component
public class SmsUtils extends ApplicationObjectSupport{
	public final static transient Log log = LogFactory.getLog(SmsUtils.class);
	
	private SmsService smsService;
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	private HashOperations<String, String, String> hashOps = null;
	

    @PostConstruct
	public void init(){	
    	boolean exist = getApplicationContext().containsBean("smsService");
    	if(exist)
    		smsService = (SmsService) getApplicationContext().getBean("smsService");;
		hashOps = redisTemplate.opsForHash();
	}
    
	/**
	 * 发送验证码
	 * @param prefix 验证码类型前缀
	 * @param phone 手机号
	 * @param codeLength 验证码长度
	 * @param expireMinute 过期时间
	 */
	public int sendCode(String prefix, String phone, int codeLength, int expireMinute){
		if(StringUtils.isNotEmpty(phone)){
			String retCode = smsService.sendRandomCode(phone, codeLength, expireMinute);
			if(StringUtils.isNotEmpty(retCode)){
				hashOps.put(prefix+phone, phone, retCode);
				redisTemplate.expire(prefix+phone, expireMinute, TimeUnit.MINUTES);
				return 1;
			}else{
				log.error(String.format("Send sms failed, prefix:%s phone:%s", prefix, phone));
				return 0;
			}
		}else{
			log.error(String.format("Send sms failed, prefix:%s phone:%s", prefix, phone));
			return 0;
		}
	}
	
	/**
	 * 
	 * @param phone
	 * @return
	 */
	public int sendUserBindCode(String phone){
		return sendCode("user_bind_", phone, 4, 1);
	}
	
	/**
	 * 验证码
	 * @param prefix 验证码类型前缀
	 * @param phone 手机号
	 * @param expectCode 用户输入值
	 * @return
	 */
	public boolean checkCode(String prefix, String phone, String expectCode){
		String actualCode = hashOps.get(prefix+phone, phone);
		if(StringUtils.isEmpty(phone) || StringUtils.isEmpty(expectCode) || StringUtils.isEmpty(actualCode)){
			return false;
		}else{
			return actualCode.equals(expectCode);
		}	
	}
	
	/**
	 * 验证用户绑定
	 * @param phone
	 * @param expectCode
	 * @return
	 */
	public boolean checkUserBindCode(String phone, String expectCode){
		if(phone.equals("15138462002") && expectCode.equals("8888")) //苹果上架临时测试账号
			return true;
		else
			return checkCode("user_bind_", phone, expectCode);
	}
}