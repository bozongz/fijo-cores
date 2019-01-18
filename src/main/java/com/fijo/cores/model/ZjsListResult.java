package com.fijo.cores.model;



/**
 * zjs 返回数据列表结果
 * 
 * @author zhangbo
 *
 */
public class ZjsListResult<T> extends BaseObject<ZjsListResult<T>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6446366042927413496L;

	private String message; //响应提示信息
	
	private Integer responseid; //响应标识
	
	private ZjsListData<T> data; //返回数据列表

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the responseid
	 */
	public Integer getResponseid() {
		return responseid;
	}

	/**
	 * @param responseid the responseid to set
	 */
	public void setResponseid(Integer responseid) {
		this.responseid = responseid;
	}

	/**
	 * @return the data
	 */
	public ZjsListData<T> getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(ZjsListData<T> data) {
		this.data = data;
	}

	
}
