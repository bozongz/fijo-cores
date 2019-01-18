package com.fijo.cores.admin.authority.service.impl;

import com.fijo.cores.admin.authority.cache.SysUserCache;
import com.fijo.cores.admin.authority.model.*;
import com.fijo.cores.admin.authority.service.*;
import com.fijo.cores.cache.cqengine.index.SysUserIndex;
import com.fijo.cores.service.impl.LogicAdvanceService;
import com.fijo.cores.shiro.AppUserSession;
import com.fijo.cores.utils.Constants;
import com.fijo.cores.utils.ObjectUtil;
import com.fijo.cores.utils.SmsUtils;
import com.fijo.cores.utils.StringUtil;
import com.fijo.cores.utils.configs.CoreConfig;
import com.fijo.cores.utils.enums.SNSLoginType;
import com.fijo.cores.web.filter.SNSAuthenticationToken;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.fijo.cores.admin.authority.cache.SysUserCache;
import com.fijo.cores.admin.authority.model.*;
import com.fijo.cores.admin.authority.service.*;
import com.fijo.cores.cache.cqengine.index.SysUserIndex;
import com.fijo.cores.cache.cqengine.search.SysUserSearch;
import com.fijo.cores.service.impl.LogicAdvanceService;
import com.fijo.cores.shiro.AppUserSession;
import com.fijo.cores.utils.Constants;
import com.fijo.cores.utils.ObjectUtil;
import com.fijo.cores.utils.SmsUtils;
import com.fijo.cores.utils.StringUtil;
import com.fijo.cores.utils.configs.CoreConfig;
import com.fijo.cores.utils.enums.SNSLoginType;
import com.fijo.cores.web.filter.SNSAuthenticationToken;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 考虑用户数量庞大，只对默认的id和loginName进行容器启动时初始化缓存，userCode、openid、unionid则使用Spring Cache在运行期间加载
 * 
 * @author zhangbo
 *
 */
@Service(value="sysUserAdvanceService")
public class SysUserAdvanceService extends LogicAdvanceService<SysUser,Integer> implements ISysUserAdvanceService {
	
	private ISysUserService sysUserService;
	
	private SysUserCache sysUserCache;

	@Autowired
	private ISysOrgAdvanceService sysOrgAdvanceService;

	@Autowired
	private ISysRoleAdvanceService sysRoleAdvanceService;
	
	@Autowired
	private ISysPermissionAdvanceService sysPermissionAdvanceService;
	
	@Autowired
	private CoreConfig config;
	
	@Autowired
	private SmsUtils smsUtil;

	@Autowired
	private AppUserSession appUserSession;

    @Autowired
    private SysUserSearch sysUserSearch;

	@Autowired
	private RedisTemplate<String, Map<String, Object>> redisTemplate;

	private BoundHashOperations<String, Integer, Map<String, Object>> usersTreeDataHolder = null;
	private BoundHashOperations<String, String, Map<String, Object>> usersRoleTreeDataHolder = null;
	private BoundHashOperations<String, Integer, Map<String, Object>> permissionsTreeDataHolder = null;
    private BoundHashOperations<String, String, List<DynamicUserTreeNode>> choseDynamicUserTreeDataHolder = null;
    private BoundHashOperations<String, String, List<DynamicUserTreeNode>> searchDynamicUserTreeDataHolder = null;

	@Autowired
	public SysUserAdvanceService(@Qualifier(value="sysUserService")ISysUserService sysUserService, SysUserCache sysUserCache) {
		super(sysUserService, sysUserCache);
		this.sysUserService = sysUserService;		
		this.sysUserCache = sysUserCache;
	}
	
    @PostConstruct
	public void init(){	
		usersTreeDataHolder = redisTemplate.boundHashOps("usersTreeDataHolder");
		usersRoleTreeDataHolder = redisTemplate.boundHashOps("usersRoleTreeDataHolder");
		permissionsTreeDataHolder = redisTemplate.boundHashOps("permissionsTreeDataHolder");
        choseDynamicUserTreeDataHolder = redisTemplate.boundHashOps("choseDynamicUserTreeDataHolder");
        searchDynamicUserTreeDataHolder = redisTemplate.boundHashOps("searchDynamicUserTreeDataHolder");
	}

	@Override
	public SysUser getByUserCode(String userCode) {
		return sysUserService.getByUserCode(userCode);
	}

	@Override
	public SysUser getByUnionid(String mpNum, String unionid) {
		return sysUserService.getByUnionid(mpNum, unionid);
	}

	@Override
	public SysUser getByOpenid(String openid) {
		return loadByCustom("openid", openid);		
	}
	
	@Override
	public SysUser getByAccesstoken(String accesstoken){
		return loadByCustom("accesstoken", accesstoken);
	}

	@Override
	public List<SysUser> getByRole(Integer roleId) {
		return sysUserService.getByRole(roleId);
	}

	@Override
	public List<SysUser> getByOrg(Integer orgId, Integer userType) {
		return sysUserService.getByOrg(orgId, userType);
	}

	@Override
	public List<SysUser> getByIterateOrg(Integer orgId, Integer userType) {
		return sysUserService.getByIterateOrg(orgId, userType);
	}

    /**
     * 一. 穷举遍历递归部门用户树
     * 递归层层向下查询子部门，直到所有部门加载完成。查询部门时，把该部门所有用户也作为叶子节点加载
     * @param userType
     * @return
     */
	@Override
	public Map<String, Object> getUsersTreeData(Integer userType) {
		log.debug("Invoke getUsersTreeData with userType:"+userType);
		Map<String, Object> data = usersTreeDataHolder.get(userType);
		if(data == null){
			data = getUsersTree(userType);
			usersTreeDataHolder.put(userType, data);
		}
		return data;
	}
	
