package com.fijo.cores.app.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.fijo.cores.app.model.*;
import com.fijo.cores.app.service.IProcessAuditAdvanceService;
import com.fijo.cores.app.service.IProcessHeaderAdvanceService;
import com.fijo.cores.app.service.IProcessStepAdvanceService;
import com.fijo.cores.app.service.IProcessTrackService;
import com.fijo.cores.exceptions.UnExpectedStepException;
import com.fijo.cores.service.IGenericService;
import com.fijo.cores.service.impl.GenericAdvanceService;
import com.fijo.cores.utils.DateUtil;
import com.fijo.cores.utils.enums.ProcessEnum;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.fijo.cores.app.model.ProcessAudit;
import com.fijo.cores.app.model.ProcessHeader;
import com.fijo.cores.app.model.ProcessModel;
import com.fijo.cores.app.model.ProcessStep;
import com.fijo.cores.app.model.ProcessStepCondition;
import com.fijo.cores.app.model.ProcessStepCondition.operation;
import com.fijo.cores.app.model.ProcessStepConfiguration;
import com.fijo.cores.app.model.ProcessStepConfiguration.logical;
import com.fijo.cores.app.model.ProcessTask;
import com.fijo.cores.app.model.ProcessTrack;
import com.fijo.cores.app.service.IProcessAuditAdvanceService;
import com.fijo.cores.app.service.IProcessHeaderAdvanceService;
import com.fijo.cores.app.service.IProcessStepAdvanceService;
import com.fijo.cores.app.service.IProcessTrackService;
import com.fijo.cores.cache.IGenericCache;
import com.fijo.cores.exceptions.UnExpectedStepException;
import com.fijo.cores.service.IGenericService;
import com.fijo.cores.service.impl.GenericAdvanceService;
import com.fijo.cores.utils.DateUtil;
import com.fijo.cores.utils.enums.ProcessEnum;


@Service(value="processStepAdvanceService")
public class ProcessStepAdvanceService extends GenericAdvanceService<ProcessStep,Integer> implements IProcessStepAdvanceService {

	@Autowired
	public IProcessHeaderAdvanceService processHeaderAdvanceService;
	
	@Autowired
	private IProcessAuditAdvanceService processAuditAdvanceService;
	
	@Autowired
	public IProcessTrackService processTrackService;
	
	@Autowired
	@Qualifier("processTaskService")
	private IGenericService<ProcessTask, Long> processTaskService;
	
	@Autowired
	public ProcessStepAdvanceService(
			@Qualifier(value="processStepService")IGenericService<ProcessStep, Integer> processStepService,
			@Qualifier(value="processStepCache")IGenericCache<ProcessStep,Integer> processStepCache) {
		super(processStepService, processStepCache);
	}
	
	@Override
	public ProcessStep getProcessStatusCheckNextStep(Integer stepId, ProcessEnum result){
		ProcessStep step = getKeyHashOps().get(stepId);	
		String nextStepCode = null;
		switch(result){
			case fail: //返回修改
				nextStepCode = step.getFailId();
				break;
			case pass: //进入下一个环节
				nextStepCode = step.getPassId();
				break;
			case refuse: //驳回结束
				nextStepCode = step.getStopId();
				break;
			case continued: //重复该环节
				nextStepCode = step.getStepCode();
				break;
			default:{
				log.error(String.format("10001", "Not such audit result of next process step id: %s", stepId ));
				throw new UnExpectedStepException("10001", String.format("10001", "Not such audit result of next process step id: %s", stepId ));
			}
		}
		if(nextStepCode == null){			
			log.error(String.format("10001", "Not such audit result of next process step id: %s", stepId ));
			throw new UnExpectedStepException("10001", String.format("10001", "Not such audit result of next process step id: %s", stepId ));
		}
		return getUniqueHashOps().get(nextStepCode);
	}
	
