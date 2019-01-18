package com.fijo.cores.service.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.fijo.cores.utils.ObjectUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.springframework.cache.annotation.CacheConfig;

import com.fijo.cores.exceptions.FoundMoreThanOneException;
import com.fijo.cores.mapper.IGenericMapper;
import com.fijo.cores.model.GenericModel;
import com.fijo.cores.service.IGenericService;
import com.fijo.cores.utils.ObjectUtil;

/**
 * 基础实体通用服务层(不可直接使用，需要注入Mapper接口)
 * 
 * 数据如何操作完全取决于Mybatis的映射文件
 * 
 * @author zhangbo
 *
 * @param <T>
 * @param <PK>
 */
@CacheConfig(cacheNames = {"redis:"}, cacheResolver="genericCacheResolver", keyGenerator="genericKeyGenerator")
public class GenericMapperService<T extends GenericModel<T>, PK extends Serializable> implements IGenericService<T, PK> {
	
	public transient final Log log = LogFactory.getLog(getClass());

	protected SqlSession sqlSession;
	
	protected IGenericMapper<T, PK> mapper;

	/**
	 * 可通过配置文件，直接泛型Model获得Service实例
	 * 
	 * @param sqlSession
	 * @param persistentMapper
	 */
	@SuppressWarnings("unchecked")
	public GenericMapperService(SqlSession sqlSession, final Class<T> persistentMapper) {
		this.sqlSession = sqlSession;
		mapper = (IGenericMapper<T, PK>) sqlSession.getMapper(persistentMapper);
	}
	
	/**
	 * 可通过子类继承@Autowired构造函数注入SqlSession从而获得Mapper后，构造Service实例
	 * 
	 * @param sqlSession
	 */
	public GenericMapperService(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}

	public T getById(PK id) {
		log.debug("@Generic Mapper Service get single object by id: " + id);
		return (T) mapper.getById(id);
	}

	public T getByUnique(Object unique) {
		log.debug("@Generic Mapper Service get single object by unique: " + unique);
		return (T) mapper.getByUnique(unique);
	}
	
	public T getLast(){
		log.debug("@Generic Mapper Service get last object");
		return (T) mapper.getLast();
	}
	
	public T getOne(T o){
		log.debug("@Generic Mapper Service get one object by: " + o);
		Collection<T> list = getAll(o);
		if(list == null || list.size() == 0)
			return null;
		else{
			if(list.size() != 1)
				throw new FoundMoreThanOneException();
			else
				return list.iterator().next();
		}
	}
	
	public Collection<T> getAll() {
		log.debug("@Generic Mapper Service get all objects.");
		return mapper.getAll();
	}

	public Collection<T> getAll(T o) {
		if(o.getPageindex()!=null && o.getPagesize()!=null){
			RowBounds rowBounds = new RowBounds(o.getPageindex(), o.getPagesize());
			return getAll(o, rowBounds);
		}else{
			log.debug("@Generic Mapper Service get all objects by object: " + o);
			return mapper.getAll(o);
		}
		
	}

	public Collection<T> getAll(RowBounds rowBounds) {
		log.debug("@Generic Mapper Service get all objects offset: " + rowBounds.getOffset()
				+ " limit " + rowBounds.getLimit());
		return mapper.getAll(rowBounds);
	}

	public Collection<T> getAll(T o, RowBounds rowBounds) {
		log.debug("@Generic Mapper Service get all objects by object: " + o + " offset: "
				+ rowBounds.getOffset() + " limit " + rowBounds.getLimit());
		return mapper.getAll(o, rowBounds);
	}

	public Collection<T> getAll(Map<String, Object> params) {		
		if(params.get("pageindex")!=null && params.get("pagesize")!=null){
			RowBounds rowBounds = new RowBounds((Integer)params.get("pageindex"), (Integer)params.get("pagesize"));
			log.debug("@Generic Mapper Service get all objects by params: " + params
					+ " offset: " + rowBounds.getOffset() + " limit "
					+ rowBounds.getLimit());
			return mapper.getAll(params, rowBounds);
		}else{
			log.debug("@Generic Mapper Service get all objects by params: " + params);
			return mapper.getAll(params);
		}				
	}
	
