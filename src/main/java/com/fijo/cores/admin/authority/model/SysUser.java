package com.fijo.cores.admin.authority.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fijo.cores.utils.annotations.*;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.fijo.cores.model.LogicModel;
import com.fijo.cores.utils.annotations.*;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author zhangbo
 *
 * 	MySQL错误“Specified key was too long; max key length is 767 bytes”的解决办法
 *
 * 建立索引时，数据库计算key的长度是累加所有Index用到的字段的char长度后再按下面比例乘起来不能超过限定的key长度1000：
 * latin1 = 1 byte = 1 character
 * uft8 = 3 byte = 1 character
 * uft16 = 4 byte = 1 character
 * gbk = 2 byte = 1 character
 * 举例能看得更明白些，以GBK为例：
 * CREATE UNIQUE INDEX `unique_record` ON reports (`report_name`, `report_client`, `report_city`);
 * 其中report_name varchar(200), report_client varchar(200), report_city varchar(200)
 * (200 + 200 +200) * 2 = 1200 > 1000，所有就会报1071错误，只要将report_city改为varchar(100)那么索引就能成功建立。
 *
 * 另外，字符集设置为utf8mb4等同于使用uft16，即varchar长度不能超过191个
 */
@Entity
//@Table(name="sys_user", uniqueConstraints={@UniqueConstraint(columnNames={"phone", "userType", "removed"}),@UniqueConstraint(columnNames={"email", "userType", "removed"})
//,@UniqueConstraint(columnNames={"qqCode", "userType", "removed"}),@UniqueConstraint(columnNames={"weChatCode", "userType", "removed"})
//,@UniqueConstraint(columnNames={"weiboCode", "userType", "removed"}),@UniqueConstraint(columnNames={"alipayCode", "userType", "removed"})})
@Table(name="sys_user")
@ReferenceTables(joinTables={ @ReferenceTable(table="sys_user_role", value="用户与角色"),
		@ReferenceTable(table="sys_user_permission", value="用户与权限")})
@ApiModel
public class SysUser extends LogicModel<SysUser> {
	private static final long serialVersionUID=3832626162173359411L;

	@Id
	@Column(name="id")
    @SequenceGenerator(name="sys_user_seq", sequenceName="sys_user_seq")
    @GeneratedValue(strategy=GenerationType.AUTO, generator="sys_user_seq")
    @ApiModelProperty(value="主键Id")
	private Integer id;

    @ExcelVOAttribute(name = "登录标识", column = "A")
	@NotNullColumn(value="登录标识")
    @Column(nullable = false, length = 100)
    @Unique //Redis 缓存唯一标识
    @ApiModelProperty(value="登录标识")
	private String loginName;
	
	@ExcelVOAttribute(name = "用户姓名", column = "B")
	@NotNullColumn(value="用户姓名")
	@Column(name="username", length=50)
    @ApiModelProperty(value="用户姓名")
	private String username;
	
	@ExcelVOAttribute(name = "用户编号", column = "C")
	@NotNullColumn(value="用户编号")
	@Column(name="userCode", length=100, unique=true)
    @ApiModelProperty(value="用户编号")
	private String userCode;

    @ExcelVOAttribute(name = "用户唯一编码", column = "L")
    @NotNullColumn(value="用户编码")
    @Column(name="uniqueCode", length=255, unique=true)
    @ApiModelProperty(value="用户唯一编码")
    private String uniqueCode;

    @Column(nullable=true)
    @ApiModelProperty(value="密码")
	private String password; 	
	
	@JsonIgnore
	private String salt;


    @ExcelVOAttribute(name = "组织编号", column = "D")
    @Transient
    @ApiModelProperty(value="组织编号")
    private String orgCode;

    @ExcelVOAttribute(name = "显示顺序", column = "E")
    @Column(nullable = true)
    @ApiModelProperty(value="显示顺序")
    private Integer orderBy;

    @ExcelVOAttribute(name = "邮箱地址", column = "F")
    @NotNullColumn(value="邮箱地址")
    @Column(name="email", length=80)
    @ApiModelProperty(value="邮箱地址")
    private String email;

	@ExcelVOAttribute(name = "手机号码", column = "G")
	@NotNullColumn(value="手机号码")
	@Column(name="phone", length=20)
    @ApiModelProperty(value="手机号码")
	private String phone;
	
