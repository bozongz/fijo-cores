package com.fijo.cores.app.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fijo.cores.utils.annotations.NotNullColumn;
import com.fijo.cores.utils.enums.ProcessEnum;
import com.fijo.cores.model.GenericModel;
import com.fijo.cores.utils.annotations.NotNullColumn;
import com.fijo.cores.utils.enums.ProcessEnum;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 业务审批组织对应(一个ProcessAudit对应多个ProcessStep)
 * 
 * @author zhangbo
 *
 */
@Entity
@Table(name = "app_process_audit", uniqueConstraints={@UniqueConstraint(columnNames={"submitUserId", "submitOrgId", "processStepId", "aversion"})})
@XmlRootElement
@ApiModel
public class ProcessAudit extends GenericModel<ProcessAudit> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2749196087177329065L;

	@Id
	@Column(name = "auditId")
    @SequenceGenerator(name="app_process_audit_seq", sequenceName="app_process_audit_seq")
    @GeneratedValue(strategy=GenerationType.AUTO, generator="app_process_audit_seq")
    @ApiModelProperty(value="主键Id")
	private Integer auditId;
		
	@Column(name = "submitUserId", nullable = true, length = 100) //根据发起人，确定审批配置后，先加载审批对象给前端用户进行选择，然后再根据选择结果触发待办（若为空，再判断组织）
	@JsonIgnore
    @ApiModelProperty(value="发起人Id")
	private String submitUserId; 
	
	@Transient
	private String submitUserIds;
	
	/**
	 * 定义一个审批流所涉及所有环节的发起组织审批线条
	 * 各环节发起组织一致，但相同环节可由不同的角色或人员审批
	 */		
	@Column(name = "submitOrgId", nullable = true, length = 100) //根据发起部门，确定审批配置后，直接触发待办（若为空，则所有部门均为此审批配置）
	@JsonIgnore
    @ApiModelProperty(value="发起部门Id")
	private String submitOrgId; 
	
	@Transient
	private String submitOrgIds;
	
	@NotNullColumn(value="审批类型") //audit_role("基于角色"), audit_user("基于用户"), audit_both("分支审批, subjects可为空")
	@Column(name = "subjectType", nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
    @ApiModelProperty(value="审批类型")
	private ProcessEnum subjectType;
	
	@NotNullColumn(value="审批对象")  //支持保存多个SysRoleId 或者 多个SysUserId
	@Column(name = "subjects", nullable = true)
    @ApiModelProperty(value="审批对象")
	private String subjects;
    
    @NotNullColumn(value="流程环节")
	@ManyToOne
	@JoinColumn(name = "processStepId", nullable = false)
    @ApiModelProperty(value="流程环节")
	private ProcessStep processStep;
	
	@Column(name = "aversion", nullable = false)
    @JsonIgnore
    @ApiModelProperty(value="版本号")
    private Integer aversion;
		
	@Transient	
	private String subjectsDesc; //由ProcessOrgDecorator获取
	
    public ProcessAudit() {
		super();
    }
    
	public ProcessAudit(Integer auditId) {
		super();
		this.auditId = auditId;
	}

	public ProcessAudit(ProcessEnum subjectType, String subjects) {
		super();
		this.subjectType = subjectType;
		this.subjects = subjects;
	}

	/**
	 * @return the auditId
	 */
	public Integer getAuditId() {
		return auditId;
	}

	/**
	 * @param auditId the auditId to set
	 */
	public void setAuditId(Integer auditId) {
		this.auditId = auditId;
	}

	/**
	 * @return the subjectType
	 */
	public ProcessEnum getSubjectType() {
		return subjectType;
	}

	/**
	 * @param subjectType the subjectType to set
	 */
	public void setSubjectType(ProcessEnum subjectType) {
		this.subjectType = subjectType;
	}

	/**
	 * @return the subjects
	 */
	public String getSubjects() {
		return subjects;
	}

	/**
	 * @param subjects the subjects to set
	 */
	public void setSubjects(String subjects) {
		this.subjects = subjects;
	}

	public ProcessStep getProcessStep() {
		return processStep;
	}

	public void setProcessStep(ProcessStep processStep) {
		this.processStep = processStep;
	}

	/**
	 * @return the subjectsDesc
	 */
	public String getSubjectsDesc() {
		return subjectsDesc;
	}

	/**
	 * @param subjectsDesc the subjectsDesc to set
	 */
	public void setSubjectsDesc(String subjectsDesc) {
		this.subjectsDesc = subjectsDesc;
	}

	public String getSubmitUserId() {
		return submitUserId;
	}

	public void setSubmitUserId(String submitUserId) {
		this.submitUserId = submitUserId;
	}

	public String getSubmitOrgId() {
		return submitOrgId;
	}

	public void setSubmitOrgId(String submitOrgId) {
		this.submitOrgId = submitOrgId;
	}

	public Integer getAversion() {
		return aversion;
	}

	public void setAversion(Integer aversion) {
		this.aversion = aversion;
	}

	public String getSubmitUserIds() {
		return submitUserIds;
	}

	public void setSubmitUserIds(String submitUserIds) {
		this.submitUserIds = submitUserIds;
	}

	public String getSubmitOrgIds() {
		return submitOrgIds;
	}

	public void setSubmitOrgIds(String submitOrgIds) {
		this.submitOrgIds = submitOrgIds;
	}
	
}