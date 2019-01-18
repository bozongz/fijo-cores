package com.fijo.cores.service.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import com.fijo.cores.model.SystemModel;
import com.fijo.cores.utils.DateUtil;
import com.fijo.cores.utils.ObjectUtil;
import org.apache.ibatis.session.SqlSession;

import com.fijo.cores.mapper.ISystemMapper;
import com.fijo.cores.model.SystemModel;
import com.fijo.cores.service.ISystemService;
import com.fijo.cores.utils.DateUtil;
import com.fijo.cores.utils.ObjectUtil;

/**
 * 系统实体通用服务层
 * 
 * 数据如何操作完全取决于Mybatis的映射文件
 * 
 * 需要记录数据操作的创建时间和更新时间
 * 
 * @author zhangbo
 *
 * @param <T>
 * @param <PK>
 */
public class SystemService<T extends SystemModel<T>, PK extends Serializable>
		extends GenericMapperService<T, PK> implements ISystemService<T, PK>{
	
	protected ISystemMapper<T, PK> mapper;
	
	/**
	 * 可通过配置文件，直接泛型Model获得Service实例
	 * 
	 * @param sqlSession
	 * @param persistentMapper
	 */
	@SuppressWarnings("unchecked")
	public SystemService(SqlSession sqlSession, Class<T> persistentMapper) {
		super(sqlSession, persistentMapper);
		this.mapper = (ISystemMapper<T, PK>) sqlSession.getMapper(persistentMapper);
		super.setMapper(mapper);
	}
	
	/**
	 * 可通过子类继承@Autowired构造函数注入SqlSession从而获得Mapper后，构造Service实例
	 * 
	 * @param sqlSession
	 */
	public SystemService(SqlSession sqlSession) {
		super(sqlSession);
	}
	
	@Override
	public int create(T o) {
		log.debug("@System Service create object: "+ o);
		wrapCreateInfo(o);
		return mapper.create(o);
	}

	@Override
	public int batchCreate(Collection<T> os) {
		log.debug("@System Service batch create objects: "+ os);
		for(T o: os){
			wrapCreateInfo(o);
		}
		return mapper.batchCreate(os);
	}

	@Override
	public int update(T o) {
		log.debug("@System Service update object: "+ o);
		wrapUpdateInfo(o);
		return mapper.update(o);
	}

	@Override
	public int batchUpdate(Collection<T> os) {
		log.debug("@System Service batch update objects: "+ os);
		for(T o: os){
			wrapUpdateInfo(o);
		}
		return mapper.batchUpdate(os);
	}

	@Override
	public int delete(PK id) {
		log.debug("@System Service delete object by id: "+ id);
		return mapper.delete(DateUtil.getCurrent(), id);
	}

	@Override
	public int delete(T o) {
		if(ObjectUtil.isEmpty(o)){
			log.debug("@System Service forbidden delete all objects with empty object: " + o);
			return 0;
		}else{
			log.debug("@System Service delete objects by object: "+ o);
			wrapUpdateInfo(o);
			return mapper.delete(o);
		}
	}
	
	@Override
	public int batchDelete(Set<PK> ids) {
		log.debug("@System Service batch delete objects by ids: "+ ids);
		return mapper.batchDelete(DateUtil.getCurrent(), ids);
	}
	
	@Override
	public int batchDelete(Collection<T> os) {
		log.debug("@System Service batch update objects: "+ os);
		for(T o: os){
			wrapUpdateInfo(o);
		}
		return mapper.batchDelete(os);
	}
	
	protected void wrapUpdateInfo(T o) {
		o.setUpdateDate(DateUtil.getCurrent());
	}

	protected void wrapCreateInfo(T o) {
		o.setCreateDate(DateUtil.getCurrent());
		o.setUpdateDate(DateUtil.getCurrent());
		wrapUpdateInfo(o);
	}

	/**
	 * @param mapper the mapper to set
	 */
	public void setMapper(ISystemMapper<T, PK> mapper) {
		this.mapper = mapper;
		super.setMapper(mapper);
	}
}