	@Override
	public ProcessStep getActualNextStep(ProcessEnum result, ProcessModel<?> process){
		//执行检查登录用户是否有权限处理该环节，如果权限不足将抛出UnauthorizedException（防止URL穿透）
		ProcessTask currUserTask = processAuditAdvanceService.checkCurrentUserAudit(process.getProcessTypeId(), process.getProcessHeaderId(), process.getId());		
		ProcessStep currStep = loadByKey(process.getProcessStepId());
		if(currStep.getStepClass().equals(ProcessEnum.fork)){ //流程已经停留在fork节点
			//从当前用户实际执行的ProcessTask对应的ProcessStep中获取一下环节
			currStep = loadByKey(currUserTask.getStepId());
			process.setProcessStepId(currStep.getStepId());
			process.setProcessStepCode(currStep.getStepCode());
			return getActualNextStep(result, process);
		}else{
			ProcessStep nextStep = getProcessStatusCheckNextStep(process.getProcessStepId(), result);	
			if(nextStep.getStepClass().equals(ProcessEnum.fork)){ //流程到达分支节点，生成并行跟踪记录后，直接返回fork节点
				ProcessStep forkStep = nextStep;
				ProcessStep params = new ProcessStep();
				params.setForkFromId(forkStep.getStepCode());
				params.setStepClass(ProcessEnum.parallel);
				Collection<ProcessStep> forkedParallelSteps = getAll(params);
				for(ProcessStep parallel : forkedParallelSteps){
					ProcessTrack track = new ProcessTrack(process.getProcessTypeId(), process.getProcessHeaderId(), process.getId(), forkStep.getStepCode(), parallel.getStepCode());
					processTrackService.create(track);
				}			
				return forkStep;
			}else if(nextStep.getStepClass().equals(ProcessEnum.join)){ //流程到达汇聚节点				
				ProcessStep forkStep = loadByUnique(nextStep.getForkFromId()); // 流程从fork开始，节点状态就未发生变化，一致保持为fork。直至track中所有parallel到达join
				ProcessTrack track = new ProcessTrack(process.getProcessTypeId(), process.getProcessHeaderId(), process.getId(), forkStep.getStepCode(), process.getProcessStepCode(), nextStep.getStepCode());
				int ret = processTrackService.update(track);
				log.debug(ret);
				//判断是否需要将fork跃迁
				track.setCurrentStepCode(null);
				boolean forkTojoin = processTrackService.checkAllParallelToJoin(track, forkStep.getPassId());
				if(forkTojoin){ // 所有track节点到达join位置
					//2.1 删除所有track
					processTrackService.delete(track);
					//2.2 删除这最后一个到达join的待办
					//由ProcessTaskListener进行处理
					//2.3fork跃迁至join，获取join的下一环节信息
					ProcessStep joinStep = loadByUnique(forkStep.getPassId());
					process.setProcessStepId(joinStep.getStepId());
					process.setProcessStepCode(joinStep.getStepCode());
					return getActualNextStep(result, process);
				}else{
					return forkStep;  //保持为fork
				}
			}else if(nextStep.getStepClass().equals(ProcessEnum.parallel)){
				ProcessStep forkStep = loadByUnique(nextStep.getForkFromId());
				ProcessTrack track = new ProcessTrack(process.getProcessTypeId(), process.getProcessHeaderId(), process.getId(), forkStep.getStepCode(), process.getProcessStepCode(), nextStep.getStepCode());
				int ret = processTrackService.update(track);
				log.debug(ret);
				return forkStep;  //保持为fork
			}else{
				if(nextStep.getStepType().equals(ProcessEnum.special)){ //条件触发的特殊环节
					ProcessAudit audit = getConfigurationAudit(nextStep, process);
					if(audit == null){ //数据不满足配置，直接跳过，再找下一环节
						process.setProcessStepId(nextStep.getStepId());
						process.setProcessStepCode(nextStep.getStepCode());
						return getActualNextStep(result, process);
					}else{
						return nextStep;
					}
				}
				else{
					return nextStep;
				}
			}
		}
	}
	
