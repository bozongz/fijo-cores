/**
 * 版权所有 © 北京晟壁科技有限公司 2008-2016。保留一切权利!
 */
package com.fijo.cores.cache.cqengine.index;

import com.fijo.cores.admin.authority.model.SysOrg;
import com.fijo.cores.admin.authority.model.SysUser;
import com.fijo.cores.model.BaseObject;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.attribute.SimpleNullableAttribute;
import com.googlecode.cqengine.query.option.QueryOptions;
import com.fijo.cores.admin.authority.model.SysOrg;
import com.fijo.cores.admin.authority.model.SysUser;
import com.fijo.cores.model.BaseObject;

import java.util.List;

/**
 * 用途： 
 * 作者: zhangbo
 * 时间: 2016-06-03  17:57
 *
 * 参考：
 * http://vishnu667.github.io/using-cqengine/
 * https://github.com/npgall/cqengine
 *
 */
public class SysUserIndex extends BaseObject<SysUserIndex> {
    private Integer id;
    private String loginName;
    private Integer orgId;
    private Integer parentId;
    private Integer ownerOrgId;
    private String position;
    private SysUser sysUser;
    private List<SysOrg> hierarchyOrgs;
    private Integer orderBy;
    private String uniqueCode;

    /**
     *
     * @param id
     * @param loginName
     * @param orgId
     * @param parentId
     * @param ownerOrgId
     * @param position
     * @param sysUser
     * @param hierarchyOrgs
     * @param orderBy
     */
    public SysUserIndex(Integer id, String loginName, String uniqueCode, Integer orgId, Integer parentId, Integer ownerOrgId, String position, SysUser sysUser, List<SysOrg> hierarchyOrgs, Integer orderBy) {
        this.id = id;
        this.loginName = loginName;
        this.uniqueCode = uniqueCode;
        this.orgId = orgId;
        this.parentId = parentId;
        this.ownerOrgId = ownerOrgId;
        this.position = position;
        this.sysUser = sysUser;
        this.hierarchyOrgs = hierarchyOrgs;
        this.orderBy = orderBy;
    }

    public static final SimpleAttribute<SysUserIndex, Integer> ID = new SimpleAttribute<SysUserIndex, Integer>("id") {
        public Integer getValue(SysUserIndex user, QueryOptions queryOptions) { return user.id; }
    };

    public static final SimpleAttribute<SysUserIndex, String> LOGIN_NAME = new SimpleAttribute<SysUserIndex, String>("loginName") {
        public String getValue(SysUserIndex user, QueryOptions queryOptions) { return user.loginName; }
    };

    public static final SimpleAttribute<SysUserIndex, Integer> ORG_ID = new SimpleAttribute<SysUserIndex, Integer>("orgId") {
        public Integer getValue(SysUserIndex user, QueryOptions queryOptions) { return user.orgId; }
    };

    public static final SimpleNullableAttribute<SysUserIndex, Integer> PARENT_ID = new SimpleNullableAttribute<SysUserIndex, Integer>("parentId") {
        public Integer getValue(SysUserIndex user, QueryOptions queryOptions) { return user.parentId; }
    };

    public static final SimpleNullableAttribute<SysUserIndex, Integer> OWNER_ORG = new SimpleNullableAttribute<SysUserIndex, Integer>("ownerOrgId") {
        public Integer getValue(SysUserIndex user, QueryOptions queryOptions) { return user.ownerOrgId; }
    };

    public static final SimpleNullableAttribute<SysUserIndex, String> POSITION = new SimpleNullableAttribute<SysUserIndex, String>("position") {
        public String getValue(SysUserIndex user, QueryOptions queryOptions) { return user.position; }
    };

    public static final SimpleNullableAttribute<SysUserIndex, Integer> ORDERBY = new SimpleNullableAttribute<SysUserIndex, Integer>("orderBy") {
        public Integer getValue(SysUserIndex user, QueryOptions queryOptions) { return user.orderBy; }
    };

    public static final SimpleNullableAttribute<SysUserIndex, String> UNIQUE_CODE = new SimpleNullableAttribute<SysUserIndex, String>("uniqueCode") {
        public String getValue(SysUserIndex user, QueryOptions queryOptions) { return user.uniqueCode; }
    };

    public Integer getId() {
        return id;
    }

    public String getLoginName() {
        return loginName;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public Integer getOwnerOrgId() {
        return ownerOrgId;
    }

    public String getPosition() {
        return position;
    }

    public SysUser getSysUser() {
        return sysUser;
    }

    public List<SysOrg> getHierarchyOrgs() {
        return hierarchyOrgs;
    }

    public Integer getOrderBy() {
        return orderBy;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public Integer getParentId() {
        return parentId;
    }
}
