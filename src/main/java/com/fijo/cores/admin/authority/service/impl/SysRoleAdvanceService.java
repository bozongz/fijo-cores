package com.fijo.cores.admin.authority.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.fijo.cores.admin.authority.model.SysPermission;
import com.fijo.cores.admin.authority.model.SysRole;
import com.fijo.cores.service.impl.GenericAdvanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.fijo.cores.admin.authority.model.SysPermission;
import com.fijo.cores.admin.authority.model.SysRole;
import com.fijo.cores.admin.authority.service.ISysPermissionAdvanceService;
import com.fijo.cores.admin.authority.service.ISysRoleAdvanceService;
import com.fijo.cores.admin.authority.service.ISysRoleService;
import com.fijo.cores.cache.IGenericCache;
import com.fijo.cores.service.impl.GenericAdvanceService;


@Service(value="sysRoleAdvanceService")
public class SysRoleAdvanceService extends GenericAdvanceService<SysRole,Integer> implements ISysRoleAdvanceService{

	private ISysRoleService sysRoleService;
	
	@Autowired
	private ISysPermissionAdvanceService sysPermissionAdvanceService;
	
	@Autowired
	private RedisTemplate<String, Map<String, Object>> redisTemplate;
	
	private BoundHashOperations<String, Integer, Map<String, Object>> rolePermissionsTreeDataHolder = null;
	
	@Autowired
	public SysRoleAdvanceService(
			ISysRoleService sysRoleService,
			@Qualifier(value="sysRoleCache")IGenericCache<SysRole,Integer> sysRoleCache) {
		super(sysRoleService, sysRoleCache);
		this.sysRoleService = sysRoleService;
	}

	@PostConstruct
	public void init(){	
		rolePermissionsTreeDataHolder = redisTemplate.boundHashOps("rolePermissionsTreeDataHolder");
	}
	
	@Override
	public int deleteSysUserRoleByRoleId(Integer roleId) {		
		return sysRoleService.deleteSysUserRoleByRoleId(roleId);
	}

	@Override
	public int deleteSysUserRoleByUserId(Integer userId) {		
		return sysRoleService.deleteSysUserRoleByUserId(userId);
	}

	@Override
	public int createSysUserRole(Integer userId, Integer roleId) {
		return sysRoleService.createSysUserRole(userId, roleId);
	}

	@Override
	public int deleteSysRolePermissionByRoleId(Integer roleId) {
		return sysRoleService.deleteSysRolePermissionByRoleId(roleId);
	}

	@Override
	public int createSysRolePermission(Integer roleId, Integer permissionId) {
		return sysRoleService.createSysRolePermission(roleId, permissionId);
	}

	@Override
	public List<SysRole> getByUser(Integer userId) {
		return sysRoleService.getByUser(userId);
	}

	@Override
	public Map<String, Object> getPermissionsTreeData(Integer roleId) {
		Map<String, Object> data = rolePermissionsTreeDataHolder.get(roleId);
		if(data == null){
			data = getPermissionsTree(roleId);
			rolePermissionsTreeDataHolder.put(roleId, data);
		}
		return data;
	}
	
	/**
	 * 构造权限树(按角色对人员和权限授权，没有考虑人员直接对权限授权)
	 * @param id
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> getPermissionsTree(Integer roleId){
		SysPermission root = sysPermissionAdvanceService.getRoot();
		List<SysPermission> checkedSysPermissions = sysPermissionAdvanceService.getByRole(roleId);
		Map<String, Object> rootNode = Maps.newHashMap();
		rootNode.put("key", root.getId()); // 设置根结点id
		rootNode.put("title", root.getDescription()); // 设置根结点文本内容
		rootNode.put("select", true); // 设置根结点选中情况
		rootNode.put("expand", true); // 设置根结点叶子结点开闭状态
		rootNode.put("isFolder", true);
		rootNode.put("cssselect", "select"); 
	    rootNode.put("children", queryChildren(root, checkedSysPermissions)); // 递归设置根结点子节点
		return rootNode;
	}

	/**
	 * 递归层层向下查询子节点
	 * @param parent
	 * @param checkedSysPermissions
	 * @return
	 */
	private List<Map<String, Object>> queryChildren(SysPermission parent, List<SysPermission> checkedSysPermissions) {
		List<Integer> checkPermissionIds = Lists.newArrayList();
		for(SysPermission p : checkedSysPermissions){
			checkPermissionIds.add(p.getId());	
		}
		
		List<Map<String, Object>> childrenNodes = Lists.newLinkedList();
		List<SysPermission> children = sysPermissionAdvanceService.getByParent(parent.getId());
		for (SysPermission child : children) {
			Map<String, Object> childNodeMap = Maps.newHashMap();
			Boolean isChecked = false;
			if (checkPermissionIds != null && checkPermissionIds.contains(child.getId())) {
				isChecked = true;
			}
			List<Map<String, Object>> childChildren = queryChildren(child, checkedSysPermissions);
			childNodeMap.put("key", child.getId());
			childNodeMap.put("title", child.getDescription());
			if (childChildren != null && childChildren.size() > 0) {
				childNodeMap.put("children", childChildren);
			}
			childNodeMap.put("select", isChecked);
			childNodeMap.put("cssselect", isChecked?"select":""); 
			childNodeMap.put("expand", isChecked ? true:false);
			childNodeMap.put("isFolder", child.getType().equals("button") ? false:true);
			childrenNodes.add(childNodeMap);
		}
		return childrenNodes;
	}

    /**
     * 清空按条件筛选的缓存数据
     */
    public void clearCacheHolder(){
        if(rolePermissionsTreeDataHolder.keys().size() > 0) {
            rolePermissionsTreeDataHolder.delete(rolePermissionsTreeDataHolder.keys().toArray());
        }
    }
}
