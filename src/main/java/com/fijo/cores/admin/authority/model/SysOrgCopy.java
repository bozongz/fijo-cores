/*
 * 版权所有 © 北京晟壁科技有限公司 2008-2027。保留一切权利!
 */
package com.fijo.cores.admin.authority.model;

import com.fijo.cores.model.SystemModel;
import com.fijo.cores.utils.annotations.ExcelVOAttribute;
import com.fijo.cores.utils.annotations.NotNullColumn;
import com.fijo.cores.utils.annotations.Unique;
import com.fijo.cores.model.SystemModel;
import com.fijo.cores.utils.annotations.ExcelVOAttribute;
import com.fijo.cores.utils.annotations.NotNullColumn;
import com.fijo.cores.utils.annotations.Unique;
import com.wordnik.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * 用途：
 * 作者: zhangbo
 * 时间: 2018/4/28  21:27
 */
@Entity
@Table(name = "sys_org_copy")
public class SysOrgCopy extends SystemModel<SysOrgCopy> {

    private static final long serialVersionUID = -1447427183198771078L;

    @Id
    @Column(name = "id")
    @SequenceGenerator(name="sys_org_copy_seq", sequenceName="sys_org_copy_seq")
    @GeneratedValue(strategy=GenerationType.AUTO, generator="sys_org_copy_seq")
    @ApiModelProperty(value="主键Id")
    private Integer id;

    @ExcelVOAttribute(name = "组织编码", column = "A")
    @Column(name = "orgCode", length = 20)
    private String orgCode;  //非常重要，目前TIM导出的20位组织编码有重复，因此该字段不能在SysOrg中作为orgCode的唯一键，将该字段放到shortName中保存

    @ExcelVOAttribute(name = "TIM组织ID", column = "B")
    @NotNullColumn(value="TIM组织ID")
    @Column(name = "timOrgId", length = 20, unique = true)
    @Unique
    private String timOrgId;

    @ExcelVOAttribute(name = "TIM父组织ID", column = "C")
    @NotNullColumn(value="TIM组织ID")
    @Column(name = "timOrgParentId", length = 20)
    private String timOrgParentId;

    @ExcelVOAttribute(name = "组织名称", column = "D")
    @NotNullColumn(value="组织名称")
    @Column(name = "orgName", nullable = false, length = 100)
    private String orgName;

    @ExcelVOAttribute(name = "组织全路径", column = "E")
    @Column(name = "description", length = 200)
    private String description;

    @ExcelVOAttribute(name = "显示顺序", column = "F")
    @NotNullColumn(value="显示顺序")
    @Column(nullable = true)
    private Integer orderBy;

    @ExcelVOAttribute(name = "备注描述", column = "G")
    @NotNullColumn(value="备注")
    @Column(nullable = true)
    private String remark;

    public SysOrgCopy() {
        super();
    }

    public SysOrgCopy(Integer id) {
        super();
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getTimOrgId() {
        return timOrgId;
    }

    public void setTimOrgId(String timOrgId) {
        this.timOrgId = timOrgId;
    }

    public String getTimOrgParentId() {
        return timOrgParentId;
    }

    public void setTimOrgParentId(String timOrgParentId) {
        this.timOrgParentId = timOrgParentId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(Integer orderBy) {
        this.orderBy = orderBy;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
