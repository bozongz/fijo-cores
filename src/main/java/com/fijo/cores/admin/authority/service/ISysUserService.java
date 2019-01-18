package com.fijo.cores.admin.authority.service;
import java.util.List;

import com.fijo.cores.admin.authority.model.SysUser;
import com.fijo.cores.service.ILogicService;
import com.fijo.cores.admin.authority.model.SysUser;
import com.fijo.cores.service.ILogicService;

public interface ISysUserService extends ILogicService<SysUser,Integer> {

    //	String HASH_ALGORITHM = "SHA-1";
    int SALT_SIZE = 8;
    int HASH_INTERATIONS = 1024;

	SysUser getByUserCode(String userCode);
	
	SysUser getByUnionid(String mpNum, String unionid);
	
	SysUser getByOpenid(String openid);
	
	SysUser getByAccesstoken(String accesstoken);
	
	/**
	 * 获取所属部门系统用户
	 * @param orgId
	 * @return
	 */
	List<SysUser> getByOrg(Integer orgId, Integer userType);

	/**
	 * 获取包括传入部门在内的子部门下的用户
	 * @param orgId
	 * @return
	 */
	List<SysUser> getByIterateOrg(Integer orgId, Integer userType);
	
	/**
	 * 获取关联角色系统用户
	 * @param roleId
	 * @return
	 */
	List<SysUser> getByRole(Integer roleId);
	
	/**
	 * 主要用于微信(关注或H5页面浏览)、App（getByPhone）创建免秘用户（创建人信息为系统管理员admin）
	 * @param u
	 * @return
	 */
	int createOrUpdateViaAdmin(SysUser u);
	
	/**
	 * 创建人信息为系统管理员admin
	 * @param u
	 * @return
	 */
	int createViaAdmin(SysUser u);
	
	/**
	 * 更新人信息为系统管理员admin
	 * @param u
	 * @return
	 */
	int updateViaAdmin(SysUser u);
	
	/**
	 * 主要用于微信、App自动更新无密码用户（删除人信息为系统管理员admin）
	 * @param u
	 * @return
	 */
	int deleteUserByAdmin(SysUser u);
	
	/**
	 * 修改密码
	 * @param u
	 * @return
	 */
	int updatePassword(SysUser u);
	
	/**
	 * 修改微信用户分组和备注
	 * @param groupid
	 * @param remark
	 * @return
	 */
	int updateGroupRemark(Integer id, Integer groupid, String remark);
	
	/**
	 * 物理删除用户
	 * @param userId
	 * @return
	 */
	int forceDelete(Integer userId);
	
	List<SysUser> getUserListByOrgGroup(SysUser sysUser);

}