	@ExcelVOAttribute(name = "职位", column = "H")
	@Column(name="position")
    @ApiModelProperty(value="职位")
	private String position;

    @Temporal(TemporalType.DATE)
    @Column(name = "birthDate")
    @ApiModelProperty(value="生日")
    private Date birthDate;

    // 性别(1是男性，2是女性，0是未知)
    @ExcelVOAttribute(name = "性别", column = "I")
    @Column(nullable=true)
    @ApiModelProperty(value="1男2女")
    private Integer sex;

    @ExcelVOAttribute(name = "生日", column = "J")
    @Transient
    private String birthDateStr;
		
	@NotNullColumn(value="所属组织")
    @ManyToOne
	@JoinColumn(name="org_id", nullable=false)
    @ApiModelProperty(value="所属组织")
    private SysOrg sysOrg;


    @Column(name="officePhone")
    @ApiModelProperty(value="办公电话")
    private String officePhone;

	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="sys_user_role", joinColumns={ @JoinColumn(name="user_id") }, inverseJoinColumns=@JoinColumn(name="role_id"))
    @ApiModelProperty(value="关联角色")
	private List<SysRole> roleList=Lists.newLinkedList(); // 有序的关联对象集合
	
	@ManyToMany(fetch=FetchType.EAGER)
    @ApiModelProperty(value="关联权限")
	@JoinTable(name="sys_user_permission", joinColumns={ @JoinColumn(name="user_id") }, inverseJoinColumns=@JoinColumn(name="permission_id"))
	private List<SysPermission> permissionList=Lists.newLinkedList(); // 有序的关联对象集合

    @ManyToMany(fetch=FetchType.EAGER)
    @ApiModelProperty(value="关联分组")
    @JoinTable(name="sys_user_group", joinColumns={ @JoinColumn(name="user_id",referencedColumnName="loginName") }, inverseJoinColumns=@JoinColumn(name="group_id",referencedColumnName="name"))
    private List<SysGroup> groupList=Lists.newLinkedList(); // 有序的关联对象集合

	@NotNullColumn(value="QQ号")
	@Column(name="qqCode", length=40, nullable=true)
    @ApiModelProperty(value="QQ号")
	private String qqCode;
	
	@NotNullColumn(value="微信号")
	@Column(name="weChatCode", length=40, nullable=true)
    @ApiModelProperty(value="微信号")
	private String weChatCode;
	
	@NotNullColumn(value="微博号")
	@Column(name="weiboCode", length=40, nullable=true)
    @ApiModelProperty(value="微博号")
	private String weiboCode;
	
	@NotNullColumn(value="支付宝账号")
	@Column(name="alipayCode", length=40, nullable=true)
    @ApiModelProperty(value="支付宝账号")
	private String alipayCode;
	
	//用户推广来源
	@NotNullColumn(value="场景代码")
	@Column(name="sceneCode", length=20, nullable=true)
    @ApiModelProperty(value="场景代码")
	private String sceneCode;
	
	@Column(name="sceneValue", length=20, nullable=true)
    @ApiModelProperty(value="场景值")
    private String sceneValue; //场景值
	
	//用户标签
	@NotNullColumn(value="用户标签")
	@Column(length=50, nullable=true)
    @ApiModelProperty(value="用户标签")
    private String tag;
	
	@Column(name="accesstoken", length=40, nullable=true, unique=true)
    @ApiModelProperty(value="访问令牌")
    private String accesstoken;
	
	//0 会员级用户， 1 系统级用户， 2管理端用户（如俱乐部）
	@Column(name="userType", nullable=true, columnDefinition="int default 0")
    @ApiModelProperty(value="用户类型")
    private Integer userType;
	
	//用户级别(扩展)
    @ExcelVOAttribute(name = "用户级别POSITIONLEVEL", column = "K")
	@Column(name="userLevel", nullable=true)
    @ApiModelProperty(value="用户级别(")
    private Integer userLevel;
	
	
	@Column(name="mpNum", length=20, nullable=true)
    @ApiModelProperty(value="微信服务号Id")
    private String mpNum; //微信服务号
	
	@NotNullColumn(value="服务号名称")
	@Column(name="mpName", length=50, nullable=true)
    @ApiModelProperty(value="服务号名称")
    private String mpName;
	
	@Column(name="openid", length=40, nullable=true, unique=true)
    @ApiModelProperty(value="openid")
	private String openid;
	
	//mysql unique字段最多长度255 
	@Column(name="unionid", length=40, nullable=true)//不能唯一, 用户可能既关注前端公众号，又关注后端公众号unique字段最多长度255
    @ApiModelProperty(value="unionid")
	private String unionid;
	
	//是否关注，值为0时，代表此用户没有关注该公众号，拉取不到其余信息
    @ApiModelProperty(value="是否关注")
	private Integer subscribe;
	
	@NotNullColumn(value="用户昵称")
	@Column(length=50, nullable=true)
    @ApiModelProperty(value="用户昵称")
	private String nickname;
	
	// 国家
	@NotNullColumn(value="所在国家")
	@Column(length=20, nullable=true)
    @ApiModelProperty(value="所在国家")
    private String country;
	
	// 省份
	@NotNullColumn(value="所在省份")
	@Column(length=20, nullable=true)
    @ApiModelProperty(value="所在省份")
	private String province;
	
	// 城市
	@NotNullColumn(value="所在城市")
	@Column(length=20, nullable=true)
    @ApiModelProperty(value="所在城市")
	private String city;
	
	// 语言
	@NotNullColumn(value="使用语言")
	@Column(length=20, nullable=true)
    @ApiModelProperty(value="使用语言")
	private String language;
	
	// 用户头像链接
	@NotNullColumn(value="头像链接")
	@Column(length=255, nullable=true)
    @ApiModelProperty(value="头像链接")
	private String headimgurl;
	
	// 用户特权信息（JSON数组，需要应用自行转换）
	@Transient
	@JsonIgnore
	private String[] privilege;
	
	// 用户关注时间 单位秒
	@NotNullColumn(value="关注时间")
	@Column(nullable=true)
    @ApiModelProperty(value="关注时间")
	private Long subscribe_time;

	//公众号运营者对粉丝的备注，公众号运营者可在微信公众平台用户管理界面对粉丝添加备注
	@NotNullColumn(value="用户备注")
	@Column(length=250, nullable=true)
    @ApiModelProperty(value="用户备注")
	private String remark;
	
	//用户微信分组
	@NotNullColumn(value="微信分组")
	@Column(nullable=true)
    @ApiModelProperty(value="微信分组")
	private Integer groupid;
	
	@NotNullColumn(value="背景链接")
	@Column(length=255, nullable=true)
    @ApiModelProperty(value="背景链接")
	private String backgroundurl;

    @ApiModelProperty(value="个性签名")
	private String signature; //个性签名
	
	@Transient
	@JsonIgnore
	private String sessionId;

    @ApiModelProperty(value="所属组织Id")
    private Integer ownerOrgId;

    @ApiModelProperty(value="组织层级主键空格分隔")
    private String hierarchyOrgIds;

	/**
	 * Default constructor - creates a new instance with no values set.
	 */
	public SysUser() {
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id=id;
	}

	/**
	 * @return the loginName
	 */
	public String getLoginName() {
		return loginName;
	}

	/**
	 * @param loginName the loginName to set
	 */
	public void setLoginName(String loginName) {
		this.loginName=loginName;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username=username;
	}

	/**
	 * @return the password
	 */
	@JsonIgnore
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	@JsonProperty
	public void setPassword(String password) {
		this.password=password;
	}

	/**
	 * @return the salt
	 */
	public String getSalt() {
		return salt;
	}

	/**
	 * @param salt the salt to set
	 */
	public void setSalt(String salt) {
		this.salt=salt;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone=phone;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email=email;
	}


	/**
	 * @return the sysOrg
	 */
	public SysOrg getSysOrg() {
		return sysOrg;
	}

	/**
	 * @param sysOrg the sysOrg to set
	 */
	public void setSysOrg(SysOrg sysOrg) {
		this.sysOrg=sysOrg;
	}

	/**
	 * @return the roleList
	 */
	public List<SysRole> getRoleList() {
		return roleList;
	}

	/**
	 * @param roleList the roleList to set
	 */
	public void setRoleList(List<SysRole> roleList) {
		this.roleList=roleList;
	}

	/**
	 * @return the permissionList
	 */
	public List<SysPermission> getPermissionList() {
		return permissionList;
	}

	/**
	 * @param permissionList the permissionList to set
	 */
	public void setPermissionList(List<SysPermission> permissionList) {
		this.permissionList=permissionList;
	}

	/**
	 * @return the userCode
	 */
	public String getUserCode() {
		return userCode;
	}

	/**
	 * @param userCode the userCode to set
	 */
	public void setUserCode(String userCode) {
		this.userCode=userCode;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position=position;
	}

	public String getOfficePhone() {
		return officePhone;
	}

	public void setOfficePhone(String officePhone) {
		this.officePhone=officePhone;
	}

	/**
	 * @return the qqCode
	 */
	public String getQqCode() {
		return qqCode;
	}

	/**
	 * @param qqCode the qqCode to set
	 */
	public void setQqCode(String qqCode) {
		this.qqCode=qqCode;
	}

	/**
	 * @return the weiboCode
	 */
	public String getWeiboCode() {
		return weiboCode;
	}

	/**
	 * @param weiboCode the weiboCode to set
	 */
	public void setWeiboCode(String weiboCode) {
		this.weiboCode=weiboCode;
	}

	/**
	 * @return the alipayCode
	 */
	public String getAlipayCode() {
		return alipayCode;
	}

	/**
	 * @param alipayCode the alipayCode to set
	 */
	public void setAlipayCode(String alipayCode) {
		this.alipayCode=alipayCode;
	}

	/**
	 * @return the mpNum
	 */
	public String getMpNum() {
		return mpNum;
	}

	/**
	 * @param mpNum the mpNum to set
	 */
	public void setMpNum(String mpNum) {
		this.mpNum=mpNum;
	}

	/**
	 * @return the openid
	 */
	public String getOpenid() {
		return openid;
	}

	/**
	 * @param openid the openid to set
	 */
	public void setOpenid(String openid) {
		this.openid=openid;
	}

	/**
	 * @return the unionid
	 */
	public String getUnionid() {
		return unionid;
	}

	/**
	 * @param unionid the unionid to set
	 */
	public void setUnionid(String unionid) {
		this.unionid=unionid;
	}

	/**
	 * @return the accesstoken
	 */
	public String getAccesstoken() {
		return accesstoken;
	}

	/**
	 * @param accesstoken the accesstoken to set
	 */
	public void setAccesstoken(String accesstoken) {
		this.accesstoken=accesstoken;
	}


	/**
	 * @return the userType
	 */
	public Integer getUserType() {
		return userType;
	}

	/**
	 * @param userType the userType to set
	 */
	public void setUserType(Integer userType) {
		this.userType=userType;
	}

	/**
	 * @return the userLevel
	 */
	public Integer getUserLevel() {
		return userLevel;
	}

	/**
	 * @param userLevel the userLevel to set
	 */
	public void setUserLevel(Integer userLevel) {
		this.userLevel=userLevel;
	}

	/**
	 * @return the mpName
	 */
	public String getMpName() {
		return mpName;
	}

	/**
	 * @param mpName the mpName to set
	 */
	public void setMpName(String mpName) {
		this.mpName=mpName;
	}

	/**
	 * @return the subscribe
	 */
	public Integer getSubscribe() {
		return subscribe;
	}

	/**
	 * @param subscribe the subscribe to set
	 */
	public void setSubscribe(Integer subscribe) {
		this.subscribe=subscribe;
	}

	/**
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * @param nickname the nickname to set
	 */
	public void setNickname(String nickname) {
		this.nickname=nickname;
	}

	/**
	 * @return the sex
	 */
	public Integer getSex() {
		return sex;
	}

	/**
	 * @param sex the sex to set
	 */
	public void setSex(Integer sex) {
		this.sex=sex;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country=country;
	}

	/**
	 * @return the province
	 */
	public String getProvince() {
		return province;
	}

	/**
	 * @param province the province to set
	 */
	public void setProvince(String province) {
		this.province=province;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city=city;
	}

	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language=language;
	}

	/**
	 * @return the headimgurl
	 */
	public String getHeadimgurl() {
		return headimgurl;
	}

	/**
	 * @param headimgurl the headimgurl to set
	 */
	public void setHeadimgurl(String headimgurl) {
		this.headimgurl=headimgurl;
	}

	/**
	 * @return the privilege
	 */
	public String[] getPrivilege() {
		return privilege;
	}

	/**
	 * @param privilege the privilege to set
	 */
	public void setPrivilege(String[] privilege) {
		this.privilege=privilege;
	}

	/**
	 * @return the subscribe_time
	 */
	public Long getSubscribe_time() {
		return subscribe_time;
	}

	/**
	 * @param subscribe_time the subscribe_time to set
	 */
	public void setSubscribe_time(Long subscribe_time) {
		this.subscribe_time=subscribe_time;
	}

	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * @param remark the remark to set
	 */
	public void setRemark(String remark) {
		this.remark=remark;
	}

	/**
	 * @return the groupid
	 */
	public Integer getGroupid() {
		return groupid;
	}

	/**
	 * @param groupid the groupid to set
	 */
	public void setGroupid(Integer groupid) {
		this.groupid=groupid;
	}

	/**
	 * @return the sceneCode
	 */
	public String getSceneCode() {
		return sceneCode;
	}

	/**
	 * @param sceneCode the sceneCode to set
	 */
	public void setSceneCode(String sceneCode) {
		this.sceneCode=sceneCode;
	}

	/**
	 * @return the sceneValue
	 */
	public String getSceneValue() {
		return sceneValue;
	}

	/**
	 * @param sceneValue the sceneValue to set
	 */
	public void setSceneValue(String sceneValue) {
		this.sceneValue=sceneValue;
	}

	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * @param tag the tag to set
	 */
	public void setTag(String tag) {
		this.tag=tag;
	}

	/**
	 * @return the weChatCode
	 */
	public String getWeChatCode() {
		return weChatCode;
	}

	/**
	 * @param weChatCode the weChatCode to set
	 */
	public void setWeChatCode(String weChatCode) {
		this.weChatCode=weChatCode;
	}

	/**
	 * @return the sessionId
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * @param sessionId the sessionId to set
	 */
	public void setSessionId(String sessionId) {
		this.sessionId=sessionId;
	}

	/**
	 * @return the backgroundurl
	 */
	public String getBackgroundurl() {
		return backgroundurl;
	}

	/**
	 * @param backgroundurl the backgroundurl to set
	 */
	public void setBackgroundurl(String backgroundurl) {
		this.backgroundurl = backgroundurl;
	}

	/**
	 * @return the signature
	 */
	public String getSignature() {
		return signature;
	}

	/**
	 * @param signature the signature to set
	 */
	public void setSignature(String signature) {
		this.signature = signature;
	}

	/**
	 * @return the birthDate
	 */
	@JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
	public Date getBirthDate() {
		return birthDate;
	}

	/**
	 * @param birthDate the birthDate to set
	 */
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	/**
	 * @return the orgCode
	 */
	public String getOrgCode() {
		return orgCode;
	}

	/**
	 * @param orgCode the orgCode to set
	 */
	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	/**
	 * @return the orderBy
	 */
	public Integer getOrderBy() {
		return orderBy;
	}

	/**
	 * @param orderBy the orderBy to set
	 */
	public void setOrderBy(Integer orderBy) {
		this.orderBy = orderBy;
	}

	/**
	 * @return the birthDateStr
	 */
	public String getBirthDateStr() {
		return birthDateStr;
	}

	/**
	 * @param birthDateStr the birthDateStr to set
	 */
	public void setBirthDateStr(String birthDateStr) {
		this.birthDateStr = birthDateStr;
	}

    public Integer getOwnerOrgId() {
        return ownerOrgId;
    }

    public void setOwnerOrgId(Integer ownerOrgId) {
        this.ownerOrgId = ownerOrgId;
    }

    public String getHierarchyOrgIds() {
        return hierarchyOrgIds;
    }

    public void setHierarchyOrgIds(String hierarchyOrgIds) {
        this.hierarchyOrgIds = hierarchyOrgIds;
    }

    public List<SysGroup> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<SysGroup> groupList) {
        this.groupList = groupList;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    @Override
    public int compareTo(SysUser obj) {
        if(null == this.getOrderBy())
            return 1; //NULL 排到后面
        else if(null == obj.getOrderBy())
            return -1; //NULL 排到前面
        else
            return ComparisonChain.start()
                    .compare(this.getOrderBy(), obj.getOrderBy())
                    .compare(ToStringBuilder.reflectionToString(this), ToStringBuilder.reflectionToString(obj))
                    .result();
    }

}