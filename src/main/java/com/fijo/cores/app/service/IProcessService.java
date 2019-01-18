/**
 * 
 */
package com.fijo.cores.app.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fijo.cores.app.model.ProcessHeader;
import com.fijo.cores.app.model.ProcessJsonData;
import com.fijo.cores.app.model.ProcessModel;
import com.fijo.cores.app.model.ProcessStep;
import com.fijo.cores.model.KeyValue;
import com.fijo.cores.service.ILogicService;
import com.fijo.cores.utils.enums.ProcessEnum;
import com.fijo.cores.app.event.ProcessTaskCreateCallback;
import com.fijo.cores.app.event.ProcessTaskRemoveCallback;
import com.fijo.cores.app.model.DynamicAuditUser;
import com.fijo.cores.app.model.ProcessHeader;
import com.fijo.cores.app.model.ProcessJsonData;
import com.fijo.cores.app.model.ProcessModel;
import com.fijo.cores.app.model.ProcessStep;
import com.fijo.cores.model.KeyValue;
import com.fijo.cores.service.ILogicService;
import com.fijo.cores.utils.enums.ProcessEnum;

/**
 * 业务流程实体通用服务层
 * 
 * 创建单据、单据草稿、生成待办、处理待办、撤销申请、生成审批记录、记录流程汇总状态、上传文件、添加关联
 * 
 * @author zhangbo
 *
 * @param <T>
 * @param <PK>
 */
public interface IProcessService<T extends ProcessModel<T>, PK extends Serializable> extends ILogicService<T, PK> {
	/**
	 * 获取当前流程的下一个唯一编码
	 * @return
	 */
	String getProcessSeqCode();
	
	/**
	 * 获取当前业务流程信息(并检查流程是否可用)
	 * @return
	 */
	ProcessHeader getProcessHeaderAndCheck();
	
	/**
	 * 创建申请与单据并存的档案流程申请
	 * @param o
	 * @return
	 */
	T createArchive(T o);
	
	/**
	 * 更新申请与单据并存的档案流程申请
	 * 1.修改申请草稿后，提交正式申请
	 * 2.驳回修改后，再次提交正式申请
	 * 3.修改正式档案，提交新的正式申请
	 * @param o
	 * @return
	 */
	T updateArchive(T o);
	
	/**
	 * 创建申请与单据并存的档案流程草稿
	 * @param o
	 * @return
	 */
	T createArchiveDraft(T o);
			
	/**
	 * 更新申请与单据并存的档案流程草稿
	 * @param o
	 * @return
	 */
	T updateArchiveDraft(T o);
	
	/**
	 * 审批申请与单据并存的档案流程环节跃迁
	 * @param data
	 * @return
	 */
	T updateArchiveStep(ProcessJsonData<T, PK> data);
	
	/**
	 * 创建申请与单据分离的流程申请
	 * @param o
	 * @return
	 */
	T createOnce(T o);
	
	/**
	 * 更新申请与单据分离的流程申请
	 * 1.修改申请草稿后，提交正式申请
	 * 2.驳回修改后，再次提交正式申请
	 * 3.修改正式档案，提交新的正式申请
	 * @param o
	 * @return
	 */
	T updateOnce(T o);
	
	/**
	 * 创建申请与单据分离的流程草稿
	 * @param o
	 * @return
	 */
	T createOnceDraft(T o);
			
	/**
	 * 更新申请与单据分离的流程草稿
	 * @param o
	 * @return
	 */
	T updateOnceDraft(T o);
	
	/**
	 * 审批申请与单据分离的流程环节跃迁
	 * @param data
	 * @return
	 */
	T updateOnceStep(ProcessJsonData<T, PK> data);
	
	/**
	 * 首次提交申请或驳回修改后提交申请创建主单据关联数据（仅首次提交及驳回修改会调用）
	 * @param o
	 * @return
	 */
	void createRelationDataOnSubmit(T o);
	
	/**
	 * 档案型流程入库后更新替换过期档案（新档案流程主键需要替换为老档案流程主键，以保持其他未知业务表的关联外键不变）
	 * @param previousId
	 * @param currentId
	 */
	void updatePreviousArchive(Long previousId, Long currentId);
	
	/**
	 * 上传文件（所有环节都会调用）
	 * @param o
	 * @return
	 */
	Map<String,String> uploadFiles(T o);
	
	/**
	 * 撤销申请，删除待办及日志
	 */
	int deleteApply(PK id);
	
	/**
	 * 删除流程主单据以所有相关关联数据
	 * @param id
	 * @return
	 */
	int deleteProcess(Long id);

	/**
	 * 删除主单据关联数据
	 * @param id
	 */
	void deleteRelation(Long id);
	
	/**
	 * 删除草稿
	 * @param id
	 * @return
	 */
	int deleteDraft(PK id);
	
	/**
	 * 删除文件
	 * @param o
	 * @return
	 */
	Map<String,String> removeFiles(T o);
	
	/**
	 * 查看主单据流程时加载的关联数据
	 * @param o
	 */
	void loadRelationData(T o);
	
	/**
	 * 选择审批人信息(下拉框形式)
	 * @param id
	 * @return
	 */
	List<KeyValue<Integer,String>> getAuditUsers(PK id);
	
	/**
	 * 选择审批人信息(树形菜单形式)
	 * @param id
	 * @return
	 */
	Map<String, Object> getAuditUsersTree(PK id);
	
	/**
	 * 流程环节跃迁时执行的操作（所有环节都会调用，包括流程结束最后一步）
	 * @param o
	 */
	void onProcessChanged(T o);
	
	/**
	 * 流程最终结束时执行 
	 * @param o
	 */
	void afterProcessFinished(T o);
	
	/**
	 * 确定审批信息（可以由前端用户指定，也可由后台配置提供）
	 * @param step
	 * @param o
	 */
	void swithAudit(ProcessStep step, T o);
	
	/**
	 * 确定审批信息（可以由前端用户指定，也可由后台配置提供）
	 * @param step
	 * @param result
	 * @param o
	 */
	void swithAudit(ProcessStep step, ProcessEnum result, T o);
	
	/**
	 * 	创建生成待办时的回调
	 * @return
	 */
	ProcessTaskCreateCallback createProcessTaskCreateCallback();
	
	/**
	 * 创建删除待办时的回调
	 * @return
	 */
	ProcessTaskRemoveCallback createProcessTaskRemoveCallback();
	
	/**
	 * 由业务动态二次过滤审批人
	 * @param dynamicAuditUser
	 */
	void setDynamicAuditUser(DynamicAuditUser dynamicAuditUser);
}