	private Map<String, Object> getUsersTree(Integer userType) {
		SysOrg root = sysOrgAdvanceService.getRoot();
		Map<String, Object> rootNode = Maps.newHashMap();
		rootNode.put("key", null); //选择的是用户，因此组织不需要ID
		rootNode.put("title", root.getOrgName()); // 设置根结点文本内容
		rootNode.put("nodetype", "org");
		rootNode.put("select", false); // 设置根结点选中情况
		rootNode.put("expand", true); // 设置根结点叶子结点开闭状态
		rootNode.put("isFolder", true);
		rootNode.put("cssselect", ""); 
		List<Map<String, Object>> sysUserSysOrgNodes = Lists.newArrayList();
		sysUserSysOrgNodes.addAll(querySysOrgUser(root, userType)); //获取部门下属用户
		sysUserSysOrgNodes.addAll(querySysOrgChildren(root, userType));
	    rootNode.put("children", sysUserSysOrgNodes); // 递归设置根结点子节点
		return rootNode;
	}
	
	/**
	 * 递归层层 向下 查询子部门，直到所有部门加载完成。查询部门时，把该部门所有用户也作为叶子节点加载
	 * @param parent
	 * @param roleId
	 * @return
	 */
	private List<Map<String, Object>> querySysOrgChildren(SysOrg parent, Integer userType) {
		List<Map<String, Object>> childrenNodes = Lists.newArrayList();
		List<SysOrg> children = sysOrgAdvanceService.getByParent(parent.getId());
		for (SysOrg child : children) {
			Map<String, Object> childNodeMap = Maps.newHashMap();
			Collection<Map<String, Object>> childChildren = querySysOrgChildren(child, userType); //查询下级组织
			childNodeMap.put("key", null); //选择的是用户，因此组织不需要ID
			childNodeMap.put("title", child.getOrgName());
			childNodeMap.put("nodetype", "org");
			childNodeMap.put("select", false); // 组织永远不让选中
			childNodeMap.put("expand", false); //本组织节点默认关闭closed
			childNodeMap.put("isFolder", true);
			childNodeMap.put("cssselect", ""); 
			//if (childChildren != null && childChildren.size() > 0) { //一旦加上该判断，将导致如果该部门没有儿子部门，那么该部门员工无法获取
				List<Map<String, Object>> sysUserSysOrgNodes = Lists.newLinkedList();
				List<Map<String, Object>> sysOrgUsers = querySysOrgUser(child,userType);
				sysUserSysOrgNodes.addAll(sysOrgUsers);   //加入本组织下用户
				sysUserSysOrgNodes.addAll(childChildren); //加入本组织下级组织
				childNodeMap.put("children", sysUserSysOrgNodes);
				for(Map<String, Object> checkUser : sysOrgUsers){ //如果本组织下用户拥有传入的角色，那么组织节点打开open
					if((boolean) checkUser.get("select")){
						childNodeMap.put("expand", true);
						break;
					}
				}
			//}
			childrenNodes.add(childNodeMap);
		}
		return childrenNodes;
	}
	
	/**
	 * 获取部门下属用户
	 * @param sysOrg
	 * @param roleId
	 * @return
	 */
	private List<Map<String, Object>> querySysOrgUser(SysOrg sysOrg, Integer userType){
		List<Map<String, Object>> sysUserNodes = Lists.newArrayList();
		List<SysUser> children = sysUserService.getByOrg(sysOrg.getId(), userType);
		for (SysUser child : children) {
			Map<String, Object> childNodeMap = Maps.newHashMap();
			childNodeMap.put("key", child.getId());
			childNodeMap.put("title", child.getUsername());
			childNodeMap.put("nodetype", "user");
			childNodeMap.put("expand", true); //用户节点默认打开open
			childNodeMap.put("select", false);
			childNodeMap.put("isFolder", false);
			childNodeMap.put("cssselect", ""); 
			sysUserNodes.add(childNodeMap);
		}
		return sysUserNodes;
	}

    /**
     * 二. 穷举遍历递归部门用户树[需要关联角色]
     *  递归层层向下查询子部门，直到所有部门加载完成。查询部门时，把该部门所有用户也作为叶子节点加载，并显示用户角色
     * @param roleId
     * @param userType
     * @return
     */
	@Override
	public Map<String, Object> getUsersRoleTreeData(Integer roleId, Integer userType) {
		log.debug("Invoke getUsersRoleTreeData with userType:"+userType+" roleId:"+roleId);
        Map<String, Object> data = usersRoleTreeDataHolder.get(roleId+Constants.UNDERLINE+userType);
		if(data == null){
			data = getUsersRoleTree(roleId,userType);
			usersRoleTreeDataHolder.put(roleId+Constants.UNDERLINE+userType, data);
		}
		return data;
	}
	
	private Map<String, Object> getUsersRoleTree(Integer roleId, Integer userType) {
		SysOrg root = sysOrgAdvanceService.getRoot();
		Map<String, Object> rootNode = Maps.newHashMap();
		rootNode.put("key", null); //选择的是用户，因此组织不需要ID
		rootNode.put("title", root.getOrgName()); // 设置根结点文本内容
		rootNode.put("nodetype", "org");
		rootNode.put("select", false); // 设置根结点选中情况
		rootNode.put("expand", true); // 设置根结点叶子结点开闭状态
		rootNode.put("isFolder", true);
		rootNode.put("cssselect", ""); 
		List<Map<String, Object>> sysUserSysOrgNodes = Lists.newArrayList();
		sysUserSysOrgNodes.addAll(querySysOrgUserByRole(root, roleId, userType)); //获取部门下属用户
		sysUserSysOrgNodes.addAll(querySysOrgChildren(root,  roleId, userType));
	    rootNode.put("children", sysUserSysOrgNodes); // 递归设置根结点子节点
		return rootNode;
	}
	
