/**
 * 
 */
package com.fijo.cores.app.model;

import java.io.Serializable;
import java.util.Collection;

/**
 * 支持根据特殊业务动态过滤可选择的审批人（新增或删除）
 * 
 * @author zhangbo
 *
 */
public class DynamicAuditUser implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2051215942435004733L;

	public enum OPS {add, sub};
	
	private Collection<Integer> auditors;
	
	private DynamicAuditUser.OPS ops;

	public DynamicAuditUser() {
		super();
	}

	public DynamicAuditUser(Collection<Integer> auditors, OPS ops) {
		super();
		this.auditors = auditors;
		this.ops = ops;
	}

	/**
	 * @return the auditors
	 */
	public Collection<Integer> getAuditors() {
		return auditors;
	}

	/**
	 * @param auditors the auditors to set
	 */
	public void setAuditors(Collection<Integer> auditors) {
		this.auditors = auditors;
	}

	/**
	 * @return the ops
	 */
	public DynamicAuditUser.OPS getOps() {
		return ops;
	}

	/**
	 * @param ops the ops to set
	 */
	public void setOps(DynamicAuditUser.OPS ops) {
		this.ops = ops;
	}

	
	
}
