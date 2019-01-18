package com.fijo.cores.utils;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 属性文件公用类
 * 
 * @author zhangbo
 * 
 */
public class PropertiesUtils {
	private static transient final Log log = LogFactory.getLog(PropertiesUtils.class);
	
	/**
	 * 
	 * @param fileName config.properties
	 * @return
	 * @throws ConfigurationException
	 */
	public static PropertiesConfiguration getConfig(String fileName) throws ConfigurationException{
		PropertiesConfiguration config = new PropertiesConfiguration(fileName);
		return config;
	}

	/**
	 * 
	 * @param fileName config.properties
	 * @param key
	 * @return
	 * @throws ConfigurationException
	 */
	public static String getValue(String fileName, String key) throws ConfigurationException{
		PropertiesConfiguration config = new PropertiesConfiguration(fileName);
		return (String)config.getProperty(key);
	}

	/**
	 * 
	 * @param fileName config.properties
	 * @param keyValues
	 * @throws ConfigurationException
	 */
	public static void modifyProperties(String fileName, Map<String,String> keyValues) throws ConfigurationException{
		PropertiesConfiguration config = new PropertiesConfiguration(fileName);
		Iterator<String> iter = keyValues.keySet().iterator(); 
		while (iter.hasNext()) { 
			String key = iter.next(); 
		    config.setProperty(key, keyValues.get(key));
		}
		config.save();
	}
}
