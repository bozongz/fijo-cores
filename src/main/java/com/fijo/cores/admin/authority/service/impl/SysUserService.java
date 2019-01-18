package com.fijo.cores.admin.authority.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.fijo.cores.admin.authority.mapper.SysPermissionMapper;
import com.fijo.cores.admin.authority.mapper.SysRoleMapper;
import com.fijo.cores.admin.authority.mapper.SysUserMapper;
import com.fijo.cores.admin.authority.model.SysOrg;
import com.fijo.cores.admin.authority.model.SysUser;
import com.fijo.cores.admin.authority.service.ISysOrgService;
import com.fijo.cores.admin.authority.service.ISysUserService;
import com.fijo.cores.service.impl.LogicService;
import com.fijo.cores.utils.DateUtil;
import com.fijo.cores.utils.Digests;
import com.fijo.cores.utils.Encodes;
import com.fijo.cores.utils.configs.CoreConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.fijo.cores.admin.authority.mapper.SysPermissionMapper;
import com.fijo.cores.admin.authority.mapper.SysRoleMapper;
import com.fijo.cores.admin.authority.mapper.SysUserMapper;
import com.fijo.cores.admin.authority.model.SysOrg;
import com.fijo.cores.admin.authority.model.SysUser;
import com.fijo.cores.admin.authority.service.ISysOrgService;
import com.fijo.cores.admin.authority.service.ISysUserService;
import com.fijo.cores.service.impl.LogicService;
import com.fijo.cores.utils.DateUtil;
import com.fijo.cores.utils.Digests;
import com.fijo.cores.utils.Encodes;
import com.fijo.cores.utils.configs.CoreConfig;

@Service(value="sysUserService")
@CacheConfig(cacheNames = {"runtime:"}, cacheResolver="genericCacheResolver", keyGenerator="genericKeyGenerator")
public class SysUserService extends LogicService<SysUser,Integer> implements ISysUserService {
	private static transient final Log log = LogFactory.getLog(SysUserService.class);

	private SysUserMapper mapper;
	
	private SysRoleMapper roleMapper;
	
	private SysPermissionMapper permissionMapper;
	
	@Autowired
	private ISysOrgService sysOrgService;
	
	@Autowired
	private CoreConfig config;

	
	@Autowired
    public SysUserService(@Qualifier(value="sqlSessionTemplateSimple") SqlSession sqlSession) {
		super(sqlSession);
		this.mapper = sqlSession.getMapper(SysUserMapper.class);
		this.roleMapper = sqlSession.getMapper(SysRoleMapper.class);
		this.permissionMapper = sqlSession.getMapper(SysPermissionMapper.class);
		super.setMapper(mapper);
    }
	
	/**
	 * 设定安全的密码，生成随机的salt并经过1024次 sha-1 hash
	 */
	private void entryptPassword(SysUser user) {
		//设置盐分
		byte[] salt = Digests.generateSalt(SALT_SIZE);
		user.setSalt(Encodes.encodeHex(salt));
		//设置加密密码
		byte[] hashPassword = Digests.sha1(user.getPassword().getBytes(), salt, HASH_INTERATIONS);
		user.setPassword(Encodes.encodeHex(hashPassword));
	}
	
	@Override
	public SysUser getByUserCode(String userCode){
		return mapper.getByUserCode(userCode);
	}
	
	@Override
	public List<SysUser> getByOrg(Integer orgId, Integer userType) {
		return mapper.getByOrg(orgId, userType);
	}

	@Override
	public SysUser getByUnionid(String mpNum, String unionid) {		
		return mapper.getByUnionid(mpNum, unionid);
	}

	@Override
	public SysUser getByOpenid(String openid) {
		return mapper.getByOpenid(openid);
	}
	
	@Override
	public SysUser getByAccesstoken(String accesstoken){
		return mapper.getByAccesstoken(accesstoken);
	}
	
