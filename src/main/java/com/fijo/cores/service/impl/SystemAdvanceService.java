/**
 * 
 */
package com.fijo.cores.service.impl;

import java.io.Serializable;

import com.fijo.cores.model.SystemModel;
import com.fijo.cores.cache.IGenericCache;
import com.fijo.cores.model.SystemModel;
import com.fijo.cores.service.ISystemAdvanceService;
import com.fijo.cores.service.ISystemService;

/**
 * 系统实体通用服务层与缓存的集成高级服务层
 * 
 * @author zhangbo
 *
 * @param <K>
 * @param <V>
 */
public class SystemAdvanceService<V extends SystemModel<V>, K extends Serializable>
		extends GenericAdvanceService<V, K> implements ISystemAdvanceService<V, K> {

	private ISystemService<V, K> systemService;
	
	private IGenericCache<V, K> cacheService;
	
	public SystemAdvanceService(ISystemService<V, K> systemService,
			IGenericCache<V, K> cacheService) {
		super(systemService, cacheService);
		this.systemService = systemService;
		this.cacheService = cacheService;
	}


}
