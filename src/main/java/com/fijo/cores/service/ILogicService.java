/**
 * 
 */
package com.fijo.cores.service;

import java.io.Serializable;
import java.util.Collection;

import com.fijo.cores.model.LogicModel;

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
public interface ILogicService<T extends LogicModel<T>, PK extends Serializable> extends ISystemService<T, PK> {
	
	int updateEnable(boolean enabled, Collection<PK> ids);
	
}
