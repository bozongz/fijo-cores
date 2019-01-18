/**
 * 
 */
package com.fijo.cores.app.service;

import java.util.List;
import java.util.Map;

import com.fijo.cores.app.model.ProcessModel;
import com.fijo.cores.service.IGenericAdvanceService;
import com.fijo.cores.utils.enums.ProcessEnum;
import com.fijo.cores.app.model.ProcessAudit;
import com.fijo.cores.app.model.ProcessModel;
import com.fijo.cores.app.model.ProcessTask;
import com.fijo.cores.service.IGenericAdvanceService;
import com.fijo.cores.utils.enums.ProcessEnum;

/**
 * 缓存审批节点配置信息
 * 
 * @author zhangbo
 *
 */
public interface IProcessAuditAdvanceService extends IGenericAdvanceService<ProcessAudit,Integer> {
	
	/**
	 * 根据环节Id,流程数据获取审批信息
	 * @param stepId 环节Id
	 * @param process 业务流程数据
	 * @return
	 */
	ProcessAudit getAudit(Integer stepId, ProcessModel<?> process);
	
	/**
	 * 根据环节编码,流程数据获取审批信息
	 * @param stepCode 环节编码
	 * @param process 业务流程数据
	 * @return
	 */
	ProcessAudit getProcessAudit(String stepCode, ProcessModel<?> process);

	/**
	 * 根据环节编码，获取该环节的可选审批人或审批角色
	 * @param stepCode
	 * @return
	 */
	List<Integer> getAuditors(String stepCode);
	
	/**
	 * 用于获取可选审批人或审批角色, 在ProcessTaskListener发起待办时，或者各流程环节选择审批领导时（下拉框、树形菜单）
	 * @param subjects
	 * @return
	 */
	List<Integer> getAuditors(ProcessEnum sujectType, String subjects);
	
	/**
	 * 我的申请中查看审批对象描述信息
	 * @param subjects 审批对象
	 * @param subjectType 审批对象类型
	 * @param createUserId 如果基于角色审批，并且该角色是拟制人角色，那么根据单据createUserId显示拟制人姓名
	 * @return 
	 */
	String getSubjectsDesc(String subjects, ProcessEnum subjectType, Integer createUserId);
	
	/**
	 * 从待办列表中检查是否为审批人进行审批
	 * @param processTypeId
	 * @param processHeaderId
	 * @param processReceiptId
	 */
	ProcessTask checkCurrentUserAudit(Integer processTypeId, Integer processHeaderId, Long processReceiptId);

    /**
     * 从Redis中将userId-orgId对应的审批信息清除
     * @param stepCode
     * @param oldAuditId
     */
    void deleteUserOrgHashOps(String stepCode, Integer oldAuditId);

    /**
     * 在Redis中新增userId-orgId对应的审批信息
     * @param o
     */
    void createUserOrgHashOps(ProcessAudit o);
}
