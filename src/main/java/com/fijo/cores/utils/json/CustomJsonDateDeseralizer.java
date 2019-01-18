/**
 * 
 */
package com.fijo.cores.utils.json;

import java.text.DateFormat;

import com.fasterxml.jackson.databind.deser.std.DateDeserializers.DateDeserializer;

/**
 * @author zhangbo
 *
 */
public class CustomJsonDateDeseralizer extends DateDeserializer{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4117925864317313966L;

    @Override
    protected DateDeserializer withDateFormat(DateFormat df, String formatString) {
        return new DateDeserializer(this, new CustomDateFormat(formatString), formatString);
    }

}
