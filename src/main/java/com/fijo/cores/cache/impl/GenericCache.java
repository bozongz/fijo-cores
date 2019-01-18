/**
 * 
 */
package com.fijo.cores.cache.impl;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.persistence.Id;

import com.fijo.cores.cache.IGenericCache;
import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.service.IGenericService;
import com.fijo.cores.utils.Constants;
import com.fijo.cores.utils.ObjectUtil;
import com.fijo.cores.utils.annotations.Unique;
import com.fijo.cores.utils.configs.CoreConfig;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.fijo.cores.cache.IGenericCache;
import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.model.GenericModel;
import com.fijo.cores.service.IGenericService;
import com.fijo.cores.utils.Constants;
import com.fijo.cores.utils.ObjectUtil;
import com.fijo.cores.utils.annotations.Unique;
import com.fijo.cores.utils.configs.CoreConfig;

/**
 * 将相对静态的由数据库存储的数据加载到内存进行缓存
 * 
 * 默认实现以Id作为Key键，以对象作为Value值
 * 
 * @author zhangbo
 *
 */
public class GenericCache<V extends GenericModel<V>, K extends Serializable> implements IGenericCache<V,K> {
	public transient final Log log = LogFactory.getLog(getClass());
	
	private IGenericService<V, K> service;
	private Class<V> clazz;
	
	@Autowired
	protected CoreConfig coreConfig;
	
	@Autowired
	protected RedisTemplate<String, V> redisTemplate;
	
	protected BoundHashOperations<String, K, V> keyHashOps;
	
	protected BoundHashOperations<String, Object, V> uniqueHashOps;
	
	protected Set<String> customKeys = Sets.newHashSet();
	protected Map<String, BoundHashOperations<String, Object, V>> customHashOpsHolder = Maps.newHashMap(); //在构造函数进行注册
	
	protected Field id = null;
	
	protected Field unique = null;
	
	/**
	 * 通过构造函数注入service
	 */
	public GenericCache(IGenericService<V, K> service) {
		this.service = service;
	}

	/**
	 * 构造函数进行注册特殊的Key
	 * @param key
	 */
	public void registerCustomkey(String key){
		customKeys.add(key);
	}
	
