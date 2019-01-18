/**
 * 
 */
package com.fijo.cores.cache;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import com.fijo.cores.utils.configs.CoreConfig;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.fijo.cores.model.GenericModel;
import com.fijo.cores.utils.configs.CoreConfig;

/**
 * 将相对静态的由数据库存储的数据加载到缓存服务器进行缓存
 * 
 * 默认实现以Id作为Key键，以对象作为Value值
 * 
 * @author zhangbo
 *
 */
public interface IGenericCache<V extends GenericModel<V>, K extends Serializable> {
	
	void initLoad() throws IllegalArgumentException, IllegalAccessException;

	
	/**
	 * 根据键，获取值
	 * @param k
	 * @return
	 */
	V loadByKey(K k);
	
	/**
	 * 根据一个唯一键，获取值
	 * @param k
	 * @return
	 */
	V loadByUnique(Object unique);
	
	/**
	 * 根据自定义的Key键和Key值，获取指定对象
	 * @param keyName
	 * @param keyValue
	 * @return
	 */
	V loadByCustom(String keyName, Object keyValue);
	
	/**
	 * 获取所有值
	 * @return
	 */
	Collection<V> getValues();
	
	/**
	 * 根据键，增加或替换值
	 * @param k
	 * @param v
	 */
	void saveOrUpdate(K k, V v);
	
	/**
	 * 根据键，删除值
	 * @param k
	 */
	void removeValue(K k);
	
	/**
	 * 根据键，批量删除值
	 * @param ks
	 */
	void removeValues(Object[] keys);
	
	/**
	 * 清空缓存
	 */
	void removeAll();

    List<V> multiGetByKey(Collection<K> keys);

    List<V> multiGetByUnique(Collection<Object> keys);

	/**
	 * 获取主键缓存Operations
	 * @return
	 */
	BoundHashOperations<String, K, V> getKeyHashOps();
	
	/**
	 * 获取唯一键缓存Operations
	 * @return
	 */
	BoundHashOperations<String, Object, V> getUniqueHashOps();

	RedisTemplate<String, V> getRedisTemplate();

	Field getId();

	Field getUnique();
	
	CoreConfig getCoreConfig();
}
