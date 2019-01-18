package com.fijo.cores.admin.authority.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import com.baidubce.util.JsonUtils;
import com.fijo.cores.model.SystemModel;
import com.fijo.cores.utils.DateUtil;
import com.fijo.cores.utils.annotations.NotNullColumn;
import com.fijo.cores.utils.annotations.ReferenceTable;
import com.fijo.cores.utils.annotations.ReferenceTables;
import com.fijo.cores.utils.annotations.Unique;
import com.fijo.cores.model.SystemModel;
import com.fijo.cores.utils.DateUtil;
import com.fijo.cores.utils.annotations.NotNullColumn;
import com.fijo.cores.utils.annotations.ReferenceTable;
import com.fijo.cores.utils.annotations.ReferenceTables;
import com.fijo.cores.utils.annotations.Unique;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@Entity
@Table(name = "sys_role")
@ReferenceTables(joinTables={ @ReferenceTable(table="sys_user_role", value="用户与角色"),
		@ReferenceTable(table="sys_role_permission", value="角色与权限")})
@ApiModel
public class SysRole extends SystemModel<SysRole> {
	private static final long serialVersionUID = 3690197650654049848L;
	@Id
	@Column(name = "id")
    @SequenceGenerator(name="sys_role_seq", sequenceName="sys_role_seq")
    @GeneratedValue(strategy=GenerationType.AUTO, generator="sys_role_seq")
    @ApiModelProperty(value="主键Id")
	private Integer id;
	
	@NotNullColumn(value="角色编码")
	@Column(nullable = false, unique = true, length = 100)
	@Unique
    @ApiModelProperty(value="角色编码")
	private String name;
	
	@Column(length = 200)
    @ApiModelProperty(value="角色名称")
	private String description;
	
	@NotNullColumn(value="角色类型")
	@Column(nullable = false, length = 50)
    @ApiModelProperty(value="角色类型")
	private String type;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "sys_role_permission", joinColumns = { @JoinColumn(name = "role_id") }, inverseJoinColumns = @JoinColumn(name = "permission_id"))
    @ApiModelProperty(value="相关权限")
	private List<SysPermission> permissionList = new ArrayList<SysPermission>(); // 有序的关联对象集合
	
	public static void main(String[] args) {
		SysRole role = new SysRole();
    	role.setName("r1");
    	role.setDescription("r1");
    	role.setType("t1");
    	role.setCreateDate(DateUtil.getCurrent());
    	System.out.println(JsonUtils.toJsonString(role));
	}

	/**
	 * Default constructor - creates a new instance with no values set.
	 */
	public SysRole() {
	}


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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the permissionList
	 */
	public List<SysPermission> getPermissionList() {
		return permissionList;
	}

	/**
	 * @param permissionList the permissionList to set
	 */
	public void setPermissionList(List<SysPermission> permissionList) {
		this.permissionList = permissionList;
	}


}