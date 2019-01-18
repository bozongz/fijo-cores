/**
 * 
 */
package com.fijo.cores.cache;

import java.io.Serializable;
import java.util.Collection;

import com.fijo.cores.model.GenericModel;

/**
 * 将相对静态的由数据库存储的数据加载到内存进行缓存
 * 
 * @author zhangbo
 *
 */
public interface ILogicCache<V extends GenericModel<V>, K extends Serializable> extends IGenericCache<V, K>{
	
	/**
	 * 根据键，批量替换可用性
	 * @param ks
	 * @param enabled
	 */
	public void updateEnabled(boolean enabled,Collection<K> ks);

}