	@Override
	public ProcessStep getFirstStep(Integer headerId) {
		ProcessStep firstStep = null;
		List<ProcessStep> steps = processHeaderAdvanceService.getSteps(headerId);
		if(steps==null || steps.size()==0){
			log.error(String.format("10005 Not found processHeader: %s", headerId ));
			throw new UnExpectedStepException("10005", String.format("10005 Not found processHeader: %s", headerId ));
		}
		else{
			int firstStepNum = 0;
			for(ProcessStep step : steps){
				if(step.getStepType().equals(ProcessEnum.first)){ //表示该环节为起始环节
					firstStep = step;
					firstStepNum++;
				}
			}
			if(firstStepNum > 1){
				log.error(String.format("10007 Found more than one first step of this processHeader: %s", headerId ));
				throw new UnExpectedStepException("10007", String.format("10007 Found more than one first step of this processHeader: %s", headerId ));
			}
		}
		if(firstStep == null){
			log.error(String.format("10006 Not found first step of this processHeader: %s", headerId ));
			throw new UnExpectedStepException("10006", String.format("10006 Not found first step of this processHeader: %s", headerId ));
		}
		return firstStep;
	}

	@Override
	public ProcessStep getStartStep(Integer headerId) {
		ProcessStep firstStep = getFirstStep(headerId);
		ProcessStep startStep = loadByUnique(firstStep.getPassId());
		if(startStep == null){
			log.error(String.format("10008 Not found Start step of this processHeader: %s", headerId ));
			throw new UnExpectedStepException("10005", String.format("10008 Not found Start step of this processHeader: %s", headerId));
		}
		return startStep;
	}

	@Override
	public ProcessStep getLastStep(Integer headerId) {
		ProcessStep lastStep = null;
		ProcessHeader header = processHeaderAdvanceService.loadByKey(headerId);
		if(header == null){
			log.error(String.format("10005 Not found processHeader: %s", headerId ));
			throw new UnExpectedStepException("10005", String.format("10005 Not found processHeader: %s", headerId ));
		}
		else if(header.getSteps().size() == 0){
			log.error(String.format("10006 Not found any steps of this processHeader: %s", headerId ));
			throw new UnExpectedStepException("10006", String.format("10006 Not found any steps of this processHeader: %s", headerId ));
		}
		else{
			int lastStepNum = 0;
			for(ProcessStep step : header.getSteps()){
				if(step.getStepType().equals(ProcessEnum.stop)){ //表示该环节为正常结束环节
					lastStep = step;
					lastStepNum++;
				}
			}
			if(lastStepNum > 1){
				log.error(String.format("10007 Found more than one first step of this processHeader: %s", headerId ));
				throw new UnExpectedStepException("10007", String.format("10007 Found more than one first step of this processHeader: %s", headerId ));
			}
		}
		if(lastStep == null){
			log.error(String.format("10006 Not found first step of this processHeader: %s", headerId ));
			throw new UnExpectedStepException("10006", String.format("10006 Not found first step of this processHeader: %s", headerId ));
		}
		return lastStep;
	}
	
	@Override
	public boolean isFirst(Integer stepId) {
		ProcessStep step = loadByKey(stepId);
		if(step == null)
			return false;
		return step.getStepType().equals(ProcessEnum.first);
	}

	@Override
	public boolean isStart(Integer headerId, Integer stepId) {
		return getStartStep(headerId).getStepId().equals(stepId);
	}
	
	@Override
	public boolean isFinish(Integer stepId) {
		return isSuccessFinish(stepId) || isErrorFinish(stepId);
	}

	@Override
	public boolean isSuccessFinish(Integer stepId) {
		ProcessStep step = loadByKey(stepId);
		if(step != null)
			return step.getStepType().equals(ProcessEnum.stop);
		else 
			return false;
	}

	@Override
	public boolean isErrorFinish(Integer stepId) {
		ProcessStep step = loadByKey(stepId);
		if(step != null)
			return step.getStepType().equals(ProcessEnum.errorStop);
		else 
			return false;
	}

	@Override
	public ProcessAudit getConfigurationAudit(ProcessStep step, ProcessModel<?> process) {
		ProcessAudit audit = null;
		int count = 0;
		for(ProcessStepConfiguration configuration: step.getConfigurations()){
			audit = checkConfigurationConditions(configuration, process);
			if(audit != null){
				count++;
			}
		}
		if(count > 1){
			log.error(String.format("10010", String.format("Too many configurations can satisfied this step :%s with process: %s", step.getStepCode(), process)));
			throw new UnExpectedStepException("10010", String.format("Too many configurations can satisfied this step :%s with process: %s", step.getStepCode(), process));
		}
		return audit;
	}

