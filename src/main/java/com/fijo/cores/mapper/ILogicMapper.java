package com.fijo.cores.mapper;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import org.apache.ibatis.annotations.Param;

import com.fijo.cores.model.LogicModel;

/**
 * 业务实体通用持久层
 * 
 * 业务实体保存创建人信息和更新人信息
 * 
 * @author zhangbo
 *
 * @param <T>
 */
public interface ILogicMapper<T extends LogicModel<T>, PK extends Serializable>
		extends ISystemMapper<T, PK> {

	/**
	 * 用户 更新可用状态
	 * 
	 * @param updateUserId
	 * @param updateUserCode
	 * @param updateUserName
	 * @param updateDate
	 * @param enabled
	 * @param ids
	 * @return
	 */
	int updateEnable(@Param("updateUserId") Integer updateUserId,
			@Param("updateUserCode") String updateUserCode,
			@Param("updateUserName") String updateUserName,
			@Param("updateDate") Date updateDate,
			@Param("enabled") boolean enabled, @Param("list") Collection<PK> ids);

	/**
	 * 用户 逻辑删除(实现依赖mapper.xml)
	 * 
	 * @param updateUserId
	 * @param updateUserCode
	 * @param updateUserName
	 * @param updateDate
	 * @param id
	 * @return
	 */
	int delete(@Param("updateUserId") Integer updateUserId,
			@Param("updateUserCode") String updateUserCode,
			@Param("updateUserName") String updateUserName,
			@Param("updateDate") Date updateDate, @Param("id") PK id);

	/**
	 * 用户 逻辑删除(实现依赖mapper.xml)
	 * 
	 * @param updateUserId
	 * @param updateUserCode
	 * @param updateUserName
	 * @param updateDate
	 * @param ids
	 * @return
	 */
	int batchDelete(@Param("updateUserId") Integer updateUserId,
			@Param("updateUserCode") String updateUserCode,
			@Param("updateUserName") String updateUserName,
			@Param("updateDate") Date updateDate, @Param("list") Collection<PK> ids);
}
