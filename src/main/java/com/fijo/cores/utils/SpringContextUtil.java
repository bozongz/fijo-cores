/**
 * 版权所有 © 北京晟壁科技有限公司 2008-2016。保留一切权利!
 */
package com.fijo.cores.utils;

import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.stereotype.Component;

/**
 * 用途：获取Spring Ioc容器上下文中的Bean
 * 作者: zhangbo
 * 时间: 2016-08-11  11:07 
 */
@Component
public class SpringContextUtil extends ApplicationObjectSupport {

    public Object getBeanByName(String name){
        return getApplicationContext().getBean(name);
    }

    public Object getBeanByClass(Class clazz){
        return getApplicationContext().getBean(clazz);
    }
}
