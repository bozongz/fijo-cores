package com.fijo.cores.admin.authority.model;

import com.fijo.cores.model.SystemModel;
import com.fijo.cores.utils.annotations.*;
import com.google.common.collect.ComparisonChain;
import com.fijo.cores.model.SystemModel;
import com.fijo.cores.utils.annotations.*;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;

@Entity
@Table(name = "sys_org")
@ReferenceTables(joinTables={ @ReferenceTable(table="sys_org", value="上级组织"),
		@ReferenceTable(table="sys_user", value="组织与用户")})
@ApiModel
public class SysOrg extends SystemModel<SysOrg> {

	private static final long serialVersionUID = -1447427183198771078L;

    /**
     * 用于决策选择公司，分别表示相同组织、上级组织、所属公司、全部组织
     */
    public enum SwithOrgType{Same, Parent, Owner, all}

	@Id
	@Column(name = "id")
    @SequenceGenerator(name="sys_org_seq", sequenceName="sys_org_seq")
    @GeneratedValue(strategy=GenerationType.AUTO, generator="sys_org_seq")
    @ApiModelProperty(value="主键Id")
	private Integer id;
	
	@ExcelVOAttribute(name = "组织全路径", column = "D")
	@Column(name = "description", length = 200)
    @ApiModelProperty(value="组织全路径")
	private String description;
	
	@ExcelVOAttribute(name = "组织编码", column = "A")
	@NotNullColumn(value="组织编码")
	@Column(name = "orgCode", length = 80, unique = true)
	@Unique
    @ApiModelProperty(value="组织编码")
	private String orgCode;
	
	@ExcelVOAttribute(name = "组织名称", column = "C")
	@NotNullColumn(value="组织名称")
	@Column(name = "orgName", nullable = false, length = 100)
    @ApiModelProperty(value="组织名称")
	private String orgName;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="parent_id", nullable=true) //根节点允许为空
    @ApiModelProperty(value="父级组织")
	private SysOrg parent; 

	@ExcelVOAttribute(name = "父组织编码", column = "B")
	@Transient
	private Integer parentId;
	
	@Transient
	private String parentName;
	
	@NotNullColumn(value="组织级别")
	@Column(nullable = true)
    @ApiModelProperty(value="组织级别")
	private Integer orgLevel; 
	
	@NotNullColumn(value="组织分类")
	@Column(nullable = true)
    @ApiModelProperty(value="组织分类")
	private Integer orgCategory;

    @NotNullColumn(value="组织类型")
    @Column(nullable = true)
    @ApiModelProperty(value="组织类型")
    private Integer orgType;

    @ExcelVOAttribute(name = "显示顺序", column = "E")
	@Column(nullable = true)
    @ApiModelProperty(value="显示顺序")
	private Integer orderBy; 
	
	@ExcelVOAttribute(name = "简称", column = "G")
	@Column(nullable = true)
    @ApiModelProperty(value="简称")
	private String shortName; 
	
	@ExcelVOAttribute(name = "备注描述", column = "F")	
	@Column(nullable = true)
    @ApiModelProperty(value="备注描述")
	private String remark;

    @Column(name = "removed", nullable = false, columnDefinition = "int default 0")
    @ProcessProperty
    @ApiModelProperty(value="是否逻辑删除")
    protected Boolean removed;

	public SysOrg() {
		super();
	}

	public SysOrg(Integer id) {
		super();
		this.id = id;
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
	 * @return the orgCode
	 */
	public String getOrgCode() {
		return orgCode;
	}

	/**
	 * @param orgCode the orgCode to set
	 */
	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
	
	/**
	 * @return the parent
	 */
	public SysOrg getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(SysOrg parent) {
		this.parent = parent;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	/**
	 * @return the orgLevel
	 */
	public Integer getOrgLevel() {
		return orgLevel;
	}

	/**
	 * @param orgLevel the orgLevel to set
	 */
	public void setOrgLevel(Integer orgLevel) {
		this.orgLevel = orgLevel;
	}

	/**
	 * @return the orgType
	 */
	public Integer getOrgType() {
		return orgType;
	}

	/**
	 * @param orgType the orgType to set
	 */
	public void setOrgType(Integer orgType) {
		this.orgType = orgType;
	}

	/**
	 * @return the orderBy
	 */
	public Integer getOrderBy() {
		return orderBy;
	}

	/**
	 * @param orderBy the orderBy to set
	 */
	public void setOrderBy(Integer orderBy) {
		this.orderBy = orderBy;
	}

	/**
	 * @return the shortName
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * @param shortName the shortName to set
	 */
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * @param remark the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

    public Integer getOrgCategory() {
        return orgCategory;
    }

    public void setOrgCategory(Integer orgCategory) {
        this.orgCategory = orgCategory;
    }

    public Boolean getRemoved() {
        return removed;
    }

    public void setRemoved(Boolean removed) {
        this.removed = removed;
    }

    @Override
    public int compareTo(SysOrg obj) {
        if(null == this.getOrderBy())
            return 1; //NULL 排到后面
        else if(null == obj.getOrderBy())
            return -1; //NULL 排到前面
        else
            return ComparisonChain.start()
                    .compare(this.getOrderBy(), obj.getOrderBy())
                    .compare(ToStringBuilder.reflectionToString(this), ToStringBuilder.reflectionToString(obj))
                    .result();
    }
}