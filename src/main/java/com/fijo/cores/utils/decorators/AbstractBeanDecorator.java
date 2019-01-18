/**
 * 
 */
package com.fijo.cores.utils.decorators;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author zhangbo
 * 
 * 实现对一个bean的property属性值进行装饰替换
 * 
 */
public abstract class AbstractBeanDecorator implements IBeanDecorator{
	public final Log log = LogFactory.getLog(getClass());
	

}