	/**
	 * 递归层层向下查询子部门，直到所有部门加载完成。查询部门时，把该部门所有用户也作为叶子节点加载
	 * @param parent
	 * @param roleId
	 * @return
	 */
	private List<Map<String, Object>> querySysOrgChildren(SysOrg parent, Integer roleId, Integer userType) {
		List<Map<String, Object>> childrenNodes = Lists.newArrayList();
		List<SysOrg> children = sysOrgAdvanceService.getByParent(parent.getId());
		for (SysOrg child : children) {
			Map<String, Object> childNodeMap = Maps.newHashMap();
			Collection<Map<String, Object>> childChildren = querySysOrgChildren(child, roleId, userType); //查询下级组织
			childNodeMap.put("key", null); //选择的是用户，因此组织不需要ID
			childNodeMap.put("title", child.getOrgName());
			childNodeMap.put("nodetype", "org");
			childNodeMap.put("select", false); // 组织永远不让选中
			childNodeMap.put("expand", false); //本组织节点默认关闭closed
			childNodeMap.put("isFolder", true);
			childNodeMap.put("cssselect", ""); 
			//if (childChildren != null && childChildren.size() > 0) { //一旦加上该判断，将导致如果该部门没有儿子部门，那么该部门员工无法获取
				List<Map<String, Object>> sysUserSysOrgNodes = Lists.newArrayList();
				List<Map<String, Object>> sysOrgUsers = querySysOrgUserByRole(child,roleId,userType);
				sysUserSysOrgNodes.addAll(sysOrgUsers);   //加入本组织下用户
				sysUserSysOrgNodes.addAll(childChildren); //加入本组织下级组织
				childNodeMap.put("children", sysUserSysOrgNodes);
				for(Map<String, Object> checkUser : sysOrgUsers){ //如果本组织下用户拥有传入的角色，那么组织节点打开open
					if((boolean) checkUser.get("select")){
						childNodeMap.put("expand", true);
						break;
					}
				}
			//}
			childrenNodes.add(childNodeMap);
		}
		return childrenNodes;
	}

	/**
	 * 获取部门下属用户(显示该用户是否拥有制定角色)
	 * @param sysOrg
	 * @param roleId
	 * @return
	 */
	private List<Map<String, Object>> querySysOrgUserByRole(SysOrg sysOrg, Integer roleId, Integer userType){
		List<Integer> checkUserIds = Lists.newArrayList();
		List<SysUser> roleUsers = sysUserService.getByRole(roleId);
		for(SysUser u : roleUsers){
			checkUserIds.add(u.getId());
		}
		List<Map<String, Object>> sysUserNodes = Lists.newArrayList();
		List<SysUser> children = sysUserService.getByOrg(sysOrg.getId(), userType);
		for (SysUser child : children) {
			Map<String, Object> childNodeMap = Maps.newHashMap();
			childNodeMap.put("key", child.getId());
			childNodeMap.put("title", child.getUsername());
			childNodeMap.put("nodetype", "user");
			childNodeMap.put("expand", true); //用户节点默认打开open
			childNodeMap.put("select", checkUserIds.contains(child.getId()));
			childNodeMap.put("isFolder", false);
			childNodeMap.put("cssselect", checkUserIds.contains(child.getId())?"select":""); 
			sysUserNodes.add(childNodeMap);
		}
		return sysUserNodes;
	}

    /**
     * 三. 穷举遍历用户权限树，用于对用户直接授权
     * @param userId
     * @return
     */
	@Override
	public Map<String, Object> getUserPermissionsTreeData(Integer userId) {
		log.debug("Invoke getUserPermissionsTreeData with userId:"+userId);
		Map<String, Object> data = permissionsTreeDataHolder.get(userId);
		if(data == null){
			data = getPermissionsTree(userId);
			permissionsTreeDataHolder.put(userId, data);
		}
		return data;
	}
	
