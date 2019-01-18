package com.fijo.cores.admin.authority.service.impl;

import java.util.LinkedList;
import java.util.List;

import com.fijo.cores.admin.authority.mapper.SysOrgMapper;
import com.fijo.cores.admin.authority.model.SysOrg;
import com.fijo.cores.service.impl.SystemService;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.fijo.cores.admin.authority.mapper.SysOrgMapper;
import com.fijo.cores.admin.authority.model.SysOrg;
import com.fijo.cores.admin.authority.service.ISysOrgService;
import com.fijo.cores.service.impl.SystemService;

@Service(value = "sysOrgService")
public class SysOrgService extends SystemService<SysOrg,Integer> implements ISysOrgService{

	private SysOrgMapper mapper;

	@Autowired
	public SysOrgService(@Qualifier(value="sqlSessionTemplateSimple") SqlSession sqlSession) {
		super(sqlSession);
		this.mapper = sqlSession.getMapper(SysOrgMapper.class);
		super.setMapper(mapper);
	}
	
	@Override
	public SysOrg getRoot() {
		return mapper.getRoot();
	}

	@Override
	public List<SysOrg> getExcludeRoot() {
		return mapper.getExcludeRoot();
	}

	/**
	 * 通过父亲部门获取部门
	 * @param parentId
	 * @return
	 */
	@Override
	public List<SysOrg> getByParent(Integer parentId) {
		return mapper.getByParent(parentId);
	}
	
	@Override
	public Integer countByParent(@Param("parentId") Integer parentId){
		return mapper.countByParent(parentId);
	}
	
	/**
	 * 获取下属部门（不包括传入部门）
	 * @param id
	 * @return
	 */
	@Override
	public List<SysOrg> getChildrenOrg(Integer id) {
		List<SysOrg> result = Lists.newArrayList();
		SysOrg parent = mapper.getById(id);
		if(parent != null){
			List<SysOrg> children = getByParent(parent.getId());
			result.addAll(children);
			for(SysOrg child:children){
				result.addAll(getChildrenOrg(child.getId()));
			}
		}
		return result;
	}
	
	/**
	 * 获取下一级子部门（不包括传入部门，不包括子部门的子部门）
	 * @param id
	 * @return
	 */
	@Override
	public List<SysOrg> getNextChildrenOrg(Integer id) {
		List<SysOrg> result = Lists.newArrayList();
		SysOrg parent = mapper.getById(id);
		if(parent != null){
			List<SysOrg> children = getByParent(parent.getId());
			result.addAll(children);
		}
		return result;
	}

	/**
	 * 通过子部门获取父亲部门（不包括子部门）
	 * @param id
	 * @return
	 */
	public LinkedList<SysOrg> getParentByChild(Integer id){
		Integer childId = id;
		LinkedList<SysOrg> parentList = Lists.newLinkedList();
		SysOrg parent = null;
		do{
			parent = getParent(childId);
			if(parent != null){
				parentList.add(parent);
				childId = parent.getId();
			}
		}while(parent != null);
		return parentList;
	}
	
	public SysOrg getParent(Integer id){
		SysOrg child = mapper.getById(id);
		return child==null? null:child.getParent();
	}
}
