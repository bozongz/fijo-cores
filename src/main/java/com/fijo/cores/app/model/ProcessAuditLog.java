package com.fijo.cores.app.model;

import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fijo.cores.utils.annotations.NotNullColumn;
import com.fijo.cores.utils.enums.ProcessEnum;
import com.fijo.cores.model.GenericModel;
import com.fijo.cores.utils.annotations.NotNullColumn;
import com.fijo.cores.utils.enums.ProcessEnum;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 业务流程审批记录
 * 
 * @author zhangbo
 *
 */
@Entity
@Table(name = "app_process_audit_log")
@XmlRootElement
@ApiModel
public class ProcessAuditLog extends GenericModel<ProcessAuditLog> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7840711197568350595L;

	@Id
	@Column(name = "id")
    @SequenceGenerator(name="app_process_audit_log_seq", sequenceName="app_process_audit_log_seq")
    @GeneratedValue(strategy=GenerationType.AUTO, generator="app_process_audit_log_seq")
    @ApiModelProperty(value="主键Id")
	private Long id;
	
	@NotNullColumn(value="处理人部门信息")
	@Column(name = "orgId", nullable = false) //不允许为空
    @ApiModelProperty(value="处理人部门Id")
	private Integer orgId; 

	@Column(name = "orgName", nullable = false) //不允许为空
    @ApiModelProperty(value="处理人部门")
	private String orgName;
	
	//所有业务单据的主键必须统一类型，否则无法写入待办和审批记录
	@NotNullColumn(value="业务单据Id")
	@Column(name = "receiptId", nullable = false)
    @ApiModelProperty(value="业务单据Id")
	private Long receiptId;

	@NotNullColumn(value="流程类型")
	@Column(name = "typeId", nullable = false)
    @ApiModelProperty(value="流程类型Id")
	private Integer typeId;

	@NotNullColumn(value="流程头")
	@Column(name = "headerId", nullable = false)
    @ApiModelProperty(value="流程头Id")
	private Integer headerId;
	
	@NotNullColumn(value="流程环节")
	@Column(name = "stepId", nullable = false, length = 20)
    @ApiModelProperty(value="流程环节Id")
	private Integer stepId;
	
	@NotNullColumn(value="已处理环节")
	@Column(name = "previousStepId", nullable = false, length = 20)
    @ApiModelProperty(value="已处理环节Id")
	private Integer previousStepId;
	
	@NotNullColumn(value="审批结果")
	@Column(name = "result", nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
    @ApiModelProperty(value="审批结果")
	private ProcessEnum result;
		
	private String resultDesc;
	
	@Column(name = "opinion", length = 200)
    @ApiModelProperty(value="审批意见")
	private String opinion;

    @ApiModelProperty(value="流程类型")
    private String typeDesc;

    @ApiModelProperty(value="流程头")
	private String headerDesc;

    @ApiModelProperty(value="上一环节")
	private String previousStepDesc;

    @ApiModelProperty(value="当前环节")
	private String stepDesc;
	
	@Column(name = "createUserId", nullable = false, length = 50)
    @ApiModelProperty(value="创建人Id")
	protected Integer createUserId;	
	
	@Column(name = "createUserCode", nullable = true, length = 50)
    @ApiModelProperty(value="创建人编码")
	protected String createUserCode;	
	
	@Column(name = "createUserName", nullable = false, length = 50)
    @ApiModelProperty(value="创建人名称")
	protected String createUserName; 
	
	@Temporal(TemporalType.TIMESTAMP) 
	@Column(name = "createDate", nullable = false)
    @ApiModelProperty(value="创建时间")
	protected Date createDate;

	public ProcessAuditLog() {
		super();
	}

	public ProcessAuditLog(Long id) {
		super();
		this.id = id;
	}
	public ProcessAuditLog(Integer typeId, Integer headerId, Long receiptId) {
		super();
		this.receiptId = receiptId;
		this.typeId = typeId;
		this.headerId = headerId;
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
	 * @return the receiptId
	 */
	public Long getReceiptId() {
		return receiptId;
	}
	/**
	 * @param receiptId the receiptId to set
	 */
	public void setReceiptId(Long receiptId) {
		this.receiptId = receiptId;
	}
	
	public Integer getTypeId() {
		return typeId;
	}
	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}
	public Integer getHeaderId() {
		return headerId;
	}
	public void setHeaderId(Integer headerId) {
		this.headerId = headerId;
	}
	/**
	 * @return the stepId
	 */
	public Integer getStepId() {
		return stepId;
	}
	/**
	 * @param stepId the stepId to set
	 */
	public void setStepId(Integer stepId) {
		this.stepId = stepId;
	}
	/**
	 * @return the previousStepId
	 */
	public Integer getPreviousStepId() {
		return previousStepId;
	}
	/**
	 * @param previousStepId the previousStepId to set
	 */
	public void setPreviousStepId(Integer previousStepId) {
		this.previousStepId = previousStepId;
	}
	/**
	 * @return the result
	 */
	public ProcessEnum getResult() {
		return result;
	}
	/**
	 * @param result the result to set
	 */
	public void setResult(ProcessEnum result) {
		this.result = result;
	}
	/**
	 * @return the opinion
	 */
	public String getOpinion() {
		return opinion;
	}
	/**
	 * @param opinion the opinion to set
	 */
	public void setOpinion(String opinion) {
		this.opinion = opinion;
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
	 * @return the previousStepDesc
	 */
	public String getPreviousStepDesc() {
		return previousStepDesc;
	}
	/**
	 * @param previousStepDesc the previousStepDesc to set
	 */
	public void setPreviousStepDesc(String previousStepDesc) {
		this.previousStepDesc = previousStepDesc;
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
	 * @return the createUserId
	 */
	public Integer getCreateUserId() {
		return createUserId;
	}
	/**
	 * @param createUserId the createUserId to set
	 */
	public void setCreateUserId(Integer createUserId) {
		this.createUserId = createUserId;
	}
	/**
	 * @return the createUserCode
	 */
	public String getCreateUserCode() {
		return createUserCode;
	}
	/**
	 * @param createUserCode the createUserCode to set
	 */
	public void setCreateUserCode(String createUserCode) {
		this.createUserCode = createUserCode;
	}
	/**
	 * @return the createUserName
	 */
	public String getCreateUserName() {
		return createUserName;
	}
	/**
	 * @param createUserName the createUserName to set
	 */
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	public String getResultDesc() {
		return resultDesc;
	}
	public void setResultDesc(String resultDesc) {
		this.resultDesc = resultDesc;
	}
	/**
	 * @return the createDate
	 */
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	public Date getCreateDate() {
		return createDate;
	}
	/**
	 * @param createDate the createDate to set
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	@Override
	public int compareTo(ProcessAuditLog obj) {
		return this.id.compareTo(obj.getId());
	}


}