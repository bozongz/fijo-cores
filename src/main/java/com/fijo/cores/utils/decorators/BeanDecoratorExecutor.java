package com.fijo.cores.utils.decorators;

import java.util.Collection;
import java.util.List;


public class BeanDecoratorExecutor{
	public static void populates(Collection<?> beans,
			List<Object[]> decorators) {
		for (Object bean : beans) {
			for(Object[] d:decorators){
				IBeanDecorator decorator = (IBeanDecorator)d[0];
				String property = (String)d[1];
				Object strategy = d[2];
				decorator.decorate(bean, property, strategy);	
			}
		}		
	}
	
}
