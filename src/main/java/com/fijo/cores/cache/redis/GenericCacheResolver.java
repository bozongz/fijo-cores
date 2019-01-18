/**
 * 
 */
package com.fijo.cores.cache.redis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fijo.cores.utils.Constants;
import com.fijo.cores.utils.configs.CoreConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.stereotype.Component;

import com.fijo.cores.utils.Constants;
import com.fijo.cores.utils.configs.CoreConfig;

/**
 * https://jira.spring.io/browse/SPR-10778 由于该特性还未得到Spring官方支持，因此自定义实现
 * 
 * SpringCache 只适用于一些简单的场景，比如选择用户部门树。对于一些复杂的场景如：缓存只维护一份value对应多key，通过不同的key更新value时需要进行同步；
 * 再例如缓存对象是集合和哈希，一个元素的更新不能清空整个缓存对象等。
 * 
 * @author zhangbo
 *
 */
@Component
public class GenericCacheResolver implements CacheResolver {
	@Autowired
	private CacheManager cacheManager;

	@Autowired
	protected CoreConfig coreConfig;
	
	@Override
	public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
		List<Cache> caches = new ArrayList<Cache>();
		for (String cacheName : context.getOperation().getCacheNames()) {
			caches.add(cacheManager.getCache(coreConfig.getCtx()+Constants.COLON+cacheName));
		}
		return caches;
	}

}
