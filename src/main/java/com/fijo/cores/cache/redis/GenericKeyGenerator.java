/**
 * 
 */
package com.fijo.cores.cache.redis;

import java.lang.reflect.Method;

import com.fijo.cores.utils.Constants;
import com.fijo.cores.utils.configs.CoreConfig;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import com.fijo.cores.utils.Constants;
import com.fijo.cores.utils.configs.CoreConfig;

/**
 * 自定义Redis中对象的key
 * 
 * SpringCache 只适用于一些简单的场景，比如选择用户部门树。对于一些复杂的场景如：缓存只维护一份value对应多key，通过不同的key更新value时需要进行同步；
 * 再例如缓存对象是集合和哈希，一个元素的更新不能清空整个缓存对象等。
 * 
 * @author zhangbo
 *
 */
@Component
public class GenericKeyGenerator implements KeyGenerator {
	public final Log log = LogFactory.getLog(getClass());
	
	@Autowired
	protected CoreConfig coreConfig;
	
	@Override
	public Object generate(Object target, Method method, Object... params) {		
		StringBuffer sb = new StringBuffer();
		for(Object param : params){
			if(param != null){
				sb.append(param.toString()).append(Constants.COLON);
			}
		}
		String key = coreConfig.getCtx()+Constants.COLON+target.getClass().getSimpleName()+
				Constants.COLON+method.getName()+Constants.COLON+StringUtils.removeEnd(sb.toString(), Constants.COLON);
		log.debug("Saved key: "+key);
		return key;
	}
}