	/**
	 * 递归层层向下查询用户权限
	 * @param userId
	 * @return
	 */
	private Map<String, Object> getPermissionsTree(Integer userId) {		
		SysPermission root = sysPermissionAdvanceService.getRoot();
		List<SysPermission> checkedSysPermissions = sysPermissionAdvanceService.getSysUserPermission(userId);
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
     * 四. 根据所选组织，动态自定义构建组织成员及下级组织的树形菜单（含首节点）
     * @param orgId
     * @param userType
     * @return
     */
    @Override
    public List<DynamicUserTreeNode> getFirstDynamicUserTree(Integer orgId, Integer userType){
    	log.debug("Invoke getFirstDynamicUserTree with orgId:"+orgId+" userType:"+userType);
        List<DynamicUserTreeNode> resultList = Lists.newArrayList();
        SysOrg root = sysOrgAdvanceService.loadByKey(orgId);
        DynamicUserTreeNode rootNode = new DynamicUserTreeNode();
        rootNode.setType("org");
        rootNode.setChild(true);
        rootNode.setId(root.getId());
        rootNode.setPid(root.getParent()==null?null:root.getParent().getId());
        rootNode.setTitle(root.getOrgName());
        //查询组织相对与跟组织所在的层级，跟组织为0
        SysOrg sysOrg = sysOrgAdvanceService.loadByKey(root.getId());
    	int i =0;
    	i = topTraverse(i,sysOrg);
    	rootNode.setLevel(i);
    	
        resultList.add(rootNode);
        return loadUserAndOrg(resultList, orgId, userType);
    }

    /**
     * 四. 根据所选组织，动态自定义构建组织成员及下级组织的树形菜单
     * @param orgId
     * @param userType
     * @return
     */
    @Override
    public List<DynamicUserTreeNode> getChoseDynamicUserTree(Integer orgId, Integer userType) {
    	log.debug("Invoke getChoseDynamicUserTree with orgId:"+orgId+" userType:"+userType);
        List<DynamicUserTreeNode> data = choseDynamicUserTreeDataHolder.get(orgId+Constants.UNDERLINE+userType);
        if(data == null){
            data = loadUserAndOrg(Lists.<DynamicUserTreeNode>newArrayList(), orgId, userType);
            choseDynamicUserTreeDataHolder.put(orgId+Constants.UNDERLINE+userType, data);
        }
        return data;
    }

    private List<DynamicUserTreeNode> loadUserAndOrg(List<DynamicUserTreeNode> resultList, Integer orgId, Integer userType){
    	List<SysOrg> orgList = sysOrgAdvanceService.getByParent(orgId);
    	wrapDynamicOrg(resultList, orgList);
        List<SysUser> userList = getByOrg(orgId,userType);
        wrapDynamicUser(resultList, userList);
        return resultList;
    }

    /**
     * 五. 根据组织、上级组织、所属公司、职位、用户信息，递归查找父亲组织及相应职务人员
     * @param orgId
     * @param parentId
     * @param ownerId
     * @param position
     * @param userId
     * @return
     */
    @Override
    public List<DynamicUserTreeNode> searchDynamicParentUserTree(Integer orgId, Integer parentId, Integer ownerId, String position, Integer userId, String loginName, String uniqueCode) {
        log.debug(String.format("Invoke searchDynamicUserTree with orgId: %s, parentId: %s, ownerId: %s, position: %s, userId: %s, loginName: %s, uniqueCode: %s", orgId,parentId,ownerId,position, userId, loginName,uniqueCode));
        List<DynamicUserTreeNode> data = searchDynamicUserTreeDataHolder.get(orgId+Constants.UNDERLINE+parentId+Constants.UNDERLINE+ownerId+Constants.UNDERLINE+position+Constants.UNDERLINE+userId+Constants.UNDERLINE+loginName+Constants.UNDERLINE+uniqueCode);
        if(data == null){
            if(Boolean.valueOf(config.getValue("app.enable.cqengine"))) {
                data = loadDynamicParentUserTreeByCQEngine(orgId,parentId,ownerId,position,userId,loginName,uniqueCode);
            }else{
                data = loadDynamicParentUserTreeByDatabase(orgId,parentId,ownerId,position,userId,loginName,uniqueCode);
            }
            searchDynamicUserTreeDataHolder.put(orgId+Constants.UNDERLINE+parentId+Constants.UNDERLINE+ownerId+Constants.UNDERLINE+position+Constants.UNDERLINE+userId+Constants.UNDERLINE+loginName+Constants.UNDERLINE+uniqueCode, data);
        }
        return data;
    }
    
    /**
     * 五. 根据组织、上级组织、所属公司、职位、用户信息，群组递归查找父亲组织及相应职务人员
     * @param orgId
     * @param parentId
     * @param ownerId
     * @param position
     * @param userId
     * @return
     */
    @Override
    public List<DynamicUserTreeNode> searchDynamicParentUserTree(Integer orgId, Integer parentId, Integer ownerId, String position, Integer userId, String loginName, String uniqueCode,Integer group_id) {
        log.debug(String.format("Invoke searchDynamicUserTree with orgId: %s, parentId: %s, ownerId: %s, position: %s, userId: %s, loginName: %s, uniqueCode: %s", orgId,parentId,ownerId,position, userId, loginName,uniqueCode));
        List<DynamicUserTreeNode> data = null;
        if(data == null){
            if(Boolean.valueOf(config.getValue("app.enable.cqengine"))) {
                data = loadDynamicParentUserTreeByCQEngine(orgId,parentId,ownerId,position,userId,loginName,uniqueCode,group_id);
            }else{
                data = loadDynamicParentUserTreeByDatabase(orgId,parentId,ownerId,position,userId,loginName,uniqueCode,group_id);
            }
            searchDynamicUserTreeDataHolder.put(orgId+Constants.UNDERLINE+parentId+Constants.UNDERLINE+ownerId+Constants.UNDERLINE+position+Constants.UNDERLINE+userId+Constants.UNDERLINE+loginName+Constants.UNDERLINE+uniqueCode, data);
        }
        return data;
    }

    /**
     * 5.1
     */
    private List<DynamicUserTreeNode> loadDynamicParentUserTreeByDatabase(Integer orgId, Integer parentId, Integer ownerId, String position, Integer userId, String loginName, String uniqueCode){
        List<DynamicUserTreeNode> resultList = Lists.newArrayList();
        Set<SysOrg> unduplicatedOrgSet = Sets.newHashSet();
        Collection<SysUser> userList = Lists.newArrayList();
        if(null != userId){
            SysUser sysUser = loadByKey(userId);
            if(sysUser!=null){
            	userList.add(sysUser);
            }
        }else if(StringUtils.isNotEmpty(loginName)){
            SysUser sysUser = loadByUnique(loginName);
            if(sysUser!=null){
            	userList.add(sysUser);
            }
        }else if(StringUtils.isNotEmpty(uniqueCode)){
            SysUser sysUser = loadByCustom("uniqueCode", uniqueCode);
            if(sysUser!=null){
            	userList.add(sysUser);
            }
        }else {
            SysUser params = new SysUser();
            SysOrg sysOrg = new SysOrg();
            if(null != orgId)
                sysOrg.setId(orgId);
            SysOrg parent = new SysOrg(parentId);
            sysOrg.setParent(parent);
            params.setSysOrg(sysOrg);
            params.setOwnerOrgId(ownerId);
            params.setPosition(position);
            params.setEnabled(true);
            userList = getAll(params);
        }

        for (SysUser user : userList) {
            unduplicatedOrgSet.addAll(sysOrgAdvanceService.getHierarchyOrgs(user.getSysOrg().getId()));
        }
        wrapDynamicUser(resultList, userList);
        wrapDynamicOrg(resultList, unduplicatedOrgSet);
        return resultList;
    }
    
    /**
     * 5.1 带群组
     */
    private List<DynamicUserTreeNode> loadDynamicParentUserTreeByDatabase(Integer orgId, Integer parentId, Integer ownerId, String position, Integer userId, String loginName, String uniqueCode,Integer group_id){
        List<DynamicUserTreeNode> resultList = Lists.newArrayList();
        Set<SysOrg> unduplicatedOrgSet = Sets.newHashSet();
        Collection<SysUser> userList = Lists.newArrayList();
        if(null != userId){
            SysUser sysUser = loadByKey(userId);
            userList.add(sysUser);
        }else if(StringUtils.isNotEmpty(loginName)){
            SysUser sysUser = loadByUnique(loginName);
            userList.add(sysUser);
        }else if(StringUtils.isNotEmpty(uniqueCode)){
            SysUser sysUser = loadByCustom("uniqueCode", uniqueCode);
            userList.add(sysUser);
        }else if(group_id!=null){
            SysUser params = new SysUser();
            SysOrg sysOrg = new SysOrg();
            if(null != orgId)
                sysOrg.setId(orgId);
            SysOrg parent = new SysOrg(parentId);
            sysOrg.setParent(parent);
            params.setSysOrg(sysOrg);
            params.setOwnerOrgId(ownerId);
            params.setPosition(position);
            params.setGroupid(group_id);
            params.setEnabled(true);
            userList = getUserListByOrgGroup(params);
//            userList = getAll(params);
        }else {
            SysUser params = new SysUser();
            SysOrg sysOrg = new SysOrg();
            if(null != orgId)
                sysOrg.setId(orgId);
            SysOrg parent = new SysOrg(parentId);
            sysOrg.setParent(parent);
            params.setSysOrg(sysOrg);
            params.setOwnerOrgId(ownerId);
            params.setPosition(position);
            params.setEnabled(true);
            userList = getAll(params);
        }

        for (SysUser user : userList) {
            unduplicatedOrgSet.addAll(sysOrgAdvanceService.getHierarchyOrgs(user.getSysOrg().getId()));
        }
        wrapDynamicUser(resultList, userList);
        wrapDynamicOrg(resultList, unduplicatedOrgSet);
        return resultList;
    }

    /**
     * 5.2
     */
    private List<DynamicUserTreeNode> loadDynamicParentUserTreeByCQEngine(Integer orgId, Integer parentId, Integer ownerId, String position, Integer userId, String loginName, String uniqueCode){
        List<DynamicUserTreeNode> resultList = Lists.newArrayList();
        Set<SysOrg> unduplicatedOrgSet = Sets.newHashSet();
        Collection<SysUserIndex> userList;
        if(null != userId){
            userList = sysUserSearch.searchQuery(userId);
        }else {
            userList = sysUserSearch.searchQuery(loginName, uniqueCode, orgId, parentId, ownerId, position);
        }
        for (SysUserIndex user : userList) {
            DynamicUserTreeNode userNode = new DynamicUserTreeNode();
            userNode.setType("user");
            userNode.setChild(false);
            userNode.setId(user.getSysUser().getId());
            userNode.setPid(user.getSysUser().getSysOrg().getId());
            userNode.setTitle(user.getSysUser().getUsername());
            //查询组织相对与跟组织所在的层级，跟组织为0,用户相对与本身的组织又加一层级
            SysOrg sysOrg = sysOrgAdvanceService.loadByKey(user.getSysUser().getSysOrg().getId());
            int i = 0;
            i = topTraverse(i,sysOrg);
            i = i +1;
            userNode.setLevel(i);
            
            
            unduplicatedOrgSet.addAll(user.getHierarchyOrgs());
            resultList.add(userNode);
        }
        wrapDynamicOrg(resultList, unduplicatedOrgSet);
        return resultList;
    }

    /**
     * 5.2 带群组
     */
    private List<DynamicUserTreeNode> loadDynamicParentUserTreeByCQEngine(Integer orgId, Integer parentId, Integer ownerId, String position, Integer userId, String loginName, String uniqueCode,Integer group_id){
        List<DynamicUserTreeNode> resultList = Lists.newArrayList();
        Set<SysOrg> unduplicatedOrgSet = Sets.newHashSet();
        Collection<SysUserIndex> userList;
        if(null != userId){
            userList = sysUserSearch.searchQuery(userId);
        }else {
            userList = sysUserSearch.searchQuery(loginName, uniqueCode, orgId, parentId, ownerId, position);
        }
        for (SysUserIndex user : userList) {
            DynamicUserTreeNode userNode = new DynamicUserTreeNode();
            userNode.setType("user");
            userNode.setChild(false);
            userNode.setId(user.getSysUser().getId());
            userNode.setPid(user.getSysUser().getSysOrg().getId());
            userNode.setTitle(user.getSysUser().getUsername());
            //查询组织相对与跟组织所在的层级，跟组织为0,用户相对与本身的组织又加一层级
            SysOrg sysOrg = sysOrgAdvanceService.loadByKey(user.getSysUser().getSysOrg().getId());
            int i = 0;
            i = topTraverse(i,sysOrg);
            i = i +1;
            userNode.setLevel(i);
            
            
            unduplicatedOrgSet.addAll(user.getHierarchyOrgs());
            resultList.add(userNode);
        }
        wrapDynamicOrg(resultList, unduplicatedOrgSet);
        return resultList;
    }
    
    /**
     * 六. 根据所在组织、职位查找儿子递归组织及相应职务人员
     * @param orgId
     * @param position
     * @return
     */
    @Override
    public List<DynamicUserTreeNode> searchDynamicChildUserTree(Integer orgId, String position){
        log.debug(String.format("Invoke searchDynamicUserChildTree with orgId: %s, position: %s", orgId,position));
        List<DynamicUserTreeNode> data = searchDynamicUserTreeDataHolder.get(orgId+Constants.UNDERLINE+position);
        if(data == null){
            if(Boolean.valueOf(config.getValue("app.enable.cqengine"))) {
                data = loadDynamicChildUserTreeByCQEngine(orgId,position);
            }else{
                data = loadDynamicChildUserTreeByDatabase(orgId,position);
            }
            searchDynamicUserTreeDataHolder.put(orgId+Constants.UNDERLINE+position, data);
        }
        return data;
    }

    /** 6.1 Start**/
    private List<DynamicUserTreeNode> loadDynamicChildUserTreeByDatabase(Integer orgId, String position){
        List<DynamicUserTreeNode> resultList = Lists.newArrayList();
        // 查询传入组织及父亲组织
        SysOrg root = sysOrgAdvanceService.loadByKey(orgId);
        wrapDynamicOrg(resultList, sysOrgAdvanceService.getHierarchyOrgs(root.getId()));
        // 查询当前组织的人员
        Collection<SysUser> userList = queryUserByDatabase(orgId, position);
        wrapDynamicUser(resultList, userList);
        //进入递归迭代
        List<DynamicUserTreeNode> childOrgAndUserList = queryChildOrgByDatabase(orgId, position);
        resultList.addAll(childOrgAndUserList);
        return resultList;
    }

    private List<DynamicUserTreeNode> queryChildOrgByDatabase(Integer orgId, String position) {
        List<DynamicUserTreeNode> childOrgAndUserList = Lists.newArrayList();
        List<SysOrg> children = sysOrgAdvanceService.getByParent(orgId);
        wrapDynamicOrg(childOrgAndUserList, children);
        for (SysOrg child : children) {
            Collection<SysUser> userList = queryUserByDatabase(child.getId(), position);
            wrapDynamicUser(childOrgAndUserList, userList);
            childOrgAndUserList.addAll(queryChildOrgByDatabase(child.getId(), position));
        }
        return childOrgAndUserList;
    }

    private Collection<SysUser> queryUserByDatabase(Integer orgId, String position){
        SysUser params = new SysUser();
        SysOrg sysOrg = new SysOrg();
        if(null != orgId)
            sysOrg.setId(orgId);
        params.setPosition(position);
        params.setSysOrg(sysOrg);
        params.setEnabled(true);
        return getAll(params);
    }
    /** 6.1 End **/


    /** 6.2 Start**/
    private List<DynamicUserTreeNode> loadDynamicChildUserTreeByCQEngine(Integer orgId, String position){
        List<DynamicUserTreeNode> resultList = Lists.newArrayList();
        // 查询传入组织及父亲组织
        SysOrg root = sysOrgAdvanceService.loadByKey(orgId);
        wrapDynamicOrg(resultList, sysOrgAdvanceService.getHierarchyOrgs(root.getId()));
        // 查询当前组织的人员
        List<DynamicUserTreeNode> userList = queryUserByCQEngine(orgId, position);
        resultList.addAll(userList);
        //进入递归迭代
        List<DynamicUserTreeNode> childOrgAndUserList = queryChildOrgByCQEngine(orgId, position);
        resultList.addAll(childOrgAndUserList);
        return resultList;
    }

    private List<DynamicUserTreeNode> queryChildOrgByCQEngine(Integer orgId, String position){
        List<DynamicUserTreeNode> childOrgAndUserList = Lists.newArrayList();
        List<SysOrg> children = sysOrgAdvanceService.getByParent(orgId);
        wrapDynamicOrg(childOrgAndUserList, children);
        for (SysOrg child : children) {
            List<DynamicUserTreeNode> userNodeList = queryUserByCQEngine(child.getId(), position);
            childOrgAndUserList.addAll(userNodeList);
            childOrgAndUserList.addAll(queryChildOrgByCQEngine(child.getId(), position));
        }
        return childOrgAndUserList;
    }

    private List<DynamicUserTreeNode> queryUserByCQEngine(Integer orgId, String position){
        List<DynamicUserTreeNode> userNodeList = Lists.newArrayList();
        Set<SysOrg> unduplicatedOrgSet = Sets.newHashSet();
        Collection<SysUserIndex> userList = sysUserSearch.searchQuery(null, null, orgId, null, null, position);
        for (SysUserIndex user : userList) {
            DynamicUserTreeNode userNode = new DynamicUserTreeNode();
            userNode.setType("user");
            userNode.setChild(false);
            userNode.setId(user.getSysUser().getId());
            userNode.setPid(user.getSysUser().getSysOrg().getId());
            userNode.setTitle(user.getSysUser().getUsername());
            //查询组织相对与跟组织所在的层级，跟组织为0,用户相对与本身的组织又加一层级
            SysOrg sysOrg = sysOrgAdvanceService.loadByKey(user.getSysUser().getSysOrg().getId());
            int i = 0;
            i = topTraverse(i,sysOrg);
            i = i +1;
            userNode.setLevel(i);
            
            unduplicatedOrgSet.add(user.getSysUser().getSysOrg());
            userNodeList.add(userNode);
        }
        wrapDynamicOrg(userNodeList, unduplicatedOrgSet);
        return userNodeList;
    }
    /** 6.2 End **/

    private void wrapDynamicUser(Collection<DynamicUserTreeNode> resultList, Collection<SysUser> userList){
        for(SysUser user : userList){
            DynamicUserTreeNode node = new DynamicUserTreeNode();
            node.setType("user");
            node.setChild(false);
            node.setId(user.getId());
            node.setPid(user.getSysOrg().getId());
            node.setTitle(user.getUsername());
            //查询组织相对与跟组织所在的层级，跟组织为0
            SysOrg sysOrg = sysOrgAdvanceService.loadByKey(user.getSysOrg().getId());
            int i = 0;
            i = topTraverse(i,sysOrg);
            i = i +1;
            node.setLevel(i);
            resultList.add(node);
        }
    }
    
    /**
     * 当前组织查找跟组织遍历了几次，跟组织是0级，
     * @param i
     * @param org
     * @return
     */
    private int topTraverse(int i ,SysOrg org){
    	if(org.getParentId()!=null){
    		i++;
    		SysOrg root = sysOrgAdvanceService.loadByKey(org.getParentId());
    		i = topTraverse(i,root);
    	}
    	return i;
    }

    private void wrapDynamicOrg(Collection<DynamicUserTreeNode> resultList, Collection<SysOrg> orgList){
        for(SysOrg org : orgList){
        	SysOrg sysOrg = sysOrgAdvanceService.loadByKey(org.getId());
        	int i =0;
        	i = topTraverse(i,sysOrg);
            DynamicUserTreeNode node = new DynamicUserTreeNode();
            Integer children = sysOrgAdvanceService.countByParent(org.getId());
            if(children != null && children>0)
                node.setChild(true);
            else
                node.setChild(false);
            node.setType("org");
            node.setId(org.getId());
            if (null != org.getParent())
                node.setPid(org.getParent().getId());
            node.setTitle(org.getOrgName());
            node.setLevel(i);
            resultList.add(node);
        }
    }

	@Override
	public int updatePassword(SysUser u) {
		return sysUserService.updatePassword(u);
	}	

	@Override
	public int updateGroupRemark(Integer id, Integer groupid, String remark) {
		return sysUserService.updateGroupRemark(id, groupid, remark);
	}

    private void wrapSysUser(SysUser u){
        SysOrg owner = sysOrgAdvanceService.getOwner(u.getSysOrg().getId());
        u.setOwnerOrgId(owner.getId());
        u.setHierarchyOrgIds(sysOrgAdvanceService.getHierarchyOrgIds(u.getSysOrg().getId()));
    }

    @Override
    public int create(SysUser u) {
        wrapSysUser(u);
        int ret = super.create(u);
        if(ret > 0) {
            sysUserSearch.createToIndex(u, sysOrgAdvanceService.getHierarchyOrgs(u.getSysOrg().getId()));
            clearCacheHolder();
        }
        log.debug(ret);
        return ret;
    }

    @Override
    public int batchCreate(Collection<SysUser> os) {
        for(SysUser u:os){
            wrapSysUser(u);
        }
        int ret = super.batchCreate(os);
        if(ret > 0){
            for(SysUser u:os){
                sysUserSearch.createToIndex(u, sysOrgAdvanceService.getHierarchyOrgs(u.getSysOrg().getId()));
            }
            clearCacheHolder();
        }
        log.debug(ret);
        return ret;
    }

    @Override
    public int update(SysUser o) {
        int ret = super.update(o);
        if(ret > 0){
            clearCacheHolder();
        }
        log.debug(ret);
        return ret;
    }

    @Override
    public int update(Map<String, Object> params) {
        int ret = super.update(params);
        if(ret > 0){
            clearCacheHolder();
        }
        log.debug(ret);
        return ret;
    }

    @Override
    public int batchUpdate(Collection<SysUser> os) {
        int ret = super.batchUpdate(os);
        if(ret > 0){
            clearCacheHolder();
        }
        log.debug(ret);
        return ret;
    }

    @Override
    public int delete(Integer id) {
        int ret = super.delete(id);
        if(ret > 0) {
            sysUserSearch.removeFromIndex(id);
        }
        if(ret > 0){
            clearCacheHolder();
        }
        log.debug(ret);
        return ret;
    }

    @Override
    public int delete(SysUser o) {
        Collection<SysUser> os = getAll(o);
        int ret = super.delete(o);
        if(ret > 0) {
            Collection<Integer> ids = ObjectUtil.getIdVaueList(os);
            sysUserSearch.removeAllFromIndex(ids);
        }
        if(ret > 0){
            clearCacheHolder();
        }
        log.debug(ret);
        return ret;
    }

    @Override
    public int batchDelete(Set<Integer> ids) {
        int ret = super.batchDelete(ids);
        if(ret > 0) {
            sysUserSearch.removeAllFromIndex(ids);
        }
        if(ret > 0){
            clearCacheHolder();
        }
        log.debug(ret);
        return ret;
    }

    @Override
    public int batchDelete(Collection<SysUser> os) {
        Collection<Integer> ids = ObjectUtil.getIdVaueList(os);
        int ret = super.batchDelete(os);
        if(ret > 0){
            sysUserSearch.removeAllFromIndex(ids);
        }
        if(ret > 0){
            clearCacheHolder();
        }
        log.debug(ret);
        return ret;
    }

	@Override
	public int createOrUpdateViaAdmin(SysUser u) {
        wrapSysUser(u);
		int ret = sysUserService.createOrUpdateViaAdmin(u);
        if(ret > 0) {
            saveOrUpdate(u);
            sysUserSearch.createToIndex(u, sysOrgAdvanceService.getHierarchyOrgs(u.getSysOrg().getId()));
        }
        if(ret > 0){
            clearCacheHolder();
        }
        log.debug(ret);
        return ret;
	}
	
	@Override
	public int createViaAdmin(SysUser u) {
        wrapSysUser(u);
		int ret = sysUserService.createViaAdmin(u);
        if(ret > 0) {
            saveOrUpdate(u);
            sysUserSearch.createToIndex(u, sysOrgAdvanceService.getHierarchyOrgs(u.getSysOrg().getId()));
        }
        if(ret > 0){
            clearCacheHolder();
        }
        log.debug(ret);
        return ret;
	}

	@Override
	public int updateViaAdmin(SysUser u){
		int ret = sysUserService.updateViaAdmin(u);
        if(ret > 0) {
            saveOrUpdate(u);
            sysUserSearch.createToIndex(u, sysOrgAdvanceService.getHierarchyOrgs(u.getSysOrg().getId()));
        }
        if(ret > 0){
            clearCacheHolder();
        }
        log.debug(ret);
        return ret;
	}
	
	@Override
	public int deleteUserByAdmin(SysUser u) {
		int ret = sysUserService.deleteUserByAdmin(u);
        if(ret > 0) {
            removeValue(u.getId());
        }
        if(ret > 0){
            clearCacheHolder();
        }
        log.debug(ret);
        return ret;
	}
	
	@Override
	public int forceDelete(Integer userId) {
		int ret = sysUserService.forceDelete(userId);
        if(ret > 0) {
            log.debug("forceDelete userId:" + userId);
            removeValue(userId);
        }
        if(ret > 0){
            clearCacheHolder();
        }
        log.debug(ret);
        return ret;
	}

	/**
	 * 前端用户知道openid，不知道phone
	 */
	@Override
	public Map<String, Object> updateBindFrontendUser(String phone, String expectCode) {
		Map<String, Object> map = Maps.newHashMap();	
		SysUser sysUser = null;
		boolean check = smsUtil.checkUserBindCode(phone, expectCode);
		if(check){
			int ret = 0;
			Map<String,Object> params = Maps.newHashMap();
			params.put("phone", phone);
			params.put("userType", Integer.valueOf(config.getValue("app.usertype.frontend")));
			Collection<SysUser> users = sysUserService.getAll(params);
			ShiroUser shiroUser = appUserSession.getCurrentUser();
			if(users.isEmpty()){ //用户没有用App注册过				
				SysUser weChatUser = sysUserService.getByOpenid(shiroUser.getOpenid());
				weChatUser.setPhone(phone);
				weChatUser.setRemoved(false);
				weChatUser.setEnabled(true);		
				sysUser = weChatUser;
			}else {//用户曾使用App注册过(以原App用户信息为准)
				SysUser appUser = users.iterator().next();
				log.debug(appUser);
				SysUser weChatUser = getByOpenid(shiroUser.getOpenid());
				log.debug(weChatUser);
				if(weChatUser != null){
					BeanUtils.copyProperties(weChatUser, appUser, new String[]{"id", "phone", "accesstoken", "groupid", "sysOrg"});
					ret = forceDelete(weChatUser.getId()); //物理删除微信用户，否则openid不能唯一
					log.debug(ret);
				}
				sysUser = appUser;
			}
			sysUser.setUsername(StringUtil.filterEmoji(sysUser.getUsername()));
			sysUser.setNickname(StringUtil.filterEmoji(sysUser.getNickname()));
			sysUser.setSignature(StringUtil.filterEmoji(sysUser.getSignature()));
			ret = updateViaAdmin(sysUser);
            if(ret > 0){
                clearCacheHolder();
            }
            log.debug(ret);
			SNSAuthenticationToken snsToken = new SNSAuthenticationToken(sysUser.getOpenid(), SNSLoginType.weixin);
			SecurityUtils.getSubject().login(snsToken);	
			map.put("message", "绑定成功!");
			map.put("responseid", 1);			
		}else{
			map.put("message", "短信验证错误!");
			map.put("responseid", 0);
		}
		return map;
	}

	/**
	 * 后端端用户知道phone，不知道openid(不自动写入DB)
	 */
	@Override
	public Map<String, Object> updateBindBackendUser(SysUser weChatUser, String phone, String expectCode) {
		Map<String, Object> map = Maps.newHashMap();	
		boolean check = smsUtil.checkUserBindCode(phone, expectCode);
		if(check){
			int ret = 0;
			Map<String,Object> params = Maps.newHashMap();
			params.put("phone", phone);
            params.put("userType", Integer.valueOf(config.getValue("app.usertype.backend"))); //确定绑定用户类型
			Collection<SysUser> users = sysUserService.getAll(params);
			if(users.isEmpty()){
				map.put("message", "此手机号未在系统维护!");
				map.put("responseid", 0);
			}else if(users.size() > 1){
                map.put("message", "此手机号存在多个账号!");
                map.put("responseid", 0);
            }else{
				SysUser backendUser = users.iterator().next();
				BeanUtils.copyProperties(weChatUser, backendUser, new String[]{"id", "phone", "accesstoken", "groupid", "sysOrg"});
				backendUser.setPhone(phone);
				backendUser.setRemoved(false);
				backendUser.setEnabled(true);
				backendUser.setUsername(StringUtil.filterEmoji(backendUser.getUsername()));
				backendUser.setNickname(StringUtil.filterEmoji(backendUser.getNickname()));
				backendUser.setSignature(StringUtil.filterEmoji(backendUser.getSignature()));
				ret = updateViaAdmin(backendUser);
                if(ret > 0){
                    clearCacheHolder();
                }
                log.debug(ret);
				if(ret > 0){
					SNSAuthenticationToken snsToken = new SNSAuthenticationToken(backendUser.getOpenid(), SNSLoginType.weixin);
					SecurityUtils.getSubject().login(snsToken);	
					map.put("message", "绑定成功!");
					map.put("responseid", 1);
				}else{
					map.put("message", "更新后台账号失败!");
					map.put("responseid", 0);
				}
			}
		}else{
			map.put("message", "短信验证错误!");
			map.put("responseid", 0);
		}
		return map;
	}

    /**
     * 清空按条件筛选的缓存数据
     */
    public void clearCacheHolder(){
        if(usersTreeDataHolder.keys().size() > 0 ) {
            usersTreeDataHolder.delete(usersTreeDataHolder.keys().toArray());
        }
        if(usersRoleTreeDataHolder.keys().size() > 0) {
            usersRoleTreeDataHolder.delete(usersRoleTreeDataHolder.keys().toArray());
        }
        if(permissionsTreeDataHolder.keys().size() > 0) {
            permissionsTreeDataHolder.delete(permissionsTreeDataHolder.keys().toArray());
        }
        if(choseDynamicUserTreeDataHolder.keys().size() > 0) {
            choseDynamicUserTreeDataHolder.delete(choseDynamicUserTreeDataHolder.keys().toArray());
        }
        if(searchDynamicUserTreeDataHolder.keys().size() > 0) {
            searchDynamicUserTreeDataHolder.delete(searchDynamicUserTreeDataHolder.keys().toArray());
        }
    }

	@Override
	public List<SysUser> getUserListByOrgGroup(SysUser sysUser) {
		// TODO Auto-generated method stub
		return sysUserService.getUserListByOrgGroup(sysUser);
	}




	
}
