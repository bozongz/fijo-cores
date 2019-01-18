/**
 * 
 */
package com.fijo.cores.utils.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 是否为唯一性字段
 * 
 * @author zhangbo
 *
 */ 
@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.FIELD)  
public @interface Unique { 
}
