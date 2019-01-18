/**
 * 
 */
package com.fijo.cores.service.impl;

import java.io.Serializable;
import java.util.Collection;

import com.fijo.cores.service.ILogicAdvanceService;
import com.fijo.cores.cache.IGenericCache;
import com.fijo.cores.model.LogicModel;
import com.fijo.cores.service.ILogicAdvanceService;
import com.fijo.cores.service.ILogicService;

/**
 * 业务实体通用服务层与缓存的集成高级服务层
 * @author zhangbo
 *
 * @param <K>
 * @param <V>
 */
public class LogicAdvanceService<V extends LogicModel<V>, K extends Serializable> 
	extends SystemAdvanceService<V, K> implements ILogicAdvanceService<V, K> {

	private ILogicService<V, K> logicService;
	
	public LogicAdvanceService(ILogicService<V, K> logicService,
			IGenericCache<V, K> cacheService) {
		super(logicService, cacheService);
		this.logicService = logicService;
	}

	@Override
	public int updateEnable(boolean enabled, Collection<K> ids) {		
		log.debug("@Logic Advance Service make enable objects : "+enabled+" by "+ ids);
		int ret = logicService.updateEnable(enabled, ids);
		if(ret > 0){
			for(K id:ids){
				V o = logicService.getById(id);
				saveOrUpdate(id, o);
			}
		}
		return ret;
	}

}
