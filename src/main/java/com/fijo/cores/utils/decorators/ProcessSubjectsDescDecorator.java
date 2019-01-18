/**
 * 
 */
package com.fijo.cores.utils.decorators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fijo.cores.app.model.ProcessModel;
import com.fijo.cores.app.service.IProcessAuditAdvanceService;

/**
 * 直接根据审批对象主键Id字符串，解析装饰审批对象描述信息
 * 
 * @author zhangbo
 *
 */
@Component
public class ProcessSubjectsDescDecorator extends AbstractBeanDecorator {

	@Autowired
	private IProcessAuditAdvanceService auditCache;	
	
	/**
	 * @param bean 流程
	 * @param property subjects
	 * @param strategy subjectsDesc
	 */
	@Override
	public void decorate(Object bean, String property, Object strategy) {			
		ProcessModel<?> process = (ProcessModel<?>)bean;	
		if(null !=process.getSubjects() && null != process.getSubjectType()){
			String subjectsDesc = auditCache.getSubjectsDesc(process.getSubjects(), process.getSubjectType(), process.getCreateUserId());
			process.setSubjectsDesc(subjectsDesc);	
		}
	}

}
