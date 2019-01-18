package com.fijo.cores.admin.authority.service.impl;

import com.fijo.cores.admin.authority.model.SysGroup;
import com.fijo.cores.admin.authority.service.ISysGroupService;
import com.fijo.cores.service.impl.GenericAdvanceService;
import com.fijo.cores.admin.authority.model.SysGroup;
import com.fijo.cores.admin.authority.service.ISysGroupAdvanceService;
import com.fijo.cores.admin.authority.service.ISysGroupService;
import com.fijo.cores.admin.authority.service.ISysPermissionAdvanceService;
import com.fijo.cores.cache.IGenericCache;
import com.fijo.cores.service.impl.GenericAdvanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service(value = "sysGroupAdvanceService")
public class SysGroupAdvanceService extends GenericAdvanceService<SysGroup, Integer> implements ISysGroupAdvanceService {

    private ISysGroupService sysGroupService;

    @Autowired
    private ISysPermissionAdvanceService sysPermissionAdvanceService;

    @Autowired
    private RedisTemplate<String, Map<String, Object>> redisTemplate;

    private BoundHashOperations<String, String, Map<String, Object>> rolePermissionsTreeDataHolder = null;

    @Autowired
    public SysGroupAdvanceService(
            ISysGroupService sysGroupService,
            @Qualifier(value = "sysGroupCache") IGenericCache<SysGroup, Integer> sysGroupCache) {
        super(sysGroupService, sysGroupCache);
        this.sysGroupService = sysGroupService;
    }

    @Override
    public int deleteSysUserGroupByGroupId(Integer roleId) {
        return sysGroupService.deleteSysUserGroupByGroupId(roleId);
    }

    @Override
    public int deleteSysUserGroupByUserId(Integer userId) {
        return sysGroupService.deleteSysUserGroupByUserId(userId);
    }

    @Override
    public int createSysUserGroup(Integer userId, Integer roleId) {
        return sysGroupService.createSysUserGroup(userId, roleId);
    }

    @Override
    public List<SysGroup> getByUser(Integer userId) {
        return sysGroupService.getByUser(userId);
    }

    @Override
    public List<String> getGroupUser(Integer groupid) {
        return sysGroupService.getGroupUser(groupid);
    }

}
