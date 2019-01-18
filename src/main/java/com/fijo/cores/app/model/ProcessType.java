package com.fijo.cores.app.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

import com.fijo.cores.utils.annotations.NotNullColumn;
import com.fijo.cores.utils.annotations.Unique;
import com.fijo.cores.model.GenericModel;
import com.fijo.cores.utils.annotations.NotNullColumn;
import com.fijo.cores.utils.annotations.Unique;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 业务流程分类
 * 
 * @author zhangbo
 *
 */
@Entity
@Table(name = "app_process_type")
@XmlRootElement
@ApiModel
public class ProcessType extends GenericModel<ProcessType> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -13798030722354856L;

	@Id
	@Column(name = "typeId")
    @SequenceGenerator(name="app_process_type_seq", sequenceName="app_process_type_seq")
    @GeneratedValue(strategy=GenerationType.AUTO, generator="app_process_type_seq")
    @ApiModelProperty(value="主键Id")
	private Integer typeId;

	@NotNullColumn(value="流程类型编码")
	@Column(name = "typeCode", unique=true, nullable = false, length = 20)
	@Unique
    @ApiModelProperty(value="流程类型编码")
	private String typeCode;

	@NotNullColumn(value="流程类型描述")
	@Column(name = "typeDesc", nullable = false, length = 200)
    @ApiModelProperty(value="流程类型描述")
	private String typeDesc;

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

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
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
	
}