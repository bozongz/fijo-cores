/**
 * 
 */
package com.fijo.cores.service;

import java.io.Serializable;

import com.fijo.cores.model.SystemModel;
import com.fijo.cores.model.SystemModel;

/**
 * 同时具备数据库CURD操作逻辑与缓存逻辑的高级抽象业务类
 * 
 * @author zhangbo
 *
 */
public interface ISystemAdvanceService<V extends SystemModel<V>,K extends Serializable>
		extends IGenericAdvanceService<V, K>, ISystemService<V, K> {
}
