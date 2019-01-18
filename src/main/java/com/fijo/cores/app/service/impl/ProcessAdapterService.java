package com.fijo.cores.app.service.impl;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fijo.cores.admin.authority.model.ShiroUser;
import com.fijo.cores.admin.authority.model.SysUser;
import com.fijo.cores.admin.authority.service.ISysUserAdvanceService;
import com.fijo.cores.app.event.*;
import com.fijo.cores.app.mapper.IProcessModelMapper;
import com.fijo.cores.app.model.*;
import com.fijo.cores.app.schedule.ProcessTaskCallbackRetrySchedule;
import com.fijo.cores.app.service.*;
import com.fijo.cores.exceptions.ProcessUnavailableException;
import com.fijo.cores.model.KeyValue;
import com.fijo.cores.service.impl.LogicService;
import com.fijo.cores.shiro.AppUserSession;
import com.fijo.cores.utils.AppCodeGenerator;
import com.fijo.cores.utils.Constants;
import com.fijo.cores.utils.DateUtil;
import com.fijo.cores.utils.ObjectUtil;
import com.fijo.cores.utils.configs.CoreConfig;
import com.fijo.cores.utils.enums.ProcessEnum;
import com.fijo.cores.utils.enums.ProcessHeaderCodeDynaEnum;
import com.fijo.cores.app.event.*;
import com.fijo.cores.app.schedule.ProcessTaskCallbackRetrySchedule;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.fijo.cores.admin.authority.model.ShiroUser;
import com.fijo.cores.admin.authority.model.SysUser;
import com.fijo.cores.admin.authority.service.ISysUserAdvanceService;
import com.fijo.cores.app.event.ProcessTaskEvent.NoticMethod;
import com.fijo.cores.app.mapper.IProcessModelMapper;
import com.fijo.cores.app.model.DynamicAuditUser;
import com.fijo.cores.app.model.ProcessAudit;
import com.fijo.cores.app.model.ProcessHeader;
import com.fijo.cores.app.model.ProcessJsonData;
import com.fijo.cores.app.model.ProcessModel;
import com.fijo.cores.app.model.ProcessStep;
import com.fijo.cores.app.service.IProcessAuditAdvanceService;
import com.fijo.cores.app.service.IProcessDraftService;
import com.fijo.cores.app.service.IProcessHeaderAdvanceService;
import com.fijo.cores.app.service.IProcessService;
import com.fijo.cores.app.service.IProcessStatusService;
import com.fijo.cores.app.service.IProcessStepAdvanceService;
import com.fijo.cores.exceptions.ProcessUnavailableException;
import com.fijo.cores.model.KeyValue;
import com.fijo.cores.service.impl.LogicService;
import com.fijo.cores.shiro.AppUserSession;
import com.fijo.cores.utils.AppCodeGenerator;
import com.fijo.cores.utils.Constants;
import com.fijo.cores.utils.DateUtil;
import com.fijo.cores.utils.ObjectUtil;
import com.fijo.cores.utils.configs.CoreConfig;
import com.fijo.cores.utils.enums.ProcessEnum;
import com.fijo.cores.utils.enums.ProcessHeaderCodeDynaEnum;

/**
 * 业务流程实体通用服务层
 * 
 * 创建单据、单据草稿、生成待办、处理待办、撤销申请、生成审批记录、记录流程汇总状态、上传文件、添加关联
 * 
 * 关于文件上传的补充说明：
 * 
 * createRelationDataOnSubmit 与 uploadFiles 方法独立，主要考虑在审批过程中，审批人依然可以继续上传文件，而createRelationDataOnSubmit只能在拟制人申请或修改单据时执行
 * 
 * （一）修改文件
 * 1. 仅删除文件，不添加新文件 ： 前端调用FileUploaderController.deleteFiles进行处理，业务流程不需任何操作
 * 2. 不删除文件，仅添加新文件： 前端调用FileUploaderController.uploadFile完成文件上传后，提交保存业务流程会调用uploadFiles方法实现保存数据库记录（校验文件路径是否已存在，不存在才插入新的记录，从而解决审批或修改时文件记录不会被重复保存2次）
 * 3. 全部删除文件，又增加新的文件：解决方案1+2
 * 4. 部分删除文件，再增加新的文件：对于删除的文件， 前端调用FileUploaderController.deleteFiles进行处理，业务流程不需任何操作；对于新增的文件，按2处理
 * 
 * （二）档案性流程审批存档后，置换主键ID
 * 由于文件在审批时发生变化不会调用createRelationDataOnSubmit进行更换关联关系，所以一旦档案性流程归档，如果老文件没有进行变化，因此需要先调用FileUploaderService的update方法更新老档案的文件关联单据外键receiptId，再在业务流程的updatePreviousArchive方法中实现置换新旧receiptId
 * 
 * @author zhangbo
 *
 * @param <T>
 * @param <PK>
 */
