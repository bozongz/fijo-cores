package com.fijo.cores.admin.authority.mapper;

import java.util.List;

import com.fijo.cores.admin.authority.model.SysUser;
import com.fijo.cores.mapper.ILogicMapper;
import org.apache.ibatis.annotations.Param;

import com.fijo.cores.admin.authority.model.SysUser;
import com.fijo.cores.mapper.ILogicMapper;

public interface SysUserMapper extends ILogicMapper<SysUser,Integer> {

	SysUser getByUserCode(@Param("userCode")String userCode);
	
	SysUser getByUnionid(@Param("mpNum")String mpNum, @Param("unionid")String unionid);
	
	SysUser getByOpenid(@Param("openid")String openid);
	
	SysUser getByAccesstoken(@Param("accesstoken")String accesstoken);
	
	List<SysUser> getByOrg(@Param("orgId")Integer orgId, @Param("userType")Integer userType);

	List<SysUser> getByRole(Integer roleId);

	int updatePassword(SysUser u);
	
	int updateGroupRemark(@Param("id")Integer id, @Param("groupid")Integer groupid, @Param("remark")String remark);
	
	int createOrUpdateViaAdmin(SysUser u);
	
	int forceDelete(Integer userId);
	
	List<SysUser> getUserListByOrgGroup(SysUser sysUser);

}
