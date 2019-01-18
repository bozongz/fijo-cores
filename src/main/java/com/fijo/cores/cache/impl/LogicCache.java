/**
 * 
 */
package com.fijo.cores.cache.impl;

import java.io.Serializable;
import java.util.Collection;

import com.fijo.cores.service.ILogicService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fijo.cores.cache.ILogicCache;
import com.fijo.cores.model.LogicModel;
import com.fijo.cores.service.ILogicService;

/**
 * 将相对静态的由数据库存储的数据加载到缓存服务器进行缓存
 * 
 * 默认实现以Id作为Key键，以对象作为Value值
 * 
 * @author zhangbo
 *
 */
public class LogicCache<V extends LogicModel<V>, K extends Serializable> 
	extends GenericCache<V, K> implements ILogicCache<V, K> {

	public final Log log = LogFactory.getLog(getClass());

	@SuppressWarnings("unused")
	private ILogicService<V, K> service;

	public LogicCache(ILogicService<V, K> service) {		
		super(service);
		this.service = service;
	}
	
	/**
	 * 根据键，批量替换可用性
	 * @param ks
	 * @param enabled
	 */
	public void updateEnabled(boolean enabled, Collection<K> ks){
		log.debug("@ update all cache values with keys: "+ks+" for "+enabled);
		for(K k : ks){
			V v = loadByKey(k);
			v.setEnabled(enabled);
			saveOrUpdate(k, v);
		}		
	}
	
}
