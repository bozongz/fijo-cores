package com.fijo.cores.admin.authority.mapper;

import java.util.List;

import com.fijo.cores.mapper.ISystemMapper;
import org.apache.ibatis.annotations.Param;

import com.fijo.cores.admin.authority.model.SysOrg;
import com.fijo.cores.mapper.ISystemMapper;

public interface SysOrgMapper extends ISystemMapper<SysOrg,Integer> {
	
	SysOrg getRoot();
	
	List<SysOrg> getExcludeRoot();	

	List<SysOrg> getByParent(@Param("parentId") Integer parentId);
	
	Integer countByParent(@Param("parentId") Integer parentId);
}