package com.fijo.cores.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.DEFAULT) 
@JsonIgnoreProperties(ignoreUnknown=true)
//@JsonInclude(Include.NON_EMPTY) 
public abstract class BaseObject<T> implements Serializable, Comparable<T> {


    private static final long serialVersionUID = -2968408892537486897L;

    @Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	/**
	 * 对象排序（参考物料集合查询排序）
	 */
	@Override
	public int compareTo(T obj) {
		return CompareToBuilder.reflectionCompare(this, obj);
	}

}
