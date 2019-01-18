package com.fijo.cores.app.model;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.fijo.cores.utils.annotations.NotNullColumn;
import com.fijo.cores.utils.annotations.ProcessProperty;
import com.fijo.cores.utils.enums.ProcessEnum;
import com.fijo.cores.model.LogicModel;
import com.fijo.cores.utils.annotations.NotNullColumn;
import com.fijo.cores.utils.annotations.ProcessProperty;
import com.fijo.cores.utils.enums.ProcessEnum;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;


/**
 * 业务流程实体基类
 * @author zhangbo
 *
 */
@MappedSuperclass
@ApiModel
public class ProcessModel<T> extends LogicModel<T> {
	/**
	 * 
	 */
	protected static final long serialVersionUID = 6437256811072081419L;
	
	//所有业务单据的主键必须统一类型，否则无法写入待办和审批记录
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	@ProcessProperty
    @ApiModelProperty(value="业务单据主键Id")
	protected Long id;
	
	@NotNullColumn(value="是否为草稿")
	@Column(name = "iscg", nullable = false, columnDefinition = "int default 0")
	@ProcessProperty
    @ApiModelProperty(value="是否为草稿")
	protected Boolean iscg;
	
	@NotNullColumn(value="单据编号")
	@Column(name = "code", nullable = false,length = 100)
	@ProcessProperty
    @ApiModelProperty(value="单据编号")
	protected String code;

	@NotNullColumn(value="单据名称")
	@Column(name = "title", nullable = false, length = 500)
	//@ProcessProperty 可以让编辑修改流程时，修改申请标题
    @ApiModelProperty(value="单据名称")
	protected String title;
	
	@NotNullColumn(value="流程类型")
	@Column(name = "processTypeId", nullable = false) 
	@ProcessProperty
    @ApiModelProperty(value="流程类型Id")
	protected Integer processTypeId;

	@Transient
	protected String typeDesc;
	
	@NotNullColumn(value="流程描述")
	@Column(name = "processHeaderId", nullable = false) 
	@ProcessProperty
    @ApiModelProperty(value="流程描述Id")
	protected Integer processHeaderId;

	@Transient
	protected String headerDesc;
	
	@Transient	
	protected String processHeaderCode;
	
	@NotNullColumn(value="流程环节")
	@Column(name = "processStepId", nullable = false) 
	@ProcessProperty
    @ApiModelProperty(value="流程环节Id")
	protected Integer processStepId;
	
	@NotNullColumn(value="流程环节")
	@Column(name = "processStepCode", nullable = false)
	@ProcessProperty
    @ApiModelProperty(value="流程环节编码")
	protected String processStepCode;
	
	@Transient
	protected String stepDesc; //当前环节状态

	@NotNullColumn(value="审批对象")
	@Column(name = "subjects", nullable = true) //分支环节可以为空
	@ProcessProperty
    @ApiModelProperty(value="审批对象")
	protected String subjects;
	
	@NotNullColumn(value="审批对象")
	@Column(name = "subjectType", nullable = false)
	@Enumerated(EnumType.STRING)
	@ProcessProperty
    @ApiModelProperty(value="审批对象类型")
	protected ProcessEnum subjectType;
	
	@Transient
	protected String subjectsDesc;
	
	@NotNullColumn(value="发起部门")
	@Column(name = "orgId", nullable = false)
	@ProcessProperty
    @ApiModelProperty(value="发起部门Id")
	protected Integer orgId; 
	 
	@Column(name = "orgName", nullable = false)
	@ProcessProperty
    @ApiModelProperty(value="发起部门名称")
	protected String orgName;

	@Transient
	protected String auditUser; //用于用户自己选择审批人(多个Id空格分隔)
	
	@Transient
	protected String auditRole; //用于用户自己选择审批角色(多个Id空格分隔)
	
	public ProcessModel() {
		super();
	}

	public ProcessModel(Long id) {
		super();
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	
	/**
	 * @return the processTypeId
	 */
	public Integer getProcessTypeId() {
		return processTypeId;
	}

	/**
	 * @param processTypeId the processTypeId to set
	 */
	public void setProcessTypeId(Integer processTypeId) {
		this.processTypeId = processTypeId;
	}

	/**
	 * @return the typeDesc
	 */
	public String getTypeDesc() {
		return typeDesc;
	}

	/**
	 * @param typeDesc the typeDesc to set
	 */
	public void setTypeDesc(String typeDesc) {
		this.typeDesc = typeDesc;
	}

	/**
	 * @return the processHeaderId
	 */
	public Integer getProcessHeaderId() {
		return processHeaderId;
	}

	/**
	 * @param processHeaderId the processHeaderId to set
	 */
	public void setProcessHeaderId(Integer processHeaderId) {
		this.processHeaderId = processHeaderId;
	}

	/**
	 * @return the headerDesc
	 */
	public String getHeaderDesc() {
		return headerDesc;
	}

	/**
	 * @param headerDesc the headerDesc to set
	 */
	public void setHeaderDesc(String headerDesc) {
		this.headerDesc = headerDesc;
	}

	/**
	 * @return the processStepId
	 */
	public Integer getProcessStepId() {
		return processStepId;
	}

	/**
	 * @param processStepId the processStepId to set
	 */
	public void setProcessStepId(Integer processStepId) {
		this.processStepId = processStepId;
	}

	/**
	 * @return the stepDesc
	 */
	public String getStepDesc() {
		return stepDesc;
	}

	/**
	 * @param stepDesc the stepDesc to set
	 */
	public void setStepDesc(String stepDesc) {
		this.stepDesc = stepDesc;
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

	/**
	 * @return the orgId
	 */
	public Integer getOrgId() {
		return orgId;
	}

	/**
	 * @param orgId the orgId to set
	 */
	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	/**
	 * @return the orgName
	 */
	public String getOrgName() {
		return orgName;
	}

	/**
	 * @param orgName the orgName to set
	 */
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the processHeaderCode
	 */
	public String getProcessHeaderCode() {
		return processHeaderCode;
	}

	/**
	 * @param processHeaderCode the processHeaderCode to set
	 */
	public void setProcessHeaderCode(String processHeaderCode) {
		this.processHeaderCode = processHeaderCode;
	}

	public String getSubjects() {
		return subjects;
	}

	public void setSubjects(String subjects) {
		this.subjects = subjects;
	}

	public ProcessEnum getSubjectType() {
		return subjectType;
	}

	public void setSubjectType(ProcessEnum subjectType) {
		this.subjectType = subjectType;
	}

	public String getProcessStepCode() {
		return processStepCode;
	}

	public void setProcessStepCode(String processStepCode) {
		this.processStepCode = processStepCode;
	}

	public Boolean getIscg() {
		return iscg;
	}

	public void setIscg(Boolean iscg) {
		this.iscg = iscg;
	}

	public String getAuditUser() {
		return auditUser;
	}

	public void setAuditUser(String auditUser) {
		this.auditUser = auditUser;
	}

	public String getAuditRole() {
		return auditRole;
	}

	public void setAuditRole(String auditRole) {
		this.auditRole = auditRole;
	}	

}