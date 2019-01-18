package com.fijo.cores.admin.syslog.service.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import com.fijo.cores.admin.syslog.mapper.SysOperateInfoMapper;
import com.fijo.cores.admin.syslog.service.ISysOperateInfoService;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fijo.cores.admin.syslog.mapper.SysOperateInfoMapper;
import com.fijo.cores.admin.syslog.model.SysOperateInfo;
import com.fijo.cores.admin.syslog.model.SysOperateInfoExample;
import com.fijo.cores.admin.syslog.service.ISysOperateInfoService;

@Service("sysOperateInfoService")
public class SysOperateInfoService implements ISysOperateInfoService {
	@Autowired
	@Qualifier("sqlSessionTemplateSimple")
	private SqlSession sqlSession;
	private SysOperateInfoMapper mapper;

	@PostConstruct
	private void initMappers() {
		mapper = sqlSession.getMapper(SysOperateInfoMapper.class);
	}

	@Override
	public int countByExample(SysOperateInfoExample example) {
		return mapper.countByExample(example);
	}

	@Override
	public int deleteByExample(SysOperateInfoExample example) {
		return mapper.deleteByExample(example);
	}

	@Override
	public int deleteByPrimaryKey(Long id) {
		return mapper.deleteByPrimaryKey(id);
	}

	@Override
	public int insert(SysOperateInfo record) {
		return mapper.insert(record);
	}

	@Override
	public int insertSelective(SysOperateInfo record) {
		return mapper.insertSelective(record);
	}

	@Override
	public List<SysOperateInfo> selectByExample(SysOperateInfoExample example) {
		return mapper.selectByExample(example);
	}

	@Override
	public List<SysOperateInfo> selectByExample(SysOperateInfoExample example,
			RowBounds rowBounds) {
		return mapper.selectByExample(example, rowBounds);
	}

	@Override
	public SysOperateInfo selectByPrimaryKey(Long id) {
		return mapper.selectByPrimaryKey(id);
	}

	@Override
	public int updateByExampleSelective(SysOperateInfo record,
			SysOperateInfoExample example) {
		return mapper.updateByExampleSelective(record, example);
	}

	@Override
	public int updateByExample(SysOperateInfo record,
			SysOperateInfoExample example) {
		return mapper.updateByExample(record, example);
	}

	@Override
	public int updateByPrimaryKeySelective(SysOperateInfo record) {
		return mapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(SysOperateInfo record) { 
		return mapper.updateByPrimaryKey(record);
	}



}
