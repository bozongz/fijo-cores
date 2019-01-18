package com.fijo.cores.admin.authority.model;

import java.io.Serializable;
import java.util.List;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 自定义Authentication对象，使得Subject除了携带用户的登录名外还可以携带更多信息.
 */
@ApiModel
public class ShiroUser implements Serializable {
	private static final long serialVersionUID = -1373760761780840081L;
    @ApiModelProperty(value="登陆名")
	public String loginName;
    @ApiModelProperty(value="用户名称")
	public String userName;
    @ApiModelProperty(value="用户唯一编码")
	public String uniqueCode;
    @ApiModelProperty(value="用户编码")
    public String userCode;
    @ApiModelProperty(value="用户Id")
	public Integer userId;
    @ApiModelProperty(value="组织Id")
	public Integer orgId;
    @ApiModelProperty(value="组织名称")
	public String orgName;
    @ApiModelProperty(value="头像")
	public String headUrl;
    @ApiModelProperty(value="访问令牌")
	public String accesstoken;
    @ApiModelProperty(value="微信openid")
	public String openid;
    @ApiModelProperty(value="微信unionid")
	public String unionid;
    @ApiModelProperty(value="手机号码")
	public String phone;
    @ApiModelProperty(value="所属组织Id")
    private Integer ownerOrgId;
    @ApiModelProperty(value="相关角色")
	public List<Integer> roleIds;

	public ShiroUser(String loginName, String userName, String uniqueCode, String userCode,
			Integer userId, Integer orgId, String orgName,
			List<Integer> roleIds, String headUrl, String accesstoken,
			String openid, String unionid, String phone, Integer ownerOrgId) {
		super();
		this.loginName = loginName;
		this.userName = userName;
        this.uniqueCode = uniqueCode;
		this.userCode = userCode;
		this.userId = userId;
		this.orgId = orgId;
		this.orgName = orgName;
		this.roleIds = roleIds;
		this.headUrl = headUrl;
		this.accesstoken = accesstoken;
		this.openid = openid;
		this.unionid = unionid;
		this.phone = phone;
        this.ownerOrgId = ownerOrgId;
	}

	/**
	 * 本函数输出将作为默认的<shiro:principal/>输出.
	 */
	@Override
	public String toString() {
		return userName;
	}
	
	public String toFullString(){
		return ToStringBuilder.reflectionToString(this);
	}

	/**
	 * @return the loginName
	 */
	public String getLoginName() {
		return loginName;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

    public String getUniqueCode() {
        return uniqueCode;
    }

    /**
	 * @return the userCode
	 */
	public String getUserCode() {
		return userCode;
	}

	/**
	 * @return the userId
	 */
	public Integer getUserId() {
		return userId;
	}

	/**
	 * @return the orgId
	 */
	public Integer getOrgId() {
		return orgId;
	}

	/**
	 * @return the orgName
	 */
	public String getOrgName() {
		return orgName;
	}

	/**
	 * @return the roleIds
	 */
	public List<Integer> getRoleIds() {
		return roleIds;
	}

	public String getHeadUrl() {
		return headUrl;
	}

	/**
	 * @return the accesstoken
	 */
	public String getAccesstoken() {
		return accesstoken;
	}

	/**
	 * @return the openid
	 */
	public String getOpenid() {
		return openid;
	}

	/**
	 * @return the unionid
	 */
	public String getUnionid() {
		return unionid;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param headUrl the headUrl to set
	 */
	public void setHeadUrl(String headUrl) {
		this.headUrl = headUrl;
	}

    public Integer getOwnerOrgId() {
        return ownerOrgId;
    }

    /**
	 * 重载equals,只计算loginName;
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ShiroUser other = (ShiroUser) obj;
		if (loginName == null) {
			if (other.loginName != null) {
				return false;
			}
		} else if (!loginName.equals(other.loginName)) {
			return false;
		}
		return true;
	}
}
