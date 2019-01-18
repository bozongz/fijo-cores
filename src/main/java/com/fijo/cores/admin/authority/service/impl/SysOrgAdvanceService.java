package com.fijo.cores.admin.authority.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.fijo.cores.admin.authority.model.SysOrg;
import com.fijo.cores.service.impl.GenericAdvanceService;
import com.fijo.cores.utils.Constants;
import com.google.common.collect.Lists;
import com.fijo.cores.utils.Constants;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fijo.cores.admin.authority.model.SysOrg;
import com.fijo.cores.admin.authority.service.ISysOrgAdvanceService;
import com.fijo.cores.admin.authority.service.ISysOrgService;
import com.fijo.cores.cache.IGenericCache;
import com.fijo.cores.service.impl.GenericAdvanceService;


@Service(value="sysOrgAdvanceService")
public class SysOrgAdvanceService extends GenericAdvanceService<SysOrg,Integer> implements ISysOrgAdvanceService{

	private ISysOrgService sysOrgService;
	
	@Autowired
	public SysOrgAdvanceService(
			ISysOrgService sysOrgService,
			@Qualifier(value="sysOrgCache")IGenericCache<SysOrg,Integer> sysOrgCache) {
		super(sysOrgService, sysOrgCache);
		this.sysOrgService = sysOrgService;
	}
	
	@Override
	@Cacheable
	public SysOrg getRoot() {	
		return sysOrgService.getRoot();
	}

	@Override
	@Cacheable
	public List<SysOrg> getExcludeRoot() {
		return sysOrgService.getExcludeRoot();
	}

	@Override
	@Cacheable
	public List<SysOrg> getByParent(Integer parentId) {
		return sysOrgService.getByParent(parentId);
	}

	public Integer countByParent(@Param("parentId") Integer parentId){
		return sysOrgService.countByParent(parentId);
	}
	
	@Override
	@Cacheable
	public List<SysOrg> getChildrenOrg(Integer id) {
		return sysOrgService.getChildrenOrg(id);
	}
	
	@Override
	@Cacheable
	public List<SysOrg> getNextChildrenOrg(Integer id) {
		// TODO Auto-generated method stub
		return sysOrgService.getNextChildrenOrg(id);
	}

	@Override
	@Cacheable
	public LinkedList<SysOrg> getParentByChild(Integer id) {
		return sysOrgService.getParentByChild(id);
	}

	@Override
	@Cacheable
	public SysOrg getParent(Integer id) {
		return sysOrgService.getParent(id);
	}

    /**
     * 获取所属部门公司
     * @param id
     * @return
     */
    @Override
    @Cacheable
    public SysOrg getOwner(Integer id) {
        SysOrg org = loadByKey(id);
        if(org.getParent() == null)
            return org;
        else
            return getOwner(org.getParent().getId());
    }

    /**
     * 获取所属部门公司层级Id字符串
     * @param orgId
     * @return
     */
    @Override
    public String getHierarchyOrgIds(Integer orgId){
        SysOrg org = loadByKey(orgId);
        if(org == null)
            throw new NullPointerException("Can not find sysorg with orgId: " +orgId);
        if(null == org.getParent() || null == org.getParent().getId()){
            return orgId.toString();
        }else{
            return orgId + Constants.SPACE + getHierarchyOrgIds(org.getParent().getId());
        }
    }

    /**
     * 获取所属部门公司层级对象
     * @param orgId
     * @return
     */
    @Override
    public List<SysOrg> getHierarchyOrgs(Integer orgId){
        String parentIds = getHierarchyOrgIds(orgId);
        String[] parentIdArray = StringUtils.split(StringUtils.trim(parentIds), Constants.SPACE);
        List<SysOrg> parentSysOrgs = Lists.newArrayList();
        for(String parentId:parentIdArray){
            parentSysOrgs.add(loadByKey(Integer.valueOf(parentId)));
        }
        Collections.sort(parentSysOrgs);
        return parentSysOrgs;
    }


}
