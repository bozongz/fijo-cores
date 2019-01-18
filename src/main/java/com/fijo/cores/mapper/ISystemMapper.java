package com.fijo.cores.mapper;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import com.fijo.cores.model.SystemModel;
import org.apache.ibatis.annotations.Param;

import com.fijo.cores.model.SystemModel;

/**
 * 业务实体通用持久层
 * 
 * 业务实体保存创建人信息和更新人信息
 * 
 * @author zhangbo
 *
 * @param <T>
 */
public interface ISystemMapper<T extends SystemModel<T>, PK extends Serializable>
		extends IGenericMapper<T, PK> {
	
	/**
	 * 应用系统 逻辑删除
	 * @param updateDate
	 * @param id
	 * @return
	 */
	int delete(@Param("updateDate") Date updateDate, @Param("id")PK id);
	
	/**
	 * 应用系统 批量逻辑删除
	 * @param updateDate
	 * @param ids
	 * @return
	 */
	int batchDelete(@Param("updateDate") Date updateDate, @Param("list") Collection<PK> ids);
	
}