@CacheConfig(cacheNames = {"runtime:"}, cacheResolver="genericCacheResolver", keyGenerator="genericKeyGenerator")
public abstract class ProcessAdapterService<T extends ProcessModel<T>, PK extends Serializable>
		extends LogicService<T, PK> implements IProcessService<T, PK> {
	
	@Autowired
    private ApplicationEventPublisher eventPublisher;
	
	protected IProcessModelMapper<T, PK> mapper;
	
	private String headerCode;
	
	private ProcessTaskEvent.NoticMethod noticeMethod;
	
	@Autowired
	private CoreConfig config;
	
	@Autowired
	private AppUserSession appUserSession;
	
	@Autowired
	private IProcessHeaderAdvanceService processHeaderAdvanceService;

	@Autowired
	private IProcessStepAdvanceService processStepAdvanceService;
	
	@Autowired
	private IProcessAuditAdvanceService processAuditAdvanceService;
	
	@Autowired
	private ISysUserAdvanceService sysUserAdvanceService;
	
	@Autowired
	private IProcessStatusService processStatusService;
	
	@Autowired
	private IProcessDraftService processDraftService;
	
	//增加强应用，以便AsyncAndSyncEventMulticaster在启动时注册Listener
	@Autowired
	private ProcessAuditLogListener<T, PK> processAuditLogListener;
	@Autowired
	private ProcessDraftListener processDraftListener;
	@Autowired
	private ProcessRemoveListener processRemoveListener;
	@Autowired
	private ProcessTaskListener<T, PK> processTaskListener;
	@Autowired
	private ProcessUpdateListener processUpdateListener;
    @Autowired
    private ProcessTaskCallbackRetrySchedule processTaskCallbackRetrySchedule;
    @Autowired
    private ProcessTaskCreateCallbackListener processTaskCreateCallbackListener;
    @Autowired
    private ProcessTaskRemoveCallbackListener processTaskRemoveCallbackListener;

	private DynamicAuditUser dynamicAuditUser;
	
	public ProcessAdapterService(SqlSession sqlSession, ProcessHeaderCodeDynaEnum processHeaderCodeDynaEnum) {
		super(sqlSession);
	}
	
	@Override	
	public T createArchive(T o) {
		T returnProcess = null;
		int ret = 0;
		ProcessHeader processHeader = getProcessHeaderAndCheck();
		o.setProcessTypeId(processHeader.getTypeId()); //流程类型
		o.setProcessHeaderId(processHeader.getHeaderId()); //流程头
		ProcessStep startStep = processStepAdvanceService.getStartStep(processHeader.getHeaderId());
		o.setProcessStepId(startStep.getStepId()); //记录流程开始环节作为当前环节
		o.setProcessStepCode(processStepAdvanceService.loadByKey(o.getProcessStepId()).getStepCode());
		swithAudit(startStep, o);
		if(StringUtils.isEmpty((o.getCode()))){
			o.setCode(getProcessSeqCode());	
		}				
		if(o.getId() == null){
			o.setEnabled(false);
			ret = super.create(o); //全新提交
		}
		else if(o.getEnabled()){
			o.setEnabled(false);
			ret = super.create(o); //修改档案提交申请
		}
		else{
			o.setEnabled(false);
			ret = super.update(o); //从草稿提交
		}
		if(ret > 0){
			createRelationDataOnSubmit(o);
			uploadFiles(o);
			//记录操作日志（新建/修改申请状态由FirstStep向StartStep跃迁）
			ProcessJsonData<T,PK> processData = new ProcessJsonData<T,PK>(ProcessEnum.created, o);
			eventPublisher.publishEvent(new ProcessAuditLogEvent<T,PK>(this, processData, processStepAdvanceService.getFirstStep(processHeader.getHeaderId()).getStepId()));
			//记录审批代办
			publishProcessTaskEvent(processData, null);
			returnProcess = o;
		}
		return returnProcess;
	}
	
	@SuppressWarnings("unchecked")
	@Override	
	public T updateArchive(T o) {
		T returnProcess = null;
		T dbData = mapper.getById((PK) o.getId());
		if(dbData.getEnabled()){ //已有正式档案
			BeanUtils.copyProperties(o, dbData, ObjectUtil.getProcessServiceIdField(getClass()).getName()); //将前端表单数据复制数据库数据后，重新新增一条数据
			return createArchive(dbData); //3.修改正式档案，提交新的正式申请
		}else{ //1.修改申请草稿后，提交正式申请   2.驳回修改后，再次提交正式申请
			ProcessHeader processHeader = getProcessHeaderAndCheck();
			o.setProcessTypeId(processHeader.getTypeId()); //流程类型
			o.setProcessHeaderId(processHeader.getHeaderId()); //流程头
			Integer previousStepId = o.getProcessStepId(); //当前环节作为历史环节	
			ProcessStep startStep = processStepAdvanceService.getStartStep(processHeader.getHeaderId());
			o.setProcessStepId(startStep.getStepId()); //记录流程开始环节作为当前环节
			o.setProcessStepCode(processStepAdvanceService.loadByKey(o.getProcessStepId()).getStepCode());
			swithAudit(startStep, o);
			int ret = mapper.update(o);
			if(ret > 0){
				createRelationDataOnSubmit(o);
				uploadFiles(o);
				//记录操作日志（新建/修改申请状态由FirstStep向StartStep跃迁）
				ProcessJsonData<T,PK> processData = new ProcessJsonData<T,PK>(ProcessEnum.updated, o);		
				eventPublisher.publishEvent(new ProcessAuditLogEvent<T,PK>(this, processData, previousStepId));
				//记录审批代办				
				publishProcessTaskEvent(processData, previousStepId);
				returnProcess = o;
			}
			return returnProcess;
		}
	}
	
	@Override
	public T createArchiveDraft(T o) {
		T returnProcess = null;
		ProcessHeader processHeader = getProcessHeaderAndCheck();
		o.setProcessTypeId(processHeader.getTypeId()); //流程类型
		o.setProcessHeaderId(processHeader.getHeaderId()); //流程头
		ProcessStep startStep = processStepAdvanceService.getStartStep(processHeader.getHeaderId());
		o.setProcessStepId(startStep.getStepId()); //记录流程开始环节作为当前环节
		o.setProcessStepCode(processStepAdvanceService.loadByKey(o.getProcessStepId()).getStepCode());
		swithAudit(startStep, o);
		if(StringUtils.isEmpty((o.getCode()))){
			o.setCode(getProcessSeqCode());	
		}	
		o.setEnabled(false);
		int ret = super.create(o);
		if(ret > 0){
			createRelationDataOnSubmit(o);
			uploadFiles(o);
			eventPublisher.publishEvent(new ProcessDraftEvent(this, o));
			returnProcess = o;
		}
		return returnProcess;
	}
	
	@SuppressWarnings("unchecked")
	public T updateArchiveDraft(T o) {
		T returnProcess = null;
		T dbData = mapper.getById((PK) o.getId());
		if(dbData.getEnabled()){ //2.修改正式档案，再修改为草稿
			BeanUtils.copyProperties(o, dbData, ObjectUtil.getProcessServiceIdField(getClass()).getName()); //将前端表单数据复制数据库数据后，重新新增一条数据
			return createArchiveDraft(dbData);
		}else{//1.修改申请草稿
			ProcessHeader processHeader = getProcessHeaderAndCheck();
			o.setProcessTypeId(processHeader.getTypeId()); //流程类型
			o.setProcessHeaderId(processHeader.getHeaderId()); //流程头
			ProcessStep startStep = processStepAdvanceService.getStartStep(processHeader.getHeaderId());
			o.setProcessStepId(startStep.getStepId()); //记录流程开始环节作为当前环节
			o.setProcessStepCode(processStepAdvanceService.loadByKey(o.getProcessStepId()).getStepCode());
			swithAudit(startStep, o);
			int ret = super.update(o);
			if(ret > 0){
				createRelationDataOnSubmit(o);
				uploadFiles(o);
				eventPublisher.publishEvent(new ProcessDraftEvent(this, o));
				returnProcess = o;
			}
			return returnProcess;
		}		
	}
	
	@Override
	public T updateArchiveStep(ProcessJsonData<T, PK> data){
		T currentProcess = data.getBusinessData();
		T returnProcess = null;
		getProcessHeaderAndCheck(); //检查流程是否可用
		int ret = 0;
		Long currentReceiptId = null;
		Long previousReceiptId = null;
		Integer previousStepId = currentProcess.getProcessStepId();			
		ProcessStep nextStep = processStepAdvanceService.getActualNextStep(data.getResult(), currentProcess);
		currentProcess.setProcessStepId(nextStep.getStepId()); //记录流程当前待处理环节	
		currentProcess.setProcessStepCode(nextStep.getStepCode());
		swithAudit(nextStep, data.getResult(), currentProcess);
		//流程结束环节检查是否需要以新换旧
		if(processStepAdvanceService.isFinish(nextStep.getStepId())){
			currentProcess.setEnabled(true); //流程结束设置为Enabled=true
			T previousProcess = mapper.getEnabledPrevious(currentProcess.getCode());
			if(previousProcess != null){ //编码相同的数据，需要进行以新换旧的处理
				currentReceiptId = currentProcess.getId();
				previousReceiptId = previousProcess.getId();
				/**以新换旧不换主键Id**/
				BeanUtils.copyProperties(currentProcess, previousProcess, ObjectUtil.getProcessServiceIdField(getClass()).getName());
				deleteRelation(previousReceiptId); //删除老的原始档案关联关系（单据子表明细数据和之前上传的文件）
				updatePreviousArchive(previousReceiptId, currentReceiptId); // 维护新的档案关联关系（新的关联关系指回老的主键）
				deleteProcess(currentReceiptId); //删除新的档案
				currentProcess = previousProcess;					
			}
		}
		ret = mapper.update(currentProcess);		
		if(ret > 0 ){		
			onProcessChanged(currentProcess);
			uploadFiles(currentProcess);
			//记录操作日志				
			eventPublisher.publishEvent(new ProcessAuditLogEvent<T,PK>(this, data, previousStepId));
			//记录审批代办			
			publishProcessTaskEvent(data, previousStepId);
			//编码相同的数据，需要进行以新换旧的处理
			if(processStepAdvanceService.isFinish(nextStep.getStepId())){
				eventPublisher.publishEvent(new ProcessUpdateEvent(this, currentProcess.getProcessTypeId(), currentProcess.getProcessHeaderId(), currentReceiptId, previousReceiptId));
				afterProcessFinished(currentProcess);
			}
			returnProcess = currentProcess;
		}
		return returnProcess;
	}
	
	@Override
	public T createOnce(T o) {
		T returnProcess = null;
		int ret = 0;
		ProcessHeader processHeader = getProcessHeaderAndCheck();
		o.setProcessTypeId(processHeader.getTypeId()); //流程类型
		o.setProcessHeaderId(processHeader.getHeaderId()); //流程头				
		ProcessStep startStep = processStepAdvanceService.getStartStep(processHeader.getHeaderId());
		o.setProcessStepId(startStep.getStepId()); //记录流程开始环节作为当前环节
		o.setProcessStepCode(processStepAdvanceService.loadByKey(o.getProcessStepId()).getStepCode());
		swithAudit(startStep, o);
        if(StringUtils.isEmpty((o.getCode()))){
            o.setCode(getProcessSeqCode());
        }
        o.setEnabled(true); //一次性申请的流程Enabled=true
		if(o.getId() == null)
			ret = super.create(o); //全新提交
		else
			ret = super.update(o); //从草稿提交
		if(ret > 0){
			createRelationDataOnSubmit(o);
			uploadFiles(o);
			//记录操作日志（新建/修改申请状态由FirstStep向StartStep跃迁）
			ProcessJsonData<T,PK> processData = new ProcessJsonData<T,PK>(ProcessEnum.created, o);			
			eventPublisher.publishEvent(new ProcessAuditLogEvent<T,PK>(this, processData, processStepAdvanceService.getFirstStep(processHeader.getHeaderId()).getStepId()));
			//记录审批代办
			publishProcessTaskEvent(processData, null);
			returnProcess = o;
		}
		return returnProcess;
	}

	@Override
	public T updateOnce(T o) {
		T returnProcess = null;
		ProcessHeader processHeader = getProcessHeaderAndCheck();
		o.setProcessTypeId(processHeader.getTypeId()); //流程类型
		o.setProcessHeaderId(processHeader.getHeaderId()); //流程头
		Integer previousStepId = o.getProcessStepId(); //当前环节作为历史环节	
		ProcessStep startStep = processStepAdvanceService.getStartStep(processHeader.getHeaderId());
		o.setProcessStepId(startStep.getStepId()); //记录流程开始环节作为当前环节
		o.setProcessStepCode(processStepAdvanceService.loadByKey(o.getProcessStepId()).getStepCode());
		swithAudit(startStep, o);
		int ret = mapper.update(o);
		if(ret > 0){
			createRelationDataOnSubmit(o);
			uploadFiles(o);
			//记录操作日志（新建/修改申请状态由FirstStep向StartStep跃迁）
			ProcessJsonData<T,PK> processData = new ProcessJsonData<T,PK>(ProcessEnum.updated, o);	
			eventPublisher.publishEvent(new ProcessAuditLogEvent<T,PK>(this, processData, previousStepId));
			//记录审批代办				
			publishProcessTaskEvent(processData, previousStepId);
			returnProcess = o;
		}
		return returnProcess;
	}

	@Override
	public T createOnceDraft(T o) {
		T returnProcess = null;
		ProcessHeader processHeader = getProcessHeaderAndCheck();
		o.setProcessTypeId(processHeader.getTypeId()); //流程类型
		o.setProcessHeaderId(processHeader.getHeaderId()); //流程头
		ProcessStep startStep = processStepAdvanceService.getStartStep(processHeader.getHeaderId());
		o.setProcessStepId(startStep.getStepId()); //记录流程开始环节作为当前环节
		o.setProcessStepCode(processStepAdvanceService.loadByKey(o.getProcessStepId()).getStepCode());
		swithAudit(startStep, o);
        if(StringUtils.isEmpty((o.getCode()))){
            o.setCode(getProcessSeqCode());
        }
        o.setEnabled(false);
		int ret = super.create(o);
		if(ret > 0){
			createRelationDataOnSubmit(o);
			uploadFiles(o);
			eventPublisher.publishEvent(new ProcessDraftEvent(this, o));
			returnProcess = o;
		}
		return returnProcess;
	}

	@Override
	public T updateOnceDraft(T o) {
		T returnProcess = null;
		ProcessHeader processHeader = getProcessHeaderAndCheck();
		o.setProcessTypeId(processHeader.getTypeId()); //流程类型
		o.setProcessHeaderId(processHeader.getHeaderId()); //流程头
		ProcessStep startStep = processStepAdvanceService.getStartStep(processHeader.getHeaderId());
		o.setProcessStepId(startStep.getStepId()); //记录流程开始环节作为当前环节
		o.setProcessStepCode(processStepAdvanceService.loadByKey(o.getProcessStepId()).getStepCode());
		swithAudit(startStep, o);
		int ret = super.update(o);
		if(ret > 0){
			createRelationDataOnSubmit(o);
			uploadFiles(o);
			eventPublisher.publishEvent(new ProcessDraftEvent(this, o));
			returnProcess = o;
		}
		return returnProcess;	
	}

	@Override
	public T updateOnceStep(ProcessJsonData<T, PK> data){
		T currentProcess = data.getBusinessData();
		T returnProcess = null;
		getProcessHeaderAndCheck(); //检查流程是否可用
		int ret = 0;
		Integer previousStepId = currentProcess.getProcessStepId();
		ProcessStep nextStep =processStepAdvanceService.getActualNextStep(data.getResult(), currentProcess);
		currentProcess.setProcessStepId(nextStep.getStepId()); //记录流程当前待处理环节
		currentProcess.setProcessStepCode(nextStep.getStepCode());
		swithAudit(nextStep, data.getResult(), currentProcess);
		if(processStepAdvanceService.isFinish(nextStep.getStepId())){
			currentProcess.setEnabled(true); 
		}
		ret = mapper.update(currentProcess);
		if(ret > 0 ){		
			onProcessChanged(currentProcess);
			uploadFiles(currentProcess);
			//记录操作日志				
			eventPublisher.publishEvent(new ProcessAuditLogEvent<T,PK>(this, data, previousStepId));
			//记录审批代办			
			publishProcessTaskEvent(data, previousStepId);
			if(processStepAdvanceService.isFinish(nextStep.getStepId())){				
				afterProcessFinished(currentProcess);
			}
			returnProcess = currentProcess;
		}
		return returnProcess;
	}

	@Override
	public int deleteApply(PK id) {
		T dbData = mapper.getById(id);	
		int ret = 0;
		if(dbData != null){
            Map<String, String> map = removeFiles(dbData);//删除关联文件
            log.debug(map);
			deleteRelation(dbData.getId()); //删除关联数据
			ret = deleteProcess(dbData.getId()); //删除业务数据
			if(ret > 0){
				//删除代办、主单据状态、流程相关文件
				eventPublisher.publishEvent(new ProcessRemoveEvent(this, dbData));
			}
		}
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public int deleteProcess(Long id) {
		log.debug(id);
		log.debug(mapper);
		int ret = mapper.delete((PK) id);
		log.debug(ret);
		return ret;
	}

	@Override
	public int deleteDraft(PK id) {
		T dbData = mapper.getById(id);	
		int ret = 0;
		if(dbData != null){
            //删除关联文件
            Map<String, String> map = removeFiles(dbData);
            log.debug(map);
            //删除关联数据
			deleteRelation(dbData.getId());
			//删除草稿
			ret = processDraftService.delete(processTaskListener.getProcessDraft(dbData));
			log.debug(ret);
			//删除业务数据
			ret = deleteProcess(dbData.getId()); 
		}
		return ret;
	}
	
	@Override
	public ProcessHeader getProcessHeaderAndCheck() {
		ProcessHeader processHeader = processHeaderAdvanceService.loadByUnique(headerCode);
		if(!processHeader.getEnabled()){
			throw new ProcessUnavailableException("10011", String.format("10011 Current Process is unavailable: %s", processHeader.getHeaderCode() ));
		}
		return processHeader;
	}
		
	@Override
	public String getProcessSeqCode(){
		String prefix = getProcessHeaderAndCheck().getHeaderCode();
		String seqCode = AppCodeGenerator.nextDateCode(prefix);
		//测试期间，编码规则增加容器启动次数，CoreConfig会在每次容器重启时，计算当天重启次数，以避免编码重复
		if(Boolean.valueOf(config.getValue("app.debug"))){
			String times = config.getValue("app.restart.times");
			log.debug(times);
			seqCode += Constants.LINE+times+Constants.LINE+AppCodeGenerator.nextRandomInt(1);
		}
		return seqCode;
	}

	
	@Override
	//@Cacheable 不能使用缓存，否则流程跃迁每个环节候选审批人由于从缓存加载，因此不会发生变化
	public List<KeyValue<Integer, String>> getAuditUsers(PK id) {
		List<KeyValue<Integer,String>> optionsList = Lists.newArrayList();
		List<Integer> auditUserList = getRuntimeAuditorsByProcess(id);
		DynamicAuditUser dynamic = getDynamicAuditUser();
		if(dynamic != null){
			if(dynamic.getOps().equals(DynamicAuditUser.OPS.add)){
				auditUserList.addAll(dynamic.getAuditors());
			}else{
				auditUserList.removeAll(dynamic.getAuditors());
			}
		}
		for (Integer userId : auditUserList) {
			SysUser sysUser = sysUserAdvanceService.loadByKey(userId);
			if (sysUser != null) {
				optionsList.add(new KeyValue<Integer,String>(sysUser.getId(), sysUser.getUsername()));					
			}
		}
		return optionsList;
	}
	

	@SuppressWarnings("unchecked")
	@Override
	//@Cacheable 不能使用缓存，否则流程跃迁每个环节候选审批人由于从缓存加载，因此不会发生变化
	public Map<String, Object> getAuditUsersTree(PK id){
		List<KeyValue<Integer,String>> auditUserList = getAuditUsers(id);
		List<Integer> auditUserIds = Lists.newArrayList();
		for(KeyValue<Integer,String> auditor:auditUserList){
			auditUserIds.add(auditor.getKey());
		}
		//查找可用于作为审批候选人的用户类型
		String userType = config.getValue("app.usertype");
	    if(StringUtils.isEmpty(userType))
	    	userType = config.getValue("app.usertype.frontend");
	    if(StringUtils.isEmpty(userType))
	    	userType = "0";
	    Map<String, Object> fullUsersTree = sysUserAdvanceService.getUsersTreeData(Integer.valueOf(userType)); //userType=0
	    excludeUnAuditors((List<Map<String, Object>>) fullUsersTree.get("children"), auditUserIds);
		return fullUsersTree;
	}
	
	/**
	 * 从所有用户数中过滤掉非候选人的用户
	 * @param children
	 * @param auditUserIds
	 */
	@SuppressWarnings("unchecked")
	private void excludeUnAuditors(List<Map<String, Object>> fullUsers, List<Integer> auditUserIds){
		if(fullUsers != null){
			Iterator<Map<String,Object>> childMap = fullUsers.iterator();
			while(childMap.hasNext()){
				Map<String, Object> child = childMap.next();
				if(child.get("nodetype").equals("user")){
					Integer userId = (Integer) child.get("key");
					if(!auditUserIds.contains(userId)){
						childMap.remove();
					}
				}else{
					excludeUnAuditors((List<Map<String, Object>>) child.get("children"), auditUserIds);
				}
			}
		}
	}
	
	/**
	 * 根据具体流程单据主键Id，获取下一审批人信息
	 * @param id
	 * @return 审批候选人userId列表
	 */
	private List<Integer> getRuntimeAuditorsByProcess(PK id){
		ShiroUser user = appUserSession.getCurrentUser();
		if(id == null){ //初次提交
			ProcessStep firstStep = processStepAdvanceService.getFirstStep(getProcessHeaderAndCheck().getHeaderId());
			return processAuditAdvanceService.getAuditors(processStepAdvanceService.loadByUnique(firstStep.getPassId()).getStepCode());
		}else{
			T data = mapper.getById(id);		
			ProcessStep processStep = processStepAdvanceService.loadByKey(data.getProcessStepId());
			log.debug(processStepAdvanceService.getStartStep(processStep.getHeader().getHeaderId()));
			log.debug(processStep.getStepId());
			log.debug(user.getUserId());
			log.debug(data.getCreateUserId());
			//如果当前流程处于已结束环节，那么可选择审批对象为发起环节对应的审批对象（用于流程入库后重新提交编辑修改申请场景）
			if(processStepAdvanceService.isFinish(processStep.getStepId())){
				ProcessStep firstStep = processStepAdvanceService.getFirstStep(getProcessHeaderAndCheck().getHeaderId());
				return processAuditAdvanceService.getAuditors(processStepAdvanceService.loadByUnique(firstStep.getPassId()).getStepCode());
			}
			//如果当前流程处于启动环节，并且当前用户等于流程创建用户，那么可选择审批对象为当前环节的可选择项(用于即满足用户提交申请后，领导未审批前，还可变更审批领导信息场景)
			else if(processStepAdvanceService.getStartStep(processStep.getHeader().getHeaderId()).getStepId().equals(processStep.getStepId()) 
					&& user.getUserId().equals(data.getCreateUserId())){
				return processAuditAdvanceService.getAuditors(processStep.getStepCode());						
			}
			//自循环环节点
			else if(processStep.getStepType().equals(ProcessEnum.continually)){ 
				return processAuditAdvanceService.getAuditors(processStep.getStepCode());	
			}
			//否则，自动跃迁环节/自定义配置跃迁环节，可选择用户为当前环节的通过环节passId所对应的可选择项	
			else{						
				return processAuditAdvanceService.getAuditors(processStepAdvanceService.loadByUnique(processStep.getPassId()).getStepCode());			
			}
		}
	}

	
	/**
	 * 确定审批信息（可以由前端用户指定，也可由后台配置提供）
	 * @param step
	 * @param o
	 */
	@Override
	public void swithAudit(ProcessStep step, T o){
		swithAudit(step, ProcessEnum.pass, o);
	}
	
	/**
	 * 确定审批信息（可以由前端用户指定，也可由后台配置提供）
	 * @param step
	 * @param result
	 * @param o
	 */
	@Override
	public void swithAudit(ProcessStep step, ProcessEnum result, T o){
		log.debug("auditUser: "+o.getAuditUser());
		log.debug("auditRole: "+o.getAuditRole());
		//流程向前跃迁时，可以自主选择或者按配置获取审批人信息
		if(result.equals(ProcessEnum.pass) || result.equals(ProcessEnum.continued)){
			//用户既没有指定审批人，同时也没有审批角色
			if(StringUtils.isEmpty(o.getAuditUser()) && StringUtils.isEmpty(o.getAuditRole())){ 
				ProcessAudit audit = processAuditAdvanceService.getProcessAudit(step.getStepCode(), o);
				o.setSubjects(audit.getSubjects());
				o.setSubjectType(audit.getSubjectType());
			}
			//用户指定审批角色
			else if(StringUtils.isEmpty(o.getAuditUser())){ 
				o.setSubjects(o.getAuditRole());
				o.setSubjectType(ProcessEnum.audit_role);
			}
			//用户指定审批人
			else{ 
				o.setSubjects(o.getAuditUser());
				o.setSubjectType(ProcessEnum.audit_user);
			}
		//流程向后回退时，只能按照配置返回发起人	
		}else{
			ProcessAudit audit = processAuditAdvanceService.getProcessAudit(step.getStepCode(), o);
			o.setSubjects(audit.getSubjects());
			o.setSubjectType(audit.getSubjectType());
		}
	}

    protected void publishProcessTaskEvent(ProcessJsonData<T,PK> processData,Integer previousStepId){
		ProcessTaskCreateCallback createCallback = createProcessTaskCreateCallback();
		ProcessTaskRemoveCallback removeCallback = createProcessTaskRemoveCallback();
		if(createCallback!=null && removeCallback!=null){
			eventPublisher.publishEvent(new ProcessTaskEvent<>(this, processData, getNoticeMethod(), previousStepId, createCallback, removeCallback));
			return;
		}
		else if(createCallback!=null){
			eventPublisher.publishEvent(new ProcessTaskEvent<>(this, processData, getNoticeMethod(), previousStepId, createCallback));
			return;
		}
		else if(removeCallback!=null){
			eventPublisher.publishEvent(new ProcessTaskEvent<>(this, processData, getNoticeMethod(), previousStepId, removeCallback));
			return;
		}
		else{
			eventPublisher.publishEvent(new ProcessTaskEvent<>(this, processData, getNoticeMethod(), previousStepId));
			return;
		}
	}
	
	@Override
	protected void wrapCreateInfo(T o) {
		ShiroUser user = appUserSession.getCurrentUser();
		o.setCreateUserId(user.getUserId());
		o.setCreateUserCode(user.getUserCode());
		o.setCreateUserName(user.getUserName());
		o.setCreateDate(DateUtil.getCurrent());
		o.setOrgId(user.getOrgId()); // 提单创建部门	
		o.setOrgName(user.getOrgName()); // 提单创建部门
		wrapUpdateInfo(o);
	}
	
	public void setMapper(IProcessModelMapper<T, PK> mapper) {
		this.mapper = mapper;
		super.setMapper(mapper);
	}

	public void setHeaderCode(String headerCode) {
		this.headerCode = headerCode;
	}
	
	public String getHeaderCode() {
		return headerCode;
	}

	public void setNoticeMethod(ProcessTaskEvent.NoticMethod noticeMethod) {
		this.noticeMethod = noticeMethod;
	}

	public ProcessTaskEvent.NoticMethod getNoticeMethod() {
		return noticeMethod;
	}
	
	/**
	 * @return the dynamicAuditUser
	 */
	public DynamicAuditUser getDynamicAuditUser() {
		return dynamicAuditUser;
	}

	/**
	 * @param dynamicAuditUser the dynamicAuditUser to set
	 */
	public void setDynamicAuditUser(DynamicAuditUser dynamicAuditUser) {
		this.dynamicAuditUser = dynamicAuditUser;
	}

}
