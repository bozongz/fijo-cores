/**
 * 
 */
package com.fijo.cores.utils.decorators;

import com.fijo.cores.utils.enums.GenericEnum;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Component;

import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.utils.enums.GenericEnum;

/**
 * @author zhangbo
 *
 */
@Component
public class EnumsDecorator extends AbstractBeanDecorator {
	
	@Override
	public void decorate(Object bean, String property, Object strategy) {
		try {
			GenericEnum someEnum = (GenericEnum) PropertyUtils.getProperty(bean, property);
			if (someEnum != null) {
				PropertyUtils.setProperty(bean, (String)strategy, someEnum.getValue());
			}
		} catch (Exception e) {
			log.error(Exceptions.getStackTraceAsString(e));
		}
	}
}
