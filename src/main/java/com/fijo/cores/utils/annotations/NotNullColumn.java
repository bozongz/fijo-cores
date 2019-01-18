/**
 * 
 */
package com.fijo.cores.utils.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author zhangbo
 *
 */
@Target({METHOD, FIELD}) 
@Retention(RUNTIME)
public @interface NotNullColumn {
	public String value();
}
