package com.fijo.cores.utils.enums;

/**
 * 流程枚举
 * 
 * @author zhangbo
 *
 */
public enum ProcessEnum implements GenericEnum{	
	//ProcessType Code
	GLOBAL("全局流程"),
	
	//ProcessAudit 流程处理类型
	audit_role("基于角色"), audit_user("基于用户"), audit_both("分支审批"), 
	
	//ProcessStep 环节分类stepClass
	serial("串行节点"), parallel("并行节点"), fork("分支节点"), join("汇聚节点"),
	
	//ProcessStep 环节类型stepType
	//修改这部分Enum，务必修改ProcessStatusMapper.xml的SQL
	first("首环节"), stop("结束环节"), errorStop("终止环节"), autoChange("跃迁环节"), continually("循环环节"), special("条件触发环节"), defaults("默认环节"), 
	
	//流程审批结果
	pass("同意"), fail("退回"), refuse("终止"), continued("转协助办理"),

	created("已提交"), 
	updated("已修改"),

    completed("已归档");

    private String value;

	private ProcessEnum(String value) {
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
}
