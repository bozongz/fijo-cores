package com.fijo.cores.web;

import java.util.Map;

import com.fijo.cores.utils.SmsUtils;
import com.fijo.cores.model.JsonResponse;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.fijo.cores.utils.SmsUtils;

/**
 * 手机验证码
 * @author zhangbo
 */
@Controller
@RequestMapping("/action/sms")
public class SMSController {

	@Autowired
	private SmsUtils smsUtil;
	
	/**
	 * 4位长度验证码，有效期1分钟
	 * @param phone
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/sendUserBindCode", method = RequestMethod.POST, produces="application/json;charset=UTF-8")	
	@ResponseBody
    @ApiOperation(value = "发送用户绑定验证码", httpMethod = "POST", notes = "发送用户绑定验证码", response = Map.class,
            produces="application/json",consumes="application/json")
	public Map<String, Object> sendUserBindCode(@RequestBody Map<String,String> phone) throws Exception {
		Map<String, Object> map = Maps.newHashMap();		
		if(StringUtils.isNotEmpty(phone.get("phone"))){
			int ret = smsUtil.sendUserBindCode(phone.get("phone"));
			map.put("message", ret == 1 ? "":"获取短信验证码失败!");
			map.put("responseid", ret == 1 ? 1:0);
		}else{
			map.put("message","手机号不可为空!");
			map.put("responseid", 0);
		}
		return map;
	}

	@RequestMapping(value = "/checkUserBindCode", method = RequestMethod.POST, produces="application/json;charset=UTF-8")	
	@ResponseBody
    @ApiOperation(value = "校验用户绑定验证码", httpMethod = "POST", notes = "校验用户绑定验证码", response = Map.class,
            produces="application/json",consumes="application/json")
	public Map<String, Object> checkUserBindCode(@RequestBody Map<String,String> phoneAndCode) throws Exception {
		Map<String, Object> map = Maps.newHashMap();		
		boolean check = smsUtil.checkUserBindCode(phoneAndCode.get("phone"), phoneAndCode.get("expectCode"));
		map.put("message", check ? "验证成功":"验证失败!");
		map.put("responseid", check ? 1:0);
		return map;
	}
}
