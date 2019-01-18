/**
 * 
 */
package com.fijo.cores.app.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fijo.cores.model.GenericModel;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 流程环节跃迁附加条件
 * 
 * @author zhangbo
 *
 */
@Entity
@Table(name = "app_process_step_condition")
@ApiModel
public class ProcessStepCondition extends GenericModel<ProcessStepCondition>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 965846198094369857L;

	public enum operation {
		gt, ge, lt, le, eq, Null
	}
	
	@Id
	@Column(name = "conditionId")
    @SequenceGenerator(name="app_condition_seq", sequenceName="app_condition_seq")
    @GeneratedValue(strategy=GenerationType.AUTO, generator="app_condition_seq")
	@JsonIgnore
    @ApiModelProperty(value="主键Id")
	private Integer conditionId;
	
	@Column(name = "name", nullable = false, length = 20)
    @ApiModelProperty(value="条件名称")
	private String name;
	
	@Column(name = "opt", nullable = false, length = 10)
	@Enumerated(EnumType.STRING)
    @ApiModelProperty(value="条件操作符")
	private operation opt;

    @ApiModelProperty(value="条件值")
	private Object value;
	
	@ManyToOne
    @JoinColumn(name = "configurationId", nullable=false)
	@JsonIgnore
    @ApiModelProperty(value="条件配置")
	private ProcessStepConfiguration configuration;
	 
	public ProcessStepCondition() {
		super();
	}

	public ProcessStepCondition(String name, operation opt, Object value) {
		super();
		this.name = name;
		this.opt = opt;
		this.value = value;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the opt
	 */
	public operation getOpt() {
		return opt;
	}
	/**
	 * @param opt the opt to set
	 */
	public void setOpt(operation opt) {
		this.opt = opt;
	}
	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * @return the conditionId
	 */
	public Integer getConditionId() {
		return conditionId;
	}

	/**
	 * @param conditionId the conditionId to set
	 */
	public void setConditionId(Integer conditionId) {
		this.conditionId = conditionId;
	}

	public ProcessStepConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(ProcessStepConfiguration configuration) {
		this.configuration = configuration;
	}
	
	
}
