package com.fijo.cores.app.mapper;

import java.io.Serializable;

import com.fijo.cores.mapper.ILogicMapper;
import com.fijo.cores.app.model.ProcessModel;
import com.fijo.cores.mapper.ILogicMapper;

/**
 * 业务流程实体通用持久层
 * 
 * 创建单据、单据草稿、生成待办、处理待办、撤销申请、生成审批记录、记录流程汇总状态、上传文件、添加关联
 * 
 * @author zhangbo
 *
 * @param <T>
 */
public interface IProcessModelMapper<T extends ProcessModel<T>, PK extends Serializable> extends ILogicMapper<T, PK> {
	
	/**
	 * 
	 * @param code
	 * @return
	 */
	T getEnabledPrevious(String code);
}
