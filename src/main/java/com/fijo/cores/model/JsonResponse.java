/**
 * 
 */
package com.fijo.cores.model;

import com.fijo.cores.utils.AppCodeGenerator;

/**
 * @author Li
 *
 */
public class JsonResponse extends BaseObject<JsonResponse>{
    private static final long serialVersionUID = -7314451132207798931L;

    private Integer responseid;
	private Object message;
	private Object data;
    private String requestId;

    public JsonResponse() {
        this.requestId = AppCodeGenerator.nextUnLimitCode();
    }

    public JsonResponse(Integer responseid) {
        this.responseid = responseid;
        this.requestId = AppCodeGenerator.nextUnLimitCode();
    }

    public Integer getResponseid() {
		return responseid;
	}

	public void setResponseid(Integer responseid) {
		this.responseid = responseid;
	}

	public Object getMessage() {
		return message;
	}

	public void setMessage(Object message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
