package com.fijo.cores.app.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fijo.cores.utils.enums.ProcessEnum;
import com.fijo.cores.utils.enums.ProcessEnum;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Json数据提交模型
 * 
 * @author zhangbo
 *
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.DEFAULT) 
@JsonIgnoreProperties(ignoreUnknown=true)
@ApiModel
public class ProcessJsonData<T extends ProcessModel<?>, PK extends Serializable> {
    @ApiModelProperty(value="审批结果")
	private ProcessEnum result;

    @ApiModelProperty(value="审批意见")
	private String opinion;

    @ApiModelProperty(value="流程实体")
	private T businessData;

	public ProcessJsonData() {
		super();
	}

	public ProcessJsonData(ProcessEnum result, T businessData) {
		super();
		this.result = result;
		this.businessData = businessData;
	}

	public ProcessJsonData(ProcessEnum result, String opinion, T businessData) {
		super();
		this.result = result;
		this.opinion = opinion;
		this.businessData = businessData;
	}

	/**
	 * @return the result
	 */
	public ProcessEnum getResult() {
		return result;
	}

	/**
	 * @param result
	 *            the result to set
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
	 * @param opinion
	 *            the opinion to set
	 */
	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	/**
	 * @return the businessData
	 */
	public T getBusinessData() {
		return businessData;
	}

	/**
	 * @param businessData
	 *            the businessData to set
	 */
	public void setBusinessData(T businessData) {
		this.businessData = businessData;
	}

}