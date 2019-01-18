package com.fijo.cores.app.model;

import java.util.ArrayList;
import java.util.List;

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
 * 业务流程头
 * 
 * @author zhangbo
 *
 */
@Entity
@Table(name = "app_process_header")
@XmlRootElement
@ApiModel
public class ProcessHeader extends GenericModel<ProcessHeader> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8775430057069370476L;

	@Id
	@Column(name = "headerId")
    @SequenceGenerator(name="app_process_header_seq", sequenceName="app_process_header_seq")
    @GeneratedValue(strategy=GenerationType.AUTO, generator="app_process_header_seq")
    @ApiModelProperty(value="主键Id")
	private Integer headerId;

	@NotNullColumn(value="流程编码")
	@Column(name = "headerCode", unique=true, nullable = false, length = 50)
	@Unique
    @ApiModelProperty(value="流程编码")
	private String headerCode;

	@NotNullColumn(value="流程描述")
	@Column(name = "headerDesc", nullable = false, length = 200)
    @ApiModelProperty(value="流程描述")
	private String headerDesc;

	@NotNullColumn(value="流程类型")
	@Column(name = "typeId", nullable = false)
    @ApiModelProperty(value="流程类型Id")
    private Integer typeId;
	
	@OneToMany(mappedBy = "header")
    @ApiModelProperty(value="环节信息")
    private List<ProcessStep> steps = new ArrayList<ProcessStep>();
	
	@Column(name = "enabled", nullable = false, columnDefinition = "int default 1")
    @ApiModelProperty(value="是否可用")
    private Boolean enabled;
	
	@Column(name = "hversion", nullable = false)
    @ApiModelProperty(value="版本号")
    private Integer hversion;
	
    @Transient
    private String typeDesc; 
    
    public ProcessHeader() {
		super();
    }
    
	public ProcessHeader(Integer headerId) {
		super();
		this.headerId = headerId;
	}
	
	/**
	 * @return the headerId
	 */
	public Integer getHeaderId() {
		return headerId;
	}

	/**
	 * @param headerId the headerId to set
	 */
	public void setHeaderId(Integer headerId) {
		this.headerId = headerId;
	}

	/**
	 * @return the headerCode
	 */
	public String getHeaderCode() {
		return headerCode;
	}

	/**
	 * @param headerCode the headerCode to set
	 */
	public void setHeaderCode(String headerCode) {
		this.headerCode = headerCode;
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
	 * @return the steps
	 */
	public List<ProcessStep> getSteps() {
		return steps;
	}

	/**
	 * @param steps the steps to set
	 */
	public void setSteps(List<ProcessStep> steps) {
		this.steps = steps;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Integer getHversion() {
		return hversion;
	}

	public void setHversion(Integer hversion) {
		this.hversion = hversion;
	}
	
}