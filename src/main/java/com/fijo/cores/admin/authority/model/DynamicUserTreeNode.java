/**
 * 
 */
package com.fijo.cores.admin.authority.model;

import com.fijo.cores.model.BaseObject;
import com.fijo.cores.model.BaseObject;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 根据所选组织，动态自定义构建组织成员及下级组织的树形菜单
 *
 * @author Li
 *
 */
@ApiModel
public class DynamicUserTreeNode extends BaseObject<DynamicUserTreeNode> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3484465213103389831L;
	/**
	 * 
	 */
    @ApiModelProperty(value="组织或用户Id")
	private Integer id;
    @ApiModelProperty(value="组织或用户名称")
	private String title;
    @ApiModelProperty(value="组织父级Id")
	private Integer pid;
    @ApiModelProperty(value="用户级别")
	private Integer level;
    @ApiModelProperty(value="是否有儿子")
	private boolean child;
    @ApiModelProperty(value="标识组织或类型")
	private String type;
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
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
	 * @return the pid
	 */
	public Integer getPid() {
		return pid;
	}
	/**
	 * @param pid the pid to set
	 */
	public void setPid(Integer pid) {
		this.pid = pid;
	}
	/**
	 * @return the child
	 */
	public boolean isChild() {
		return child;
	}
	/**
	 * @param child the child to set
	 */
	public void setChild(boolean child) {
		this.child = child;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	
	
	
}
