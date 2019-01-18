package com.fijo.cores.service.impl;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fijo.cores.utils.Constants;
import com.fijo.cores.utils.ObjectUtil;
import com.fijo.cores.utils.configs.CoreConfig;
import com.fijo.cores.utils.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.fijo.cores.cache.IGenericCache;
import com.fijo.cores.model.GenericModel;
import com.fijo.cores.service.IGenericAdvanceService;
import com.fijo.cores.service.IGenericService;
import com.fijo.cores.utils.ObjectUtil;
import com.fijo.cores.utils.configs.CoreConfig;

/**
 * 基础实体通用服务层与缓存的集成高级服务层
 * @author zhangbo
 *
 * @param <K>
 * @param <V>
 */
@CacheConfig(cacheNames = {"runtime:"}, cacheResolver="genericCacheResolver", keyGenerator="genericKeyGenerator")
public class GenericAdvanceService<V extends GenericModel<V>, K extends Serializable>
		implements IGenericAdvanceService<V, K> {
	public transient final Log log = LogFactory.getLog(getClass());

	private IGenericService<V, K> genericService;
	
	private IGenericCache<V, K> cacheService;

	public GenericAdvanceService(IGenericService<V, K> genericService,
			IGenericCache<V, K> cacheService) {
		super();
		this.genericService = genericService;
		this.cacheService = cacheService;
	}

	@Override
	public void initLoad() throws IllegalArgumentException,
			IllegalAccessException {
		getCacheService().initLoad();
	}

	/**
	 * 从DB查询
	 */
	@Override
	public V getById(K id) {
		log.debug("@Generic Advance Service get single object by id: " + id);
		return getGenericService().getById(id);
	}

	/**
	 * 从DB查询
	 */
	@Override
	public V getByUnique(Object unique) {
		log.debug("@Generic Advance Service get single object by unique: " + unique);
		return getGenericService().getByUnique(unique);
	}

	/**
	 * 从缓存查询
	 */
	@Override
	public V loadByKey(K k) {
		log.debug("@Generic Advance Service get cache value with key: "+k);
		return getCacheService().loadByKey(k);
	}
	
	/**
	 * 从缓存查询
	 */
	@Override
	public V loadByUnique(Object unique) {
		log.debug("@Generic Advance Service get cache value with unique: "+unique);
		return getCacheService().loadByUnique(unique);
	}

	/**
	 * 从缓存查询
	 */
	@Override
	public V loadByCustom(String keyName, Object keyValue) {
		log.debug(String.format("@Generic Advance Service get cache value with custom key name: %s and key value: %s ", keyName, keyValue));
		return getCacheService().loadByCustom(keyName, keyValue);
	}
	
	@Override
	public V getLast() {
		log.debug("@Generic Advance Service get last object");
		return getGenericService().getLast();
	}

	@Override
	public V getOne(V o) {
		log.debug("@Generic Advance Service get one object by: " + o);
		return getGenericService().getOne(o);
	}

	@Override
	public Collection<V> getAll() {
		log.debug("@Generic Advance Service get all objects.");
		return getCacheService().getValues();
	}

	@Override
	public Collection<V> getAll(V o) {
		log.debug("@Generic Advance Service get all objects by object: " + o);
		return getGenericService().getAll(o);
	}

	@Override
	public Collection<V> getAll(RowBounds rowBounds) {
		log.debug("@Generic Advance Service get all objects offset: " + rowBounds.getOffset()
				+ " limit " + rowBounds.getLimit());
		return getGenericService().getAll(rowBounds);
	}

	@Override
	public Collection<V> getAll(V o, RowBounds rowBounds) {
		log.debug("@Generic Advance Service get all objects by object: " + o + " offset: "
				+ rowBounds.getOffset() + " limit " + rowBounds.getLimit());
		return getGenericService().getAll(o, rowBounds);
	}

	@Override
	public Collection<V> getAll(Map<String, Object> params) {
		log.debug("@Generic Advance Service get all objects by params: " + params);
		return getGenericService().getAll(params);
	}

	@Override
	public Collection<V> getAll(Map<String, Object> params, RowBounds rowBounds) {
		log.debug("@Generic Advance Service get all objects by params: " + params + " offset: "
				+ rowBounds.getOffset() + " limit " + rowBounds.getLimit());
		return getGenericService().getAll(params, rowBounds);
	}

	@Override
	public Collection<V> queryAnyway(Map<String, Object> params) {
		return getGenericService().queryAnyway(params);
	}
	
	@Override
	public Collection<V> queryAnyway(V o, RowBounds rowBounds) {
		return getGenericService().queryAnyway(o, rowBounds);
	}

	@Override
	public Integer getCount(V o) {
		log.debug("@Generic Advance Service get counter by object: " + o);
		return getGenericService().getCount(o);
	}

	@Override
	public Integer getCount(Map<String, Object> params) {
		log.debug("@Generic Advance Service get counter by params: " + params);
		return getGenericService().getCount(params);
	}

	@Override
	public int create(V o) {
		log.debug("@Generic Advance Service create object: " + o);
		int ret = getGenericService().create(o);
		if(ret > 0){
			saveOrUpdate(o);
		}
		return ret;
	}

	@Override
	public int batchCreate(Collection<V> os) {
		log.debug("@Generic Advance Service batch create objects: " + os);
		int ret = getGenericService().batchCreate(os);
		if(ret > 0){
			for(V o:os){
				saveOrUpdate(o);
			}
		}
		return ret;
	}

	@Override
	public int update(V o) {
		log.debug("@Generic Advance Service update object: " + o);
		int ret = getGenericService().update(o);
		if(ret > 0){			
			saveOrUpdate(o);
		}
		return ret;
	}

	@Override
	public int update(Map<String, Object> params) {
		log.debug("@Generic Advance Service update object by params: " + params);
		int ret = getGenericService().update(params);
		if(ret > 0){
			Collection<V> os = getAll(params);
			for(V o:os){
				saveOrUpdate(o);
			}
		}
		return ret;
	}

	@Override
	public int batchUpdate(Collection<V> os) {
		log.debug("@Generic Advance Service batch update objects: " + os);
		int ret = getGenericService().batchUpdate(os);
		if(ret > 0){
			for(V o:os){
				saveOrUpdate(o);
			}
		}
		return ret;
	}

	@Override
	public int delete(K id) {
		log.debug("@Generic Advance Service delete object by id: " + id);
		int ret = getGenericService().delete(id);
		if(ret > 0){
			removeValue(id);
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int delete(V o) {
		log.debug("@Generic Advance Service delete objects by object: " + o);
        Collection<V> os = getGenericService().getAll(o);
		int ret =  getGenericService().delete(o);
		if(ret > 0){
            Collection ids = ObjectUtil.getIdVaueList(os);
            removeValues(ids.toArray());
		}
		return ret;
	}

	@Override
	public int batchDelete(Set<K> ids) {
		log.debug("@Generic Advance Service batch delete objects by ids: " + ids);
		int ret =  getGenericService().batchDelete(ids);
		if(ret > 0){						
			removeValues(ids.toArray());
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int batchDelete(Collection<V> os) {
		log.debug("@Generic Advance Service batch logic delete objects: " + os);
        Collection<Object> idList = ObjectUtil.getIdVaueList(os);
        int ret =  getGenericService().batchDelete(os);
		if(ret > 0){
			if(idList.size() > 0)
				removeValues(idList.toArray());
		}
		return ret;
	}
	
	@Override
	public void selectCustom(Object parameter, ResultHandler handler) {
		log.debug("@Generic Advance Service execute custom action: " + parameter);
		getGenericService().selectCustom(parameter, handler);
	}

	@Override
	public Collection<V> getValues() {
		log.debug("@Generic Advance Service get all cache values.");
		return getCacheService().getValues();
	}
	
	@SuppressWarnings("unchecked")
	public void saveOrUpdate(V v) {
        saveOrUpdate((K) ObjectUtil.getIdVaue(v), v);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void saveOrUpdate(K k, V v) {
		log.debug("@Generic Advance Service update cache value with key: "+k+" , and value with: "+v);
        v = getGenericService().getById((K)ObjectUtil.getIdVaue(v));
        getCacheService().saveOrUpdate(k, v);
	}
	
	@Override
	public void removeValue(K k) {
		log.debug("@Generic Advance Service remove cache value with key: "+k);
		getCacheService().removeValue(k);
	}

    @Override
    public void removeValues(Object[] keys) {
        //log.debug("@Generic Advance Service remove all cache values with keys: "+ Arrays.toString(keys));
        getCacheService().removeValues(keys);
    }

	@Override
	public void removeAll() {
		log.debug("@Generic Advance Service remove all cache");
		getCacheService().removeAll();
	}

    @Override
    public List<V> multiGetByKey(Collection<K> keys){
        StringBuffer sb = new StringBuffer();
        for(K key:keys){
            sb.append(key+ Constants.SPACE);
        }
        log.debug("@Generic Advance Service multiGetByKey with:"+sb.toString());
        return getCacheService().multiGetByKey(keys);
    }

    @Override
    public List<V> multiGetByUnique(Collection<Object> keys){
        StringBuffer sb = new StringBuffer();
        for(Object key:keys){
            sb.append(key+ Constants.SPACE);
        }
        log.debug("@Generic Advance Service multiGetByUnique with:"+sb.toString());
        return getCacheService().multiGetByUnique(keys);
    }

	@Override
	public BoundHashOperations<String, K, V> getKeyHashOps() {
		return getCacheService().getKeyHashOps();
	}

	@Override
	public BoundHashOperations<String, Object, V> getUniqueHashOps() {
		return getCacheService().getUniqueHashOps();
	}

	@Override
	public IGenericService<V, K> getGenericService() {
		return genericService;
	}

	@Override
	public IGenericCache<V, K> getCacheService() {
		return cacheService;
	}

	@Override
	public RedisTemplate<String, V> getRedisTemplate() {
		return getCacheService().getRedisTemplate();
	}

	@Override
	public Field getId() {
		return getCacheService().getId();
	}

	@Override
	public Field getUnique() {
		return getCacheService().getUnique();
	}

	@Override
	public CoreConfig getCoreConfig() {
		return getCacheService().getCoreConfig();
	}


}
