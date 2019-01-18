package com.fijo.cores.admin.syslog.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.fijo.cores.admin.syslog.model.SysOperateInfo;
import com.fijo.cores.admin.syslog.model.SysOperateInfoExample;

public interface ISysOperateInfoService {
	 /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_operate_info
     *
     * @mbggenerated Sun Aug 17 21:36:32 CST 2014
     */
    int countByExample(SysOperateInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_operate_info
     *
     * @mbggenerated Sun Aug 17 21:36:32 CST 2014
     */
    int deleteByExample(SysOperateInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_operate_info
     *
     * @mbggenerated Sun Aug 17 21:36:32 CST 2014
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_operate_info
     *
     * @mbggenerated Sun Aug 17 21:36:32 CST 2014
     */
    int insert(SysOperateInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_operate_info
     *
     * @mbggenerated Sun Aug 17 21:36:32 CST 2014
     */
    int insertSelective(SysOperateInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_operate_info
     *
     * @mbggenerated Sun Aug 17 21:36:32 CST 2014
     */
    List<SysOperateInfo> selectByExample(SysOperateInfoExample example);

    List<SysOperateInfo> selectByExample(SysOperateInfoExample example, RowBounds rowBounds);
    
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_operate_info
     *
     * @mbggenerated Sun Aug 17 21:36:32 CST 2014
     */
    SysOperateInfo selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_operate_info
     *
     * @mbggenerated Sun Aug 17 21:36:32 CST 2014
     */
    int updateByExampleSelective(@Param("record") SysOperateInfo record, @Param("example") SysOperateInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_operate_info
     *
     * @mbggenerated Sun Aug 17 21:36:32 CST 2014
     */
    int updateByExample(@Param("record") SysOperateInfo record, @Param("example") SysOperateInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_operate_info
     *
     * @mbggenerated Sun Aug 17 21:36:32 CST 2014
     */
    int updateByPrimaryKeySelective(SysOperateInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_operate_info
     *
     * @mbggenerated Sun Aug 17 21:36:32 CST 2014
     */
    int updateByPrimaryKey(SysOperateInfo record);
}
