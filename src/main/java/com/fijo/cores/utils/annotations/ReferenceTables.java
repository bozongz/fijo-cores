/**
 * 
 */
package com.fijo.cores.utils.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * @author zhangbo
 *
 */
@Target(TYPE) 
@Retention(RUNTIME)
public @interface ReferenceTables {
	ReferenceTable[] joinTables() default {};
//	String name() default "";
//	String[] tables() default {};
}
