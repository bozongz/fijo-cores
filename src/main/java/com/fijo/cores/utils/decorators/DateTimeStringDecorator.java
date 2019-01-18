/**
 * 
 */
package com.fijo.cores.utils.decorators;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Component;

import com.fijo.cores.exceptions.Exceptions;

/**
 * @author zhangbo
 * 
 */
@Component
public class DateTimeStringDecorator extends AbstractBeanDecorator {

	private String defaultStrategy = "yyyy-MM-dd HH:mm:ss";

	@Override
	public void decorate(Object bean, String property, Object strategy) {
		String format = strategy == null ? defaultStrategy : strategy
				.toString();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Object oldValue = null;
		Object newValue = null;
		try {
			oldValue = PropertyUtils.getProperty(bean, property);
			if (oldValue instanceof Date) {
				newValue = oldValue;
			} else if (oldValue instanceof String) {
				Date date = sdf.parse((String) oldValue);
				newValue = sdf.format(date);
			}
			PropertyUtils.setProperty(bean, property, newValue);
		} catch (IllegalAccessException e) {
			log.error(Exceptions.getStackTraceAsString(e));
		} catch (InvocationTargetException e) {
			log.error(Exceptions.getStackTraceAsString(e));
		} catch (NoSuchMethodException e) {
			log.error(Exceptions.getStackTraceAsString(e));
		} catch (ParseException e) {
			log.error(Exceptions.getStackTraceAsString(e));
		}
	}

}