	@SuppressWarnings("rawtypes")
	private ProcessAudit checkConfigurationConditions(ProcessStepConfiguration configuration, ProcessModel<?> process) {
		List<ProcessStepCondition> conditions = configuration.getConditions();
		List<Boolean> results = Lists.newArrayList();
		for (ProcessStepCondition condition : conditions) {
			Comparable beanValue = null;
			Comparable conditionValue = null;
			try {
				beanValue = (Comparable) PropertyUtils.getNestedProperty(process, condition.getName());
				if (condition.getOpt() == ProcessStepCondition.operation.Null) {
					if (beanValue == null)
						results.add(true);
					else
						results.add(false);
				} else {
					if (beanValue instanceof BigDecimal) {
						conditionValue = new BigDecimal(condition.getValue().toString()); // JSON将数字转换为BigDecimal
					} else if (beanValue instanceof Double) {
						conditionValue = Double.valueOf(condition.getValue().toString()); // JSON将数字转换为Double
					} else if (beanValue instanceof Float) {
						conditionValue = Float.valueOf(condition.getValue().toString()); // JSON将数字转换为Float
					} else if (beanValue instanceof Long) {
						conditionValue = Long.valueOf(condition.getValue().toString()); // JSON将数字转换为Long
					} else if (beanValue instanceof Integer) {
						conditionValue = Integer.valueOf(condition.getValue().toString()); // JSON将数字转换为Integer
					} else if (beanValue instanceof Short) {
						conditionValue = Short.valueOf(condition.getValue().toString()); // JSON将数字转换为Short
					} else if (beanValue instanceof Date) {
						conditionValue = DateUtil.parseDate(condition.getValue().toString()); // JSON将数字转换为Date
					} else if (beanValue instanceof Boolean) {
						conditionValue = Boolean.valueOf(condition.getValue().toString()); // JSON将数字转换为Date
					}else if (beanValue instanceof String) {
						conditionValue = String.valueOf(condition.getValue()); // JSON将数字转换为String
					}
				}
			} catch (IllegalAccessException | InvocationTargetException
					| NoSuchMethodException e) {
				log.error(String.format("100", "checkConfigurationConditions from %s with property %s failed:", process, condition.getName()));
			}
			if (beanValue != null && conditionValue != null) {
				@SuppressWarnings("unchecked")
				int compare = beanValue.compareTo(conditionValue);
				switch (condition.getOpt()) {
					case gt: {
						if(compare==1)
							results.add(true);
						else
							results.add(false);		
						break;
					}
					case ge: {
						if(compare==1 || compare==0)
							results.add(true);
						else
							results.add(false);
						break;
					}
					case lt: {
						if(compare==-1)
							results.add(true);
						else
							results.add(false);	
						break;
					}
					case le: {
						if(compare==-1 || compare==0)
							results.add(true);
						else
							results.add(false);
						break;
					}
					case eq: {
						if(compare==0)
							results.add(true);
						else
							results.add(false);
						break;
					}
					default:
						break;
				}
			}
		}
		if(results.size() == 0){
			return null;
		}else{
			if (configuration.getLogic().equals(ProcessStepConfiguration.logical.And)) {
				if (results.contains(false))
					return null;
				else
					return new ProcessAudit(configuration.getSubjectType(), configuration.getSubjects());
			} else if (configuration.getLogic().equals(ProcessStepConfiguration.logical.Or)) {
				if (results.contains(true))
					return new ProcessAudit(configuration.getSubjectType(), configuration.getSubjects());
				else
					return null;
			}
		}
		return null;
	}
	
	/**
	 * 选择审批结果可选项（不常用）
	 * @param stepCode
	 * @return
	 */
	@Override
	public List<ProcessEnum> getSelectOptions(String stepCode) {
		List<ProcessEnum> options = Lists.newArrayList();
		ProcessStep step = getUniqueHashOps().get(stepCode);
		if(step.getPassId() != null)
			options.add(ProcessEnum.pass);
		if(step.getFailId() != null)
			options.add(ProcessEnum.fail);
		if(step.getStopId() != null)
			options.add(ProcessEnum.refuse);
		return options;
	}
	
}
