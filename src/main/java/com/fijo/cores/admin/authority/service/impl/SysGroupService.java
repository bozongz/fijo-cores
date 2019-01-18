package com.fijo.cores.admin.authority.service.impl;

import com.fijo.cores.admin.authority.mapper.SysGroupMapper;
import com.fijo.cores.admin.authority.model.SysGroup;
import com.fijo.cores.admin.authority.service.ISysGroupService;
import com.fijo.cores.service.impl.GenericMapperService;
import com.fijo.cores.admin.authority.mapper.SysGroupMapper;
import com.fijo.cores.admin.authority.model.SysGroup;
import com.fijo.cores.admin.authority.service.ISysGroupService;
import com.fijo.cores.service.impl.GenericMapperService;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;


@Service(value = "sysGroupService")
public class SysGroupService extends GenericMapperService<SysGroup, Integer> implements ISysGroupService {

    private SysGroupMapper mapper;

    @Autowired
    public SysGroupService(@Qualifier(value = "sqlSessionTemplateSimple") SqlSession sqlSession) {
        super(sqlSession);
        this.mapper = sqlSession.getMapper(SysGroupMapper.class);
        super.setMapper(mapper);
    }

    @Override
    public int createSysUserGroup(Integer userId, Integer groupId) {
        return mapper.createSysUserGroup(userId, groupId);
    }

    @Override
    public int deleteSysUserGroupByGroupId(Integer groupId) {
        return mapper.deleteSysUserGroupByGroupId(groupId);
    }

    @Override
    public int deleteSysUserGroupByUserId(Integer userId) {
        return mapper.deleteSysUserGroupByUserId(userId);
    }

    @Override
    public List<SysGroup> getByUser(Integer userId) {
        return mapper.getByUser(userId);
    }

    @Override
    public List<String> getGroupUser(Integer groupid) {
        return mapper.getGroupUser(groupid);
    }
}