	@Override
	public List<SysUser> getByIterateOrg(Integer orgId, Integer userType) {
		List<SysUser> result = Lists.newArrayList();
		result.addAll(getByOrg(orgId, userType));
		Collection<SysOrg> childrenOrg = sysOrgService.getChildrenOrg(orgId);
		for(SysOrg child:childrenOrg){
			result.addAll(getByOrg(child.getId(), userType));
		}
		return result;
	}
	
	@Override
	public List<SysUser> getByRole(Integer roleId){
		return mapper.getByRole(roleId);
	}
	
	@Override
	public int create(SysUser u) {
		entryptPassword(u);
		wrapCreateInfo(u);
		return mapper.create(u);
	}

	@Override
	public int update(SysUser u) {
		wrapUpdateInfo(u);
		return mapper.update(u);
	}
	
	@Override
	public int createOrUpdateViaAdmin(SysUser u){
		SysUser admin = getByUnique(config.getValue("app.user.admin"));
		u.setCreateUserId(admin.getId());
		u.setCreateUserCode(admin.getUserCode());
		u.setCreateUserName(admin.getUsername());
		u.setCreateDate(DateUtil.getCurrent());
		u.setUpdateDate(DateUtil.getCurrent());
		int ret = mapper.createOrUpdateViaAdmin(u);
		log.debug(ret);
		return ret;
	}
	
	@Override
	public int createViaAdmin(SysUser u) {
        entryptPassword(u);
		SysUser admin = getByUnique(config.getValue("app.user.admin"));
		u.setCreateUserId(admin.getId());
		u.setCreateUserCode(admin.getUserCode());
		u.setCreateUserName(admin.getUsername());
		u.setCreateDate(DateUtil.getCurrent());
		u.setUpdateDate(DateUtil.getCurrent());
		int ret = mapper.create(u);
		log.debug(ret);
		return ret;
	}

	@Override
	public int updateViaAdmin(SysUser u){
		SysUser admin = getByUnique(config.getValue("app.user.admin"));
		u.setUpdateUserId(admin.getId());
		u.setUpdateUserCode(admin.getUserCode());
		u.setUpdateUserName(admin.getUsername());
		u.setUpdateDate(DateUtil.getCurrent());
		return mapper.update(u);
	}
	
	@Override
	public int deleteUserByAdmin(SysUser u) {
		SysUser admin = getByUnique(config.getValue("app.user.admin"));
		u.setUpdateUserId(admin.getId());
		u.setUpdateUserCode(admin.getUserCode());
		u.setUpdateUserName(admin.getUsername());
		u.setUpdateDate(DateUtil.getCurrent());
		return mapper.delete(u);
	}
	
	@Override
	public int updatePassword(SysUser u) {
		entryptPassword(u);
		return mapper.updatePassword(u);
	}

	/**
	 * 逻辑删除或物理删除(实现依赖mapper.xml)
	 * @param ids
	 * @return
	 */
	@Override
	public int delete(Integer id) {
		log.debug("@SysUserService delete object by id: " + id);
		permissionMapper.deleteSysUserPermissionByUserId(id);
		roleMapper.deleteSysUserRoleByUserId(id);
		SysUser user = getById(id);
		return super.delete(user);
	}

	/**
	 * 逻辑删除或物理删除(实现依赖mapper.xml)
	 * @param ids
	 * @return
	 */
	@Override
	public int batchDelete(Set<Integer> ids) {
		log.debug("@SysUserService batch delete objects by ids: " + ids);
		for(Integer id :ids){
			permissionMapper.deleteSysUserPermissionByUserId(id);
			roleMapper.deleteSysUserRoleByUserId(id);
		}		
		return super.batchDelete(ids);
	}
	
	@Override
	public int updateGroupRemark(Integer id, Integer groupid, String remark) {
		return mapper.updateGroupRemark(id, groupid, remark);
	}

	@Override
	public int forceDelete(Integer userId) {
		return mapper.forceDelete(userId);
	}

	@Override
	public List<SysUser> getUserListByOrgGroup(SysUser sysUser) {
		// TODO Auto-generated method stub
		return mapper.getUserListByOrgGroup(sysUser);
	}


}
