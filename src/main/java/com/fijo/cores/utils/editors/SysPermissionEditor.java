/**
 * 
 */
package com.fijo.cores.utils.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.util.StringUtils;

import com.fijo.cores.admin.authority.model.SysPermission;
import com.fijo.cores.admin.authority.service.ISysPermissionService;

/**
 * @author zhangbo
 *
 */
public class SysPermissionEditor extends PropertyEditorSupport{
	private ISysPermissionService service;
	
	public SysPermissionEditor(ISysPermissionService service) {
		super();
		this.service = service;
	}

	@Override
	public void setAsText(String textValue) throws IllegalArgumentException {
		if(StringUtils.isEmpty(textValue))
			setValue(null);
		else{
			Integer id = Integer.parseInt(textValue);
			SysPermission o = service.getById(id);
			setValue(o);
		}
	}
}
