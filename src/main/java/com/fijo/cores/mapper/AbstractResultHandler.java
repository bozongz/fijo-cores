/**
 * 
 */
package com.fijo.cores.mapper;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

import com.google.common.collect.Lists;

/**
 * 通用的Mybatis查询结果集封装
 * 
 * @author zhangbo
 *
 */
public abstract class AbstractResultHandler<E> implements ResultHandler{
	public final Log log = LogFactory.getLog(getClass());
	
	private final List<E> resultList = Lists.newArrayList();
	
	@Override
	public void handleResult(ResultContext context) {
		@SuppressWarnings("unchecked")
		E obj = (E)context.getResultObject();  	
		log.debug(obj);
		resultList.add(obj);
	}

	/**
	 * @return the resultList
	 */
	public List<E> getResultList() {
		return resultList;
	}
	
}
