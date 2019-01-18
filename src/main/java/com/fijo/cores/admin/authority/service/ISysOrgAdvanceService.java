package com.fijo.cores.admin.authority.service;
import com.fijo.cores.admin.authority.model.SysOrg;
import com.fijo.cores.service.IGenericAdvanceService;
import com.fijo.cores.admin.authority.model.SysOrg;
import com.fijo.cores.service.IGenericAdvanceService;

import java.util.List;

public interface ISysOrgAdvanceService extends IGenericAdvanceService<SysOrg,Integer>, ISysOrgService{

    /**
     * 获取所属部门公司
     * @param id
     * @return
     */
    SysOrg getOwner(Integer id);

    /**
     * 获取所属部门公司层级Id字符串
     * @param orgId
     * @return
     */
    String getHierarchyOrgIds(Integer orgId);

    /**
     * 获取所属部门公司层级对象
     * @param orgId
     * @return
     */
    List<SysOrg> getHierarchyOrgs(Integer orgId);
}
