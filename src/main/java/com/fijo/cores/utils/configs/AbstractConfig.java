package com.fijo.cores.utils.configs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import com.fijo.cores.utils.Constants;
import org.springframework.util.ResourceUtils;

import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.utils.Constants;

/**
 * 读取系统配置属性
 * 
 * @author zhangbo
 *
 */
public abstract class AbstractConfig {

	private static Properties prop=new Properties();
	
	public static void loadConfigFile(String classPathFileName) {
		try {
			File rcFile = ResourceUtils.getFile(classPathFileName);
			if(rcFile.exists()){				
		    	try {
					prop.load(new InputStreamReader(new FileInputStream(rcFile), Constants.CHARSET));
				} catch (IOException e) {
					Exceptions.printException(e);
				}
			}
		} catch (FileNotFoundException e) {
			Exceptions.printException(e);
		}
	}
	
	public String getValue(String key) {
		return prop.getProperty(key);
	}

	/**
	 * @return the prop
	 */
	public static Properties getProp() {
		return prop;
	}

	
}