	@Override
	public  Collection<T> getAll(Map<String, Object> params, RowBounds rowBounds){
		log.debug("@Generic Mapper Service get all objects by params: " + params + " offset: "
				+ rowBounds.getOffset() + " limit " + rowBounds.getLimit());
		return mapper.getAll(params, rowBounds);
	}
	
	@Override
	public Collection<T> queryAnyway(Map<String, Object> params){
		return mapper.queryAnyway(params);
	}
	
	@Override
	public Collection<T> queryAnyway(T o, RowBounds rowBounds) {
		return mapper.queryAnyway(o, rowBounds);
	}
	
	@Override
	public Integer getCount(T o) {
		log.debug("@Generic Mapper Service get counter by object: " + o);
		return mapper.getCount(o);
	}

	@Override
	public Integer getCount(Map<String, Object> params) {
		log.debug("@Generic Mapper Service get counter by params: " + params);
		return mapper.getCount(params);
	}
	
	public int create(T o) {
		log.debug("@Generic Mapper Service create object: " + o);
		return mapper.create(o);
	}

	public int batchCreate(Collection<T> os) {
		log.debug("@Generic Mapper Service batch create objects: " + os);
		return mapper.batchCreate(os);
	}

	public int update(T o) {
		log.debug("@Generic Mapper Service update object: " + o);
		return mapper.update(o);
	}

	public int update(Map<String, Object> params){
		log.debug("@Generic Mapper Service update object by params: " + params);
		return mapper.update(params);
	}
	
	public int batchUpdate(Collection<T> os) {
		log.debug("@Generic Mapper Service batch update objects: " + os);
		return mapper.batchUpdate(os);
	}

	/**
	 * 逻辑删除或物理删除(实现依赖mapper.xml)
	 * @param id
	 * @return
	 */
	public int delete(PK id) {
		log.debug("@Generic Mapper Service delete object by id: " + id);
		return mapper.delete(id);
	}

	/**
	 * 逻辑删除或物理删除(实现依赖mapper.xml)
	 * @param ids
	 * @return
	 */
	public int batchDelete(Set<PK> ids) {
		log.debug("@Generic Mapper Service batch delete objects by ids: " + ids);
		return mapper.batchDelete(ids);
	}
	
	/**
	 * 逻辑删除
	 * @param os
	 * @return
	 */
	public int batchDelete(Collection<T> os){
		log.debug("@Generic Mapper Service batch logic delete objects: " + os);
		return mapper.batchDelete(os);
	}
	
	/**
	 * 按条件逻辑删除或物理删除(实现依赖mapper.xml)
	 * @param o
	 * @return
	 */
	public int delete(T o) {
		if(ObjectUtil.isEmpty(o)){
			log.debug("@Generic Mapper Service forbidden delete all objects with empty object: " + o);
			return 0;
		}else{
			log.debug("@Generic Mapper Service delete objects by object: " + o);
			return mapper.delete(o);
		}
	}

	@Override
	public void selectCustom(Object parameter, ResultHandler handler) {
		log.debug("@Generic Mapper Service execute custom action: " + parameter);
		mapper.selectCustom(parameter, handler);
	}
	
	/**
	 * @return the mapper
	 */
	public IGenericMapper<T, PK> getMapper() {
		return mapper;
	}

	/**
	 * @param mapper
	 *            the mapper to set
	 */
	public void setMapper(IGenericMapper<T, PK> mapper) {
		this.mapper = mapper;
	}

	/**
	 * @return the sqlSession
	 */
	public SqlSession getSqlSession() {
		return sqlSession;
	}

	/**
	 * @param sqlSession the sqlSession to set
	 */
	public void setSqlSession(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}

}
