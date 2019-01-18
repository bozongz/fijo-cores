package com.fijo.cores.admin.syslog.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.fijo.cores.admin.syslog.model.SysLoginInfo;
import com.fijo.cores.admin.syslog.model.SysLoginInfoExample;



public interface ISysLoginInfoService {
	 /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_logininfo
     *
     * @mbggenerated Mon Aug 18 00:48:04 CST 2014
     */
    int countByExample(SysLoginInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_logininfo
     *
     * @mbggenerated Mon Aug 18 00:48:04 CST 2014
     */
    int deleteByExample(SysLoginInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_logininfo
     *
     * @mbggenerated Mon Aug 18 00:48:04 CST 2014
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_logininfo
     *
     * @mbggenerated Mon Aug 18 00:48:04 CST 2014
     */
    int insert(SysLoginInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_logininfo
     *
     * @mbggenerated Mon Aug 18 00:48:04 CST 2014
     */
    int insertSelective(SysLoginInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_logininfo
     *
     * @mbggenerated Mon Aug 18 00:48:04 CST 2014
     */
    List<SysLoginInfo> selectByExample(SysLoginInfoExample example);

	List<SysLoginInfo> selectByExample(SysLoginInfoExample example, RowBounds rowBounds);
	
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_logininfo
     *
     * @mbggenerated Mon Aug 18 00:48:04 CST 2014
     */
    SysLoginInfo selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_logininfo
     *
     * @mbggenerated Mon Aug 18 00:48:04 CST 2014
     */
    int updateByExampleSelective(@Param("record") SysLoginInfo record, @Param("example") SysLoginInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_logininfo
     *
     * @mbggenerated Mon Aug 18 00:48:04 CST 2014
     */
    int updateByExample(@Param("record") SysLoginInfo record, @Param("example") SysLoginInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_logininfo
     *
     * @mbggenerated Mon Aug 18 00:48:04 CST 2014
     */
    int updateByPrimaryKeySelective(SysLoginInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_logininfo
     *
     * @mbggenerated Mon Aug 18 00:48:04 CST 2014
     */
    int updateByPrimaryKey(SysLoginInfo record);
}