package com.fijo.cores.admin.authority.model;

import com.fijo.cores.utils.annotations.ReferenceTable;
import com.fijo.cores.utils.annotations.ReferenceTables;
import com.fijo.cores.utils.annotations.Unique;
import com.fijo.cores.model.GenericModel;
import com.fijo.cores.utils.annotations.ReferenceTable;
import com.fijo.cores.utils.annotations.ReferenceTables;
import com.fijo.cores.utils.annotations.Unique;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Entity
@Table(name = "sys_group")
@ReferenceTables(joinTables = {@ReferenceTable(table = "sys_user_grop", value = "用户与组")})
@ApiModel
public class SysGroup extends GenericModel<SysGroup> {

    private static final long serialVersionUID = -425891999687281986L;

    @Id
    @Column(name = "id")
    @SequenceGenerator(name="sys_group_seq", sequenceName="sys_group_seq")
    @GeneratedValue(strategy=GenerationType.AUTO, generator="sys_group_seq")
    @ApiModelProperty(value="主键Id")
    private Integer id;

    @Column(nullable = false, length = 100)
    @Unique
    @ApiModelProperty(value="唯一组名")
    private String name;

    @Column(name = "type", columnDefinition = "VARCHAR(200) default 'assignment'", nullable = false, length = 50)
    @ApiModelProperty(value="组类型")
    private String type;

    @Column(name = "description", length = 500)
    @ApiModelProperty(value="组织描述")
    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}