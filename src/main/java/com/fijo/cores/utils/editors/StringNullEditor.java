/**
 * 
 */
package com.fijo.cores.utils.editors;

import java.beans.PropertyEditorSupport;

import com.fijo.cores.utils.Constants;
import com.fijo.cores.utils.Constants;

/**
 * 将前端null参数绑定为空值
 * 
 * @author zhangbo
 * 
 */
public class StringNullEditor extends PropertyEditorSupport {
	public void setAsText(String textValue) throws IllegalArgumentException {
		if(textValue==null || textValue.trim().equals(Constants.NULL) || textValue.equals(Constants.EMPTY))
			setValue(null);
		else
			setValue(textValue);
	}
}
