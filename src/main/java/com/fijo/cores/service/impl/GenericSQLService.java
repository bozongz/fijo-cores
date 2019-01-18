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
import org.mybatis.spring.support.SqlSessionDaoSupport;

import com.fijo.cores.exceptions.FoundMoreThanOneException;
import com.fijo.cores.model.GenericModel;
import com.fijo.cores.service.IGenericService;
import com.fijo.cores.utils.ObjectUtil;

/**
 * 基础实体通用服务层(不可直接使用，需要注入Mybatis映射文件的命名空间)
 * 
 * 数据如何操作完全取决于Mybatis的映射文件
 * 
 * @author zhangbo
 *
 * @param <T>
 * @param <PK>
 */
public class GenericSQLService<T extends GenericModel<T>, PK extends Serializable>
		extends SqlSessionDaoSupport implements IGenericService<T, PK> {

	public transient final Log log = LogFactory.getLog(getClass());
	
	private String namespace;

	public GenericSQLService(String namespace) {
		this.namespace = namespace;
	}

	@Override
	public T getById(PK id) {
		log.debug("@Generic SQL Service get single object by id: " + id);
		return getSqlSession().selectOne(namespace+".getById", id);		
	}

	@Override
	public T getByUnique(Object unique) {
		log.debug("@Generic SQL Service get single object by unique: " + unique);
		return getSqlSession().selectOne(namespace+".getByUnique", unique);
	}

	@Override
	public T getLast() {
		log.debug("@Generic SQL Service get last object");
		return getSqlSession().selectOne(namespace+".getLast");
	}
	
	@Override
	public T getOne(T o){
		log.debug("@Generic SQL Service get one object by: " + o);
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
	
	@Override
	public Collection<T> getAll() {
		log.debug("@Generic SQL Service get all objects.");
		return getSqlSession().selectList(namespace+".getAll");
	}

	@Override
	public Collection<T> getAll(T o) {
		if(o.getPageindex()!=null && o.getPagesize()!=null){
			RowBounds rowBounds = new RowBounds(o.getPageindex(), o.getPagesize());
			log.debug("@Generic SQL Service get all objects by object: " + o
					+ " offset: " + rowBounds.getOffset() + " limit "
					+ rowBounds.getLimit());
			return getAll(o, rowBounds);
		}else{
			log.debug("@Generic SQL Service get all objects by object: " + o);
			return getSqlSession().selectList(namespace+".getAll", o);
		}
	}

	@Override
	public Collection<T> getAll(RowBounds rowBounds) {
		log.debug("@Generic SQL Service get all objects offset: "
				+ rowBounds.getOffset() + " limit " + rowBounds.getLimit());		
		return getSqlSession().selectList(namespace+".getAll", null, rowBounds);
	}

	@Override
	public Collection<T> getAll(T o, RowBounds rowBounds) {
		log.debug("@Generic SQL Service get all objects by object: " + o
				+ " offset: " + rowBounds.getOffset() + " limit "
				+ rowBounds.getLimit());
		return getSqlSession().selectList(namespace+".getAll", o, rowBounds);
	}

	@Override
	public Collection<T> queryAnyway(Map<String, Object> params) {
		return getSqlSession().selectList(namespace+".queryAnyway", params);
	}
	
	@Override
	public Collection<T> queryAnyway(T o, RowBounds rowBounds) {
		return getSqlSession().selectList(namespace+".queryAnyway", o, rowBounds);
	}

	@Override
	public Collection<T> getAll(Map<String, Object> params) {		
		if(params.get("pageindex")!=null && params.get("pagesize")!=null){
			RowBounds rowBounds = new RowBounds((Integer)params.get("pageindex"), (Integer)params.get("pagesize"));
			log.debug("@Generic SQL Service get all objects by params: " + params
					+ " offset: " + rowBounds.getOffset() + " limit "
					+ rowBounds.getLimit());
			return getSqlSession().selectList(namespace+".getAll", params, rowBounds);
		}else{
			log.debug("@Generic SQL Service get all objects by params: " + params);
			return getSqlSession().selectList(namespace+".getAll", params);
		}				
	}

	@Override
	public Collection<T> getAll(Map<String, Object> params, RowBounds rowBounds) {
		log.debug("@Generic SQL Service get all objects by params: " + params
				+ " offset: " + rowBounds.getOffset() + " limit "
				+ rowBounds.getLimit());
		return getSqlSession().selectList(namespace+".getAll", params, rowBounds);
	}

	@Override
	public Integer getCount(T o) {
		return getSqlSession().selectOne(namespace+".getCount", o);
	}

	@Override
	public Integer getCount(Map<String, Object> params) {
		return getSqlSession().selectOne(namespace+".getCount", params);
	}

	@Override
	public int create(T o) {
		log.debug("@Generic SQL Service create object: " + o);
		return getSqlSession().insert(namespace+".create", o);
	}

	@Override
	public int batchCreate(Collection<T> os) {
		log.debug("@Generic SQL Service batch create objects: " + os);
		return getSqlSession().insert(namespace+".batchCreate", os);
	}

	@Override
	public int update(T o) {
		log.debug("@Generic SQL Service update object: " + o);
		return getSqlSession().update(namespace+".update", o);
	}

	@Override
	public int update(Map<String, Object> params) {
		log.debug("@Generic SQL Service update params: " + params);
		return getSqlSession().update(namespace+".update", params);
	}

	@Override
	public int batchUpdate(Collection<T> os) {
		log.debug("@Generic SQL Service batch update objects: " + os);
		return getSqlSession().update(namespace+".batchUpdate", os);
	}

	/**
	 * 逻辑删除或物理删除(实现依赖mapper.xml)
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public int delete(PK id) {
		log.debug("@Generic SQL Service delete object by id: " + id);
		return getSqlSession().delete(namespace+".delete", id);
	}

	/**
	 * 逻辑删除或物理删除(实现依赖mapper.xml)
	 * 
	 * @param ids
	 * @return
	 */
	@Override
	public int batchDelete(Set<PK> ids) {
		log.debug("@Generic SQL Service batch delete objects by ids: " + ids);
		return getSqlSession().delete(namespace+".batchDelete", ids);
	}

	/**
	 * 逻辑删除
	 * @param os
	 * @return
	 */
	@Override
	public int batchDelete(Collection<T> os){
		log.debug("@Generic SQL Service batch logic delete objects: " + os);
		return getSqlSession().delete(namespace+".batchLogicDelete", os);
	}
	
	/**
	 * 按条件逻辑删除或物理删除(实现依赖mapper.xml)
	 * 
	 * @param o
	 * @return
	 */
	@Override
	public int delete(T o) {
		if(ObjectUtil.isEmpty(o)){
			log.debug("@Generic SQL Service forbidden delete all objects with empty object: " + o);
			return 0;
		}else{
			log.debug("@Generic SQL Service delete objects by object: " + o);
			return getSqlSession().delete(namespace+".delete", o);
		}
	}

	@Override
	public void selectCustom(Object parameter, ResultHandler handler) {
		log.debug("@Generic SQL Service execute custom action: " + parameter);
		getSqlSession().select(namespace+".selectCustom", parameter, handler);
	}


}
