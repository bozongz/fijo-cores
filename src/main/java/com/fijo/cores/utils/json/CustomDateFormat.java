/**
 * 
 */
package com.fijo.cores.utils.json;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

/**
 * @author zhangbo
 *
 */
public class CustomDateFormat extends SimpleDateFormat{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7995520130892658428L;

	public CustomDateFormat(String formatString){
		super(formatString);
	}
	
	@Override  
    public Date parse(String source) throws ParseException {  
		if(StringUtils.isNotEmpty(source) && !source.equalsIgnoreCase("null") && !source.equalsIgnoreCase("\"null\"") && !source.equalsIgnoreCase("\'null\'")){
			return super.parse(source);
		}
		else
			return null;
    }  
}
