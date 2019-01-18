/**
 * 
 */
package com.fijo.cores.utils.decorators;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fijo.cores.admin.authority.model.SysUser;
import com.fijo.cores.admin.authority.service.ISysUserAdvanceService;
import com.fijo.cores.exceptions.Exceptions;

/**
 * 通过SysUser的主键ID或者userCode获取中文姓名username
 * 
 * @author zhangbo
 *
 */
@Component
public class SysUserDecorator extends AbstractBeanDecorator {

	@Autowired
	private ISysUserAdvanceService sysUserAdvanceService;
	
	@Override
	public void decorate(Object bean, String property, Object strategy) {		
		try {
			Object key = PropertyUtils.getProperty(bean, property);		
			if(key != null){
				SysUser sysUser = null;
				if(key instanceof Integer)
					sysUser = sysUserAdvanceService.loadByKey((Integer)key);	
				if(key instanceof String)
					sysUser = sysUserAdvanceService.loadByUnique((String)key);	
				if(sysUser != null)
					PropertyUtils.setProperty(bean, (String)strategy, sysUser.getUsername());				
			}
		} catch (NullPointerException | IllegalAccessException | InvocationTargetException
				| NoSuchMethodException e) {			
			log.error(Exceptions.getStackTraceAsString(e));
		}
	}

}
