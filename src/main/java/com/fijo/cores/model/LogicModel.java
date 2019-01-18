package com.fijo.cores.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import com.fijo.cores.utils.annotations.ProcessProperty;
import com.fijo.cores.utils.annotations.ProcessProperty;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@MappedSuperclass
@ApiModel
public abstract class LogicModel<T> extends SystemModel<T>{
    private static final long serialVersionUID = 8604271441269463749L;
	
	@Column(name = "enabled", nullable = false, columnDefinition = "int default 1")
	@ProcessProperty
    @ApiModelProperty(value="是否可用")
	protected Boolean enabled;
	
	@Column(name = "removed", nullable = false, columnDefinition = "int default 0")
	@ProcessProperty
    @ApiModelProperty(value="是否逻辑删除")
	protected Boolean removed;
	
	@Column(name = "createUserId", nullable = false)
	@ProcessProperty
    @ApiModelProperty(value="创建人Id")
	protected Integer createUserId;	
	
	@Column(name = "createUserCode", nullable = true, length = 50)
	@ProcessProperty
    @ApiModelProperty(value="创建人编号")
	protected String createUserCode;	
	
	@Column(name = "createUserName", nullable = false, length = 50)
	@ProcessProperty
    @ApiModelProperty(value="创建人")
	protected String createUserName; 

	@Column(name = "updateUserId")
	@ProcessProperty
    @ApiModelProperty(value="更新人Id")
	protected Integer updateUserId;	
	
	@Column(name = "updateUserCode", length = 50)
	@ProcessProperty
    @ApiModelProperty(value="更新人编号")
	protected String updateUserCode;	
	
	@Column(name = "updateUserName", length = 50)
	@ProcessProperty
    @ApiModelProperty(value="更新人")
	protected String updateUserName; 

	/**
	 * @return the enabled
	 */
	public Boolean getEnabled() {
		return enabled;
	}
	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	/**
	 * @return the removed
	 */
	public Boolean getRemoved() {
		return removed;
	}
	/**
	 * @param removed the removed to set
	 */
	public void setRemoved(Boolean removed) {
		this.removed = removed;
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

	/**
	 * @return the updateUserCode
	 */
	public String getUpdateUserCode() {
		return updateUserCode;
	}
	/**
	 * @param updateUserCode the updateUserCode to set
	 */
	public void setUpdateUserCode(String updateUserCode) {
		this.updateUserCode = updateUserCode;
	}
	/**
	 * @return the updateUserName
	 */
	public String getUpdateUserName() {
		return updateUserName;
	}
	/**
	 * @param updateUserName the updateUserName to set
	 */
	public void setUpdateUserName(String updateUserName) {
		this.updateUserName = updateUserName;
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
	 * @return the updateUserId
	 */
	public Integer getUpdateUserId() {
		return updateUserId;
	}
	/**
	 * @param updateUserId the updateUserId to set
	 */
	public void setUpdateUserId(Integer updateUserId) {
		this.updateUserId = updateUserId;
	}

	/**
	 * 必须确保enabled和removed有值
	 * @return
	 */
	public boolean validate(){
		return this!=null && enabled!=null && enabled && removed!=null && !removed;
	}
}
