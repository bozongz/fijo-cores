package com.fijo.cores.admin.authority.service;

import com.fijo.cores.service.IGenericService;
import com.fijo.cores.admin.authority.model.SysGroup;
import com.fijo.cores.service.IGenericService;

import java.util.List;


public interface ISysGroupService extends IGenericService<SysGroup, Integer> {

    int deleteSysUserGroupByGroupId(Integer groupId);

    int deleteSysUserGroupByUserId(Integer userId);

    int createSysUserGroup(Integer userId, Integer groupId);

    List<SysGroup> getByUser(Integer userId);

    List<String> getGroupUser(Integer groupid);
}