	@SuppressWarnings("unchecked")
	@PostConstruct
	public void initLoad() throws IllegalArgumentException, IllegalAccessException{	
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(Id.class)) {
				id = field;
			}
			if (field.isAnnotationPresent(Unique.class)) {
				unique = field;
			}
		}
		//主键缓存空间
		this.keyHashOps = redisTemplate.boundHashOps(coreConfig.getCtx()+Constants.COLON+clazz.getSimpleName()+Constants.COLON+id.getName());
		setKeyHashOps(keyHashOps);	
		
		//默认唯一键缓存空间
		if(unique != null){
			this.uniqueHashOps = redisTemplate.boundHashOps(coreConfig.getCtx()+Constants.COLON+clazz.getSimpleName()+Constants.COLON+unique.getName());
			setUniqueHashOps(uniqueHashOps);
		}
		
		//自定义字段缓存空间
		if(customKeys.size() > 0){
			for(String keyName: customKeys){
				BoundHashOperations<String, Object, V> customHashOps = redisTemplate.boundHashOps(coreConfig.getCtx()+Constants.COLON+clazz.getSimpleName()+Constants.COLON+keyName);				
				customHashOpsHolder.put(coreConfig.getCtx()+Constants.COLON+clazz.getSimpleName()+Constants.COLON+keyName, customHashOps);
			}
		}

		Collection<V> itemCollection = service.getAll();
		for (V o : itemCollection){
            doSometingForEachObject(o);
			id.setAccessible(true);
			getKeyHashOps().put((K) id.get(o), o);//主键缓存
			if(unique != null){
				unique.setAccessible(true);
				if(unique.get(o) != null)
					getUniqueHashOps().put(unique.get(o), o);//默认唯一键缓存
			}
			for(String keyName: customKeys){
				Field customKeyField = ObjectUtil.getIndicateField(o, keyName);
				if(customKeyField != null){
					customKeyField.setAccessible(true);
					if(customKeyField.get(o) != null){
						customHashOpsHolder.get(coreConfig.getCtx()+Constants.COLON+clazz.getSimpleName()+Constants.COLON+keyName).put(customKeyField.get(o), o);//自定义字段缓存
					}
				}
			}
		}
        doSometingAfterLoad(itemCollection);
	}

    public void doSometingForEachObject(V o){
        // generic cache do nothing
    }

    public void doSometingAfterLoad(Collection<V> itemCollection){
        // generic cache do nothing
    }
	
	/**
	 * 根据键，获取值
	 * @param k
	 * @return
	 */
	@Override
	public V loadByKey(K k){
		if(k != null){
			V v = getKeyHashOps().get(k);
			log.debug(String.format("@GenericCache get cache value with key: %s and value: %s", k,v));
			return v;
		}else{
			log.warn("@GenericCache cant not get cache value with empty key!");
			return null;
		}
	}

	@Override
	public V loadByUnique(Object unique) {
		if(unique != null){
			V v = getUniqueHashOps().get(unique);
			log.debug(String.format("@GenericCache get cache value with unique: %s and value: %s", unique,v));
			return v;
		}else{
			log.warn("@GenericCache cant not get cache value with empty unique!");
			return null;
		}
	}

	@Override
	public V loadByCustom(String keyName, Object keyValue){
		if(StringUtils.isNotEmpty(keyName) && keyValue!=null){
			V v = null;
			BoundHashOperations<String, Object, V> customHashOps = customHashOpsHolder.get(coreConfig.getCtx()+Constants.COLON+clazz.getSimpleName()+Constants.COLON+keyName);
			if(customHashOps != null)
				v = customHashOps.get(keyValue);
			log.debug(String.format("@GenericCache get cache value with custom key name: %s and key value: %s and return value: %s ", keyName, keyValue, v));
			return v;
		}else{
			log.warn(String.format("@GenericCache cant not get cache value with empty keyName %s and empty keyValue %s",keyName,keyValue));
			return null;
		}
	}
	
	/**
	 * 获取所有值
	 * @return
	 */
	@Override
	public Collection<V> getValues(){
		Collection<V> values = getKeyHashOps().values();
		log.debug(String.format("@GenericCache get all cache values: %s", values));
		if (values instanceof List) {
			Collections.sort((List<V>) values);
		}
		return values;
	}
	
	/**
	 * 根据键，增加或替换值
	 * @param k
	 * @param v
	 */
	@Override
	public void saveOrUpdate(K k, V v){
		log.debug("@GenericCache saveOrUpdate cache value with key: "+k+" , and value with: "+v);
		getKeyHashOps().put(k, v); //更新主键缓存
		
		Field unique = ObjectUtil.getUniqueField(v); //更新默认唯一键缓存
		if(unique != null){
			unique.setAccessible(true);
			try {
				if(unique.get(v) != null)
					getUniqueHashOps().put(unique.get(v), v);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				log.error("@GenericCache saveOrUpdate cache value failed with key: "+k+" , and value with: "+v);
			}
		}
		
		for(String keyName: customKeys){ //更新自定义字段缓存
			Field customKeyField = ObjectUtil.getIndicateField(v, keyName);
			if(customKeyField != null){
				customKeyField.setAccessible(true);
				try {
					if(customKeyField.get(v) != null){
						customHashOpsHolder.get(coreConfig.getCtx()+Constants.COLON+clazz.getSimpleName()+Constants.COLON+keyName).put(customKeyField.get(v), v);
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					log.error("@GenericCache saveOrUpdate cache value failed with key: "+k+" , and value with: "+v);
				}
			}
		}
	}
	
	/**
	 * 根据键，删除值
	 * @param k
	 */
	@Override
	public void removeValue(K k){
		log.debug("@GenericCache remove cache value with key: "+k);
		V v = getKeyHashOps().get(k);
		if(v != null){
			getKeyHashOps().delete(k); //删除主键缓存
			
			Field unique = ObjectUtil.getUniqueField(v); //删除默认唯一键缓存
			if(unique != null){
				unique.setAccessible(true);
				try {
					if(unique.get(v) != null)
						getUniqueHashOps().delete(unique.get(v));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					Exceptions.printException(e);
				}
			}
			
			for(String keyName: customKeys){ //删除自定义字段缓存
				Field customKeyField = ObjectUtil.getIndicateField(v, keyName);
				if(customKeyField != null){
					customKeyField.setAccessible(true);
					try {
						if(customKeyField.get(v) != null){
							customHashOpsHolder.get(coreConfig.getCtx()+Constants.COLON+clazz.getSimpleName()+Constants.COLON+keyName).delete(customKeyField.get(v));
						}
					} catch (IllegalArgumentException | IllegalAccessException e) {
						log.error("@GenericCache removeValue cache value failed with key: "+k+" , and value with: "+v);
					}
				}
			}
		}
	}
	
	/**
	 * 根据键，批量删除值
	 * @param ks
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void removeValues(Object[] keys){ 
		log.debug("@GenericCache remove all cache values with keys: "+keys);
		Set<V> delObjects = Sets.newHashSet();
		for(Object key : keys){ //记录待删除的所有对象
			delObjects.add(loadByKey((K) key));
		}

		if(delObjects.size() > 0){
			getKeyHashOps().delete(keys);//删除主键缓存
			
			
			if(unique != null){ //删除默认唯一键缓存
				Set<Object> delUniqueKeys = Sets.newHashSet();
				for(Object del:delObjects){
					try {
						unique.setAccessible(true);
						if(unique.get(del) != null)
							delUniqueKeys.add(unique.get(del));
					} catch (IllegalArgumentException | IllegalAccessException e) {
					}
				}
				if(delUniqueKeys.size() > 0)
					getUniqueHashOps().delete(delUniqueKeys.toArray());
			}
			
			for(String keyName: customKeys){ //删除自定义字段缓存
				Set<Object> delCustomeKeys = Sets.newHashSet();
				for(Object del:delObjects){
					try {
						Field customKeyField = ObjectUtil.getIndicateField((GenericModel<?>) del, keyName);
						customKeyField.setAccessible(true);
						if(customKeyField.get(del) != null)
							delCustomeKeys.add(customKeyField.get(del));
					} catch (IllegalArgumentException | IllegalAccessException e) {
					}
				}
				if(delCustomeKeys.size() > 0)
					customHashOpsHolder.get(coreConfig.getCtx()+Constants.COLON+clazz.getSimpleName()+Constants.COLON+keyName).delete(delCustomeKeys.toArray());
			}
		}
	}	

	/**
	 * 清空缓存
	 */
	@Override
	public void removeAll(){
		log.debug("@GenericCache remove all cache");
		Set<K> keys = getKeyHashOps().keys();
		if(keys.size() > 0){
			getKeyHashOps().delete(keys.toArray());//删除主键缓存
			
			if(unique != null){ //删除默认唯一键缓存
				getUniqueHashOps().delete(getUniqueHashOps().keys().toArray());
			}		
			
			for(String keyName: customKeys){ //删除自定义字段缓存
				BoundHashOperations<String, Object, V> map = customHashOpsHolder.get(keyName);
				if(map != null)
					map.delete(map.keys().toArray());
			}
		}
	}


    @Override
    public List<V> multiGetByKey(Collection<K> keys){
        return getKeyHashOps().multiGet(keys);
    }

    @Override
    public List<V> multiGetByUnique(Collection<Object> keys){
        return getUniqueHashOps().multiGet(keys);
    }

	/**
	 * @return the clazz
	 */
	public Class<V> getClazz() {
		return clazz;
	}

	/**
	 * @param clazz the clazz to set
	 */
	public void setClazz(Class<V> clazz) {
		this.clazz = clazz;
	}

	/**
	 * @return the service
	 */
	public IGenericService<V, K> getService() {
		return service;
	}

	/**
	 * @param service the service to set
	 */
	public void setService(IGenericService<V, K> service) {
		this.service = service;
	}

	/**
	 * @return the keyHashOps
	 */
	@Override
	public BoundHashOperations<String, K, V> getKeyHashOps() {
		return keyHashOps;
	}

	/**
	 * @param keyHashOps the keyHashOps to set
	 */
	public void setKeyHashOps(BoundHashOperations<String, K, V> keyHashOps) {
		this.keyHashOps = keyHashOps;
	}

	/**
	 * @param uniqueHashOps the uniqueHashOps to set
	 */
	public void setUniqueHashOps(
			BoundHashOperations<String, Object, V> uniqueHashOps) {
		this.uniqueHashOps = uniqueHashOps;
	}

	/**
	 * @return the uniqueHashOps
	 */
	@Override
	public BoundHashOperations<String, Object, V> getUniqueHashOps() {
		return uniqueHashOps;
	}

	/**
	 * @return the redisTemplate
	 */
	@Override
	public RedisTemplate<String, V> getRedisTemplate() {
		return redisTemplate;
	}

	/**
	 * @return the id
	 */
	@Override
	public Field getId() {
		return id;
	}

	/**
	 * @return the unique
	 */
	@Override
	public Field getUnique() {
		return unique;
	}

	/**
	 * @return the coreConfig
	 */
	@Override
	public CoreConfig getCoreConfig() {
		return coreConfig;
	}

	
}
