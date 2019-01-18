/**
 * 
 */
package com.fijo.cores.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.fijo.cores.model.GenericModel;

/**
 * 基础实体通用服务层
 * 
 * 数据如何操作完全取决于Mybatis的映射文件
 * 
 * @author zhangbo
 *
 * @param <T>
 * @param <PK>
 */
public interface IGenericService<T extends GenericModel<T>, PK extends Serializable> {
	
	T getById(PK id);
	
	T getByUnique(Object unique);
	
	T getLast();
	
	T getOne(T o);
	
	Collection<T> getAll();
	
	Collection<T> getAll(T o);
	
	Collection<T> getAll(RowBounds rowBounds);
	
	Collection<T> getAll(T o, RowBounds rowBounds);

	Collection<T> getAll(Map<String, Object> params);
	
	Collection<T> getAll(Map<String, Object> params, RowBounds rowBounds);
	
	/**
	 * 忽略逻辑删除，忽略缓存，直接从数据库查询数据
	 * @param o
	 * @param rowBounds
	 * @return
	 */
	Collection<T> queryAnyway(Map<String, Object> params);
	Collection<T> queryAnyway(T o, RowBounds rowBounds);
	
	Integer getCount(T o);

	Integer getCount(Map<String, Object> params);
	
	int create(T o);

	int batchCreate(Collection<T> os);
	
	int update(T o);
	
	int update(Map<String, Object> params);
	
	int batchUpdate(Collection<T> os);
	
	/**
	 * 逻辑删除或物理删除(实现依赖mapper.xml)
	 * @param id
	 * @return
	 */
	int delete(PK id);

	/**
	 * 逻辑删除或物理删除(实现依赖mapper.xml)
	 * @param ids
	 * @return
	 */
	int batchDelete(Set<PK> ids);
	
	/**
	 * 逻辑删除
	 * @param os
	 * @return
	 */
	int batchDelete(Collection<T> os);
	
	/**
	 * 按条件逻辑删除或物理删除(实现依赖mapper.xml)
	 * @param o
	 * @return
	 */
	int delete(T o);
	
	void selectCustom(Object parameter, ResultHandler handler);
}
