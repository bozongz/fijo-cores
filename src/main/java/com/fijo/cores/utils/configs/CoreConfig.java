package com.fijo.cores.utils.configs;

import java.util.Map;

import javax.annotation.PostConstruct;

import com.fijo.cores.utils.DateUtil;
import com.fijo.cores.utils.PropertiesUtils;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.utils.DateUtil;
import com.fijo.cores.utils.PropertiesUtils;

@Component
public class CoreConfig extends AbstractConfig{
	
    // 百度地图API
    public static final String baiduMapAk;

	static {
        CoreConfig.initRestartTimes();
		CoreConfig.loadConfigFile("classpath:config.properties");
        CoreConfig.loadConfigFile("classpath:jdbc.properties");
        CoreConfig.loadConfigFile("classpath:redis.properties");
		CoreConfig.loadConfigFile("classpath:mail.properties");
		baiduMapAk = getProp().getProperty("bae.lbs.map.ak");
	}

	public static String getBaiduMapAk() {
		return baiduMapAk;
	}
	
	public String getCtx(){
		return StringUtils.removeEnd(getValue("app.root"), ".root");
	}
	
	/**
	 * 启动时先修改配置文件后，再加载该配置文件
	 */
	public static void initRestartTimes(){
		Map<String,String> keyValues = Maps.newHashMap();
		try {
			String today = PropertiesUtils.getValue("config.properties", "app.today");
			Integer times = Integer.valueOf(PropertiesUtils.getValue("config.properties", "app.restart.times"));
			if(today.equals(DateUtil.getToday())){
				times++;
				keyValues.put("app.restart.times", String.valueOf(times));				
			}else{
				times = 1;
				keyValues.put("app.restart.times", String.valueOf(times));
				keyValues.put("app.today", DateUtil.getToday());
			}
			PropertiesUtils.modifyProperties("config.properties", keyValues);
		} catch (ConfigurationException e) {
			Exceptions.printException(e);
		}
	}

}
