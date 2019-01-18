package com.fijo.cores.web.initializer;

import java.io.*;

import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.utils.configs.CoreConfig;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.utils.configs.CoreConfig;

@Component
public class LoadPostProcessor implements BeanPostProcessor {
	public static transient final Log log = LogFactory.getLog(LoadPostProcessor.class);
	private static File beanFile = new File(System.getProperty("user.dir")+"/LoadPostProcessor.txt");

	@Autowired
	protected CoreConfig config;
	
	static {
		try {
			FileUtils.forceDeleteOnExit(beanFile);
			FileUtils.touch(beanFile);
		} catch (IOException e) {
			log.error(Exceptions.getStackTraceAsString(e));
		}
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		if(Boolean.valueOf(config.getValue("app.debug"))){
            log.debug("load bean of name "+beanName+" successfully......");
			if (beanFile.exists()) {
                FileWriter fileWritter = null;
                try {
                    fileWritter = new FileWriter(beanFile.getName(),true);
                    BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
                    bufferWritter.write(beanName+"\r\n");
                    bufferWritter.close();
                }catch (IOException e) {
				}finally {
                    try {
                        if (null != fileWritter)
                            fileWritter.close();
                    } catch (IOException e) {
                    }
                }
            }
		}
		return bean;
	}
}
