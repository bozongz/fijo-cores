/**
 * 
 */
package com.fijo.cores.web;

import javax.servlet.http.HttpServletResponse;

import com.fijo.cores.utils.configs.CoreConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.CookieGenerator;

import com.fijo.cores.utils.configs.CoreConfig;

/**
 * @author zhangbo
 *
 */
@Component
public class CustomCookieGenerator {

	@Autowired
	protected CoreConfig config;
	
	private static CookieGenerator generator = null;
	
	static{
		generator = new CookieGenerator();
		generator.setCookieMaxAge(CookieGenerator.DEFAULT_COOKIE_MAX_AGE);
	}
	
	public void createCookieName(HttpServletResponse response, String cookieName, String cookieValue){
		//开发调试期间不保存Cookie
		if(!Boolean.valueOf(config.getValue("app.debug"))){
			generator.setCookieName(cookieName);
            generator.setCookieHttpOnly(true);
            generator.setCookieSecure(true);
			generator.addCookie(response, cookieValue);		
		}
	}
}
