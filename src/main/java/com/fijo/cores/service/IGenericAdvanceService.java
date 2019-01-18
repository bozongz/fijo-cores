/**
 * 
 */
package com.fijo.cores.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.fijo.cores.cache.IGenericCache;
import com.fijo.cores.model.GenericModel;

/**
 * 同时具备数据库CURD操作逻辑与缓存逻辑的高级抽象业务类
 * 
 * @author zhangbo
 *
 */
public interface IGenericAdvanceService<V extends GenericModel<V>, K extends Serializable>
		extends IGenericService<V, K>, IGenericCache<V, K> {

	IGenericService<V, K> getGenericService();

	IGenericCache<V, K> getCacheService();

    List<V> multiGetByKey(Collection<K> keys);

    List<V> multiGetByUnique(Collection<Object> keys);
}
