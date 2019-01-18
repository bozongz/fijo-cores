package com.fijo.cores.admin.syslog.service.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import com.fijo.cores.admin.syslog.service.ISysLoginInfoService;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fijo.cores.admin.syslog.mapper.SysLoginInfoMapper;
import com.fijo.cores.admin.syslog.model.SysLoginInfo;
import com.fijo.cores.admin.syslog.model.SysLoginInfoExample;
import com.fijo.cores.admin.syslog.service.ISysLoginInfoService;


@Service(value="sysLoginInfoService")
public class SysLoginInfoService implements ISysLoginInfoService {
	@Autowired
	@Qualifier("sqlSessionTemplateSimple")
	private SqlSession sqlSession;
	private SysLoginInfoMapper mapper;

	@PostConstruct
	private void initMappers() {
		mapper = sqlSession.getMapper(SysLoginInfoMapper.class);
	}

	@Override
	public int countByExample(SysLoginInfoExample example) {
		return mapper.countByExample(example);
	}

	@Override
	public int deleteByExample(SysLoginInfoExample example) {
		return mapper.deleteByExample(example);
	}

	@Override
	public int deleteByPrimaryKey(Long id) {
		return mapper.deleteByPrimaryKey(id);
	}

	@Override
	public int insert(SysLoginInfo record) {
		return mapper.insert(record);
	}

	@Override
	public int insertSelective(SysLoginInfo record) {
		return mapper.insertSelective(record);
	}

	@Override
	public List<SysLoginInfo> selectByExample(SysLoginInfoExample example) {
		return mapper.selectByExample(example);
	}

	@Override
	public List<SysLoginInfo> selectByExample(SysLoginInfoExample example,
			RowBounds rowBounds) {
		return mapper.selectByExample(example, rowBounds);
	}

	@Override
	public SysLoginInfo selectByPrimaryKey(Long id) {
		return mapper.selectByPrimaryKey(id);
	}

	@Override
	public int updateByExampleSelective(SysLoginInfo record,
			SysLoginInfoExample example) {
		return mapper.updateByExample(record, example);
	}

	@Override
	public int updateByExample(SysLoginInfo record, SysLoginInfoExample example) {
		return mapper.updateByExample(record, example);
	}

	@Override
	public int updateByPrimaryKeySelective(SysLoginInfo record) {
		return mapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(SysLoginInfo record) {
		return mapper.updateByPrimaryKey(record);
	}

}

