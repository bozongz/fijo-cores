/**
 * 
 */
package com.fijo.cores.utils.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhangbo
 * 
 * 在一个应用中实现同时可发布异步事件和同步事件
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AsyncEventListener {
}