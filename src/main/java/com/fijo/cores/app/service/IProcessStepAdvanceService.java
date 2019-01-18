/**
 * 
 */
package com.fijo.cores.app.service;

import java.util.List;

import com.fijo.cores.app.model.ProcessModel;
import com.fijo.cores.app.model.ProcessStep;
import com.fijo.cores.service.IGenericAdvanceService;
import com.fijo.cores.utils.enums.ProcessEnum;
import com.fijo.cores.app.model.ProcessAudit;
import com.fijo.cores.app.model.ProcessModel;
import com.fijo.cores.app.model.ProcessStep;
import com.fijo.cores.service.IGenericAdvanceService;
import com.fijo.cores.utils.enums.ProcessEnum;

/**
 * 缓存审批节点配置信息
 * 
 * @author zhangbo
 *
 */
public interface IProcessStepAdvanceService extends IGenericAdvanceService<ProcessStep,Integer> {
	
	/**
	 * 提供给ProcessStatusController进行handleProcess处理时判断下一环节（不考虑stepType=special的情况）
	 * @param stepId
	 * @param result
	 * @return
	 */
	ProcessStep getProcessStatusCheckNextStep(Integer stepId, ProcessEnum result);
	
	/**
	 * 返回流程审批时实际跃迁的下一环节信息（考虑stepType=special的情况）
	 * @param result 审批结果
	 * @param process 判断环节特殊配置configurations（可选）
	 * @return
	 */
	ProcessStep getActualNextStep(ProcessEnum result, ProcessModel<?> process);
	
	/**
	 * 返回流程运行版本起始环节
	 * @param headerId
	 * @return
	 */
	ProcessStep getFirstStep(Integer headerId);
	
	/**
	 * 返回流程发起环节
	 * @param headerId
	 * @return
	 */
	ProcessStep getStartStep(Integer headerId);	
	
	/**
	 * 获取流程正常结束环节
	 * @param headerId
	 * @return
	 */
	ProcessStep getLastStep(Integer headerId);
	
	/**
	 * 判断发起环节（首环节）
	 * @param stepId
	 * @return
	 */
	boolean isFirst(Integer stepId);
	
	/**
	 * 判断启动环节
	 * @param headerId
	 * @param stepId
	 * @return
	 */
	boolean isStart(Integer headerId, Integer stepId);
	
	/**
	 * 判断是否结束环节，用于判断是否生成待办
	 * @param stepId
	 * @return
	 */
	boolean isFinish(Integer stepId);
	
	/**
	 * 判断正常结束环节
	 * @param stepId
	 * @return
	 */
	boolean isSuccessFinish(Integer stepId);
	
	/**
	 * 返回错误结束环节
	 * @param stepId
	 * @return
	 */
	boolean isErrorFinish(Integer stepId);
	
	/**
	 * 按照特殊环节配置的条件，根据流程的实时数据，获取对应的流程环节
	 * @param step
	 * @param process
	 * @return
	 */
	ProcessAudit getConfigurationAudit(ProcessStep step, ProcessModel<?> process);
	
	/**
	 * 选择审批结果可选项（不常用）
	 * @param stepCode
	 * @return
	 */
	List<ProcessEnum> getSelectOptions(String stepCode);
}
