/**
 * 
 */
package com.fijo.cores.utils.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.util.StringUtils;

import com.fijo.cores.admin.authority.model.SysOrg;
import com.fijo.cores.admin.authority.service.ISysOrgService;

/**
 * @author zhangbo
 *
 */
public class SysOrgEditor extends PropertyEditorSupport{
	private ISysOrgService service;
	
	public SysOrgEditor(ISysOrgService service) {
		super();
		this.service = service;
	}

	@Override
	public void setAsText(String textValue) throws IllegalArgumentException {
		if(StringUtils.isEmpty(textValue))
			setValue(null);
		else{
			Integer id = Integer.parseInt(textValue);
			SysOrg o = service.getById(id);
			setValue(o);
		}
	}
}
