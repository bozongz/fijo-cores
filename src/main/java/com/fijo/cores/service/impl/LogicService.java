package com.fijo.cores.service.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import com.fijo.cores.shiro.AppUserSession;
import com.fijo.cores.utils.DateUtil;
import com.fijo.cores.utils.ObjectUtil;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;

import com.fijo.cores.admin.authority.model.ShiroUser;
import com.fijo.cores.mapper.ILogicMapper;
import com.fijo.cores.model.LogicModel;
import com.fijo.cores.service.ILogicService;
import com.fijo.cores.shiro.AppUserSession;
import com.fijo.cores.utils.DateUtil;
import com.fijo.cores.utils.ObjectUtil;

/**
 * 业务实体通用服务层
 * 
 * 涉及业务实体的所有操作需要记录创建人信息和更新人信息
 * 
 * @author zhangbo
 *
 * @param <T>
 * @param <PK>
 */
public class LogicService<T extends LogicModel<T>, PK extends Serializable>
		extends SystemService<T, PK> implements ILogicService<T, PK> {
	
	protected ILogicMapper<T, PK> mapper;
	
	@Autowired
	private AppUserSession appUserSession;
	
	/**
	 * 可通过配置文件，直接泛型Model获得Service实例
	 * 
	 * @param sqlSession
	 * @param persistentMapper
	 */
	@SuppressWarnings("unchecked")
	public LogicService(SqlSession sqlSession, Class<T> persistentMapper) {
		super(sqlSession, persistentMapper);
		this.mapper = (ILogicMapper<T, PK>) sqlSession.getMapper(persistentMapper);
		super.setMapper(mapper);
	}
	
	/**
	 * 可通过子类继承@Autowired构造函数注入SqlSession从而获得Mapper后，构造Service实例
	 * @param sqlSession
	 */
	public LogicService(SqlSession sqlSession) {
		super(sqlSession);
	}

	public int updateEnable(boolean enabled, Collection<PK> ids) {
		ShiroUser user = appUserSession.getCurrentUser();
		log.debug("@Logic Service make enable objects : "+enabled+" by "+ ids);
		return mapper.updateEnable(user.getUserId(), user.getUserCode(), user.getUserName(),DateUtil.getCurrent(), enabled, ids);
	}

	@Override
	public int delete(PK id) {
		log.debug("@Logic Service delete object by id: "+ id);
		ShiroUser user = appUserSession.getCurrentUser();
		return mapper.delete(user.getUserId(),user.getUserCode(), user.getUserName(),DateUtil.getCurrent(), id);
	}

	@Override
	public int batchDelete(Set<PK> ids) {
		log.debug("@Logic Service batch delete objects by ids: "+ ids);
		ShiroUser user = appUserSession.getCurrentUser();
		return mapper.batchDelete(user.getUserId(), user.getUserCode(), user.getUserName(),DateUtil.getCurrent(), ids);
	}	

	@Override
	public int delete(T o) {
		if(ObjectUtil.isEmpty(o)){
			log.debug("@Logic Service forbidden delete all objects with empty object: " + o);
			return 0;
		}else{
			log.debug("@Logic Service delete objects by object: "+ o);
			wrapUpdateInfo(o);
			return mapper.delete(o);
		}
	}

	@Override
	protected void wrapUpdateInfo(T o) {
		ShiroUser user = appUserSession.getCurrentUser();
		o.setUpdateUserId(user.getUserId());
		o.setUpdateUserCode(user.getUserCode());
		o.setUpdateUserName(user.getUserName());
		o.setUpdateDate(DateUtil.getCurrent());
	}

	@Override
	protected void wrapCreateInfo(T o) {
		ShiroUser user = appUserSession.getCurrentUser();
		o.setCreateUserId(user.getUserId());
		o.setCreateUserCode(user.getUserCode());
		o.setCreateUserName(user.getUserName());
		o.setCreateDate(DateUtil.getCurrent());
		o.setUpdateDate(DateUtil.getCurrent());
		wrapUpdateInfo(o);
	}

	/**
	 * 使用子类LogicMapper
	 * 
	 * @param mapper the mapper to set
	 */
	public void setMapper(ILogicMapper<T, PK> mapper) {
		this.mapper = mapper;
		super.setMapper(mapper);
	}
	
}
