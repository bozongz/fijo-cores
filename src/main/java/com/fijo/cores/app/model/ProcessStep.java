package com.fijo.cores.app.model;

import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fijo.cores.utils.annotations.Unique;
import com.fijo.cores.utils.enums.ProcessEnum;
import com.fijo.cores.model.GenericModel;
import com.fijo.cores.utils.annotations.NotNullColumn;
import com.fijo.cores.utils.annotations.Unique;
import com.fijo.cores.utils.enums.ProcessEnum;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 业务流程明细
 * 
 * @author zhangbo
 *
 */
@Entity
@Table(name = "app_process_step", uniqueConstraints={@UniqueConstraint(columnNames={"stepCode", "sversion"})})
@ApiModel
public class ProcessStep extends GenericModel<ProcessStep> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6188237835028866729L;

	@Id
	@Column(name = "stepId")
    @SequenceGenerator(name="app_process_step_seq", sequenceName="app_process_step_seq")
    @GeneratedValue(strategy=GenerationType.AUTO, generator="app_process_step_seq")
    @ApiModelProperty(value="主键Id")
	private Integer stepId;
	
	@JsonProperty("id")
	@Column(name = "stepCode", length = 20, nullable = false)
	@Unique
    @ApiModelProperty(value="环节编码")
	private String stepCode;
	
	@NotNullColumn(value="环节类型")
	@Column(name = "stepType", nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
    @ApiModelProperty(value="环节类型")
	private ProcessEnum stepType;
	
	@NotNullColumn(value="环节分类")
	@Column(name = "stepClass", nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
    @ApiModelProperty(value="环节分类")
	private ProcessEnum stepClass;
	
	@NotNullColumn(value="环节状态")
	@Column(name = "stepDesc", nullable = false, length = 50)
    @ApiModelProperty(value="环节状态")
    private String stepDesc;
	
	@Column(name = "passId", nullable = true) //通过环节
    @ApiModelProperty(value="通过环节Id")
    private String passId;
	
	@Transient
	private String passStep; 
	
	@Column(name = "failId", nullable = true) //驳回环节
    @ApiModelProperty(value="驳回环节Id")
    private String failId;
	
	@Transient
	private String failStep; 
	
	@Column(name = "stopId", nullable = true) //终止环节
    @ApiModelProperty(value="终止环节Id")
    private String stopId;
	
	@Transient
	private String stopStep;
	
	@Column(name = "forkFromId", nullable = true) //分支来源环节
    @ApiModelProperty(value="分支来源环节Id")
    private String forkFromId;
	
	@NotNullColumn(value="流程头")
    @ManyToOne
	@JoinColumn(name = "headerId")
	@JsonIgnore
    @ApiModelProperty(value="流程头")
    private ProcessHeader header;	
    
	@NotNullColumn(value="流程类型")
	@Column(name = "typeId")
    @ApiModelProperty(value="流程类型Id")
    private Integer typeId;
    
	@Column(name = "ltop", nullable=true)
    @ApiModelProperty(value="垂直坐标")
    private String ltop;
	
	@Column(name = "lleft", nullable=true)
    @ApiModelProperty(value="水平坐标")
    private String lleft;
	
	@Column(name = "sversion", nullable = false)
	@JsonIgnore
    @ApiModelProperty(value="版本号")
    private Integer sversion;
	
	@OneToMany(fetch=FetchType.EAGER, cascade = CascadeType.ALL, mappedBy="processStep")
    @ApiModelProperty(value="环节审批信息")
    private List<ProcessAudit> audits;
	
	@OneToMany(fetch=FetchType.EAGER, cascade = CascadeType.ALL, mappedBy="processStep")
    @ApiModelProperty(value="环节配置信息")
	private List<ProcessStepConfiguration> configurations;
	
	@Transient
	private String process;
	   
	public ProcessStep() {
		super();
	}

	public ProcessStep(Integer stepId) {
		super();
		this.stepId = stepId;
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

	public String getStepCode() {
		return stepCode;
	}

	public void setStepCode(String stepCode) {
		this.stepCode = stepCode;
	}

	/**
	 * @return the stepType
	 */
	public ProcessEnum getStepType() {
		return stepType;
	}

	/**
	 * @param stepType the stepType to set
	 */
	public void setStepType(ProcessEnum stepType) {
		this.stepType = stepType;
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
	 * @return the passId
	 */
	public String getPassId() {
		return passId;
	}

	/**
	 * @param passId the passId to set
	 */
	public void setPassId(String passId) {
		this.passId = passId;
	}

	/**
	 * @return the passStep
	 */
	public String getPassStep() {
		return passStep;
	}

	/**
	 * @param passStep the passStep to set
	 */
	public void setPassStep(String passStep) {
		this.passStep = passStep;
	}
	
	/**
	 * @return the failId
	 */
	public String getFailId() {
		return failId;
	}

	/**
	 * @param failId the failId to set
	 */
	public void setFailId(String failId) {
		this.failId = failId;
	}

	/**
	 * @return the failStep
	 */
	public String getFailStep() {
		return failStep;
	}

	/**
	 * @param failStep the failStep to set
	 */
	public void setFailStep(String failStep) {
		this.failStep = failStep;
	}

	/**
	 * @return the stopId
	 */
	public String getStopId() {
		return stopId;
	}

	/**
	 * @param stopId the stopId to set
	 */
	public void setStopId(String stopId) {
		this.stopId = stopId;
	}

	/**
	 * @return the stopStep
	 */
	public String getStopStep() {
		return stopStep;
	}

	/**
	 * @param stopStep the stopStep to set
	 */
	public void setStopStep(String stopStep) {
		this.stopStep = stopStep;
	}

	/**
	 * @return the header
	 */
	public ProcessHeader getHeader() {
		return header;
	}

	/**
	 * @param header the header to set
	 */
	public void setHeader(ProcessHeader header) {
		this.header = header;
	}

	/**
	 * @return the typeId
	 */
	public Integer getTypeId() {
		return typeId;
	}

	/**
	 * @param typeId the typeId to set
	 */
	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public List<ProcessStepConfiguration> getConfigurations() {
		return configurations;
	}

	public void setConfigurations(List<ProcessStepConfiguration> configurations) {
		this.configurations = configurations;
	}
	
	public List<ProcessAudit> getAudits() {
		return audits;
	}

	public void setAudits(List<ProcessAudit> audits) {
		this.audits = audits;
	}

	public String getLtop() {
		return ltop;
	}

	public void setLtop(String ltop) {
		this.ltop = ltop;
	}

	public String getLleft() {
		return lleft;
	}

	public void setLleft(String lleft) {
		this.lleft = lleft;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public Integer getSversion() {
		return sversion;
	}

	public void setSversion(Integer sversion) {
		this.sversion = sversion;
	}

	public ProcessEnum getStepClass() {
		return stepClass;
	}

	public void setStepClass(ProcessEnum stepClass) {
		this.stepClass = stepClass;
	}

	/**
	 * @return the forkFromId
	 */
	public String getForkFromId() {
		return forkFromId;
	}

	/**
	 * @param forkFromId the forkFromId to set
	 */
	public void setForkFromId(String forkFromId) {
		this.forkFromId = forkFromId;
	}


	
}